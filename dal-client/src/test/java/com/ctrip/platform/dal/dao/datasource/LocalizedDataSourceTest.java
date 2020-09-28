package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.LocalizationState;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

public class LocalizedDataSourceTest {

    private static final String SELECT_SQL = "select * from person where name = 'selectName'";
    private static final String SELECT_SQL2 = "select * from person where name = 'selectName2'";
    private static final String INSERT_SQL = "insert into person (name, age) values ('insertName', 10)";
    private static final String UPDATE_SQL = "update person set age = 1 where name = 'updateName'";
    private static final String DELETE_SQL = "delete from person where name = 'deleteName'";

    private static final String SELECT_SQL_TEMPLATE = "select * from person where name = ?";
    private static final String INSERT_SQL_TEMPLATE = "insert into person (name, age) values (?, 10)";
    private static final String UPDATE_SQL_TEMPLATE = "update person set age = 1 where name = ?";
    private static final String DELETE_SQL_TEMPLATE = "delete from person where name = ?";

    private static final String SELECT_PARAM = "selectName";
    private static final String SELECT_PARAM2 = "selectName2";
    private static final String INSERT_PARAM = "insertName";
    private static final String UPDATE_PARAM = "updateName";
    private static final String DELETE_PARAM = "deleteName";

    private static final String TEST_ZONE = "testZone";

    @Test
    public void testBlockingDataSource() throws SQLException {
        DataSource dataSource = getBlockingDataSource();
        testStatementBlocked(dataSource);
        testPreparedStatementBlocked(dataSource);
    }

    @Test
    public void testNormalDataSource() throws SQLException {
        DataSource dataSource = getNormalDataSource();
        testStatementPassed(dataSource);
        testPreparedStatementPassed(dataSource);
    }

