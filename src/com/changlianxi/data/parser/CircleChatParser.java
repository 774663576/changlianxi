package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.CircleChat;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.request.Result;

public class CircleChatParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        int cid = (Integer) params.get("cid");
        int uid = (Integer) params.get("uid");
        ChatType type = ChatType.convert((String) params.get("type"));

        if (!jsonObj.has("cmid") || !jsonObj.has("time")) {
            return Result.defContentErrorResult();
        }
        String time = jsonObj.getString("time");
        int cmid = jsonObj.getInt("cmid");
        if (time == null || cmid == 0) {
            return Result.defContentErrorResult();
        }

        String content = "";
        if (type == ChatType.TYPE_IMAGE) {
            if (!jsonObj.has("image")) {
                return Result.defContentErrorResult();
            } else {
                content = jsonObj.getString("image");
            }
        } else if (type == ChatType.TYPE_TEXT) {
            content = (String) params.get("content");
        } else {
            return Result.defContentErrorResult();
        }

        CircleChat chat = new CircleChat(cid, cmid, uid, content);
        chat.setTime(time);
        chat.setType(type);
        chat.setStatus(Status.NEW);
        Result ret = new Result();
        ret.setData(chat);
        return ret;
    }

}
