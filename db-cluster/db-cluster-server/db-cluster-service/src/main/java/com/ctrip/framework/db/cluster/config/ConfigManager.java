package com.ctrip.framework.db.cluster.config;

import com.ctrip.framework.db.cluster.util.Constants;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import qunar.agile.Conf;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;
import java.util.Set;

/**
 * Created by shenjie on 2019/3/5.
 */
public class ConfigManager {

    private static final String DELIMITER = ",";
    private static final ConfigManager instance = new ConfigManager();

    // qconfig key
    private static final String KEY_TITAN_PLUGIN_URL = "titanPluginUrl";
    private static final String KEY_ALLOWED_IPS = "allowedIps";

    // qconfig default value
    private static final String DEFAULT_TITAN_PLUGIN_URL = "http://qconfig.ctripcorp.com/plugins/titan/config?appid=100010061";

    // qconfig key
    private volatile String titanPluginUrl;
    private volatile Set<String> allowedIps;

    private ConfigManager() {
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

    public static ConfigManager getInstance() {
        return instance;
    }

    private void configInit(Map<String, String> conf) {
        Conf configMap = Conf.fromMap(conf);
        titanPluginUrl = configMap.getString(KEY_TITAN_PLUGIN_URL, DEFAULT_TITAN_PLUGIN_URL);
        allowedIps = string2Set(configMap.getString(KEY_ALLOWED_IPS, ""));
    }

    private Set<String> string2Set(String s) {
        if (StringUtils.isNotBlank(s)) {
            return Sets.newHashSet(s.split(DELIMITER));
        }
        return null;
    }

    public String getKeyTitanPluginUrl() {
        return titanPluginUrl;
    }

    public Set<String> getAllowedIps() {
        return allowedIps;
    }

}
