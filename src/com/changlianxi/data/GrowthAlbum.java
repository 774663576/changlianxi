package com.changlianxi.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;

public class GrowthAlbum extends AbstractData implements Serializable {

    private static final long serialVersionUID = 1L;
    private int cid = 0;
    private List<GrowthAlbumImages> pics = new ArrayList<GrowthAlbumImages>();
    private String albumName = "";// 相册名称
    private String albumDate = "";// 相册时间
    private int albumTotal = 0;// 相册中照片总数
    private int albumContributors = 0;// 相册中贡献者总人数
    private String strKey = "";
    private String year = "";
    private String month = "";
    private String day = "";

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public GrowthAlbum(int cid, String strKey) {
        this.strKey = strKey;
        this.cid = cid;
    }

    public String getStrKey() {
        return strKey;
    }

    public void setStrKey(String strKey) {
        this.strKey = strKey;
    }

    public List<GrowthAlbumImages> getPics() {
        return pics;
    }

    public void setPics(List<GrowthAlbumImages> pics) {
        this.pics = pics;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumDate() {
        return albumDate;
    }

    public void setAlbumDate(String albumDate) {
        this.albumDate = albumDate;
    }

    public int getAlbumTotal() {
        return albumTotal;
    }

    public void setAlbumTotal(int albumTotal) {
        this.albumTotal = albumTotal;
    }

    public int getAlbumContributors() {
        return albumContributors;
    }

    public void setAlbumContributors(int albumContributors) {
        this.albumContributors = albumContributors;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "Growth [cid=" + cid + ", albumContributors="
                + albumContributors + ", albumTotal=" + albumTotal
                + ", albumDate=" + albumDate + ", albumName=" + albumName
                + ", strKey=" + strKey + ", praised=" + ", images=" + pics
                + "]";
    }

    @Override
    public void read(SQLiteDatabase db) {
        Cursor cursor = db
                .query(Const.GROWTH_ALBUM_TABLE_NAME,
                        new String[] { "cid,albumName,albumDate,year,month,day,albumTotal,albumContributors, strKey" },
                        "strKey=? and cid=? and albumName=? and year=? and month=? and day=?",
                        new String[] { this.strKey, this.cid + "", albumName,
                                year, month, day }, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            String strKey = cursor.getString(cursor.getColumnIndex("strKey"));
            String albumName = cursor.getString(cursor
                    .getColumnIndex("albumName"));
            String albumDate = cursor.getString(cursor
                    .getColumnIndex("albumDate"));
            int albumTotal = cursor.getInt(cursor.getColumnIndex("albumTotal"));
            int albumContributors = cursor.getInt(cursor
                    .getColumnIndex("albumContributors"));
            String year = cursor.getString(cursor.getColumnIndex("year"));
            String month = cursor.getString(cursor.getColumnIndex("month"));
            String day = cursor.getString(cursor.getColumnIndex("day"));
            this.cid = cid;
            this.albumContributors = albumContributors;
            this.albumTotal = albumTotal;
            this.strKey = strKey;
            this.albumName = albumName;
            this.albumDate = albumDate;
            this.month = month;
            this.year = year;
            this.day = day;
        }
        cursor.close();
        // read growth images
        List<GrowthAlbumImages> images = new ArrayList<GrowthAlbumImages>();
        Cursor cursor2 = db.query(Const.GROWTH_ALBUM_IMAGE_TABLE_NAME,
                new String[] { "picID", "cid", "picGrowthID", "picHappened",
                        "location", "picPath", "strKey", "albumName", "year",
                        "month", "day" },
                "cid=? and albumName=? and year=? and month=? and day=?",
                new String[] { this.cid + "", albumName, year, month, day },
                null, null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            for (int i = 0; i < cursor2.getCount(); i++) {
                int picID = cursor2.getInt(cursor2.getColumnIndex("picID"));
                int cid = cursor2.getInt(cursor2.getColumnIndex("cid"));
                int picGrowthID = cursor2.getInt(cursor2
                        .getColumnIndex("picGrowthID"));
                String picHappened = cursor2.getString(cursor2
                        .getColumnIndex("picHappened"));
                String location = cursor2.getString(cursor2
                        .getColumnIndex("location"));
                String picPath = cursor2.getString(cursor2
                        .getColumnIndex("picPath"));
                String strKey = cursor2.getString(cursor2
                        .getColumnIndex("strKey"));
                String albumName = cursor2.getString(cursor2
                        .getColumnIndex("albumName"));
                String year = cursor2.getString(cursor2.getColumnIndex("year"));
                String month = cursor2.getString(cursor2
                        .getColumnIndex("month"));
                String day = cursor2.getString(cursor2.getColumnIndex("day"));
                GrowthAlbumImages albumImages = new GrowthAlbumImages(cid,
                        picID, picGrowthID, picPath, picHappened, location);
                albumImages.setStrKey(strKey);
                albumImages.setYear(year);
                albumImages.setMonth(month);
                albumImages.setDay(day);
                albumImages.setAlbumName(albumName);
                images.add(albumImages);
                cursor2.moveToNext();

            }
        }
        cursor2.close();
        this.pics = images;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.GROWTH_ALBUM_TABLE_NAME;
        ContentValues cv = new ContentValues();
        cv.put("cid", cid);
        cv.put("albumName", albumName);
        cv.put("albumDate", albumDate);
        cv.put("albumTotal", albumTotal);
        cv.put("albumContributors", albumContributors);
        cv.put("strKey", strKey);
        cv.put("year", year);
        cv.put("month", month);
        cv.put("day", day);

        db.insert(dbName, null, cv);
        // write images
        for (GrowthAlbumImages gImage : this.pics) {
            gImage.setStrKey(strKey);
            gImage.setAlbumName(albumName);
            gImage.setYear(year);
            gImage.setMonth(month);
            gImage.setDay(day);
            gImage.write(db);
        }
    }

    public GrowthAlbum(int cid, List<GrowthAlbumImages> pics, String albumName,
            String albumDate, int albumTotal, int albumContributors) {
        this.pics = pics;
        this.albumContributors = albumContributors;
        this.albumDate = albumDate;
        this.albumTotal = albumTotal;
        this.albumName = albumName;
        this.cid = cid;
    }

}
