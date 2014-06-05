package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.Amendment;
import com.changlianxi.data.MyCard;
import com.changlianxi.data.request.Result;

public class AmendmentsParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        JSONArray jsonArr = jsonObj.getJSONArray("amendments");
        if (jsonArr == null) {
            return Result.defContentErrorResult();
        }

        List<Amendment> amendments = new ArrayList<Amendment>();
        for (int i = jsonArr.length() - 1; i > 0; i--) {
            JSONObject obj = (JSONObject) jsonArr.opt(i);
            int amid = obj.getInt("id");
            int cid = obj.getInt("cid");
            int uid = obj.getInt("uid");
            String content = obj.getString("content");
            String time = obj.getString("time");

            Amendment amd = new Amendment(amid, cid, uid);
            amd.setContent(content);
            amd.setTime(time);
            amd.setStatus(Status.NEW);

            amendments.add(amd);
        }

        MyCard mc = new MyCard();
        mc.setAmendments(amendments);
        Result ret = new Result();
        ret.setData(mc);
        return ret;
    }

}
