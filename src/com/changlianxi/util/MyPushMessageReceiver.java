package com.changlianxi.util;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.changlianxi.R;
import com.changlianxi.WelcomeActivity;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.Circle;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.PersonChat;
import com.changlianxi.inteface.PushMessages;
import com.changlianxi.inteface.PushOnBind;

/**
 * Push消息处理receiver。请编写您需要的回调函数，
 * 一般来说：
 * onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息；
 * onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调；
 * onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下：
 * 0 - Success
 * 10001 - Network Problem
 * 30600 - Internal Server Error
 * 30601 - Method Not Allowed
 * 30602 - Request Params Not Valid
 * 30603 - Authentication Failed
 * 30604 - Quota Use Up Payment Required
 * 30605 - Data Required Not Found
 * 30606 - Request Time Expires Timeout
 * 30607 - Channel Token Timeout
 * 30608 - Bind Relation Not Found
 * 30609 - Bind Number Too Many
 * 
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 * 
 */
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {

    private static PushOnBind pushBind;
    public static PushMessages pushMessage;
    public static int mNewNum = 0;// 通知栏新消息条目
    public static final int NOTIFY_ID = 0x000;

    /**
     * 调用PushManager.startWork后，sdk将对push server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。
     * 如果您需要用单播推送，需要把这里获取的channel id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     * 
     * @param context
     *          BroadcastReceiver的执行Context
     * @param errorCode
     *          绑定接口返回值，0 - 成功
     * @param appid 
     *          应用id。errorCode非0时为null
     * @param userId
     *          应用user id。errorCode非0时为null
     * @param channelId
     *          应用channel id。errorCode非0时为null
     * @param requestId
     *          向服务端发起的请求id。在追查问题时有用；
     * @return
     *     none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
        // 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
        if (errorCode == 0) {
            BaiDuPushUtils.setBind(context, true);
            // Utils.showToast("百度推送绑定成功", Toast.LENGTH_SHORT);
            if (pushBind != null) {
                pushBind.onBind(channelId, userId);
            }
        } else {
            // Utils.showToast("百度推送绑定失败", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 接收透传消息的函数。
     * 
     * @param context 上下文
     * @param message 推送的消息
     * @param customContentString 自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        resolutionJson(message);

    }

    /**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context 上下文
     * @param title 推送的通知的标题
     * @param description 推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
    }

    /**
     * PushManager.stopWork() 的回调函数。
     * 
     * @param context 上下文
     * @param errorCode 错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        // 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
            BaiDuPushUtils.setBind(context, false);
        }

    }

    /**
     * 解析接受的json字符 确定是聊天信息还是私信信息
     * 
     * @param strJson
     */
    private void resolutionJson(String strJson) {
        if (strJson == null || "".equals(strJson)) {
            return;
        }

        try {
            JSONObject json = new JSONObject(strJson);
            String type = json.getString("t");
            boolean isBackHome = Utils.isTopActivity(CLXApplication
                    .getInstance());
            if (type.equals("MESSAGE")) {
                if (pushMessage == null) {
                    if (isBackHome) {
                        showNotify(strJson);
                    }
                    ResolutionPushJson.resolutionJson(strJson);
                } else {
                    pushMessage.getPushMessages(strJson);
                }
            } else {
                ResolutionPushJson.resolutionJson(strJson);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void showNotify(String message) {
        PersonChat chat = ChatParser.getChatModle(message);
        if (chat == null) {// 自己发送的消息不谈
            return;
        }
        mNewNum++;
        // 更新通知栏
        CLXApplication application = CLXApplication.getInstance();
        int icon = R.drawable.app_icon;
        long when = System.currentTimeMillis();
        Circle c = new Circle(chat.getCid());
        String circleName = c.getName();
        Notification notification = new Notification(icon, circleName, when);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        // 设置默认声音
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.contentView = null;
        Intent intent = new Intent(application, WelcomeActivity.class);
        intent.putExtra("ruid", chat.getPartner());
        intent.putExtra("cid", chat.getCid());
        CircleMember m = new CircleMember(chat.getCid(), 0, chat.getPartner());
        PendingIntent contentIntent = PendingIntent.getActivity(application, 0,
                intent, 0);
        notification.setLatestEventInfo(CLXApplication.getInstance(),
                (CharSequence) m.getName() + " (" + mNewNum + "条新消息)",
                (CharSequence) chat.getContent(), contentIntent);
        application.getNotificationManager().notify(NOTIFY_ID, notification);// 通知一下才会生效哦
    }

    public static void setPushMessageCallBack(PushMessages push) {
        pushMessage = push;

    }

    public static void setPushOnBind(PushOnBind bind) {
        pushBind = bind;
    }

    @Override
    public void onDelTags(Context arg0, int arg1, List<String> arg2,
            List<String> arg3, String arg4) {

    }

    @Override
    public void onListTags(Context arg0, int arg1, List<String> arg2,
            String arg3) {

    }

    @Override
    public void onSetTags(Context arg0, int arg1, List<String> arg2,
            List<String> arg3, String arg4) {

    }
}
