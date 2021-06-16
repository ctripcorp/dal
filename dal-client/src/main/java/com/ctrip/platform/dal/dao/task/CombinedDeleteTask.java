package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalContextClient;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CombinedDeleteTask<T> extends AbstractCombinedInsertTask<T>{

    private static final String TMPL_COMBINED_SQL_DELETE = "DELETE FROM %s WHERE %s in (%s)";
    private static final String IN_TEMPLATE = "(%s),";

    @Override
    public Integer execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, DalBulkTaskContext<T> taskContext) throws SQLException {
        StatementParameters parameters = new StatementParameters();
        StringBuilder values = new StringBuilder();

        List<String> pkColumns = clonePkColumns();
        int startIndex = 1;
        for (Integer index : daoPojos.keySet()) {
            Map<String, ?> daoPojo = daoPojos.get(index);

            int paramCount = addParameters(startIndex, parameters, daoPojo, pkColumns);
            startIndex += paramCount;
            values.append(String.format(IN_TEMPLATE, combine("?", paramCount, ",")));
        }
        String tableName = getRawTableName(hints);
        if (taskContext instanceof DalContextConfigure) {
            ((DalContextConfigure) taskContext).addTables(tableName);
            ((DalContextConfigure) taskContext).setShardingCategory(shardingCategory);
        }

        String sql = String.format(TMPL_COMBINED_SQL_DELETE,
                quote(tableName),
                combineColumns(pkColumns, COLUMN_SEPARATOR),
                values.substring(0, values.length() - 2) + ")");

        if (client instanceof DalContextClient)
            return ((DalContextClient) client).update(sql, parameters, hints, taskContext);
        else
            throw new DalRuntimeException("The client is not instance of DalClient");
    }

    @Override
    protected String getSqlTpl() {
        return null;
    }

    protected List<String> clonePkColumns() {
        List<String> pk = new ArrayList<>();
        for (String s : pkColumns)
            pk.add(s);
        return pk;
    }
 }
