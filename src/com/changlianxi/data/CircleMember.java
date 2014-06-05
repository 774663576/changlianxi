package com.changlianxi.data;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.Gendar;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.CircleMemberBasicParser;
import com.changlianxi.data.parser.CircleMemberDetailParser;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.MapParser;
import com.changlianxi.data.parser.MoreArrayParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.parser.StringParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.MapResult;
import com.changlianxi.data.request.MoreArrayResult;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.StringResult;
import com.changlianxi.db.Const;
import com.changlianxi.db.DBUtils;
import com.changlianxi.util.StringUtils;

/**
 * Circle Member
 * 
 * Usage:
 * 
 * get a member's detail info:
 *  // new member 
 *  member.read();
 *  member.readDetails(); 
 *  member.getDetails();
 * 
 * refresh a member's detail info:
 *  // new member 
 *  member.read() 
 *  member.refresh(); // request and merge with local data 
 *  member.writeDetails(); 
 *  member.write();
 * 
 * get or refresh a member's basic info:
 *  // new member 
 *  member.read();
 *  member.refreshBasic();
 * 
 * upload after edit:
 *  // new member1
 *  // ...edit... 
 *  // new member2 after edit
 *  member1.uploadAfterEdit(member2); 
 *  member1.write();
 * 
 * upload avatar:
 *  // new member 
 *  // ...edit avatar... 
 *  // new avatar
 *  member.uploadAvatar(newAvatar); 
 *  member.write();
 * 
 * invite one member:
 *  // new member 
 *  // another member 
 *  member.invite(another);
 *  another.write();
 * 
 * invite more than one member:
 *  // new member
 *  // another member list
 *  member.inviteMore(memberList);
 *  // memberList write // write one by one
 *  
 *  for get a members' name and avatar:
 *  // new member
 *  if (!member.readNameAndAvatar(db)) {
 *      member.refreshBasic();
        member.write(db);
 *  }
 *  // member.getName();
 *  // member.getAvatar();
 * 
 * other operations:
 *  // new member 
 *  member.quit(); // quit the circle
 *  member.acceptInvitation(); // accept invitation 
 *  member.refuseInvitation(); // refuse invitation
 *  member.kickout(); // be kick out
 * 
 * @author nnjme
 * 
 */
public class CircleMember extends AbstractData implements Serializable {
    private static final long serialVersionUID = -2915046714387992693L;
    public final static String DETAIL_API = "/people/idetail";
    public final static String BASIC_API = "/people/ibasic";
    public final static String EDIT_API = "/people/iedit";
    public final static String UPLOAD_AVATAR_API = "/people/iuploadAvatar";
    public final static String QUIT_API = "/circles/iquit";
    public final static String ACCETP_INVITATION_API = "/circles/iacceptInvitation";
    public final static String REFUSE_INVITATION_API = "/circles/irefuseInvitation";
    public final static String KICKOUT_API = "/circles/ikickOut";
    public final static String INVITE_ONE_API = "/people/iinviteOne";
    public final static String INVITE_MORE_API = "/people/iinviteMore";
    public final static String PRIVACY_SETTINGS_API = "/circles/iprivacy";
    public final static String INITMYDETAIL_API = "/circles/iinitMyDetail";

    private int cid = 0;
    private int uid = 0;
    private int pid = 0;
    private String name = "";
    private String cellphone = "";
    private String location = "";
    private Gendar gendar = Gendar.UNKNOWN;
    private String avatar = "";
    private String birthday = "";
    private String employer = "";
    private String jobtitle = "";
    private String lastModTime = "";
    private String roleId = "";
    private String detailIds = "";
    private String auth = "";
    private String sortkey = "";// 用来排序的关键字
    private String pinyinFir = "";// 名字首字母//搜索时使用
    private List<PersonDetail> details = new ArrayList<PersonDetail>();
    private CircleMemberState state = CircleMemberState.STATUS_INVALID;
    private int cmid = 0;
    private String register = "";
    private String inviteCode = "";
    private String inviteRt = "1";// 邀请结果标示 1成功 非1失败
    private String inviteContent = "";// 邀请内容 短信预览界面 使用
    private String privacySettings = "";
    private List<EditData> editData = new ArrayList<EditData>();
    private List<PersonDetail> keyAndValue = new ArrayList<PersonDetail>();

    public CircleMember(int cid) {
        this(cid, 0);
    }

    public CircleMember(int cid, int pid) {
        this(cid, pid, 0);
    }

    public CircleMember(int cid, int pid, int uid) {
        this(cid, pid, uid, "");
    }

    public CircleMember(int cid, int pid, int uid, String name) {
        this.cid = cid;
        this.pid = pid;
        this.uid = uid;
        this.name = name;
    }

    public String getInviteRt() {
        return inviteRt;
    }

    public void setInviteRt(String inviteRt) {
        this.inviteRt = inviteRt;
    }

