package com.changlianxi.data.enums;

import java.util.HashMap;
import java.util.Map;

import com.changlianxi.util.Logger;
import com.changlianxi.util.Logger.Level;

import android.util.Log;

public enum RetError {
    NONE, INVALID, UNKOWN, NETWORK_ERROR, NOT_POST_REQUEST, NEED_MORE_PARAMS, NOT_EXIST_USER, USER_ALREADY_EXIST, WRONG_PASSWORD, FAIL_SEND_AUTH_CODE, DB_SAVE_ERROR, FAIL_VERIFY_AUTH_CODE, FIELD_NOT_EXIST, NOT_EXIST_PERSON, PERSON_ALREADY_IN_CIRCLE, NOT_EXIST_CIRCLE, PERMISSION_DENIED, IMAGE_SAVE_ERROR, CREATOR_CANNOT_QUIT_CIRCLE, CIRCLE_CANNOT_DISSOLVE, REPEAT_OPERATION, TARGET_NOT_EXIST, MSG_DIST_ERROR, CONDITION_NOT_SATISFY, TOKEN_INVALID, OLD_PASSWD_WRONG, INVALID_OPERATION, WRANG_MOBILE_PHONE_NUMBER, CELLPHONE_ALREADY_USED, USER_ALREADY_BIND;

    public static Map<String, RetError> str2Error = new HashMap<String, RetError>();
    static {
        for (RetError err : RetError.values()) {
            str2Error.put(err.name(), err);
        }
    }
    public static Map<String, String> s2t = new HashMap<String, String>();
    static {
        s2t.put("NETWORK_ERROR", "杯具，网络不通，快检查下吧！");
        s2t.put("NOT_POST_REQUEST", "非post请求");
        s2t.put("NEED_MORE_PARAMS", "请求参数不完整");
        s2t.put("NOT_EXIST_USER", "用户不存在");
        s2t.put("USER_ALREADY_EXIST", "用户已存在");
        s2t.put("WRONG_PASSWORD", "密码不正确");
        s2t.put("FAIL_SEND_AUTH_CODE", "发送校验码短信失败");
        s2t.put("DB_SAVE_ERROR", "数据库操作失败");
        s2t.put("FAIL_VERIFY_AUTH_CODE", "校验码不正确");
        s2t.put("FIELD_NOT_EXIST", "字段不存在");
        s2t.put("NOT_EXIST_PERSON", "成员不存在");
        s2t.put("PERSON_ALREADY_IN_CIRCLE", "成员已存在圈子中");
        s2t.put("NOT_EXIST_CIRCLE", "圈子不存在");
        s2t.put("PERMISSION_DENIED", "权限不够");
        s2t.put("IMAGE_SAVE_ERROR", "保存图片出错");
        s2t.put("CREATOR_CANNOT_QUIT_CIRCLE", "创建者不能退出圈子");
        s2t.put("CIRCLE_CANNOT_DISSOLVE", "圈子不能被解散");
        s2t.put("REPEAT_OPERATION", "重复操作");
        s2t.put("TARGET_NOT_EXIST", "目标不存在");
        s2t.put("MSG_DIST_ERROR", "消息发送出错");
        s2t.put("CONDITION_NOT_SATISFY", "条件不满足");
        s2t.put("TOKEN_INVALID", "用户身份验证失败");
        s2t.put("OLD_PASSWD_WRONG", "老密码错误");
        s2t.put("INVALID_OPERATION", "无效操作");
        s2t.put("WRANG_MOBILE_PHONE_NUMBER", "手机号码格式不正确");
        s2t.put("CELLPHONE_ALREADY_USED", "手机号已注册或已版定");
        s2t.put("USER_ALREADY_BIND", "该用户已绑定手机号");
    }

    public static RetError convert(String err) {
        if (!str2Error.containsKey(err)) {
            return RetError.UNKOWN;
        } else {
            return str2Error.get(err);
        }
    }

    public static String toText(RetError err) {
        if (s2t.containsKey(err.toString())) {
            return s2t.get(err.toString());
        }
        return "未知错误！";
    }

}
