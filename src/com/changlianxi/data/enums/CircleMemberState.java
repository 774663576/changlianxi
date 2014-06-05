package com.changlianxi.data.enums;

import java.util.HashMap;
import java.util.Map;

public enum CircleMemberState {

    STATUS_INVALID,
    STATUS_INVITING,
    STATUS_ENTER_AND_VERIFYING,
    STATUS_REFUSED,
    STATUS_VERIFIED,
    STATUS_KICKOFFING,
    STATUS_KICKOUT,
    STATUS_QUIT;

    public static Map<String, CircleMemberState> s2s = new HashMap<String, CircleMemberState>();
    static {
        for (CircleMemberState s : CircleMemberState.values()) {
            s2s.put(s.name(), s);
        }
    }

    public static CircleMemberState convert(String s) {
        if (s2s.containsKey(s)) {
            return s2s.get(s);
        }
        return CircleMemberState.STATUS_INVALID;
    }

    public static boolean isInCircle(CircleMemberState s) {
        return s == CircleMemberState.STATUS_ENTER_AND_VERIFYING
                || s == CircleMemberState.STATUS_VERIFIED
                || s == CircleMemberState.STATUS_KICKOFFING||s==CircleMemberState.STATUS_INVITING;
    }

    public static boolean notInCircle(CircleMemberState s) {
        return s == CircleMemberState.STATUS_INVALID
                || s == CircleMemberState.STATUS_REFUSED
                || s == CircleMemberState.STATUS_KICKOUT
                || s == CircleMemberState.STATUS_QUIT;
    }
}
