package com.xie.im.util;

public class StringUtils {

    public static boolean isEmpty(String str) {
        String temp = str;
        if (str != null) {
            temp = str.trim();
        }
        if ("".equals(temp) || temp == null) {
            return true;
        }
        return false;
    }
}
