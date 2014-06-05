package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.PersonChat;
import com.changlianxi.data.PersonChatList;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;

public class PersonChatListParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        int partner = jsonObj.getInt("ruid");
        int total = jsonObj.getInt("total");
        int requestTime = jsonObj.getInt("current");
        JSONArray jsonArr = jsonObj.getJSONArray("messages");
        if (jsonArr == null || partner == 0) {
            return Result.defContentErrorResult();
        }

        List<PersonChat> chats = new ArrayList<PersonChat>();
        long start = 0L, end = 0L;
        for (int i = jsonArr.length() - 1; i >= 0; i--) {
            JSONObject obj = (JSONObject) jsonArr.opt(i);
            int chatId = obj.getInt("id");
            int cid = obj.getInt("cid");
            int sender = obj.getInt("uid");
            String type = obj.getString("type");
            String content = obj.getString("content");
            String time = obj.getString("time");
            int isRead = obj.getInt("isread");
            PersonChat chat = new PersonChat(cid, partner, chatId, sender,
                    content);
            chat.setType(ChatType.convert(type));
            chat.setTime(time);
            chat.setRead(isRead > 0);
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

        PersonChatList cl = new PersonChatList(partner);
        cl.setChats(chats);
        cl.setTotal(total);
        cl.setStartTime(start);
        cl.setEndTime(end);
        if (total < chats.size()) {
            cl.setLastReqTime(end);
        } else {
            cl.setLastReqTime(requestTime);
        }

        Result ret = new Result();
        ret.setData(cl);
        return ret;
    }

}
