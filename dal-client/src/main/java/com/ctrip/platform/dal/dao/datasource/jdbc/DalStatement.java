package com.ctrip.platform.dal.dao.datasource.jdbc;

import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;

import java.sql.*;

public class DalStatement implements Statement {
    private Statement statement;
    private RefreshableDataSource dataSource;

    public DalStatement(Statement statement, RefreshableDataSource dataSource) {
        this.statement = statement;
        this.dataSource = dataSource;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        SQLException exception = null;
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        SQLException exception = null;
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }

    }

    @Override
    public void close() throws SQLException {
        statement.close();
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
        statement.cancel();
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
        SQLException exception = null;
        try {
            return statement.execute(sql);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
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
        return statement.getMoreResults();
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
        SQLException exception = null;
        try {
            return statement.executeBatch();
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return statement.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return statement.getMoreResults();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return statement.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        SQLException exception = null;
        try {
            return statement.executeUpdate(sql, autoGeneratedKeys);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        SQLException exception = null;
        try {
            return statement.executeUpdate(sql, columnIndexes);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        SQLException exception = null;
        try {
            return statement.executeUpdate(sql, columnNames);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        SQLException exception = null;
        try {
            return statement.execute(sql, autoGeneratedKeys);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        SQLException exception = null;
        try {
            return statement.execute(sql, columnIndexes);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        SQLException exception = null;
        try {
            return statement.execute(sql, columnNames);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return statement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return statement.isClosed();
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
        SQLException exception = null;
        try {
            return statement.executeLargeBatch();
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        SQLException exception = null;
        try {
            return statement.executeLargeUpdate(sql);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        SQLException exception = null;
        try {
            return statement.executeLargeUpdate(sql, autoGeneratedKeys);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        SQLException exception = null;
        try {
            return statement.executeLargeUpdate(sql, columnIndexes);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        SQLException exception = null;
        try {
            return statement.executeLargeUpdate(sql, columnNames);
        } catch (SQLException e) {
            exception = e;
            throw e;
        } finally {
            dataSource.handleException(exception);
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return statement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return statement.isWrapperFor(iface);
    }
}
