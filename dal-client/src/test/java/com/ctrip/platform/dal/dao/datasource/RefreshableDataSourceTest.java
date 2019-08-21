package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class RefreshableDataSourceTest {
    private ExecutorService executor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("RefreshableDataSourceTest"));

    private ExecutorService executorOne = new ThreadPoolExecutor(10, 120, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("RefreshableDataSourceTest"));

    @Test
    public void testSingleTimeoutDataSourceSwitchOnce() throws Exception {
        Properties p1 = new Properties();
        p1.setProperty("userName", "root");
        p1.setProperty("password", "111111");
        p1.setProperty("connectionUrl", "jdbc:mysql://localhost:3306/test");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p1.setProperty("connectionProperties", "connectTimeout=5000");
        DataSourceConfigure configure1 = new DataSourceConfigure("test", p1);

        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test", configure1);

        Properties p2 = new Properties();
        p2.setProperty("userName", "refresh");
        p2.setProperty("password", "111111");
        p2.setProperty("connectionUrl", "jdbc:mysql://1.1.1.1:3306/test");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p2.setProperty("connectionProperties", "connectTimeout=3000");
        final DataSourceConfigure configure2 = new DataSourceConfigure("test", p2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    refreshableDataSource.refreshDataSource("test", configure2);
                } catch (SQLException e) {
//                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(500);
        //切换过程中的请求，如果获取datasource不为空，不报错，且配置为新
        Assert.assertEquals("jdbc:mysql://1.1.1.1:3306/test", ((org.apache.tomcat.jdbc.pool.DataSource) refreshableDataSource.getSingleDataSource().getDataSource()).getUrl());
        Assert.assertEquals("refresh", ((org.apache.tomcat.jdbc.pool.DataSource) refreshableDataSource.getSingleDataSource().getDataSource()).getUsername());

        Assert.assertNotNull(refreshableDataSource.getSingleDataSource());
        Assert.assertNotNull(refreshableDataSource.getSingleDataSource().getDataSource());
        Assert.assertEquals("jdbc:mysql://1.1.1.1:3306/test", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getProperty("connectionUrl"));

        Thread.sleep(3000);
        Assert.assertEquals("jdbc:mysql://1.1.1.1:3306/test", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getProperty("connectionUrl"));
    }


    //       sw1     sw2    sw3
    //                                 done1       done2     done3
    //        0      100    200        3000        6000      9000
    //                                             done3
    //                                             6000
    @Test
    public void testSingleTimeoutDataSourceSwitchMultipleTimes() throws Exception {
        Properties p1 = new Properties();
        p1.setProperty("userName", "Original");
        p1.setProperty("password", "111111");
        p1.setProperty("connectionUrl", "jdbc:mysql://localhost:3306/test");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p1.setProperty("connectionProperties", "connectTimeout=5000");
        DataSourceConfigure configure1 = new DataSourceConfigure("test", p1);
        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test", configure1);

        Properties p2 = new Properties();
        p2.setProperty("userName", "FirstRefresh");
        p2.setProperty("password", "111111");
        p2.setProperty("connectionUrl", "jdbc:mysql://1.1.1.1:3306/test");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p2.setProperty("connectionProperties", "connectTimeout=3000");
        final DataSourceConfigure configure2 = new DataSourceConfigure("test", p2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    refreshableDataSource.refreshDataSource("test", configure2);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(200);
        Assert.assertEquals("jdbc:mysql://1.1.1.1:3306/test", ((org.apache.tomcat.jdbc.pool.DataSource)refreshableDataSource.getSingleDataSource().getDataSource()).getUrl());
        Assert.assertEquals("FirstRefresh", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getUserName());

        Properties p3 = new Properties();
        p3.setProperty("userName", "SecondRefresh");
        p3.setProperty("password", "111111");
        p3.setProperty("connectionUrl", "jdbc:mysql://1.1.1.1:3306/test");
        p3.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p3.setProperty("connectionProperties", "connectTimeout=3000");
        final DataSourceConfigure configure3 = new DataSourceConfigure("test", p3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    refreshableDataSource.refreshDataSource("test", configure3);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(200);
        Assert.assertEquals("jdbc:mysql://1.1.1.1:3306/test", ((org.apache.tomcat.jdbc.pool.DataSource)refreshableDataSource.getSingleDataSource().getDataSource()).getUrl());
        Assert.assertEquals("SecondRefresh", ((org.apache.tomcat.jdbc.pool.DataSource)refreshableDataSource.getSingleDataSource().getDataSource()).getUsername());
        Assert.assertEquals("SecondRefresh", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getUserName());
        SingleDataSource secondDataSource = refreshableDataSource.getSingleDataSource();

        Properties p4 = new Properties();
        p4.setProperty("userName", "ThirdRefresh");
        p4.setProperty("password", "111111");
        p4.setProperty("connectionUrl", "jdbc:mysql://1.1.1.1:3306/test");
        p4.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p4.setProperty("connectionProperties", "connectTimeout=3000");
        final DataSourceConfigure configure4 = new DataSourceConfigure("test", p4);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    refreshableDataSource.refreshDataSource("test", configure4);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(200);
        Assert.assertEquals("jdbc:mysql://1.1.1.1:3306/test", ((org.apache.tomcat.jdbc.pool.DataSource)refreshableDataSource.getSingleDataSource().getDataSource()).getUrl());
        Assert.assertEquals("ThirdRefresh", ((org.apache.tomcat.jdbc.pool.DataSource)refreshableDataSource.getSingleDataSource().getDataSource()).getUsername());
        Assert.assertEquals("ThirdRefresh", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getUserName());
        SingleDataSource thirdDataSource = refreshableDataSource.getSingleDataSource();

//        第一次timeout异常时间到，第二次切换被cancel，第三次切换生效
        Thread.sleep(3000);
        Assert.assertEquals("ThirdRefresh", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getUserName());
        Assert.assertNotNull(thirdDataSource.getDataSource());

        Thread.sleep(3000);
        Assert.assertEquals("ThirdRefresh", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getUserName());

        Thread.sleep(3000);
        Assert.assertEquals("ThirdRefresh", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getUserName());
        Assert.assertNotNull(thirdDataSource.getDataSource());
    }

    @Test
    public void testOneSuccessDataSourceAfterOneConnectTimeoutDataSource() throws Exception {
        Properties p1 = new Properties();
        p1.setProperty("userName", "root");
        p1.setProperty("password", "111111");
        p1.setProperty("connectionUrl", "jdbc:mysql://localhost:3306/test");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p1.setProperty("connectionProperties", "connectTimeout=10000");
        DataSourceConfigure configure1 = new DataSourceConfigure("test", p1);

        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test", configure1);

        Properties p2 = new Properties();
        p2.setProperty("userName", "root");
        p2.setProperty("password", "111111");
        p2.setProperty("connectionUrl", "jdbc:mysql://1.1.1.1:3306/test");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p2.setProperty("connectionProperties", "connectTimeout=3000");
        final DataSourceConfigure configure2 = new DataSourceConfigure("test", p2);

        Properties p3 = new Properties();
        p3.setProperty("userName", "root");
        p3.setProperty("password", "111111");
        p3.setProperty("connectionUrl", "jdbc:mysql://10.32.20.139:3306/test");
        p3.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p3.setProperty("connectionProperties", "connectTimeout=3000");
        final DataSourceConfigure configure3 = new DataSourceConfigure("test", p3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    refreshableDataSource.refreshDataSource("test", configure2);
                    refreshableDataSource.refreshDataSource("test", configure3);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(4000);
        Assert.assertNotNull(refreshableDataSource.getSingleDataSource());
        Assert.assertNotNull(refreshableDataSource.getSingleDataSource().getDataSource());
        Assert.assertEquals("jdbc:mysql://10.32.20.139:3306/test", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getProperty("connectionUrl"));
    }

    @Test
    public void testMultipleTimeoutDataSourcesRefresh() throws Exception {
        List<RefreshableDataSource> list = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            DataSourceConfigure configure = new DataSourceConfigure("test" + i, (Properties) createOriginalProperties().clone());
            RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test" + i, configure);
            list.add(refreshableDataSource);
        }

        for (final RefreshableDataSource refreshableDataSource : list)
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        String name = refreshableDataSource.getSingleDataSource().getName();
                        DataSourceConfigure configure = new DataSourceConfigure(name, (Properties) createTimeoutProperties().clone());
                        refreshableDataSource.refreshDataSource(name, configure);
                    } catch (SQLException e) {
//                        e.printStackTrace();
                    }
                }
            });


        Thread.sleep(500);
        for (RefreshableDataSource refreshableDataSource : list)
               Assert.assertEquals("new",refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getUserName());

        for (final RefreshableDataSource refreshableDataSource : list)
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        String name = refreshableDataSource.getSingleDataSource().getName();
                        DataSourceConfigure configure = new DataSourceConfigure(name, (Properties) createOriginalProperties().clone());
                        refreshableDataSource.refreshDataSource(name, configure);
                    } catch (SQLException e) {
//                        e.printStackTrace();
                    }
                }
            });

        Thread.sleep(10000);
        for (RefreshableDataSource refreshableDataSource : list)
                Assert.assertEquals("original",refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getUserName());

    }

    private Properties createOriginalProperties() {
        Properties p1 = new Properties();
        p1.setProperty("userName", "original");
        p1.setProperty("password", "111111");
        p1.setProperty("connectionUrl", "jdbc:mysql://localhost:3306");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p1.setProperty("connectionProperties", "connectTimeout=1000");
        return p1;
    }

    private Properties createTimeoutProperties() {
        Properties p1 = new Properties();
        p1.setProperty("userName", "new");
        p1.setProperty("password", "111111");
        p1.setProperty("connectionUrl", "jdbc:mysql://1.1.1.1:3306/test");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        p1.setProperty("connectionProperties", "connectTimeout=1000");
        return p1;
    }

