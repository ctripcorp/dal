package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.common.exception.QServiceException;
import qunar.tc.qconfig.plugin.ConfigDetail;
import qunar.tc.qconfig.plugin.ConfigField;
import qunar.tc.qconfig.plugin.QconfigService;

import java.io.IOException;
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

    public PluginConfig(QconfigService qconfigService, EnvProfile envProfile) throws QServiceException, IOException {
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
                String topProfile = envProfile.formatTopProfile();
                try {
                    refresh();

                    long refreshInterval = getRefreshInterval();
                    if (refreshInterval != refreshIntervalMs) {
                        Cat.logEvent("Plugin.Config.Refresh.Interval.Changed", String.format("%s:%s->%s",
                                topProfile, refreshIntervalMs, refreshInterval));
                        logger.info("registerTimer(): [{}] remote refresh interval changed: newRefreshInterval: {}, oldRefreshInterval: {}.",
                                topProfile, refreshInterval, refreshIntervalMs);
                        refreshIntervalMs = refreshInterval;
                        rescheduleTimer.reschedule(refreshInterval, refreshInterval);
                    }
                } catch (Exception e) {
                    logger.error("registerTimer(): refresh [" + topProfile + "] plugin config failed.", e);
                }
            }
        }, refreshIntervalMs, refreshIntervalMs);
    }

    private void refresh() throws QServiceException, IOException {
        String topProfile = envProfile.formatTopProfile();
        logger.info("refresh(): refresh [{}] plugin configs begin ...", topProfile);

         Properties newPluginConfigs = getPluginConfigs();
        Cat.logEvent("Plugin.Config.Refresh", topProfile);
        int newPluginConfigSize = (newPluginConfigs == null ? 0 : newPluginConfigs.size());
        logger.info("refresh(): get [{}] new plugin configs, config size is {}", topProfile, newPluginConfigSize);

        if (newPluginConfigs != null && !newPluginConfigs.isEmpty()) {
            pluginConfigs = newPluginConfigs;
            logger.info("refresh(): [{}] new config replace old config success.", topProfile);
        } else {
            logger.warn("refresh(): [{}] new config is null or empty.", topProfile);
        }

        logger.info("refresh(): refresh [{}] plugin configs end ...", topProfile);
    }

    private long getRefreshInterval() {
        long refreshInterval;
        try {
            String topProfile = envProfile.formatTopProfile();
            String refreshIntervalInQConfig = pluginConfigs.getProperty(CommonConstants.PLUGIN_CONFIG_REFRESH_INTERVAL_MS);
            if (!Strings.isNullOrEmpty(refreshIntervalInQConfig)) {
                refreshInterval = Long.parseLong(refreshIntervalInQConfig);
                logger.info("getRefreshInterval(): [{}] remote refresh interval is {}ms.", topProfile, refreshInterval);
            } else {
                throw new DbConfigPluginException(topProfile + " plugin config (plugin.config.refresh.interval.ms) is null or empty");
            }
            return refreshInterval;
        } catch (Exception e) {
            throw e;
        }
    }

    private Properties getPluginConfigs() throws QServiceException, IOException {
        ConfigField configField = new ConfigField(
                TitanConstants.TITAN_QCONFIG_PLUGIN_APPID,
                TitanConstants.TITAN_QCONFIG_PLUGIN_CONFIG_FILE,
                envProfile.formatTopProfile());
        List<ConfigDetail> cdList = QconfigServiceUtils.currentConfigWithPriority(qconfigService, "Config", Lists.newArrayList(configField));

        if (cdList == null || cdList.isEmpty()) {
            throw new IllegalStateException("Not find ConfigDetail list for configField=" + configField);
        }
        String contentText = cdList.get(0).getContent();
        Properties properties = CommonHelper.parseString2Properties(contentText);

        return properties;
    }

}
