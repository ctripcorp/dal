package com.ctrip.platform.dal.dao.task;

import java.util.concurrent.FutureTask;

/**
 * @author c7ch23en
 */
public class DalRequestFutureTask<V> extends FutureTask<V> {

    private final String logicDbName;
    private final String shard;
    private final DalThreadPoolExecutor executor;

    private STATUS status;

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
