package com.ctrip.framework.db.cluster.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ctrip.framework.db.cluster.util.RegularCommons.*;

/**
 * Created by pcxie on 2019/3/25.
 */
public class CMongoRegularUtil {
    public static boolean password(String password) {
        Pattern pattern = Pattern.compile(passWordRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean userid(String userName) {
        Pattern pattern = Pattern.compile(userNameRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }

    public static boolean hostName(String hostName) {
        Pattern pattern = Pattern.compile(hostNameRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(hostName);
        return matcher.matches();
    }

    public static boolean judgeIPV6(String ipv6) {
        Pattern pattern = Pattern.compile(ipv6Regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ipv6);
        return matcher.matches();
    }

    public static boolean judgeIPV4(String ipv4) {
        Pattern pattern = Pattern.compile(ipv4Regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ipv4);
        return matcher.matches();
    }

    public static boolean judgePort(String port) {
        Pattern pattern = Pattern.compile(portRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(port);
        return matcher.matches();
    }

    public static boolean clusterType(String clusterInfo) {
        if (clusterInfo.equalsIgnoreCase("Replication") || clusterInfo.equalsIgnoreCase("Sharding")) {
            return true;
        }
        return false;
    }

    public static boolean dbName(String dbName) {
        Pattern pattern = Pattern.compile(dbNameRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(dbName);
        return matcher.matches();
    }

    public static boolean clusterName(String clusterName) {
        if (!clusterName.trim().contains("-")) {
            return false;
        }
        //全是小写英文字母
        String cluster = clusterName.split("-")[0];
//        大小写英文字母
        String dbName = clusterName.split("-")[1];
        Pattern clusterPattern = Pattern.compile(clusterRegex);
        Matcher clusterMatcher = clusterPattern.matcher(cluster);
        Pattern dbNamePatter = Pattern.compile(dbNameRegex, Pattern.CASE_INSENSITIVE);
        Matcher dbNameMatcher = dbNamePatter.matcher(dbName);
        return clusterMatcher.matches() && dbNameMatcher.matches();
    }

}
