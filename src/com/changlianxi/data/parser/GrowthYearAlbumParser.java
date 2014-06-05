package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthAlbum;
import com.changlianxi.data.GrowthAlbumImages;
import com.changlianxi.data.GrowthAlbumList;
import com.changlianxi.data.request.Result;

public class GrowthYearAlbumParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null && "".equals(jsonObj)) {
            return Result.defContentErrorResult();
        }
        List<GrowthAlbum> albumList = new ArrayList<GrowthAlbum>();
        List<GrowthAlbumImages> pics = null;
        List<Growth> growthList = new ArrayList<Growth>();
        int cid = jsonObj.getInt("cid");
        JSONArray jsonGrowthAlbum = jsonObj.getJSONArray("albums");
        for (int j = 0; j < jsonGrowthAlbum.length(); j++) {
            JSONObject obj = (JSONObject) jsonGrowthAlbum.opt(j);
            String albumName = obj.getString("name");
            String albumDate = obj.getString("date");
            int albumTotal = obj.getInt("total");
            int albumContributors = obj.getInt("contributors");
            JSONArray jsonImages = obj.getJSONArray("pics");
            pics = new ArrayList<GrowthAlbumImages>();
            for (int i = 0; i < jsonImages.length(); i++) {
                JSONObject objPic = (JSONObject) jsonImages.opt(i);
                int picId = objPic.getInt("id");
                int picGrowthID = objPic.getInt("growth_id");
                String picHappened = objPic.getString("happened");
                String picLocation = objPic.getString("location");
                String picPath = objPic.getString("img");
                GrowthAlbumImages gimg = new GrowthAlbumImages(cid, picId,
                        picGrowthID, picPath, picHappened, picLocation);
                pics.add(gimg);
            }
            GrowthAlbum album = new GrowthAlbum(cid, pics, albumName,
                    albumDate, albumTotal, albumContributors);
            albumList.add(album);

        }
        JSONArray growthArray = jsonObj.getJSONArray("growths");
        for (int i = 0; i < growthArray.length(); i++) {
            JSONObject obj = (JSONObject) growthArray.opt(i);
            int id = obj.getInt("id");
            int publisher = obj.getInt("uid");
            String content = obj.getString("content");
            String location = obj.getString("location");
            String happened = obj.getString("happen");
            String published = obj.getString("publish");
            int praise = obj.getInt("praise");
            int comment = obj.getInt("comment");
            int myPraise = obj.getInt("mypraise");
            Growth growth = new Growth(cid, id, publisher, content, location,
                    happened, published);
            growth.setPraiseCnt(praise);
            growth.setCommentCnt(comment);
            growth.setPraised(myPraise > 0);
            growthList.add(growth);
        }
        GrowthAlbumList growthAlbumList = new GrowthAlbumList(cid, albumList,
                growthList);
        Result ret = new Result();
        ret.setData(growthAlbumList);
        return ret;
    }
}
