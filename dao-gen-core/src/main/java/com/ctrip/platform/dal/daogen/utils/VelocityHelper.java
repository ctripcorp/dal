package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class VelocityHelper {
    public static String getMockValForUnitTest(JavaParameterHost host, String seed) {
        if (host.getJavaClass().equals(int.class) || host.getJavaClass().equals(Integer.class)) {
            return host.isPrimary() ? seed : "-1";
        } else if (host.getJavaClass().equals(long.class) || host.getJavaClass().equals(Long.class)) {
            return host.isPrimary() ? "(long)" + seed : "-1l";
        } else if (host.getJavaClass().equals(Float.class) || host.getJavaClass().equals(Double.class)) {
            return host.isPrimary() ? "(float)" + seed : "-1f";
        } else if (host.getJavaClass().equals(Double.class) || host.getJavaClass().equals(double.class)) {
            return host.isPrimary() ? "(double)" + seed : "-1d";
        } else if (host.getJavaClass().equals(short.class) || host.getJavaClass().equals(Short.class)) {
            return String.format("(short)(%s)", host.isPrimary() ? seed : 1);
        } else if (host.getJavaClass().equals(boolean.class) || host.getJavaClass().equals(Boolean.class))
            return "false";
        else if (host.getJavaClass().equals(String.class)) {
            if (host.getSqlType() == 1 || host.getSqlType() == -9) {
                return "\"C\"";
            } else if (host.getSqlType() == -16) {
                return "\"<xml>hello</xml>\"";
            }
            return "\"test\"";
        } else if (host.getJavaClass().equals(Timestamp.class))
            return "new Timestamp(System.currentTimeMillis())";
        else if (host.getJavaClass().equals(BigDecimal.class))
            return "BigDecimal.ZERO";
        else if (host.getJavaClass().equals(Date.class))
            return "Date.valueOf(\"2014-6-19\")";
        else
            return "null";
    }
}
