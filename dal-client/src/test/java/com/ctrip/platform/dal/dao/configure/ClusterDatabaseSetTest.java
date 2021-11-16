package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigProvider;
import com.ctrip.framework.dal.cluster.client.config.DefaultLocalConfigProvider;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class ClusterDatabaseSetTest {

    @Test
    public void testNonShardingCluster() {
        ClusterConfigProvider provider = new DefaultLocalConfigProvider("NonShardingCluster");
        // todo-lhj
        ClusterConfig config = provider.getClusterConfig(new DefaultDalConfigCustomizedOption());

        Cluster cluster = config.generate();
        ClusterDatabaseSet databaseSet = new ClusterDatabaseSet("NonShardingCluster", cluster, new DalConnectionLocator() {
            @Override
            public void setup(Collection<DatabaseSet> databaseSets) {
            }

            @Override
            public Connection getConnection(String name) throws Exception {
                return null;
            }

            @Override
            public Connection getConnection(String name, ConnectionAction action) throws Exception {
                return null;
            }

            @Override
            public Connection getConnection(DataSourceIdentity id) throws Exception {
                return null;
            }

            @Override
            public Connection getConnection(DataSourceIdentity id, ConnectionAction action) throws Exception {
                return null;
            }

            @Override
            public IntegratedConfigProvider getIntegratedConfigProvider() {
                return null;
            }

            @Override
            public void setupCluster(Cluster cluster) {
            }

            @Override
            public void uninstallCluster(Cluster cluster) {
            }

            @Override
            public void initialize(Map<String, String> settings) throws Exception {
            }
        });
        Assert.assertFalse(databaseSet.isShardingSupported());
        Assert.assertEquals(1, databaseSet.getMasterDbs().size());
        Assert.assertEquals(0, databaseSet.getSlaveDbs().size());
    }

}
