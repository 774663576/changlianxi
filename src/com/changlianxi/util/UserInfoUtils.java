package com.changlianxi.util;

/**
 * �û���ϸ���Ϲ�����
 * 
 * @author teeker_bin
 * 
 */
public class UserInfoUtils {
    /* ������� */
    public static final String infoTitleKey[] = { "������Ϣ", "��ϵ�绰", "��������",
            "�罻�˺�", "��ַ��Ϣ", "��������", "��������" };
    /* �������ݶ԰���������key "D_NAME", */
    public static final String basicStr[] = { "D_NAME", "D_EMPLOYER",
            "D_JOBTITLE", "D_BIRTHDAY", "D_GENDAR", "D_NICKNAME", "D_REMARK" };

    /* ��������key����Ӧ���������� "����", */
    public static final String basicChineseStr[] = { "����", "��λ", "ְλ", "����",
            "�Ԅe" , "�ǳ�", "��ע"};
    public static final String basicUserStr[] = { "D_NAME", "D_EMPLOYER",
            "D_JOBTITLE", "D_BIRTHDAY", "D_GENDAR", "D_NICKNAME", "D_REMARK" };
    public static final String basicUserStr2[] = { "D_NAME", "D_EMPLOYER",
        "D_JOBTITLE", "D_BIRTHDAY", "D_GENDAR", "D_NICKNAME", "D_REMARK" ,"D_AVATAR"};

    /* ��������key����Ӧ���������� "����", */
    public static final String basicUserChineseStr[] = { "����", "��λ", "ְλ",
            "����", "�Ԅe", "�ǳ�", "��ע" };
    public static final String basicUserChineseStr2[] = { "����", "��λ", "ְλ",
        "����", "�Ԅe", "�ǳ�", "��ע", "ͷ��" };
    /* ��ϵ��ʽ���ݶ԰���������key */
    public static final String contactPhone[] = { "D_CELLPHONE", "D_MOBILE",
            "D_WORK_PHONE", "D_HOME_PHONE", "D_PRIVATE_PHONE", "D_WORK_FAX",
            "D_HOME_FAX", "D_PAGER", "D_SHORT_PHONE", "D_OTHER_PHONE" };
    /* ��ϵ��ʽ����key����Ӧ���������� */
    public static final String contactPhoneChinesetStr[] = { "�ֻ���", "���õ绰",
            "�����绰", "סլ�绰", "˽�˵绰", "��������", "סլ����", "����", "�̺���", "��������" };

    /*��������key*/
    public static final String emailStr[] = { "D_EMAIL", "D_WORK_EMAIL",
            "D_PERSONAL_EMAIL", "D_OTHER_EMAIL" };
    /*������������key*/
    public static final String emailChineseStr[] = { "��������", "��������", "��������",
            "��������" };

    /* �˺����ݶ԰���������key */
    public static final String socialStr[] = { "D_WEIXIN", "D_QQ",
            "D_SINA_WEIBO", "D_RENREN", "D_QQ_WEIBO", "D_BLOG", "D_FACEBOOK",
            "D_TWITTER", "D_SKYPE" };
    /* �˺�����key����Ӧ���������� */
    public static final String socialChineseStr[] = { "΢��", "QQ", "����΢��",
            "������", "��Ѷ΢��", "���˲���", "FaceBook", "Twitter", "Skype" };
    /* ��ַ���ݶ԰���������key */
    public static final String addressStr[] = { "D_BIRTH_PLACE",
            "D_HOME_ADDRESS", "D_WORK_ADDRESS" };
    /* ��ַ����key����Ӧ���������� */
    public static final String addressChineseStr[] = { "����", "��ס��ַ", "������ַ" };
    /* �������ݶ԰���������key */
    /* ��������key����Ӧ���������� */
    /* ������������������key */
    public static final String eduStr[] = { "D_KINDER_GARTEN",
            "D_GRADE_SCHOOL", "D_JUNIOR_SCHOOL", "D_SENIOR_SCHOOL",
            "D_COLLEGE", "D_TECHNICAL_SCHOOL", "D_JUNIOR_COLLEGE",
            "D_MASTER_COLLEGE", "D_PHD_COLLEGE" };
    /* ����key����Ӧ���������� */
    public static final String eduChinesStr[] = { "�׶�԰", "Сѧ", "����", "����",
            "��ѧ", "��ר", "��ר", "˶ʿ", "��ʿ" };
    /* ��������������������key */
    public static final String workStr[] = { "D_JOB" };
    /* ����key����Ӧ���������� */
    public static final String workChineseStr[] = { "����" };
    /* ��������type */
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
    public static final String infoKeyChinese[] = { "����", "��λ", "ְλ", "����",
            "�Ԅe", "�ǳ�", "��ע", "�ֻ���", "���õ绰", "�����绰", "סլ�绰", "˽�˵绰", "��������",
            "סլ����", "����", "�̺���", "��������", "��������", "��������", "��������", "��������", "΢��",
            "QQ", "����΢��", "������", "��Ѷ΢��", "���˲���", "FaceBook", "Twitter",
            "Skype", "����", "��ס��ַ", "������ַ", "�׶�԰", "Сѧ", "����", "����", "��ѧ", "��ר",
            "��ר", "˶ʿ", "��ʿ", "����" };
    public static final String infoKeyChinese2[] = { "����", "��λ", "ְλ", "����","ͷ��",
        "�Ԅe", "�ǳ�", "��ע", "�ֻ���", "���õ绰", "�����绰", "סլ�绰", "˽�˵绰", "��������",
        "סլ����", "����", "�̺���", "��������", "��������", "��������", "��������", "��������", "΢��",
        "QQ", "����΢��", "������", "��Ѷ΢��", "���˲���", "FaceBook", "Twitter",
        "Skype", "����", "��ס��ַ", "������ַ", "�׶�԰", "Сѧ", "����", "����", "��ѧ", "��ר",
        "��ר", "˶ʿ", "��ʿ", "����" };

    /**
     * ��Ӣ��keyװ��Ϊ����key
     * 
     * @param key
     *            Ҫת����key
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
     * ������keyװ��ΪӢ��key
     * 
     * @param key
     *            Ҫת����key
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
