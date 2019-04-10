package idegentest;


import IDAutoGenerator.*;
import IDNotAotuGenerator.NoAutoGenIDDao;
import IDNotAotuGenerator.TableWithNoIdentity;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.util.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;


/**
 * JUnit test of PersonGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
 **/
public class IdGenTestOnMysql {
    private static Logger logger = LoggerFactory.getLogger(IdGenTestOnMysql.class);
    private static AutoGenIDDao dao = null;
    private static AutoGenIDDao tableShardDao = null;
    private static AutoGenIDDao dbShardDao = null;
    private static AutoGenIDDao dbTableShardDao = null;
    private static NoAutoGenIDDao idNotAGDao = null;
    private static AutoGenIntegerIDDao integerIDDao = null;
    private static AutoGenBigintIDDao bigintIDDao = null;

    protected static String NO_SHARD_DAO = "noShardTestOnMysql";
    protected static String TABLE_SHARD_DAO = "ShardColModShardByTableOnMysql";
    protected static String DB_SHARD_DAO = "ShardColModShardByDBOnMysql";
    protected static String DB_TABLE_SHARD_DAO = "SimpleShardByDBTableOnMysql";
    protected static String TEST_EXCLUDE_DAO = "testExclude";
    protected static String TEST_INCLUDE_DAO = "testInclude";
    private static String EXCLUDE_TABLE1 = "Person_0";
    private static String EXCLUDE_TABLE2 = "perSon_1";
    private static String INCLUDE_NO_SEQUENCE_TABLE = "idGenai";
    private static String INCLUDE_SEQUENCE_TABLE = "peRson";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath() + "IdGen/Dal.config");
        dao = new AutoGenIDDao(NO_SHARD_DAO);
        tableShardDao = new AutoGenIDDao(TABLE_SHARD_DAO);
        dbShardDao = new AutoGenIDDao(DB_SHARD_DAO);
        dbTableShardDao = new AutoGenIDDao(DB_TABLE_SHARD_DAO);
        idNotAGDao = new NoAutoGenIDDao();
        integerIDDao = new AutoGenIntegerIDDao(NO_SHARD_DAO);
        bigintIDDao = new AutoGenBigintIDDao();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DalClientFactory.shutdownFactory();
    }

    @Before
    public void setUp() throws Exception {
        dao.test_def_update(new DalHints());
        dao.test_def_update(new DalHints(), "0");
        dao.test_def_update(new DalHints(), "1");
        dbShardDao.test_def_update(new DalHints().inShard(0));
        dbShardDao.test_def_update(new DalHints().inShard(1));
        dbTableShardDao.test_def_update(new DalHints().inShard(0), "0");
        dbTableShardDao.test_def_update(new DalHints().inShard(0), "1");
        dbTableShardDao.test_def_update(new DalHints().inShard(1), "0");
        dbTableShardDao.test_def_update(new DalHints().inShard(1), "1");
        idNotAGDao.test_def_update(new DalHints());
        integerIDDao.test_def_update(null);
        bigintIDDao.test_def_update(null);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testStringID() throws Exception {
        DalTableDao dao = new DalTableDao(TableWithStringIdentity.class,NO_SHARD_DAO);
        TableWithStringIdentity singlePojo = new TableWithStringIdentity();
        singlePojo.setName("stringIDInsert");
        try {
            dao.insert(new DalHints(), singlePojo);
            fail();
        }catch (Exception e){
            assertEquals("Unknown Exception, caused by: Unsupported auto-incremental column type",e.getMessage());
        }

    }

    @Test
    public void testBigintID() throws Exception {
//      insert single pojo
        TableWithBigintIdentity singlePojo = new TableWithBigintIdentity();
        singlePojo.setName("testBigintIDSinglePojo");
        bigintIDDao.insert(new DalHints().setIdentityBack(), singlePojo);
        assertEquals("testBigintIDSinglePojo", bigintIDDao.queryByPk(singlePojo.getID()).getName());

//        insert pojo list
        List<TableWithBigintIdentity> pojoList = new ArrayList<>();
        TableWithBigintIdentity pojo1 = new TableWithBigintIdentity();
        pojo1.setName("testBigintIDPojoList1");
        TableWithBigintIdentity pojo2 = new TableWithBigintIdentity();
        pojo2.setName("testBigintIDPojoList2");
        pojoList.add(pojo1);
        pojoList.add(pojo2);
        bigintIDDao.insert(new DalHints().setIdentityBack(), pojoList);
        assertEquals("testBigintIDPojoList1", bigintIDDao.queryByPk(pojo1.getID()).getName());
        assertEquals("testBigintIDPojoList2", bigintIDDao.queryByPk(pojo2.getID()).getName());

//        batchInsert
        pojoList.clear();
        TableWithBigintIdentity batchInsertPojo1 = new TableWithBigintIdentity();
        batchInsertPojo1.setName("testBigintIDBatchInsert1");
        TableWithBigintIdentity batchInsertPojo2 = new TableWithBigintIdentity();
        batchInsertPojo2.setName("testBigintIDBatchInsert2");
        pojoList.add(batchInsertPojo1);
        pojoList.add(batchInsertPojo2);
        bigintIDDao.batchInsert(new DalHints(), pojoList);
        assertEquals("testBigintIDBatchInsert1", bigintIDDao.queryBy(batchInsertPojo1).get(0).getName());
        assertEquals("testBigintIDBatchInsert2", bigintIDDao.queryBy(batchInsertPojo2).get(0).getName());

//        combinedInsert
        pojoList.clear();
        TableWithBigintIdentity combinedInsert1 = new TableWithBigintIdentity();
        combinedInsert1.setName("testBigintIDCombinedInsert1");
        TableWithBigintIdentity combinedInsert2 = new TableWithBigintIdentity();
        combinedInsert2.setName("testBigintIDCombinedInsert2");
        pojoList.add(combinedInsert1);
        pojoList.add(combinedInsert2);
        bigintIDDao.combinedInsert(new DalHints().setIdentityBack(), pojoList);
        assertEquals("testBigintIDCombinedInsert1", bigintIDDao.queryByPk(combinedInsert1.getID()).getName());
        assertEquals("testBigintIDCombinedInsert2", bigintIDDao.queryByPk(combinedInsert2.getID()).getName());
    }

    @Test
    public void testBigintIDReplace() throws Exception {
//      insert single pojo
        TableWithBigintIdentity singlePojo = new TableWithBigintIdentity();
        singlePojo.setName("testBigintIDSinglePojo");
        bigintIDDao.replace(new DalHints().setIdentityBack(), singlePojo);
        assertEquals("testBigintIDSinglePojo", bigintIDDao.queryByPk(singlePojo.getID()).getName());

//        insert pojo list
        List<TableWithBigintIdentity> pojoList = new ArrayList<>();
        TableWithBigintIdentity pojo1 = new TableWithBigintIdentity();
        pojo1.setName("testBigintIDPojoList1");
        TableWithBigintIdentity pojo2 = new TableWithBigintIdentity();
        pojo2.setName("testBigintIDPojoList2");
        pojoList.add(pojo1);
        pojoList.add(pojo2);
        bigintIDDao.replace(new DalHints().setIdentityBack(), pojoList);
        assertEquals("testBigintIDPojoList1", bigintIDDao.queryByPk(pojo1.getID()).getName());
        assertEquals("testBigintIDPojoList2", bigintIDDao.queryByPk(pojo2.getID()).getName());

//        batchInsert
        pojoList.clear();
        TableWithBigintIdentity batchInsertPojo1 = new TableWithBigintIdentity();
        batchInsertPojo1.setName("testBigintIDBatchInsert1");
        TableWithBigintIdentity batchInsertPojo2 = new TableWithBigintIdentity();
        batchInsertPojo2.setName("testBigintIDBatchInsert2");
        pojoList.add(batchInsertPojo1);
        pojoList.add(batchInsertPojo2);
        bigintIDDao.batchReplace(new DalHints(), pojoList);
        assertEquals("testBigintIDBatchInsert1", bigintIDDao.queryBy(batchInsertPojo1).get(0).getName());
        assertEquals("testBigintIDBatchInsert2", bigintIDDao.queryBy(batchInsertPojo2).get(0).getName());

//        combinedInsert
        pojoList.clear();
        TableWithBigintIdentity combinedInsert1 = new TableWithBigintIdentity();
        combinedInsert1.setName("testBigintIDCombinedInsert1");
        TableWithBigintIdentity combinedInsert2 = new TableWithBigintIdentity();
        combinedInsert2.setName("testBigintIDCombinedInsert2");
        pojoList.add(combinedInsert1);
        pojoList.add(combinedInsert2);
        bigintIDDao.combinedReplace(new DalHints().setIdentityBack(), pojoList);
        assertEquals("testBigintIDCombinedInsert1", bigintIDDao.queryByPk(combinedInsert1.getID()).getName());
        assertEquals("testBigintIDCombinedInsert2", bigintIDDao.queryByPk(combinedInsert2.getID()).getName());
    }

    @Test
    public void testIntegerID() throws Exception {
//        int entity and int table
        TableWithIntegerIdentity singlePojo = new TableWithIntegerIdentity();
        singlePojo.setName("testIntegerIDDao");
        try {
            integerIDDao.insert(new DalHints().setIdentityBack(), singlePojo);
            fail();
        } catch (Exception e) {
            assertEquals("Unknown Exception, caused by: The range of the generated id type exceeds that of the auto-incremental column type",e.getMessage());
        }
//        long entity and int table
        AutoGenIDDao dao = new AutoGenIDDao(NO_SHARD_DAO, "testtable");
        TableWithIdentity pojo = new TableWithIdentity();
        try {
            dao.insert(null, pojo);
            fail();
        } catch (Exception e) {
            assertEquals("Data truncation: Out of range value for column 'ID' at row 1", e.getMessage());
        }
    }


    @Test
    public void testExcludeTable1() throws Exception {
        AutoGenIDDao testExcludeDao1 = new AutoGenIDDao(TEST_EXCLUDE_DAO, EXCLUDE_TABLE1);
        AutoGenIDDao testExcludeDao2 = new AutoGenIDDao(TEST_EXCLUDE_DAO, EXCLUDE_TABLE2);
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setName("testExcludeSingleInsert");
        try {
            testExcludeDao1.insert(null, singlePojo);
            testExcludeDao2.insert(null, singlePojo);
        } catch (Exception e) {
            fail();
        }
        assertEquals("testExcludeSingleInsert", testExcludeDao1.queryByPk(1L, null).getName());
        assertEquals("testExcludeSingleInsert", testExcludeDao2.queryByPk(1L, null).getName());

        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("testExcludeInsertList1");
        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("testExcludeInsertList2");
        pojoList.add(pojo1);
        pojoList.add(pojo2);
        try {
            testExcludeDao1.insert(null, pojoList);
            testExcludeDao2.insert(null, pojoList);
        } catch (Exception e) {
            fail();
        }
        assertEquals("testExcludeInsertList1", testExcludeDao1.queryByPk(2L, null).getName());
        assertEquals("testExcludeInsertList2", testExcludeDao1.queryByPk(3L, null).getName());
        assertEquals("testExcludeInsertList1", testExcludeDao2.queryByPk(2L, null).getName());
        assertEquals("testExcludeInsertList2", testExcludeDao2.queryByPk(3L, null).getName());

        pojo1.setName("testExcludeBatchInsert1");
        pojo2.setName("testExcludeBatchInsert2");
        try {
            testExcludeDao1.batchInsert(null, pojoList);
            testExcludeDao2.batchInsert(null, pojoList);
        } catch (Exception e) {
            fail();
        }
        assertEquals("testExcludeBatchInsert1", testExcludeDao1.queryByPk(4L, null).getName());
        assertEquals("testExcludeBatchInsert2", testExcludeDao1.queryByPk(5L, null).getName());
        assertEquals("testExcludeBatchInsert1", testExcludeDao2.queryByPk(4L, null).getName());
        assertEquals("testExcludeBatchInsert2", testExcludeDao2.queryByPk(5L, null).getName());

        pojo1.setName("testExcludeCombinedInsert1");
        pojo2.setName("testExcludeCombinedInsert2");
        try {
            testExcludeDao1.combinedInsert(null, pojoList);
            testExcludeDao2.combinedInsert(null, pojoList);
        } catch (Exception e) {
            fail();
        }
        assertEquals("testExcludeCombinedInsert1", testExcludeDao1.queryByPk(6L, null).getName());
        assertEquals("testExcludeCombinedInsert2", testExcludeDao1.queryByPk(7L, null).getName());
        assertEquals("testExcludeCombinedInsert1", testExcludeDao2.queryByPk(6L, null).getName());
        assertEquals("testExcludeCombinedInsert2", testExcludeDao2.queryByPk(7L, null).getName());
    }

    @Test
    public void testExcludeTable2() throws Exception {
        try {
            AutoGenIDDao testExcludeDao = new AutoGenIDDao(TEST_EXCLUDE_DAO, INCLUDE_NO_SEQUENCE_TABLE);
            TableWithIdentity singlePojo = new TableWithIdentity();
            singlePojo.setName("testExcludeTable");
            testExcludeDao.insert(null, singlePojo);
            fail();
        } catch (Exception e) {

        }

        try {
            AutoGenIDDao testExcludeDao2 = new AutoGenIDDao(TEST_EXCLUDE_DAO, INCLUDE_SEQUENCE_TABLE);
            TableWithIdentity singlePojo2 = new TableWithIdentity();
            singlePojo2.setName("testExcludeTable2");
            testExcludeDao2.insert(null, singlePojo2);
            assertNotEquals(1L, testExcludeDao2.queryBy(singlePojo2, null));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testIncludeTable() throws Exception {
        AutoGenIDDao testIncludeDao = new AutoGenIDDao(TEST_INCLUDE_DAO, INCLUDE_SEQUENCE_TABLE);
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setName("testIncludeTable");
        testIncludeDao.insert(null, singlePojo);
        assertNotEquals(1L, testIncludeDao.queryBy(singlePojo, null));

        try {
            AutoGenIDDao testIncludeDao2 = new AutoGenIDDao(TEST_INCLUDE_DAO, INCLUDE_NO_SEQUENCE_TABLE);
            TableWithIdentity singlePojo2 = new TableWithIdentity();
            singlePojo2.setName("testIncludeTable2");
            testIncludeDao2.insert(null, singlePojo2);
            fail();
        } catch (Exception e) {

        }

        AutoGenIDDao testIncludeDao3 = new AutoGenIDDao(TEST_INCLUDE_DAO, EXCLUDE_TABLE1);
        TableWithIdentity singlePojo3 = new TableWithIdentity();
        singlePojo3.setName("testIncludeTable3");
        testIncludeDao3.insert(null, singlePojo3);
        assertEquals(1L, testIncludeDao3.queryBy(singlePojo3, null).get(0).getID().longValue());
    }

    @Test
    public void testSingleInsert() throws Exception {
//        null hints
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setAge(20);
        singlePojo.setName("TestSinglePojoInsertWithNullHints");
        dao.insert(new DalHints(), singlePojo);

        List<TableWithIdentity> queryPojos = dao.queryBy(singlePojo, null);
        assertEquals(1, queryPojos.size());
        assertNotEquals(1L, queryPojos.get(0).getID().longValue());

//     hints.enableIdentityInsert
        TableWithIdentity singlePojo2 = new TableWithIdentity();
        singlePojo2.setID(1L);
        singlePojo2.setAge(20);
        singlePojo2.setName("TestSinglePojoInsertWithEnableIdentityInsert");
        dao.insert(new DalHints().enableIdentityInsert(), singlePojo2);

        TableWithIdentity queryPojo = dao.queryByPk(1L, null);
        assertEquals("TestSinglePojoInsertWithEnableIdentityInsert", queryPojo.getName());

//        hints.setIdentityBack
        TableWithIdentity singlePojo3 = new TableWithIdentity();
        singlePojo3.setAge(20);
        singlePojo3.setName("TestSinglePojoInsertWithSetIdentityBack");
        dao.insert(new DalHints().setIdentityBack(), singlePojo3);
        TableWithIdentity queryPojo2 = dao.queryByPk(singlePojo3.getID(), null);
        assertEquals("TestSinglePojoInsertWithSetIdentityBack", queryPojo2.getName());

        //        keyholder
        TableWithIdentity singlePojo4 = new TableWithIdentity();
        singlePojo4.setAge(20);
        singlePojo4.setName("TestSinglePojoInsertWithKeyholder");
        KeyHolder keyholder = new KeyHolder();
        dao.insert(new DalHints().setIdentityBack(), keyholder, singlePojo4);
        TableWithIdentity queryPojo3 = dao.queryByPk(singlePojo4.getID(), null);
        assertEquals("TestSinglePojoInsertWithKeyholder", queryPojo3.getName());
        assertEquals(singlePojo4.getID(), keyholder.getKey());
    }

    @Test
    public void testSingleReplace() throws Exception {
//        null hints
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setAge(20);
        singlePojo.setName("TestSinglePojoInsertWithNullHints");
        dao.replace(new DalHints(), singlePojo);

        List<TableWithIdentity> queryPojos = dao.queryBy(singlePojo, null);
        assertEquals(1, queryPojos.size());
        assertNotEquals(1L, queryPojos.get(0).getID().longValue());

//     hints.enableIdentityInsert
        TableWithIdentity singlePojo2 = new TableWithIdentity();
        singlePojo2.setID(1L);
        singlePojo2.setAge(20);
        singlePojo2.setName("TestSinglePojoInsertWithEnableIdentityInsert");
        dao.replace(new DalHints().enableIdentityInsert(), singlePojo2);

        TableWithIdentity queryPojo = dao.queryByPk(1L, null);
        assertEquals("TestSinglePojoInsertWithEnableIdentityInsert", queryPojo.getName());

//        hints.setIdentityBack
        TableWithIdentity singlePojo3 = new TableWithIdentity();
        singlePojo3.setAge(20);
        singlePojo3.setName("TestSinglePojoInsertWithSetIdentityBack");
        dao.replace(new DalHints().setIdentityBack(), singlePojo3);
        TableWithIdentity queryPojo2 = dao.queryByPk(singlePojo3.getID(), null);
        assertEquals("TestSinglePojoInsertWithSetIdentityBack", queryPojo2.getName());

        //        keyholder
        TableWithIdentity singlePojo4 = new TableWithIdentity();
        singlePojo4.setAge(20);
        singlePojo4.setName("TestSinglePojoInsertWithKeyholder");
        KeyHolder keyholder = new KeyHolder();
        dao.replace(new DalHints().setIdentityBack(), keyholder, singlePojo4);
        TableWithIdentity queryPojo3 = dao.queryByPk(singlePojo4.getID(), null);
        assertEquals("TestSinglePojoInsertWithKeyholder", queryPojo3.getName());
        assertEquals(singlePojo4.getID(), keyholder.getKey());
    }

    @Test
    public void testTableShardSingleInsert() throws Exception {
//        null hints
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setAge(20);
        singlePojo.setName("TestTableShardSinglePojoInsertWithNullHints");
        tableShardDao.insert(new DalHints(), singlePojo);

        List<TableWithIdentity> queryPojos = tableShardDao.queryBy(singlePojo, null);
        assertEquals(1, queryPojos.size());
        assertNotEquals(1L, queryPojos.get(0).getID().longValue());

//     hints.enableIdentityInsert
        TableWithIdentity singlePojo2 = new TableWithIdentity();
        singlePojo2.setID(1L);
        singlePojo2.setName("TestTableShardWithEnableIdentityInsert");
        tableShardDao.insert(new DalHints().enableIdentityInsert().inTableShard(1), singlePojo2);

        TableWithIdentity queryPojo = tableShardDao.queryByPk(1L, new DalHints().inTableShard(1));
        assertEquals("TestTableShardWithEnableIdentityInsert", queryPojo.getName());

//        hints.setIdentityBack
        TableWithIdentity singlePojo3 = new TableWithIdentity();
        singlePojo3.setName("TestTableShardWithSetIdentityBack");
        tableShardDao.insert(new DalHints().setIdentityBack().setTableShardValue(22), singlePojo3);
        TableWithIdentity queryPojo2 = tableShardDao.queryByPk(singlePojo3.getID(), new DalHints().inTableShard(0));
        assertEquals("TestTableShardWithSetIdentityBack", queryPojo2.getName());

        //        keyholder
        TableWithIdentity singlePojo4 = new TableWithIdentity();
        singlePojo4.setName("TestTableShardSinglePojoInsertWithKeyholder");
        KeyHolder keyholder = new KeyHolder();
        tableShardDao.insert(new DalHints().setIdentityBack().setShardColValue("age", 21), keyholder, singlePojo4);
        TableWithIdentity queryPojo3 = tableShardDao.queryByPk(singlePojo4.getID(), new DalHints().inTableShard(1));
        assertEquals("TestTableShardSinglePojoInsertWithKeyholder", queryPojo3.getName());
        assertEquals(singlePojo4.getID(), keyholder.getKey());
    }

    @Test
    public void testTableShardSingleReplace() throws Exception {
//        null hints
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setAge(20);
        singlePojo.setName("TestTableShardSinglePojoInsertWithNullHints");
        tableShardDao.replace(new DalHints(), singlePojo);

        List<TableWithIdentity> queryPojos = tableShardDao.queryBy(singlePojo, null);
        assertEquals(1, queryPojos.size());
        assertNotEquals(1L, queryPojos.get(0).getID().longValue());

//     hints.enableIdentityInsert
        TableWithIdentity singlePojo2 = new TableWithIdentity();
        singlePojo2.setID(1L);
        singlePojo2.setName("TestTableShardWithEnableIdentityInsert");
        tableShardDao.replace(new DalHints().enableIdentityInsert().inTableShard(1), singlePojo2);

        TableWithIdentity queryPojo = tableShardDao.queryByPk(1L, new DalHints().inTableShard(1));
        assertEquals("TestTableShardWithEnableIdentityInsert", queryPojo.getName());

//        hints.setIdentityBack
        TableWithIdentity singlePojo3 = new TableWithIdentity();
        singlePojo3.setName("TestTableShardWithSetIdentityBack");
        tableShardDao.replace(new DalHints().setIdentityBack().setTableShardValue(22), singlePojo3);
        TableWithIdentity queryPojo2 = tableShardDao.queryByPk(singlePojo3.getID(), new DalHints().inTableShard(0));
        assertEquals("TestTableShardWithSetIdentityBack", queryPojo2.getName());

        //        keyholder
        TableWithIdentity singlePojo4 = new TableWithIdentity();
        singlePojo4.setName("TestTableShardSinglePojoInsertWithKeyholder");
        KeyHolder keyholder = new KeyHolder();
        tableShardDao.replace(new DalHints().setIdentityBack().setShardColValue("age", 21), keyholder, singlePojo4);
        TableWithIdentity queryPojo3 = tableShardDao.queryByPk(singlePojo4.getID(), new DalHints().inTableShard(1));
        assertEquals("TestTableShardSinglePojoInsertWithKeyholder", queryPojo3.getName());
        assertEquals(singlePojo4.getID(), keyholder.getKey());
    }


    @Test
    public void testDBTableShardSingleInsert() throws Exception {
//        null hints
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setAge(20);
        singlePojo.setName("TestDBTableShardSingleInsertWithNullHints");
        dbTableShardDao.insert(new DalHints().inShard(0).inTableShard(0), singlePojo);

        List<TableWithIdentity> queryPojos = dbTableShardDao.queryBy(singlePojo, new DalHints().inShard(0).inTableShard(0));
        assertEquals(1, queryPojos.size());
        assertNotEquals(1L, queryPojos.get(0).getID().longValue());

//     hints.enableIdentityInsert
        TableWithIdentity singlePojo2 = new TableWithIdentity();
        singlePojo2.setID(1L);
        singlePojo2.setName("TestDBTableShardWithEnableIdentityInsert");
        dbTableShardDao.insert(new DalHints().enableIdentityInsert().inShard(0).inTableShard(1), singlePojo2);

        TableWithIdentity queryPojo = dbTableShardDao.queryByPk(1L, new DalHints().inShard(0).inTableShard(1));
        assertEquals("TestDBTableShardWithEnableIdentityInsert", queryPojo.getName());

//        hints.setIdentityBack
        TableWithIdentity singlePojo3 = new TableWithIdentity();
        singlePojo3.setName("TestDBTableShardWithSetIdentityBack");
        dbTableShardDao.insert(new DalHints().setIdentityBack().inShard(1).inTableShard(0), singlePojo3);
        TableWithIdentity queryPojo2 = dbTableShardDao.queryByPk(singlePojo3.getID(), new DalHints().inShard(1).inTableShard(0));
        assertEquals("TestDBTableShardWithSetIdentityBack", queryPojo2.getName());

        //        keyholder
        TableWithIdentity singlePojo4 = new TableWithIdentity();
        singlePojo4.setName("TestDBTableShardWithKeyholder");
        KeyHolder keyholder = new KeyHolder();
        dbTableShardDao.insert(new DalHints().setIdentityBack().inShard(1).inTableShard(1), keyholder, singlePojo4);
        TableWithIdentity queryPojo3 = dbTableShardDao.queryByPk(singlePojo4.getID(), new DalHints().inShard(1).inTableShard(1));
        assertEquals("TestDBTableShardWithKeyholder", queryPojo3.getName());
        assertEquals(singlePojo4.getID(), keyholder.getKey());
    }

    @Test
    public void testDBTableShardSingleReplace() throws Exception {
//        null hints
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setAge(20);
        singlePojo.setName("TestDBTableShardSingleInsertWithNullHints");
        dbTableShardDao.replace(new DalHints().inShard(0).inTableShard(0), singlePojo);

        List<TableWithIdentity> queryPojos = dbTableShardDao.queryBy(singlePojo, new DalHints().inShard(0).inTableShard(0));
        assertEquals(1, queryPojos.size());
        assertNotEquals(1L, queryPojos.get(0).getID().longValue());

//     hints.enableIdentityInsert
        TableWithIdentity singlePojo2 = new TableWithIdentity();
        singlePojo2.setID(1L);
        singlePojo2.setName("TestDBTableShardWithEnableIdentityInsert");
        dbTableShardDao.replace(new DalHints().enableIdentityInsert().inShard(0).inTableShard(1), singlePojo2);

        TableWithIdentity queryPojo = dbTableShardDao.queryByPk(1L, new DalHints().inShard(0).inTableShard(1));
        assertEquals("TestDBTableShardWithEnableIdentityInsert", queryPojo.getName());

//        hints.setIdentityBack
        TableWithIdentity singlePojo3 = new TableWithIdentity();
        singlePojo3.setName("TestDBTableShardWithSetIdentityBack");
        dbTableShardDao.replace(new DalHints().setIdentityBack().inShard(1).inTableShard(0), singlePojo3);
        TableWithIdentity queryPojo2 = dbTableShardDao.queryByPk(singlePojo3.getID(), new DalHints().inShard(1).inTableShard(0));
        assertEquals("TestDBTableShardWithSetIdentityBack", queryPojo2.getName());

        //        keyholder
        TableWithIdentity singlePojo4 = new TableWithIdentity();
        singlePojo4.setName("TestDBTableShardWithKeyholder");
        KeyHolder keyholder = new KeyHolder();
        dbTableShardDao.replace(new DalHints().setIdentityBack().inShard(1).inTableShard(1), keyholder, singlePojo4);
        TableWithIdentity queryPojo3 = dbTableShardDao.queryByPk(singlePojo4.getID(), new DalHints().inShard(1).inTableShard(1));
        assertEquals("TestDBTableShardWithKeyholder", queryPojo3.getName());
        assertEquals(singlePojo4.getID(), keyholder.getKey());
    }

    @Test
    public void testDBShardSingleInsert() throws Exception {
//        null hints
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setAge(20);
        singlePojo.setName("TestDBShardSinglePojoInsertWithNullHints");
        dbShardDao.insert(new DalHints(), singlePojo);

        List<TableWithIdentity> queryPojos = dbShardDao.queryBy(singlePojo, null);
        assertEquals(1, queryPojos.size());
        assertNotEquals(1L, queryPojos.get(0).getID().longValue());

//     hints.enableIdentityInsert
        TableWithIdentity singlePojo2 = new TableWithIdentity();
        singlePojo2.setID(1L);
        singlePojo2.setName("TestDBShardWithEnableIdentityInsert");
        dbShardDao.insert(new DalHints().enableIdentityInsert().inShard(1), singlePojo2);

        TableWithIdentity queryPojo = dbShardDao.queryByPk(1L, new DalHints().inShard(1));
        assertEquals("TestDBShardWithEnableIdentityInsert", queryPojo.getName());

//        hints.setIdentityBack
        TableWithIdentity singlePojo3 = new TableWithIdentity();
        singlePojo3.setName("TestDBShardWithSetIdentityBack");
        dbShardDao.insert(new DalHints().setIdentityBack().setShardValue(22), singlePojo3);
        TableWithIdentity queryPojo2 = dbShardDao.queryByPk(singlePojo3.getID(), new DalHints().inShard(0));
        assertEquals("TestDBShardWithSetIdentityBack", queryPojo2.getName());

        //        keyholder
        TableWithIdentity singlePojo4 = new TableWithIdentity();
        singlePojo4.setName("TestDBShardSinglePojoInsertWithKeyholder");
        KeyHolder keyholder = new KeyHolder();
        dbShardDao.insert(new DalHints().setIdentityBack().setShardColValue("age", 21), keyholder, singlePojo4);
        TableWithIdentity queryPojo3 = dbShardDao.queryByPk(singlePojo4.getID(), new DalHints().inShard(1));
        assertEquals("TestDBShardSinglePojoInsertWithKeyholder", queryPojo3.getName());
        assertEquals(singlePojo4.getID(), keyholder.getKey());
    }

    @Test
    public void testDBShardSingleReplace() throws Exception {
//        null hints
        TableWithIdentity singlePojo = new TableWithIdentity();
        singlePojo.setAge(20);
        singlePojo.setName("TestDBShardSinglePojoInsertWithNullHints");
        dbShardDao.replace(new DalHints(), singlePojo);

        List<TableWithIdentity> queryPojos = dbShardDao.queryBy(singlePojo, null);
        assertEquals(1, queryPojos.size());
        assertNotEquals(1L, queryPojos.get(0).getID().longValue());

//     hints.enableIdentityInsert
        TableWithIdentity singlePojo2 = new TableWithIdentity();
        singlePojo2.setID(1L);
        singlePojo2.setName("TestDBShardWithEnableIdentityInsert");
        dbShardDao.replace(new DalHints().enableIdentityInsert().inShard(1), singlePojo2);

        TableWithIdentity queryPojo = dbShardDao.queryByPk(1L, new DalHints().inShard(1));
        assertEquals("TestDBShardWithEnableIdentityInsert", queryPojo.getName());

//        hints.setIdentityBack
        TableWithIdentity singlePojo3 = new TableWithIdentity();
        singlePojo3.setName("TestDBShardWithSetIdentityBack");
        dbShardDao.replace(new DalHints().setIdentityBack().setShardValue(22), singlePojo3);
        TableWithIdentity queryPojo2 = dbShardDao.queryByPk(singlePojo3.getID(), new DalHints().inShard(0));
        assertEquals("TestDBShardWithSetIdentityBack", queryPojo2.getName());

        //        keyholder
        TableWithIdentity singlePojo4 = new TableWithIdentity();
        singlePojo4.setName("TestDBShardSinglePojoInsertWithKeyholder");
        KeyHolder keyholder = new KeyHolder();
        dbShardDao.replace(new DalHints().setIdentityBack().setShardColValue("age", 21), keyholder, singlePojo4);
        TableWithIdentity queryPojo3 = dbShardDao.queryByPk(singlePojo4.getID(), new DalHints().inShard(1));
        assertEquals("TestDBShardSinglePojoInsertWithKeyholder", queryPojo3.getName());
        assertEquals(singlePojo4.getID(), keyholder.getKey());
    }

    @Test
    public void testSingleInsertList() throws Exception {
//        null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("testSingleInsertList1");

        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("testSingleInsertList2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dao.insert(new DalHints(), pojoList);
        assertEquals(2, dao.count(null));
        assertNotEquals(1L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertNotEquals(2L, dao.queryBy(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(3L);
        pojo1.setName("testEnableIdentityInsertList1");
        pojo2.setID(4L);
        pojo2.setName("testEnableIdentityInsertList2");

        dao.insert(new DalHints().enableIdentityInsert(), pojoList);
        assertEquals("testEnableIdentityInsertList1", dao.queryByPk(3L, null).getName());
        assertEquals("testEnableIdentityInsertList2", dao.queryByPk(4L, null).getName());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("testSetIdentityBackInsertList1");
        pojo2.setID(null);
        pojo2.setName("testSetIdentityBackInsertList2");

        dao.insert(new DalHints().setIdentityBack(), pojoList);
        assertNotNull(dao.queryBy(pojo1, null).get(0));
        assertNotNull(dao.queryBy(pojo2, null).get(0));
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());

//        keyholder
        pojo1.setID(null);
        pojo1.setName("testKeyholderInsertList1");
        pojo2.setID(null);
        pojo2.setName("testKeyholderInsertList2");
        KeyHolder keyHolder = new KeyHolder();
        dao.insert(new DalHints(), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(dao.queryBy(pojo1, null).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(dao.queryBy(pojo2, null).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }

    @Test
    public void testSingleReplaceList() throws Exception {
//        null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("testSingleInsertList1");

        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("testSingleInsertList2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dao.replace(new DalHints(), pojoList);
        assertEquals(2, dao.count(null));
        assertNotEquals(1L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertNotEquals(2L, dao.queryBy(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(3L);
        pojo1.setName("testEnableIdentityInsertList1");
        pojo2.setID(4L);
        pojo2.setName("testEnableIdentityInsertList2");

        dao.replace(new DalHints().enableIdentityInsert(), pojoList);
        assertEquals("testEnableIdentityInsertList1", dao.queryByPk(3L, null).getName());
        assertEquals("testEnableIdentityInsertList2", dao.queryByPk(4L, null).getName());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("testSetIdentityBackInsertList1");
        pojo2.setID(null);
        pojo2.setName("testSetIdentityBackInsertList2");

        dao.replace(new DalHints().setIdentityBack(), pojoList);
        assertNotNull(dao.queryBy(pojo1, null).get(0));
        assertNotNull(dao.queryBy(pojo2, null).get(0));
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());

//        keyholder
        pojo1.setID(null);
        pojo1.setName("testKeyholderInsertList1");
        pojo2.setID(null);
        pojo2.setName("testKeyholderInsertList2");
        KeyHolder keyHolder = new KeyHolder();
        dao.replace(new DalHints(), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(dao.queryBy(pojo1, null).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(dao.queryBy(pojo2, null).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }

    @Test
    public void testTableShardSingleInsertList() throws Exception {
//        null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("testTableShardSingleInsertList1");
        pojo1.setAge(20);
        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("testTableShardSingleInsertList2");
        pojo2.setAge(21);
        pojoList.add(pojo1);
        pojoList.add(pojo2);

        tableShardDao.insert(new DalHints(), pojoList);
        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(0)));
        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(1)));
        assertNotEquals(1L, tableShardDao.queryBy(pojo1, new DalHints().inTableShard(0)).get(0).getID().longValue());
        assertNotEquals(1L, tableShardDao.queryBy(pojo2, new DalHints().inTableShard(1)).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(3L);
        pojo1.setAge(null);
        pojo1.setName("testEnableIdentityInsertList1");
        pojo2.setID(4L);
        pojo2.setAge(null);
        pojo2.setName("testEnableIdentityInsertList2");

        tableShardDao.insert(new DalHints().enableIdentityInsert().inTableShard(0), pojoList);
        assertEquals("testEnableIdentityInsertList1", tableShardDao.queryByPk(3L, new DalHints().inTableShard(0)).getName());
        assertEquals("testEnableIdentityInsertList2", tableShardDao.queryByPk(4L, new DalHints().inTableShard(0)).getName());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("testSetIdentityBackInsertList1");
        pojo2.setID(null);
        pojo2.setName("testSetIdentityBackInsertList2");

        tableShardDao.insert(new DalHints().setIdentityBack().setTableShardValue(21), pojoList);
        assertNotNull(tableShardDao.queryBy(pojo1, new DalHints().setTableShardValue(21)).get(0));
        assertNotNull(tableShardDao.queryBy(pojo2, new DalHints().setTableShardValue(21)).get(0));
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());

//        keyholder
        pojo1.setID(null);
        pojo1.setName("testKeyholderInsertList1");
        pojo2.setID(null);
        pojo2.setName("testKeyholderInsertList2");
        KeyHolder keyHolder = new KeyHolder();
        tableShardDao.insert(new DalHints().setShardColValue("age", 20), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(tableShardDao.queryBy(pojo1, new DalHints().inTableShard(0)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(tableShardDao.queryBy(pojo2, new DalHints().inTableShard(0)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }

    @Test
    public void testDBTableShardSingleInsertList() throws Exception {
//        null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("testDBTableShardSingleInsertList1");
        pojo1.setAge(21);
        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("testDBTableShardSingleInsertList2");
        pojo2.setAge(20);
        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dbTableShardDao.insert(new DalHints().inShard(0).inTableShard(0), pojoList);
        assertEquals(2, dbTableShardDao.count(new DalHints().inShard(0).inTableShard(0)));
        assertNotEquals(1L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());
        assertNotEquals(2L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(3L);
        pojo1.setName("testEnableIdentityInsertList1");
        pojo2.setID(4L);
        pojo2.setName("testEnableIdentityInsertList2");

        dbTableShardDao.insert(new DalHints().enableIdentityInsert().inShard(0).inTableShard(1), pojoList);
        assertEquals("testEnableIdentityInsertList1", dbTableShardDao.queryByPk(3L, new DalHints().inShard(0).inTableShard(1)).getName());
        assertEquals("testEnableIdentityInsertList2", dbTableShardDao.queryByPk(4L, new DalHints().inShard(0).inTableShard(1)).getName());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("testSetIdentityBackInsertList1");
        pojo2.setID(null);
        pojo2.setName("testSetIdentityBackInsertList2");

        dbTableShardDao.insert(new DalHints().setIdentityBack().inShard(1).inTableShard(0), pojoList);
        assertNotNull(dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(0)).get(0));
        assertNotNull(dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(0)).get(0));
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());

//        keyholder
        pojo1.setID(null);
        pojo1.setName("testKeyholderInsertList1");
        pojo2.setID(null);
        pojo2.setName("testKeyholderInsertList2");
        KeyHolder keyHolder = new KeyHolder();
        dbTableShardDao.insert(new DalHints().inShard(1).inTableShard(1), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }

    @Test
    public void testDBShardSingleInsertList() throws Exception {
//        null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("testDBShardSingleInsertList1");
        pojo1.setAge(20);
        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("testDBShardSingleInsertList2");
        pojo2.setAge(21);
        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dbShardDao.insert(new DalHints(), pojoList);
        assertEquals(1, dbShardDao.count(new DalHints().inShard(0)));
        assertEquals(1, dbShardDao.count(new DalHints().inShard(1)));
        assertNotEquals(1L, dbShardDao.queryBy(pojo1, new DalHints().inShard(0)).get(0).getID().longValue());
        assertNotEquals(1L, dbShardDao.queryBy(pojo2, new DalHints().inShard(1)).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(3L);
        pojo1.setAge(null);
        pojo1.setName("testEnableIdentityInsertList1");
        pojo2.setID(4L);
        pojo2.setAge(null);
        pojo2.setName("testEnableIdentityInsertList2");

        dbShardDao.insert(new DalHints().enableIdentityInsert().inShard(0), pojoList);
        assertEquals("testEnableIdentityInsertList1", dbShardDao.queryByPk(3L, new DalHints().inShard(0)).getName());
        assertEquals("testEnableIdentityInsertList2", dbShardDao.queryByPk(4L, new DalHints().inShard(0)).getName());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("testSetIdentityBackInsertList1");
        pojo2.setID(null);
        pojo2.setName("testSetIdentityBackInsertList2");

        dbShardDao.insert(new DalHints().setIdentityBack().setShardValue(21), pojoList);
        assertNotNull(dbShardDao.queryBy(pojo1, new DalHints().setShardValue(21)).get(0));
        assertNotNull(dbShardDao.queryBy(pojo2, new DalHints().setShardValue(21)).get(0));
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());

//        keyholder
        pojo1.setID(null);
        pojo1.setName("testKeyholderInsertList1");
        pojo2.setID(null);
        pojo2.setName("testKeyholderInsertList2");
        KeyHolder keyHolder = new KeyHolder();
        dbShardDao.insert(new DalHints().setShardColValue("age", 20), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(dbShardDao.queryBy(pojo1, new DalHints().inShard(0)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(dbShardDao.queryBy(pojo2, new DalHints().inShard(0)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }

    @Test
    public void testBatchInsert() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("BatchInsertWithNullHints1");

        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("BatchInsertWithNullHints2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dao.batchInsert(new DalHints(), pojoList);
        assertEquals(2, dao.count(null));
        assertNotEquals(1L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertNotEquals(2L, dao.queryBy(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
        dao.batchInsert(new DalHints().enableIdentityInsert(), pojoList);
        assertEquals(20L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertEquals(21L, dao.queryBy(pojo2, null).get(0).getID().longValue());
    }

    @Test
    public void testBatchReplace() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("BatchReplaceWithNullHints1");

        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("BatchReplaceWithNullHints2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dao.batchReplace(new DalHints(), pojoList);
        assertEquals(2, dao.count(null));
        assertNotEquals(1L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertNotEquals(2L, dao.queryBy(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setName("BatchReplaceWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setName("BatchReplaceWithEnableIdentityInsert2");
        dao.batchReplace(new DalHints().enableIdentityInsert(), pojoList);
        assertEquals(20L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertEquals(21L, dao.queryBy(pojo2, null).get(0).getID().longValue());
    }

    @Test
    public void testTableShardBatchInsert() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("BatchInsertWithNullHints1");
        pojo1.setAge(20);
        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("BatchInsertWithNullHints2");
        pojo2.setAge(21);
        pojoList.add(pojo1);
        pojoList.add(pojo2);

        tableShardDao.batchInsert(new DalHints(), pojoList);
        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(0)));
        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(1)));
        assertNotEquals(1L, tableShardDao.queryBy(pojo1, new DalHints().inTableShard(0)).get(0).getID().longValue());
        assertNotEquals(1L, tableShardDao.queryBy(pojo2, new DalHints().inTableShard(1)).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setAge(null);
        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setAge(null);
        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
        tableShardDao.batchInsert(new DalHints().enableIdentityInsert().inTableShard(1), pojoList);
        assertEquals(20L, tableShardDao.queryBy(pojo1, new DalHints().inTableShard(1)).get(0).getID().longValue());
        assertEquals(21L, tableShardDao.queryBy(pojo2, new DalHints().inTableShard(1)).get(0).getID().longValue());
    }

    @Test
    public void testDBTableShardBatchInsert() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("BatchInsertWithNullHints1");
        pojo1.setAge(20);
        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("BatchInsertWithNullHints2");
        pojo2.setAge(21);
        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dbTableShardDao.batchInsert(new DalHints().inShard(0).inTableShard(0), pojoList);
        assertEquals(2, dbTableShardDao.count(new DalHints().inShard(0).inTableShard(0)));
        assertNotEquals(1L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());
        assertNotEquals(2L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setAge(null);
        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setAge(null);
        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
        dbTableShardDao.batchInsert(new DalHints().enableIdentityInsert().inShard(1).inTableShard(1), pojoList);
        assertEquals(20L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue());
        assertEquals(21L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue());
    }

    @Test
    public void testDBShardBatchInsert() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("BatchInsertWithNullHints1");
        pojo1.setAge(20);
        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("BatchInsertWithNullHints2");
        pojo2.setAge(21);
        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dbShardDao.batchInsert(new DalHints(), pojoList);
        assertEquals(1, dbShardDao.count(new DalHints().inShard(0)));
        assertEquals(1, dbShardDao.count(new DalHints().inShard(1)));
        assertNotEquals(1L, dbShardDao.queryBy(pojo1, new DalHints().inShard(0)).get(0).getID().longValue());
        assertNotEquals(1L, dbShardDao.queryBy(pojo2, new DalHints().inShard(1)).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setAge(null);
        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setAge(null);
        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
        dbShardDao.batchInsert(new DalHints().enableIdentityInsert().inShard(1), pojoList);
        assertEquals(20L, dbShardDao.queryBy(pojo1, new DalHints().inShard(1)).get(0).getID().longValue());
        assertEquals(21L, dbShardDao.queryBy(pojo2, new DalHints().inShard(1)).get(0).getID().longValue());
    }

    @Test
    public void testCombinedInsert() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("CombinedInsertWithNullHints1");

        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("CombinedInsertWithNullHints2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dao.combinedInsert(new DalHints(), pojoList);
        assertEquals(2, dao.count(null));
        assertNotEquals(1L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertNotEquals(2L, dao.queryBy(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
        dao.combinedInsert(new DalHints().enableIdentityInsert(), pojoList);
        assertEquals(20L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertEquals(21L, dao.queryBy(pojo2, null).get(0).getID().longValue());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("CombinedInsertWithSetIdentityBack1");
        pojo2.setID(null);
        pojo2.setName("CombinedInsertWithSetIdentityBack2");

        dao.combinedInsert(new DalHints().setIdentityBack(), pojoList);
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());
        assertEquals("CombinedInsertWithSetIdentityBack1", dao.queryBy(pojo1, null).get(0).getName());
        assertNotNull("CombinedInsertWithSetIdentityBack2", dao.queryBy(pojo2, null).get(0).getName());

//        keyholder
        pojo1.setID(null);
        pojo1.setName("CombinedInsertWithKeyholder1");
        pojo2.setID(null);
        pojo2.setName("CombinedInsertWithKeyholder2");
        KeyHolder keyHolder = new KeyHolder();
        dao.combinedInsert(new DalHints(), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(dao.queryBy(pojo1, null).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(dao.queryBy(pojo2, null).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }

    @Test
    public void testCombinedReplace() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setName("CombinedReplaceWithNullHints1");

        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setName("CombinedReplaceWithNullHints2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dao.combinedReplace(new DalHints(), pojoList);
        assertEquals(2, dao.count(null));
        assertNotEquals(1L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertNotEquals(2L, dao.queryBy(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setName("CombinedReplaceWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setName("CombinedReplaceWithEnableIdentityInsert2");
        dao.combinedReplace(new DalHints().enableIdentityInsert(), pojoList);
        assertEquals(20L, dao.queryBy(pojo1, null).get(0).getID().longValue());
        assertEquals(21L, dao.queryBy(pojo2, null).get(0).getID().longValue());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("CombinedReplaceWithSetIdentityBack1");
        pojo2.setID(null);
        pojo2.setName("CombinedReplaceWithSetIdentityBack2");

        dao.combinedReplace(new DalHints().setIdentityBack(), pojoList);
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());
        assertEquals("CombinedReplaceWithSetIdentityBack1", dao.queryBy(pojo1, null).get(0).getName());
        assertNotNull("CombinedReplaceWithSetIdentityBack2", dao.queryBy(pojo2, null).get(0).getName());

//        keyholder
        pojo1.setID(null);
        pojo1.setName("CombinedReplaceWithKeyholder1");
        pojo2.setID(null);
        pojo2.setName("CombinedReplaceWithKeyholder2");
        KeyHolder keyHolder = new KeyHolder();
        dao.combinedReplace(new DalHints(), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(dao.queryBy(pojo1, null).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(dao.queryBy(pojo2, null).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }

    @Test
    public void testTableShardCombinedInsert() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setAge(20);
        pojo1.setName("CombinedInsertWithNullHints1");

        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setAge(21);
        pojo2.setName("CombinedInsertWithNullHints2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        tableShardDao.combinedInsert(new DalHints(), pojoList);
        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(0)));
        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(1)));
        assertNotEquals(1L, tableShardDao.queryBy(pojo1, null).get(0).getID().longValue());
        assertNotEquals(2L, tableShardDao.queryBy(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setAge(null);
        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setAge(null);
        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
        tableShardDao.combinedInsert(new DalHints().enableIdentityInsert().inTableShard(0), pojoList);
        assertEquals(20L, tableShardDao.queryBy(pojo1, new DalHints().inTableShard(0)).get(0).getID().longValue());
        assertEquals(21L, tableShardDao.queryBy(pojo2, new DalHints().inTableShard(0)).get(0).getID().longValue());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("CombinedInsertWithSetIdentityBack1");
        pojo2.setID(null);
        pojo2.setName("CombinedInsertWithSetIdentityBack2");

        tableShardDao.combinedInsert(new DalHints().setIdentityBack().setTableShardValue(21), pojoList);
        assertNotNull(tableShardDao.queryBy(pojo1, new DalHints().setTableShardValue(21)).get(0));
        assertNotNull(tableShardDao.queryBy(pojo2, new DalHints().setTableShardValue(21)).get(0));
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());

//        keyholder
        pojo1.setID(null);
        pojo1.setName("CombinedInsertWithKeyholder1");
        pojo2.setID(null);
        pojo2.setName("CombinedInsertWithKeyholder2");
        KeyHolder keyHolder = new KeyHolder();
        tableShardDao.combinedInsert(new DalHints().setShardColValue("age", 20), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(tableShardDao.queryBy(pojo1, new DalHints().setShardColValue("age", 20)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(tableShardDao.queryBy(pojo2, new DalHints().setShardColValue("age", 20)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }

    @Test
    public void testDBTableShardCombinedInsert() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setAge(20);
        pojo1.setName("CombinedInsertWithNullHints1");

        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setAge(21);
        pojo2.setName("CombinedInsertWithNullHints2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dbTableShardDao.combinedInsert(new DalHints().inShard(0).inTableShard(0), pojoList);
        assertEquals(2, dbTableShardDao.count(new DalHints().inShard(0).inTableShard(0)));
        assertNotEquals(1L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());
        assertNotEquals(2L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setAge(null);
        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setAge(null);
        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
        dbTableShardDao.combinedInsert(new DalHints().enableIdentityInsert().inShard(0).inTableShard(1), pojoList);
        assertEquals(20L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(0).inTableShard(1)).get(0).getID().longValue());
        assertEquals(21L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(0).inTableShard(1)).get(0).getID().longValue());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("CombinedInsertWithSetIdentityBack1");
        pojo2.setID(null);
        pojo2.setName("CombinedInsertWithSetIdentityBack2");

        dbTableShardDao.combinedInsert(new DalHints().setIdentityBack().inShard(1).inTableShard(0), pojoList);
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());
        assertEquals("CombinedInsertWithSetIdentityBack1", dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(0)).get(0).getName());
        assertEquals("CombinedInsertWithSetIdentityBack2", dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(0)).get(0).getName());


//        keyholder
        pojo1.setID(null);
        pojo1.setName("CombinedInsertWithKeyholder1");
        pojo2.setID(null);
        pojo2.setName("CombinedInsertWithKeyholder2");
        KeyHolder keyHolder = new KeyHolder();
        dbTableShardDao.combinedInsert(new DalHints().inShard(1).inTableShard(1), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }

    @Test
    public void testDBShardCombinedInsert() throws Exception {
//       null hints
        List<TableWithIdentity> pojoList = new ArrayList<>();
        TableWithIdentity pojo1 = new TableWithIdentity();
        pojo1.setAge(20);
        pojo1.setName("CombinedInsertWithNullHints1");

        TableWithIdentity pojo2 = new TableWithIdentity();
        pojo2.setAge(21);
        pojo2.setName("CombinedInsertWithNullHints2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        dbShardDao.combinedInsert(new DalHints(), pojoList);
        assertEquals(1, dbShardDao.count(new DalHints().inShard(0)));
        assertEquals(1, dbShardDao.count(new DalHints().inShard(1)));
        assertNotEquals(1L, dbShardDao.queryBy(pojo1, null).get(0).getID().longValue());
        assertNotEquals(2L, dbShardDao.queryBy(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(20L);
        pojo1.setAge(null);
        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
        pojo2.setID(21L);
        pojo2.setAge(null);
        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
        dbShardDao.combinedInsert(new DalHints().enableIdentityInsert().inShard(0), pojoList);
        assertEquals(20L, dbShardDao.queryBy(pojo1, new DalHints().inShard(0)).get(0).getID().longValue());
        assertEquals(21L, dbShardDao.queryBy(pojo2, new DalHints().inShard(0)).get(0).getID().longValue());

//        setIdentityBack
        pojo1.setID(null);
        pojo1.setName("CombinedInsertWithSetIdentityBack1");
        pojo2.setID(null);
        pojo2.setName("CombinedInsertWithSetIdentityBack2");

        dbShardDao.combinedInsert(new DalHints().setIdentityBack().setShardValue(21), pojoList);
        assertNotNull(dbShardDao.queryBy(pojo1, new DalHints().setShardValue(21)).get(0));
        assertNotNull(dbShardDao.queryBy(pojo2, new DalHints().setShardValue(21)).get(0));
        assertNotNull(pojo1.getID());
        assertNotNull(pojo2.getID());

//        keyholder
        pojo1.setID(null);
        pojo1.setName("CombinedInsertWithKeyholder1");
        pojo2.setID(null);
        pojo2.setName("CombinedInsertWithKeyholder2");
        KeyHolder keyHolder = new KeyHolder();
        dbShardDao.combinedInsert(new DalHints().setShardColValue("age", 20), keyHolder, pojoList);

        assertNull(pojo1.getID());
        assertNull(pojo2.getID());
        assertEquals(dbShardDao.queryBy(pojo1, new DalHints().setShardColValue("age", 20)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
        assertEquals(dbShardDao.queryBy(pojo2, new DalHints().setShardColValue("age", 20)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
    }


    @Test
    public void testIDNotAGSingleInsert() throws Exception {
//        null hints, null id
        TableWithNoIdentity singlePojo = new TableWithNoIdentity();
        singlePojo.setAge(20);
        singlePojo.setName("TestSinglePojoInsertWithNullHints");
        try {
            idNotAGDao.insert(new DalHints(), singlePojo);
            fail();
        } catch (Exception e) {

        }

        //        null hints, not null id
        TableWithNoIdentity singlePojo1 = new TableWithNoIdentity();
        singlePojo1.setID(100L);
        singlePojo1.setAge(20);
        singlePojo1.setName("TestSinglePojoInsertWithNullHints1");
        try {
            idNotAGDao.insert(new DalHints(), singlePojo1);
        } catch (Exception e) {
            fail();
        }
        TableWithNoIdentity queryPojo1 = idNotAGDao.queryByPk(100L, null);
        assertEquals("TestSinglePojoInsertWithNullHints1", queryPojo1.getName());

//     hints.enableIdentityInsert
        TableWithNoIdentity singlePojo2 = new TableWithNoIdentity();
        singlePojo2.setID(1L);
        singlePojo2.setAge(20);
        singlePojo2.setName("TestSinglePojoInsertWithEnableIdentityInsert");
        idNotAGDao.insert(new DalHints().enableIdentityInsert(), singlePojo2);

        TableWithNoIdentity queryPojo = idNotAGDao.queryByPk(1L, null);
        assertEquals("TestSinglePojoInsertWithEnableIdentityInsert", queryPojo.getName());

//        hints.setIdentityBack
        TableWithNoIdentity singlePojo3 = new TableWithNoIdentity();
        singlePojo3.setID(20L);
        singlePojo3.setAge(20);
        singlePojo3.setName("TestSinglePojoInsertWithSetIdentityBack");
        idNotAGDao.insert(new DalHints().setIdentityBack(), singlePojo3);
        TableWithNoIdentity queryPojo2 = idNotAGDao.queryByPk(singlePojo3.getID(), null);
        assertEquals("TestSinglePojoInsertWithSetIdentityBack", queryPojo2.getName());
    }

    @Test
    public void testIdNotAGDaoBatchInsert() throws Exception {
//       null hints
        List<TableWithNoIdentity> pojoList = new ArrayList<>();
        TableWithNoIdentity pojo1 = new TableWithNoIdentity();
        pojo1.setName("BatchInsertWithNullHints1");

        TableWithNoIdentity pojo2 = new TableWithNoIdentity();
        pojo2.setName("BatchInsertWithNullHints2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        try {
            idNotAGDao.batchInsert(new DalHints(), pojoList);
            fail();
        } catch (Exception e) {

        }
        pojo1.setID(10L);
        pojo2.setID(20L);
        idNotAGDao.batchInsert(new DalHints(), pojoList);
        assertEquals(2, idNotAGDao.count(null));
        assertEquals(10L, idNotAGDao.queryLike(pojo1, null).get(0).getID().longValue());
        assertEquals(20L, idNotAGDao.queryLike(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(30L);
        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
        pojo2.setID(31L);
        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
        idNotAGDao.batchInsert(new DalHints().enableIdentityInsert(), pojoList);
        assertEquals(30L, idNotAGDao.queryLike(pojo1, null).get(0).getID().longValue());
        assertEquals(31L, idNotAGDao.queryLike(pojo2, null).get(0).getID().longValue());
    }

    @Test
    public void testIdNotAGDaoCombinedInsert() throws Exception {
//       null hints
        List<TableWithNoIdentity> pojoList = new ArrayList<>();
        TableWithNoIdentity pojo1 = new TableWithNoIdentity();
        pojo1.setName("CombinedInsertWithNullHints1");

        TableWithNoIdentity pojo2 = new TableWithNoIdentity();
        pojo2.setName("CombinedInsertWithNullHints2");

        pojoList.add(pojo1);
        pojoList.add(pojo2);

        try {
            idNotAGDao.combinedInsert(new DalHints(), pojoList);
            fail();
        } catch (Exception e) {

        }

        pojo1.setID(10L);
        pojo2.setID(20L);
        idNotAGDao.combinedInsert(new DalHints(), pojoList);
        assertEquals(2, idNotAGDao.count(null));
        assertEquals(10L, idNotAGDao.queryLike(pojo1, null).get(0).getID().longValue());
        assertEquals(20L, idNotAGDao.queryLike(pojo2, null).get(0).getID().longValue());

//        enableIdentityInsert
        pojo1.setID(30L);
        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
        pojo2.setID(31L);
        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
        idNotAGDao.combinedInsert(new DalHints().enableIdentityInsert(), pojoList);
        assertEquals(30L, idNotAGDao.queryLike(pojo1, null).get(0).getID().longValue());
        assertEquals(31L, idNotAGDao.queryLike(pojo2, null).get(0).getID().longValue());

//        setIdentityBack
        pojo1.setID(40L);
        pojo1.setName("CombinedInsertWithSetIdentityBack1");
        pojo2.setID(41L);
        pojo2.setName("CombinedInsertWithSetIdentityBack2");

        idNotAGDao.combinedInsert(new DalHints().setIdentityBack(), pojoList);
        assertNotNull(idNotAGDao.queryLike(pojo1, null).get(0));
        assertNotNull(idNotAGDao.queryLike(pojo2, null).get(0));
    }


    @Test
    public void testConcurrentSingleInsert() throws Exception {

//        for (int j = 0; j < 100; j++) {
//            final IdGenerator idGenerator = IdGeneratorFactory.getInstance().getOrCreateLongIdGenerator("testName1");
        final List<Long> idList = Collections.synchronizedList(new ArrayList<Long>());
        int requireSize = 1000;
        final CountDownLatch latch = new CountDownLatch(requireSize);
        final AtomicBoolean result = new AtomicBoolean(true);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < requireSize; i++) {
            executor.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TableWithIdentity pojo = new TableWithIdentity();
                        pojo.setName("testIdGenConcurrent");
                        dao.insert(new DalHints().setIdentityBack(), pojo);
                        assertEquals(1, dao.queryBy(pojo, null).size());
                        idList.add(pojo.getID().longValue());
                    } catch (Exception e) {
                        logger.info(e.toString());
                        result.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            }));
        }
        latch.await();
        Assert.assertTrue(result.get());
        long end = System.currentTimeMillis();
        long cost = end - start;
        int idSize = idList.size();
        logger.info(idSize + " cost " + cost + " ms");
        Assert.assertEquals(requireSize, idSize);
//            check duplicate
        Set<Long> idSet = new HashSet<>();
        idSet.addAll(idList);
        Assert.assertEquals(idSize, idSet.size());
//        }

    }


    @Test
    public void testConcurrentCombinedInsert() throws Exception {
        int requireSize = 1000;
        final CountDownLatch latch = new CountDownLatch(requireSize);
        final AtomicBoolean result = new AtomicBoolean(true);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
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
                        dao.combinedInsert(new DalHints().setIdentityBack(), list);
                        assertEquals(pojo1.getID().longValue(), dao.queryBy(pojo1, null).get(0).getID().longValue());
                        assertEquals(pojo2.getID().longValue(), dao.queryBy(pojo2, null).get(0).getID().longValue());
                        assertEquals("ConcurrentCombinedInsert1", dao.queryBy(pojo1, null).get(0).getName());
                        assertEquals("ConcurrentCombinedInsert2", dao.queryBy(pojo2, null).get(0).getName());
//                        idList.add(pojo.getID().longValue());
                    } catch (Exception e) {
                        logger.info(e.toString());
                        result.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            }));
        }
        latch.await();
        Assert.assertTrue(result.get());
        long end = System.currentTimeMillis();
        long cost = end - start;

        List<TableWithIdentity> ret = dao.queryAll(null);
        int idSize = ret.size();
        logger.info(idSize + " cost " + cost + " ms");
        Assert.assertEquals(requireSize * 2, idSize);

//            check duplicate
        Set<Long> idSet = new HashSet<>();
        for (TableWithIdentity pojo : ret)
            idSet.add(pojo.getID().longValue());
        Assert.assertEquals(idSize, idSet.size());
//        }

    }
    /*@Test
    public void testDalTableDaoQueryTop() throws Exception{
        Integer age = 20;
        Integer count = 2;
        List<TableWithIdentity> list = dao.queryTop(age, count, null);
        assertEquals(2, list.size());
    }

    @Test
    public void testDalTableDaoQueryFrom() throws Exception{
        Integer age=21;
        List<TableWithIdentity> list1=dao.queryFromWithOrderBy(age,0,3,null);
        assertEquals(3,list1.size());

        List<TableWithIdentity> list2=dao.queryFromWithOrderBy(age,1,3,null);
        assertEquals(2,list2.size());


        List<TableWithIdentity> list3= dao.queryFromWithoutOrderBy(age, 0, 3, null);
        assertEquals(3,list3.size());

        List<TableWithIdentity> list4=dao.queryFromWithoutOrderBy(age,1,3,null);
        assertEquals(2,list4.size());

    }


    @Test
    public void testCount() throws Exception {
        int affected = dao.count(new DalHints());
        assertEquals(6, affected);
    }

    @Test
    public void testDelete1() throws Exception {
        DalHints hints = new DalHints();
        TableWithIdentity daoPojo = new TableWithIdentity();
        daoPojo.setAge(20);

        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);
        int affected = dao.delete(hints, daoPojo);
        assertEquals(1, affected);
    }
//
//	@Test
//	public void testDelete2() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGen> daoPojos = dao.queryAll(null);
//		int[] affected = dao.delete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//
//	@Test
//	public void testBatchDelete() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonGen> daoPojos = dao.queryAll(null);
//		int[] affected = dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//	}
//
	@Test
	public void testQueryAll() throws Exception {
		List<TableWithIdentity> list = dao.queryAll(new DalHints().selectByNames());
		assertEquals(6, list.size());
	}

	@Test
    public void testQueryAllByPage() throws  Exception{
        List<TableWithIdentity> list=dao.queryAllByPage(1,3,new DalHints().selectByNames());
        assertEquals(3,list.size());
    }

    @Test
    public void testQueryByPK() throws Exception{
        TableWithIdentity ret=dao.queryByPk(1,new DalHints().selectByNames());
        assertNotNull(ret.getID());

        TableWithIdentity ret2=dao.queryByPk(ret,new DalHints().selectByNames());
        assertNotNull(ret2.getID());
    }

    @Test
    public void testQueryLike() throws Exception{
        TableWithIdentity sample=new TableWithIdentity();
        sample.setName("Initial_Shard_00");
        List<TableWithIdentity> list=dao.queryLike(sample,new DalHints().selectByNames());
        assertEquals(1,list.size());

        try {
            list.clear();
            TableWithIdentity nullFieldSample = new TableWithIdentity();
            list = dao.queryLike(nullFieldSample, new DalHints().selectByNames());
            assertEquals(6,list.size());
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }

        try {
            list.clear();
            list = dao.queryLike(null, new DalHints().selectByNames());
            fail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryBy() throws Exception{
        TableWithIdentity sample=new TableWithIdentity();
        sample.setName("Initial_Shard_00");
        List<TableWithIdentity> list=dao.queryBy(sample,new DalHints().selectByNames());
        assertEquals(1,list.size());

        try {
            list.clear();
            TableWithIdentity nullFieldSample = new TableWithIdentity();
            list = dao.queryBy(nullFieldSample, new DalHints().selectByNames());
            fail();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            list.clear();
            list = dao.queryBy(null, new DalHints().selectByNames());
            fail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert1() throws Exception {
        DalHints hints = new DalHints();
        TableWithIdentity daoPojo = new TableWithIdentity();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao.insert(hints.enableIdentityInsert(), daoPojo);
//		int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);


        TableWithIdentity ret = dao.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());
    }

    @Test
    public void testInsert1SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        TableWithIdentity daoPojo = new TableWithIdentity();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao.insert(hints.setIdentityBack(), daoPojo);
//		int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);
        assertEquals(7, daoPojo.getID().intValue());

        TableWithIdentity ret = dao.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());
    }

    @Test
    public void testInsert2SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();

        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
//			else
//				daoPojo.setName("Initial_Shard_1" + i);
            daoPojos.add(daoPojo);
        }

        int[] affected = dao.insert(hints.setIdentityBack(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
        for(int i=0;i<daoPojos.size();i++) {
            assertEquals(i + 7, daoPojos.get(i).getID().intValue());
        }
    }

    @Test
    public void testInsert2() throws Exception {
        DalHints hints = new DalHints();

        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
//			else
//				daoPojo.setName("Initial_Shard_1" + i);
            daoPojos.add(daoPojo);
        }

        int[] affected = dao.insert(hints.enableIdentityInsert(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);

        TableWithIdentity ret = dao.queryByPk(30, new DalHints());
        assertNotNull(ret);
    }


    @Test
    public void testInsert3() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        TableWithIdentity daoPojo = new TableWithIdentity();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");
        int affected = dao.insert(hints.enableIdentityInsert(), keyHolder, daoPojo);
        assertEquals(1, affected);
        assertEquals("insert", dao.queryByPk(10, new DalHints()).getName());
        assertEquals(1, keyHolder.size());
        assertEquals(10L, keyHolder.getKey());
    }

    @Test
    public void testInsert3SetIdentityBack() throws Exception {
        KeyHolder keyHolder = new KeyHolder();
        TableWithIdentity daoPojo = new TableWithIdentity();
        daoPojo.setAge(20);
        daoPojo.setName("insert");
        int affected = dao.insert(new DalHints().setIdentityBack(), keyHolder, daoPojo);
        assertEquals(1, affected);
        assertEquals("insert", dao.queryByPk(7, new DalHints()).getName());
        assertEquals(1, keyHolder.size());
        assertEquals(7L, keyHolder.getKey());
        assertEquals(7, daoPojo.getID().intValue());
    }

    @Test
    public void testInsert4() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
//			daoPojo.setID(20+i*2);
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
            else
                daoPojo.setName(null);
            daoPojos.add(daoPojo);
        }
        int[] affected = dao.insert(hints, keyHolder, daoPojos);

        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
        assertEquals(6, keyHolder.size());

        assertEquals(12L, keyHolder.getKey(5));
    }

    @Test
    public void testInsert4SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
            else
                daoPojo.setName(null);
            daoPojos.add(daoPojo);
        }
        int[] affected = dao.insert(hints.setIdentityBack(), keyHolder, daoPojos);

        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
        assertEquals(6, keyHolder.size());
        int i = 0;
        for (TableWithIdentity pojo : daoPojos)
            assertEquals(keyHolder.getKey(i++).intValue(), pojo.getID().intValue());
    }

    @Test
    public void testInsert5() throws Exception {
        DalHints hints = new DalHints();
        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
            else
                daoPojo.setName("Initial_Shard_1" + i);
            daoPojos.add(daoPojo);
        }
        int[] affected = dao.batchInsert(hints.enableIdentityInsert(), daoPojos);
        assertEquals(6, affected.length);

//		for(int i=0;i<affected.length;i++)
//			System.out.print(affected[i]+" ");
//		System.out.println();

        TableWithIdentity ret = dao.queryByPk(30, new DalHints());
        assertNotNull(ret);

    }

    @Test
    public void testNullInsert1() throws Exception {
        DalHints hints = new DalHints();
        TableWithIdentity daoPojo = new TableWithIdentity();
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);


        TableWithIdentity ret = dao.queryByPk(7, new DalHints());
        assertNotNull(ret);
        assertNotNull(ret.getBirth());
    }

    @Test
    public void testNullInsert2() throws Exception {
        DalHints hints = new DalHints();

        List<TableWithIdentity> daoPojos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
            daoPojos.add(daoPojo);
        }

        int[] affected = dao.insert(hints, daoPojos);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);

        daoPojos = dao.queryAll(hints);
        for (int i = 0; i < daoPojos.size(); i++) {
            assertNotNull(daoPojos.get(i).getBirth());
            if (i == 7 || i == 9 || i == 11)
                assertEquals("hello", daoPojos.get(i).getName());
        }
    }

    @Test
    public void testNullInsert5() throws Exception {
        DalHints hints = new DalHints();
        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);

            daoPojos.add(daoPojo);
        }
        int[] affected = dao.batchInsert(hints, daoPojos);
        assertEquals(6, affected.length);


        daoPojos = dao.queryAll(hints);
        for (int i = 0; i < daoPojos.size(); i++) {
            assertNotNull(daoPojos.get(i).getBirth());
            if (i == 7 || i == 9 || i == 11)
                assertNull(daoPojos.get(i).getName());
            else assertNotNull(daoPojos.get(i).getName());
        }

    }

    @Test
    public void testNullCombinedInsert1() throws Exception {
        DalHints hints = new DalHints();
        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);

            daoPojos.add(daoPojo);
        }
        int affected = dao.combinedInsert(hints, daoPojos);
        assertEquals(6, affected);

        daoPojos = dao.queryAll(hints);
        for (int i = 0; i < daoPojos.size(); i++) {
            assertNotNull(daoPojos.get(i).getBirth());
            if (i == 7 || i == 9 || i == 11)
                assertNull(daoPojos.get(i).getName());
            else assertNotNull(daoPojos.get(i).getName());
        }
    }


    @Test
    public void testCombinedInsert1() throws Exception {
        DalHints hints = new DalHints();
        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
            else
                daoPojo.setName("Initial_Shard_1" + i);
            daoPojos.add(daoPojo);
        }
        int affected = dao.combinedInsert(hints.enableIdentityInsert(), daoPojos);
        assertEquals(6, affected);

        TableWithIdentity ret = dao.queryByPk(30, new DalHints());
        assertNotNull(ret);
    }

    @Test
    public void testCombinedInsert1etIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
            else
                daoPojo.setName("Initial_Shard_1" + i);
            daoPojos.add(daoPojo);
        }
        int affected = dao.combinedInsert(hints.setIdentityBack(), daoPojos);
        assertEquals(6, affected);

        for(int i=0;i<daoPojos.size();i++) {
            assertEquals(i + 7, daoPojos.get(i).getID().intValue());
        }
    }

    @Test
    public void testCombinedInsert2() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            daoPojo.setName("Initial_" + i);
            daoPojos.add(daoPojo);
        }
        int affected = dao.combinedInsert(hints.enableIdentityInsert(), keyHolder, daoPojos);
        assertEquals(6, affected);

        assertEquals(6, keyHolder.size());
        TableWithIdentity ret = dao.queryByPk(30, new DalHints());
        assertNotNull(ret);
//		for(int i=0;i<keyHolder.size();i++) {
//			System.out.println(keyHolder.getKey(i));
//		}
//
//		assertEquals(30l, keyHolder.getKey(5));
    }

    @Test
    public void testCombinedInsert2SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<TableWithIdentity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            TableWithIdentity daoPojo = new TableWithIdentity();
            daoPojo.setAge(i + 20);
            daoPojo.setName("Initial_" + i);
            daoPojos.add(daoPojo);
        }
        int affected = dao.combinedInsert(hints.setIdentityBack(), keyHolder, daoPojos);
        assertEquals(6, affected);
        assertEquals(6, keyHolder.size());
        int i = 0;
        for (TableWithIdentity pojo : daoPojos) {
            assertEquals(pojo.getID().intValue(), keyHolder.getKey(i++).intValue());
        }
//		for(int i=0;i<keyHolder.size();i++) {
//			System.out.println(keyHolder.getKey(i));
//		}
//
//		assertEquals(30l, keyHolder.getKey(5));
    }

    @Test
    public void testQuery() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
        List<TableWithIdentity> list = dao.queryAll(null);
        assertEquals(6, list.size());
    }

    //
    @Test
    public void testQueryByPk1() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        TableWithIdentity affected = dao.queryByPk(id, hints);
        assertNotNull(affected);
    }

    //
    @Test
    public void testQueryByPk2() throws Exception {
        DalHints hints = new DalHints();
        TableWithIdentity daoPojo = new TableWithIdentity();
        daoPojo.setAge(20);

        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);
        TableWithIdentity affected = dao.queryByPk(daoPojo, hints);
        assertNotNull(affected);
    }

    //
    @Test
    public void testUpdate1() throws Exception {
        DalHints hints = new DalHints();
        TableWithIdentity daoPojo = new TableWithIdentity();
        daoPojo.setAge(20);

        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);

        int affected = dao.update(hints, daoPojo);
        assertEquals(1, affected);
//		daoPojo = dao.queryByPk(createPojo(1), null);
//		verifyPojo(daoPojo);
    }

    @Test
    public void testDirtyFlagUpdate1() throws Exception {
        DalHints hints = new DalHints();
        TableWithIdentity daoPojo = dao.queryByPk(1, hints);
        daoPojo.setName("updateDirtyFlag1");
        daoPojo.setID(2);

        int affected = dao.update(hints, daoPojo);
        assertEquals(1, affected);
        daoPojo = dao.queryByPk(2, hints);
        assertEquals("updateDirtyFlag1", daoPojo.getName());
        assertEquals(21, daoPojo.getAge().intValue());

        daoPojo = dao.queryByPk(3, hints);
        daoPojo.setName("updateDirtyFlag2");
        daoPojo.setID(4);

//		affected=dao.update(hints.updateUnchangedField(), daoPojo);
//		assertEquals(1, affected);
//		daoPojo = dao.queryByPk(4, hints);
//		assertEquals("updateDirtyFlag2", daoPojo.getName());
//		assertEquals(20, daoPojo.getAge().intValue());
    }

    @Test
    public void testDirtyFlagUpdate2() throws Exception {
        DalHints hints = new DalHints();
        List<TableWithIdentity> daoPojos = new ArrayList<>();
        TableWithIdentity daoPojo = dao.queryByPk(1, hints);
        daoPojo.setID(2);
        daoPojo.setName("updateDirtyFlag2");
        daoPojos.add(daoPojo);

        daoPojo = dao.queryByPk(3, hints);
        daoPojo.setID(4);
        daoPojo.setName("updateDirtyFlag4");
        daoPojos.add(daoPojo);

        int[] affected = dao.update(hints, daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected);
        daoPojo = dao.queryByPk(2, hints);
        assertEquals("updateDirtyFlag2", daoPojo.getName());
        assertEquals(21, daoPojo.getAge().intValue());

        daoPojo = dao.queryByPk(4, hints);
        assertEquals("updateDirtyFlag4", daoPojo.getName());
        assertEquals(21, daoPojo.getAge().intValue());

        daoPojos.get(0).setID(5);
        daoPojos.get(0).setName("updateDirtyFlag5");
        daoPojos.get(1).setID(6);
        daoPojos.get(1).setName("updateDirtyFlag6");

        affected = dao.update(hints.updateUnchangedField(), daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected);
        daoPojo = dao.queryByPk(5, hints);
        assertEquals("updateDirtyFlag5", daoPojo.getName());
        assertEquals(20, daoPojo.getAge().intValue());

        daoPojo = dao.queryByPk(6, hints);
        assertEquals("updateDirtyFlag6", daoPojo.getName());
        assertEquals(20, daoPojo.getAge().intValue());


    }

    @Test
    public void testDirtyFlagBatchUpdate2() throws Exception {
        DalHints hints = new DalHints();
        List<TableWithIdentity> daoPojos = new ArrayList<>();
        TableWithIdentity daoPojo = dao.queryByPk(1, hints);
        daoPojo.setID(2);
        daoPojo.setName("updateDirtyFlag2");
        daoPojos.add(daoPojo);

        daoPojo = dao.queryByPk(3, hints);
        daoPojo.setID(4);
        daoPojo.setName("updateDirtyFlag4");
        daoPojos.add(daoPojo);

        int[] affected = dao.batchUpdate(hints, daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected);
        daoPojo = dao.queryByPk(2, hints);
        assertEquals("updateDirtyFlag2", daoPojo.getName());
        assertEquals(21, daoPojo.getAge().intValue());

        daoPojo = dao.queryByPk(4, hints);
        assertEquals("updateDirtyFlag4", daoPojo.getName());
        assertEquals(21, daoPojo.getAge().intValue());

        daoPojos.get(0).setID(5);
        daoPojos.get(0).setName("updateDirtyFlag5");
        daoPojos.get(1).setID(6);
        daoPojos.get(1).setName("updateDirtyFlag6");

        affected = dao.batchUpdate(hints.updateUnchangedField(), daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected);
        daoPojo = dao.queryByPk(5, hints);
        assertEquals("updateDirtyFlag5", daoPojo.getName());
        assertEquals(20, daoPojo.getAge().intValue());

        daoPojo = dao.queryByPk(6, hints);
        assertEquals("updateDirtyFlag6", daoPojo.getName());
        assertEquals(20, daoPojo.getAge().intValue());


    }
//	@Test
//	public void testUpdate2() throws Exception {
//		DalHints hints = new DalHints();
//		List<TableWithIdentity> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.update(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
//	}

//	@Test
//	public void testBatchUpdate() throws Exception {
//		DalHints hints = new DalHints();
//		List<TableWithIdentity> daoPojos = dao.queryAll(new DalHints());
//		changePojos(daoPojos);
//		int[] affected = dao.batchUpdate(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
//		verifyPojos(dao.queryAll(new DalHints()));
//	}


    @Test
    public void testtest_def_update() throws Exception {
        dao.test_def_update(new DalHints());
        int ret = dao.count(new DalHints());
        assertEquals(0, ret);
    }

    @Test
    public void testtest_build_query_fieldList_multipleOrderBy() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<String> ret = dao.test_build_query_fieldList_multipleOrderBy(Age, null);
        assertEquals("Initial_Shard_11", ret.get(0));
        assertEquals("Initial_Shard_00", ret.get(3));
    }

    @Test
    public void testtest_build_query_fieldList_multipleOrderByReverse() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<String> ret = dao.test_build_query_fieldList_multipleOrderByReverse(Age, null);
        assertEquals("Initial_Shard_00", ret.get(0));
        assertEquals("Initial_Shard_13", ret.get(3));
    }

    @Test
    public void testtest_build_query_fieldListByPage_multipleOrderBy() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<String> ret = dao.test_build_query_fieldListByPage_multipleOrderBy(Age, 1, 3, null);
        assertEquals("Initial_Shard_11", ret.get(0));
    }

    @Test
    public void testtest_build_query_fieldListByPage_multipleOrderByReverse() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<String> ret = dao.test_build_query_fieldListByPage_multipleOrderByReverse(Age, 1, 3, null);
        assertEquals("Initial_Shard_00", ret.get(0));
    }

    @Test
    public void testtest_build_query_field_first_multipleOrderBy() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        String ret = dao.test_build_query_field_first_multipleOrderBy(Age, null);
        assertEquals("Initial_Shard_11", ret);
    }

    @Test
    public void testtest_build_query_field_first_multipleOrderByReverse() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        String ret = dao.test_build_query_field_first_multipleOrderByReverse(Age, null);
        assertEquals("Initial_Shard_00", ret);
    }

    @Test
    public void testtest_build_query_list_multipleOrderBy() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<TableWithIdentity> ret = dao.test_build_query_list_multipleOrderBy(Age, null);
        assertEquals("Initial_Shard_11", ret.get(0).getName());
    }

    @Test
    public void testtest_build_query_list_multipleOrderByReverse() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<TableWithIdentity> ret = dao.test_build_query_list_multipleOrderByReverse(Age, null);
        assertEquals("Initial_Shard_00", ret.get(0).getName());
    }

    @Test
    public void testtest_build_queryPartial_list_multipleOrderBy() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<TableWithIdentity> ret = dao.test_build_queryPartial_list_multipleOrderBy(Age, null);
        assertEquals("Initial_Shard_11", ret.get(0).getName());
    }

    @Test
    public void testtest_build_queryPartial_list_multipleOrderByReverse() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<TableWithIdentity> ret = dao.test_build_queryPartial_list_multipleOrderByReverse(Age, new DalHints().set(DalHintEnum.fetchSize, 1000));
        assertEquals("Initial_Shard_00", ret.get(0).getName());
    }

    @Test
    public void testtest_build_query_listByPage_multipleOrderBy() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<TableWithIdentity> ret = dao.test_build_query_listByPage_multipleOrderBy(Age, 2, 3, null);
        assertEquals("Initial_Shard_00", ret.get(0).getName());
    }

    @Test
    public void testtest_build_query_listByPage_multipleOrderByReverse() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<TableWithIdentity> ret = dao.test_build_query_listByPage_multipleOrderByReverse(Age, 1, 3, null);
        assertEquals("Initial_Shard_00", ret.get(0).getName());
    }

    @Test
    public void testtest_build_queryPartial_listByPage_multipleOrderBy() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<TableWithIdentity> ret = dao.test_build_queryPartial_listByPage_multipleOrderBy(Age, 2, 3, null);
        assertEquals("Initial_Shard_00", ret.get(0).getName());
    }

    @Test
    public void testtest_build_queryPartial_listByPage_multipleOrderByReverse() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        List<TableWithIdentity> ret = dao.test_build_queryPartial_listByPage_multipleOrderByReverse(Age, 2, 3, null);
        assertEquals("Initial_Shard_13", ret.get(0).getName());
    }

    @Test
    public void testtest_build_query_first_multipleOrderBy() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        TableWithIdentity daoPojo = dao.test_build_query_first_multipleOrderBy(Age, new DalHints());
        assertEquals("Initial_Shard_11", daoPojo.getName());
    }

    @Test
    public void testtest_build_query_first_multipleOrderByReverse() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        TableWithIdentity daoPojo = dao.test_build_query_first_multipleOrderByReverse(Age, new DalHints());
        assertEquals("Initial_Shard_00", daoPojo.getName());
    }


    @Test
    public void testtest_build_queryPartial_first_multipleOrderBy() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        TableWithIdentity daoPojo = dao.test_build_queryPartial_first_multipleOrderBy(Age, new DalHints());
        assertEquals("Initial_Shard_11", daoPojo.getName());
    }

    @Test
    public void testtest_build_queryPartial_first_multipleOrderByReverse() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(21);
        TableWithIdentity daoPojo = dao.test_build_queryPartial_first_multipleOrderByReverse(Age, new DalHints());
        assertEquals("Initial_Shard_00", daoPojo.getName());
    }

    @Test
    public void testBuildQueryLikeWithMatchPattern() throws Exception {
        List<TableWithIdentity> ret = dao.testBuildQueryLikeWithMatchPattern("_00", MatchPattern.END_WITH, null);
        assertEquals(1, ret.size());
        ret = dao.testBuildQueryLikeWithMatchPattern("Init", MatchPattern.BEGIN_WITH, null);
        assertEquals(6, ret.size());
        ret = dao.testBuildQueryLikeWithMatchPattern("Shard", MatchPattern.CONTAINS, null);
        assertEquals(6, ret.size());
        ret = dao.testBuildQueryLikeWithMatchPattern("Shard", MatchPattern.USER_DEFINED, null);
        assertEquals(0, ret.size());
    }

    @Test
    public void testBuildQueryLikeNullableWithMatchPattern() throws Exception {
        List<TableWithIdentity> ret = dao.testBuildQueryLikeNullableWithMatchPattern("_00", MatchPattern.END_WITH, null);
        assertEquals(1, ret.size());
        ret = dao.testBuildQueryLikeNullableWithMatchPattern("Init", MatchPattern.BEGIN_WITH, null);
        assertEquals(6, ret.size());
        ret = dao.testBuildQueryLikeNullableWithMatchPattern("Shard", MatchPattern.CONTAINS, null);
        assertEquals(6, ret.size());
        ret = dao.testBuildQueryLikeNullableWithMatchPattern("Shard", MatchPattern.USER_DEFINED, null);
        assertEquals(0, ret.size());
        ret = dao.testBuildQueryLikeNullableWithMatchPattern(null, MatchPattern.CONTAINS, null);
        assertEquals(6, ret.size());
    }

    @Test
    public void test_def_query() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        List<TableWithIdentity> daoPojos = dao.test_def_query(Age, null);
        assertEquals(0, daoPojos.size());

        Age.add(20);
        daoPojos = dao.test_def_query(Age, null);
        assertEquals(3, daoPojos.size());
    }

    @Test
    public void test_def_partialQuery() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        List<TableWithIdentity> daoPojos = dao.test_def_partialQuery(Age, null);
        assertEquals(0, daoPojos.size());

        Age.add(20);
        daoPojos = dao.test_def_partialQuery(Age, null);
        assertEquals(3, daoPojos.size());
        assertEquals("Initial_Shard_00", daoPojos.get(0).getName());
        assertNull(daoPojos.get(0).getAge());
    }

    @Test
    public void test_def_queryForObject() throws Exception {
        List<Integer> ID = new ArrayList<>();
        ID.add(3);
        ID.add(30);
        TableWithIdentity daoPojos = dao.test_def_queryForObject(ID, null);
        assertEquals("Initial_Shard_02", daoPojos.getName());
    }

    @Test
    public void test_def_partialQueryForObject() throws Exception {
        List<Integer> ID = new ArrayList<>();
        ID.add(3);
        ID.add(30);
        TableWithIdentity daoPojos = dao.test_def_partialQueryForObject(ID, null);
        assertEquals("Initial_Shard_02", daoPojos.getName());
        assertNull(daoPojos.getAge());
    }

    @Test
    public void test_def_queryForObjectNullable() throws Exception {
        List<Integer> ID = new ArrayList<>();
        ID.add(0);
        ID.add(30);
        TableWithIdentity daoPojos = dao.test_def_queryForObjectNullable(ID, null);
        assertNull(daoPojos);

        ID.add(3);
        daoPojos = dao.test_def_queryForObjectNullable(ID, null);
        assertEquals("Initial_Shard_02", daoPojos.getName());
    }

    @Test
    public void test_def_partialQueryForObjectNullable() throws Exception {
        List<Integer> ID = new ArrayList<>();
        ID.add(0);
        ID.add(30);
        TableWithIdentity daoPojos = dao.test_def_partialQueryForObjectNullable(ID, null);
        assertNull(daoPojos);

        ID.add(3);
        daoPojos = dao.test_def_partialQueryForObjectNullable(ID, null);
        assertEquals("Initial_Shard_02", daoPojos.getName());
        assertNull(daoPojos.getAge());
    }

    @Test
    public void test_def_queryFirst() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(31);
        TableWithIdentity daoPojos = dao.test_def_queryFirst(Age, null);
        assertEquals("Initial_Shard_04", daoPojos.getName());
    }

    @Test
    public void test_def_partialQueryFirst() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(20);
        Age.add(31);
        TableWithIdentity daoPojos = dao.test_def_partialQueryFirst(Age, null);
        assertEquals("Initial_Shard_04", daoPojos.getName());
        assertNull(daoPojos.getAge());
    }

    @Test
    public void test_def_queryFirstNullable() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        TableWithIdentity daoPojos = dao.test_def_queryFirstNullable(Age, null);
        assertNull(daoPojos);

        Age.add(20);
        daoPojos = dao.test_def_queryFirstNullable(Age, null);
        assertEquals("Initial_Shard_04", daoPojos.getName());
    }

    @Test
    public void test_def_partialQueryFirstNullable() throws Exception {
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        TableWithIdentity daoPojos = dao.test_def_partialQueryFirstNullable(Age, null);
        assertNull(daoPojos);

        Age.add(20);
        daoPojos = dao.test_def_partialQueryFirstNullable(Age, null);
        assertEquals("Initial_Shard_04", daoPojos.getName());
        assertNull(daoPojos.getAge());
    }

    @Test
    public void test_def_queryTop() throws Exception {
        int count = 3;
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        List<TableWithIdentity> daoPojos = dao.test_def_queryTop(Age, null, count);
        assertEquals(0, daoPojos.size());

        Age.add(20);
        daoPojos = dao.test_def_queryTop(Age, null, count);
        assertEquals("Initial_Shard_04", daoPojos.get(0).getName());
    }

    @Test
    public void test_def_partialQueryTop() throws Exception {
        int count = 3;
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        List<TableWithIdentity> daoPojos = dao.test_def_partialQueryTop(Age, null, count);
        assertEquals(0, daoPojos.size());

        Age.add(20);
        daoPojos = dao.test_def_partialQueryTop(Age, null, count);
        assertEquals("Initial_Shard_04", daoPojos.get(0).getName());
        assertNull(daoPojos.get(0).getAge());
    }

    @Test
    public void test_def_queryFrom() throws Exception {
        int start = 1;
        int count = 2;
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        List<TableWithIdentity> daoPojos = dao.test_def_queryFrom(Age, null, start, count);
        assertEquals(0, daoPojos.size());

        Age.add(20);
        daoPojos = dao.test_def_queryFrom(Age, null, start, count);
        assertEquals("Initial_Shard_02", daoPojos.get(0).getName());
    }

    @Test
    public void testTableBuilderParameterIndex() throws Exception{
        int start=0;
        int count=2;
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        Age.add(20);
        List<TableWithIdentity> daoPojos=dao.testTableBuilderParameterIndex("Initial_Shard_02",Age,3,null,start,count);
        assertEquals(1,daoPojos.size());
    }

    @Test
    public void testTableBuilderParameterIndexNoIn() throws Exception{
        int start=0;
        int count=2;
        Integer Age = 20;
        List<TableWithIdentity> daoPojos=dao.testTableBuilderParameterIndexNoIn("Initial_Shard_02",Age,3,null,start,count);
        assertEquals(1,daoPojos.size());
    }

    @Test
    public void test_def_update_ParameterIndex() throws Exception{
        Integer Age = 20;
        int ret=dao.test_def_update_ParameterIndex("Initial_Shard_02",Age,3,null);
        assertEquals(1,ret);
        assertEquals(5,dao.count(null));
    }

    @Test
    public void testColumnParameter() throws Exception{
        String columnName="Age";
        List<TableWithIdentity> ret=dao.testColumnParameter(columnName,null);
        assertEquals(6,ret.size());
    }


    @Test
    public void test_def_update_InParameterIndex() throws Exception{
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        Age.add(20);
        int ret=dao.test_def_update_InParameterIndex("Initial_Shard_02",Age,3,null);
        assertEquals(1,ret);
        assertEquals(5,dao.count(null));
    }

    @Test
    public void testFreeSqlBuilderParameterIndex() throws Exception{
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        Age.add(20);
        List<TableWithIdentity> daoPojos=dao.testFreeSqlBuilderParameterIndex("Initial_Shard_02",Age,3,null);
        assertEquals(1,daoPojos.size());
    }
    @Test
    public void testFreeSqlBuilderMultipleInParams() throws Exception{
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        Age.add(20);

        List<Integer> ID = new ArrayList<>();
        ID.add(1);
        ID.add(2);
        ID.add(3);

        List<TableWithIdentity> daoPojos=dao.testFreeSqlBuilderMultipleInParams("Initial_Shard_02",Age,ID,null);
        assertEquals(1,daoPojos.size());
    }

    @Test
    public void testFreeSqlBuilderWithDuplicateParameterIndex() throws Exception{
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        Age.add(20);
        try{
        List<TableWithIdentity> daoPojos=dao.testFreeSqlBuilderWithDuplicateParameterIndex("Initial_Shard_02",Age,3,null);
        fail();
        assertEquals(1,daoPojos.size());
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    @Test
    public void testFreeSqlBuilderWithDiscontinuedParameterIndex() throws Exception{
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        Age.add(20);
        List<TableWithIdentity> daoPojos=dao.testFreeSqlBuilderWithDiscontinuedParameterIndex("Initial_Shard_02",Age,3,null);
        assertEquals(1,daoPojos.size());
    }


    @Test
    public void testFreeSqlBuilderParameterIndexNotIn() throws Exception{
        Integer Age =20;
        List<TableWithIdentity> daoPojos=dao.testFreeSqlBuilderParameterIndexNotIn("Initial_Shard_02",Age,3,null);
        assertEquals(1,daoPojos.size());
    }

    @Test
    public void testFreeSqlBuilderWithNoParameters() throws Exception{
        List<TableWithIdentity> daoPojos=dao.testFreeSqlBuilderWithNoParameter(null);
        assertEquals(1,daoPojos.size());
    }

    @Test
    public void test_def_partialQueryFrom() throws Exception {
        int start = 1;
        int count = 2;
        List<Integer> Age = new ArrayList<>();
        Age.add(30);
        Age.add(31);
        List<TableWithIdentity> daoPojos = dao.test_def_partialQueryFrom(Age, null, start, count);
        assertEquals(0, daoPojos.size());

        Age.add(20);
        daoPojos = dao.test_def_partialQueryFrom(Age, null, start, count);
        assertEquals("Initial_Shard_02", daoPojos.get(0).getName());
        assertNull(daoPojos.get(0).getAge());
    }

    @Test
    public void test_countWhereCondition() throws Exception {

        String name = "Initial";
        int count = dao.countWhereCondition(name, new DalHints());
        assertEquals(6, count);
    }

    @Test
    public void testTransPass() throws Exception {

        DalCommand command = new DalCommand() {

            @Override
            public boolean execute(DalClient client) throws SQLException {
                TableWithIdentity ret = dao.queryByPk(1, new DalHints());
                ret.setAge(1000);
                dao.update(new DalHints(), ret);
                return true;
            }
        };
        try {
            client.execute(command, new DalHints());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1000, dao.queryByPk(1, new DalHints()).getAge().intValue());
    }

    @Test
    public void testTransFail() throws Exception {
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                try {
                    TableWithIdentity pojo = new TableWithIdentity();
                    pojo.setID(2);
                    dao.delete(new DalHints(), pojo);
                    TableWithIdentity ret = dao.queryByPk(1, new DalHints());
                    ret.setAge(2000);
                    ret.setID(3); //插入已存在的主鍵3，造成主鍵衝突
                    dao.insert(new DalHints().enableIdentityInsert(), ret);
                }catch (Exception e){
                    throw e;
                }
                return true;
            }
        };
        try {
            client.execute(command, new DalHints());
        } catch (Exception e) {
//			e.printStackTrace();
        }
        assertEquals(21, dao.queryByPk(2, new DalHints()).getAge().intValue());
        assertEquals(6, dao.count(new DalHints()));
    }

    @Test
    public void testTransCommandsFail() throws SQLException {
        List<DalCommand> cmds = new LinkedList<>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                TableWithIdentity ret = dao.queryByPk(1, new DalHints());
                ret.setAge(1000);
                dao.update(new DalHints(), ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                TableWithIdentity pojo = new TableWithIdentity();
                pojo.setID(2);
                dao.delete(new DalHints(), pojo);
                TableWithIdentity ret = dao.queryByPk(1, new DalHints());
                ret.setAge(2000);
                ret.setID(3);
                dao.insert(new DalHints().enableIdentityInsert(), ret);//主键冲突
                return true;
            }
        });

        try {
            client.execute(cmds, new DalHints());
        } catch (Exception e) {
//			e.printStackTrace();
        }

        assertEquals(20, dao.queryByPk(1, new DalHints()).getAge().intValue());
        assertEquals(21, dao.queryByPk(2, new DalHints()).getAge().intValue());
        assertEquals(6, dao.count(new DalHints()));

    }

    @Test
    public void testTransCommandsPass() throws SQLException {
        List<DalCommand> cmds = new LinkedList<>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                TableWithIdentity ret = dao.queryByPk(1, new DalHints());
                ret.setAge(1000);
                dao.update(new DalHints(), ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                TableWithIdentity pojo = new TableWithIdentity();
                pojo.setID(2);
                dao.delete(new DalHints(), pojo);
                TableWithIdentity ret = dao.queryByPk(1, new DalHints());
                ret.setAge(2000);
                ret.setID(7);
                dao.insert(new DalHints().enableIdentityInsert(), ret);
                return true;
            }
        });

        try {
            client.execute(cmds, new DalHints());

        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(1000, dao.queryByPk(1, new DalHints()).getAge().intValue());
        assertNull(dao.queryByPk(2, new DalHints()));
        assertEquals(2000, dao.queryByPk(7, new DalHints()).getAge().intValue());
        assertEquals(6, dao.count(new DalHints()));

    }

    @Test
    public void testTransCommandsPassDepand() throws SQLException {
        final KeyHolder key = new KeyHolder();
        List<DalCommand> cmds = new LinkedList<>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                TableWithIdentity ret = dao.queryByPk(1, new DalHints());
                dao.insert(new DalHints(), key, ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                TableWithIdentity pojo = new TableWithIdentity();
                pojo.setID(key.getKey().intValue());
                dao.delete(new DalHints(), pojo);
                TableWithIdentity ret = dao.queryByPk(1,
                        new DalHints());
                ret.setAge(2000);
                ret.setID(8);
                dao.insert(new DalHints().enableIdentityInsert(), ret);
                return true;
            }
        });

        try {
            client.execute(cmds, new DalHints());

        } catch (Exception e) {

            e.printStackTrace();

        }

        assertEquals(7, dao.count(new DalHints()));
        assertEquals(2000, dao.queryByPk(8, new DalHints()).getAge().intValue());

    }

    @Test
    public void testTransCommandsFailDepand() throws SQLException {
        final KeyHolder key = new KeyHolder();
        List<DalCommand> cmds = new LinkedList<>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                TableWithIdentity ret = dao.queryByPk(1,
                        new DalHints());
//			ret.setAge(1000);
//			dao.update(new DalHints(), ret);

                dao.insert(new DalHints(), key, ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                TableWithIdentity pojo = new TableWithIdentity();
                pojo.setID(key.getKey().intValue());
                dao.delete(new DalHints(), pojo);
                TableWithIdentity ret = dao.queryByPk(1,
                        new DalHints());
                ret.setAge(2000);
                ret.setID(2);
                dao.insert(new DalHints().enableIdentityInsert(), ret);
                return true;
            }
        });

        try {
            client.execute(cmds, new DalHints());
        } catch (SQLException e) {
//			e.printStackTrace();
        }


//		assertEquals(1000, dao.queryByPk(1, new DalHints()).getAge().intValue());
//		assertNull(dao.queryByPk(2, new DalHints()));
//		assertEquals(2000, dao.queryByPk(4, new DalHints()).getAge().intValue());
        assertEquals(6, dao.count(new DalHints()));
//		assertEquals(2000, dao.queryByPk(5, new DalHints()).getAge().intValue());
    }*/

}
