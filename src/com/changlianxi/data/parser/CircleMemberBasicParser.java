package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.CircleMember;
import com.changlianxi.data.request.Result;

public class CircleMemberBasicParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }
        if (!jsonObj.has("person") || !jsonObj.has("cid")) {
            return Result.defContentErrorResult();
        }
        JSONObject jsonPerson = jsonObj.getJSONObject("person");
        int cid = jsonObj.getInt("cid");
        if (jsonPerson == null || cid == 0) {
            return Result.defContentErrorResult();
        }

        // member basic info
        int pid = jsonPerson.getInt("id");
        int uid = jsonPerson.getInt("user_id");
        String name = jsonPerson.getString("name");
        String cellphone = jsonPerson.getString("cellphone");
        String location = jsonPerson.getString("location");
        String avatar = jsonPerson.getString("avatar");
        String employer = jsonPerson.getString("employer");
        CircleMember member = new CircleMember(cid, pid, uid);
        member.setName(name);
        member.setCellphone(cellphone);
        member.setLocation(location);
        member.setAvatar(avatar);
        member.setEmployer(employer);

        Result ret = new Result();
        ret.setData(member);
        return ret;
    }

}
