package com.ctrip.datasource.titan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.ctrip.platform.dal.exceptions.DalConfigException;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.datasource.configure.AllInOneConfigureReader;
import com.ctrip.datasource.configure.ConnectionStringParser;
import com.ctrip.datasource.configure.DataSourceConfigureProcessor;
import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.status.ProductVersionManager;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.TypedConfig;

public class TitanProvider implements DataSourceConfigureProvider {
    // This is to make sure we can get APPID if user really set so
    private static final Logger logger = LoggerFactory.getLogger(TitanProvider.class);
    private static final String EMPTY_ID = "999999";
    private static final int DEFAULT_TIMEOUT = 15 * 1000;

    public static final String SERVICE_ADDRESS = "serviceAddress";
    public static final String APPID = "appid";
    public static final String TIMEOUT = "timeout";
    public static final String USE_LOCAL_CONFIG = "useLocalConfig";
    public static final String DATABASE_CONFIG_LOCATION = "databaseConfigLocation";
    private static final String PROD_SUFFIX = "_SH";

    private String svcUrl;
    private String app_id;
    private String subEnv;
    private int time_out;
    private boolean useLocal;
    private String databaseConfigLocation;
    private ConnectionStringParser parser = new ConnectionStringParser();
    // used for simulate prod environemnt
    private boolean isDebug;

    public static final String TITAN = "arch.dal.titan.subenv";
    public static final String ENV = "environment";
    public static final String SUB_ENV = "subEnvironment";
    public static final String DB_NAME = "DBKeyName";

    // private static final String
    private static final String TITAN_APP_ID = "123456"; // 100010061

    private static final String DAL_LOCAL_DATASOURCE = "DAL.local.datasource";
    private static final String CTRIP_DATASOURCE_VERSION = "Ctrip.datasource.version";
    private static final String DAL_LOCAL_DATASOURCELOCATION = "DAL.local.datasourcelocation";

    private static final String DAL_DYNAMIC_DATASOURCE = "DAL";
    private static final String DAL_NOTIFY_LISTENER = "DataSource::notifyListener";
    private static final String DAL_NOTIFY_LISTENER_START = "DataSource.notifyListener.start";
    private static final String DAL_NOTIFY_LISTENER_END = "DataSource.notifyListener.end";

    private static final String DAL_REFRESH_DATASOURCE = "DataSource::refreshDataSourceConfig";
    private static final String DATASOURCE_OLD_CONNECTIONURL = "DataSource::oldConnectionUrl";
    private static final String DATASOURCE_NEW_CONNECTIONURL = "DataSource::newConnectionUrl";

    private static Map<String, TypedConfig<String>> configMap = new ConcurrentHashMap<>();

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
    private AllInOneConfigureReader allinonProvider = new AllInOneConfigureReader();

    public void initialize(Map<String, String> settings) throws Exception {
        ProductVersionManager.getInstance().register(CTRIP_DATASOURCE_VERSION, initVersion());

        if (DataSourceConfigureParser.getInstance().isDataSourceXmlExist()) {
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCELOCATION,
                    DataSourceConfigureParser.getInstance().getDataSourceXmlLocation());
        }

        startUpLog.clear();
        config = new HashMap<>(settings);

        info("Initialize Titan provider");

        svcUrl = discoverTitanServiceUrl(settings);
        app_id = discoverAppId(settings);
        subEnv = Foundation.server().getSubEnv();
        subEnv = subEnv == null ? null : subEnv.trim();

        info("Titan Service Url: " + svcUrl);
        info("Appid: " + app_id);
        info("Sub-environment: " + (subEnv == null ? "N/A" : subEnv));

