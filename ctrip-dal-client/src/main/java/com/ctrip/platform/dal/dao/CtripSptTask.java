package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.TVPHelper;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.dao.task.*;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtripSptTask<T> extends AbstractIntArrayBulkTask<T> {
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String TVP_TPL = "TVP_%s";
    private static final String TVP_EXEC = "exec %s %s";

    private String sptTpl;
    private TVPHelper tvpHelper;

    public CtripSptTask(String sptTpl, TVPHelper tvpHelper) {
        this.sptTpl = sptTpl;
        this.tvpHelper = tvpHelper;
    }

    @Override
    public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, DalBulkTaskContext<T> taskContext)
            throws SQLException {
        String tableName = getRawTableName(hints);
        String tvpName = buildTvpName(tableName);
        String spName = String.format(sptTpl, tableName);
        String execSql = buildExecSql(spName);
        SQLServerDataTable dataTable = getDataTable(tvpName, daoPojos);
        StatementParameters parameters = new StatementParameters();
        int index = 1;
        parameters.set(index, tvpName, DatabaseCategory.SQL_SERVER_TYPE_TVP, dataTable);
        DalHints newHints = hints.clone();
        newHints.retrieveAllResultsFromSp();
        int length = daoPojos.size();
        int[] result = new int[length];

        if (taskContext instanceof DalContextConfigure)
            ((DalContextConfigure) taskContext).addTables(tableName);

        if (client instanceof DalContextClient) {
            Map<String, ?> map = ((DalContextClient) client).call(execSql, parameters, newHints, taskContext);
            int value = getReturnValue(map);
            for (int i = 0; i < length; i++) {
                result[i] = value;
            }
            return result;
        } else
            throw new DalRuntimeException("The client is not instance of DalClient");
    }

    private SQLServerDataTable getDataTable(String tvpName, Map<Integer, Map<String, ?>> daoPojos) throws SQLException {
        if (daoPojos == null || daoPojos.size() == 0)
            return null;

        SQLServerDataTable dataTable = getDataTableByMetadata(tvpName, daoPojos);
        return dataTable;
    }

    private SQLServerDataTable getDataTableByMetadata(String tvpName, Map<Integer, Map<String, ?>> daoPojos)
            throws SQLException {
        SQLServerDataTable dataTable = null;
        List<String> orderedColumns = null;
        try {
            orderedColumns = tvpHelper.getTVPColumns(logicDbName, tvpName, columnTypes, client);
        } catch (Throwable e) {
            LOGGER.error(String.format("An error occured while getting tvp columns,logic db name: %s,tvp name: %s,",
                    logicDbName, tvpName), e);
        }

        if (orderedColumns == null || orderedColumns.isEmpty()) {
            throw new DalException(String.format("Columns of TVP %s are null or empty.", tvpName));
        }

        dataTable = new SQLServerDataTable();
        Map<String, Integer> map = new HashMap<>(columnTypes);
        // add column metadata
        for (String column : orderedColumns) {
            Integer value = map.get(column);
            if (value == null)
                continue;

            dataTable.addColumnMetadata(column, value);
        }

        // add actual data
        for (Map.Entry<Integer, Map<String, ?>> entry : daoPojos.entrySet()) {
            Map<String, ?> temp = entry.getValue();
            List<Object> list = new ArrayList<>();
            for (String column : orderedColumns) {
                Object value = temp.get(column);
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
