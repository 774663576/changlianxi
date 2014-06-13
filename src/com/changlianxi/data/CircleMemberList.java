package com.changlianxi.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.CircleMemberListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * List of members in a circle
 * 
 * Usage:
 * 
 * get a circle's member list
 *  // new CircleMemberList cml 
 *  cml.read();
 *  cml.getMembers();
 * 
 * refresh a circle's member
 *  // new CircleMemberList cml ...
 *  cml.refresh();
 *  // cml.refreshAllMembers(); // refresh all members 
 *  // cml.update() 
 *  // cml.write() ...
 * 
 * 
 * @author jieme
 * 
 */
@SuppressLint("UseSparseArrays")
public class CircleMemberList extends AbstractData {
    public final static String LIST_API = "/circles/imembers";
    private int cid = 0;
    private long startTime = 0L; // data start time, in milliseconds
    private long endTime = 0L; // data end time
    private long lastReqTime = 0L; // last request time of data
    private int total = 0;
    private List<CircleMember> members = new ArrayList<CircleMember>();

    enum Type {
        NEW, MOD, DEL
    }

    public CircleMemberList(int cid) {
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

    public List<CircleMember> getMembers() {
        return members;
    }

    public List<CircleMember> getLegalMembers() {
        List<CircleMember> legalMembers = new ArrayList<CircleMember>();
        for (CircleMember cm : this.members) {
            if (!CircleMemberState.notInCircle(cm.getState())) {
                legalMembers.add(cm);
            }
        }
        return legalMembers;
    }

    public void setMembers(List<CircleMember> members) {
        if (members != null) {
            this.members = members;
        }
    }

    private void sort() {
        Collections.sort(members, new Comparator<CircleMember>() {
            @Override
            public int compare(CircleMember lhs, CircleMember rhs) {
                return lhs.getSortkey().compareTo(rhs.getSortkey());
            }
        });
    }

    /**
     * 模糊查询
     * 
     * @param tableName
     */
    public List<CircleMember> fuzzyQuery(String str, SQLiteDatabase db) {
        if (members == null) {
            members = new ArrayList<CircleMember>();
        } else {
            members.clear();
        }
        Cursor cursor2 = db
                .query(Const.CIRCLE_MEMBER_TABLE_NAME,
                        new String[] { "uid", "pid", "cid", "name", "avatar",
                                "cellphone", "state" },
                        "name like ?  or sortkey like ? or pinyinFir like ? or cellphone like ?",
                        new String[] { "%" + str + "%", "%" + str + "%",
                                "%" + str + "%", "%" + str + "%" }, null, null,
                        null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            for (int i = 0; i < cursor2.getCount(); i++) {
                int cid = cursor2.getInt(cursor2.getColumnIndex("cid"));
                int uid = cursor2.getInt(cursor2.getColumnIndex("uid"));
                int pid = cursor2.getInt(cursor2.getColumnIndex("pid"));
                String name = cursor2.getString(cursor2.getColumnIndex("name"));
                String state = cursor2.getString(cursor2
                        .getColumnIndex("state"));
                if (CircleMemberState.notInCircle(CircleMemberState
                        .convert(state))) {
                    cursor2.moveToNext();
                    continue;
                }
                String cellphone = cursor2.getString(cursor2
                        .getColumnIndex("cellphone"));
                String avatar = cursor2.getString(cursor2
                        .getColumnIndex("avatar"));
                CircleMember member = new CircleMember(cid);
                member.setUid(uid);
                member.setPid(pid);
                member.setCellphone(cellphone);
                member.setAvatar(avatar);
                member.setName(name);
                members.add(member);
                cursor2.moveToNext();
            }
        }
        cursor2.close();
        return members;

    }

    @Override
    public void read(SQLiteDatabase db) {
        if (members == null) {
            members = new ArrayList<CircleMember>();
        } else {
            members.clear();
        }
        // read ids
        Cursor cursor = db.query(Const.CIRCLE_MEMBER_TABLE_NAME,
                new String[] { "pid" }, "cid=?",
                new String[] { this.cid + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int pid = cursor.getInt(cursor.getColumnIndex("pid"));
                CircleMember member = new CircleMember(cid, pid);
                members.add(member);
                cursor.moveToNext();
            }
        }
        cursor.close();

        // read one by one
        for (CircleMember m : members) {
            m.read(db);
            long time = DateUtils.convertToDate(m.getLastModTime());
            if (time > 0) {
                time /= 1000;
            }
            if (startTime == 0 || time < startTime) {
                startTime = time;
            }
            if (endTime == 0 || time > endTime) {
                endTime = time;
            }
        }

        // read last request times
        Cursor cursor2 = db.query(Const.TIME_RECORD_TABLE_NAME,
                new String[] { "time" }, "key=? and subkey=?", new String[] {
                        Const.TIME_RECORD_KEY_PREFIX_CIRCLEMEMBER + this.cid,
                        "last_req_time" }, null, null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            long time = cursor2.getLong(cursor2.getColumnIndex("time"));
            this.lastReqTime = time;
        }
        cursor2.close();

        this.status = Status.OLD;
        sort();
    }

    @Override
    public void write(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            if (this.status != Status.OLD) {
                // write one by one
                for (CircleMember m : members) {
                    m.write(db);
                }
                // write last request time
                ContentValues cv = new ContentValues();
                cv.put("time", lastReqTime);
                int affected = db.update(Const.TIME_RECORD_TABLE_NAME, cv,
                        "key=? and subkey=?", new String[] {
                                Const.TIME_RECORD_KEY_PREFIX_CIRCLEMEMBER
                                        + this.cid, "last_req_time" });
                if (affected == 0) {
                    cv.put("key", Const.TIME_RECORD_KEY_PREFIX_CIRCLEMEMBER
                            + this.cid);
                    cv.put("subkey", "last_req_time");
                    db.insert(Const.TIME_RECORD_TABLE_NAME, null, cv);
                }

                this.status = Status.OLD;
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof CircleMemberList)) {
            return;
        }
        CircleMemberList another = (CircleMemberList) data;
        if (another.members.size() == 0) {
            return;
        }
        Map<Integer, CircleMember> olds = new HashMap<Integer, CircleMember>();
        for (CircleMember m : this.members) {

            olds.put(m.getPid(), m);
        }

        for (CircleMember am : another.members) {
            int pid = am.getPid();
            if (olds.containsKey(pid)) {
                // mod or del
                olds.get(pid).updateListSummary(am);
            } else {
                // new
                this.members.add(am);
                this.total++;
            }
        }

        // update start/end time and last request time
        this.startTime = Math.min(this.startTime, another.getStartTime());
        this.endTime = Math.max(this.endTime, another.getEndTime());
        this.lastReqTime = another.lastReqTime;

        this.status = Status.UPDATE;
        sort();
    }

