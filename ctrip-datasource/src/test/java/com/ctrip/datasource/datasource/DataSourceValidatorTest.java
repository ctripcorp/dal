package com.ctrip.datasource.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.ctrip.datasource.log.ILogger.MockILoggerImpl;
import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceValidator;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;

public class DataSourceValidatorTest {
    private static final String mySqlName = "mysqldaltest01db_W";
    private static final String SQL_SERVER_NAME = "dalservicedb";
    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String CONNECTION_URL =
            "jdbc:mysql://DST56614:3306/dao_test?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "!QAZ@WSX1qaz2wsx";

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static ILogger MOCK_LOGGER = new MockILoggerImpl();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DataSourceValidator.setILogger(MOCK_LOGGER);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DataSourceValidator.setILogger(LOGGER);
    }

    @Test
    public void testMySqlDataSourceValidator() throws Exception {
        Connection connection = null;
        try {
            Class.forName(DRIVER_CLASS_NAME);
            connection = DriverManager.getConnection(CONNECTION_URL, USER_NAME, PASSWORD);
        } catch (Throwable e) {
        }

        try {
            Statement statement1 = connection.createStatement();
            statement1.setQueryTimeout(5); // set timeout,a timer will be created.
            statement1.execute("select SLEEP(1)");
        } catch (Throwable e) {
            System.out.println("Statement1 exception captured.");
        }

        boolean result = false;

        try {
            DataSourceValidator validator = new DataSourceValidator();
            result = validator.validate(connection, PooledConnection.VALIDATE_BORROW);
        } catch (Throwable e) {
            System.out.println(String.format("DataSource validation:%s", result));
        }

        // emulate connection pool to close the connection.
        connection.close();

        try {
            Statement statement2 = connection.createStatement();
            statement2.execute("select SLEEP(1)");
            // statement2.execute("select SLEEP(60)");
        } catch (Throwable e) {
            System.out.println("Statement2 exception captured.");
        }

        Thread.sleep(300 * 1000);
    }

    @Test
    public void testSqlServerDataSourceValidator() throws Exception {
        TitanProvider provider = new TitanProvider();
        Map<String, String> settings = new HashMap<>();
        provider.initialize(settings);

        Set<String> names = new HashSet<>();
        names.add(SQL_SERVER_NAME);
        provider.setup(names);

        DataSourceLocator loc = new DataSourceLocator(provider);
        DataSource dataSource = loc.getDataSource(SQL_SERVER_NAME);
        int i = 0;
        while (true) {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(3);
            statement.execute("select 1");
            connection.close();
            System.out.println(i);
            i++;
            Thread.sleep(100);
        }
    }

    @Test
    public void testDataSourceValidatorValidateInit() throws SQLException {
        testDataSourceValidate(PooledConnection.VALIDATE_INIT);
    }

    @Test
    public void testDataSourceValidatorValidateBorrow() throws SQLException {
        testDataSourceValidate(PooledConnection.VALIDATE_BORROW);
    }

    private void testDataSourceValidate(int validateAction) throws SQLException {
        Connection connection = null;
        try {
            Class.forName(DRIVER_CLASS_NAME);
            connection = DriverManager.getConnection(CONNECTION_URL, USER_NAME, PASSWORD);
        } catch (Throwable e) {
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute("select SLEEP(1)");
        } catch (Throwable e) {
            Assert.fail();
        }

        boolean result = false;

        try {
            DataSourceValidator validator = new DataSourceValidator();
            result = validator.validate(connection, validateAction);
            Assert.assertTrue(result);
        } catch (Throwable e) {
            Assert.fail();
        }

        if (connection != null)
            connection.close();
    }

}
