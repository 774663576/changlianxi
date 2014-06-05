package com.changlianxi.data;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.db.Const;
import com.changlianxi.util.SharedUtils;

public class Global extends AbstractData {

    public static final String NEW_CIRCLE_NUM = "newCircles";
    public static final String NEW_PERSONCHAT_NUM = "newPersonChats";
    public static final String NEW_AMENDMENTS_NUM = "newAmendments";

    private Map<String, Integer> messages = new HashMap<String, Integer>();

    @Override
    public void read(SQLiteDatabase db) {
        String dbName = Const.MYINFO_TABLE_NAME;
        Cursor cursor = db.query(dbName, new String[] { "type", "count" },
                null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String type = cursor.getString(cursor.getColumnIndex("type"));
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                this.messages.put(type, count);
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            cursor.close();

            ContentValues cv = new ContentValues();
            cv.put("type", NEW_CIRCLE_NUM);
            cv.put("count", 0);
            db.insert(dbName, null, cv);
            cv = new ContentValues();
            cv.put("type", NEW_PERSONCHAT_NUM);
            cv.put("count", 0);
            db.insert(dbName, null, cv);
            cv = new ContentValues();
            cv.put("type", NEW_AMENDMENTS_NUM);
            cv.put("count", 0);
            db.insert(dbName, null, cv);
        }

        this.status = Status.OLD;
    }

    @Override
    public void write(SQLiteDatabase db) {
        String dbName = Const.MYINFO_TABLE_NAME;
        for (String type : messages.keySet()) {
            int count = messages.get(type);

            ContentValues cv = new ContentValues();
            cv.put("type", type);
            cv.put("count", count);
            int affected = db.update(dbName, cv, "type=?",
                    new String[] { type });
            if (affected == 0) {
                db.insert(dbName, null, cv);
            }
        }
    }

    public int getNewCircleNum() {
        return messages.containsKey(NEW_CIRCLE_NUM) ? messages
                .get(NEW_CIRCLE_NUM) : 0;
    }

    public void setNewCircleNum(int num) {
        if (messages.containsKey(NEW_CIRCLE_NUM)) {
            if (messages.get(NEW_CIRCLE_NUM) != num) {
                messages.put(NEW_CIRCLE_NUM, num);
                this.status = Status.UPDATE;
            }
        } else {
            if (num > 0) {
                messages.put(NEW_CIRCLE_NUM, num);
                this.status = Status.UPDATE;
            }
        }
    }

    public int getNewPersonChatNum() {
        return messages.containsKey(NEW_PERSONCHAT_NUM) ? messages
                .get(NEW_PERSONCHAT_NUM) : 0;
    }

    public void setNewPersonChatNum(int num) {
        if (messages.containsKey(NEW_PERSONCHAT_NUM)) {
            if (messages.get(NEW_PERSONCHAT_NUM) != num) {
                messages.put(NEW_PERSONCHAT_NUM, num);
                this.status = Status.UPDATE;
            }
        } else {

            if (num > 0) {
                messages.put(NEW_CIRCLE_NUM, num);
                this.status = Status.UPDATE;
            }
        }
    }

    public int getNewAmendmentsNum() {
        return messages.containsKey(NEW_AMENDMENTS_NUM) ? messages
                .get(NEW_AMENDMENTS_NUM) : 0;
    }

    public void setNewAmendmentsNum(int num) {
        if (messages.containsKey(NEW_AMENDMENTS_NUM)) {
            if (messages.get(NEW_AMENDMENTS_NUM) != num) {
                messages.put(NEW_AMENDMENTS_NUM, num);
                this.status = Status.UPDATE;
            }
        } else {
            if (num > 0) {
                messages.put(NEW_AMENDMENTS_NUM, num);
                this.status = Status.UPDATE;
            }
        }
    }

    public static String getUid() {
        return SharedUtils.getString(SharedUtils.SP_UID, "");
    }

    public static int getIntUid() {
        String uid = getUid();
        if (uid.length() > 0) {
            return Integer.parseInt(uid);
        }
        return 0;
    }

    public static String getUserToken() {
        return SharedUtils.getString(SharedUtils.SP_TOKEN, "");
    }
}
