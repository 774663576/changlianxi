package com.changlianxi.task;

import com.changlianxi.data.PersonChat;
import com.changlianxi.data.enums.RetError;

/**
 * 向公共账号发送私信线程
 * 
 * @author teeker_bin
 * 
 */
public class SendPulbicMessageThread extends Thread {
    private PersonChat pChat = null;
    private GetPublicResponse callBack;

    public interface GetPublicResponse {
        void publicResponse(String response);
    }

    public void setCallBack(GetPublicResponse callBack) {
        this.callBack = callBack;
    }

    public SendPulbicMessageThread(PersonChat pChat) {
        this.pChat = pChat;

    }

    public void setpChat(PersonChat pChat) {
        this.pChat = pChat;
    }

    public void run() {
        RetError ret = pChat.sendPublicText();
        callBack.publicResponse(pChat.getResponse());

    }
}
