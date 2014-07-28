package com.changlianxi.task;

import com.changlianxi.data.Circle;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

/**
 * 
 * 上传圈子LOGO
  *
 */
public class UpLoadCircleLogoTask extends BaseAsyncTask<Circle, Void, RetError> {
    private Circle circle;
    private String logoPath = "";

    public UpLoadCircleLogoTask(String logoPath) {
        this.logoPath = logoPath;
    }

    @Override
    protected RetError doInBackground(Circle... params) {
        circle = params[0];
        RetError retError = null;
        retError = circle.uploadLogo(logoPath);
        if (retError == RetError.NONE) {
            circle.write(DBUtils.getDBsa(2));
        }
        return retError;
    }
}
