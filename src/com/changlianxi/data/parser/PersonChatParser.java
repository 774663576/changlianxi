package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.PersonChat;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.request.Result;

public class PersonChatParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }
        System.out.println("json::::::::::::::" + jsonObj);
        int cid = (Integer) params.get("cid");
        int uid = (Integer) params.get("uid");
        int partner = (Integer) params.get("ruid");
        ChatType type = ChatType.convert((String) params.get("type"));

        if (!jsonObj.has("mid") || !jsonObj.has("time")) {
            return Result.defContentErrorResult();
        }
        String time = jsonObj.getString("time");
        int mid = jsonObj.getInt("mid");
        if (time == null || mid == 0) {
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

        PersonChat chat = new PersonChat(mid, partner, cid, uid, content);
        chat.setTime(time);
        chat.setType(type);
        chat.setStatus(Status.NEW);
        Result ret = new Result();
        ret.setData(chat);
        return ret;
    }

}
