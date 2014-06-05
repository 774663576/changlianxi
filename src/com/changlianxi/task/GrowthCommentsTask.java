package com.changlianxi.task;

import java.util.List;

import com.changlianxi.data.GrowthComment;
import com.changlianxi.data.GrowthCommentList;
import com.changlianxi.data.enums.RetError;

public class GrowthCommentsTask extends
        BaseAsyncTask<List<GrowthComment>, Void, RetError> {
    private GrowthCommentList growthList;
    private long startTime;

    public GrowthCommentsTask(GrowthCommentList growthList, long startTime) {
        this.growthList = growthList;
        this.startTime = startTime;
    }

    @Override
    protected RetError doInBackground(List<GrowthComment>... params) {
        if (!isNet) {
            return RetError.NONE;
        }
        growthList.refresh(0, startTime);
        return RetError.NONE;
    }
}
