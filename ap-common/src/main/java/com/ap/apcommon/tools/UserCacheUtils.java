package com.ap.apcommon.tools;

public class UserCacheUtils {
    public static final String USER_INFO_PREFIX = "user:info:";

    public static String getKey(Long userId) {
        return USER_INFO_PREFIX + userId;
    }
}
