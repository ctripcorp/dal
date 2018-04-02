package com.ctrip.datasource.configure.qconfig;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
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
    private static final String TITAN_APP_ID = "100010061";
    private static final String DAL = "DAL";
    private static final String GET_CONNECTIONSTRING = "ConnectionString::getConnectionString";
    private static final String NORMAL_CONNECTIONSTRING = "Normal ConnectionString";
    private static final String FAILOVER_CONNECTIONSTRING = "Failover ConnectionString";

    private ConnectionStringParser connectionStringParser = ConnectionStringParser.getInstance();
    private Map<String, MapConfig> configMap = new ConcurrentHashMap<>();
    private Set<String> keyNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

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
                String normalConnectionString;
                String failoverConnectionString;
                String transactionName = String.format("%s:%s", GET_CONNECTIONSTRING, name);
                Transaction transaction = Cat.newTransaction(DAL, transactionName);

                try {
                    Map<String, String> map = config.asMap();
                    normalConnectionString = map.get(TITAN_KEY_NORMAL);
                    failoverConnectionString = map.get(TITAN_KEY_FAILOVER);
                } catch (Throwable e) {
                    throw new DalException(String.format("Error getting connection string from QConfig for %s", name),
                            e);
                }

                DataSourceConfigure configure;
                DataSourceConfigure failoverConfigure;
                try {
                    configure = connectionStringParser.parse(name, normalConnectionString);
                    failoverConfigure = connectionStringParser.parse(name, failoverConnectionString);

                    String msg = String.format("%s=%s,%s=%s", NORMAL_CONNECTIONSTRING, configure.getConnectionUrl(),
                            FAILOVER_CONNECTIONSTRING, failoverConfigure.getConnectionUrl());
                    transaction.addData(NORMAL_CONNECTIONSTRING, configure.getConnectionUrl());
                    transaction.addData(FAILOVER_CONNECTIONSTRING, failoverConfigure.getConnectionUrl());
                    transaction.setStatus(Transaction.SUCCESS);
                    Cat.logEvent(DAL, transactionName, Message.SUCCESS, msg);
                } catch (Throwable e) {
                    transaction.setStatus(e);
                    throw new IllegalArgumentException(String.format("Connection string of %s is illegal.", name), e);
                } finally {
                    transaction.complete();
                }

                ConnectionString connectionString =
                        new ConnectionString(normalConnectionString, failoverConnectionString);
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

                callback.onChanged(map);
            }
        });
    }

}