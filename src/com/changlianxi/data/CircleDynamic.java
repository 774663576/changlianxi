package com.changlianxi.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.DynamicType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.MapParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.MapResult;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Circle Dynamic
 * 
 * Usage:
 * 
 * get a circle dynamic info:
 *     // new dynamic
 *     dynamic.read();
 *     // dynamic.get***()
 *     
 * enter approve/avoid:
 *    // new dynamic
 *    dynamic.enterApprove(true);// false
 *
 * kick out approve/avoid:
 *    // new dynamic
 *    dynamic.kickoutApprove(true);// false
 * 
 * @author nnjme
 * 
 */
public class CircleDynamic extends AbstractData {
    public final static String APPROVE_ENTER_API = "/news/ienterApprove";
    public final static String APPROVE_KICKOUT_API = "/news/ikickoutApprove";

    private int cid = 0; // circle id
    private int id = 0; // dynamic id
    private DynamicType type = DynamicType.TYPE_UNKNOWN;
    private int uid1 = 0;
    private int uid2 = 0;
    private int pid2 = 0;
    private String content = "";
    private String detail = "";
    private String time = "";
    private boolean needApproved = false;
    private boolean isPassed = false; // used while approve enter/kickout type
    private boolean isPersonal = false;

    public CircleDynamic(int cid, int id) {
        this(cid, id, false);
    }

