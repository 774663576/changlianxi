package com.changlianxi.task;

import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class CircleMemberListTask extends
        BaseAsyncTask<CircleMemberList, Void, RetError> {
    private CircleMemberList circleMemberList;
    private GetCircleMemberList finishCallBack;

    @Override
    protected RetError doInBackground(CircleMemberList... params) {
        if (isCancelled() || params == null) {
            return RetError.NONE;
        }
        circleMemberList = params[0];
        if (!isNet) {
            return RetError.NETWORK_ERROR;
        }
        circleMemberList.refresh(circleMemberList.getLastReqTime() / 1000);
        circleMemberList.write(DBUtils.getDBsa(2));
        finishCallBack.getFinish();
        return RetError.NONE;
    }

    public void setFinishCallBack(GetCircleMemberList finishCallBack) {
        this.finishCallBack = finishCallBack;
    }

    public interface GetCircleMemberList {
        void getFinish();
    }
}
