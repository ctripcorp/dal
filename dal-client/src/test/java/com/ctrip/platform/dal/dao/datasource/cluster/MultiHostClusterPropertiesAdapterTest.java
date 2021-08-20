package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.LocalizedAccessStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.MultiHostStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.OrderedAccessStrategy;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.ctrip.framework.dal.cluster.client.cluster.ClusterType.MGR;
import static com.ctrip.framework.dal.cluster.client.cluster.ClusterType.OB;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public class MultiHostClusterPropertiesAdapterTest {

    public static final String CUSTOM_STRATEGY = "CustomDefineStrategy";

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
    public void getMultiHostStrategy() {
        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(mgrRouteStrategyConfig, MGR);
        String routeStrategy = clusterPropertiesAdapter.routeStrategyName();
        Assert.assertEquals(MGR.defaultRouteStrategies(), routeStrategy);
        MultiHostStrategy multiHostStrategy = clusterPropertiesAdapter.getMultiHostStrategy();
        Assert.assertTrue(multiHostStrategy instanceof OrderedAccessStrategy);

        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(obRouteStrategyConfig, OB);
        routeStrategy = clusterPropertiesAdapter.routeStrategyName();
        Assert.assertEquals(OB.defaultRouteStrategies(), routeStrategy);
        multiHostStrategy = clusterPropertiesAdapter.getMultiHostStrategy();
        Assert.assertTrue(multiHostStrategy instanceof LocalizedAccessStrategy);

        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(mgrRouteStrategyConfig, MGR);

    }

    @Test(expected = DalRuntimeException.class)
    public void getMultiHostStrategyWithException() {
        clusterPropertiesAdapter = new MultiHostClusterPropertiesAdapter(customRouteStrategyConfig, MGR);
        String routeStrategy = clusterPropertiesAdapter.routeStrategyName();
        Assert.assertEquals(CUSTOM_STRATEGY, routeStrategy);
        clusterPropertiesAdapter.getMultiHostStrategy();
    }
}