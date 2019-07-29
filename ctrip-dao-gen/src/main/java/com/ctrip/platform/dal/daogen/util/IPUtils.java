package com.ctrip.platform.dal.daogen.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by taochen on 2019/7/26.
 */
public class IPUtils {
    public static boolean isIPAddress(String ipAddress) {
        String ipTemplate = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ipTemplate);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    public static String getLocalHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
