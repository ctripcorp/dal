package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrderedAccessStrategyTest {

    private final ExecutorService executor = Executors.newFixedThreadPool(16);

    @Test
    public void testNormal() throws Exception {
        MultiHostDataSource dataSource = new MockMultiHostDataSource(mockDataSourceConfigs(),
                mockClusterProperties(), "zone1");
        while (true) {
            executor.submit(() -> {
                try (Connection connection = dataSource.getConnection()) {
                    System.out.println("return " + connection.getMetaData().getURL() + " time is :" + new Date().toString());
                } catch (SQLException e) {
                    System.out.println("error time is :" + new Date().toString());
                    e.printStackTrace();
                }
            });
            TimeUnit.MILLISECONDS.sleep(20);
        }
    }

    @Test
    public void testFailover() throws Exception {
    }

    private Map<HostSpec, DataSourceConfigure> mockDataSourceConfigs() {
        Map<HostSpec, DataSourceConfigure> dataSourceConfigs = new HashMap<>();
        HostSpec host1 = HostSpec.of("10.32.20.12", 3306, "zone1");
        dataSourceConfigs.put(host1, mockDataSourceConfig(host1));
        HostSpec host2 = HostSpec.of("10.32.20.12", 3307, "zone2");
        dataSourceConfigs.put(host2, mockDataSourceConfig(host2));
        HostSpec host3 = HostSpec.of("10.32.20.12", 3308, "zone3");
        dataSourceConfigs.put(host3, mockDataSourceConfig(host3));
        return dataSourceConfigs;
    }

    private DataSourceConfigure mockDataSourceConfig(HostSpec host) {
        DataSourceConfigure config = new DataSourceConfigure(host.toString());
        config.setDriverClass("com.mysql.jdbc.Driver");
        config.setConnectionUrl(String.format("jdbc:mysql://%s:%d/mytest", host.host(), host.port()));
        config.setUserName("rpl_user");
        config.setPassword("123456");
        config.setProperty(DataSourceConfigureConstants.VALIDATIONINTERVAL, "30000");
        return config;
    }

    private MultiHostClusterProperties mockClusterProperties() {
        return new MultiHostClusterProperties() {
            @Override
            public String routeStrategyName() {
                return "orderedAccess";
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                Properties properties = new Properties();
                properties.put("FailoverTimeMS", 10000L);
                properties.put("BlacklistTimeoutMS", 10000L);
                List<String> zoneOrder = new ArrayList<>();
                zoneOrder.add("zone1");
                zoneOrder.add("zone2");
                zoneOrder.add("zone3");
                properties.put("ZonesPriority", zoneOrder);
                return new CaseInsensitiveProperties(properties);
            }
        };
    }

}