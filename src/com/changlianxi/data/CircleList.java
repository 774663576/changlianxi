package com.changlianxi.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.CircleListParser;
import com.changlianxi.data.parser.CirclesNotifyParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.db.DBUtils;

/**
 * Circle List of a user
 * 
 * Usage:
 * 
 * get a user's circle list
 *     // new CircleList cl
 *     cl.read();
 *     cl.getCircles();
 * 
 * refresh a user's circle list
 *     // new CircleList cl
 *     cl.read();
 *     ...
 *     cl.refresh(); // get new and mod and del circles
 *     
 *     ...
 *     cl.write();
 *     
 *     
 * @author jieme
 *
 */
public class CircleList extends AbstractData {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public final static String LIST_API = "/circles/ilist";
    public final static String NOTIFY_API = "/circles/inotify";// 圈子提醒数接口

    private List<Circle> circles = null;
    private long lastReqTime = 0L; // last request time

    public CircleList(List<Circle> circles) {
        this.circles = circles;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public void setCircles(List<Circle> circles) {
        this.circles = circles;
    }

    public long getLastReqTime() {
        return lastReqTime;
    }

    public void setLastReqTime(long lastReqTime) {
        this.lastReqTime = lastReqTime;
    }

    private void sort(boolean byTimeAsc) {
        Collections.sort(this.circles, Circle.getComparator(byTimeAsc));
    }

    @Override
    public void read(SQLiteDatabase db) {
        if (this.circles == null) {
            this.circles = new ArrayList<Circle>();
        } else {
            this.circles.clear();
        }

        // read ids
        Cursor cursor = db.query(Const.CIRCLE_TABLE_NAME,
                new String[] { "id" }, null, null, null, null, null);
        List<Integer> cids = new ArrayList<Integer>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                cids.add(id);
                cursor.moveToNext();
            }
        }
        cursor.close();

        // read one by one
        for (int cid : cids) {
            Circle c = new Circle(cid);
            c.read(db);
            this.circles.add(c);
        }

