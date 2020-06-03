package com.ctrip.platform.dal.dao.task;

import java.util.concurrent.Callable;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.exceptions.DalException;

public class RequestTaskWrapper<T> extends DalRequestCallable<T> implements Callable<T> {
    private final static String UNKNOWN_SHARD = "N/A";
    private final DalLogger logger = DalClientFactory.getDalLogger();
    private final String shard;
    private final LogContext logContext;

    public RequestTaskWrapper(Callable<T> task, LogContext logContext) {
        super(null, null, task);
        this.shard = UNKNOWN_SHARD;
        this.logContext = logContext;
    }

    public RequestTaskWrapper(String logicDbName, String shard, Callable<T> task, LogContext logContext) {
        super(logicDbName, shard, task);
        this.shard = shard != null ? shard : UNKNOWN_SHARD;
        this.logContext = logContext;
    }

    @Override
    public T call() throws Exception {
        Throwable error = null;
        T result = null;

        logger.startTask(logContext, shard);

        try {
            LogEntry.populateCurrentCaller(logContext.getCaller());

            result = getTask().call();

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
