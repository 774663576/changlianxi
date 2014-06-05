package com.changlianxi.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.os.Environment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;

public class FileUtils {

    /**
     * 根据文件绝对路径获取文件名称
     * 
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isBlank(filePath)) return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 获取根目录
     */
    public static String getRootDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 使用当前时间戳拼接一个唯�?��文件�?
     * 
     * @param format
     * @return
     */
    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
        String fileName = format.format(new Timestamp(System
                .currentTimeMillis()));
        return fileName;
    }

    /**
     * 获取文件扩展名
     * 
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (StringUtils.isBlank(fileName)) return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    /**
     * 判断sd卡是否存在
     * 
     * @return
     */
    public static boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * 创建文件夹
     * 
     * @param dir
     */
    public static void createDir(String dir) {
        String sdpath = getRootDir();
        File destDir = new File(sdpath + dir);
        if (!destDir.exists()) {// 创建文件夹
            destDir.mkdirs();
        }
    }

    /**
     * 得到绝对路径
     * 
     * @param dir
     * @return
     */
    public static String getgetAbsoluteDir(String dir) {
        return getRootDir() + dir;

    }

    /**
     * 获取拍照路径
     */
    public static String getCameraPath() {
        String sdpath = getRootDir();
        File destDir = new File(sdpath + "/changlianxi/camera");
        if (!destDir.exists()) {// 创建文件夹
            destDir.mkdirs();
        }
        createNoMediaFile();
        return destDir.getAbsolutePath();

    }

    public static String getCLXDir() {
        return getRootDir() + "/changlianxi/";

    }

    private static void createNoMediaFile() {
        File file = new File(getRootDir() + "/changlianxi/.nomedia");
        if (!file.exists()) {// 判断文件是否存在（不存在则创建这个文件）
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取缓存图片的路径
     * 
     * @param imgUrl
     */
    public static String getCachePath(String imgUrl) {
        File discCache = DiscCacheUtil.findInCache(imgUrl, ImageLoader
                .getInstance().getDiscCache());
        return discCache == null ? "" : discCache.getAbsolutePath();
    }

    /**
     * 文件转byte
     * @param f
     * @return
     */
    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

}