package com.changlianxi.task;

import com.changlianxi.data.PersonChat;
import com.changlianxi.data.enums.RetError;
import com.changlianxi.db.DBUtils;
import com.changlianxi.inteface.SendMessageAndChatCallBack;

/**
 * 私信和聊天线程
 * 
 * @author teeker_bin
 * 
 */
public class SendMessageThread extends Thread {
    public boolean running = true;
    private SendMessageAndChatCallBack callBack;
    private PersonChat pChat = null;
    public boolean send = false;

    public SendMessageThread(PersonChat pChat) {
        this.pChat = pChat;

    }

    public void setpChat(PersonChat pChat) {
        this.pChat = pChat;
    }

    public void setMessageAndChatCallBack(SendMessageAndChatCallBack callBack) {
        this.callBack = callBack;
    }

    public void setRun(boolean runing) {
        this.running = runing;
    }

    public void run() {
        // while (running) {
        // if (send) {
        RetError ret = pChat.sendText();
        if (ret == RetError.NONE) {
            pChat.write(DBUtils.getDBsa(2));
        } else {
            // callBack.getRetError(ret);
        }
        // send = false;
        // }
        // }
    }
}
