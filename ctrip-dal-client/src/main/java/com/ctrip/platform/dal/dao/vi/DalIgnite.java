package com.ctrip.platform.dal.dao.vi;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.datasource.titan.LogEntry;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import qunar.tc.qconfig.client.TypedConfig;

import com.ctrip.framework.vi.IgniteManager.SimpleLogger;
import com.ctrip.framework.vi.annotation.Ignite;
import com.ctrip.framework.vi.ignite.AbstractCtripIgnitePlugin;
import com.ctrip.platform.dal.dao.DalClientFactory;

@Ignite(id = "fx.dal.ignite", type = Ignite.PluginType.Component, after = "vi.ignite")
public class DalIgnite extends AbstractCtripIgnitePlugin {
    private Map<String, String> configs = new HashMap<>();
    private final String HELP_URL = "http://conf.ctripcorp.com/pages/viewpage.action?pageId=143877535";
    private final String IGNITE = "Ignite";
    private final String IGNITING_DAL_CLIENT = "Igniting Dal Client";

    @Override
    public Map<String, String> coreConfigs() {
        return configs;
    }

    @Override
    public String helpUrl() {
        return HELP_URL;
    }

    @Override
    public boolean warmUP(SimpleLogger logger) {
        Transaction t = Cat.newTransaction(DalLogTypes.DAL, IGNITE);
        if (!isDalConfigExist(logger)) {
            logger.warn("Can not find dal.config from either local or remote.");
            logger.warn("This maybe normal case for those who upgrade from older ctrip-dal-client.");
            logger.warn("If app only use dal data source, please change dependency from ctrip-dal-client to ctrip-datasource.");
            logger.warn(String.format("Refer to %s for more information", HELP_URL));
            return true;
        }

        try {
            logger.info("Initialize Dal Factory");
            DalClientFactory.initClientFactory();

            validateConnectionStrings();

            validatePoolProperties();

            if (DataSourceConfigureManager.config != null)
                configs.putAll(DataSourceConfigureManager.config);

            log(logger);
            logger.info("success initialized Dal Factory");

            logger.info("Start warm up datasources");
            DalClientFactory.warmUpConnections();
            logger.info("success warmed up datasources");

            logger.info("Start warm up id generators");
            DalClientFactory.warmUpIdGenerators();
            logger.info("Success warmed up id generators");

            Cat.logEvent(DalLogTypes.DAL, IGNITE, Message.SUCCESS, IGNITING_DAL_CLIENT);
            t.setStatus(Message.SUCCESS);

            return true;
        } catch (Throwable e) {
            if (DataSourceConfigureManager.config == null) {
                logger.error("Can not load dal.config from neither local nor remote.");
            } else {
                configs.putAll(DataSourceConfigureManager.config);
            }

            log(logger);
            logger.error("Fail", e);
            logger.info(String.format("Please check %s", HELP_URL));

            t.setStatus(e);
            Cat.logError(e);

            return false;
        } finally {
            t.complete();
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

    private void validateConnectionStrings() throws Exception {
        DataSourceConfigureLocator locator = DataSourceConfigureLocatorManager.getInstance();
        Map<String, DalConnectionString> failedConnectionStringMap = locator.getFailedConnectionStrings();
        if (failedConnectionStringMap == null || failedConnectionStringMap.isEmpty())
            return;

        StringBuilder errorMsg = new StringBuilder();
        for (Map.Entry<String, DalConnectionString> entry : failedConnectionStringMap.entrySet()) {
            if (entry.getValue() instanceof DalInvalidConnectionString) {
                errorMsg.append(String.format("[TitanKey: %s, ErrorMessage: %s] ", entry.getKey(), ((DalInvalidConnectionString) entry.getValue()).getConnectionStringException().getMessage()));
            }
        }

        Map<String, DalConnectionStringConfigure> failedVariableConnectionStringMap = locator.getFailedVariableConnectionStrings();
        if (failedVariableConnectionStringMap == null || failedVariableConnectionStringMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, DalConnectionStringConfigure> entry : failedVariableConnectionStringMap.entrySet()) {
            if (entry.getValue() instanceof DalInvalidConnectionString) {
                errorMsg.append(String.format("[dbName: %s, ErrorMessage: %s] ", entry.getKey(), ((InvalidVariableConnectionString) entry.getValue()).getConnectionStringException().getMessage()));
            }
        }

        throw new DalRuntimeException(errorMsg.toString());
    }

    private void validatePoolProperties() throws Exception {
        DalConfigure configure = DalClientFactory.getDalConfigure();
        if (configure.getDatabaseSetNames().size() == 0) {
            return;
        }
        DataSourceConfigureLocator locator = DataSourceConfigureLocatorManager.getInstance();
        PropertiesWrapper wrapper = locator.getPoolProperties();
        if (wrapper == null) {
            throw new DalRuntimeException("Error getting PoolProperties from QConfig");
        }
    }
}
