package com.changlianxi.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.PersonChatListParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Person's Chat List
 * 
 * Usage: 
 * 
 * get chat list:
 *     // new PersonChatList pcl
 *     pcl.read();
 *     pcl.getChats();
 * 
 * refresh chat list:
 *     // new PersonChatList pcl
 *     pcl.read();
 *     ...
 *     pcl.refresh(); // get new chats
 *     
 *     ...
 *     pcl.write();
 *     
 *     
 * @author jieme
 *
 */
public class PersonChatList extends AbstractData {
    public final static String LIST_API = "/messages/imessages";

    private int partner = 0;
    private long startTime = 0L; // data start time, in milliseconds
    private long endTime = 0L; // data end time
    private long lastReqTime = 0L; // last request time of chat data
    private int total = 0;
    private List<PersonChat> chats = new ArrayList<PersonChat>();

    public PersonChatList(int partner) {
        this.partner = partner;
    }

    public int getPartner() {
        return partner;
    }

    public void setPartner(int partner) {
        this.partner = partner;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getLastReqTime() {
        return lastReqTime;
    }

    public void setLastReqTime(long lastReqTime) {
        this.lastReqTime = lastReqTime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<PersonChat> getChats() {
        return chats;
    }

    public void setChats(List<PersonChat> chats) {
        this.chats = chats;
    }

    private void sort(boolean byTimeAsc) {
        Collections.sort(this.chats, PersonChat.getComparator(byTimeAsc));
    }

    @Override
    public void read(SQLiteDatabase db) {
        if (this.chats == null) {
            this.chats = new ArrayList<PersonChat>();
        } else {
            this.chats.clear();
        }

        Cursor cursor = db.query(Const.PERSON_CHAT_TABLE_NAME, new String[] {
                "chatId", "cid", "sender", "type", "content", "time" },
                "partner=?", new String[] { this.partner + "" }, null, null,
                null);
        if (cursor.getCount() > 0) {
            long start = 0, end = 0;
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int chatId = cursor.getInt(cursor.getColumnIndex("chatId"));
                int cid = cursor.getInt(cursor.getColumnIndex("cid"));
                int sender = cursor.getInt(cursor.getColumnIndex("sender"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String content = cursor.getString(cursor
                        .getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));

                PersonChat chat = new PersonChat(cid, partner, chatId, sender,
                        content);
                chat.setType(ChatType.convert(type));
                chat.setTime(time);
                chat.setStatus(Status.OLD);
                this.chats.add(chat);

                long tmp = DateUtils.convertToDate(time);
                if (start == 0 || tmp < start) {
                    start = tmp;
                }
                if (end == 0 || tmp > end) {
                    end = tmp;
                }
                cursor.moveToNext();
            }
            this.startTime = start;
            this.endTime = end;
        }
        cursor.close();

        // read last request times
        Cursor cursor2 = db.query(Const.CHAT_PARTNER_TABLE_NAME,
                new String[] { "lastChatsReqTime" }, "partner=?",
                new String[] { this.partner + "" }, null, null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            long lastReqTime = cursor2.getLong(cursor2
                    .getColumnIndex("lastChatsReqTime"));
            this.lastReqTime = lastReqTime;
        }
        cursor2.close();

        this.status = Status.OLD;
        sort(true);
    }

    @Override
    public void write(SQLiteDatabase db) {
        if (this.status != Status.OLD) {
            // write one by one
            int cacheCnt = 0;
            for (PersonChat chat : chats) {
                if (chat.getStatus() != Status.DEL) {
                    cacheCnt++;
                }
                if (cacheCnt > Const.CHAT_MAX_CACHE_COUNT_PER_PERSON) {
                    chat.setStatus(Status.DEL);
                }
                chat.write(db);
            }

            // write last request time
            ContentValues cv = new ContentValues();
            cv.put("lastChatsReqTime", lastReqTime);
            db.update(Const.CHAT_PARTNER_TABLE_NAME, cv, "partner=?",
                    new String[] { this.partner + "" });

            this.status = Status.OLD;
        }
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void update(IData data) {
        if (!(data instanceof PersonChatList)) {
            return;
        }

        PersonChatList another = (PersonChatList) data;
        if (another.chats.size() == 0) {
            return;
        }

        boolean isNewer = true; // another list is newer than current list
        if (another.endTime <= this.startTime) {
            isNewer = false;
        }

        // old ones
        Map<Integer, PersonChat> olds = new HashMap<Integer, PersonChat>();
        for (PersonChat chat : this.chats) {
            olds.put(chat.getChatId(), chat);
        }

        // join new ones
        boolean canJoin = false;
        Map<Integer, PersonChat> news = new HashMap<Integer, PersonChat>();
        for (PersonChat chat : another.chats) {
            int chatId = chat.getChatId();
            news.put(chatId, chat);

            if (olds.containsKey(chatId)) {
                olds.get(chatId).update(chat);
                canJoin = true;
            } else {
                this.chats.add(chat);
            }
        }

        if (isNewer) {
            this.lastReqTime = another.lastReqTime;
            if (another.total == another.getChats().size()) {
                canJoin = true;
            }

            if (!canJoin) {
                for (int gid : olds.keySet()) {
                    if (news.containsKey(gid)) {
                        olds.get(gid).setStatus(Status.DEL);
                    }
                }
                this.startTime = another.startTime;
            }
            this.endTime = another.endTime;
        } else {
            this.startTime = another.startTime;
        }

        this.status = Status.UPDATE;
        sort(true);
    }

    /**
     * refresh new chats list from server for the first time
     */
    public void refresh() {
        refresh(0);
    }

    /**
     * refresh new chats list from server, with start time
     * 
     * @param startTime
     */
    public void refresh(long startTime) {
        refresh(startTime, 0);
    }

    /**
     * refresh new chats list from server, with start and end time
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public RetError refresh(long startTime, long endTime) {
        IParser parser = new PersonChatListParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ruid", partner);
        if (startTime > 0) {
            params.put("start", startTime);
        }
        if (endTime > 0) {
            params.put("end", endTime);
        }

        Result ret = ApiRequest.requestWithToken(PersonChatList.LIST_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            update((PersonChatList) ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }
    
    public void insert(PersonChat chat) {
        PersonChatList pcl = new PersonChatList(chat.getPartner());
        List<PersonChat> chats = new ArrayList<PersonChat>();
        chats.add(chat);
        pcl.setChats(chats);
        long time = DateUtils.convertToDate(chat.getTime());
        pcl.setLastReqTime(time);
        pcl.setTotal(1);
        pcl.setStartTime(time);
        pcl.setEndTime(time);

        update(pcl);
    }

}
