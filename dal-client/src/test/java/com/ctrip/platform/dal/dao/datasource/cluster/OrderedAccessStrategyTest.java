package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OrderedAccessStrategyTest {

    private long failOverTime = 1000;
    private long blackListTimeOut = 1000;
    private long fixedValidatePeriod = 3000;
    private HostSpec hostSpec1 = HostSpec.of("local", 3306);
    private HostSpec hostSpec2 = HostSpec.of("local", 3307);
    private HostSpec hostSpec3 = HostSpec.of("local", 3308);
    Set<HostSpec> configuredHost = new HashSet<>();
    List<HostSpec> orderedHosts = new ArrayList<>();

    {
        configuredHost.add(hostSpec1);
        configuredHost.add(hostSpec2);
        configuredHost.add(hostSpec3);
        orderedHosts.add(hostSpec1);
        orderedHosts.add(hostSpec2);
        orderedHosts.add(hostSpec3);

    }

    @Test
    public void pickConnectionTest() {

    }

    @Test
    public void initializeTest() {
        OrderedAccessStrategy strategy = new OrderedAccessStrategy();
        try {
            strategy.getConnectionValidator();
        } catch (Exception e) {
            Assert.assertEquals("OrderedAccessStrategy is not ready, status: birth", e.getMessage());
        }

        try {
            strategy.destroy();
        } catch (Exception e) {
            Assert.assertEquals("OrderedAccessStrategy is not ready, status: birth", e.getMessage());
        }
    }

    private ShardMeta initShardMeta() {
        Set<HostSpec> hostSpecList = new HashSet<>();
        hostSpecList.add(HostSpec.of("local", 3306));
        hostSpecList.add(HostSpec.of("local", 3307));
        hostSpecList.add(HostSpec.of("local", 3308));

        ShardMeta shardMeta = new ShardMeta(){
            @Override
            public String clusterName() {
                return "LocalTest_dalCluster";
            }

            @Override
            public int shardIndex() {
                return 0;
            }

            @Override
            public Set<HostSpec> configuredHosts() {
                return hostSpecList;
            }
        };

        return shardMeta;
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
                return "OrderedAccessStrategy";
            }

            @Override
            public CaseInsensitiveProperties routeStrategyProperties() {
                Properties properties = new Properties();
                properties.put("failoverTimeMS", "10000");
                properties.put("blacklistTimeoutMS", "10000");
                properties.put("fixedValidatePeriodMS", "30000");

                properties.put("ZonesPriority", "zone1,zone2,zone3");
                return new CaseInsensitiveProperties(properties);
            }
        };
    }

}