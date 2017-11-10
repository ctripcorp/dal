package com.ctrip.platform.dal.sql.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.task.DalRequest;
import com.ctrip.platform.dal.dao.task.DalRequestExecutor;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.ForkedTransaction;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

public class DalCatLogger {
	private static final String RECORD_COUNT = "DAL.recordCount";
	
	private static final String AFFECTED_ROWS = "DAL.affectedRows";
    private static final String TYPE_SQL_REQUEST_EXECUTION = "SQL.dao";
    private static final String TYPE_SQL_CROSS_SHARD_TASK_EXECUTION = "SQL.crossShard";
    private static final String TYPE_SQL_TASK_EXECUTION = "SQL.task";
    private static final String TYPE_SQL_STATEMENT_EXECUTION = "SQL.statement";
    
    private static final String TYPE_SQL_ASYN_EXECUTION = "DAL.isAsynchcronized";
    private static final String TYPE_SQL_CROSS_SHARD_EXECUTION_TYPE = "DAL.executionType";
    private static final String TYPE_SQL_TASK_COUNT = "DAL.shardCount";
    private static final String TYPE_SQL_SHARDS = "DAL.shards";
    private static final String TYPE_SQL_GET_CONNECTION_COST = "DAL.getConnectionCost";
    private static final String TYPE_SQL_START_TASK_POOL_SIZE = "DAL.startTaskPoolSize";
    private static final String TYPE_SQL_END_TASK_POOL_SIZE = "DAL.endTaskPoolSize";
    
