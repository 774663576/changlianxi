package com.changlianxi.applation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.graphics.Bitmap;

import com.baidu.frontia.FrontiaApplication;
import com.changlianxi.R;
import com.changlianxi.util.CrashHandler;
import com.changlianxi.util.FileUtils;
import com.changlianxi.util.FinalBitmapLoadTool;
import com.changlianxi.util.Logger;
import com.changlianxi.util.Logger.Level;
import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class CLXApplication extends Application {
    private static CLXApplication instance;
    private static List<Activity> activityList = new ArrayList<Activity>();
    private static List<Activity> smsInviteAactivityList = new ArrayList<Activity>();
    private static DisplayImageOptions option;
    private static ImageLoader imageLoader;
    private static FinalBitmap fb;
    private NotificationManager mNotificationManager;

    public static CLXApplication getInstance() {
        return instance;
    }

    public void setInstance(CLXApplication instance) {
        this.instance = instance;
    }

    public static DisplayImageOptions getOption() {
        return option;
    }

    public static void setOption(DisplayImageOptions option) {
        CLXApplication.option = option;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FrontiaApplication.initFrontiaApplication(this);
        setInstance(this);
        initImageLoader();
        FinalBitmapLoadTool.init();
        Logger.setWriteFile(false); // 设置日志是写文件还是使用标准输出
        Logger.setLogLevel(Level.DEBUG); // 日志级别
        Logger.setOutPut(false);// 不在控制台输出
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(this);
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

    private void initImageLoader() {
        // 初始化图片缓存
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this)
                .discCache(
                        new FileCountLimitedDiscCache(new File(FileUtils
                                .getRootDir() + "/changlianxi/cache"), 500))
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024);
        ImageLoader.getInstance().init(builder.build());
        option = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.head_bg)
                .showImageForEmptyUri(R.drawable.head_bg)
                .showImageOnFail(R.drawable.head_bg).cacheInMemory(true)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader = ImageLoader.getInstance();
        fb = FinalBitmap.create(this);
        fb.configMemoryCacheSize(20 * 1024 * 1024)
                .configBitmapLoadThreadSize(3);
        fb.configDiskCachePath(FileUtils.getRootDir() + File.separator
                + "changlianxi" + File.separator + "img");
    }

    public static FinalBitmap getFb() {
        return fb;
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

}
