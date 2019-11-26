package com.ctrip.framework.db.cluster.service.config;

import com.ctrip.framework.db.cluster.util.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import qunar.agile.Conf;
import qunar.tc.qconfig.client.MapConfig;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by shenjie on 2019/3/5.
 */
@Service
public class ConfigService {

    private static final String DELIMITER = ",";
    private static final String COLON = ":";

    // qconfig key
    private static final String KEY_PLUGIN_TITAN_URL = "pluginTitanUrl";
    private static final String KEY_PLUGIN_MONGO_URL = "pluginMongoUrl";
    private static final String KEY_PLUGIN_DAL_URL = "pluginDalUrl";
    private static final String KEY_QCONFIG_REST_API_URL = "qconfigRestApiUrl";
    private static final String KEY_QCONFIG_REST_API_TOKEN = "qconfigRestApiToken";
    private static final String KEY_ALLOWED_IPS = "allowedIps";
    private static final String KEY_SECRET_SERVICE_URL = "secretServiceUrl";
    private static final String KEY_SSL_CODE = "sslCode";
    private static final String KEY_DB_CONNECTION_CHECK_URL = "dbConnectionCheckUrl";
    private static final String KEY_DB_CONNECTION_CHECK_ENABLED_RELEASE_TYPES = "dbConnectionCheckEnabledReleaseTypes";
    private static final String KEY_PLUGIN_RETRY_TIMES = "pluginRetryTimes";
    private static final String KEY_MAIL_RECEIVERS = "mailReceivers";
    private static final String KEY_HTTP_READ_TIMEOUT_IN_MS = "httpReadTimeoutInMs";
    private static final String KEY_FRESHNESS_ENABLED = "freshnessEnabled";
    private static final String KEY_FRESHNESS_CLUSTER_ENABLED_AND_THRESHOLD_SECOND = "freshnessClusterEnabledAndThresholdSecond";
    private static final String KEY_TITAN_KEY_SYNCHRONIZE_SCHEDULE_DELAY_MINUTES = "titanKeySynchronizeScheduleDelayMinutes";
    private static final String KEY_TITAN_KEY_SYNCHRONIZE_SCHEDULE_PAGE_SIZE = "titanKeySynchronizeSchedulePageSize";
    private static final String KEY_ZONES = "zones";
    private static final String KEY_MERGE_ZONE = "mergeZone";

    private static final String KEY_CLUSTER_NAME_REGEX = "clusterNameRegex";
    private static final String KEY_DB_NAME_REGEX = "dbNameRegex";
    private static final String KEY_USER_NAME_REGEX = "userNameRegex";
    private static final String KEY_PASSWORD_REGEX = "passWordRegex";
    private static final String KEY_DOMAIN_REGEX = "domainRegex";
    private static final String KEY_PORT_REGEX = "portRegex";
    private static final String KEY_IPV4_REGEX = "ipv4Regex";
    private static final String KEY_IPV6_REGEX = "ipv6Regex";

