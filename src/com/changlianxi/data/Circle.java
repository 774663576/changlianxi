package com.changlianxi.data;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.ArrayParser;
import com.changlianxi.data.parser.CircleParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.parser.StringParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.ArrayResult;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.StringResult;
import com.changlianxi.db.Const;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.StringUtils;

/**
 * Circle data
 * 
 * Usage:
 * 
 * get a circle info:
 *  // new circle 
 *  circle.read(); 
 *  // circle.get***()
 * 
 * refresh a circle's detail info:
 *  // new circle 
 *  circle.read() 
 *  circle.refresh(); // request and merge with local data 
 *  circle.write();
 * 
 * 
 * upload after edit:
 *  // new circle1
 *  // ...edit... 
 *  // new circle2 after edit
 *  circle1.uploadAfterEdit(circle2); 
 *  circle1.write();
 * 
 * upload logo:
 *  // new circle 
 *  // ...edit logo... 
 *  // new logo
 *  circle.uploadLogo(newLogo); 
 *  circle.write();
 * 
 * add new circle:
 *  // new circle
 *  // ...set circle info... 
 *  circle.uploadForAdd();
 *  circle.write();
 * 
 * other operations:
 *  // new circle 
 *  circle.dissolve();
 *  circle.write();
 * 
 * @author nnjme
 * 
 */
public class Circle extends AbstractData implements Serializable {
    private static final long serialVersionUID = -4396119074161355179L;
    public final static String DETAIL_API = "/circles/idetail";
    public final static String EDIT_API = "/circles/iedit";
    public final static String EDIT_LOGO_API = "/circles/iuploadLogo";
    public final static String ADD_API = "/circles/iadd";
    public final static String DISSOLVE_API = "/circles/idissolve";
    private final static String EDIT_GROUP_API = "/circles/ieditGroups";
    private final static String SET_MANAGERS_API = "/circles/isetManagers";
    // circle basic info
    private int id = 0;
    private String name = "";
    private String description = "";
    private String logo = "";
    private int creator = 0;
    private int myInvitor = 0;
    private String created = "";
    private String joinTime = "";
    private boolean isNew = false;
    private CircleMemberState myState = CircleMemberState.STATUS_INVALID;
    private int sequence = 0;
    // member counts
    private int totalCnt = 0;
    private int invitingCnt = 0;
    private int verifiedCnt = 0;
    private int unverifiedCnt = 0;

    // new count
    private int newMemberCnt = 0;
    private int newGrowthCnt = 0;
    private int newMyDetailEditCnt = 0;
    private int newDynamicCnt = 0;
    private int newGrowthCommentCnt = 0;

    // circle roles
    private List<CircleRole> roles = new ArrayList<CircleRole>();

    // circle groups
    private List<CircleGroup> groups = new ArrayList<CircleGroup>();

    public Circle(int id) {
        this(id, "");
    }

    public Circle(int id, String name) {
        this(id, name, "");
    }

    public Circle(int id, String name, String description) {
        this(id, name, description, "");
    }

    public Circle(int id, String name, String description, String logo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.logo = logo;
    }

    public boolean isEmpty() {
        return this.creator == 0;
    }

