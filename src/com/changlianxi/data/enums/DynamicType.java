package com.changlianxi.data.enums;

import java.util.HashMap;
import java.util.Map;

public enum DynamicType {

    TYPE_UNKNOWN,
    TYPE_NEW_CIRCLE,
    TYPE_INVINTING,
    TYPE_ENTERING,
    TYPE_REFUSE_INVITE,
    TYPE_APPROVE_IGNORE,
    TYPE_APPROVE_ACCEPT,
    TYPE_PASS_APPROVE,
    TYPE_EDIT_PERSON,
    TYPE_EDIT_ACCEPT,
    TYPE_EDIT_REFUSE,
    TYPE_KICKOUT,
    TYPE_KICKOUT_IGNORE,
    TYPE_KICKOUT_ACCEPT,
    TYPE_PASS_KICKOUT,
    TYPE_QUIT_CIRCLE,
    TYPE_DISSOLVE_CIRCLE;

    public static Map<String, DynamicType> s2t = new HashMap<String, DynamicType>();
    static {
        for (DynamicType type : DynamicType.values()) {
            s2t.put(type.name(), type);
        }
    }

    public static DynamicType convert(String s) {
        if (s2t.containsKey(s)) {
            return s2t.get(s);
        }
        return DynamicType.TYPE_UNKNOWN;
    }
}
