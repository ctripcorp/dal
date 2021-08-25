package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.common.enums.ImplicitAllShardsSwitch;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.DalRequestContext;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper;
import com.ctrip.platform.dal.dao.helper.RequestContext;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.dao.sqlbuilder.AbstractFreeSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.TableSqlBuilder;
import com.ctrip.platform.dal.exceptions.DalException;
import org.apache.commons.lang.StringUtils;

public class DalSqlTaskRequest<T> implements DalRequest<T> {
    private String caller;
    private DalLogger logger;
    private String logicDbName;
    private SqlBuilder builder;
    private StatementParameters parameters;
    private DalHints hints;
    private SqlTask<T> task;
    private ResultMerger<T> merger;
    private Set<String> shards;
    private Map<String, List<?>> parametersByShard;
    private DalTaskContext taskContext;
    private ShardExecutionCallback<T> callback;
    private static DalPropertiesLocator dalPropertiesLocator = DalPropertiesManager.getInstance().getDalPropertiesLocator();

    public DalSqlTaskRequest(String logicDbName, SqlBuilder builder, DalHints hints, SqlTask<T> task,
                             ResultMerger<T> merger) throws SQLException {
        this(logicDbName, builder, hints, task, merger, null);
    }

    public DalSqlTaskRequest(String logicDbName, SqlBuilder builder, DalHints hints, SqlTask<T> task,
                             ResultMerger<T> merger, ShardExecutionCallback<T> callback) throws SQLException {
        this.logger = DalClientFactory.getDalLogger();
        this.logicDbName = logicDbName;
        this.builder = builder;
        this.parameters = builder.buildParameters();
        this.hints = hints != null ? hints.clone() : new DalHints();
        this.task = task;
        this.merger = merger;
        this.caller = LogContext.getRequestCaller();
        prepareRequestContext();
        this.shards = getShards();
        this.callback = callback;
    }

    @Override
    public String getCaller() {
        return caller;
    }

    @Override
    public boolean isAsynExecution() {
        return hints.isAsyncExecution();
    }

    @Override
    public void validateAndPrepare() throws SQLException {
        DalShardingHelper.detectDistributedTransaction(shards);
        taskContext = task.createTaskContext();
    }

    private void prepareRequestContext() {
        hints.setRequestContext(null);
        if (task instanceof TaskAdapter) {
            RequestContext ctx = new DalRequestContext().setLogicTableName(((TaskAdapter) task).rawTableName);
            hints.setRequestContext(ctx);
        } else if (builder instanceof AbstractFreeSqlBuilder) {
            List<AbstractFreeSqlBuilder.Table> tables = ((AbstractFreeSqlBuilder) builder).getTables();
            if (tables != null && tables.size() == 1) {
                RequestContext ctx = new DalRequestContext().setLogicTableName(tables.get(0).getTableName());
                hints.setRequestContext(ctx);
            } else if (hints.getSpecifiedTableName() != null) {
                RequestContext ctx = new DalRequestContext().setLogicTableName(hints.getSpecifiedTableName());
                hints.setRequestContext(ctx);
            }
        } else if (hints.getSpecifiedTableName() != null) {
            RequestContext ctx = new DalRequestContext().setLogicTableName(hints.getSpecifiedTableName());
            hints.setRequestContext(ctx);
        }
    }

    @Override
    public String getLogicDbName() {
        return logicDbName;
    }

    @Override
    public boolean isCrossShard() {
        return (shards != null && shards.size() > 1);
    }

    @Override
    public TaskCallable<T> createTask() throws SQLException {
        DalHints tmpHints = hints.clone();
        if (shards != null && shards.size() == 1) {
            tmpHints.inShard(shards.iterator().next());
        }

        return create(parameters, tmpHints, taskContext.fork(), tmpHints.getShardId());
    }

    @Override
    public Map<String, TaskCallable<T>> createTasks() throws SQLException {
        Map<String, TaskCallable<T>> tasks = new HashMap<>();

        if (parametersByShard == null) {
            // Create by given shards
            for (String shard : shards) {
                tasks.put(shard, create(parameters.duplicate(), hints.clone().inShard(shard), taskContext.fork(), shard));
            }
        } else {
            // Create by sharded values
            for (Map.Entry<String, ?> shard : parametersByShard.entrySet()) {
                StatementParameters tempParameters =
                        parameters.duplicateWith(hints.getShardBy(), (List) shard.getValue());
                tasks.put(shard.getKey(),
                        create(tempParameters, hints.clone().inShard(shard.getKey()), taskContext.fork(), shard.getKey()));
            }
        }

        return tasks;
    }

