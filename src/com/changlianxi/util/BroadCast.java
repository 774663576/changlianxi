package com.changlianxi.util;

import android.content.Context;
import android.content.Intent;

/**
 * 广播发送消息类
 * 
 * @author teeker_bin
 * 
 */
public class BroadCast {
	/**
	 * 发送空值的广播
	 * 
	 * @param context
	 * @param action
	 */
	public static void sendBroadCast(Context context, String action) {
		Intent mIntent = new Intent(action);
		context.sendBroadcast(mIntent);
	}

	/**
	 * 发送带值广播
	 * 
	 * @param context
	 * @param intent
	 */
	public static void sendBroadCast(Context context, Intent intent) {
		context.sendBroadcast(intent);
	}
}
