package com.ctrip.platform.dal.dao.task;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.DalThreadPoolExecutorConfig;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author c7ch23en
 */
public class DalThreadPoolExecutor extends ThreadPoolExecutor {

    private final DalThreadPoolExecutorConfig executorConfig;
    private final Map<DalRequestIdentity, AtomicInteger> runningTasks = new ConcurrentHashMap<>();

    public DalThreadPoolExecutor(DalThreadPoolExecutorConfig executorConfig,
                                 BlockingQueue<Runnable> workQueue) {
        super(executorConfig.getCorePoolSize(), executorConfig.getMaxPoolSize(),
                executorConfig.getKeepAliveSeconds(), TimeUnit.SECONDS, workQueue);
        this.executorConfig = executorConfig;
    }

    public DalThreadPoolExecutor(DalThreadPoolExecutorConfig executorConfig,
                                 BlockingQueue<Runnable> workQueue,
                                 ThreadFactory threadFactory) {
        super(executorConfig.getCorePoolSize(), executorConfig.getMaxPoolSize(),
                executorConfig.getKeepAliveSeconds(), TimeUnit.SECONDS, workQueue, threadFactory);
        this.executorConfig = executorConfig;
    }

    public DalThreadPoolExecutor(DalThreadPoolExecutorConfig executorConfig,
                                 BlockingQueue<Runnable> workQueue,
                                 RejectedExecutionHandler handler) {
        super(executorConfig.getCorePoolSize(), executorConfig.getMaxPoolSize(),
                executorConfig.getKeepAliveSeconds(), TimeUnit.SECONDS, workQueue, handler);
        this.executorConfig = executorConfig;
    }

    public DalThreadPoolExecutor(DalThreadPoolExecutorConfig executorConfig,
                                 BlockingQueue<Runnable> workQueue,
                                 ThreadFactory threadFactory,
                                 RejectedExecutionHandler handler) {
        super(executorConfig.getCorePoolSize(), executorConfig.getMaxPoolSize(),
                executorConfig.getKeepAliveSeconds(), TimeUnit.SECONDS, workQueue, threadFactory, handler);
        this.executorConfig = executorConfig;
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof DalRequestCallable)
            return new DalRequestFutureTask<>((DalRequestCallable<T>) callable, this);
        return super.newTaskFor(callable);
    }

    public void tryExecute(DalRequestFutureTask<?> task) {
        String logicDbName = task.getLogicDbName();
        String shard = task.getShard();
        if (!StringUtils.isEmpty(logicDbName) && !StringUtils.isEmpty(shard)) {
            DalRequestIdentity shardIdentity = new DalRequestIdentity(logicDbName, shard);
            AtomicInteger shardTaskCount = runningTasks.get(shardIdentity);
            if (shardTaskCount == null) {
                synchronized (runningTasks) {
                    shardTaskCount = runningTasks.get(shardIdentity);
                    if (shardTaskCount == null) {
                        shardTaskCount = new AtomicInteger(0);
                        runningTasks.put(shardIdentity, shardTaskCount);
                    }
                }
            }
            int maxThreadsPerShard = executorConfig.getMaxThreadsPerShard(logicDbName);
            if (maxThreadsPerShard > 0 && shardTaskCount.incrementAndGet() > maxThreadsPerShard)
                shardTaskCount.decrementAndGet();
            else {
                try {
                    // actual execution
                    task.internalRun();
                    return;
                } finally {
                    shardTaskCount.decrementAndGet();
                }
            }
        }
        // retry execution, possibly put to the end of the taskQueue
        execute(task);
    }

    static class DalRequestIdentity {
        private final String logicDbName;
        private final String shard;

        public DalRequestIdentity(String logicDbName, String shard) {
            this.logicDbName = logicDbName;
            this.shard = shard;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DalRequestIdentity that = (DalRequestIdentity) o;
            return Objects.equals(logicDbName, that.logicDbName) &&
                    Objects.equals(shard, that.shard);
        }

        @Override
        public int hashCode() {
            return Objects.hash(logicDbName, shard);
        }

        @Override
        public String toString() {
            return "DalRequestIdentity {" +
                    "logicDbName='" + logicDbName + '\'' +
                    ", shard='" + shard + '\'' +
                    '}';
        }
    }

    protected DalThreadPoolExecutorConfig getExecutorConfig() {
        return executorConfig;
    }

}