    private TaskCallable<T> create(StatementParameters parameters, DalHints hints,
                                   DalTaskContext taskContext, String dbShard) throws SQLException {
        return new SqlTaskCallable<>(logicDbName, parameters, hints, task, taskContext, builder, merger,
                logger, dbShard, callback);
    }

    @Override
    public ResultMerger<T> getMerger() {
        return merger;
    }

    @Override
    public void endExecution() {

    }

    private Set<String> getShards() throws SQLException {
        Set<String> shards = null;
        if (!DalShardingHelper.isShardingEnabled(logicDbName))
            return null;

        if (hints.isAllShards()) {
            shards = DalShardingHelper.getAllShards(logicDbName);
        } else if (hints.isInShards()) {
            shards = hints.getShards();
        } else if (hints.isShardBy()) {
            // The new code gen will set hints shardBy to indicate this is a potential cross shard operation
            // Check parameters. It can only surpport DB shard at this level
            StatementParameter parameter = parameters.get(hints.getShardBy(), ParameterDirection.Input);
            parametersByShard = DalShardingHelper.shuffle(logicDbName, (List) parameter.getValue(), hints);
            shards = parametersByShard.keySet();
        } else if (dalPropertiesLocator.getImplicitAllShardsSwitch() == ImplicitAllShardsSwitch.ON) {
            // implicit all shards if no shard can be located
            if (!DalShardingHelper.locateShardId(logicDbName, hints.setParameters(parameters.duplicate()))) {
                hints.inAllShards();
                shards = DalShardingHelper.getAllShards(logicDbName);
            }
        }


        if (shards != null && shards.size() > 1) {
            logOnMultipleShards(shards, builder);
        }

        return shards;
    }

    private void logOnMultipleShards(Set<String> shards, SqlBuilder builder) {
        String dbShards = StringUtils.join(shards, ",");

        if (builder instanceof AbstractFreeSqlBuilder) {
            AbstractFreeSqlBuilder freeSqlBuilder = (AbstractFreeSqlBuilder) builder;
            logger.warn(
                    String.format("Execute on multiple shards %s detected: %s", dbShards, freeSqlBuilder.build(" "))); // space
                                                                                                                       // used
                                                                                                                       // for
                                                                                                                       // placeholder
        } else {
            logger.warn(String.format("Execute on multiple shards %s detected: %s", dbShards, builder.build()));
        }
    }

    protected static class SqlTaskCallable<T> implements TaskCallable<T> {
        private ILogger iLogger = DalElementFactory.DEFAULT.getILogger();
        private static final String SQL_CROSSSHARD = "SQL.crossShard";
        private static final String IMPLICIT_IN_ALL_TABLE_SHARDS = "implicitInAllTableShards";

        private DalClient client;
        private StatementParameters parameters;
        private DalHints hints;
        private SqlTask<T> task;
        private DalTaskContext dalTaskContext;
        private String dbShard;
        private ShardExecutionCallback<T> callback;

        private boolean isTableShardingEnabled;
        private String logicDbName;
        private String rawTableName;
        private SqlBuilder builder;
        private Set<String> tableShards;
        private Map<String, List<?>> parametersByTableShard;
        private ResultMerger<T> merger;
        private DalLogger logger;

        public SqlTaskCallable(String logicDbName, StatementParameters parameters, DalHints hints, SqlTask<T> task,
                               DalTaskContext dalTaskContext, SqlBuilder builder, ResultMerger<T> merger,
                               DalLogger logger, String dbShard, ShardExecutionCallback<T> callback)
                throws SQLException {
            this.logicDbName = logicDbName;
            this.client = DalClientFactory.getClient(logicDbName);
            this.hints = hints;
            this.task = task;
            this.parameters = parameters;
            this.dalTaskContext = dalTaskContext;
            this.dbShard = dbShard;
            this.callback = callback;

            // for table sharding
            this.builder = builder;
            this.logger = logger;

            String tableName = getTableName();
            this.rawTableName = tableName;
            this.isTableShardingEnabled = DalShardingHelper.isTableShardingEnabled(logicDbName, tableName);
            this.tableShards = getTableShards();
            // we should create new ResultMerger here if table sharding is enabled
            this.merger = createResultMerger(isTableShardingEnabled, merger, builder, hints);
        }

        public StatementParameters getParameters() {
            return parameters;
        }

