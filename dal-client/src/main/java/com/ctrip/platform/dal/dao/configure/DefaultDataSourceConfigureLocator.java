package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultDataSourceConfigureLocator implements DataSourceConfigureLocator {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataSourceConfigureLocator.class);
    private static final String SEPARATOR = "\\.";
    protected PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();

    private Map<String, ConnectionString> connectionStrings = new ConcurrentHashMap<>();
    protected AtomicReference<PropertiesWrapper> propertiesWrapperReference = new AtomicReference<>();
    private AtomicReference<IPDomainStatus> ipDomainStatusReference = new AtomicReference<>(IPDomainStatus.IP);

    // user datasource.xml pool properties configure
    private Map<String, PoolPropertiesConfigure> userPoolPropertiesConfigure = new ConcurrentHashMap<>();

    private Map<String, DataSourceConfigure> dataSourceConfiguresCache = new ConcurrentHashMap<>();

    private Set<String> dataSourceConfigureKeySet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

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
        DataSourceConfigure c = cloneDataSourceConfigure(null);

        try {
            PropertiesWrapper wrapper = propertiesWrapperReference.get();
            // override app-level properties
            Properties appProperties = wrapper.getAppProperties();
            if (appProperties != null && !appProperties.isEmpty()) {
                overrideProperties(c.getProperties(), appProperties);
                String log = "App 覆盖结果:" + poolPropertiesHelper.propertiesToString(c.getProperties());
                LOGGER.info(log);
            }

            // override datasource-level properties
            Map<String, Properties> datasourceProperties = wrapper.getDatasourceProperties();
            if (datasourceProperties != null && !datasourceProperties.isEmpty()) {
                if (name != null) {
                    Properties p1 = datasourceProperties.get(name);
                    if (p1 != null && !p1.isEmpty()) {
                        overrideProperties(c.getProperties(), p1);
                        String log = name + " 覆盖结果:" + poolPropertiesHelper.propertiesToString(c.getProperties());
                        LOGGER.info(log);
                    } else {
                        String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
                        possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
                        Properties p2 = datasourceProperties.get(possibleName);
                        if (p2 != null && !p2.isEmpty()) {
                            overrideProperties(c.getProperties(), p2);
                            String log = possibleName + " 覆盖结果："
                                    + poolPropertiesHelper.propertiesToString(c.getProperties());
                            LOGGER.info(log);
                        }
                    }
                }
            }

            // override config from connection settings,datasource.xml
            overrideConnectionStringConfigureAndDataSourceXml(connectionStringConfigure, c, connectionString, name);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }

        return c;
    }

    protected void overrideConnectionStringConfigureAndDataSourceXml(
            ConnectionStringConfigure connectionStringConfigure, DataSourceConfigure c,
            ConnectionString connectionString, String name) {
        if (connectionStringConfigure != null) {
            // merge connection string configure
            mergeConnectionStringConfigure(c, connectionStringConfigure);
            c.setConnectionString(connectionString);
            String log = "connection url:" + connectionStringConfigure.getConnectionUrl();
            LOGGER.info(log);

            // override datasource.xml
            if (name != null) {
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
        String originalXml = String.format("datasource.xml 属性：%s",
                poolPropertiesHelper.propertiesToString(dataSourceXml.getProperties()));
        overrideProperties(c.getProperties(), dataSourceXml.getProperties());
        String xmlLog = "datasource.xml 覆盖结果:" + poolPropertiesHelper.propertiesToString(c.toProperties());
        LOGGER.info(xmlLog);
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

        dataSourceConfigure.setName(connectionStringConfigure.getName());
        dataSourceConfigure.setConnectionUrl(connectionStringConfigure.getConnectionUrl());
        dataSourceConfigure.setUserName(connectionStringConfigure.getUserName());
        dataSourceConfigure.setPassword(connectionStringConfigure.getPassword());
        dataSourceConfigure.setDriverClass(connectionStringConfigure.getDriverClass());
        dataSourceConfigure.setVersion(connectionStringConfigure.getVersion());
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
