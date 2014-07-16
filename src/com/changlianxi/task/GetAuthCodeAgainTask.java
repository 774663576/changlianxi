package com.changlianxi.task;

import com.changlianxi.data.Register;
import com.changlianxi.data.enums.RetError;

public class GetAuthCodeAgainTask extends
        BaseAsyncTask<Register, Void, RetError> {
    private Register register;

    @Override
    protected RetError doInBackground(Register... params) {
        register = params[0];
        RetError ret = register.getAuthCodeAgain();
        return ret;
    }

}
