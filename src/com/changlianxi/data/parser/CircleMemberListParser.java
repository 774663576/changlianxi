package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.CircleMemberList;
import com.changlianxi.data.PersonDetail;
import com.changlianxi.data.enums.CircleMemberState;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;
import com.changlianxi.util.PinYinUtils;
import com.changlianxi.util.StringUtils;

public class CircleMemberListParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        int cid = (Integer) params.get("cid");
        int total = jsonObj.getInt("total");
        int requestTime = jsonObj.getInt("current");
        JSONArray jsonArr = jsonObj.getJSONArray("members");
        if (jsonArr == null) {
            return Result.defContentErrorResult();
        }

        List<CircleMember> members = new ArrayList<CircleMember>();
        long start = 0L, end = 0L;
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject obj = (JSONObject) jsonArr.opt(i);
            int pid = obj.getInt("id");
            int uid = obj.getInt("uid");
            String name = obj.getString("name");
            String cellphone = obj.getString("cellphone");
            String avatar = obj.getString("avatar");
            String employer = obj.getString("employer");
            String jobtitle = obj.getString("jobtitle");
            String time = obj.getString("time");
            String location = obj.getString("location");
            // String role = obj.getString("role_id");
            // int roleId = "".equals(role) ? 0 : Integer.valueOf(role);
            String roleId = obj.getString("role_id");
            String state = obj.getString("state");
            String auth = obj.getString("auth");
            String privacy = obj.getString("privacy");
            String ic = obj.getString("ic");
            String sortkey = PinYinUtils.getPinYin(name).toUpperCase();
            String pinyinFir = PinYinUtils.getFirstPinYin(name).toLowerCase();
            CircleMember m = new CircleMember(cid, pid, uid);
            m.setName(name);
            m.setCellphone(cellphone);
            m.setAvatar(StringUtils.JoinString(avatar, "_160x160"));
            m.setEmployer(employer);
            m.setJobtitle(jobtitle);
            m.setLocation(location);
            m.setRoleId(roleId);
            m.setState(CircleMemberState.convert(state));
            m.setPrivacySettings(privacy);
            m.setSortkey(sortkey);
            m.setPinyinFir(pinyinFir);
            m.setAuth(auth);
            m.setLastModTime(time);
            m.setInviteCode(ic);

            long tmp = DateUtils.convertToDate(time);
            if (end == 0 || tmp > end) {
                end = tmp;
            }
            if (start == 0 || tmp < start) {
                start = tmp;
            }
            JSONArray jsonDetails = obj.getJSONArray("details");
            // member properties
            List<PersonDetail> properties = new ArrayList<PersonDetail>();
            for (int j = 0; j < jsonDetails.length(); j++) {
                JSONObject objDetail = (JSONObject) jsonDetails.opt(j);
                int id = objDetail.getInt("id");
                String type = objDetail.getString("type");
                String value = objDetail.getString("value");
                PersonDetailType pType = PersonDetailType.convertToType(type);
                if (pType == PersonDetailType.UNKNOWN) {
                    continue;
                }
                PersonDetail p = new PersonDetail(id, cid, pid, uid, pType,
                        value);
                if (objDetail.has("start")) {
                    p.setStart(objDetail.getString("start"));
                }
                if (objDetail.has("end")) {
                    p.setEnd(objDetail.getString("end"));
                }
                if (objDetail.has("remark")) {
                    p.setRemark(objDetail.getString("remark"));
                }

                properties.add(p);
            }
            m.setDetails(properties);
            members.add(m);
        }

        CircleMemberList cml = new CircleMemberList(cid);
        cml.setMembers(members);
        cml.setTotal(total);
        cml.setStartTime(start);
        cml.setEndTime(end);
        cml.setLastReqTime(end);
//        if (total > members.size()) {
//            cml.setLastReqTime(end);
//        } else {
//            cml.setLastReqTime(requestTime);
//        }
        Result ret = new Result();
        ret.setData(cml);

        return ret;
    }
}