        private String getTableName() {
            String tableName = "";

            if (builder == null)
                return tableName;

            if (builder instanceof TableSqlBuilder) {
                TableSqlBuilder tableSqlBuilder = (TableSqlBuilder) builder;
                tableName = tableSqlBuilder.getTableName();
            } else if (builder instanceof AbstractFreeSqlBuilder) {
                AbstractFreeSqlBuilder freeSqlBuilder = (AbstractFreeSqlBuilder) builder;
                tableName = getTableNameFromAbstractFreeSqlBuilder(freeSqlBuilder);
            }

            return tableName;
        }

        private String getTableNameFromAbstractFreeSqlBuilder(AbstractFreeSqlBuilder freeSqlBuilder) {
            List<AbstractFreeSqlBuilder.Table> tables = freeSqlBuilder.getTables();
            if (tables == null || tables.isEmpty())
                return "";

            if (tables.size() == 1)
                return tables.get(0).getTableName();

            return "";
        }

        private Set<String> getTableShards() throws SQLException {
            Set<String> tableShards = null;
            if (!isTableShardingEnabled)
                return null;

            if (hints.isAllTableShards()) {
                tableShards = DalShardingHelper.getAllTableShards(logicDbName, rawTableName);
            } else if (hints.isInTableShards()) {
                tableShards = hints.getTableShards();
            } else if (hints.isTableShardBy()) {
                StatementParameter parameter = parameters.get(hints.getTableShardBy(), ParameterDirection.Input);
                parametersByTableShard =
                        DalShardingHelper.shuffleByTable(logicDbName, rawTableName, null, (List) parameter.getValue());
                tableShards = parametersByTableShard.keySet();
            } else if (dalPropertiesLocator.getImplicitAllShardsSwitch() == ImplicitAllShardsSwitch.ON) {
                // implicit all table shards if no table shard can be located
                try {
                    DalShardingHelper.locateTableShardId(logicDbName, rawTableName, hints, compileParameters(parameters.duplicate()), null);
                } catch (SQLException e) {
                    hints.inAllTableShards();
                    tableShards = DalShardingHelper.getAllTableShards(logicDbName, rawTableName);
                }
            }

            if (tableShards != null && tableShards.size() > 1) {
                logOnMultipleTableShards(tableShards, builder);
            }

            return tableShards;
        }

        private void logOnMultipleTableShards(Set<String> tableShards, SqlBuilder builder) throws SQLException {
            if (builder instanceof AbstractFreeSqlBuilder) {
                if (rawTableName == null || rawTableName.isEmpty()) {
                    throw new DalException(
                            "Cannot execute on muliple table shards by AbstractFreeSqlBuilder without a table name.");
                }

                AbstractFreeSqlBuilder freeSqlBuilder = (AbstractFreeSqlBuilder) builder;
                for (String tableShard : tableShards) {
                    String tableShardStr = getTableShardString(tableShard, rawTableName, null, null);
                    logger.warn("Execute on multiple table shards detected: " + freeSqlBuilder.build(tableShardStr));
                }
            } else {
                logger.warn("Execute on multiple table shards detected: " + builder.build());
            }
        }

        @Override
        public T call() throws Exception {
            if (isTableShardingEnabled) {
                return executeByTableSharding(tableShards, client, parameters, hints, dalTaskContext, builder, merger);
            }

            return execute(null, client, parameters, hints, dalTaskContext, builder, merger, tableShards);
        }

        private T executeByTableSharding(Set<String> tableShards, DalClient client, StatementParameters parameters,
                DalHints hints, DalTaskContext taskContext, SqlBuilder builder, ResultMerger<T> merger)
                throws SQLException {

            T result = null;
            if (parametersByTableShard == null) {
                // Not cross table shards, table id can be inferred.
                if (tableShards == null || tableShards.size() == 0) {
                    return execute(null, client, parameters, hints, taskContext, builder, merger, tableShards);
                }

                // By given table shards
                result = executeByGivenTableShards(tableShards, client, parameters, hints, taskContext, builder,
                        merger);
            } else {
                // By table sharded values
                result = executeByShardedParameterValues(parametersByTableShard, client, parameters, hints, taskContext,
                        builder, merger, tableShards);
            }

            return result;
        }

