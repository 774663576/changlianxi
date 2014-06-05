package com.changlianxi.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.text.TextUtils;

/**
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {

    private static final Map<String, DateFormat> DFS = new HashMap<String, DateFormat>();
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private DateUtils() {
    }

    public static Date stringToDate(String str) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = fmt.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 获取当前时间字符串，格式为："yyyy-MM-dd HH:mm:ss"
     * 
     * @return
     */
    public static String getCurrDateStr() {
        return getCurrDateStr(null);
    }

    /**
     * 返回指定格式的当前时间字符串，如果format为空或者为null则使用默认格式："yyyy-MM-dd HH:mm:ss"
     * 
     * @Throws 如果传入的格式不支持则抛出 IllegalArgumentException 异常
     * @param format
     * @return
     */
    public static String getCurrDateStr(String format) {
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return format(new Date(), format);
    }

    public static String getYear(String str, String format) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df1 = new SimpleDateFormat(format);
        String strdate = "";
        try {
            Date date = df.parse(str);
            strdate = df1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strdate;
    }

    /**
     * 把时间戳装换为一定格式的日期
     * @param timestamp
     */
    public static String timestampConvertToDate(String timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String date = sdf.format(new Date(Long.valueOf(timestamp) * 1000));
        return date;
    }

    public static String getMonth(String str, String format) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df1 = new SimpleDateFormat(format);
        String strdate = "";
        try {
            Date date = df.parse(str);
            strdate = df1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strdate;
    }

    public static String getGrowthShowTime(String str) {
        DateFormat df1 = new SimpleDateFormat("yyyy年MM月dd日");
        String strdate = "";
        try {
            Date date = df.parse(str);
            strdate = df1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return strdate;
    }

    public static DateFormat getFormat(String pattern) {
        DateFormat format = DFS.get(pattern);
        if (format == null) {
            format = new SimpleDateFormat(pattern);
            DFS.put(pattern, format);
        }
        return format;
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return getFormat(pattern).format(date);
    }

    private static Calendar convert(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 按一定格式截取字符串日期
     * 
     * @param str
     * @return
     */
    public static String interceptDateStr(String str, String format) {
        DateFormat df1 = new SimpleDateFormat(format);
        String strdate = "";
        try {
            Date date = df.parse(str);
            strdate = df1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strdate;

    }

    /**
     * 返回指定日期相应位移后的日期
     * 
     * @param date
     *            参考日期
     * @param field
     *            位移单位，见 {@link Calendar}
     * @param offset
     *            位移数量，正数表示之后的时间，负数表示之前的时间
     * @return 位移后的日期
     */
    public static Date offsetDate(Date date, int field, int offset) {
        Calendar calendar = convert(date);
        calendar.add(field, offset);
        return calendar.getTime();
    }

    /**
     * 转换为php时间戳 减去后三位
     * 
     * @param str
     */
    public static String phpTime(Long str) {
        String strTime = String.valueOf(str);
        return strTime.substring(0, strTime.length() - 3);

    }

    /**
     * 将string类型转换为date
     * 
     * @param str
     */
    public static long convertToDate(String str) {
        Date date = null;
        try {
            date = df.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 将string类型转换为date
     * 
     * @param str
     */
    public static long convertGrowthToDate(String str) {
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            date = df.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 计算成长记录发布时间
     * 
     * @param strTime
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String publishedTime(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long second = 0;
        long l = 0;
        long month = 0;
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            Date nowDate = df.parse(getCurrDateStr("yyyy-MM-dd HH:mm:ss"));
            Date date = df.parse(time);
            l = nowDate.getTime() - date.getTime();
            month = (l / (24 * 60 * 60 * 1000)) / 30;
            day = l / (24 * 60 * 60 * 1000);
            hour = (l / (60 * 60 * 1000) - day * 24);
            min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            second = ((l / 1000) - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            if (hour < 1 && day < 1 && nowDate.getDay() == date.getDay()) {
                return interceptDateStr(time, "HH:mm");
            }
            if (hour < 1 && day < 1 && nowDate.getDay() != date.getDay()) {
                return "昨天" + interceptDateStr(time, "HH:mm");
            }
            if (day < 1 && hour >= 1 && nowDate.getDay() == date.getDay()) {
                return "今天  " + interceptDateStr(time, "HH:mm");
            }
            if (day < 1 && hour >= 1 && nowDate.getDay() != date.getDay()) {
                return "昨天  " + interceptDateStr(time, "HH:mm");
            }
            if (day < 2 && day >= 1 && (nowDate.getDay() - date.getDay() == 1)) {
                return "昨天" + interceptDateStr(time, "HH:mm");
            }
            if (day < 2 && day >= 1 && (nowDate.getDay() - date.getDay() != 1)) {
                return interceptDateStr(time, "MM-dd HH:mm");
            }
            if (day < 365 && day >= 2) {
                return interceptDateStr(time, "MM-dd HH:mm");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return interceptDateStr(time, "yyyy-MM-dd HH:mm");
    }

    /**
     * 计算成长评论发布时间
     * 
     * @param strTime
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String publishedTime2(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long second = 0;
        long l = 0;
        long month = 0;
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            Date nowDate = df.parse(getCurrDateStr("yyyy-MM-dd HH:mm:ss"));
            Date date = df.parse(time);
            l = nowDate.getTime() - date.getTime();
            month = l / (30 * 24 * 60 * 60 * 1000);
            day = l / (24 * 60 * 60 * 1000);
            hour = (l / (60 * 60 * 1000) - day * 24);
            min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            second = ((l / 1000) - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            if (min < 1) {
                return second + "秒前";
            }
            if (hour < 1 && min >= 1 && day < 1) {
                return min + "分钟前";
            }
            if (day < 1 && hour >= 1 && nowDate.getDay() == date.getDay()) {
                return "今天  " + interceptDateStr(time, "HH:mm");
            }
            if (day < 1 && hour >= 1 && nowDate.getDay() != date.getDay()) {
                return "昨天  " + interceptDateStr(time, "HH:mm");
            }
            if (day < 2 && day >= 1 && (nowDate.getDay() - date.getDay() == 1)) {
                return "昨天" + interceptDateStr(time, "HH:mm");
            }
            if (day < 2 && day >= 1 && (nowDate.getDay() - date.getDay() != 1)) {
                return interceptDateStr(time, "MM-dd HH:mm");
            }
            if (day < 30 && day >= 2) {
                return interceptDateStr(time, "MM-dd HH:mm");
            }
            if (day < 365 && day >= 30) {
                return interceptDateStr(time, "MM-dd HH:mm");
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return interceptDateStr(time, "yyyy-MM-dd HH:mm");
    }

    /**
     * 计算聊天/动态发布时间
     * 
     * @param strTime
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String publishedTime3(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long l = 0;
        long day = 0;
        try {
            Date nowDate = df.parse(getCurrDateStr("yyyy-MM-dd HH:mm"));
            Date date = df.parse(time);
            l = nowDate.getTime() - date.getTime();
            day = l / (24 * 60 * 60 * 1000);
            if (day < 1 && nowDate.getDay() == date.getDay()) {
                return interceptDateStr(time, "HH:mm");
            }
            if (day < 1 && nowDate.getDay() != date.getDay()) {
                return interceptDateStr(time, "MM-dd HH:mm");
            }
            if (day < 365 && day >= 1) {
                return interceptDateStr(time, "MM-dd HH:mm");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return interceptDateStr(time, "yyyy-MM-dd HH:mm");
    }

    public static long getDay(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            Date now = df.parse(getCurrDateStr());
            Date date = df.parse(time);
            long l = now.getTime() - date.getTime();
            day = l / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return day;
    }

    public static String getHour(String time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long hour = 0;
        try {
            Date now = df.parse(getCurrDateStr("yyyy-MM-dd HH:mm:ss"));
            Date date = df.parse(time);
            long l = now.getTime() - date.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            if (hour < 1) {
                if (min == 0) {
                    return "刚刚";
                }
                return min + "分钟前";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hour + "小时前";

    }

    /**
     * 两个日期比较 聊天时使用
     * 
     * @param starTime
     * @param endTime
     * @return
     */
    public static boolean compareDate(String starTime, String endTime,
            int timeInterval) {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date begin = dfs.parse(starTime);
            Date end = dfs.parse(endTime);
            long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
            long minute = between % 3600 / 60;
            if (minute >= timeInterval) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

    /**
     * 编辑资料时选择日期使用
     * 
     * @param starTime
     * @param endTime
     * @return
     */
    public static boolean compareDate(String starTime, String endTime) {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean flag = false;
        try {
            Date begin = dfs.parse(starTime);
            Date end = dfs.parse(endTime);
            flag = end.after(begin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;

    }

    public static final long A_SECOND = 1000;
    public static final long A_MINUTE = 60 * A_SECOND;
    public static final long AN_HOUR = 60 * A_MINUTE;
    public static final long A_DAY = 24 * AN_HOUR;

    public static String formatTime(String time) {
        Calendar now = Calendar.getInstance();
        return formatTime(time, now.getTimeInMillis());
    }

    public static String formatTime(String time, long baseTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(time);
            long dateTime = date.getTime();
            long diff = baseTime - dateTime;
            if (diff < 10 * A_SECOND) {
                return "刚刚";
            }
            if (diff < A_MINUTE) {
                return diff / A_SECOND + "秒前";
            }
            if (diff < AN_HOUR) {
                return diff / A_MINUTE + "分钟前";
            }
            if (diff < A_DAY) {
                return diff / AN_HOUR + "小时前";
            }
            if (diff < 2 * A_DAY) {
                if (((baseTime - A_DAY) % A_DAY == dateTime % A_DAY)) {
                    return "昨天";
                } else {
                    return "前天";
                }
            }
            if (diff < 3 * A_DAY) {
                if (((baseTime - 2 * A_DAY) % A_DAY == dateTime % A_DAY)) {
                    return "前天";
                }
            }

            Calendar target = Calendar.getInstance();
            target.setTimeInMillis(date.getTime());
            Calendar base = Calendar.getInstance();
            base.setTimeInMillis(baseTime);
            if (target.get(Calendar.YEAR) == base.get(Calendar.YEAR)) {
                SimpleDateFormat df2 = new SimpleDateFormat("MM-dd HH:mm");
                return df2.format(target.getTime());
            } else {
                SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return df2.format(target.getTime());
            }
        } catch (ParseException e) {
            return time;
        }
    }

}