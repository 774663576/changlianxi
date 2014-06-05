package com.changlianxi.data.request;

import java.util.ArrayList;
import java.util.List;

public class MoreArrayResult extends Result {
    private List<List<Object>> arrs = new ArrayList<List<Object>>();
    private String avatar = "";

    public MoreArrayResult() {
    }

    public MoreArrayResult(List<List<Object>> arrs, String avatar) {
        this.avatar = avatar;
        this.setArrs(arrs);
    }

    public List<List<Object>> getArrs() {
        return arrs;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setArrs(List<List<Object>> arrs) {
        this.arrs = arrs;
    }

    @Override
    public String toString() {
        return "ArrayResult [data=" + arrs + ", status=" + status + ", err="
                + err + "]  size=" + arrs.size();
    }
}
