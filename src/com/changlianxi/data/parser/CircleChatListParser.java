package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.CircleChat;
import com.changlianxi.data.CircleChatList;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;

public class CircleChatListParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        int cid = jsonObj.getInt("cid");
        int total = jsonObj.getInt("total");
        int requestTime = jsonObj.getInt("current");
        JSONArray jsonArr = jsonObj.getJSONArray("chats");
        if (jsonArr == null || cid == 0) {
            return Result.defContentErrorResult();
        }

        List<CircleChat> chats = new ArrayList<CircleChat>();
        long start = 0L, end = 0L;
        for (int i = jsonArr.length() - 1; i > 0; i--) {
            JSONObject obj = (JSONObject) jsonArr.opt(i);
            int chatId = obj.getInt("id");
            String type = obj.getString("type");
            int sender = obj.getInt("sender");
            String content = obj.getString("content");
            String time = obj.getString("time");

            CircleChat chat = new CircleChat(cid, chatId, sender, content);
            chat.setType(ChatType.convert(type));
            chat.setTime(time);
            chat.setStatus(Status.NEW);

            chats.add(chat);
            long tmp = DateUtils.convertToDate(time);
            if (end == 0 || tmp > end) {
                end = tmp;
            }
            if (start == 0 || tmp < start) {
                tmp = start;
            }
        }

        CircleChatList cl = new CircleChatList(cid);
        cl.setChats(chats);
        cl.setLastReqTime(requestTime);
        cl.setTotal(total);
        cl.setStartTime(start);
        cl.setEndTime(end);
        Result ret = new Result();
        ret.setData(cl);
        return ret;
    }

}
