package com.ctrip.datasource.titan;

import com.ctrip.datasource.util.DalEncrypter;
import com.ctrip.datasource.util.Version;
import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.status.ProductVersionManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DataSourceConfigureHelper implements DataSourceConfigureConstants {
    protected static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private static final String EMPTY_ID = "999999";
    private static final String CTRIP_DATASOURCE_VERSION = "Ctrip.datasource.version";
    private static final String DAL_LOCAL_DATASOURCE = "DAL.local.datasource";
    private static final String DAL_LOCAL_DATASOURCELOCATION = "DAL.local.datasourcelocation";

    // For dal ignite
    public static List<LogEntry> startUpLog = new ArrayList<>();
    public static Map<String, String> config = null;

    // used for simulate prod environemnt
    protected boolean isDebug;
    protected String appId;
    protected boolean useLocal;
    protected boolean ignoreExternalException = false;
    protected String databaseConfigLocation;

    private DalEncrypter dalEncrypter = null;

    private DataSourceConfigureParser dataSourceConfigureParser = DataSourceConfigureParser.getInstance();

    protected boolean getUseLocal() {
        return useLocal;
    }

    protected boolean getIgnoreExternalException() {
        return ignoreExternalException;
    }

    protected String getDatabaseConfigLocation() {
        return databaseConfigLocation;
    }

    protected String getAppId() {
        return appId;
    }

    protected void _initialize(Map<String, String> settings) throws Exception {
        startUpLog.clear();
        config = new HashMap<>(settings);

        info("Initialize Titan provider");

        appId = discoverAppId(settings);
        info("Appid: " + appId);

        useLocal = Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG));
        info("Use local: " + useLocal);

        ignoreExternalException = Boolean.parseBoolean(settings.get(IGNORE_EXTERNAL_EXCEPTION));
        info("ignore external exception: " + ignoreExternalException);

        databaseConfigLocation = settings.get(DATABASE_CONFIG_LOCATION);
        info("DatabaseConfig location:" + (databaseConfigLocation == null ? "N/A" : databaseConfigLocation));

        isDebug = Boolean.parseBoolean(settings.get(IS_DEBUG));
        info("isDebug: " + isDebug);

        ProductVersionManager.getInstance().register(CTRIP_DATASOURCE_VERSION, Version.getVersion());

        if (dataSourceConfigureParser.isDataSourceXmlExist()) {
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCE, getAppId());
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCELOCATION,
                    DataSourceConfigureParser.getInstance().getDataSourceXmlLocation());
        }
    }

    private String discoverAppId(Map<String, String> settings) throws DalException {
        // First try framework foundation
        String appId = Foundation.app().getAppId();
        if (!(appId == null || appId.trim().isEmpty()))
            return appId.trim();

        // Try pre-configred settings
        String appid = settings.get(APPID);
        if (!(appid == null || appid.trim().isEmpty()))
            return appid.trim();

        // Try original logic
        appid = LogConfig.getAppID();
        if (appid == null || appid.equals(EMPTY_ID))
            appid = Cat.getManager().getDomain();

        if (!(appid == null || appid.trim().isEmpty()))
            return appid.trim();

        DalException e = new DalException("Can not locate app.id for this application");
        error(e.getMessage(), e);
        throw e;
    }

    protected synchronized DalEncrypter getEncrypter() {
        if (dalEncrypter == null) {
            try {
                dalEncrypter = new DalEncrypter(LoggerAdapter.DEFAULT_SECRET_KEY);
            } catch (Throwable e) {
                LOGGER.warn("DalEncrypter initialization failed.");
            }
        }

        return dalEncrypter;
    }

    protected void info(String msg) {
        LOGGER.info(msg);

        LogEntry ent = new LogEntry();
        ent.type = LogEntry.INFO;
        ent.msg = msg;
        startUpLog.add(ent);
    }

    protected void error(String msg, Throwable e) {
        LOGGER.error(msg, e);

        LogEntry ent = new LogEntry();
        ent.type = LogEntry.ERROR2;
        ent.msg = msg;
        ent.e = e;
        startUpLog.add(ent);
    }

}
