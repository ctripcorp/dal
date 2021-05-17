package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ctrip.platform.dal.common.enums.DalTransactionStatus;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.ClusterDatabaseSet;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.DalTransactionHelper;
import com.ctrip.platform.dal.dao.log.Callback;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalTransactionConflictException;
import com.ctrip.platform.dal.exceptions.ErrorCode;
import com.ctrip.platform.dal.exceptions.TransactionSystemException;

public class DalTransaction {
    private String logicDbName;
    private DalConnection connHolder;
    private List<DalTransactionListener> listeners;
    private int level = 0;
    private boolean rolledBack = false;
    private boolean completed = false;
    private boolean rollbackOnly = false;
    private DalLogger logger;

    private DalTransactionStatus status = DalTransactionStatus.Initial;
    private Map<Integer, DalTransactionStatusWrapper> transactionStatusWrapperMap = new ConcurrentHashMap<>();
    private DalTransactionHelper transactionHelper = DalTransactionHelper.getInstance();
    private static final int FIRST_LEVEL = 1;

    private static final String SQL_Transaction = "SQL.transaction";
    private static final String DAL_TRANSACTION_SET_ROLLBACK_ONLY = "Transaction::setRollbackOnly:%s";
    private static final String DAL_TRANSACTION_EXECUTE_ROLLBACK_ONLY = "Transaction::executeRollbackOnly:%s";
    private static final ILogger iLogger = DalElementFactory.DEFAULT.getILogger();

    private List<RollbackOnlyWrapper> rollbackOnlyWrapperList = new CopyOnWriteArrayList<>();

    public DalTransaction(DalConnection connHolder, String logicDbName) throws SQLException {
        this.logicDbName = logicDbName;
        this.connHolder = connHolder;
        connHolder.getConn().setAutoCommit(false);
        this.logger = DalClientFactory.getDalLogger();
    }

    public void validate(String desiganateLogicDbName, String desiganateShard) throws SQLException {
        if (desiganateLogicDbName == null || desiganateLogicDbName.length() == 0)
            throw new DalException(ErrorCode.LogicDbEmpty);

        if (!desiganateLogicDbName.equals(this.logicDbName))
            throw new DalException(ErrorCode.TransactionDistributed, this.logicDbName, desiganateLogicDbName);

        String curShard = connHolder.getShardId();
        if (curShard == null)
            return;

        if (desiganateShard == null)
            return;

        try{
            DalConfigure dalConfigure = DalClientFactory.getDalConfigure();
            DatabaseSet databaseSet = dalConfigure.getDatabaseSet(desiganateLogicDbName);
            if (databaseSet instanceof ClusterDatabaseSet && Integer.valueOf(curShard).equals(Integer.valueOf(desiganateShard)))
                return;
        } catch (Exception e) {
            // needn't handle
        }

        if (!curShard.equals(desiganateShard))
            throw new DalException(ErrorCode.TransactionDistributedShard, curShard, desiganateShard);
    }

    public String getLogicDbName() {
        return logicDbName;
    }

    public DalConnection getConnection() {
        return connHolder;
    }

    public void register(DalTransactionListener listener) {
        if (listeners == null)
            listeners = new ArrayList<>();

        listeners.add(listener);
    }

    public List<DalTransactionListener> getListeners() {
        return listeners;
    }

    public int getLevel() {
        return level;
    }

    public boolean isRolledBack() {
        return rolledBack;
    }

    public int startTransaction() throws SQLException {
        if (rolledBack || completed)
            throw new DalException(ErrorCode.TransactionState);

        return level++;
    }

    public void endTransaction(int startLevel) throws SQLException {
        if (rolledBack || completed)
            throw new DalException(ErrorCode.TransactionState);

        if (startLevel != (level - 1)) {
            rollback();
            throw new DalException(ErrorCode.TransactionLevelMatch, (level - 1), startLevel);
        }

        if (level > FIRST_LEVEL) {
            setTransactionStatusOnCommit();
            level--;
            return;
        }

        if (status == DalTransactionStatus.Rollback || status == DalTransactionStatus.Conflict) {
            setTransactionStatusOnCommit();
            rollbackIfNeeded();
            throwExceptionOnCommitConflicted();
        }

        try {
            processRollbackOnlyIfExist();
        } catch (Throwable e) {
            throw new DalException(e.getMessage(), e);
        }

        commitIfNeeded();
    }

