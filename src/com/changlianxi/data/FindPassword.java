package com.changlianxi.data;

import java.util.HashMap;
import java.util.Map;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.parser.StringParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.StringResult;
import com.changlianxi.util.MD5;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;

public class FindPassword {
    private static final String IRETRIEVE_PASSWORD = "/users/iretrievePassword";
    private static final String IVERIFYAUTH_CODE = "/users/iverifyAuthCode";
    private static final String IAGAIN_GET_AUTH_CODE = "/users/isendAuthCode";
    private static final String ISET_PASSWROD = "/users/isetPasswd";
    private String cellPhone = "";
    private String email = "";
    private String uid = "";

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RetError retrievePasswrod() {
        IParser parser = new StringParser("uid");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("cellphone", cellPhone);
        params.put("email", email);
        params.put("version",
                Utils.getVersionName(CLXApplication.getInstance()));
        String tag = "".equals(cellPhone) ? email : cellPhone;
        params.put(
                "tag",
                MD5.MD5_32(StringUtils.reverseSort(tag)
                        + Utils.getVersionName(CLXApplication.getInstance())
                        + tag
                        + Utils.getVersionName(CLXApplication.getInstance())));
        Result ret = ApiRequest.requestWithToken(IRETRIEVE_PASSWORD, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            this.uid = sret.getStr();
            SharedUtils.setString("uid", uid);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
    *  验证  验证码是否正确
    */
    public RetError verifyAuthCode(String auth_code) {
        IParser parser = new SimpleParser();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("auth_code", auth_code);
        params.put("type", "retrievePasswd");
        Result ret = ApiRequest.requestWithToken(IVERIFYAUTH_CODE, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
    * 重新获取验证码
    * @return
    */
    public RetError getAuthCodeAgain() {
        IParser parser = new SimpleParser();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("type", "retrievePasswd");
        if ("".equals(email)) {
            params.put("cellphone", cellPhone);
        } else {
            params.put("email", email);
        }
        Result ret = ApiRequest.requestWithToken(IAGAIN_GET_AUTH_CODE, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * 设置密码
     * @param pswd
     * @return
     */
    public RetError setPassword(String pswd) {
        IParser parser = new SimpleParser();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("type", "retrievePasswd");
        if ("".equals(email)) {
            params.put("cellphone", cellPhone);
        } else {
            params.put("email", email);
        }
        params.put("passwd", pswd);
        Result ret = ApiRequest.requestWithToken(ISET_PASSWROD, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }
}
