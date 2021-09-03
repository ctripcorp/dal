package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.cluster.ClusterType;
import com.ctrip.platform.dal.cluster.cluster.RouteStrategyEnum;
import com.ctrip.platform.dal.cluster.database.Database;
import com.ctrip.platform.dal.cluster.database.DatabaseCategory;
import com.ctrip.platform.dal.cluster.database.DatabaseRole;
import com.ctrip.platform.dal.cluster.multihost.ClusterRouteStrategyConfig;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.MultiMasterStrategy;
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
        Assert.assertEquals(ClusterType.NORMAL, cluster.getClusterType());
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
        Assert.assertEquals(RouteStrategyEnum.WRITE_ORDERED.getAlias(), routeStrategy.routeStrategyName());
        CaseInsensitiveProperties properties = routeStrategy.routeStrategyProperties();
        Assert.assertEquals("z3,z2,z1", properties.get(MultiMasterStrategy.ZONES_PRIORITY));
        Assert.assertEquals("10000", properties.get(MultiMasterStrategy.FAILOVER_TIME_MS));
        Assert.assertNull(properties.get(MultiMasterStrategy.BLACKLIST_TIMEOUT_MS));
        Assert.assertNull(properties.get(MultiMasterStrategy.FIXED_VALIDATE_PERIOD_MS));
    }

}