    private void processRollbackOnlyIfExist() throws Throwable {
        if (rollbackOnlyWrapperList.size() == 0)
            return;

        List<RollbackOnlyWrapper> list = new ArrayList<>(rollbackOnlyWrapperList);
        for (RollbackOnlyWrapper wrapper : list) {
            if (wrapper.isRollbackOnly()) {
                rollbackOnlyIfNeeded(wrapper.getLevel());
            } else if (wrapper.hasError()) {
                rollbackIfNeeded();
                // Throwable e = wrapper.getError();
                // throw e;
            }
        }
    }

    private void commitIfNeeded() throws SQLException {
        if (rolledBack || completed)
            return;

        commit();
    }

    private void commit() throws SQLException {
        // Back to the first transaction, about to commit
        beforeCommit();
        level = 0;
        completed = true;
        cleanup(true);
        afterCommit();
    }

    private void setTransactionStatusOnCommit() {
        if (status == DalTransactionStatus.Initial) {
            status = DalTransactionStatus.Commit;
        } else if (status == DalTransactionStatus.Rollback) {
            status = DalTransactionStatus.Conflict;
        }

        DalTransactionStatusWrapper wrapper = getTransactionStatusWrapper();
        wrapper.setTransactionStatus(status);
        wrapper.setActualStatus(DalTransactionStatus.Commit);
    }

    private void throwExceptionOnCommitConflicted() throws SQLException {
        String message = transactionHelper.getTransactionConflictMessage(transactionStatusWrapperMap);
        transactionStatusWrapperMap.clear();
        throw new DalTransactionConflictException(ErrorCode.TransactionStateConflicted, message);
    }

    public void rollbackTransaction() throws SQLException {
        if (rolledBack)
            return;

        if (level > FIRST_LEVEL) {
            setTransactionStatusOnRollback();
            level--;
            return;
        }

        if (status == DalTransactionStatus.Commit || status == DalTransactionStatus.Conflict) {
            setTransactionStatusOnRollback();
            logExceptionOnRollbackConflicted();
        }

        try {
            processRollbackOnlyIfExist();
        } catch (Throwable e) {
            throw new DalException(e.getMessage(), e);
        }

        rollbackIfNeeded();
    }

    private void rollbackIfNeeded() {
        if (rolledBack)
            return;

        rollback();
    }

    private void rollback() {
        beforeRollback();
        rolledBack = true;
        // Even the rollback fails, we still set the flag to true;
        cleanup(false);
        afterRollback();
    }

    private void logExceptionOnRollbackConflicted() {
        String message = transactionHelper.getTransactionConflictMessage(transactionStatusWrapperMap);
        transactionStatusWrapperMap.clear();
        DalTransactionConflictException exception =
                new DalTransactionConflictException(ErrorCode.TransactionStateConflicted, message);
        logger.error(ErrorCode.TransactionStateConflicted.getMessage(), exception);
    }

    private void setTransactionStatusOnRollback() {
        if (status == DalTransactionStatus.Initial) {
            status = DalTransactionStatus.Rollback;
        } else if (status == DalTransactionStatus.Commit) {
            status = DalTransactionStatus.Conflict;
        }

        DalTransactionStatusWrapper wrapper = getTransactionStatusWrapper();
        wrapper.setTransactionStatus(status);
        wrapper.setActualStatus(DalTransactionStatus.Rollback);
    }

    public void setRollbackErrorMessage(Throwable e) {
        if (e == null)
            return;

        String errorMessage = e.getMessage();
        if (errorMessage == null || errorMessage.isEmpty())
            return;

        DalTransactionStatusWrapper wrapper = getTransactionStatusWrapper();
        wrapper.setErrorMessage(errorMessage);

        setExceptionIntoRollbackOnlyWrapper(e);
    }

