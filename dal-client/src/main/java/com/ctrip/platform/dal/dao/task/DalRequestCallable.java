package com.ctrip.platform.dal.dao.task;

import java.util.concurrent.Callable;

/**
 * @author c7ch23en
 */
public class DalRequestCallable<V> implements Callable<V> {

    private final String logicDbName;
    private final String shard;
    private final Callable<V> task;

    public DalRequestCallable(String logicDbName, String shard, Callable<V> task) {
        this.logicDbName = logicDbName;
        this.shard = shard;
        this.task = task;
    }

    @Override
    public V call() throws Exception {
        return task.call();
    }

    public String getLogicDbName() {
        return logicDbName;
    }

    public String getShard() {
        return shard;
    }

    protected Callable<V> getTask() {
        return task;
    }

}
