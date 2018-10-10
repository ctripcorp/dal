package com.ctrip.platform.dal.dao.vi;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.titan.LogEntry;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import qunar.tc.qconfig.client.TypedConfig;

import com.ctrip.framework.vi.IgniteManager.SimpleLogger;
import com.ctrip.framework.vi.annotation.Ignite;
import com.ctrip.framework.vi.ignite.AbstractCtripIgnitePlugin;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.CtripDalConfig;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;

@Ignite(id = "fx.dal.ignite", type = Ignite.PluginType.Component)
public class DalIgnite extends AbstractCtripIgnitePlugin {
    private Map<String, String> configs = new HashMap<>();

    @Override
    public Map<String, String> coreConfigs() {
        return configs;
    }

    @Override
    public String helpUrl() {
        return "http://conf.ctripcorp.com/display/FRAM/Java+Client+FAQ";
    }

    @Override
    public boolean warmUP(SimpleLogger logger) {
        if (!isDalConfigExist(logger)) {
            logger.warn("Can not find dal.config from either local or remote.");
            logger.warn("This maybe normal case for those who upgrade from older ctrip-dal-cleint.");
            logger.warn(
                    "If app only use dal data source, please change dependecy from ctrip-dal-client to ctrip-datasource.");
            logger.warn(
                    "Refer to http://conf.ctripcorp.com/pages/viewpage.action?pageId=136437942 for more infomation");
            return true;
        }

        try {
            logger.info("Initialize Dal Factory");
            DalClientFactory.initClientFactory();

            if (DataSourceConfigureManager.config != null)
                configs.putAll(DataSourceConfigureManager.config);

            log(logger);
            logger.info("success initialized Dal Factory");

            logger.info("Start warm up datasources");
            DalClientFactory.warmUpConnections();
            logger.info("success warmed up datasources");

            checkIdGenConfig();

            return true;
        } catch (Throwable e) {
            if (DataSourceConfigureManager.config == null) {
                logger.error("Can not load dal.config from neither local nor remote.");
            } else {
                configs.putAll(DataSourceConfigureManager.config);
            }

            log(logger);
            logger.error("Fail", e);
            logger.info("Please check http://conf.ctripcorp.com/pages/viewpage.action?pageId=60842135");
            return false;
        }
    }

    private boolean isDalConfigExist(SimpleLogger logger) {
        logger.info("Try to locate dal.config from local");
        URL dalLoc = DalConfigureFactory.getDalConfigUrl();

        if (dalLoc != null) {
            logger.info("Found dal.config at " + dalLoc);
            return true;
        } else {
            logger.warn("Can not find dal.config from local");
        }

        logger.info("Try to locate dal.config from qConfig");

        try {
            TypedConfig<String> config = TypedConfig.get(CtripDalConfig.DAL_CONFIG, TypedConfig.STRING_PARSER);
            String content = config.current();
            logger.info("Found dal.config from qConfig");
            return true;
        } catch (Throwable e) {
            logger.warn("Can not find dal.config from qConfig :" + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean selfCheck(SimpleLogger logger) {
        return true;
    }

    private void log(SimpleLogger logger) {
        List<LogEntry> startUpLog = new ArrayList<>(DataSourceConfigureManager.startUpLog);
        for (LogEntry e : startUpLog) {
            switch (e.type) {
                case LogEntry.INFO:
                    logger.info(e.msg);
                    break;
                case LogEntry.WARN:
                    logger.warn(e.msg);
                    break;
                case LogEntry.ERROR:
                    logger.error(e.msg);
                    break;
                case LogEntry.ERROR2:
                    logger.error(e.msg, e.e);
                    break;
                default:
                    break;
            }
        }
    }

    private void checkIdGenConfig() {
        DalConfigure dalConfigure = DalClientFactory.getDalConfigure();
        Set<String> dbSetNames = dalConfigure.getDatabaseSetNames();
        for (String dbSetName : dbSetNames) {
            DatabaseSet dbSet = dalConfigure.getDatabaseSet(dbSetName);
            if (dbSet.getDatabaseCategory() == DatabaseCategory.SqlServer && dbSet.getIdGenConfig() != null) {
                throw new DalRuntimeException("Id generator does not support MS Sql Server yet");
            }
        }
    }

}
