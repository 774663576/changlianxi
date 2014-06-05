package com.changlianxi.inteface;

import com.changlianxi.data.enums.RetError;

/**
 * 发送私信和聊天回调接口
 * 
 * @author teeker_bin
 * 
 */
public interface SendMessageAndChatCallBack {

    void getRetError(RetError ret);
}
