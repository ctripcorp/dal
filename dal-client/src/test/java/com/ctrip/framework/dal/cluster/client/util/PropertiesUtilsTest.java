package com.ctrip.framework.dal.cluster.client.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author limingdong
 * @create 2021/10/18
 */
public class PropertiesUtilsTest {

    String content = "dataSourceFactory=com.ctrip.platform.dal.application.clickhouse.TestCHDataSourceFactory\ndriverClassName=ru.yandex.clickhouse.ClickHouseDriver";

    @Test
    public void testToProperties() throws IOException {
        Properties properties = PropertiesUtils.toProperties(content);
        Assert.assertEquals(2, properties.size());
    }
}