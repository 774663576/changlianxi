package com.changlianxi.data.request;

import com.changlianxi.data.IData;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;

public class Result {

    private IData data = null;

    protected RetStatus status = RetStatus.SUCC;

    protected RetError err = RetError.NONE;

    public Result() {
    }

    public Result(IData data, RetStatus status, RetError err) {
        this.data = data;
        this.status = status;
        this.err = err;
    }

    public IData getData() {
        return data;
    }

    public void setData(IData data) {
        this.data = data;
    }

    public RetStatus getStatus() {
        return status;
    }

    public void setStatus(RetStatus status) {
        this.status = status;
    }

    public RetError getErr() {
        return err;
    }

    public void setErr(RetError err) {
        this.err = err;
    }

    public void setErr(String err) {
        this.err = RetError.convert(err);
    }

    @Override
    public String toString() {
        return "Result [data=" + data + ", status=" + status + ", err=" + err
                + "]";
    }

    public static Result defContentErrorResult() {
        return new Result(null, RetStatus.FAIL, RetError.NONE);
    }

}
