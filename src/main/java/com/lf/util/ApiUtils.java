package com.lf.util;

public class ApiUtils {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
