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
import com.changlianxi.data.parser.CircleChatListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Chat List of a circle
 * 
 * Usage:
 * 
 * get a circle's chat list
 *     // new CircleChatList cl
 *     cl.read();
 *     cl.getChats();
 * 
 * refresh a circle's chat list
 *     // new CircleChatList cl
 *     cl.read();
 *     ...
 *     cl.refresh(); // get new chats
 *     
 *     ...
 *     cl.write();
 *     
 *     
 * @author jieme
 *
 */
public class CircleChatList extends AbstractData {
    public final static String LIST_API = "/chats/ilist";

    private int cid = 0;
    private long startTime = 0L; // data start time, in milliseconds
    private long endTime = 0L; // data end time
    private long lastReqTime = 0L; // last request time of chat data
    private int total = 0;

    private List<CircleChat> chats = null;

    public CircleChatList(int cid) {
        this.cid = cid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
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

    public List<CircleChat> getChats() {
        return chats;
    }

    public void setChats(List<CircleChat> chats) {
        this.chats = chats;
    }

    private void sort(boolean byTimeAsc) {
        Collections.sort(this.chats, CircleChat.getComparator(byTimeAsc));
    }

    @Override
    public void read(SQLiteDatabase db) {
        if (this.chats == null) {
            this.chats = new ArrayList<CircleChat>();
        } else {
            this.chats.clear();
        }

        Cursor cursor = db.query(Const.CIRCLE_CHAT_TABLE_NAME, new String[] {
                "chatId", "sender", "type", "content", "time" }, "cid=?",
                new String[] { this.cid + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            long start = 0, end = 0;
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int chatId = cursor.getInt(cursor.getColumnIndex("chatId"));
                int sender = cursor.getInt(cursor.getColumnIndex("sender"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String content = cursor.getString(cursor
                        .getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));

                CircleChat chat = new CircleChat(cid, chatId, sender, content);
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
        Cursor cursor2 = db.query(Const.TIME_RECORD_TABLE_NAME,
                new String[] { "time" }, "key=? and subkey=?", new String[] {
                        Const.TIME_RECORD_KEY_PREFIX_CIRCLECHAT + this.cid,
                        "last_req_time" }, null, null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            long time = cursor2.getLong(cursor2.getColumnIndex("time"));
            this.lastReqTime = time;
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
            for (CircleChat chat : chats) {
                if (chat.getStatus() != Status.DEL) {
                    cacheCnt++;
                }
                if (cacheCnt > Const.CHAT_MAX_CACHE_COUNT_PER_CIRCLE) {
                    chat.setStatus(Status.DEL);
                }
                chat.write(db);
            }

            // write last request time
            ContentValues cv = new ContentValues();
            cv.put("time", lastReqTime);
            int affected = db.update(Const.TIME_RECORD_TABLE_NAME, cv,
                    "key=? and subkey=?", new String[] {
                            Const.TIME_RECORD_KEY_PREFIX_CIRCLECHAT + this.cid,
                            "last_req_time" });
            if (affected == 0) {
                cv.put("key", Const.TIME_RECORD_KEY_PREFIX_CIRCLECHAT
                        + this.cid);
                cv.put("subkey", "last_req_time");
                db.insert(Const.TIME_RECORD_TABLE_NAME, null, cv);
            }

            this.status = Status.OLD;
        }
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void update(IData data) {
        if (!(data instanceof CircleChatList)) {
            return;
        }

        CircleChatList another = (CircleChatList) data;
        if (another.chats.size() == 0) {
            return;
        }

        boolean isNewer = true; // another list is newer than current list
        if (another.endTime <= this.startTime) {
            isNewer = false;
        }

        // old ones
        Map<Integer, CircleChat> olds = new HashMap<Integer, CircleChat>();
        for (CircleChat chat : this.chats) {
            olds.put(chat.getChatId(), chat);
        }

        // join new ones
        boolean canJoin = false;
        Map<Integer, CircleChat> news = new HashMap<Integer, CircleChat>();
        for (CircleChat chat : another.chats) {
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
        IParser parser = new CircleChatListParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        if (startTime > 0) {
            params.put("start", startTime);
        }
        if (endTime > 0) {
            params.put("end", endTime);
        }

        Result ret = ApiRequest.requestWithToken(CircleChatList.LIST_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            update((CircleChatList) ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public void insert(CircleChat chat) {
        CircleChatList ccl = new CircleChatList(chat.getCid());
        List<CircleChat> chats = new ArrayList<CircleChat>();
        chats.add(chat);
        ccl.setChats(chats);
        long time = DateUtils.convertToDate(chat.getTime());
        ccl.setLastReqTime(time);
        ccl.setTotal(1);
        ccl.setStartTime(time);
        ccl.setEndTime(time);

        update(ccl);
    }

}
