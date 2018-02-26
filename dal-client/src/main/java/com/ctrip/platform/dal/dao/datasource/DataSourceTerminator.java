package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DataSourceTerminator {
    private static volatile DataSourceTerminator terminator = null;
    private static volatile ScheduledExecutorService service = null;
    private static final int INIT_DELAY = 0;

    private static final int POOL_SIZE = 4;
    private static final String THREAD_NAME = "DataSourceTerminator";

    public synchronized static DataSourceTerminator getInstance() {
        if (terminator == null) {
            terminator = new DataSourceTerminator();
            service = Executors.newScheduledThreadPool(POOL_SIZE, new CustomThreadFactory(THREAD_NAME));
        }

        return terminator;
    }

    public synchronized void close(final SingleDataSourceTask task) {
        if (task == null)
            return;

        DataSourceTerminateTask temp = task.getDataSourceTerminateTask();
        DataSourceTerminateTask terminateTask = temp != null ? temp : new DefaultDataSourceTerminateTask();
        terminateTask.init(task.getSingleDataSource());
        ScheduledFuture future = service.schedule(terminateTask, INIT_DELAY, TimeUnit.MILLISECONDS);
    }

}
