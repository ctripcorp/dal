package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.AbstractIntArrayBulkTask;
import com.ctrip.platform.dal.dao.task.BulkTaskContext;

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
    public int[] execute(DalHints hints, Map<Integer, Map<String, ?>> daoPojos, BulkTaskContext<T> tastContext) throws SQLException {
        String spName = String.format(spTempName, getRawTableName(hints));

        String callSql = CtripSqlServerSpBuilder.buildSqlServerCallSql(spName, columns);
        StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
        
        int i = 0;
        for (Integer index :daoPojos.keySet()) {
            StatementParameters parameters = new StatementParameters();

            addParametersByIndex(parameters, daoPojos.get(index), columns);
            
            parametersList[i++] = parameters;
        }
        
        return client.batchCall(callSql, parametersList, hints);
    }   
}