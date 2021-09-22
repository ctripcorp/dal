package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.mgr.MGRStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob.OBStrategy;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
            public RouteStrategy generate() {
                return doGenerate(routeStrategyName());
            }

            @Override
            public String routeStrategyName() {
                return RouteStrategyEnum.WRITE_ORDERED.name();
            }

            @Override
            public boolean multiMaster() {
                return true;
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                return null;
            }
        };

        obRouteStrategyConfig = new ClusterRouteStrategyConfig() {
            @Override
            public RouteStrategy generate() {
                return doGenerate(routeStrategyName());
            }

            @Override
            public String routeStrategyName() {
                return RouteStrategyEnum.WRITE_CURRENT_ZONE_FIRST.getAlias();
            }

            @Override
            public boolean multiMaster() {
                return true;
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                return null;
            }
        };

        customRouteStrategyConfig = new ClusterRouteStrategyConfig() {
            @Override
            public RouteStrategy generate() {
                return doGenerate(routeStrategyName());
            }

            @Override
            public String routeStrategyName() {
                return CUSTOM_STRATEGY;
            }

            @Override
            public boolean multiMaster() {
                return false;
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                return null;
            }
        };
    }

    @Test
    public void getRouteStrategy() {
        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(mgrRouteStrategyConfig, CLUSTER_NAME);
        String routeStrategy = clusterPropertiesAdapter.routeStrategyName();
        Assert.assertEquals(RouteStrategyEnum.WRITE_ORDERED.name(), routeStrategy);
        RouteStrategy multiHostStrategy = clusterPropertiesAdapter.generate();
        Assert.assertTrue(multiHostStrategy instanceof MGRStrategy);

        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(obRouteStrategyConfig, CLUSTER_NAME);
        routeStrategy = clusterPropertiesAdapter.routeStrategyName();
        Assert.assertEquals(RouteStrategyEnum.WRITE_CURRENT_ZONE_FIRST.getAlias(), routeStrategy);
        multiHostStrategy = clusterPropertiesAdapter.generate();
        Assert.assertTrue(multiHostStrategy instanceof OBStrategy);

        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(mgrRouteStrategyConfig, CLUSTER_NAME);

    }

    @Test(expected = DalRuntimeException.class)
    public void getRouteStrategyWithException() {
        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(customRouteStrategyConfig, CLUSTER_NAME);
        String routeStrategy = clusterPropertiesAdapter.routeStrategyName();
        Assert.assertEquals(CUSTOM_STRATEGY, routeStrategy);
        clusterPropertiesAdapter.generate();
    }

    private RouteStrategy doGenerate(String strategyName) {
        String clazz = RouteStrategyEnum.parse(strategyName);
        try {
            return  (com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy)Class.forName(clazz).newInstance();
        } catch (Throwable t) {
            String msg = "Error constructing route strategy: " + strategyName;
            throw new DalRuntimeException(msg, t);
        }
    }
}