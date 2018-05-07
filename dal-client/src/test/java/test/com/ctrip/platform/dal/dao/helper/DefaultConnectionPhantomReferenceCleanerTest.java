package test.com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import org.junit.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by lilj on 2018/4/19.
 */
public class DefaultConnectionPhantomReferenceCleanerTest {
    private static final String cleanerThreadName = "DAL-DefaultConnectionPhantomReferenceCleaner";
    private static Class<?> defaultConnectionPhantomReferenceCleaner;
    private static Class<?> nonRegisteringDriver;
    private static Class<?> singleDatasource;
    private static Field driverClassName;
    private static Field containsMySQL;
    private static Field connectionPhantomReference;
    private static Field DEFAULT_INTERVAL;
    private static Method shutdownMethod;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
//        用于修改driverClassName,模拟driver不存在的情况
        defaultConnectionPhantomReferenceCleaner = Class.forName("com.ctrip.platform.dal.dao.helper.DefaultConnectionPhantomReferenceCleaner");
        driverClassName = defaultConnectionPhantomReferenceCleaner.getDeclaredField("driverClassName");
        driverClassName.setAccessible(true);

//        获取containsMySQL，用于每个case之前的复原
        singleDatasource = Class.forName("com.ctrip.platform.dal.dao.datasource.SingleDataSource");
        containsMySQL = singleDatasource.getDeclaredField("containsMySQL");
        containsMySQL.setAccessible(true);

//        获取connectionPhantomReference，用于检测cleaner功能是否生效
        nonRegisteringDriver = Class.forName(driverClassName.get(defaultConnectionPhantomReferenceCleaner).toString());
        connectionPhantomReference = nonRegisteringDriver.getDeclaredField("connectionPhantomRefs");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(connectionPhantomReference, connectionPhantomReference.getModifiers() & ~Modifier.FINAL);
        connectionPhantomReference.setAccessible(true);

//        获取cleaner运行间隔时间为10秒，修改这个时间便于测试cleaner功能是否生效
        DEFAULT_INTERVAL = defaultConnectionPhantomReferenceCleaner.getDeclaredField("DEFAULT_INTERVAL");
        modifiersField.setInt(DEFAULT_INTERVAL, DEFAULT_INTERVAL.getModifiers() & ~Modifier.FINAL);
        DEFAULT_INTERVAL.setAccessible(true);
        DEFAULT_INTERVAL.set(defaultConnectionPhantomReferenceCleaner, 10);

//        获取cleaner类的shutdown方法
        shutdownMethod = defaultConnectionPhantomReferenceCleaner.getDeclaredMethod("shutdown");
        shutdownMethod.setAccessible(true);
    }

    @Before
    public void setUp() throws Exception {
//        将singleDataSource中的containsMySQL还原为false
        ((AtomicBoolean) containsMySQL.get(singleDatasource)).getAndSet(false);

//        将cleaner中的driverClassName还原
        modifyDriverName('r');
    }

    @After
    public void tearDown() throws Exception {
        shutdownMethod.invoke(defaultConnectionPhantomReferenceCleaner);
        Thread.sleep(1000);
    }

    public int getConnectionPhantomReferenceSize() throws Exception {
        return ((ConcurrentHashMap) connectionPhantomReference.get(nonRegisteringDriver)).size();
    }


    public void modifyDriverName(char theLastLetter) throws Exception {
//      修改驱动类名
        String s = driverClassName.get(defaultConnectionPhantomReferenceCleaner).toString();
        Field f = String.class.getDeclaredField("value");
        f.setAccessible(true);
        char[] v = (char[]) f.get(s);
        v[v.length - 1] = theLastLetter;
    }

    private void verifyCleanerThreadCount(int count) {
        //        获取当前所有线程，验证当前phantomConnectionReferenceCleaner只有一个
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);
        int cleanerThreadCount = 0;
        for (int i = 0; i < noThreads; i++) {
//            System.out.println("线程号：" + i + " = " + lstThreads[i].getName());
            if (lstThreads[i].getName().contains(cleanerThreadName))
                cleanerThreadCount++;
        }
        Assert.assertEquals(String.format("cleanerThreadCount is not %d", count), count, cleanerThreadCount);
    }

    @Test
    public void testMultipleDataSources() throws Exception {
//       先后创建两个mysql datasource,一个sqlserver datasource
        DataSourceConfigure dataSourceConfigure1 = new DataSourceConfigure();
        dataSourceConfigure1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        dataSourceConfigure1.setProperty("connectionUrl", "jdbc:mysql://10.2.74.111:55111/mysqldaltest01db?useUnicode=true&characterEncoding=UTF-8");
        dataSourceConfigure1.setProperty("password", "k4AvZUIdDAcbyUvLirWG");
        dataSourceConfigure1.setProperty("userName", "tt_daltest1_2");
        SingleDataSource singleDataSource1 = new SingleDataSource("mysqldaltest01db_w", dataSourceConfigure1);
        Assert.assertNotNull(singleDataSource1);

        DataSourceConfigure dataSourceConfigure2 = new DataSourceConfigure();
        dataSourceConfigure2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        dataSourceConfigure2.setProperty("connectionUrl", "jdbc:mysql://10.32.21.149:3306/llj_test?useUnicode=true&characterEncoding=UTF-8");
        dataSourceConfigure2.setProperty("password", "!QAZ@WSX1qaz2wsx");
        dataSourceConfigure2.setProperty("userName", "root");
        SingleDataSource singleDataSource2 = new SingleDataSource("llj_test", dataSourceConfigure2);
        Assert.assertNotNull(singleDataSource2);

        DataSourceConfigure dataSourceConfigure3 = new DataSourceConfigure();
        dataSourceConfigure3.setProperty("driverClassName", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSourceConfigure3.setProperty("connectionUrl", "jdbc:sqlserver://10.32.21.149:1433;DatabaseName=llj");
        dataSourceConfigure3.setProperty("password", "!QAZ@WSX1qaz2wsx");
        dataSourceConfigure3.setProperty("userName", "sa");
        SingleDataSource singleDataSource3 = new SingleDataSource("llj", dataSourceConfigure3);
        Assert.assertNotNull(singleDataSource3);

        Thread.sleep(5000);

        verifyCleanerThreadCount(1);
    }


    @Test
    public void testConcurrent() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(String.format("%s start", Thread.currentThread().getName()));
                    DataSourceConfigure dataSourceConfigure1 = new DataSourceConfigure();
                    dataSourceConfigure1.setProperty("driverClassName", "com.mysql.jdbc.Driver");
                    dataSourceConfigure1.setProperty("connectionUrl", "jdbc:mysql://10.2.74.111:55111/mysqldaltest01db?useUnicode=true&characterEncoding=UTF-8");
                    dataSourceConfigure1.setProperty("password", "k4AvZUIdDAcbyUvLirWG");
                    dataSourceConfigure1.setProperty("userName", "tt_daltest1_2");
                    SingleDataSource singleDataSource1 = new SingleDataSource("mysqldaltest01db_w", dataSourceConfigure1);
                    Assert.assertNotNull(singleDataSource1);
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(String.format("%s start", Thread.currentThread().getName()));
                    DataSourceConfigure dataSourceConfigure2 = new DataSourceConfigure();
                    dataSourceConfigure2.setProperty("driverClassName", "com.mysql.jdbc.Driver");
                    dataSourceConfigure2.setProperty("connectionUrl", "jdbc:mysql://10.32.21.149:3306/llj_test?useUnicode=true&characterEncoding=UTF-8");
                    dataSourceConfigure2.setProperty("password", "!QAZ@WSX1qaz2wsx");
                    dataSourceConfigure2.setProperty("userName", "root");
                    SingleDataSource singleDataSource2 = new SingleDataSource("llj_test", dataSourceConfigure2);
                    Assert.assertNotNull(singleDataSource2);
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        }).start();
        latch.await();
        verifyCleanerThreadCount(1);
    }


    @Test
    public void testDriverClassNotFound() throws Exception {
        modifyDriverName('l');

//      创建数据源启动清理线程
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        dataSourceConfigure.setProperty("connectionUrl", "jdbc:mysql://10.32.21.149:3306/llj_test?useUnicode=true&characterEncoding=UTF-8");
        dataSourceConfigure.setProperty("password", "!QAZ@WSX1qaz2wsx");
        dataSourceConfigure.setProperty("userName", "root");
        SingleDataSource singleDataSource = new SingleDataSource("llj_test", dataSourceConfigure);
        Assert.assertNotNull(singleDataSource);

//      验证驱动类不存在时，线程不会启动
        Thread.sleep(5000);
        verifyCleanerThreadCount(0);
    }


    @Test
    public void testNoMySQL() throws Exception {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setProperty("driverClassName", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSourceConfigure.setProperty("connectionUrl", "jdbc:sqlserver://10.32.21.149:1433;DatabaseName=llj");
        dataSourceConfigure.setProperty("password", "!QAZ@WSX1qaz2wsx");
        dataSourceConfigure.setProperty("userName", "sa");
        SingleDataSource singleDataSource = new SingleDataSource("llj", dataSourceConfigure);
        Assert.assertNotNull(singleDataSource);

        Thread.sleep(5000);

        verifyCleanerThreadCount(0);
    }

    @Test
    public void testCleanResult() throws Exception {
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSourceConfigure.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        dataSourceConfigure.setProperty("connectionUrl", "jdbc:mysql://10.32.21.149:3306/llj_test?useUnicode=true&characterEncoding=UTF-8");
        dataSourceConfigure.setProperty("password", "!QAZ@WSX1qaz2wsx");
        dataSourceConfigure.setProperty("userName", "root");
        SingleDataSource singleDataSource = new SingleDataSource("llj_test", dataSourceConfigure);
        Assert.assertNotNull(singleDataSource);

        List<Connection> connectionsList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            connectionsList.add(singleDataSource.getDataSource().getConnection());
        }

        for (Connection connection : connectionsList) {
            connection.close();
        }

        Thread.sleep(1000);
        System.out.println("before clean: " + getConnectionPhantomReferenceSize());
        Assert.assertTrue(getConnectionPhantomReferenceSize() >= 100);

        Thread.sleep(15000);
        System.out.println("after clean: " + getConnectionPhantomReferenceSize());
        Assert.assertEquals(0, getConnectionPhantomReferenceSize());
    }
}
