package com.ctrip.platform.dal.dao.client;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.Callable;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.HintsAwareExtractor;
import com.ctrip.platform.dal.dao.task.DalTaskContext;
import com.ctrip.platform.dal.exceptions.DalException;
import com.mysql.jdbc.JDBC4Connection;
import com.mysql.jdbc.MySQLConnection;

import static com.ctrip.platform.dal.dao.client.DalDirectClient.DbCategory.mysql;
import static com.ctrip.platform.dal.dao.client.DalDirectClient.DbCategory.sqlserver;

/**
 * The direct connection implementation for DalClient.
 *
 * @author jhhe
 */
public class DalDirectClient implements DalContextClient, DalClientExtension {
    protected volatile DalStatementCreator stmtCreator;
    protected volatile DalConnectionManager connManager;
    protected volatile DalTransactionManager transManager;
    protected volatile DalLogger logger;
    protected String dbCategory;

    public DalDirectClient(DalConfigure config, String logicDbName) {
        init(config, logicDbName);
    }

    public DalDirectClient() {
    }

    @Override
    public void init(DalConfigure configure, String logicDbName) {
        if (stmtCreator == null) {
            synchronized (DalDirectClient.class) {
                if (stmtCreator == null) {
                    connManager = new DalConnectionManager(logicDbName, configure);
                    transManager = new DalTransactionManager(connManager);
                    stmtCreator = new DalStatementCreator(configure.getDatabaseSet(logicDbName).getDatabaseCategory());
                    logger = DalClientFactory.getDalLogger();
                    initCategory(configure, logicDbName);
                }
            }
        }
    }

    private void initCategory(DalConfigure configure, String logicDbName) {
        try{
            DatabaseSet databaseSet = configure.getDatabaseSet(logicDbName);
            if (StringUtils.isEmpty(databaseSet.getProvider())) {
                this.dbCategory = "";
            } else if (databaseSet.getProvider().trim().toLowerCase().startsWith(mysql.name())){
                this.dbCategory = mysql.name();
            } else {
                this.dbCategory = sqlserver.name();
            }
        } catch (Throwable t) {

        }
    }

    enum DbCategory {
        mysql, sqlserver
    }

    @Override
    public void destroy() {

    }


    @Override
    public <T> T query(String sql, StatementParameters parameters, final DalHints hints,
                       final DalResultSetExtractor<T> extractor, final DalTaskContext dalTaskContext) throws SQLException {
        ConnectionAction<T> action = new ConnectionAction<T>() {
            @Override
            public T execute() throws Exception {
                conn = getConnection(hints, this);

                preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
                beginExecute();
                rs = executeQuery(preparedStatement, entry);
                endExecute();

                T result;

                if (extractor instanceof HintsAwareExtractor)
                    result = ((DalResultSetExtractor<T>) ((HintsAwareExtractor) extractor).extractWith(hints))
                            .extract(rs);
                else
                    result = extractor.extract(rs);

                entry.setResultCount(fetchSize(rs, result));

                return result;
            }
        };
        action.populate(DalEventEnum.QUERY, sql, parameters, dalTaskContext);

        return doInConnection(action, hints);
    }


    @Override
    public <T> T query(String sql, StatementParameters parameters, final DalHints hints,
                       final DalResultSetExtractor<T> extractor) throws SQLException {
        return query(sql, parameters, hints, extractor, null);
    }

    @Override
    public List<?> query(String sql, StatementParameters parameters, final DalHints hints,
                         final List<DalResultSetExtractor<?>> extractors, final DalTaskContext dalTaskContext) throws SQLException {
        ConnectionAction<List<?>> action = new ConnectionAction<List<?>>() {
            @Override
            public List<?> execute() throws Exception {
                conn = getConnection(hints, this);
                preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
                List<Object> result = new ArrayList<>();
                beginExecute();

                executeMultiple(preparedStatement, entry);

                int count = 0;

                for (DalResultSetExtractor<?> extractor : extractors) {
                    ResultSet resultSet = preparedStatement.getResultSet();
                    Object partResult;
                    if (extractor instanceof HintsAwareExtractor)
                        partResult = ((DalResultSetExtractor) ((HintsAwareExtractor) extractor).extractWith(hints))
                                .extract(resultSet);
                    else
                        partResult = extractor.extract(resultSet);
                    result.add(partResult);

                    count += fetchSize(resultSet, partResult);

                    preparedStatement.getMoreResults();
                }

                endExecute();

                entry.setResultCount(count);

                return result;
            }
        };
        action.populate(DalEventEnum.QUERY, sql, parameters, dalTaskContext);

        return doInConnection(action, hints);
    }