        useLocal = Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG));
        info("Use local: " + useLocal);

        databaseConfigLocation = settings.get(DATABASE_CONFIG_LOCATION);
        info("DatabaseConfig location:" + (databaseConfigLocation == null ? "N/A" : databaseConfigLocation));

        String timeoutStr = settings.get(TIMEOUT);
        time_out = timeoutStr == null || timeoutStr.isEmpty() ? DEFAULT_TIMEOUT : Integer.parseInt(timeoutStr);
        info("Titan connection timeout: " + time_out);

        isDebug = Boolean.parseBoolean(settings.get("isDebug"));
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
        app_id = Foundation.app().getAppId();
        if (!(app_id == null || app_id.trim().isEmpty()))
            return app_id.trim();

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

    private static final Map<String, String> titanMapping = new HashMap<>();

    static {
        // LPT,FAT/FWS,UAT,PRO
        titanMapping.put("FAT", "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("FWS", "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("LPT", "https://ws.titan.lpt.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("UAT", "https://ws.titan.uat.qa.nt.ctripcorp.com/titanservice/query");
        titanMapping.put("PRO", "https://ws.titan.ctripcorp.com/titanservice/query");
    }

    @Override
    public void setup(Set<String> dbNames) {
        // Try best to match pool configure for the given names.
        checkMissingPoolConfig(dbNames);

        // If it uses local Database.Config
        Map<String, DataSourceConfigure> dataSourceConfigures = null;
        if (svcUrl == null || svcUrl.isEmpty() || useLocal) {
            dataSourceConfigures = allinonProvider.getDataSourceConfigures(dbNames, useLocal, databaseConfigLocation);
        } else {
            try {
                refreshTypedConfigMap(dbNames);
                dataSourceConfigures = getDataSourceConfigureConnectionSettings(dbNames);
                addListeners(dbNames);
            } catch (Exception e) {
                error("Fail to setup Titan Provider", e);
                throw new RuntimeException(e);
            }
        }

        addDataSourceConfigures(dataSourceConfigures);
        processDataSourcePoolSettings(dbNames);
        info("--- End datasource config  ---");
    }

    private void checkMissingPoolConfig(Set<String> dbNames) {
        DataSourceConfigureParser parser = DataSourceConfigureParser.getInstance();

        if (parser.isDataSourceXmlExist())
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCE, app_id);

        for (String name : dbNames) {
            if (parser.contains(name))
                continue;

            String possibleName = name.endsWith(PROD_SUFFIX) ? name.substring(0, name.length() - PROD_SUFFIX.length())
                    : name + PROD_SUFFIX;

            if (parser.contains(possibleName)) {
                parser.copyDataSourceConfigure(possibleName, name);
            } else {
                // It is strongly recommended to add datasource config in datasource.xml for each of the
                // connectionString in dal.config
                // Add missing one
                DataSourceConfigure c = new DataSourceConfigure();
                c.setName(name);
                parser.addDataSourceConfigure(name, c);
            }
        }
    }

    private void refreshTypedConfigMap(Set<String> dbNames) {
        if (dbNames == null || dbNames.isEmpty())
            return;
        for (String name : dbNames) {
            try {
                TypedConfig<String> config =
                        TypedConfig.get(TITAN_APP_ID, name, null, new TypedConfig.Parser<String>() {
                            public String parse(String connectionString) {
                                return connectionString;
                            }
                        });

                if (config != null) {
                    configMap.put(name, config);
                }
            } catch (Throwable e) {
                throw new RuntimeException(new FileNotFoundException(String.format(
                        "An error occured while getting titan keyname %s from QConfig:%s", name, e.getMessage())));
            }
        }
    }

    private Map<String, DataSourceConfigure> getDataSourceConfigureConnectionSettings(Set<String> dbNames)
            throws Exception {
        Map<String, DataSourceConfigure> configures = new HashMap<>();
        if (dbNames == null || dbNames.isEmpty()) {
            return configures;
        }

        for (String name : dbNames) {
            if (isDebug) {
                configures.put(name, new DataSourceConfigure());
                continue;
            }

            TypedConfig<String> config = configMap.get(name);
            if (config != null) {
                String connectionString = config.current();
                DataSourceConfigure connectionSetting = null;
                DataSourceConfigure configure = DataSourceConfigureParser.getInstance().getDataSourceConfigure(name);
                try {
                    connectionSetting = parser.parse(name, connectionString);
                    mergeConnectionSettings(configure, connectionSetting);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(String.format("Connection string of %s is illegal.", name), e);
                }
                configures.put(name, connectionSetting);
            }
        }

        return configures;
    }

    private void mergeConnectionSettings(DataSourceConfigure configure, DataSourceConfigure connectionSettings) {
        if (configure == null)
            return;

        Map<String, String> connectionSettingsMap = connectionSettings.getMap();
        Map<String, String> map = configure.getMap();
        if (connectionSettingsMap == null || map == null)
            return;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            connectionSettingsMap.put(entry.getKey(), entry.getValue());
        }
    }

    private void addListeners(Set<String> dbNames) {
        if (dbNames == null || dbNames.isEmpty())
            return;
        for (final String name : dbNames) {
            TypedConfig<String> config = configMap.get(name);
            if (config == null)
                continue;

            config.addListener(new Configuration.ConfigListener<String>() {
                @Override
                public void onLoad(String connectionString) {
                    Transaction transaction = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DAL_NOTIFY_LISTENER);
                    transaction.addData(DAL_NOTIFY_LISTENER_START);
                    Set<String> names = new HashSet<>();
                    names.add(name);
                    try {
                        notifyListeners(names);
                        transaction.addData(DAL_NOTIFY_LISTENER_END);
                        transaction.setStatus(Transaction.SUCCESS);
                    } catch (Throwable e) {
                        DalConfigException exception = new DalConfigException(e);
                        transaction.setStatus(exception);
                        Cat.logError(exception);
                        logger.error(String.format("DalConfigException:%s", e.getMessage()), exception);
                    } finally {
                        transaction.complete();
                    }
                }
            });
        }
    }

    private void addDataSourceConfigures(Map<String, DataSourceConfigure> map) {
        if (map == null)
            return;

        for (Map.Entry<String, DataSourceConfigure> entry : map.entrySet()) {
            DataSourceConfigureParser.getInstance().addDataSourceConfigure(entry.getKey(), entry.getValue());
        }
    }

    private void processDataSourcePoolSettings(Set<String> dbNames) {
        for (String name : dbNames) {
            processPoolSettings(name);
        }
    }

    private void notifyListeners(Set<String> dbNames) throws Exception {
        if (dbNames == null || dbNames.isEmpty())
            return;

        Map<String, DataSourceConfigureChangeListener> listeners =
                DataSourceConfigureParser.getInstance().getChangeListeners();
        if (listeners == null || listeners.isEmpty())
            return;

        // refresh TypedConfig
        refreshTypedConfigMap(dbNames);

        // get new DataSourceConfigure connection settings
        Map<String, DataSourceConfigure> map = getDataSourceConfigureConnectionSettings(dbNames);

        for (String name : dbNames) {
            DataSourceConfigureChangeListener listener = listeners.get(name);
            if (listener == null)
                continue;

            DataSourceConfigure connectionSettingsConfigure = map.get(name);
            if (connectionSettingsConfigure == null)
                continue;

            Transaction transaction = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE);
            try {
                // fetch old configure
                DataSourceConfigure oldConfigure = DataSourceConfigureParser.getInstance().getDataSourceConfigure(name);
                String oldVersion = oldConfigure.getVersion();
                String oldConnectionUrl = oldConfigure.toConnectionUrl();
                transaction.addData(DATASOURCE_OLD_CONNECTIONURL, oldConnectionUrl);
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE, Message.SUCCESS,
                        String.format("%s:%s", DATASOURCE_OLD_CONNECTIONURL, oldConnectionUrl));

                // refresh DataSourceConfigure connection settings
                DataSourceConfigure newConfigure = refreshConnectionSettings(name, connectionSettingsConfigure);
                String newVersion = newConfigure.getVersion();
                String newConnectionUrl = newConfigure.toConnectionUrl();
                transaction.addData(DATASOURCE_NEW_CONNECTIONURL, newConnectionUrl);
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE, Message.SUCCESS,
                        String.format("%s:%s", DATASOURCE_NEW_CONNECTIONURL, newConnectionUrl));

                // compare version
                if (oldVersion != null && newVersion != null) {
                    if (oldVersion.equals(newVersion)) {
                        continue;
                    }
                }

                // notify listener to recreate datasource,destroy old datasource,etc
                DataSourceConfigureChangeEvent event =
                        new DataSourceConfigureChangeEvent(name, newConfigure, oldConfigure);

                listener.configChanged(event);
                transaction.setStatus(Transaction.SUCCESS);
            } catch (Throwable e) {
                transaction.setStatus(e);
                Cat.logError(e);
                throw e;
            } finally {
                transaction.complete();
            }
        }
    }

    private DataSourceConfigure refreshConnectionSettings(String name,
            DataSourceConfigure connectionSettingsConfigure) {
        DataSourceConfigure oldConfigure = DataSourceConfigureParser.getInstance().getDataSourceConfigure(name);
        DataSourceConfigure newConfigure = DataSourceConfigureProcessor.getInstance()
                .refreshConnectionSettings(connectionSettingsConfigure, oldConfigure);
        DataSourceConfigureParser.getInstance().addDataSourceConfigure(name, newConfigure);
        return newConfigure;
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String dbName) {
        return DataSourceConfigureParser.getInstance().getDataSourceConfigure(dbName);
    }

    @Override
    public void register(String dbName, DataSourceConfigureChangeListener listener) {
        DataSourceConfigureParser.getInstance().addChangeListener(dbName, listener);
    }

    private void processPoolSettings(String name) {
        //// TODO
        // log active connection number for each titan keyname

        info("--- Key datasource config for " + name + " ---");
        DataSourceConfigure dataSourceConfigure = DataSourceConfigureParser.getInstance().getDataSourceConfigure(name);
        // Process DataSourceConfigure
        dataSourceConfigure = DataSourceConfigureProcessor.getInstance().getDataSourceConfigure(dataSourceConfigure);
        DataSourceConfigureParser.getInstance().addDataSourceConfigure(name, dataSourceConfigure);

        Properties properties = dataSourceConfigure.getProperties();
        info("maxAge: " + properties.getProperty(DataSourceConfigureConstants.MAX_AGE));
        info("maxActive: " + properties.getProperty(DataSourceConfigureConstants.MAXACTIVE));
        info("minIdle: " + properties.getProperty(DataSourceConfigureConstants.MINIDLE));
        info("initialSize: " + properties.getProperty(DataSourceConfigureConstants.INITIALSIZE));

        info("testWhileIdle: " + properties.getProperty(DataSourceConfigureConstants.TESTWHILEIDLE));
        info("testOnBorrow: " + properties.getProperty(DataSourceConfigureConstants.TESTONBORROW));
        info("testOnReturn: " + properties.getProperty(DataSourceConfigureConstants.TESTONRETURN));

        info("removeAbandonedTimeout: " + properties.getProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT));

        info("connectionProperties: " + properties.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES));
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
