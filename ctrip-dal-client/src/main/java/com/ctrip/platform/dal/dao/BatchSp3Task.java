package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.*;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

/**
 * Common SP3 task for batch operation
 * @author jhhe
 *
 * @param <T>
 */
public class BatchSp3Task<T> extends AbstractIntArrayBulkTask<T> {
    private String spTempName;
    private String[] columns;
    
    public BatchSp3Task(String spName, String[] columns) {
        this.spTempName = spName;
        this.columns = columns;
    }
    
    @Override
    public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, DalBulkTaskContext<T> taskContext) throws SQLException {
        String tableName = getRawTableName(hints);
        String spName = String.format(spTempName, tableName);

        String callSql = buildSqlServerCallSql(spName, columns);
        StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
        
        int i = 0;
        for (Integer index :daoPojos.keySet()) {
            StatementParameters parameters = new StatementParameters();

            addParametersByIndex(parameters, daoPojos.get(index), columns);
            
            parametersList[i++] = parameters;
        }

        if (taskContext instanceof DalContextConfigure)
            ((DalContextConfigure) taskContext).addTables(tableName);

        if (client instanceof DalContextClient)
            return ((DalContextClient) client).batchCall(callSql, parametersList, hints, taskContext);
        else
            throw new DalRuntimeException("The client is not instance of DalClient");
    }   
}