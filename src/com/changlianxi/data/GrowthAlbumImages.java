package com.changlianxi.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.StringUtils;

public class GrowthAlbumImages extends AbstractData implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int picID = 0;// 照片id
    private int picGrowthID = 0;// 照片所属成长id
    private String picPath = "";// 照片地址
    private String picHappened = "";// 照片发生时间
    private String location = "";// 照片发生地点
    public final static String API_ICORRECTPIC = "/growth/icorrectPic";
    private String strKey = "";
    private int cid;
    private String albumName = "";
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

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getStrKey() {
        return strKey;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public void setStrKey(String strKey) {
        this.strKey = strKey;
    }

    public GrowthAlbumImages(int cid, int picID, int picGrowthID,
            String picPath, String picHappened, String location) {
        this.picGrowthID = picGrowthID;
        this.picHappened = picHappened;
        this.picID = picID;
        this.location = location;
        this.picPath = picPath;
        this.cid = cid;
    }

    public GrowthAlbumImages() {
    }

    public int getPicID() {
        return picID;
    }

    public void setPicID(int picID) {
        this.picID = picID;
    }

    public int getPicGrowthID() {
        return picGrowthID;
    }

    public void setPicGrowthID(int picGrowthID) {
        this.picGrowthID = picGrowthID;
    }

    public String getPicPath() {
        return picPath;
    }

    public String getPicPath(int size) {
        return getPicPath(size, size);
    }

    public String getPicPath(int width, int height) {
        return StringUtils.getAliyunOSSImageUrl(picPath, width, height);
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getPicHappened() {
        return picHappened;
    }

    public void setPicHappened(String picHappened) {
        this.picHappened = picHappened;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "AlbumImage [picID=" + picID + ", picGrowthID=" + picGrowthID
                + ", picPath=" + picPath + ", location=" + location + "]";
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.GROWTH_ALBUM_IMAGE_TABLE_NAME;
        ContentValues cv = new ContentValues();
        cv.put("picID", picID);
        cv.put("cid", cid);
        cv.put("picGrowthID", picGrowthID);
        cv.put("picHappened", picHappened);
        cv.put("location", location);
        cv.put("picPath", picPath);
        cv.put("strKey", strKey);
        cv.put("albumName", albumName);
        cv.put("year", year);
        cv.put("month", month);
        cv.put("day", day);
        db.insert(dbName, null, cv);

    }

    public RetError editTimeAndLocation(int cid, long editHappened,
            String editLocation) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gpid", picID);
        params.put("happened", editHappened);
        params.put("location", editLocation);
        IParser parser = new SimpleParser();
        Result ret = ApiRequest.requestWithToken(API_ICORRECTPIC, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

}
