package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.request.Result;

public interface IParser {

    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception;
}
