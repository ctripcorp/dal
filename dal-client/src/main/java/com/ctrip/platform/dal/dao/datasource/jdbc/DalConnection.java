package com.ctrip.platform.dal.dao.datasource.jdbc;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.datasource.ClusterDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;
import org.apache.tomcat.jdbc.pool.PooledConnection;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public class DalConnection implements Connection {

    private static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private Connection connection;
    private final AtomicReference<SQLException> discardCauseRef = new AtomicReference<>();
    private RefreshableDataSource dataSource;

    public DalConnection(Connection connection, RefreshableDataSource dataSource) {
        this.connection = connection;
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        return connection;
    }

    private boolean isDiscardException(Throwable t) {
        Throwable t1 = t;
        while (t1 instanceof DalException) {
            t1 = t1.getCause();
        }
        while (t1 != null && !(t1 instanceof SQLException)) {
            t1 = t1.getCause();
        }
        if (t1 == null)
            return false;
        DatabaseCategory dbCategory = dataSource.getSingleDataSource().getDataSourceConfigure().getDatabaseCategory();
        SQLException se = (SQLException) t1;
        if (dbCategory.isSpecificException(se))
            return true;
        return isDiscardException(se.getNextException());
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new DalStatement(connection.createStatement(), this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new DalPreparedStatement(connection.prepareStatement(sql), this, sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return new DalCallableStatement(connection.prepareCall(sql), this, sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        innerExecute(() -> connection.commit());
    }

    @Override
    public void rollback() throws SQLException {
        innerExecute(() -> connection.rollback());
    }

    @Override
    public void close() throws SQLException {
        SQLException discardCause = discardCauseRef.get();
        if (discardCause != null) {
            try {
                markDiscard(discardCause);
            } catch (Throwable t) {
                LOGGER.warn("mark connection discarded exception", t);
            } finally {
                discardCauseRef.set(null);
            }
        }
        connection.close();
    }

    private void markDiscard(SQLException cause) throws SQLException {
        long startTime = System.currentTimeMillis();
        PooledConnection conn = connection.unwrap(PooledConnection.class);
        conn.setDiscarded(true);
        String connUrl = conn.getPoolProperties().getUrl();
        String logName = String.format("Connection::discardConnection:%s", LoggerHelper.getSimplifiedDBUrl(connUrl));
        LOGGER.logTransaction(DalLogTypes.DAL_DATASOURCE, logName, connUrl, startTime);
        LOGGER.warn(String.format("connection marked discarded: %s", connUrl), cause);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return innerExecute(() -> connection.isClosed());
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        if (metaData == null)
            return null;
        DataSourceIdentity id = dataSource.getId();
        if (id instanceof ClusterDataSourceIdentity)
            return new ClusterDatabaseMetaDataImpl(metaData, ((ClusterDataSourceIdentity) id).getDatabase());
        return metaData;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new DalStatement(connection.createStatement(resultSetType, resultSetConcurrency), this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new DalPreparedStatement(connection.prepareStatement(sql, resultSetType, resultSetConcurrency), this, sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new DalCallableStatement(connection.prepareCall(sql, resultSetType, resultSetConcurrency), this, sql);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        innerExecute(() -> connection.rollback(savepoint));
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new DalStatement(connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability), this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new DalPreparedStatement(connection.prepareStatement(sql, resultSetType, resultSetConcurrency), this, sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new DalCallableStatement(connection.prepareCall(sql, resultSetType, resultSetHoldability), this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return new DalPreparedStatement(connection.prepareStatement(sql, autoGeneratedKeys), this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return new DalPreparedStatement(connection.prepareStatement(sql, columnIndexes), this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return new DalPreparedStatement(connection.prepareStatement(sql, columnNames), this, sql);
    }

    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return innerExecute(() -> connection.isValid(timeout));
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        connection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        innerExecute(() -> connection.abort(executor));
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }

    protected void innerExecute(SqlRunnable task) throws SQLException {
        SQLException error = null;
        try {
            task.run();
        } catch (SQLException e) {
            error = e;
            throw e;
        } finally {
            handleExceptionForConnection(error);
        }
    }

    protected void innerExecute(SqlRunnable task, boolean isUpdateOperation) throws SQLException {
        SQLException error = null;
        try {
            task.run();
        } catch (SQLException e) {
            error = e;
            throw e;
        } finally {
            handleExceptionForConnection(error);
            handleExceptionForDataSource(error, isUpdateOperation);
        }
    }

    protected <T> T innerExecute(SqlCallable<T> task) throws SQLException {
        SQLException error = null;
        try {
            return task.call();
        } catch (SQLException e) {
            error = e;
            throw e;
        } finally {
            handleExceptionForConnection(error);
        }
    }

    protected <T> T innerExecute(SqlCallable<T> task, boolean isUpdateOperation) throws SQLException {
        SQLException error = null;
        try {
            return task.call();
        } catch (SQLException e) {
            error = e;
            throw e;
        } finally {
            handleExceptionForConnection(error);
            handleExceptionForDataSource(error, isUpdateOperation);
        }
    }

    private void handleExceptionForConnection(SQLException e) {
        try {
            if (isDiscardException(e))
                discardCauseRef.set(e);
        } catch (Throwable t) {
            LOGGER.warn("DalConnection handleException exception", t);
        }
    }

    private void handleExceptionForDataSource(SQLException e, boolean isUpdateOperation) {
        try {
            dataSource.handleException(e, isUpdateOperation);
        } catch (Throwable t) {
            LOGGER.warn("RefreshableDataSource handleException exception", t);
        }
    }

}
