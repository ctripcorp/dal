package com.ctrip.platform.dal.daogen.util;

import com.dianping.cat.Cat;
import qunar.tc.qconfig.client.MapConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by taochen on 2019/7/26.
 */
public class IPUtils {
    private static final String QCONFIG_KEY = "sendEmailIpAddress";

    private static final String SEND_EMAIL_IP_KEY = "ipAddress";

    private static final int RETRY_TIME = 3;

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

    public static String getExecuteIPFromQConfig() {
        MapConfig config = null;
        for (int i = 0; i < RETRY_TIME; ++i) {
            config = MapConfig.get(String.valueOf(EmailUtils.getLocalAppID()), QCONFIG_KEY, null);
            if (config != null) {
                break;
            }
        }
        Map<String, String> map = config.asMap();
        String ip = map.get(SEND_EMAIL_IP_KEY);
        Cat.logEvent("SendEmailIP", ip);
        return ip;
    }
}
