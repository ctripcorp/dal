package com.ctrip.datasource.cluster;

import com.ctrip.datasource.datasource.CtripLocalizationValidatorFactory;
import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.ucs.client.api.RequestContext;
import com.ctrip.framework.ucs.client.api.UcsClient;
import com.ctrip.platform.dal.dao.cluster.DynamicCluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.ucs.client.api.StrategyValidatedResult;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DefaultDalPropertiesLocator;
import com.ctrip.platform.dal.dao.datasource.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClusterSwitchTest {

    private static final String CLUSTER_NAME1 = "cluster_config_1";
    private static final String CLUSTER_NAME2 = "cluster_config_2";
    private static final String CLUSTER_NAME3 = "cluster_config_3";
    private static final String CLUSTER_NAME4 = "cluster_config_normal";
    private static final String CLUSTER_NAME5 = "cluster_config_drc";
    private static final String CLUSTER_NAME6 = "cluster_config_drc_2";

    private LocalClusterConfigProvider clusterConfigProvider = new LocalClusterConfigProvider();

    @Before
    public void beforeTest() {
        DataSourceCreator.getInstance().closeAllDataSources();
    }

    @Test
    public void testClusterSwitch() throws Exception {
        MockClusterConfig config = new MockClusterConfig(getClusterConfig(CLUSTER_NAME1));
        DynamicCluster cluster = new DynamicCluster(config);
        DefaultDalConnectionLocator locator = new DefaultDalConnectionLocator();
        Map<String, String> properties = new HashMap<>();
        properties.put(DefaultDalConnectionLocator.DATASOURCE_CONFIG_PROVIDER, TitanProvider.class.getName());
        locator.initialize(properties);
        locator.setup(new HashSet<>());
        new ClusterDatabaseSet(CLUSTER_NAME1, cluster, locator);

        TitanProvider provider = new TitanProvider();
        provider.initialize(new HashMap<>());
        provider.setup(new HashSet<>());
        DataSourceLocator dsLocator = new DataSourceLocator(provider);
        Set<DataSource> dsSet = new HashSet<>();

        List<Database> prevDatabases = cluster.getDatabases();
        for (Database db : prevDatabases)
            Assert.assertTrue(dsSet.add(dsLocator.getDataSource(new ClusterDataSourceIdentity(db))));

        List<Database> prevDatabases2 = cluster.getDatabases();
        for (Database db : prevDatabases2)
            Assert.assertFalse(dsSet.add(dsLocator.getDataSource(new ClusterDataSourceIdentity(db))));

        config.doSwitch(getClusterConfig(CLUSTER_NAME2));
        cluster.doSwitch(config);

        List<Database> currDatabases = cluster.getDatabases();
        for (Database db : currDatabases) {
            DataSource ds = dsLocator.getDataSource(new ClusterDataSourceIdentity(db));
            Assert.assertTrue(dsSet.add(ds));
            Assert.assertEquals(1, ((RefreshableDataSource) ds).getSingleDataSource().getReferenceCount());
        }

        for (Database db : prevDatabases) {
            DataSource ds = dsLocator.getDataSource(new ClusterDataSourceIdentity(db));
            Assert.assertTrue(dsSet.add(ds));
            Assert.assertEquals(db.isMaster() ? 2 : 1, ((RefreshableDataSource) ds).getSingleDataSource().getReferenceCount());
        }

        // switch from normal to drc
        config.doSwitch(getClusterConfig(CLUSTER_NAME6));
        cluster.doSwitch(config);
        currDatabases = cluster.getDatabases();
        for (Database db : currDatabases) {
            DataSource ds = dsLocator.getDataSource(new ClusterDataSourceIdentity(db));
            Assert.assertTrue(ds instanceof LocalizedDataSource);
        }

        // switch from drc to normal
        config.doSwitch(getClusterConfig(CLUSTER_NAME1));
        cluster.doSwitch(config);
        currDatabases = cluster.getDatabases();
        for (Database db : currDatabases) {
            DataSource ds = dsLocator.getDataSource(new ClusterDataSourceIdentity(db));
            Assert.assertFalse(ds instanceof LocalizedDataSource);
        }
    }

    @Test
    public void testClusterDynamicDataSourceSwitch() throws Exception {
        int shardIndex = 0;
        ClusterInfo clusterInfo = new ClusterInfo(CLUSTER_NAME1, shardIndex, DatabaseRole.MASTER, false);
        MockClusterConfig config = new MockClusterConfig(getClusterConfig(CLUSTER_NAME1));
        DynamicCluster cluster = new DynamicCluster(config);
        TitanProvider provider = new TitanProvider();
        provider.initialize(new HashMap<>());
        provider.setup(new HashSet<>());
        ClusterDynamicDataSource dataSource = new ClusterDynamicDataSource(clusterInfo, cluster, provider, new DefaultLocalizationValidatorFactory());

        Assert.assertEquals(dataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl(),
                cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());
        System.out.println("connStr before: " + cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());

        config.doSwitch(getClusterConfig(CLUSTER_NAME3));
        cluster.doSwitch(config);

        Assert.assertEquals(dataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl(),
                cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());
        System.out.println("connStr after: " + cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());
    }

    @Test
    public void testClusterDynamicDataSourceDrcSwitch() throws Exception {
        int shardIndex = 0;
        ClusterInfo clusterInfo = new ClusterInfo(CLUSTER_NAME4, shardIndex, DatabaseRole.MASTER, false);
        MockClusterConfig config = new MockClusterConfig(getClusterConfig(CLUSTER_NAME4));
        DynamicCluster cluster = new DynamicCluster(config);
        TitanProvider provider = new TitanProvider();
        provider.initialize(new HashMap<>());
        provider.setup(new HashSet<>());
        DalPropertiesLocator locator = new DefaultDalPropertiesLocator();
        ClusterDynamicDataSource dataSource = new ClusterDynamicDataSource(clusterInfo, cluster, provider, new CtripLocalizationValidatorFactory(new UcsClient() {
            @Override
            public RequestContext getCurrentRequestContext() {
                return new RequestContext() {
                    @Override
                    public StrategyValidatedResult validate(int expectStrategyId) {
                        return StrategyValidatedResult.ShardBlock;
                    }
                };
            }
        }, locator));

        Assert.assertEquals(dataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl(),
                cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());
        System.out.println("connStr before: " + cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());

        LocalizationUtils.testStatementPassed(dataSource);
        LocalizationUtils.testPreparedStatementPassed(dataSource);

        locator.setProperties(buildDrcProps1());
        LocalizationUtils.testStatementPassed(dataSource);
        LocalizationUtils.testPreparedStatementPassed(dataSource);

        locator.setProperties(buildDrcProps2());
        LocalizationUtils.testStatementPassed(dataSource);
        LocalizationUtils.testPreparedStatementPassed(dataSource);

        locator.setProperties(buildDrcProps3());
        LocalizationUtils.testStatementPassed(dataSource);
        LocalizationUtils.testPreparedStatementPassed(dataSource);

        locator.setProperties(buildDrcProps4());
        LocalizationUtils.testStatementPassed(dataSource);
        LocalizationUtils.testPreparedStatementPassed(dataSource);

        config.doSwitch(getClusterConfig(CLUSTER_NAME5));
        cluster.doSwitch(config);

        Assert.assertEquals(dataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl(),
                cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());
        System.out.println("connStr after: " + cluster.getMasterOnShard(shardIndex).getConnectionString().getPrimaryConnectionUrl());

        LocalizationUtils.testStatementBlocked(dataSource);
        LocalizationUtils.testPreparedStatementBlocked(dataSource);

        locator.setProperties(buildDrcProps1());
        LocalizationUtils.testStatementPassed(dataSource);
        LocalizationUtils.testPreparedStatementPassed(dataSource);

        locator.setProperties(buildDrcProps2());
        LocalizationUtils.testStatementBlocked(dataSource);
        LocalizationUtils.testPreparedStatementBlocked(dataSource);

        locator.setProperties(buildDrcProps3());
        LocalizationUtils.testStatementPassed(dataSource);
        LocalizationUtils.testPreparedStatementPassed(dataSource);

        locator.setProperties(buildDrcProps4());
        LocalizationUtils.testStatementBlocked(dataSource);
        LocalizationUtils.testPreparedStatementBlocked(dataSource);
    }

    private ClusterConfig getClusterConfig(String clusterName) {
        return clusterConfigProvider.getClusterConfig(clusterName);
    }

    private Map<String, String> buildDrcProps1() {
        Map<String, String> props = new HashMap<>();
        props.put("DrcStage", "test");
        props.put("DrcStage.test.Localized", "false");
        return props;
    }

    private Map<String, String> buildDrcProps2() {
        Map<String, String> props = new HashMap<>();
        props.put("DrcStage", "test");
        props.put("DrcStage.test.Localized", "true");
        return props;
    }

    private Map<String, String> buildDrcProps3() {
        Map<String, String> props = new HashMap<>();
        props.put("DrcStage", "final");
        props.put("DrcStage.test.Localized", "true");
        return props;
    }

    private Map<String, String> buildDrcProps4() {
        Map<String, String> props = new HashMap<>();
        props.put("DrcStage", "final");
        props.put("DrcStage.final.Localized", "true");
        return props;
    }

    private static class MockClusterConfig implements ClusterConfig {

        private AtomicReference<ClusterConfig> configRef = new AtomicReference<>();

        public MockClusterConfig(ClusterConfig config) {
            configRef.set(config);
        }

        @Override
        public String getClusterName() {
            return configRef.get().getClusterName();
        }

        public void doSwitch(ClusterConfig config) {
            configRef.getAndSet(config);
        }

        @Override
        public boolean checkSwitchable(ClusterConfig newConfig) {
            return configRef.get().checkSwitchable(newConfig);
        }

        @Override
        public Cluster generate() {
            return configRef.get().generate();
        }

        @Override
        public void addListener(Listener<ClusterConfig> listener) {
            configRef.get().addListener(listener);
        }

    }

}
