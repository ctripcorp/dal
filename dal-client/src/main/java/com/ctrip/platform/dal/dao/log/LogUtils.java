package com.ctrip.platform.dal.dao.log;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.IClusterDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.log.DataSourceLogContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class LogUtils {

    private static LogFilter logFilter;

    static {
        try {
            logFilter = DalPropertiesManager.getInstance().getDalPropertiesLocator().exceptionLogFilter();
        } catch (Exception e) {
            logFilter = new LogFilter() {
                @Override
                public boolean filter(Throwable throwable) {
                    return false;
                }
            };
        }
    }

    private static final ThreadLocal<DataSourceLogContext> logContext = new ThreadLocal(){

        @Override
        protected Object initialValue() {
            return new DataSourceLogContext();
        }
    };

    public static DataSourceLogContext getLogContext() {
        return logContext.get();
    }

    public static void sqlTransactionStart() {
        logContext.get().setSqlTransactionStartTime(System.currentTimeMillis());
    }

    public static void connectionInterval() {
        logContext.get().setConnectionObtained(System.currentTimeMillis());
    }

    public static void markLogged(boolean hasLogged) {
        getLogContext().setHasLogged(hasLogged);
    }

    public static void clearLogContext() {
        logContext.get().clear();
    }

    public static void logReadStrategy(Cluster cluster) {
        try {
            getLogContext().setReadStrategy(cluster.getCustomizedOption().getRouteStrategy());
        } catch (Throwable t) {
            // no need to do
        }
    }

    public static Map<String, String> buildPropertiesFromDataSourceId(DataSourceIdentity dataSourceId) {
        if (dataSourceId instanceof IClusterDataSourceIdentity) {
            IClusterDataSourceIdentity _dataSourceId = (IClusterDataSourceIdentity) dataSourceId;
            Map<String, String> properties = new HashMap<>();
            if (_dataSourceId.getClusterName() != null)
                properties.put("DAL.cluster", _dataSourceId.getClusterName());
            if (_dataSourceId.getShardIndex() != null)
                properties.put("DAL.cluster.shard", String.valueOf(_dataSourceId.getShardIndex()));
            if (_dataSourceId.getDatabaseRole() != null)
                properties.put("DAL.cluster.role", _dataSourceId.getDatabaseRole().getValue());
            return properties;
        }
        return null;
    }

    public static boolean ignoreError(Throwable throwable) {
        return logFilter.filter(throwable);
    }

}
