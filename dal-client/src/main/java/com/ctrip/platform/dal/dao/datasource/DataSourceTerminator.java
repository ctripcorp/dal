package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DataSourceTerminator {
    private static volatile DataSourceTerminator terminator = null;
    private ScheduledExecutorService service =
            Executors.newScheduledThreadPool(POOL_SIZE, new CustomThreadFactory(THREAD_NAME));

    private static final int INIT_DELAY = 0;
    private static final int POOL_SIZE = 4;
    private static final String THREAD_NAME = "DataSourceTerminator";

    public synchronized static DataSourceTerminator getInstance() {
        if (terminator == null) {
            terminator = new DataSourceTerminator();
        }

        return terminator;
    }

    public void close(final SingleDataSource oldDataSource) {
        DataSourceTerminateTaskFactory factory = ServiceLoaderHelper.getInstance(DataSourceTerminateTaskFactory.class);
        DataSourceTerminateTask task;
        if (factory == null) {
            task = new DefaultDataSourceTerminateTask(oldDataSource);
        } else {
            task = factory.createTask(oldDataSource);
        }

        task.setScheduledExecutorService(service);
        ScheduledFuture future = service.schedule(task, INIT_DELAY, TimeUnit.MILLISECONDS);
    }

}
