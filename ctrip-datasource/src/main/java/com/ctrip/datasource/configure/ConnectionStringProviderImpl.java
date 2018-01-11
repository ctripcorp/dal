package com.ctrip.datasource.configure;

import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.common.enums.SourceType;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.log.LogEntry;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.status.ProductVersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionStringProviderImpl implements ConnectionStringProvider, DataSourceConfigureConstants {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionStringProviderImpl.class);

    // For dal ignite
    public static List<LogEntry> startUpLog = new ArrayList<>();
    public static Map<String, String> config = null;

    /**
     * Used to access local Database.config file fo dev environment
     */
    private AllInOneConfigureReader allInOneProvider = new AllInOneConfigureReader();
    private ConnectionStringParser parser = ConnectionStringParser.getInstance();
    private DataSourceConfigureParser dataSourceConfigureParser = DataSourceConfigureParser.getInstance();

    // used for simulate prod environemnt
    private boolean isDebug;
    private int timeOut;
    private String appId;
    private boolean useLocal;
    private String databaseConfigLocation;

    private static final int DEFAULT_TIMEOUT = 15 * 1000;
    private static final String EMPTY_ID = "999999";

    private static final String TITAN_APP_ID = "100010061";
    private static final String CTRIP_DATASOURCE_VERSION = "Ctrip.datasource.version";
    private static final String DAL_LOCAL_DATASOURCE = "DAL.local.datasource";
    private static final String DAL_LOCAL_DATASOURCELOCATION = "DAL.local.datasourcelocation";

    private static final String DAL_DYNAMIC_DATASOURCE = "DAL";
    private static final String DAL_NOTIFY_LISTENER = "DataSource::notifyListener";

    private DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();

    private Map<String, MapConfig> configMap = new ConcurrentHashMap<>();
    private Set<String> keyNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private volatile static ConnectionStringProviderImpl processor = null;

    public synchronized static ConnectionStringProviderImpl getInstance() {
        if (processor == null) {
            processor = new ConnectionStringProviderImpl();
        }
        return processor;
    }

    private MapConfig getConfigMap(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return configMap.get(keyName);
    }

    private void addConfigMap(String name, MapConfig config) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        configMap.put(keyName, config);
    }

    private boolean getUseLocal() {
        return useLocal;
    }

    private String getDatabaseConfigLocation() {
        return databaseConfigLocation;
    }

    private String getAppId() {
        return appId;
    }

    @Override
    public void initialize(Map<String, String> settings) throws DalException {
        startUpLog.clear();
        config = new HashMap<>(settings);

        info("Initialize Titan provider");

        appId = discoverAppId(settings);
        info("Appid: " + appId);

        useLocal = Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG));
        info("Use local: " + useLocal);

        databaseConfigLocation = settings.get(DATABASE_CONFIG_LOCATION);
        info("DatabaseConfig location:" + (databaseConfigLocation == null ? "N/A" : databaseConfigLocation));

        String timeoutStr = settings.get(TIMEOUT);
        timeOut = timeoutStr == null || timeoutStr.isEmpty() ? DEFAULT_TIMEOUT : Integer.parseInt(timeoutStr);
        info("Titan connection timeout: " + timeOut);

        isDebug = Boolean.parseBoolean(settings.get(IS_DEBUG));
        info("isDebug: " + isDebug);

        ProductVersionManager.getInstance().register(CTRIP_DATASOURCE_VERSION, initVersion());

        if (dataSourceConfigureParser.isDataSourceXmlExist()) {
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCE, getAppId());

            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCELOCATION,
                    DataSourceConfigureParser.getInstance().getDataSourceXmlLocation());
        }
    }

    @Override
    public Map<String, DataSourceConfigure> initializeConnectionStrings(Set<String> dbNames, SourceType sourceType) {
        Map<String, DataSourceConfigure> dataSourceConfigures = null;
        boolean useLocal = getUseLocal();

        // If it uses local Database.Config
        if (sourceType == SourceType.Local) {
            dataSourceConfigures =
                    allInOneProvider.getDataSourceConfigures(dbNames, useLocal, getDatabaseConfigLocation());

            for (Map.Entry<String, DataSourceConfigure> name : dataSourceConfigures.entrySet()) {
                logger.debug("DAL debug:(initializeConnectionStrings)local:name:{},url:{}", name.getKey(),
                        name.getValue().getConnectionUrl());
            }
        } else {
            try {
                dataSourceConfigures = getConnectionStrings(dbNames);

                for (Map.Entry<String, DataSourceConfigure> name : dataSourceConfigures.entrySet()) {
                    logger.debug("DAL debug:(initializeConnectionStrings)remote:name:{},url:{}", name.getKey(),
                            name.getValue().getConnectionUrl());
                }
            } catch (Exception e) {
                error("Fail to setup Titan Provider", e);
                throw new RuntimeException(e);
            }
        }

        return dataSourceConfigures;
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

    @Override
    public Map<String, DataSourceConfigure> getConnectionStrings(Set<String> dbNames) throws Exception {
        refreshConnectionStringMapConfig(dbNames);
        return _getConnectionStrings(dbNames);
    }

    @Override
    public DataSourceConfigure parseConnectionString(String name, String connectionString) {
        return parser.parse(name, connectionString);
    }

    @Override
    public DataSourceConfigure getConnectionStringProperties(DataSourceConfigure configure) {
        if (configure == null)
            return null;

        DataSourceConfigure c = new DataSourceConfigure();
        c.setName(configure.getName());
        c.setConnectionUrl(configure.getConnectionUrl());
        c.setUserName(configure.getUserName());
        c.setPassword(configure.getPassword());
        c.setDriverClass(configure.getDriverClass());
        c.setVersion(configure.getVersion());
        return c;
    }

    private void refreshConnectionStringMapConfig(Set<String> dbNames) {
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

    private MapConfig getTitanMapConfig(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        Feature feature = Feature.create().setHttpsEnable(true).build();
        return MapConfig.get(TITAN_APP_ID, keyName, feature);
    }

    private Map<String, DataSourceConfigure> _getConnectionStrings(Set<String> dbNames) throws Exception {
        Map<String, DataSourceConfigure> configures = new HashMap<>();
        if (dbNames == null || dbNames.isEmpty())
            return configures;

        for (String name : dbNames) {
            String keyName = ConnectionStringKeyHelper.getKeyName(name);

            if (isDebug) {
                configures.put(keyName, new DataSourceConfigure());
                continue;
            }

            MapConfig config = getConfigMap(name);
            if (config != null) {
                String connectionString = null;
                try {
                    Map<String, String> map = config.asMap();
                    connectionString = map.get(TITAN_KEY_NORMAL);
                } catch (Throwable e) {
                    throw new DalException(String.format("Error getting connection string from QConfig for %s", name),
                            e);
                }

                DataSourceConfigure connectionStrings = null;
                try {
                    connectionStrings = parser.parse(name, connectionString);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(String.format("Connection string of %s is illegal.", name), e);
                }

                configures.put(keyName, connectionStrings);
            }
        }

        return configures;
    }

    @Override
    public void addConnectionStringChangedListener(final String name, final ConnectionStringChanged callback) {
        MapConfig config = getConfigMap(name);
        if (config == null)
            return;

        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                if (map == null || map.isEmpty())
                    throw new RuntimeException("Parameter for onLoad event is null.");

                String keyName = ConnectionStringKeyHelper.getKeyName(name);
                if (!keyNames.contains(keyName)) {
                    keyNames.add(keyName);
                    logger.debug("DAL debug:(addConnectionStringChangedListener)key {} first time onLoad", keyName);
                    return;
                }

                String normalConnectionString = map.get(DataSourceConfigureConstants.TITAN_KEY_NORMAL);
                if (normalConnectionString == null || normalConnectionString.isEmpty())
                    throw new RuntimeException("Normal connection string is null.");

                String failoverConnectionString = map.get(DataSourceConfigureConstants.TITAN_KEY_FAILOVER);
                if (failoverConnectionString == null || failoverConnectionString.isEmpty())
                    throw new RuntimeException("Failover connection string is null.");

                DataSourceConfigure configure = parseConnectionString(name, normalConnectionString);
                String newVersion = configure.getVersion();
                DataSourceConfigure oldConfigure = dataSourceConfigureLocator.getDataSourceConfigure(name);
                String oldVersion = oldConfigure.getVersion();

                if (newVersion != null && oldVersion != null) {
                    if (newVersion.equals(oldVersion)) {
                        String msg = String.format("New version of %s equals to old version.", name);
                        String transactionName = String.format("%s:%s", DAL_NOTIFY_LISTENER, name);
                        Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, msg);
                        logger.info(msg);
                        return;
                    }
                }

                callback.onChanged(map);
            }
        });
    }

    private void info(String msg) {
        logger.info(msg);

        LogEntry ent = new LogEntry();
        ent.type = LogEntry.INFO;
        ent.msg = msg;
        startUpLog.add(ent);
    }

    private void error(String msg, Throwable e) {
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

    // for unit test only
    @Override
    public void clear() {
        isDebug = false;
        String subEnv = null;
        int timeOut = 0;
        String svcUrl = null;
        String appId = null;
        boolean useLocal = false;
        String databaseConfigLocation = null;
        config = null;
        startUpLog.clear();
        configMap = new ConcurrentHashMap<>();
    }

}
