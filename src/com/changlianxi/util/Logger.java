package com.changlianxi.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

import com.changlianxi.util.DateUtils;

import android.text.TextUtils;

/**
 * 日志记录工具类
 * 
 */
public class Logger {
    private static boolean isOutPut = true;

    /**
     * 日志级别
     * 
     */
    public enum Level {

        /*** 调试级别 **/
        DEBUG(0), // 调试
        /*** 消息记录级别 **/
        INFO(1), // 消息
        /*** 警告级别 **/
        WARN(2), // 警告
        /*** 错误级别 **/
        ERROR(3), // 错误
        /*** 严重错误级别 **/
        FATAL(4); // 严重错误

        private final int m_level;

        private Level(int level) {
            m_level = level;
        }

        /**
         * 返回Level的int值
         * 
         * @return
         */
        public int getLevel() {
            return m_level;
        }

        public String getLevelDescn() {
            switch (m_level) {
                case 0:
                    return "DEBUG";
                case 1:
                    return "INFO";
                case 2:
                    return "WARN";
                case 3:
                    return "ERROR";
                case 4:
                    return "FATAL";
                default:
                    break;
            }
            return "";
        }
    };

    // 当前日志级别，默认为DEBUG
    private static Level logLevel = Level.DEBUG;

    // 日志存放目录，默认为SD卡下/asr/crash下
    private static String logPath = FileUtils
            .getgetAbsoluteDir("/changlianxi/crash");

    // 日志文件名前辍
    private static String m_logNamePrex = "crash_";

    // 日志文件大小最大值，以字节为单位
    private static long maxFileSize = 1024 * 1024L;

    // 日志文件的保存天数
    private static int logRemainDays = 7;

    // 是否写日志文件，如果不写日志文件则使用标准输出System.out
    private static boolean writeFile = false;

    /**
     * 设置是否写日志文件
     * 
     * @param writeFile
     *            true为写日志文件，false为标准输出
     */
    public static void setWriteFile(boolean writeFile) {
        Logger.writeFile = writeFile;
    }

    /**
     * 设置日志级别
     * 
     * @param level
     */
    public static void setLogLevel(Level level) {
        logLevel = level;
    }

    /**
     * 设置日志存入路径，如果设置的路径不存在则使用默认路径，如果默认路径也找不到则不写日志
     * 
     * @param path
     */
    public static void setLogPath(String path) {
        logPath = path;
    }

    /**
     * 设置日志文件的前辍，不设置则使用默认值“crash_”
     * 
     * @param prex
     */
    public static void setLogFilePrex(String prex) {
        m_logNamePrex = prex;
    }

    /**
     * 设置日志文件大小最大值，以字节为单位
     * 
     * @param size
     *            可以允许的日志文件大小，字节数
     */
    public static void setFileMaxSize(long size) {
        maxFileSize = size;
    }

    /**
     * 设置日志文件的保存天数
     * 
     * @param days
     */
    public static void setLogRemainDays(int days) {
        logRemainDays = days;
    }

    public static void setOutPut(boolean isOutPut) {
        Logger.isOutPut = isOutPut;
    }

    /**
     * 获取日志文件名，包含路径
     * 
     * @return
     */
    private static String getLogFileName() {
        if (TextUtils.isEmpty(logPath)) {
            if (TextUtils.isEmpty(FileUtils
                    .getgetAbsoluteDir("/changlianxi/crash"))) {
                return "";
            }
            logPath = FileUtils.getgetAbsoluteDir("/changlianxi/crash");
        }
        String fileName = logPath + "/" + m_logNamePrex
                + DateUtils.getCurrDateStr("yyyyMMdd") + ".log";
        return fileName;
    }

    /**
     * 清理超出保存天数的日志文件
     */
    private static void clearInvalidLogFile(File logPath) {
        // 获取过期日期
        Date expiredDate = DateUtils.offsetDate(new Date(),
                Calendar.DAY_OF_MONTH, -logRemainDays);
        String expiredFileName = m_logNamePrex
                + DateUtils.format(expiredDate, "yyyyMMdd") + ".log";
        File fList[] = logPath.listFiles();
        for (int i = 0; i < fList.length; i++) {
            String fName = StringUtils.substringAfterLast(fList[i].getName(),
                    "/");
            if (fName.startsWith(m_logNamePrex)
                    && expiredFileName.compareTo(fName) >= 0) {
                fList[i].delete();
            }
        }
    }

    private static File checkLogFile() {
        String fileName = getLogFileName(); // 获取日志文件名
        // 如果获取日志文件名为空，则说明SD卡没有成功挂载，所以不写日志
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        File filePath = new File(logPath);
        // 如果日志存放路径不存在则创建日志路径
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File logFile = new File(fileName);
        try {
            if (logFile.isFile() && logFile.length() >= maxFileSize) {
                logFile.delete();
            }
            if (!logFile.exists()) { // 如果日志文件不存在则清理已经超出保存期限的日志文件，并创建新的日志文件
                logFile.createNewFile();
                clearInvalidLogFile(filePath);
            }
        } catch (IOException e) {
            return null;
        }
        return logFile;
    }

