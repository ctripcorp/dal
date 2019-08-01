package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author c7ch23en
 */
public class TargetDataSourceManager {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private final Map<DataSourceConfigure, SingleDataSource> targetDataSources = new ConcurrentHashMap<>();

    public SingleDataSource getTargetDataSource(DataSourceConfigure configure) {
        SingleDataSource ds = targetDataSources.get(configure);
        if (ds == null) {
            synchronized (targetDataSources) {
                ds = targetDataSources.get(configure);
                if (ds == null) {
                    try {
                        ds = createTargetDataSource(configure);
                        targetDataSources.put(configure, ds);
                    } catch (Throwable t) {
                        String msg = String.format("creating target datasource exception for configure: %s", configure);
                        LOGGER.error(msg, t);
                        throw new RuntimeException(msg, t);
                    }
                }
            }
        }
        return ds;
    }

    private SingleDataSource createTargetDataSource(DataSourceConfigure configure) {
        return new SingleDataSource(configure.getName(), configure);
    }

}