    public List<CircleGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CircleGroup> groups) {
        this.groups = groups;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalLogo() {
        return logo;
    }

    public String getLogo() {
        return getLogo(160);
    }

    public String getLogo(int size) {
        return StringUtils.getAliyunOSSImageUrl(logo, size, size);
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public CircleMemberState getMyState() {
        return myState;
    }

    public void setMyState(CircleMemberState myState) {
        this.myState = myState;
    }

    public List<CircleRole> getRoles() {
        return roles;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setRoles(List<CircleRole> roles) {
        if (roles != null) {
            this.roles = roles;
        }
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int getMyInvitor() {
        return myInvitor;
    }

    public void setMyInvitor(int myInvitor) {
        this.myInvitor = myInvitor;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public int getInvitingCnt() {
        return invitingCnt;
    }

    public void setInvitingCnt(int invitingCnt) {
        this.invitingCnt = invitingCnt;
    }

    public int getVerifiedCnt() {
        return verifiedCnt;
    }

    public void setVerifiedCnt(int verifiedCnt) {
        this.verifiedCnt = verifiedCnt;
    }

    public int getUnverifiedCnt() {
        return unverifiedCnt;
    }

    public void setUnverifiedCnt(int unverifiedCnt) {
        this.unverifiedCnt = unverifiedCnt;
    }

    public int getNewMemberCnt() {
        return newMemberCnt;
    }

    public void setNewMemberCnt(int newMemberCnt) {
        if (this.newMemberCnt != newMemberCnt) {
            this.newMemberCnt = newMemberCnt;
            if (this.status == Status.OLD) {
                this.status = Status.UPDATE;
            }
        }
    }

    public int getNewGrowthCnt() {
        return newGrowthCnt;
    }

    public void setNewGrowthCnt(int newGrowthCnt) {
        if (this.newGrowthCnt != newGrowthCnt) {
            this.newGrowthCnt = newGrowthCnt;
            if (this.status == Status.OLD) {
                this.status = Status.UPDATE;
            }
        }
    }

    public int getNewMyDetailEditCnt() {
        return newMyDetailEditCnt;
    }

    public void setNewMyDetailEditCnt(int newChatCnt) {
        if (this.newMyDetailEditCnt != newChatCnt) {
            this.newMyDetailEditCnt = newChatCnt;
            if (this.status == Status.OLD) {
                this.status = Status.UPDATE;
            }
        }
    }

    public int getNewDynamicCnt() {
        return newDynamicCnt;
    }

    public void setNewDynamicCnt(int newDynamicCnt) {
        if (this.newDynamicCnt != newDynamicCnt) {
            this.newDynamicCnt = newDynamicCnt;
            if (this.status == Status.OLD) {
                this.status = Status.UPDATE;
            }
        }
    }

    public int getNewGrowthCommentCnt() {
        return newGrowthCommentCnt;
    }

    public void setNewGrowthCommentCnt(int newGrowthCommentCnt) {
        if (this.newGrowthCommentCnt != newGrowthCommentCnt) {
            this.newGrowthCommentCnt = newGrowthCommentCnt;
            if (this.status == Status.OLD) {
                this.status = Status.UPDATE;
            }
        }
    }

    @Override
    public String toString() {
        return "Circle [id=" + id + ", name=" + name + "]";
    }

    public void getCircleCreatprById(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.CIRCLE_TABLE_NAME,
                new String[] { "creator" }, "id=?",
                new String[] { this.id + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int creator = cursor.getInt(cursor.getColumnIndex("creator"));
            this.creator = creator;

        }
        cursor.close();
    }

    @Override
    public void read(SQLiteDatabase db) {
        // read circle basic info and member counts
        Cursor cursor = db.query(Const.CIRCLE_TABLE_NAME, new String[] {
                "name", "logo", "description", "isNew", "mystate", "creator",
                "myInvitor", "created", "joinTime", "total", "inviting",
                "verified", "unverified", "newMemberCnt", "newGrowthCnt",
                "newMyDetailEditCnt", "newDynamicCnt", "newGrowthCommentCnt" },
                "id=?", new String[] { this.id + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String logo = cursor.getString(cursor.getColumnIndex("logo"));
            String description = cursor.getString(cursor
                    .getColumnIndex("description"));
            int isNew = cursor.getInt(cursor.getColumnIndex("isNew"));
            String myState = cursor.getString(cursor.getColumnIndex("myState"));
            int creator = cursor.getInt(cursor.getColumnIndex("creator"));
            int myInvitor = cursor.getInt(cursor.getColumnIndex("myInvitor"));
            String created = cursor.getString(cursor.getColumnIndex("created"));
            String joinTime = cursor.getString(cursor
                    .getColumnIndex("joinTime"));
            int total = cursor.getInt(cursor.getColumnIndex("total"));
            int inviting = cursor.getInt(cursor.getColumnIndex("inviting"));
            int verified = cursor.getInt(cursor.getColumnIndex("verified"));
            int unverified = cursor.getInt(cursor.getColumnIndex("unverified"));
            int newMemberCnt = cursor.getInt(cursor
                    .getColumnIndex("newMemberCnt"));
            int newGrowthCnt = cursor.getInt(cursor
                    .getColumnIndex("newGrowthCnt"));
            int newChatCnt = cursor.getInt(cursor
                    .getColumnIndex("newMyDetailEditCnt"));
            int newDynamicCnt = cursor.getInt(cursor
                    .getColumnIndex("newDynamicCnt"));
            int newGrowthCommentCnt = cursor.getInt(cursor
                    .getColumnIndex("newGrowthCommentCnt"));

            this.name = name;
            this.logo = logo;
            this.description = description;
            this.isNew = (isNew > 0);
            this.myState = CircleMemberState.convert(myState);
            this.creator = creator;
            this.myInvitor = myInvitor;
            this.created = created;
            this.joinTime = joinTime;
            this.totalCnt = total;
            this.invitingCnt = inviting;
            this.verifiedCnt = verified;
            this.unverifiedCnt = unverified;
            this.newMemberCnt = newMemberCnt;
            this.newGrowthCnt = newGrowthCnt;
            this.newMyDetailEditCnt = newChatCnt;
            this.newDynamicCnt = newDynamicCnt;
            this.newGrowthCommentCnt = newGrowthCommentCnt;

        }
        cursor.close();

        // read circle roles
        List<CircleRole> roles = new ArrayList<CircleRole>();
        Cursor cursor2 = db.query(Const.CIRCLE_ROLE_TABLE_NAME,
                new String[] { "id" }, "cid=?", new String[] { this.id + "" },
                null, null, null);
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            for (int i = 0; i < cursor2.getCount(); i++) {
                int crid = cursor2.getInt(cursor.getColumnIndex("id"));
                CircleRole cRole = new CircleRole(this.id, crid);
                roles.add(cRole);

                cursor2.moveToNext();
            }
        }
        cursor2.close();
        for (CircleRole cRole : roles) {
            cRole.read(db);
        }
        this.setRoles(roles);

        // set status
        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        if (!db.isOpen()) {
            db = DBUtils.dbase.getWritableDatabase();
        }

        String dbName = Const.CIRCLE_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "id=?", new String[] { id + "" });
            clearCircleCache(id, db);
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("name", name);
        cv.put("logo", logo);
        cv.put("description", description);
        cv.put("isNew", isNew ? 1 : 0);
        cv.put("myState", myState.name());
        cv.put("creator", creator);
        cv.put("myinvitor", myInvitor);
        cv.put("created", created);
        cv.put("joinTime", joinTime);
        cv.put("total", totalCnt);
        cv.put("inviting", invitingCnt);
        cv.put("verified", verifiedCnt);
        cv.put("unverified", unverifiedCnt);
        if (this.status == Status.NEW) {
            db.delete(dbName, "id=?", new String[] { id + "" }); // TODO too bad
            db.insert(dbName, null, cv);
        } else if (this.status == Status.UPDATE) {
            db.update(dbName, cv, "id=?", new String[] { id + "" });
        }

        // write roles
        for (CircleRole cRole : roles) {
            cRole.write(db);
        }
        // wirte circle groups
        writeCirleGroups(db);
        this.status = Status.OLD;
    }

    private void clearCircleCache(int cid, SQLiteDatabase db) {
        // role
        String dbName = Const.CIRCLE_ROLE_TABLE_NAME;
        db.delete(dbName, "cid=?", new String[] { cid + "" });

        // members and person details
        dbName = Const.CIRCLE_MEMBER_TABLE_NAME;
        db.delete(dbName, "cid=?", new String[] { cid + "" });
        dbName = Const.PERSON_DETAIL_TABLE_NAME1;
        db.delete(dbName, "cid=?", new String[] { cid + "" });

        // growth, growth images, growth comments
        dbName = Const.GROWTH_TABLE_NAME;
        db.delete(dbName, "cid=?", new String[] { cid + "" });
        dbName = Const.GROWTH_IMAGE_TABLE_NAME;
        db.delete(dbName, "cid=?", new String[] { cid + "" });
        dbName = Const.GROWTH_COMMENT_TABLE_NAME;
        db.delete(dbName, "cid=?", new String[] { cid + "" });

        // chats
        // dbName = Const.CIRCLE_CHAT_TABLE_NAME;
        // db.delete(dbName, "cid=?", new String[] { cid + "" });

        // dynamic
        dbName = Const.CIRCLE_DYNAMIC_TABLE_NAME;
        db.delete(dbName, "cid=?", new String[] { cid + "" });

        // time record
        dbName = Const.TIME_RECORD_TABLE_NAME;
        db.delete(dbName, "key=?",
                new String[] { Const.TIME_RECORD_KEY_PREFIX_CIRCLECHAT + cid });
        db.delete(
                dbName,
                "key=?",
                new String[] { Const.TIME_RECORD_KEY_PREFIX_CIRCLEDYNAMIC + cid });
        db.delete(
                dbName,
                "key=?",
                new String[] { Const.TIME_RECORD_KEY_PREFIX_CIRCLEMEMBER + cid });
        db.delete(dbName, "key=?",
                new String[] { Const.TIME_RECORD_KEY_PREFIX_GROWTH + cid });
        db.delete(
                dbName,
                "key=?",
                new String[] { Const.TIME_RECORD_KEY_PREFIX_COMMENTSFORME + cid });
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof Circle)) {
            return;
        }
        Circle another = (Circle) data;
        boolean isChange = false;
        if (this.id != another.id) {
            this.id = another.id;
            isChange = true;
        }
        if (!this.name.equals(another.name)) {
            this.name = another.name;
            isChange = true;
        }
        if (!this.logo.equals(another.logo)) {
            this.logo = another.logo;
            isChange = true;
        }
        if (!this.description.equals(another.description)) {
            this.description = another.description;
            isChange = true;
        }
        if (this.myState != another.myState) {
            this.myState = another.myState;
            isChange = true;
        }
        if (this.creator != another.creator) {
            this.creator = another.creator;
            isChange = true;
        }
        if (!this.created.equals(another.created)) {
            this.created = another.created;
            isChange = true;
        }
        if (this.totalCnt != another.totalCnt) {
            this.totalCnt = another.totalCnt;
            isChange = true;
        }
        if (this.invitingCnt != another.invitingCnt) {
            this.invitingCnt = another.invitingCnt;
            isChange = true;
        }
        if (this.verifiedCnt != another.verifiedCnt) {
            this.verifiedCnt = another.verifiedCnt;
            isChange = true;
        }
        if (this.unverifiedCnt != another.unverifiedCnt) {
            this.unverifiedCnt = another.unverifiedCnt;
            isChange = true;
        }

        updateRoles(another, true);
        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    @SuppressLint("UseSparseArrays")
    private boolean updateRoles(Circle another, boolean changeCount) {
        boolean isChange = false;
        Map<Integer, CircleRole> olds = new HashMap<Integer, CircleRole>();
        Map<Integer, CircleRole> news = new HashMap<Integer, CircleRole>();
        for (CircleRole cr : this.roles) {
            olds.put(cr.getId(), cr);
        }
        for (CircleRole cr : another.roles) {
            int crid = cr.getId();
            news.put(crid, cr);
            if (olds.containsKey(crid)) {
                olds.get(crid).update(cr, changeCount);
                if (olds.get(crid).getStatus() == Status.UPDATE) {
                    isChange = true;
                }
            } else {
                // new
                this.roles.add(cr);
                isChange = true;
            }
        }
        for (CircleRole cr : this.roles) {
            if (!news.containsKey(cr.getId())) {
                cr.setStatus(Status.DEL);
                isChange = true;
            }
        }
        return isChange;
    }

    private void updateForEditInfo(Circle another) {
        boolean isChange = false;
        if (this.id != another.id) {
            this.id = another.id;
            isChange = true;
        }
        if (!this.name.equals(another.name)) {
            this.name = another.name;
            isChange = true;
        }
        if (!this.description.equals(another.description)) {
            this.description = another.description;
            isChange = true;
        }
        updateRoles(another, false);
        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    public void updateForListChange(Circle another) {
        boolean isChange = false;
        if (this.id != another.id) {
            this.id = another.id;
            isChange = true;
        }
        if (!this.name.equals(another.name)) {
            this.name = another.name;
            isChange = true;
        }
        if (!this.logo.equals(another.logo)) {
            this.logo = another.logo;
            isChange = true;
        }
        if (this.myInvitor != another.myInvitor) {
            this.myInvitor = another.myInvitor;
            isChange = true;
        }
        if (this.isNew != another.isNew) {
            this.isNew = another.isNew;
            isChange = true;
        }
        if (this.creator != another.creator) {
            this.creator = another.creator;
            isChange = true;
        }
        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    public void writeCirleGroups(SQLiteDatabase db) {
        for (CircleGroup group : groups) {
            group.write(db);
        }
    }

    public void readCircleGorups(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.CIRCLE_GROUP_TABLE_NAME, new String[] {
                "cid", "group_name", "group_id" }, "cid=?", new String[] { id
                + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String group_name = cursor.getString(cursor
                        .getColumnIndex("group_name"));
                int cid = cursor.getInt(cursor.getColumnIndex("cid"));
                int group_id = cursor.getInt(cursor.getColumnIndex("group_id"));
                CircleGroup group = new CircleGroup(cid, group_id, group_name);
                group.setStatus(Status.OLD);
                this.groups.add(group);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    public int getCircleGroupsCount(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.CIRCLE_GROUP_TABLE_NAME,
                new String[] { "group_id" }, "cid=?", new String[] { id + "" },
                null, null, null);
        return cursor.getCount();
    }

    /**
     * refresh this circle info from server
     */
    public RetError refresh() {
        return this.refresh(id);
    }

    /**
     * refresh this circle info with circle id from server
     * 
     * @param id
     */
    public RetError refresh(int id) {
        IParser parser = new CircleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", id);
        Result ret = ApiRequest.requestWithToken(Circle.DETAIL_API, params,
                parser);

        if (ret.getStatus() == RetStatus.SUCC) {
            this.update(ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    private boolean isReallyChangedForUpload(Circle another) {
        return (another.id != this.id) || (!another.name.equals(this.name))
                || (!another.description.equals(this.description));
    }

    @SuppressLint("UseSparseArrays")
    private JSONArray getChangedRolesForUpload(Circle another) {
        Map<Integer, CircleRole> olds = new HashMap<Integer, CircleRole>();
        for (CircleRole cr : this.roles) {
            olds.put(cr.getId(), cr);
        }

        JSONArray jsonArr = new JSONArray();
        Map<Integer, CircleRole> news = new HashMap<Integer, CircleRole>();
        for (CircleRole cr : another.roles) {
            int crid = cr.getId();
            news.put(crid, cr);

            if (olds.containsKey(crid)) {
                if (!cr.getName().equals(olds.get(crid).getName())) {
                    // edit
                    try {
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("op", "edit");
                        jsonObj.put("id", crid);
                        jsonObj.put("name", cr.getName());
                        jsonArr.put(jsonObj);
                    } catch (Exception e) {
                    }
                }
            } else {
                // new
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("op", "new");
                    jsonObj.put("id", 0);
                    jsonObj.put("name", cr.getName());
                    jsonArr.put(jsonObj);
                } catch (Exception e) {
                }
            }
        }

        for (CircleRole cr : this.roles) {
            if (!news.containsKey(cr.getId())) {
                // del
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("op", "del");
                    jsonObj.put("id", cr.getId());
                    jsonObj.put("name", cr.getName());
                    jsonArr.put(jsonObj);
                } catch (Exception e) {
                }
            }
        }

        return jsonArr;
    }

    /**
     * upload edit info to server, and update local data while upload success
     * 
     * @param another
     * @return
     */
    public RetError uploadAfterEdit(Circle another) {
        JSONArray changedRoles = getChangedRolesForUpload(another);
        if (changedRoles.length() == 0 && !isReallyChangedForUpload(another)) {
            return RetError.NONE;
        }

        IParser parser = new ArrayParser("roles");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", another.id);
        params.put("name", another.name);
        params.put("description", another.description);
        if (changedRoles.length() > 0) {
            params.put("roles", changedRoles.toString());
        }

        Result ret = ApiRequest.requestWithToken(Circle.EDIT_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            ArrayResult aret = (ArrayResult) ret;
            if (changedRoles.length() > 0
                    && changedRoles.length() == aret.getArrs().size()) {
                List<CircleRole> roles = new ArrayList<CircleRole>();
                for (int i = 0; i < changedRoles.length(); i++) {
                    int crid = (Integer) aret.getArrs().get(i);
                    if (crid > 0) {
                        try {
                            CircleRole role = new CircleRole(another.id, crid);
                            JSONObject jobj = (JSONObject) changedRoles.opt(i);
                            role.setName(jobj.getString("name"));
                            roles.add(role);
                        } catch (JSONException e) {
                        }
                    }
                }
                another.setRoles(roles);
            } else {
                // size not equal???
                updateForEditInfo(another);

            }

            return RetError.NONE;
        } else {

            return ret.getErr();
        }
    }

    private boolean isLogoChanged(String logo) {
        return logo != null && logo.length() > 0 && !this.logo.equals(logo);
    }

    /**
     * upload new logo to server, and update local logo while upload success
     * 
     * @param logo
     * @return
     */
    public RetError uploadLogo(String logo) {
        if (!isLogoChanged(logo)) {
            return RetError.NONE;
        }
        File file = new File(logo);
        if (file == null || !file.exists()) {
            return RetError.INVALID;
        }

        IParser parser = new StringParser("logo");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", id);
        Result ret = ApiRequest.uploadFileWithToken(Circle.EDIT_LOGO_API,
                params, file, "logo", parser);
        file.delete();
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            this.logo = sret.getStr();
            this.status = Status.UPDATE;
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * upload new circle to server, and reset id and some count info while
     * upload success
     * 
     * @return
     */
    public RetError uploadForAdd() {
        IParser parser = new StringParser("cid");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("description", description);

        Result ret = ApiRequest
                .requestWithToken(Circle.ADD_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            this.id = Integer.parseInt(sret.getStr());
            this.totalCnt = 1;
            this.verifiedCnt = 1;
            // this.status = Status.NEW;
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * dissolve the circle
     * 
     * @return
     */
    public RetError dissolve() {
        IParser parser = new SimpleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", id);

        Result ret = ApiRequest.requestWithToken(Circle.DISSOLVE_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            this.status = Status.DEL;
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public void getCircleName(SQLiteDatabase db) {
        Cursor cursor = db.query(Const.CIRCLE_TABLE_NAME,
                new String[] { "name", }, "id=?",
                new String[] { this.id + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex("name"));
            this.name = name;
        }
        cursor.close();
    }

    /**
     * 获取编辑的圈子分组  json格式
     * @param editGroups
     * @return
     */
    public String getEditGroups(List<CircleGroup> editGroups) {
        JSONArray array = null;
        try {
            array = new JSONArray();
            JSONObject object = null;
            for (CircleGroup group : editGroups) {
                object = new JSONObject();
                if (group.getGroupsId() == 0) {
                    object.put("op", "new");
                } else {
                    object.put("op", "del");
                    object.put("id", group.getGroupsId());
                }
                object.put("name", group.getGroupsName());
                array.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array.toString();
    }

    /**
     * 编辑圈子分组
     */
    public RetError editGroups(List<CircleGroup> editListsGroups) {
        String groups = getEditGroups(editListsGroups);
        IParser parser = new ArrayParser("results");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", id);
        params.put("groups", groups);
        Result ret = ApiRequest.requestWithToken(Circle.EDIT_GROUP_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            for (CircleGroup group : editListsGroups) {
                if (group.getGroupsId() != 0) {
                    group.setStatus(com.changlianxi.data.AbstractData.Status.DEL);
                    group.write(DBUtils.getDBsa(2));
                }
            }
            ArrayResult aret = (ArrayResult) ret;
            for (int i = 0; i < aret.getArrs().size(); i++) {
                for (CircleGroup group : editListsGroups) {
                    if (group.getGroupsId() == 0) {
                        int id = Integer.valueOf(String.valueOf(aret.getArrs()
                                .get(i)));
                        group.setGroupsId(id);
                        group.setStatus(Status.NEW);
                        group.write(DBUtils.getDBsa(2));
                        break;
                    }
                }
            }
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * 设置圈子管理员
     */
    public RetError setManagers(List<CircleMember> oldListMembers,
            List<CircleMember> newListMembers) {
        StringBuilder sb = new StringBuilder();
        for (CircleMember m : newListMembers) {
            sb.append(m.getPid() + ",");
        }
        IParser parser = new SimpleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", id);
        params.put("managers", sb.toString());
        Result ret = ApiRequest.requestWithToken(Circle.SET_MANAGERS_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            Set<CircleMember> result = new HashSet<CircleMember>();
            // 删除的管理员
            result.clear();
            result.addAll(oldListMembers);
            result.removeAll(newListMembers);
            for (CircleMember m : result) {
                m.setStatus(Status.UPDATE);
                m.setManager(false);
                m.updateManager(DBUtils.getDBsa(2));
            }
            // 新增加的管理成员
            result.clear();
            result.addAll(newListMembers);
            result.removeAll(oldListMembers);
            for (CircleMember m : result) {
                m.setStatus(Status.UPDATE);
                m.setManager(true);
                m.updateManager(DBUtils.getDBsa(2));
            }
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    public static Comparator<Circle> getComparator(boolean byTimeAsc) {
        if (byTimeAsc) {
            return new Comparator<Circle>() {
                @Override
                public int compare(Circle l, Circle r) {
                    long lTime = DateUtils.convertToDate(l.getJoinTime()), rTime = DateUtils
                            .convertToDate(r.getJoinTime());
                    int lSequ = l.getSequence(), rSequ = r.getSequence();
                    if (lSequ != 0 && rSequ != 0) {
                        return lSequ > rSequ ? 1 : -1;
                    }
                    return lTime > rTime ? 1 : -1;
                }
            };
        } else {
            return new Comparator<Circle>() {
                @Override
                public int compare(Circle l, Circle r) {
                    long lTime = DateUtils.convertToDate(l.getJoinTime()), rTime = DateUtils
                            .convertToDate(r.getJoinTime());
                    int lSequ = l.getSequence(), rSequ = r.getSequence();
                    if (lSequ != 0 && rSequ != 0) {
                        return lSequ > rSequ ? 1 : -1;
                    }
                    return lTime > rTime ? -1 : 1;
                }
            };
        }
    }

}
