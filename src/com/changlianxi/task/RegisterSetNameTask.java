package com.changlianxi.task;

import com.changlianxi.data.Register;
import com.changlianxi.data.enums.RetError;

public class RegisterSetNameTask extends
        BaseAsyncTask<Register, Void, RetError> {
    private Register register;
    private String name = "";

    public RegisterSetNameTask(String name) {
        this.name = name;
    }

    @Override
    protected RetError doInBackground(Register... params) {
        register = params[0];
        RetError ret = register.setName(name);
        return ret;
    }

}