    private void setExceptionIntoRollbackOnlyWrapper(Throwable e) {
        RollbackOnlyWrapper wrapper = new RollbackOnlyWrapper();
        wrapper.setError(e);
        rollbackOnlyWrapperList.add(wrapper);
    }

    private DalTransactionStatusWrapper getTransactionStatusWrapper() {
        DalTransactionStatusWrapper wrapper = transactionStatusWrapperMap.get(level);
        if (wrapper == null) {
            wrapper = new DalTransactionStatusWrapper();
            transactionStatusWrapperMap.put(level, wrapper);
        }

        return wrapper;
    }

    private void cleanup(boolean commit) {
        Connection conn = connHolder.getConn();
        if (commit) {
            try {
                conn.commit();
            } catch (SQLException ex) {
                throw new TransactionSystemException("Could not commit JDBC transaction", ex);
            }
        }
        else {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("Could not roll back JDBC transaction", ex);
            }
        }

        try {
            conn.setAutoCommit(true);
        } catch (Throwable e) {
            logger.error("Can not setAutoCommit on current connection", e);
        }

        connHolder.close();
        DalTransactionManager.clearCurrentTransaction();
    }

    private void beforeCommit() throws SQLException {
        if (listeners == null)
            return;

        // The before commit can cause transaction termination by throwing exception
        for (DalTransactionListener listener : listeners)
            listener.beforeCommit();
    }

    private void beforeRollback() {
        if (listeners == null)
            return;

        for (DalTransactionListener listener : listeners) {
            try {
                listener.beforeRollback();
            } catch (Throwable e) {
                logError(e);
            }
        }
    }

    private void afterCommit() {
        if (listeners == null)
            return;

        for (DalTransactionListener listener : listeners) {
            try {
                listener.afterCommit();
            } catch (Throwable e) {
                logError(e);
            }
        }
    }

    private void afterRollback() {
        if (listeners == null)
            return;

        for (DalTransactionListener listener : listeners) {
            try {
                listener.afterRollback();
            } catch (Throwable e) {
                logError(e);
            }
        }
    }

    private void logError(Throwable e) {
        try {
            logger.error(e.getMessage(), e);
        } catch (Throwable e2) {
            System.err.println(e2);
        }
    }

    protected void setRollbackOnly() {
        this.rollbackOnly = true;
        setRollbackOnlyWrapper();
        iLogger.logEvent(SQL_Transaction,
                String.format(DAL_TRANSACTION_SET_ROLLBACK_ONLY, logicDbName == null ? "" : logicDbName),
                getRollbackOnlyMessage(level));
    }

    private void setRollbackOnlyWrapper() {
        RollbackOnlyWrapper wrapper = new RollbackOnlyWrapper();
        wrapper.setRollbackOnly();
        wrapper.setLevel(level);
        rollbackOnlyWrapperList.add(wrapper);
    }

    private void rollbackOnlyIfNeeded(int level) throws Exception {
        if (rolledBack)
            return;

        rollbackOnly(level);
    }

    private void rollbackOnly(int level) throws Exception {
        final String name =
                String.format(DAL_TRANSACTION_EXECUTE_ROLLBACK_ONLY, logicDbName == null ? "" : logicDbName);
        final String msg = getRollbackOnlyMessage(level);

        iLogger.logTransaction(SQL_Transaction, name, msg, new Callback() {
            @Override
            public void execute() {
                rollback();
                iLogger.logEvent(SQL_Transaction, name, msg);
            }
        }, msg);
    }

    private String getRollbackOnlyMessage(int level) {
        String dbName = logicDbName == null ? "" : logicDbName;
        String connectionId = "";
        if (connHolder != null) {
            Connection con = connHolder.getConn();
            if (con != null) {
                connectionId = con.toString();
            }
        }

        return String.format("LogicDbName:%s, Level:%s, ConnectionId:%s", dbName, level, connectionId);
    }

}
