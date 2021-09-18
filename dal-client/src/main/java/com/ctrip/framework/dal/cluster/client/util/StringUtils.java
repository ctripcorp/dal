package com.ctrip.framework.dal.cluster.client.util;

/**
 * @author c7ch23en
 */
public class StringUtils {

    public static boolean isEmpty(String s) {
        return (s == null || s.isEmpty());
    }

    public static boolean isTrimmedEmpty(String s) {
        return (s == null || s.trim().isEmpty());
    }

    public static String toTrimmedLowerCase(String s) {
        return s.trim().toLowerCase();
    }

    public static String toTrimmedUpperCase(String s) {
        return s.trim().toUpperCase();
    }

    public static String fromInt(Integer i) {
        return i != null ? String.valueOf(i) : null;
    }

    public static Integer toInt(String s) {
        return s != null ? Integer.parseInt(s) : null;
    }

}
