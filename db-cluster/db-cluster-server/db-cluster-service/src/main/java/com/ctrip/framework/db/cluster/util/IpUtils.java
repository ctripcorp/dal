package com.ctrip.framework.db.cluster.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by shenjie on 2019/3/19.
 */
public class IpUtils {

    public static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    public static boolean checkAllowedIp(HttpServletRequest request, Set<String> allowedIps) {
        String ip = getRequestIp(request);
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        if (allowedIps == null) {
            return false;
        }

        return allowedIps.contains(ip);
    }
}