    /**
     * 写日志操作处理方法
     * 
     * @param caller
     *            调用者类对象
     * @param msg
     * @param level
     */
    private static void writeLog(Object caller, String msg, Level level) {
        String logTag = "";
        if (caller != null) {
            if (caller instanceof String) {
                logTag = caller.toString();
            } else {
                logTag = caller.getClass().getName();
            }
        }
        if (level.getLevel() < logLevel.getLevel()) {
            return;
        }
        if (!writeFile) {
            return;
        }
        File logFile = checkLogFile(); // 检查日志文件
        if (logFile == null) {
            return;
        }
        /*** 组织日志输入内容 **/
        String writeMsg = "[" + DateUtils.getCurrDateStr() + "] [";
        writeMsg += level.getLevelDescn() + "] ";
        writeMsg += logTag + ": ";
        writeMsg += msg + "\n";
        try {
            FileWriter fw = new FileWriter(logFile, true);
            fw.append(writeMsg);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将异常消息对象转换成字符串信息
     * 
     * @param ex
     * @return
     */
    private static String throwableToString(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        return writer.toString();
    }

    /**
     * 调试
     * 
     * @param caller
     *            调用者类对象
     * @param msg
     *            日志信息字符串
     */
    public static void debug(Object caller, String msg) {
        writeLog(caller, msg, Level.DEBUG);
    }

    /**
     * 调试
     * 
     * @param caller
     *            调用者类对象
     * @param e
     *            异常信息对象
     */
    public static void debug(Object caller, Throwable e) {
        writeLog(caller, throwableToString(e), Level.DEBUG);
    }

    /**
     * 消息
     * 
     * @param caller
     *            调用者类对象
     * @param msg
     *            日志信息字符串
     */
    public static void info(Object caller, String msg) {
        writeLog(caller, msg, Level.INFO);
    }

    /**
     * 消息
     * 
     * @param caller
     *            调用者类对象
     * @param e
     *            异常信息对象
     */
    public static void info(Object caller, Throwable e) {
        writeLog(caller, throwableToString(e), Level.INFO);
    }

    /**
     * 警告
     * 
     * @param caller
     *            调用者类对象
     * @param msg
     *            日志信息字符串
     */
    public static void warn(Object caller, String msg) {
        writeLog(caller, msg, Level.WARN);
    }

    /**
     * 警告
     * 
     * @param caller
     *            调用者类对象
     * @param e
     *            异常信息对象
     */
    public static void warn(Object caller, Throwable e) {
        writeLog(caller, throwableToString(e), Level.WARN);
    }

    /**
     * 错误
     * 
     * @param caller
     *            调用者类对象
     * @param msg
     *            日志信息字符串
     */
    public static void error(Object caller, String msg) {
        writeLog(caller, msg, Level.ERROR);
    }

    /**
     * 错误
     * 
     * @param caller
     *            调用者类对象
     * @param e
     *            异常信息对象
     */
    public static void error(Object caller, Throwable e) {
        writeLog(caller, throwableToString(e), Level.ERROR);
    }

    /**
     * 严重错误
     * 
     * @param caller
     *            调用者类对象
     * @param e
     *            异常信息对象
     */
    public static void fatal(Object caller, String msg) {
        writeLog(caller, msg, Level.FATAL);
    }

    /**
     * 严重错误
     * 
     * @param caller
     *            调用者类对象
     * @param msg
     *            日志信息字符串
     */
    public static void fatal(Object caller, Throwable e) {
        writeLog(caller, throwableToString(e), Level.FATAL);
    }

    /**
     * 输出log信息
     * 
     * @param caller
     * @param msg
     * @param level
     */
    public static void out(Object caller, String msg, Level level) {
        outLog(caller, msg, level);
    }

    /**
     * 输出log信息
     * 
     * @param caller
     * @param e
     * @param level
     */
    public static void out(Object caller, Throwable e, Level level) {
        outLog(caller, throwableToString(e), level);
    }

    /**
     * 写日志操作处理方法
     * 
     * @param caller
     *            调用者类对象
     * @param msg
     * @param level
     */
    private static void outLog(Object caller, String msg, Level level) {
        String logTag = "";
        if (caller != null) {
            if (caller instanceof String) {
                logTag = caller.toString();
            } else {
                logTag = caller.getClass().getName();
            }
        }
        if (level.getLevel() < logLevel.getLevel()) {
            return;
        }
        if (!isOutPut) {
            return;
        }
        /*** 组织输出内容 **/
        String outMsg = "[" + DateUtils.getCurrDateStr() + "] [";
        outMsg += level.getLevelDescn() + "] ";
        outMsg += logTag + ": ";
        outMsg += msg + "\n";
        System.out.println(outMsg);
    }
}
