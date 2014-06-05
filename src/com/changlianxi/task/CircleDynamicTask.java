package com.changlianxi.task;

import com.changlianxi.data.CircleDynamicList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.DateUtils;

public class CircleDynamicTask extends
        BaseAsyncTask<CircleDynamicList, Void, RetError> {
    private CircleDynamicList list = null;
    private boolean readDB = false;
    private boolean isMore = false;
    private int newDynamicCount = 0;

    public CircleDynamicTask(boolean readDB, boolean isMore, int newDynamicCount) {
        this.readDB = readDB;
        this.isMore = isMore;
        this.newDynamicCount = newDynamicCount;
    }

    @Override
    protected RetError doInBackground(CircleDynamicList... params) {
        list = params[0];
        if (readDB) {
            list.read(DBUtils.getDBsa(1));
            callBack.readDBFinish();
        }
        if (list.getDynamics().size() != 0 && newDynamicCount == 0) {
            return RetError.NONE;
        }
        if (!isNet) {
            return RetError.NETWORK_ERROR;
        }
        RetError ret = RetError.NONE;
        if (isMore) {
            ret = list.refresh(
                    0l,
                    Long.valueOf(DateUtils.phpTime(DateUtils.convertToDate(list
                            .getDynamics().get(list.getDynamics().size() - 1)
                            .getTime()))));

        } else {
            list.refresh(list.getLastReqTime());
        }
        list.write(DBUtils.getDBsa(2));
        return ret;
    }

}
