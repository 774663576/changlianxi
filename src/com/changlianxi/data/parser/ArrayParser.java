package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.request.ArrayResult;
import com.changlianxi.data.request.Result;

public class ArrayParser implements IParser {

    private String specialKey = "";

    public ArrayParser(String specialKey) {
        this.specialKey = specialKey;
    }

    public String getSpecialKey() {
        return specialKey;
    }

    public void setSpecialKey(String specialKey) {
        this.specialKey = specialKey;
    }

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        JSONArray jsonArr = jsonObj.getJSONArray(specialKey);
        if (jsonArr == null) {
            return Result.defContentErrorResult();
        }
        List<Object> values = new ArrayList<Object>();
        for (int i = 0; i < jsonArr.length(); i++) {
            Object v = (Object) jsonArr.opt(i);
            values.add(v);
        }

        return new ArrayResult(values);
    }
}