    @Override
    public List<?> query(String sql, StatementParameters parameters, final DalHints hints,
                         final List<DalResultSetExtractor<?>> extractors) throws SQLException {
        return query(sql, parameters, hints, extractors, null);
    }

    @Override
    public int update(String sql, StatementParameters parameters, final DalHints hints, final DalTaskContext dalTaskContext) throws SQLException {
        final KeyHolder generatedKeyHolder = hints.getKeyHolder();
        ConnectionAction<Integer> action = new ConnectionAction<Integer>() {
            @Override
            public Integer execute() throws Exception {
                conn = getConnection(hints, this);
                // For old generated free update, the parameters is not compiled before invoke direct client
                parameters.compile();
                if (generatedKeyHolder == null)
                    preparedStatement = createPreparedStatement(conn, sql, parameters, hints);
                else
                    preparedStatement = createPreparedStatement(conn, sql, parameters, hints, generatedKeyHolder);

                beginExecute();
                int rows = executeUpdate(preparedStatement, entry);
                endExecute();

                if (generatedKeyHolder == null)
                    return rows;

                int pojosCount=0;
                List<Map<String, Object>> presetKeys = null;
                List<Map<String, Object>> dbReturnedKeys = null;
                if (dalTaskContext != null) {
                    pojosCount = dalTaskContext.getPojosCount();
                    presetKeys = dalTaskContext.getIdentityFields();
                }
                rs = preparedStatement.getGeneratedKeys();
                if (rs != null) {
                    DalRowMapperExtractor<Map<String, Object>> rse =
                            new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper());
                    dbReturnedKeys = rse.extract(rs);
                }

                int actualKeySize = 0;
                List<Map<String, Object>> returnedKeys = getFinalGeneratedKeys(dbReturnedKeys, presetKeys, pojosCount);
                if (returnedKeys != null) {
                    generatedKeyHolder.addKeys(returnedKeys);
                    actualKeySize = returnedKeys.size();
                }
                generatedKeyHolder.addEmptyKeys(pojosCount - actualKeySize);

                return rows;
            }
        };
        action.populate(generatedKeyHolder == null ? DalEventEnum.UPDATE_SIMPLE : DalEventEnum.UPDATE_KH, sql,
                parameters, dalTaskContext);

