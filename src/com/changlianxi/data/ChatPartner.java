package com.changlianxi.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.ChatType;
import com.changlianxi.db.Const;

/**
 * Chat Partner
 * 
 * Usage:
 * 
 * get a partner info:
 *     // new ChatPartner
 *     partner.read();
 *     // partner.get***()
 * 
 * @author nnjme
 * 
 */
public class ChatPartner extends AbstractChat {
    private int cid = 0; // circle id
    private int partner = 0; // the other user id, who chat with me
    private String partnerName = "";
    private int unReadCnt = 0; // my unread chat count

    public ChatPartner(int cid, int partner, int chatId) {
        super(chatId);
        this.cid = cid;
        this.partner = partner;
    }

    public ChatPartner(int cid, int partner, int chatId, String content) {
        super(chatId);
        this.cid = cid;
        this.partner = partner;
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

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public int getUnReadCnt() {
        return unReadCnt;
    }

    public void setUnReadCnt(int unReadCnt) {
        this.unReadCnt = unReadCnt;
    }

    @Override
    public String toString() {
        return "PersonChatPartner [cid=" + cid + ", partner=" + partner
                + ", partnerName=" + partnerName + ", chatId=" + chatId
                + ", type=" + type + ", content=" + content + ", time=" + time + "]";
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.CHAT_PARTNER_TABLE_NAME, new String[] {
                "cid", "partnerName", "chatId", "type", "content", "time", "unReadCnt" },
                "partner=?", new String[] { this.partner + "" }, null, null,
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            String partnerName = cursor.getString(cursor.getColumnIndex("partnerName"));
            int chatId = cursor.getInt(cursor.getColumnIndex("chatId"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int unReadCnt = cursor.getInt(cursor.getColumnIndex("unReadCnt"));

            this.cid = cid;
            this.partnerName = partnerName;
            this.chatId = chatId;
            this.type = ChatType.convert(type);
            this.content = content;
            this.time = time;
            this.unReadCnt = unReadCnt;
        }
        cursor.close();

        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.CHAT_PARTNER_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "partner=?", new String[] { this.partner + "" });
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("chatId", chatId);
        cv.put("cid", cid);
        cv.put("partner", partner);
        cv.put("partnerName", partnerName);
        cv.put("type", type.name());
        cv.put("content", content);
        cv.put("time", time);
        cv.put("unReadCnt", unReadCnt);

        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "partner=?",
                    new String[] { this.partner + "" });
        }

        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof ChatPartner)) {
            return;
        }

        ChatPartner another = (ChatPartner) data;
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
        if (!this.partnerName.equals(another.partnerName)) {
            this.partnerName = another.partnerName;
            isChange = true;
        }
        if (this.type != another.type) {
            this.type = another.type;
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
        if (this.unReadCnt != another.unReadCnt) {
            this.unReadCnt = another.unReadCnt;
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

}
