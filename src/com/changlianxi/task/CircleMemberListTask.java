package com.changlianxi.task;

import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class CircleMemberListTask extends
        BaseAsyncTask<CircleMemberList, Void, RetError> {
    private CircleMemberList circleMemberList;
    private int newMemberCount = 0;
    private int newMyDetailEditCount = 0;

    public CircleMemberListTask(int newMemberCount, int newMyDetailEditCount) {
        this.newMemberCount = newMemberCount;
        this.newMyDetailEditCount = newMyDetailEditCount;
    }

    @Override
    protected RetError doInBackground(CircleMemberList... params) {
        if (isCancelled() || params == null) {
            return RetError.NONE;
        }
        circleMemberList = params[0];
        circleMemberList.read(DBUtils.getDBsa(1));
        callBack.readDBFinish();
        // if (circleMemberList.getMembers().size() != 0
        // && newMemberCount + newMyDetailEditCount == 0) {
        // return RetError.NONE;
        // }
        if (!isNet) {
            return RetError.NETWORK_ERROR;
        }
        circleMemberList.refresh(circleMemberList.getLastReqTime());
        circleMemberList.write(DBUtils.getDBsa(2));
        return RetError.NONE;
    }

}
