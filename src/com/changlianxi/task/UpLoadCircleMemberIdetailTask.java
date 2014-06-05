package com.changlianxi.task;

import android.content.Context;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.enums.RetError;

public class UpLoadCircleMemberIdetailTask extends
        BaseAsyncTask<CircleMember, Void, RetError> {
    private CircleMember oldCircleMember = null;
    private CircleMember newCircleMember = null;
    private String avatarPath = "";

    public UpLoadCircleMemberIdetailTask(Context context, String avatarPath) {
        this.avatarPath = avatarPath;
    }

    @Override
    protected RetError doInBackground(CircleMember... arg0) {
        oldCircleMember = arg0[0];
        newCircleMember = arg0[1];
        RetError ret = oldCircleMember.uploadAfterEdit(newCircleMember,
                avatarPath);

        return ret;
    }

}
