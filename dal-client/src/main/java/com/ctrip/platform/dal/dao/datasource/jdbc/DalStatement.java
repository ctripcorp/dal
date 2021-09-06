package com.ctrip.platform.dal.dao.datasource.jdbc;

import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.log.OperationType;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;
import com.ctrip.platform.dal.dao.datasource.read.GroupConnection;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.SqlUtils;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.*;
import java.util.Collection;

public class DalStatement implements Statement {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private Statement statement;
    protected DalConnection connection;
    private SqlContext context;
    protected StatementParameters logParameters = null;

    public DalStatement(Statement statement, DalConnection connection, SqlContext context) {
        this.statement = statement;
        this.connection = connection;
        this.context = context;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return executeStatement(() -> statement.executeQuery(sql), false, sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return executeStatement(() -> statement.executeUpdate(sql), true, sql);
    }

    @Override
    public void close() throws SQLException {
        innerExecute(() -> statement.close());
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return statement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        statement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return statement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        statement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        statement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return statement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        statement.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        innerExecute(() -> statement.cancel());
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return statement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        statement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        statement.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return executeStatement(() -> statement.execute(sql), isUpdateOperation(sql), sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return statement.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return statement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return innerExecute(() -> statement.getMoreResults());
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        statement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return statement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        statement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return statement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return statement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return statement.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        statement.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        statement.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return executeStatement(() -> statement.executeBatch(), true);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return statement.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return innerExecute(() -> statement.getMoreResults(current));
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return innerExecute(() -> statement.getGeneratedKeys());
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeStatement(() -> statement.executeUpdate(sql, autoGeneratedKeys), true, sql);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeStatement(() -> statement.executeUpdate(sql, columnIndexes), true, sql);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeStatement(() -> statement.executeUpdate(sql, columnNames), true, sql);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return executeStatement(() -> statement.execute(sql, autoGeneratedKeys), isUpdateOperation(sql), sql);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return executeStatement(() -> statement.execute(sql, columnIndexes), isUpdateOperation(sql), sql);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return executeStatement(() -> statement.execute(sql, columnNames), isUpdateOperation(sql), sql);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return statement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return innerExecute(() -> statement.isClosed());
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        statement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return statement.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        statement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return statement.isCloseOnCompletion();
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        return statement.getLargeUpdateCount();
    }

    @Override
    public void setLargeMaxRows(long max) throws SQLException {
        statement.setLargeMaxRows(max);
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        return statement.getLargeMaxRows();
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        return executeStatement(() -> statement.executeLargeBatch(), true);
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return executeStatement(() -> statement.executeLargeUpdate(sql), true, sql);
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeStatement(() -> statement.executeLargeUpdate(sql, autoGeneratedKeys), true, sql);
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeStatement(() -> statement.executeLargeUpdate(sql, columnIndexes), true, sql);
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeStatement(() -> statement.executeLargeUpdate(sql, columnNames), true, sql);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return statement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return statement.isWrapperFor(iface);
    }

    protected void innerExecute(SqlRunnable task) throws SQLException {
        connection.innerExecute(task);
    }

    protected void innerExecute(SqlRunnable task, boolean isUpdateOperation) throws SQLException {
        connection.innerExecute(task, isUpdateOperation);
    }

    protected <T> T innerExecute(SqlCallable<T> task) throws SQLException {
        return connection.innerExecute(task);
    }

    protected <T> T innerExecute(SqlCallable<T> task, boolean isUpdateOperation) throws SQLException {
        try {
            if (DalPropertiesManager.getInstance().getDalPropertiesLocator().enableUcsContextLog())
                LOGGER.logRequestContext();
        } catch (Throwable t) {
            // ignore
        }
        T result = connection.innerExecute(task, isUpdateOperation);
        getSqlContext().populateQueryRows(fetchQueryRows(result));
        return result;
    }

    private int fetchQueryRows(Object result) {
        if (result == null) {
            return 0;
        }

        if (result instanceof Collection<?>) {
            return ((Collection<?>) result).size();
        }

        return 1;
    }

    protected <T> T executeStatement(SqlCallable<T> task, boolean isUpdateOperation, String sql) throws SQLException {
        context.populateSql(sql);
        return executeStatement(task, isUpdateOperation);
    }

    protected <T> T executeStatement(SqlCallable<T> task, boolean isUpdateOperation) throws SQLException {
        beforeExecution(isUpdateOperation ? OperationType.UPDATE : OperationType.QUERY);
        Throwable errorIfAny = null;
        try {
            return innerExecute(task, isUpdateOperation);
        } catch (Throwable t) {
            errorIfAny = t;
            throw t;
        } finally {
            afterExecution(errorIfAny);
        }
    }

    private void beforeExecution(OperationType operation) {
        try {
            context.populateCaller();
            context.populateOperationType(operation);
            context.startExecution();
            context.populateSqlTransaction(System.currentTimeMillis());
            context.populateReadStrategy(GroupConnection.getLogContext().getReadStrategy());
            context.populateParameters(logParameters);
        } catch (Throwable t) {
            // ignore
        }
    }

    private void afterExecution(Throwable errorIfAny) {
        try {
            context.endExecution(errorIfAny);
        } catch (Throwable t) {
            // ignore
        }
    }

    public SqlContext getSqlContext() {
        return context;
    }

    protected boolean isUpdateOperation(String sql) {
        return !SqlUtils.isReadOperation(sql);
    }

}
