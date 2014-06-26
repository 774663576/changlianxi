package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;

public class CreateNewCircleTask extends BaseAsyncTask<Circle, Void, RetError> {
    private Circle circle = null;

    @Override
    protected RetError doInBackground(Circle... params) {
        circle = params[0];
        RetError reError = circle.uploadForAdd();
        // if (reError == RetError.NONE) {
        // circle.write(DBUtils.getDBsa(2));
        // }

        return reError;
    }

}