    // qconfig default value
    private static final String DEFAULT_PLUGIN_TITAN_URL = "http://qconfig.ctripcorp.com/plugins/titan";
    private static final String DEFAULT_PLUGIN_MONGO_URL = "http://qconfig.ctripcorp.com/plugins/mongo/config";
    private static final String DEFAULT_PLUGIN_DAL_URL = "http://qconfig.ctripcorp.com/plugins/dal/config";
    private static final String DEFAULT_QCONFIG_REST_API_URL = "http://qconfig.ctripcorp.com/restapi";
    private static final String DEFAULT_QCONFIG_REST_API_TOKEN = "4860EB8C49F291DC5D5AB96E7812CB86";
    private static final String DEFAULT_DB_CONNECTION_CHECK_URL = "http://mysqlapi.db.ctripcorp.com:8080/database/checktitanconnect";
    private static final String DEFAULT_DB_CONNECTION_CHECK_ENABLED_RELEASE_TYPES = "normal_release";
    private static final int DEFAULT_PLUGIN_RETRY_TIMES = 1;
    private static final String DEFAULT_MAIL_RECEIVERS = "shenjie@ctrip.com";
    private static final int DEFAULT_HTTP_READ_TIMEOUT_IN_MS = 10000;
    private static final boolean DEFAULT_FRESHNESS_ENABLED = true;
    private static final String DEFAULT_FRESHNESS_CLUSTER_ENABLED_AND_THRESHOLD_SECOND = ""; // example:"cluster1:5,cluster2:2,cluster3:10"
    private static final int DEFAULT_TITAN_KEY_SYNCHRONIZE_SCHEDULE_DELAY_MINUTES = 1;
    private static final int DEFAULT_TITAN_KEY_SYNCHRONIZE_SCHEDULE_PAGE_SIZE = 5000;
    private static final String DEFAULT_ZONES = "shajq,shaoy,shafq,sharb";
    // TODO: 2019/11/26 update dynamic release
    private static final String DEFAULT_MERGE_ZONE = "shajq,sharb"; // example:"shajq,sharb;shafq,shaoy,xxx" 金桥日坂机房不区分, 福泉欧阳xxx机房三者不区分

    private static final String DEFAULT_CLUSTER_NAME_REGEX = "^[a-zA-Z0-9_-]+$";
    private static final String DEFAULT_DB_NAME_REGEX = "^[a-zA-Z0-9_-]+$";
    private static final String DEFAULT_USER_NAME_REGEX = "^[a-zA-Z0-9_-]+$";
    private static final String DEFAULT_PASSWORD_REGEX = "^[\\x00-\\x7F]+$";
    private static final String DEFAULT_DOMAIN_REGEX = "^[a-zA-Z0-9._-]+$";
    private static final String DEFAULT_PORT_REGEX = "^[0-9]{1,5}$";
    private static final String DEFAULT_IPV4_REGEX = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
    private static final String DEFAULT_IPV6_REGEX = "^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}|:((:[\\da−fA−F]1,4)1,6|:)|:((:[\\da−fA−F]1,4)1,6|:)|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?|([\\da−fA−F]1,4:)6:|([\\da−fA−F]1,4:)6:";

    // qconfig key
    private volatile String pluginTitanUrl;
    private volatile String pluginMongoUrl;
    private volatile String pluginDalUrl;
    private volatile String qconfigRestApiUrl;
    private volatile String qconfigRestApiToken;
    private volatile Set<String> allowedIps;
    private volatile String secretServiceUrl;
    private volatile String sslCode;
    private volatile String dbConnectionCheckUrl;
    private volatile Set<String> dbConnectionCheckEnabledReleaseTypes;
    private volatile int pluginReTryTimes;
    private volatile Set<String> mailReceivers;
    private volatile int httpReadTimeoutInMs;
    private volatile boolean freshnessEnabled;
    private volatile Map<String, Integer> freshnessClusterEnabledAndThresholdSecond;
    private volatile int titanKeySynchronizeScheduleDelayMinutes;
    private volatile int titanKeySynchronizeSchedulePageSize;
    private volatile Set<String> zones;
    private volatile Set<String> mergeZones;

