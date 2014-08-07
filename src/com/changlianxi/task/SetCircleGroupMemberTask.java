package com.changlianxi.task;

import java.util.List;

import com.changlianxi.data.CircleGroup;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;

public class SetCircleGroupMemberTask extends
        BaseAsyncTask<CircleGroup, Void, RetError> {
    private CircleGroup cGroup;
    private List<CircleMember> newListMembers;
    List<CircleMember> oldListMembers;

    public SetCircleGroupMemberTask(List<CircleMember> newListMembers,
            List<CircleMember> oldListMembers) {
        this.newListMembers = newListMembers;
        this.oldListMembers = oldListMembers;
    }

    @Override
    protected RetError doInBackground(CircleGroup... arg0) {
        cGroup = arg0[0];
        RetError ret = cGroup.setGroupMembers(newListMembers, oldListMembers);
        return ret;
    }

}
