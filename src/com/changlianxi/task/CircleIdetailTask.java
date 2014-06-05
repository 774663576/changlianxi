package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

/**
 * 获取圈子的详细资料
 * 
 * @author teeker_bin
 */
public class CircleIdetailTask extends BaseAsyncTask<Void, Void, RetError> {
    private Circle circle;

    public CircleIdetailTask(Circle circle) {
        this.circle = circle;

    }

    @Override
    protected RetError doInBackground(Void... params) {
        RetError result = null;
        circle.read(DBUtils.getDBsa(1));
        callBack.readDBFinish();
        if (!isNet) {
            return RetError.NETWORK_ERROR;
        }
        result = circle.refresh();
        circle.write(DBUtils.getDBsa(2));
        return result;
    }

}
