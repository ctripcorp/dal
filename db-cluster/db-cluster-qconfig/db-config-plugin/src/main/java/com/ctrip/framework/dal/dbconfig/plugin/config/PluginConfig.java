package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.plugin.ConfigDetail;
import qunar.tc.qconfig.plugin.ConfigField;
import qunar.tc.qconfig.plugin.QconfigService;

import java.util.List;
import java.util.Properties;

/**
 * @author c7ch23en
 */
public class PluginConfig {

    private static final Logger logger = LoggerFactory.getLogger(PluginConfig.class);
    private QconfigService qconfigService;
    private EnvProfile envProfile;
    private volatile Properties pluginConfigs = new Properties();
    private volatile long refreshIntervalMs;

    public PluginConfig(QconfigService qconfigService, EnvProfile envProfile) {
        this.qconfigService = qconfigService;
        this.envProfile = envProfile;

        refresh();
        refreshIntervalMs = getRefreshInterval();
        registerTimer();
    }

    public String getParamValue(String key) {
        String result = null;
        if (pluginConfigs != null && !pluginConfigs.isEmpty()) {
            result = pluginConfigs.getProperty(key);
        }

        return result;
    }

    public Properties getCurrentContentProp() {
        return pluginConfigs;
    }

    private void registerTimer() {
        RescheduleTimer rescheduleTimer = new RescheduleTimer();
        rescheduleTimer.schedule(new Runnable() {
            @Override
            public void run() {
                refresh();

                long refreshInterval = getRefreshInterval();
                if (refreshInterval != refreshIntervalMs) {
                    Cat.logEvent("Plugin.Config.Changed", String.format("%s:%s:%s",
                            envProfile.formatTopProfile(), refreshInterval, refreshIntervalMs));
                    logger.info("registerTimer(): [{}] remote refresh interval changed: newRefreshInterval: {}, oldRefreshInterval: {}.",
                            envProfile.formatTopProfile(), refreshInterval, refreshIntervalMs);
                    refreshIntervalMs = refreshInterval;
                    rescheduleTimer.reschedule(refreshInterval, refreshInterval);
                }
            }
        }, refreshIntervalMs, refreshIntervalMs);
    }

    private void refresh() {
        logger.info("refresh(): refresh [{}] plugin configs begin ...", envProfile.formatTopProfile());

        Properties newPluginConfigs = getPluginConfigs();
        Cat.logEvent("Plugin.Config.Refresh", envProfile.formatTopProfile());
        logger.info("refresh(): get [{}] new plugin configs, config size is {}", envProfile.formatTopProfile(), newPluginConfigs.size());

        if (newPluginConfigs != null && !newPluginConfigs.isEmpty()) {
            pluginConfigs = newPluginConfigs;
            logger.info("refresh(): [{}] new config replace old config success.", envProfile.formatTopProfile());
        } else {
            logger.warn("refresh(): [{}] new config is null or empty.", envProfile.formatTopProfile());
        }

        logger.info("refresh(): refresh [{}] plugin configs end ...", envProfile.formatTopProfile());
    }

    private long getRefreshInterval() {
        long refreshInterval = 1000;    // default: 1000ms
        String refreshIntervalInQConfig = null;
        try {
            refreshIntervalInQConfig = pluginConfigs.getProperty(CommonConstants.PLUGIN_CONFIG_REFRESH_INTERVAL_MS);
            if (!Strings.isNullOrEmpty(refreshIntervalInQConfig)) {
                refreshInterval = Long.parseLong(refreshIntervalInQConfig);
                logger.info("getRefreshInterval(): [{}] remote refresh interval is {}ms.", envProfile.formatTopProfile(), refreshInterval);
            }
        } catch (Exception e) {
            logger.warn("Get [" + envProfile.formatTopProfile() + "] plugin config (plugin.config.refresh.interval.ms) failed.", e);
        }
        return refreshInterval;
    }

    private Properties getPluginConfigs() {
        Properties properties = new Properties();
        try {
            if (envProfile == null || Strings.isNullOrEmpty(envProfile.formatTopProfile())) {
                Cat.logError("TitanQconfigPlugin.Config", new RuntimeException("getPluginConfigs(): profile is null or empty!"));
                new RuntimeException("getPluginConfigs(): profile is null or empty!");
            }

            ConfigField configField = new ConfigField(
                    TitanConstants.TITAN_QCONFIG_PLUGIN_APPID,
                    TitanConstants.TITAN_QCONFIG_PLUGIN_CONFIG_FILE,
                    envProfile.formatTopProfile());
            List<ConfigDetail> cdList = QconfigServiceUtils.currentConfigWithPriority(qconfigService, "Config", Lists.newArrayList(configField));

            if (cdList == null || cdList.isEmpty()) {
                throw new IllegalStateException("Not find ConfigDetail list for configField=" + configField);
            }
            String contentText = cdList.get(0).getContent();
            properties = CommonHelper.parseString2Properties(contentText);

        } catch (Exception e) {
            logger.error("Get [" + envProfile.formatTopProfile() + "] plugin configs failed.", e);
        }
        return properties;
    }

}