    public CircleDynamic(int cid, int id, boolean isPersonal) {
        this.cid = cid;
        this.id = id;
        this.isPersonal = isPersonal;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DynamicType getType() {
        return type;
    }

    public void setType(DynamicType type) {
        this.type = type;
    }

    public int getUid1() {
        return uid1;
    }

    public void setUid1(int uid1) {
        this.uid1 = uid1;
    }

    public int getUid2() {
        return uid2;
    }

    public void setUid2(int uid2) {
        this.uid2 = uid2;
    }

    public int getPid2() {
        return pid2;
    }

    public void setPid2(int pid2) {
        this.pid2 = pid2;
    }

    /**
     * get composite content 
     * 
     * @return
     */
    public String getCompositeContent(String userName1, String userName2) {
        String ret = content;
        if (ret.indexOf("[X]") >= 0) {
            ret = ret.replaceFirst("[X]", userName1);
        }
        if (ret.indexOf("[Y]") >= 0) {
            ret = ret.replaceFirst("[Y]", userName2);
        }
        return ret;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public boolean isNeedApproved() {
        return needApproved;
    }

    public void setNeedApproved(boolean needApproved) {
        this.needApproved = needApproved;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean isPassed) {
        this.isPassed = isPassed;
    }

    public String toString() {
        return "CircleDynamic [id=" + id + ", cid=" + cid + ", type=" + type
                + ", content=" + content + ", time=" + time + "]";
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.CIRCLE_DYNAMIC_TABLE_NAME, new String[] {
                "cid", "uid1", "uid2", "pid1", "type", "content", "detail",
                "time", "needApproved" }, "id=? and isPersonal=?",
                new String[] { this.id + "", isPersonal ? "1" : "0" }, null,
                null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            int uid1 = cursor.getInt(cursor.getColumnIndex("uid1"));
            int uid2 = cursor.getInt(cursor.getColumnIndex("uid2"));
            int pid2 = cursor.getInt(cursor.getColumnIndex("pid2"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String detail = cursor.getString(cursor.getColumnIndex("detail"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int needApproved = cursor.getInt(cursor
                    .getColumnIndex("needApproved"));

            this.cid = cid;
            this.uid1 = uid1;
            this.uid2 = uid2;
            this.pid2 = pid2;
            this.type = DynamicType.convert(type);
            this.content = content;
            this.detail = detail;
            this.time = time;
            this.needApproved = (needApproved > 0);
        }
        cursor.close();

        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.CIRCLE_DYNAMIC_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "id=? and isPersonal=?", new String[] {
                    this.id + "", isPersonal ? "1" : "0" });
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("cid", cid);
        cv.put("uid1", uid1);
        cv.put("uid2", uid2);
        cv.put("pid2", pid2);
        cv.put("type", type.name());
        cv.put("content", content);
        cv.put("detail", detail);
        cv.put("time", time);
        cv.put("needApproved", needApproved ? 1 : 0);
        cv.put("isPersonal", isPersonal ? "1" : "0");

        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "id=? and isPersonal=?", new String[] {
                    this.id + "", isPersonal ? "1" : "0" });

            // update the other dynamic with same content
            cv.put("isPersonal", isPersonal ? "0" : "1");
            db.update(dbName, cv, "id=? and isPersonal=?", new String[] {
                    this.id + "", isPersonal ? "0" : "1" });
        }

        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof CircleDynamic)) {
            return;
        }

        CircleDynamic another = (CircleDynamic) data;
        boolean isChange = false;
        if (this.id != another.id) {
            this.id = another.id;
            isChange = true;
        }
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.uid1 != another.uid1) {
            this.uid1 = another.uid1;
            isChange = true;
        }
        if (this.uid2 != another.uid2) {
            this.uid2 = another.uid2;
            isChange = true;
        }
        if (this.pid2 != another.pid2) {
            this.pid2 = another.pid2;
            isChange = true;
        }
        if (this.type != another.type) {
            this.type = another.type;
            isChange = true;
        }
        if (!this.content.equals(another.content)) {
            this.content = another.content;
            isChange = true;
        }
        if (!this.detail.equals(another.detail)) {
            this.detail = another.detail;
            isChange = true;
        }
        if (!this.time.equals(another.time)) {
            this.time = another.time;
            isChange = true;
        }
        if (this.needApproved != another.needApproved) {
            this.needApproved = another.needApproved;
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    /**
     * approve or not approve for a member's enter authentication
     * 
     * @param attitude
     * @return
     */
    public RetError enterApprove(boolean attitude) {
        if (this.type != DynamicType.TYPE_ENTERING || !this.needApproved) {
            return RetError.INVALID;
        }

        String[] keys = { "detail", "pass" };
        IParser parser = new MapParser(keys);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("pid", pid2);
        params.put("attitude", attitude ? 1 : 0);

        Result ret = ApiRequest.requestWithToken(
                CircleDynamic.APPROVE_ENTER_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            MapResult mret = (MapResult) ret;
            if (attitude) {
                this.detail = (String) (mret.getMaps().get("detail"));
                this.isPassed = ((Integer) (mret.getMaps().get("pass")) > 0);
            }
            this.needApproved = false;
            this.setStatus(Status.UPDATE);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * approve or not approve for a member's kickout authentication
     * 
     * @param attitude
     * @return
     */
    public RetError kickoutApprove(boolean attitude) {
        if (this.type != DynamicType.TYPE_KICKOUT || !this.needApproved) {
            return RetError.INVALID;
        }
        String[] keys = { "detail", "pass" };
        IParser parser = new MapParser(keys);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("pid", pid2);
        params.put("attitude", attitude ? 1 : 0);

        Result ret = ApiRequest.requestWithToken(
                CircleDynamic.APPROVE_KICKOUT_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            MapResult mret = (MapResult) ret;
            if (attitude) {
                this.detail = (String) (mret.getMaps().get("detail"));
                this.isPassed = ((Integer) (mret.getMaps().get("pass")) > 0);
            }
            this.needApproved = false;
            this.setStatus(Status.UPDATE);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public static Comparator<CircleDynamic> getComparator(boolean byTimeAsc) {
        if (byTimeAsc) {
            return new Comparator<CircleDynamic>() {
                @Override
                public int compare(CircleDynamic l, CircleDynamic r) {
                    long lTime = l.getId(), rTime = r.getId();
                    return lTime > rTime ? 1 : -1;
                }
            };
        } else {
            return new Comparator<CircleDynamic>() {
                @Override
                public int compare(CircleDynamic l, CircleDynamic r) {
                    long lTime = l.getId(), rTime = r.getId();
                    return lTime > rTime ? -1 : 1;
                }
            };
        }
    }

}
