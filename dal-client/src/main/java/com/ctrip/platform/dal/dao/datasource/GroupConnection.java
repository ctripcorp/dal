package com.ctrip.platform.dal.dao.datasource;


import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalConnection;
import com.ctrip.platform.dal.dao.datasource.jdbc.DalDataSource;
import com.ctrip.platform.dal.dao.datasource.log.SqlContext;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class GroupConnection implements Connection {

    private DataSource writeDataSource;
    Map<Database, DataSource> readDataSource;
    private volatile Connection rConnection;
    private volatile Connection wConnection;
    private List<Statement> openedStatements = new ArrayList<Statement>();
    private int transactionIsolation = -1;
    private boolean autoCommit = true;
    private boolean closed = false;
    private String catalog;
    private String schema;


    public GroupConnection(DataSource writeDataSource, Map<Database, DataSource> readDataSource) {
        this.writeDataSource = writeDataSource;
        this.readDataSource = readDataSource;
    }

    private void checkClosed() throws SQLException {
        if (closed) {
            throw new SQLException("No operations allowed after connection closed.");
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        checkClosed();
        Statement stmt = new GroupStatement(this);
        openedStatements.add(stmt);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkClosed();
        PreparedStatement pstmt = new GroupPreparedStatement(this, sql);
        openedStatements.add(pstmt);
        return pstmt;
    }

    private Connection getReadConnection() throws SQLException {
        if (rConnection == null) {
            synchronized (this) {
                if (rConnection == null) {
                    rConnection = readDataSource.values().iterator().next().getConnection();
                }
                if (catalog != null) {
                    rConnection.setCatalog(catalog);
                }
                if (schema != null) {
                    rConnection.setSchema(schema);
                }
            }
        }

        return rConnection;
    }

    Connection getRealConnection(String sql, boolean forceWrite) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return null;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {

    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        GroupStatement stmt = (GroupStatement) createStatement();
        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        GroupPreparedStatement pstmt = (GroupPreparedStatement) prepareStatement(sql);
        pstmt.setResultSetType(resultSetType);
        pstmt.setResultSetConcurrency(resultSetConcurrency);
        return pstmt;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        GroupPreparedStatement pstmt = (GroupPreparedStatement) prepareStatement(sql, resultSetType, resultSetConcurrency);
        pstmt.setResultSetHoldability(resultSetHoldability);
        return pstmt;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        GroupPreparedStatement pstmt = (GroupPreparedStatement) prepareStatement(sql);
        pstmt.setAutoGeneratedKeys(autoGeneratedKeys);
        return pstmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {

    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
