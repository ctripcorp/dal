package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLConstants;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author c7ch23en
 */
public class ApiDataSourceIdentityTest {

    static final String DB_NAME = "test";
    final MockConnectionStringConfigureProvider provider = new MockConnectionStringConfigureProvider(DB_NAME);

    @Test
    public void testNormal() {
        provider.setNormalConfig(true);
        ApiDataSourceIdentity id = new ApiDataSourceIdentity(provider);
        ClusterInfo clusterInfo = id.getClusterInfo();
        Assert.assertEquals(DB_NAME, clusterInfo.getClusterName());
        Assert.assertEquals(0, clusterInfo.getShardIndex().intValue());
        Assert.assertEquals(DatabaseRole.MASTER, clusterInfo.getRole());
        Cluster cluster = clusterInfo.getCluster();
        Assert.assertEquals(DB_NAME, cluster.getClusterName());
        Assert.assertEquals(ClusterType.NORMAL, cluster.getClusterType());
        Assert.assertEquals(DatabaseCategory.MYSQL, cluster.getDatabaseCategory());
        Assert.assertFalse(cluster.dbShardingEnabled());
        Assert.assertEquals(1, cluster.getDatabases().size());
        Database database = cluster.getDatabases().iterator().next();
        Assert.assertEquals("localhost", database.getConnectionString().getPrimaryHost());
        Assert.assertEquals(3306, database.getConnectionString().getPrimaryPort());
        ClusterRouteStrategyConfig routeStrategy = cluster.getRouteStrategyConfig();
        Assert.assertNotNull(routeStrategy);
    }

    @Test
    public void testMultiHost() {
        provider.setNormalConfig(false);
        ApiDataSourceIdentity id = new ApiDataSourceIdentity(provider);
        ClusterInfo clusterInfo = id.getClusterInfo();
        Assert.assertEquals(DB_NAME, clusterInfo.getClusterName());
        Assert.assertEquals(0, clusterInfo.getShardIndex().intValue());
        Assert.assertEquals(DatabaseRole.MASTER, clusterInfo.getRole());
        Cluster cluster = clusterInfo.getCluster();
        Assert.assertEquals(DB_NAME, cluster.getClusterName());
        Assert.assertEquals(ClusterType.MGR, cluster.getClusterType());
        Assert.assertEquals(DatabaseCategory.MYSQL, cluster.getDatabaseCategory());
        Assert.assertFalse(cluster.dbShardingEnabled());
        Assert.assertEquals(3, cluster.getDatabases().size());
        Set<String> hosts = cluster.getDatabases()
                .stream()
                .map(database -> database.getConnectionString().getPrimaryHost())
                .collect(Collectors.toSet());
        Assert.assertEquals(1, hosts.size());
        Assert.assertTrue(hosts.contains("localhost"));
        Set<Integer> ports = cluster.getDatabases()
                .stream()
                .map(database -> database.getConnectionString().getPrimaryPort())
                .collect(Collectors.toSet());
        Assert.assertEquals(3, ports.size());
        Assert.assertTrue(ports.contains(3306));
        Assert.assertTrue(ports.contains(3307));
        Assert.assertTrue(ports.contains(3308));
        Set<String> zones = cluster.getDatabases()
                .stream()
                .map(database -> database.getZone().toLowerCase())
                .collect(Collectors.toSet());
        Assert.assertEquals(3, zones.size());
        Assert.assertTrue(zones.contains("z1"));
        Assert.assertTrue(zones.contains("z2"));
        Assert.assertTrue(zones.contains("z3"));
        ClusterRouteStrategyConfig routeStrategy = cluster.getRouteStrategyConfig();
        Assert.assertEquals(ClusterConfigXMLConstants.ORDERED_ACCESS_STRATEGY, routeStrategy.routeStrategyName());
        CaseInsensitiveProperties properties = routeStrategy.routeStrategyProperties();
        Assert.assertEquals("z3,z2,z1", properties.get(DataSourceConfigureConstants.ZONES_PRIORITY));
        Assert.assertEquals("10000", properties.get(DataSourceConfigureConstants.FAILOVER_TIME_MS));
        Assert.assertNull(properties.get(DataSourceConfigureConstants.BLACKLIST_TIMEOUT_MS));
        Assert.assertNull(properties.get(DataSourceConfigureConstants.FIXED_VALIDATE_PERIOD_MS));
    }

}