        private T executeByGivenTableShards(Set<String> tableShards, DalClient client, StatementParameters parameters,
                DalHints hints, DalTaskContext taskContext, SqlBuilder builder, ResultMerger<T> merger)
                throws SQLException {

            for (String tableShard : tableShards) {
                Throwable error = null;
                ShardExecutionResult<T> executionResult;
                try {
                    T partial = execute(tableShard, client, parameters, hints, taskContext, builder, merger, tableShards);
                    merger.addPartial(tableShard, partial);
                    // TODO: dbShard may be inaccurate
                    executionResult = new ShardExecutionResultImpl<>(dbShard, tableShard, partial);
                } catch (Throwable e) {
                    error = e;
                    executionResult = new ShardExecutionResultImpl<>(dbShard, tableShard, e);
                }

                hints.handleError("Error when execute table shard operation", error, callback, executionResult);
            }

            return merger.merge();
        }

        private T executeByShardedParameterValues(Map<String, List<?>> parametersByTableShard, DalClient client,
                StatementParameters parameters, DalHints hints, DalTaskContext taskContext, SqlBuilder builder,
                ResultMerger<T> merger, Set<String> tableShards) throws SQLException {

            for (Map.Entry<String, ?> tableShard : parametersByTableShard.entrySet()) {
                Throwable error = null;
                ShardExecutionResult<T> executionResult;
                try {
                    T partial = execute(tableShard.getKey(), client,
                            parameters.duplicateWith(hints.getTableShardBy(), (List) tableShard.getValue()), hints,
                            taskContext, builder, merger, tableShards);
                    merger.addPartial(tableShard.getKey(), partial);
                    // TODO: dbShard may be inaccurate
                    executionResult = new ShardExecutionResultImpl<>(dbShard, tableShard.getKey(), partial);
                } catch (Throwable e) {
                    error = e;
                    executionResult = new ShardExecutionResultImpl<>(dbShard, tableShard.getKey(), e);
                }

                hints.handleError("Error when execute table shard operation", error, callback, executionResult);
            }

            return merger.merge();
        }

        private T execute(String tableShardId, DalClient client, StatementParameters parameters, DalHints hints,
                DalTaskContext taskContext, SqlBuilder builder, ResultMerger<T> merger, Set<String> tableShards)
                throws SQLException {
            String tableName = "";
            StatementParameters originalParameters = parameters.duplicate();
            StatementParameters compiledParameters = compileParameters(parameters.duplicate());

            if (builder instanceof TableSqlBuilder || builder instanceof AbstractFreeSqlBuilder) {
                tableName = rawTableName;
                if (isTableShardingEnabled) {
                    return executeWithSqlBuilder(tableShardId, tableName, client, originalParameters,
                            compiledParameters, hints, taskContext, builder, merger, tableShards);
                }
            }

            if (!tableName.isEmpty())
                if (taskContext instanceof DalContextConfigure)
                    ((DalContextConfigure) taskContext).addTables(tableName);

            String compiledSql = compileSql(builder.build(), originalParameters);
            return task.execute(client, compiledSql, compiledParameters, hints.clone(), taskContext);
        }

        private T executeWithSqlBuilder(String tableShardId, String tableName, DalClient client,
                StatementParameters originalParameters, StatementParameters compiledParameters, DalHints hints,
                DalTaskContext taskContext, SqlBuilder builder, ResultMerger<T> merger, Set<String> tableShards)
                throws SQLException {
            String tempTableName = tableName;
            String tableShardStr = null;
            try {
                tableShardStr = getTableShardStr(tableShardId, tempTableName, hints, originalParameters); // compiledParameters
            } catch (Exception e) {
                if (tableShardStr == null || tableShardStr.isEmpty()) {
                    if (hints.isImplicitInAllTableShards()) {
                        return executeOnImplicitInAllTableShards(tempTableName, client, originalParameters, hints,
                                taskContext, builder, merger);
                    } else {
                        if (builder instanceof TableSqlBuilder) {
                            throw new DalException("Can not locate table shard for " + logicDbName, e);
                        } else if (builder instanceof AbstractFreeSqlBuilder) {
                            AbstractFreeSqlBuilder freeSqlBuilder = (AbstractFreeSqlBuilder) builder;
                            String compiledSql = compileSql(freeSqlBuilder.build(), originalParameters);
                            return task.execute(client, compiledSql, compiledParameters, hints.clone(),
                                    taskContext);
                        }
                    }
                }
            }

            tempTableName = tempTableName + tableShardStr;
            String sql = null;
            if (builder instanceof TableSqlBuilder) {
                TableSqlBuilder tableBuilder = (TableSqlBuilder) builder;
                sql = tableBuilder.build(tableShardStr);
            } else if (builder instanceof AbstractFreeSqlBuilder) {
                AbstractFreeSqlBuilder freeSqlBuilder = (AbstractFreeSqlBuilder) builder;
                if (tableShards == null || tableShards.isEmpty()) { // non-cross table sharding
                    sql = freeSqlBuilder.build();
                } else {
                    sql = freeSqlBuilder.build(tableShardStr);
                }
            }

            return executeTask(taskContext, tempTableName, client, sql, originalParameters, compiledParameters, hints);
        }

