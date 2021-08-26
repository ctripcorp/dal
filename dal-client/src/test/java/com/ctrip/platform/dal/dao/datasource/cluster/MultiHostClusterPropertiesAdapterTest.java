package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.cluster.multihost.ClusterRouteStrategyConfig;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.mgr.MGRStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.OBStrategy;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.ctrip.platform.dal.cluster.cluster.ClusterType.MGR;
import static com.ctrip.platform.dal.cluster.cluster.ClusterType.OB;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public class MultiHostClusterPropertiesAdapterTest {

    private static final String CUSTOM_STRATEGY = "CustomDefineStrategy";

    private static final String CLUSTER_NAME = "test_cluster_name";

    private MultiHostClusterPropertiesAdapter clusterPropertiesAdapter;

    private ClusterRouteStrategyConfig mgrRouteStrategyConfig;

    private ClusterRouteStrategyConfig obRouteStrategyConfig;

    private ClusterRouteStrategyConfig customRouteStrategyConfig;

    @Before
    public void setUp() throws Exception {
        mgrRouteStrategyConfig = new ClusterRouteStrategyConfig() {
            @Override
            public String routeStrategyName() {
                return MGR.defaultRouteStrategies();
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                return null;
            }
        };

        obRouteStrategyConfig = new ClusterRouteStrategyConfig() {
            @Override
            public String routeStrategyName() {
                return OB.defaultRouteStrategies();
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                return null;
            }
        };

        customRouteStrategyConfig = new ClusterRouteStrategyConfig() {
            @Override
            public String routeStrategyName() {
                return CUSTOM_STRATEGY;
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                return null;
            }
        };
    }

    @Test
    public void getRouteStrategy() {
        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(mgrRouteStrategyConfig, MGR, CLUSTER_NAME);
        String routeStrategy = clusterPropertiesAdapter.routeStrategyName();
        Assert.assertEquals(MGR.defaultRouteStrategies(), routeStrategy);
        RouteStrategy multiHostStrategy = clusterPropertiesAdapter.getRouteStrategy();
        Assert.assertTrue(multiHostStrategy instanceof MGRStrategy);

        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(obRouteStrategyConfig, OB, CLUSTER_NAME);
        routeStrategy = clusterPropertiesAdapter.routeStrategyName();
        Assert.assertEquals(OB.defaultRouteStrategies(), routeStrategy);
        multiHostStrategy = clusterPropertiesAdapter.getRouteStrategy();
        Assert.assertTrue(multiHostStrategy instanceof OBStrategy);

        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(mgrRouteStrategyConfig, MGR, CLUSTER_NAME);

    }

    @Test(expected = DalRuntimeException.class)
    public void getRouteStrategyWithException() {
        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(customRouteStrategyConfig, MGR, CLUSTER_NAME);
        String routeStrategy = clusterPropertiesAdapter.routeStrategyName();
        Assert.assertEquals(CUSTOM_STRATEGY, routeStrategy);
        clusterPropertiesAdapter.getRouteStrategy();
    }
}