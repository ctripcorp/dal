package com.ctrip.framework.dal.dbconfig.plugin.handler;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.google.common.base.Strings;
import qunar.tc.qconfig.plugin.QconfigService;

import java.util.Arrays;
import java.util.List;

/**
 * @author c7ch23en
 */
public abstract class BaseAdminHandler implements AdminHandler, CommonConstants {

    private QconfigService qconfigService;
    private PluginConfigManager pluginConfigManager;

    protected BaseAdminHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
        this.qconfigService = qconfigService;
        this.pluginConfigManager = pluginConfigManager;
    }

    protected QconfigService getQconfigService() {
        return qconfigService;
    }

    protected PluginConfigManager getPluginConfigManager() {
        return pluginConfigManager;
    }

    // todo: 不同plugin配置
    protected boolean checkPermission(String clientIp, EnvProfile profile) {
        try {
            PluginConfig config = pluginConfigManager.getPluginConfig(profile);
            String ipWhitelist = config.getParamValue(TitanConstants.TITAN_ADMIN_SERVER_LIST);
            return checkPermission(ipWhitelist, clientIp);
        } catch (Exception e) {
            throw new DbConfigPluginException("check permission exception", e);
        }
    }

    private boolean checkPermission(String whitelist, String ip) {
        if (!Strings.isNullOrEmpty(whitelist) && !Strings.isNullOrEmpty(ip)) {
            String[] whitelistArray = whitelist.split(",");
            List<String> whitelistList = Arrays.asList(whitelistArray);
            return whitelistList.contains(ip);
        }
        return false;
    }

}
