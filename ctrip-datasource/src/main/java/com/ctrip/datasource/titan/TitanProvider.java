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
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureHolder;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;

import com.ctrip.datasource.configure.AllInOneConfigureReader;
import com.ctrip.datasource.configure.DataSourceConfigureProcessor;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.exceptions.DalConfigException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.status.ProductVersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;
import qunar.tc.qconfig.client.TypedConfig;

public class TitanProvider implements DataSourceConfigureProvider {
    private static final Logger logger = LoggerFactory.getLogger(TitanProvider.class);
    private static final String PROD_SUFFIX = "_SH";
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

    /**
     * Used to access local Database.config file fo dev environment
     */
    private AllInOneConfigureReader allInOneProvider = new AllInOneConfigureReader();

    private ConnectionStringProcessor connectionStringProcessor = ConnectionStringProcessor.getInstance();
    private DataSourceConfigureProcessor dataSourceConfigureProcessor = DataSourceConfigureProcessor.getInstance();
    private DataSourceConfigureHolder dataSourceConfigureHolder = DataSourceConfigureHolder.getInstance();
    private DataSourceConfigureParser dataSourceConfigureParser = DataSourceConfigureParser.getInstance();

    // DataSourceConfigure change listener
    private Map<String, DataSourceConfigureChangeListener> dataSourceConfigureChangeListeners =
            new ConcurrentHashMap<>();

    private ThreadPoolExecutor executor =
            new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public void initialize(Map<String, String> settings) throws Exception {
        connectionStringProcessor.initialize(settings);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String dbName) {
        return dataSourceConfigureHolder.getDataSourceConfigure(dbName);
    }

    @Override
    public void setup(Set<String> dbNames) {
        if (dataSourceConfigureParser.isDataSourceXmlExist())
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCE, connectionStringProcessor.getAppId());

        // If it uses local Database.Config
        Map<String, DataSourceConfigure> dataSourceConfigures = null;
        String svcUrl = connectionStringProcessor.getSvcUrl();
        boolean useLocal = connectionStringProcessor.getUseLocal();

        if (svcUrl == null || svcUrl.isEmpty() || useLocal) {
            dataSourceConfigures = allInOneProvider.getDataSourceConfigures(dbNames, useLocal,
                    connectionStringProcessor.getDatabaseConfigLocation());
        } else {
            try {
                connectionStringProcessor.refreshConnectionSettingsMap(dbNames);
                dataSourceConfigures = connectionStringProcessor.getConnectionSettings(dbNames);
            } catch (Exception e) {
                connectionStringProcessor.error("Fail to setup Titan Provider", e);
                throw new RuntimeException(e);
            }
        }

        dataSourceConfigures = mergeDataSourceConfigures(dataSourceConfigures);
        addDataSourceConfigures(dataSourceConfigures);

