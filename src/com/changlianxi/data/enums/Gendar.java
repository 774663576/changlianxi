package com.changlianxi.data.enums;

public enum Gendar {

    UNKNOWN,
    MAN,
    WOMAN;

    public static Gendar parseInt2Gendar(int i) {
        switch (i) {
            case 1:
                return Gendar.MAN;
            case 2:
                return Gendar.WOMAN;
            default:
                return Gendar.UNKNOWN;
        }
    }

    public static Gendar parseString2Gendar(String s) {
        if ("男".equals(s)) {
            return Gendar.MAN;
        }
        if ("女".equals(s)) {
            return Gendar.WOMAN;
        }
        return Gendar.UNKNOWN;
    }

    public static int parseGendar2Int(Gendar g) {
        return g.ordinal();
    }

    public static String parseGendar2String(Gendar g) {
        if (Gendar.MAN == g) {
            return "男";
        }
        if (Gendar.WOMAN == g) {
            return "女";
        }
        return "";

    }

}
