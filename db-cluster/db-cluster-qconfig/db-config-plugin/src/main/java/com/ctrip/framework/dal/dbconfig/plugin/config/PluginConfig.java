package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.dianping.cat.Cat;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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

    private QconfigService qconfigService;
    private EnvProfile envProfile;

    public PluginConfig(QconfigService qconfigService, EnvProfile envProfile) {
        this.qconfigService = qconfigService;
        this.envProfile = envProfile;
    }

    //Get config param value
    public String getParamValue(String key) throws QServiceException, IOException {
        String result = null;
        if(envProfile != null && !Strings.isNullOrEmpty(envProfile.formatTopProfile())){
            Properties contentProp = getCurrentContentProp();
            if(contentProp != null){
                result = contentProp.getProperty(key);
            }
        }else{
            Cat.logError("TitanQconfigPlugin.Config", new RuntimeException("getParamValue(): profile is null or empty!"));
        }

        return result;
    }

    //query and get latest config file content
    public Properties getCurrentContentProp() throws QServiceException, IOException {
        ConfigField configField = new ConfigField(
                TitanConstants.TITAN_QCONFIG_PLUGIN_APPID,
                TitanConstants.TITAN_QCONFIG_PLUGIN_CONFIG_FILE,
                envProfile.formatTopProfile());

        List<ConfigDetail> cdList = QconfigServiceUtils.currentConfigWithPriority(qconfigService, "Config", Lists.newArrayList(configField));
        if(cdList == null || cdList.isEmpty()){
            throw new IllegalStateException("Not find ConfigDetail list for configField=" + configField);
        }
        String contentText = cdList.get(0).getContent();
        Properties contentProp = CommonHelper.parseString2Properties(contentText);
        return contentProp;
    }

}
