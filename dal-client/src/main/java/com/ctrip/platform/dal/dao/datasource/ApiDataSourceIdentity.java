package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.util.ObjectHolder;
import com.ctrip.platform.dal.dao.cluster.ClusterConfigAdapter;
import com.ctrip.platform.dal.dao.cluster.DynamicCluster;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.log.NullSqlContext;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.Objects;

public class ApiDataSourceIdentity implements ClusterInfoDelegateIdentity {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String ID_FORMAT = "%s_api"; //dbName

    private ConnectionStringConfigureProvider provider;
    private String id;
    private final ObjectHolder<Cluster> clusterHolder = new ObjectHolder<>();

    public ApiDataSourceIdentity(ConnectionStringConfigureProvider provider) {
        this.provider = provider;
        init();
    }

    private void init() {
        Cluster cluster = getCluster();
        id = String.format(ID_FORMAT, cluster.getClusterName());
    }

    private Cluster getCluster() {
        return clusterHolder.getOrCreate(() -> {
            try {
                ClusterConfig clusterConfig = new ClusterConfigAdapter(provider);
                return new DynamicCluster(clusterConfig);
            } catch (Exception e) {
                LOGGER.error("Create cluster failed for db:" + provider.getDbName(), e);
                throw new DalRuntimeException(e);
            }
        });
    }

    public ConnectionStringConfigureProvider getProvider() {
        return provider;
    }

    @Override
    public ClusterInfo getClusterInfo() {
        Cluster cluster = getCluster();
        return new ClusterInfo(cluster.getClusterName(), 0, DatabaseRole.MASTER, false, cluster);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SqlContext createSqlContext() {
        return new NullSqlContext();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiDataSourceIdentity that = (ApiDataSourceIdentity) o;
        return Objects.equals(provider, that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider);
    }

}
