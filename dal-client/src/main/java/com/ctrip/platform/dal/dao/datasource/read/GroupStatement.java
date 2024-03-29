package com.ctrip.platform.dal.dao.datasource.read;

import com.ctrip.platform.dal.common.enums.SqlType;
import com.ctrip.platform.dal.dao.helper.SqlUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupStatement extends AbstractUnsupportedOperationStatement implements Statement {

    protected GroupConnection groupConnection;

    protected Statement innerStatement = null;

    protected GroupResultSet currentResultSet = null;

    protected List<String> batchedSqls;

    protected boolean closed = false;

    protected int fetchSize;

    protected int maxRows;

    protected boolean moreResults = false;

    protected int queryTimeout = 0;

    protected int resultSetConcurrency = ResultSet.CONCUR_READ_ONLY;

    protected int resultSetHoldability = -1;

    protected int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

    protected int updateCount = -1;

    public GroupStatement(GroupConnection connection) {
        this.groupConnection = connection;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        checkClosed();
        closeCurrentResultSet();

        Connection conn = this.groupConnection.getRealConnection(sql, false);

        return executeQueryOnConnection(conn, sql);
    }

    private ResultSet executeQueryOnConnection(Connection conn, String sql) throws SQLException {
        Statement stmt = createInnerStatement(conn, false);
        currentResultSet = new GroupResultSet(stmt.executeQuery(sql));
        return currentResultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return executeUpdateInternal(sql, -1, null, null);
    }

    private int executeUpdateInternal(final String sql, final int autoGeneratedKeys, final int[] columnIndexes,
                                      final String[] columnNames) throws SQLException {
        checkClosed();
        closeCurrentResultSet();

        Connection conn = this.groupConnection.getRealConnection(sql, true);

        updateCount = executeUpdateOnConnection(conn, sql, autoGeneratedKeys, columnIndexes, columnNames);

        return updateCount;
    }

    private int executeUpdateOnConnection(Connection conn, String sql, int autoGeneratedKeys, int[] columnIndexes,
                                          String[] columnNames) throws SQLException {
        Statement stmt = createInnerStatement(conn, false);

        if (autoGeneratedKeys == -1 && columnIndexes == null && columnNames == null) {
            return stmt.executeUpdate(sql);
        } else if (autoGeneratedKeys != -1) {
            return stmt.executeUpdate(sql, autoGeneratedKeys);
        } else if (columnIndexes != null) {
            return stmt.executeUpdate(sql, columnIndexes);
        } else if (columnNames != null) {
            return stmt.executeUpdate(sql, columnNames);
        } else {
            return stmt.executeUpdate(sql);
        }
    }

    @Override
    public void close() throws SQLException {
        if (closed) {
            return;
        }
        closed = true;

        try {
            if (currentResultSet != null) {
                currentResultSet.close();
            }
        } finally {
            currentResultSet = null;
        }

        try {
            if (this.innerStatement != null) {
                this.innerStatement.close();
            }
        } finally {
            this.innerStatement = null;
        }
    }

    protected void closeCurrentResultSet() throws SQLException {
        if (currentResultSet != null) {
            try {
                currentResultSet.close();
            } catch (SQLException e) {
                // ignore it
            } finally {
                currentResultSet = null;
            }
        }
    }

    private Statement createInnerStatement(Connection conn, boolean isBatch) throws SQLException {
        Statement stmt;
        if (isBatch) {
            stmt = conn.createStatement();
        } else {
            int tmpResultSetHoldability = this.resultSetHoldability;
            if (tmpResultSetHoldability == -1) {
                tmpResultSetHoldability = conn.getHoldability();
            }

            stmt = conn.createStatement(this.resultSetType, this.resultSetConcurrency, tmpResultSetHoldability);
        }

        stmt.setQueryTimeout(queryTimeout);
        stmt.setFetchSize(fetchSize);
        stmt.setMaxRows(maxRows);

        setInnerStatement(stmt);
        return stmt;
    }

    protected void setInnerStatement(Statement innerStatement) {
        if (this.innerStatement != null) {
            try {
                this.innerStatement.close();
            } catch (SQLException e) {
                // ignore it
            }
        }
        this.innerStatement = innerStatement;
    }

    @Override
    public int getMaxRows() throws SQLException {
        return maxRows;
    }

    @Override
    public void setMaxRows(int maxRows) throws SQLException {
        this.maxRows = maxRows;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return queryTimeout;
    }

    @Override
    public void setQueryTimeout(int queryTimeout) throws SQLException {
        this.queryTimeout = queryTimeout;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        if (innerStatement != null) {
            return innerStatement.getWarnings();
        }
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
        if (innerStatement != null) {
            innerStatement.clearWarnings();
        }
    }

    private boolean executeInternal(String sql, int autoGeneratedKeys, int[] columnIndexes, String[] columnNames)
            throws SQLException {
        SqlType sqlType = SqlUtils.getSqlType(sql);
        if (sqlType.isQuery()) {
            executeQuery(sql);
            return true;
        } else {
            if (autoGeneratedKeys == -1 && columnIndexes == null && columnNames == null) {
                executeUpdate(sql);
            } else if (autoGeneratedKeys != -1) {
                executeUpdate(sql, autoGeneratedKeys);
            } else if (columnIndexes != null) {
                executeUpdate(sql, columnIndexes);
            } else if (columnNames != null) {
                executeUpdate(sql, columnNames);
            } else {
                executeUpdate(sql);
            }

            return false;
        }
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return executeInternal(sql, -1, null, null);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return currentResultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return this.updateCount;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return moreResults;
    }

    @Override
    public void setFetchSize(int fetchSize) throws SQLException {
        this.fetchSize = fetchSize;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return this.fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.resultSetConcurrency;
    }

    public void setResultSetConcurrency(int resultSetConcurrency) {
        this.resultSetConcurrency = resultSetConcurrency;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return this.resultSetType;
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        checkClosed();

        if (batchedSqls == null) {
            batchedSqls = new ArrayList<String>();
        }
        if (sql != null) {
            batchedSqls.add(sql);
        }
    }

    protected void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("No operations allowed after statement closed.");
        }
    }

    @Override
    public void clearBatch() throws SQLException {
        checkClosed();
        if (batchedSqls != null) {
            batchedSqls.clear();
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        try {
            checkClosed();
            closeCurrentResultSet();

            if (batchedSqls == null || batchedSqls.isEmpty()) {
                return new int[0];
            }

            Connection conn = this.groupConnection.getRealConnection(null, true);
            return executeBatchOnConnection(conn, batchedSqls);
        } finally {
            if (batchedSqls != null) {
                batchedSqls.clear();
            }
        }
    }

    private int[] executeBatchOnConnection(final Connection conn, final List<String> batchedSqls) throws SQLException {
        Statement stmt = createInnerStatement(conn, true);
        for (String sql : batchedSqls) {
            stmt.addBatch(sql);
        }
        return stmt.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.groupConnection;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        if (this.innerStatement != null) {
            return new GroupResultSet(this.innerStatement.getGeneratedKeys());
        } else {
            throw new SQLException("No update operations executed before getGeneratedKeys");
        }
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return executeUpdateInternal(sql, autoGeneratedKeys, null, null);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return executeUpdateInternal(sql, -1, columnIndexes, null);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return executeUpdateInternal(sql, -1, null, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return executeInternal(sql, autoGeneratedKeys, null, null);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return executeInternal(sql, -1, columnIndexes, null);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return executeInternal(sql, -1, null, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.resultSetHoldability;
    }

    public void setResultSetHoldability(int resultSetHoldability) {
        this.resultSetHoldability = resultSetHoldability;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return (T) this;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.getClass().isAssignableFrom(iface);
    }
}
