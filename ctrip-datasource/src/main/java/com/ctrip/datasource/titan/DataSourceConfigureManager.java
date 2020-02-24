package com.ctrip.datasource.titan;

import com.ctrip.datasource.configure.AllInOneConfigureReader;
import com.ctrip.datasource.configure.CtripVariableDataSourceConfigureProvider;
import com.ctrip.datasource.configure.qconfig.ConnectionStringProviderImpl;
import com.ctrip.datasource.configure.qconfig.IPDomainStatusProviderImpl;
import com.ctrip.datasource.configure.qconfig.PoolPropertiesProviderImpl;
import com.ctrip.datasource.util.DalEncrypter;
import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.datasource.common.enums.SourceType;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.*;
import com.ctrip.datasource.datasource.IPDomainStatusChanged;
import com.ctrip.datasource.datasource.IPDomainStatusProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.exceptions.DalConfigException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class DataSourceConfigureManager extends DataSourceConfigureHelper {
    private static final ILog clog = LogManager.getLogger(DataSourceConfigureManager.class);

    private volatile static DataSourceConfigureManager manager = null;

    public synchronized static DataSourceConfigureManager getInstance() {
        if (manager == null) {
            manager = new DataSourceConfigureManager();
        }
        return manager;
    }

    private static final String DATASOURCE_NOTIFY_LISTENER_START = "DataSource.notifyListener.start";
    private static final String DATASOURCE_NOTIFY_LISTENER_END = "DataSource.notifyListener.end";

    // Connection string
    private static final String CONNECTIONSTRING_REFRESHCONNECTIONSTRING = "ConnectionString::refreshConnectionString";
    private static final String OLD_NORMAL_CONNECTIONURL = "Old normal connection url";
    private static final String OLD_FAILOVER_CONNECTIONURL = "Old failover connection url";
    private static final String NEW_NORMAL_CONNECTIONURL = "New normal connection url";
    private static final String NEW_FAILOVER_CONNECTIONURL = "New failover connection url";
    private static final String ENCRYPTED_NEW_NORMAL_CONNECTIONSTRING = "Encrypted new normal connectionString";
    private static final String ENCRYPTED_NEW_FAILOVER_CONNECTIONSTRING = "Encrypted new failover connectionString";

    private static final String POOLPROPERTIES_REFRESH_POOLPROPERTIES = "PoolProperties::refreshPoolProperties";
    private static final String IPDOMAINSTATUS_REFRESH_IPDOMAINSTATUS = "IPDomainStatus::refreshIPDomainStatus";

    private static final String SET_PROPERTIES = "Set PoolProperties";
    private static final String SET_IP_DOMAIN_STATUS = "Set IPDomainStatus";

    // DataSourceConfigure
    private static final String DATASOURCECONFIGURE_REFRESH_DATASOURCECONFIG =
            "DataSourceConfig::refreshDataSourceConfig";
    private static final String CONNECTIONSTRING_OLD_CONNECTIONURL = "Old connection url";
    private static final String CONNECTIONSTRING_NEW_CONNECTIONURL = "New connection url";
    private static final String DATASOURCECONFIGURE_OLD_CONFIGURE = "Old DataSourceConfig";
    private static final String DATASOURCECONFIGURE_NEW_CONFIGURE = "New DataSourceConfig";

    private static final String DATASOURCE_CONFIGURE_CHANGE_LISTENER_IS_NULL =
            "DataSourceConfigureChangeListenerIsNull:%s";
    private static final String DATASOURCE_CONFIGURE_CHANGE_LISTENER_IS_NULL_MESSAGE =
            "DataSourceConfigureChangeListener of %s is null.";

    private static final String THREAD_NAME = "DataSourceConfigureManager";

    /**
     * Used to access local Database.config file fo dev environment
     */
    private AllInOneConfigureReader allInOneProvider = new AllInOneConfigureReader();

    private AbstractVariableDataSourceConfigureProvider variableConnectionStringProvider = new CtripVariableDataSourceConfigureProvider();
    private ConnectionStringProvider connectionStringProvider = new ConnectionStringProviderImpl();
    private PoolPropertiesProvider poolPropertiesProvider = new PoolPropertiesProviderImpl();
    private IPDomainStatusProvider ipDomainStatusProvider = new IPDomainStatusProviderImpl();

    private AtomicReference<Boolean> isPoolPropertiesListenerAdded = new AtomicReference<>(false);
    private AtomicReference<Boolean> isIPDomainStatusListenerAdded = new AtomicReference<>(false);

    private DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();

    private volatile boolean isInitialized = false;
    private Map<DataSourceIdentity, DataSourceConfigureChangeListener> dataSourceConfigureChangeListeners =
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

    private void setupPoolProperties() {
        try {
            // set pool properties
            DalPoolPropertiesConfigure poolProperties = poolPropertiesProvider.getPoolProperties();
            dataSourceConfigureLocator.setPoolProperties(poolProperties);
        } catch (Throwable e) {
            if (getIgnoreExternalException()) {
                Cat.logError("fail to get pool properties from qconfig. ", e);
            }
            else {
                throw e;
            }
        }

        boolean isPoolListenerAdded = isPoolPropertiesListenerAdded.get();
        if (!isPoolListenerAdded) {
            addPoolPropertiesChangedListener();
            isPoolPropertiesListenerAdded.compareAndSet(false, true);
        }

        boolean isStatusListenerAdded = isIPDomainStatusListenerAdded.get();
        if (!isStatusListenerAdded) {
            addIPDomainStatusChangedListener();
            isIPDomainStatusListenerAdded.compareAndSet(false, true);
        }
    }

    public synchronized void setup(Set<String> dbNames, SourceType sourceType) {
        for (String dbName : dbNames)
            keyNameMap.remove(dbName);

        Set<String> names;
        try {
            names = getFilteredNames(dbNames, sourceType);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        try {
            // set ip domain status
            IPDomainStatus status = ipDomainStatusProvider.getStatus();
            dataSourceConfigureLocator.setIPDomainStatus(status);
        } catch (Throwable e) {
            if (getIgnoreExternalException()) {
                Cat.logError("fail to get IPDomainStatus from qconfig. ", e);
            }
            else {
                throw e;
            }
        }

        setupPoolProperties();

        if (names.isEmpty())
            return;

        // set connection strings
        Map<String, DalConnectionString> connectionStrings = getConnectionStrings(names, sourceType);
        dataSourceConfigureLocator.setConnectionStrings(connectionStrings);

        if (sourceType == SourceType.Remote)
            addConnectionStringChangedListeners(names);
    }

    public synchronized void setup(Set<String> dbNames) {
        setupPoolProperties();

        Map<String, DalConnectionStringConfigure> connectionStringConfigs = getConnectionStringConfigures(dbNames);
        dataSourceConfigureLocator.setVariableConnectionStringConfigs(connectionStringConfigs);
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

    private Map<String, DalConnectionString> getConnectionStrings(Set<String> names, SourceType sourceType) {
        Map<String, DalConnectionString> connectionStrings = null;
        if (isDebug) {
            connectionStrings = new HashMap<>();
            if (names == null || names.isEmpty())
                return connectionStrings;

            for (String name : names) {
                String keyName = ConnectionStringKeyHelper.getKeyName(name);
                connectionStrings.put(keyName, new ConnectionString(keyName, null, null));
            }

            return connectionStrings;
        }

        // If it uses local Database.Config
        if (sourceType == SourceType.Local) {
            boolean useLocal = getUseLocal();
            connectionStrings = allInOneProvider.getConnectionStrings(names, useLocal, getDatabaseConfigLocation());
        } else {
            try {
                connectionStrings = connectionStringProvider.getConnectionStrings(names);
            } catch (Exception e) {
                error("Fail to setup Titan Provider", e);
                throw new RuntimeException(e);
            }
        }

        return connectionStrings;
    }

    private Map<String, DalConnectionStringConfigure> getConnectionStringConfigures(Set<String> dbNames) {
        Map<String, DalConnectionStringConfigure> connectionStringConfigs = null;
        // get config from api
        try {
            connectionStringConfigs = variableConnectionStringProvider.getConnectionStrings(dbNames);
        } catch (Exception e) {
            error("Fail to setup VariableDataSourceConfigure provider", e);
            throw new RuntimeException(e);
        }

        return  connectionStringConfigs;
    }

    public DataSourceConfigure mergeDataSourceConfig(DalConnectionString connectionString) {
        return dataSourceConfigureLocator.mergeDataSourceConfigure(connectionString);
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
                public void onChanged(DalConnectionString connectionString) {
                    addConnectionStringNotifyTask(name, connectionString);
                }
            });

            listenerKeyNames.add(name);
        }
    }

    private void addConnectionStringNotifyTask(String name, DalConnectionString connectionString) {
        String ipConnectionString = connectionString.getIPConnectionString();
        if (ipConnectionString == null || ipConnectionString.isEmpty())
            throw new RuntimeException("IP connection string is null.");

        String domainConnectionString = connectionString.getDomainConnectionString();
        if (domainConnectionString == null || domainConnectionString.isEmpty())
            throw new RuntimeException("Domain connection string is null.");

        DataSourceIdentity id = new DataSourceName(name);
        String keyName = id.getId();

        DalConnectionStringConfigure connectionStringConfigure = connectionString.getIPConnectionStringConfigure();
        String newVersion = connectionStringConfigure.getVersion();
        DataSourceConfigure oldConfigure = dataSourceConfigureLocator.getDataSourceConfigure(id);

        String oldVersion = null;
        if (oldConfigure != null)
            oldVersion = oldConfigure.getVersion();

        // set connection string
        DalConnectionString oldConnectionString =
                dataSourceConfigureLocator.setConnectionString(keyName, connectionString);
        if (connectionString.equals(oldConnectionString)) {
            String msg = String.format("New connection string of %s equals to old connection string.", name);
            String eventName = String.format("%s:%s", CONNECTIONSTRING_REFRESHCONNECTIONSTRING, name);
            Cat.logEvent(DalLogTypes.DAL_CONFIGURE, eventName, Message.SUCCESS, msg);
            LOGGER.info(msg);
            return;
        }

        // validate version
        if (newVersion != null && oldVersion != null) {
            if (newVersion.equals(oldVersion)) {
                String msg = String.format("New version of %s equals to old version.", name);
                String eventName = String.format("%s:%s", CONNECTIONSTRING_REFRESHCONNECTIONSTRING, name);
                Cat.logEvent(DalLogTypes.DAL_CONFIGURE, eventName, Message.SUCCESS, msg);
                LOGGER.info(msg);
                return;
            }
        }

        String transactionName = String.format("%s:%s", CONNECTIONSTRING_REFRESHCONNECTIONSTRING, name);
        Transaction t = Cat.newTransaction(DalLogTypes.DAL_CONFIGURE, transactionName);
        DalEncrypter encrypter = getEncrypter();
        String encryptedNewIPConnectionString = encrypter.desEncrypt(ipConnectionString);
        String encryptedNewDomainConnectionString = encrypter.desEncrypt(domainConnectionString);

        ConnectionStringConfigure oldIPConfigure = null;
        ConnectionStringConfigure oldDomainConfigure = null;
        String oldNormalUrl = null;
        String oldFailoverUrl = null;
        if (oldConnectionString != null) {
            oldIPConfigure = oldConnectionString.getIPConnectionStringConfigure();
            oldDomainConfigure = oldConnectionString.getDomainConnectionStringConfigure();
            oldNormalUrl = oldIPConfigure.getConnectionUrl();
            oldFailoverUrl = oldDomainConfigure.getConnectionUrl();
        }

        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, OLD_NORMAL_CONNECTIONURL, Message.SUCCESS, oldNormalUrl);
        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, OLD_FAILOVER_CONNECTIONURL, Message.SUCCESS, oldFailoverUrl);

        ConnectionStringConfigure newIPConfigure = connectionString.getIPConnectionStringConfigure();
        ConnectionStringConfigure newDomainConfigure = connectionString.getDomainConnectionStringConfigure();
        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, NEW_NORMAL_CONNECTIONURL, Message.SUCCESS,
                newIPConfigure.getConnectionUrl());
        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, NEW_FAILOVER_CONNECTIONURL, Message.SUCCESS,
                newDomainConfigure.getConnectionUrl());
        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, ENCRYPTED_NEW_NORMAL_CONNECTIONSTRING, Message.SUCCESS,
                encryptedNewIPConnectionString);
        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, ENCRYPTED_NEW_FAILOVER_CONNECTIONSTRING, Message.SUCCESS,
                encryptedNewDomainConnectionString);
        t.setStatus(Transaction.SUCCESS);


        DataSourceConfigure newConfigure = dataSourceConfigureLocator.getDataSourceConfigure(id);
        DataSourceConfigureChangeEvent event = new DataSourceConfigureChangeEvent(keyName, newConfigure, oldConfigure);

        Map<DataSourceIdentity, DataSourceConfigureChangeEvent> events = new HashMap<>();
        events.put(id, event);

        Set<DataSourceIdentity> ids = new HashSet<>();
        ids.add(id);
        try {
            addNotifyTask(ids, events);
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
            public void onChanged(DalPoolPropertiesConfigure configure) {
                addPoolPropertiesNotifyTask(configure);
            }
        });
    }

    private boolean isPoolPropertiesChanged(Properties oldProperties, Properties newProperties) {
        if (oldProperties != null && newProperties != null)
            if (oldProperties.equals(newProperties))
                return false;
        return true;
    }

    private void addPoolPropertiesNotifyTask(DalPoolPropertiesConfigure configure) {
        Transaction t = Cat.newTransaction(DalLogTypes.DAL_CONFIGURE, POOLPROPERTIES_REFRESH_POOLPROPERTIES);
        t.addData(DATASOURCE_NOTIFY_LISTENER_START);
        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, POOLPROPERTIES_REFRESH_POOLPROPERTIES, Message.SUCCESS,
                DATASOURCE_NOTIFY_LISTENER_START);

        try {
            Set<DataSourceIdentity> ids = getRefreshableDataSourceIdentities();
            Map<DataSourceIdentity, DataSourceConfigure> oldConfigures = getDataSourceConfigureByNames(ids);

            // set pool properties
            Properties oldOriginalProperties = dataSourceConfigureLocator.setPoolProperties(configure);
            t.addData(SET_PROPERTIES);
            Cat.logEvent(DalLogTypes.DAL_CONFIGURE, POOLPROPERTIES_REFRESH_POOLPROPERTIES, Message.SUCCESS,
                    SET_PROPERTIES);

            if (!isPoolPropertiesChanged(oldOriginalProperties, configure.getProperties())) {
                String msg = String.format("New pool properties equal to old pool properties.");
                Cat.logEvent(DalLogTypes.DAL_CONFIGURE, POOLPROPERTIES_REFRESH_POOLPROPERTIES, Message.SUCCESS,
                        SET_PROPERTIES);
                LOGGER.info(msg);
                return;
            }

            Map<DataSourceIdentity, DataSourceConfigure> newConfigures = getDataSourceConfigureByNames(ids);
            Map<DataSourceIdentity, DataSourceConfigureChangeEvent> events =
                    getDataSourceConfigureChangeEvent(ids, oldConfigures, newConfigures);

            addNotifyTask(ids, events);
            t.addData(DATASOURCE_NOTIFY_LISTENER_END);
            t.setStatus(Transaction.SUCCESS);
            Cat.logEvent(DalLogTypes.DAL_CONFIGURE, POOLPROPERTIES_REFRESH_POOLPROPERTIES, Message.SUCCESS,
                    DATASOURCE_NOTIFY_LISTENER_END);
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
                IPDomainStatus currentStatus = dataSourceConfigureLocator.getIPDomainStatus();
                if (currentStatus.equals(status)) {
                    String msg = String.format("New status equals to current status:%s", status.toString());
                    Cat.logEvent(DalLogTypes.DAL_CONFIGURE, IPDOMAINSTATUS_REFRESH_IPDOMAINSTATUS, Message.SUCCESS,
                            msg);
                    LOGGER.info(msg);
                    return;
                }

                addIPDomainStatusNotifyTask(status);
            }
        });
    }

    private void addIPDomainStatusNotifyTask(IPDomainStatus status) {
        Transaction t = Cat.newTransaction(DalLogTypes.DAL_CONFIGURE, IPDOMAINSTATUS_REFRESH_IPDOMAINSTATUS);
        String switchStatus = String.format("Switch status:%s", status.toString());
        t.addData(switchStatus);
        t.addData(DATASOURCE_NOTIFY_LISTENER_START);
        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, IPDOMAINSTATUS_REFRESH_IPDOMAINSTATUS, Message.SUCCESS, switchStatus);
        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, IPDOMAINSTATUS_REFRESH_IPDOMAINSTATUS, Message.SUCCESS,
                DATASOURCE_NOTIFY_LISTENER_START);

        try {
            Set<DataSourceIdentity> ids = getRefreshableDataSourceIdentities();
            Map<DataSourceIdentity, DataSourceConfigure> oldConfigures = getDataSourceConfigureByNames(ids);

            // set ip domain status
            dataSourceConfigureLocator.setIPDomainStatus(status);
            t.addData(SET_IP_DOMAIN_STATUS);
            Cat.logEvent(DalLogTypes.DAL_CONFIGURE, IPDOMAINSTATUS_REFRESH_IPDOMAINSTATUS, Message.SUCCESS,
                    SET_IP_DOMAIN_STATUS);

            Map<DataSourceIdentity, DataSourceConfigure> newConfigures = getDataSourceConfigureByNames(ids);
            Map<DataSourceIdentity, DataSourceConfigureChangeEvent> events =
                    getDataSourceConfigureChangeEvent(ids, oldConfigures, newConfigures);

            addNotifyTask(ids, events);
            t.addData(DATASOURCE_NOTIFY_LISTENER_END);
            t.setStatus(Transaction.SUCCESS);
            Cat.logEvent(DalLogTypes.DAL_CONFIGURE, IPDOMAINSTATUS_REFRESH_IPDOMAINSTATUS, Message.SUCCESS,
                    DATASOURCE_NOTIFY_LISTENER_END);
        } catch (Throwable e) {
            DalConfigException exception = new DalConfigException(e);
            t.setStatus(exception);
            Cat.logError(exception);
            LOGGER.error(String.format("DalConfigException:%s", e.getMessage()), exception);
        } finally {
            t.complete();
        }
    }

    private Set<DataSourceIdentity> getRefreshableDataSourceIdentities() {
        Set<DataSourceIdentity> ids = new HashSet<>(dataSourceConfigureChangeListeners.keySet());
        Set<String> keyNames = dataSourceConfigureLocator.getSuccessfulConnectionStrings().keySet();
        for (String keyName : keyNames)
            ids.add(new DataSourceName(keyName));
        return ids;
    }

    private Map<DataSourceIdentity, DataSourceConfigure> getDataSourceConfigureByNames(Set<DataSourceIdentity> ids) {
        Map<DataSourceIdentity, DataSourceConfigure> map = new HashMap<>();
        for (DataSourceIdentity id : ids) {
            DataSourceConfigure configure = dataSourceConfigureLocator.getDataSourceConfigure(id);
            map.put(id, configure);
        }
        return map;
    }

    private Map<DataSourceIdentity, DataSourceConfigureChangeEvent> getDataSourceConfigureChangeEvent(Set<DataSourceIdentity> ids,
            Map<DataSourceIdentity, DataSourceConfigure> oldConfigures, Map<DataSourceIdentity, DataSourceConfigure> newConfigures) {
        Map<DataSourceIdentity, DataSourceConfigureChangeEvent> events = new HashMap<>();
        if (oldConfigures == null || oldConfigures.isEmpty())
            return events;

        if (newConfigures == null || newConfigures.isEmpty())
            return events;

        for (DataSourceIdentity id : ids) {
            DataSourceConfigure oldConfigure = oldConfigures.get(id);
            DataSourceConfigure newConfigure = newConfigures.get(id);
            if (oldConfigure == null || newConfigure == null)
                continue;

            DataSourceConfigureChangeEvent event =
                    new DataSourceConfigureChangeEvent(id.getId(), newConfigure, oldConfigure);
            events.put(id, event);
        }

        return events;
    }

    private void addNotifyTask(final Set<DataSourceIdentity> ids, final Map<DataSourceIdentity, DataSourceConfigureChangeEvent> events) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    executeNotifyTask(ids, events);
                } catch (Throwable e) {
                    Cat.logError(e);
                }
            }
        });
    }

    private void executeNotifyTask(Set<DataSourceIdentity> ids, Map<DataSourceIdentity, DataSourceConfigureChangeEvent> events)
            throws Exception {
        if (ids == null || ids.isEmpty())
            return;

        if (events == null || events.isEmpty())
            return;

        Map<DataSourceIdentity, DataSourceConfigureChangeListener> listeners =
                copyChangeListeners(dataSourceConfigureChangeListeners);

        for (DataSourceIdentity id : ids) {
            String name = id.getId();
            String transactionName = String.format("%s:%s", DATASOURCECONFIGURE_REFRESH_DATASOURCECONFIG, name);
            Transaction transaction = Cat.newTransaction(DalLogTypes.DAL_CONFIGURE, transactionName);

            DataSourceConfigureChangeEvent event = events.get(id);
            if (event == null)
                continue;

            try {
                // old configure
                DataSourceConfigure oldConfigure = event.getOldDataSourceConfigure();
                String oldConnectionUrl = null;
                Properties oldPoolProperties = null;
                if (oldConfigure != null) {
                    oldConnectionUrl = oldConfigure.toConnectionUrl();
                    oldPoolProperties = oldConfigure.toProperties();
                }
                // log
                transaction.addData(CONNECTIONSTRING_OLD_CONNECTIONURL, String.format("%s:%s", name, oldConnectionUrl));
                Cat.logEvent(DalLogTypes.DAL_CONFIGURE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", CONNECTIONSTRING_OLD_CONNECTIONURL, name, oldConnectionUrl));
                transaction.addData(DATASOURCECONFIGURE_OLD_CONFIGURE,
                        String.format("%s:%s", name, poolPropertiesHelper.propertiesToString(oldPoolProperties)));
                Cat.logEvent(DalLogTypes.DAL_CONFIGURE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCECONFIGURE_OLD_CONFIGURE, name,
                                poolPropertiesHelper.propertiesToString(oldPoolProperties)));

                // new configure
                DataSourceConfigure newConfigure = event.getNewDataSourceConfigure();
                String newConnectionUrl = newConfigure.toConnectionUrl();

                // log
                transaction.addData(CONNECTIONSTRING_NEW_CONNECTIONURL, String.format("%s:%s", name, newConnectionUrl));
                Cat.logEvent(DalLogTypes.DAL_CONFIGURE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", CONNECTIONSTRING_NEW_CONNECTIONURL, name, newConnectionUrl));
                transaction.addData(DATASOURCECONFIGURE_NEW_CONFIGURE, String.format("%s:%s", name,
                        poolPropertiesHelper.propertiesToString(newConfigure.toProperties())));
                Cat.logEvent(DalLogTypes.DAL_CONFIGURE, transactionName, Message.SUCCESS,
                        String.format("%s:%s:%s", DATASOURCECONFIGURE_NEW_CONFIGURE, name,
                                poolPropertiesHelper.propertiesToString(newConfigure.toProperties())));

                transaction.setStatus(Transaction.SUCCESS);

                DataSourceConfigureChangeListener listener = listeners.get(id);
                if (listener == null) {
                    listener = getDataSourceConfigureChangeListener(id);
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

    private DataSourceConfigureChangeListener getDataSourceConfigureChangeListener(DataSourceIdentity id) throws Exception {
        String keyName = id.getId();
        // Log Clog,CAT event if DataSourceConfigureChangeListener is null.
        String message = String.format(DATASOURCE_CONFIGURE_CHANGE_LISTENER_IS_NULL_MESSAGE, keyName);
        clog.warn(message);
        Cat.logEvent(DalLogTypes.DAL_CONFIGURE, DATASOURCE_CONFIGURE_CHANGE_LISTENER_IS_NULL, Message.SUCCESS, message);

        DataSourceConfigureChangeListener listener;

        // Get listener from cache again,maybe at this time,DataSourceConfigureChangeListener of the keyName is
        // available.
        listener = dataSourceConfigureChangeListeners.get(id);
        if (listener != null)
            return listener;

        // If we still can't get the DataSourceConfigureChangeListener,then we try to get or create the
        // RefreshableDataSource from DataSourceLocator.
        DataSource ds = getOrCreateDataSource(id);
        if (ds instanceof DataSourceConfigureChangeListener) {
            listener = (DataSourceConfigureChangeListener) ds;
        }

        return listener;
    }

    private DataSource getOrCreateDataSource(DataSourceIdentity id) throws Exception {
        TitanProvider provider = new TitanProvider();
        DataSourceLocator locator = new DataSourceLocator(provider);
        return locator.getDataSource(id);
    }

    public void register(DataSourceIdentity id, DataSourceConfigureChangeListener listener) {
        // TODO: post process - check switch
        dataSourceConfigureChangeListeners.put(id, listener);
    }

    public void unregister(DataSourceIdentity id) {
        dataSourceConfigureChangeListeners.remove(id);
        dataSourceConfigureLocator.removeDataSourceConfigure(id);
    }

    private Map<DataSourceIdentity, DataSourceConfigureChangeListener> copyChangeListeners(
            Map<DataSourceIdentity, DataSourceConfigureChangeListener> map) {
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

    public void setVariableConnectionStringProvider(AbstractVariableDataSourceConfigureProvider provider) {
        this.variableConnectionStringProvider = provider;
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
        config.clear();
        startUpLog.clear();
    }

}
