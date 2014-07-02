package com.changlianxi.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.view.WindowManager;

import com.changlianxi.LoginActivity;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.Global;
import com.changlianxi.db.DBUtils;
import com.changlianxi.db.DataBaseHelper;
import com.changlianxi.inteface.ConfirmDialog;

/**
 * 解析推送过来的消息
 * 
 * @author teeker_bin
 * 
 */
public class ResolutionPushJson {
    public static final String COMMENT_TYPE = "GROWTH_COMMENT";// 成长评论推送
    public static final String NEW_TYPE = "NEW_NEWS";// 新的动态推送
    public static final String GROWTH_TYPE = "NEW_GROWTH";// 新的成长推送
    public static final String QUIT_TYPE = "FORCE_QUIT";// 第三方登录推送
    public static final String NEW_CIRCLE = "NEW_CIRCLE";// 新圈子邀请推送
    public static final String MESSAGE = "MESSAGE";// 私信
    public static final String KICKOUT = "KICKOUT_NOTICE";// 踢出圈子提醒
    public static final String TYPE_MY_EDIT = "MY_EDIT";// 个人圈子信息修改
    public static final String TYPE_MEMBER_UPDATE = "MEMBER_UPDATE";// 个人圈子信息修改

    public static void resolutionJson(String jsonStr) {
        String alert = "";
        String uid = "";
        String unRead = "";
        int cid = 0;
        try {
            JSONObject json = new JSONObject(jsonStr);
            String type = json.getString("t");
            uid = json.getString("uid");
            if (type.equals(QUIT_TYPE)) {// 强制退出
                alert = json.getString("alert");
                if (Global.getUid().equals(uid)) {
                    showNotify(alert, type);
                }
                return;
            }
            cid = json.getInt("cid");
            if (KICKOUT.equals(type)) {// 踢出圈子
                alert = json.getString("alert");
                Intent intent = new Intent(Constants.KICKOUT_CIRCLE);
                intent.putExtra("cid", cid);
                BroadCast.sendBroadCast(CLXApplication.getInstance(), intent);
                showNotify(alert, type);
            }
            if (uid.equals(Global.getUid())) {
                return;
            }
            if (type.equals(COMMENT_TYPE)) {// 成长评论
                unRead = json.getString("unread");
                upDateCirclePromptCount(cid, unRead);
            } else if (type.equals(GROWTH_TYPE)) {// 新的成长
                unRead = json.getString("unread");
                upDateCirclePromptCount(cid, unRead);
            } else if (type.equals(NEW_TYPE)) {// 新的动态
                unRead = json.getString("unread");
                upDateCirclePromptCount(cid, unRead);
            } else if (NEW_CIRCLE.equals(type)) {// 新圈子邀请
                alert = json.getString("alert");
                BroadCast.sendBroadCast(CLXApplication.getInstance(),
                        Constants.REFRESH_CIRCLE_LIST);
                showNotify(alert, type);

            } else if (MESSAGE.equals(type)) {// 新的私信
                alert = json.getString("alert");
                setPromptInDB(type);
                BroadCast.sendBroadCast(CLXApplication.getInstance(),
                        Constants.LEFT_MENU_MESSAGE_PROMPT);
            } else if (TYPE_MEMBER_UPDATE.equals(type)) {// 成员更新
                unRead = json.getString("unread");
                upDateCirclePromptCount(cid, unRead);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void setPromptInDB(String type) {
        Global g = new Global();
        g.read(DBUtils.getDBsa(1));
        if (MESSAGE.equals(type)) {
            g.setNewPersonChatNum(1);
        }
        g.write(DBUtils.getDBsa(2));
    }

    private static void finish() {
        SharedUtils.setString("uid", "");
        SharedUtils.setString("token", "");
        BaiDuPushUtils.setBind(CLXApplication.getInstance(), false);
        DataBaseHelper.setIinstanceNull();
        DBUtils.dbase = null;
        DBUtils.close();
        CLXApplication.exit(false);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(CLXApplication.getInstance(), LoginActivity.class);
        CLXApplication.getInstance().startActivity(intent);

    }

    private static void upDateCirclePromptCount(int cid, String unread) {
        Intent intent = new Intent(Constants.UPDETE_CIRCLE_PROMPT_COUNT);
        intent.putExtra("cid", cid);
        intent.putExtra("unread", unread);
        BroadCast.sendBroadCast(CLXApplication.getInstance(), intent);
    }

    private static void showNotify(String alert, String type) {
        if (Utils.isTopActivity(CLXApplication.getInstance())) {
            Utils.showNotify(alert, type);
            if (type.equals(QUIT_TYPE)) {
                SharedUtils.setString("uid", "");
                SharedUtils.setString("token", "");
                BaiDuPushUtils.setBind(CLXApplication.getInstance(), false);
                DataBaseHelper.setIinstanceNull();
                DBUtils.dbase = null;
                DBUtils.close();
                CLXApplication.exit(false);
            }
        } else {
            if (type.equals(QUIT_TYPE)) {
                editDialog();
            }
        }
    }

    private static void editDialog() {
        Dialog dialog = DialogUtil.promptDialog(CLXApplication.getInstance(),
                "您的账号已在其他设备登录，请重新登录!", "确定", new ConfirmDialog() {
                    @Override
                    public void onOKClick() {
                        finish();
                    }

                    @Override
                    public void onCancleClick() {

                    }
                });
        dialog.getWindow()
                .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }
}
