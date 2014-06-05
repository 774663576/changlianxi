package com.changlianxi.task;

import com.changlianxi.data.GrowthAlbumList;
import com.changlianxi.data.enums.RetError;

public class GetGrowthAlbumTask extends
        BaseAsyncTask<GrowthAlbumList, Void, RetError> {
    private GrowthAlbumList albumList = null;
    private int startY;
    private int endY;

    public GetGrowthAlbumTask(GrowthAlbumList albumList, int startY, int endY) {
        this.albumList = albumList;
        this.startY = startY;
        this.endY = endY;
    }

    @Override
    protected RetError doInBackground(GrowthAlbumList... param) {
        albumList.setStrKey("year");
        albumList.setYear("");
        albumList.setMonth("");
        albumList.setDay("");
        RetError ret = albumList.refushYearAlbum(startY, endY);
        return ret;
    }
}
