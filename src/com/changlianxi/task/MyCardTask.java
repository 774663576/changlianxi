package com.changlianxi.task;

import android.database.sqlite.SQLiteDatabase;

import com.changlianxi.data.MyCard;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class MyCardTask extends BaseAsyncTask<MyCard, Void, RetError> {
    private MyCard card;

    @Override
    protected RetError doInBackground(MyCard... params) {
        card = params[0];
        card.read(DBUtils.getDBsa(1));
        card.readDetails(DBUtils.getDBsa(1));
        callBack.readDBFinish();
        if (!isNet) {
            return RetError.NETWORK_ERROR;
        }
        RetError refresh = card.refresh(false);
        SQLiteDatabase db = DBUtils.getDBsa(2);
        db.beginTransaction();
        try {
            card.write(DBUtils.getDBsa(2));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return refresh;

    }
}
