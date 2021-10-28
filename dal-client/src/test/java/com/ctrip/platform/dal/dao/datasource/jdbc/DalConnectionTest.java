package com.ctrip.platform.dal.dao.datasource.jdbc;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.*;

public class DalConnectionTest {
    private Properties properties = new Properties();
    private RefreshableDataSource dataSource = null;
    private Connection connection = null;

    {
        properties.setProperty(USER_NAME, "root");
        properties.setProperty(PASSWORD, "!QAZ@WSX1qaz2wsx");
        properties.setProperty(CONNECTION_URL, "jdbc:mysql://10.32.20.116:3306/llj_test");
        properties.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");
        DataSourceConfigure configure = new DataSourceConfigure("DalService2DB_w", properties);
        try {
            dataSource = new RefreshableDataSource("DalService2DB_w", configure);
            connection = dataSource.getConnection();
        } catch (SQLException e) {

        }
    }

    @Test
    public void testConnectionType() throws SQLException {
        if (connection instanceof DalConnection) {
            //
        } else {
            Assert.fail();
        }
        connection.close();
    }

    @Test
    public void testStatementType() throws SQLException {
        Statement statement = connection.createStatement();
        if (!(statement instanceof DalStatement)) {
            Assert.fail();
        }
        statement.close();
        PreparedStatement preparedStatement = connection.prepareStatement("select 1");
        if (!(preparedStatement instanceof DalPreparedStatement)) {
            Assert.fail();
        }
        preparedStatement.close();
        CallableStatement callableStatement = connection.prepareCall("select 1");
        if (!(callableStatement instanceof DalCallableStatement)) {
            Assert.fail();
        }
        callableStatement.close();
    }

//    @Test
    public void testDiscardConnection() throws SQLException {
        Properties p = new Properties();
        p.setProperty(USER_NAME, "tt_daltest_3");
        p.setProperty(PASSWORD, "R0NeM30TcbAfWz7aHoWx");
        p.setProperty(CONNECTION_URL, "jdbc:mysql://10.2.22.223:55777/dalservice2db?useUnicode=true&characterEncoding=UTF-8");
        p.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");
        DataSourceConfigure c = new DataSourceConfigure("dalservice2db", p);
        RefreshableDataSource ds = new RefreshableDataSource("dalservice2db", c);
        PooledConnection pConn = null;
        try (Connection connection = ds.getConnection()) {
            pConn = connection.unwrap(PooledConnection.class);
            System.out.println("discarded0: " + pConn.isDiscarded());
            try {
//            PreparedStatement ps = connection.prepareStatement("select * from person5756 where id > 0");
//            ResultSet rs = ps.executeQuery();
//            System.out.println("result set: " + rs);
                PreparedStatement ps = connection.prepareStatement("update person33 set name = 'testDiscard' where id > 0");
                int rows = ps.executeUpdate();
                System.out.println("affected rows: " + rows);
                System.out.println("discarded1: " + pConn.isDiscarded());
            } catch (SQLException e) {
                System.out.println("error: " + e);
                System.out.println("error: " + e.getErrorCode());
                e.printStackTrace();
                System.out.println("discarded2: " + pConn.isDiscarded());
                throw e;
            } finally {
//                connection.close();
                System.out.println("discarded3: " + pConn.isDiscarded());
            }
        } catch (Throwable t) {
            // ignore
        }
        if (pConn != null)
            System.out.println("discarded4: " + pConn.isDiscarded());
    }

}
