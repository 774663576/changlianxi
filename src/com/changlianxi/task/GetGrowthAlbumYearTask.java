package com.changlianxi.task;

import com.changlianxi.data.GrowthAlbumList;
import com.changlianxi.data.enums.RetError;

public class GetGrowthAlbumYearTask extends
        BaseAsyncTask<GrowthAlbumList, Void, RetError> {
    private GrowthAlbumList albumList = null;
    private int startY;
    private int endY;
    private int startM;
    private int endM;

    public GetGrowthAlbumYearTask(GrowthAlbumList albumList, int startY,
            int endY, int startM, int endM) {
        this.albumList = albumList;
        this.startY = startY;
        this.endY = endY;
        this.startM = startM;
        this.endM = endM;
    }

    @Override
    protected RetError doInBackground(GrowthAlbumList... param) {
        albumList.setStrKey("year");
        albumList.setYear(startY + "");
        albumList.setMonth("");
        albumList.setDay("");
        RetError ret = albumList.refushMonthAlbum(startY, endY, startM, endM);
        return ret;
    }
}
