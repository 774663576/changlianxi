package com.changlianxi.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.GrowthYearAlbumParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

public class GrowthAlbumList extends AbstractData implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public final static String IYEARALBUM_API = "/growth/iYearAlbum";
    public final static String IMONTHALBUM_API = "/growth/iMonthAlbum";
    public final static String IDAYALBUM_API = "/growth/iDayAlbum";

    private List<GrowthAlbum> albumList = new ArrayList<GrowthAlbum>();
    private List<Growth> growthList = new ArrayList<Growth>();
    private String strKey = "";
    private String year = "";
    private String month = "";
    private String day = "";
    private int serverCount = -1;// 服务器返回的当前请求结果条数

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

    public String getStrKey() {
        return strKey;
    }

    public void setStrKey(String strKey) {
        this.strKey = strKey;
    }

    public List<Growth> getGrowthList() {
        return growthList;
    }

    public void setGrowthList(List<Growth> growthList) {
        this.growthList = growthList;
    }

    public int getServerCount() {
        return serverCount;
    }

    public void setServerCount(int serverCount) {
        this.serverCount = serverCount;
    }

    private int cid = 0;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public List<GrowthAlbum> getAlbum() {
        Collections.sort(this.albumList, getAlbumComparator(false));
        return albumList;
    }

    public void setAlbum(List<GrowthAlbum> album) {
        this.albumList = album;
    }

    public GrowthAlbumList(int cid, List<GrowthAlbum> album) {
        this.cid = cid;
        this.albumList = album;
    }

    public GrowthAlbumList(int cid, List<GrowthAlbum> album,
            List<Growth> growthList) {
        this.cid = cid;
        this.albumList = album;
        this.growthList = growthList;
    }

    @Override
    public void read(SQLiteDatabase db) {
        albumList.clear();
        growthList.clear();
        Cursor cursor = db.query(Const.GROWTH_ALBUM_TABLE_NAME,
                new String[] { "cid,strKey,albumName,year,month,day" },
                "cid=? and strKey=? and year=? and month=? and day=?",
                new String[] { this.cid + "", strKey, year, month, day }, null,
                null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int cid = cursor.getInt(cursor.getColumnIndex("cid"));
                String strKey = cursor.getString(cursor
                        .getColumnIndex("strKey"));
                String albumName = cursor.getString(cursor
                        .getColumnIndex("albumName"));
                GrowthAlbum album = new GrowthAlbum(cid, strKey);
                album.setAlbumName(albumName);
                album.setYear(year);
                album.setMonth(month);
                album.setDay(day);
                albumList.add(album);
                cursor.moveToNext();
            }
        }
        cursor.close();
        for (GrowthAlbum album : albumList) {
            album.read(db);

        }
        // read growth
        Cursor cursor2 = db.query(Const.ALBUM_GROWTH_TABLE_NAME, new String[] {
                "cid", "id", "publisher", "content", "location", "happened",
                "published", "praiseCnt", "commentCnt", "ispraised", "strKey",
                "albumName", "year", "month", "day" },
                "cid=? and strKey=? and year=? and month=? and day=?",
                new String[] { this.cid + "", strKey, year, month, day }, null,
                null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            for (int i = 0; i < cursor2.getCount(); i++) {
                int cid = cursor2.getInt(cursor2.getColumnIndex("cid"));
                int id = cursor2.getInt(cursor2.getColumnIndex("id"));
                int publisher = cursor2.getInt(cursor2
                        .getColumnIndex("publisher"));
                String content = cursor2.getString(cursor2
                        .getColumnIndex("content"));
                String location = cursor2.getString(cursor2
                        .getColumnIndex("location"));
                String happened = cursor2.getString(cursor2
                        .getColumnIndex("happened"));
                String published = cursor2.getString(cursor2
                        .getColumnIndex("published"));
                int praiseCnt = cursor2.getInt(cursor2
                        .getColumnIndex("praiseCnt"));
                int commentCnt = cursor2.getInt(cursor2
                        .getColumnIndex("commentCnt"));
                int isPraised = cursor2.getInt(cursor2
                        .getColumnIndex("isPraised"));
                Growth g = new Growth(cid, id, publisher, content, location,
                        happened, published);
                g.setPraiseCnt(praiseCnt);
                g.setCommentCnt(commentCnt);
                g.setPraised(isPraised > 0);
                growthList.add(g);
                cursor2.moveToNext();
            }
        }
        cursor2.close();

    }

    @Override
    public void write(SQLiteDatabase db) {
        db.delete(Const.GROWTH_ALBUM_TABLE_NAME,
                "strKey=? and year=? and month=? and day=? and cid=?",
                new String[] { strKey, year, month, day, cid + "" });
        db.delete(Const.GROWTH_ALBUM_IMAGE_TABLE_NAME,
                "strKey=? and year=? and month=? and day=? and cid=?",
                new String[] { strKey, year, month, day, cid + "" });
        // for (GrowthAlbum album : albumList) {
        for (int i = 0; i < albumList.size(); i++) {
            GrowthAlbum album = albumList.get(i);
            album.setStrKey(strKey);
            album.setYear(year);
            album.setMonth(month);
            album.setDay(day);
            album.write(db);
        }
        // write growth
        db.delete(Const.ALBUM_GROWTH_TABLE_NAME,
                "strKey=? and year=? and month=? and day=? and cid=?",
                new String[] { strKey, year, month, day, cid + "" });
        String dbName = Const.ALBUM_GROWTH_TABLE_NAME;
        for (int i = 0; i < growthList.size(); i++) {
            // for (Growth growth : growthList) {
            Growth growth = growthList.get(i);
            ContentValues cv = new ContentValues();
            cv.put("cid", cid);
            cv.put("id", growth.getId());
            cv.put("publisher", growth.getPublisher());
            cv.put("content", growth.getContent());
            cv.put("location", growth.getLocation());
            cv.put("happened", growth.getHappened());
            cv.put("published", growth.getPublished());
            cv.put("praiseCnt", growth.getPraiseCnt());
            cv.put("commentCnt", growth.getCommentCnt());
            cv.put("isPraised", growth.isPraised() ? 1 : 0);
            cv.put("strKey", strKey);
            cv.put("year", year);
            cv.put("month", month);
            cv.put("day", day);
            db.insert(dbName, null, cv);
        }

    }

    private void update(GrowthAlbumList another) {
        this.serverCount = another.getAlbum().size();

        for (GrowthAlbum album : another.getAlbum()) {
            this.albumList.add(album);
        }
        for (Growth g : another.getGrowthList()) {
            this.growthList.add(g);
        }
    }

    private void updateYear(GrowthAlbumList another) {
        this.serverCount = another.getAlbum().size();
        List<String> names = new ArrayList<String>();
        for (GrowthAlbum album : this.albumList) {
            names.add(album.getAlbumName());
        }
        for (GrowthAlbum album : another.getAlbum()) {
            if (!names.contains(album.getAlbumName())) {
                this.albumList.add(album);
            } else {
                for (int i = 0; i < this.albumList.size(); i++) {
                    if (this.albumList.get(i).getAlbumName()
                            .equals(album.getAlbumName())) {
                        this.albumList.remove(i);
                        this.albumList.add(album);
                        break;
                    }
                }
            }
        }
        List<Integer> ids = new ArrayList<Integer>();
        for (Growth g : this.growthList) {
            ids.add(g.getId());
        }
        for (Growth g : another.getGrowthList()) {
            if (!ids.contains(g.getId())) {
                this.growthList.add(g);
            }
        }
    }

    public void upDateDayAlbum(GrowthAlbumList another) {
        this.serverCount = another.getAlbum().size();
        for (GrowthAlbum album : another.getAlbum()) {
            if (album.getAlbumName().equals(
                    this.albumList.get(this.albumList.size() - 1)
                            .getAlbumName())) {
                for (GrowthAlbumImages albumImg : album.getPics()) {
                    this.albumList.get(this.albumList.size() - 1).getPics()
                            .add(albumImg);
                }
                continue;
            }
            this.albumList.add(album);
        }
        for (Growth g : another.getGrowthList()) {
            this.growthList.add(g);
        }
    }

    public RetError refushYearAlbum(int startY, int endY) {
        IParser parser = new GrowthYearAlbumParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("startY", startY);
        params.put("endY", endY);
        Result ret = ApiRequest
                .requestWithToken(IYEARALBUM_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            GrowthAlbumList another = (GrowthAlbumList) ret.getData();
            updateYear(another);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public RetError refushMonthAlbum(int startY, int endY, int startM, int endM) {
        IParser parser = new GrowthYearAlbumParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("startY", startY);
        params.put("endY", endY);
        params.put("startM", startM);
        params.put("endM", endM);
        Result ret = ApiRequest.requestWithToken(IMONTHALBUM_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            GrowthAlbumList another = (GrowthAlbumList) ret.getData();
            update(another);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public RetError refushDayhAlbum(int startY, int endY, int startM, int endM,
            int startD, int endD, int startTime, int endTime) {
        IParser parser = new GrowthYearAlbumParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("startM", startM);
        params.put("endM", endM);
        params.put("startY", startY);
        params.put("endY", endY);
        params.put("startD", startD);
        params.put("endD", endD);
        Result ret = ApiRequest.requestWithToken(IDAYALBUM_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            GrowthAlbumList another = (GrowthAlbumList) ret.getData();
            update(another);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * 下拉刷新使用
     * @param startY
     * @param endY
     * @param startM
     * @param endM
     * @param startD
     * @param endD
     * @param startTime
     * @param endTime
     * @return
     */
    public RetError refushDayhAlbumMore(int startY, int endY, int startM,
            int endM, int startD, int endD, int startTime, int endTime) {
        IParser parser = new GrowthYearAlbumParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("startM", startM);
        params.put("endM", endM);
        params.put("startY", startY);
        params.put("endY", endY);
        params.put("startD", startD);
        params.put("endD", endD);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        Result ret = ApiRequest.requestWithToken(IDAYALBUM_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            GrowthAlbumList another = (GrowthAlbumList) ret.getData();
            // update(another);
            upDateDayAlbum(another);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    private Comparator<GrowthAlbum> getAlbumComparator(boolean byTimeAsc) {
        if (byTimeAsc) {
            return new Comparator<GrowthAlbum>() {
                @Override
                public int compare(GrowthAlbum l, GrowthAlbum r) {
                    long lTime = DateUtils
                            .convertGrowthToDate(l.getAlbumDate()), rTime = DateUtils
                            .convertGrowthToDate(r.getAlbumDate());
                    return lTime > rTime ? 1 : -1;
                }
            };
        } else {
            return new Comparator<GrowthAlbum>() {
                @Override
                public int compare(GrowthAlbum l, GrowthAlbum r) {
                    long lTime = DateUtils
                            .convertGrowthToDate(l.getAlbumDate()), rTime = DateUtils
                            .convertGrowthToDate(r.getAlbumDate());
                    return lTime > rTime ? -1 : 1;
                }
            };
        }
    }
}
