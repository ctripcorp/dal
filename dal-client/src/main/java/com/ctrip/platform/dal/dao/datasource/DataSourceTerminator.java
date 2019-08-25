package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;

import javax.sql.DataSource;
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

    private DataSourceTerminateTaskFactory factory = null;

    public synchronized static DataSourceTerminator getInstance() {
        if (terminator == null) {
            terminator = new DataSourceTerminator();
            terminator.initDataSourceTerminateTaskFactory();
        }

        return terminator;
    }

    private void initDataSourceTerminateTaskFactory() {
        factory = ServiceLoaderHelper.getInstance(DataSourceTerminateTaskFactory.class);
    }

    public void close(SingleDataSource oldDataSource) {
        DataSourceTerminateTask task;
        if (factory != null) {
            task = factory.createTask(oldDataSource);
        } else {
            task = new DefaultDataSourceTerminateTask(oldDataSource);
        }
        task.setScheduledExecutorService(service);
        service.schedule(task, INIT_DELAY, TimeUnit.MILLISECONDS);
    }

    public void close(String name, DataSource ds, DataSourceConfigure configure) {
        DataSourceTerminateTask task;
        if (factory != null) {
            task = factory.createTask(name, ds, configure);
        } else {
            task = new DefaultDataSourceTerminateTask(name, ds, configure);
        }
        task.setScheduledExecutorService(service);
        service.schedule(task, INIT_DELAY, TimeUnit.MILLISECONDS);
    }

}
