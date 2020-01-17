package com.ctrip.platform.dal.dao.datasource.jdbc;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import org.junit.Assert;
import org.junit.Test;

import java.sql.*;
import java.util.Properties;

import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.*;

public class DalConnectionTest {
    private Properties properties = new Properties();
    private RefreshableDataSource dataSource = null;
    private Connection connection = null;

    {
        properties.setProperty(USER_NAME, "root");
        properties.setProperty(PASSWORD, "123456");
        properties.setProperty(CONNECTION_URL, "jdbc:mysql://localhost:3306/test");
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
}
