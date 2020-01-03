package com.ctrip.datasource.datasource;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.framework.dal.cluster.client.database.ConnectionString;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.datasource.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.HashSet;

public class DataSourceLocatorTest {

    @Before
    public void beforeTest() {
        DataSourceCreator.getInstance().closeAllDataSources();
    }

    @Test
    public void testClusterSetupAndUninstall() {
        TitanProvider provider = new TitanProvider();
        provider.setup(new HashSet<>());
        DataSourceLocator locator = new DataSourceLocator(provider);
        MockConnectionString connStr1 = new MockConnectionString("10.32.20.139", 3306, "llj_test", "root", "!QAZ@WSX1qaz2wsx");
        MockDatabase db1 = new MockDatabase(connStr1);
        MockDatabase db2 = new MockDatabase(connStr1);
        DataSource ds1 = locator.getDataSource(new ClusterDataSourceIdentity(db1));
        DataSource ds2 = locator.getDataSource(new ClusterDataSourceIdentity(db2));
        Assert.assertNotSame(ds1, ds2);
        SingleDataSource sds1 = ((RefreshableDataSource) ds1).getSingleDataSource();
        SingleDataSource sds2 = ((RefreshableDataSource) ds2).getSingleDataSource();
        Assert.assertSame(sds1, sds2);
        DataSource ds3 = locator.getDataSource(new ClusterDataSourceIdentity(db1));
        Assert.assertSame(ds1, ds3);
        locator.removeDataSource(new ClusterDataSourceIdentity(db1));
        DataSource ds4 = locator.getDataSource(new ClusterDataSourceIdentity(db1));
        SingleDataSource sds4 = ((RefreshableDataSource) ds4).getSingleDataSource();
        Assert.assertNotSame(ds1, ds4);
        Assert.assertSame(sds1, sds4);
        Assert.assertEquals(2, sds1.getReferenceCount());
    }

    private static class MockDatabase implements Database {

        private ConnectionString connectionString;

        public MockDatabase(ConnectionString connectionString) {
            this.connectionString = connectionString;
        }

        @Override
        public String getClusterName() {
            return "mockCluster";
        }

        @Override
        public int getShardIndex() {
            return 0;
        }

        @Override
        public boolean isMaster() {
            return true;
        }

        @Override
        public ConnectionString getConnectionString() {
            return connectionString;
        }

        @Override
        public String[] getAliasKeys() {
            return null;
        }

    }

    private static class MockConnectionString implements ConnectionString {

        private static final String CONNECTION_URL_PATTERN = "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8";

        private String ip;
        private Integer port;
        private String dbName;
        private String uid;
        private String pwd;

        public MockConnectionString(String ip, Integer port, String dbName, String uid, String pwd) {
            this.ip = ip;
            this.port = port;
            this.dbName = dbName;
            this.uid = uid;
            this.pwd = pwd;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        @Override
        public String getPrimaryConnectionUrl() {
            return String.format(CONNECTION_URL_PATTERN, ip, port, dbName);
        }

        @Override
        public String getFailOverConnectionUrl() {
            return getPrimaryConnectionUrl();
        }

        @Override
        public String getDbName() {
            return dbName;
        }

        @Override
        public String getUsername() {
            return uid;
        }

        @Override
        public String getPassword() {
            return pwd;
        }

        @Override
        public String getDriverClassName() {
            return "com.mysql.jdbc.Driver";
        }

        @Override
        public String getPrimaryHost() {
            return ip;
        }

        @Override
        public int getPrimaryPort() {
            return port;
        }

    }

}