    private void testStatementBlocked(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        Assert.assertTrue(metaData instanceof LocalizedDatabaseMetaDataImpl);
        String url = ((LocalizedDatabaseMetaDataImpl) metaData).getExtendedURL();
        Assert.assertTrue(url.endsWith(TEST_ZONE.toUpperCase()));

        Statement statement = connection.createStatement();

        // executeQuery
        statement.executeQuery(SELECT_SQL);

        // executeUpdate
        try {
            statement.executeUpdate(INSERT_SQL);
            Assert.fail("insert blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try {
            statement.executeUpdate(UPDATE_SQL);
            Assert.fail("update blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try {
            statement.executeUpdate(DELETE_SQL);
            Assert.fail("delete blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }

        // execute
        statement.execute(SELECT_SQL);
        try {
            statement.execute(INSERT_SQL);
            Assert.fail("insert blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try {
            statement.execute(UPDATE_SQL);
            Assert.fail("update blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try {
            statement.execute(DELETE_SQL);
            Assert.fail("delete blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }

        // executeBatch
//        statement.addBatch(SELECT_SQL);
//        statement.addBatch(SELECT_SQL2);
//        statement.executeBatch();
        statement.addBatch(INSERT_SQL);
        statement.addBatch(UPDATE_SQL);
        try {
            statement.executeBatch();
            Assert.fail("executeBatch blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        statement.addBatch(SELECT_SQL);
        statement.addBatch(DELETE_SQL);
        try {
            statement.executeBatch();
            Assert.fail("executeBatch blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }

        // executeLargeBatch
//        statement.addBatch(SELECT_SQL);
//        statement.addBatch(SELECT_SQL2);
//        statement.executeLargeBatch();
        statement.addBatch(INSERT_SQL);
        statement.addBatch(UPDATE_SQL);
        try {
            statement.executeLargeBatch();
            Assert.fail("executeBatch blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        statement.addBatch(SELECT_SQL);
        statement.addBatch(DELETE_SQL);
        try {
            statement.executeLargeBatch();
            Assert.fail("executeBatch blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }

        // executeLargeUpdate
        try {
            statement.executeLargeUpdate(INSERT_SQL);
            Assert.fail("insert blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try {
            statement.executeLargeUpdate(UPDATE_SQL);
            Assert.fail("update blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try {
            statement.executeLargeUpdate(DELETE_SQL);
            Assert.fail("delete blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }

        statement.close();
        connection.close();
    }

    private void testPreparedStatementBlocked(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        Assert.assertTrue(metaData instanceof LocalizedDatabaseMetaDataImpl);
        String url = ((LocalizedDatabaseMetaDataImpl) metaData).getExtendedURL();
        Assert.assertTrue(url.endsWith(TEST_ZONE.toUpperCase()));

        // executeQuery
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SQL_TEMPLATE);
        preparedStatement.setString(1, SELECT_PARAM);
        preparedStatement.executeQuery();
        preparedStatement.close();

        // executeUpdate
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(INSERT_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, INSERT_PARAM);
            innerPreparedStatement.executeUpdate();
            Assert.fail("insert blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(UPDATE_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, UPDATE_PARAM);
            innerPreparedStatement.executeUpdate();
            Assert.fail("update blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(DELETE_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, DELETE_PARAM);
            innerPreparedStatement.executeUpdate();
            Assert.fail("delete blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }

        // execute
        String multiSql = SELECT_SQL_TEMPLATE + ";" + SELECT_SQL_TEMPLATE;
        PreparedStatement preparedStatement2 = connection.prepareStatement(multiSql);
        preparedStatement2.setString(1, SELECT_PARAM);
        preparedStatement2.setString(2, SELECT_PARAM2);
        preparedStatement2.execute();
        preparedStatement2.close();
        multiSql = SELECT_SQL_TEMPLATE + ";" + INSERT_SQL_TEMPLATE;
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(multiSql)) {
            innerPreparedStatement.setString(1, SELECT_PARAM);
            innerPreparedStatement.setString(2, INSERT_PARAM);
            innerPreparedStatement.execute();
            // execute multiple sql blocking test
//            Assert.fail("execute multiSql blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        multiSql = UPDATE_SQL_TEMPLATE + ";" + DELETE_SQL_TEMPLATE;
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(multiSql)) {
            innerPreparedStatement.setString(1, UPDATE_PARAM);
            innerPreparedStatement.setString(2, DELETE_PARAM);
            innerPreparedStatement.execute();
            Assert.fail("execute multiSql blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }

        // executeLargeUpdate
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(INSERT_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, INSERT_PARAM);
            innerPreparedStatement.executeLargeUpdate();
            Assert.fail("insert blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(UPDATE_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, UPDATE_PARAM);
            innerPreparedStatement.executeLargeUpdate();
            Assert.fail("update blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(DELETE_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, DELETE_PARAM);
            innerPreparedStatement.executeLargeUpdate();
            Assert.fail("delete blocking failed");
        } catch (DalException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.NonLocalRequestBlocked.getCode());
        }

        connection.close();
    }

    private void testStatementPassed(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        Assert.assertTrue(metaData instanceof LocalizedDatabaseMetaDataImpl);
        String url = ((LocalizedDatabaseMetaDataImpl) metaData).getExtendedURL();
        Assert.assertTrue(url.endsWith("UTF-8"));

        Statement statement = connection.createStatement();

        // executeQuery
        statement.executeQuery(SELECT_SQL);

        // executeUpdate
        statement.executeUpdate(INSERT_SQL);
        statement.executeUpdate(UPDATE_SQL);
        statement.executeUpdate(DELETE_SQL);

        // execute
        statement.execute(SELECT_SQL);
        statement.execute(INSERT_SQL);
        statement.execute(UPDATE_SQL);
        statement.execute(DELETE_SQL);

        // executeBatch
        statement.addBatch(SELECT_SQL);
        statement.addBatch(SELECT_SQL2);
        statement.executeBatch();
        statement.addBatch(INSERT_SQL);
        statement.addBatch(UPDATE_SQL);
        statement.executeBatch();
        statement.addBatch(SELECT_SQL);
        statement.addBatch(DELETE_SQL);
        statement.executeBatch();

        // executeLargeBatch
        statement.addBatch(SELECT_SQL);
        statement.addBatch(SELECT_SQL2);
        statement.executeLargeBatch();
        statement.addBatch(INSERT_SQL);
        statement.addBatch(UPDATE_SQL);
        statement.executeLargeBatch();
        statement.addBatch(SELECT_SQL);
        statement.addBatch(DELETE_SQL);
        statement.executeLargeBatch();

        // executeLargeUpdate
        statement.executeLargeUpdate(INSERT_SQL);
        statement.executeLargeUpdate(UPDATE_SQL);
        statement.executeLargeUpdate(DELETE_SQL);

        statement.close();
        connection.close();
    }

    private void testPreparedStatementPassed(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        Assert.assertTrue(metaData instanceof LocalizedDatabaseMetaDataImpl);
        String url = ((LocalizedDatabaseMetaDataImpl) metaData).getExtendedURL();
        Assert.assertTrue(url.endsWith("UTF-8"));

        // executeQuery
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SQL_TEMPLATE);
        preparedStatement.setString(1, SELECT_PARAM);
        preparedStatement.executeQuery();
        preparedStatement.close();

        // executeUpdate
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(INSERT_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, INSERT_PARAM);
            innerPreparedStatement.executeUpdate();
        }
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(UPDATE_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, UPDATE_PARAM);
            innerPreparedStatement.executeUpdate();
        }
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(DELETE_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, DELETE_PARAM);
            innerPreparedStatement.executeUpdate();
        }

        // execute
        String multiSql = SELECT_SQL_TEMPLATE + ";" + SELECT_SQL_TEMPLATE;
        PreparedStatement preparedStatement2 = connection.prepareStatement(multiSql);
        preparedStatement2.setString(1, SELECT_PARAM);
        preparedStatement2.setString(2, SELECT_PARAM2);
        preparedStatement2.execute();
        preparedStatement2.close();
        multiSql = SELECT_SQL_TEMPLATE + ";" + INSERT_SQL_TEMPLATE;
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(multiSql)) {
            innerPreparedStatement.setString(1, SELECT_PARAM);
            innerPreparedStatement.setString(2, INSERT_PARAM);
            innerPreparedStatement.execute();
        }
        multiSql = UPDATE_SQL_TEMPLATE + ";" + DELETE_SQL_TEMPLATE;
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(multiSql)) {
            innerPreparedStatement.setString(1, UPDATE_PARAM);
            innerPreparedStatement.setString(2, DELETE_PARAM);
            innerPreparedStatement.execute();
        }

        // executeLargeUpdate
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(INSERT_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, INSERT_PARAM);
            innerPreparedStatement.executeLargeUpdate();
        }
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(UPDATE_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, UPDATE_PARAM);
            innerPreparedStatement.executeLargeUpdate();
        }
        try (PreparedStatement innerPreparedStatement = connection.prepareStatement(DELETE_SQL_TEMPLATE)) {
            innerPreparedStatement.setString(1, DELETE_PARAM);
            innerPreparedStatement.executeLargeUpdate();
        }

        connection.close();
    }

    private DataSource getNormalDataSource() {
        DataSourceConfigure config = getDataSourceConfig();
        return new LocalizedDataSource(config.getName(), config);
    }

    private DataSource getBlockingDataSource() {
        DataSourceConfigure config = getDataSourceConfig();
        return new LocalizedDataSource(new ConstantLocalizationValidator(false) {
            @Override
            public ValidationResult validateRequest(boolean isUpdateOperation) {
                return isUpdateOperation ? super.validateRequest(true) :
                        new ValidationResult(true, "ucs pass", "dal pass");
            }

            @Override
            public LocalizationConfig getLocalizationConfig() {
                return new LocalizationConfigImpl(1, TEST_ZONE, LocalizationState.ACTIVE);
            }
        }, config.getName(), config);
    }

    private DataSourceConfigure getDataSourceConfig() {
        Properties p = new Properties();
        p.setProperty("userName", "root");
        p.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p.setProperty("connectionUrl", "jdbc:mysql://10.32.20.128:3306/llj_test?useUnicode=true&characterEncoding=UTF-8");
        p.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        return new DataSourceConfigure("llj_test", p);
    }

}
