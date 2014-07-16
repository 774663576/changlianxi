package com.changlianxi.task;

import com.changlianxi.data.Register;
import com.changlianxi.data.enums.RetError;

public class VerifyAuthCodeTask extends BaseAsyncTask<Register, Void, RetError> {
    private Register register;
    private String authCode = "";

    public VerifyAuthCodeTask(String authCode) {
        this.authCode = authCode;
    }

    @Override
    protected RetError doInBackground(Register... params) {
        register = params[0];
        RetError ret = register.verifyAuthCode(authCode);
        return ret;
    }

}
