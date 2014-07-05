package com.changlianxi.applation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;

import com.baidu.frontia.FrontiaApplication;
import com.changlianxi.chooseImage.CheckImageLoaderConfiguration;
import com.changlianxi.util.Logger;

public class CLXApplication extends Application {
    private static CLXApplication instance;
    private static List<Activity> activityList = new ArrayList<Activity>();
    private static List<Activity> smsInviteAactivityList = new ArrayList<Activity>();
    private NotificationManager mNotificationManager;

    public static CLXApplication getInstance() {
        return instance;
    }

    public void setInstance(CLXApplication instance) {
        this.instance = instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FrontiaApplication.initFrontiaApplication(this);
        setInstance(this);
        CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
        // Logger.setWriteFile(false); // 设置日志是写文件还是使用标准输出
        // Logger.setLogLevel(Level.DEBUG); // 日志级别
        Logger.setOutPut(false);// 不在控制台输出`
        // CrashHandler catchHandler = CrashHandler.getInstance();
        // catchHandler.init(this);
    }

    // 添加Activity到容器中
    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 添加創建圈子Activity到容器中
    public static void addInviteActivity(Activity activity) {
        smsInviteAactivityList.add(activity);
    }

    // 删除对应的Activity
    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    // 遍历所有Activity并finish
    public static void exit(boolean flag) {
        for (int i = 0; i < activityList.size(); i++) {
            Activity activity = activityList.get(i);
            if (activity != null) {
                activity.finish();
            }
        }
        activityList.clear();
        if (flag) {
            System.exit(0);
        }
    }

    // 遍历创建圈子activity并finish
    public static void exitSmsInvite() {
        for (int i = 0; i < smsInviteAactivityList.size(); i++) {
            Activity activity = smsInviteAactivityList.get(i);
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

}
