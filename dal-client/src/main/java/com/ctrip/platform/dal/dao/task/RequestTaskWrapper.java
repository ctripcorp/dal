package com.ctrip.platform.dal.dao.task;

import java.util.concurrent.Callable;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.exceptions.DalException;

public class RequestTaskWrapper<T> implements Callable<T> {
    private DalLogger logger = DalClientFactory.getDalLogger();
    private String shard;
    private Callable<T> task;
    private LogContext logContext;
    
    public RequestTaskWrapper(String shard, Callable<T> task, LogContext logContext) {
        this.shard = shard;
        this.task = task;
        this.logContext = logContext;
    }
    
    @Override
    public T call() throws Exception {
        Throwable error = null;
        T result = null;
        
        logger.startTask(logContext, shard);
        
        try {
            LogEntry.populateCurrentCaller(logContext.getCaller());
            
            result = task.call();
            
            LogEntry.clearCurrentCaller();
        } catch (Throwable e) {
            error = e;
        }

        logger.endTask(logContext, shard, error);
        
        if(error != null)
            throw DalException.wrap(error);
        
        return result;
    }
}
