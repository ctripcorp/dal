package com.ctrip.datasource.datasource.BackgroundExecutor;

import com.ctrip.datasource.helper.DNS.CtripDatabaseDomainChecker;
import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import com.ctrip.platform.dal.dao.datasource.tomcat.DalTomcatDataSource;
import com.ctrip.platform.dal.dao.helper.DatabaseDomainChecker;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.Assert;
import org.junit.Test;
import javax.sql.DataSource;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatasourceBackgroundExecutorTest {
    private static final String SELECT_SQL = "select 1";
    private static final String CONNECTION_STRING =
            "Data Source=dst56614.cn1.global.ctrip.com,1433;Initial Catalog=PerformanceTest;UID=sa;password=!QAZ@WSX1qaz2wsx;";

    private static final String USER_NAME = "sa";
    private static final String PASSWORD = "!QAZ@WSX1qaz2wsx";
    private static final String CONNECTION_URL = "jdbc:sqlserver://dst56614.cn1.global.ctrip.com:1433;DatabaseName=PerformanceTest";
    private static final String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final String CONNECTION_URL_2 = "jdbc:sqlserver://10.32.20.128:1433;DatabaseName=daoTest";

    private static final String NETWORK_ADDRESS_CACHE_TTL = "networkaddress.cache.ttl";
    private static final String NETWORK_ADDRESS_CACHE_TTL_VALUE_IN_SECONDS = "1";

    private static final String DOMAIN = "dst56614.cn1.global.ctrip.com";
    private static final String INVALID_IP = "10.10.10.10";

    private String getIP() {
        String domain = "dst56614.cn1.global.ctrip.com";
        String result = "";
        try {
            InetAddress address = InetAddress.getByName(domain);
            result = address.getHostAddress();
        } catch (Throwable e) {
            System.out.println("DNS resolving error:" + e.getCause().getMessage());
        }

        return result;
    }

    @Test
    public void testDatabaseDomainChecker() throws SQLException {
        // set jvm dns cache time
        java.security.Security.setProperty(NETWORK_ADDRESS_CACHE_TTL, NETWORK_ADDRESS_CACHE_TTL_VALUE_IN_SECONDS);

        // ensure no IP be set.
        setIPToValid();

        String name = "dst56614.cn1.global.ctrip.com";
        Properties properties = new Properties();
        properties.setProperty("userName", USER_NAME);
        properties.setProperty("password", PASSWORD);
        properties.setProperty("connectionUrl", CONNECTION_URL);
        properties.setProperty("driverClassName", DRIVER_CLASS_NAME);

        DalConnectionString connectionString = new ConnectionString(name, CONNECTION_STRING, CONNECTION_STRING);

        try {
            DataSourceConfigure dataSourceConfigure = new DataSourceConfigure(name, properties);
            dataSourceConfigure.setConnectionString(connectionString);
            RefreshableDataSource rds = new RefreshableDataSource(name, dataSourceConfigure);

            DatabaseDomainChecker checker = new CtripDatabaseDomainChecker();
            checker.start(rds);

            // ensure check thread started
            Thread.sleep(1 * 1000);
            executeUnderValidIPStatus(rds);

            String connectionId = getConnectionId("PerformanceTest");

            // set ip to invalid
            setIPToInvalid();

            // ensure datasource has been changed
            Thread.sleep(6 * 1000);
            executeUnderInvalidIPStatus(rds);

            // wait for datasource being closed
            Thread.sleep(10 * 1000);

            Assert.assertTrue(isConnectionKilled(connectionId));
        } catch (Throwable e) {
            Assert.fail();
        } finally {
            setIPToValid();
        }
    }

    private Connection getConnection(RefreshableDataSource rds) throws SQLException {
        if (rds == null)
            return null;

        SingleDataSource singleDataSource = rds.getSingleDataSource();
        if (singleDataSource == null)
            return null;

        DataSource dataSource = singleDataSource.getDataSource();
        if (dataSource == null)
            return null;

        return dataSource.getConnection();
    }

    private void executeUnderValidIPStatus(RefreshableDataSource rds) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection(rds);
            if (connection == null) {
                Assert.fail();
            }
            statement = connection.createStatement();
            statement.execute(SELECT_SQL);
            Assert.assertTrue(true);
        } catch (Throwable e) {
            System.out.println(e.getCause().getMessage());
            Assert.fail();
        } finally {
            closeResources(connection, statement);
        }
    }

    private void executeUnderInvalidIPStatus(RefreshableDataSource rds) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection(rds);
            if (connection == null) {
                Assert.fail();
            }
            statement = connection.createStatement();
            statement.execute(SELECT_SQL);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertTrue(true);
        } finally {
            closeResources(connection, statement);
        }
    }

    private void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Throwable e) {
            }
        }

        closeResources(connection, statement);
    }

    private void closeResources(Connection connection, Statement statement) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Throwable e) {
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (Throwable e) {

            }
        }
    }

    private void setIPToInvalid() {
        HostsHelper.updateHost(INVALID_IP, DOMAIN);
    }

    private void setIPToValid() {
        HostsHelper.deleteHost(INVALID_IP, DOMAIN);
    }

    private String getConnectionId(String dbName) {
        String connectionId = "";
        PoolProperties properties = getPoolProperties();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        org.apache.tomcat.jdbc.pool.DataSource dataSource = null;

        try {
            dataSource = new DalTomcatDataSource(properties);
            dataSource.createPool();
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("exec sp_who2");
            connectionId = getConnectionId(resultSet, dbName);
        } catch (Throwable e) {
            System.out.println("getConnectionId error:" + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
            dataSource.close();
        }

        return connectionId;
    }

    private PoolProperties getPoolProperties() {
        PoolProperties properties = new PoolProperties();
        properties.setUsername(USER_NAME);
        properties.setPassword(PASSWORD);
        properties.setUrl(CONNECTION_URL_2);
        properties.setDriverClassName(DRIVER_CLASS_NAME);
        return properties;
    }

    private boolean isConnectionKilled(String connectionId) {
        PoolProperties properties = getPoolProperties();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        org.apache.tomcat.jdbc.pool.DataSource dataSource = null;
        boolean isKilled = false;
        try {
            dataSource = new DalTomcatDataSource(properties);
            dataSource.createPool();
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("exec sp_who2");
            isKilled = isConnectionKilled(resultSet, connectionId);
        } catch (Throwable e) {
            System.out.print("isConnectionKilled error:" + e.getMessage());
        } finally {
            closeResources(connection, statement, resultSet);
            dataSource.close();
        }
        return isKilled;
    }

    private String getConnectionId(ResultSet resultSet, String dbName) {
        String connectionId = null;
        if (resultSet == null)
            return connectionId;

        try {
            while (resultSet.next()) {
                String temp = resultSet.getString("DBName");
                if (temp == null)
                    continue;

                if (temp.equals(dbName)) {
                    connectionId = String.valueOf(resultSet.getInt(1));
                    break;
                }
            }
        } catch (Throwable e) {
            System.out.println("getConnectionId error:" + e.getMessage());
        }

        return connectionId;
    }

    private boolean isConnectionKilled(ResultSet resultSet, String connectionId) {
        boolean result = false;
        if (resultSet == null)
            return result;

        if (connectionId == null || connectionId.isEmpty())
            return result;

        try {
            while (resultSet.next()) {
                String id = String.valueOf(resultSet.getInt(1));
                if (id.equals(connectionId)) {
                    result = true;
                    break;
                }
            }
        } catch (Throwable e) {
            System.out.println("isConnectionKilled error:" + e.getMessage());
        }

        return result;
    }

}
