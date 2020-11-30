package com.ctrip.platform.dal.dao.datasource.log;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterSwitchedEvent;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.dal.cluster.client.config.LocalizationState;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.sharding.context.DbShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.context.TableShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class ClusterDbSqlContextTest extends BaseSqlContextTest {

    private static final String CLUSTER = "test_cluster";
    private static final int SHARD = 1;
    private static final DatabaseRole ROLE = DatabaseRole.MASTER;

    @Test
    public void testSuccess() throws Exception {
        ClusterDbSqlContext context =
                new ClusterDbSqlContext(mockCluster(), SHARD, ROLE, CLIENT_VERSION, CLIENT_ZONE, DB_NAME);
        buildContextSuccess(context);
        assertContextSuccess(context);
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(CLUSTER, tags.get(ClusterDbSqlContext.CLUSTER));
        Assert.assertEquals(String.valueOf(SHARD), tags.get(ClusterDbSqlContext.SHARD));
        Assert.assertEquals(ROLE.getValue(), tags.get(ClusterDbSqlContext.ROLE));

        ClusterDbSqlContext forked = (ClusterDbSqlContext) context.fork();
        assertFork(forked);
        tags = forked.toMetricTags();
        Assert.assertEquals(CLUSTER, tags.get(ClusterDbSqlContext.CLUSTER));
        Assert.assertEquals(String.valueOf(SHARD), tags.get(ClusterDbSqlContext.SHARD));
        Assert.assertEquals(ROLE.getValue(), tags.get(ClusterDbSqlContext.ROLE));
    }

    @Test
    public void testFail() throws Exception {
        ClusterDbSqlContext context =
                new ClusterDbSqlContext(mockCluster(), SHARD, ROLE, CLIENT_VERSION, CLIENT_ZONE, DB_NAME);
        buildContextFail(context, new RuntimeException());
        assertContextFail(context);
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(CLUSTER, tags.get(ClusterDbSqlContext.CLUSTER));
        Assert.assertEquals(String.valueOf(SHARD), tags.get(ClusterDbSqlContext.SHARD));
        Assert.assertEquals(ROLE.getValue(), tags.get(ClusterDbSqlContext.ROLE));

        ClusterDbSqlContext forked = (ClusterDbSqlContext) context.fork();
        assertFork(forked);
        tags = forked.toMetricTags();
        Assert.assertEquals(CLUSTER, tags.get(ClusterDbSqlContext.CLUSTER));
        Assert.assertEquals(String.valueOf(SHARD), tags.get(ClusterDbSqlContext.SHARD));
        Assert.assertEquals(ROLE.getValue(), tags.get(ClusterDbSqlContext.ROLE));
    }

    private Cluster mockCluster() {
        return new Cluster() {
            @Override
            public String getClusterName() {
                return CLUSTER;
            }

            @Override
            public ClusterType getClusterType() {
                return ClusterType.DRC;
            }

            @Override
            public DatabaseCategory getDatabaseCategory() {
                return DatabaseCategory.MYSQL;
            }

            @Override
            public boolean dbShardingEnabled() {
                return true;
            }

            @Override
            public Integer getDbShard(String tableName, DbShardContext context) {
                return SHARD;
            }

            @Override
            public Set<Integer> getAllDbShards() {
                return null;
            }

            @Override
            public boolean tableShardingEnabled(String tableName) {
                return false;
            }

            @Override
            public String getTableShard(String tableName, TableShardContext context) {
                return null;
            }

            @Override
            public Set<String> getAllTableShards(String tableName) {
                return null;
            }

            @Override
            public String getTableShardSeparator(String tableName) {
                return null;
            }

            @Override
            public List<Database> getDatabases() {
                return null;
            }

            @Override
            public Database getMasterOnShard(int shardIndex) {
                return null;
            }

            @Override
            public List<Database> getSlavesOnShard(int shardIndex) {
                return null;
            }

            @Override
            public ClusterIdGeneratorConfig getIdGeneratorConfig() {
                return null;
            }

            @Override
            public LocalizationConfig getLocalizationConfig() {
                return new LocalizationConfig() {
                    @Override
                    public Integer getUnitStrategyId() {
                        return 1;
                    }

                    @Override
                    public String getZoneId() {
                        return null;
                    }

                    @Override
                    public LocalizationState getLocalizationState() {
                        return LocalizationState.ACTIVE;
                    }
                };
            }

            @Override
            public void addListener(Listener<ClusterSwitchedEvent> listener) {
            }

            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                return null;
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }
        };
    }

}
