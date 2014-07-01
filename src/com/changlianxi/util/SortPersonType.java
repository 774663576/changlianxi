package com.changlianxi.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.changlianxi.data.enums.PersonDetailType;
import com.changlianxi.modle.Info;

public class SortPersonType {
    public static Map<PersonDetailType, Integer> typeSort = new HashMap<PersonDetailType, Integer>();
    public static List<PersonDetailType> basic = new ArrayList<PersonDetailType>();
    public static List<PersonDetailType> contactPhone = new ArrayList<PersonDetailType>();
    public static List<PersonDetailType> social = new ArrayList<PersonDetailType>();
    public static List<PersonDetailType> email = new ArrayList<PersonDetailType>();
    public static List<PersonDetailType> add = new ArrayList<PersonDetailType>();
    public static List<PersonDetailType> edu = new ArrayList<PersonDetailType>();
    public static List<Integer> basicID = new ArrayList<Integer>();
    public static List<Integer> contactPhoneID = new ArrayList<Integer>();
    public static List<Integer> socialID = new ArrayList<Integer>();
    public static List<Integer> emailID = new ArrayList<Integer>();
    public static List<Integer> addID = new ArrayList<Integer>();
    public static List<Integer> eduID = new ArrayList<Integer>();
    public static List<Integer> workID = new ArrayList<Integer>();

