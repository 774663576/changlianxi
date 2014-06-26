package com.changlianxi.data.enums;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

public enum PersonDetailType {

    UNKNOWN, D_NAME, D_GENDAR, D_GRE_BIRTHDAY, D_BIRTHDAY, D_AVATAR, D_EMPLOYER, D_JOBTITLE, D_NICKNAME, D_REMARK, D_ROLE, D_CELLPHONE, D_WORK_PHONE, D_HOME_PHONE, D_MOBILE, D_PRIVATE_PHONE, D_WORK_FAX, D_HOME_FAX, D_PAGER, D_SHORT_PHONE, D_OTHER_PHONE, D_ACCOUNT_EMAIL, D_EMAIL, D_PERSONAL_EMAIL, D_WORK_EMAIL, D_OTHER_EMAIL, D_CURRENT_ADDRESS, D_POSTAL_ADDRESS, D_WORK_ADDRESS, D_HOME_ADDRESS, D_BIRTH_PLACE, D_QQ, D_WEIXIN, D_SINA_WEIBO, D_RENREN, D_QQ_WEIBO, D_TWITTER, D_FACEBOOK, D_SKYPE, D_BLOG, D_HOME_PAGE, D_COLLEGE, D_SENIOR_SCHOOL, D_JUNIOR_COLLEGE, D_TECHNICAL_SCHOOL, D_JUNIOR_SCHOOL, D_GRADE_SCHOOL, D_MASTER_COLLEGE, D_PHD_COLLEGE, D_KINDER_GARTEN, D_OTHER_EDU, D_JOB;

    public static Map<String, PersonDetailType> s2t = new HashMap<String, PersonDetailType>();
    static {
        for (PersonDetailType type : PersonDetailType.values()) {
            s2t.put(type.name(), type);
        }
    }

    public static PersonDetailType convertToType(String s) {
        if (s2t.containsKey(s)) {
            return s2t.get(s);
        }
        return PersonDetailType.UNKNOWN;
    }

    public static boolean hasTimeRange(PersonDetailType type) {
        return type == D_COLLEGE || type == D_GRADE_SCHOOL
                || type == D_JUNIOR_COLLEGE || type == D_JUNIOR_SCHOOL
                || type == D_KINDER_GARTEN || type == D_MASTER_COLLEGE
                || type == D_PHD_COLLEGE || type == D_SENIOR_SCHOOL
                || type == D_TECHNICAL_SCHOOL || type == D_JOB;
    }

