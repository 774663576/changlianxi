package com.changlianxi.data.request;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.changlianxi.data.Global;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.util.HttpUrlHelper;
import com.changlianxi.util.Logger;
import com.changlianxi.util.Logger.Level;

public class ApiRequest {

    private static Result parse(IParser parser, String httpResult,
            Map<String, Object> params) {
        if (httpResult == null) {
            return new Result(null, RetStatus.FAIL, RetError.NETWORK_ERROR);
        }
        try {
            JSONObject jsonObj = new JSONObject(httpResult);
            String rt = jsonObj.getString("rt");
            if (!rt.equals("1")) {
                String err = jsonObj.getString("err");
                Result ret = new Result();
                ret.setStatus(RetStatus.FAIL);
                ret.setErr(err);

                return ret;
            }

            Result ret = parser.parse(params, jsonObj);
            return ret;
        } catch (Exception e) {
            Logger.out("ApiRequest.parse", e, Level.WARN);
        }
        return Result.defContentErrorResult();
    }

    public static Result requestWithToken(String url, String uid, String token,
            Map<String, Object> params, IParser parser) {
        params.put("uid", uid);
        params.put("token", token);
        for (String key : params.keySet()) {
            Logger.out("ApiRequest.request",
                    "[param] " + key + ", " + params.get(key), Level.DEBUG);
        }
        Logger.out("ApiRequest.request", "[url] " + url, Level.DEBUG);

        String result = HttpUrlHelper.postData(params, url);
        Logger.out("ApiRequest.request", "[result] " + result, Level.DEBUG);

        return parse(parser, result, params);
    }

    public static Result requestWithToken(String url,
            Map<String, Object> params, IParser parser) {
        return requestWithToken(url, Global.getUid(), Global.getUserToken(),
                params, parser);
    }

    public static Result uploadFileWithToken(String url, String uid,
            String token, Map<String, Object> params, File file, String pkey,
            IParser parser) {
        params.put("uid", uid);
        params.put("token", token);
        for (String key : params.keySet()) {
            Logger.out("ApiRequest.request",
                    "[param] " + key + ", " + params.get(key), Level.DEBUG);
        }
        Logger.out("ApiRequest.request", "[url] " + url, Level.DEBUG);

        String result = HttpUrlHelper.upLoadPic(HttpUrlHelper.DEFAULT_HOST,
                url, params, file, pkey);
        Logger.out("ApiRequest.request", "[result] " + result, Level.DEBUG);
        return parse(parser, result, params);
    }

    public static Result uploadFileWithToken(String url,
            Map<String, Object> params, File file, String pkey, IParser parser) {
        return uploadFileWithToken(url, Global.getUid(), Global.getUserToken(),
                params, file, pkey, parser);
    }

    public static Result uploadFileArrayWithToken(String url, String uid,
            String token, Map<String, Object> params, List<File> files,
            String pkey, IParser parser) {
        params.put("uid", uid);
        params.put("token", token);
        for (String key : params.keySet()) {
            Logger.out("ApiRequest.request",
                    "[param] " + key + ", " + params.get(key), Level.DEBUG);
        }
        Logger.out("ApiRequest.request", "[url] " + url, Level.DEBUG);

        String result = HttpUrlHelper.upLoadPicArray(
                HttpUrlHelper.DEFAULT_HOST, url, params, files, pkey);
        Logger.out("ApiRequest.request", "[result] " + result, Level.DEBUG);

        return parse(parser, result, params);
    }

    public static Result uploadFileArrayWithToken(String url,
            Map<String, Object> params, List<File> files, String pkey,
            IParser parser) {
        return uploadFileArrayWithToken(url, Global.getUid(),
                Global.getUserToken(), params, files, pkey, parser);
    }

}
