package com.changlianxi.data;

import android.database.sqlite.SQLiteDatabase;

public interface IData {

    public void read(SQLiteDatabase db);

    public void write(SQLiteDatabase db);

    public void update(IData data);
}
