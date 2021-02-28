package com.ctrip.platform.dal.dao.datasource.tomcat;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.AbstractConnectionListener;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import org.apache.tomcat.jdbc.pool.*;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DalConnectionPoolTest {

    @Test
    public void testValidation() throws Exception {
        DataSourceConfigure config = new DataSourceConfigure("test");
        config.setDriverClass("com.mysql.jdbc.Driver");
        config.setConnectionUrl("jdbc:mysql://10.32.20.125:3306/llj_test");
        config.setUserName("root");
        config.setPassword("!QAZ@WSX1qaz2wsx");
        config.setProperty(DataSourceConfigureConstants.VALIDATORCLASSNAME, "com.ctrip.platform.dal.dao.datasource.tomcat.MockValidator");
        config.setProperty(DataSourceConfigureConstants.VALIDATIONINTERVAL, "5000");
        RefreshableDataSource dataSource = new RefreshableDataSource(config.getName(), config);
        for (int i = 0; i < 20; i++) {
            try (Connection connection = dataSource.getConnection()) {
                System.out.println(connection.getMetaData().getURL());
            } catch (Throwable t) {
                System.out.println("exception type: " + t.getClass());
                t.printStackTrace();
            } finally {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    /**
     * need local mysql
     * 
     * @throws SQLException
     */
    @Test
    public void simpleTest() throws SQLException {
        int initSize = 5;
        PoolConfiguration p = new PoolProperties();
        p.setUrl("jdbc:mysql://localhost:3306/test?a=1,b=2");
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername("root");
        p.setPassword("root");
        p.setInitialSize(initSize);

        final AtomicInteger create = new AtomicInteger();
        final AtomicInteger release = new AtomicInteger();
        final AtomicInteger abandon = new AtomicInteger();

        DalConnectionPool.setConnectionListener(new AbstractConnectionListener() {
            @Override
            public void doOnCreateConnection(String poolDesc, Connection connection, DataSourceIdentity dataSourceId, long startTime) {
                create.incrementAndGet();
            }

            @Override
            public void doOnReleaseConnection(String poolDesc, Connection connection) {
                release.incrementAndGet();
            }

            @Override
            protected void doOnAbandonConnection(String poolDesc, Connection connection) {
                abandon.incrementAndGet();
            }

            @Override
            public int getOrder() {
                return 0;
            }
        });

        DataSource dataSource = new DalTomcatDataSource(p);
        ConnectionPool dalConnectionPool = dataSource.createPool();

        Assert.assertEquals(initSize, create.get());

        List<Connection> connections = new LinkedList<>();
        for (int i = 0; i < initSize; i++) {
            connections.add(dalConnectionPool.getConnection());
        }
        Assert.assertEquals(initSize, create.get());
        for (int i = 0; i < initSize; i++) {
            connections.add(dalConnectionPool.getConnection());
        }
        Assert.assertEquals(initSize * 2, create.get());

        for (Connection connection : connections) {
            connection.close();
        }

        Assert.assertEquals(initSize * 2, create.get());
        Assert.assertEquals(0, release.get());

        dalConnectionPool.purge();

        Assert.assertEquals(initSize * 2, create.get());
        Assert.assertEquals(initSize * 2, release.get());
    }

}
