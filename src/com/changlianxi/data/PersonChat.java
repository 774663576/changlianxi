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
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.PersonChatParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;

/**
 * Person Chat
 * 
 * Usage:
 * 
 * get a chat info:
 *     // new chat
 *     chat.read();
 *     // chat.get***()
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
 * @author nnjme
 * 
 */
public class PersonChat extends AbstractChat {
    public final static String SEND_TEXT_API = "/messages/isend";
    public final static String SEND_IMAGE_API = "/messages/isendImg";

    private int cid = 0; // circle id
    private int partner = 0; // the other user id, who i chat with
    private int sender = 0; // sender uid, me or the partner
    private boolean isRead = true;

    public PersonChat(int cid, int partner, int chatId) {
        super(chatId);
        this.cid = cid;
        this.partner = partner;
    }

    public PersonChat(int cid, int partner, int chatId, int sender,
            String content) {
        super(chatId);
        this.cid = cid;
        this.partner = partner;
        this.sender = sender;
        this.content = content;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getPartner() {
        return partner;
    }

    public void setPartner(int partner) {
        this.partner = partner;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public String toString() {
        return "PersonChat [cid=" + cid + ", partner=" + partner + ", sender="
                + sender + ", chatId=" + chatId + ", type=" + type
                + ", content=" + content + ", time=" + time + "]";
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.PERSON_CHAT_TABLE_NAME,
                new String[] { "cid", "partner", "sender", "type", "content",
                        "time", "isRead" }, "chatId=?",
                new String[] { this.chatId + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            int partner = cursor.getInt(cursor.getColumnIndex("partner"));
            int sender = cursor.getInt(cursor.getColumnIndex("sender"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int isRead = cursor.getInt(cursor.getColumnIndex("isRead"));

            this.cid = cid;
            this.partner = partner;
            this.sender = sender;
            this.type = ChatType.convert(type);
            this.content = content;
            this.time = time;
            this.isRead = (isRead > 0);
        }
        cursor.close();

        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.PERSON_CHAT_TABLE_NAME;
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
        cv.put("partner", partner);
        cv.put("sender", sender);
        cv.put("type", type.name());
        cv.put("content", content);
        cv.put("time", time);
        cv.put("isRead", isRead ? 1 : 0);

        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "chatId=?", new String[] { this.chatId + "" });
        }

        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof PersonChat)) {
            return;
        }

        PersonChat another = (PersonChat) data;
        boolean isChange = false;
        if (this.chatId != another.chatId) {
            this.chatId = another.chatId;
            isChange = true;
        }
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.partner != another.partner) {
            this.partner = another.partner;
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
        if (this.isRead != another.isRead) {
            this.isRead = another.isRead;
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

        IParser parser = new PersonChatParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("ruid", partner);
        params.put("type", ChatType.TYPE_IMAGE.name());

        Result ret = ApiRequest.uploadFileWithToken(PersonChat.SEND_IMAGE_API,
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
        IParser parser = new PersonChatParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("ruid", partner);
        params.put("content", content);
        params.put("type", ChatType.TYPE_TEXT.name());

        Result ret = ApiRequest.requestWithToken(PersonChat.SEND_TEXT_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            this.update(ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

}
