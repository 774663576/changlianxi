package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.CircleChat;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.request.Result;

public class CircleChatDetailParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        int cid = (Integer) params.get("cid");
        if (!jsonObj.has("chat")) {
            return Result.defContentErrorResult();
        }

        JSONObject jsonChat = jsonObj.getJSONObject("chat");
        if (jsonChat == null) {
            return Result.defContentErrorResult();
        }

        int cmid = jsonChat.getInt("id");
        String type = jsonChat.getString("type");
        int sender = jsonChat.getInt("sender");
        String content = jsonChat.getString("content");
        String time = jsonChat.getString("time");
        ChatType cType = ChatType.convert(type);

        CircleChat chat = new CircleChat(cid, cmid, sender, content);
        chat.setTime(time);
        chat.setType(cType);
        chat.setStatus(Status.NEW);
        Result ret = new Result();
        ret.setData(chat);
        return ret;
    }

}
