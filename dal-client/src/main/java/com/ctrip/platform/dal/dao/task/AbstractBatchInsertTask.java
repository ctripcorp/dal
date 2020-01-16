package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalContextClient;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractBatchInsertTask<T> extends InsertTaskAdapter<T> implements BulkTask<int[], T> {

    @Override
    public int[] getEmptyValue() {
        return new int[0];
    }

    @Override
    public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, DalBulkTaskContext<T> taskContext) throws SQLException {
        StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
        int i = 0;

        Set<String> unqualifiedColumns = taskContext.getUnqualifiedColumns();

        for (Integer index :daoPojos.keySet()) {
            Map<String, ?> pojo = daoPojos.get(index);
            removeUnqualifiedColumns(pojo, unqualifiedColumns);

            StatementParameters parameters = new StatementParameters();
            addParameters(parameters, pojo);
            parametersList[i++] = parameters;
        }

        String tableName = getRawTableName(hints);
        if (taskContext instanceof DalContextConfigure) {
            ((DalContextConfigure) taskContext).addTables(tableName);
            ((DalContextConfigure) taskContext).setShardingCategory(shardingCategory);
        }

        String batchInsertSql = buildBatchInsertSql(hints, unqualifiedColumns, tableName);

        int[] result;
        if (client instanceof DalContextClient)
            result = ((DalContextClient) client).batchUpdate(batchInsertSql, parametersList, hints, taskContext);
        else
            throw new DalRuntimeException("The client is not instance of DalClient");
        return result;
    }

    private String buildBatchInsertSql(DalHints hints, Set<String> unqualifiedColumns, String tableName) throws SQLException {
        List<String> finalInsertableColumns = buildValidColumnsForInsert(unqualifiedColumns);

        String values = combine(PLACE_HOLDER, finalInsertableColumns.size(), COLUMN_SEPARATOR);
        String insertColumns = combineColumns(finalInsertableColumns, COLUMN_SEPARATOR);

        return String.format(getSqlTpl(), quote(tableName), insertColumns, values);
    }

    @Override
    public BulkTaskResultMerger<int[]> createMerger() {
        return new ShardedIntArrayResultMerger();
    }

    protected abstract String getSqlTpl();
}