        private T executeOnImplicitInAllTableShards(String tempTableName, DalClient client,
                StatementParameters originalParameters, DalHints hints, DalTaskContext taskContext, SqlBuilder builder,
                ResultMerger<T> merger) throws SQLException {
            logger.warn("Try to execute on all table shards due to implicit inAllTableShards hints.");
            iLogger.logEvent(SQL_CROSSSHARD, IMPLICIT_IN_ALL_TABLE_SHARDS,
                    String.format("LogicDbName:%s, TableName:%s", logicDbName, tempTableName));

            // implicit execute in all table shards
            Set<String> allTableShards = DalShardingHelper.getAllTableShards(logicDbName, rawTableName);
            return executeByGivenTableShards(allTableShards, client, originalParameters, hints, taskContext, builder,
                    merger);
        }

        private String getTableShardStr(String tableShardId, String tableName, DalHints hints,
                StatementParameters compiledParameters) throws SQLException {
            String tempTableName = tableName;
            String tableShardStr = getTableShardString(tableShardId, tempTableName, hints, compiledParameters);
            return tableShardStr;
        }

        private T executeTask(DalTaskContext taskContext, String tempTableName, DalClient client, String sql,
                StatementParameters originalParameters, StatementParameters compiledParameters, DalHints hints)
                throws SQLException {
            if (taskContext instanceof DalContextConfigure)
                ((DalContextConfigure) taskContext).addTables(tempTableName);

            String compiledSql = compileSql(sql, originalParameters);
            return task.execute(client, compiledSql, compiledParameters, hints.clone(), taskContext);
        }

        private String compileSql(String sql, StatementParameters parameters) throws SQLException {
            if (!parameters.containsInParameter())
                return sql;

            return SQLCompiler.compile(sql, parameters.getAllInParameters());
        }

        private StatementParameters compileParameters(StatementParameters parameters) {
            if (!parameters.containsInParameter())
                return parameters;

            parameters.compile();
            return parameters;
        }

        private String getTableShardString(String tableShardId, String tableName, DalHints hints,
                StatementParameters parameters) throws SQLException {
            String tempTableShardId = null;
            if (tableShardId != null) {
                tempTableShardId = tableShardId;
            } else {
                tempTableShardId =
                        DalShardingHelper.locateTableShardId(logicDbName, tableName, hints, parameters, null);
            }

            return DalShardingHelper.buildShardStr(logicDbName, tableName, tempTableShardId);
        }

        private ResultMerger createResultMerger(boolean isTableShardingEnabled, ResultMerger<T> merger,
                SqlBuilder builder, DalHints hints) {
            if (merger == null)
                return null;

            if (!isTableShardingEnabled)
                return merger;

            ResultMerger rm = createResultMergerByMergerType(merger);
            if (rm == null) {
                rm = createResultMergerByBuilderType(builder, hints);
            }

            return rm;
        }

        private ResultMerger createResultMergerByMergerType(ResultMerger<T> merger) {
            return ResultMergerHelper.createResultMerger(merger);
        }

        private ResultMerger createResultMergerByBuilderType(SqlBuilder builder, DalHints hints) {
            if (builder instanceof SelectSqlBuilder) {
                SelectSqlBuilder selectSqlBuilder = (SelectSqlBuilder) builder;
                return selectSqlBuilder.createNewResultMerger(hints);
            }

            if (builder instanceof FreeSelectSqlBuilder) {
                FreeSelectSqlBuilder freeSelectSqlBuilder = (FreeSelectSqlBuilder) builder;
                return freeSelectSqlBuilder.createNewResultMerger(hints);
            }

            return null;
        }

        protected Set<String> tableShards(){
            return this.tableShards;
        }

        @Override
        public DalTaskContext getDalTaskContext() {
            return this.dalTaskContext;
        }

        @Override
        public String getPreparedDbShard() {
            return dbShard != null ? dbShard : hints.getShardId();
        }
    }

}
