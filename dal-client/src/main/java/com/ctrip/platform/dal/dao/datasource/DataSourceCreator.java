package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.ServiceLoaderHelper;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataSourceCreator {
    private static volatile DataSourceCreator creator = null;

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

    public SingleDataSource createSingleDataSource(String name, DataSourceConfigure configure, DataSourceCreatePoolListener listener) {
        SingleDataSource singleDataSource = new SingleDataSource(name, configure,null);
        singleDataSource.setListener(listener);
        DataSourceCreateTask task;
        if (factory != null)
            task = factory.createTask(name, configure, singleDataSource);
        else
            task = new DefaultDataSourceCreateTask(name, configure, singleDataSource);
        singleDataSource.setTask(task);

        return singleDataSource;
    }
}
