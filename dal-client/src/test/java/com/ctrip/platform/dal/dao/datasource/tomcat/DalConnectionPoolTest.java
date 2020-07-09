package com.ctrip.platform.dal.dao.datasource.tomcat;

import com.ctrip.platform.dal.dao.datasource.AbstractConnectionListener;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DalConnectionPoolTest {

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
