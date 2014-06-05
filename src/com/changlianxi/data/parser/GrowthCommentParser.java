package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.GrowthComment;
import com.changlianxi.data.request.Result;

public class GrowthCommentParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        if (!jsonObj.has("gcid") || !jsonObj.has("time")
                || !jsonObj.has("count")) {
            return Result.defContentErrorResult();
        }

        int cid = (Integer) params.get("cid");
        int gcid = jsonObj.getInt("gcid");
        String time = jsonObj.getString("time");
        int count = jsonObj.getInt("count");
        if (gcid == 0 || time == null) {
            return Result.defContentErrorResult();
        }

        String uid = (String) params.get("uid");
        int gid = (Integer) params.get("gid");
        String content = (String) params.get("content");
        GrowthComment comment = new GrowthComment(cid, gid, gcid,
                Integer.parseInt(uid), content);
        comment.setTime(time);
        comment.setTotal(count);
        comment.setStatus(Status.NEW);
        Result ret = new Result();
        ret.setData(comment);
        return ret;
    }

}
