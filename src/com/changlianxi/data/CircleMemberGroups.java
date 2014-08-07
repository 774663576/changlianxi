package com.changlianxi.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;

public class CircleMemberGroups extends AbstractData {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int cid;
    private int group_id;
    private int pid;

    public CircleMemberGroups(int cid, int group_id, int pid) {
        this.cid = cid;
        this.group_id = group_id;
        this.pid = pid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "cid:" + cid + "  pid:" + pid + "  group_id:" + group_id;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.CIRCLE_MEMBERS_GROUPS_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "cid=? and pid=?", new String[] { cid + "",
                    pid + "" });
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put("cid", cid);
        cv.put("group_id", group_id);
        cv.put("pid", pid);
        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        }
        this.status = Status.OLD;
    }

    @Override
    public void read(SQLiteDatabase db) {

    }
}
