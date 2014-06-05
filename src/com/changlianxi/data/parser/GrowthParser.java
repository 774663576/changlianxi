package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthImage;
import com.changlianxi.data.request.Result;

public class GrowthParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }
        if (!jsonObj.has("cid") || !jsonObj.has("growth")) {
            return Result.defContentErrorResult();
        }

        int cid = jsonObj.getInt("cid");
        JSONObject jsonGrowth = jsonObj.getJSONObject("growth");
        if (jsonGrowth == null || cid == 0) {
            return Result.defContentErrorResult();
        }

        // growth info
        int id = jsonGrowth.getInt("id");
        int publisher = jsonGrowth.getInt("uid");
        String content = jsonGrowth.getString("content");
        String location = jsonGrowth.getString("location");
        String happened = jsonGrowth.getString("happen");
        String published = jsonGrowth.getString("publish");
        int praise = jsonGrowth.getInt("praise");
        int comment = jsonGrowth.getInt("comment");
        int myPraise = jsonGrowth.getInt("mypraise");

        // growth images
        JSONArray jsonImages = jsonGrowth.getJSONArray("images");
        List<GrowthImage> images = new ArrayList<GrowthImage>();
        for (int i = 0; i < jsonImages.length(); i++) {
            JSONObject obj = (JSONObject) jsonImages.opt(i);
            int imgId = obj.getInt("id");
            String img = obj.getString("img");
            GrowthImage gimg = new GrowthImage(cid, id, imgId, img);
            images.add(gimg);

        }

        Growth growth = new Growth(cid, id, publisher, content, location,
                happened, published);
        growth.setPraiseCnt(praise);
        growth.setCommentCnt(comment);
        growth.setPraised(myPraise > 0);
        growth.setImages(images);
        Result ret = new Result();
        ret.setData(growth);

        return ret;
    }

}
