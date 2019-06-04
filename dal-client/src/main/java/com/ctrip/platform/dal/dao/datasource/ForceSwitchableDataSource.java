package com.ctrip.platform.dal.dao.datasource;


import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;


public class ForceSwitchableDataSource extends RefreshableDataSource implements IForceSwitchableDataSource {
    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private IDataSourceConfigureProvider provider;
    private CopyOnWriteArraySet<SwitchListener> listeners = new CopyOnWriteArraySet<>();
    private HostAndPort currentHostAndPort;
    private volatile boolean isForceSwitched;
    private volatile boolean isConnected;
    private ReentrantLock lock = new ReentrantLock();
    private static ThreadPoolExecutor executor;
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 1L;


    public ForceSwitchableDataSource(IDataSourceConfigureProvider provider) throws SQLException {
        this(provider.getDataSourceConfigure().getDBName(), provider);
    }

    public ForceSwitchableDataSource(String name, IDataSourceConfigureProvider provider) throws SQLException {
        super(name, DataSourceConfigure.valueOf(provider.getDataSourceConfigure()));
        this.provider = provider;
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CustomThreadFactory("WarmUpConnections"));
        executor.allowCoreThreadTimeOut(true);
    }

    public SwitchableDataSourceStatus forceSwitch(String ip, Integer port) {
        synchronized (lock) {
            SwitchableDataSourceStatus oldStatus = getStatus();
            DataSourceConfigure configure = getSingleDataSource().getDataSourceConfigure().clone();
            String name = getSingleDataSource().getName();

            isForceSwitched = true;
            isConnected = false;

            configure.replaceURL(ip, port);

            try {
                refreshDataSource(name, configure, new ForceSwitchListener() {
                    public void onCreatePoolSuccess() {
                        isConnected = true;
                        final SwitchableDataSourceStatus status = getStatus(true);
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
                        isConnected = false;
                        final SwitchableDataSourceStatus status = getStatus(false);
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
            } catch (SQLException e) {
                throw new DalRuntimeException(e);
            }

            return oldStatus;
        }
    }

    public SwitchableDataSourceStatus getStatus() {
        return getStatus(false);
    }

    private SwitchableDataSourceStatus getStatus(final boolean isPoolCreated) {
        synchronized (lock) {
            org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) getSingleDataSource().getDataSource();

            final String url = ds.getUrl();

            if (isUrlChanged(url)) {
                try {
                    final CountDownLatch latch = new CountDownLatch(1);
                    executor.submit(new Runnable() {
                        public void run() {
                            try {
                                setIpPortCache(ConnectionStringParser.parseHostPortFromURL(getConnection().getMetaData().getURL()));
                                setIsConnected(true);
                            } catch (Exception e) {
                                if (isPoolCreated)
                                    setIpPortCache(ConnectionStringParser.parseHostPortFromURL(url));
                                else
                                    setIpPortCache(new HostAndPort());
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

            return new SwitchableDataSourceStatus(isForceSwitched, currentHostAndPort.getHost(), currentHostAndPort.getPort(), isConnected);
        }
    }

    public SwitchableDataSourceStatus restore() {
        synchronized (lock) {
            String name = getSingleDataSource().getName();
            SwitchableDataSourceStatus oldStatus = getStatus();
            DataSourceConfigure configure = DataSourceConfigure.valueOf(provider.forceLoadDataSourceConfigure());

            isConnected = false;

            try {
                refreshDataSource(name, configure, new RestoreListener() {
                    public void onCreatePoolSuccess() {
                        isConnected = true;
                        final SwitchableDataSourceStatus status = getStatus(true);
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
                        isConnected = false;
                        final SwitchableDataSourceStatus status = getStatus(false);
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
            } catch (SQLException e) {
                throw new DalRuntimeException(e);
            }

            isForceSwitched = false;
            return oldStatus;
        }
    }

    public void addListener(SwitchListener listener) {
        listeners.add(listener);
    }

    private void setIpPortCache(HostAndPort hostAndPort) {
        this.currentHostAndPort = hostAndPort;
    }


    public void setIsConnected(boolean isconnected) {
        this.isConnected = isconnected;
    }


    private boolean isUrlChanged(String url) {
        if (currentHostAndPort == null || currentHostAndPort.getConnectionUrl() == null)
            return true;
        return !(url.equalsIgnoreCase(currentHostAndPort.getConnectionUrl()));
    }

    @Override
    public void configChanged(DataSourceConfigureChangeEvent event) throws SQLException {
        if (isForceSwitched)
            return;
        super.configChanged(event);
    }

}
