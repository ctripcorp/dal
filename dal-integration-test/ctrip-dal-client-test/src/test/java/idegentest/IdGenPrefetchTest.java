package idegentest;

import IDAutoGenerator.AutoGenBigintIDDao;
import IDAutoGenerator.AutoGenIDDao;
import IDAutoGenerator.TableWithBigintIdentity;
import IDAutoGenerator.TableWithIdentity;

import com.ctrip.framework.idgen.client.IdGeneratorFactory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.DalInvalidConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocatorManager;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;


public class IdGenPrefetchTest {
    private static Logger logger = LoggerFactory.getLogger(IdGenPrefetchTest.class);

//    @BeforeClass
//    public static void setUpBeforeClass() throws Exception {
//        DalClientFactory.shutdownFactory();
//        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "IdGen/Dal.config");
//
//    }
//
//
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DalClientFactory.shutdownFactory();
    }

    @Before
    public void setUp() throws Exception {
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "IdGen/Dal.config");
    }

    @After
    public void tearDown() throws Exception {
//        DalClientFactory.shutdownFactory();
    }


//    @Test
//    public void testMyIdGeneratorFactory() throws Exception {
//        AutoGenIDDao dao = new AutoGenIDDao("noShardTestOnMysql");
//        dao.test_def_update(new DalHints());
//        TableWithIdentity pojo = new TableWithIdentity();
//        pojo.setName("testMyIdGeneratorFactory");
//        dao.insert(new DalHints().setIdentityBack(), pojo);
//        assertEquals(50L, pojo.getID().longValue());
//    }

    @Test
    public void testSequenceDbNameInSingleDbSet() throws Exception {
//      配置sequenceDbName="existDb"且在qconfig注册，逻辑库名字"testExistSequenceDbName"并没有注册
        try {
            AutoGenIDDao dao = new AutoGenIDDao("testExistSequenceDbName");
        } catch (Exception e) {
            logger.error(e.getMessage());
            fail();
        }
//      配置sequenceDbName="nonExistDb"且没有在qconfig注册，逻辑库名字"testNonExistSequenceDbName"已注册
        try {
            AutoGenIDDao dao = new AutoGenIDDao("testNonExistSequenceDbName");
            fail();
        } catch (Exception e) {
            logger.error(e.getMessage());
            assertTrue(e.getMessage().contains("sequence") || e.getMessage().contains("Sequence"));
            assertTrue(e.getMessage().contains("invalid") || e.getMessage().contains("Invalid"));
        }
    }

    @Test
    public void testSequenceDbNameInDiffDbSet() throws Exception {
        final AutoGenIDDao dao1;
        final AutoGenIDDao dao2;

//      不同的逻辑库，配置相同的物理库，且配置sequenceDbName="existDb"
        dao1 = new AutoGenIDDao("testExistSequenceDbName");
        dao2 = new AutoGenIDDao("testExistSequenceDbName2");
        dao1.test_def_update(null);

        int requireSize = 1000;
        final CountDownLatch latch = new CountDownLatch(requireSize * 2);
        final AtomicBoolean result = new AtomicBoolean(true);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());


        for (int i = 0; i < requireSize; i++) {
            executor.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TableWithIdentity pojo1 = new TableWithIdentity();
                        pojo1.setName("ConcurrentCombinedInsert1");
                        TableWithIdentity pojo2 = new TableWithIdentity();
                        pojo2.setName("ConcurrentCombinedInsert2");
                        List<TableWithIdentity> list = new ArrayList<>();
                        list.add(pojo1);
                        list.add(pojo2);
                        dao1.combinedInsert(new DalHints().setIdentityBack(), list);
                        assertEquals(pojo1.getID().longValue(), dao1.queryBy(pojo1, null).get(0).getID().longValue());
                        assertEquals(pojo2.getID().longValue(), dao1.queryBy(pojo2, null).get(0).getID().longValue());
                        assertEquals("ConcurrentCombinedInsert1", dao1.queryBy(pojo1, null).get(0).getName());
                        assertEquals("ConcurrentCombinedInsert2", dao1.queryBy(pojo2, null).get(0).getName());
//                        idList.add(pojo.getID().longValue());
                    } catch (Exception e) {
                        logger.error(e.toString());
                        result.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            }));
        }
        for (int i = 0; i < requireSize; i++) {
            executor.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TableWithIdentity pojo1 = new TableWithIdentity();
                        pojo1.setName("ConcurrentCombinedInsert1");
                        TableWithIdentity pojo2 = new TableWithIdentity();
                        pojo2.setName("ConcurrentCombinedInsert2");
                        List<TableWithIdentity> list = new ArrayList<>();
                        list.add(pojo1);
                        list.add(pojo2);
                        dao2.combinedInsert(new DalHints().setIdentityBack(), list);
                        assertEquals(pojo1.getID().longValue(), dao2.queryBy(pojo1, null).get(0).getID().longValue());
                        assertEquals(pojo2.getID().longValue(), dao2.queryBy(pojo2, null).get(0).getID().longValue());
                        assertEquals("ConcurrentCombinedInsert1", dao2.queryBy(pojo1, null).get(0).getName());
                        assertEquals("ConcurrentCombinedInsert2", dao2.queryBy(pojo2, null).get(0).getName());
//                        idList.add(pojo.getID().longValue());
                    } catch (Exception e) {
                        logger.error(e.toString());
                        result.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            }));
        }
        latch.await();
        Assert.assertTrue(result.get());

        List<TableWithIdentity> ret = dao1.queryAll(null);
        int idSize = ret.size();
        Assert.assertEquals(requireSize * 4, idSize);

