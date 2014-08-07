package com.changlianxi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.Global;

/**
 * sqlist db helper
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_PREFIX = "clx";
    // private static final int DATABASE_VERSION_2 = 2;
    private static final int DATABASE_VERSION_3 = 3;
    private static final int DATABASE_VERSION_4 = 4;
    private static final int DATABASE_VERSION = DATABASE_VERSION_4;
    private static DataBaseHelper instance;

    public DataBaseHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    public static DataBaseHelper getInstance() {

        return getInstance(CLXApplication.getInstance());
    }

    public static DataBaseHelper getInstance(Context context) {
        String uid = Global.getUid();
        return getInstance(context, uid);
    }

    public static DataBaseHelper getInstance(Context context, String postfix) {
        if (instance == null) {
            instance = new DataBaseHelper(context, DATABASE_PREFIX + postfix,
                    null, DATABASE_VERSION);

        }
        return instance;
    }

    public static void setIinstanceNull() {
        instance = null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDB(db);
    }

    private void createDB(SQLiteDatabase db) {
        // circles
        db.execSQL("create table IF NOT EXISTS " + Const.CIRCLE_TABLE_NAME
                + "( " + Const.CIRCLE_TABLE_STRUCTURE + " )");
        // circle roles
        db.execSQL("create table IF NOT EXISTS " + Const.CIRCLE_ROLE_TABLE_NAME
                + "(" + Const.CIRCLE_ROLE_TABLE_STRUCTURE + ")");
        // circle members
        db.execSQL("create table IF NOT EXISTS "
                + Const.CIRCLE_MEMBER_TABLE_NAME + "("
                + Const.CIRCLE_MEMBER_TABLE_STRUCTURE + ")");
        // time record
        db.execSQL("create table IF NOT EXISTS " + Const.TIME_RECORD_TABLE_NAME
                + "(" + Const.TIME_RECORD_TABLE_STRUCTURE + ")");
        // person details1
        db.execSQL("create table IF NOT EXISTS "
                + Const.PERSON_DETAIL_TABLE_NAME1 + "("
                + Const.PERSON_DETAIL_TABLE_STRUCTURE1 + ")");
        // growths
        db.execSQL("create table IF NOT EXISTS " + Const.GROWTH_TABLE_NAME
                + "(" + Const.GROWTH_TABLE_STRUCTURE + ")");
        // growth images
        db.execSQL("create table IF NOT EXISTS "
                + Const.GROWTH_IMAGE_TABLE_NAME + "("
                + Const.GROWTH_IMAGE_TABLE_STRUCTURE + ")");
        // growth comments
        db.execSQL("create table IF NOT EXISTS "
                + Const.GROWTH_COMMENT_TABLE_NAME + "("
                + Const.GROWTH_COMMENT_TABLE_STRUCTURE + ")");
        // chat partners
        db.execSQL("create table IF NOT EXISTS "
                + Const.CHAT_PARTNER_TABLE_NAME + "("
                + Const.CHAT_PARTNER_TABLE_STRUCTURE + ")");
        // person chat
        db.execSQL("create table IF NOT EXISTS " + Const.PERSON_CHAT_TABLE_NAME
                + "(" + Const.PERSON_CHAT_TABLE_STRUCTURE + ")");
        // dynamics
        db.execSQL("create table IF NOT EXISTS "
                + Const.CIRCLE_DYNAMIC_TABLE_NAME + "("
                + Const.CIRCLE_DYNAMIC_TABLE_STRUCTURE + ")");
        // amendments
        db.execSQL("create table IF NOT EXISTS " + Const.AMENDMENT_TABLE_NAME
                + "(" + Const.AMENDMENT_TABLE_STRUCTURE + ")");
        // my info
        db.execSQL("create table IF NOT EXISTS " + Const.MYINFO_TABLE_NAME
                + "(" + Const.MYINFO_TABLE_STRUCTURE + ")");

        // growth album images
        db.execSQL("create table IF NOT EXISTS "
                + Const.GROWTH_ALBUM_IMAGE_TABLE_NAME + "("
                + Const.GROWTH_ALBUM_IMAGE_TABLE_STRUCTURE + ")");

        // growth album
        db.execSQL("create table IF NOT EXISTS "
                + Const.GROWTH_ALBUM_TABLE_NAME + "("
                + Const.GROWTH_ALBUM_TABLE_STRUCTURE + ")");

        // growth album
        db.execSQL("create table IF NOT EXISTS "
                + Const.ALBUM_GROWTH_TABLE_NAME + "("
                + Const.ALBUM_GROWTH_TABLE_STRUCTURE + ")");
        // circle groups
        db.execSQL("create table IF NOT EXISTS "
                + Const.CIRCLE_GROUP_TABLE_NAME + "("
                + Const.CIRCLE_GROUP_TABLE_STRUCTURE + ")");
        // circle members groups
        db.execSQL("create table IF NOT EXISTS "
                + Const.CIRCLE_MEMBERS_GROUPS_TABLE_NAME + "("
                + Const.CIRCLE_MEMBERS_GROUPS_STRUCTURE + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete old tables
        if (oldVersion <= DATABASE_VERSION_3) {
            deleteOldDB(db);
        }

        // create new tables
        if (newVersion == DATABASE_VERSION_4) {
            createDB(db);
        }
    }

    private void deleteOldDB(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Const.CIRCLE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.CIRCLE_ROLE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.CIRCLE_MEMBER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.PERSON_DETAIL_TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS " + Const.TIME_RECORD_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.GROWTH_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.GROWTH_IMAGE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.GROWTH_COMMENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.CIRCLE_CHAT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.PERSON_CHAT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.CHAT_PARTNER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.CIRCLE_DYNAMIC_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.AMENDMENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.MYINFO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "
                + Const.GROWTH_ALBUM_IMAGE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.GROWTH_ALBUM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.ALBUM_GROWTH_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Const.CIRCLE_GROUP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "
                + Const.CIRCLE_MEMBERS_GROUPS_TABLE_NAME);

    }

}
