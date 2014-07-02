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

public class CircleMemberListParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        int cid = (Integer) params.get("cid");
        int total = jsonObj.getInt("total");
        String urlBase = jsonObj.getString("urlbase");
        // int requestTime = jsonObj.getInt("current");
        JSONArray jsonArr = jsonObj.getJSONArray("members");
        if (jsonArr == null) {
            return Result.defContentErrorResult();
        }

        List<CircleMember> members = new ArrayList<CircleMember>();
        long start = 0L, end = 0L;
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject obj = (JSONObject) jsonArr.opt(i);
            int pid = obj.getInt("pid");
            int uid = obj.getInt("uid");
            String name = obj.getString("name");
            String pinyin = obj.getString("py");
            String jianpin = obj.getString("jp");
            String state = obj.getString("state");
            String ic = obj.getString("ic");
            String time = obj.getString("time");
            CircleMember m = new CircleMember(cid, pid, uid);
            m.setName(name);
            m.setSortkey(pinyin.toLowerCase());
            m.setPinyinFir(jianpin.toLowerCase());
            m.setState(CircleMemberState.convert(state));
            m.setLastModTime(time);
            m.setInviteCode(ic);
            if (obj.has("account_email")) {
                String account_email = obj.getString("account_email");
                m.setAccount_email(account_email);
            }
            if (obj.has("cellphone")) {
                String cellphone = obj.getString("cellphone");
                m.setCellphone(cellphone);
            }
            if (obj.has("avatar")) {
                String avatar = obj.getString("avatar").trim();
                if (!avatar.isEmpty()) {
                    m.setAvatar(urlBase + avatar);
                }
            }
            if (obj.has("employer")) {
                String employer = obj.getString("employer");
                m.setEmployer(employer);
            }
            if (obj.has("location")) {
                String location = obj.getString("location");
                m.setLocation(location);
            }

            long tmp = DateUtils.convertToDate(time);
            if (end == 0 || tmp > end) {
                end = tmp;
            }
            if (start == 0 || tmp < start) {
                start = tmp;
            }

            // detail properties
            JSONArray jsonDetails = obj.getJSONArray("details");
            List<PersonDetail> properties = new ArrayList<PersonDetail>();
            for (int j = 0; j < jsonDetails.length(); j++) {
                JSONObject objDetail = (JSONObject) jsonDetails.opt(j);
                int id = objDetail.getInt("id");
                String type = objDetail.getString("t");
                String value = objDetail.getString("v");
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
        cml.setLastReqTime(end); // TODO

        Result ret = new Result();
        ret.setData(cml);

        return ret;
    }
}
