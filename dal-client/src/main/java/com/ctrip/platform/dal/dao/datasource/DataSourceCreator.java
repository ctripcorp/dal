package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
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

    public SingleDataSource getOrCreateSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        SingleDataSource ds = targetDataSourceCache.get(configure);
        if (ds == null) {
            synchronized (targetDataSourceCache) {
                ds = targetDataSourceCache.get(configure);
                if (ds == null) {
                    try {
                        ds = createSingleDataSource(name, configure, listener);
                        targetDataSourceCache.put(configure, ds);
                    } catch (Throwable t) {
                        String msg = String.format("error when creating single datasource: %s", name);
                        LOGGER.error(msg, t);
                        throw new RuntimeException(msg, t);
                    }
                }
            }
        }
        return ds;
    }

    private SingleDataSource createSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        SingleDataSource ds = new SingleDataSource(name, configure, listener);
        service.schedule(ds, INIT_DELAY, TimeUnit.MILLISECONDS);
        return ds;
    }

}
