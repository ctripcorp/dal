package com.ctrip.datasource.titan;

import com.ctrip.datasource.configure.AllInOneConfigureReader;
import com.ctrip.datasource.configure.qconfig.ConnectionStringProviderImpl;
import com.ctrip.datasource.configure.qconfig.IPDomainStatusProviderImpl;
import com.ctrip.datasource.configure.qconfig.PoolPropertiesProviderImpl;
import com.ctrip.datasource.util.DalEncrypter;
import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.datasource.common.enums.SourceType;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import com.ctrip.datasource.datasource.IPDomainStatusChanged;
import com.ctrip.datasource.datasource.IPDomainStatusProvider;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.exceptions.DalConfigException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DataSourceConfigureManager extends DataSourceConfigureHelper {
    private volatile static DataSourceConfigureManager manager = null;

    public synchronized static DataSourceConfigureManager getInstance() {
        if (manager == null) {
            manager = new DataSourceConfigureManager();
        }
        return manager;
    }

    private static final String DAL_DYNAMIC_DATASOURCE = "DAL";
    private static final String DATASOURCE_NOTIFY_LISTENER_START = "DataSource.notifyListener.start";
    private static final String DATASOURCE_NOTIFY_LISTENER_END = "DataSource.notifyListener.end";
    private static final String DATASOURCE_CONNECTIONSTRING_CHANGED = "ConnectionString::connectionStringChanged";
    private static final String DATASOURCE_POOLPROPERTIES_CHANGED = "PoolProperties::poolPropertiesChanged";
    private static final String DATASOURCE_IPDOMAINSTATUS_CHANGED = "IPDomainStatus::ipDomainStatusChanged";
    private static final String DATASOURCE_REFRESH_DATASOURCECONFIG = "DataSourceConfig::refreshDataSourceConfig";
    private static final String DATASOURCE_OLD_CONFIGURE = "DataSourceConfig::oldConfigure";
    private static final String DATASOURCE_NEW_CONFIGURE = "DataSourceConfig::newConfigure";
    private static final String CONNECTIONSTRING_OLD_CONNECTIONURL = "ConnectionString::oldConnectionUrl";
    private static final String CONNECTIONSTRING_NEW_CONNECTIONURL = "ConnectionString::newConnectionUrl";

    private static final String THREAD_NAME = "DataSourceConfigureManager";

    /**
     * Used to access local Database.config file fo dev environment
     */
    private AllInOneConfigureReader allInOneProvider = new AllInOneConfigureReader();

    private ConnectionStringProvider connectionStringProvider = new ConnectionStringProviderImpl();
    private PoolPropertiesProvider poolPropertiesProvider = new PoolPropertiesProviderImpl();
    private IPDomainStatusProvider ipDomainStatusProvider = new IPDomainStatusProviderImpl();

    private AtomicReference<Boolean> isPoolPropertiesListenerAdded = new AtomicReference<>(false);
    private AtomicReference<Boolean> isIPDomainStatusListenerAdded = new AtomicReference<>(false);

    private volatile boolean isInitialized = false;
    private Map<String, DataSourceConfigureChangeListener> dataSourceConfigureChangeListeners =
            new ConcurrentHashMap<>();
    private Map<String, SourceType> keyNameMap = new ConcurrentHashMap<>();
    private Set<String> listenerKeyNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    // Single-thread thread pool,used as queue.
    private ExecutorService executor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory(THREAD_NAME));

    public synchronized void initialize(Map<String, String> settings) throws Exception {
        if (isInitialized)
            return;

        _initialize(settings);
        isInitialized = true;
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

        Map<String, DataSourceConfigure> configures = getConnectionStrings(names, sourceType);
        Map<String, String> poolProperties = poolPropertiesProvider.getPoolProperties();
        setPoolProperties(poolProperties);
        configures = mergeDataSourceConfigures(configures);
        addDataSourceConfigures(configures);

        if (sourceType == SourceType.Remote) {
            addDataSourceConfigureKeySet(names);
            addConnectionStringChangedListeners(names);

            boolean isPoolListenerAdded = isPoolPropertiesListenerAdded.get().booleanValue();
            if (!isPoolListenerAdded) {
                addPoolPropertiesChangedListener();
                isPoolPropertiesListenerAdded.compareAndSet(false, true);
            }

            boolean isStatusListenerAdded = isIPDomainStatusListenerAdded.get().booleanValue();
            if (!isStatusListenerAdded) {
                addIPDomainStatusChangedListener();
                isIPDomainStatusListenerAdded.compareAndSet(false, true);
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
                    LOGGER.error(msg, e);
                    throw e;
                }
                continue;
            }

            keyNameMap.put(keyName, sourceType);
            set.add(keyName);
        }

        return set;
    }

    private Map<String, DataSourceConfigure> getConnectionStrings(Set<String> dbNames, SourceType sourceType) {
        Map<String, DataSourceConfigure> dataSourceConfigures = null;
        if (isDebug) {
            dataSourceConfigures = new HashMap<>();
            if (dbNames == null || dbNames.isEmpty())
                return dataSourceConfigures;
            for (String name : dbNames) {
                String keyName = ConnectionStringKeyHelper.getKeyName(name);
                dataSourceConfigures.put(keyName, new DataSourceConfigure());
            }

            return dataSourceConfigures;
        }

        // If it uses local Database.Config
        if (sourceType == SourceType.Local) {
            boolean useLocal = getUseLocal();
            dataSourceConfigures =
                    allInOneProvider.getDataSourceConfigures(dbNames, useLocal, getDatabaseConfigLocation());
        } else {
            try {
                dataSourceConfigures = connectionStringProvider.getConnectionStrings(dbNames);
            } catch (Exception e) {
                error("Fail to setup Titan Provider", e);
                throw new RuntimeException(e);
            }
        }

        return dataSourceConfigures;
    }

    public DataSourceConfigure mergeDataSourceConfig(DataSourceConfigure configure) {
        return mergeDataSourceConfigure(configure);
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
                            String transactionName = String.format("%s:%s", DATASOURCE_CONNECTIONSTRING_CHANGED, name);
                            Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, msg);
                            LOGGER.info(msg);
                            return;
                        }
                    }

                    addConnectionStringNotifyTask(name, map);
                }
            });

            listenerKeyNames.add(name);
        }
    }

    private void addConnectionStringNotifyTask(String name, Map<String, String> map) {
        String transactionName = String.format("%s:%s", DATASOURCE_CONNECTIONSTRING_CHANGED, name);
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        Transaction t = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, transactionName);
        Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, DATASOURCE_NOTIFY_LISTENER_START);

        String normalConnectionString = map.get(DataSourceConfigureConstants.TITAN_KEY_NORMAL);
        String failoverConnectionString = map.get(DataSourceConfigureConstants.TITAN_KEY_FAILOVER);
        DalEncrypter encrypter = getEncrypter();
        t.addData(encrypter.desEncrypt(normalConnectionString));
        t.addData(encrypter.desEncrypt(failoverConnectionString));

        DataSourceConfigure oldConfigure = getDataSourceConfigure(keyName);
        DataSourceConfigure newConfigure =
                getDataSourceConfigure(keyName, normalConnectionString, failoverConnectionString);
        DataSourceConfigureChangeEvent event = new DataSourceConfigureChangeEvent(keyName, newConfigure, oldConfigure);

        Map<String, DataSourceConfigureChangeEvent> events = new HashMap<>();
        events.put(keyName, event);

        Set<String> names = new HashSet<>();
        names.add(keyName);

        try {
            addNotifyTask(names, events);
            Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, DATASOURCE_NOTIFY_LISTENER_END);
            t.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            DalConfigException exception = new DalConfigException(e);
            t.setStatus(exception);
            Cat.logError(exception);
            LOGGER.error(String.format("DalConfigException:%s", e.getMessage()), exception);
        } finally {
            t.complete();
        }
    }

    private void addPoolPropertiesChangedListener() {
        poolPropertiesProvider.addPoolPropertiesChangedListener(new PoolPropertiesChanged() {
            @Override
            public void onChanged(Map<String, String> map) {
                addPoolPropertiesNotifyTask(map);
            }
        });
    }

    private void addPoolPropertiesNotifyTask(Map<String, String> map) {
        Transaction t = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DATASOURCE_POOLPROPERTIES_CHANGED);
        t.addData(DATASOURCE_NOTIFY_LISTENER_START);
        Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DATASOURCE_POOLPROPERTIES_CHANGED, Message.SUCCESS,
                DATASOURCE_NOTIFY_LISTENER_START);

        try {
            setPoolProperties(map);
            Set<String> names = getDataSourceConfigureKeySet();
            Map<String, DataSourceConfigureChangeEvent> events = new HashMap<>();
            for (String name : names) {
                String keyName = ConnectionStringKeyHelper.getKeyName(name);
                DataSourceConfigure oldConfigure = getDataSourceConfigure(keyName);
                DataSourceConfigure newConfigure = getConnectionStringProperties(oldConfigure);
                newConfigure = mergeDataSourceConfigure(newConfigure);
                DataSourceConfigureChangeEvent event =
                        new DataSourceConfigureChangeEvent(keyName, newConfigure, oldConfigure);
                events.put(keyName, event);
            }

            addNotifyTask(names, events);
            Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DATASOURCE_POOLPROPERTIES_CHANGED, Message.SUCCESS,
                    DATASOURCE_NOTIFY_LISTENER_END);
            t.addData(DATASOURCE_NOTIFY_LISTENER_END);
            t.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            String msg = "DataSourceConfigureManager addPoolPropertiesChangedListener warn:" + e.getMessage();
            LOGGER.warn(msg, e);
        } finally {
            t.complete();
        }
    }

    private void addIPDomainStatusChangedListener() {
        ipDomainStatusProvider.addIPDomainStatusChangedListener(new IPDomainStatusChanged() {
            @Override
            public void onChanged(IPDomainStatus status) {
                IPDomainStatus currentStatus = getIPDomainStatus();
                if (currentStatus.equals(status)) {
                    String msg = String.format("New status equals to current status:%s", status.toString());
                    Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DATASOURCE_IPDOMAINSTATUS_CHANGED, Message.SUCCESS, msg);
                    LOGGER.info(msg);
                    return;
                }

                setIPDomainStatus(status);
                addIPDomainStatusNotifyTask(status);
            }
        });
    }

    private void addIPDomainStatusNotifyTask(IPDomainStatus status) {
        Transaction t = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, DATASOURCE_IPDOMAINSTATUS_CHANGED);
        String switchStatus = String.format("Switch status:%s", status.toString());
        t.addData(switchStatus);
        t.addData(DATASOURCE_NOTIFY_LISTENER_START);
        Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DATASOURCE_IPDOMAINSTATUS_CHANGED, Message.SUCCESS, switchStatus);
        Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DATASOURCE_IPDOMAINSTATUS_CHANGED, Message.SUCCESS,
                DATASOURCE_NOTIFY_LISTENER_START);

        try {
            Set<String> names = getDataSourceConfigureKeySet();
            Map<String, DataSourceConfigureChangeEvent> events = new HashMap<>();
            for (String name : names) {
                DataSourceConfigure oldConfigure = getDataSourceConfigure(name);
                DataSourceConfigure newConfigure = getDataSourceConfigure(name, status);
                DataSourceConfigureChangeEvent event =
                        new DataSourceConfigureChangeEvent(name, newConfigure, oldConfigure);
                events.put(name, event);
            }

            addNotifyTask(names, events);
            Cat.logEvent(DAL_DYNAMIC_DATASOURCE, DATASOURCE_IPDOMAINSTATUS_CHANGED, Message.SUCCESS,
                    DATASOURCE_NOTIFY_LISTENER_END);
            t.addData(DATASOURCE_NOTIFY_LISTENER_END);
            t.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            DalConfigException exception = new DalConfigException(e);
            t.setStatus(exception);
            Cat.logError(exception);
            LOGGER.error(String.format("DalConfigException:%s", e.getMessage()), exception);
        } finally {
            t.complete();
        }
    }

    private void addNotifyTask(final Set<String> names, final Map<String, DataSourceConfigureChangeEvent> events) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    executeNotifyTask(names, events);
                } catch (Throwable e) {
                    Cat.logError(e);
                }
            }
        });
    }

    private void executeNotifyTask(Set<String> names, Map<String, DataSourceConfigureChangeEvent> events)
            throws Exception {
        if (names == null || names.isEmpty())
            return;

        if (events == null || events.isEmpty())
            return;

        Map<String, DataSourceConfigureChangeListener> listeners =
                copyChangeListeners(dataSourceConfigureChangeListeners);

        for (String name : names) {
            String keyName = ConnectionStringKeyHelper.getKeyName(name);
            String transactionName = String.format("%s:%s", DATASOURCE_REFRESH_DATASOURCECONFIG, name);
            Transaction transaction = Cat.newTransaction(DAL_DYNAMIC_DATASOURCE, transactionName);

            DataSourceConfigureChangeEvent event = events.get(keyName);
            if (event == null)
                continue;

            try {
                // old configure
                DataSourceConfigure oldConfigure = event.getOldDataSourceConfigure();
                String oldConnectionUrl = oldConfigure.toConnectionUrl();

                // log
                transaction.addData(CONNECTIONSTRING_OLD_CONNECTIONURL, String.format("%s:%s", name, oldConnectionUrl));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", CONNECTIONSTRING_OLD_CONNECTIONURL, name, oldConnectionUrl));
                transaction.addData(DATASOURCE_OLD_CONFIGURE,
                        String.format("%s:%s", name, poolPropertiesHelper.mapToString(oldConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, String.format("%s:%s:%s",
                        DATASOURCE_OLD_CONFIGURE, name, poolPropertiesHelper.mapToString(oldConfigure.toMap())));

                // new configure
                DataSourceConfigure newConfigure = event.getNewDataSourceConfigure();
                String newConnectionUrl = newConfigure.toConnectionUrl();

                // log
                transaction.addData(CONNECTIONSTRING_NEW_CONNECTIONURL, String.format("%s:%s", name, newConnectionUrl));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", CONNECTIONSTRING_NEW_CONNECTIONURL, name, newConnectionUrl));
                transaction.addData(DATASOURCE_NEW_CONFIGURE,
                        String.format("%s:%s", name, poolPropertiesHelper.mapToString(newConfigure.toMap())));
                Cat.logEvent(DAL_DYNAMIC_DATASOURCE, transactionName, Message.SUCCESS, String.format("%s:%s:%s",
                        DATASOURCE_NEW_CONFIGURE, name, poolPropertiesHelper.mapToString(newConfigure.toMap())));

                transaction.setStatus(Transaction.SUCCESS);

                // refresh configure
                addDataSourceConfigure(name, newConfigure);

                DataSourceConfigureChangeListener listener = listeners.get(keyName);
                if (listener == null) {
                    boolean containsKey = DataSourceLocator.containsKey(keyName);
                    if (!containsKey)
                        continue;

                    String msg = String.format("Listener of %s is null.", keyName);
                    Exception exception = new RuntimeException(msg);
                    Cat.logError(exception);
                    LOGGER.error(msg, exception);
                    throw exception;
                }

                listener.configChanged(event);
            } catch (Throwable e) {
                transaction.setStatus(e);
                Cat.logError(e);
                LOGGER.error(e.getMessage(), e);
                throw e;
            } finally {
                transaction.complete();
            }
        }
    }

    public void register(String name, DataSourceConfigureChangeListener listener) {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        dataSourceConfigureChangeListeners.put(keyName, listener);
    }

    private Map<String, DataSourceConfigureChangeListener> copyChangeListeners(
            Map<String, DataSourceConfigureChangeListener> map) {
        if (map == null || map.isEmpty())
            return new ConcurrentHashMap<>();

        return new ConcurrentHashMap<>(map);
    }

    public void setConnectionStringProvider(ConnectionStringProvider provider) {
        this.connectionStringProvider = provider;
    }

    public void setPoolPropertiesProvider(PoolPropertiesProvider provider) {
        this.poolPropertiesProvider = provider;
    }

    public void setIPDomainStatusProvider(IPDomainStatusProvider provider) {
        this.ipDomainStatusProvider = provider;
    }

    // for unit test only
    public void clear() {
        isInitialized = false;
        dataSourceConfigureChangeListeners = new ConcurrentHashMap<>();
        keyNameMap = new ConcurrentHashMap<>();
        listenerKeyNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

        isDebug = false;
        appId = null;
        useLocal = false;
        databaseConfigLocation = null;
        config = null;
        startUpLog.clear();
    }

}
