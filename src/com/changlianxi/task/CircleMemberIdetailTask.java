package com.changlianxi.task;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class CircleMemberIdetailTask extends
        BaseAsyncTask<CircleMember, Void, RetError> {
    private CircleMember circleMember;

    @Override
    protected RetError doInBackground(CircleMember... arg0) {
        if (isCancelled() || arg0 == null) {
            return null;
        }
        circleMember = arg0[0];
        circleMember.read(DBUtils.getDBsa(1));
        circleMember.readDetails(DBUtils.getDBsa(1));
        return RetError.NONE;

    }

}
