package com.changlianxi.data;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;
import com.changlianxi.util.StringUtils;

/**
 * Image in growth.
 * 
 * 
 * @author nnjme
 * 
 */
public class GrowthImage extends AbstractData implements Serializable {
    private static final long serialVersionUID = -8022091263326602854L;
    private int cid = 0;
    private int gid = 0;
    private int imgId = 0;
    private String img = "";

    public GrowthImage(int cid, int gid, int imgId) {
        this(cid, gid, imgId, "");
    }

    public GrowthImage(int cid, int gid, int imgId, String img) {
        this.cid = cid;
        this.gid = gid;
        this.imgId = imgId;
        this.img = img;
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

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getImg() {
        return img;
    }

    public String getImg(String size) {
        return StringUtils.JoinString(img, size);
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "GrowthImage [cid=" + cid + ", gid=" + gid + ", imgId=" + imgId
                + ", img=" + img + "]" + status;
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.GROWTH_IMAGE_TABLE_NAME, new String[] {
                "cid", "gid", "img" }, "imgId=?", new String[] { this.imgId
                + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            int gid = cursor.getInt(cursor.getColumnIndex("gid"));
            String img = cursor.getString(cursor.getColumnIndex("img"));

            this.cid = cid;
            this.gid = gid;
            this.img = img;
        }
        cursor.close();

        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.GROWTH_IMAGE_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "imgId=?", new String[] { imgId + "" });
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("cid", cid);
        cv.put("gid", gid);
        cv.put("imgId", imgId);
        cv.put("img", img);

        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "imgId=?", new String[] { imgId + "" });
        }
        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof GrowthImage)) {
            return;
        }
        GrowthImage another = (GrowthImage) data;
        boolean isChange = false;
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.gid != another.gid) {
            this.gid = another.gid;
            isChange = true;
        }
        if (this.imgId != another.imgId) {
            this.imgId = another.imgId;
            isChange = true;
        }
        if (!this.img.equals(another.img)) {
            this.img = another.img;
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

}
