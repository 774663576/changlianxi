package com.changlianxi.task;

import com.changlianxi.data.GrowthAlbumList;
import com.changlianxi.data.enums.RetError;

public class GetGrowthAlbumMonthTask extends
        BaseAsyncTask<GrowthAlbumList, Void, RetError> {
    private GrowthAlbumList albumList = null;
    private int startY;
    private int endY;
    private int startM;
    private int endM;
    private int startDay;
    private int endDay;
    private int startTime;
    private int endTime;
    private boolean isMore = false;

    public GetGrowthAlbumMonthTask(GrowthAlbumList albumList, int startY,
            int endY, int startM, int endM, int startDay, int endDay,
            int startTime, int endTime, boolean isMore) {
        this.albumList = albumList;
        this.startY = startY;
        this.endY = endY;
        this.startM = startM;
        this.endM = endM;
        this.startDay = startDay;
        this.endDay = endDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isMore = isMore;
    }

    @Override
    protected RetError doInBackground(GrowthAlbumList... param) {
        albumList.setStrKey("month");
        albumList.setYear(startY + "");
        albumList.setMonth("" + startM);
        albumList.setDay("");
        // albumList.read(DBUtils.getDBsa());
        // callBack.readDBFinish();
        RetError ret = RetError.NONE;
        if (isMore) {
            ret = albumList.refushDayhAlbumMore(startY, endY, startM, endM,
                    startDay, endDay, startTime, endTime);
        } else {
            ret = albumList.refushDayhAlbum(startY, endY, startM, endM,
                    startDay, endDay, startTime, endTime);
        }
        // albumList.write(DBUtils.getDBsa());
        return ret;
    }
}
