package com.ctrip.datasource.titan;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.configure.LocalClusterInfoProvider;
import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.ClusterInfoProvider;
import com.ctrip.datasource.configure.CtripClusterInfoProvider;
import com.ctrip.datasource.configure.CtripLocalClusterConfigProvider;
import com.ctrip.datasource.configure.qconfig.CtripClusterConfigProvider;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.datasource.common.enums.SourceType;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceName;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

public class TitanProvider implements IntegratedConfigProvider {

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String USE_LOCAL_CONFIG = "useLocalConfig";
    //private static final String CONNECTION_STRING_TYPE = "dal.connectionString";
    private static final String MYSQL_API_CONNECTION_STRING = "mysqlApiConnectionString";
    private static final String NULL_MYSQL_API_CONNECTION_STRING = "nullConnectionString";
    private static final String CONNECTION_STRING_CHANGE = "connectionStringChange";
    private static final String CONNECTION_STRING_OLD = "Old ConnectionString:%s";
    private static final String CONNECTION_STRING_NEW = "New ConnectionString:%s";
    private static final String NULL_STRING = "null";
    private DataSourceConfigureManager dataSourceConfigureManager = DataSourceConfigureManager.getInstance();
    private SourceType sourceType = SourceType.Remote;
    private DalPropertiesManager dalPropertiesManager = DalPropertiesManager.getInstance();
    private ClusterConfigProvider clusterConfigProvider = new CtripClusterConfigProvider();
    private ClusterInfoProvider clusterInfoProvider = new CtripClusterInfoProvider(DalPropertiesManager.getInstance().getDalPropertiesLocator(), HttpExecutor.getInstance());

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        setSourceType(settings);
        dataSourceConfigureManager.initialize(settings);
        if (sourceType == SourceType.Local) {
            clusterConfigProvider = new CtripLocalClusterConfigProvider();
            clusterInfoProvider = new LocalClusterInfoProvider();
        }
    }

    @Override
    public void setup(Set<String> names) {
        dalPropertiesManager.setup();
        dataSourceConfigureManager.setup(names, sourceType);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String name) {
        return DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(name);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(DataSourceIdentity id) {
        return DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(id);
    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(String name){
        Set<String> names = new HashSet<>();
        names.add(name);
        dataSourceConfigureManager.setup(names, sourceType);
        return getDataSourceConfigure(name);
    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(DataSourceIdentity id) {
        Set<String> names = new HashSet<>();
        dataSourceConfigureManager.setup(names, sourceType);
        return getDataSourceConfigure(id);
    }

    @Override
    public void register(String name, DataSourceConfigureChangeListener listener) {
        register(new DataSourceName(name), listener);
    }

    @Override
    public void register(DataSourceIdentity id, DataSourceConfigureChangeListener listener) {
        dataSourceConfigureManager.register(id, listener);
        registerMysqlApi(id, listener);
    }

    @Override
    public void unregister(String name) {
        unregister(new DataSourceName(name));
    }

    @Override
    public void unregister(DataSourceIdentity id) {
        dataSourceConfigureManager.unregister(id);
    }

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        return clusterConfigProvider.getClusterConfig(clusterName);
    }

    private void registerMysqlApi(final DataSourceIdentity id, DataSourceConfigureChangeListener listener) {
        if (id instanceof ApiDataSourceIdentity) {
            ConnectionStringConfigureProvider provider = ((ApiDataSourceIdentity) id).getProvider();
            provider.addListener(new Listener<DalConnectionStringConfigure>() {
                @Override
                public void onChanged(DalConnectionStringConfigure newConnectionStringConfigure) {
                    if (newConnectionStringConfigure != null) {
                        String newConnectionUrl = newConnectionStringConfigure.getConnectionUrl();

                        DataSourceConfigureLocator dataSourceConfigureLocator = DataSourceConfigureLocatorManager.getInstance();
                        DataSourceConfigure oldDataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure(id);
                        String oldConnectionUrl = oldDataSourceConfigure.getConnectionUrl();
                        LOGGER.logEvent(DalLogTypes.DAL_CONNECTION_STRING, MYSQL_API_CONNECTION_STRING, newConnectionUrl);

                        if (!newConnectionUrl.equalsIgnoreCase(oldConnectionUrl)) {
                            Transaction t = Cat.newTransaction(DalLogTypes.DAL_CONNECTION_STRING, CONNECTION_STRING_CHANGE);
                            LOGGER.logEvent(DalLogTypes.DAL_CONNECTION_STRING, MYSQL_API_CONNECTION_STRING, String.format(CONNECTION_STRING_OLD, oldConnectionUrl));
                            LOGGER.logEvent(DalLogTypes.DAL_CONNECTION_STRING, MYSQL_API_CONNECTION_STRING, String.format(CONNECTION_STRING_NEW, newConnectionUrl));

                            dataSourceConfigureLocator.setApiConnectionString(id,
                                    new ApiDataSourceIdentity.ApiConnectionStringImpl(newConnectionStringConfigure));
                            DataSourceConfigure newDataSourceConfigure = dataSourceConfigureLocator.getDataSourceConfigure(id);

                            DataSourceConfigureChangeEvent event = new DataSourceConfigureChangeEvent(id.getId(),
                                    newDataSourceConfigure, oldDataSourceConfigure);
                            synchronized (TitanProvider.class) {
                                try {
                                    listener.configChanged(event);
                                    t.setStatus(Message.SUCCESS);
                                } catch (SQLException e) {
                                    Cat.logError(String.format("mgr datasource[name:%s] switch failed", id.getId()), e);
                                    t.setStatus(e);
                                } finally {
                                    t.complete();
                                }
                            }
                        }
                    }
                    else {
                        LOGGER.logEvent(DalLogTypes.DAL_CONNECTION_STRING, NULL_MYSQL_API_CONNECTION_STRING, NULL_STRING);
                    }
                }
            });
        }
    }

    private void setSourceType(Map<String, String> settings) {
        if (settings != null && Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG))) {
            sourceType = SourceType.Local;
            return;
        }
        setSourceTypeByEnv();
    }

    public void setSourceTypeByEnv() {
        Env env = Foundation.server().getEnv();
        if (env.equals(Env.UNKNOWN) || env.equals(Env.DEV) || env.equals(Env.LOCAL)) {
            sourceType = SourceType.Local;
        }
    }

    // for unit test only
    public void clear() {
        dataSourceConfigureManager.clear();
    }

    public ClusterInfo tryGetClusterInfo(String titanKey) {
        return getClusterInfo(titanKey);
    }

    @Override
    public ClusterInfo getClusterInfo(String titanKey) {
        return clusterInfoProvider.getClusterInfo(titanKey);
    }

}