    static {
        typeSort.put(PersonDetailType.D_AVATAR, 1);
        typeSort.put(PersonDetailType.D_NAME, 2);
        typeSort.put(PersonDetailType.D_EMPLOYER, 3);
        typeSort.put(PersonDetailType.D_JOBTITLE, 4);
        typeSort.put(PersonDetailType.D_BIRTHDAY, 5);
        typeSort.put(PersonDetailType.D_LUNAR_BIRTHDAY, 6);
        typeSort.put(PersonDetailType.D_GENDAR, 7);
        typeSort.put(PersonDetailType.D_NICKNAME, 8);
        typeSort.put(PersonDetailType.D_REMARK, 9);

        typeSort.put(PersonDetailType.D_CELLPHONE, 10);
        typeSort.put(PersonDetailType.D_MOBILE, 11);
        typeSort.put(PersonDetailType.D_WORK_PHONE, 12);
        typeSort.put(PersonDetailType.D_HOME_PHONE, 13);
        typeSort.put(PersonDetailType.D_PRIVATE_PHONE, 14);
        typeSort.put(PersonDetailType.D_WORK_FAX, 15);
        typeSort.put(PersonDetailType.D_HOME_FAX, 16);
        typeSort.put(PersonDetailType.D_PAGER, 17);
        typeSort.put(PersonDetailType.D_SHORT_PHONE, 18);
        typeSort.put(PersonDetailType.D_OTHER_PHONE, 19);

        typeSort.put(PersonDetailType.D_ACCOUNT_EMAIL, 20);
        typeSort.put(PersonDetailType.D_EMAIL, 21);
        typeSort.put(PersonDetailType.D_WORK_EMAIL, 22);
        typeSort.put(PersonDetailType.D_PERSONAL_EMAIL, 23);
        typeSort.put(PersonDetailType.D_OTHER_EMAIL, 24);

        typeSort.put(PersonDetailType.D_BIRTH_PLACE, 25);
        typeSort.put(PersonDetailType.D_CURRENT_ADDRESS, 26);
        typeSort.put(PersonDetailType.D_POSTAL_ADDRESS, 27);
        typeSort.put(PersonDetailType.D_HOME_ADDRESS, 28);
        typeSort.put(PersonDetailType.D_WORK_ADDRESS, 29);

        typeSort.put(PersonDetailType.D_WEIXIN, 30);
        typeSort.put(PersonDetailType.D_QQ, 31);
        typeSort.put(PersonDetailType.D_SINA_WEIBO, 32);
        typeSort.put(PersonDetailType.D_RENREN, 33);
        typeSort.put(PersonDetailType.D_QQ_WEIBO, 34);
        typeSort.put(PersonDetailType.D_BLOG, 35);
        typeSort.put(PersonDetailType.D_FACEBOOK, 36);
        typeSort.put(PersonDetailType.D_TWITTER, 37);
        typeSort.put(PersonDetailType.D_SKYPE, 38);

        typeSort.put(PersonDetailType.D_KINDER_GARTEN, 39);
        typeSort.put(PersonDetailType.D_GRADE_SCHOOL, 40);
        typeSort.put(PersonDetailType.D_JUNIOR_SCHOOL, 41);
        typeSort.put(PersonDetailType.D_SENIOR_SCHOOL, 42);
        typeSort.put(PersonDetailType.D_COLLEGE, 43);
        typeSort.put(PersonDetailType.D_TECHNICAL_SCHOOL, 44);
        typeSort.put(PersonDetailType.D_JUNIOR_COLLEGE, 45);
        typeSort.put(PersonDetailType.D_MASTER_COLLEGE, 46);
        typeSort.put(PersonDetailType.D_PHD_COLLEGE, 47);
        typeSort.put(PersonDetailType.D_JOB, 48);

        basic.add(PersonDetailType.D_BIRTHDAY);
        basic.add(PersonDetailType.D_REMARK);
        basic.add(PersonDetailType.D_LUNAR_BIRTHDAY);
        basic.add(PersonDetailType.D_NICKNAME);
        basic.add(PersonDetailType.D_JOBTITLE);
        basic.add(PersonDetailType.D_EMPLOYER);
        basic.add(PersonDetailType.D_GENDAR);

        contactPhone.add(PersonDetailType.D_CELLPHONE);
        contactPhone.add(PersonDetailType.D_WORK_PHONE);
        contactPhone.add(PersonDetailType.D_HOME_PHONE);
        contactPhone.add(PersonDetailType.D_MOBILE);
        contactPhone.add(PersonDetailType.D_PRIVATE_PHONE);
        contactPhone.add(PersonDetailType.D_WORK_FAX);
        contactPhone.add(PersonDetailType.D_HOME_FAX);
        contactPhone.add(PersonDetailType.D_PAGER);
        contactPhone.add(PersonDetailType.D_SHORT_PHONE);
        contactPhone.add(PersonDetailType.D_OTHER_PHONE);

        social.add(PersonDetailType.D_HOME_PAGE);
        social.add(PersonDetailType.D_BLOG);
        social.add(PersonDetailType.D_SKYPE);
        social.add(PersonDetailType.D_FACEBOOK);
        social.add(PersonDetailType.D_TWITTER);
        social.add(PersonDetailType.D_QQ_WEIBO);
        social.add(PersonDetailType.D_RENREN);
        social.add(PersonDetailType.D_SINA_WEIBO);
        social.add(PersonDetailType.D_QQ);
        social.add(PersonDetailType.D_WEIXIN);

        email.add(PersonDetailType.D_EMAIL);
        email.add(PersonDetailType.D_ACCOUNT_EMAIL);
        email.add(PersonDetailType.D_PERSONAL_EMAIL);
        email.add(PersonDetailType.D_WORK_EMAIL);
        email.add(PersonDetailType.D_OTHER_EMAIL);

        add.add(PersonDetailType.D_WORK_ADDRESS);
        add.add(PersonDetailType.D_HOME_ADDRESS);
        add.add(PersonDetailType.D_BIRTH_PLACE);
        add.add(PersonDetailType.D_CURRENT_ADDRESS);
        add.add(PersonDetailType.D_POSTAL_ADDRESS);

        edu.add(PersonDetailType.D_OTHER_EDU);
        edu.add(PersonDetailType.D_KINDER_GARTEN);
        edu.add(PersonDetailType.D_PHD_COLLEGE);
        edu.add(PersonDetailType.D_MASTER_COLLEGE);
        edu.add(PersonDetailType.D_GRADE_SCHOOL);
        edu.add(PersonDetailType.D_JUNIOR_SCHOOL);
        edu.add(PersonDetailType.D_TECHNICAL_SCHOOL);
        edu.add(PersonDetailType.D_JUNIOR_COLLEGE);
        edu.add(PersonDetailType.D_SENIOR_SCHOOL);
        edu.add(PersonDetailType.D_COLLEGE);
        for (int i = 1; i < 10; i++) {
            basicID.add(i);
        }
        for (int i = 10; i < 20; i++) {
            contactPhoneID.add(i);
        }
        for (int i = 20; i < 25; i++) {
            emailID.add(i);
        }
        for (int i = 25; i < 30; i++) {
            addID.add(i);
        }
        for (int i = 30; i < 39; i++) {
            socialID.add(i);
        }
        for (int i = 39; i < 48; i++) {
            eduID.add(i);
        }
        workID.add(48);
    }

    public static Comparator<Info> getComparator() {
        return new Comparator<Info>() {
            @Override
            public int compare(Info l, Info r) {
                Integer lSort = l.getSortKey(), rSort = r.getSortKey();
                return lSort > rSort ? 1 : -1;
            }
        };

    }

}
