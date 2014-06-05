package com.changlianxi.task;

import com.changlianxi.data.ChatPartnerList;
import com.changlianxi.data.enums.RetError;

public class MessageListTask extends
        BaseAsyncTask<ChatPartnerList, Void, RetError> {
    private ChatPartnerList list;

    @Override
    protected RetError doInBackground(ChatPartnerList... params) {
        list = params[0];
        if (!isNet) {
            return RetError.NONE;
        }
        list.refresh(list.getLastReqTime());
        return RetError.NONE;
    }

}