    public static Map<PersonDetailType, String> t2text = new HashMap<PersonDetailType, String>();
    public static Map<PersonDetailType, Integer> t2id = new HashMap<PersonDetailType, Integer>();
    @SuppressLint("UseSparseArrays")
    public static Map<Integer, PersonDetailType> id2t = new HashMap<Integer, PersonDetailType>();
    static {
        t2text.put(D_NAME, "姓名");
        t2id.put(D_NAME, 1);
        t2text.put(D_GENDAR, "性别");
        t2id.put(D_GENDAR, 2);
        t2text.put(D_BIRTHDAY, "生日");
        t2text.put(D_GRE_BIRTHDAY, "公历生日");
        t2id.put(D_BIRTHDAY, 3);
        t2text.put(D_AVATAR, "头像");
        t2id.put(D_AVATAR, 4);
        t2text.put(D_EMPLOYER, "单位");
        t2id.put(D_EMPLOYER, 5);
        t2text.put(D_JOBTITLE, "职位");
        t2id.put(D_JOBTITLE, 6);
        t2text.put(D_NICKNAME, "昵称");
        t2id.put(D_NICKNAME, 7);
        t2text.put(D_REMARK, "备注");
        t2id.put(D_REMARK, 8);
        t2text.put(D_ROLE, "圈子内角色");
        t2id.put(D_ROLE, 9);

        t2text.put(D_CELLPHONE, "手机号");
        t2id.put(D_CELLPHONE, 10);
        t2text.put(D_WORK_PHONE, "工作");
        t2id.put(D_WORK_PHONE, 11);
        t2text.put(D_HOME_PHONE, "家庭");
        t2id.put(D_HOME_PHONE, 12);
        t2text.put(D_MOBILE, "常用");
        t2id.put(D_MOBILE, 13);
        t2text.put(D_PRIVATE_PHONE, "私人");
        t2id.put(D_PRIVATE_PHONE, 14);
        t2text.put(D_WORK_FAX, "工作传真");
        t2id.put(D_WORK_FAX, 15);
        t2text.put(D_HOME_FAX, "家庭传真");
        t2id.put(D_HOME_FAX, 16);
        t2text.put(D_PAGER, "传呼");
        t2id.put(D_PAGER, 17);
        t2text.put(D_SHORT_PHONE, "短号码");
        t2id.put(D_SHORT_PHONE, 18);
        t2text.put(D_OTHER_PHONE, "其他");
        t2id.put(D_OTHER_PHONE, 19);

        t2text.put(D_ACCOUNT_EMAIL, "电子邮箱");
        t2text.put(D_EMAIL, "常用邮箱");
        t2id.put(D_EMAIL, 20);
        t2text.put(D_PERSONAL_EMAIL, "个人邮箱");
        t2id.put(D_PERSONAL_EMAIL, 21);
        t2text.put(D_WORK_EMAIL, "工作邮箱");
        t2id.put(D_WORK_EMAIL, 22);
        t2text.put(D_OTHER_EMAIL, "其他邮箱");
        t2id.put(D_OTHER_EMAIL, 23);

        t2text.put(D_CURRENT_ADDRESS, "当前地址");
        t2text.put(D_POSTAL_ADDRESS, "通讯地址");
        t2text.put(D_WORK_ADDRESS, "工作地址");
        t2id.put(D_WORK_ADDRESS, 24);
        t2text.put(D_HOME_ADDRESS, "居住地址");
        t2id.put(D_HOME_ADDRESS, 25);
        t2text.put(D_BIRTH_PLACE, "籍贯");
        t2id.put(D_BIRTH_PLACE, 26);

        t2text.put(D_QQ, "QQ");
        t2id.put(D_QQ, 27);
        t2text.put(D_WEIXIN, "微信");
        t2id.put(D_WEIXIN, 28);
        t2text.put(D_SINA_WEIBO, "新浪微博");
        t2id.put(D_SINA_WEIBO, 29);
        t2text.put(D_RENREN, "人人");
        t2id.put(D_RENREN, 30);
        t2text.put(D_QQ_WEIBO, "腾讯微博");
        t2id.put(D_QQ_WEIBO, 31);
        t2text.put(D_TWITTER, "twitter");
        t2id.put(D_TWITTER, 32);
        t2text.put(D_FACEBOOK, "facebook");
        t2id.put(D_FACEBOOK, 33);
        t2text.put(D_SKYPE, "skype");
        t2id.put(D_SKYPE, 34);
        t2text.put(D_BLOG, "博客");
        t2id.put(D_BLOG, 35);
        t2text.put(D_HOME_PAGE, "个人主页");
        t2id.put(D_HOME_PAGE, 36);

        t2text.put(D_COLLEGE, "大学");
        t2id.put(D_COLLEGE, 37);
        t2text.put(D_SENIOR_SCHOOL, "高中");
        t2id.put(D_SENIOR_SCHOOL, 38);
        t2text.put(D_JUNIOR_COLLEGE, "大专");
        t2id.put(D_JUNIOR_COLLEGE, 39);
        t2text.put(D_TECHNICAL_SCHOOL, "中专");
        t2id.put(D_TECHNICAL_SCHOOL, 40);
        t2text.put(D_JUNIOR_SCHOOL, "初中");
        t2id.put(D_JUNIOR_SCHOOL, 41);
        t2text.put(D_GRADE_SCHOOL, "小学");
        t2id.put(D_GRADE_SCHOOL, 42);
        t2text.put(D_MASTER_COLLEGE, "硕士");
        t2id.put(D_MASTER_COLLEGE, 43);
        t2text.put(D_PHD_COLLEGE, "博士");
        t2id.put(D_PHD_COLLEGE, 44);
        t2text.put(D_KINDER_GARTEN, "幼儿园");
        t2id.put(D_KINDER_GARTEN, 45);
        t2text.put(D_OTHER_EDU, "其他");
        t2id.put(D_OTHER_EDU, 46);

        t2text.put(D_JOB, "工作");
        t2id.put(D_JOB, 47);

        for (PersonDetailType type : t2id.keySet()) {
            id2t.put(t2id.get(type), type);
        }
    }

    public static String toText(PersonDetailType type) {
        if (t2text.containsKey(type)) {
            return t2text.get(type);
        }
        return "未定义";
    }

    public static int getID(PersonDetailType type) {
        if (t2id.containsKey(type)) {
            return t2id.get(type);
        }
        return 0;
    }

    public static PersonDetailType convertToType(int id) {
        if (id2t.containsKey(id)) {
            return id2t.get(id);
        }
        return PersonDetailType.UNKNOWN;
    }

}
