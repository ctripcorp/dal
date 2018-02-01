package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.datasource.DataSourceTerminator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSourceTerminatorTest {
    private static final String name = "mysqldaltest01db_W";
    private static ExecutorService executorService1 = Executors.newSingleThreadExecutor();
    private static ExecutorService executorService2 = Executors.newSingleThreadExecutor();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testDataSourceTerminator() throws Exception {
        DataSourceConfigure dataSourceConfigure = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        final SingleDataSource dataSource = new SingleDataSource(name.toLowerCase(), dataSourceConfigure);

        // leaked busy connection
        executorService1.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = dataSource.getDataSource().getConnection();
                    connection.createStatement().execute("select sleep(70)");
                    // connection.close();
                } catch (Throwable e) {
                }
            }
        });

        // idle connection
        executorService2.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 5; i++) {
                        Connection connection = dataSource.getDataSource().getConnection();
                        connection.createStatement().execute("select 1");
                        connection.close();
                        Thread.sleep(1 * 1000);
                    }
                } catch (Throwable e) {
                }
            }
        });

        System.out.println("Sleep for 2 seconds...");
        Thread.sleep(2 * 1000);

        DataSourceTerminator.getInstance().close(dataSource);
        System.out.println("Sleep for 600 seconds...");
        Thread.sleep(600 * 1000);
    }

}
