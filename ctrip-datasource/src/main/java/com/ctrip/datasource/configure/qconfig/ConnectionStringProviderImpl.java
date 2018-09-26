package com.ctrip.datasource.configure.qconfig;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;
import qunar.tc.qconfig.client.exception.ResultUnexpectedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionStringProviderImpl implements ConnectionStringProvider, DataSourceConfigureConstants {
    private static final String TITAN_APP_ID = "100010061";
    private static final String DAL = "DAL";
    private String CONNECTION_STRING_GET_MAPCONFIG_FORMAT = "ConnectionString::getMapConfig:%s";
    private String CONNECTION_STRING_GET_CONNECTIONSTRING_FORMAT = "ConnectionString::getConnectionString:%s";
    private String CONNECTION_STRING_LISTENER_ON_LOAD_FORMAT = "ConnectionString::listenerOnLoad:%s";

    private static final String NORMAL_CONNECTIONSTRING = "Normal ConnectionString";
    private static final String FAILOVER_CONNECTIONSTRING = "Failover ConnectionString";
    private static final int HTTP_STATUS_CODE_404 = 404;

    private String NULL_MAPCONFIG_EXCEPTION_FORMAT = "MapConfig for titan key %s is null.";
    private String QCONFIG_COMMON_EXCEPTION_MESSAGE_FORMAT =
            "An error occured while getting connection string from QConfig for titan key %s.";
    private String QCONFIG_404_EXCEPTION_MESSAGE_FORMAT =
            "Titan key %s does not exist or has been disabled, please remove it from your Dal.config or code.";
    private String ON_LOAD_PARAMETER_EXCEPTION = "Map parameter for titan key %s of onLoad event is null.";

    private ConcurrentMap<String, MapConfig> configMap = new ConcurrentHashMap<>();
    private Set<String> keyNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public Map<String, ConnectionString> getConnectionStrings(Set<String> dbNames) throws Exception {
        Map<String, ConnectionString> configures = new HashMap<>();
        if (dbNames == null || dbNames.isEmpty())
            return configures;

        for (String name : dbNames) {
            String keyName = ConnectionStringKeyHelper.getKeyName(name);
            MapConfig config = getMapConfig(name);
            if (config != null) {
                ConnectionString connectionString = getConnectionString(config, keyName, name);
                configures.put(keyName, connectionString);
            }
        }

        return configures;
    }

    private MapConfig getMapConfig(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        if (!configMap.containsKey(keyName)) {
            MapConfig mapConfig = _getMapConfig(keyName);
            configMap.put(keyName, mapConfig);
        }

        return configMap.get(keyName);
    }

    private MapConfig _getMapConfig(String name) {
        MapConfig config = null;
        Transaction transaction = Cat.newTransaction(DAL, String.format(CONNECTION_STRING_GET_MAPCONFIG_FORMAT, name));
        try {
            String keyName = ConnectionStringKeyHelper.getKeyName(name);
            Feature feature = Feature.create().setHttpsEnable(true).build();
            config = MapConfig.get(TITAN_APP_ID, keyName, feature);
            if (config == null)
                throw new RuntimeException(String.format(NULL_MAPCONFIG_EXCEPTION_FORMAT, name));

            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            Cat.logError(String.format(QCONFIG_COMMON_EXCEPTION_MESSAGE_FORMAT, name), e);
            throw e;
        } finally {
            transaction.complete();
        }

        return config;
    }

    private ConnectionString getConnectionString(MapConfig config, String keyName, String name) throws Exception {
        ConnectionString connectionString = null;
        String transactionName = String.format(CONNECTION_STRING_GET_CONNECTIONSTRING_FORMAT, name);
        Transaction transaction = Cat.newTransaction(DAL, transactionName);

        try {
            Map<String, String> map = config.asMap();
            connectionString = _getConnectionString(map, keyName);
            transaction.addData(NORMAL_CONNECTIONSTRING,
                    connectionString.getIPConnectionStringConfigure().getConnectionUrl());
            transaction.addData(FAILOVER_CONNECTIONSTRING,
                    connectionString.getDomainConnectionStringConfigure().getConnectionUrl());
            transaction.setStatus(Transaction.SUCCESS);
        } catch (ResultUnexpectedException e) {
            String exceptionMessageFormat = QCONFIG_COMMON_EXCEPTION_MESSAGE_FORMAT;
            if (e.getStatus() == HTTP_STATUS_CODE_404)
                exceptionMessageFormat = QCONFIG_404_EXCEPTION_MESSAGE_FORMAT;

            throw new DalException(String.format(exceptionMessageFormat, name), e);
        } catch (Throwable e) {
            throw new DalException(String.format(QCONFIG_COMMON_EXCEPTION_MESSAGE_FORMAT, name), e);
        }

        return connectionString;
    }

    private ConnectionString _getConnectionString(Map<String, String> map, String keyName) {
        String ipConnectionString = map.get(TITAN_KEY_NORMAL);
        String domainConnectionString = map.get(TITAN_KEY_FAILOVER);
        return new ConnectionString(keyName, ipConnectionString, domainConnectionString);
    }

    @Override
    public void addConnectionStringChangedListener(final String name, final ConnectionStringChanged callback) {
        MapConfig config = getMapConfig(name);
        _addConnectionStringChangedListener(config, name, callback);
    }

    private void _addConnectionStringChangedListener(MapConfig config, final String name,
            final ConnectionStringChanged callback) {
        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                Transaction transaction =
                        Cat.newTransaction(DAL, String.format(CONNECTION_STRING_LISTENER_ON_LOAD_FORMAT, name));
                try {
                    if (map == null || map.isEmpty())
                        throw new RuntimeException(String.format(ON_LOAD_PARAMETER_EXCEPTION, name));

                    String keyName = ConnectionStringKeyHelper.getKeyName(name);
                    if (!keyNames.contains(keyName)) {
                        keyNames.add(keyName);
                        return;
                    }

                    executeCallback(keyName, map, callback);
                    transaction.setStatus(Transaction.SUCCESS);
                } catch (Throwable e) {
                    transaction.setStatus(e);
                    Cat.logError(e);
                    throw e;
                } finally {
                    transaction.complete();
                }
            }
        });
    }

    private void executeCallback(String keyName, Map<String, String> map, final ConnectionStringChanged callback) {
        String ipConnectionString = map.get(TITAN_KEY_NORMAL);
        String domainConnectionString = map.get(TITAN_KEY_FAILOVER);
        ConnectionString connectionString = new ConnectionString(keyName, ipConnectionString, domainConnectionString);
        callback.onChanged(connectionString);
    }

}
