package com.ctrip.framework.dal.dbconfig.plugin.util;


import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants.X_REAL_IP;
import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.*;

public final class NetworkUtil {
    /**
     * 获取请求主机IP地址
     *
     * @param request
     * @return
     */
    public final static String getClientIp(HttpServletRequest request) {
        // nginx, squid 等反向代理一般利用此字段来传递中间链路上的节点ip
        // X-Forwarded-For[0]一般表示客户端，即代理看到的对端remote address
        // 用逗号+空格分隔，代理将上一跳追加到此header中
        String ip = request.getHeader("X-Forwarded-For");
        if (!Strings.isNullOrEmpty(ip)) {
            int pos = ip.indexOf(',');
            if (pos >= 0) {
                ip = ip.substring(0, pos);
            }
        } else {
            // 反向代理会把自己的上一跳放入X-Real-IP, 不追加
            ip = request.getHeader(X_REAL_IP);
            if (Strings.isNullOrEmpty(ip)) {
                ip = request.getRemoteAddr();
            }
        }

        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    public final static String getNetType(HttpServletRequest request) {
        String netType = request.getHeader(HEADER_NET_TYPE);
        if (Strings.isNullOrEmpty(netType)) {
            return PUBLIC_NET_TYPE;
        }
        return netType;
    }

    public final static boolean isFromPublicNet(String netType) {
        if (Strings.isNullOrEmpty(netType)) {
            return true;
        }

        if (PRIVATE_NET_TYPE.equalsIgnoreCase(netType)) {
            return false;
        } else {
            return true;
        }
    }

}
