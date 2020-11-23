package com.ctrip.platform.dal.dao.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.ListenableSupport;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.config.*;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.multihost.DefaultClusterRouteStrategyConfig;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.datasource.cluster.HostSpec;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class ClusterConfigAdapter extends ListenableSupport<ClusterConfig> implements ClusterConfig {

    private final ConnectionStringConfigureProvider provider;
    private final AtomicReference<ClusterConfig> clusterConfigRef = new AtomicReference<>();

    public ClusterConfigAdapter(ConnectionStringConfigureProvider provider) {
        this.provider = provider;
        init();
    }

    private void init() {
        try {
            DalConnectionStringConfigure configure = provider.getConnectionString();
            load(configure);
            provider.addListener(current -> {
                try {
                    load(current);
                } catch (Throwable t) {
                    // ignore
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Init ClusterConfigAdapter failed: " + provider.getDbName());
        }
    }

    private void load(DalConnectionStringConfigure configure) {
        ClusterConfig clusterConfig;
        if (configure instanceof InvalidVariableConnectionString) {
            throw new DalRuntimeException("connectionString invalid for db: " + provider.getDbName(),
                    ((InvalidVariableConnectionString) configure).getConnectionStringException());
        }
        if (configure == null)
            throw new RuntimeException("Get null config from mysqlapi for db: " + provider.getDbName());
        if (configure instanceof MultiHostConnectionStringConfigure)
            clusterConfig = buildMultiHostClusterConfig((MultiHostConnectionStringConfigure) configure);
        else
            clusterConfig = buildNormalClusterConfig(configure);
        clusterConfigRef.getAndSet(clusterConfig);
        for (Listener<ClusterConfig> listener : getListeners()) {
            try {
                listener.onChanged(this);
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private ClusterConfig buildMultiHostClusterConfig(MultiHostConnectionStringConfigure configure) {
        ClusterConfigWithNoVersion clusterConfig =
                new ClusterConfigWithNoVersion(configure.getName(), ClusterType.MGR, DatabaseCategory.MYSQL);
        DatabaseShardConfigImpl databaseShardConfig = new DatabaseShardConfigImpl(clusterConfig, 0);
        List<HostSpec> hosts = configure.getHosts();
        hosts.forEach(host -> {
            DatabaseConfigImpl databaseConfig = new DatabaseConfigImpl(databaseShardConfig);
            databaseConfig.setIp(host.host());
            databaseConfig.setPort(host.port());
            databaseConfig.setZone(host.zone());
            databaseConfig.setDbName(configure.getDbName());
            databaseConfig.setUid(configure.getUserName());
            databaseConfig.setPwd(configure.getPassword());
            databaseShardConfig.addDatabaseConfig(databaseConfig);
        });
        clusterConfig.addDatabaseShardConfig(databaseShardConfig);
        DefaultClusterRouteStrategyConfig routeStrategy =
                new DefaultClusterRouteStrategyConfig(ClusterConfigXMLConstants.ORDERED_ACCESS_STRATEGY);
        if (configure.getZonesPriority() != null)
            routeStrategy.setProperty(DataSourceConfigureConstants.ZONES_PRIORITY,
                    configure.getZonesPriority());
        if (configure.getFailoverTimeMS() != null)
            routeStrategy.setProperty(DataSourceConfigureConstants.FAILOVER_TIME_MS,
                    String.valueOf(configure.getFailoverTimeMS()));
        if (configure.getBlacklistTimeoutMS() != null)
            routeStrategy.setProperty(DataSourceConfigureConstants.BLACKLIST_TIMEOUT_MS,
                    String.valueOf(configure.getBlacklistTimeoutMS()));
        if (configure.getFixedValidatePeriodMS() != null)
            routeStrategy.setProperty(DataSourceConfigureConstants.FIXED_VALIDATE_PERIOD_MS,
                    String.valueOf(configure.getFixedValidatePeriodMS()));
        clusterConfig.setRouteStrategyConfig(routeStrategy);
        return clusterConfig;
    }

    private ClusterConfig buildNormalClusterConfig(DalConnectionStringConfigure configure) {
        ClusterConfigWithNoVersion clusterConfig =
                new ClusterConfigWithNoVersion(configure.getName(), ClusterType.NORMAL, DatabaseCategory.MYSQL);
        DatabaseShardConfigImpl databaseShardConfig = new DatabaseShardConfigImpl(clusterConfig, 0);
        DatabaseConfigImpl databaseConfig = new DatabaseConfigImpl(databaseShardConfig);
        HostAndPort hostAndPort = ConnectionStringParser.parseHostPortFromURL(configure.getConnectionUrl());
        databaseConfig.setIp(hostAndPort.getHost());
        databaseConfig.setPort(hostAndPort.getPort());
        databaseConfig.setDbName(provider.getDbName());
        databaseConfig.setUid(configure.getUserName());
        databaseConfig.setPwd(configure.getPassword());
        databaseShardConfig.addDatabaseConfig(databaseConfig);
        clusterConfig.addDatabaseShardConfig(databaseShardConfig);
        return clusterConfig;
    }

    @Override
    public String getClusterName() {
        return clusterConfigRef.get().getClusterName();
    }

    @Override
    public boolean checkSwitchable(ClusterConfig newConfig) {
        return false;
    }

    @Override
    public Cluster generate() {
        return clusterConfigRef.get().generate();
    }

    private static class ClusterConfigWithNoVersion extends ClusterConfigImpl {
        public ClusterConfigWithNoVersion(String clusterName, DatabaseCategory databaseCategory) {
            super(clusterName, databaseCategory, 0);
        }

        public ClusterConfigWithNoVersion(String clusterName, ClusterType clusterType,
                                          DatabaseCategory databaseCategory) {
            super(clusterName, clusterType, databaseCategory, 0);
        }

        @Override
        public boolean checkSwitchable(ClusterConfig newConfig) {
            return true;
        }
    }

}
