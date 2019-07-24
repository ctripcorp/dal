package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.dianping.cat.Cat;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.plugin.QconfigService;

import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/7/19.
 */
public class PluginConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(PluginConfigManager.class);
    private static final String PLUGIN_ENV = "pro,uat,fat";
    private QconfigService qconfigService;
    private Map<String, PluginConfig> pluginConfigCache = Maps.newHashMap();

    public PluginConfigManager(QconfigService qconfigService) {
        this.qconfigService = qconfigService;
        fillCache(pluginConfigCache);
    }

    public PluginConfig getPluginConfig(EnvProfile envProfile) {
        PluginConfig pluginConfig = null;
        if (envProfile != null && !Strings.isNullOrEmpty(envProfile.formatTopProfile())) {
            pluginConfig = pluginConfigCache.get(envProfile.formatTopProfile());
        } else {
            Cat.logError("TitanQConfigPlugin.Config", new RuntimeException("getPluginConfig(): profile is null or empty!"));
        }

        return pluginConfig;
    }

    private void fillCache(Map<String, PluginConfig> pluginConfigCache) {
        logger.info("fillCache(): fill cache data begin ...");
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        List<String> pluginConfigEnv = splitter.splitToList(PLUGIN_ENV);
        for (String env : pluginConfigEnv) {
            EnvProfile envProfile = new EnvProfile(env);
            PluginConfig pluginConfig = new PluginConfig(qconfigService, envProfile);
            pluginConfigCache.put(envProfile.formatTopProfile(), pluginConfig);
        }
        logger.info("fillCache(): fill cache data end ...");
    }

}
