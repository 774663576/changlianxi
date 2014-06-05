package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.PersonDetail;
import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.data.request.Result;

public class CircleMemberDetailParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        if (!jsonObj.has("person") || !jsonObj.has("cid")
                || !jsonObj.has("puid") || !jsonObj.has("pid")) {
            return Result.defContentErrorResult();
        }
        JSONArray jsonPersons = jsonObj.getJSONArray("person");
        int cid = jsonObj.getInt("cid");
        int pid = jsonObj.getInt("pid");
        int uid = jsonObj.getInt("puid");
        if (jsonPersons == null || cid == 0 || pid == 0) {
            return Result.defContentErrorResult();
        }

        // member properties
        List<PersonDetail> properties = new ArrayList<PersonDetail>();
        for (int i = 0; i < jsonPersons.length(); i++) {
            JSONObject obj = (JSONObject) jsonPersons.opt(i);
            int id = obj.getInt("id");
            String type = obj.getString("type");
            String value = obj.getString("value");
            PersonDetailType pType = PersonDetailType.convertToType(type);
            if (pType == PersonDetailType.UNKNOWN) {
                continue;
            }
            PersonDetail p = new PersonDetail(id, cid, pid, uid, pType, value);
            if (obj.has("start")) {
                p.setStart(obj.getString("start"));
            }
            if (obj.has("end")) {
                p.setEnd(obj.getString("end"));
            }
            if (obj.has("remark")) {
                p.setRemark(obj.getString("remark"));
            }
            properties.add(p);
        }

        CircleMember member = new CircleMember(cid, pid, uid);
        member.setDetails(properties);
        Result ret = new Result();
        ret.setData(member);
        return ret;
    }
}
