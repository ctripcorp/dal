package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.dao.datasource.ClusterDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceName;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultDataSourceConfigureLocator implements DataSourceConfigureLocator {
    protected static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    protected PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();

    private Map<DataSourceIdentity, DalConnectionString> apiConnectionStrings = new ConcurrentHashMap<>();
    private Map<String, DalConnectionString> connectionStrings = new ConcurrentHashMap<>();
    protected AtomicReference<PropertiesWrapper> propertiesWrapperReference = new AtomicReference<>();
    private AtomicReference<IPDomainStatus> ipDomainStatusReference = new AtomicReference<>(IPDomainStatus.IP);

    // user datasource.xml pool properties configure
    private Map<String, DalPoolPropertiesConfigure> userPoolPropertiesConfigure = new ConcurrentHashMap<>();
    private Map<DataSourceIdentity, DataSourceConfigure> dataSourceConfiguresCache = new ConcurrentHashMap<>();

    private static final String SEPARATOR = "\\.";
    private static final String CLUSTER_SPLITTER = "-";
    private static final String DATASOURCE_PROPERTIES_EXCEPTION_MESSAGE =
            "An error occured while getting datasource properties.";
    private static final String APP_OVERRIDE_RESULT = "App override result: ";
    private static final String OVERRIDE_RESULT = " override result: ";
    private static final String DATASOURCE_XML_OVERRIDE_RESULT = "datasource.xml override result: ";
    private static final String CONNECTION_URL = "connection url:";

    private String FINAL_OVERRIDE_RESULT_FORMAT = "Final override result: %s";
    protected String POOLPROPERTIES_MERGEPOOLPROPERTIES_FORMAT = "PoolProperties::mergePoolProperties:%s";

    @Override
    public void addUserPoolPropertiesConfigure(String name, DalPoolPropertiesConfigure configure) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        userPoolPropertiesConfigure.put(keyName, configure);
        dataSourceConfiguresCache.put(new DataSourceName(name), new DataSourceConfigure(keyName, configure.getProperties()));
    }

    @Override
    public DalPoolPropertiesConfigure getUserPoolPropertiesConfigure(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return userPoolPropertiesConfigure.get(keyName);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String name) {
        return getDataSourceConfigure(new DataSourceName(name));
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(DataSourceIdentity id) {
        DataSourceConfigure configure = dataSourceConfiguresCache.get(id);
        if (configure != null)
            return configure;

        DalConnectionString connectionString = getConnectionString(id);
        if (connectionString instanceof DalInvalidConnectionString)
            return null;

        configure = mergeDataSourceConfigure(connectionString);
        if (configure != null) {
            dataSourceConfiguresCache.put(id, configure);
        }
        return configure;
    }

    @Override
    public void removeDataSourceConfigure(DataSourceIdentity id) {
        dataSourceConfiguresCache.remove(id);
    }

    private DalConnectionString getConnectionString(DataSourceIdentity id) {
        if (id instanceof ClusterDataSourceIdentity)
            return ((ClusterDataSourceIdentity) id).getDalConnectionString();
        else if (id instanceof ApiDataSourceIdentity) {
            DalConnectionString dalConnectionString = apiConnectionStrings.get(id);

            if (dalConnectionString == null) {
                dalConnectionString = ((ApiDataSourceIdentity) id).getConnectionString();
                apiConnectionStrings.put(id, dalConnectionString);
            }
            return dalConnectionString;
        }
        else
            return connectionStrings.get(id.getId());
    }

    private void removeDataSourceConfigures() {
        dataSourceConfiguresCache.clear();
    }

    @Override
    public Set<String> getDataSourceConfigureKeySet() {
        Set<String> keySet = new HashSet<>();
        for (Map.Entry<String, DalConnectionString> entry : connectionStrings.entrySet()) {
            keySet.add(entry.getKey());
        }
        return keySet;
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
    public void setConnectionStrings(Map<String, DalConnectionString> map) {
        if (map == null || map.isEmpty())
            return;

        for (Map.Entry<String, DalConnectionString> entry : map.entrySet()) {
            String keyName = ConnectionStringKeyHelper.getKeyName(entry.getKey());
            connectionStrings.put(keyName, entry.getValue());
            dataSourceConfiguresCache.remove(new DataSourceName(keyName));
        }
    }

    @Override
    public DalConnectionString setConnectionString(String name, DalConnectionString connectionString) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        DalConnectionString oldConnectionString = connectionStrings.put(keyName, connectionString);
        dataSourceConfiguresCache.remove(new DataSourceName(keyName));
        return (oldConnectionString instanceof DalInvalidConnectionString) ? null : oldConnectionString;
    }

    @Override
    public DalConnectionString setApiConnectionString(DataSourceIdentity id, DalConnectionString connectionString) {
        DalConnectionString oldConnectionString = apiConnectionStrings.put(id, connectionString);
        dataSourceConfiguresCache.remove(id);
        return (oldConnectionString instanceof DalInvalidConnectionString) ? null : oldConnectionString;
    }

    public Map<String, DalConnectionString> getAllConnectionStrings() {
        Map<String, DalConnectionString> copyConnectionStrings = new ConcurrentHashMap<>(connectionStrings);
        return copyConnectionStrings;
    }

    public Map<String, DalConnectionString> getSuccessfulConnectionStrings() {
        Map<String, DalConnectionString> successfulConnectionStrings = new ConcurrentHashMap<>();
        for (Map.Entry<String, DalConnectionString> entry : connectionStrings.entrySet()) {
            if (!(entry.getValue() instanceof DalInvalidConnectionString))
                successfulConnectionStrings.put(ConnectionStringKeyHelper.getKeyName(entry.getKey()), entry.getValue());
        }
        return successfulConnectionStrings;
    }

    public Map<String, DalConnectionString> getFailedConnectionStrings() {
        Map<String, DalConnectionString> failedConnectionStrings = new ConcurrentHashMap<>();
        for (Map.Entry<String, DalConnectionString> entry : connectionStrings.entrySet()) {
            if (entry.getValue() instanceof DalInvalidConnectionString)
                failedConnectionStrings.put(ConnectionStringKeyHelper.getKeyName(entry.getKey()), entry.getValue());
        }
        return failedConnectionStrings;
    }

    @Override
    public Properties setPoolProperties(DalPoolPropertiesConfigure configure) {
        PropertiesWrapper oldWrapper = propertiesWrapperReference.get();
        Properties oldOriginalProperties =
                oldWrapper == null ? null : deepCopyProperties(oldWrapper.getOriginalProperties());
        if (configure == null)
            return oldOriginalProperties;

        Properties properties = configure.getProperties();
        Properties newOriginalProperties = deepCopyProperties(properties);
        Properties appProperties = new Properties(); // app level
        Map<String, Properties> datasourceProperties = new HashMap<>(); // datasource level
        processProperties(properties, appProperties, datasourceProperties);

        PropertiesWrapper wrapper = new PropertiesWrapper(newOriginalProperties, appProperties, datasourceProperties);
        propertiesWrapperReference.set(wrapper);
        removeDataSourceConfigures();

        return oldOriginalProperties;
    }

    public PropertiesWrapper getPoolProperties() {
        return propertiesWrapperReference.get();
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
    public DataSourceConfigure mergeDataSourceConfigure(DalConnectionString connectionString) {
        DalConnectionStringConfigure connectionStringConfigure = getConnectionStringConfigure(connectionString);
        if (connectionStringConfigure == null)
            return null;

        String name = connectionStringConfigure.getName();
        String logName = String.format(POOLPROPERTIES_MERGEPOOLPROPERTIES_FORMAT, name);
        DataSourceConfigure c = cloneDataSourceConfigure(null);

        try {
            PropertiesWrapper wrapper = propertiesWrapperReference.get();
            if (wrapper == null)
                return c;

            overrideProperties(connectionStringConfigure, connectionString, wrapper, c, name, logName);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }

        return c;
    }

    protected void overrideProperties(DalConnectionStringConfigure connectionStringConfigure,
            DalConnectionString connectionString, PropertiesWrapper wrapper, DataSourceConfigure c, String name,
            String logName) {
        // override app-level properties
        overrideAppLevelProperties(wrapper, c, logName);

        // override datasource-level properties
        overrideDataSourceLevelProperties(wrapper, c, name, logName, connectionString);

        // override config from connection settings,datasource.xml
        overrideConnectionStringConfigureAndDataSourceXml(connectionStringConfigure, c, connectionString, name);

        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName,
                String.format(FINAL_OVERRIDE_RESULT_FORMAT, poolPropertiesHelper.propertiesToString(c.toProperties())));
    }

    private void overrideAppLevelProperties(PropertiesWrapper wrapper, DataSourceConfigure c, String logName) {
        Properties appProperties = wrapper.getAppProperties();
        if (appProperties == null || appProperties.isEmpty())
            return;

        overrideProperties(c.getProperties(), appProperties);
        String log = APP_OVERRIDE_RESULT + poolPropertiesHelper.propertiesToString(c.getProperties());
        LOGGER.info(log);
        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, log);
    }

    private void overrideDataSourceLevelProperties(PropertiesWrapper wrapper, DataSourceConfigure c, String name,
                                                   String logName, DalConnectionString connectionString) {
        if (name == null || name.isEmpty())
            return;

        Map<String, Properties> datasourceProperties = wrapper.getDatasourceProperties();
        if (datasourceProperties == null || datasourceProperties.isEmpty())
            return;

        overrideDataSourceLevelProperties(datasourceProperties, c, name, logName, connectionString);
    }

    private void overrideDataSourceLevelProperties(Map<String, Properties> datasourceProperties, DataSourceConfigure c,
                                                   String name, String logName, DalConnectionString connectionString) {
        if (name.contains(CLUSTER_SPLITTER)) {
            String[] aliasKeys = null;
            if (connectionString instanceof ClusterDataSourceIdentity.ClusterConnectionStringImpl)
                aliasKeys = ((ClusterDataSourceIdentity.ClusterConnectionStringImpl) connectionString).getAliasKeys();
            overrideClusterDataSourceProperties(datasourceProperties, c, name, logName, aliasKeys);
        }
        else
            overrideNamedDataSourceProperties(datasourceProperties, c, name, logName);
    }

    private boolean overrideNamedDataSourceProperties(Map<String, Properties> datasourceProperties,
                                                      DataSourceConfigure c, String name, String logName) {
        Properties p = datasourceProperties.get(name);
        if (p != null && !p.isEmpty()) {
            overrideDataSourceProperties(p, c, name, logName);
            return true;
        } else {
            String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
            possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
            p = datasourceProperties.get(possibleName);
            if (p != null && !p.isEmpty()) {
                overrideDataSourceProperties(p, c, possibleName, logName);
                return true;
            }
        }
        return false;
    }

    private boolean overrideClusterDataSourceProperties(Map<String, Properties> datasourceProperties, DataSourceConfigure c,
                                                        String name, String logName, String[] aliasKeys) {
        String[] identities = name.split(CLUSTER_SPLITTER);
        if (identities.length < 3) {
            // warn
            return false;
        } else {
            String cluster = identities[0];
            String shard = identities[1];
            String role = identities[2];
            // step 1: cluster-shard-role
            String id = String.format("%s-%s-%s", cluster, shard, role);
            Properties p = datasourceProperties.get(id);
            if (p != null && !p.isEmpty()) {
                overrideDataSourceProperties(p, c, id, logName);
                return true;
            }
            // step 2: cluster-role
            id = String.format("%s-%s", cluster, role);
            p = datasourceProperties.get(id);
            if (p != null && !p.isEmpty()) {
                overrideDataSourceProperties(p, c, id, logName);
                return true;
            }
            // step 3: cluster-shard
            id = String.format("%s-%s", cluster, shard);
            p = datasourceProperties.get(id);
            if (p != null && !p.isEmpty()) {
                overrideDataSourceProperties(p, c, id, logName);
                return true;
            }
            // step 4: cluster
            id = cluster;
            p = datasourceProperties.get(id);
            if (p != null && !p.isEmpty()) {
                overrideDataSourceProperties(p, c, id, logName);
                return true;
            }
            // step 5: alias keys
            if (aliasKeys != null && aliasKeys.length > 0)
                for (int i = 0; i < aliasKeys.length; i++)
                    if (overrideNamedDataSourceProperties(datasourceProperties, c, aliasKeys[i], logName))
                        return true;
            // no override
            return false;
        }
    }

    private void overrideDataSourceProperties(Properties p, DataSourceConfigure c, String name, String logName) {
        overrideProperties(c.getProperties(), p);
        String log = name + OVERRIDE_RESULT + poolPropertiesHelper.propertiesToString(c.getProperties());
        LOGGER.info(log);
        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, log);
    }

    protected void overrideConnectionStringConfigureAndDataSourceXml(
            DalConnectionStringConfigure connectionStringConfigure, DataSourceConfigure c,
            DalConnectionString connectionString, String name) {
        if (connectionStringConfigure == null)
            return;

        // merge connection string configure
        mergeConnectionStringConfigure(c, connectionStringConfigure, connectionString);

        // override datasource.xml
        overrideDataSourceXml(name, c);
    }

    private void mergeConnectionStringConfigure(DataSourceConfigure c,
            DalConnectionStringConfigure connectionStringConfigure, DalConnectionString connectionString) {
        mergeConnectionStringConfigure(c, connectionStringConfigure);
        c.setConnectionString(connectionString);
        String log = CONNECTION_URL + connectionStringConfigure.getConnectionUrl();
        LOGGER.info(log);
    }

    private void overrideDataSourceXml(String name, DataSourceConfigure c) {
        if (name == null || name.isEmpty())
            return;

        DalPoolPropertiesConfigure dataSourceXml = getUserPoolPropertiesConfigure(name);
        if (dataSourceXml != null) {
            overrideDataSourceXml(c, dataSourceXml);
        } else {
            String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
            possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
            DalPoolPropertiesConfigure ppc = getUserPoolPropertiesConfigure(possibleName);
            if (ppc != null) {
                overrideDataSourceXml(c, ppc);
            }
        }
    }

    protected DalConnectionStringConfigure getConnectionStringConfigure(DalConnectionString connectionString) {
        if (connectionString == null)
            return null;

        DalConnectionStringConfigure configure = null;
        IPDomainStatus status = getIPDomainStatus();
        if (status.equals(IPDomainStatus.IP)) {
            configure = connectionString.getIPConnectionStringConfigure();
        } else if (status.equals(IPDomainStatus.Domain)) {
            configure = connectionString.getDomainConnectionStringConfigure();
        }

        return configure;
    }

    protected void overrideDataSourceXml(DataSourceConfigure c, DalPoolPropertiesConfigure dataSourceXml) {
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
            DalConnectionStringConfigure connectionStringConfigure) {
        if (dataSourceConfigure == null || connectionStringConfigure == null)
            return;

        setName(dataSourceConfigure, connectionStringConfigure);
        setConnectionUrl(dataSourceConfigure, connectionStringConfigure);
        setUserName(dataSourceConfigure, connectionStringConfigure);
        setPassword(dataSourceConfigure, connectionStringConfigure);
        setDriverClass(dataSourceConfigure, connectionStringConfigure);
        setVersion(dataSourceConfigure, connectionStringConfigure);
        setHostName(dataSourceConfigure,connectionStringConfigure);
    }

    private void setHostName(DataSourceConfigure dataSourceConfigure, DalConnectionStringConfigure connectionStringConfigure) {
        String hostName = connectionStringConfigure.getHostName();
        if (hostName != null)
            dataSourceConfigure.setHostName(hostName);
    }

    private void setName(DataSourceConfigure dataSourceConfigure, DalConnectionStringConfigure connectionStringConfigure) {
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
            DalConnectionStringConfigure connectionStringConfigure) {
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
