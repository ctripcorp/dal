package com.ctrip.framework.plugin.test.application.service;

import com.dianping.cat.Cat;
import org.springframework.stereotype.Service;
import qunar.agile.Conf;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import javax.annotation.PostConstruct;
import java.util.Map;

import static com.ctrip.framework.plugin.test.application.util.Constants.CONFIG_FILE_NAME;

/**
 * Created by shenjie on 2019/3/5.
 */
@Service
public class ConfigService {

    // qconfig key
    private static final String KEY_TITAN_KEY_NAME = "titanKeyName";
    private static final String KEY_MONGO_CLUSTER_NAME = "mongoClusterName";
    private static final String KEY_HTTPS_ENABLE = "httpsEnable";

    // qconfig default value
    private static final String DEFAULT_TITAN_KEY_NAME = "daltestdb_W";
    private static final String DEFAULT_MONGO_CLUSTER_NAME = "testmongocluster";
    private static final boolean DEFAULT_HTTPS_ENABLE = true;

    // qconfig key
    private volatile String titanKeyName;
    private volatile String mongoClusterName;
    private volatile boolean httpsEnable;

    @PostConstruct
    private void init() throws Exception {
        // init for config
        try {
            MapConfig config = MapConfig.get(CONFIG_FILE_NAME);
            configInit(config.asMap());
            config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
                @Override
                public void onLoad(Map<String, String> conf) {
                    configInit(conf);
                }
            });
        } catch (Exception e) {
            Cat.logError("ConfigService init failed", e);
            throw new Exception("ConfigService init failed", e);
        }
    }

    private void configInit(Map<String, String> conf) {
        Conf configMap = Conf.fromMap(conf);
        titanKeyName = configMap.getString(KEY_TITAN_KEY_NAME, DEFAULT_TITAN_KEY_NAME);
        mongoClusterName = configMap.getString(KEY_MONGO_CLUSTER_NAME, DEFAULT_MONGO_CLUSTER_NAME);
        httpsEnable = configMap.getBoolean(KEY_HTTPS_ENABLE, DEFAULT_HTTPS_ENABLE);
    }

    public String getTitanKeyName() {
        return titanKeyName;
    }

    public String getMongoClusterName() {
        return mongoClusterName;
    }

    public boolean isEnableHttps() {
        return httpsEnable;
    }
}
