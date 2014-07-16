package com.changlianxi.task;

import com.changlianxi.data.FindPassword;
import com.changlianxi.data.enums.RetError;

public class FindPswdVerifyAuthCodeTask extends
        BaseAsyncTask<FindPassword, Void, RetError> {
    private FindPassword mFind;
    private String authCode = "";

    public FindPswdVerifyAuthCodeTask(String authCode) {
        this.authCode = authCode;
    }

    @Override
    protected RetError doInBackground(FindPassword... params) {
        mFind = params[0];
        RetError ret = mFind.verifyAuthCode(authCode);
        return ret;
    }

}
