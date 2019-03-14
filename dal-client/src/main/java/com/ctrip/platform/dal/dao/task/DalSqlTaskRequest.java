package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.TableSqlBuilder;

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

    public DalSqlTaskRequest(String logicDbName, SqlBuilder builder, DalHints hints, SqlTask<T> task,
            ResultMerger<T> merger) throws SQLException {
        this.logger = DalClientFactory.getDalLogger();
        this.logicDbName = logicDbName;
        this.builder = builder;
        this.parameters = builder.buildParameters();
        this.hints = hints;
        this.task = task;
        this.merger = merger;
        this.shards = getShards();
        this.caller = LogContext.getRequestCaller();
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

    @Override
    public boolean isCrossShard() {
        return (shards != null && shards.size() > 1);
    }

    @Override
    public Callable<T> createTask() throws SQLException {
        DalHints tmpHints = hints.clone();
        if (shards != null && shards.size() == 1) {
            tmpHints.inShard(shards.iterator().next());
        }

        return create(parameters, tmpHints, taskContext.fork());
    }

    @Override
    public Map<String, Callable<T>> createTasks() throws SQLException {
        Map<String, Callable<T>> tasks = new HashMap<>();

        if (parametersByShard == null) {
            // Create by given shards
            for (String shard : shards) {
                tasks.put(shard, create(parameters.duplicate(), hints.clone().inShard(shard), taskContext.fork()));
            }
        } else {
            // Create by sharded values
            for (Map.Entry<String, ?> shard : parametersByShard.entrySet()) {
                StatementParameters tempParameters =
                        parameters.duplicateWith(hints.getShardBy(), (List) shard.getValue());
                tasks.put(shard.getKey(),
                        create(tempParameters, hints.clone().inShard(shard.getKey()), taskContext.fork()));
            }
        }

        return tasks;
    }

    private Callable<T> create(StatementParameters parameters, DalHints hints, DalTaskContext taskContext)
            throws SQLException {
        return new SqlTaskCallable<>(logicDbName, parameters, hints, task, taskContext, builder, merger, logger);
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
            parametersByShard = DalShardingHelper.shuffle(logicDbName, (List) parameter.getValue());
            shards = parametersByShard.keySet();
        }

        if (shards != null && shards.size() > 1)
            logger.warn("Execute on multiple shards detected: " + builder.build());

        return shards;
    }

    private static class SqlTaskCallable<T> implements Callable<T> {
        private DalClient client;
        private StatementParameters parameters;
        private DalHints hints;
        private SqlTask<T> task;
        private DalTaskContext dalTaskContext;

        private boolean isTableShardingEnabled;
        private String logicDbName;
        private String rawTableName;
        private SqlBuilder builder;
        private Set<String> tableShards;
        private Map<String, List<?>> parametersByTableShard;
        private ResultMerger<T> merger;
        private DalLogger logger;

        public SqlTaskCallable(String logicDbName, StatementParameters parameters, DalHints hints, SqlTask<T> task,
                DalTaskContext dalTaskContext, SqlBuilder builder, ResultMerger<T> merger, DalLogger logger)
                throws SQLException {
            this.logicDbName = logicDbName;
            this.client = DalClientFactory.getClient(logicDbName);
            this.hints = hints;
            this.task = task;
            this.parameters = parameters;
            this.dalTaskContext = dalTaskContext;

            // for table sharding
            this.builder = builder;
            this.logger = logger;

            String tableName = getTableName();
            this.rawTableName = tableName;
            this.isTableShardingEnabled = DalShardingHelper.isTableShardingEnabled(logicDbName, tableName);
            this.tableShards = getTableShards();
            this.merger = createResultMerger(isTableShardingEnabled, merger, builder, hints); // we should create new
                                                                                              // ResultMerger here if
                                                                                              // table sharding is
                                                                                              // enabled
        }

        private String getTableName() {
            if (builder == null)
                return "";

            if (!(builder instanceof TableSqlBuilder))
                return "";

            TableSqlBuilder tableSqlBuilder = (TableSqlBuilder) builder;
            return tableSqlBuilder.getTableName();
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
            }

            if (tableShards != null && tableShards.size() > 1)
                logger.warn("Execute on multiple table shards detected: " + builder.build());

            return tableShards;
        }

        @Override
        public T call() throws Exception {
            if (isTableShardingEnabled) {
                return executeByTableSharding(tableShards, client, parameters, hints, dalTaskContext, builder, merger);
            }

            return execute(null, client, parameters, hints, dalTaskContext, builder, merger);
        }

        private T executeByTableSharding(Set<String> tableShards, DalClient client, StatementParameters parameters,
                DalHints hints, DalTaskContext taskContext, SqlBuilder builder, ResultMerger<T> merger)
                throws SQLException {

            T result = null;
            if (parametersByTableShard == null) {
                // Not cross table shards, table id can be inferred.
                if (tableShards == null || tableShards.size() == 0) {
                    return execute(null, client, parameters, hints, taskContext, builder, merger);
                }

                // By given table shards
                result = executeByGivenTableShards(tableShards, client, parameters, hints, taskContext, builder,
                        merger);
            } else {
                // By table sharded values
                result = executeByShardedParameterValues(parametersByTableShard, client, parameters, hints, taskContext,
                        builder, merger);
            }

            return result;
        }

        private T executeByGivenTableShards(Set<String> tableShards, DalClient client, StatementParameters parameters,
                DalHints hints, DalTaskContext taskContext, SqlBuilder builder, ResultMerger<T> merger)
                throws SQLException {

            for (String tableShard : tableShards) {
                Throwable error = null;
                try {
                    T partial = execute(tableShard, client, parameters, hints, taskContext, builder, merger);
                    merger.addPartial(tableShard, partial);
                } catch (Throwable e) {
                    error = e;
                }

                hints.handleError("Error when execute table shard operation", error);
            }

            return merger.merge();
        }

        private T executeByShardedParameterValues(Map<String, List<?>> parametersByTableShard, DalClient client,
                StatementParameters parameters, DalHints hints, DalTaskContext taskContext, SqlBuilder builder,
                ResultMerger<T> merger) throws SQLException {

            for (Map.Entry<String, ?> tableShard : parametersByTableShard.entrySet()) {
                Throwable error = null;
                try {
                    T partial = execute(tableShard.getKey(), client,
                            parameters.duplicateWith(hints.getTableShardBy(), (List) tableShard.getValue()), hints,
                            taskContext, builder, merger);
                    merger.addPartial(tableShard.getKey(), partial);
                } catch (Throwable e) {
                    error = e;
                }

                hints.handleError("Error when execute table shard operation", error);
            }

            return merger.merge();
        }

        private T execute(String tableShardId, DalClient client, StatementParameters parameters, DalHints hints,
                DalTaskContext taskContext, SqlBuilder builder, ResultMerger<T> merger) throws SQLException {
            String tableName = "";
            StatementParameters originalParameters = parameters.duplicate();
            StatementParameters compiledParameters = compileParameters(parameters.duplicate());

            if (builder instanceof TableSqlBuilder) {
                TableSqlBuilder tableBuilder = (TableSqlBuilder) builder;
                tableName = rawTableName;
                if (isTableShardingEnabled) {
                    return executeWithTableSqlBuilder(tableShardId, tableName, client, originalParameters,
                            compiledParameters, hints, taskContext, builder, tableBuilder, merger);
                }
            }

            if (!tableName.isEmpty())
                if (taskContext instanceof DalContextConfigure)
                    ((DalContextConfigure) taskContext).addTables(tableName);

            String compiledSql = compileSql(builder.build(), originalParameters);
            return task.execute(client, compiledSql, compiledParameters, hints.clone(), taskContext.fork());
        }

        private T executeWithTableSqlBuilder(String tableShardId, String tableName, DalClient client,
                StatementParameters originalParameters, StatementParameters compiledParameters, DalHints hints,
                DalTaskContext taskContext, SqlBuilder builder, TableSqlBuilder tableBuilder, ResultMerger<T> merger)
                throws SQLException {
            String tempTableName = tableName;
            String tableShardStr = null;
            try {
                tableShardStr = getTableShardString(tableShardId, tempTableName, hints, compiledParameters);
            } catch (SQLException e) {
                logger.warn(e.getMessage());
            }

            if (tableShardStr == null || tableShardStr.isEmpty()) {
                if (hints.isImplicitInAllTableShards()) {
                    logger.warn("Try to execute on all table shards due to implicit inAllTableShards hints.");
                    // implicit execute in all table shards
                    Set<String> allTableShards = DalShardingHelper.getAllTableShards(logicDbName, rawTableName);
                    return executeByGivenTableShards(allTableShards, client, originalParameters, hints, taskContext,
                            builder, merger);
                } else {
                    throw new SQLException("Can not locate table shard for " + logicDbName);
                }
            }

            tempTableName = tempTableName + tableShardStr;

            if (taskContext instanceof DalContextConfigure)
                ((DalContextConfigure) taskContext).addTables(tempTableName);

            String compiledSql = compileSql(tableBuilder.build(tableShardStr), originalParameters);
            return task.execute(client, compiledSql, compiledParameters, hints.clone(), taskContext.fork());
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

            return DalShardingHelper.buildShardStr(logicDbName, tempTableShardId);
        }

        private ResultMerger createResultMerger(boolean isTableShardingEnabled, ResultMerger<T> merger,
                SqlBuilder builder, DalHints hints) throws SQLException {
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

    }

}
