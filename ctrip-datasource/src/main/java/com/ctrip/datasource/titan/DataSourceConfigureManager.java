package com.ctrip.datasource.titan;

import com.ctrip.datasource.configure.ConnectionStringProviderImpl;
import com.ctrip.datasource.configure.PoolPropertiesProviderImpl;
import com.ctrip.datasource.util.DalEncrypter;
import com.ctrip.platform.dal.common.enums.SourceType;
import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import com.ctrip.platform.dal.exceptions.DalConfigException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DataSourceConfigureManager {
    private volatile static DataSourceConfigureManager manager = null;

    public synchronized static DataSourceConfigureManager getInstance() {
        if (manager == null) {
            manager = new DataSourceConfigureManager();
        }
        return manager;
    }

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfigureManager.class);

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
    private ConnectionStringProvider connectionStringProvider = ConnectionStringProviderImpl.getInstance();
    private PoolPropertiesProvider poolPropertiesProvider = PoolPropertiesProviderImpl.getInstance();
    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();

    private DalEncrypter dalEncrypter = null;
    private volatile boolean isInitialized = false;

    private Map<String, DataSourceConfigureChangeListener> dataSourceConfigureChangeListeners =
            new ConcurrentHashMap<>();
    private Map<String, SourceType> keyNameMap = new ConcurrentHashMap<>();
    private Set<String> listenerKeyNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private AtomicReference<Boolean> isPoolPropertiesListenerAdded = new AtomicReference<>(false);

    // Single-thread thread pool,used as queue.
    private ThreadPoolExecutor executor =
            new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public synchronized void initialize(Map<String, String> settings) throws Exception {
        if (isInitialized)
            return;

        connectionStringProvider.initialize(settings);
        isInitialized = true;
    }

    public DataSourceConfigure getDataSourceConfigure(String name) {
        return dataSourceConfigureLocator.getDataSourceConfigure(name);
    }

    public synchronized void setup(Set<String> dbNames, SourceType sourceType) {
        Set<String> names = null;
        try {
            names = getFilteredNames(dbNames, sourceType);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        if (names == null || names.isEmpty())
            return;

        dataSourceConfigureLocator.addDataSourceConfigureKeySet(names);
        Map<String, DataSourceConfigure> configures =
                connectionStringProvider.initializeConnectionStrings(names, sourceType);
        configures = mergeDataSourceConfigures(configures);
        addDataSourceConfigures(configures);

        if (sourceType == SourceType.Remote) {
            addConnectionStringChangedListeners(names);

            boolean isAdded = isPoolPropertiesListenerAdded.get().booleanValue();
            if (!isAdded) {
                addPoolPropertiesChangedListeners();
                isPoolPropertiesListenerAdded.compareAndSet(false, true);
            }
        }
    }

    private Set<String> getFilteredNames(Set<String> names, SourceType sourceType) throws Exception {
        Set<String> set = new HashSet<>();
        if (names == null || names.isEmpty())
            return set;

        for (String name : names) {
            String keyName = ConnectionStringKeyHelper.getKeyName(name);
            if (keyNameMap.containsKey(keyName)) {
                SourceType st = keyNameMap.get(keyName);
                if (st != sourceType) {
                    String msg =
                            String.format("Key %s is used in both local and remote mode which is prohibited.", keyName);
                    Exception e = new RuntimeException(msg);
                    Cat.logError(e);
                    logger.error(msg, e);
                    throw e;
                }
                continue;
            }

            keyNameMap.put(keyName, sourceType);
            set.add(keyName);
        }

        return set;
    }

    private Map<String, DataSourceConfigure> mergeDataSourceConfigures(Map<String, DataSourceConfigure> map) {
        if (map == null || map.isEmpty())
            return null;

        Map<String, DataSourceConfigure> configures = new HashMap<>();
        for (Map.Entry<String, DataSourceConfigure> entry : map.entrySet()) {
            String key = entry.getKey();
            DataSourceConfigure configure = poolPropertiesProvider.mergeDataSourceConfigure(entry.getValue());
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

    private void addConnectionStringChangedListeners(Set<String> names) {
        if (names == null || names.isEmpty())
            return;

        for (final String name : names) {
            // double check to avoid adding listener multiple times
            if (listenerKeyNames.contains(name))
                continue;

            connectionStringProvider.addConnectionStringChangedListener(name, new ConnectionStringChanged() {
                @Override
                public void onChanged(Map<String, String> map) {
                    addConnectionStringNotifyTask(name, map);
                }
            });

            listenerKeyNames.add(name);
        }
    }

    private void addConnectionStringNotifyTask(String name, Map<String, String> map) {
        String transactionName = String.format("%s:%s", DAL_NOTIFY_LISTENER, name);
        Transaction t = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, transactionName);
        Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, DAL_NOTIFY_LISTENER_START);

        String normalConnectionString = map.get(DataSourceConfigureConstants.TITAN_KEY_NORMAL);
        String failoverConnectionString = map.get(DataSourceConfigureConstants.TITAN_KEY_FAILOVER);
        DalEncrypter encrypter = getEncrypter();
        t.addData(encrypter.desEncrypt(normalConnectionString));
        t.addData(encrypter.desEncrypt(failoverConnectionString));

        DataSourceConfigure configure = connectionStringProvider.parseConnectionString(name, normalConnectionString);
        Map<String, DataSourceConfigure> configures = new HashMap<>();
        configures.put(name, configure);
        configures = mergeDataSourceConfigures(configures);

        Set<String> names = new HashSet<>();
        names.add(name);

        try {
            addNotifyTask(names, configures);
            Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, DAL_NOTIFY_LISTENER_END);
            t.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            DalConfigException exception = new DalConfigException(e);
            t.setStatus(exception);
            Cat.logError(exception);
            logger.error(String.format("DalConfigException:%s", e.getMessage()), exception);
        } finally {
            t.complete();
        }
    }

    private void addPoolPropertiesChangedListeners() {
        poolPropertiesProvider.addPoolPropertiesChangedListener(new PoolPropertiesChanged() {
            @Override
            public void onChanged(Map<String, String> map) {
                addPoolPropertiesNotifyTask(map);
            }
        });
    }

    private void addPoolPropertiesNotifyTask(Map<String, String> map) {
        Transaction t = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DAL_NOTIFY_LISTENER);
        t.addData(DAL_NOTIFY_LISTENER_START);

        try {
            poolPropertiesProvider.setPoolProperties(map);
            Set<String> names = dataSourceConfigureLocator.getDataSourceConfigureKeySet();
            Map<String, DataSourceConfigure> configures = new HashMap<>();
            for (String name : names) {
                DataSourceConfigure configure = getDataSourceConfigure(name);
                configure = connectionStringProvider.getConnectionStringProperties(configure);
                configures.put(name, configure);
            }

            configures = mergeDataSourceConfigures(configures);
            addNotifyTask(names, configures);
            t.addData(DAL_NOTIFY_LISTENER_END);
            t.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            String msg = "DataSourceConfigureProcessor addChangeListener warn:" + e.getMessage();
            logger.warn(msg, e);
        } finally {
            t.complete();
        }
    }

    private void addNotifyTask(final Set<String> names, final Map<String, DataSourceConfigure> configures) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    executeNotifyTask(names, configures);
                } catch (Throwable e) {
                    Cat.logError(e);
                }
            }
        });
    }

    private void executeNotifyTask(Set<String> names, Map<String, DataSourceConfigure> configures) throws Exception {
        if (names == null || names.isEmpty())
            return;

        if (configures == null || configures.isEmpty())
            return;

        Map<String, DataSourceConfigureChangeListener> listeners =
                copyChangeListeners(dataSourceConfigureChangeListeners);

        for (String name : names) {
            String keyName = ConnectionStringKeyHelper.getKeyName(name);
            String transactionName = String.format("%s:%s", DAL_REFRESH_DATASOURCE, name);
            Transaction transaction = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, transactionName);

            try {
                // old configure
                DataSourceConfigure oldConfigure = getDataSourceConfigure(name);
                String oldConnectionUrl = oldConfigure.toConnectionUrl();

                // log
                transaction.addData(DATASOURCE_OLD_CONNECTIONURL, String.format("%s:%s", name, oldConnectionUrl));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_OLD_CONNECTIONURL, name, oldConnectionUrl));
                transaction.addData(DATASOURCE_OLD_CONFIGURE,
                        String.format("%s:%s", name, poolPropertiesHelper.mapToString(oldConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, String.format("%s:%s:%s",
                        DATASOURCE_OLD_CONFIGURE, name, poolPropertiesHelper.mapToString(oldConfigure.toMap())));

                // new configure
                DataSourceConfigure newConfigure = configures.get(keyName);
                String newConnectionUrl = newConfigure.toConnectionUrl();

                // log
                transaction.addData(DATASOURCE_NEW_CONNECTIONURL, String.format("%s:%s", name, newConnectionUrl));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCE_NEW_CONNECTIONURL, name, newConnectionUrl));
                transaction.addData(DATASOURCE_NEW_CONFIGURE,
                        String.format("%s:%s", name, poolPropertiesHelper.mapToString(newConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, String.format("%s:%s:%s",
                        DATASOURCE_NEW_CONFIGURE, name, poolPropertiesHelper.mapToString(newConfigure.toMap())));

                transaction.setStatus(Transaction.SUCCESS);

                dataSourceConfigureLocator.addDataSourceConfigure(name, newConfigure);

                DataSourceConfigureChangeListener listener = listeners.get(keyName);
                if (listener == null) {
                    boolean containsKey = DataSourceLocator.containsKey(keyName);
                    if (!containsKey)
                        continue;

                    String msg = String.format("Listener of %s is null.", keyName);
                    Exception exception = new RuntimeException(msg);
                    Cat.logError(exception);
                    logger.error(msg, exception);
                    throw exception;
                }

                DataSourceConfigureChangeEvent event =
                        new DataSourceConfigureChangeEvent(name, newConfigure, oldConfigure);
                listener.configChanged(event);
            } catch (Throwable e) {
                transaction.setStatus(e);
                Cat.logError(e);
                logger.error(e.getMessage(), e);
                throw e;
            } finally {
                transaction.complete();
            }
        }
    }

    public void register(String name, DataSourceConfigureChangeListener listener) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        dataSourceConfigureChangeListeners.put(keyName, listener);
        logger.debug("DAL debug:(register)add listener for {}", name);
    }

    private Map<String, DataSourceConfigureChangeListener> copyChangeListeners(
            Map<String, DataSourceConfigureChangeListener> map) {
        if (map == null || map.isEmpty())
            return new ConcurrentHashMap<>();

        return new ConcurrentHashMap<>(map);
    }

    private synchronized DalEncrypter getEncrypter() {
        if (dalEncrypter == null) {
            try {
                dalEncrypter = new DalEncrypter(LoggerAdapter.DEFAULT_SECERET_KEY);
            } catch (Throwable e) {
                logger.warn("DalEncrypter initialization failed.");
            }
        }

        return dalEncrypter;
    }

    // for unit test only
    public void clear() {
        isInitialized = false;
        dataSourceConfigureChangeListeners = new ConcurrentHashMap<>();
        keyNameMap = new ConcurrentHashMap<>();
        listenerKeyNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        connectionStringProvider.clear();
    }

}
