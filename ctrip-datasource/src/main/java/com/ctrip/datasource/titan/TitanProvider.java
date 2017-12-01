package com.ctrip.datasource.titan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ctrip.datasource.configure.ConnectionStringProcessor;
import com.ctrip.datasource.util.DalEncrypter;
import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;

import com.ctrip.datasource.configure.DataSourceConfigureProcessor;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyNameHelper;
import com.ctrip.platform.dal.exceptions.DalConfigException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.status.ProductVersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

public class TitanProvider implements DataSourceConfigureProvider {
    private static final Logger logger = LoggerFactory.getLogger(TitanProvider.class);
    private static final String DAL_LOCAL_DATASOURCE = "DAL.local.datasource";

    private static final String DAL_DYNAMIC_DATASOURCE = "DAL";
    private static final String DAL_NOTIFY_LISTENER = "DataSource::notifyListener";
    private static final String DAL_NOTIFY_LISTENER_START = "DataSource.notifyListener.start";
    private static final String DAL_NOTIFY_LISTENER_END = "DataSource.notifyListener.end";

    private static final String DAL_REFRESH_DATASOURCE = "DataSource::refreshDataSourceConfig";
    private static final String DATASOURCE_OLD_CONNECTIONURL = "DataSource::oldConnectionUrl";
    private static final String DATASOURCE_NEW_CONNECTIONURL = "DataSource::newConnectionUrl";
    private static final String DATASOURCE_OLD_CONFIGURE = "DataSource::oldConfigure";
    private static final String DATASOURCE_NEW_CONFIGURE = "DataSource::newConfigure";

    private DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocator.getInstance();
    private DataSourceConfigureParser dataSourceConfigureParser = DataSourceConfigureParser.getInstance();

    private ConnectionStringProcessor connectionStringProcessor = ConnectionStringProcessor.getInstance();
    private DataSourceConfigureProcessor dataSourceConfigureProcessor = DataSourceConfigureProcessor.getInstance();

    private DalEncrypter encrypter = null;

    // DataSourceConfigure change listener
    private Map<String, DataSourceConfigureChangeListener> dataSourceConfigureChangeListeners =
            new ConcurrentHashMap<>();

    // Single thread,act as queue
    private ThreadPoolExecutor executor =
            new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public void initialize(Map<String, String> settings) throws Exception {
        connectionStringProcessor.initialize(settings);
        encrypter = new DalEncrypter(LoggerAdapter.DEFAULT_SECERET_KEY);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String dbName) {
        return dataSourceConfigureLocator.getDataSourceConfigure(dbName);
    }

    @Override
    public void setup(Set<String> dbNames) {
        if (dbNames == null || dbNames.isEmpty())
            return;

        dataSourceConfigureLocator.addDataSourceConfigureKeySet(dbNames);

        if (dataSourceConfigureParser.isDataSourceXmlExist())
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCE, connectionStringProcessor.getAppId());

        Map<String, DataSourceConfigure> dataSourceConfigures =
                connectionStringProcessor.initializeDataSourceConfigureConnectionSettings(dbNames);
        dataSourceConfigures = mergeDataSourceConfigures(dataSourceConfigures);
        addDataSourceConfigures(dataSourceConfigures);

