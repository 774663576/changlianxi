package com.changlianxi.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;

public class SaveContactsToPhone {
    /**
     * 保存到已有联系人
     * 
     */
    public static void saveToExistingContacts(Context context,
            String home_address, String work_address, String username,
            String cellPhone, String workPhone, String homePhone,
            String commonlyUsed_email, String home_email, String work_email,
            String compony, String title) {
        String address = "";
        if (!"".equals(home_address)) {
            address = home_address;
        } else {
            address = work_address;
        }
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType("vnd.android.cursor.item/contact");
        // 姓名
        intent.putExtra(Intents.Insert.NAME, username);
        // 电话、电话类型
        intent.putExtra(Intents.Insert.PHONE, cellPhone);
        intent.putExtra(Intents.Insert.PHONE_TYPE, "常用电话");
        intent.putExtra(Intents.Insert.SECONDARY_PHONE, homePhone);
        intent.putExtra(Intents.Insert.SECONDARY_PHONE_TYPE, "家庭电话");
        intent.putExtra(Intents.Insert.TERTIARY_PHONE, workPhone);
        intent.putExtra(Intents.Insert.TERTIARY_PHONE_TYPE, "工作电话");
        // email
        intent.putExtra(Intents.Insert.EMAIL, commonlyUsed_email);
        intent.putExtra(Intents.Insert.EMAIL_TYPE, "常用邮箱");
        intent.putExtra(Intents.Insert.SECONDARY_EMAIL, home_email);
        intent.putExtra(Intents.Insert.SECONDARY_EMAIL_TYPE, "个人邮箱");
        intent.putExtra(Intents.Insert.TERTIARY_EMAIL, work_email);
        intent.putExtra(Intents.Insert.TERTIARY_EMAIL_TYPE, "工作邮箱");
        // 住址
        intent.putExtra(Intents.Insert.POSTAL, address);
        intent.putExtra(Intents.Insert.POSTAL_TYPE, "地址");
        // 公司、职位
        intent.putExtra(Intents.Insert.COMPANY, compony);
        intent.putExtra(Intents.Insert.JOB_TITLE, title);
        context.startActivity(intent);
    }

    /**
    * 新建联系人
    * 
    */
    public static void newContacts(Context context, String home_address,
            String work_address, String username, String cellPhone,
            String workPhone, String homePhone, String commonlyUsed_email,
            String home_email, String work_email, String compony, String title) {
        String address = "";
        if (!"".equals(home_address)) {
            address = home_address;
        } else {
            address = work_address;
        }
        Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
        Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        // 姓名
        intent.putExtra(Intents.Insert.NAME, username);
        // 电话、电话类型
        intent.putExtra(Intents.Insert.PHONE, cellPhone);
        intent.putExtra(Intents.Insert.PHONE_TYPE, "常用电话");
        intent.putExtra(Intents.Insert.SECONDARY_PHONE, homePhone);
        intent.putExtra(Intents.Insert.SECONDARY_PHONE_TYPE, "家庭电话");
        intent.putExtra(Intents.Insert.TERTIARY_PHONE, workPhone);
        intent.putExtra(Intents.Insert.TERTIARY_PHONE_TYPE, "工作电话");
        // email
        intent.putExtra(Intents.Insert.EMAIL, commonlyUsed_email);
        intent.putExtra(Intents.Insert.EMAIL_TYPE, "常用邮箱");
        intent.putExtra(Intents.Insert.SECONDARY_EMAIL, home_email);
        intent.putExtra(Intents.Insert.SECONDARY_EMAIL_TYPE, "个人邮箱");
        intent.putExtra(Intents.Insert.TERTIARY_EMAIL, work_email);
        intent.putExtra(Intents.Insert.TERTIARY_EMAIL_TYPE, "工作邮箱");
        // 住址
        intent.putExtra(Intents.Insert.POSTAL, address);
        intent.putExtra(Intents.Insert.POSTAL_TYPE, "地址");
        // 公司、职位
        intent.putExtra(Intents.Insert.COMPANY, compony);
        intent.putExtra(Intents.Insert.JOB_TITLE, title);
        context.startActivity(intent);
    }

}
