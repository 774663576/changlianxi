package com.changlianxi.data.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.changlianxi.data.AbstractData.Status;
import com.changlianxi.data.ChatPartner;
import com.changlianxi.data.ChatPartnerList;
import com.changlianxi.data.enums.ChatType;
import com.changlianxi.data.request.Result;
import com.changlianxi.util.DateUtils;

public class ChatPartnerListParser implements IParser {

    @Override
    public Result parse(Map<String, Object> params, JSONObject jsonObj)
            throws Exception {
        if (jsonObj == null) {
            return Result.defContentErrorResult();
        }

        int total = jsonObj.getInt("total");
        int requestTime = jsonObj.getInt("current");
        JSONArray jsonArr = jsonObj.getJSONArray("messages");
        if (jsonArr == null) {
            return Result.defContentErrorResult();
        }
        List<ChatPartner> partners = new ArrayList<ChatPartner>();
        long start = 0L, end = 0L;
        for (int i = jsonArr.length() - 1; i >= 0; i--) {
            JSONObject obj = (JSONObject) jsonArr.opt(i);
            int partnerId = obj.getInt("uid");
            String partnerName = "";
            String uavatar = "";
            if (obj.has("uname")) {
                partnerName = obj.getString("uname");
            }
            if (obj.has("uavatar")) {
                uavatar = obj.getString("uavatar");
            }
            int chatId = obj.getInt("mid");
            int cid = obj.getInt("cid");
            String type = obj.getString("type");
            String content = obj.getString("msg");
            String time = obj.getString("time");
            int unReadCnt = obj.getInt("new");
            ChatPartner partner = new ChatPartner(cid, partnerId, chatId,
                    content);
            partner.setPartnerName(partnerName);
            partner.setType(ChatType.convert(type));
            partner.setTime(time);
            partner.setUnReadCnt(unReadCnt);
            partner.setStatus(Status.NEW);
            partner.setuAvatar(uavatar);
            partners.add(partner);

            long tmp = DateUtils.convertToDate(time);
            if (end == 0 || tmp > end) {
                end = tmp;
            }
            if (start == 0 || tmp < start) {
                tmp = start;
            }
        }

        ChatPartnerList pcpl = new ChatPartnerList();
        pcpl.setPartners(partners);
        pcpl.setTotal(total);
        pcpl.setStartTime(start);
        pcpl.setEndTime(end);
        if (total < partners.size()) {
            pcpl.setLastReqTime(end);
        } else {
            pcpl.setLastReqTime(requestTime);
        }
        Result ret = new Result();
        ret.setData(pcpl);

        return ret;
    }
}
