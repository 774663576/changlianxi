package com.changlianxi.data;

import java.util.HashMap;
import java.util.Map;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.data.enums.RetStatus;
import com.changlianxi.data.parser.IParser;
import com.changlianxi.data.parser.MapParser;
import com.changlianxi.data.parser.SimpleParser;
import com.changlianxi.data.parser.StringParser;
import com.changlianxi.data.request.ApiRequest;
import com.changlianxi.data.request.MapResult;
import com.changlianxi.data.request.Result;
import com.changlianxi.data.request.StringResult;
import com.changlianxi.util.MD5;
import com.changlianxi.util.SharedUtils;
import com.changlianxi.util.StringUtils;
import com.changlianxi.util.Utils;

public class Register {
    private static final String IREGISTER_API = "/users/iregister";
    private static final String IVERFITY_AUTH_CODE = "/users/iverifyAuthCode";
    private static final String IAGAIN_GET_AUTH_CODE = "/users/isendAuthCode";
    private static final String ICOMPLETE_REGISTER = "/users/icompleteRegister";
    private static final String ISET_NAME = "/users/isetUserInfo";
    private String cellPhone = "";
    private String email = "";
    private String uid = "";
    private String token = "";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    /**
     * 账号验证
     */
    public RetError cellPhoneAndEmailVerify() {
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
        Result ret = ApiRequest.requestWithToken(IREGISTER_API, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            StringResult sret = (StringResult) ret;
            this.uid = sret.getStr();
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
        params.put("type", "register");
        Result ret = ApiRequest.requestWithToken(IVERFITY_AUTH_CODE, params,
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
        params.put("type", "register");
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
     *注册完成设置密码
     * @param password
     * @return
     */
    public RetError setPassword(String password) {
        String[] keys = { "token", "uid" };
        IParser parser = new MapParser(keys);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("version",
                Utils.getVersionName(CLXApplication.getInstance()));
        params.put("device", Utils.getModelAndRelease());
        params.put("os", Utils.getOS());
        if ("".equals(email)) {
            params.put("cellphone", cellPhone);
        } else {
            params.put("email", email);
        }
        params.put("passwd", password);
        Result ret = ApiRequest.requestWithToken(ICOMPLETE_REGISTER, params,
                parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            MapResult mret = (MapResult) ret;
            this.token = (String) (mret.getMaps().get("token"));
            this.uid = (String) (mret.getMaps().get("uid"));
            SharedUtils.setString("token", token);
            SharedUtils.setString("uid", uid);
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }

    /**
     * 注册完成后设置姓名
     * @param name
     * @return
     */
    public RetError setName(String name) {
        IParser parser = new SimpleParser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("f", "name");
        params.put("value", name);
        Result ret = ApiRequest.requestWithToken(ISET_NAME, params, parser);
        if (ret.getStatus() == RetStatus.SUCC) {
            return RetError.NONE;
        } else {
            return ret.getErr();
        }
    }
}
