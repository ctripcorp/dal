package com.ctrip.datasource.titan;

import com.ctrip.datasource.util.DalEncrypter;
import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureWrapper;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.status.ProductVersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class DataSourceConfigureHelper implements DataSourceConfigureConstants {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfigureHelper.class);

    private static final String EMPTY_ID = "999999";
    private static final String CTRIP_DATASOURCE_VERSION = "Ctrip.datasource.version";
    private static final String DAL_LOCAL_DATASOURCE = "DAL.local.datasource";
    private static final String DAL_LOCAL_DATASOURCELOCATION = "DAL.local.datasourcelocation";
    private static final String DAL = "DAL";
    private static final String SEPARATOR = "\\.";
    private static final String POOLPROPERTIES_MERGEPOOLPROPERTIES = "PoolProperties::mergePoolProperties";

    // For dal ignite
    public static List<LogEntry> startUpLog = new ArrayList<>();
    public static Map<String, String> config = null;

    // used for simulate prod environemnt
    protected boolean isDebug;
    protected String appId;
    protected boolean useLocal;
    protected String databaseConfigLocation;

    private DalEncrypter dalEncrypter = null;
    private AtomicReference<IPDomainStatus> ipDomainStatusReference = new AtomicReference<>(IPDomainStatus.IP);
    private AtomicReference<DataSourceConfigureWrapper> dataSourceConfigureWrapperReference = new AtomicReference<>();

    protected DataSourceConfigureParser dataSourceConfigureParser = DataSourceConfigureParser.getInstance();
    protected DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
    protected PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();

    protected boolean getUseLocal() {
        return useLocal;
    }

    protected String getDatabaseConfigLocation() {
        return databaseConfigLocation;
    }

    protected String getAppId() {
        return appId;
    }

    protected void setIPDomainStatus(IPDomainStatus status) {
        ipDomainStatusReference.set(status);
    }

    protected IPDomainStatus getIPDomainStatus() {
        return ipDomainStatusReference.get();
    }

    protected void _initialize(Map<String, String> settings) throws Exception {
        startUpLog.clear();
        config = new HashMap<>(settings);

        info("Initialize Titan provider");

        appId = discoverAppId(settings);
        info("Appid: " + appId);

        useLocal = Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG));
        info("Use local: " + useLocal);

        databaseConfigLocation = settings.get(DATABASE_CONFIG_LOCATION);
        info("DatabaseConfig location:" + (databaseConfigLocation == null ? "N/A" : databaseConfigLocation));

        isDebug = Boolean.parseBoolean(settings.get(IS_DEBUG));
        info("isDebug: " + isDebug);

        ProductVersionManager.getInstance().register(CTRIP_DATASOURCE_VERSION, initVersion());

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

    protected Map<String, DataSourceConfigure> mergeDataSourceConfigures(Map<String, DataSourceConfigure> map) {
        if (map == null || map.isEmpty())
            return null;

        Map<String, DataSourceConfigure> configures = new HashMap<>();
        for (Map.Entry<String, DataSourceConfigure> entry : map.entrySet()) {
            DataSourceConfigure configure = mergeDataSourceConfigure(entry.getValue());
            configures.put(entry.getKey(), configure);
        }

        return configures;
    }

    protected DataSourceConfigure mergeDataSourceConfigure(DataSourceConfigure configure) {
        DataSourceConfigure c = cloneDataSourceConfigure(null);
        Transaction transaction = Cat.newTransaction(DAL, POOLPROPERTIES_MERGEPOOLPROPERTIES);

        try {
            DataSourceConfigureWrapper wrapper = dataSourceConfigureWrapperReference.get();

            // override app-level config from QConfig
            DataSourceConfigure dataSourceConfigure = wrapper.getDataSourceConfigure();
            if (dataSourceConfigure != null) {
                overrideDataSourceConfigure(c, dataSourceConfigure);
                String log = "App 覆盖结果:" + poolPropertiesHelper.mapToString(c.toMap());
                LOGGER.info(log);
                Cat.logEvent(DAL, POOLPROPERTIES_MERGEPOOLPROPERTIES, Message.SUCCESS, log);
            }

            // override datasource-level config from QConfig
            Map<String, DataSourceConfigure> dataSourceConfigureMap = wrapper.getDataSourceConfigureMap();
            if (configure != null && dataSourceConfigureMap != null) {
                String name = configure.getName();
                if (name != null) {
                    DataSourceConfigure sourceConfigure = dataSourceConfigureMap.get(name);
                    if (sourceConfigure != null) {
                        overrideDataSourceConfigure(c, sourceConfigure);
                        String log = name + " 覆盖结果:" + poolPropertiesHelper.mapToString(c.toMap());
                        LOGGER.info(log);
                        Cat.logEvent(DAL, POOLPROPERTIES_MERGEPOOLPROPERTIES, Message.SUCCESS, log);
                    } else {
                        String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
                        possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
                        DataSourceConfigure sc = dataSourceConfigureMap.get(possibleName);
                        if (sc != null) {
                            overrideDataSourceConfigure(c, sc);
                            String log = possibleName + " 覆盖结果：" + poolPropertiesHelper.mapToString(c.toMap());
                            LOGGER.info(log);
                            Cat.logEvent(DAL, POOLPROPERTIES_MERGEPOOLPROPERTIES, Message.SUCCESS, log);
                        }
                    }
                }
            }

            // override config from connection settings,datasource.xml
            if (configure != null) {
                // override connection settings
                overrideDataSourceConfigure(c, configure);
                c.setName(configure.getName());
                c.setVersion(configure.getVersion());
                c.setConnectionString(configure.getConnectionString());
                String log = "connection url:" + configure.getConnectionUrl();
                LOGGER.info(log);

                // override datasource.xml
                String name = configure.getName();
                if (name != null) {
                    DataSourceConfigure dataSourceXml = dataSourceConfigureLocator.getUserDataSourceConfigure(name);
                    if (dataSourceXml != null) {
                        overrideDataSourceXml(c, dataSourceXml);
                    } else {
                        String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
                        possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
                        DataSourceConfigure sc = dataSourceConfigureLocator.getUserDataSourceConfigure(possibleName);
                        if (sc != null) {
                            overrideDataSourceXml(c, sc);
                        }
                    }
                }
            }

            Cat.logEvent(DAL, POOLPROPERTIES_MERGEPOOLPROPERTIES, Message.SUCCESS,
                    String.format("最终覆盖结果：%s", poolPropertiesHelper.mapToString(c.toMap())));
            Map<String, String> datasource = c.getMap();
            Properties prop = c.getProperties();
            setProperties(datasource, prop); // set properties from map
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            LOGGER.error(e.getMessage(), e);
        } finally {
            transaction.complete();
        }
        return c;
    }

    private void overrideDataSourceXml(DataSourceConfigure c, DataSourceConfigure dataSourceXml) {
        String originalXml =
                String.format("datasource.xml 属性：%s", poolPropertiesHelper.mapToString(dataSourceXml.toMap()));
        Cat.logEvent(DAL, POOLPROPERTIES_MERGEPOOLPROPERTIES, Message.SUCCESS, originalXml);

        overrideDataSourceConfigure(c, dataSourceXml);
        String xmlLog = "datasource.xml 覆盖结果:" + poolPropertiesHelper.mapToString(c.toMap());
        LOGGER.info(xmlLog);
        Cat.logEvent(DAL, POOLPROPERTIES_MERGEPOOLPROPERTIES, Message.SUCCESS, xmlLog);
    }

    private DataSourceConfigure cloneDataSourceConfigure(DataSourceConfigure configure) {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        if (configure == null)
            return dataSourceConfigure;

        dataSourceConfigure.setName(configure.getName());
        dataSourceConfigure.setMap(new HashMap<>(configure.getMap()));
        dataSourceConfigure.setProperties(deepCopyProperties(configure.getProperties()));
        dataSourceConfigure.setVersion(configure.getVersion());
        return dataSourceConfigure;
    }

    private Properties deepCopyProperties(Properties properties) {
        if (properties == null)
            return null;

        Properties p = new Properties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            p.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }

        return p;
    }

    private void overrideDataSourceConfigure(DataSourceConfigure lowlevel, DataSourceConfigure highlevel) {
        if (lowlevel == null || highlevel == null)
            return;
        Map<String, String> lowlevelMap = lowlevel.getMap();
        Map<String, String> highlevelMap = highlevel.getMap();
        if (lowlevelMap == null || highlevelMap == null)
            return;
        for (Map.Entry<String, String> entry : highlevelMap.entrySet()) {
            lowlevelMap.put(entry.getKey(), entry.getValue()); // override entry of map
        }
    }

    private void setProperties(Map<String, String> datasource, Properties prop) {
        if (datasource == null || prop == null)
            return;

        for (Map.Entry<String, String> entry : datasource.entrySet()) {
            prop.setProperty(entry.getKey(), entry.getValue());
        }
    }

    protected DataSourceConfigure getDataSourceConfigure(String name) {
        return dataSourceConfigureLocator.getDataSourceConfigure(name);
    }

    protected DataSourceConfigure getDataSourceConfigure(String name, IPDomainStatus status) {
        DataSourceConfigure temp = getDataSourceConfigure(name);
        ConnectionString connectionString = temp.getConnectionString();
        String cs = getConnectionStringByIPDomainStatus(status, connectionString);
        DataSourceConfigure configure = dataSourceConfigureLocator.parseConnectionString(name, cs);
        configure = mergeDataSourceConfigure(configure);
        configure.setConnectionString(connectionString);
        return configure;
    }

    protected DataSourceConfigure getDataSourceConfigure(String name, String normalConnectionString,
            String failoverConnectionString) {
        ConnectionString connectionString = new ConnectionString(normalConnectionString, failoverConnectionString);
        IPDomainStatus status = getIPDomainStatus();
        String cs = getConnectionStringByIPDomainStatus(status, connectionString);
        DataSourceConfigure configure = dataSourceConfigureLocator.parseConnectionString(name, cs);
        configure = mergeDataSourceConfigure(configure);
        configure.setConnectionString(connectionString);
        return configure;
    }

    protected String getConnectionStringByIPDomainStatus(IPDomainStatus status, ConnectionString connectionString) {
        return status.equals(IPDomainStatus.IP) ? connectionString.getNormalConnectionString()
                : connectionString.getFailoverConnectionString();
    }

    protected void setPoolProperties(Map<String, String> map) {
        Map<String, String> originalMap = new HashMap<>(map);
        Map<String, String> datasource = new HashMap<>(); // app level
        Map<String, Map<String, String>> datasourceMap = new HashMap<>(); // datasource level
        processDataSourceConfigure(map, datasource, datasourceMap);

        // set app level map
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        setDataSourceConfigure(dataSourceConfigure, datasource);

        // set datasource level map
        Map<String, DataSourceConfigure> dataSourceConfigureMap = new HashMap<>();
        setDataSourceConfigureMap(dataSourceConfigureMap, datasourceMap);

        DataSourceConfigureWrapper wrapper =
                new DataSourceConfigureWrapper(originalMap, dataSourceConfigure, dataSourceConfigureMap);
        setDataSourceConfigureWrapperReference(wrapper);
    }

    private void processDataSourceConfigure(Map<String, String> map, Map<String, String> datasource,
            Map<String, Map<String, String>> datasourceMap) {
        if (map == null || map.isEmpty())
            return;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String[] array = entry.getKey().split(SEPARATOR);
            if (array.length == 1) { // app level
                datasource.put(array[0], entry.getValue());
            } else if (array.length == 2) { // datasource level
                String datasourceName = array[0];
                if (!datasourceMap.containsKey(datasourceName))
                    datasourceMap.put(datasourceName, new HashMap<String, String>());
                Map<String, String> temp = datasourceMap.get(datasourceName);
                temp.put(array[1], entry.getValue());
            }
        }
    }

    private void setDataSourceConfigureMap(Map<String, DataSourceConfigure> configureMap,
            Map<String, Map<String, String>> datasourceMap) {
        if (configureMap == null || datasourceMap.isEmpty())
            return;
        for (Map.Entry<String, Map<String, String>> entry : datasourceMap.entrySet()) {
            DataSourceConfigure config = new DataSourceConfigure();
            setDataSourceConfigure(config, entry.getValue());
            String keyName = ConnectionStringKeyHelper.getKeyName(entry.getKey());
            configureMap.put(keyName, config);
        }
    }

    private void setDataSourceConfigureWrapperReference(DataSourceConfigureWrapper wrapper) {
        dataSourceConfigureWrapperReference.set(wrapper);
    }

    private void setDataSourceConfigure(DataSourceConfigure configure, Map<String, String> datasource) {
        if (configure == null || datasource.isEmpty())
            return;
        configure.setMap(datasource);
    }

    protected void addDataSourceConfigureKeySet(Set<String> names) {
        dataSourceConfigureLocator.addDataSourceConfigureKeySet(names);
    }

    protected Set<String> getDataSourceConfigureKeySet() {
        return dataSourceConfigureLocator.getDataSourceConfigureKeySet();
    }

    protected void addDataSourceConfigures(Map<String, DataSourceConfigure> map) {
        if (map == null || map.isEmpty())
            return;

        for (Map.Entry<String, DataSourceConfigure> entry : map.entrySet()) {
            addDataSourceConfigure(entry.getKey(), entry.getValue());
        }
    }

    protected void addDataSourceConfigure(String name, DataSourceConfigure configure) {
        dataSourceConfigureLocator.addDataSourceConfigure(name, configure);
    }

    protected DataSourceConfigure getConnectionStringProperties(DataSourceConfigure configure) {
        return dataSourceConfigureLocator.getConnectionStringProperties(configure);
    }

    protected synchronized DalEncrypter getEncrypter() {
        if (dalEncrypter == null) {
            try {
                dalEncrypter = new DalEncrypter(LoggerAdapter.DEFAULT_SECERET_KEY);
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

    protected String initVersion() {
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
