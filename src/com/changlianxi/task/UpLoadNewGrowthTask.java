package com.changlianxi.task;

import com.changlianxi.data.Growth;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class UpLoadNewGrowthTask extends BaseAsyncTask<Growth, Void, RetError> {
    private Growth growth = null;

    @Override
    protected RetError doInBackground(Growth... params) {
        growth = params[0];
        RetError ret = growth.uploadForAdd1();
        if (ret == RetError.NONE) {
            growth.write(DBUtils.getDBsa(2));

        }
        return ret;
    }

}
