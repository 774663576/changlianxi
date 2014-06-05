package com.changlianxi.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.GrowthCommentListParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.util.DateUtils;

/**
 * Comment List of a growth
 * 
 * Usage:
 * 
 * get a growth's comment list:
 *  // new GrowthCommentList gcl // or gcl = grow.getCommentList(); 
 *  gcl.read(); 
 *  gcl.getComments();
 * 
 * refresh a growth's comments list:
 *  // new GrowthCommentList gcl 
 *  gcl.read();
 *  // ...
 *  gcl.refresh(); // get new comments
 *  // ...
 *  gcl.write();
 * 
 * 
 * @author jieme
 * 
 */
public class GrowthCommentList extends AbstractData implements Serializable {

    private static final long serialVersionUID = 719620751679662881L;
    public final static String LIST_API = "/growth/icomments";

    protected int cid = 0;
    protected int gid = 0;
    protected long startTime = 0L; // data start time, in milliseconds
    protected long endTime = 0L; // data end time
    protected long lastReqTime = 0L; // last request time of comment data
    protected int total = 0;
    protected List<GrowthComment> comments = new ArrayList<GrowthComment>();
    protected List<Integer> list = new ArrayList<Integer>();
    private int serverCount;

    public GrowthCommentList() {
        super();
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public GrowthCommentList(int cid, int gid) {
        this.cid = cid;
        this.gid = gid;
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

    public List<GrowthComment> getComments() {
        return comments;
    }

    public void setComments(List<GrowthComment> comments) {
        this.comments = comments;
    }

    public void addComment(GrowthComment comment) {
        this.comments.add(0, comment);
        this.status = Status.UPDATE;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getLastReqTime() {
        return lastReqTime;
    }

    public void setLastReqTime(long lastReqTime) {
        this.lastReqTime = lastReqTime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getServerCount() {
        return serverCount;
    }

    public void setServerCount(int serverCount) {
        this.serverCount = serverCount;
    }

    protected void sort(boolean byTimeAsc) {
        Collections.sort(this.comments, GrowthComment.getComparator(byTimeAsc));
    }

    @Override
    public void read(SQLiteDatabase db) {
        if (comments == null) {
            comments = new ArrayList<GrowthComment>();
        } else {
            comments.clear();
        }

        // read comments
        Cursor cursor = db.query(Const.GROWTH_COMMENT_TABLE_NAME, new String[] {
                "gcid", "uid", "replyid", "content", "time" },
                "gid=? and isForMe=?", new String[] { this.gid + "", "0" },
                null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int gcid = cursor.getInt(cursor.getColumnIndex("gcid"));
                int uid = cursor.getInt(cursor.getColumnIndex("uid"));
                int replyid = cursor.getInt(cursor.getColumnIndex("replyid"));
                String content = cursor.getString(cursor
                        .getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));

                GrowthComment comment = new GrowthComment(cid, gid, gcid, uid,
                        content);
                comment.setReplyid(replyid);
                comment.setTime(time);
                comment.setStatus(Status.OLD);
                comments.add(comment);
                long joinTime = DateUtils.convertToDate(time);
                if (startTime == 0 || joinTime < startTime) {
                    startTime = joinTime;
                }
                if (endTime == 0 || joinTime > endTime) {
                    endTime = joinTime;
                }

                cursor.moveToNext();
            }
        }
        cursor.close();

        // read last request times
        Cursor cursor2 = db.query(Const.GROWTH_TABLE_NAME,
                new String[] { "lastCommentsReqTime" }, "id=?",
                new String[] { this.gid + "" }, null, null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            long lastCommentsReqTime = cursor2.getLong(cursor2
                    .getColumnIndex("lastCommentsReqTime"));
            this.lastReqTime = lastCommentsReqTime;
        }
        cursor2.close();

        this.status = Status.OLD;
        sort(false);
    }

    @Override
    public void write(SQLiteDatabase db) {
        if (this.status != Status.OLD) {
            // write one by one
            int cacheCnt = 0;
            for (GrowthComment comment : comments) {
                if (comment.getStatus() != Status.DEL) {
                    cacheCnt++;
                }
                if (cacheCnt > Const.GROWTH_COMMENT_MAX_CACHE_COUNT_PER_ITEM) {
                    comment.setStatus(Status.DEL);
                }
                comment.write(db);
            }

            // write last request time
            ContentValues cv = new ContentValues();
            cv.put("lastCommentsReqTime", lastReqTime);
            db.update(Const.GROWTH_TABLE_NAME, cv, "id=?", new String[] { gid
                    + "" });

            this.status = Status.OLD;
        }
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void update(IData data) {
        if (!(data instanceof GrowthCommentList)) {
            return;
        }

        GrowthCommentList another = (GrowthCommentList) data;
        this.list = another.getList();
        if (another.comments.size() == 0) {
            return;
        }
        this.serverCount = another.comments.size();
        this.total = another.getTotal();
        boolean isNewer = true; // another list is newer than current list
        if (another.endTime <= this.startTime) {
            isNewer = false;
        }

        // old ones
        Map<Integer, GrowthComment> olds = new HashMap<Integer, GrowthComment>();
        for (GrowthComment comment : this.comments) {
            olds.put(comment.getGcid(), comment);
        }

        // join new ones
        boolean canJoin = false;
        Map<Integer, GrowthComment> news = new HashMap<Integer, GrowthComment>();
        for (GrowthComment comment : another.comments) {
            int gid = comment.getGcid();
            news.put(gid, comment);

            if (olds.containsKey(gid)) {
                olds.get(gid).update(comment);
                canJoin = true;
            } else {
                this.comments.add(comment);
            }
        }

        if (isNewer) {
            this.lastReqTime = another.lastReqTime;
            if (another.total == another.getComments().size()) {
                canJoin = true;
            }

            if (!canJoin) {
                for (int gid : olds.keySet()) {
                    if (news.containsKey(gid)) {
                        olds.get(gid).setStatus(Status.DEL);
                    }
                }
                this.startTime = another.startTime;
            }
            this.endTime = another.endTime;
        } else {
            this.startTime = another.startTime;
        }
        this.status = Status.UPDATE;
        sort(false);
    }

    /**
     * refresh new growth comments list from server for the first time
     */
    public void refresh() {
        refresh(0);
    }

    /**
     * refresh new growth comments list from server, with start time
     * 
     * @param startTime
     */
    public void refresh(long startTime) {
        refresh(startTime, 0);
    }

    /**
     * refresh new growth comments list from server, with start and end time
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public RetError refresh(long startTime, long endTime) {
        IParser parser = new GrowthCommentListParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", gid);
        if (startTime > 0) {
            params.put("start", startTime);
        }
        if (endTime > 0) {
            params.put("end", endTime);
        }
        Result ret = ApiRequest.requestWithToken(GrowthCommentList.LIST_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            update((GrowthCommentList) ret.getData());

            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public void insert(GrowthComment comment) {
        GrowthCommentList gcl = new GrowthCommentList(cid, comment.getGid());
        List<GrowthComment> comments = new ArrayList<GrowthComment>();
        comments.add(comment);
        gcl.setComments(comments);
        long time = DateUtils.convertToDate(comment.getTime());
        gcl.setLastReqTime(time);
        gcl.setTotal(1);
        gcl.setStartTime(time);
        gcl.setEndTime(time);

        update(gcl);
    }

}
