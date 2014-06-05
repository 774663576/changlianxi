package com.changlianxi.data;

import java.util.Comparator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Comment in growth.
 * 
 * 
 * @author nnjme
 * 
 */
public class GrowthComment extends AbstractData {
    private int cid = 0;
    private int gid = 0;
    private int gcid = 0;
    private int uid = 0;
    private int replyid = 0;
    private String content = "";
    private String time = "";
    private int total = 0; // just use to record total comments while pushlish a
                           // new comment
    private boolean isForMe = false;

    public GrowthComment(int cid, int gid, int gcid, int uid, String content) {
        this(cid, gid, gcid, uid, content, false);
    }

    public GrowthComment(int cid, int gid, int gcid, int uid, String content,
            boolean isForMe) {
        this.cid = cid;
        this.gid = gid;
        this.gcid = gcid;
        this.uid = uid;
        this.content = content;
        this.isForMe = isForMe;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getGcid() {
        return gcid;
    }

    public void setGcid(int gcid) {
        this.gcid = gcid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getReplyid() {
        return replyid;
    }

    public void setReplyid(int replyid) {
        this.replyid = replyid;
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

    public String getFormatTime() {
        return DateUtils.formatTime(time);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "GrowthComment [cid=" + cid + ", gid=" + gid + ", gcid=" + gcid
                + ", uid=" + uid + ", replyid=" + replyid + ", content="
                + content + ", time=" + time + "]";
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.GROWTH_COMMENT_TABLE_NAME, new String[] {
                "cid", "gid", "uid", "replyid", "content", "time" },
                "gcid=? and isForMe=?", new String[] { this.gcid + "",
                        isForMe ? "1" : "0" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            int gid = cursor.getInt(cursor.getColumnIndex("gid"));
            int uid = cursor.getInt(cursor.getColumnIndex("uid"));
            int replyid = cursor.getInt(cursor.getColumnIndex("replyid"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String time = cursor.getString(cursor.getColumnIndex("time"));

            this.cid = cid;
            this.gid = gid;
            this.uid = uid;
            this.replyid = replyid;
            this.content = content;
            this.time = time;
        }
        cursor.close();

        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.GROWTH_COMMENT_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "gcid=? and isForMe=?", new String[] { gcid + "",
                    isForMe ? "1" : "0" });
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("cid", cid);
        cv.put("gid", gid);
        cv.put("gcid", gcid);
        cv.put("uid", uid);
        cv.put("replyid", replyid);
        cv.put("content", content);
        cv.put("time", time);
        cv.put("isForMe", isForMe ? 1 : 0);

        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "gcid=? and isForMe=?", new String[] {
                    gcid + "", isForMe ? "1" : "0" });
        }
        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof GrowthComment)) {
            return;
        }
        GrowthComment another = (GrowthComment) data;
        boolean isChange = false;
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.gid != another.gid) {
            this.gid = another.gid;
            isChange = true;
        }
        if (this.gcid != another.gcid) {
            this.gcid = another.gcid;
            isChange = true;
        }
        if (this.uid != another.uid) {
            this.uid = another.uid;
            isChange = true;
        }
        if (this.replyid != another.replyid) {
            this.replyid = another.replyid;
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

    public static Comparator<GrowthComment> getComparator(boolean byTimeAsc) {
        if (byTimeAsc) {
            return new Comparator<GrowthComment>() {
                @Override
                public int compare(GrowthComment l, GrowthComment r) {
                    long lTime = DateUtils.convertToDate(l.getTime()), rTime = DateUtils
                            .convertToDate(r.getTime());
                    return lTime > rTime ? 1 : -1;
                }
            };
        } else {
            return new Comparator<GrowthComment>() {
                @Override
                public int compare(GrowthComment l, GrowthComment r) {
                    long lTime = DateUtils.convertToDate(l.getTime()), rTime = DateUtils
                            .convertToDate(r.getTime());
                    return lTime > rTime ? -1 : 1;
                }
            };
        }
    }

}
