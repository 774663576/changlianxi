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

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.GrowthListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Growth List of a circle
 * 
 * Usage:
 * 
 * get a circle's growth list:
 *  // new GrowthList gl 
 *  gl.read(); 
 *  gl.getGrowths();
 * 
 * refresh a circle's growth list:
 *  // new GrowthList gl 
 *  gl.read();
 *  // ...
 *  gl.refresh(); // get new growths
 *  // ... 
 *  gl.write();
 * 
 * 
 * @author jieme
 * 
 */
public class GrowthList extends AbstractData {
    public final static String LIST_API = "/growth/ilist";

    private int cid = 0;
    private long startTime = 0L; // data start time, in milliseconds
    private long endTime = 0L; // data end time
    private long lastReqTime = 0L; // last request time of growth data
    private int total = 0;
    private List<Growth> growths = new ArrayList<Growth>();
    private List<Integer> delIds = new ArrayList<Integer>(); // deleted growth
                                                             // ids
    private int serverCount = -1;// 服务器返回的当前请求结果条数

    public GrowthList(int cid) {
        this.cid = cid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getServerCount() {
        return serverCount;
    }

    public void setServerCount(int serverCount) {
        this.serverCount = serverCount;
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

    public List<Growth> getGrowths() {
        for (int i = growths.size() - 1; i >= 0; i--) {
            if (growths.get(i).getId() == 0) {
                growths.remove(i);
            }
        }
        return growths;
    }

    public void setGrowths(List<Growth> growths) {
        if (growths != null) {
            this.growths = growths;
        }
    }

    public List<Integer> getDelIds() {
        return delIds;
    }

    public void setDelIds(List<Integer> delIds) {
        this.delIds = delIds;
    }

    private void sort(boolean byTimeAsc) {
        Collections.sort(this.growths, Growth.getPublishedComparator(false));
    }

    @Override
    public void read(SQLiteDatabase db) {
        if (growths == null) {
            growths = new ArrayList<Growth>();
        } else {
            growths.clear();
        }

        // read ids
        Cursor cursor = db.query(Const.GROWTH_TABLE_NAME,
                new String[] { "id" }, "cid=?", new String[] { this.cid + "" },
                null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int gid = cursor.getInt(cursor.getColumnIndex("id"));
                Growth growth = new Growth(cid, gid);
                growths.add(growth);
                cursor.moveToNext();
            }
        }
        cursor.close();

        // read one by one
        for (Growth growth : growths) {
            growth.read(db);

            long joinTime = DateUtils.convertToDate(growth.getPublished());
            if (joinTime > 0) {
                joinTime /= 1000;
            }
            if (startTime == 0 || joinTime < startTime) {
                startTime = joinTime;
            }
            if (endTime == 0 || joinTime > endTime) {
                endTime = joinTime;
            }
        }
        // read last request times
        Cursor cursor2 = db.query(Const.TIME_RECORD_TABLE_NAME,
                new String[] { "time" }, "key=? and subkey=?", new String[] {
                        Const.TIME_RECORD_KEY_PREFIX_GROWTH + this.cid,
                        "last_req_time" }, null, null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            long time = cursor2.getLong(cursor2.getColumnIndex("time"));
            // if (time > 0) {
            // time /= 1000;
            // }
            this.lastReqTime = time;
        }
        cursor2.close();

        this.status = Status.OLD;
        sort(true);
    }

    @Override
    public void write(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            if (this.status != Status.OLD) {
                // write one by one
                int cacheCnt = 0;
                for (Growth growth : growths) {
                    if (growth.getStatus() != Status.DEL) {
                        cacheCnt++;
                    }
                    if (cacheCnt > Const.GROWTH_MAX_CACHE_COUNT_PER_CIRCLE) {
                        growth.setStatus(Status.DEL);
                    }
                    growth.write(db);
                }

                // write last request time
                ContentValues cv = new ContentValues();
                cv.put("time", lastReqTime);
                int affected = db.update(Const.TIME_RECORD_TABLE_NAME, cv,
                        "key=? and subkey=?", new String[] {
                                Const.TIME_RECORD_KEY_PREFIX_GROWTH + this.cid,
                                "last_req_time" });
                if (affected == 0) {
                    cv.put("key", Const.TIME_RECORD_KEY_PREFIX_GROWTH
                            + this.cid);
                    cv.put("subkey", "last_req_time");
                    db.insert(Const.TIME_RECORD_TABLE_NAME, null, cv);
                }

                this.status = Status.OLD;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            // process it
            e.printStackTrace();
        } finally {
            // end a transaction
            db.endTransaction();
        }
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void update(IData data) {
        if (!(data instanceof GrowthList)) {
            return;
        }

        GrowthList another = (GrowthList) data;
        if (another.growths.size() == 0) {
            return;
        }
        this.serverCount = another.getGrowths().size();

        boolean isNewer = true; // another list is newer than current list
        if (another.endTime <= this.startTime) {
            isNewer = false;
        }

        // old ones
        Map<Integer, Growth> olds = new HashMap<Integer, Growth>();
        for (Growth growth : this.growths) {
            olds.put(growth.getId(), growth);
        }

        // join new ones
        boolean canJoin = false;
        Map<Integer, Growth> news = new HashMap<Integer, Growth>();
        for (Growth growth : another.growths) {
            int gid = growth.getId();
            news.put(gid, growth);

            if (olds.containsKey(gid)) {
                olds.get(gid).update(growth);
                canJoin = true;
            } else {
                this.growths.add(growth);
            }
        }
        for (int gid : another.delIds) {
            if (olds.containsKey(gid)) {
                olds.get(gid).setStatus(Status.DEL);
            }
        }

        if (isNewer) {
            this.lastReqTime = another.lastReqTime;
            if (another.total == another.getGrowths().size()) {
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
     * refresh new growths list from server for the first time
     */
    public void refresh() {
        refresh(0);
    }

    /**
     * refresh new growths list from server, with start time
     * 
     * @param startTime
     */
    public void refresh(long startTime) {
        refresh(startTime, 0);
    }

    /**
     * refresh new growths list from server, with start and end time
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public RetError refresh(long startTime, long endTime) {
        IParser parser = new GrowthListParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        if (startTime > 0) {
            params.put("start", startTime);
        }
        if (endTime > 0) {
            params.put("end", endTime);
        }

        Result ret = ApiRequest.requestWithToken(GrowthList.LIST_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            update((GrowthList) ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

}
