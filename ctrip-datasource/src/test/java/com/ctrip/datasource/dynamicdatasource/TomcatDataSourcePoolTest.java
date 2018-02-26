package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.datasource.datasource.CtripDataSourceTerminateTask;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.datasource.DataSourceTerminator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import com.ctrip.platform.dal.dao.datasource.SingleDataSourceTask;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TomcatDataSourcePoolTest {
    private static final String name = "mysqldaltest01db_W";
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testTomcatDataSourcePool() throws Exception {
        DataSourceConfigure dataSourceConfigure = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        final SingleDataSource dataSource = new SingleDataSource(name.toLowerCase(), dataSourceConfigure);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = dataSource.getDataSource().getConnection();
                    connection.createStatement().execute("select sleep(30)");
                    // connection.close();
                } catch (Throwable e) {
                }
            }
        });

        Thread.sleep(2 * 1000);
        SingleDataSourceTask task = new SingleDataSourceTask(dataSource, new CtripDataSourceTerminateTask());
        DataSourceTerminator.getInstance().close(task);
        Thread.sleep(30000 * 1000);
    }
}
