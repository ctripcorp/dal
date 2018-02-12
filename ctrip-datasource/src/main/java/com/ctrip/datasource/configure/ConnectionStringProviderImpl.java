package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionStringProviderImpl implements ConnectionStringProvider, DataSourceConfigureConstants {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionStringProviderImpl.class);

    private static final String TITAN_APP_ID = "100010061";
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

    @Override
    public Map<String, DataSourceConfigure> getConnectionStrings(Set<String> dbNames) throws Exception {
        refreshConnectionStringMapConfig(dbNames);
        return _getConnectionStrings(dbNames);
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
                throw new RuntimeException(new FileNotFoundException(String
                        .format("Error occured while getting titan keyname %s from QConfig:%s", name, e.getMessage())));
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
            MapConfig config = getConfigMap(name);

            if (config != null) {
                String cs = null;
                String failoverConnectionString = null;
                try {
                    Map<String, String> map = config.asMap();
                    cs = map.get(TITAN_KEY_NORMAL);
                    failoverConnectionString = map.get(TITAN_KEY_FAILOVER);
                } catch (Throwable e) {
                    throw new DalException(String.format("Error getting connection string from QConfig for %s", name),
                            e);
                }

                DataSourceConfigure configure = null;
                try {
                    configure = dataSourceConfigureLocator.parseConnectionString(name, cs);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(String.format("Connection string of %s is illegal.", name), e);
                }

                ConnectionString connectionString = new ConnectionString(cs, failoverConnectionString);
                configure.setConnectionString(connectionString);
                configures.put(keyName, configure);
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
                    return;
                }

                String normalConnectionString = map.get(TITAN_KEY_NORMAL);
                if (normalConnectionString == null || normalConnectionString.isEmpty())
                    throw new RuntimeException("Normal connection string is null.");

                String failoverConnectionString = map.get(TITAN_KEY_FAILOVER);
                if (failoverConnectionString == null || failoverConnectionString.isEmpty())
                    throw new RuntimeException("Failover connection string is null.");

                // validate version
                DataSourceConfigure configure =
                        dataSourceConfigureLocator.parseConnectionString(name, normalConnectionString);
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

}
