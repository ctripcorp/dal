package com.ctrip.platform.dal.daogen.config;

import com.ctrip.platform.dal.daogen.util.EmailUtils;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;

/**
 * Created by taochen on 2019/8/6.
 */
public class MonitorConfigManager {
    private static final String QCONFIG_KEY = "monitorConfig";

    private static final int RETRY_TIME = 3;

    private static MonitorConfig monitorConfig = null;

    static {
        initMonitorConfig();
    }

    private static void initMonitorConfig() {
        MapConfig config = null;
        for (int i = 0; i < RETRY_TIME; ++i) {
            config = MapConfig.get(String.valueOf(EmailUtils.getLocalAppID()), QCONFIG_KEY, null);
            if (config != null) {
                break;
            }
        }
        Map<String, String> map = config.asMap();
        monitorConfig = new MonitorConfig(map);
        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> stringStringMap) {
                monitorConfig = new MonitorConfig(stringStringMap);
            }
        });
    }

    public static MonitorConfig getMonitorConfig() {
        return monitorConfig;
    }
}
