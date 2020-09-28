package com.ctrip.platform.dal;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author c7ch23en
 */
public class MGRConnectionDeadlockTest {

    private DataSource dataSource;
    private Driver driver;

    public MGRConnectionDeadlockTest() throws SQLException {
        dataSource = createDalDataSource();
//        driver = new com.mysql.cj.jdbc.Driver();
    }

    @Test
    public void testRequest() throws SQLException {
        try (Connection connection = getConnection()) {
            testRequest(connection);
        }
    }

    @Test
    public void testGetUrl() throws Exception {
        while (true) {
            try (Connection connection = getConnection()) {
                testGetUrl(connection);
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
    public void testDeadlock() throws SQLException {
        int requestDelayMs = 1;
        int getUrlDelayMs = 5000;
        try (Connection connection = getConnection()) {
            CountDownLatch latch = new CountDownLatch(2);
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(requestDelayMs);
                    testRequest(connection);
                    System.out.println("testRequest completed");
                } catch (Exception e) {
                    System.out.println("testRequest failed");
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }, "deadlock-test-request").start();
            new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(getUrlDelayMs);
                    testGetUrl(connection);
                    System.out.println("testGetUrl completed");
                } catch (Exception e) {
                    System.out.println("testGetUrl failed");
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }, "deadlock-test-geturl").start();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void testRequest(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select name from test_tbl where id = ?")) {
            statement.setInt(1, 100);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    System.out.println("\n  name = " + rs.getString(1) + "\n");
                else
                    System.out.println("\n  no result\n");
            }
        }
    }

    private void testGetUrl(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String url = metaData.getURL();
        System.out.println("\n  url = " + url + "\n");
//        connection.close();
    }

    private Connection getConnection() throws SQLException {
        return getConnectionFromDataSource();
//        return getConnectionFromDriver();
    }

    private Connection getConnectionFromDataSource() throws SQLException {
        return dataSource.getConnection();
    }

    private Connection getConnectionFromDriver() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "sha705e3");
        String url = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=127.0.0.1)(port=3306)," +
                "address=(type=master)(protocol=tcp)(host=127.0.0.1)(port=3306)," +
                "address=(type=master)(protocol=tcp)(host=127.0.0.1)(port=3306)/mockmaster?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
//        url = "jdbc:mysql://127.0.0.1:3306/mockmaster";
        return driver.connect(url, properties);
    }

    private RefreshableDataSource createDalDataSource() {
        DataSourceConfigure config = buildDataSourceConfig2();
        return new RefreshableDataSource(config.getName(), config);
    }

    private DataSourceConfigure buildDataSourceConfig1() {
        DataSourceConfigure config = new DataSourceConfigure("mgr");
        config.setConnectionUrl("jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=127.0.0.1)(port=3306)," +
                "address=(type=master)(protocol=tcp)(host=127.0.0.1)(port=3306)," +
                "address=(type=master)(protocol=tcp)(host=127.0.0.1)(port=3306)/mockmaster");
//        config.setConnectionUrl("jdbc:mysql://127.0.0.1:3306/mockmaster");
        config.setUserName("root");
        config.setPassword("sha705e3");
        config.setDriverClass("com.mysql.jdbc.Driver");
        config.setProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT, "500");
        config.setProperty(DataSourceConfigureConstants.LOGABANDONED, "true");
        return config;
    }

    private DataSourceConfigure buildDataSourceConfig2() {
        DataSourceConfigure config = new DataSourceConfigure("mgr");
        config.setConnectionUrl("jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.32.20.5)(port=3306)," +
                "address=(type=master)(protocol=tcp)(host=10.32.20.5)(port=3307)," +
                "address=(type=master)(protocol=tcp)(host=10.32.20.5)(port=3308)/mytest");
//        config.setConnectionUrl("jdbc:mysql://127.0.0.1:3306/mockmaster");
        config.setUserName("rpl_user");
        config.setPassword("123456");
        config.setDriverClass("com.mysql.jdbc.Driver");
        config.setProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT, "500");
        config.setProperty(DataSourceConfigureConstants.LOGABANDONED, "true");
        return config;
    }

    private DataSource createTomcatDataSource() {
        return createDalDataSource().getSingleDataSource().getDataSource();
    }

}
