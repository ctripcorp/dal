package com.ctrip.platform.dal.dao.datasource.jdbc;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.*;

public class DalStatementTest {
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
    public void testStatementExecuteQuery() throws Exception {
        Statement statement = connection.createStatement();
        statement.executeQuery("select 1");

        Assert.assertEquals(dataSource.getFirstErrorTime(), 0);
        Assert.assertEquals(dataSource.getLastReportErrorTime(), 0);

        try {
            statement.executeQuery("select *from noTable");
        } catch (SQLException e) {

        }
        Assert.assertNotEquals(dataSource.getFirstErrorTime(), 0);
        Assert.assertEquals(dataSource.getLastReportErrorTime(), 0);

        Thread.sleep(1000*60);
        try {
            statement.executeQuery("select *from noTable");
        } catch (SQLException e) {

        }

        Assert.assertNotEquals(dataSource.getFirstErrorTime(), 0);
        Assert.assertNotEquals(dataSource.getLastReportErrorTime(), 0);

        try {
            statement.executeQuery("select 1");
        } catch (SQLException e) {

        }

        Assert.assertEquals(dataSource.getFirstErrorTime(), 0);
        Assert.assertEquals(dataSource.getLastReportErrorTime(), 0);
    }

    @Test
    public void testStatementExecuteUpdate() throws Exception {
        Statement statement = connection.createStatement();
//        statement.executeUpdate("update noTable set id = 1");
//        TimeErrorLog timeErrorLog1 = dataSource.getSingleDataSource().getTimeErrorLog();
//        Assert.assertEquals(timeErrorLog1.getFirstErrorTime(), 0);
//        Assert.assertEquals(timeErrorLog1.getLastReportErrorTime(), 0);

        try {
            statement.executeUpdate("update noTable set id=2");
        } catch (SQLException e) {

        }

        Assert.assertNotEquals(dataSource.getFirstErrorTime(), 0);
        Assert.assertEquals(dataSource.getLastReportErrorTime(), 0);

        Thread.sleep(1000*60);
        try {
            statement.executeQuery("select *from noTable");
        } catch (SQLException e) {

        }

        Assert.assertNotEquals(dataSource.getFirstErrorTime(), 0);
        Assert.assertNotEquals(dataSource.getLastReportErrorTime(), 0);

        try {
            statement.execute("select 1");
        } catch (SQLException e) {

        }

        Assert.assertEquals(dataSource.getFirstErrorTime(), 0);
        Assert.assertEquals(dataSource.getLastReportErrorTime(), 0);
    }
}
