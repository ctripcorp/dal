package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.task.AbstractIntArrayBulkTask;
import com.ctrip.platform.dal.dao.task.BulkTaskContext;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class CtripSptTask<T> extends AbstractIntArrayBulkTask<T> {
    protected static final String TVP_TPL = "TVP_%s";
    protected static final String TVP_EXEC = "exec %s %s";

    public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, BulkTaskContext<T> taskContext,
            String sptTpl) throws SQLException {
        String tableName = getRawTableName(hints);
        String tvpName = buildTvpName(tableName);
        String spName = String.format(sptTpl, tableName);
        String execSql = buildExecSql(spName);
        SQLServerDataTable dataTable = getDataTable(daoPojos);
        StatementParameters parameters = new StatementParameters();
        int index = 1;
        parameters.set(index, tvpName, -1, dataTable);
        DalHints newHints = hints.clone();
        newHints.retrieveAllResultsFromSp();
        Map<String, ?> map = client.call(execSql, parameters, newHints);
        return new int[0];
    }

    private SQLServerDataTable getDataTable(Map<Integer, Map<String, ?>> daoPojos) throws SQLServerException {
        if (daoPojos == null || daoPojos.size() == 0)
            return null;
        Map<String, Integer> map = new TreeMap<>(columnTypes);
        if (map == null || map.size() == 0)
            return null;

        SQLServerDataTable dataTable = new SQLServerDataTable();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            dataTable.addColumnMetadata(entry.getKey(), entry.getValue().intValue());
        }

        for (Map.Entry<Integer, Map<String, ?>> entry : daoPojos.entrySet()) {
            Map<String, ?> temp = entry.getValue();
            List<Object> list = new ArrayList<>();
            for (String key : map.keySet()) {
                Object value = temp.get(key);
                list.add(value); // if value==null ?
            }
            dataTable.addRow(list.toArray());
        }

        return dataTable;
    }

    private String buildTvpName(String tableName) {
        return String.format(TVP_TPL, tableName);
    }

    private String buildExecSql(String spName) {
        return String.format(TVP_EXEC, spName, PLACE_HOLDER);
    }

}
