package com.changlianxi.util;

import java.io.File;

import net.tsz.afinal.FinalBitmap;
import android.widget.ImageView;

import com.changlianxi.applation.CLXApplication;

public class FinalBitmapLoadTool {
    private static FinalBitmap fb = FinalBitmap.create(CLXApplication
            .getInstance());

    public static FinalBitmap getFb() {
        return fb;
    }

    public static void init() {
        fb.configMemoryCacheSize(20 * 1024 * 1024)
                .configBitmapLoadThreadSize(3);
        fb.configDiskCachePath(FileUtils.getRootDir() + File.separator
                + "changlianxi" + File.separator + "img");
    }

    public static void display(String uri, ImageView imageView, int default_pic) {
        fb.configLoadfailImage(default_pic);
        fb.configLoadingImage(default_pic);
        fb.display(imageView, uri);
    }

    public static void destory() {
        fb.onDestroy();

    }

    public static void onPause() {
        // fb.onPause();
        fb.pauseWork(true);

    }

    public static void onResume() {
        // fb.onResume();
        fb.pauseWork(false);

    }
}
