package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.ListenableSupport;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;
import com.ctrip.framework.dal.cluster.client.sharding.context.DbShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.context.TableShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class DynamicCluster extends ListenableSupport<ClusterSwitchedEvent> implements Cluster {

    private ClusterConfig clusterConfig;
    private AtomicReference<Cluster> innerCluster = new AtomicReference<>();

    public DynamicCluster(ClusterConfig clusterConfig) {
        this.clusterConfig = clusterConfig;
        this.innerCluster.set(clusterConfig.generate());
        registerListener();
    }

    @Override
    public String getClusterName() {
        return getInnerCluster().getClusterName();
    }

    @Override
    public ClusterType getClusterType() {
        return getInnerCluster().getClusterType();
    }

    @Override
    public DatabaseCategory getDatabaseCategory() {
        return getInnerCluster().getDatabaseCategory();
    }

    @Override
    public int getDbShardCount() {
        return getInnerCluster().getDbShardCount();
    }

    @Override
    public Integer getDbShard(String tableName, DbShardContext context) {
        return getInnerCluster().getDbShard(tableName, context);
    }

    @Override
    public boolean tableShardingEnabled(String tableName) {
        return getInnerCluster().tableShardingEnabled(tableName);
    }

    @Override
    public String getTableShard(String tableName, TableShardContext context) {
        return getInnerCluster().getTableShard(tableName, context);
    }

    @Override
    public Set<String> getAllTableShards(String tableName) {
        return getInnerCluster().getAllTableShards(tableName);
    }

    @Override
    public String getTableShardSeparator(String tableName) {
        return getInnerCluster().getTableShardSeparator(tableName);
    }

    @Override
    public List<Database> getDatabases() {
        return getInnerCluster().getDatabases();
    }

    @Override
    public Database getMasterOnShard(int shardIndex) {
        return getInnerCluster().getMasterOnShard(shardIndex);
    }

    @Override
    public List<Database> getSlavesOnShard(int shardIndex) {
        return getInnerCluster().getSlavesOnShard(shardIndex);
    }

    @Override
    public ClusterIdGeneratorConfig getIdGeneratorConfig() {
        return getInnerCluster().getIdGeneratorConfig();
    }

    private void registerListener() {
        clusterConfig.addListener(new Listener<ClusterConfig>() {
            @Override
            public void onChanged(ClusterConfig current) {
                doSwitch(current);
            }
        });
    }

    public void doSwitch(ClusterConfig current) {
        Cluster curr = clusterConfig.generate();
        Cluster prev = innerCluster.getAndSet(curr);
        // TODO: TO BE REFACTORED
        ClusterSwitchedEvent event = new ClusterSwitchedEvent(curr, prev);
        for (Listener<ClusterSwitchedEvent> listener : getListeners()) {
            try {
                listener.onChanged(event);
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private Cluster getInnerCluster() {
        Cluster cluster = innerCluster.get();
        if (cluster == null)
            throw new ClusterRuntimeException("inner cluster not exists");
        return cluster;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getInnerCluster().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getInnerCluster().isWrapperFor(iface);
    }

}
