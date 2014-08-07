package com.changlianxi.task;

import java.util.List;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleGroup;
import com.changlianxi.data.enums.RetError;

public class SetCircleGroupTask extends BaseAsyncTask<Circle, Void, RetError> {
    private Circle circle;
    private List<CircleGroup> editListsGroups;

    public SetCircleGroupTask(List<CircleGroup> editListsGroups) {
        this.editListsGroups = editListsGroups;
    }

    @Override
    protected RetError doInBackground(Circle... params) {
        circle = params[0];
        RetError ret = circle.editGroups(editListsGroups);
        return ret;
    }
}
