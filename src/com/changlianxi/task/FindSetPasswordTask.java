package com.changlianxi.task;

import com.changlianxi.data.FindPassword;
import com.changlianxi.data.enums.RetError;

public class FindSetPasswordTask extends
        BaseAsyncTask<FindPassword, Void, RetError> {
    private FindPassword mFind;
    private String password = "";

    public FindSetPasswordTask(String password) {
        this.password = password;
    }

    @Override
    protected RetError doInBackground(FindPassword... params) {
        mFind = params[0];
        RetError ret = mFind.setPassword(password);
        return ret;
    }

}
