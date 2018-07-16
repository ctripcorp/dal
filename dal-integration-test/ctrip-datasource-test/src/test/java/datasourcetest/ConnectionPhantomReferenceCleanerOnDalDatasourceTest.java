package datasourcetest;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import org.junit.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lilj on 2018/4/21.
 */
public class ConnectionPhantomReferenceCleanerOnDalDatasourceTest {
    private static final String cleanerThreadName = "DAL-DefaultConnectionPhantomReferenceCleaner";
    private static Class<?> defaultConnectionPhantomReferenceCleaner;
    private static Class<?> nonRegisteringDriver;
    private static Class<?> singleDatasource;
    private static Class<?> dataSourceLocator;
    private static Field driverClassName;
    private static Field containsMySQL;
    private static Field connectionPhantomReference;
    private static Field DEFAULT_INTERVAL;
    private static Field cache;
    private static Method shutdownMethod;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
//        用于修改driverClassName,模拟driver不存在的情况
        defaultConnectionPhantomReferenceCleaner = Class.forName("com.ctrip.platform.dal.dao.helper.DefaultConnectionPhantomReferenceCleaner");
        driverClassName = defaultConnectionPhantomReferenceCleaner.getDeclaredField("driverClassName");
        driverClassName.setAccessible(true);

//        获取connectionPhantomReference，用于检测cleaner功能是否生效
        nonRegisteringDriver = Class.forName(driverClassName.get(defaultConnectionPhantomReferenceCleaner).toString());
        connectionPhantomReference = nonRegisteringDriver.getDeclaredField("connectionPhantomRefs");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(connectionPhantomReference, connectionPhantomReference.getModifiers() & ~Modifier.FINAL);
        connectionPhantomReference.setAccessible(true);

//        获取cleaner运行间隔时间为10秒一次，修改这个时间便于测试cleaner功能是否生效
        DEFAULT_INTERVAL = defaultConnectionPhantomReferenceCleaner.getDeclaredField("DEFAULT_INTERVAL");
        modifiersField.setInt(DEFAULT_INTERVAL, DEFAULT_INTERVAL.getModifiers() & ~Modifier.FINAL);
        DEFAULT_INTERVAL.setAccessible(true);
        DEFAULT_INTERVAL.set(defaultConnectionPhantomReferenceCleaner, 10);

//        获取cleaner类的shutdown方法
        shutdownMethod = defaultConnectionPhantomReferenceCleaner.getDeclaredMethod("shutdown");
        shutdownMethod.setAccessible(true);

//        获取containsMySQL，用于每个case之前的复原
        singleDatasource = Class.forName("com.ctrip.platform.dal.dao.datasource.SingleDataSource");
        containsMySQL = singleDatasource.getDeclaredField("containsMySQL");
        containsMySQL.setAccessible(true);

//        获取DataSourceLocator中的数据源缓存，以便于每次case前清理
        dataSourceLocator = Class.forName("com.ctrip.platform.dal.dao.datasource.DataSourceLocator");
        cache = dataSourceLocator.getDeclaredField("cache");
        modifiersField.set(cache, cache.getModifiers() & ~Modifier.FINAL);
        cache.setAccessible(true);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
//        还原cleaner运行间隔时间
        DEFAULT_INTERVAL.set(defaultConnectionPhantomReferenceCleaner, 900);
    }

    @Before
    public void setUp() throws Exception {
//        将singleDataSource中的containsMySQL还原为false
        ((AtomicBoolean) containsMySQL.get(singleDatasource)).getAndSet(false);

//        清理数据源缓存
        ((ConcurrentHashMap) cache.get(dataSourceLocator)).clear();
    }

    @After
    public void tearDown() throws Exception {
        shutdownMethod.invoke(defaultConnectionPhantomReferenceCleaner);
    }

    public int getConnectionPhantomReferenceSize() throws Exception {
        return ((ConcurrentHashMap) connectionPhantomReference.get(nonRegisteringDriver)).size();
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
    public void testDalDataSource() throws Exception {
        new DalDataSourceFactory().createDataSource("DalServiceDB");
        new DalDataSourceFactory().createDataSource("DalService1DB");
        verifyCleanerThreadCount(0);
        System.out.println("size before clean: " + getConnectionPhantomReferenceSize());
        Assert.assertTrue(getConnectionPhantomReferenceSize() == 0);

        new DalDataSourceFactory().createDataSource("DalService2DB_W");
        new DalDataSourceFactory().createDataSource("DalService3DB_W");
        verifyCleanerThreadCount(1);
        System.out.println("size before clean: " + getConnectionPhantomReferenceSize());
        Assert.assertEquals(2, getConnectionPhantomReferenceSize());
        Thread.sleep(10000);
        System.out.println("size after clean: " + getConnectionPhantomReferenceSize());
        Assert.assertEquals(0, getConnectionPhantomReferenceSize());
    }

    @Test
    public void testConcurrentDatasource() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new DalDataSourceFactory().createDataSource("DalService2DB_W");
                    new DalDataSourceFactory().createDataSource("DalService3DB_W");
                } catch (Exception e) {
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
                    new DalDataSourceFactory().createDataSource("DalServiceDB");
                    new DalDataSourceFactory().createDataSource("DalService1DB");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        }).start();
        latch.await();
        verifyCleanerThreadCount(1);
        Assert.assertEquals(2, getConnectionPhantomReferenceSize());
        Thread.sleep(10000);
        Assert.assertEquals(0, getConnectionPhantomReferenceSize());
    }
}
