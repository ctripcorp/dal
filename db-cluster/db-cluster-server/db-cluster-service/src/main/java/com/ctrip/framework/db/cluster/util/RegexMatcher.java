package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.db.cluster.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pcxie on 2019/3/25.
 */
@Component
public class RegexMatcher {

    @Autowired
    private ConfigService configService;

    public boolean password(String password) {
        Pattern pattern = Pattern.compile(configService.getPasswordRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean userId(String userName) {
        Pattern pattern = Pattern.compile(configService.getUserNameRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }

    public boolean host(String host) {
        return hostName(host) || ipv4(host) || ipv6(host);
    }

    public boolean hostName(String hostName) {
        Pattern pattern = Pattern.compile(configService.getHostNameRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(hostName);
        return matcher.matches();
    }

    public boolean ipv6(String ipv6) {
        Pattern pattern = Pattern.compile(configService.getIpv6Regex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ipv6);
        return matcher.matches();
    }

    public boolean ipv4(String ipv4) {
        Pattern pattern = Pattern.compile(configService.getIpv4Regex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ipv4);
        return matcher.matches();
    }

    public boolean port(String port) {
        Pattern pattern = Pattern.compile(configService.getPortRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(port);
        return matcher.matches();
    }

    public boolean clusterType(String clusterInfo) {
        if (clusterInfo.equalsIgnoreCase("Replication") || clusterInfo.equalsIgnoreCase("Sharding")) {
            return true;
        }
        return false;
    }

    public boolean dbName(String dbName) {
        Pattern pattern = Pattern.compile(configService.getDbNameRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(dbName);
        return matcher.matches();
    }

    public boolean clusterName(String clusterName) {
        Pattern pattern = Pattern.compile(configService.getClusterNameRegex());
        Matcher matcher = pattern.matcher(clusterName);
        return matcher.matches();
    }

}
