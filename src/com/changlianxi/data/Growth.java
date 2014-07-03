package com.changlianxi.data;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.EditGrowthParser;
import com.changlianxi.data.parser.GrowthCommentParser;
import com.changlianxi.data.parser.GrowthImageParser;
import com.changlianxi.data.parser.GrowthParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.parser.StringParser;
import com.changlianxi.data.parser.UploadGrowthParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.StringResult;
import com.changlianxi.db.Const;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.BitmapUtils;
import com.changlianxi.util.DateUtils;

/**
 * circle growth
 * 
 * Usage:
 * 
 * get a growth info:
 *  // new growth 
 *  growth.read(); 
 *  // growth.get***()
 * 
 * refresh a growth's detail info:
 *  // new growth 
 *  growth.read() 
 *  growth.refresh(); // request and merge with local data 
 *  growth.write();
 * 
 * 
 * upload after edit:
 *  // new growth1
 *  // ...edit... 
 *  // new growth2 after edit
 *  growth1.uploadAfterEdit(growth2); 
 *  growth1.write();
 * 
 * upload images:
 *  // new growth 
 *  // ...edit images... 
 *  // new images list
 *  growth.uploadImages(imageList); 
 *  growth.write();
 * 
 * add new growth:
 *  // new growth 
 *  // ...set growth info... 
 *  growth.uploadForAdd();
 *  // new images list 
 *  growth.uploadImages(imageList); 
 *  growth.write();
 * 
 * delete growth:
 *  // new growth 
 *  // ...set growth info... 
 *  growth.uploadForDel();
 *  growth.write();
 * 
 * praise growth:
 *  // new growth 
 *  // ... 
 *  growth.uploadMyPraise(false); // cancel:
 *  growth.uploadMyPraise(true); 
 *  growth.write();
 * 
 * 
 * @author nnjme
 * 
 */
public class Growth extends AbstractData implements Serializable {
    private static final long serialVersionUID = 6781304245643137573L;
    public final static String DETAIL_API = "/growth/idetail";
    // public final static String EDIT_API = "/growth/igrowth";
    public final static String UPLOAD_IMAGE_API = "/growth/iuploadImage";
    public final static String REMOVE_IMAGE_API = "/growth/iremoveImage";
    public final static String ADD_API = "/growth/igrowth";
    public final static String ADD_API2 = "/growth/igrowth2";
    public final static String EDIT_API = "/growth/ieditGrowth";
    public final static String REMOVE_API = "/growth/iremoveGrowth";
    public final static String PRAISE_API = "/growth/imyPraise";
    public final static String CANCEL_PRAISE_API = "/growth/icancelPraise";
    public final static String COMMENT_API = "/growth/imyComment";
    public final static String SHARE_API = "/growth/ishare";

    private int id = 0;
    private int cid = 0;
    private int publisher = 0;
    private String content = "";
    private String location = "";
    private String happened = ""; // happen time
    private String published = ""; // publish time
    private String coordinate = "";// coordinate用来构造一个经纬度信息的字符串，经度、纬度的长度都控制在10个字符，纬度在前经度在后，以逗号分割。
    private int praiseCnt = 0;
    private int commentCnt = 0;
    private boolean isPraised = false; // do I praised this growth
    private boolean isPraising = false;
    private List<GrowthImage> images = new ArrayList<GrowthImage>();
    private GrowthCommentList commentList = null;
    private boolean isUpLoading = false;
    private boolean loadingFail = false;

    public Growth(int cid) {
        this(cid, 0);
    }

    public Growth(int cid, int id) {
        this.cid = cid;
        this.id = id;
        this.commentList = new GrowthCommentList(cid, id);
    }