	public static void start(CtripLogEntry entry) {
		try {
			String sqlType = getCaller(entry);
			Transaction catTransaction = Cat.newTransaction(CatConstants.TYPE_SQL, sqlType);
			entry.setCatTransaction(catTransaction);
			
			String method = entry.getEvent() == null ? "dal_test" : CatInfo.getTypeSQLInfo(entry.getEvent());
			if(entry.getPramemters() != null){
				Cat.logEvent(CatConstants.TYPE_SQL_METHOD, method, Message.SUCCESS, entry.getEncryptParameters(DalCLogger.isEncryptLogging(), entry).replaceAll(",", "&"));
			} else {
				Cat.logEvent(CatConstants.TYPE_SQL_METHOD, method, Message.SUCCESS, "");
			}
			
			catTransaction.addData(entry.getSqls() == null ? "" : StringUtils.join(entry.getSqls(), ";"));
			if (entry.getCallString() != null && entry.getCallString().length() > 0)
				catTransaction.addData(entry.getCallString());
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void catTransactionSuccess(CtripLogEntry entry, int count){
		try {
			Cat.logEvent(CatConstants.TYPE_SQL_DATABASE, entry.getDbUrl());
			
			if(entry.getEvent() == DalEventEnum.QUERY)
			    Cat.logEvent(RECORD_COUNT, String.valueOf(entry.getResultCount()));
			else {
    			if(entry.getAffectedRows()!=null)
    			    Cat.logEvent(AFFECTED_ROWS, String.valueOf(entry.getAffectedRows()));
    			
    			if(entry.getAffectedRowsArray()!=null)
    			    Cat.logEvent(AFFECTED_ROWS, String.valueOf(entry.getAffectedRowsArray()));
			}
			
			entry.getCatTransaction().setStatus(Transaction.SUCCESS);
			entry.getCatTransaction().complete();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void catTransactionFailed(CtripLogEntry entry, Throwable e){
		try {
			Cat.logEvent(CatConstants.TYPE_SQL_DATABASE, entry.getDbUrl());
			Cat.logEvent(RECORD_COUNT, String.valueOf(entry.getResultCount()));
			entry.getCatTransaction().setStatus(e);
			Cat.logError(e);
			entry.getCatTransaction().complete();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}
	
    public static <T> LogContext start(DalRequest<T> request) {
        CtripLogContext clc = new CtripLogContext();
        String caller = request.isAsynExecution() ? request.getCaller() + "[asynchcronized]" : request.getCaller();
        clc.setCaller(caller);
        clc.setRequestTransaction(Cat.newTransaction(TYPE_SQL_REQUEST_EXECUTION, clc.getCaller()));
        Cat.logEvent(CtripDalLogger.DAL_VERSION, CtripDalLogger.getDalVersion());
        Cat.logEvent(TYPE_SQL_ASYN_EXECUTION, String.valueOf(request.isAsynExecution()));
        return clc;
    }

    public static void end(LogContext logContext, final Throwable e) {
        endTransaction(((CtripLogContext)logContext).getRequestTransaction(), e);
    }

    public static void startCrossShardTasks(LogContext logContext, boolean isSequentialExecution) {
        CtripLogContext clc = (CtripLogContext)logContext;
        clc.setCorssShardTasksTransaction(Cat.newTransaction(TYPE_SQL_CROSS_SHARD_TASK_EXECUTION, clc.getCaller()));
        Cat.logEvent(TYPE_SQL_CROSS_SHARD_EXECUTION_TYPE, isSequentialExecution ? "sequential" : "parallel");
        Cat.logEvent(TYPE_SQL_START_TASK_POOL_SIZE, String.valueOf(DalRequestExecutor.getPoolSize()));
        Cat.logEvent(TYPE_SQL_TASK_COUNT, String.valueOf(logContext.getShards().size()));
        Cat.logEvent(TYPE_SQL_SHARDS, logContext.getShards().toString());
        
        if(isSequentialExecution)
            return;
        
        //Create ForkedTransaction
        Map<String, ForkedTransaction> taskTransactions = new ConcurrentHashMap<String, ForkedTransaction>();
        for(String shard: clc.getShards()) {
            ForkedTransaction subTransaction = Cat.newForkedTransaction(TYPE_SQL_TASK_EXECUTION,clc.getCaller() + ":shard:" + shard);
            taskTransactions.put(shard, subTransaction);
        }
        clc.setTaskTransactions(taskTransactions);
    }

    public static void endCrossShards(LogContext logContext, final Throwable e) {
        Cat.logEvent(TYPE_SQL_END_TASK_POOL_SIZE, String.valueOf(DalRequestExecutor.getPoolSize()));
        endTransaction(((CtripLogContext)logContext).getCorssShardTasksTransaction(), e);
    }

    public static void startTask(LogContext logContext, String shard) {
        CtripLogContext clc = (CtripLogContext)logContext;
        if(logContext.isSingleTask() || logContext.isSeqencialExecution())
            clc.setSingleTaskTransaction(Cat.newTransaction(TYPE_SQL_TASK_EXECUTION, clc.getCaller()));
        else
            clc.getTaskTransactions().get(shard).fork();
    }

    public static void endTask(LogContext logContext, String shard, final Throwable e) {
        CtripLogContext clc = (CtripLogContext)logContext;
        Transaction tran = logContext.isSingleTask() || logContext.isSeqencialExecution()? 
            clc.getSingleTaskTransaction():
            clc.getTaskTransactions().get(shard);

        endTransaction(tran, e);
    }

    public static void startStatement(CtripLogEntry entry) {
        Cat.logEvent(TYPE_SQL_GET_CONNECTION_COST, String.valueOf(entry.getConnectionCost())+ " ms");
        String sqlType = getCaller(entry);
        entry.setStatementTransaction(Cat.newTransaction(TYPE_SQL_STATEMENT_EXECUTION, sqlType));
    }

    public static void endStatement(CtripLogEntry entry, final Throwable e) {
        endTransaction(entry.getStatementTransaction(), e);
    }
    
    private static void endTransaction(Transaction tran, Throwable e) {
        if(e == null)
            tran.setStatus(Transaction.SUCCESS);
        else {
            tran.setStatus(e);
            Cat.logError(e);
        }
        tran.complete();
    }
    
    private static String getCaller(CtripLogEntry entry) {
        String sqlType = entry.getDao() + "." + entry.getMethod();
        
        if("java.util.concurrent.FutureTask$Sync.innerRun".equals(sqlType))
            return "dalAsyncAction";
        
        return sqlType;
    }
}