    // 正则表达式
    private volatile String clusterNameRegex;
    private volatile String dbNameRegex;
    private volatile String userNameRegex;
    private volatile String passWordRegex;
    private volatile String domainRegex;
    private volatile String portRegex;
    private volatile String ipv4Regex;
    private volatile String ipv6Regex;

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
        qconfigRestApiUrl = configMap.getString(KEY_QCONFIG_REST_API_URL, DEFAULT_QCONFIG_REST_API_URL);
        qconfigRestApiToken = configMap.getString(KEY_QCONFIG_REST_API_TOKEN, DEFAULT_QCONFIG_REST_API_TOKEN);
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
        freshnessEnabled = configMap.getBoolean(KEY_FRESHNESS_ENABLED, DEFAULT_FRESHNESS_ENABLED);
        freshnessClusterEnabledAndThresholdSecond = convertClusterFreshnessThresholdSecond(
                configMap.getString(KEY_FRESHNESS_CLUSTER_ENABLED_AND_THRESHOLD_SECOND, DEFAULT_FRESHNESS_CLUSTER_ENABLED_AND_THRESHOLD_SECOND)
        );
        titanKeySynchronizeScheduleDelayMinutes = configMap.getInt(
                KEY_TITAN_KEY_SYNCHRONIZE_SCHEDULE_DELAY_MINUTES, DEFAULT_TITAN_KEY_SYNCHRONIZE_SCHEDULE_DELAY_MINUTES
        );
        titanKeySynchronizeSchedulePageSize = configMap.getInt(
                KEY_TITAN_KEY_SYNCHRONIZE_SCHEDULE_PAGE_SIZE, DEFAULT_TITAN_KEY_SYNCHRONIZE_SCHEDULE_PAGE_SIZE
        );
        zones = string2Set(configMap.getString(KEY_ZONES, DEFAULT_ZONES));
        mergeZones = string2Set(configMap.getString(KEY_MERGE_ZONE, DEFAULT_MERGE_ZONE));

        clusterNameRegex = configMap.getString(KEY_CLUSTER_NAME_REGEX, DEFAULT_CLUSTER_NAME_REGEX);
        dbNameRegex = configMap.getString(KEY_DB_NAME_REGEX, DEFAULT_DB_NAME_REGEX);
        userNameRegex = configMap.getString(KEY_USER_NAME_REGEX, DEFAULT_USER_NAME_REGEX);
        passWordRegex = configMap.getString(KEY_PASSWORD_REGEX, DEFAULT_PASSWORD_REGEX);
        domainRegex = configMap.getString(KEY_DOMAIN_REGEX, DEFAULT_DOMAIN_REGEX);
        portRegex = configMap.getString(KEY_PORT_REGEX, DEFAULT_PORT_REGEX);
        ipv4Regex = configMap.getString(KEY_IPV4_REGEX, DEFAULT_IPV4_REGEX);
        ipv6Regex = configMap.getString(KEY_IPV6_REGEX, DEFAULT_IPV6_REGEX);
    }

    Set<String> string2Set(String s) {
        if (StringUtils.isNotBlank(s)) {
            return Sets.newHashSet(s.split(DELIMITER)).stream()
                    .filter(StringUtils::isNotBlank).map(String::trim)
                    .collect(Collectors.toSet());
        }
        return Sets.newHashSet();
    }

    Map<String, Integer> convertClusterFreshnessThresholdSecond(final String str) {
        if (StringUtils.isBlank(str)) {
            return Maps.newHashMap();
        } else {
            final List<String> clusterThresholdPairs = Lists.newArrayList(str.split(DELIMITER)).stream()
                    .filter(StringUtils::isNotBlank).map(String::trim)
                    .collect(Collectors.toList());

            final Map<String, Integer> clusterThresholdMap = Maps.newHashMapWithExpectedSize(clusterThresholdPairs.size());
            clusterThresholdPairs.forEach(pair -> {
                final String[] split = pair.split(COLON);
                if (split.length == 2) {
                    clusterThresholdMap.put(split[0].trim(), Integer.valueOf(split[1].trim()));
                }
            });

            return clusterThresholdMap;
        }
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

    public String getQConfigRestApiUrl() {
        return qconfigRestApiUrl;
    }

    public String getQConfigRestApiToken() {
        return qconfigRestApiToken;
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

    public boolean getFreshnessEnabled() {
        return freshnessEnabled;
    }

    public Map<String, Integer> getFreshnessClusterEnabledAndThresholdSecond() {
        return freshnessClusterEnabledAndThresholdSecond;
    }

    public int getTitanKeySynchronizeScheduleDelayMinutes() {
        return titanKeySynchronizeScheduleDelayMinutes;
    }

    public int getTitanKeySynchronizeSchedulePageSize() {
        return titanKeySynchronizeSchedulePageSize;
    }

    public Set<String> getZones() {
        return zones;
    }

    public Set<String> getMergeZones() {
        return mergeZones;
    }
}
