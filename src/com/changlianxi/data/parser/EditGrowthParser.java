package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthImage;
import com.changlianxi.data.request.Result;

public class EditGrowthParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        if (!jsonObj.has("gid")) {
            return Result.defContentErrorResult();
        }
        Growth g = new Growth(0);
        List<GrowthImage> imgs = new ArrayList<GrowthImage>();
        int gid = jsonObj.getInt("gid");
        JSONArray jsonArr = jsonObj.getJSONArray("images");
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject obj = (JSONObject) jsonArr.opt(i);
            int id = obj.getInt("id");
            String img = obj.getString("img");
            GrowthImage gImage = new GrowthImage(0, gid, id, img);
            imgs.add(gImage);
        }
        g.setImages(imgs);
        Result ret = new Result();
        ret.setData(g);
        return ret;
    }

}
