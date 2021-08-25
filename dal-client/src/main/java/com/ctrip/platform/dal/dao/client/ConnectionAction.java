package com.ctrip.platform.dal.dao.client;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.config.LocalizationConfig;
import com.ctrip.platform.dal.common.enums.ShardingCategory;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.configure.ClusterDatabaseSet;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidatable;
import com.ctrip.platform.dal.dao.datasource.ValidationResult;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;
import com.ctrip.platform.dal.dao.task.DalContextConfigure;
import com.ctrip.platform.dal.dao.task.DalTaskContext;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.TransactionSystemException;

public abstract class ConnectionAction<T> {
    private static final EnvUtils envUtils = DalElementFactory.DEFAULT.getEnvUtils();

    public DalEventEnum operation;
    public String sql;
    public String callString;
    public String[] sqls;
    public StatementParameters parameters;
    public StatementParameters[] parametersList;
    public DalCommand command;
    public List<DalCommand> commands;
    public DalConnection connHolder;
    public Set<String> usedDbs = new HashSet<>();
    public Connection conn;
    public Statement statement;
    public PreparedStatement preparedStatement;
    public CallableStatement callableStatement;
    public ResultSet rs;
    public long start;
    private DalPropertiesLocator dalPropertiesLocator = DalPropertiesManager.getInstance().getDalPropertiesLocator();
    public DalLogger logger = DalClientFactory.getDalLogger();
    public LogEntry entry;
    public Throwable e;
    public Set<String> tables;
    private static final String SWITCH_OFF = "SwitchOff";
    public ShardingCategory shardingCategory;
    public DalTaskContext dalTaskContext;

    void populate(DalEventEnum operation, String sql, StatementParameters parameters, DalTaskContext dalTaskContext) {
        this.operation = operation;
        this.sql = sql;
        this.parameters = parameters;
        if (dalTaskContext != null) {
            this.dalTaskContext = dalTaskContext;
            this.tables = dalTaskContext.getTables();
            this.shardingCategory = dalTaskContext.getShardingCategory();
        }
    }

    void populate(DalEventEnum operation, String sql, StatementParameters parameters) {
        populate(operation, sql, parameters, null);
    }

    void populate(String[] sqls, DalTaskContext dalTaskContext) {
        this.operation = DalEventEnum.BATCH_UPDATE;
        this.sqls = sqls;
        if (dalTaskContext != null) {
            this.dalTaskContext = dalTaskContext;
            this.tables = dalTaskContext.getTables();
            this.shardingCategory = dalTaskContext.getShardingCategory();
        }
    }

    void populate(String[] sqls) {
        populate(sqls, null);
    }

    void populate(String sql, StatementParameters[] parametersList, DalTaskContext dalTaskContext) {
        this.operation = DalEventEnum.BATCH_UPDATE_PARAM;
        this.sql = sql;
        this.parametersList = parametersList;
        if (dalTaskContext != null) {
            this.dalTaskContext = dalTaskContext;
            this.tables = dalTaskContext.getTables();
            this.shardingCategory = dalTaskContext.getShardingCategory();
        }
    }

    void populate(String sql, StatementParameters[] parametersList) {
        populate(sql, parametersList, null);
    }

    void populate(DalCommand command) {
        this.operation = DalEventEnum.EXECUTE;
        this.command = command;
    }

    void populate(List<DalCommand> commands) {
        this.operation = DalEventEnum.EXECUTE;
        this.commands = commands;
    }

    void populateSp(String callString, StatementParameters parameters, DalTaskContext dalTaskContext) {
        this.operation = DalEventEnum.CALL;
        this.callString = callString;
        this.parameters = parameters;
        if (dalTaskContext != null) {
            this.dalTaskContext = dalTaskContext;
            this.tables = dalTaskContext.getTables();
            this.shardingCategory = dalTaskContext.getShardingCategory();
        }
    }

    void populateSp(String callString, StatementParameters parameters) {
        populateSp(callString, parameters, null);
    }

    void populateSp(String callString, StatementParameters[] parametersList, DalTaskContext dalTaskContext) {
        this.operation = DalEventEnum.BATCH_CALL;
        this.callString = callString;
        this.parametersList = parametersList;
        if (dalTaskContext != null) {
            this.dalTaskContext = dalTaskContext;
            this.tables = dalTaskContext.getTables();
            this.shardingCategory = dalTaskContext.getShardingCategory();
        }
    }

    void populateSp(String callString, StatementParameters[] parametersList) {
        populateSp(callString, parametersList, null);
    }

    public void populateDbMeta() {
        DbMeta meta = null;

        entry.setTransactional(DalTransactionManager.isInTransaction());

        if (DalTransactionManager.isInTransaction()) {
            meta = DalTransactionManager.getCurrentDbMeta();

        } else {
            if (connHolder != null) {
                meta = connHolder.getMeta();
            }
        }

        if (meta != null)
            meta.populate(entry);

        if (connHolder != null) {
            entry.setMaster(connHolder.isMaster());
            entry.setShardId(connHolder.getShardId());
        }

        recordLocalizationValidation();
    }