//            check duplicate
        Set<Long> idSet = new HashSet<>();
        for (TableWithIdentity pojo : ret)
            idSet.add(pojo.getID().longValue());
        Assert.assertEquals(idSize, idSet.size());
    }

    @Test
    public void testPrefetchSuccess() throws Exception {
        IdGeneratorFactory instance = IdGeneratorFactory.getInstance();
        Class<?> idGeneratorFactory = com.ctrip.framework.idgen.client.IdGeneratorFactory.class;
        Field idGeneratorCache = idGeneratorFactory.getDeclaredField("idGeneratorCache");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(idGeneratorCache, idGeneratorCache.getModifiers() & ~Modifier.FINAL);
        idGeneratorCache.setAccessible(true);

        ConcurrentMap<String, LongIdGenerator> cache = (ConcurrentMap<String, LongIdGenerator>) idGeneratorCache.get(instance);

        assertNull(cache.get("testPrefetchWithRegisteredDB.bigintidtable"));
        assertNull(cache.get("noshardtestonmysql_1.person"));
        assertNull(cache.get("noshardtestonmysql_2.testtable"));
        assertNull(cache.get("noshardtestonmysql_3.person"));

        try {
            MockIgnite(null);
        } catch (Exception e) {
            logger.error(e.getMessage());
            fail();
        }

        assertNotNull(cache.get("testprefetchwithregistereddb.bigintidtable"));
        assertNull(cache.get("noshardtestonmysql_1.person"));
        assertNull(cache.get("noshardtestonmysql_2.testtable"));
        assertNull(cache.get("noshardtestonmysql_3.person"));

        //      insert single pojo
        AutoGenBigintIDDao bigintIDDao = new AutoGenBigintIDDao();
        TableWithBigintIdentity singlePojo = new TableWithBigintIdentity();
        singlePojo.setName("testBigintIDSinglePojo");
        bigintIDDao.insert(new DalHints().setIdentityBack(), singlePojo);
        assertEquals("testBigintIDSinglePojo", bigintIDDao.queryByPk(singlePojo.getID()).getName());

    }

    @Test
    public void testPrefetchFail() throws Exception {
        DalClientFactory.shutdownFactory();
        try {
            MockIgnite(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "IdGenPrefetch/Dal.config");
            fail();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        DalClientFactory.shutdownFactory();
    }

    private void MockIgnite(String url) throws Exception {
        logger.info("Initialize Dal Factory");
        if (url == null)
            DalClientFactory.initClientFactory();
        else
            DalClientFactory.initClientFactory(url);

        validateConnectionStrings();

        logger.info("success initialized Dal Factory");

        logger.info("Start warm up datasources");
        DalClientFactory.warmUpConnections();
        logger.info("success warmed up datasources");

        logger.info("Start warm up id generators");
        DalClientFactory.warmUpIdGenerators();
        logger.info("Success warmed up id generators");
    }

    private void validateConnectionStrings() throws Exception {
        DataSourceConfigureLocator locator = DataSourceConfigureLocatorManager.getInstance();
        Map<String, DalConnectionString> failedConnectionStringMap = locator.getFailedConnectionStrings();
        if (failedConnectionStringMap == null || failedConnectionStringMap.isEmpty())
            return;

        StringBuilder errorMsg = new StringBuilder();
        for (Map.Entry<String, DalConnectionString> entry : failedConnectionStringMap.entrySet()) {
            if (entry.getValue() instanceof DalInvalidConnectionString) {
                errorMsg.append(String.format("[TitanKey: %s, ErrorMessage: %s] ", entry.getKey(), ((DalInvalidConnectionString) entry.getValue()).getConnectionStringException().getMessage()));
            }
        }
        throw new DalRuntimeException(errorMsg.toString());
    }
}