    public boolean isEmpty() {
        return pid == 0 && uid == 0;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAvatar(String size) {
        return StringUtils.JoinString(avatar, size);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public void setJobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }

    public CircleMemberState getState() {
        return state;
    }

    public void setState(CircleMemberState state) {
        this.state = state;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Gendar getGendar() {
        return gendar;
    }

    public void setGendar(Gendar gendar) {
        this.gendar = gendar;
    }

    public void setGendar(int gendar) {
        this.gendar = Gendar.parseInt2Gendar(gendar);
    }

    public void setGendar(String gendar) {
        this.gendar = Gendar.parseString2Gendar(gendar);
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLastModTime() {
        return lastModTime;
    }

    public void setLastModTime(String lastModTime) {
        this.lastModTime = lastModTime;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public List<PersonDetail> getDetails() {
        for (int i = details.size() - 1; i >= 0; i--) {
            if (details.get(i).status == Status.DEL) {
                details.remove(i);
            }
        }
        return details;
    }

    public void setDetails(List<PersonDetail> properties) {
        this.details = properties;
    }

    public List<PersonDetail> getKeyAndValue() {
        for (int i = keyAndValue.size() - 1; i >= 0; i--) {
            if (keyAndValue.get(i).status == Status.DEL) {
                keyAndValue.remove(i);
            }
        }
        return keyAndValue;
    }

    public void setKeyAndValue(List<PersonDetail> keyAndValue) {
        this.keyAndValue = keyAndValue;
    }

    public int getCmid() {
        return cmid;
    }

    public void setCmid(int cmid) {
        this.cmid = cmid;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getInviteContent() {
        return inviteContent;
    }

    public void setInviteContent(String inviteContent) {
        this.inviteContent = inviteContent;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getSortkey() {
        return sortkey;
    }

    public void setSortkey(String sortkey) {
        this.sortkey = sortkey;
    }

    public String getPinyinFir() {
        return pinyinFir;
    }

    public void setPinyinFir(String pinyinFir) {
        this.pinyinFir = pinyinFir;
    }

    public String getPrivacySettings() {
        return privacySettings;
    }

    public List<EditData> getEditData() {
        return editData;
    }

    public void setEditData(List<EditData> editData) {
        this.editData = editData;
    }

    public List<PersonDetailType> getPrivacyTypes() {
        String[] ids = privacySettings.split(",");
        List<PersonDetailType> typeList = new ArrayList<PersonDetailType>();
        for (String idStr : ids) {
            int id = Integer.parseInt(idStr);
            PersonDetailType type = PersonDetailType.convertToType(id);
            if (type != PersonDetailType.UNKNOWN) {
                typeList.add(type);
            }
        }
        return typeList;
    }

    public void setPrivacySettings(String privacySettings) {
        this.privacySettings = privacySettings;
    }

    public void setPrivacySettingsByIds(List<Integer> idList) {
        StringBuffer privacySettings = new StringBuffer();
        for (int id : idList) {
            if (privacySettings.length() > 0) {
                privacySettings.append(',');
            }
            privacySettings.append(id);
        }
        this.privacySettings = privacySettings.toString();
    }

    public void setPrivacySettingsByTypes(List<PersonDetailType> typeList) {
        List<Integer> idList = new ArrayList<Integer>();
        for (PersonDetailType type : typeList) {
            int id = PersonDetailType.getID(type);
            if (id > 0) {
                idList.add(id);
            }
        }
        setPrivacySettingsByIds(idList);
    }

    @Override
    public String toString() {
        return "CircleMember [cid=" + cid + ", uid=" + uid + ", pid=" + pid
                + ", name=" + name + ", cellphone=" + cellphone + ", location="
                + location + "]" + "detail=" + details + "sortKey=" + sortkey;
    }

    @Override
    public void read(SQLiteDatabase db) {
        if (!db.isOpen()) {
            db = DBUtils.dbase.getReadableDatabase();
        }
        String conditionsKey = "cid=? and pid=?";
        String[] conditionsValue = { this.cid + "", this.pid + "" };
        if (pid == 0) {
            conditionsKey = "cid=? and uid=?";
            conditionsValue = new String[] { this.cid + "", this.uid + "" };
        }
        Cursor cursor = db.query(Const.CIRCLE_MEMBER_TABLE_NAME, new String[] {
                "uid", "pid", "name", "cellphone", "location", "gendar",
                "avatar", "birthday", "employer", "jobtitle", "lastModTime",
                "roleId", "state", "detailIds", "cmid", "inviteCode", "auth",
                "pinyinFir", "sortkey", "privacySettings", "register" },
                conditionsKey, conditionsValue, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int uid = cursor.getInt(cursor.getColumnIndex("uid"));
            int pid = cursor.getInt(cursor.getColumnIndex("pid"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String cellphone = cursor.getString(cursor
                    .getColumnIndex("cellphone"));
            String location = cursor.getString(cursor
                    .getColumnIndex("location"));
            int gendar = cursor.getInt(cursor.getColumnIndex("gendar"));
            String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
            String birthday = cursor.getString(cursor
                    .getColumnIndex("birthday"));
            String employer = cursor.getString(cursor
                    .getColumnIndex("employer"));
            String jobtitle = cursor.getString(cursor
                    .getColumnIndex("jobtitle"));
            String lastModTime = cursor.getString(cursor
                    .getColumnIndex("lastModTime"));
            String roleId = cursor.getString(cursor.getColumnIndex("roleId"));
            String state = cursor.getString(cursor.getColumnIndex("state"));
            String detailIds = cursor.getString(cursor
                    .getColumnIndex("detailIds"));
            int cmid = cursor.getInt(cursor.getColumnIndex("cmid"));
            String inviteCode = cursor.getString(cursor
                    .getColumnIndex("inviteCode"));
            String auth = cursor.getString(cursor.getColumnIndex("auth"));
            String pinyinFir = cursor.getString(cursor
                    .getColumnIndex("pinyinFir"));
            String sortkey = cursor.getString(cursor.getColumnIndex("sortkey"));
            String privacySettings = cursor.getString(cursor
                    .getColumnIndex("privacySettings"));
            String register = cursor.getString(cursor
                    .getColumnIndex("register"));
            this.uid = uid;
            this.pid = pid;
            this.name = name;
            this.cellphone = cellphone;
            this.location = location;
            this.gendar = Gendar.parseInt2Gendar(gendar);
            this.avatar = avatar;
            this.birthday = birthday;
            this.employer = employer;
            this.jobtitle = jobtitle;
            this.lastModTime = lastModTime;
            this.roleId = roleId;
            this.state = CircleMemberState.convert(state);
            this.detailIds = detailIds;
            this.cmid = cmid;
            this.inviteCode = inviteCode;
            this.auth = auth;
            this.pinyinFir = pinyinFir;
            this.sortkey = sortkey;
            this.privacySettings = privacySettings;
            this.register = register;
            // set status
            this.status = Status.OLD;
        }
        cursor.close();
    }

    public boolean readNameAndAvatar(SQLiteDatabase db) {
        String conditionsKey = "cid=? and pid=?";
        String[] conditionsValue = { this.cid + "", this.pid + "" };
        if (pid == 0) {
            conditionsKey = "cid=? and uid=?";
            conditionsValue = new String[] { this.cid + "", this.uid + "" };
        }
        Cursor cursor = db.query(Const.CIRCLE_MEMBER_TABLE_NAME, new String[] {
                "uid", "pid", "name", "avatar" }, conditionsKey,
                conditionsValue, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int uid = cursor.getInt(cursor.getColumnIndex("uid"));
            int pid = cursor.getInt(cursor.getColumnIndex("pid"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
            this.uid = uid;
            this.pid = pid;
            this.name = name;
            this.avatar = avatar;
            // set status
            this.status = Status.OLD;

            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void getNameAndAvatar(SQLiteDatabase db) {
        String conditionsKey = "cid=? and pid=?";
        String[] conditionsValue = { this.cid + "", this.pid + "" };
        if (pid == 0) {
            conditionsKey = "cid=? and uid=?";
            conditionsValue = new String[] { this.cid + "", this.uid + "" };
        }
        Cursor cursor = db.query(Const.CIRCLE_MEMBER_TABLE_NAME, new String[] {
                "uid", "pid", "name", "avatar" }, conditionsKey,
                conditionsValue, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int uid = cursor.getInt(cursor.getColumnIndex("uid"));
            int pid = cursor.getInt(cursor.getColumnIndex("pid"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
            this.uid = uid;
            this.pid = pid;
            this.name = name;
            this.avatar = avatar;
            // set status
            this.status = Status.OLD;
        }
        cursor.close();
    }

    public void readDetails(SQLiteDatabase db) {
        details.clear();
        Cursor cursor;
        String conditionsKey = "cid=? and pid=?";
        String[] conditionsValue = { this.cid + "", this.pid + "" };
        if (pid == 0 && uid != 0) {
            conditionsKey = "cid=? and uid=?";
            conditionsValue = new String[] { this.cid + "", this.uid + "" };
        } else if (cid == 0 && pid == 0 && uid != 0) {
            conditionsKey = "cid=?";
            conditionsValue = new String[] { this.cid + "" };
        }
        cursor = db.query(Const.PERSON_DETAIL_TABLE_NAME1, new String[] {
                "type", "value", "start", "end", "id", "cid", "pid", "uid" },
                conditionsKey, conditionsValue, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int cid = cursor.getInt(cursor.getColumnIndex("cid"));
                int pid = cursor.getInt(cursor.getColumnIndex("pid"));
                int uid = cursor.getInt(cursor.getColumnIndex("uid"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String value = cursor.getString(cursor.getColumnIndex("value"));
                String start = cursor.getString(cursor.getColumnIndex("start"));
                String end = cursor.getString(cursor.getColumnIndex("end"));
                this.pid = pid;
                PersonDetail detail = new PersonDetail(id, cid, pid, uid,
                        PersonDetailType.convertToType(type),
                        value == null ? "" : value);
                detail.setEnd(end);
                detail.setStart(start);
                details.add(detail);
                cursor.moveToNext();
            }
        }
        cursor.close();

    }

    public void readMyCard(SQLiteDatabase db) {
        Cursor cursor = db
                .query(Const.CIRCLE_MEMBER_TABLE_NAME, new String[] { "uid",
                        "pid", "name", "cellphone", "location", "gendar",
                        "avatar", "birthday", "employer", "jobtitle",
                        "register" }, "cid=? and uid=?", new String[] {
                        this.cid + "", this.uid + "" }, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int uid = cursor.getInt(cursor.getColumnIndex("uid"));
            int pid = cursor.getInt(cursor.getColumnIndex("pid"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String cellphone = cursor.getString(cursor
                    .getColumnIndex("cellphone"));
            String location = cursor.getString(cursor
                    .getColumnIndex("location"));
            int gendar = cursor.getInt(cursor.getColumnIndex("gendar"));
            String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
            String birthday = cursor.getString(cursor
                    .getColumnIndex("birthday"));
            String employer = cursor.getString(cursor
                    .getColumnIndex("employer"));
            String jobtitle = cursor.getString(cursor
                    .getColumnIndex("jobtitle"));
            String register = cursor.getString(cursor
                    .getColumnIndex("register"));
            this.uid = uid;
            this.pid = pid;
            this.name = name;
            this.cellphone = cellphone;
            this.location = location;
            this.gendar = Gendar.parseInt2Gendar(gendar);
            this.avatar = avatar;
            this.birthday = birthday;
            this.employer = employer;
            this.jobtitle = jobtitle;
            this.register = register;
        }
        cursor.close();

    }

    // public void readDetails(SQLiteDatabase db) {
    // List<PersonDetail> mDetials = new ArrayList<PersonDetail>();
    // if (detailIds == null || "".equals(detailIds)
    // || "null".equals(detailIds)) {
    // detailIds = "";
    // } else {
    // String[] ids = this.detailIds.split(",");
    // if (ids.length > 0) {
    // for (String pdid : ids) {
    // if (!"".equals(pdid) && Integer.parseInt(pdid) > 0) {
    // PersonDetail detail = new PersonDetail(
    // Integer.parseInt(pdid), cid);
    // detail.read(db);
    // mDetials.add(detail);
    // }
    // }
    // }
    // }
    // this.setDetails(mDetials);
    // this.syncBasicAndDetail(true);
    //
    // }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.CIRCLE_MEMBER_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put("cid", cid);
        cv.put("uid", uid);
        cv.put("pid", pid);
        cv.put("name", name);
        cv.put("cellphone", cellphone);
        cv.put("location", location);
        cv.put("gendar", Gendar.parseGendar2Int(gendar));
        cv.put("avatar", avatar);
        cv.put("birthday", birthday);
        cv.put("employer", employer);
        cv.put("jobtitle", jobtitle);
        cv.put("lastModTime", lastModTime);
        cv.put("roleId", roleId);
        cv.put("state", state.name());
        cv.put("cmid", cmid);
        cv.put("inviteCode", inviteCode);
        cv.put("auth", auth);
        cv.put("pinyinFir", pinyinFir);
        cv.put("sortkey", sortkey);
        cv.put("privacySettings", privacySettings);
        cv.put("register", register);
        db.delete(dbName, "cid=? and pid=? and uid=?", new String[] { cid + "",
                pid + "", uid + "" });
        db.insert(dbName, null, cv);
        writeDetails(db);

    }

    public void writeDetails(SQLiteDatabase db) {
        if (!db.isOpen()) {
            db = DBUtils.dbase.getReadableDatabase();
        }
        if (cid == 0) {
            db.delete(Const.PERSON_DETAIL_TABLE_NAME1, "cid=?",
                    new String[] { 0 + "" });
        } else {
            db.delete(Const.PERSON_DETAIL_TABLE_NAME1,
                    "cid=? and pid=? and uid=?", new String[] { cid + "",
                            pid + "", uid + "" });
        }
        for (PersonDetail pd : details) {
            pd.write(db);
        }
    }

    @Override
    public void update(IData data) {
        if (!(data instanceof CircleMember)) {
            return;
        }

        CircleMember another = (CircleMember) data;
        boolean isChange = false;
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.uid != another.uid) {
            this.uid = another.uid;
            isChange = true;
        }
        if (this.pid != another.pid) {
            this.pid = another.pid;
            isChange = true;
        }
        if (!this.name.equals(another.name)) {
            this.name = another.name;
            isChange = true;
        }
        if (!this.cellphone.equals(another.cellphone)) {
            this.cellphone = another.cellphone;
            isChange = true;
        }
        if (!this.location.equals(another.location)) {
            this.location = another.location;
            isChange = true;
        }
        if (this.gendar != another.gendar) {
            this.gendar = another.gendar;
            isChange = true;
        }
        if (!this.avatar.equals(another.avatar)) {
            this.avatar = another.avatar;
            isChange = true;
        }
        if (!this.birthday.equals(another.birthday)) {
            this.birthday = another.birthday;
            isChange = true;
        }
        if (!this.employer.equals(another.employer)) {
            this.employer = another.employer;
            isChange = true;
        }
        if (!this.jobtitle.equals(another.jobtitle)) {
            this.jobtitle = another.jobtitle;
            isChange = true;
        }
        if (!this.auth.equals(another.auth)) {
            this.auth = another.auth;
            isChange = true;
        }

        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    /**
     * update for member list synchronize
     * 
     * @param another
     */
    public void updateListSummary(CircleMember another) {
        boolean isChange = false;
        if (this.cid != another.cid) {
            this.cid = another.cid;
            isChange = true;
        }
        if (this.uid != another.uid) {
            this.uid = another.uid;
            isChange = true;
        }
        if (this.pid != another.pid) {
            this.pid = another.pid;
            isChange = true;
        }
        if (!this.name.equals(another.name)) {
            this.name = another.name;
            isChange = true;
        }
        if (!this.cellphone.equals(another.cellphone)) {
            this.cellphone = another.cellphone;
            isChange = true;
        }
        if (!this.location.equals(another.location)) {
            this.location = another.location;
            isChange = true;
        }
        if (!this.avatar.equals(another.avatar)) {
            this.avatar = another.avatar;
            isChange = true;
        }
        if (!this.employer.equals(another.employer)) {
            this.employer = another.employer;
            isChange = true;
        }
        if (!this.jobtitle.equals(another.jobtitle)) {
            this.jobtitle = another.jobtitle;
            isChange = true;
        }
        if (!this.lastModTime.equals(another.lastModTime)) {
            this.lastModTime = another.lastModTime;
            isChange = true;
        }
        if (this.roleId != another.roleId) {
            this.roleId = another.roleId;
            isChange = true;
        }
        if (this.state != another.state) {
            this.state = another.state;
            isChange = true;
        }
        if (this.privacySettings != another.privacySettings) {
            this.privacySettings = another.privacySettings;
            isChange = true;
        }
        if (this.sortkey != another.sortkey) {
            this.sortkey = another.sortkey;
            isChange = true;
        }
        if (this.details != another.details) {
            this.details = another.details;
            isChange = true;
        }
        if (isChange && this.status == Status.OLD) {
            this.status = Status.UPDATE;
        }
    }

    @SuppressLint("UseSparseArrays")
    protected boolean updateDetails(CircleMember another) {
        if (another.getDetails().size() == 0) {
            return false;
        }

        boolean isChange = false;
        if ("".equals(this.detailIds)) {
            this.setDetails(another.getDetails());
            return true;
        }

        Map<Integer, PersonDetail> olds = new HashMap<Integer, PersonDetail>();
        Map<Integer, PersonDetail> news = new HashMap<Integer, PersonDetail>();
        for (PersonDetail cr : this.details) {
            olds.put(cr.getId(), cr);
        }
        for (PersonDetail p : another.details) {
            int propid = p.getId();
            if (propid <= 0) {
                continue;
            }
            news.put(propid, p);
            if (olds.containsKey(propid)) {
                // update
                olds.get(propid).update(p);
                isChange = true;
            } else {
                // new
                this.details.add(p);

                isChange = true;
            }
        }
        for (PersonDetail p : this.details) {
            if (!news.containsKey(p.getId())) {
                // del
                p.setStatus(Status.DEL);
                isChange = true;
            }
        }
        return isChange;
    }

    protected void syncBasicAndDetail(boolean forward) {
        Map<PersonDetailType, PersonDetail> type2Details = new HashMap<PersonDetailType, PersonDetail>();
        for (PersonDetail pd : this.details) {
            type2Details.put(pd.getType(), pd);
        }

        if (forward) {
            // basic => detail
            PersonDetailType type = PersonDetailType.D_NAME;
            if (!"".equals(this.name)) {
                if (type2Details.containsKey(type)) {
                    type2Details.get(type).setValue(this.name);
                } else {
                    PersonDetail pd = new PersonDetail(Integer.MAX_VALUE - 1,
                            cid);
                    pd.setType(type);
                    pd.setValue(this.name);
                    this.details.add(pd);
                }
            }
            type = PersonDetailType.D_CELLPHONE;
            if (!"".equals(this.cellphone)) {
                if (type2Details.containsKey(type)) {
                    type2Details.get(type).setValue(this.cellphone);

                } else {
                    PersonDetail pd = new PersonDetail(Integer.MAX_VALUE - 2,
                            cid);
                    pd.setType(type);
                    pd.setValue(this.cellphone);
                    this.details.add(pd);
                }
            }
            type = PersonDetailType.D_GENDAR;
            if (Gendar.UNKNOWN != this.gendar) {
                if (type2Details.containsKey(type)) {
                    type2Details.get(type).setValue(
                            Gendar.parseGendar2String(this.gendar));

                } else {
                    PersonDetail pd = new PersonDetail(Integer.MAX_VALUE - 3,
                            cid);
                    pd.setType(type);
                    pd.setValue(Gendar.parseGendar2String(this.gendar));
                    this.details.add(pd);
                }
            }
            type = PersonDetailType.D_AVATAR;
            if (!"".equals(this.avatar)) {
                if (type2Details.containsKey(type)) {
                    type2Details.get(type).setValue(this.avatar);
                } else {
                    PersonDetail pd = new PersonDetail(Integer.MAX_VALUE - 4,
                            cid);
                    pd.setType(type);
                    pd.setValue(this.avatar);
                    this.details.add(pd);
                }
            }
            type = PersonDetailType.D_BIRTHDAY;
            if (!"".equals(this.birthday)) {
                if (type2Details.containsKey(type)) {
                    type2Details.get(type).setValue(this.birthday);
                } else {
                    PersonDetail pd = new PersonDetail(Integer.MAX_VALUE - 5,
                            cid);
                    pd.setType(type);
                    pd.setValue(this.birthday);
                    this.details.add(pd);
                }
            }
            type = PersonDetailType.D_EMPLOYER;
            if (!"".equals(this.employer)) {
                if (type2Details.containsKey(type)) {
                    type2Details.get(type).setValue(this.employer);
                } else {
                    PersonDetail pd = new PersonDetail(Integer.MAX_VALUE - 6,
                            cid);
                    pd.setType(type);
                    pd.setValue(this.employer);
                    this.details.add(pd);
                }
            }
            type = PersonDetailType.D_JOBTITLE;
            if (!"".equals(this.jobtitle)) {
                if (type2Details.containsKey(type)) {
                    type2Details.get(type).setValue(this.jobtitle);
                } else {
                    PersonDetail pd = new PersonDetail(Integer.MAX_VALUE - 7,
                            cid);
                    pd.setType(type);
                    pd.setValue(this.jobtitle);
                    this.details.add(pd);
                }
            }
        } else {
            // detail => basic
            PersonDetailType type = PersonDetailType.D_NAME;
            if (type2Details.containsKey(type)) {
                if (type2Details.get(type).getStatus() == Status.DEL) {
                    this.name = "";
                } else {
                    this.name = type2Details.get(type).getValue();
                }
            }
            type = PersonDetailType.D_CELLPHONE;
            if (type2Details.containsKey(type)) {
                if (type2Details.get(type).getStatus() == Status.DEL) {
                    this.cellphone = "";
                } else {
                    this.cellphone = type2Details.get(type).getValue();
                }
            }
            type = PersonDetailType.D_GENDAR;
            if (type2Details.containsKey(type)) {
                if (type2Details.get(type).getStatus() == Status.DEL) {
                    this.gendar = Gendar.UNKNOWN;
                } else {
                    this.gendar = Gendar.parseString2Gendar(type2Details.get(
                            type).getValue());
                }

            }
            type = PersonDetailType.D_AVATAR;
            if (type2Details.containsKey(type)) {
                if (type2Details.get(type).getStatus() == Status.DEL) {
                    this.avatar = "";
                } else {
                    this.avatar = type2Details.get(type).getValue();
                }
            }
            type = PersonDetailType.D_BIRTHDAY;
            if (type2Details.containsKey(type)) {
                if (type2Details.get(type).getStatus() == Status.DEL) {
                    this.birthday = "";
                } else {
                    this.birthday = type2Details.get(type).getValue();
                }
            }
            type = PersonDetailType.D_EMPLOYER;
            if (type2Details.containsKey(type)) {
                if (type2Details.get(type).getStatus() == Status.DEL) {
                    this.employer = "";
                } else {
                    this.employer = type2Details.get(type).getValue();
                }
            }
            type = PersonDetailType.D_JOBTITLE;
            if (type2Details.containsKey(type)) {
                if (type2Details.get(type).getStatus() == Status.DEL) {
                    this.jobtitle = "";
                } else {
                    this.jobtitle = type2Details.get(type).getValue();
                }
            }
        }
    }

    /**
     * refresh circle member's detail info
     * 
     * @return
     */
    public RetError refresh() {
        return refresh(0L);
    }

    /**
     * refresh circle member's detail info from start time
     * 
     * @param start
     * @return
     */
    public RetError refresh(long start) {
        IParser parser = new CircleMemberDetailParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("pid", pid);
        params.put("start", start);
        Result ret = ApiRequest.requestWithToken(CircleMember.DETAIL_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {

            if (this.updateDetails((CircleMember) ret.getData())) {
                this.syncBasicAndDetail(false);
                this.status = Status.UPDATE;
            }
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * get and update basic info of a member
     * 
     * @return
     */
    public RetError refreshBasic() {
        IParser parser = new CircleMemberBasicParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("pid2", pid);
        if (uid != 0) {
            params.put("uid2", uid);
        }
        Result ret = ApiRequest.requestWithToken(CircleMember.BASIC_API,
                params, parser);

        if (ret.getStatus() == RetStatus.SUCC) {
            this.update(ret.getData());
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    @SuppressLint("UseSparseArrays")
    public JSONArray getChangedDetails(CircleMember another) {
        Map<Integer, PersonDetail> olds = new HashMap<Integer, PersonDetail>();
        for (PersonDetail pp : this.details) {
            olds.put(pp.getId(), pp);
        }

        JSONArray jsonArr = new JSONArray();
        Map<Integer, PersonDetail> news = new HashMap<Integer, PersonDetail>();
        for (PersonDetail pd : another.details) {
            int propid = pd.getId();
            news.put(propid, pd);

            if (olds.containsKey(propid)) {
                if (!pd.equals(olds.get(propid))) {
                    // edit
                    try {
                        JSONObject jsonObj = pd.toJson();
                        jsonObj.put("op", "edit");
                        jsonArr.put(jsonObj);
                    } catch (Exception e) {
                    }
                }
            } else {
                // new
                try {
                    JSONObject jsonObj = pd.toJson();
                    jsonObj.put("op", "new");
                    jsonArr.put(jsonObj);
                } catch (Exception e) {
                }
            }
        }

        for (PersonDetail pd : this.details) {
            if (!news.containsKey(pd.getId())) {
                // del
                try {
                    JSONObject jsonObj = pd.toJson();
                    jsonObj.put("op", "del");
                    jsonArr.put(jsonObj);
                } catch (Exception e) {
                }
            }
        }

        return jsonArr;
    }

    protected void updateForEditInfo(CircleMember another,
            JSONArray changedDetails, List<Object> ret) {
        if (changedDetails.length() > 0
                && changedDetails.length() == ret.size()) {

            List<PersonDetail> targetPds = new ArrayList<PersonDetail>();
            for (int i = 0; i < changedDetails.length(); i++) {
                Object o = ret.get(i);
                int retPropid;
                if (o instanceof Integer) {
                    retPropid = (Integer) ret.get(i);
                } else {
                    retPropid = Integer.parseInt((String) ret.get(i));
                }
                if (retPropid > 0) {
                    try {
                        JSONObject jobj = (JSONObject) changedDetails.opt(i);
                        int id = jobj.getInt("id");
                        PersonDetail pd = new PersonDetail(id, cid);
                        pd.setType(PersonDetailType.convertToType(jobj
                                .getString("t")));
                        pd.setPid(pid);
                        pd.setUid(uid);
                        pd.setValue(jobj.getString("v"));
                        if (jobj.has("start")) {
                            pd.setStart(jobj.getString("start"));
                        }
                        if (jobj.has("end")) {
                            pd.setEnd(jobj.getString("end"));
                        }
                        if (jobj.has("remark")) {
                            pd.setRemark(jobj.getString("remark"));
                        }
                        String op = jobj.getString("op");
                        if ("new".equals(op)) {
                            pd.setStatus(Status.NEW);
                            pd.setId(retPropid);
                        } else if ("edit".equals(op)) {
                            pd.setStatus(Status.UPDATE);
                        } else if ("del".equals(op)) {
                            pd.setStatus(Status.DEL);
                        }
                        targetPds.add(pd);
                    } catch (JSONException e) {
                    }
                }
            }
            for (int i = 0; i < targetPds.size(); i++) {
                PersonDetail pd = targetPds.get(i);
                pd.write(DBUtils.getDBsa(2));
            }
        }
    }

    // protected void updateForEditInfo(CircleMember another,
    // JSONArray changedDetails, List<Object> ret) {
    // if (changedDetails.length() > 0
    // && changedDetails.length() == ret.size()) {
    //
    // List<PersonDetail> targetPds = new ArrayList<PersonDetail>();
    // for (int i = 0; i < changedDetails.length(); i++) {
    // Object o = ret.get(i);
    // int retPropid;
    // if (o instanceof Integer) {
    // retPropid = (Integer) ret.get(i);
    // } else {
    // retPropid = Integer.parseInt((String) ret.get(i));
    // }
    // if (retPropid > 0) {
    // try {
    // JSONObject jobj = (JSONObject) changedDetails.opt(i);
    // int oldPropId = jobj.getInt("id");
    // PersonDetail pd = new PersonDetail(retPropid, cid);
    //
    // pd.setType(PersonDetailType.convertToType(jobj
    // .getString("t")));
    // pd.setValue(jobj.getString("v"));
    // if (jobj.has("start")) {
    // pd.setStart(jobj.getString("start"));
    // }
    // if (jobj.has("end")) {
    // pd.setEnd(jobj.getString("end"));
    // }
    // if (jobj.has("remark")) {
    // pd.setRemark(jobj.getString("remark"));
    // }
    //
    // String op = jobj.getString("op");
    // if ("new".equals(op)) {
    // pd.setStatus(Status.NEW);
    // } else if ("edit".equals(op)) {
    // pd.setStatus(Status.NEW);
    // if (oldPropId > 0) {
    // PersonDetail oldPd = new PersonDetail(
    // oldPropId, cid);
    // oldPd.setStatus(Status.DEL);
    // targetPds.add(oldPd);
    // }
    // } else if ("del".equals(op)) {
    // pd.setStatus(Status.DEL);
    // }
    // targetPds.add(pd);
    // } catch (JSONException e) {
    // }
    // }
    // }
    // System.out.println("targetPdstargetPds::" + changedDetails);
    // List<PersonDetail> oldDetails = another.getDetails();
    // for (int i = 0, len = targetPds.size(); i < len; i++) {
    // PersonDetail detail = targetPds.get(i);
    // int id1 = detail.getId();
    // boolean oldTag = false;
    // for (int j = 0; j < oldDetails.size(); j++) {
    // int id2 = oldDetails.get(j).getId();
    // if (id1 == id2) {
    // if (detail.getStatus() == Status.DEL) {
    // oldDetails.remove(j);
    // } else {
    // oldDetails.set(j, detail);
    // }
    // oldTag = true;
    // break;
    // }
    // }
    // if (!oldTag && detail.getStatus() == Status.NEW) {
    // oldDetails.add(detail);
    // }
    // }
    //
    // if (this.updateDetails(another)) {
    // // this.syncBasicAndDetail(false);
    // this.status = Status.UPDATE;
    // }
    // } else {
    // // size not equal???
    // }
    // }

    /**
     * upload edit info to server, and update local data while upload success
     * 
     * @param another
     * @return
     */
    // public RetError uploadAfterEdit(CircleMember another) {
    // JSONArray changedDetails = getChangedDetails(another);
    // if (changedDetails.length() == 0) {
    // return RetError.NONE;
    // }
    //
    // IParser parser = new ArrayParser("details");
    // Map<String, Object> params = new HashMap<String, Object>();
    // params.put("cid", another.cid);
    // params.put("pid", another.pid);
    // params.put("person", changedDetails.toString());
    // Result ret = ApiRequest.requestWithToken(CircleMember.EDIT_API, params,
    // parser);
    // if (ret.getStatus() == RetStatus.SUCC) {
    // ArrayResult aret = (ArrayResult) ret;
    // updateForEditInfo(another, changedDetails, aret.getArrs());
    // return RetError.NONE;
    // } else {
    // return ret.getErr();
    // }
    // }
    public RetError uploadAfterEdit(CircleMember another, String avatarUrl) {
        JSONArray changedDetails = getChangedDetails(another);

        if (changedDetails.length() == 0 && "".equals(avatarUrl)) {
            return RetError.NONE;
        }
        File file = null;
        file = new File(avatarUrl);
        String[] keys = { "details", "amendments" };
        IParser parser = new MoreArrayParser(keys);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", another.cid);
        params.put("pid", another.pid);
        params.put("person", changedDetails.toString());

        Result ret = ApiRequest.uploadFileWithToken(CircleMember.EDIT_API,
                params, file, "avatar", parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            MoreArrayResult aret = (MoreArrayResult) ret;
            updateForEditInfo(another, changedDetails, aret.getArrs().get(0));
            upDateEditData(aret.getArrs().get(1));
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    protected boolean isAvatarChanged(String avatar) {
        return avatar != null && avatar.length() > 0
                && !this.avatar.equals(avatar);
    }

    /**
     * 获取修改以后的数据
     */
    private void upDateEditData(List<Object> arrs) {
        editData.clear();
        for (Object str : arrs) {
            try {
                JSONObject json = (JSONObject) str;
                int id = json.getInt("id");
                String operation = json.getString("operation");
                String detail = json.getString("detail");
                String type = json.getString("type");
                PersonDetailType pType = PersonDetailType.convertToType(type);
                EditData edit = new EditData(id, pType, operation, detail);
                editData.add(edit);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * upload new avatar to server, and update local avatar while upload success
     * 
     * @param avatar
     * @return
     */
    public RetError uploadAvatar(String avatar) {
        if (!isAvatarChanged(avatar)) {
            return RetError.NONE;
        }
        File file = new File(avatar);
        if (file == null || !file.exists()) {
            return RetError.INVALID;
        }

        IParser parser = new StringParser("avatar");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("pid", pid);
        params.put("avatar", avatar);

        Result ret = ApiRequest.uploadFileWithToken(
                CircleMember.UPLOAD_AVATAR_API, params, file, "avatar", parser);
        file.delete();
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            this.avatar = sret.getStr();
            this.status = Status.UPDATE;
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * quit from the circle
     * 
     * @return
     */
    public RetError quit() {
        int uid = Integer.parseInt(Global.getUid());
        if (uid != this.uid || !CircleMemberState.isInCircle(this.state)) {
            return RetError.INVALID;
        }

        IParser parser = new SimpleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);

        Result ret = ApiRequest.requestWithToken(CircleMember.QUIT_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            this.state = CircleMemberState.STATUS_QUIT;
            this.status = Status.UPDATE;

            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * accept the invitation for the circle
     * 
     * @return
     */
    public RetError acceptInvitation() {
        int uid = Global.getIntUid();
        if (uid != this.uid
                || (CircleMemberState.STATUS_INVITING != this.state)) {
            return RetError.INVALID;
        }

        IParser parser = new StringParser("auth");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);

        Result ret = ApiRequest.requestWithToken(
                CircleMember.ACCETP_INVITATION_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            int auth = Integer.parseInt(sret.getStr());
            this.state = auth > 0 ? CircleMemberState.STATUS_VERIFIED
                    : CircleMemberState.STATUS_ENTER_AND_VERIFYING;
            this.status = Status.UPDATE;

            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * refuse the invitation for the circle
     * 
     * @return
     */
    public RetError refuseInvitation() {
        int uid = Integer.parseInt(Global.getUid());
        if (uid != this.uid
                || (CircleMemberState.STATUS_INVITING != this.state)) {
            return RetError.INVALID;
        }

        IParser parser = new SimpleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);

        Result ret = ApiRequest.requestWithToken(
                CircleMember.REFUSE_INVITATION_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            this.state = CircleMemberState.STATUS_REFUSED;
            this.status = Status.UPDATE;

            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * kickout a circle member
     * 
     * @return
     */
    public RetError kickout() {
        if (!CircleMemberState.isInCircle(this.state)) {
            return RetError.INVALID;
        }

        IParser parser = new StringParser("auth");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("pid", pid);

        Result ret = ApiRequest.requestWithToken(CircleMember.KICKOUT_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            int auth = Integer.parseInt(sret.getStr());
            this.state = auth > 0 ? CircleMemberState.STATUS_KICKOUT
                    : CircleMemberState.STATUS_KICKOFFING;
            this.status = Status.UPDATE;

            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * kickout a circle member
     * 
     * @return
     */
    public int kickout2() {
        if (!CircleMemberState.isInCircle(this.state)) {
            return -1;
        }

        String[] keys = { "auth", "need" };
        IParser parser = new MapParser(keys);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("pid", pid);

        Result ret = ApiRequest.requestWithToken(CircleMember.KICKOUT_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            MapResult mret = (MapResult) ret;
            int auth = (Integer) (mret.getMaps().get("auth"));
            int need = (Integer) (mret.getMaps().get("need"));
            this.state = auth > 0 ? CircleMemberState.STATUS_KICKOUT
                    : CircleMemberState.STATUS_KICKOFFING;
            this.status = Status.UPDATE;

            return need;
        } else {
            if (ret.getErr() == RetError.REPEAT_OPERATION) {
                return -3;

            }
            return -2;
        }
    }

    /**
     * invite another new member to join the same circle
     * 
     * @param another
     * @return
     */
    public RetError invite(CircleMember another) {
        int uid = Integer.parseInt(Global.getUid());
        if (uid != this.uid) {
            return RetError.INVALID;
        }
        if ("".equals(another.name) || "".equals(another.cellphone)) {
            return RetError.INVALID;
        }

        String[] keys = { "pid", "rep", "code", "cmid" };
        IParser parser = new MapParser(keys);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", another.cid);
        params.put("name", another.name);
        params.put("cellphone", another.cellphone);
        if (!"".equals(another.birthday)) {
            params.put("birthday", another.birthday);
        }
        if (!"".equals(another.employer)) {
            params.put("employer", another.employer);
        }
        if (!"".equals(another.jobtitle)) {
            params.put("jobtitle", another.jobtitle);
        }

        Result ret = ApiRequest.requestWithToken(CircleMember.INVITE_ONE_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            MapResult mret = (MapResult) ret;
            int pid = Integer.parseInt((String) mret.getMaps().get("pid"));
            int cmid = Integer.parseInt((String) mret.getMaps().get("cmid"));
            int isRepeat = (Integer) (mret.getMaps().get("rep"));
            String code = (String) (mret.getMaps().get("code"));

            another.pid = pid;
            another.cmid = cmid;
            another.inviteCode = code;
            if (isRepeat > 0) {
                // already exist
                another.status = Status.OLD;
            } else {
                // new member
                another.status = Status.NEW;
            }

            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * invite more members to join the same circle
     * 
     * @param members
     * @return
     */
    public RetError inviteMore(List<CircleMember> members) {
        int uid = Integer.parseInt(Global.getUid());
        if (uid != this.uid) {
            return RetError.INVALID;
        }
        if (members.size() <= 0) {
            return RetError.INVALID;
        }
        if (members.size() == 1) {
            return invite(members.get(0));
        }

        // create request parameter: persons
        JSONArray jsonArr = new JSONArray();
        for (CircleMember member : members) {
            try {
                JSONObject json = new JSONObject();
                json.put("name", member.getName());
                json.put("cellphone", member.getCellphone());
                jsonArr.put(json);
            } catch (Exception e) {
            }
        }
        String persons = jsonArr.toString();

        IParser parser = new StringParser("details");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("persons", persons);

        Result ret = ApiRequest.requestWithToken(CircleMember.INVITE_MORE_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            String details = sret.getStr();
            // parse details
            String[] splits = details.split(";");
            int cnt = Math.min(members.size(), splits.length);
            for (int i = 0; i < cnt; i++) {
                String[] attrs = splits[i].split(",");
                CircleMember member = members.get(i);
                if (attrs.length != 4) {
                    member.inviteRt = "-1";
                    continue;
                }
                if (!"1".equals(attrs[0])) {
                    member.inviteRt = "-1";
                    continue;
                }
                int pid = Integer.parseInt(attrs[1]);
                String code = attrs[2];
                int cmid = Integer.parseInt(attrs[3]);
                member.pid = pid;
                member.cmid = cmid;
                if ("".equals(code)) {
                    // already exist
                    member.status = Status.OLD;
                } else {
                    // new member
                    member.inviteCode = code;
                    member.status = Status.NEW;
                }
            }
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * set privacy settings of a circle member
     * 
     * @param typeList
     * 
     * @return
     */
    public RetError setPrivacy(List<PersonDetailType> typeList) {
        StringBuffer privacySettings = new StringBuffer();
        for (PersonDetailType type : typeList) {
            int id = PersonDetailType.getID(type);
            if (id > 0) {
                if (privacySettings.length() > 0) {
                    privacySettings.append(',');
                }
                privacySettings.append(id);
            }
        }
        IParser parser = new SimpleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("privacy", privacySettings.toString());

        Result ret = ApiRequest.requestWithToken(
                CircleMember.PRIVACY_SETTINGS_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            this.setPrivacySettingsByTypes(typeList);
            this.status = Status.UPDATE;

            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * 获取操作权限
     * @param cid
     * @param uid
     */
    public boolean isAuth(SQLiteDatabase db) {
        getMemberState(db);
        return CircleMemberState.STATUS_VERIFIED.equals(this.state)
                || CircleMemberState.STATUS_KICKOFFING.equals(this.state) ? true
                : false;

    }

    public void getMemberState(SQLiteDatabase db) {
        if (!db.isOpen()) {
            db = DBUtils.dbase.getReadableDatabase();
        }
        String conditionsKey = "cid=? and pid=?";
        String[] conditionsValue = { this.cid + "", this.pid + "" };
        if (pid == 0) {
            conditionsKey = "cid=? and uid=?";
            conditionsValue = new String[] { this.cid + "", this.uid + "" };
        }
        Cursor cursor = db.query(Const.CIRCLE_MEMBER_TABLE_NAME,
                new String[] { "state" }, conditionsKey, conditionsValue, null,
                null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String state = cursor.getString(cursor.getColumnIndex("state"));
            this.state = CircleMemberState.convert(state);

        }
        cursor.close();
    }

    // 成员首次加入圈子时，用来初始化个人资料的接口。
    public RetError initMydetails(String circleIds, String personalIds) {
        IParser parser = new SimpleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("circleIds", circleIds);
        params.put("personalIds", personalIds);
        Result ret = ApiRequest.requestWithToken(CircleMember.INITMYDETAIL_API,
                params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

}
