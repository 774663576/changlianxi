package com.changlianxi.data;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.db.Const;

public class PersonDetail extends AbstractData implements Serializable {
    private static final long serialVersionUID = -1090438511326476150L;

    private int _id = 0;
    private int id = 0;
    private int cid = 0;
    private PersonDetailType type = PersonDetailType.UNKNOWN;
    private String value = "0";
    private String start = "";
    private String end = "";
    private String remark = "";
    private int pid = 0;
    private int uid = 0;

    public PersonDetail(PersonDetailType type, String value) {
        this.type = type;
        this.value = value;
    }

    public PersonDetail(int id, int cid) {
        this.id = id;
        this.cid = cid;
    }

    public PersonDetail(int id, int cid, int pid, int uid,
            PersonDetailType type, String value) {
        this.id = id;
        this.cid = cid;
        this.type = type;
        this.value = value;
        this.pid = pid;
        this.uid = uid;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public PersonDetailType getType() {
        return type;
    }

    public void setType(PersonDetailType type) {
        this.type = type;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "PersonProperty [id=" + id + ", cid=" + cid + ", type=" + type
                + ", value=" + value + "]" + "starus=" + status;
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.PERSON_DETAIL_TABLE_NAME1, new String[] {
                "_id", "uid", "pid", "type", "value", "start", "end" }, "id=? and cid=?",
                new String[] { this.id + "", this.cid + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            int uid = cursor.getInt(cursor.getColumnIndex("uid"));
            int pid = cursor.getInt(cursor.getColumnIndex("pid"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String value = cursor.getString(cursor.getColumnIndex("value"));
            String start = cursor.getString(cursor.getColumnIndex("start"));
            String end = cursor.getString(cursor.getColumnIndex("end"));
            
            this._id = _id;
            this.uid = uid;
            this.pid = pid;
            this.type = PersonDetailType.convertToType(type);
            this.value = value == null ? "" : value;
            this.start = start;
            this.end = end;
            
            this.status = Status.OLD;
        }
        cursor.close();
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.PERSON_DETAIL_TABLE_NAME1;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            if (cid == 0) {// for my card
                db.delete(dbName, "id=?", new String[] { id + "", });
            } else {
                db.delete(dbName, "id=? and cid=?", new String[] { id + "",
                        cid + "" });
            }
            return;
        }
        
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("cid", cid);
        cv.put("pid", pid);
        cv.put("uid", uid);
        cv.put("type", type.name()); // TODO encrypt
        cv.put("value", value);
        cv.put("start", start);
        cv.put("end", end);
        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "id=? and cid=?", new String[] { id + "",
                    cid + "" });
        }
        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof PersonDetail)) {
            return;
        }
        PersonDetail another = (PersonDetail) data;
        boolean isChange = false;
        if (this.id != another.id) {
            this.id = another.id;
            isChange = true;
        }
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.type != another.type) {
            this.type = another.type;
            isChange = true;
        }
        if (!this.value.equals(another.value)) {
            this.value = another.value;
            isChange = true;
        }
        if (!this.start.equals(another.start)) {
            this.start = another.start;
            isChange = true;
        }
        if (!this.end.equals(another.end)) {
            this.end = another.end;
            isChange = true;
        }
        if (!this.remark.equals(another.remark)) {
            this.remark = another.remark;
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PersonDetail)) {
            return false;
        }
        PersonDetail another = (PersonDetail) o;
        if (this.id != another.id) {
            return false;
        }
        if (this.cid != another.cid) {
            return false;
        }
        if (this.type != another.type) {
            return false;
        }
        if (!this.value.equals(another.value)) {
            return false;
        }
        if (!this.start.equals(another.start)) {
            return false;
        }
        if (!this.end.equals(another.end)) {
            return false;
        }
        if (!this.remark.equals(another.remark)) {
            return false;
        }

        return true;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("t", type.name());
        json.put("v", value);
        if (PersonDetailType.hasTimeRange(type)) {
            json.put("start", start);
            json.put("end", end);
        }
        return json;
    }

    public String toDbInsertString() {
        return "(" + id + "," + cid + "," + pid + "," + uid + ",'"
                + this.getType().name() + "','" + value + "','" + start + "','"
                + end + "')";
    }

    public static String getDbInsertKeyString() {
        return " (id, cid, pid, uid, type, value, start, end) ";
    }
}
