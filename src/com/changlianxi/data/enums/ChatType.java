package com.changlianxi.data.enums;

import java.util.HashMap;
import java.util.Map;

public enum ChatType {

    UNKNOWN,
    TYPE_TEXT,
    TYPE_IMAGE,
    TYPE_AUDIO,
    TYPE_VIDEO,
    TYPE_POSITION;

    public static Map<String, ChatType> s2t = new HashMap<String, ChatType>();
    static {
        for (ChatType type : ChatType.values()) {
            s2t.put(type.name(), type);
        }
    }

    public static ChatType convert(String s) {
        if (s2t.containsKey(s)) {
            return s2t.get(s);
        }
        return ChatType.UNKNOWN;
    }
}
