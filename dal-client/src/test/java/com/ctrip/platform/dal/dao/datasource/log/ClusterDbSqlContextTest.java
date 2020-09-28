package com.ctrip.platform.dal.dao.datasource.log;

import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author c7ch23en
 */
public class ClusterDbSqlContextTest extends BaseSqlContextTest {

    private static final String CLUSTER = "test_cluster";
    private static final int SHARD = 1;
    private static final DatabaseRole ROLE = DatabaseRole.MASTER;

    @Test
    public void testSuccess() throws Exception {
        ClusterDbSqlContext context =
                new ClusterDbSqlContext(CLUSTER, SHARD, ROLE, CLIENT_VERSION, CLIENT_ZONE, DB_NAME);
        buildContextSuccess(context);
        assertContextSuccess(context);
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(CLUSTER, tags.get(ClusterDbSqlContext.CLUSTER));
        Assert.assertEquals(String.valueOf(SHARD), tags.get(ClusterDbSqlContext.SHARD));
        Assert.assertEquals(ROLE.getValue(), tags.get(ClusterDbSqlContext.ROLE));

        ClusterDbSqlContext forked = (ClusterDbSqlContext) context.fork();
        assertFork(forked);
        tags = forked.toMetricTags();
        Assert.assertEquals(CLUSTER, tags.get(ClusterDbSqlContext.CLUSTER));
        Assert.assertEquals(String.valueOf(SHARD), tags.get(ClusterDbSqlContext.SHARD));
        Assert.assertEquals(ROLE.getValue(), tags.get(ClusterDbSqlContext.ROLE));
    }

    @Test
    public void testFail() throws Exception {
        ClusterDbSqlContext context =
                new ClusterDbSqlContext(CLUSTER, SHARD, ROLE, CLIENT_VERSION, CLIENT_ZONE, DB_NAME);
        buildContextFail(context, new RuntimeException());
        assertContextFail(context);
        Map<String, String> tags = context.toMetricTags();
        Assert.assertEquals(CLUSTER, tags.get(ClusterDbSqlContext.CLUSTER));
        Assert.assertEquals(String.valueOf(SHARD), tags.get(ClusterDbSqlContext.SHARD));
        Assert.assertEquals(ROLE.getValue(), tags.get(ClusterDbSqlContext.ROLE));

        ClusterDbSqlContext forked = (ClusterDbSqlContext) context.fork();
        assertFork(forked);
        tags = forked.toMetricTags();
        Assert.assertEquals(CLUSTER, tags.get(ClusterDbSqlContext.CLUSTER));
        Assert.assertEquals(String.valueOf(SHARD), tags.get(ClusterDbSqlContext.SHARD));
        Assert.assertEquals(ROLE.getValue(), tags.get(ClusterDbSqlContext.ROLE));
    }

}
