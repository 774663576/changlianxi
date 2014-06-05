package com.changlianxi.task;

import com.changlianxi.data.PersonChatList;
import com.changlianxi.data.enums.RetError;

public class PersonChatListTast extends
        BaseAsyncTask<PersonChatList, Void, RetError> {
    private PersonChatList chatList = null;
    private long startTime = 0l;
    private long endTime = 0l;

    public PersonChatListTast(long sTime, long eTime) {
        this.startTime = sTime;
        this.endTime = eTime;

    }

    @Override
    protected RetError doInBackground(PersonChatList... params) {
        chatList = params[0];
        RetError ret = chatList.refresh(startTime, endTime - 1);
        return ret;
    }
}
