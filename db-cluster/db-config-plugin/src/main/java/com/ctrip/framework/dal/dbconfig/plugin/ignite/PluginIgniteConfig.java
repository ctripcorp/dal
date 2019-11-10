package com.ctrip.framework.dal.dbconfig.plugin.ignite;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;


public class PluginIgniteConfig {
    private static Logger logger = LoggerFactory.getLogger(PluginIgniteConfig.class);
    private static Map<String, String> igniteConfigMap;

    private static PluginIgniteConfig INSTANCE;

    private PluginIgniteConfig() {
        init();
    }

    public static PluginIgniteConfig getInstance() {
        if (INSTANCE == null) {
            synchronized (PluginIgniteConfig.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PluginIgniteConfig();
                }
            }
        }
        return INSTANCE;
    }

    private void init() {
        try {
            MapConfig config = MapConfig.get(TitanConstants.TITAN_QCONFIG_PLUGIN_APPID, TitanConstants.TITAN_QCONFIG_PLUGIN_IGNITE_FILE, Feature.DEFAULT);
            igniteConfigMap = config.asMap();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("init(): configMap init fail!");
            sb.append(" group=").append(TitanConstants.TITAN_QCONFIG_PLUGIN_APPID);
            sb.append(", dataId=").append(TitanConstants.TITAN_QCONFIG_PLUGIN_IGNITE_FILE);
            String errMsg = sb.toString();
            logger.error(errMsg, e);
            Cat.logError(errMsg, e);
        }
    }


    //Get ignite config param value
    public String getIgniteParamValue(String key) {
        String value = null;
        if (igniteConfigMap != null) {
            value = igniteConfigMap.get(key);
        }
        return value;
    }

}
