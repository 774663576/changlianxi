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

import com.changlianxi.data.enums.DynamicType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.CircleDynamicListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Dynamic List of a circle
 * 
 * Usage:
 * 
 * get a circle's dynamic list:
 *     // new CircleDynamicList cdl
 *     cdl.read();
 *     cdl.getDynamics();
 * 
 * refresh a circle's dynamic list:
 *     // new CircleDynamicList cdl
 *     cdl.read();
 *     ...
 *     cdl.refresh(); // get new dynamics
 *     
 *     ...
 *     cdl.write();
 *
 * insert a new dynamic:
 *     // new CircleDynamicList cdl
 *     cdl.read();
 *     ...
 *     cdl.insert(new_dynamic);
 *     
 *     ...
 *     cdl.write();
 *     
 * @author jieme
 *
 */
public class CircleDynamicList extends AbstractData {
    public final static String LIST_API = "/news/ilist";

    private int cid = 0;
    private long startTime = 0L; // data start time, in milliseconds
    private long endTime = 0L; // data end time
    private long lastReqTime = 0L; // last request time of chat data
    private int total = 0;
    private boolean isPersonal = false;
    private int serverCount = -1;// 服务器返回的当前请求结果条数
    private List<CircleDynamic> dynamics = new ArrayList<CircleDynamic>();

    public CircleDynamicList(int cid) {
        this(cid, false);
    }

    public CircleDynamicList(int cid, boolean isPersonal) {
        this.cid = cid;
        this.isPersonal = isPersonal;
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

    public List<CircleDynamic> getDynamics() {
        return dynamics;
    }

    public void setDynamics(List<CircleDynamic> dynamics) {
        this.dynamics = dynamics;
    }

    public int getServerCount() {
        return serverCount;
    }

    public void setServerCount(int serverCount) {
        this.serverCount = serverCount;
    }

    private void sort(boolean byTimeAsc) {
        Collections.sort(this.dynamics, CircleDynamic.getComparator(byTimeAsc));
    }

    @Override
    public void read(SQLiteDatabase db) {
        if (this.dynamics == null) {
            this.dynamics = new ArrayList<CircleDynamic>();
        } else {
            this.dynamics.clear();
        }

        Cursor cursor = db.query(Const.CIRCLE_DYNAMIC_TABLE_NAME, new String[] {
                "id", "uid1", "uid2", "pid2", "type", "content", "detail",
                "time", "needApproved" }, "cid=? and isPersonal=?",
                new String[] { this.cid + "", isPersonal ? "1" : "0" }, null,
                null, null);
        if (cursor.getCount() > 0) {
            long start = 0, end = 0;
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int uid1 = cursor.getInt(cursor.getColumnIndex("uid1"));
                int uid2 = cursor.getInt(cursor.getColumnIndex("uid2"));
                int pid2 = cursor.getInt(cursor.getColumnIndex("pid2"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String content = cursor.getString(cursor
                        .getColumnIndex("content"));
                String detail = cursor.getString(cursor
                        .getColumnIndex("detail"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                int needApproved = cursor.getInt(cursor
                        .getColumnIndex("needApproved"));

                CircleDynamic dynamic = new CircleDynamic(cid, id, isPersonal);
                dynamic.setUid1(uid1);
                dynamic.setUid2(uid2);
                dynamic.setPid2(pid2);
                dynamic.setType(DynamicType.convert(type));
                dynamic.setContent(content);
                dynamic.setDetail(detail);
                dynamic.setTime(time);
                dynamic.setNeedApproved(needApproved > 0);
                dynamic.setStatus(Status.OLD);
                this.dynamics.add(dynamic);

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
                        Const.TIME_RECORD_KEY_PREFIX_CIRCLEDYNAMIC + this.cid,
                        "last_req_time" + (isPersonal ? "_main" : "") }, null,
                null, null);
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
            int cacheCnt = 0;
            for (CircleDynamic dynamic : dynamics) {
                if (dynamic.getStatus() != Status.DEL) {
                    cacheCnt++;
                }
                if (cacheCnt > Const.DYNAMIC_MAX_CACHE_COUNT_PER_CIRCLE) {
                    dynamic.setStatus(Status.DEL);
                }
                dynamic.write(db);
            }

            // write last request time
            ContentValues cv = new ContentValues();
            cv.put("time", lastReqTime);
            int affected = db.update(Const.TIME_RECORD_TABLE_NAME, cv,
                    "key=? and subkey=?", new String[] {
                            Const.TIME_RECORD_KEY_PREFIX_CIRCLEDYNAMIC
                                    + this.cid,
                            "last_req_time" + (isPersonal ? "_main" : "") });
            if (affected == 0) {
                cv.put("key", Const.TIME_RECORD_KEY_PREFIX_CIRCLEDYNAMIC
                        + this.cid);
                cv.put("subkey", "last_req_time" + (isPersonal ? "_main" : ""));
                db.insert(Const.TIME_RECORD_TABLE_NAME, null, cv);
            }

            this.status = Status.OLD;
        }
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void update(IData data) {
        if (!(data instanceof CircleDynamicList)) {
            return;
        }

        CircleDynamicList another = (CircleDynamicList) data;
        if (another.dynamics.size() == 0) {
            return;
        }
        this.serverCount = another.dynamics.size();
        boolean isNewer = true; // another list is newer than current list
        if (another.endTime <= this.startTime) {
            isNewer = false;
        }

        // old ones
        Map<Integer, CircleDynamic> olds = new HashMap<Integer, CircleDynamic>();
        for (CircleDynamic dynamic : this.dynamics) {
            olds.put(dynamic.getId(), dynamic);
        }

        // join new ones
        boolean canJoin = false;
        Map<Integer, CircleDynamic> news = new HashMap<Integer, CircleDynamic>();
        for (CircleDynamic dynamic : another.dynamics) {
            int did = dynamic.getId();
            news.put(did, dynamic);

            if (olds.containsKey(did)) {
                olds.get(did).update(dynamic);
                canJoin = true;
            } else {
                this.dynamics.add(dynamic);
            }
        }

        if (isNewer) {
            this.lastReqTime = another.lastReqTime;
            if (another.total == another.getDynamics().size()) {
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

        sort(false);
        this.status = Status.UPDATE;
    }

    /**
     * refresh new dynamics list from server for the first time
     */
    public void refresh() {
        refresh(0);
    }

    /**
     * refresh new dynamics list from server, with start time
     * 
     * @param startTime
     */
    public void refresh(long startTime) {
        refresh(startTime, 0);
    }

    /**
     * refresh new dynamics list from server, with start and end time
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public RetError refresh(long startTime, long endTime) {
        IParser parser = new CircleDynamicListParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        if (startTime > 0) {
            params.put("start", startTime);
        }
        if (endTime > 0) {
            params.put("end", endTime);
        }
        if (isPersonal) {
            params.put("type", "main");
        }

        Result ret = ApiRequest.requestWithToken(CircleDynamicList.LIST_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            update((CircleDynamicList) ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public void insert(CircleDynamic dynamic) {
        CircleDynamicList cdl = new CircleDynamicList(dynamic.getCid());
        List<CircleDynamic> dynamics = new ArrayList<CircleDynamic>();
        dynamics.add(dynamic);
        cdl.setDynamics(dynamics);
        long time = DateUtils.convertToDate(dynamic.getTime());
        cdl.setLastReqTime(time);
        cdl.setTotal(1);
        cdl.setStartTime(time);
        cdl.setEndTime(time);

        update(cdl);
    }

}
