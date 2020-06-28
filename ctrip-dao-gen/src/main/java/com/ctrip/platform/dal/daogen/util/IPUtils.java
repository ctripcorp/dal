package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.config.MonitorConfigManager;
import com.dianping.cat.Cat;
import org.apache.commons.lang.StringUtils;
import qunar.tc.qconfig.client.MapConfig;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by taochen on 2019/7/26.
 */
public class IPUtils {
    private static final String DOMAIN_READ = "read";

    private static final String DOMAIN_SLAVE = "slave";

    public static boolean isIPAddress(String ipAddress) {
        String ipTemplate = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ipTemplate);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    public static boolean isSlaveDomain(String serverName) {
        boolean result = false;
        if (StringUtils.isNotBlank(serverName) && (serverName.contains(DOMAIN_SLAVE) || serverName.contains(DOMAIN_READ))) {
            result =  true;
        }
        return result;
    }

    public static String getLocalHostIp() {
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        Cat.logEvent("getLocalIP", ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            Cat.logError("get local ip fail!", e);
        }
        return null;
    }

    public static String getExecuteIPFromQConfig() {
        String ip = MonitorConfigManager.getMonitorConfig().getIpAddress();
        Cat.logEvent("SendEmailIP", ip);
        return ip;
    }
}
