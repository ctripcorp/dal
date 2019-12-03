package com.ctrip.datasource.cluster;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.cluster.DynamicCluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.platform.dal.dao.configure.ClusterDatabaseSet;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.LocalClusterConfigProvider;
import com.ctrip.platform.dal.dao.datasource.*;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClusterSwitchTest {

    private static final String CLUSTER_NAME1 = "cluster_config_1";
    private static final String CLUSTER_NAME2 = "cluster_config_2";
    private static final String CLUSTER_NAME3 = "cluster_config_3";

    private LocalClusterConfigProvider clusterConfigProvider = new LocalClusterConfigProvider();

    @Test
    public void testClusterSwitch() throws Exception {
        MockClusterConfig config = new MockClusterConfig(getClusterConfig(CLUSTER_NAME1));
        DynamicCluster cluster = new DynamicCluster(config);
        DefaultDalConnectionLocator locator = new DefaultDalConnectionLocator();
        Map<String, String> properties = new HashMap<>();
        properties.put(DefaultDalConnectionLocator.DATASOURCE_CONFIG_PROVIDER, TitanProvider.class.getName());
        locator.initialize(properties);
        locator.setup(new HashSet<>());
        new ClusterDatabaseSet(CLUSTER_NAME1, cluster, locator);

        TitanProvider provider = new TitanProvider();
        provider.setup(new HashSet<>());
        DataSourceLocator dsLocator = new DataSourceLocator(provider);
        Set<DataSource> dsSet = new HashSet<>();

        List<Database> prevDatabases = cluster.getDatabases();
        for (Database db : prevDatabases)
            Assert.assertTrue(dsSet.add(dsLocator.getDataSource(new ClusterDataSourceIdentity(db))));

        List<Database> prevDatabases2 = cluster.getDatabases();
        for (Database db : prevDatabases2)
            Assert.assertFalse(dsSet.add(dsLocator.getDataSource(new ClusterDataSourceIdentity(db))));

        config.doSwitch(getClusterConfig(CLUSTER_NAME2));
        cluster.doSwitch(config);

        List<Database> currDatabases = cluster.getDatabases();
        for (Database db : currDatabases) {
            DataSource ds = dsLocator.getDataSource(new ClusterDataSourceIdentity(db));
            Assert.assertTrue(dsSet.add(ds));
            Assert.assertEquals(1, ((RefreshableDataSource) ds).getSingleDataSource().getReferenceCount());
        }

        for (Database db : prevDatabases) {
            DataSource ds = dsLocator.getDataSource(new ClusterDataSourceIdentity(db));
            Assert.assertTrue(dsSet.add(ds));
            Assert.assertEquals(db.isMaster() ? 2 : 1, ((RefreshableDataSource) ds).getSingleDataSource().getReferenceCount());
        }
    }

    @Test
    public void testClusterDynamicDataSourceSwitch() {
        int shardIndex = 0;
        ClusterInfo clusterInfo = new ClusterInfo(CLUSTER_NAME1, shardIndex, DatabaseRole.MASTER);
        MockClusterConfig config = new MockClusterConfig(getClusterConfig(CLUSTER_NAME1));
        DynamicCluster cluster = new DynamicCluster(config);
        TitanProvider provider = new TitanProvider();
        provider.setup(new HashSet<>());
        ClusterDynamicDataSource dataSource = new ClusterDynamicDataSource(clusterInfo, cluster, provider);

        Assert.assertEquals(dataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl(),
                cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());
        System.out.println("connStr before: " + cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());

        config.doSwitch(getClusterConfig(CLUSTER_NAME3));
        cluster.doSwitch(config);

        Assert.assertEquals(dataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl(),
                cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());
        System.out.println("connStr after: " + cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());
    }

    private ClusterConfig getClusterConfig(String clusterName) {
        return clusterConfigProvider.getClusterConfig(clusterName);
    }

    private static class MockClusterConfig implements ClusterConfig {

        private AtomicReference<ClusterConfig> configRef = new AtomicReference<>();

        public MockClusterConfig(ClusterConfig config) {
            configRef.set(config);
        }

        public void doSwitch(ClusterConfig config) {
            configRef.getAndSet(config);
        }

        @Override
        public boolean checkSwitchable(ClusterConfig newConfig) {
            return configRef.get().checkSwitchable(newConfig);
        }

        @Override
        public Cluster generate() {
            return configRef.get().generate();
        }

        @Override
        public void addListener(Listener<ClusterConfig> listener) {
            configRef.get().addListener(listener);
        }

    }

}
