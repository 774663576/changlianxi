package com.changlianxi.util;

public class ErrorCodeUtil {
    private static String errorCodeEnglish[] = { "NOT_POST_REQUEST",
            "NEED_MORE_PARAMS", "NOT_EXIST_USER", "USER_ALREADY_EXIST",
            "WRONG_PASSWORD", "FAIL_SEND_AUTH_CODE", "DB_SAVE_ERROR",
            "FAIL_VERIFY_AUTH_CODE", "FIELD_NOT_EXIST", "NOT_EXIST_PERSON",
            "PERSON_ALREADY_IN_CIRCLE", "NOT_EXIST_CIRCLE",
            "PERMISSION_DENIED", "IMAGE_SAVE_ERROR",
            "CREATOR_CANNOT_QUIT_CIRCLE", "CIRCLE_CANNOT_DISSOLVE",
            "REPEAT_OPERATION", "TARGET_NOT_EXIST", "MSG_DIST_ERROR",
            "CONDITION_NOT_SATISFY", "TOKEN_INVALID", "OLD_PASSWD_WRONG" };
    private static String errorCodeChinese[] = { "非post请求", "请求参数不完整", "用户不存在",
            "您输入的手机号码已注册", "密码不正确", "发送校验码短信失败", "数据库操作失败",
            "啊哦，验证码不对\n验证码为6个数字，请再确认输入一次", "字段不存在", "成员不存在", "成员已存在圈子中",
            "圈子不存在", "权限不够", "保存图片出错", "创建者不能退出圈子", "圈子不能被解散", "重复操作", "目标不存在",
            "消息发送出错", "条件不满足", "用户身份验证失败", "老密码错误" };

    /**
     * 将英文错误码转换为汉语错误码
     * 
     * @param errorCode
     *            要转换的errorCode
     */
    public static String convertToChines(String errorCode) {
        for (int i = 0; i < errorCodeEnglish.length; i++) {
            if (errorCodeEnglish[i].equals(errorCode)) {
                return errorCodeChinese[i];
            }
        }
        return errorCode;

    }
}