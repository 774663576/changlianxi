package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleRole;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.StringUtils;

public class CircleParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        JSONObject jsonCircle = jsonObj.getJSONObject("circle");
        JSONArray jsonRoles = jsonObj.getJSONArray("roles");
        JSONObject jsonMembersCount = jsonObj.getJSONObject("membersCount");
        int cid = jsonObj.getInt("cid");
        if (jsonCircle == null || jsonRoles == null || jsonMembersCount == null
                || cid == 0) {
            return Result.defContentErrorResult();
        }

        // circle basic info
        String name = jsonCircle.getString("name");
        String logo = jsonCircle.getString("logo");
        String description = jsonCircle.getString("description");
        int creator = jsonCircle.getInt("creator");
        String created = jsonCircle.getString("created");
        Circle c = new Circle(cid, name, description, logo);
        c.setCreator(creator);
        c.setCreated(created);

        // circle roles
        List<CircleRole> roles = new ArrayList<CircleRole>();
        for (int i = 0; i < jsonRoles.length(); i++) {
            JSONObject obj = (JSONObject) jsonRoles.opt(i);
            int roleId = obj.getInt("id");
            String roleName = obj.getString("name");
            int roleCount = obj.getInt("count");
            CircleRole role = new CircleRole(cid, roleId, roleName, roleCount);
            roles.add(role);
        }
        c.setRoles(roles);

        // circle member counts
        c.setTotalCnt(jsonMembersCount.getInt("total"));
        c.setInvitingCnt(jsonMembersCount.getInt("inviting"));
        c.setVerifiedCnt(jsonMembersCount.getInt("verified"));
        c.setUnverifiedCnt(jsonMembersCount.getInt("unverified"));

        Result ret = new Result();
        ret.setData(c);
        return ret;
    }

}