        addConnectionStringChangeListeners(dbNames);
        addDataSourceConfigureChangeListeners();
        connectionStringProcessor.info("--- End datasource config  ---");
    }

    private Map<String, DataSourceConfigure> mergeDataSourceConfigures(Map<String, DataSourceConfigure> map) {
        if (map == null || map.isEmpty())
            return null;

        Map<String, DataSourceConfigure> configures = new HashMap<>();
        for (Map.Entry<String, DataSourceConfigure> entry : map.entrySet()) {
            // TODO log active connection number for each titan keyname
            String key = entry.getKey();
            connectionStringProcessor.info("--- Key datasource config for " + key + " ---");
            DataSourceConfigure configure = dataSourceConfigureProcessor.getDataSourceConfigure(entry.getValue());
            configures.put(key, configure);
        }

        return configures;
    }

    private void addDataSourceConfigures(Map<String, DataSourceConfigure> map) {
        if (map == null || map.isEmpty())
            return;

        for (Map.Entry<String, DataSourceConfigure> entry : map.entrySet()) {
            dataSourceConfigureLocator.addDataSourceConfigure(entry.getKey(), entry.getValue());
        }
    }

    private void addConnectionStringChangeListeners(Set<String> dbNames) {
        if (dbNames == null || dbNames.isEmpty())
            return;

        for (final String name : dbNames) {
            MapConfig config = connectionStringProcessor.getConfigMap(name);
            if (config == null)
                continue;

            config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
                @Override
                public void onLoad(Map<String, String> map) {
                    notifyConnectionStringChangeListener(name, map);
                }
            });

            logger.info("Added ConnectionStringChangeListener for" + name);
        }
    }

    private void notifyConnectionStringChangeListener(String name, Map<String, String> map) {
        String transactionName = String.format("%s:%s", DAL_NOTIFY_LISTENER, name);
        Transaction transaction = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, transactionName);
        Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, DAL_NOTIFY_LISTENER_START);
        if (map != null) {
            String normalConnectionString = map.get(ConnectionStringProcessor.TITAN_KEY_NORMAL);
            String failoverConnectionString = map.get(ConnectionStringProcessor.TITAN_KEY_FAILOVER);
            transaction.addData(encrypter.desEncrypt(normalConnectionString));
            transaction.addData(encrypter.desEncrypt(failoverConnectionString));
        }

        Set<String> names = new HashSet<>();
        names.add(name);
        try {
            notifyListeners(names);
            Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, DAL_NOTIFY_LISTENER_END);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            DalConfigException exception = new DalConfigException(e);
            transaction.setStatus(exception);
            Cat.logError(exception);
            logger.error(String.format("DalConfigException:%s", e.getMessage()), exception);
        } finally {
            transaction.complete();
        }
    }

    private void addDataSourceConfigureChangeListeners() {
        MapConfig config = dataSourceConfigureProcessor.getConfig();
        if (config == null)
            return;

        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                Transaction t = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DAL_NOTIFY_LISTENER);
                t.addData(DAL_NOTIFY_LISTENER_START);
                try {
                    notifyListeners(null);
                    t.addData(DAL_NOTIFY_LISTENER_END);
                    t.setStatus(Transaction.SUCCESS);
                } catch (Throwable e) {
                    String msg = "DataSourceConfigureProcessor addChangeListener warn:" + e.getMessage();
                    logger.warn(msg, e);
                } finally {
                    t.complete();
                }
            }
        });

        logger.info("Added DataSourceConfigureChangeListener");
    }

    private void notifyListeners(final Set<String> names) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean connectionStringChanged = true;
                    // connection string changed
                    if (names != null && names.size() > 0) {
                        executeNotifyTask(names, connectionStringChanged);
                    } else { // datasource.properties changed
                        Set<String> keySet = dataSourceConfigureLocator.getDataSourceConfigureKeySet();
                        connectionStringChanged &= false;
                        executeNotifyTask(keySet, connectionStringChanged);
                    }
                } catch (Throwable e) {
                    Cat.logError(e);
                }
            }
        });
    }

    private void executeNotifyTask(Set<String> names, boolean isConnectionStringChanged) throws Exception {
        if (names == null || names.isEmpty())
            return;

        Map<String, DataSourceConfigure> configures =
                connectionStringProcessor.getDataSourceConfigureConnectionSettings(names);
        dataSourceConfigureProcessor.refreshDataSourceConfigurePoolSettings();
        configures = mergeDataSourceConfigures(configures);

        Map<String, DataSourceConfigureChangeListener> listeners =
                copyChangeListeners(dataSourceConfigureChangeListeners);

        for (String name : names) {
            String keyName = ConnectionStringKeyNameHelper.getKeyName(name);
            String transactionName = String.format("%s:%s", DAL_REFRESH_DATASOURCE, name);

            Transaction transaction = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, transactionName);
            try {
                // old configure
                DataSourceConfigure oldConfigure = getDataSourceConfigure(name);
                String oldVersion = oldConfigure.getVersion();
                String oldConnectionUrl = oldConfigure.toConnectionUrl();
                // log
                transaction.addData(DATASOURCE_OLD_CONNECTIONURL, String.format("%s:%s", name, oldConnectionUrl));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_OLD_CONNECTIONURL, name, oldConnectionUrl));
                transaction.addData(DATASOURCE_OLD_CONFIGURE,
                        String.format("%s:%s", name, dataSourceConfigureProcessor.mapToString(oldConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_OLD_CONFIGURE, name,
                                dataSourceConfigureProcessor.mapToString(oldConfigure.toMap())));

                // new configure
                DataSourceConfigure newConfigure = configures.get(keyName);
                String newVersion = newConfigure.getVersion();
                String newConnectionUrl = newConfigure.toConnectionUrl();
                // log
                transaction.addData(DATASOURCE_NEW_CONNECTIONURL, String.format("%s:%s", name, newConnectionUrl));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_NEW_CONNECTIONURL, name, newConnectionUrl));
                transaction.addData(DATASOURCE_NEW_CONFIGURE,
                        String.format("%s:%s", name, dataSourceConfigureProcessor.mapToString(newConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_NEW_CONFIGURE, name,
                                dataSourceConfigureProcessor.mapToString(newConfigure.toMap())));

                transaction.setStatus(Transaction.SUCCESS);

                // compare version of connection string
                if (isConnectionStringChanged && oldVersion != null && newVersion != null) {
                    if (oldVersion.equals(newVersion)) {
                        Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS,
                                String.format("New version of %s equals to old version.", name));
                        continue;
                    }
                }

                dataSourceConfigureLocator.addDataSourceConfigure(name, newConfigure);
                DataSourceConfigureChangeListener listener = listeners.get(keyName);
                if (listener == null)
                    continue;

                DataSourceConfigureChangeEvent event =
                        new DataSourceConfigureChangeEvent(name, newConfigure, oldConfigure);
                listener.configChanged(event);
            } catch (Throwable e) {
                transaction.setStatus(e);
                Cat.logError(e);
                throw e;
            } finally {
                transaction.complete();
            }
        }
    }

    @Override
    public void register(String dbName, DataSourceConfigureChangeListener listener) {
        String keyName = ConnectionStringKeyNameHelper.getKeyName(dbName);
        dataSourceConfigureChangeListeners.put(keyName, listener);
    }

    private Map<String, DataSourceConfigureChangeListener> copyChangeListeners(
            Map<String, DataSourceConfigureChangeListener> map) {
        if (map == null || map.isEmpty()) {
            return new ConcurrentHashMap<>();
        }

        Map<String, DataSourceConfigureChangeListener> listeners = new HashMap<>();
        for (Map.Entry<String, DataSourceConfigureChangeListener> entry : map.entrySet()) {
            listeners.put(entry.getKey(), entry.getValue());
        }
        return listeners;
    }

}
