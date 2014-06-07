package com.changlianxi.task;

import com.changlianxi.data.PersonChatList;
import com.changlianxi.data.enums.RetError;

public class PublicMessageChatListTask extends
        BaseAsyncTask<PersonChatList, Void, RetError> {
    private PersonChatList chatList = null;
    private long startTime = 0l;
    private long endTime = 0l;

    public PublicMessageChatListTask(long sTime, long eTime) {
        this.startTime = sTime;
        this.endTime = eTime;

    }

    @Override
    protected RetError doInBackground(PersonChatList... params) {
        chatList = params[0];
        RetError ret = chatList.refreshPublic(startTime, endTime - 1);
        return ret;
    }
}
