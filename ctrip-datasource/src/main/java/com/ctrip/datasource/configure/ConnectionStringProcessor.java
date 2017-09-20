package com.ctrip.datasource.configure;

import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.status.ProductVersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.TypedConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionStringProcessor {
    public static class LogEntry {
        public static final int INFO = 0;
        public static final int WARN = 1;
        public static final int ERROR = 2;
        public static final int ERROR2 = 3;

        public int type;
        public String msg;
        public Throwable e;
    }

    // For dal ignite
    public static List<LogEntry> startUpLog = new ArrayList<>();
    public static Map<String, String> config = null;

    private static final Logger logger = LoggerFactory.getLogger(ConnectionStringProcessor.class);

    private static ConnectionStringProcessor processor = null;
    private ConnectionStringParser parser = ConnectionStringParser.getInstance();

    public static final String USE_LOCAL_CONFIG = "useLocalConfig";
    public static final String DATABASE_CONFIG_LOCATION = "databaseConfigLocation";
    public static final String SERVICE_ADDRESS = "serviceAddress";
    public static final String TIMEOUT = "timeout";
    public static final String APPID = "appid";

    // used for simulate prod environemnt
    private boolean isDebug;
    private String subEnv;
    private int timeOut;
    private String svcUrl;
    private String appId;
    private boolean useLocal;
    private String databaseConfigLocation;

    private static final int DEFAULT_TIMEOUT = 15 * 1000;
    private static final String EMPTY_ID = "999999";

    private static final String TITAN_APP_ID = "123456"; // 100010061

    private static final String CTRIP_DATASOURCE_VERSION = "Ctrip.datasource.version";
    private static final String DAL_LOCAL_DATASOURCELOCATION = "DAL.local.datasourcelocation";

    private Map<String, TypedConfig<String>> configMap = new ConcurrentHashMap<>();
    private static final Map<String, String> titanMapping = new HashMap<>();

    public synchronized static ConnectionStringProcessor getInstance() {
        if (processor == null) {
            processor = new ConnectionStringProcessor();
        }
        return processor;
    }

    public TypedConfig<String> getConfigMap(String name) {
        return configMap.get(name.toUpperCase());
    }

    public void addConfigMap(String name, TypedConfig<String> config) {
        configMap.put(name.toUpperCase(), config);
    }

    public String getSvcUrl() {
        return svcUrl;
    }

    public boolean getUseLocal() {
        return useLocal;
    }

    public String getDatabaseConfigLocation() {
        return databaseConfigLocation;
    }

    public String getAppId() {
        return appId;
    }

    static {
        // LPT,FAT/FWS,UAT,PRO
        titanMapping.put("FAT", "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("FWS", "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("LPT", "https://ws.titan.lpt.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("UAT", "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("PRO", "https://ws.titan.ctripcorp.com/titanservice/query");
    }

    public void initialize(Map<String, String> settings) throws DalException {
        ProductVersionManager.getInstance().register(CTRIP_DATASOURCE_VERSION, initVersion());

        if (DataSourceConfigureParser.getInstance().isDataSourceXmlExist()) {
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCELOCATION,
                    DataSourceConfigureParser.getInstance().getDataSourceXmlLocation());
        }

        startUpLog.clear();
        config = new HashMap<>(settings);

        info("Initialize Titan provider");
        svcUrl = discoverTitanServiceUrl(settings);
        appId = discoverAppId(settings);
        subEnv = Foundation.server().getSubEnv();
        subEnv = subEnv == null ? null : subEnv.trim();

        info("Titan Service Url: " + svcUrl);
        info("Appid: " + appId);
        info("Sub-environment: " + (subEnv == null ? "N/A" : subEnv));

        useLocal = Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG));
        info("Use local: " + useLocal);

        databaseConfigLocation = settings.get(DATABASE_CONFIG_LOCATION);
        info("DatabaseConfig location:" + (databaseConfigLocation == null ? "N/A" : databaseConfigLocation));

        String timeoutStr = settings.get(TIMEOUT);
        timeOut = timeoutStr == null || timeoutStr.isEmpty() ? DEFAULT_TIMEOUT : Integer.parseInt(timeoutStr);
        info("Titan connection timeout: " + timeOut);

        isDebug = Boolean.parseBoolean(settings.get("isDebug"));
    }

    /**
     * Get titan service URL in order of 1. environment varaible 2. serviceAddress 3. local
     *
     * @param settings
     * @return
     */
    public String discoverTitanServiceUrl(Map<String, String> settings) {
        // First we use environment to determine titan service address
        Env env = Foundation.server().getEnv();
        if (titanMapping.containsKey(env.toString()))
            return titanMapping.get(env.toString());

        // Then we check serviceAddress
        String svcUrl = settings.get(SERVICE_ADDRESS);

        // A little clean up
        if (svcUrl != null && svcUrl.trim().length() != 0) {
            svcUrl = svcUrl.trim();
            if (svcUrl.endsWith("/"))
                svcUrl = svcUrl.substring(0, svcUrl.length() - 1);
            return svcUrl;
        }

        // Indicate local database.config should be used
        return null;
    }

    public String discoverAppId(Map<String, String> settings) throws DalException {
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

    public void refreshConnectionSettingsMap(Set<String> dbNames) {
        if (dbNames == null || dbNames.isEmpty())
            return;
        for (String name : dbNames) {
            try {
                TypedConfig<String> config = getTitanTypedConfig(name);
                if (config != null) {
                    addConfigMap(name, config);
                }
            } catch (Throwable e) {
                throw new RuntimeException(new FileNotFoundException(String.format(
                        "An error occured while getting titan keyname %s from QConfig:%s", name, e.getMessage())));
            }
        }
    }

    public TypedConfig<String> getTitanTypedConfig(String name) {
        TypedConfig<String> config =
                TypedConfig.get(TITAN_APP_ID, name.toLowerCase(), null, new TypedConfig.Parser<String>() { // name.toUpperCase()
                    public String parse(String connectionString) {
                        return connectionString;
                    }
                });

        return config;
    }

    public Map<String, DataSourceConfigure> getConnectionSettings(Set<String> dbNames) throws Exception {
        Map<String, DataSourceConfigure> configures = new HashMap<>();
        if (dbNames == null || dbNames.isEmpty())
            return configures;

        for (String name : dbNames) {
            if (isDebug) {
                configures.put(name, new DataSourceConfigure());
                continue;
            }

            TypedConfig<String> config = getConfigMap(name);
            if (config != null) {
                String connectionString = config.current();
                DataSourceConfigure connectionSetting = null;
                try {
                    connectionSetting = parser.parse(name, connectionString);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(String.format("Connection string of %s is illegal.", name), e);
                }
                configures.put(name.toUpperCase(), connectionSetting);
            }
        }

        return configures;
    }

    public void info(String msg) {
        logger.info(msg);

        LogEntry ent = new LogEntry();
        ent.type = LogEntry.INFO;
        ent.msg = msg;
        startUpLog.add(ent);
    }

    public void error(String msg, Throwable e) {
        logger.error(msg, e);

        LogEntry ent = new LogEntry();
        ent.type = LogEntry.ERROR2;
        ent.msg = msg;
        startUpLog.add(ent);
    }

    private String initVersion() {
        String path = "/CtripDatasourceVersion.prop";
        InputStream stream = Version.class.getResourceAsStream(path);
        if (stream == null) {
            return "UNKNOWN";
        }
        Properties props = new Properties();
        try {
            props.load(stream);
            stream.close();
            return (String) props.get("version");
        } catch (IOException e) {
            return "UNKNOWN";
        }
    }

}
