package com.ctrip.framework.db.cluster.config;

import com.ctrip.framework.db.cluster.util.Constants;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import qunar.agile.Conf;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 * Created by shenjie on 2019/3/5.
 */
@Service
public class ConfigService {

    private static final String DELIMITER = ",";

    // qconfig key
    private static final String KEY_PLUGIN_TITAN_URL = "pluginTitanUrl";
    private static final String KEY_PLUGIN_MONGO_URL = "pluginMongoUrl";
    private static final String KEY_ALLOWED_IPS = "allowedIps";
    private static final String KEY_SECRET_SERVICE_URL = "secretServiceUrl";
    private static final String KEY_SSL_CODE = "sslCode";

    // 正则表达式
    private static final String KEY_CLUSTER_NAME_REGEX = "clusterNameRegex";
    private static final String KEY_DB_NAME_REGEX = "dbNameRegex";
    private static final String KEY_USER_NAME_REGEX = "userNameRegex";
    private static final String KEY_PASSWORD_REGEX = "passWordRegex";
    private static final String KEY_HOST_NAME_REGEX = "hostNameRegex";
    private static final String KEY_IPV4_REGEX = "ipv4Regex";
    private static final String KEY_IPV6_REGEX = "ipv6Regex";
    private static final String KEY_PORT_REGEX = "portRegex";

    // qconfig default value
    private static final String DEFAULT_PLUGIN_TITAN_URL = "http://qconfig.ctripcorp.com/plugins/titan/config";
    private static final String DEFAULT_PLUGIN_MONGO_URL = "http://qconfig.ctripcorp.com/plugins/mongo/config";

    // 正则表达式
    private static final String DEFAULT_CLUSTER_NAME_REGEX = "^[a-z]+$";
    private static final String DEFAULT_DB_NAME_REGEX = "[a-zA-Z]{1,}+";
    private static final String DEFAULT_USER_NAME_REGEX = "^[a-zA-Z][a-zA-Z_]{1,}$";
    private static final String DEFAULT_PASSWORD_REGEX = "^\\w+$";
    private static final String DEFAULT_HOST_NAME_REGEX = "(((https|http)?://)?([a-z0-9]+[.])|(www.))\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";
    private static final String DEFAULT_IPV4_REGEX = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    private static final String DEFAULT_IPV6_REGEX = "^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}|:((:[\\da−fA−F]1,4)1,6|:)|:((:[\\da−fA−F]1,4)1,6|:)|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?|([\\da−fA−F]1,4:)6:|([\\da−fA−F]1,4:)6:";
    private static final String DEFAULT_PORT_REGEX = "^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)";

    // qconfig key
    private volatile String pluginTitanUrl;
    private volatile String pluginMongoUrl;
    private volatile Set<String> allowedIps;
    private volatile String secretServiceUrl;
    private volatile String sslCode;

    // 正则表达式
    private volatile String clusterNameRegex;
    private volatile String dbNameRegex;
    private volatile String userNameRegex;
    private volatile String passWordRegex;
    private volatile String hostNameRegex;
    private volatile String ipv4Regex;
    private volatile String ipv6Regex;
    private volatile String portRegex;

    @PostConstruct
    private void init() {
        // init for config
        MapConfig config = MapConfig.get(Constants.CONFIG_FILE_NAME);
        configInit(config.asMap());
        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> conf) {
                configInit(conf);
            }
        });
    }

    private void configInit(Map<String, String> conf) {
        Conf configMap = Conf.fromMap(conf);
        pluginTitanUrl = configMap.getString(KEY_PLUGIN_TITAN_URL, DEFAULT_PLUGIN_TITAN_URL);
        pluginMongoUrl = configMap.getString(KEY_PLUGIN_MONGO_URL, DEFAULT_PLUGIN_MONGO_URL);
        allowedIps = string2Set(configMap.getString(KEY_ALLOWED_IPS, ""));
        secretServiceUrl = configMap.getString(KEY_SECRET_SERVICE_URL, "");
        sslCode = configMap.getString(KEY_SSL_CODE, "");

        clusterNameRegex = configMap.getString(KEY_CLUSTER_NAME_REGEX, DEFAULT_CLUSTER_NAME_REGEX);
        dbNameRegex = configMap.getString(KEY_DB_NAME_REGEX, DEFAULT_DB_NAME_REGEX);
        userNameRegex = configMap.getString(KEY_USER_NAME_REGEX, DEFAULT_USER_NAME_REGEX);
        passWordRegex = configMap.getString(KEY_PASSWORD_REGEX, DEFAULT_PASSWORD_REGEX);
        hostNameRegex = configMap.getString(KEY_HOST_NAME_REGEX, DEFAULT_HOST_NAME_REGEX);
        ipv4Regex = configMap.getString(KEY_IPV4_REGEX, DEFAULT_IPV4_REGEX);
        ipv6Regex = configMap.getString(KEY_IPV6_REGEX, DEFAULT_IPV6_REGEX);
        portRegex = configMap.getString(KEY_PORT_REGEX, DEFAULT_PORT_REGEX);

    }

    private Set<String> string2Set(String s) {
        if (StringUtils.isNotBlank(s)) {
            return Sets.newHashSet(s.split(DELIMITER));
        }
        return null;
    }

    public String getPluginTitanUrl() {
        return pluginTitanUrl;
    }

    public String getPluginMongoUrl() {
        return pluginMongoUrl;
    }


    public Set<String> getAllowedIps() {
        return allowedIps;
    }

    public String getSecretServiceUrl() {
        return secretServiceUrl;
    }

    public String getSslCode() {
        return sslCode;
    }

    public String getClusterNameRegex() {
        return clusterNameRegex;
    }

    public String getDbNameRegex() {
        return dbNameRegex;
    }

    public String getUserNameRegex() {
        return userNameRegex;
    }

    public String getPasswordRegex() {
        return passWordRegex;
    }

    public String getHostNameRegex() {
        return hostNameRegex;
    }

    public String getIpv4Regex() {
        return ipv4Regex;
    }

    public String getIpv6Regex() {
        return ipv6Regex;
    }

    public String getPortRegex() {
        return portRegex;
    }

}
