package com.ctrip.datasource.configure;

import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureCollection;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyNameHelper;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.status.ProductVersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

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
    private static final Logger logger = LoggerFactory.getLogger(ConnectionStringProcessor.class);

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

    /**
     * Used to access local Database.config file fo dev environment
     */
    private AllInOneConfigureReader allInOneProvider = new AllInOneConfigureReader();
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

    private static final String TITAN_APP_ID = "100010061"; // 100010061 123456
    public static final String TITAN_KEY_NORMAL = "normal";
    public static final String TITAN_KEY_FAILOVER = "failover";

    private static final String CTRIP_DATASOURCE_VERSION = "Ctrip.datasource.version";
    private static final String DAL_LOCAL_DATASOURCELOCATION = "DAL.local.datasourcelocation";

    private Map<String, MapConfig> configMap = new ConcurrentHashMap<>();
    private static final Map<String, String> titanMapping = new HashMap<>();

    private static ConnectionStringProcessor processor = null;

    public synchronized static ConnectionStringProcessor getInstance() {
        if (processor == null) {
            processor = new ConnectionStringProcessor();
        }
        return processor;
    }

    public MapConfig getConfigMap(String name) {
        String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
        return configMap.get(keyName);
    }

    private void addConfigMap(String name, MapConfig config) {
        String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
        configMap.put(keyName, config);
    }

    private String getSvcUrl() {
        return svcUrl;
    }

    private boolean getUseLocal() {
        return useLocal;
    }

    private String getDatabaseConfigLocation() {
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

    public Map<String, DataSourceConfigureCollection> initializeDataSourceConfigureConnectionSettings(
            Set<String> dbNames) {
        Map<String, DataSourceConfigureCollection> dataSourceConfigures = null;
        String svcUrl = getSvcUrl();
        boolean useLocal = getUseLocal();

        // If it uses local Database.Config
        if (svcUrl == null || svcUrl.isEmpty() || useLocal) {
            dataSourceConfigures =
                    allInOneProvider.getDataSourceConfigures(dbNames, useLocal, getDatabaseConfigLocation());
        } else {
            try {
                dataSourceConfigures = refreshAndGetDataSourceConfigureConnectionSettings(dbNames);
            } catch (Exception e) {
                error("Fail to setup Titan Provider", e);
                throw new RuntimeException(e);
            }
        }

        return dataSourceConfigures;
    }

    /**
     * Get titan service URL in order of 1. environment varaible 2. serviceAddress 3. local
     *
     * @param settings
     * @return
     */
    private String discoverTitanServiceUrl(Map<String, String> settings) {
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

    public Map<String, DataSourceConfigureCollection> refreshAndGetDataSourceConfigureConnectionSettings(
            Set<String> dbNames) throws Exception {
        refreshConnectionSettingsMap(dbNames);
        return getConnectionSettings(dbNames);
    }

    private void refreshConnectionSettingsMap(Set<String> dbNames) {
        if (dbNames == null || dbNames.isEmpty())
            return;
        for (String name : dbNames) {
            try {
                MapConfig config = getTitanMapConfig(name);
                if (config != null) {
                    addConfigMap(name, config);
                }
            } catch (Throwable e) {
                throw new RuntimeException(new FileNotFoundException(String.format(
                        "An error occured while getting titan keyname %s from QConfig:%s", name, e.getMessage())));
            }
        }
    }

    public MapConfig getTitanMapConfig(String name) {
        String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
        Feature feature = Feature.create().setHttpsEnable(true).build();
        return MapConfig.get(TITAN_APP_ID, keyName, feature);
    }

    private Map<String, DataSourceConfigureCollection> getConnectionSettings(Set<String> dbNames) throws Exception {
        Map<String, DataSourceConfigureCollection> configures = new HashMap<>();
        if (dbNames == null || dbNames.isEmpty())
            return configures;

        for (String name : dbNames) {
            if (isDebug) {
                configures.put(name, new DataSourceConfigureCollection());
                continue;
            }

            MapConfig config = getConfigMap(name);
            if (config != null) {
                String connectionString = null;
                String failoverConnectionString = null;
                try {
                    Map<String, String> map = config.asMap();
                    connectionString = map.get(TITAN_KEY_NORMAL);
                    failoverConnectionString = map.get(TITAN_KEY_FAILOVER);
                } catch (Throwable e) {
                    throw new DalException(String.format("Error getting connection string from QConfig for %s", name),
                            e);
                }

                DataSourceConfigure connectionSetting = null;
                DataSourceConfigure failoverConnectionSetting = null;
                try {
                    connectionSetting = parser.parse(name, connectionString);
                    failoverConnectionSetting = parser.parse(name, failoverConnectionString);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(String.format("Connection string of %s is illegal.", name), e);
                }

                String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
                DataSourceConfigureCollection collection =
                        new DataSourceConfigureCollection(connectionSetting, failoverConnectionSetting);
                configures.put(keyName, collection);
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
        ent.e = e;
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
