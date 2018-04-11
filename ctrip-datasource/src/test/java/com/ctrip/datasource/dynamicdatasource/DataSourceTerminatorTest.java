package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.datasource.DataSourceTerminator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSourceTerminatorTest {
    private static final String name1 = "mysqldaltest01db_W";
    private static final String name2 = "mysqldaltest02db_W";
    private static ExecutorService executorService1 = Executors.newSingleThreadExecutor();
    private static ExecutorService executorService2 = Executors.newSingleThreadExecutor();
    private static ExecutorService executorService3 = Executors.newFixedThreadPool(4);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testAbandonedConnection() throws Exception {
        _testAbandonedConnection();
        System.out.println("Sleep for 300 seconds...");
        Thread.sleep(300 * 1000);
    }

    @Test
    public void testBusyConnection() throws Exception {
        _testBusyConnection();
        System.out.println("Sleep for 300 seconds...");
        Thread.sleep(300 * 1000);
    }

    @Test
    public void testHangConnection() throws Exception {
        _testHangConnection();
        System.out.println("Sleep for 300 seconds...");
        Thread.sleep(300 * 1000);
    }

    @Test
    public void testMutipleConnections() throws Exception {
        _testMultipleConnections();
        System.out.println("Sleep for 300 seconds...");
        Thread.sleep(300 * 1000);
    }

    private void _testAbandonedConnection() throws Exception {
        DataSourceConfigure dataSourceConfigure =
                DefaultDataSourceConfigureLocator.getInstance().getDataSourceConfigure(name1);
        final SingleDataSource dataSource = new SingleDataSource(name1.toLowerCase(), dataSourceConfigure);

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
    }

    private void _testBusyConnection() throws Exception {
        DataSourceConfigure dataSourceConfigure =
                DefaultDataSourceConfigureLocator.getInstance().getDataSourceConfigure(name2);
        final SingleDataSource dataSource = new SingleDataSource(name2.toLowerCase(), dataSourceConfigure);

        // busy connection
        executorService1.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connection = dataSource.getDataSource().getConnection();
                    connection.createStatement().execute("select sleep(10)");
                    connection.close();
                } catch (Throwable e) {
                }
            }
        });

        System.out.println("Sleep for 2 seconds...");
        Thread.sleep(2 * 1000);
        DataSourceTerminator.getInstance().close(dataSource);
    }

    private void _testHangConnection() throws Exception {
        DataSourceConfigure dataSourceConfigure =
                DefaultDataSourceConfigureLocator.getInstance().getDataSourceConfigure(name1);

        for (int i = 0; i < 5; i++) {
            final SingleDataSource dataSource = new SingleDataSource(name1.toLowerCase(), dataSourceConfigure);
            executorService3.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Connection connection = dataSource.getDataSource().getConnection();
                        connection.createStatement().execute("select sleep(15)");
                        connection.close();
                    } catch (Throwable e) {
                    }
                }
            });

            Thread.sleep(5 * 100);
            DataSourceTerminator.getInstance().close(dataSource);
        }
    }

    private void _testMultipleConnections() throws Exception {
        DataSourceConfigure dataSourceConfigure =
                DefaultDataSourceConfigureLocator.getInstance().getDataSourceConfigure(name1);

        for (int i = 0; i < 100; i++) {
            final SingleDataSource dataSource = new SingleDataSource(name1.toLowerCase(), dataSourceConfigure);
            executorService3.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Connection connection = dataSource.getDataSource().getConnection();
                        connection.createStatement().execute("select sleep(1)");
                        connection.close();
                    } catch (Throwable e) {
                    }
                }
            });

            Thread.sleep(5 * 100);
            DataSourceTerminator.getInstance().close(dataSource);
        }
    }

}
