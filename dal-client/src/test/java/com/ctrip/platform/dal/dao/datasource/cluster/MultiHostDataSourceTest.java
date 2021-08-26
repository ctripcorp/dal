package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author c7ch23en
 */
public class MultiHostDataSourceTest {

    private final ExecutorService executor = Executors.newFixedThreadPool(16);

    @Test
    public void testNormal() throws Exception {
        MultiHostDataSource dataSource = new MockMultiHostDataSource(mockDataSourceConfigs(),
                mockClusterProperties(), "zone1");
        HostSpec expectedHost = HostSpec.of("10.32.20.125", 3306);
        testNormal(dataSource, expectedHost);
    }

    @Test
    public void testFailover() throws Exception {
    }

    private void testNormal(DataSource dataSource, HostSpec expectedHost) throws Exception {
        CountDownLatch latch = new CountDownLatch(1000);
        AtomicInteger failures = new AtomicInteger(0);
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                try (Connection connection = dataSource.getConnection()) {
                    String url = connection.getMetaData().getURL();
                    if (!url.contains(expectedHost.host()) || !url.contains(String.valueOf(expectedHost.port())))
                        failures.incrementAndGet();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        Assert.assertEquals(0, failures.get());
    }

    private Map<HostSpec, DataSourceConfigure> mockDataSourceConfigs() {
        Map<HostSpec, DataSourceConfigure> dataSourceConfigs = new HashMap<>();
        HostSpec host1 = HostSpec.of("10.32.20.125", 3306, "zone1");
        dataSourceConfigs.put(host1, mockDataSourceConfig(host1));
        HostSpec host2 = HostSpec.of("dst56614", 3306, "zone2");
        dataSourceConfigs.put(host2, mockDataSourceConfig(host2));
        HostSpec host3 = HostSpec.of("10.32.20.5", 3308, "zone3");
        dataSourceConfigs.put(host3, mockDataSourceConfig(host3));
        return dataSourceConfigs;
    }

    private DataSourceConfigure mockDataSourceConfig(HostSpec host) {
        DataSourceConfigure config = new DataSourceConfigure(host.toString());
        config.setDriverClass("com.mysql.jdbc.Driver");
        config.setConnectionUrl(String.format("jdbc:mysql://%s:%d/Shard_0", host.host(), host.port()));
        config.setUserName("root");
        config.setPassword("!QAZ@WSX1qaz2wsx");
        return config;
    }

    private MultiHostClusterProperties mockClusterProperties() {
        return new MultiHostClusterProperties() {
            @Override
            public RouteStrategy getRouteStrategy() {
                return null;
            }

            @Override
            public String routeStrategyName() {
                return null;
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                return null;
            }
        };
    }

}
