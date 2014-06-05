package com.changlianxi.task;

import com.changlianxi.data.MyCard;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;

public class ReadMyCardTask extends BaseAsyncTask<MyCard, Void, RetError> {
    private MyCard card;

    @Override
    protected RetError doInBackground(MyCard... params) {
        card = params[0];
        card.readMyCard(DBUtils.getDBsa(1));
        card.readDetails(DBUtils.getDBsa(1));
        return RetError.NONE;

    }
}
