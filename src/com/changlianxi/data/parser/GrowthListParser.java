package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthImage;
import com.changlianxi.data.GrowthList;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;

public class GrowthListParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        int cid = jsonObj.getInt("cid");
        int total = jsonObj.getInt("total");
        int requestTime = jsonObj.getInt("current");
        String urlBase = jsonObj.getString("urlbase");

        JSONArray jsonArr = jsonObj.getJSONArray("growths");
        if (jsonArr == null) {
            return Result.defContentErrorResult();
        }
        List<Growth> growths = new ArrayList<Growth>();
        long start = 0L, end = 0L;
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject obj = (JSONObject) jsonArr.opt(i);
            // growth info
            int id = obj.getInt("id");
            int publisher = obj.getInt("uid");
            String content = obj.getString("content");
            String location = obj.getString("location");
            String happened = obj.getString("happen");
            String published = obj.getString("publish");
            int praise = obj.getInt("praise");
            int comment = obj.getInt("comment");
            int myPraise = obj.getInt("mypraise");

            // growth images
            JSONArray jsonImages = obj.getJSONArray("images");
            List<GrowthImage> images = new ArrayList<GrowthImage>();
            for (int j = 0; j < jsonImages.length(); j++) {
                JSONObject obj2 = (JSONObject) jsonImages.opt(j);
                int imgId = obj2.getInt("imgid");
                String img = urlBase + obj2.getString("img");
                GrowthImage gimg = new GrowthImage(cid, id, imgId, img);
                images.add(gimg);
            }

            Growth growth = new Growth(cid, id, publisher, content, location,
                    happened, published);
            growth.setPraiseCnt(praise);
            growth.setCommentCnt(comment);
            growth.setPraised(myPraise > 0);
            growth.setImages(images);
            growths.add(growth);

            long tmp = DateUtils.convertToDate(published);
            if (end == 0 || tmp > end) {
                end = tmp;
            }
            if (start == 0 || tmp < start) {
                tmp = start;
            }
        }

        JSONArray delArr = jsonObj.getJSONArray("dels");
        List<Integer> delIds = new ArrayList<Integer>();
        if (delArr != null) {
            for (int i = 0; i < delArr.length(); i++) {
                int delid = (Integer) delArr.opt(i);
                delIds.add(delid);
            }
        }

        GrowthList gl = new GrowthList(cid);
        gl.setGrowths(growths);
        gl.setTotal(total);
        gl.setStartTime(start);
        gl.setEndTime(end);
        if (total < growths.size()) {
            gl.setLastReqTime(end);
        } else {
            gl.setLastReqTime(requestTime);
        }
        gl.setDelIds(delIds);

        Result ret = new Result();
        ret.setData(gl);
        return ret;
    }

}
