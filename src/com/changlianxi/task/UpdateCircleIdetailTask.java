package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

/**
 * 编辑圈子信息
 * @author teeker_bin
 *
 */
public class UpdateCircleIdetailTask extends
        BaseAsyncTask<Void, Void, RetError> {
    private Circle newCircle;
    private Circle oldCircle;

    public UpdateCircleIdetailTask(Circle oldCircle, Circle newCircle) {
        this.newCircle = newCircle;
        this.oldCircle = oldCircle;
    }

    @Override
    protected RetError doInBackground(Void... params) {
        RetError retError = null;
        retError = oldCircle.uploadAfterEdit(newCircle);
        if (retError == RetError.NONE) {
            oldCircle.write(DBUtils.getDBsa(2));
        }
        return retError;
    }

}
