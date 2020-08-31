package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class CtripLocalClusterConfigProviderTest {

    @Test
    public void testGetClusterConfig1() {
        CtripLocalContext context = new CtripLocalContextImpl(null,
                null, true, null);
        CtripLocalClusterConfigProvider provider = new CtripLocalClusterConfigProvider(context);
        ClusterConfig config = provider.getClusterConfig("mock1_dalcluster");
        assertClusterConfig(config,
                new DatabaseMeta("10.32.20.128", 3306, "mock11", "root", "root"),
                new DatabaseMeta("10.32.20.128", 3306, "mock12", "root", "root"));
        config = provider.getClusterConfig("mock2_dalcluster");
        assertClusterConfig(config,
                new DatabaseMeta(CtripLocalMySQLPropertiesParser.DEFAULT_HOST,
                        CtripLocalMySQLPropertiesParser.DEFAULT_PORT, "mock2db",
                        CtripLocalMySQLPropertiesParser.DEFAULT_UID,
                        CtripLocalMySQLPropertiesParser.DEFAULT_PWD));
        config = provider.getClusterConfig("mock3_dalcluster");
        assertClusterConfig(config,
                new DatabaseMeta(CtripLocalMySQLPropertiesParser.DEFAULT_HOST,
                        CtripLocalMySQLPropertiesParser.DEFAULT_PORT, "mock3",
                        CtripLocalMySQLPropertiesParser.DEFAULT_UID,
                        CtripLocalMySQLPropertiesParser.DEFAULT_PWD));
    }

    private void assertClusterConfig(ClusterConfig config, DatabaseMeta... databaseMetas) {
        Cluster cluster = config.generate();
        Assert.assertEquals(databaseMetas.length, cluster.getAllDbShards().size());
        for (int i = 0; i < databaseMetas.length; i++) {
            Database database = cluster.getMasterOnShard(i);
            String url = String.format(ConnectionStringParser.DBURL_MYSQL,
                    databaseMetas[i].host, databaseMetas[i].port, databaseMetas[i].dbName,
                    ConnectionStringParser.DEFAULT_ENCODING);
            Assert.assertEquals(url, database.getConnectionString().getPrimaryConnectionUrl());
            Assert.assertEquals(databaseMetas[i].uid, database.getConnectionString().getUsername());
            Assert.assertEquals(databaseMetas[i].pwd, database.getConnectionString().getPassword());
        }
    }

    static class DatabaseMeta {
        String host;
        int port;
        String dbName;
        String uid;
        String pwd;

        public DatabaseMeta(String host, int port, String dbName, String uid, String pwd) {
            this.host = host;
            this.port = port;
            this.dbName = dbName;
            this.uid = uid;
            this.pwd = pwd;
        }
    }

}
