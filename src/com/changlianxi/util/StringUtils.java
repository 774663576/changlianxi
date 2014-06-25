package com.changlianxi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.Context;
import android.text.TextUtils;

import com.changlianxi.R;

public class StringUtils {
    public static String StringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        String regEx = "[^0-9]";
        // 清除掉所有特殊字符
        // String regEx = "[ (+]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 判断给定字符串是否空白串�?br> 空白串是指由空格、制表符、回车符、换行符组成的字符串<br>
     * 若输入字符串为null或空字符串，返回true
     * 
     * @param input
     * @return boolean
     */
    public static boolean isBlank(String input) {
        if (input == null || "".equals(input)) return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串拼接
     * 
     * @param str
     * @return
     */
    public static String JoinString(String str, String joinStr) {
        if (str == null || str.equals("")) {
            return "";
        }
        int point = str.lastIndexOf('.');
        return str.substring(0, point) + joinStr + str.substring(point);
    }

    /**
     * 返回str中最后一个separator子串后面的字符串 当str == null || str == "" || separator == ""
     * 时返回str； 当separator==null || 在str中不存在子串separator 时返回 ""
     * 
     * @param str
     *            源串
     * @param separator
     *            子串
     * @return
     */
    public static String substringAfterLast(String str, String separator) {
        if (TextUtils.isEmpty(str) || "".equals(separator)) {
            return str;
        }

        if (separator == null) {
            return "";
        }
        int idx = str.lastIndexOf(separator);
        if (idx < 0) {
            return str;
        }

        return str.substring(idx + separator.length());
    }

    /**
     * 去除字符串头部字符 比如 +86
     * 
     * @param srcStr
     * @param head
     * @return
     */
    public static String cutHead(String srcStr, String head) {
        if (TextUtils.isEmpty(srcStr)) return srcStr;
        if (srcStr.startsWith(head)) return substringAfter(srcStr, head);
        return srcStr;
    }

    /**
     * 返回str中separator子串后面的字符串 当str == null || str == "" || separator == ""
     * 时返回str； 当separator==null || 在str中不存在子串separator 时返回 ""
     * 
     * @param str
     *            源串
     * @param separator
     *            子串
     * @return
     */
    public static String substringAfter(String str, String separator) {
        if (TextUtils.isEmpty(str) || "".equals(separator)) {
            return str;
        }

        if (separator == null) {
            return "";
        }
        int idx = str.indexOf(separator);
        if (idx < 0) {
            return "";
        }

        return str.substring(idx + separator.length());
    }

    /***
     * 全角转半角
     * 
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 倒叙输出一个字符串
     * 
     * @param str
     * @return
     */
    public static String reverseSort(String str) {
        String str2 = "";
        for (int i = str.length() - 1; i > -1; i--) {
            str2 += String.valueOf(str.charAt(i));
        }

        return str2;
    }

    /**
     * 表情删除时使用 获取标签"："的位置
     * 
     * @param str
     * @return
     */
    public static int getPositionEmoj(String str) {
        String[] arr = new String[str.length()];
        for (int i = str.length() - 2; i >= 0; i--) {
            arr[i] = str.substring(i, (i + 1));
            if (":".equals(arr[i])) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 获取短信邀请内容
     * @param inviteName 被邀请人姓名
     * @param selfName 邀请人姓名
     * @param circleName 圈子名称
     * @param inviteCode 邀请码
     */
    public static String getInviteContent(Context context, String inviteName,
            String selfName, String circleName, String inviteCode) {
        String data = context.getResources().getString(R.string.sms_content);
        return data = String.format(data, inviteName, circleName, inviteCode);
    }

    /**
     * 获取短信邀请内容
     * @param inviteName 被邀请人姓名
     * @param selfName 邀请人姓名
     * @param circleName 圈子名称
     * @param inviteCode 邀请码
     */
    public static String getWarneContent(String inviteName, String inviteCode,
            String circleName, String otherName) {
        String data = SharedUtils.getString("inviteTemplate", "");
        return data = String.format(data, inviteName, circleName, otherName,
                inviteCode);
    }

    /**
     * 用****替换手机号的中间四位
     * @param num
     * @return
     */
    public static String replaceNum(String num) {
        if (num.length() == 0) {
            return num;
        }
        return num.substring(0, 3) + "****"
                + num.substring(num.length() - 4, num.length());
    }

    /** 
     * 计算位数 
     * @param str 
     * @return 
     */
    public static double calculatePlaces(String s) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        // 进位取整
        return Math.ceil(valueLength);
    }

    /** 
     * 截取8位字符串 
     * @param str 
     * @return 
     */
    public static String cutEight(String s) {
        String string = "";
        int a = 0;
        char arr[] = s.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
            if ((c >= 0x0391 && c <= 0xFFE5)) // 中文字符
            {
                a = a + 2;
                string = string + c;
            } else if ((c >= 0x0000 && c <= 0x00FF)) // 英文字符
            {
                a = a + 1;
                string = string + c;
            }
            if (a == 15 || a == 16) {
                return string;
            }
        }
        return s;
    }

    public static String getAlbumContributors(int index, int count, String name) {
        if (index == count) {
            return "贡献者：" + name + count + "人";
        }
        return "贡献者：" + name + "等" + count + "人";
    }
    
    /**
     * 从阿里云的处理后的图片地址复原到原始地址
     * 
     * @param url
     * @return
     */
    public static String revertAliyunOSSImageUrl(String url) {
        if ((url == null) || url.isEmpty()) {
            return "";
        }
        if (url.indexOf('@') > 0) {
            return url.substring(0, url.lastIndexOf('@'));
        }
        return url;
    }

    
    /**
     * 得到通过阿里云图片服务进行图片缩放后的图片url地址
     * 
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static String getAliyunOSSImageUrl(String url, int width, int height) {
        return getAliyunOSSImageUrl(url, width, height, false, false, false,
                false, 100, 100, 0, null);
    }

    /**
     * 得到通过阿里云图片服务进行图片处理后的图片url地址
     * 
     * @param url, 原始图片地址
     * @param width, 目标图片宽度
     * @param height, 目标图片高度
     * @param immobilize, 目标图片是否固定矿高，默认不固定
     * @param cut, 目标图片是否剪切，默认不剪切
     * @param edge, 缩放是长边优先还是短边优先，默认长边优先
     * @param orient, 是否根据相机拍摄方向自适应目标图片，默认不自适应
     * @param relativeQuality, 目标图片的相对质量，默认100
     * @param absoluteQuality, 目标图片的绝对质量，默认100
     * @param multiple, 目标图片的尺寸调整倍数，默认1
     * @param format, 目标图片的格式，默认是原始图片的格式
     * @return
     */
    public static String getAliyunOSSImageUrl(String url, int width,
            int height, boolean immobilize, boolean cut, boolean edge,
            boolean orient, int relativeQuality, int absoluteQuality,
            int multiple, String format) {
        if ((url == null) || url.isEmpty()) {
            return "";
        }

        String fix = width + "w_" + height + "h";
        if (immobilize) {
            fix += "_1i";
        }
        if (cut) {
            fix += "_1c";
        }
        if (edge) {
            fix += "_1e";
        }
        if (orient) {
            fix += "_1o";
        }
        if ((relativeQuality > 0) && (relativeQuality < 100)) {
            fix += "_" + relativeQuality + "q";
        }
        if ((absoluteQuality > 0) && (absoluteQuality < 100)) {
            fix += "_" + absoluteQuality + "Q";
        }
        if (multiple > 0) {
            fix += "_" + multiple + "x";
        }
        if ((format == null) || format.isEmpty()) {
            format = url.substring(url.lastIndexOf('.'));
        }
        fix += format;

        return url + '@' + fix;
    }
}