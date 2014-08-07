package com.changlianxi.task;

import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class GetCircleGroupMembersTask extends
        BaseAsyncTask<CircleMemberList, Void, RetError> {
    private int group_id;
    private CircleMemberList memberList;

    public GetCircleGroupMembersTask(int group_id) {
        this.group_id = group_id;
    }

    @Override
    protected RetError doInBackground(CircleMemberList... params) {
        memberList = params[0];
        memberList.readCircleMembersByGroupID(DBUtils.getDBsa(1), group_id);
        return RetError.NONE;
    }

}
