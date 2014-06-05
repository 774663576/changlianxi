package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.CircleDynamic;
import com.changlianxi.data.CircleDynamicList;
import com.changlianxi.data.enums.DynamicType;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;

public class CircleDynamicListParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        boolean isPersonal = false;
        if (params.containsKey("type")
                && "main".equals((String) params.get("type"))) {
            isPersonal = true;
        }
        int cid = jsonObj.getInt("cid");
        int total = jsonObj.getInt("total");
        int requestTime = jsonObj.getInt("current");
        JSONArray jsonArr = jsonObj.getJSONArray("news");
        if (jsonArr == null || cid == 0) {
            return Result.defContentErrorResult();
        }

        List<CircleDynamic> dynamics = new ArrayList<CircleDynamic>();
        long start = 0L, end = 0L;
        for (int i = jsonArr.length() - 1; i >= 0; i--) {
            JSONObject obj = (JSONObject) jsonArr.opt(i);
            int did = obj.getInt("id");
            String type = obj.getString("type");
            int uid1 = obj.getInt("user1");
            int uid2 = obj.getInt("user2");
            int pid2 = obj.getInt("person2");
            String content = obj.getString("content");
            String detail = obj.getString("detail");
            String time = obj.getString("created");
            int needApprove = obj.getInt("need_approve");

            CircleDynamic dynamic = new CircleDynamic(cid, did, isPersonal);
            dynamic.setUid1(uid1);
            dynamic.setUid2(uid2);
            dynamic.setPid2(pid2);
            dynamic.setType(DynamicType.convert(type));
            dynamic.setContent(content);
            dynamic.setDetail(detail);
            dynamic.setTime(time);
            dynamic.setNeedApproved(needApprove > 0);
            dynamic.setStatus(Status.NEW);
            dynamics.add(dynamic);
            long tmp = DateUtils.convertToDate(time);
            if (end == 0 || tmp > end) {
                end = tmp;
            }
            if (start == 0 || tmp < start) {
                tmp = start;
            }
        }
        CircleDynamicList cdl = new CircleDynamicList(cid);
        cdl.setDynamics(dynamics);
        cdl.setTotal(total);
        cdl.setStartTime(start);
        cdl.setEndTime(end);
        if (total < dynamics.size()) {
            cdl.setLastReqTime(end);
        } else {
            cdl.setLastReqTime(requestTime);
        }

        Result ret = new Result();
        ret.setData(cdl);
        return ret;
    }

}
