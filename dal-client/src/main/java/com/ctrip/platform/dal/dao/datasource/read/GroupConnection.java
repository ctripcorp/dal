package com.ctrip.platform.dal.dao.datasource.read;


import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.database.Database;
import com.ctrip.platform.dal.cluster.shard.DatabaseShard;
import com.ctrip.platform.dal.cluster.shard.read.RouterType;
import com.ctrip.platform.dal.common.enums.SqlType;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.IntegratedConfigProvider;
import com.ctrip.platform.dal.dao.helper.SqlUtils;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

public class GroupConnection extends AbstractUnsupportedOperationConnection {

    private static final String SQL_FORCE_WRITE_HINT = "/*+dal:write*/";

    private ClusterInfo clusterInfo;
    private Integer shardIndex;

    private DataSource writeDataSource;
    Map<Database, DataSource> readDataSource;

    private volatile GroupDataSource groupDataSource;
    private volatile Connection rConnection;
    private volatile Connection wConnection;
    private RouterType routerType;
    private List<Statement> openedStatements = new ArrayList<Statement>();
    private int transactionIsolation = -1;
    private boolean autoCommit = true;
    private boolean closed = false;
    private String catalog;
    private String schema;


    public GroupConnection(GroupDataSource groupDataSource) {
        this.groupDataSource = groupDataSource;
        this.clusterInfo = groupDataSource.clusterInfo;
        this.writeDataSource = groupDataSource.writeDataSource;
        this.readDataSource = groupDataSource.readDataSource;
        this.shardIndex = clusterInfo.getShardIndex();
        this.routerType = groupDataSource.routerType;
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

    protected Connection getReadConnection() throws SQLException {
        if (rConnection == null) {
            synchronized (this) {
                if (rConnection == null) {
                    rConnection = pickRead();
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

    protected Connection pickRead() throws SQLException {
        DatabaseShard databaseShard = clusterInfo.getCluster().getDatabaseShard(shardIndex);
        HostSpec hostSpec = databaseShard.getRouteStrategy().pickRead(buildContext());
        DataSource dataSource = readDataSource.get(databaseShard.parseFromHostSpec(hostSpec));
        if (dataSource == null) {
            synchronized (groupDataSource) {
                dataSource = readDataSource.get(databaseShard.parseFromHostSpec(hostSpec));
                if (dataSource == null) {
                    groupDataSource.init();
                    this.readDataSource = groupDataSource.readDataSource;
                    this.readDataSource.get(databaseShard.parseFromHostSpec(hostSpec));
                }
            }
        }
        System.out.println(hostSpec);
        return dataSource.getConnection();
    }

    protected Map<String, Object> buildContext() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("slaveOnly", false);
        map.put("isPro", false);
        return map;
    }

    protected Connection getWriteConnection() throws SQLException {
        System.out.println("master");
        if (wConnection == null) {
            synchronized (this) {
                if (wConnection == null) {
                    wConnection = writeDataSource.getConnection();

                    if (wConnection.getAutoCommit() != autoCommit) {
                        wConnection.setAutoCommit(autoCommit);
                    }
                    if (transactionIsolation > 0) {
                        wConnection.setTransactionIsolation(transactionIsolation);
                    }
                    if (catalog != null) {
                        wConnection.setCatalog(catalog);
                    }
                    if (schema != null) {
                        wConnection.setSchema(schema);
                    }
                }
            }
        }

        return wConnection;
    }

    Connection getRealConnection(String sql, boolean forceWrite) throws SQLException {
        if (this.routerType == RouterType.SLAVE_ONLY) {
            return getReadConnection();
        } else if (this.routerType == RouterType.MASTER_ONLY) {
            return getWriteConnection();
        }

        if (forceWrite) {
            return getWriteConnection();
        } else if (!autoCommit || StringUtils.trimToEmpty(sql).contains(SQL_FORCE_WRITE_HINT)) {
            return getWriteConnection();
        }

        SqlType sqlType = SqlUtils.getSqlType(sql);
        System.out.println(sql + ":" + sqlType.value());
        if (sqlType.isRead()) {
            return getReadConnection();
        } else {
            return getWriteConnection();
        }
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return prepareCall(sql, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkClosed();
        if (this.autoCommit == autoCommit) {
            return;
        }
        this.autoCommit = autoCommit;
        if (this.wConnection != null) {
            this.wConnection.setAutoCommit(autoCommit);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        checkClosed();
        return this.autoCommit;
    }

    @Override
    public void commit() throws SQLException {
        checkClosed();
        if (autoCommit) {
            return;
        }

        if (wConnection != null) {
            wConnection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        checkClosed();
        if (autoCommit) {
            return;
        }

        if (wConnection != null) {
            wConnection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (closed) {
            return;
        }
        closed = true;

        final List<SQLException> exceptions = new LinkedList<SQLException>();

        try {
            for (Statement stmt : openedStatements) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    exceptions.add(e);
                }
            }
            try {
                if (rConnection != null && !rConnection.isClosed()) {
                    rConnection.close();
                }
            } catch (SQLException e) {
                exceptions.add(e);
            }
            try {
                if (wConnection != null && !wConnection.isClosed()) {
                    wConnection.close();
                }
            } catch (SQLException e) {
                exceptions.add(e);
            }
        } finally {
            openedStatements.clear();
            rConnection = null;
            wConnection = null;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        checkClosed();

        if (rConnection != null) {
            return rConnection.getMetaData();
        }

        if(wConnection != null) {
            return wConnection.getMetaData();
        }

        if(RouterType.SLAVE_ONLY == routerType) {
            return getReadConnection().getMetaData();
        }

        return this.getWriteConnection().getMetaData();
    }


    @Override
    public boolean isReadOnly() throws SQLException {
        if (routerType != null && routerType == RouterType.SLAVE_ONLY) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.catalog = catalog;
    }

    @Override
    public String getCatalog() throws SQLException {
        if(rConnection != null) {
            return rConnection.getCatalog();
        }

        if(wConnection != null) {
            return wConnection.getCatalog();
        }

        if(RouterType.SLAVE_ONLY == routerType) {
            return getReadConnection().getCatalog();
        }

        return getWriteConnection().getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        checkClosed();
        this.transactionIsolation = level;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.transactionIsolation;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        if (rConnection != null) {
            return rConnection.getWarnings();
        } else if (wConnection != null) {
            return wConnection.getWarnings();
        } else {
            return null;
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
        if (rConnection != null) {
            rConnection.clearWarnings();
        }
        if (wConnection != null) {
            wConnection.clearWarnings();
        }
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
        return prepareCall(sql, resultSetType, resultSetConcurrency, Integer.MIN_VALUE);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        checkClosed();

        if (wConnection == null) {
            throw new SQLException("savePoint failed wConnction is null");
        }
        setAutoCommit(false);

        return wConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        checkClosed();
        setAutoCommit(false);

        if (wConnection == null) {
            throw new SQLException("savePoint failed wConnction is null");
        }

        return wConnection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        checkClosed();
        if (autoCommit) {
            return;
        }

        if (wConnection != null) {
            wConnection.rollback(savepoint);
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkClosed();
        if (autoCommit) {
            return;
        }

        if (wConnection != null) {
            wConnection.releaseSavepoint(savepoint);
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        GroupStatement stmt = (GroupStatement) createStatement(resultSetType, resultSetConcurrency);
        stmt.setResultSetHoldability(resultSetHoldability);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        GroupPreparedStatement pstmt = (GroupPreparedStatement) prepareStatement(sql, resultSetType, resultSetConcurrency);
        pstmt.setResultSetHoldability(resultSetHoldability);
        return pstmt;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkClosed();
        CallableStatement cstmt = null;
        // 存储过程强制走写库
        Connection conn = getRealConnection(sql, true);
        cstmt = getCallableStatement(conn, sql, resultSetType, resultSetConcurrency, resultSetHoldability);

        openedStatements.add(cstmt);
        return cstmt;
    }

    private CallableStatement getCallableStatement(Connection conn, String sql, int resultSetType,
                                                   int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (resultSetType == Integer.MIN_VALUE) {
            return conn.prepareCall(sql);
        } else if (resultSetHoldability == Integer.MIN_VALUE) {
            return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        } else {
            return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        GroupPreparedStatement pstmt = (GroupPreparedStatement) prepareStatement(sql);
        pstmt.setAutoGeneratedKeys(autoGeneratedKeys);
        return pstmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        GroupPreparedStatement pstmt = (GroupPreparedStatement) prepareStatement(sql);
        pstmt.setColumnIndexes(columnIndexes);
        return pstmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        GroupPreparedStatement pstmt = (GroupPreparedStatement) prepareStatement(sql);
        pstmt.setColumnNames(columnNames);
        return pstmt;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        this.schema = schema;
    }

    @Override
    public String getSchema() throws SQLException {
        if(rConnection != null) {
            return rConnection.getSchema();
        }

        if(wConnection != null) {
            return wConnection.getSchema();
        }

        if(RouterType.SLAVE_ONLY == routerType) {
            return getReadConnection().getSchema();
        }
        return getWriteConnection().getSchema();
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
