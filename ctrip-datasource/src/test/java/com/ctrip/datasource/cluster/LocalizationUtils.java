package com.ctrip.datasource.cluster;

import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;
import org.junit.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class LocalizationUtils {

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

    public static void testStatementBlocked(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
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

    public static void testPreparedStatementBlocked(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();

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

    public static void testStatementPassed(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
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

    public static void testPreparedStatementPassed(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();

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

}
