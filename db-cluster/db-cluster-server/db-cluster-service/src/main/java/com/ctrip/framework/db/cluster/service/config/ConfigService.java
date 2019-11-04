package com.ctrip.framework.db.cluster.service.config;

import com.ctrip.framework.db.cluster.util.Constants;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import qunar.agile.Conf;
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
    private static final String KEY_PLUGIN_DAL_URL = "pluginDalUrl";
    private static final String KEY_ALLOWED_IPS = "allowedIps";
    private static final String KEY_SECRET_SERVICE_URL = "secretServiceUrl";
    private static final String KEY_SSL_CODE = "sslCode";
    private static final String KEY_DB_CONNECTION_CHECK_URL = "dbConnectionCheckUrl";
    private static final String KEY_DB_CONNECTION_CHECK_ENABLED_RELEASE_TYPES = "dbConnectionCheckEnabledReleaseTypes";
    private static final String KEY_PLUGIN_RETRY_TIMES = "pluginRetryTimes";
    private static final String KEY_MAIL_RECEIVERS = "mailReceivers";
    private static final String KEY_HTTP_READ_TIMEOUT_IN_MS = "httpReadTimeoutInMs";

    private static final String KEY_CLUSTER_NAME_REGEX = "clusterNameRegex";
    private static final String KEY_DB_NAME_REGEX = "dbNameRegex";
    private static final String KEY_USER_NAME_REGEX = "userNameRegex";
    private static final String KEY_PASSWORD_REGEX = "passWordRegex";
    private static final String KEY_DOMAIN_REGEX = "domainRegex";
    private static final String KEY_PORT_REGEX = "portRegex";
    private static final String KEY_IPV4_REGEX = "ipv4Regex";
    private static final String KEY_IPV6_REGEX = "ipv6Regex";
    // TODO: 2019/11/1 临时
    private static final String KEY_QCONFIG_PLUGIN_SWITCH = "qconfigPluginSwitch";



    // qconfig default value
    private static final String DEFAULT_PLUGIN_TITAN_URL = "http://qconfig.ctripcorp.com/plugins/titan/config";
    private static final String DEFAULT_PLUGIN_MONGO_URL = "http://qconfig.ctripcorp.com/plugins/mongo/config";
    private static final String DEFAULT_PLUGIN_DAL_URL = "http://qconfig.ctripcorp.com/plugins/dal/config";
    private static final String DEFAULT_DB_CONNECTION_CHECK_URL = "http://mysqlapi.db.ctripcorp.com:8080/database/checktitanconnect";
    private static final String DEFAULT_DB_CONNECTION_CHECK_ENABLED_RELEASE_TYPES = "normal_release";
    private static final int DEFAULT_PLUGIN_RETRY_TIMES = 1;
    private static final String DEFAULT_MAIL_RECEIVERS = "shenjie@ctrip.com";
    private static final int DEFAULT_HTTP_READ_TIMEOUT_IN_MS = 10000;

    private static final String DEFAULT_CLUSTER_NAME_REGEX = "^[a-zA-Z0-9_-]+$";
    private static final String DEFAULT_DB_NAME_REGEX = "^[a-zA-Z0-9_-]+$";
    private static final String DEFAULT_USER_NAME_REGEX = "^[a-zA-Z0-9_-]+$";
    private static final String DEFAULT_PASSWORD_REGEX = "^[\\x00-\\x7F]+$";
    private static final String DEFAULT_DOMAIN_REGEX = "^[a-zA-Z0-9._-]+$";
    private static final String DEFAULT_PORT_REGEX = "^[0-9]{1,5}$";
    private static final String DEFAULT_IPV4_REGEX = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    private static final String DEFAULT_IPV6_REGEX = "^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}|:((:[\\da−fA−F]1,4)1,6|:)|:((:[\\da−fA−F]1,4)1,6|:)|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?|([\\da−fA−F]1,4:)6:|([\\da−fA−F]1,4:)6:";
    // TODO: 2019/11/1 临时
    private static final boolean DEFAULT_QCONFIG_PLUGIN_SWITCH = false;


    // qconfig key
    private volatile String pluginTitanUrl;
    private volatile String pluginMongoUrl;
    private volatile String pluginDalUrl;
    private volatile Set<String> allowedIps;
    private volatile String secretServiceUrl;
    private volatile String sslCode;
    private volatile String dbConnectionCheckUrl;
    private volatile Set<String> dbConnectionCheckEnabledReleaseTypes;
    private volatile int pluginReTryTimes;
    private volatile Set<String> mailReceivers;
    private volatile int httpReadTimeoutInMs;

    // 正则表达式
    private volatile String clusterNameRegex;
    private volatile String dbNameRegex;
    private volatile String userNameRegex;
    private volatile String passWordRegex;
    private volatile String domainRegex;
    private volatile String portRegex;
    private volatile String ipv4Regex;
    private volatile String ipv6Regex;
    // TODO: 2019/11/1 临时
    private volatile boolean qconfigPluginSwitch;

    @PostConstruct
    private void init() {
        // init for config
        MapConfig config = MapConfig.get(Constants.CONFIG_FILE_NAME);
        configInit(config.asMap());
        config.addListener(this::configInit);
    }

    private void configInit(Map<String, String> conf) {
        Conf configMap = Conf.fromMap(conf);
        pluginTitanUrl = configMap.getString(KEY_PLUGIN_TITAN_URL, DEFAULT_PLUGIN_TITAN_URL);
        pluginMongoUrl = configMap.getString(KEY_PLUGIN_MONGO_URL, DEFAULT_PLUGIN_MONGO_URL);
        pluginDalUrl = configMap.getString(KEY_PLUGIN_DAL_URL, DEFAULT_PLUGIN_DAL_URL);
        allowedIps = string2Set(configMap.getString(KEY_ALLOWED_IPS, ""));
        secretServiceUrl = configMap.getString(KEY_SECRET_SERVICE_URL, "");
        sslCode = configMap.getString(KEY_SSL_CODE, "");
        dbConnectionCheckUrl = configMap.getString(KEY_DB_CONNECTION_CHECK_URL, DEFAULT_DB_CONNECTION_CHECK_URL);
        dbConnectionCheckEnabledReleaseTypes = string2Set(
                configMap.getString(KEY_DB_CONNECTION_CHECK_ENABLED_RELEASE_TYPES, DEFAULT_DB_CONNECTION_CHECK_ENABLED_RELEASE_TYPES)
        );
        pluginReTryTimes = configMap.getInt(KEY_PLUGIN_RETRY_TIMES, DEFAULT_PLUGIN_RETRY_TIMES);
        mailReceivers = string2Set(configMap.getString(KEY_MAIL_RECEIVERS, DEFAULT_MAIL_RECEIVERS));
        httpReadTimeoutInMs = configMap.getInt(KEY_HTTP_READ_TIMEOUT_IN_MS, DEFAULT_HTTP_READ_TIMEOUT_IN_MS);

        clusterNameRegex = configMap.getString(KEY_CLUSTER_NAME_REGEX, DEFAULT_CLUSTER_NAME_REGEX);
        dbNameRegex = configMap.getString(KEY_DB_NAME_REGEX, DEFAULT_DB_NAME_REGEX);
        userNameRegex = configMap.getString(KEY_USER_NAME_REGEX, DEFAULT_USER_NAME_REGEX);
        passWordRegex = configMap.getString(KEY_PASSWORD_REGEX, DEFAULT_PASSWORD_REGEX);
        domainRegex = configMap.getString(KEY_DOMAIN_REGEX, DEFAULT_DOMAIN_REGEX);
        portRegex = configMap.getString(KEY_PORT_REGEX, DEFAULT_PORT_REGEX);
        ipv4Regex = configMap.getString(KEY_IPV4_REGEX, DEFAULT_IPV4_REGEX);
        ipv6Regex = configMap.getString(KEY_IPV6_REGEX, DEFAULT_IPV6_REGEX);

        // TODO: 2019/11/1 临时
        qconfigPluginSwitch = configMap.getBoolean(KEY_QCONFIG_PLUGIN_SWITCH, DEFAULT_QCONFIG_PLUGIN_SWITCH);
    }

    private Set<String> string2Set(String s) {
        if (StringUtils.isNotBlank(s)) {
            return Sets.newHashSet(s.split(DELIMITER));
        }
        return Sets.newHashSet();
    }

    public String getPluginTitanUrl() {
        return pluginTitanUrl;
    }

    public String getPluginMongoUrl() {
        return pluginMongoUrl;
    }

    public String getPluginDalUrl() {
        return pluginDalUrl;
    }

    public String getDBConnectionCheckUrl() {
        return dbConnectionCheckUrl;
    }

    public Set<String> getDbConnectionCheckEnabledReleaseTypes() {
        return dbConnectionCheckEnabledReleaseTypes;
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

    public String getDomainRegex() {
        return domainRegex;
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

    public int getPluginReTryTimes() {
        return pluginReTryTimes;
    }

    public Set<String> getMailReceivers() {
        return mailReceivers;
    }

    public int getHttpReadTimeoutInMs() {
        return httpReadTimeoutInMs;
    }

    public boolean isQconfigPluginSwitch() {
        return qconfigPluginSwitch;
    }
}