        // read last request time
        Cursor cursor2 = db.query(Const.TIME_RECORD_TABLE_NAME,
                new String[] { "time" }, "key=? and subkey=?",
                new String[] { Const.TIME_RECORD_KEY_PREFIX_CIRCLES,
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
            for (Circle c : this.circles) {
                if (c.getId() == 0) {
                    continue;
                }
                c.write(db);
            }
            // write last request time
            ContentValues cv = new ContentValues();
            cv.put("time", lastReqTime);
            int affected = db.update(Const.TIME_RECORD_TABLE_NAME, cv,
                    "key=? and subkey=?", new String[] {
                            Const.TIME_RECORD_KEY_PREFIX_CIRCLES,
                            "last_req_time" });
            if (affected == 0) {
                cv.put("key", Const.TIME_RECORD_KEY_PREFIX_CIRCLES);
                cv.put("subkey", "last_req_time");
                db.insert(Const.TIME_RECORD_TABLE_NAME, null, cv);
            }

            this.status = Status.OLD;
        }
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof CircleList)) {
            return;
        }

        CircleList another = (CircleList) data;
        if (another.circles.size() == 0) {
            return;
        }
        // old cids
        Set<Integer> oldCids = new HashSet<Integer>();
        for (Circle c : this.circles) {
            oldCids.add(c.getId());
        }

        // update/del circles
        for (Circle ac : another.circles) {
            int acId = ac.getId();
            if (oldCids.contains(acId)) {
                for (Circle c : this.circles) {
                    if (c.getId() == acId) {
                        if (ac.getStatus() != Status.DEL) {
                            c.updateForListChange(ac);
                        } else {
                            c.setStatus(Status.DEL);
                            c.write(DBUtils.getDBsa(2));
                        }
                    }
                }
            }
        }

        // new circles
        for (Circle ac : another.circles) {
            int acId = ac.getId();
            if (!oldCids.contains(acId)) {
                this.circles.add(ac);
            }
        }

        this.lastReqTime = another.lastReqTime;
        this.status = Status.UPDATE;
        sort(false);
        for (int i = circles.size() - 1; i >= 0; i--) {
            if (circles.get(i).getStatus().equals(Status.DEL)) {
                circles.remove(i);
            }
        }
    }

    /**
     * refresh new circles list from server
     */
    public RetError refresh() {
        return refresh(0);
    }

    /**
     * refresh new circles list from server with start time
     */
    public RetError refresh(long startTime) {

        return refresh(startTime, 0);
    }

    /**
     * refresh new circles list from server with start and end time
     */
    public RetError refresh(long startTime, long endTime) {
        IParser parser = new CircleListParser();
        Map<String, Object> params = new HashMap<String, Object>();
        if (startTime > 0) {
            params.put("start", startTime);
        }
        if (endTime > 0) {
            params.put("end", endTime);
        }
        Result ret = ApiRequest.requestWithToken(CircleList.LIST_API, params,
                parser);

        if (ret.getStatus() == RetStatus.SUCC) {
            this.update(ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public void getCirclesNotify() {
        List<Integer> cids = new ArrayList<Integer>();
        for (Circle c : this.circles) {
            if (c.getId() == 0) {
                continue;
            }
            cids.add(c.getId());
        }
        String cidstr = cids.toString().substring(1)
                .substring(0, cids.toString().length() - 2);
        IParser parser = new CirclesNotifyParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cids", cidstr);
        // params.put("start", startTime);
        Result ret = ApiRequest.requestWithToken(CircleList.NOTIFY_API, params,
                parser);
        if (ret.getStatus() != RetStatus.SUCC) {
            return;
        }
        CircleList cList = (CircleList) ret.getData();
        List<Circle> circleList = cList.getCircles();
        int newMemberCnt = 0;
        int newGrowthCnt = 0;
        int newMyDetailEditCnt = 0;
        int newDynamicCnt = 0;
        int newGrowthCommentCnt = 0;
        for (int i = 0; i < circleList.size(); i++) {
            newGrowthCnt = circleList.get(i).getNewGrowthCnt();
            newDynamicCnt = circleList.get(i).getNewDynamicCnt();
            newGrowthCommentCnt = circleList.get(i).getNewGrowthCommentCnt();
            newMyDetailEditCnt = circleList.get(i).getNewMyDetailEditCnt();
            newMemberCnt = circleList.get(i).getNewMemberCnt();
            this.circles.get(i).setNewDynamicCnt(newDynamicCnt);
            this.circles.get(i).setNewGrowthCnt(newGrowthCnt);
            this.circles.get(i).setNewGrowthCommentCnt(newGrowthCommentCnt);
            this.circles.get(i).setNewMemberCnt(newMemberCnt);
            this.circles.get(i).setNewMyDetailEditCnt(newMyDetailEditCnt);
        }

    }

    public int getCircleCount(SQLiteDatabase db) {
        int count = 0;
        Cursor cursor = db.query(Const.CIRCLE_TABLE_NAME,
                new String[] { "id" }, null, null, null, null, null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getAvailableCircleCount(SQLiteDatabase db, int currentID) {
        int count = 0;
        Cursor cursor = db.query(Const.CIRCLE_TABLE_NAME, new String[] { "id",
                "isNew" }, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int isNew = cursor.getInt(cursor.getColumnIndex("isNew"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                if (isNew < 1 && currentID != id) {
                    count++;
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return count;
    }

    public void initCircles() {
        Circle circle = new Circle(-1);
        circle.setLogo("");
        circle.setNew(false);
        circle.setName("同事");
        circles.add(0, circle);
        circle = new Circle(-2);
        circle.setLogo("");
        circle.setNew(false);
        circle.setName("同学");
        circles.add(0, circle);
        circle = new Circle(-3);
        circle.setLogo("");
        circle.setNew(false);
        circle.setName("家人");
        circles.add(0, circle);

    }
}
