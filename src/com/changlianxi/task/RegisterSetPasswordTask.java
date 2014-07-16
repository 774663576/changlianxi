package com.changlianxi.task;

import com.changlianxi.data.Register;
import com.changlianxi.data.enums.RetError;

public class RegisterSetPasswordTask extends
        BaseAsyncTask<Register, Void, RetError> {
    private Register register;
    private String password = "";

    public RegisterSetPasswordTask(String password) {
        this.password = password;
    }

    @Override
    protected RetError doInBackground(Register... params) {
        register = params[0];
        RetError ret = register.setPassword(password);
        return ret;
    }

}
