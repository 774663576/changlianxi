package com.changlianxi.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作类
 * 
 * @author teeker_bin
 * 
// * @deprecated will be replaced
 * 
 */
public class DBUtils {
    public static DataBaseHelper dbase = DataBaseHelper.getInstance();
    public static SQLiteDatabase dbs = dbase.getReadableDatabase();

    /**
     * 1 表示读  2表示写
     * @param flagType
     * @return
     */
    public synchronized static SQLiteDatabase getDBsa(int flagType) {
        if (dbase == null) {
            dbase = DataBaseHelper.getInstance();
        }
        if (!dbs.isOpen()) {
            if (flagType == 1) {
                dbs = dbase.getReadableDatabase();
            } else {
                dbs = dbase.getWritableDatabase();
            }
        }
        return dbs;
    }

    public static void close() {
        dbase = null;
        if (dbs != null) {
            dbs.close();
        }
    }

}
