package com.changlianxi.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.PersonChat;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.db.DBUtils;

public class ChatParser {
    /**
        * 解析聊天内容
        * 
        * @param content
        */
    public static PersonChat getChatModle(String str) {
        String content = "";
        String time = "";
        int uid = 0;
        int cid = 0;
        ChatType type = ChatType.TYPE_TEXT;
        try {
            JSONObject json = new JSONObject(str);
            content = json.getString("c");
            time = json.getString("m");
            uid = json.getInt("uid");
            if (String.valueOf(uid).equals(SharedUtils.getString("uid", ""))) {
                return null;
            }
            cid = json.getInt("cid");
            String ct = json.getString("ct");
            if (ct.equals("TYPE_IMAGE")) {
                type = ChatType.TYPE_IMAGE;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PersonChat chat = new PersonChat(cid, uid, 0, uid, content);
        chat.setTime(time);
        chat.setType(type);
        chat.setStatus(Status.NEW);
        chat.write(DBUtils.getDBsa(2));
        return chat;
    }

}
