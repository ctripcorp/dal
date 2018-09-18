package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultDataSourceConfigureLocator implements DataSourceConfigureLocator {
    protected static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    protected PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();
    protected AtomicReference<PropertiesWrapper> propertiesWrapperReference = new AtomicReference<>();
    private AtomicReference<IPDomainStatus> ipDomainStatusReference = new AtomicReference<>(IPDomainStatus.IP);

    private Map<String, ConnectionString> connectionStrings = new ConcurrentHashMap<>();
    // user datasource.xml pool properties configure
    private Map<String, PoolPropertiesConfigure> userPoolPropertiesConfigure = new ConcurrentHashMap<>();
    private Map<String, DataSourceConfigure> dataSourceConfiguresCache = new ConcurrentHashMap<>();
    private Set<String> dataSourceConfigureKeySet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private static final String SEPARATOR = "\\.";
    protected static final String DAL = "DAL";
    private static final String DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE =
            "An error occured while getting datasource properties.";
    private static final String APP_OVERRIDE_RESULT = "App override result: ";
    private static final String OVERRIDE_RESULT = " override result: ";
    private static final String DATASOURCE_XML_OVERRIDE_RESULT = "datasource.xml override result: ";
    private static final String CONNECTION_URL = "connection url:";

    private String FINAL_OVERRIDE_RESULT_FORMAT = "Final override result: %s";
    protected String POOLPROPERTIES_MERGEPOOLPROPERTIES_FORMAT = "PoolProperties::mergePoolProperties:%s";

    @Override
    public void addUserPoolPropertiesConfigure(String name, PoolPropertiesConfigure configure) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        userPoolPropertiesConfigure.put(keyName, configure);
        dataSourceConfiguresCache.put(keyName, new DataSourceConfigure(keyName, configure.getProperties()));
    }

    @Override
    public PoolPropertiesConfigure getUserPoolPropertiesConfigure(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return userPoolPropertiesConfigure.get(keyName);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        DataSourceConfigure configure = _getDataSourceConfigure(keyName);
        return configure;
    }

    private DataSourceConfigure _getDataSourceConfigure(String name) {
        DataSourceConfigure configure = null;
        configure = dataSourceConfiguresCache.get(name);
        if (configure != null) {
            return configure;
        }

        ConnectionString connectionString = connectionStrings.get(name);
        configure = mergeDataSourceConfigure(connectionString);
        if (configure != null) {
            dataSourceConfiguresCache.put(name, configure);
        }

        return configure;
    }

    private void removeDataSourceConfigures() {
        dataSourceConfiguresCache.clear();
    }

    @Override
    public void addDataSourceConfigureKeySet(Set<String> names) {
        if (names == null || names.isEmpty())
            return;

        for (String name : names) {
            dataSourceConfigureKeySet.add(name);
        }
    }

    @Override
    public Set<String> getDataSourceConfigureKeySet() {
        return dataSourceConfigureKeySet;
    }

    @Override
    public void setIPDomainStatus(IPDomainStatus status) {
        ipDomainStatusReference.set(status);
        removeDataSourceConfigures();
    }

    @Override
    public IPDomainStatus getIPDomainStatus() {
        return ipDomainStatusReference.get();
    }

    @Override
    public void setConnectionStrings(Map<String, ConnectionString> map) {
        if (map == null || map.isEmpty())
            return;

        for (Map.Entry<String, ConnectionString> entry : map.entrySet()) {
            String keyName = ConnectionStringKeyHelper.getKeyName(entry.getKey());
            connectionStrings.put(keyName, entry.getValue());
            dataSourceConfiguresCache.remove(keyName);
        }
    }

    @Override
    public void setPoolProperties(PoolPropertiesConfigure configure) {
        if (configure == null)
            return;

        Properties properties = configure.getProperties();
        Properties originalProperties = new Properties(properties);
        Properties appProperties = new Properties(); // app level
        Map<String, Properties> datasourceProperties = new HashMap<>(); // datasource level
        processProperties(properties, appProperties, datasourceProperties);

        PropertiesWrapper wrapper = new PropertiesWrapper(originalProperties, appProperties, datasourceProperties);
        propertiesWrapperReference.set(wrapper);
        removeDataSourceConfigures();
    }

    private void processProperties(Properties properties, Properties appProperties,
            Map<String, Properties> datasourceProperties) {
        if (properties == null || properties.isEmpty())
            return;

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String[] array = entry.getKey().toString().split(SEPARATOR);
            if (array.length == 1) { // app level
                appProperties.setProperty(array[0], entry.getValue().toString());
            } else if (array.length == 2) { // datasource level
                String datasourceName = ConnectionStringKeyHelper.getKeyName(array[0]);
                if (!datasourceProperties.containsKey(datasourceName)) {
                    datasourceProperties.put(datasourceName, new Properties());
                }

                Properties temp = datasourceProperties.get(datasourceName);
                temp.setProperty(array[1], entry.getValue().toString());
            }
        }
    }

    @Override
    public DataSourceConfigure mergeDataSourceConfigure(ConnectionString connectionString) {
        ConnectionStringConfigure connectionStringConfigure = getConnectionStringConfigure(connectionString);
        if (connectionStringConfigure == null)
            return null;

        String name = connectionStringConfigure.getName();
        String logName = String.format(POOLPROPERTIES_MERGEPOOLPROPERTIES_FORMAT, name);
        DataSourceConfigure c = null;

        try {
            PropertiesWrapper wrapper = propertiesWrapperReference.get();
            if (wrapper == null)
                throw new DalException(DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE);

            c = cloneDataSourceConfigure(null);
            overrideProperties(connectionStringConfigure, connectionString, wrapper, c, name, logName);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }

        return c;
    }

    protected void overrideProperties(ConnectionStringConfigure connectionStringConfigure,
            ConnectionString connectionString, PropertiesWrapper wrapper, DataSourceConfigure c, String name,
            String logName) {
        // override app-level properties
        overrideAppLevelProperties(wrapper, c, logName);

        // override datasource-level properties
        overrideDataSourceLevelProperties(wrapper, c, name, logName);

        // override config from connection settings,datasource.xml
        overrideConnectionStringConfigureAndDataSourceXml(connectionStringConfigure, c, connectionString, name);

        LOGGER.logEvent(DAL, logName,
                String.format(FINAL_OVERRIDE_RESULT_FORMAT, poolPropertiesHelper.propertiesToString(c.toProperties())));
    }

    private void overrideAppLevelProperties(PropertiesWrapper wrapper, DataSourceConfigure c, String logName) {
        Properties appProperties = wrapper.getAppProperties();
        if (appProperties == null || appProperties.isEmpty())
            return;

        overrideProperties(c.getProperties(), appProperties);
        String log = APP_OVERRIDE_RESULT + poolPropertiesHelper.propertiesToString(c.getProperties());
        LOGGER.info(log);
        LOGGER.logEvent(DAL, logName, log);
    }

    private void overrideDataSourceLevelProperties(PropertiesWrapper wrapper, DataSourceConfigure c, String name,
            String logName) {
        if (name == null || name.isEmpty())
            return;

        Map<String, Properties> datasourceProperties = wrapper.getDatasourceProperties();
        if (datasourceProperties == null || datasourceProperties.isEmpty())
            return;

        _overrideDataSourceLevelProperties(datasourceProperties, c, name, logName);
    }

    private void _overrideDataSourceLevelProperties(Map<String, Properties> datasourceProperties, DataSourceConfigure c,
            String name, String logName) {
        Properties p1 = datasourceProperties.get(name);
        if (p1 != null && !p1.isEmpty()) {
            overrideProperties(c.getProperties(), p1);
            String log = name + OVERRIDE_RESULT + poolPropertiesHelper.propertiesToString(c.getProperties());
            LOGGER.info(log);
            LOGGER.logEvent(DAL, logName, log);
        } else {
            String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
            possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
            Properties p2 = datasourceProperties.get(possibleName);
            if (p2 != null && !p2.isEmpty()) {
                overrideProperties(c.getProperties(), p2);
                String log =
                        possibleName + OVERRIDE_RESULT + poolPropertiesHelper.propertiesToString(c.getProperties());
                LOGGER.info(log);
                LOGGER.logEvent(DAL, logName, log);
            }
        }
    }

    protected void overrideConnectionStringConfigureAndDataSourceXml(
            ConnectionStringConfigure connectionStringConfigure, DataSourceConfigure c,
            ConnectionString connectionString, String name) {
        if (connectionStringConfigure == null)
            return;

        // merge connection string configure
        mergeConnectionStringConfigure(c, connectionStringConfigure, connectionString);

        // override datasource.xml
        overrideDataSourceXml(name, c);
    }

    private void mergeConnectionStringConfigure(DataSourceConfigure c,
            ConnectionStringConfigure connectionStringConfigure, ConnectionString connectionString) {
        mergeConnectionStringConfigure(c, connectionStringConfigure);
        c.setConnectionString(connectionString);
        String log = CONNECTION_URL + connectionStringConfigure.getConnectionUrl();
        LOGGER.info(log);
    }

    private void overrideDataSourceXml(String name, DataSourceConfigure c) {
        if (name == null || name.isEmpty())
            return;

        PoolPropertiesConfigure dataSourceXml = getUserPoolPropertiesConfigure(name);
        if (dataSourceXml != null) {
            overrideDataSourceXml(c, dataSourceXml);
        } else {
            String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
            possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
            PoolPropertiesConfigure ppc = getUserPoolPropertiesConfigure(possibleName);
            if (ppc != null) {
                overrideDataSourceXml(c, ppc);
            }
        }
    }

    protected ConnectionStringConfigure getConnectionStringConfigure(ConnectionString connectionString) {
        if (connectionString == null)
            return null;

        ConnectionStringConfigure configure = null;
        IPDomainStatus status = getIPDomainStatus();
        if (status.equals(IPDomainStatus.IP)) {
            configure = connectionString.getIPConnectionStringConfigure();
        } else if (status.equals(IPDomainStatus.Domain)) {
            configure = connectionString.getDomainConnectionStringConfigure();
        }

        return configure;
    }

    protected void overrideDataSourceXml(DataSourceConfigure c, PoolPropertiesConfigure dataSourceXml) {
        overrideProperties(c.getProperties(), dataSourceXml.getProperties());
        String log = DATASOURCE_XML_OVERRIDE_RESULT + poolPropertiesHelper.propertiesToString(c.toProperties());
        LOGGER.info(log);
    }

    protected DataSourceConfigure cloneDataSourceConfigure(DataSourceConfigure configure) {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        if (configure == null)
            return dataSourceConfigure;

        dataSourceConfigure.setName(configure.getName());
        dataSourceConfigure.setProperties(deepCopyProperties(configure.getProperties()));
        dataSourceConfigure.setVersion(configure.getVersion());
        dataSourceConfigure.setConnectionString(configure.getConnectionString());
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

    protected void mergeConnectionStringConfigure(DataSourceConfigure dataSourceConfigure,
            ConnectionStringConfigure connectionStringConfigure) {
        if (dataSourceConfigure == null || connectionStringConfigure == null)
            return;

        setName(dataSourceConfigure, connectionStringConfigure);
        setConnectionUrl(dataSourceConfigure, connectionStringConfigure);
        setUserName(dataSourceConfigure, connectionStringConfigure);
        setPassword(dataSourceConfigure, connectionStringConfigure);
        setDriverClass(dataSourceConfigure, connectionStringConfigure);
        setVersion(dataSourceConfigure, connectionStringConfigure);
    }

    private void setName(DataSourceConfigure dataSourceConfigure, ConnectionStringConfigure connectionStringConfigure) {
        String name = connectionStringConfigure.getName();
        if (name != null)
            dataSourceConfigure.setName(name);
    }

    private void setConnectionUrl(DataSourceConfigure dataSourceConfigure,
            ConnectionStringConfigure connectionStringConfigure) {
        String connectionUrl = connectionStringConfigure.getConnectionUrl();
        if (connectionUrl != null)
            dataSourceConfigure.setConnectionUrl(connectionUrl);
    }

    private void setUserName(DataSourceConfigure dataSourceConfigure,
            ConnectionStringConfigure connectionStringConfigure) {
        String userName = connectionStringConfigure.getUserName();
        if (userName != null)
            dataSourceConfigure.setUserName(userName);
    }

    private void setPassword(DataSourceConfigure dataSourceConfigure,
            ConnectionStringConfigure connectionStringConfigure) {
        String password = connectionStringConfigure.getPassword();
        if (password != null)
            dataSourceConfigure.setPassword(password);
    }

    private void setDriverClass(DataSourceConfigure dataSourceConfigure,
            ConnectionStringConfigure connectionStringConfigure) {
        String driverClass = connectionStringConfigure.getDriverClass();
        if (driverClass != null)
            dataSourceConfigure.setDriverClass(driverClass);
    }

    private void setVersion(DataSourceConfigure dataSourceConfigure,
            ConnectionStringConfigure connectionStringConfigure) {
        String version = connectionStringConfigure.getVersion();
        if (version != null)
            dataSourceConfigure.setVersion(version);
    }

    protected void overrideProperties(Properties lowLevel, Properties highLevel) {
        if (lowLevel == null || highLevel == null)
            return;

        for (Map.Entry<Object, Object> entry : highLevel.entrySet()) {
            lowLevel.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
