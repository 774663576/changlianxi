package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

/**
 * 获取圈子的详细资料
 * 
 * @author teeker_bin
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
        if (retError != RetError.NONE) {
            return retError;
        }
        oldCircle.write(DBUtils.getDBsa(2));
        retError = oldCircle.uploadLogo(newCircle.getLogo());
        oldCircle.write(DBUtils.getDBsa(2));
        if (retError == RetError.INVALID) {
            return RetError.NONE;
        }
        return retError;
    }

}
