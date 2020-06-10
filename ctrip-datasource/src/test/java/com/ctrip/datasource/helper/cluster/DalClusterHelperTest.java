package com.ctrip.datasource.helper.cluster;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class DalClusterHelperTest {

//    @Test
    public void test() {
        Assert.assertFalse(DalClusterHelper.tryGetUcsStrategyId("cluster_config_1").isPresent());
        Assert.assertTrue(DalClusterHelper.tryGetUcsStrategyId("cluster_config_drc").isPresent());
        Assert.assertEquals(1, DalClusterHelper.tryGetUcsStrategyId("cluster_config_drc").get().intValue());
        Assert.assertTrue(DalClusterHelper.tryGetUcsStrategyId("cluster_config_sharding").isPresent());
        Assert.assertEquals(3, DalClusterHelper.tryGetUcsStrategyId("cluster_config_sharding").get().intValue());
    }

    @Test
    public void testFat() {
        Assert.assertFalse(DalClusterHelper.tryGetUcsStrategyId("test_cluster_1").isPresent());
        Assert.assertTrue(DalClusterHelper.tryGetUcsStrategyId("dal_sharding_cluster").isPresent());
        Assert.assertEquals(1, DalClusterHelper.tryGetUcsStrategyId("dal_sharding_cluster").get().intValue());
    }

}
