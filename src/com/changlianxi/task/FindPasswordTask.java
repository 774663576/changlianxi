package com.changlianxi.task;

import com.changlianxi.data.FindPassword;
import com.changlianxi.data.enums.RetError;

public class FindPasswordTask extends
        BaseAsyncTask<FindPassword, Void, RetError> {
    private FindPassword mFind;

    @Override
    protected RetError doInBackground(FindPassword... params) {
        mFind = params[0];
        RetError ret = mFind.retrievePasswrod();
        return ret;
    }

}
