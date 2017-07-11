package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.task.AbstractIntArrayBulkTask;
import com.ctrip.platform.dal.dao.task.BulkTaskContext;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CtripSptTask<T> extends AbstractIntArrayBulkTask<T> {
    private static final String TVP_TPL = "TVP_%s";
    private static final String TVP_EXEC = "exec %s %s";

    private String sptTpl;
    
    public CtripSptTask(String sptTpl) {
        this.sptTpl = sptTpl;
    }

    @Override
    public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, BulkTaskContext<T> taskContext)
            throws SQLException {
        String tableName = getRawTableName(hints);
        String tvpName = buildTvpName(tableName);
        String spName = String.format(sptTpl, tableName);
        String execSql = buildExecSql(spName);
        SQLServerDataTable dataTable = getDataTable(daoPojos);
        StatementParameters parameters = new StatementParameters();
        int index = 1;
        parameters.set(index, tvpName, DatabaseCategory.SQL_SERVER_TYPE_TVP, dataTable);
        DalHints newHints = hints.clone();
        newHints.retrieveAllResultsFromSp();
        int length = daoPojos.size();
        int[] result = new int[length];
        Map<String, ?> map = client.call(execSql, parameters, newHints);
        int value = getReturnValue(map);
        for (int i = 0; i < length; i++) {
            result[i] = value;
        }
        return result;
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

    private int getReturnValue(Map<String, ?> map) {
        int result = -1;
        if (map == null || map.size() == 0)
            return result;

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                List list = (ArrayList) entry.getValue();
                if (list.size() == 1) {
                    Map<String, Integer> temp = (Map<String, Integer>) list.get(0);
                    if (temp == null || temp.size() == 0)
                        return result;

                    for (Map.Entry<String, Integer> e : temp.entrySet()) {
                        result = e.getValue().intValue();
                        break;
                    }
                }
            } catch (Throwable e1) {
            }
            break;
        }
        return result;
    }

    private String buildTvpName(String tableName) {
        return String.format(TVP_TPL, tableName);
    }

    private String buildExecSql(String spName) {
        return String.format(TVP_EXEC, spName, PLACE_HOLDER);
    }

}