    private void recordLocalizationValidation() {
        Statement stmt = statement != null ? statement : preparedStatement;
        stmt = stmt != null ? stmt : callableStatement;
        try {
            if (stmt.isWrapperFor(LocalizationValidatable.class)) {
                LocalizationValidatable validatable = stmt.unwrap(LocalizationValidatable.class);
                LocalizationValidatable.ValidationStatus validationStatus = validatable.getLastValidationStatus();
                ValidationResult validationResult = validatable.getLastValidationResult();
                if ((validationStatus == LocalizationValidatable.ValidationStatus.OK ||
                        validationStatus == LocalizationValidatable.ValidationStatus.FAILED) &&
                        validationResult != null) {
                    entry.setUcsValidation(validationResult.getUcsValidationMessage());
                    entry.setDalValidation(validationResult.getDalValidationMessage());
                }
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    public void initLogEntry(String logicDbName, DalHints hints) {
        this.entry = logger.createLogEntry();
        DatabaseSet databaseSet = DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName);
        if (databaseSet instanceof ClusterDatabaseSet) {
            Cluster cluster = ((ClusterDatabaseSet) databaseSet).getCluster();
            entry.setCluster(cluster);
            entry.setClusterName(cluster.getClusterName().toLowerCase());
            LocalizationConfig localizationConfig = cluster.getLocalizationConfig();
            if (localizationConfig != null)
                entry.setDbZone(localizationConfig.getZoneId());
        }
        entry.setLogicDbName(logicDbName);
        entry.setDbCategory(DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getDatabaseCategory());
        entry.setClientVersion(Version.getVersion());
        entry.setClientZone(envUtils.getZone());
        entry.setSensitive(hints.is(DalHintEnum.sensitive));
        entry.setEvent(operation);
        entry.setShardingCategory(shardingCategory);
        wrapSql();
        entry.setCallString(callString);
        if (sqls != null)
            entry.setSqls(sqls);
        else
            entry.setSqls(sql);

        if (null != parametersList) {
            String[] params = new String[parametersList.length];
            for (int i = 0; i < parametersList.length; i++) {
                params[i] = parametersList[i].toLogString();
            }
            entry.setPramemters(params);
        } else if (parameters != null) {
            entry.setPramemters(parameters.toLogString());
            hints.setParameters(parameters);
        }
    }

    private void wrapSql() {
        /**
         * You can not add comments before callString
         */
        if (sql != null) {
            sql = wrapAPPID(sql);
        }

        if (sqls != null) {
            for (int i = 0; i < sqls.length; i++) {
                sqls[i] = wrapAPPID(sqls[i]);
            }
        }

        if (callString != null) {
            // Call can not have comments at the begining
            callString = callString + wrapAPPID("");
        }

    }

    public void start() {
        start = System.currentTimeMillis();
        logger.start(entry);
    }

    public void error(Throwable e) throws SQLException {
        this.e = e;

        // When Db is markdown, there will be no connHolder
        if (connHolder != null)
            connHolder.error(e);
    }

    public void end(Object result) throws SQLException {
        log(result, e);
        handleException(e);
    }

    public void beginExecute() {
        entry.beginExecute();
    }

    public void endExecute() {
        entry.endExectue();
    }

    public void beginConnect() {
        entry.beginConnect();
    }

    public void endConnect() {
        entry.endConnect();
    }

    private void log(Object result, Throwable e) {
        try {
            long statementExecuteTime = System.currentTimeMillis() - start;
            entry.setDuration(statementExecuteTime);

            if (dalPropertiesLocator.getTableParseSwitch() == TableParseSwitch.OFF) {
                Set<String> localTables = new HashSet<>();
                localTables.add(SWITCH_OFF);
                entry.setTables(localTables);
            } else
                entry.setTables(tables);
            if (dalTaskContext instanceof DalContextConfigure) {
                ((DalContextConfigure) dalTaskContext).sumExecuteStatementTime(statementExecuteTime);
                ((DalContextConfigure) dalTaskContext).setLogEntry(entry);
            }

            if (e == null) {
                logger.success(entry, entry.getResultCount());
            } else {
                logger.fail(entry, e);
            }
        } catch (Throwable e1) {
            logger.error("Can not log", e1);
        }
    }

    public void cleanup() {
        closeResultSet();
        closeStatement();
        closeConnection();
    }

    private void closeResultSet() {
        if (rs != null) {
            try {
                rs.close();
            } catch (Throwable e) {
                logger.error("Close result set failed.", e);
            }
        }
        rs = null;
    }

    private void closeStatement() {
        Statement _statement =
                statement != null ? statement : preparedStatement != null ? preparedStatement : callableStatement;

        statement = null;
        preparedStatement = null;
        callableStatement = null;

        if (_statement != null) {
            try {
                _statement.close();
            } catch (Throwable e) {
                logger.error("Close statement failed.", e);
            }
        }
    }

    private void closeConnection() {
        // do nothing for connection in transaction
        if (DalTransactionManager.isInTransaction())
            return;

        // For list of nested commands, the top level action will not hold any connHolder
        if (connHolder == null)
            return;

        connHolder.close();

        connHolder = null;
        conn = null;
    }

    private void handleException(Throwable e) throws SQLException {
        if (e instanceof TransactionSystemException) {
            throw (TransactionSystemException) e;
        }
        if (e != null)
            throw e instanceof SQLException ? (SQLException) e : DalException.wrap(e);
    }

    private String wrapAPPID(String sql) {
        return "/*" + logger.getAppID() + "-" + entry.getCallerInShort() + "*/" + sql;
    }

    public abstract T execute() throws Exception;
}