        addConnectionStringChangeListeners(dbNames);
        addDataSourceConfigureChangeListeners();
        connectionStringProcessor.info("--- End datasource config  ---");
    }

    private void checkMissingPoolConfig(Set<String> dbNames) {
        DataSourceConfigureParser parser = DataSourceConfigureParser.getInstance();

        if (parser.isDataSourceXmlExist())
            ProductVersionManager.getInstance().register(DAL_LOCAL_DATASOURCE, connectionStringProcessor.getAppId());

        for (String name : dbNames) {
            if (dataSourceConfigureHolder.contains(name))
                continue;

            String possibleName = name.endsWith(PROD_SUFFIX) ? name.substring(0, name.length() - PROD_SUFFIX.length())
                    : name + PROD_SUFFIX;

            if (dataSourceConfigureHolder.contains(possibleName)) {
                copyDataSourceConfigure(possibleName, name);
            } else {
                // It is strongly recommended to add datasource config in datasource.xml for each of the
                // connectionString in dal.config
                // Add missing one
                DataSourceConfigure c = new DataSourceConfigure();
                c.setName(name);
                dataSourceConfigureHolder.addDataSourceConfigure(name, c);
            }
        }
    }

    private void copyDataSourceConfigure(String sampleName, String newName) {
        DataSourceConfigure oldConfig = dataSourceConfigureHolder.getDataSourceConfigure(sampleName);

        DataSourceConfigure newConfig = new DataSourceConfigure();
        newConfig.setName(newName);
        newConfig.setProperties(dataSourceConfigureProcessor.deepCopyProperties(oldConfig.getProperties()));
        newConfig.setMap(new HashMap<>(oldConfig.getMap()));
        dataSourceConfigureHolder.addDataSourceConfigure(newName, newConfig);
    }

    private Map<String, DataSourceConfigure> mergeDataSourceConfigures(Map<String, DataSourceConfigure> map) {
        if (map == null || map.isEmpty())
            return null;

        Map<String, DataSourceConfigure> configures = new HashMap<>();
        for (Map.Entry<String, DataSourceConfigure> entry : map.entrySet()) {
            String key = entry.getKey();
            DataSourceConfigure configure = mergeDataSourceConfigure(key, entry.getValue());
            configures.put(key, configure);
        }

        return configures;
    }

    private DataSourceConfigure mergeDataSourceConfigure(String name, DataSourceConfigure configure) {
        // TODO log active connection number for each titan keyname

        connectionStringProcessor.info("--- Key datasource config for " + name + " ---");
        DataSourceConfigure dataSourceConfigure = dataSourceConfigureProcessor.getDataSourceConfigure(configure);
        return dataSourceConfigure;
    }

    private void addDataSourceConfigures(Map<String, DataSourceConfigure> map) {
        if (map == null || map.isEmpty())
            return;

        for (Map.Entry<String, DataSourceConfigure> entry : map.entrySet()) {
            dataSourceConfigureHolder.addDataSourceConfigure(entry.getKey(), entry.getValue());
        }
    }

    private void addConnectionStringChangeListeners(Set<String> dbNames) {
        if (dbNames == null || dbNames.isEmpty())
            return;

        for (final String name : dbNames) {
            TypedConfig<String> config = connectionStringProcessor.getConfigMap(name);
            if (config == null)
                continue;

            config.addListener(new Configuration.ConfigListener<String>() {
                @Override
                public void onLoad(String connectionString) {
                    notifyConnectionStringChangeListener(name);
                }
            });

            final String possibleName = dataSourceConfigureParser.getPossibleName(name);
            // listen on possible name
            TypedConfig<String> possibleConfig = connectionStringProcessor.getTitanTypedConfig(possibleName);
            possibleConfig.addListener(new Configuration.ConfigListener<String>() {
                @Override
                public void onLoad(String connectionString) {
                    // notify with actual name
                    notifyConnectionStringChangeListener(name);
                }
            });
        }

    }

    private void notifyConnectionStringChangeListener(String name) {
        Transaction transaction = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DAL_NOTIFY_LISTENER);
        transaction.addData(DAL_NOTIFY_LISTENER_START);
        Set<String> names = new HashSet<>();
        names.add(name);
        try {
            notifyListeners(names);
            transaction.addData(DAL_NOTIFY_LISTENER_END);
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
    }

    private void notifyListeners(final Set<String> names) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // connection string changed
                    if (names != null && names.size() > 0) {
                        executeNotifyTask(names);
                    } else { // datasource.properties changed
                        Set<String> keySet = dataSourceConfigureHolder.getDataSourceConfigureKeySet();
                        executeNotifyTask(keySet);
                    }
                } catch (Throwable e) {
                    Cat.logError(e);
                }
            }
        });
    }

    private void executeNotifyTask(Set<String> names) throws Exception {
        if (names == null || names.isEmpty())
            return;

        if (dataSourceConfigureChangeListeners == null || dataSourceConfigureChangeListeners.isEmpty())
            return;

        Map<String, DataSourceConfigure> configures = null;
        connectionStringProcessor.refreshConnectionSettingsMap(names);
        configures = connectionStringProcessor.getConnectionSettings(names);
        dataSourceConfigureProcessor.refreshPoolSettingsConfig();
        dataSourceConfigureProcessor.setPoolSettings();
        configures = mergeDataSourceConfigures(configures);
        Map<String, DataSourceConfigureChangeListener> listeners =
                copyChangeListeners(dataSourceConfigureChangeListeners);

        for (String name : names) {
            DataSourceConfigureChangeListener listener = listeners.get(name.toUpperCase());
            if (listener == null)
                continue;

            Transaction transaction = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE);
            try {
                // old configure
                DataSourceConfigure oldConfigure = getDataSourceConfigure(name);
                String oldVersion = oldConfigure.getVersion();
                String oldConnectionUrl = oldConfigure.toConnectionUrl();
                // log
                transaction.addData(DATASOURCE_OLD_CONNECTIONURL, String.format("%s:%s", name, oldConnectionUrl));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_OLD_CONNECTIONURL, name, oldConnectionUrl));
                transaction.addData(DATASOURCE_OLD_CONFIGURE,
                        String.format("%s:%s", name, dataSourceConfigureProcessor.mapToString(oldConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_OLD_CONFIGURE, name,
                                dataSourceConfigureProcessor.mapToString(oldConfigure.toMap())));

                // new configure
                DataSourceConfigure newConfigure = configures.get(name.toUpperCase());
                String newVersion = newConfigure.getVersion();
                String newConnectionUrl = newConfigure.toConnectionUrl();
                // log
                transaction.addData(DATASOURCE_NEW_CONNECTIONURL, String.format("%s:%s", name, newConnectionUrl));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_NEW_CONNECTIONURL, name, newConnectionUrl));
                transaction.addData(DATASOURCE_NEW_CONFIGURE,
                        String.format("%s:%s", name, dataSourceConfigureProcessor.mapToString(newConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DAL_REFRESH_DATASOURCE, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_NEW_CONFIGURE, name,
                                dataSourceConfigureProcessor.mapToString(newConfigure.toMap())));

                // compare version
                if (oldVersion != null && newVersion != null) {
                    if (oldVersion.equals(newVersion)) {
                        continue;
                    }
                }

                DataSourceConfigureChangeEvent event =
                        new DataSourceConfigureChangeEvent(name, newConfigure, oldConfigure);
                listener.configChanged(event);
                dataSourceConfigureHolder.addDataSourceConfigure(name, newConfigure);
                transaction.setStatus(Transaction.SUCCESS);
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
        addChangeListener(dbName, listener);
    }

    private void addChangeListener(String dbName, DataSourceConfigureChangeListener listener) {
        dataSourceConfigureChangeListeners.put(dbName.toUpperCase(), listener);
    }

    private Map<String, DataSourceConfigureChangeListener> copyChangeListeners(
            Map<String, DataSourceConfigureChangeListener> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, DataSourceConfigureChangeListener> listeners = new HashMap<>();
        for (Map.Entry<String, DataSourceConfigureChangeListener> entry : map.entrySet()) {
            listeners.put(entry.getKey(), entry.getValue());
        }
        return listeners;
    }

}
