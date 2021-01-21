package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeEvent;
import com.ctrip.platform.dal.dao.helper.ConnectionUtils;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RefreshableDataSourceTest {
    private ExecutorService executor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("RefreshableDataSourceTest"));

    private ExecutorService executorOne = new ThreadPoolExecutor(100, 100, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), new CustomThreadFactory("RefreshableDataSourceTest1"));

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
        p3.setProperty("connectionUrl", "jdbc:mysql://10.32.20.125:3306/test");
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
        Assert.assertEquals("jdbc:mysql://10.32.20.125:3306/test", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getProperty("connectionUrl"));
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

    // TODO: refactor
//    @Test
    public void testDataSourceSwitchNotify() throws Exception {
        Properties p1 = new Properties();
        p1.setProperty("userName", "root");
        p1.setProperty("password", "123456");
        p1.setProperty("connectionUrl", "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8;");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        final DataSourceConfigure configure1 = new DataSourceConfigure("test1", p1);

        Properties p2 = new Properties();
        p2.setProperty("userName", "root");
        p2.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p2.setProperty("connectionUrl", "jdbc:mysql://10.32.20.125:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        DataSourceConfigure configure2 = new DataSourceConfigure("test2", p2);

        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test1", configure1);
        final DataSourceConfigureChangeEvent dataSourceConfigureChangeEvent = new DataSourceConfigureChangeEvent("test2", configure2, configure1);
        final MockDataSourceSwitchListenerOne listenerOne = new MockDataSourceSwitchListenerOne();
        final MockDataSourceSwitchListenerTwo listenerTwo = new MockDataSourceSwitchListenerTwo();
        refreshableDataSource.addDataSourceSwitchListener(listenerOne);
        refreshableDataSource.addDataSourceSwitchListener(listenerTwo);
        final AtomicBoolean switched = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(150);
        for (int i = 0; i < 150; ++i) {
            final int sleep = i;
            executorOne.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(sleep + 1);
                    } catch (InterruptedException e) {
                        //ignore
                    }
                    //System.out.println(listenerOne.getStep() + ", " + listenerTwo.getStep());
                    if (listenerOne.getStep() == 10 && listenerTwo.getStep() == 20) {
                        switched.set(true);
                        Assert.assertEquals("jdbc:mysql://10.32.20.125:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;", refreshableDataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl());
                    }
                    latch.countDown();
                }
            });
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                    refreshableDataSource.configChanged(dataSourceConfigureChangeEvent);
                } catch (Exception e) {
                    //ignore
                }
            }
        }).start();


        while (true) {
            try {
                Connection connection = refreshableDataSource.getConnection();
                String currentServer = DataSourceSwitchChecker.getDBServerName(connection, refreshableDataSource.getSingleDataSource().getDataSourceConfigure());
                System.out.println(currentServer);
                if ("DST56614".equalsIgnoreCase(currentServer)) {
                    break;
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Thread.sleep(10);
            }
        }
        latch.await();
        Assert.assertTrue(switched.get());
    }

    @Test
    public void testExecuteListenerTimeOut() throws Exception {
        Properties p1 = new Properties();
        p1.setProperty("userName", "root");
        p1.setProperty("password", "123456");
        p1.setProperty("connectionUrl", "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8;");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        final DataSourceConfigure configure1 = new DataSourceConfigure("test1", p1);

        Properties p2 = new Properties();
        p2.setProperty("userName", "root");
        p2.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p2.setProperty("connectionUrl", "jdbc:mysql://10.32.20.125:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        DataSourceConfigure configure2 = new DataSourceConfigure("test2", p2);

        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test", configure1);
        final DataSourceConfigureChangeEvent dataSourceConfigureChangeEvent = new DataSourceConfigureChangeEvent("test", configure2, configure1);
        final MockDataSourceSwitchListenerOne listenerOne = new MockDataSourceSwitchListenerOne();
        final MockDataSourceSwitchListenerTwo listenerTwo = new MockDataSourceSwitchListenerTwo();
        listenerOne.setSleep(1500);
        //listenerTwo.setSleep(1500);
        refreshableDataSource.addDataSourceSwitchListener(listenerOne);
        refreshableDataSource.addDataSourceSwitchListener(listenerTwo);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                    refreshableDataSource.configChanged(dataSourceConfigureChangeEvent);
                } catch (Exception e) {
                    //ignore
                }
            }
        }).start();
        final CountDownLatch latch = new CountDownLatch(20);
        for (int i = 0; i < 20; ++i) {
            executorOne.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(20);
                        long startTime = System.currentTimeMillis();
                        refreshableDataSource.getConnection();
                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime - startTime);
                    } catch (Exception e) {

                    }
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    @Test
    public void testSetExecuteSwitchListenerTimeout() throws Exception {
//        Properties newProperties = new Properties();
//        newProperties.setProperty(USER_NAME, "root");
//        newProperties.setProperty(PASSWORD, "123456");
//        newProperties.setProperty(CONNECTION_URL, "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8;");
//        newProperties.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");
//        DataSourceConfigure dataSourceConfigure2 = new DataSourceConfigure("normal", newProperties);

        Properties p2 = new Properties();
        p2.setProperty("userName", "root");
        p2.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p2.setProperty("connectionUrl", "jdbc:mysql://10.32.20.125:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        DataSourceConfigure dataSourceConfigure1 = new DataSourceConfigure("test2", p2);

        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test", dataSourceConfigure1);
//        final DataSourceConfigureChangeEvent dataSourceConfigureChangeEvent = new DataSourceConfigureChangeEvent("test", dataSourceConfigure2, dataSourceConfigure1);
//        final MockDataSourceSwitchListenerOne listenerOne = new MockDataSourceSwitchListenerOne();
//        final MockDataSourceSwitchListenerTwo listenerTwo = new MockDataSourceSwitchListenerTwo();
//        refreshableDataSource.addDataSourceSwitchListener(listenerOne);
//        refreshableDataSource.addDataSourceSwitchListener(listenerTwo);
//
//        listenerOne.setSleep(4);
//        listenerTwo.setSleep(5);

//        refreshableDataSource.getConnection();
//        refreshableDataSource.configChanged(dataSourceConfigureChangeEvent);
//        Thread.sleep(3000);

//        refreshableDataSource.getConnection();
//        Assert.assertTrue(listenerOne.getEnd());
//        Assert.assertTrue(listenerTwo.getEnd());


//        final DataSourceConfigureChangeEvent dataSourceConfigureChangeEvent1 = new DataSourceConfigureChangeEvent("test", dataSourceConfigure1, dataSourceConfigure2);
//
//        listenerOne.setSleep(20);
//        listenerTwo.setSleep(20);
//        listenerOne.resetEnd();
//        listenerTwo.resetEnd();
//        refreshableDataSource.configChanged(dataSourceConfigureChangeEvent1);
//        Thread.sleep(3000);
//        refreshableDataSource.getConnection();
//        Assert.assertFalse(listenerOne.getEnd());
//        Assert.assertFalse(listenerTwo.getEnd());

//        final DataSourceConfigureChangeEvent dataSourceConfigureChangeEvent2 = new DataSourceConfigureChangeEvent("test", dataSourceConfigure2, dataSourceConfigure1);

//        Thread.sleep(5);
//        listenerOne.setSleep(20);
//        listenerTwo.setSleep(150);
//        listenerOne.resetEnd();
//        listenerTwo.resetEnd();
        Assert.assertEquals(10, refreshableDataSource.getSwitchListenerTimeout());
        refreshableDataSource.setDataSourceSwitchListenerTimeout(100);
        Assert.assertEquals(100, refreshableDataSource.getSwitchListenerTimeout());
        refreshableDataSource.setDataSourceSwitchListenerTimeout(600);
        Assert.assertEquals(500, refreshableDataSource.getSwitchListenerTimeout());
    }

    @Test
    public void testGetConnectionPerformance() throws Exception {
        Properties p2 = new Properties();
        p2.setProperty("userName", "root");
        p2.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p2.setProperty("connectionUrl", "jdbc:mysql://10.32.20.125:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        DataSourceConfigure configure2 = new DataSourceConfigure("test2", p2);

        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test", configure2);
        MockDataSourceSwitchListenerOne listenerOne = new MockDataSourceSwitchListenerOne();
        refreshableDataSource.addDataSourceSwitchListener(listenerOne);
        for (int i = 0; i < 100; ++i) {
            long startTime = System.currentTimeMillis();
            Connection connection = refreshableDataSource.getConnection();
            long endTime = System.currentTimeMillis();
            System.out.println(endTime - startTime);
            connection.close();
        }
    }

//    @Test
    public void testGetConnection() throws Exception {
        Properties p1 = new Properties();
        p1.setProperty("userName", "root");
        p1.setProperty("password", "123456");
        p1.setProperty("connectionUrl", "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8;");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        final DataSourceConfigure configure1 = new DataSourceConfigure("test1", p1);

        Properties p2 = new Properties();
        p2.setProperty("userName", "root");
        p2.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p2.setProperty("connectionUrl", "jdbc:mysql://10.32.20.125:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        DataSourceConfigure configure2 = new DataSourceConfigure("test2", p2);

        final RefreshableDataSource refreshableDataSource = new RefreshableDataSource("test", configure1);
        DataSourceConfigureChangeEvent dataSourceConfigureChangeEvent1 = new DataSourceConfigureChangeEvent("test", configure2, configure1);
        DataSourceConfigureChangeEvent dataSourceConfigureChangeEvent2 = new DataSourceConfigureChangeEvent("test", configure1, configure2);
        final MockDataSourceSwitchListenerOne listenerOne = new MockDataSourceSwitchListenerOne();
        final MockDataSourceSwitchListenerTwo listenerTwo = new MockDataSourceSwitchListenerTwo();
        refreshableDataSource.addDataSourceSwitchListener(listenerOne);
        refreshableDataSource.addDataSourceSwitchListener(listenerTwo);

        final CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; ++i) {
            final int time = i;
            executorOne.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                        long startTime = System.currentTimeMillis();
                        Connection connection = refreshableDataSource.getConnection();
                        long endTime = System.currentTimeMillis();
                        System.out.println(startTime + ":" + (endTime - startTime));
                        if ("jdbc:mysql://10.32.20.125:3306/llj_test".equalsIgnoreCase(ConnectionUtils.getConnectionUrl(connection))) {
                            Assert.assertEquals(listenerOne.getStep(), 10);
                            Assert.assertEquals(listenerTwo.getStep(), 20);
                        }
                    } catch (Exception e) {

                    }
                    latch.countDown();
                }
            });
        }
        refreshableDataSource.configChanged(dataSourceConfigureChangeEvent1);