    /**
     * refresh circle members list from server, for the first time
     */
    public void refresh() {
        refresh(0);
    }

    /**
     * refresh for synchronize local data with server data
     * 
     * @param startTime
     */
    public void refresh(long startTime) {
        refreshAllMembers(startTime, 0L);
    }

    /**
     * refresh all circle members list from server, it will do repeat
     * refresh request until all data is fetched.
     * 
     * @param startTime
     * @param endTime
     */
    public void refreshAllMembers(long startTime, long endTime) {
        while (true) {
            CircleMemberList cml = null;
            Result ret = requestMembers(startTime, endTime);
            if (ret != null && ret.getStatus() == RetStatus.SUCC) {
                cml = (CircleMemberList) ret.getData();
            } else {
                break;
            }
            // update for data merge
            update(cml);
            if (cml.getTotal() <= cml.getMembers().size()) {
                break;
            }

            startTime = cml.getEndTime() / 1000;
        }
    }

    private Result requestMembers(long startTime, long endTime) {
        IParser parser = new CircleMemberListParser();
        Map<String, Object> params = new HashMap<String, Object>();
        if (startTime > 0) {
            params.put("start", startTime);
        }
        if (endTime > 0) {
            params.put("end", endTime);
        }
        params.put("cid", cid);
        Result ret = ApiRequest.requestWithToken(CircleMemberList.LIST_API,
                params, parser);

        return ret;
    }

}