//    @Test
//    public void testDataSourceSwitchNotify() throws Exception {
//        Properties p1 = new Properties();
//        p1.setProperty("userName", "root");
//        p1.setProperty("password", "111111");
//        p1.setProperty("connectionUrl", "jdbc:mysql://localhost:3306/test");
//        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
//        DataSourceConfigure configure1 = new DataSourceConfigure("test", p1);
//
//        Properties p2 = new Properties();
//        p2.setProperty("userName", "root");
//        p2.setProperty("password", "111111");
//        p2.setProperty("connectionUrl", "jdbc:mysql://1.1.1.1:3306/test");
//        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
//        DataSourceConfigure configure2 = new DataSourceConfigure("test", p2);
//
//        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test", configure1);
//        final DataSourceConfigureChangeEvent dataSourceConfigureChangeEvent = new DataSourceConfigureChangeEvent("test", configure2, configure1);
//        final MockDataSourceSwitchListenerOne listenerOne = new MockDataSourceSwitchListenerOne();
//        final MockDataSourceSwitchListenerTwo listenerTwo = new MockDataSourceSwitchListenerTwo();
//        refreshableDataSource.addDataSourceSwitchListener(listenerOne);
//        refreshableDataSource.addDataSourceSwitchListener(listenerTwo);
//        for (int i = 0; i < 100; ++i) {
//            final int sleep = i;
//            executorOne.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(sleep + 1);
//                    } catch (InterruptedException e) {
//                        //ignore
//                    }
//                    if (listenerOne.getStep() == 10 && listenerTwo.getStep() == 20) {
//                        Assert.assertEquals("jdbc:mysql://1.1.1.1:3306/test", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl());
//                    }
//                }
//            });
//        }
//        new Thread( new Runnable(){
//
//            @Override
//            public void run() {
//                try {
//                    refreshableDataSource.configChanged(dataSourceConfigureChangeEvent);
//                } catch (Exception e) {
//                    //ignore
//                }
//            }
//        }).start();
//
//        List<Future> futureList = new ArrayList<>();
//        for (int i = 0; i < 20; ++i) {
//            final int sleep = i;
//            futureList.add(executorOne.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(sleep + 1);
//                    } catch (Exception e) {
//
//                    }
//                    refreshableDataSource.getDataSource();
//                }
//            }));
//        }
//        for (Future future : futureList) {
//            future.get();
//            Assert.assertEquals(10, listenerOne.getStep());
//            Assert.assertEquals(20, listenerTwo.getStep());
//        }
//    }
//
//    @Test
//    public void testExecuteListenerTimeOut() throws Exception {
//        Properties p1 = new Properties();
//        p1.setProperty("userName", "root");
//        p1.setProperty("password", "111111");
//        p1.setProperty("connectionUrl", "jdbc:mysql://localhost:3306/test");
//        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
//        DataSourceConfigure configure1 = new DataSourceConfigure("test", p1);
//
//        Properties p2 = new Properties();
//        p2.setProperty("userName", "root");
//        p2.setProperty("password", "111111");
//        p2.setProperty("connectionUrl", "jdbc:mysql://1.1.1.1:3306/test");
//        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
//        DataSourceConfigure configure2 = new DataSourceConfigure("test", p2);
//
//        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test", configure1);
//        DataSourceConfigureChangeEvent dataSourceConfigureChangeEvent = new DataSourceConfigureChangeEvent("test", configure2, configure1);
//        final MockDataSourceSwitchListenerOne listenerOne = new MockDataSourceSwitchListenerOne();
//        final MockDataSourceSwitchListenerTwo listenerTwo = new MockDataSourceSwitchListenerTwo();
//        listenerOne.setSleep(1500);
//        //listenerTwo.setSleep(1500);
//        refreshableDataSource.addDataSourceSwitchListener(listenerOne);
//        refreshableDataSource.addDataSourceSwitchListener(listenerTwo);
//        refreshableDataSource.configChanged(dataSourceConfigureChangeEvent);
//        Assert.assertEquals(1, listenerOne.getStep());
//        Assert.assertEquals(20, listenerTwo.getStep());
//
//    }
}
