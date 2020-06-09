package com.ctrip.datasource.configure.qconfig;

import com.ctrip.datasource.util.HeraldTokenUtils;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;
import qunar.tc.qconfig.client.exception.ResultUnexpectedException;
import qunar.tc.qconfig.client.plugin.HeaderPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionStringProviderImpl implements ConnectionStringProvider, DataSourceConfigureConstants {
    private static final String TITAN_APP_ID = "100010061";
    private ILogger logger= DalElementFactory.DEFAULT.getILogger();
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
    private String CONNECTIONSTRING_EXCEPTION_MESSAGE_FORMAT = "[TitanKey: %s, Exception: %s] ";
    private String ON_LOAD_PARAMETER_EXCEPTION = "Map parameter for titan key %s of onLoad event is null.";

    private ConcurrentMap<String, MapConfig> configMap = new ConcurrentHashMap<>();
    private Set<String> keyNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public Map<String, DalConnectionString> getConnectionStrings(Set<String> dbNames) throws Exception {
        Map<String, DalConnectionString> configures = new HashMap<>();
        if (dbNames == null || dbNames.isEmpty())
            return configures;

        for (String name : dbNames) {
            String keyName = ConnectionStringKeyHelper.getKeyName(name);
            MapConfig config = getMapConfig(name);
            if (config != null) {
                String ipConnectionString;
                String domainConnectionString;
                String transactionName = String.format(CONNECTION_STRING_GET_CONNECTIONSTRING_FORMAT, name);
                Transaction transaction = Cat.newTransaction(DalLogTypes.DAL_CONFIGURE, transactionName);

                try {
                    Map<String, String> map = config.asMap();
                    ipConnectionString = map.get(TITAN_KEY_NORMAL);
                    domainConnectionString = map.get(TITAN_KEY_FAILOVER);
                } catch (ResultUnexpectedException e) {
                    String errorMessage = e.getMessage();
                    if (e.getStatus() == HTTP_STATUS_CODE_404) {
                        errorMessage = String.format(QCONFIG_404_EXCEPTION_MESSAGE_FORMAT, keyName);
                    }
                    logger.error(errorMessage,e);
                    configures.put(keyName, new InvalidConnectionString(keyName, new DalException(errorMessage, e)));
                    transaction.setStatus(e);
                    transaction.complete();
                    continue;
                } catch (Throwable e) {
                    String errorMessage = String.format(CONNECTIONSTRING_EXCEPTION_MESSAGE_FORMAT, keyName, e.getMessage());
                    logger.error(errorMessage,e);
                    configures.put(keyName, new InvalidConnectionString(keyName, new DalException(errorMessage, e)));
                    transaction.setStatus(e);
                    transaction.complete();
                    continue;
                }

                DalConnectionString connectionString =
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
                    Cat.logEvent(DalLogTypes.DAL_CONFIGURE, transactionName, Message.SUCCESS, msg);
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
        Transaction transaction = Cat.newTransaction(DalLogTypes.DAL_CONFIGURE, String.format(CONNECTION_STRING_GET_MAPCONFIG_FORMAT, name));
        try {
            String keyName = ConnectionStringKeyHelper.getKeyName(name);
            Feature feature = Feature.create().setHttpsEnable(true).build();
            // register herald token
            HeraldTokenUtils.registerHeraldToken(TITAN_APP_ID, keyName);
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
                if (map == null || map.isEmpty())
                    throw new RuntimeException(String.format(ON_LOAD_PARAMETER_EXCEPTION, name));

                Transaction transaction =
                        Cat.newTransaction(DalLogTypes.DAL_CONFIGURE, String.format(CONNECTION_STRING_LISTENER_ON_LOAD_FORMAT, name));
                try {
                    String keyName = ConnectionStringKeyHelper.getKeyName(name);
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
        DalConnectionString connectionString =
                new ConnectionString(keyName, ipConnectionString, domainConnectionString);
        callback.onChanged(connectionString);
    }

}
