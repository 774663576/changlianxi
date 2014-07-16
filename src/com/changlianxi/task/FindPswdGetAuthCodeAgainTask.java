package com.changlianxi.task;

import com.changlianxi.data.FindPassword;
import com.changlianxi.data.enums.RetError;

public class FindPswdGetAuthCodeAgainTask extends
        BaseAsyncTask<FindPassword, Void, RetError> {
    private FindPassword mFind;

    @Override
    protected RetError doInBackground(FindPassword... params) {
        mFind = params[0];
        RetError ret = mFind.getAuthCodeAgain();
        return ret;
    }

}
