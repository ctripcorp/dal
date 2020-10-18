package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author c7ch23en
 */
public class MultiHostDataSourceTest {

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Test
    public void testNormal1() throws Exception {
        MultiHostDataSource dataSource = new MockedMultiHostDataSource(mockDataSourceConfigs(),
                mockClusterOptions("zone1", "zone2"));
        dataSource.reqCtxForTest = mockRequestContext("x");
        CountDownLatch latch = new CountDownLatch(100);
        AtomicInteger failures = new AtomicInteger(0);
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                try (Connection connection = dataSource.getConnection()) {
                    if (!connection.getMetaData().getURL().contains("10.32.20.128"))
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

    @Test
    public void testFailover() throws Exception {
    }

    private Map<HostSpec, DataSourceConfigure> mockDataSourceConfigs() {
        Map<HostSpec, DataSourceConfigure> dataSourceConfigs = new HashMap<>();
        HostSpec host1 = HostSpec.of("10.32.20.128", 3306, "zone1");
        dataSourceConfigs.put(host1, mockDataSourceConfig(host1));
        HostSpec host2 = HostSpec.of("dst56614", 3306, "zone2");
        dataSourceConfigs.put(host2, mockDataSourceConfig(host2));
//        HostSpec host3 = HostSpec.create("10.32.20.5", 3308, "zone3");
//        dataSourceConfigs.put(host3, mockDataSourceConfig(host3));
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

    private MultiHostClusterProperties mockClusterOptions(String... zoneOrder) {
        return new MultiHostClusterProperties() {
            @Override
            public String routeStrategy() {
                return null;
            }

            @Override
            public boolean isLocalAccessMode() {
                return false;
            }

            @Override
            public List<String> zoneOrder() {
                return Arrays.asList(zoneOrder);
            }

            @Override
            public long failoverTime() {
                return 3000;
            }

            @Override
            public long blacklistTimeout() {
                return 5000;
            }
        };
    }

    private RequestContext mockRequestContext(String clientZone) {
        return new RequestContext() {
            @Override
            public String clientZone() {
                return clientZone;
            }

            @Override
            public boolean isWriteOperation() {
                return false;
            }
        };
    }

}
