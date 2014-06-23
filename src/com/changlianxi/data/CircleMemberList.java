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
import com.changlianxi.data.enums.RetError;
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
    public final static int MAX_INSERT_COUNT_FOR_CIRCLE_MEMBER = 500;
    public final static int MAX_INSERT_COUNT_FOR_PERSONAL_DETAIL = 500;

    private int cid = 0;
    private long startTime = 0L; // data start time, in milliseconds
    private long endTime = 0L; // data end time
    private long lastReqTime = 0L; // last request time of data
    private int total = 0;
    private List<CircleMember> members = new ArrayList<CircleMember>();

    enum Type {
        NEW, MOD, DEL
    } // TODO

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
            System.out.println("state::::::::::::"+cm.getName()+"        "+cm.getState());
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

        String conditionsKey = "cid=?";
        String[] conditionsValue = { this.cid + "" };
        Cursor cursor = db.query(Const.CIRCLE_MEMBER_TABLE_NAME, new String[] {
                "_id", "uid", "pid", "cmid", "name", "cellphone", "location",
                "avatar", "employer", "lastModTime", "state", "inviteCode",
                "sortkey", "pinyinFir" }, conditionsKey, conditionsValue, null,
                null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                int uid = cursor.getInt(cursor.getColumnIndex("uid"));
                int pid = cursor.getInt(cursor.getColumnIndex("pid"));
                int cmid = cursor.getInt(cursor.getColumnIndex("cmid"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String cellphone = cursor.getString(cursor
                        .getColumnIndex("cellphone"));
                String location = cursor.getString(cursor
                        .getColumnIndex("location"));
                String avatar = cursor.getString(cursor
                        .getColumnIndex("avatar"));
                String employer = cursor.getString(cursor
                        .getColumnIndex("employer"));
                String lastModTime = cursor.getString(cursor
                        .getColumnIndex("lastModTime"));
                String state = cursor.getString(cursor.getColumnIndex("state"));
                String inviteCode = cursor.getString(cursor
                        .getColumnIndex("inviteCode"));
                String sortkey = cursor.getString(cursor
                        .getColumnIndex("sortkey"));
                String pinyinFir = cursor.getString(cursor
                        .getColumnIndex("pinyinFir"));

                CircleMember member = new CircleMember(cid, pid, uid, name);
                member.set_id(_id);
                member.setCmid(cmid);
                member.setCellphone(cellphone);
                member.setLocation(location);
                member.setAvatar(avatar);
                member.setEmployer(employer);
                member.setLastModTime(lastModTime);
                member.setState(CircleMemberState.convert(state));
                member.setInviteCode(inviteCode);
                member.setPinyinFir(pinyinFir);
                member.setSortkey(sortkey);
                // set status
                member.setStatus(Status.OLD);

                members.add(member);
                long time = DateUtils.convertToDate(member.getLastModTime());
                if (time > 0) {
                    time /= 1000;
                }
                if (startTime == 0 || time < startTime) {
                    startTime = time;
                }
                if (endTime == 0 || time > endTime) {
                    endTime = time;
                }

                cursor.moveToNext();
            }
        }
        cursor.close();

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

    /**
     * read circle members list from db 1by1
     * 
     * @param db
     * 
     * @deprecated too slow
     */
    public void readOneByOne(SQLiteDatabase db) {
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

    /**
     * write circle member 1by1
     * 
     * @param db
     * @deprecated too slow for more data
     */
    public void writeOneByOne(SQLiteDatabase db) {
        try {
            if (this.status != Status.OLD) {
                db.beginTransaction();

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

                db.setTransactionSuccessful();
                this.status = Status.OLD;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void write(SQLiteDatabase db) {
        if (this.status == Status.OLD) {
            return;
        }

        List<CircleMember> newMembers = new ArrayList<CircleMember>();
        List<CircleMember> delMembers = new ArrayList<CircleMember>();
        for (CircleMember m : members) {
            if (m.status == Status.OLD) {
                continue;
            }
            if (m.status == Status.DEL) {
                delMembers.add(m);
                continue;
            }
            if (m.status == Status.UPDATE) {
                delMembers.add(m);
            }
            newMembers.add(m);
        }

        try {
            db.beginTransaction();
            StringBuffer sqlBuffer = new StringBuffer();

            // basic info: delete delMembers
            sqlBuffer.append("delete from " + Const.CIRCLE_MEMBER_TABLE_NAME
                    + " where _id in (");
            int cnt = 0;
            for (CircleMember dm : delMembers) {
                if (cnt > 0) {
                    sqlBuffer.append(",");
                }
                if (dm.get_id() == 0) {
                    continue;
                }
                sqlBuffer.append(dm.get_id());
                cnt++;
            }
            if (cnt > 0) {
                sqlBuffer.append(")");
                db.execSQL(sqlBuffer.toString());
            }
            for (CircleMember m : delMembers) {
                m.setStatus(Status.OLD);
            }

            // basic info: insert newMembers
            sqlBuffer = new StringBuffer();
            sqlBuffer.append("insert into " + Const.CIRCLE_MEMBER_TABLE_NAME
                    + CircleMember.getDbInsertKeyString() + " values ");
            cnt = 0;
            for (CircleMember nm : newMembers) {
                if (cnt > 0) {
                    sqlBuffer.append(",");
                }
                sqlBuffer.append(nm.toDbInsertString());

                cnt++;
                if (cnt >= MAX_INSERT_COUNT_FOR_CIRCLE_MEMBER) {
                    db.execSQL(sqlBuffer.toString());

                    cnt = 0;
                    sqlBuffer = new StringBuffer();
                    sqlBuffer.append("insert into "
                            + Const.CIRCLE_MEMBER_TABLE_NAME
                            + CircleMember.getDbInsertKeyString() + " values ");
                }
            }
            if (cnt > 0) {
                db.execSQL(sqlBuffer.toString());
            }
            for (CircleMember m : newMembers) {
                m.setStatus(Status.OLD);
            }

            // detail info: delete del members' details
            sqlBuffer.append("delete from " + Const.PERSON_DETAIL_TABLE_NAME1
                    + " where _id in (");
            cnt = 0;
            for (CircleMember dm : delMembers) {
                for (PersonDetail pd : dm.getDetails()) {
                    if (cnt > 0) {
                        sqlBuffer.append(",");
                    }
                    if (dm.get_id() == 0) {
                        continue;
                    }
                    sqlBuffer.append(pd.get_id());
                    cnt++;
                }
            }
            if (cnt > 0) {
                sqlBuffer.append(")");
                db.execSQL(sqlBuffer.toString());
            }
            for (CircleMember dm : delMembers) {
                for (PersonDetail pd : dm.getDetails()) {
                    pd.setStatus(Status.OLD);
                }
            }

            // detail info: insert new members' details
            sqlBuffer = new StringBuffer();
            sqlBuffer.append("insert into " + Const.PERSON_DETAIL_TABLE_NAME1
                    + PersonDetail.getDbInsertKeyString() + " values ");
            cnt = 0;
            for (CircleMember nm : newMembers) {
                for (PersonDetail pd : nm.getDetails()) {
                    if (cnt > 0) {
                        sqlBuffer.append(",");
                    }
                    sqlBuffer.append(pd.toDbInsertString());

                    cnt++;
                    if (cnt >= MAX_INSERT_COUNT_FOR_PERSONAL_DETAIL) {
                        db.execSQL(sqlBuffer.toString());

                        cnt = 0;
                        sqlBuffer = new StringBuffer();
                        sqlBuffer.append("insert into "
                                + Const.PERSON_DETAIL_TABLE_NAME1
                                + PersonDetail.getDbInsertKeyString()
                                + " values ");
                    }
                }
            }
            if (cnt > 0) {
                db.execSQL(sqlBuffer.toString());
            }
            for (CircleMember nm : newMembers) {
                for (PersonDetail pd : nm.getDetails()) {
                    pd.setStatus(Status.OLD);
                }
            }

            // reset status
            this.status = Status.OLD;

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

            db.setTransactionSuccessful();
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
                // mod
                olds.get(pid).updateForListRefresh(am);
            } else {
                // new
                this.members.add(am);
            }
        }

        // update start/end time and last request time
        this.startTime = Math.min(this.startTime, another.getStartTime());
        this.endTime = Math.max(this.endTime, another.getEndTime());
        this.lastReqTime = another.lastReqTime;
        this.total = this.members.size()
                + (another.getTotal() - another.getMembers().size());

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

    public RetError refreshMembers(long startTime) {
        Result ret = requestMembers(startTime, 0);
        if (ret != null && ret.getStatus() == RetStatus.SUCC) {
        } else {
            return (ret != null) ? ret.getErr() : null;
        }

        // update for data merge
        CircleMemberList cml = (CircleMemberList) ret.getData();
        update(cml);

        return RetError.NONE;
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