        return doInConnection(action, hints);
    }

    @Override
    public int update(String sql, StatementParameters parameters, final DalHints hints) throws SQLException {
        return update(sql, parameters, hints, null);
    }

    @Override
    public int[] batchUpdate(String[] sqls, final DalHints hints, final DalTaskContext dalTaskContext) throws SQLException {
        ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
            @Override
            public int[] execute() throws Exception {
                conn = getConnection(hints, this);

                statement = createStatement(conn, hints);
                for (String sql : sqls)
                    statement.addBatch(sql);

                beginExecute();
                int[] ret = executeBatch(statement, entry);
                endExecute();

                return ret;
            }
        };
        action.populate(sqls, dalTaskContext);

        return executeBatch(action, hints);
    }

    @Override
    public int[] batchUpdate(String[] sqls, final DalHints hints) throws SQLException {
        return batchUpdate(sqls,hints,null);
    }


    @Override
    public int[] batchUpdate(String sql, StatementParameters[] parametersList, final DalHints hints, final DalTaskContext dalTaskContext)
            throws SQLException {
        ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
            @Override
            public int[] execute() throws Exception {
                conn = getConnection(hints, this);

                statement = createPreparedStatement(conn, sql, parametersList, hints);

                beginExecute();
                int[] ret = executeBatch(statement, entry);
                endExecute();

                return ret;
            }
        };
        action.populate(sql, parametersList, dalTaskContext);

        return executeBatch(action, hints);
    }

    @Override
    public int[] batchUpdate(String sql, StatementParameters[] parametersList, final DalHints hints)
            throws SQLException {
        return batchUpdate(sql, parametersList, hints, null);
    }

    @Override
    public void execute(DalCommand command, DalHints hints) throws SQLException {
        final DalClient client = this;
        ConnectionAction<?> action = new ConnectionAction<Object>() {
            @Override
            public Object execute() throws Exception {
                command.execute(client);
                return null;
            }
        };
        action.populate(command);

        doInTransaction(action, hints);
    }

    @Override
    public void execute(final List<DalCommand> commands, final DalHints hints) throws SQLException {
        final DalClient client = this;
        ConnectionAction<?> action = new ConnectionAction<Object>() {
            @Override
            public Object execute() throws Exception {
                for (DalCommand cmd : commands) {
                    if (!cmd.execute(client))
                        break;
                }

                return null;
            }
        };
        action.populate(commands);

        doInTransaction(action, hints);
    }

    @Override
    public Map<String, ?> call(String callString, StatementParameters parameters, final DalHints hints, final DalTaskContext dalTaskContext)
            throws SQLException {
        ConnectionAction<Map<String, ?>> action = new ConnectionAction<Map<String, ?>>() {
            @Override
            public Map<String, ?> execute() throws Exception {
                List<StatementParameter> resultParameters = new ArrayList<StatementParameter>();
                List<StatementParameter> callParameters = new ArrayList<StatementParameter>();
                resultParameters.addAll(parameters.getResultParameters());
                for (StatementParameter parameter : parameters.values()) {
                    if (parameter.isOutParameter()) {
                        callParameters.add(parameter);
                    }
                }

                if (hints.is(DalHintEnum.retrieveAllSpResults) && resultParameters.size() > 0)
                    throw new DalException(
                            "Dal hint 'autoRetrieveAllResults' should only be used when there is no special result parameter specified");

                conn = getConnection(hints, this);

                callableStatement = createCallableStatement(conn, callString, parameters, hints);

                beginExecute();
                boolean retVal = executeCall(callableStatement, entry);
                int updateCount = callableStatement.getUpdateCount();

                endExecute();

                Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
                if (retVal || updateCount != -1) {
                    returnedResults
                            .putAll(extractReturnedResults(callableStatement, resultParameters, updateCount, hints));
                }
                returnedResults.putAll(extractOutputParameters(callableStatement, callParameters));
                return returnedResults;
            }
        };
        action.populateSp(callString, parameters, dalTaskContext);

        return doInConnection(action, hints);
    }

    @Override
    public Map<String, ?> call(String callString, StatementParameters parameters, final DalHints hints)
            throws SQLException {
        return call(callString, parameters, hints, null);
    }

    @Override
    public int[] batchCall(String callString, StatementParameters[] parametersList, final DalHints hints, final DalTaskContext dalTaskContext)
            throws SQLException {
        ConnectionAction<int[]> action = new ConnectionAction<int[]>() {
            @Override
            public int[] execute() throws Exception {
                conn = getConnection(hints, this);

                callableStatement = createCallableStatement(conn, callString, parametersList, hints);

                beginExecute();
                int[] ret = executeBatch(callableStatement, entry);
                endExecute();

                return ret;
            }
        };
        action.populateSp(callString, parametersList, dalTaskContext);

        return executeBatch(action, hints);
    }

    @Override
    public int[] batchCall(String callString, StatementParameters[] parametersList, final DalHints hints)
            throws SQLException {
        return batchCall(callString, parametersList, hints, null);
    }

    public String getLogicDbName() {
        return connManager.getLogicDbName();
    }

    /**
     * First try getRow(), then try parse result
     *
     * @param rs
     * @param result
     * @return
     * @throws SQLException
     */
    private int fetchSize(ResultSet rs, Object result) throws SQLException {
        // int rowCount = 0;
        // try {
        // rowCount = rs.getRow();
        // if(rowCount == 0 && rs.isAfterLast()) {
        // rs.last();
        // rowCount = rs.getRow();
        // }
        // } catch (Throwable e) {
        // // In case not support this feature
        // }
        //
        // if(rowCount > 0)
        // return rowCount;
        //
        if (result == null)
            return 0;

        if (result instanceof Collection<?>)
            return ((Collection<?>) result).size();

        return 1;
    }

    private Map<String, Object> extractReturnedResults(CallableStatement statement,
                                                       List<StatementParameter> resultParameters, int updateCount, DalHints hints) throws SQLException {
        Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
        if (hints.is(DalHintEnum.skipResultsProcessing))
            return returnedResults;

        if (hints.is(DalHintEnum.retrieveAllSpResults))
            return autoExtractReturnedResults(statement, updateCount);

        if (resultParameters.size() == 0)
            return returnedResults;

        boolean moreResults;
        int index = 0;
        do {
            // If resultParameters is not the same as what exactly returned, there will be exception. You just
            // need to add enough result parameter to avoid this or you can set skipResultsProcessing
            StatementParameter resultParameter = resultParameters.get(index);
            String key = resultParameter.getName();
            Object value = updateCount == -1
                    ? resultParameters.get(index).getResultSetExtractor().extract(statement.getResultSet())
                    : updateCount;
            resultParameter.setValue(value);
            moreResults = statement.getMoreResults();
            updateCount = statement.getUpdateCount();
            index++;
            returnedResults.put(key, value);
        } while (moreResults || updateCount != -1);

        return returnedResults;
    }

    private Map<String, Object> autoExtractReturnedResults(CallableStatement statement, int updateCount)
            throws SQLException {
        Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
        boolean moreResults;
        int index = 0;
        DalRowMapperExtractor<Map<String, Object>> extractor;
        do {
            extractor = new DalRowMapperExtractor<>(new DalColumnMapRowMapper());
            String key = (updateCount == -1 ? "ResultSet_" : "UpdateCount_") + index;
            Object value = updateCount == -1 ? extractor.extract(statement.getResultSet()) : updateCount;
            moreResults = statement.getMoreResults();
            updateCount = statement.getUpdateCount();
            index++;
            returnedResults.put(key, value);
        } while (moreResults || updateCount != -1);

        return returnedResults;
    }

    private Map<String, Object> extractOutputParameters(CallableStatement statement,
                                                        List<StatementParameter> callParameters) throws SQLException {

        Map<String, Object> returnedResults = new LinkedHashMap<String, Object>();
        for (StatementParameter parameter : callParameters) {
            Object value = parameter.getName() == null ? statement.getObject(parameter.getIndex())
                    : statement.getObject(parameter.getName());

            parameter.setValue(value);
            if (value instanceof ResultSet) {
                value = parameter.getResultSetExtractor().extract(statement.getResultSet());
            }
            returnedResults.put(parameter.getName(), value);
        }
        return returnedResults;
    }

    private <T> T executeBatch(ConnectionAction<T> action, DalHints hints) throws SQLException {
        if (hints.is(DalHintEnum.forceAutoCommit)) {
            return doInConnection(action, hints);
        } else {
            return doInTransaction(action, hints);
        }
    }

    private <T> T doInConnection(ConnectionAction<T> action, DalHints hints) throws SQLException {
        return connManager.doInConnection(action, hints);
    }

    private <T> T doInTransaction(ConnectionAction<T> action, DalHints hints) throws SQLException {
        return transManager.doInTransaction(action, hints);
    }

    public Connection getConnection(DalHints hints, ConnectionAction<?> action) throws SQLException {
        action.beginConnect();

        long connCost = System.currentTimeMillis();
        action.connHolder = transManager.getConnection(hints, action.operation);
        Connection conn = action.connHolder.getConn();
        connCost = System.currentTimeMillis() - connCost;
        action.entry.setConnectionCost(connCost);

        try {
            if (conn.isWrapperFor(MySQLConnection.class))
                action.entry.setConnectionId(conn.unwrap(MySQLConnection.class).getId());
        } catch (Throwable t) {
            // ignore
        }
        try {
            if (conn.isWrapperFor(JDBC4Connection.class))
                action.entry.setLocalPort(conn.unwrap(JDBC4Connection.class).getIO().mysqlConnection.getLocalPort());
        } catch (Throwable t) {
            // ignore
        }

        action.endConnect();
        return conn;
    }

    private Statement createStatement(Connection conn, DalHints hints) throws Exception {
        return stmtCreator.createStatement(conn, hints);
    }

    private PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters,
                                                      DalHints hints) throws Exception {
        return stmtCreator.createPreparedStatement(conn, sql, parameters, hints);
    }

    private PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters,
                                                      DalHints hints, KeyHolder keyHolder) throws Exception {
        return stmtCreator.createPreparedStatement(conn, sql, parameters, hints, keyHolder);
    }

    private PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters[] parametersList,
                                                      DalHints hints) throws Exception {
        return stmtCreator.createPreparedStatement(conn, sql, parametersList, hints);
    }

    private CallableStatement createCallableStatement(Connection conn, String sql, StatementParameters parameters,
                                                      DalHints hints) throws Exception {
        return stmtCreator.createCallableStatement(conn, sql, parameters, hints);
    }

    private CallableStatement createCallableStatement(Connection conn, String sql, StatementParameters[] parametersList,
                                                      DalHints hints) throws Exception {
        return stmtCreator.createCallableStatement(conn, sql, parametersList, hints);
    }

    private ResultSet executeQuery(final PreparedStatement preparedStatement, final LogEntry entry) throws Exception {
        return execute(new Callable<ResultSet>() {
            public ResultSet call() throws Exception {
                return preparedStatement.executeQuery();
            }
        }, entry);
    }

    private void executeMultiple(final PreparedStatement preparedStatement, final LogEntry entry) throws Exception {
        execute(new Callable<Object>() {
            public Object call() throws Exception {
                preparedStatement.execute();
                return null;
            }
        }, entry);
    }

    private int executeUpdate(final PreparedStatement preparedStatement, final LogEntry entry) throws Exception {
        return execute(new Callable<Integer>() {
            public Integer call() throws Exception {
                return entry.setAffectedRows(preparedStatement.executeUpdate());
            }
        }, entry);
    }

    private int[] executeBatch(final Statement statement, final LogEntry entry) throws Exception {
        return execute(new Callable<int[]>() {
            public int[] call() throws Exception {
                return entry.setAffectedRowsArray(statement.executeBatch());
            }
        }, entry);
    }

    private Boolean executeCall(final CallableStatement callableStatement, final LogEntry entry) throws Exception {
        return execute(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return callableStatement.execute();
            }
        }, entry);
    }

    private int[] executeBatch(final CallableStatement callableStatement, final LogEntry entry) throws Exception {
        return execute(new Callable<int[]>() {
            public int[] call() throws Exception {
                return entry.setAffectedRowsArray(callableStatement.executeBatch());
            }
        }, entry);
    }

    private <T> T execute(Callable<T> statementTask, LogEntry entry) throws Exception {
        Throwable error = null;
        logger.startStatement(entry);

        try {
            return statementTask.call();
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            logger.endStatement(entry, error);
        }
    }

    private List<Map<String, Object>> getFinalGeneratedKeys(List<Map<String, Object>> dbReturnedKeyFields,
                                                            List<Map<String, Object>> presetKeyFields, int pojosCount) {

//        invoke dalclient.update directly
        if (pojosCount == 0)
            return dbReturnedKeyFields;

//        not autoIncrement key or autoIncrement key and no value
        if (null == presetKeyFields || presetKeyFields.size() == 0) {
             //  maybe replace conflict
            if (null == dbReturnedKeyFields || (dbReturnedKeyFields.size() != pojosCount))
                return null;
             //  no conflict
            return dbReturnedKeyFields;
        }

        List<Map<String, Object>> returnedKeyFields = new ArrayList<>();

//        autoIncrement key with user value ,  driver returned empty generated keys because of conflict
//        we use user's key
        if (null == dbReturnedKeyFields || dbReturnedKeyFields.size() == 0) {
            returnedKeyFields.addAll(presetKeyFields);
            return returnedKeyFields;
        }

//        autoIncrement key with user value ,  driver returned not empty generated keys
//        we use the value of user's key but the type of dbReturnedKeyFields
        Map<String, Object> dbReturnedKeyField = dbReturnedKeyFields.get(0);
        String keyName = dbReturnedKeyField.keySet().iterator().next();
        Object dbReturnedKey = dbReturnedKeyField.values().iterator().next();
        Class<?> clazz = dbReturnedKey.getClass();

        for (int i = 0; i < presetKeyFields.size(); i++) {
            Map<String, Object> presetKeyField = presetKeyFields.get(i);
            Number presetKey = (Number) presetKeyField.values().iterator().next();
            Object returnedKey = presetKey;
            if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
                returnedKey = presetKey.byteValue();
            } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
                returnedKey = presetKey.shortValue();
            } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
                returnedKey = presetKey.intValue();
            } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
                returnedKey = presetKey.longValue();
            } else if (clazz.equals(BigInteger.class)) {
                returnedKey = BigInteger.valueOf(presetKey.longValue());
            }
            Map<String, Object> field = new HashMap<>();
            field.put(keyName, returnedKey);
            returnedKeyFields.add(field);
        }
        return returnedKeyFields;
    }

}