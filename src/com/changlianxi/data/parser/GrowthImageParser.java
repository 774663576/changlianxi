package com.changlianxi.data.parser;

import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.GrowthImage;
import com.changlianxi.data.request.Result;

public class GrowthImageParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        if (!jsonObj.has("img_id") || !jsonObj.has("image")) {
            return Result.defContentErrorResult();
        }

        int imgId = jsonObj.getInt("img_id");
        String image = jsonObj.getString("image");
        if (imgId == 0 || image == null) {
            return Result.defContentErrorResult();
        }

        int cid = (Integer) params.get("cid");
        int gid = (Integer) params.get("gid");
        GrowthImage gImage = new GrowthImage(cid, gid, imgId, image);
        gImage.setStatus(Status.NEW);
        Result ret = new Result();
        ret.setData(gImage);
        return ret;
    }

}
