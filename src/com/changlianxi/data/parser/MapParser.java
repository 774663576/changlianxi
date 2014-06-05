package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.request.MapResult;
import com.changlianxi.data.request.Result;

public class MapParser implements IParser {

    private List<String> keys = new ArrayList<String>();

    public MapParser(List<String> keys) {
        this.keys = keys;
    }

    public MapParser(String[] keys) {
        for (String key : keys) {
            this.keys.add(key);
        }
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        Map<String, Object> key2values = new HashMap<String, Object>();
        for (String key : keys) {
            if (jsonObj.has(key)) {
                key2values.put(key, jsonObj.get(key));
            } else {
                key2values.put(key, "");
            }
        }

        return new MapResult(key2values);
    }
}
