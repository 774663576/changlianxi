package com.changlianxi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.changlianxi.applation.CLXApplication;

/**
 * * SharedPreferences 的公具类
 * 
 * @author teeker_bin
 * 
 */
public class SharedUtils {
    private static final String SP_NAME = "clx";
    private static SharedPreferences sharedPreferences = CLXApplication
            .getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    private static Editor editor = sharedPreferences.edit();
    public static final String SP_UID = "uid";
    public static final String SP_TOKEN = "token";
    public static final String SP_BPUSH_CHANNEL_ID = "bpush_channel_id";
    public static final String SP_BPUSH_USER_ID = "bpush_user_id";

    public static String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();

    }

    public static long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);

    }

    public static void setLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public static void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public static void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    // public static void setChannelID(String value) {
    // setString(SP_CHANNEL_ID, value);
    // }
    //
    // public static void setUserID(String value) {
    // setString(SP_UID, value);
    //
    // }
    //
    // public static String getChannelID() {
    // return getString("channel_id", "");
    // }
    //
    // public static String getUserID() {
    // return getString("user_id", "");
    // }
}
