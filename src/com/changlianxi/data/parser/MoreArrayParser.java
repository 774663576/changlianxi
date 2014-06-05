package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.request.MoreArrayResult;
import com.changlianxi.data.request.Result;

public class MoreArrayParser implements IParser {
    private List<String> keys = new ArrayList<String>();
    private List<List<Object>> listValues = new ArrayList<List<Object>>();
    private String avatar = "";

    public MoreArrayParser(String[] keys) {
        for (String key : keys) {
            this.keys.add(key);
        }
    }

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }
        if (jsonObj.has("avatar")) {
            avatar = jsonObj.getString("avatar");
        }

        for (String specialKey : keys) {
            JSONArray jsonArr = jsonObj.getJSONArray(specialKey);
            if (jsonArr == null) {
                return Result.defContentErrorResult();
            }
            List<Object> values = new ArrayList<Object>();
            for (int i = 0; i < jsonArr.length(); i++) {
                Object v = (Object) jsonArr.opt(i);
                values.add(v);
            }
            listValues.add(values);
        }

        return new MoreArrayResult(listValues, avatar);
    }
}
