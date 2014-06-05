package com.changlianxi.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;

/**
 * record for other people changes my info
 * 
 * @author jieme
 * 
 */
public class Amendment extends AbstractData {

    private int amid = 0;
    private int cid = 0;
    private int uid = 0;
    private String content = "";
    private String time = "";

    public Amendment(int amid, int cid, int uid) {
        this.amid = amid;
        this.cid = cid;
        this.uid = uid;
    }

    public int getAmid() {
        return amid;
    }

    public void setAmid(int amid) {
        this.amid = amid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Amendment [amid=" + amid + ", cid=" + cid + ", uid=" + uid
                + ", content=" + content + ", time=" + time + "]";
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.AMENDMENT_TABLE_NAME, new String[] {
                "cid", "uid", "content", "time" }, "amid=?",
                new String[] { this.amid + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            int uid = cursor.getInt(cursor.getColumnIndex("uid"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String time = cursor.getString(cursor.getColumnIndex("time"));

            this.cid = cid;
            this.uid = uid;
            this.content = content;
            this.time = time;
        }
        cursor.close();

        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.AMENDMENT_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "amid=?", new String[] { amid + "" });
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("amid", amid);
        cv.put("cid", cid);
        cv.put("uid", uid);
        cv.put("content", content);
        cv.put("time", time);

        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "amid=?", new String[] { amid + "" });
        }

        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof Amendment)) {
            return;
        }

        Amendment another = (Amendment) data;
        boolean isChange = false;
        if (this.amid != another.amid) {
            this.amid = another.amid;
            isChange = true;
        }
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.uid != another.uid) {
            this.uid = another.uid;
            isChange = true;
        }
        if (!this.content.equals(another.content)) {
            this.content = another.content;
            isChange = true;
        }
        if (!this.time.equals(another.time)) {
            this.time = another.time;
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

}
