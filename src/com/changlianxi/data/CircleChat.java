package com.changlianxi.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.CircleChatDetailParser;
import com.changlianxi.data.parser.CircleChatParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;

/**
 * Circle Chat
 * 
 * Usage:
 * 
 * get a chat info:
 *     // new chat
 *     chat.read();
 *     // chat.get***()
 * 
 * refresh a chat's detail info:
 *     // new chat
 *     chat.read()
 *     chat.refresh(); // request and merge with local data
 *     chat.write();
 *     
 * 
 * send image:
 *    // new chat
 *    // ...set chat image...
 *    chat.sendImage();
 *    chat.write();
 *    
 * send text:
 *    // new chat
 *    // ...set chat content...
 *    chat.sendText();
 *    chat.write();
 *    
 *    
 * 
 * @author nnjme
 * 
 */
public class CircleChat extends AbstractChat {
    public final static String SEND_TEXT_API = "/chats/isend";
    public final static String SEND_IMAGE_API = "/chats/isendImg";
    public final static String DETAIL_API = "/chats/idetail";

    private int cid = 0; // circle id
    private int sender = 0; // sender uid

    public CircleChat(int cid, int chatId) {
        super(chatId);
        this.cid = cid;
    }

    public CircleChat(int cid, int chatId, int sender, String content) {
        super(chatId);
        this.cid = cid;
        this.sender = sender;
        this.content = content;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "CircleChat [cid=" + cid + ", sender=" + sender + ", chatId="
                + chatId + ", type=" + type + ", content=" + content
                + ", time=" + time + "]";
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.CIRCLE_CHAT_TABLE_NAME, new String[] {
                "cid", "sender", "type", "content", "time" }, "chatId=?",
                new String[] { this.chatId + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            int sender = cursor.getInt(cursor.getColumnIndex("sender"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String time = cursor.getString(cursor.getColumnIndex("time"));

            this.cid = cid;
            this.sender = sender;
            this.type = ChatType.convert(type);
            this.content = content;
            this.time = time;
        }
        cursor.close();

        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.CIRCLE_CHAT_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "chatId=?", new String[] { this.chatId + "" });
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("chatId", chatId);
        cv.put("cid", cid);
        cv.put("sender", sender);
        cv.put("type", type.name());
        cv.put("content", content);
        cv.put("time", time);

        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "chatId=?", new String[] { this.chatId + "" });
        }

        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof CircleChat)) {
            return;
        }

        CircleChat another = (CircleChat) data;
        boolean isChange = false;
        if (this.chatId != another.chatId) {
            this.chatId = another.chatId;
            isChange = true;
        }
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.type != another.type) {
            this.type = another.type;
            isChange = true;
        }
        if (this.sender != another.sender) {
            this.sender = another.sender;
            isChange = true;
        }
        if (!this.content.equals(another.content)) {
            this.content = another.content;
            isChange = true;
        }
        if (!this.time.equals(another.time)) {
            this.time = another.time;
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    /**
     * send a image chat to server, and reset local data info while upload
     * success
     * 
     * @param gImage
     * @return
     */
    public RetError sendImage() {
        File file = new File(content);
        if (file == null || !file.exists()) {
            return RetError.INVALID;
        }

        IParser parser = new CircleChatParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("type", ChatType.TYPE_IMAGE.name());

        Result ret = ApiRequest.uploadFileWithToken(CircleChat.SEND_IMAGE_API,
                params, file, "image", parser);
        file.delete();
        if (ret.getStatus() == RetStatus.SUCC) {
            this.update(ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * send a text chat to server, and reset local data info while upload
     * success
     * 
     * @return
     */
    public RetError sendText() {
        IParser parser = new CircleChatParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("content", content);
        params.put("type", ChatType.TYPE_TEXT.name());

        Result ret = ApiRequest.requestWithToken(CircleChat.SEND_TEXT_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            this.update(ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * refresh this chat info from server
     */
    public RetError refresh() {
        return this.refresh(this.chatId);
    }

    /**
     * refresh chat info with id from server
     * 
     * @param chatId
     */
    public RetError refresh(int chatId) {
        IParser parser = new CircleChatDetailParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("cmid", chatId);
        Result ret = ApiRequest.requestWithToken(CircleChat.DETAIL_API, params,
                parser);

        if (ret.getStatus() == RetStatus.SUCC) {
            this.update(ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

}