    public Growth(int cid, int id, int publisher, String content,
            String location, String happened, String published) {
        this.cid = cid;
        this.id = id;
        this.publisher = publisher;
        this.content = content;
        this.location = location;
        this.happened = happened;
        this.published = published;
        this.commentList = new GrowthCommentList(cid, id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPublisher() {
        return publisher;
    }

    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHappened() {
        return happened;
    }

    public String getFormatHappenedTime() {
        return DateUtils.formatTime(happened);
    }

    public void setHappened(String happened) {
        this.happened = happened;
    }

    public String getPublished() {
        return published;
    }

    public String getFormatPublishedTime() {
        return DateUtils.formatTime(published);
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getPraiseCnt() {
        return praiseCnt;
    }

    public void setPraiseCnt(int praiseCnt) {
        this.praiseCnt = praiseCnt;
    }

    public int getCommentCnt() {
        return commentCnt;
    }

    public void setCommentCnt(int commentCnt) {
        this.commentCnt = commentCnt;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setPraised(boolean isPraised) {
        this.isPraised = isPraised;
    }

    public boolean isPraising() {
        return isPraising;
    }

    public void setPraising(boolean isPraising) {
        this.isPraising = isPraising;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    private void addImage(GrowthImage image) {
        if (this.images == null) {
            this.images = new ArrayList<GrowthImage>();
        }
        images.add(image);
    }

    public List<GrowthImage> getImages() {
        return images;
    }

    public void setImages(List<GrowthImage> images) {
        this.images = images;
    }

    public GrowthCommentList getCommentList() {
        return commentList;
    }

    public void setCommentList(GrowthCommentList commentList) {
        this.commentList = commentList;
    }

    public boolean isUpLoading() {
        return isUpLoading;
    }

    public void setUpLoading(boolean isUpLoading) {
        this.isUpLoading = isUpLoading;
    }

    public boolean isLoadingFail() {
        return loadingFail;
    }

    public void setLoadingFail(boolean loadingFail) {
        this.loadingFail = loadingFail;
    }

    @Override
    public String toString() {
        return "Growth [id=" + id + ", publisher=" + publisher + ", content="
                + content + ", location=" + location + ", happened=" + happened
                + ", published=" + published + ", praised=" + praiseCnt
                + ", commented=" + commentCnt + ", images="
                + ((images != null) ? 0 : images.size()) + "]";
    }

    @Override
    public void read(SQLiteDatabase db) {
        // read growth basic info
        Cursor cursor = db.query(Const.GROWTH_TABLE_NAME, new String[] { "cid",
                "publisher", "content", "location", "happened", "published",
                "praiseCnt", "commentCnt", "ispraised" }, "id=?",
                new String[] { this.id + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int cid = cursor.getInt(cursor.getColumnIndex("cid"));
            int publisher = cursor.getInt(cursor.getColumnIndex("publisher"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String location = cursor.getString(cursor
                    .getColumnIndex("location"));
            String happened = cursor.getString(cursor
                    .getColumnIndex("happened"));
            String published = cursor.getString(cursor
                    .getColumnIndex("published"));
            int praiseCnt = cursor.getInt(cursor.getColumnIndex("praiseCnt"));
            int commentCnt = cursor.getInt(cursor.getColumnIndex("commentCnt"));
            int isPraised = cursor.getInt(cursor.getColumnIndex("isPraised"));

            this.cid = cid;
            this.publisher = publisher;
            this.content = content;
            this.location = location;
            this.happened = happened;
            this.published = published;
            this.praiseCnt = praiseCnt;
            this.commentCnt = commentCnt;
            this.isPraised = (isPraised > 0);
        }
        cursor.close();

        // read growth images
        List<GrowthImage> images = new ArrayList<GrowthImage>();
        Cursor cursor2 = db.query(Const.GROWTH_IMAGE_TABLE_NAME, new String[] {
                "imgId", "img" }, "gid=?", new String[] { this.id + "" }, null,
                null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            for (int i = 0; i < cursor2.getCount(); i++) {
                int imgId = cursor2.getInt(cursor2.getColumnIndex("imgId"));
                String img = cursor2.getString(cursor2.getColumnIndex("img"));
                GrowthImage image = new GrowthImage(cid, id, imgId, img);
                image.setStatus(Status.OLD);
                images.add(image);

                cursor2.moveToNext();
            }
        }
        cursor2.close();
        this.setImages(images);

        // set status
        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.GROWTH_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "id=?", new String[] { id + "" });
            // delete images
            for (GrowthImage gImage : this.images) {
                gImage.setStatus(Status.DEL);
                gImage.write(db);
            }

            return;
        }
        ContentValues cv = new ContentValues();
        cv.put("cid", cid);
        cv.put("id", id);
        cv.put("publisher", publisher);
        cv.put("content", content);
        cv.put("location", location);
        cv.put("happened", happened);
        cv.put("published", published);
        cv.put("praiseCnt", praiseCnt);
        cv.put("commentCnt", commentCnt);
        cv.put("isPraised", isPraised ? 1 : 0);

        if (this.status == Status.NEW) {
            Cursor cursor = db.query(Const.GROWTH_TABLE_NAME,
                    new String[] { "id" }, "id=?", new String[] { id + "" },
                    null, null, null);
            if (cursor.getCount() > 0) {
                return;
            }
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "id=?", new String[] { id + "" });
        }

        // write images
        for (GrowthImage gImage : this.images) {
            gImage.write(db);
        }

        this.status = Status.OLD;
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof Growth)) {
            return;
        }
        Growth another = (Growth) data;

        boolean isChange = false;
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.id != another.id) {
            this.id = another.id;
            isChange = true;
        }
        if (this.publisher != another.publisher) {
            this.publisher = another.publisher;
            isChange = true;
        }
        if (!this.content.equals(another.content)) {
            this.content = another.content;
            isChange = true;
        }
        if (!this.location.equals(another.location)) {
            this.location = another.location;
            isChange = true;
        }
        if (!this.happened.equals(another.happened)) {
            this.happened = another.happened;
            isChange = true;
        }
        if (!this.published.equals(another.published)) {
            this.published = another.published;
            isChange = true;
        }
        if (this.praiseCnt != another.praiseCnt) {
            this.praiseCnt = another.praiseCnt;
            isChange = true;
        }
        if (this.commentCnt != another.commentCnt) {
            this.commentCnt = another.commentCnt;
            isChange = true;
        }
        if (this.isPraised != another.isPraised) {
            this.isPraised = another.isPraised;
            isChange = true;
        }
        if (updateImages(another)) {
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    @SuppressLint("UseSparseArrays")
    private boolean updateImages(Growth another) {
        boolean isChange = false;
        Map<Integer, GrowthImage> olds = new HashMap<Integer, GrowthImage>();
        Map<Integer, GrowthImage> news = new HashMap<Integer, GrowthImage>();
        for (GrowthImage gImage : this.images) {
            olds.put(gImage.getImgId(), gImage);
        }
        for (GrowthImage gImage : another.images) {
            int imgId = gImage.getImgId();
            news.put(imgId, gImage);
            if (olds.containsKey(imgId)) {
                olds.get(imgId).update(gImage);
                if (olds.get(imgId).getStatus() == Status.UPDATE) {
                    isChange = true;
                }
            } else {
                // new
                this.images.add(gImage);
                isChange = true;
            }

        }
        for (GrowthImage gImage : this.images) {
            if (!news.containsKey(gImage.getImgId())) {
                gImage.setStatus(Status.DEL);
                isChange = true;
            }
        }
        return isChange;
    }

    public void upDatePraise(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put("praiseCnt", praiseCnt);
        cv.put("isPraised", isPraised ? 1 : 0);
        db.update(Const.GROWTH_TABLE_NAME, cv, "id=? and cid=?", new String[] {
                id + "", cid + "" });

    }

    private void updateForEditInfo(Growth another) {
        boolean isChange = false;
        if (!this.content.equals(another.content)) {
            this.content = another.content;
            isChange = true;
        }
        if (!this.location.equals(another.location)) {
            this.location = another.location;
            isChange = true;
        }
        if (!this.happened.equals(another.happened)) {
            this.happened = another.happened;
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    /**
     * refresh this growth info from server
     */
    public RetError refresh() {
        return this.refresh(id);
    }

    /**
     * refresh growth info with id from server
     * 
     * @param id
     */
    public RetError refresh(int id) {
        IParser parser = new GrowthParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", id);
        Result ret = ApiRequest.requestWithToken(Growth.DETAIL_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            this.update(ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public boolean isReallyChangedForUpload(Growth another) {
        return (another.id != this.id)
                || (!another.content.equals(this.content))
                || (!another.location.equals(this.location))
                || (!another.happened.equals(this.happened));
    }

    public boolean isImagesChanged(List<GrowthImage> gImages) {
        if (gImages.size() != this.getImages().size()) {
            return true;
        }
        if (gImages.size() > this.getImages().size()
                || gImages.size() < this.getImages().size()) {
            return true;
        }
        List<Integer> newsID = new ArrayList<Integer>();
        for (GrowthImage img : gImages) {
            newsID.add(img.getImgId());
        }
        List<Integer> oldsID = new ArrayList<Integer>();
        for (GrowthImage img : this.getImages()) {
            oldsID.add(img.getImgId());
        }
        if (!newsID.equals(oldsID)) {
            return true;
        }
        return false;

    }

    /**
     * upload edit info to server, and update local data while upload success
     * 
     * @param another
     * @return
     */
    public RetError uploadAfterEdit(Growth another) {
        if (!isReallyChangedForUpload(another)) {
            return RetError.NONE;
        }

        IParser parser = new StringParser("gid");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", another.getCid());
        params.put("gid", another.getId());
        params.put("content", another.getContent());
        params.put("location", another.getLocation());
        params.put("time", another.getPublished());

        Result ret = ApiRequest.requestWithToken(Growth.EDIT_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            updateForEditInfo(another);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    @SuppressLint("UseSparseArrays")
    public void uploadImages(List<GrowthImage> gImages) {
        Map<Integer, GrowthImage> olds = new HashMap<Integer, GrowthImage>();
        for (GrowthImage gImage : this.images) {
            olds.put(gImage.getImgId(), gImage);

        }

        boolean isChange = false;
        Map<Integer, GrowthImage> news = new HashMap<Integer, GrowthImage>();
        for (GrowthImage gImage : gImages) {

            int imgId = gImage.getImgId();
            if (imgId == 0) {
                // new
                this.uploadNewImage(gImage);
                this.addImage(gImage);
                isChange = true;
                continue;
            }

            news.put(imgId, gImage);
            if (olds.containsKey(imgId)) {
                // already exist
            } else {
                // new
                gImage.setStatus(Status.NEW);
                this.addImage(gImage);
                isChange = true;
            }
        }
        for (int imgId : olds.keySet()) {

            if (!news.containsKey(imgId)) {
                DBUtils.getDBsa(2).delete(Const.GROWTH_IMAGE_TABLE_NAME,
                        "imgid=?", new String[] { imgId + "" });// 从本地删除
                // del
                this.uploadDelImage(olds.get(imgId));
                isChange = true;
            }
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    /**
     * upload new growth image to server, and reset image info while upload
     * success
     * 
     * @param gImage
     * @return
     */
    public RetError uploadNewImage(GrowthImage gImage) {
        IParser parser = new GrowthImageParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", id);
        File file = BitmapUtils.getImageFile(gImage.getImg());
        if (file == null || !file.exists()) {
            return RetError.INVALID;
        }
        Result ret = ApiRequest.uploadFileWithToken(Growth.UPLOAD_IMAGE_API,
                params, file, "img", parser);
        file.delete();
        if (ret.getStatus() == RetStatus.SUCC) {
            GrowthImage retGImage = (GrowthImage) ret.getData();
            gImage.setCid(cid);
            gImage.setGid(id);
            gImage.setImgId(retGImage.getImgId());
            gImage.setImg(retGImage.getImg());
            gImage.setStatus(Status.NEW);
            this.images.add(gImage);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * upload growth image delete info to server
     * 
     * @param gImage
     * @return
     */
    public RetError uploadDelImage(GrowthImage gImage) {
        IParser parser = new SimpleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", id);
        params.put("imgid", gImage.getImgId());

        Result ret = ApiRequest.requestWithToken(Growth.REMOVE_IMAGE_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            gImage.setStatus(Status.DEL);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * upload new growth to server, and reset id while upload success
     * 
     * @return
     */
    public RetError uploadForAdd() {
        IParser parser = new StringParser("gid");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", 0);
        params.put("content", content);
        params.put("location", location);
        params.put("time", happened);
        Result ret = ApiRequest
                .requestWithToken(Growth.ADD_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            this.id = Integer.parseInt(sret.getStr());
            // this.status = Status.UPDATE;
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public RetError uploadForAdd1() {
        List<File> bytesimg = new ArrayList<File>();
        for (GrowthImage img : this.images) {
            File file = BitmapUtils.getImageFile(img.getImg());
            if (file == null) {
                continue;
            }
            bytesimg.add(file);
        }
        IParser parser = new UploadGrowthParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("content", content);
        params.put("location", location);
        params.put("happen", happened);
        params.put("coordinate", coordinate);
        Result ret = ApiRequest.uploadFileArrayWithToken(ADD_API2, params,
                bytesimg, "image", parser);
        delGorwthImgFile(bytesimg);
        if (ret.getStatus() == RetStatus.SUCC) {
            Growth g = (Growth) ret.getData();
            this.id = g.getId();
            this.images.clear();
            this.images = g.getImages();
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    private void delGorwthImgFile(List<File> files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 上传编辑后的成长及图片
     * @return
     */
    public RetError uploadForEdit(Growth editGrowth) {
        List<File> listFils = getEditGrowthNewImage(editGrowth.getImages());
        String dels = getEditGrowthDelsId(editGrowth.getImages());
        IParser parser = new EditGrowthParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", id);
        params.put("content", editGrowth.getContent());
        params.put("location", editGrowth.getLocation());
        params.put("time", editGrowth.getHappened());
        params.put("dels", dels);
        // params.put("coordinate", editGrowth.getCoordinate());
        Result ret = ApiRequest.uploadFileArrayWithToken(EDIT_API, params,
                listFils, "image", parser);
        delGorwthImgFile(listFils);
        if (ret.getStatus() == RetStatus.SUCC) {
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * 获取编辑成长中新增加的图片
     */
    private List<File> getEditGrowthNewImage(List<GrowthImage> gImages) {
        List<File> listFils = new ArrayList<File>();
        for (GrowthImage img : gImages) {
            if (img.getImgId() == 0) {
                File file = BitmapUtils.getImageFile(img.getImg());
                listFils.add(file);
            }
        }
        return listFils;

    }

    /**
     * 获取编辑成长中被删除的图片ID
     */
    @SuppressLint({ "UseSparseArrays", "UseSparseArrays" })
    private String getEditGrowthDelsId(List<GrowthImage> gImages) {
        String delsId = "";
        Map<Integer, GrowthImage> olds = new HashMap<Integer, GrowthImage>();
        for (GrowthImage gImage : this.images) {
            olds.put(gImage.getImgId(), gImage);
        }
        Map<Integer, GrowthImage> news = new HashMap<Integer, GrowthImage>();
        for (GrowthImage gImage : gImages) {
            int imgId = gImage.getImgId();
            if (imgId == 0) {
                continue;
            }
            news.put(imgId, gImage);
        }
        for (int imgId : olds.keySet()) {
            if (!news.containsKey(imgId)) {
                DBUtils.getDBsa(2).delete(Const.GROWTH_IMAGE_TABLE_NAME,
                        "imgid=?", new String[] { imgId + "" });// 从本地删除
                // del
                delsId = delsId + imgId + ",";
            }
        }
        return delsId.length() > 0 ? delsId.substring(0, delsId.length() - 1)
                : delsId;
    }

    /**
     * upload growth delete info to server
     * 
     * @return
     */
    public RetError uploadForDel() {
        IParser parser = new SimpleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", id);

        Result ret = ApiRequest.requestWithToken(Growth.REMOVE_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            this.status = Status.DEL;
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * upload my praise or cancel praise info to server
     * 
     * @return
     */
    public RetError uploadMyPraise(boolean isCancel) {
        IParser parser = new StringParser("count");
        if ((!isCancel && this.isPraised) || (isCancel && !this.isPraised)) {

            return RetError.NONE;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", id);
        String api = !isCancel ? Growth.PRAISE_API : Growth.CANCEL_PRAISE_API;
        Result ret = ApiRequest.requestWithToken(api, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            String count = sret.getStr();
            this.praiseCnt = Integer.parseInt(count);
            this.isPraised = !isCancel;
            this.status = Status.UPDATE;
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * upload my comment info to server
     * 
     * @return
     */
    public RetError uploadMyComment(GrowthComment comment) {
        IParser parser = new GrowthCommentParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", id);
        params.put("content", comment.getContent());
        params.put("replyid", comment.getReplyid());

        Result ret = ApiRequest.requestWithToken(Growth.COMMENT_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            GrowthComment newComent = (GrowthComment) ret.getData();
            newComent.setTime(DateUtils.getCurrDateStr());
            System.out.println("time:::::::::::::" + newComent.getContent()
                    + "       " + newComent.getTime());
            newComent.setReplyid(comment.getReplyid());
            this.commentList.addComment(newComent);
            this.commentCnt = newComent.getTotal();
            this.status = Status.UPDATE;
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * share growth to other circles
     * 
     * @param toCid
     * @return
     */
    public RetError share(int toCid) {
        if (toCid <= 0) {
            return RetError.NONE;
        }
        IParser parser = new SimpleParser();// StringParser("gid");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("from", cid);
        params.put("to", toCid);
        params.put("gid", id);

        Result ret = ApiRequest.requestWithToken(Growth.SHARE_API, params,
                parser);
        return ret.getErr();
    }

    public static Comparator<Growth> getPublishedComparator(boolean byTimeAsc) {
        if (byTimeAsc) {
            return new Comparator<Growth>() {
                @Override
                public int compare(Growth l, Growth r) {
                    long lTime = DateUtils.convertToDate(l.getPublished()), rTime = DateUtils
                            .convertToDate(r.getPublished());
                    return lTime > rTime ? 1 : -1;
                }
            };
        } else {
            return new Comparator<Growth>() {
                @Override
                public int compare(Growth l, Growth r) {
                    long lTime = DateUtils.convertToDate(l.getPublished()), rTime = DateUtils
                            .convertToDate(r.getPublished());
                    return lTime > rTime ? -1 : 1;
                }
            };
        }
    }

    public static Comparator<Growth> getHappenedComparator(boolean byTimeAsc) {
        if (byTimeAsc) {
            return new Comparator<Growth>() {
                @Override
                public int compare(Growth l, Growth r) {
                    long lTime = DateUtils.convertToDate(l.getHappened()), rTime = DateUtils
                            .convertToDate(r.getHappened());
                    return lTime > rTime ? 1 : -1;
                }
            };
        } else {
            return new Comparator<Growth>() {
                @Override
                public int compare(Growth l, Growth r) {
                    long lTime = DateUtils.convertToDate(l.getHappened()), rTime = DateUtils
                            .convertToDate(r.getHappened());
                    return lTime > rTime ? -1 : 1;
                }
            };
        }
    }

    // TODO get comments for me
}
