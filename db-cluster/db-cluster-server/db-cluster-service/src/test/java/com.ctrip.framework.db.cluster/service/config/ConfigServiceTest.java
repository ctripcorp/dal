package com.ctrip.framework.db.cluster.service.config;

import com.ctrip.framework.db.cluster.util.Constants;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by @author zhuYongMing on 2019/11/10.
 */
public class ConfigServiceTest {

    @Test
    public void string2SetTest() {
        ConfigService configService = new ConfigService();
        final List<String> strings = new ArrayList<>(configService.string2Set("10.1.1.1, 10.1.1.1, 10.1.1.2 "));
        Assert.assertEquals(2, strings.size());
        Assert.assertEquals("10.1.1.2", strings.get(0));
        Assert.assertEquals("10.1.1.1", strings.get(1));
    }

    @Test
    public void convertClusterFreshnessThresholdSecondTest() {
        ConfigService configService = new ConfigService();
        final Map<String, Integer> clusterThresholdMap = configService.convertClusterFreshnessThresholdSecond(": ,,:, cluster1: 5,cluster2: 10 ,cluster3");
        Assert.assertEquals(5, (int) clusterThresholdMap.get("cluster1"));
        Assert.assertEquals(10, (int) clusterThresholdMap.get("cluster2"));
    }
}
