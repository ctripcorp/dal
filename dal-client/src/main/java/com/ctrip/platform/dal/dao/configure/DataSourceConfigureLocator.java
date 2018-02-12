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

public class DataSourceConfigureLocator {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfigureLocator.class);

    private volatile static DataSourceConfigureLocator locator = null;

    public synchronized static DataSourceConfigureLocator getInstance() {
        if (locator == null) {
            locator = new DataSourceConfigureLocator();
        }
        return locator;
    }

    private static AtomicReference<IPDomainStatus> ipDomainStatusReference = new AtomicReference<>(IPDomainStatus.IP);
    private AtomicReference<DataSourceConfigureWrapper> dataSourceConfigureWrapperReference = new AtomicReference<>();

    private static final String DAL_MERGE_DATASOURCE = "DataSource::mergeDataSourceConfig";
    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();
    private ConnectionStringParser parser = ConnectionStringParser.getInstance();

    // user datasource.xml configure
    private Map<String, DataSourceConfigure> userDataSourceConfigures = new ConcurrentHashMap<>();

    private Map<String, DataSourceConfigure> dataSourceConfigures = new ConcurrentHashMap<>();

    private Set<String> dataSourceConfigureKeySet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    public void setIPDomainStatus(IPDomainStatus status) {
        ipDomainStatusReference.set(status);
    }

    public IPDomainStatus getIPDomainStatus() {
        return ipDomainStatusReference.get();
    }

    public void setDataSourceConfigureWrapperReference(DataSourceConfigureWrapper wrapper) {
        dataSourceConfigureWrapperReference.set(wrapper);
    }

    public DataSourceConfigure parseConnectionString(String name, String connectionString) {
        return parser.parse(name, connectionString);
    }

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
        c.setConnectionString(configure.getConnectionString());
        return c;
    }

    // TODO CAT log
    public DataSourceConfigure mergeDataSourceConfigure(DataSourceConfigure configure) {
        DataSourceConfigure c = cloneDataSourceConfigure(null);
        // Transaction transaction = Cat.newTransaction(DAL_DATASOURCE, DAL_MERGE_DATASOURCE);

        try {
            DataSourceConfigureWrapper wrapper = dataSourceConfigureWrapperReference.get();

            // override app-level config from QConfig
            DataSourceConfigure dataSourceConfigure = wrapper.getDataSourceConfigure();
            if (dataSourceConfigure != null) {
                overrideDataSourceConfigure(c, dataSourceConfigure);
                String log = "App 覆盖结果:" + poolPropertiesHelper.mapToString(c.toMap());
                logger.info(log);
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
                        logger.info(log);
                    } else {
                        String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
                        possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
                        DataSourceConfigure sc = dataSourceConfigureMap.get(possibleName);
                        if (sc != null) {
                            overrideDataSourceConfigure(c, sc);
                            String log = possibleName + " 覆盖结果:" + poolPropertiesHelper.mapToString(c.toMap());
                            logger.info(log);
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
                String log = "connection settings 覆盖结果:" + poolPropertiesHelper.mapToString(c.toMap());
                logger.info(log);

                // override datasource.xml
                String name = configure.getName();
                if (name != null) {
                    DataSourceConfigure dataSourceXml = getUserDataSourceConfigure(name);
                    if (dataSourceXml != null) {
                        overrideDataSourceConfigure(c, dataSourceXml);
                        String xmlLog = "datasource.xml 覆盖结果:" + poolPropertiesHelper.mapToString(c.toMap());
                        logger.info(xmlLog);
                    } else {
                        String possibleName = DataSourceConfigureParser.getInstance().getPossibleName(name);
                        possibleName = ConnectionStringKeyHelper.getKeyName(possibleName);
                        DataSourceConfigure sc = getUserDataSourceConfigure(possibleName);
                        if (sc != null) {
                            overrideDataSourceConfigure(c, sc);
                            String xmlLog = "datasource.xml 覆盖结果:" + poolPropertiesHelper.mapToString(c.toMap());
                            logger.info(xmlLog);
                        }
                    }
                }
            }

            // Cat.logEvent(DAL_DATASOURCE, DAL_MERGE_DATASOURCE, Message.SUCCESS,
            // poolPropertiesHelper.mapToString(c.toMap()));
            Map<String, String> datasource = c.getMap();
            Properties prop = c.getProperties();
            setProperties(datasource, prop); // set properties from map
            // transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            // transaction.setStatus(e);
            logger.error(e.getMessage(), e);
        } finally {
            // transaction.complete();
        }
        return c;
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

    public DataSourceConfigure getUserDataSourceConfigure(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return userDataSourceConfigures.get(keyName);
    }

    public DataSourceConfigure getDataSourceConfigure(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return dataSourceConfigures.get(keyName);
    }

    public DataSourceConfigure getDataSourceConfigure(String name, IPDomainStatus status) {
        DataSourceConfigure temp = getDataSourceConfigure(name);
        ConnectionString connectionString = temp.getConnectionString();
        String cs = status.equals(IPDomainStatus.IP) ? connectionString.getNormalConnectionString()
                : connectionString.getFailoverConnectionString();
        DataSourceConfigure configure = parseConnectionString(name, cs);
        configure = mergeDataSourceConfigure(configure);
        configure.setConnectionString(connectionString);
        return configure;
    }

    public DataSourceConfigure getDataSourceConfigure(String name, String normalConnectionString,
            String failoverConnectionString) {
        ConnectionString connectionString = new ConnectionString(normalConnectionString, failoverConnectionString);
        IPDomainStatus status = getIPDomainStatus();
        String cs = status.equals(IPDomainStatus.IP) ? normalConnectionString : failoverConnectionString;
        DataSourceConfigure configure = parseConnectionString(name, cs);
        configure = mergeDataSourceConfigure(configure);
        configure.setConnectionString(connectionString);
        return configure;
    }

    public void addDataSourceConfigureKeySet(Set<String> dbNames) {
        if (dbNames == null || dbNames.isEmpty())
            return;

        for (String name : dbNames) {
            addDataSourceConfigureKey(name);
        }
    }

    private void addDataSourceConfigureKey(String name) {
        if (name == null || name.isEmpty())
            return;

        dataSourceConfigureKeySet.add(name);
    }

    public Set<String> getDataSourceConfigureKeySet() {
        return dataSourceConfigureKeySet;
    }

    public void addUserDataSourceConfigure(String name, DataSourceConfigure configure) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        userDataSourceConfigures.put(keyName, configure);
    }

    public void addDataSourceConfigure(String name, DataSourceConfigure configure) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        dataSourceConfigures.put(keyName, configure);
    }

    public boolean contains(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        return dataSourceConfigures.containsKey(keyName);
    }

}
