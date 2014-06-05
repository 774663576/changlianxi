package com.changlianxi.util;

/**
 * 用户详细资料工具类
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoUtils {
    /* 分类标题 */
    public static final String infoTitleKey[] = { "基本信息", "联系电话", "电子邮箱",
            "社交账号", "地址信息", "教育经历", "工作经历" };
    /* 基本数据对包含的属性key "D_NAME", */
    public static final String basicStr[] = { "D_NAME", "D_EMPLOYER",
            "D_JOBTITLE", "D_BIRTHDAY", "D_GENDAR", "D_NICKNAME", "D_REMARK" };

    /* 基本数据key所对应的中文名称 "姓名", */
    public static final String basicChineseStr[] = { "姓名", "单位", "职位", "生日",
            "性別" , "昵称", "备注"};
    public static final String basicUserStr[] = { "D_NAME", "D_EMPLOYER",
            "D_JOBTITLE", "D_BIRTHDAY", "D_GENDAR", "D_NICKNAME", "D_REMARK" };
    public static final String basicUserStr2[] = { "D_NAME", "D_EMPLOYER",
        "D_JOBTITLE", "D_BIRTHDAY", "D_GENDAR", "D_NICKNAME", "D_REMARK" ,"D_AVATAR"};

    /* 基本数据key所对应的中文名称 "姓名", */
    public static final String basicUserChineseStr[] = { "姓名", "单位", "职位",
            "生日", "性別", "昵称", "备注" };
    public static final String basicUserChineseStr2[] = { "姓名", "单位", "职位",
        "生日", "性別", "昵称", "备注", "头像" };
    /* 联系方式数据对包含的属性key */
    public static final String contactPhone[] = { "D_CELLPHONE", "D_MOBILE",
            "D_WORK_PHONE", "D_HOME_PHONE", "D_PRIVATE_PHONE", "D_WORK_FAX",
            "D_HOME_FAX", "D_PAGER", "D_SHORT_PHONE", "D_OTHER_PHONE" };
    /* 联系方式数据key所对应的中文名称 */
    public static final String contactPhoneChinesetStr[] = { "手机号", "常用电话",
            "工作电话", "住宅电话", "私人电话", "工作传真", "住宅传真", "传呼", "短号码", "其他号码" };

    /*电子邮箱key*/
    public static final String emailStr[] = { "D_EMAIL", "D_WORK_EMAIL",
            "D_PERSONAL_EMAIL", "D_OTHER_EMAIL" };
    /*电子邮箱中文key*/
    public static final String emailChineseStr[] = { "常用邮箱", "工作邮箱", "个人邮箱",
            "其他邮箱" };

    /* 账号数据对包含的属性key */
    public static final String socialStr[] = { "D_WEIXIN", "D_QQ",
            "D_SINA_WEIBO", "D_RENREN", "D_QQ_WEIBO", "D_BLOG", "D_FACEBOOK",
            "D_TWITTER", "D_SKYPE" };
    /* 账号数据key所对应的中文名称 */
    public static final String socialChineseStr[] = { "微信", "QQ", "新浪微博",
            "人人网", "腾讯微博", "个人博客", "FaceBook", "Twitter", "Skype" };
    /* 地址数据对包含的属性key */
    public static final String addressStr[] = { "D_BIRTH_PLACE",
            "D_HOME_ADDRESS", "D_WORK_ADDRESS" };
    /* 地址数据key所对应的中文名称 */
    public static final String addressChineseStr[] = { "籍贯", "居住地址", "工作地址" };
    /* 其他数据对包含的属性key */
    /* 其他数据key所对应的中文名称 */
    /* 教育经历包含的属性key */
    public static final String eduStr[] = { "D_KINDER_GARTEN",
            "D_GRADE_SCHOOL", "D_JUNIOR_SCHOOL", "D_SENIOR_SCHOOL",
            "D_COLLEGE", "D_TECHNICAL_SCHOOL", "D_JUNIOR_COLLEGE",
            "D_MASTER_COLLEGE", "D_PHD_COLLEGE" };
    /* 教育key所对应的中文名称 */
    public static final String eduChinesStr[] = { "幼儿园", "小学", "初中", "高中",
            "大学", "中专", "大专", "硕士", "博士" };
    /* 工作经历所包含的属性key */
    public static final String workStr[] = { "D_JOB" };
    /* 工作key所对应的中文名称 */
    public static final String workChineseStr[] = { "工作" };
    /* 资料属性type */
    public static final String infoKey[] = { "D_NAME", "D_EMPLOYER",
            "D_JOBTITLE", "D_BIRTHDAY", "D_GENDAR", "D_NICKNAME", "D_REMARK",
            "D_CELLPHONE", "D_MOBILE", "D_WORK_PHONE", "D_HOME_PHONE",
            "D_PRIVATE_PHONE", "D_WORK_FAX", "D_HOME_FAX", "D_PAGER",
            "D_SHORT_PHONE", "D_OTHER_PHONE", "D_EMAIL", "D_WORK_EMAIL",
            "D_PERSONAL_EMAIL", "D_OTHER_EMAIL", "D_WEIXIN", "D_QQ",
            "D_SINA_WEIBO", "D_RENREN", "D_QQ_WEIBO", "D_BLOG", "D_FACEBOOK",
            "D_TWITTER", "D_SKYPE", "D_BIRTH_PLACE", "D_HOME_ADDRESS",
            "D_WORK_ADDRESS", "D_KINDER_GARTEN", "D_GRADE_SCHOOL",
            "D_JUNIOR_SCHOOL", "D_SENIOR_SCHOOL", "D_COLLEGE",
            "D_TECHNICAL_SCHOOL", "D_JUNIOR_COLLEGE", "D_MASTER_COLLEGE",
            "D_PHD_COLLEGE", "D_JOB" };
    public static final String infoKey2[] = { "D_NAME", "D_EMPLOYER",
        "D_JOBTITLE", "D_BIRTHDAY","D_AVATAR", "D_GENDAR", "D_NICKNAME", "D_REMARK",
        "D_CELLPHONE", "D_MOBILE", "D_WORK_PHONE", "D_HOME_PHONE",
        "D_PRIVATE_PHONE", "D_WORK_FAX", "D_HOME_FAX", "D_PAGER",
        "D_SHORT_PHONE", "D_OTHER_PHONE", "D_EMAIL", "D_WORK_EMAIL",
        "D_PERSONAL_EMAIL", "D_OTHER_EMAIL", "D_WEIXIN", "D_QQ",
        "D_SINA_WEIBO", "D_RENREN", "D_QQ_WEIBO", "D_BLOG", "D_FACEBOOK",
        "D_TWITTER", "D_SKYPE", "D_BIRTH_PLACE", "D_HOME_ADDRESS",
        "D_WORK_ADDRESS", "D_KINDER_GARTEN", "D_GRADE_SCHOOL",
        "D_JUNIOR_SCHOOL", "D_SENIOR_SCHOOL", "D_COLLEGE",
        "D_TECHNICAL_SCHOOL", "D_JUNIOR_COLLEGE", "D_MASTER_COLLEGE",
        "D_PHD_COLLEGE", "D_JOB" };
    public static final String infoKeyChinese[] = { "姓名", "单位", "职位", "生日",
            "性別", "昵称", "备注", "手机号", "常用电话", "工作电话", "住宅电话", "私人电话", "工作传真",
            "住宅传真", "传呼", "短号码", "其他号码", "常用邮箱", "工作邮箱", "个人邮箱", "其他邮箱", "微信",
            "QQ", "新浪微博", "人人网", "腾讯微博", "个人博客", "FaceBook", "Twitter",
            "Skype", "籍贯", "居住地址", "工作地址", "幼儿园", "小学", "初中", "高中", "大学", "中专",
            "大专", "硕士", "博士", "工作" };
    public static final String infoKeyChinese2[] = { "姓名", "单位", "职位", "生日","头像",
        "性別", "昵称", "备注", "手机号", "常用电话", "工作电话", "住宅电话", "私人电话", "工作传真",
        "住宅传真", "传呼", "短号码", "其他号码", "常用邮箱", "工作邮箱", "个人邮箱", "其他邮箱", "微信",
        "QQ", "新浪微博", "人人网", "腾讯微博", "个人博客", "FaceBook", "Twitter",
        "Skype", "籍贯", "居住地址", "工作地址", "幼儿园", "小学", "初中", "高中", "大学", "中专",
        "大专", "硕士", "博士", "工作" };

    /**
     * 将英文key装换为中文key
     * 
     * @param key
     *            要转换的key
     */
    public static String convertToChines(String key) {

        for (int i = 0; i < infoKey.length; i++) {
            if (infoKey[i].equals(key)) {
                return infoKeyChinese[i];
            }
        }

        return key;

    }
    public static String convertToChines2(String key) {

        for (int i = 0; i < infoKey2.length; i++) {
            if (infoKey2[i].equals(key)) {
                return infoKeyChinese2[i];
            }
        }

        return key;

    }

    /**
     * 将中文key装换为英文key
     * 
     * @param key
     *            要转换的key
     */
    public static String convertToEnglish(String key) {
        for (int i = 0; i < infoKeyChinese.length; i++) {
            if (infoKeyChinese[i].equals(key)) {
                return infoKey[i];
            }
        }
        return key;

    }
}
