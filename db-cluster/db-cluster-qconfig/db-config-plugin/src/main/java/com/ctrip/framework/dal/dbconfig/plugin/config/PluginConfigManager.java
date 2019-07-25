package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.dianping.cat.Cat;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.plugin.QconfigService;

import java.util.Map;

/**
 * Created by shenjie on 2019/7/19.
 */
public class PluginConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(PluginConfigManager.class);
    private QconfigService qconfigService;
    private final Map<String, PluginConfig> pluginConfigCache = Maps.newConcurrentMap();
    private volatile static PluginConfigManager pluginConfigManager = null;

    private PluginConfigManager(QconfigService qconfigService) {
        this.qconfigService = qconfigService;
    }

    public static PluginConfigManager getInstance(QconfigService qconfigService) {
        if (null == pluginConfigManager) {
            synchronized (PluginConfigManager.class) {
                if (null == pluginConfigManager) {
                    pluginConfigManager = new PluginConfigManager(qconfigService);
                }
            }
        }
        return pluginConfigManager;
    }

    public PluginConfig getPluginConfig(EnvProfile envProfile) throws Exception {
        PluginConfig pluginConfig = null;
        if (envProfile != null && !Strings.isNullOrEmpty(envProfile.formatTopProfile())) {
            pluginConfig = getFromCache(envProfile);
        } else {
            DbConfigPluginException exception = new DbConfigPluginException("getPluginConfig(): profile is null or empty!");
            Cat.logError("TitanQconfigPlugin.Config", exception);
            throw exception;
        }

        return pluginConfig;
    }

    private PluginConfig getFromCache(EnvProfile envProfile) throws Exception {
        String topProfile = envProfile.formatTopProfile();
        PluginConfig pluginConfig = pluginConfigCache.get(topProfile);
        if (null == pluginConfig) {
            synchronized (pluginConfigCache) {
                pluginConfig = pluginConfigCache.get(topProfile);
                if (null == pluginConfig) {
                    logger.info("getFromCache(): initialize [{}] plugin config begin ...", topProfile);
                    pluginConfig = new PluginConfig(qconfigService, envProfile);
                    pluginConfigCache.put(topProfile, pluginConfig);
                    logger.info("getFromCache(): initialize [{}] plugin config end ...", topProfile);
                }
            }
        }
        return pluginConfig;
    }

}
