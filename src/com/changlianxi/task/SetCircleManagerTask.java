package com.changlianxi.task;

import java.util.List;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;

public class SetCircleManagerTask extends BaseAsyncTask<Circle, Void, RetError> {
    private Circle c;
    private List<CircleMember> oldListMembers;
    private List<CircleMember> newListMembers;

    public SetCircleManagerTask(List<CircleMember> oldListMembers,
            List<CircleMember> newListMembers) {
        this.oldListMembers = oldListMembers;
        this.newListMembers = newListMembers;
    }

    @Override
    protected RetError doInBackground(Circle... params) {
        c = params[0];
        RetError ret = c.setManagers(oldListMembers, newListMembers);
        return ret;
    }
}
