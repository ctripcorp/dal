package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.concurrent.FutureTask;

/**
 * @author c7ch23en
 */
public class DalRequestFutureTask<V> extends FutureTask<V> {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private final String logicDbName;
    private final String shard;
    private final DalThreadPoolExecutor executor;

    private STATUS status;

    private volatile long lastLogLimitedTime = 0;

    public DalRequestFutureTask(DalRequestCallable<V> callable, DalThreadPoolExecutor executor) {
        super(callable);
        this.logicDbName = callable.getLogicDbName();
        this.shard = callable.getShard();
        this.executor = executor;
        status = STATUS.INITIALIZED;
    }

    public String getLogicDbName() {
        return logicDbName;
    }

    public String getShard() {
        return shard;
    }

    @Override
    public void run() {
        executor.tryExecute(this);
    }

    protected void internalRun() {
        try {
            status = STATUS.RUNNING;
            super.run();
        } finally {
            status = STATUS.DONE;
        }
    }

    protected void logLimited(int limit) {
        try {
            long now = System.currentTimeMillis();
            if (now - lastLogLimitedTime >= 1000) {
                lastLogLimitedTime = now;
                LOGGER.logEvent(DalLogTypes.DAL_VALIDATION,
                        String.format("ShardThreadsLimited::%s-%s", logicDbName, shard),
                        "maxThreadsPerShard=" + limit);
                LOGGER.info(String.format("ShardThreadsLimited::%s-%s, maxThreadsPerShard=%d",
                        logicDbName, shard, limit));
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    @Override
    public boolean isDone() {
        return status == STATUS.DONE && super.isDone();
    }

    enum STATUS {
        INITIALIZED,
        RUNNING,
        DONE
    }

}
