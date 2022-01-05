package com.ctrip.framework.dal.cluster.client.database;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.DatabaseConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.DatabaseShardConfigImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2021/10/19
 */
public class DummyDatabaseTest {

    private DummyDatabase dummyDatabase;

    private DatabaseConfigImpl databaseConfig;

    private DatabaseShardConfigImpl shardConfig;

    private ClusterConfigImpl clusterConfig;

    private static final String MASTER_DOMAIN = "test_master_domain";

    private static final String SLAVE_DOMAIN = "test_slave_domain";

    @Before
    public void setUp() throws Exception {
        clusterConfig = new ClusterConfigImpl("name", DatabaseCategory.CUSTOM, 12);
        shardConfig = new DatabaseShardConfigImpl(clusterConfig, 0);
        shardConfig.setMasterDomain(MASTER_DOMAIN);
        shardConfig.setSlaveDomain(SLAVE_DOMAIN);
        databaseConfig = new DatabaseConfigImpl(shardConfig);
        dummyDatabase = new DummyDatabase(databaseConfig);
    }

    @Test
    public void buildPrimaryConnectionUrl() {
        Assert.assertNull(dummyDatabase.buildPrimaryConnectionUrl());
    }

    @Test
    public void buildFailOverConnectionUrl() {
        Assert.assertNull(dummyDatabase.buildFailOverConnectionUrl());
    }

    @Test
    public void getDriverClassName() {
        Assert.assertNull(dummyDatabase.getDriverClassName());
    }

    @Test
    public void getPrimaryConnectionUrl() {
        Assert.assertEquals(MASTER_DOMAIN, dummyDatabase.getPrimaryConnectionUrl());
    }
}