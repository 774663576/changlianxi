package com.changlianxi.task;

import java.util.List;

import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthList;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class GrowthListTask extends BaseAsyncTask<List<Growth>, Void, RetError> {
    private GrowthList growthList;
    private boolean isReadDB = false;
    private long endTime;
    private boolean isMore;
    private int newGrowthCount = 0;

    public GrowthListTask(GrowthList growthList, boolean isReadDB, long start,
            long end, boolean isMore, int newGrowthCount) {
        this.growthList = growthList;
        this.isReadDB = isReadDB;
        this.endTime = end;
        this.isMore = isMore;
        this.newGrowthCount = newGrowthCount;
    }

    @Override
    protected RetError doInBackground(List<Growth>... params) {
        if (isReadDB) {
            growthList.read(DBUtils.getDBsa(1));
            callBack.readDBFinish();
        }
        if (growthList.getGrowths().size() != 0 && newGrowthCount == 0) {
            return RetError.NONE;
        }
        if (!isNet) {
            return RetError.NETWORK_ERROR;
        }
        if (isMore) {
            growthList.refresh(0l, endTime);

        } else {
            growthList.refresh(growthList.getLastReqTime());
        }
        growthList.write(DBUtils.getDBsa(2));
        return RetError.NONE;
    }
}
