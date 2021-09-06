package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;

import java.sql.Connection;
import java.sql.SQLException;

public class DalConnection {
    private Integer oldIsolationLevel;
    private Integer newIsolationLevel;
    private Connection conn;
    private boolean master;
    private String shardId;
    private DbMeta meta;
    private DalLogger logger;

    public DalConnection(Connection conn, boolean master, String shardId, DbMeta meta) throws SQLException {
        this.oldIsolationLevel = conn.getTransactionIsolation();
        this.conn = conn;
        this.master = master;
        this.shardId = shardId;
        this.meta = meta;
        this.logger = DalClientFactory.getDalLogger();
    }

    public Connection getConn() {
        return conn;
    }

    public boolean isMaster() {
        return master;
    }

    public DbMeta getMeta() {
        return meta;
    }

    public String getShardId() {
        return shardId;
    }

    public String getDatabaseName() throws SQLException {
        return meta.getDatabaseName();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (conn.getAutoCommit() != autoCommit)
            conn.setAutoCommit(autoCommit);
    }

    public void applyHints(DalHints hints) throws SQLException {
        Integer level = hints.getInt(DalHintEnum.isolationLevel);

        if (level == null || oldIsolationLevel.equals(level))
            return;

        newIsolationLevel = level;
        conn.setTransactionIsolation(level);
    }

    public void error(Throwable e) {
    }

    public void close() {
        try {
            if (conn == null || conn.isClosed())
                return;
        } catch (Throwable e) {
            logger.error("Restore connection isolation level failed!", e);
        }

        try {
            if (newIsolationLevel != null)
                conn.setTransactionIsolation(oldIsolationLevel);
        } catch (Throwable e) {
            logger.error("Restore connection isolation level failed!", e);
        }

        try {
            conn.close();
        } catch (Throwable e) {
            logger.error("Close connection failed!", e);
        }
        conn = null;
    }

}
