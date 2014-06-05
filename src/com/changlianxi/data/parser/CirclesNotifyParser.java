package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleList;
import com.changlianxi.data.request.Result;

public class CirclesNotifyParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }
        JSONArray jsonArr = jsonObj.getJSONArray("result");
        if (jsonArr == null) {
            return Result.defContentErrorResult();
        }

        List<Circle> circles = new ArrayList<Circle>();
        for (int i = 0; i < jsonArr.length(); i++) {
            String str = jsonArr.getString(i);
            String countArray[] = str.split(",");
            int newGrowthCount;// 新成长数、
            int newDynamicCount;// 新动态数、
            int newCommentCount;// 新评论数。
            int newMemberCount;// 新成员数
            int newMyDetailEditCount;// 新我的资料修改次数
            newMyDetailEditCount = Integer.valueOf(countArray[0]);
            newMemberCount = Integer.valueOf(countArray[1]);
            newGrowthCount = Integer.valueOf(countArray[2]);
            newCommentCount = Integer.valueOf(countArray[3]);
            newDynamicCount = Integer.valueOf(countArray[4]);
            Circle c = new Circle(0);
            c.setNewGrowthCnt(newGrowthCount);
            c.setNewDynamicCnt(newDynamicCount);
            c.setNewGrowthCommentCnt(newCommentCount);
            c.setNewMemberCnt(newMemberCount);
            c.setNewMyDetailEditCnt(newMyDetailEditCount);
            circles.add(c);
        }

        Result ret = new Result();
        ret.setData(new CircleList(circles));
        return ret;
    }

}
