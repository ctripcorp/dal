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
    private static final String KEY_TITAN_PLUGIN_URL = "titanPluginUrl";
    private static final String KEY_ALLOWED_IPS = "allowedIps";
    private static final String KEY_SECRET_SERVICE_URL = "secretServiceUrl";
    private static final String KEY_SSL_CODE = "sslCode";

    // qconfig default value
    private static final String DEFAULT_TITAN_PLUGIN_URL = "http://qconfig.ctripcorp.com/plugins/titan/config?appid=100010061";

    // qconfig key
    private volatile String titanPluginUrl;
    private volatile Set<String> allowedIps;
    private volatile String secretServiceUrl;
    private volatile String sslCode;

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
        titanPluginUrl = configMap.getString(KEY_TITAN_PLUGIN_URL, DEFAULT_TITAN_PLUGIN_URL);
        allowedIps = string2Set(configMap.getString(KEY_ALLOWED_IPS, ""));
        secretServiceUrl = configMap.getString(KEY_SECRET_SERVICE_URL, "");
        sslCode = configMap.getString(KEY_SSL_CODE, "");
    }

    private Set<String> string2Set(String s) {
        if (StringUtils.isNotBlank(s)) {
            return Sets.newHashSet(s.split(DELIMITER));
        }
        return null;
    }

    public String getTitanPluginUrl() {
        return titanPluginUrl;
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

}
