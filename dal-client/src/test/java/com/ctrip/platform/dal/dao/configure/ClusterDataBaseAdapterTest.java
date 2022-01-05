package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.DatabaseConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.DatabaseShardConfigImpl;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.platform.dal.dao.datasource.ClusterInfoDelegateIdentity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2021/11/17
 */
public class ClusterDataBaseAdapterTest {

    private static final String CLUSTER_NAME = "test_cluster_name";

    private static final String MASTER_DOMAIN = "test_master_domain";

    private static final String SLAVE_DOMAIN = "test_slave_domain";

    private ClusterConfigImpl clusterConfig;

    private DatabaseShardConfigImpl databaseShardConfig;

    private DatabaseConfigImpl databaseConfig;

    private Cluster cluster;

    private ClusterDataBaseAdapter clusterDataBaseAdapter;

    @Before
    public void setUp() throws Exception {

        clusterConfig = new ClusterConfigImpl(CLUSTER_NAME, DatabaseCategory.CUSTOM, 12);
        databaseShardConfig = new DatabaseShardConfigImpl(clusterConfig, 0);
        databaseShardConfig.setMasterDomain(MASTER_DOMAIN);
        databaseShardConfig.setSlaveDomain(SLAVE_DOMAIN);

        databaseConfig = new DatabaseConfigImpl(databaseShardConfig);
        databaseShardConfig.addDatabaseConfig(databaseConfig);

        clusterConfig.addDatabaseShardConfig(databaseShardConfig);

        cluster = clusterConfig.generate();
        clusterDataBaseAdapter = new ClusterDataBaseAdapter(cluster);
    }

    @Test
    public void getClusterInfo() {
        ClusterInfo clusterInfo = clusterDataBaseAdapter.getClusterInfo();
        Assert.assertEquals(CLUSTER_NAME, clusterInfo.getClusterName());

        Assert.assertTrue(clusterDataBaseAdapter.getDataSourceIdentity() instanceof ClusterInfoDelegateIdentity);
    }
}