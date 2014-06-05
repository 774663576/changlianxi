package com.changlianxi.data.request;

import java.util.ArrayList;
import java.util.List;

public class ArrayResult extends Result {
    private List<Object> arrs = new ArrayList<Object>();

    public ArrayResult() {
    }

    public ArrayResult(List<Object> arrs) {
        this.setArrs(arrs);
    }

    public List<Object> getArrs() {
        return arrs;
    }

    public void setArrs(List<Object> arrs) {
        if (arrs != null) {
            this.arrs = arrs;
        }
    }

    @Override
    public String toString() {
        return "ArrayResult [data=" + arrs + ", status=" + status + ", err="
                + err + "]";
    }
}
