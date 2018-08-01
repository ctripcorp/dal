package com.ctrip.datasource.configure.qconfig;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.ConnectionStringConfigure;
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
import qunar.tc.qconfig.client.exception.ResultUnexpectedException;

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
    private static final int HTTP_STATUS_CODE_404 = 404;
    private String QCONFIG_COMMON_EXCEPTION_MESSAGE_FORMAT =
            "An error occured while getting connection string from QConfig for titan key %s.";
    private String QCONFIG_404_EXCEPTION_MESSAGE_FORMAT =
            "Titan key %s does not exist or has been disabled, please remove it from your Dal.config or code.";

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
    public Map<String, ConnectionString> getConnectionStrings(Set<String> dbNames) throws Exception {
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
                        .format("An error occurred while getting titan key %s from QConfig:%s", name, e.getMessage())));
            }
        }
    }

    private MapConfig getTitanMapConfig(String name) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        Feature feature = Feature.create().setHttpsEnable(true).build();
        return MapConfig.get(TITAN_APP_ID, keyName, feature);
    }

    private Map<String, ConnectionString> _getConnectionStrings(Set<String> names) throws Exception {
        Map<String, ConnectionString> configures = new HashMap<>();
        if (names == null || names.isEmpty())
            return configures;

        for (String name : names) {
            String keyName = ConnectionStringKeyHelper.getKeyName(name);
            MapConfig config = getConfigMap(name);

            if (config != null) {
                String ipConnectionString;
                String domainConnectionString;
                String transactionName = String.format("%s:%s", GET_CONNECTIONSTRING, name);
                Transaction transaction = Cat.newTransaction(DAL, transactionName);

                try {
                    Map<String, String> map = config.asMap();
                    ipConnectionString = map.get(TITAN_KEY_NORMAL);
                    domainConnectionString = map.get(TITAN_KEY_FAILOVER);
                } catch (ResultUnexpectedException e) {
                    String exceptionMessageFormat = QCONFIG_COMMON_EXCEPTION_MESSAGE_FORMAT;
                    if (e.getStatus() == HTTP_STATUS_CODE_404) {
                        exceptionMessageFormat = QCONFIG_404_EXCEPTION_MESSAGE_FORMAT;
                    }

                    throw new DalException(String.format(exceptionMessageFormat, name), e);
                } catch (Throwable e) {
                    throw new DalException(String.format(QCONFIG_COMMON_EXCEPTION_MESSAGE_FORMAT, name), e);
                }

                ConnectionString connectionString =
                        new ConnectionString(keyName, ipConnectionString, domainConnectionString);
                ConnectionStringConfigure ipConfigure;
                ConnectionStringConfigure domainConfigure;
                try {
                    ipConfigure = connectionString.getIPConnectionStringConfigure();
                    domainConfigure = connectionString.getDomainConnectionStringConfigure();

                    String msg = String.format("%s=%s,%s=%s", NORMAL_CONNECTIONSTRING, ipConfigure.getConnectionUrl(),
                            FAILOVER_CONNECTIONSTRING, domainConfigure.getConnectionUrl());
                    transaction.addData(NORMAL_CONNECTIONSTRING, ipConfigure.getConnectionUrl());
                    transaction.addData(FAILOVER_CONNECTIONSTRING, domainConfigure.getConnectionUrl());
                    transaction.setStatus(Transaction.SUCCESS);
                    Cat.logEvent(DAL, transactionName, Message.SUCCESS, msg);
                } catch (Throwable e) {
                    transaction.setStatus(e);
                    throw new IllegalArgumentException(
                            String.format("Connection string of titan key %s is illegal.", name), e);
                } finally {
                    transaction.complete();
                }

                configures.put(keyName, connectionString);
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

                String ipConnectionString = map.get(TITAN_KEY_NORMAL);
                String domainConnectionString = map.get(TITAN_KEY_FAILOVER);
                ConnectionString connectionString =
                        new ConnectionString(keyName, ipConnectionString, domainConnectionString);

                callback.onChanged(connectionString);
            }
        });
    }

}
