package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionValidator;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataSourceCreator {

    private static final int INIT_DELAY = 0;
    private static final int POOL_SIZE = 1;
    private static final String THREAD_NAME = "DataSourceCreator";
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static volatile DataSourceCreator creator = null;

    private ScheduledExecutorService service = Executors.newScheduledThreadPool(POOL_SIZE, new CustomThreadFactory(THREAD_NAME));
    private final Map<DataSourceConfigure, SingleDataSource> targetDataSourceCache = new ConcurrentHashMap<>();

    public static DataSourceCreator getInstance() {
        if (creator == null) {
            synchronized (DataSourceCreator.class) {
                if (creator == null) {
                    creator = new DataSourceCreator();
                }
            }
        }
        return creator;
    }

    public SingleDataSource getOrCreateDataSource(String name, DataSourceConfigure configure) {
        SingleDataSource ds = targetDataSourceCache.get(configure);
        if (ds == null) {
            synchronized (targetDataSourceCache) {
                ds = targetDataSourceCache.get(configure);
                if (ds == null) {
                    try {
                        ds = createDataSource(name, configure);
                        targetDataSourceCache.put(configure, ds);
                    } catch (Throwable t) {
                        String msg = String.format("error when creating single datasource: %s", name);
                        LOGGER.error(msg, t);
                        throw new RuntimeException(msg, t);
                    }
                }
            }
        }
        ds.register();
        return ds;
    }

    public SingleDataSource getOrCreateDataSourceWithoutPool(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        return getOrCreateDataSourceWithoutPool(name, configure, listener, null);
    }

    public SingleDataSource getOrCreateDataSourceWithoutPool(String name, DataSourceConfigure configure,
                                                             DataSourceCreatePoolListener listener,
                                                             ConnectionValidator clusterConnValidator) {
        SingleDataSource ds = targetDataSourceCache.get(configure);
        if (ds == null) {
            synchronized (targetDataSourceCache) {
                ds = targetDataSourceCache.get(configure);
                if (ds == null) {
                    try {
                        ds = createDataSourceWithoutPool(name, configure, listener, clusterConnValidator);
                        targetDataSourceCache.put(configure, ds);
                    } catch (Throwable t) {
                        String msg = String.format("error when creating single datasource: %s", name);
                        LOGGER.error(msg, t);
                        throw new RuntimeException(msg, t);
                    }
                }
            }
        }
        ds.register();
        return ds;
    }

    public SingleDataSource getOrAsyncCreateDataSourceWithPool(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        SingleDataSource ds = targetDataSourceCache.get(configure);
        if (ds == null) {
            synchronized (targetDataSourceCache) {
                ds = targetDataSourceCache.get(configure);
                if (ds == null) {
                    try {
                        ds = asyncCreateDataSourceWithPool(name, configure, listener);
                        targetDataSourceCache.put(configure, ds);
                    } catch (Throwable t) {
                        String msg = String.format("error when creating single datasource: %s", name);
                        LOGGER.error(msg, t);
                        throw new RuntimeException(msg, t);
                    }
                }
            }
        }
        ds.register();
        return ds;
    }

    public SingleDataSource forceCreateSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        SingleDataSource ds = targetDataSourceCache.get(configure);
        if (ds != null) {
            ds.reCreateDataSource();
            service.schedule(ds, INIT_DELAY, TimeUnit.MILLISECONDS);
        } else {
            synchronized (targetDataSourceCache) {
                ds = targetDataSourceCache.get(configure);
                if (ds == null) {
                    try {
                        ds = asyncCreateDataSourceWithPool(name, configure, listener);
                        targetDataSourceCache.put(configure, ds);
                    } catch (Throwable t) {
                        String msg = String.format("error when creating single datasource: %s", name);
                        LOGGER.error(msg, t);
                        throw new RuntimeException(msg, t);
                    }
                }
            }
        }
        ds.register();
        return ds;
    }

    public void returnDataSource(SingleDataSource ds) {
        if (ds != null && ds.unregister() <= 0) {
            DataSourceConfigure config = ds.getDataSourceConfigure();
            targetDataSourceCache.remove(config, ds);
            ds.cancelTask();
            DataSourceTerminator.getInstance().close(ds);
        }
    }

    private SingleDataSource createDataSource(String name, DataSourceConfigure configure) {
        return new SingleDataSource(name, configure);
    }

    private SingleDataSource createDataSourceWithoutPool(String name, DataSourceConfigure configure,
                                                         DataSourceCreatePoolListener listener,
                                                         ConnectionValidator clusterConnValidator) {
        return new SingleDataSource(name, configure, listener, clusterConnValidator);
    }

    private SingleDataSource asyncCreateDataSourceWithPool(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        SingleDataSource ds = new SingleDataSource(name, configure, listener);
        service.schedule(ds, INIT_DELAY, TimeUnit.MILLISECONDS);
        return ds;
    }

    public void closeAllDataSources() {
        for (SingleDataSource ds : targetDataSourceCache.values()) {
            DataSourceConfigure config = ds.getDataSourceConfigure();
            targetDataSourceCache.remove(config, ds);
            ds.cancelTask();
            DataSourceTerminator.getInstance().close(ds);
        }
    }

}
