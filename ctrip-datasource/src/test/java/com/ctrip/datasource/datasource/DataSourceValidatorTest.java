package com.ctrip.datasource.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.ctrip.datasource.log.ILogger.MockILoggerImpl;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.DataSourceValidator;
import com.ctrip.platform.dal.dao.datasource.ValidatorProxy;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataSourceValidatorTest {
    private static final String SQL_SERVER_DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String SQL_SERVER_CONNECTION_URL = "jdbc:sqlserver://dst56614.cn1.global.ctrip.com:1433;databaseName=daoTest";

    private static final String SQL_SERVER_USER_NAME = "sa";
    private static final String SQL_SERVER_PASSWORD = "!QAZ@WSX1qaz2wsx";

    private static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String MYSQL_CONNECTION_URL =
            "jdbc:mysql://dst56614.cn1.global.ctrip.com:3306/dao_test?useUnicode=true&characterEncoding=UTF-8";
    private static final String MYSQL_USER_NAME = "root";
    private static final String MYSQL_PASSWORD = "!QAZ@WSX1qaz2wsx";

    private static final String CORRECT_SQL_SERVER_INIT_SQL = "SELECT 1";
    private static final String ERROR_SQL_SERVER_INIT_SQL = "SELECT CURDATE()"; // the function is mysql only

    private static final String CORRECT_MYSQL_INIT_SQL = "SELECT 1";
    private static final String ERROR_MYSQL_INIT_SQL = "SELECT GETDATE()"; // the function is sql server only

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
    public void testSqlServerValidateInitSuccess() throws SQLException {
        testSqlServerConnectionValidateSuccess(PooledConnection.VALIDATE_INIT);
    }

    @Test
    public void testSqlServerValidateBorrowSuccess() throws SQLException {
        testSqlServerConnectionValidateSuccess(PooledConnection.VALIDATE_BORROW);
    }

    @Test
    public void testSqlServerValidateInitFailure() throws SQLException {
        testSqlServerConnectionValidFailure(PooledConnection.VALIDATE_INIT);
    }

    @Test
    public void testMySqlValidateInitSuccess() throws SQLException {
        testMySqlConnectionValidateSuccess(PooledConnection.VALIDATE_INIT);
    }

    @Test
    public void testMySqlValidateBorrowSuccess() throws SQLException {
        testMySqlConnectionValidateSuccess(PooledConnection.VALIDATE_BORROW);
    }

    @Test
    public void testMySqlValidateInitFailure() throws SQLException {
        testMySqlConnectionValidateFailure(PooledConnection.VALIDATE_INIT);
    }

    @Test
    public void testValidateQueryTimeout() throws SQLException {
        Connection connMySql = createNewMySqlConnection();
        PingConnection pingConnection = new PingConnection(connMySql);
        DataSourceValidator validator = new DataSourceValidator();
        PoolProperties properties = new PoolProperties();
        properties.setValidationQueryTimeout(500);
        validator.setPoolProperties(properties);
        validator.validate(pingConnection, PooledConnection.VALIDATE_INIT);
        Assert.assertEquals(500, pingConnection.timeout);
    }

    @Test
    public void testDefaultValidateQueryTimeout() throws SQLException {
        Connection connMySql = createNewMySqlConnection();
        PingConnection pingConnection = new PingConnection(connMySql);
        DataSourceValidator validator = new DataSourceValidator();
        PoolProperties properties = new PoolProperties();
        validator.setPoolProperties(properties);
        validator.validate(pingConnection, PooledConnection.VALIDATE_INIT);
        Assert.assertEquals(DataSourceConfigureConstants.DEFAULT_VALIDATIONQUERYTIMEOUT, pingConnection.timeout);
    }

    @Test
    public void testMinValidateQueryTimeout() throws SQLException {
        Connection connMySql = createNewMySqlConnection();
        PingConnection pingConnection = new PingConnection(connMySql);
        DataSourceValidator validator = new DataSourceValidator();
        PoolProperties properties = new PoolProperties();
        properties.setValidationQueryTimeout(2);
        validator.setPoolProperties(properties);
        validator.validate(pingConnection, PooledConnection.VALIDATE_INIT);
        Assert.assertEquals(DataSourceConfigureConstants.MIN_VALIDATIONQUERYTIMEOUT, pingConnection.timeout);
    }

    private void testSqlServerConnectionValidateSuccess(int validateAction) throws SQLException {
        Connection connection = createNewSqlServerConnection();
        testConnectionValidateSuccess(validateAction, connection, CORRECT_SQL_SERVER_INIT_SQL);
    }

    private void testMySqlConnectionValidateSuccess(int validateAction) throws SQLException {
        Connection connection = createNewMySqlConnection();
        testConnectionValidateSuccess(validateAction, connection, CORRECT_MYSQL_INIT_SQL);
    }

    private void testConnectionValidateSuccess(int validateAction, Connection connection, String initSql)
            throws SQLException {
        boolean result;

        try {
            DataSourceValidator validator = new DataSourceValidator();
            setPoolProperties(validator, initSql);
            result = validator.validate(connection, validateAction);
            Assert.assertTrue(result);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.fail();
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    private void testSqlServerConnectionValidFailure(int validateAction) throws SQLException {
        Connection connection = createNewSqlServerConnection();
        testConnectionValidateFailure(validateAction, connection, ERROR_SQL_SERVER_INIT_SQL);
    }

    private void testMySqlConnectionValidateFailure(int validateAction) throws SQLException {
        Connection connection = createNewMySqlConnection();
        testConnectionValidateFailure(validateAction, connection, ERROR_MYSQL_INIT_SQL);
    }

    private void testConnectionValidateFailure(int validateAction, Connection connection, String initSql)
            throws SQLException {
        boolean result = false;

        try {
            DataSourceValidator validator = new DataSourceValidator();
            setPoolProperties(validator, initSql);
            result = validator.validate(connection, validateAction);
            Assert.assertFalse(result);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.fail();
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    private Connection createNewSqlServerConnection() {
        return createNewConnection(SQL_SERVER_DRIVER_CLASS_NAME, SQL_SERVER_CONNECTION_URL, SQL_SERVER_USER_NAME,
                SQL_SERVER_PASSWORD);
    }

    private Connection createNewMySqlConnection() {
        return createNewConnection(MYSQL_DRIVER_CLASS_NAME, MYSQL_CONNECTION_URL, MYSQL_USER_NAME, MYSQL_PASSWORD);
    }

    private Connection createNewConnection(String driverClassName, String connectionUrl, String userName,
                                           String password) {
        Connection connection = null;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(connectionUrl, userName, password);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }

        return connection;
    }

    private void setPoolProperties(ValidatorProxy validator, String initSql) {
        PoolProperties properties = new PoolProperties();
        properties.setInitSQL(initSql);
        validator.setPoolProperties(properties);
    }

}
