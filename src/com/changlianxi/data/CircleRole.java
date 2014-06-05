package com.changlianxi.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;

public class CircleRole extends AbstractData {
    private int id = 0;
    private int cid = 0;
    private String name = "";
    private int count = 0;

    public CircleRole(int cid) {
        this(cid, 0);
    }

    public CircleRole(int cid, int id) {
        this(cid, id, "");
    }

    public CircleRole(int cid, int id, String name) {
        this(cid, id, name, 0);
    }

    public CircleRole(int cid, int id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "CircleRole [cid=" + cid + ", id=" + id + ", name=" + name
                + ", count=" + count + "]";
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.CIRCLE_ROLE_TABLE_NAME, new String[] {
                "cid", "name", "count" }, "id=?",
                new String[] { this.id + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int count = cursor.getInt(cursor.getColumnIndex("count"));

            this.cid = cid;
            this.name = name;
            this.count = count;
        }
        cursor.close();

        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.CIRCLE_ROLE_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "id=?", new String[] { id + "" });
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("cid", cid);
        cv.put("name", name);
        cv.put("count", count);

        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "id=?", new String[] { id + "" });
        }
        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof CircleRole)) {
            return;
        }
        CircleRole another = (CircleRole) data;
        update(another, true);
    }

    public void update(CircleRole another, boolean changeCount) {
        boolean isChange = false;
        if (this.id != another.id) {
            this.id = another.id;
            isChange = true;
        }
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (!this.name.equals(another.name)) {
            this.name = another.name;
            isChange = true;
        }
        if (changeCount && this.count != another.count) {
            this.count = another.count;
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

}
