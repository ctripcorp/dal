package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.common.enums.ForceSwitchedStatus;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import com.ctrip.platform.dal.dao.log.Callback;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ForceSwitchableDataSource extends RefreshableDataSource implements IForceSwitchableDataSource {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private IDataSourceConfigureProvider provider;
    private CopyOnWriteArraySet<SwitchListener> listeners = new CopyOnWriteArraySet<>();
    private HostAndPort currentHostAndPort = new HostAndPort();
    private volatile ForceSwitchedStatus forceSwitchedStatus = ForceSwitchedStatus.UnForceSwitched;
    private volatile ForceSwitchedStatus oldForceSwitchedStatus = ForceSwitchedStatus.UnForceSwitched;
    private volatile boolean poolCreated = false;
    private boolean isNullDataSource = false;
    private final Lock lock = new ReentrantLock();
    private static ThreadPoolExecutor executor;
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 1L;
    private static final String FORCE_SWITCH = "ForceSwitch::forceSwitch:%s";
    private static final String GET_STATUS = "ForceSwitch::getStatus:%s";
    private static final String RESTORE = "ForceSwitch::restore:%s";
    private static final String NULL_DATASOURCE = "nullDataSource";
    private static final DataSourceConfigureConvert dataSourceConfigureConvert = ServiceLoaderHelper.getInstance(DataSourceConfigureConvert.class);

    public ForceSwitchableDataSource(IDataSourceConfigureProvider provider) {
        this(getIDataSourceConfigure(provider) == null ? NULL_DATASOURCE : getIDataSourceConfigure(provider).getConnectionUrl(), provider);
    }

    public ForceSwitchableDataSource(String name, IDataSourceConfigureProvider provider) {
        this(new DataSourceName(name), provider);
    }

    public ForceSwitchableDataSource(DataSourceIdentity id, IDataSourceConfigureProvider provider) {
        super(id, DataSourceConfigure.valueOf(getIDataSourceConfigure(provider) == null ? new DataSourceConfigure() : getIDataSourceConfigure(provider)));
        if (getIDataSourceConfigure(provider) == null) {
            isNullDataSource = true;
        }
        this.provider = provider;
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CustomThreadFactory("ForceSwitchableDataSource"));
        executor.allowCoreThreadTimeOut(true);
        getStatus();
    }

    private static IDataSourceConfigure getIDataSourceConfigure(IDataSourceConfigureProvider provider) {
        IDataSourceConfigure iDataSourceConfigure = null;
        try {
            iDataSourceConfigure = provider.getDataSourceConfigure();
        } catch (Exception e) {
            LOGGER.error("provider get datasource configure error, ", e);
        }
        return iDataSourceConfigure;
    }

    public SwitchableDataSourceStatus forceSwitch(FirstAidKit configure, final String ip, final Integer port) {
        synchronized (lock) {
            final SwitchableDataSourceStatus oldStatus = getStatus();
            final String name;
            final DataSourceConfigure usedConfigure;
            forceSwitchedStatus = ForceSwitchedStatus.ForceSwitching;
            try {
                if (isNullDataSource) {
                    if (configure instanceof IDataSourceConfigure) {
                        usedConfigure = dataSourceConfigureConvert.desDecrypt(DataSourceConfigure.valueOf((IDataSourceConfigure) configure));
                        name = ((IDataSourceConfigure) configure).getConnectionUrl();
                    } else {
                        throw new DalRuntimeException("Force Switch Error: datasource configure is invalid");
                    }
                } else {
                    usedConfigure = getSingleDataSource().getDataSourceConfigure().clone();
                    name = getSingleDataSource().getName();
                }
                final String logName = String.format(FORCE_SWITCH, name);
                LOGGER.logTransaction(DalLogTypes.DAL_CONFIGURE, logName,
                        String.format("isNullDataSource: %s; newIp: %s, newPort: %s", isNullDataSource, ip, port),
                        new Callback() {
                    @Override
                    public void execute() throws Exception {
                        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("old connection url: %s", usedConfigure.getConnectionUrl()));
                        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("old isForceSwitched before force switch: %s, old poolCreated before force switch: %s", oldStatus.isForceSwitched(), oldStatus.isPoolCreated()));
                        usedConfigure.replaceURL(ip, port);
                        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("new connection url: %s", usedConfigure.getConnectionUrl()));
                        asyncRefreshDataSource(name, usedConfigure, new ForceSwitchListener() {
                            public void onCreatePoolSuccess() {
                                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format("onCreatePoolSuccess: %s", name), usedConfigure.getConnectionUrl());
                                poolCreated = true;
                                forceSwitchedStatus = ForceSwitchedStatus.ForceSwitched;
                                oldForceSwitchedStatus = ForceSwitchedStatus.ForceSwitched;
                                isNullDataSource = false;
                                final SwitchableDataSourceStatus status = getStatus();
                                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format("onCreatePoolSuccess::notifyListeners: %s", name), "notify listeners' onForceSwitchSuccess");
                                for (final SwitchListener listener : listeners)
                                    executor.submit(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                listener.onForceSwitchSuccess(status);
                                            } catch (Exception e) {
                                                LOGGER.error("call listener.onForceSwitchSuccess() error ", e);
                                            }
                                        }
                                    });
                            }

                            public void onCreatePoolFail(final Throwable e) {
                                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format("onCreatePoolFail: %s", name), e.getMessage());
                                forceSwitchedStatus = oldForceSwitchedStatus;
                                final SwitchableDataSourceStatus status = getStatus();
                                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format("onCreatePoolFail::notifyListeners: %s", name), "notify listeners' onForceSwitchFail");
                                for (final SwitchListener listener : listeners)
                                    executor.submit(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                listener.onForceSwitchFail(status, e);
                                            } catch (Exception e1) {
                                                LOGGER.error("call listener.onForceSwitchFail() error ", e1);
                                            }
                                        }
                                    });
                            }
                        });
                    }
                });
            } catch (Throwable t) {
                forceSwitchedStatus = oldForceSwitchedStatus;
                LOGGER.error("Force switch error", t);
                throw new DalRuntimeException("Force switch error", t);
            }
            return oldStatus;
        }
    }

    public SwitchableDataSourceStatus getStatus() {
        synchronized (lock) {
            org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) getSingleDataSource().getDataSource();

            final String url = ds.getUrl();
            final String name = getSingleDataSource().getName();
            final String logName = String.format(GET_STATUS, name);

            try {
                LOGGER.logTransaction(DalLogTypes.DAL_CONFIGURE, logName, url, new Callback() {
                    @Override
                    public void execute() throws Exception {
                        if (!currentHostAndPort.isValid() || isUrlChanged(url)) {
                            currentHostAndPort.setValid(false);
                            LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, "url changed, we will get url from connection");
                            try {
                                final CountDownLatch latch = new CountDownLatch(1);
                                executor.submit(new Runnable() {
                                    public void run() {
                                        try (Connection conn = getConnection()) {
                                            HostAndPort hostAndPort = ConnectionStringParser.parseHostPortFromURL(conn.getMetaData().getURL());
                                            hostAndPort.setValid(true);
                                            setIpPortCache(hostAndPort);
                                            setPoolCreated(true);
                                        } catch (Exception e) {
                                            setIpPortCache(ConnectionStringParser.parseHostPortFromURL(url));
                                        } finally {
                                            latch.countDown();
                                        }
                                    }
                                });
                                latch.await(1, TimeUnit.SECONDS);
                            } catch (Exception e) {
                                LOGGER.error("get connection error", e);
                            }
                        }
                    }
                });
            } catch (Throwable t) {
                LOGGER.error("Get status error", t);
                throw new DalRuntimeException("Get status error", t);
            }
            boolean isForceSwitched;
            if (ForceSwitchedStatus.ForceSwitched.equals(forceSwitchedStatus)) {
                isForceSwitched = true;
            }
            else {
                isForceSwitched = false;
            }
            if (!currentHostAndPort.isValid()) {
                setIpPortCache(ConnectionStringParser.parseHostPortFromURL(url));
                return buildSwitchableDataSourceStatus(isForceSwitched, currentHostAndPort.getHost(), url, currentHostAndPort.getPort(), false);
            }
            return buildSwitchableDataSourceStatus(isForceSwitched, currentHostAndPort.getHost(), url, currentHostAndPort.getPort(), poolCreated);
        }
    }

    public SwitchableDataSourceStatus restore() {
        synchronized (lock) {
            final SwitchableDataSourceStatus oldStatus = getStatus();
            final String name = getSingleDataSource().getName();
            final String logName = String.format(RESTORE, name);

            try {
                LOGGER.logTransaction(DalLogTypes.DAL_CONFIGURE, logName, "restore", new Callback() {
                    @Override
                    public void execute() throws Exception {
                        DataSourceConfigure configure = getSingleDataSource().getDataSourceConfigure().clone();

                        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("old connection url: %s", configure.getConnectionUrl()));
                        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("old isForceSwitched before restore: %s, old poolCreated before restore: %s", oldStatus.isForceSwitched(), oldStatus.isPoolCreated()));

                        if (!ForceSwitchedStatus.ForceSwitched.equals(forceSwitchedStatus)) {
                            LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("%s is not force switched, return", name));
                            return;
                        }

                        final DataSourceConfigure newConfigure = DataSourceConfigure.valueOf(provider.forceLoadDataSourceConfigure());
                        LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, logName, String.format("new connection url: %s", newConfigure.getConnectionUrl()));
                        asyncRefreshDataSource(name, newConfigure, new RestoreListener() {
                            public void onCreatePoolSuccess() {
                                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format("onCreatePoolSuccess: %s",name),  newConfigure.getConnectionUrl());
                                poolCreated = true;
                                forceSwitchedStatus = ForceSwitchedStatus.UnForceSwitched;
                                oldForceSwitchedStatus = ForceSwitchedStatus.UnForceSwitched;
                                final SwitchableDataSourceStatus status = getStatus();
                                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format("onCreatePoolSuccess::notifyListeners: %s",name), "notify listeners' onRestoreSuccess");
                                for (final SwitchListener listener : listeners)
                                    executor.submit(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                listener.onRestoreSuccess(status);
                                            } catch (Exception e1) {
                                                LOGGER.error("call listener.onRestoreSuccess() error ", e1);
                                            }
                                        }
                                    });
                            }

                            public void onCreatePoolFail(final Throwable e) {
                                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format("onCreatePoolFail: %s",name),  e.getMessage());
                                final SwitchableDataSourceStatus status = getStatus();
                                LOGGER.logEvent(DalLogTypes.DAL_DATASOURCE, String.format("onCreatePoolFail::notifyListeners: %s",name), "notify listeners' onRestoreFail");
                                for (final SwitchListener listener : listeners)
                                    executor.submit(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                listener.onRestoreFail(status, e);
                                            } catch (Exception e1) {
                                                LOGGER.error("call listener.onRestoreFail() error ", e1);
                                            }
                                        }
                                    });
                            }
                        });
                    }
                });
            } catch (Throwable t) {
                LOGGER.error("Restore error", t);
                throw new DalRuntimeException("Restore error", t);
            }
            return oldStatus;
        }
    }

    public void addListener(SwitchListener listener) {
        listeners.add(listener);
    }

    private void setIpPortCache(HostAndPort hostAndPort) {
        this.currentHostAndPort = hostAndPort;
    }

    private void setPoolCreated(boolean poolCreated) {
        this.poolCreated = poolCreated;
    }


    private boolean isUrlChanged(String url) {
        if (currentHostAndPort == null || currentHostAndPort.getConnectionUrl() == null)
            return true;
        return !(url.equalsIgnoreCase(currentHostAndPort.getConnectionUrl()));
    }

    @Override
    public void configChanged(DataSourceConfigureChangeEvent event) throws SQLException {
        if (!ForceSwitchedStatus.UnForceSwitched.equals(forceSwitchedStatus)) {
            LOGGER.logEvent(DalLogTypes.DAL_CONFIGURE, String.format("configChanged:%s", event.getName()), String.format("%s is force switched, return",event.getName()));
            return;
        }
        super.configChanged(event);
    }

    public FirstAidKit getFirstAidKit() {
        DataSourceConfigure dataSourceConfigure = getSingleDataSource().getDataSourceConfigure();
        return SerializableDataSourceConfig.valueOf(dataSourceConfigureConvert.desEncrypt(dataSourceConfigure));
    }

    private SwitchableDataSourceStatus buildSwitchableDataSourceStatus(boolean isForceSwitched, String hostName, String connUrl, Integer port, boolean poolCreated) {
        if (hostName == null && port == null)
            return new SwitchableDataSourceStatus(isForceSwitched, connUrl, port, poolCreated);
        else
            return new SwitchableDataSourceStatus(isForceSwitched, hostName, port, poolCreated);
    }
}
