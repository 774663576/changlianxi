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
import com.changlianxi.data.parser.ChatPartnerListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Person's Chat Partners List
 * 
 * Usage:
 * 
 * get partner list:
 *     // new ChatPartnerList cpl
 *     cpl.read();
 *     cpl.getPartners();
 * 
 * refresh partner list:
 *     // new ChatPartnerList cpl
 *     cpl.read();
 *     ...
 *     cpl.refresh(); // get new partners
 *     
 *     ...
 *     cpl.write();
 *     
 *     
 * @author jieme
 *
 */
public class ChatPartnerList extends AbstractData {
    public final static String LIST_API = "/messages/ilist";

    private long startTime = 0L; // data start time, in milliseconds
    private long endTime = 0L; // data end time
    private long lastReqTime = 0L; // last request time of chat data
    private int total = 0;
    private List<ChatPartner> partners = new ArrayList<ChatPartner>();
    private String pub = "0";// 是否需要你公共帐号私信结果，1：需要，0：不需要，

    public ChatPartnerList() {
    }

    public long getStartTime() {
        return startTime;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
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

    public List<ChatPartner> getPartners() {
        return partners;
    }

    public void setPartners(List<ChatPartner> partners) {
        this.partners = partners;
    }

    private void sort(boolean byTimeAsc) {
        Collections.sort(this.partners, ChatPartner.getComparator(byTimeAsc));
    }

    @Override
    public void read(SQLiteDatabase db) {
        if (this.partners == null) {
            this.partners = new ArrayList<ChatPartner>();
        } else {
            this.partners.clear();
        }

        Cursor cursor = db.query(Const.CHAT_PARTNER_TABLE_NAME, new String[] {
                "cid", "chatId", "partner", "partnerName", "type", "content",
                "time", "unReadCnt" }, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int chatId = cursor.getInt(cursor.getColumnIndex("chatId"));
                int cid = cursor.getInt(cursor.getColumnIndex("cid"));
                int partnerId = cursor.getInt(cursor.getColumnIndex("partner"));
                String partnerName = cursor.getString(cursor
                        .getColumnIndex("partnerName"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String content = cursor.getString(cursor
                        .getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                int unReadCnt = cursor.getInt(cursor
                        .getColumnIndex("unReadCnt"));

                ChatPartner partner = new ChatPartner(cid, partnerId, chatId,
                        content);
                partner.setPartnerName(partnerName);
                partner.setType(ChatType.convert(type));
                partner.setTime(time);
                partner.setUnReadCnt(unReadCnt);
                partner.setStatus(Status.OLD);
                this.partners.add(partner);

                long t = DateUtils.convertToDate(time);
                if (startTime == 0 || t < startTime) {
                    startTime = t;
                }
                if (endTime == 0 || t > endTime) {
                    endTime = t;
                }

                cursor.moveToNext();
            }
        }
        cursor.close();

        // read last request time
        Cursor cursor2 = db.query(Const.TIME_RECORD_TABLE_NAME,
                new String[] { "time" }, "key=? and subkey=?",
                new String[] { Const.TIME_RECORD_KEY_PREFIX_PARTNERS,
                        "last_req_time" }, null, null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            long time = cursor2.getLong(cursor2.getColumnIndex("time"));
            this.lastReqTime = time;
        }
        cursor2.close();

        sort(false);
        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        if (this.status != Status.OLD) {
            // write one by one

            for (ChatPartner partner : partners) {
                partner.write(db);
            }

            // write last request time
            ContentValues cv = new ContentValues();
            cv.put("time", lastReqTime);
            int affected = db.update(Const.TIME_RECORD_TABLE_NAME, cv,
                    "key=? and subkey=?", new String[] {
                            Const.TIME_RECORD_KEY_PREFIX_PARTNERS,
                            "last_req_time" });
            if (affected == 0) {
                cv.put("key", Const.TIME_RECORD_KEY_PREFIX_PARTNERS);
                cv.put("subkey", "last_req_time");
                db.insert(Const.TIME_RECORD_TABLE_NAME, null, cv);
            }

            this.status = Status.OLD;
        }
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void update(IData data) {
        if (!(data instanceof ChatPartnerList)) {
            return;
        }

        ChatPartnerList another = (ChatPartnerList) data;
        if (another.partners.size() == 0) {
            return;
        }

        // old ones
        Map<Integer, ChatPartner> olds = new HashMap<Integer, ChatPartner>();
        for (ChatPartner partner : this.partners) {
            olds.put(partner.getPartner(), partner);
        }

        for (ChatPartner partner : another.partners) {
            int partnerId = partner.getPartner();
            if (olds.containsKey(partnerId)) {
                // update
                olds.get(partnerId).update(partner);
            } else {
                // new
                this.partners.add(partner);
            }
        }

        // update start/end time and last request time
        this.startTime = Math.min(this.startTime, another.getStartTime());
        this.endTime = Math.max(this.endTime, another.getEndTime());
        this.lastReqTime = another.lastReqTime;

        this.status = Status.UPDATE;
        sort(false);
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
        refreshAllPartners(startTime, 0L);
    }

    /**
     * refresh all chat partners list from server, it will do repeat
     * refresh request until all data is fetched.
     * 
     * @param startTime
     * @param endTime
     */
    public void refreshAllPartners(long startTime, long endTime) {
        while (true) {
            ChatPartnerList cpl = null;
            Result ret = request(startTime, endTime);
            if (ret != null && ret.getStatus() == RetStatus.SUCC) {
                cpl = (ChatPartnerList) ret.getData();
            } else {
                break;
            }

            // update for data merge
            update(cpl);

            if (cpl.getTotal() <= cpl.getPartners().size()) {
                break;
            }
            if (cpl.getPartners().size() == 0) {
                break;
            }
            startTime = cpl.getEndTime() / 1000 + 1;
        }
    }

    private Result request(long startTime, long endTime) {
        IParser parser = new ChatPartnerListParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pub", this.pub);
        if (startTime > 0) {
            params.put("start", startTime);
        }
        if (endTime > 0) {
            params.put("end", endTime);
        }
        Result ret = ApiRequest.requestWithToken(LIST_API, params, parser);
        return ret;
    }

    /**
     * refresh new chats list from server, with start and end time
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public RetError refresh(long startTime, long endTime) {
        Result ret = request(startTime, endTime);
        if (ret.getStatus() == RetStatus.SUCC) {
            update((ChatPartnerList) ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

}
