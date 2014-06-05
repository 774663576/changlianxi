package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.StringResult;

public class StringParser implements IParser {

    private String specialKey = "";

    public StringParser(String specialKey) {
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

        String value = jsonObj.getString(specialKey);
        if (value == null) {
            return Result.defContentErrorResult();
        }
        return new StringResult(value);
    }
}