//        Thread.sleep(10);
//        refreshableDataSource.configChanged(dataSourceConfigureChangeEvent2);
        latch.await();
    }


    /* ------- dal cluster cases ------- */

    @Test
    public void testReusedSingleDataSource() {
        Properties p1 = new Properties();
        p1.setProperty("userName", "root");
        p1.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p1.setProperty("connectionUrl", "jdbc:mysql://10.32.20.125:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        DataSourceConfigure config1 = new DataSourceConfigure("config1", p1);
        DataSourceConfigure config2 = new DataSourceConfigure("config2", p1);

        Properties p2 = new Properties();
        p2.setProperty("userName", "root");
        p2.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p2.setProperty("connectionUrl", "jdbc:mysql://dst56614:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        DataSourceConfigure config3 = new DataSourceConfigure("config3", p2);

        RefreshableDataSource ds1 = new RefreshableDataSource("ds1", config1);
        RefreshableDataSource ds2 = new RefreshableDataSource("ds2", config2);
        RefreshableDataSource ds3 = new RefreshableDataSource("ds3", config3);

        Assert.assertSame(ds1.getSingleDataSource(), ds2.getSingleDataSource());
        Assert.assertNotSame(ds1.getSingleDataSource(), ds3.getSingleDataSource());
    }

    @Test
    public void testDataSourceSwitch() throws Exception {
        DataSourceCreator.getInstance().closeAllDataSources();

        Properties p1 = new Properties();
        p1.setProperty("userName", "root");
        p1.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p1.setProperty("connectionUrl", "jdbc:mysql://10.32.20.125:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        p1.setProperty("driverClassName", "com.mysql.jdbc.Driver");

        Properties p2 = new Properties();
        p2.setProperty("userName", "root");
        p2.setProperty("password", "!QAZ@WSX1qaz2wsx");
        p2.setProperty("connectionUrl", "jdbc:mysql://dst56614:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        p2.setProperty("driverClassName", "com.mysql.jdbc.Driver");

        DataSourceConfigure config1 = new DataSourceConfigure("config1", p1);
        RefreshableDataSource ds1 = new RefreshableDataSource("ds1", config1);

        DataSourceConfigure config2 = new DataSourceConfigure("config2", p2);
        RefreshableDataSource ds2 = new RefreshableDataSource("ds2", config2);

        SingleDataSource sds1 = ds1.getSingleDataSource();
        SingleDataSource sds2 = ds2.getSingleDataSource();
        Assert.assertNotSame(sds1, sds2);
        Assert.assertEquals(1, sds1.getReferenceCount());
        Assert.assertEquals(1, sds2.getReferenceCount());

        ds1.refreshDataSource(config2.getName(), config2);
        Assert.assertSame(ds1.getSingleDataSource(), sds2);
        Assert.assertEquals(0, sds1.getReferenceCount());
        Assert.assertEquals(2, sds2.getReferenceCount());

        RefreshableDataSource ds3 = new RefreshableDataSource("ds3", config1);
        RefreshableDataSource ds4 = new RefreshableDataSource("ds4", config2);
        SingleDataSource sds3 = ds3.getSingleDataSource();
        SingleDataSource sds4 = ds4.getSingleDataSource();
        Assert.assertNotSame(sds1, sds3);
        Assert.assertSame(sds2, sds4);
        Assert.assertEquals(0, sds1.getReferenceCount());
        Assert.assertEquals(3, sds2.getReferenceCount());
        Assert.assertEquals(1, sds3.getReferenceCount());

        RefreshableDataSource ds5 = new RefreshableDataSource("ds5", config2);
        SingleDataSource sds5 = ds5.getSingleDataSource();
        ds3.close();
        Assert.assertSame(sds2, sds5);
        Assert.assertEquals(0, sds3.getReferenceCount());
        Assert.assertEquals(4, sds2.getReferenceCount());

        RefreshableDataSource ds6 = new RefreshableDataSource("ds6", config1);
        SingleDataSource sds6 = ds6.getSingleDataSource();
        Assert.assertNotSame(sds3, sds6);
        Assert.assertEquals(0, sds3.getReferenceCount());
        Assert.assertEquals(1, sds6.getReferenceCount());
    }
}
