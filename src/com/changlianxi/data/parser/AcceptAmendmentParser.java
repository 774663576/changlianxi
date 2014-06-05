package com.changlianxi.data.parser;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.PersonDetail;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.request.MapResult;
import com.changlianxi.data.request.Result;

public class AcceptAmendmentParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        int cid = (Integer) params.get("cid");
        JSONObject jsonAmd = jsonObj.getJSONObject("amendment");
        if (jsonAmd == null) {
            return Result.defContentErrorResult();
        }

        int id = jsonAmd.getInt("id");
        int replaced = jsonAmd.getInt("replaced");
        String type = jsonAmd.getString("type");
        String value = jsonAmd.getString("value");
        PersonDetailType pType = PersonDetailType.convertToType(type);

        PersonDetail pd = new PersonDetail(id, cid, 0,0, pType, value);
        if (jsonAmd.has("start")) {
            pd.setStart(jsonAmd.getString("start"));
        }
        if (jsonAmd.has("end")) {
            pd.setEnd(jsonAmd.getString("end"));
        }
        if (jsonAmd.has("remark")) {
            pd.setRemark(jsonAmd.getString("remark"));
        }

        MapResult ret = new MapResult();
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("replaced", replaced);
        maps.put("detail", pd);
        ret.setMaps(maps);
        return ret;
    }

}
