package com.changlianxi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.GrowthCommentForMeListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Comment List for me
 * 
 * @author jieme
 * 
 */
public class GrowthCommentForMeList extends GrowthCommentList {
    private static final long serialVersionUID = 878132514106515486L;
    public final static String LIST_API = "/growth/icommentsForMe";

    public GrowthCommentForMeList(int cid) {
        super(cid, 0);
    }

    @Override
    public void read(SQLiteDatabase db) {
        if (comments == null) {
            comments = new ArrayList<GrowthComment>();
        } else {
            comments.clear();
        }

        // read comments
        Cursor cursor = db.query(Const.GROWTH_COMMENT_TABLE_NAME, new String[] {
                "gid", "gcid", "uid", "replyid", "content", "time" },
                "isForme=?", new String[] { "1" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int gid = cursor.getInt(cursor.getColumnIndex("gid"));
                int gcid = cursor.getInt(cursor.getColumnIndex("gcid"));
                int uid = cursor.getInt(cursor.getColumnIndex("uid"));
                int replyid = cursor.getInt(cursor.getColumnIndex("replyid"));
                String content = cursor.getString(cursor
                        .getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));

                GrowthComment comment = new GrowthComment(cid, gid, gcid, uid,
                        content, true);
                comment.setReplyid(replyid);
                comment.setTime(time);
                comment.setStatus(Status.OLD);
                comments.add(comment);

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

        // read last request times
        Cursor cursor2 = db.query(Const.TIME_RECORD_TABLE_NAME,
                new String[] { "time" }, "key=? and subkey=?", new String[] {
                        Const.TIME_RECORD_KEY_PREFIX_COMMENTSFORME + this.cid,
                        "last_req_time" }, null, null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            long time = cursor2.getLong(cursor2.getColumnIndex("time"));
            this.lastReqTime = time;
        }
        cursor2.close();

        this.status = Status.OLD;
        sort(false);
    }

    @Override
    public void write(SQLiteDatabase db) {
        if (this.status != Status.OLD) {
            // write one by one
            int cacheCnt = 0;
            for (GrowthComment comment : comments) {
                if (comment.getStatus() != Status.DEL) {
                    cacheCnt++;
                }
                if (cacheCnt > Const.GROWTH_COMMENT_MAX_CACHE_COUNT_PER_ITEM) {
                    comment.setStatus(Status.DEL);
                }
                comment.write(db);
            }

            // write last request time
            ContentValues cv = new ContentValues();
            cv.put("time", lastReqTime);
            int affected = db.update(Const.TIME_RECORD_TABLE_NAME, cv,
                    "key=? and subkey=?", new String[] {
                            Const.TIME_RECORD_KEY_PREFIX_COMMENTSFORME
                                    + this.cid, "last_req_time" });
            if (affected == 0) {
                cv.put("key", Const.TIME_RECORD_KEY_PREFIX_COMMENTSFORME
                        + this.cid);
                cv.put("subkey", "last_req_time");
                db.insert(Const.TIME_RECORD_TABLE_NAME, null, cv);
            }

            this.status = Status.OLD;
        }
    }

    @Override
    public RetError refresh(long startTime, long endTime) {
        IParser parser = new GrowthCommentForMeListParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        if (startTime > 0) {
            params.put("start", startTime);
        }
        if (endTime > 0) {
            params.put("end", endTime);
        }

        Result ret = ApiRequest.requestWithToken(
                GrowthCommentForMeList.LIST_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            update((GrowthCommentForMeList) ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

}
