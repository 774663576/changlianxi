package com.changlianxi.util;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.changlianxi.LoginActivity;
import com.changlianxi.R;
import com.changlianxi.UserInfoActivity;
import com.changlianxi.WelcomeActivity;
import com.changlianxi.applation.CLXApplication;
import com.changlianxi.data.CircleMember;
import com.changlianxi.data.Growth;
import com.changlianxi.data.GrowthAlbumImages;
import com.changlianxi.db.DBUtils;
import com.changlianxi.showBigPic.GrowthImagePagerActivity;

/**
 * 公用工具类
 * 
 * @author teeker_bin
 * 
 */
public class Utils {

    /**
     * 手机号码验证
     * 
     * @param
     * @return
     */
    public static boolean isPhoneNum(String strPhoneNum) {
        // Pattern p = Pattern
        // .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        // Matcher m = p.matcher(strPhoneNum);
        // return m.matches();
        return strPhoneNum.startsWith("1") && strPhoneNum.length() == 11;
    }

    /**
     * 确认字符串是否为email格式
     * 
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 获取屏幕宽度
     * 
     * @param context
     * @return
     */
    public static int getSecreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    /**
     * 获取屏幕高度
     * 
     * @param context
     * @return
     */
    public static int getSecreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }

    /**
     * 
     * @Description 检查网络状态
     * @param context
     * @return boolean
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) CLXApplication
                .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
    * 显示提示信息
    * 
    * @param str
    */
    public static void showToast(String str, int duration) {
        Toast toast = Toast.makeText(CLXApplication.getInstance(), str,
                duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    // 设置切换动画，从右边进入，左边退出
    public static void leftOutRightIn(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
    }

    /**
     * 右侧退出
     * 
     * @param context
     */
    public static void rightOut(Context context) {
        ((Activity) context).overridePendingTransition(R.anim.right_in,
                R.anim.right_out);

    }

    /**
    * 解決scrollview中嵌套listview显示不全问题
    * 
    * @param listView
    */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 获取短信地形内容
     */
    public static String getWarnContent(List<CircleMember> circleMembers,
            String nameWarn, String circleName, String inviteCode,
            String selfName) {

        String name = "";
        int size = circleMembers.size();
        if (size <= 3) {
            for (int i = 0; i < size; i++) {
                String n = circleMembers.get(i).getName();
                if (n.equals(nameWarn)) {
                    continue;
                }
                name += circleMembers.get(i).getName() + "、";
            }
            name = name.substring(0, name.length() - 1);
        } else {
            for (int i = 0; i < 3; i++) {
                String n = circleMembers.get(i).getName();
                if (n.equals(nameWarn)) {
                    name += circleMembers.get(size - 1).getName() + "、";
                    continue;
                }
                name += circleMembers.get(i).getName() + "、";
            }
            name = name.substring(0, name.length() - 1) + "等"
                    + circleMembers.size() + "人";
        }
        String content = StringUtils.getWarneContent(nameWarn, inviteCode,
                circleName, name, selfName);
        return content;
    }

    /**
    * 调用系统发短信界面
    * @param context
    * @param smsBody
    * @param num
    */
    public static void sendSMS(Context context, String smsBody, String num) {
        Uri smsToUri = Uri.parse("smsto:" + num);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", smsBody);
        context.startActivity(intent);

    }

    /**
     * 发送邮件
     * @param email
     * @param title
     * @param content
     */
    public static void sendEmail(Context context, String email, String title,
            String content) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:" + email));
        data.putExtra(Intent.EXTRA_SUBJECT, title);
        data.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(data);
    }

    /**
     * 获取设备imei
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * 设备型号
     */
    public static String getModelAndRelease() {
        String model = "Model:" + android.os.Build.MODEL; // 手机型号
        return model;
    }

    /**
     * 获取系统版本号
     * 
     * @return
     */
    public static String getOS() {
        String release = "Release:" + android.os.Build.VERSION.RELEASE; // android系统版本号
        return "android:" + release;

    }

    /**
     * 获取应用的当前版本号
     * 
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context) {
        String version = "";
        try {

            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo;
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            version = packInfo.versionName;

        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    /**
    * 隐藏软键盘
    */
    public static void hideSoftInput(Context context) {
        if (context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示软键盘
     */
    public static void popUp(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
    }

    /**
    * 浏览成长大图
    * 
    * @param position
    * @param imageUrls
    */
    public static void imageBrowerGrowth(Context context, int position,
            List<GrowthAlbumImages> imageUrls, Growth growth) {
        Intent intent = new Intent(context, GrowthImagePagerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_IMAGE_URLS,
                (Serializable) imageUrls);
        bundle.putSerializable("growth", (Serializable) growth);
        intent.putExtras(bundle);
        intent.putExtra(Constants.EXTRA_IMAGE_INDEX, position);
        intent.putExtra("cid", growth.getCid());
        context.startActivity(intent);
    }

    /**
    * 判断程序是否在后台
    * 
    * @param context
    * @return
    */
    public static boolean isTopActivity(Context context) {
        String packageName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (packageName.equals(tasksInfo.get(0).topActivity
                    .getPackageName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通知栏提醒
      */
    @SuppressWarnings("deprecation")
    public static void showNotify(String alert, String type) {
        // 更新通知栏
        CLXApplication application = CLXApplication.getInstance();
        int icon = R.drawable.app_icon;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, alert, when);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        // 设置默认声音
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.contentView = null;
        Intent intent = null;
        if ("FORCE_QUIT".equals(type)) {
            intent = new Intent(application, LoginActivity.class);
        } else {
            intent = new Intent(application, WelcomeActivity.class);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(application, 0,
                intent, 0);
        notification.setLatestEventInfo(CLXApplication.getInstance(), "常联系消息",
                alert, contentIntent);
        application.getNotificationManager().notify(0, notification);//
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * 
     * @param pxValue
     * @param scale
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
    * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void intentUserDetailActivity(Context context, int cid,
            int uid, int pid, String name, String avatarImg) {

        Intent it = new Intent();
        it.setClass(context, UserInfoActivity.class);
        CircleMember c = new CircleMember(cid, pid, uid);
        c.read(DBUtils.getDBsa(1));
        Bundle bundle = new Bundle();
        bundle.putSerializable("member", c);
        it.putExtras(bundle);
        context.startActivity(it);
    }

    // 媒体库更新
    // - 通过 Intent.ACTION_MEDIA_MOUNTED 进行全扫描
    public static void allScan(Context context) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
                .parse("file://" + Environment.getExternalStorageDirectory())));
    }

    // - 通过 Intent.ACTION_MEDIA_SCANNER_SCAN_FILE 扫描某个文件
    public static void fileScan(Context context, String fName) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(fName));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}
