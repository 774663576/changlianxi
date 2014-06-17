package com.changlianxi.task;

import java.io.File;

import com.changlianxi.data.MyCard;
import com.changlianxi.data.enums.RetError;

public class UpLoadMyCardIdetailTask extends
        BaseAsyncTask<MyCard, Void, RetError> {
    private MyCard oldCircleMember = null;
    private MyCard newCircleMember = null;
    private String avatarPath = "";

    public UpLoadMyCardIdetailTask(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    @Override
    protected RetError doInBackground(MyCard... arg0) {
        oldCircleMember = arg0[0];
        newCircleMember = arg0[1];
        File file = new File(avatarPath);
        RetError ret = oldCircleMember.uploadAfterEdit(newCircleMember,
                avatarPath);

        return ret;
    }

}
