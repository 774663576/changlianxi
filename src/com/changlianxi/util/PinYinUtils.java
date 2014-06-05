package com.changlianxi.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.changlianxi.util.HanziToPinyin.Token;

public class PinYinUtils {
    // 汉字返回拼音，字母原样返回，都转换为小写
    public static String getPinYin(String input) {
        ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (Token token : tokens) {
                if (Token.PINYIN == token.type) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        return sb.toString().toLowerCase();
    }

    public static String getSortKey(String displayName) {
        ArrayList<Token> tokens = HanziToPinyin.getInstance().get(displayName);
        if (tokens != null && tokens.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Token token : tokens) {
                // Put Chinese character's pinyin, then proceed with the
                // character itself.
                if (Token.PINYIN == token.type) {
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    sb.append(token.target);
                    sb.append(' ');
                    sb.append(token.source);
                } else {
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    sb.append(token.source);
                }
            }
            return sb.toString();
        }
        return displayName;
    }

    public static String getFirstPinYin(String source) {
        if (!Arrays.asList(Collator.getAvailableLocales()).contains(
                Locale.CHINA)) {
            return source;
        }
        ArrayList<Token> tokens = HanziToPinyin.getInstance().get(source);
        if (tokens == null || tokens.size() == 0) {
            return source;
        }
        StringBuffer result = new StringBuffer();
        for (Token token : tokens) {
            if (Token.PINYIN == token.type) {
                result.append(token.target.charAt(0));
            } else {
                result.append(token.source);
            }
        }
        return result.toString().toLowerCase();

    }
}
