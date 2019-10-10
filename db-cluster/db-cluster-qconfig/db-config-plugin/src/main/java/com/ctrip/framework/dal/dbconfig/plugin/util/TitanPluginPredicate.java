package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.dianping.cat.Cat;
import qunar.tc.qconfig.plugin.ConfigDetail;
import qunar.tc.qconfig.plugin.PluginPredicate;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by taochen on 2019/9/26.
 */
public class TitanPluginPredicate implements PluginPredicate<ConfigDetail> {
    @Override
    public boolean test(ConfigDetail configDetail, ConfigDetail currentConfigDetail) {
        try {
            Properties currentConfig = CommonHelper.parseString2Properties(currentConfigDetail.getContent());
            Properties oldConfig = CommonHelper.parseString2Properties(configDetail.getOldConfigDetail().getContent());
            String currentVersion = currentConfig.getProperty(TitanConstants.VERSION);
            String oldVersion = oldConfig.getProperty(TitanConstants.VERSION);
            if (oldVersion.equalsIgnoreCase(currentVersion)) {
                return true;
            }
        } catch (IOException e) {
            Cat.logError("titankey config parse error, ", e);
            throw new DbConfigPluginException(e);
        }
        return false;
    }
}
