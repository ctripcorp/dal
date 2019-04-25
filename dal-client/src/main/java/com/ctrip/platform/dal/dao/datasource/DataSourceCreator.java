package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataSourceCreator {
    private static volatile DataSourceCreator creator = null;
    private ScheduledExecutorService service =
            Executors.newScheduledThreadPool(POOL_SIZE, new CustomThreadFactory(THREAD_NAME));

    private static final int INIT_DELAY = 0;
    private static final int POOL_SIZE = 1;
    private static final String THREAD_NAME = "DataSourceCreator";

    private DataSourceCreateTaskFactory factory = null;

    public synchronized static DataSourceCreator getInstance() {
        if (creator == null) {
            creator = new DataSourceCreator();
            creator.initDataSourceCreateTaskFactory();
        }

        return creator;
    }

    private void initDataSourceCreateTaskFactory() {
        factory = ServiceLoaderHelper.getInstance(DataSourceCreateTaskFactory.class);
    }

    public SingleDataSource createSingleDataSource(String name, DataSourceConfigure configure) {
        SingleDataSource singleDataSource = new SingleDataSource(name, configure,null);
        DataSourceCreateTask task;
        if (factory != null)
            task = factory.createTask(name, configure, singleDataSource);
        else
            task = new DefaultDataSourceCreateTask(name, configure, singleDataSource);
        singleDataSource.setTask(task);
        service.schedule(task, INIT_DELAY, TimeUnit.MILLISECONDS);
        return singleDataSource;
    }
}
