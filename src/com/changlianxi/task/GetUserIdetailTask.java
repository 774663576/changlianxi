package com.changlianxi.task;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;

public class GetUserIdetailTask extends
        BaseAsyncTask<CircleMember, Void, RetError> {
    private CircleMember circleMember;

    @Override
    protected RetError doInBackground(CircleMember... arg0) {
        if (isCancelled() || arg0 == null) {
            return null;
        }
        circleMember = arg0[0];
        if (!isNet) {
            return RetError.NETWORK_ERROR;
        }
        RetError refresh = circleMember.refresh(0l);
        return refresh;

    }

}
