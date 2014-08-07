package com.changlianxi.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.db.Const;
import com.changlianxi.db.DBUtils;

/**
 * 圈子分组
 * @author teeker_bin
 *
 */
public class CircleGroup extends AbstractData {
    private static final String SET_GROUP_MEMBERS_API = "/circles/isetGroupMembers";
    private int groupsId = 0;
    private String groupsName = "";
    private int cid = 0;

    public CircleGroup(int cid, int groupId, String groupName) {
        this.cid = cid;
        this.groupsId = groupId;
        this.groupsName = groupName;
    }

    public int getGroupsId() {
        return groupsId;
    }

    public void setGroupsId(int groupsId) {
        this.groupsId = groupsId;
    }

    public String getGroupsName() {
        return groupsName;
    }

    public void setGroupsName(String groupsName) {
        this.groupsName = groupsName;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "id:" + groupsId + "   name:" + groupsName;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.CIRCLE_GROUP_TABLE_NAME;
        if (this.status == Status.OLD) {
            return;
        }
        if (this.status == Status.DEL) {
            db.delete(dbName, "cid=? and group_id=?", new String[] { cid + "",
                    groupsId + "" });
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put("cid", cid);
        cv.put("group_id", groupsId);
        cv.put("group_name", groupsName);
        if (this.status == Status.NEW) {
            db.insert(dbName, null, cv);
        }
        this.status = Status.OLD;

    }

    @Override
    public void read(SQLiteDatabase db) {

    }

    /**
     * 圈子成员，以逗号分割的圈子成员的PID列表
     * @param members
     * @return
     */
    public RetError setGroupMembers(List<CircleMember> newListMembers,
            List<CircleMember> oldListMembers) {
        StringBuilder sb = new StringBuilder();
        for (CircleMember m : newListMembers) {
            sb.append(m.getPid() + ",");
        }
        IParser parser = new SimpleParser();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("gid", groupsId);
        params.put("members", sb.toString());
        Result ret = ApiRequest.requestWithToken(SET_GROUP_MEMBERS_API, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            Set<CircleMember> result = new HashSet<CircleMember>();
            // 获取删除的成员
            result.clear();
            result.addAll(oldListMembers);
            result.removeAll(newListMembers);
            for (CircleMember m : result) {
                CircleMemberGroups mGroups = new CircleMemberGroups(cid,
                        groupsId, m.getPid());
                mGroups.setStatus(Status.DEL);
                mGroups.write(DBUtils.getDBsa(2));
            }
            // 获取新增加成员
            result.clear();
            result.addAll(newListMembers);
            result.removeAll(oldListMembers);
            for (CircleMember m : result) {
                CircleMemberGroups mGroups = new CircleMemberGroups(cid,
                        groupsId, m.getPid());
                mGroups.setStatus(Status.NEW);
                mGroups.write(DBUtils.getDBsa(2));
            }
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

}
