package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pcxie on 2019/3/25.
 */
@Component
@AllArgsConstructor
public class RegexMatcher {

    private final ConfigService configService;

    public boolean password(String password) {
        Pattern pattern = Pattern.compile(configService.getPasswordRegex());
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean userId(String userName) {
        Pattern pattern = Pattern.compile(configService.getUserNameRegex());
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }

    public boolean ip(String ip) {
        return ipv4(ip) || ipv6(ip);
    }

    public boolean domain(String domain) {
        Pattern pattern = Pattern.compile(configService.getDomainRegex());
        Matcher matcher = pattern.matcher(domain);
        return matcher.matches();
    }

    public boolean ipv6(String ipv6) {
        Pattern pattern = Pattern.compile(configService.getIpv6Regex());
        Matcher matcher = pattern.matcher(ipv6);
        return matcher.matches();
    }

    public boolean ipv4(String ipv4) {
        Pattern pattern = Pattern.compile(configService.getIpv4Regex());
        Matcher matcher = pattern.matcher(ipv4);
        return matcher.matches();
    }

    public boolean port(String port) {
        Pattern pattern = Pattern.compile(configService.getPortRegex());
        Matcher matcher = pattern.matcher(port);
        return matcher.matches();
    }

    public boolean clusterType(String clusterType) {
        if (clusterType.equalsIgnoreCase("Replication") || clusterType.equalsIgnoreCase("Sharding")) {
            return true;
        }
        return false;
    }

    public boolean checkOperateType(String operateType) {
        if (operateType.equalsIgnoreCase(Constants.OPERATION_WRITE) || operateType.equalsIgnoreCase(Constants.OPERATION_READ)) {
            return true;
        }
        return false;
    }

    public boolean dbName(String dbName) {
        Pattern pattern = Pattern.compile(configService.getDbNameRegex());
        Matcher matcher = pattern.matcher(dbName);
        return matcher.matches();
    }

    public boolean clusterName(String clusterName) {
        Pattern pattern = Pattern.compile(configService.getClusterNameRegex());
        Matcher matcher = pattern.matcher(clusterName);
        return matcher.matches();
    }
}
