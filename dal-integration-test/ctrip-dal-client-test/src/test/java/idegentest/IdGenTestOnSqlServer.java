//package idegentest;
//
//
//import IDAutoGenerator.AutoGenIDDao;
//import IDAutoGenerator.TableWithIdentity;
//import IDNotAotuGenerator.NoAutoGenIDDao;
//import IDNotAotuGenerator.TableWithNoIdentity;
//import com.ctrip.platform.dal.dao.DalHints;
//import com.ctrip.platform.dal.dao.KeyHolder;
//import org.junit.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//
///**
// * JUnit test of PersonGenDao class.
// * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
// **/
//public class IdGenTestOnSqlServer {
//    private static AutoGenIDDao dao = null;
//    private static AutoGenIDDao tableShardDao = null;
//    private static AutoGenIDDao dbShardDao = null;
//    private static AutoGenIDDao dbTableShardDao = null;
//    private static NoAutoGenIDDao idNotAGDao = null;
//    protected static String NO_SHARD_DAO = "noShardTestOnSqlServer";
//    protected static String TABLE_SHARD_DAO = "ShardColModShardByTableOnSqlServer";
//    protected static String DB_SHARD_DAO = "ShardColModShardByDBOnSqlServer";
//    protected static String DB_TABLE_SHARD_DAO = "SimpleShardByDBTableOnSqlServer";
//
//
//    @BeforeClass
//    public static void setUpBeforeClass() throws Exception {
//        dao = new AutoGenIDDao(NO_SHARD_DAO);
//        tableShardDao = new AutoGenIDDao(TABLE_SHARD_DAO);
//        dbShardDao = new AutoGenIDDao(DB_SHARD_DAO);
//        dbTableShardDao = new AutoGenIDDao(DB_TABLE_SHARD_DAO);
//        idNotAGDao = new NoAutoGenIDDao();
//    }
//
//    @AfterClass
//    public static void tearDownAfterClass() throws Exception {
//
//    }
//
//    @Before
//    public void setUp() throws Exception {
//        dao.test_def_update(new DalHints());
//        dao.test_def_update(new DalHints(), "0");
//        dao.test_def_update(new DalHints(), "1");
//        dbShardDao.test_def_update(new DalHints().inShard(0));
//        dbShardDao.test_def_update(new DalHints().inShard(1));
//        dbTableShardDao.test_def_update(new DalHints().inShard(0), "0");
//        dbTableShardDao.test_def_update(new DalHints().inShard(0), "1");
//        dbTableShardDao.test_def_update(new DalHints().inShard(1), "0");
//        dbTableShardDao.test_def_update(new DalHints().inShard(1), "1");
//        idNotAGDao.test_def_update(new DalHints());
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void testSingleInsert() throws Exception {
////        null hints
//        TableWithIdentity singlePojo = new TableWithIdentity();
//        singlePojo.setAge(20);
//        singlePojo.setName("TestSinglePojoInsertWithNullHints");
//        dao.insert(new DalHints(), singlePojo);
//
//        List<TableWithIdentity> queryPojos = dao.queryBy(singlePojo, null);
//        assertEquals(1, queryPojos.size());
//        assertNotEquals(1L, queryPojos.get(0).getID().longValue());
//
////     hints.enableIdentityInsert
//        TableWithIdentity singlePojo2 = new TableWithIdentity();
//        singlePojo2.setID(1L);
//        singlePojo2.setAge(20);
//        singlePojo2.setName("TestSinglePojoInsertWithEnableIdentityInsert");
//        dao.insert(new DalHints().enableIdentityInsert(), singlePojo2);
//
//        TableWithIdentity queryPojo = dao.queryByPk(1L, null);
//        assertEquals("TestSinglePojoInsertWithEnableIdentityInsert", queryPojo.getName());
//
////        hints.setIdentityBack
//        TableWithIdentity singlePojo3 = new TableWithIdentity();
//        singlePojo3.setAge(20);
//        singlePojo3.setName("TestSinglePojoInsertWithSetIdentityBack");
//        dao.insert(new DalHints().setIdentityBack(), singlePojo3);
//        TableWithIdentity queryPojo2 = dao.queryByPk(singlePojo3.getID(), null);
//        assertEquals("TestSinglePojoInsertWithSetIdentityBack", queryPojo2.getName());
//
//        //        keyholder
//        TableWithIdentity singlePojo4 = new TableWithIdentity();
//        singlePojo4.setAge(20);
//        singlePojo4.setName("TestSinglePojoInsertWithKeyholder");
//        KeyHolder keyholder = new KeyHolder();
//        dao.insert(new DalHints().setIdentityBack(), keyholder, singlePojo4);
//        TableWithIdentity queryPojo3 = dao.queryByPk(singlePojo4.getID(), null);
//        assertEquals("TestSinglePojoInsertWithKeyholder", queryPojo3.getName());
//        assertEquals(singlePojo4.getID(), keyholder.getKey());
//    }
//
//    @Test
//    public void testTableShardSingleInsert() throws Exception {
////        null hints
//        TableWithIdentity singlePojo = new TableWithIdentity();
//        singlePojo.setAge(20);
//        singlePojo.setName("TestTableShardSinglePojoInsertWithNullHints");
//        tableShardDao.insert(new DalHints(), singlePojo);
//
//        List<TableWithIdentity> queryPojos = tableShardDao.queryBy(singlePojo, null);
//        assertEquals(1, queryPojos.size());
//        assertNotEquals(1L, queryPojos.get(0).getID().longValue());
//
////     hints.enableIdentityInsert
//        TableWithIdentity singlePojo2 = new TableWithIdentity();
//        singlePojo2.setID(1L);
//        singlePojo2.setName("TestTableShardWithEnableIdentityInsert");
//        tableShardDao.insert(new DalHints().enableIdentityInsert().inTableShard(1), singlePojo2);
//
//        TableWithIdentity queryPojo = tableShardDao.queryByPk(1L, new DalHints().inTableShard(1));
//        assertEquals("TestTableShardWithEnableIdentityInsert", queryPojo.getName());
//
////        hints.setIdentityBack
//        TableWithIdentity singlePojo3 = new TableWithIdentity();
//        singlePojo3.setName("TestTableShardWithSetIdentityBack");
//        tableShardDao.insert(new DalHints().setIdentityBack().setTableShardValue(22), singlePojo3);
//        TableWithIdentity queryPojo2 = tableShardDao.queryByPk(singlePojo3.getID(), new DalHints().inTableShard(0));
//        assertEquals("TestTableShardWithSetIdentityBack", queryPojo2.getName());
//
//        //        keyholder
//        TableWithIdentity singlePojo4 = new TableWithIdentity();
//        singlePojo4.setName("TestTableShardSinglePojoInsertWithKeyholder");
//        KeyHolder keyholder = new KeyHolder();
//        tableShardDao.insert(new DalHints().setIdentityBack().setShardColValue("age", 21), keyholder, singlePojo4);
//        TableWithIdentity queryPojo3 = tableShardDao.queryByPk(singlePojo4.getID(), new DalHints().inTableShard(1));
//        assertEquals("TestTableShardSinglePojoInsertWithKeyholder", queryPojo3.getName());
//        assertEquals(singlePojo4.getID(), keyholder.getKey());
//    }
//
//    @Test
//    public void testDBTableShardSingleInsert() throws Exception {
////        null hints
//        TableWithIdentity singlePojo = new TableWithIdentity();
//        singlePojo.setAge(20);
//        singlePojo.setName("TestDBTableShardSingleInsertWithNullHints");
//        dbTableShardDao.insert(new DalHints().inShard(0).inTableShard(0), singlePojo);
//
//        List<TableWithIdentity> queryPojos = dbTableShardDao.queryBy(singlePojo, new DalHints().inShard(0).inTableShard(0));
//        assertEquals(1, queryPojos.size());
//        assertNotEquals(1L, queryPojos.get(0).getID().longValue());
//
////     hints.enableIdentityInsert
//        TableWithIdentity singlePojo2 = new TableWithIdentity();
//        singlePojo2.setID(1L);
//        singlePojo2.setName("TestDBTableShardWithEnableIdentityInsert");
//        dbTableShardDao.insert(new DalHints().enableIdentityInsert().inShard(0).inTableShard(1), singlePojo2);
//
//        TableWithIdentity queryPojo = dbTableShardDao.queryByPk(1L, new DalHints().inShard(0).inTableShard(1));
//        assertEquals("TestDBTableShardWithEnableIdentityInsert", queryPojo.getName());
//
////        hints.setIdentityBack
//        TableWithIdentity singlePojo3 = new TableWithIdentity();
//        singlePojo3.setName("TestDBTableShardWithSetIdentityBack");
//        dbTableShardDao.insert(new DalHints().setIdentityBack().inShard(1).inTableShard(0), singlePojo3);
//        TableWithIdentity queryPojo2 = dbTableShardDao.queryByPk(singlePojo3.getID(), new DalHints().inShard(1).inTableShard(0));
//        assertEquals("TestDBTableShardWithSetIdentityBack", queryPojo2.getName());
//
//        //        keyholder
//        TableWithIdentity singlePojo4 = new TableWithIdentity();
//        singlePojo4.setName("TestDBTableShardWithKeyholder");
//        KeyHolder keyholder = new KeyHolder();
//        dbTableShardDao.insert(new DalHints().setIdentityBack().inShard(1).inTableShard(1), keyholder, singlePojo4);
//        TableWithIdentity queryPojo3 = dbTableShardDao.queryByPk(singlePojo4.getID(), new DalHints().inShard(1).inTableShard(1));
//        assertEquals("TestDBTableShardWithKeyholder", queryPojo3.getName());
//        assertEquals(singlePojo4.getID(), keyholder.getKey());
//    }
//
//    @Test
//    public void testDBShardSingleInsert() throws Exception {
////        null hints
//        TableWithIdentity singlePojo = new TableWithIdentity();
//        singlePojo.setAge(20);
//        singlePojo.setName("TestDBShardSinglePojoInsertWithNullHints");
//        dbShardDao.insert(new DalHints(), singlePojo);
//
//        List<TableWithIdentity> queryPojos = dbShardDao.queryBy(singlePojo, null);
//        assertEquals(1, queryPojos.size());
//        assertNotEquals(1L, queryPojos.get(0).getID().longValue());
//
////     hints.enableIdentityInsert
//        TableWithIdentity singlePojo2 = new TableWithIdentity();
//        singlePojo2.setID(1L);
//        singlePojo2.setName("TestDBShardWithEnableIdentityInsert");
//        dbShardDao.insert(new DalHints().enableIdentityInsert().inShard(1), singlePojo2);
//
//        TableWithIdentity queryPojo = dbShardDao.queryByPk(1L, new DalHints().inShard(1));
//        assertEquals("TestDBShardWithEnableIdentityInsert", queryPojo.getName());
//
////        hints.setIdentityBack
//        TableWithIdentity singlePojo3 = new TableWithIdentity();
//        singlePojo3.setName("TestDBShardWithSetIdentityBack");
//        dbShardDao.insert(new DalHints().setIdentityBack().setShardValue(22), singlePojo3);
//        TableWithIdentity queryPojo2 = dbShardDao.queryByPk(singlePojo3.getID(), new DalHints().inShard(0));
//        assertEquals("TestDBShardWithSetIdentityBack", queryPojo2.getName());
//
//        //        keyholder
//        TableWithIdentity singlePojo4 = new TableWithIdentity();
//        singlePojo4.setName("TestDBShardSinglePojoInsertWithKeyholder");
//        KeyHolder keyholder = new KeyHolder();
//        dbShardDao.insert(new DalHints().setIdentityBack().setShardColValue("age", 21), keyholder, singlePojo4);
//        TableWithIdentity queryPojo3 = dbShardDao.queryByPk(singlePojo4.getID(), new DalHints().inShard(1));
//        assertEquals("TestDBShardSinglePojoInsertWithKeyholder", queryPojo3.getName());
//        assertEquals(singlePojo4.getID(), keyholder.getKey());
//    }
//
//    @Test
//    public void testSingleInsertList() throws Exception {
////        null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setName("testSingleInsertList1");
//
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setName("testSingleInsertList2");
//
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        dao.insert(new DalHints(), pojoList);
//        assertEquals(2, dao.count(null));
//        assertNotEquals(1L, dao.queryBy(pojo1, null).get(0).getID().longValue());
//        assertNotEquals(2L, dao.queryBy(pojo2, null).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(3L);
//        pojo1.setName("testEnableIdentityInsertList1");
//        pojo2.setID(4L);
//        pojo2.setName("testEnableIdentityInsertList2");
//
//        dao.insert(new DalHints().enableIdentityInsert(), pojoList);
//        assertEquals("testEnableIdentityInsertList1", dao.queryByPk(3L, null).getName());
//        assertEquals("testEnableIdentityInsertList2", dao.queryByPk(4L, null).getName());
//
////        setIdentityBack
//        pojo1.setID(null);
//        pojo1.setName("testSetIdentityBackInsertList1");
//        pojo2.setID(null);
//        pojo2.setName("testSetIdentityBackInsertList2");
//
//        dao.insert(new DalHints().setIdentityBack(), pojoList);
//        assertNotNull(dao.queryBy(pojo1, null).get(0));
//        assertNotNull(dao.queryBy(pojo2, null).get(0));
//        assertNotNull(pojo1.getID());
//        assertNotNull(pojo2.getID());
//
////        keyholder
//        pojo1.setID(null);
//        pojo1.setName("testKeyholderInsertList1");
//        pojo2.setID(null);
//        pojo2.setName("testKeyholderInsertList2");
//        KeyHolder keyHolder = new KeyHolder();
//        dao.insert(new DalHints(), keyHolder, pojoList);
//
//        assertNull(pojo1.getID());
//        assertNull(pojo2.getID());
//        assertEquals(dao.queryBy(pojo1, null).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
//        assertEquals(dao.queryBy(pojo2, null).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
//    }
//
//    @Test
//    public void testTableShardSingleInsertList() throws Exception {
////        null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setName("testTableShardSingleInsertList1");
//        pojo1.setAge(20);
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setName("testTableShardSingleInsertList2");
//        pojo2.setAge(21);
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        tableShardDao.insert(new DalHints(), pojoList);
//        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(0)));
//        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(1)));
//        assertNotEquals(1L, tableShardDao.queryBy(pojo1, new DalHints().inTableShard(0)).get(0).getID().longValue());
//        assertNotEquals(1L, tableShardDao.queryBy(pojo2, new DalHints().inTableShard(1)).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(3L);
//        pojo1.setAge(null);
//        pojo1.setName("testEnableIdentityInsertList1");
//        pojo2.setID(4L);
//        pojo2.setAge(null);
//        pojo2.setName("testEnableIdentityInsertList2");
//
//        tableShardDao.insert(new DalHints().enableIdentityInsert().inTableShard(0), pojoList);
//        assertEquals("testEnableIdentityInsertList1", tableShardDao.queryByPk(3L, new DalHints().inTableShard(0)).getName());
//        assertEquals("testEnableIdentityInsertList2", tableShardDao.queryByPk(4L, new DalHints().inTableShard(0)).getName());
//
////        setIdentityBack
//        pojo1.setID(null);
//        pojo1.setName("testSetIdentityBackInsertList1");
//        pojo2.setID(null);
//        pojo2.setName("testSetIdentityBackInsertList2");
//
//        tableShardDao.insert(new DalHints().setIdentityBack().setTableShardValue(21), pojoList);
//        assertNotNull(tableShardDao.queryBy(pojo1, new DalHints().setTableShardValue(21)).get(0));
//        assertNotNull(tableShardDao.queryBy(pojo2, new DalHints().setTableShardValue(21)).get(0));
//        assertNotNull(pojo1.getID());
//        assertNotNull(pojo2.getID());
//
////        keyholder
//        pojo1.setID(null);
//        pojo1.setName("testKeyholderInsertList1");
//        pojo2.setID(null);
//        pojo2.setName("testKeyholderInsertList2");
//        KeyHolder keyHolder = new KeyHolder();
//        tableShardDao.insert(new DalHints().setShardColValue("age", 20), keyHolder, pojoList);
//
//        assertNull(pojo1.getID());
//        assertNull(pojo2.getID());
//        assertEquals(tableShardDao.queryBy(pojo1, new DalHints().inTableShard(0)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
//        assertEquals(tableShardDao.queryBy(pojo2, new DalHints().inTableShard(0)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
//    }
//
//    @Test
//    public void testDBTableShardSingleInsertList() throws Exception {
////        null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setName("testDBTableShardSingleInsertList1");
//        pojo1.setAge(21);
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setName("testDBTableShardSingleInsertList2");
//        pojo2.setAge(20);
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        dbTableShardDao.insert(new DalHints().inShard(0).inTableShard(0), pojoList);
//        assertEquals(2, dbTableShardDao.count(new DalHints().inShard(0).inTableShard(0)));
//        assertNotEquals(1L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());
//        assertNotEquals(2L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(3L);
//        pojo1.setName("testEnableIdentityInsertList1");
//        pojo2.setID(4L);
//        pojo2.setName("testEnableIdentityInsertList2");
//
//        dbTableShardDao.insert(new DalHints().enableIdentityInsert().inShard(0).inTableShard(1), pojoList);
//        assertEquals("testEnableIdentityInsertList1", dbTableShardDao.queryByPk(3L, new DalHints().inShard(0).inTableShard(1)).getName());
//        assertEquals("testEnableIdentityInsertList2", dbTableShardDao.queryByPk(4L, new DalHints().inShard(0).inTableShard(1)).getName());
//
////        setIdentityBack
//        pojo1.setID(null);
//        pojo1.setName("testSetIdentityBackInsertList1");
//        pojo2.setID(null);
//        pojo2.setName("testSetIdentityBackInsertList2");
//
//        dbTableShardDao.insert(new DalHints().setIdentityBack().inShard(1).inTableShard(0), pojoList);
//        assertNotNull(dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(0)).get(0));
//        assertNotNull(dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(0)).get(0));
//        assertNotNull(pojo1.getID());
//        assertNotNull(pojo2.getID());
//
////        keyholder
//        pojo1.setID(null);
//        pojo1.setName("testKeyholderInsertList1");
//        pojo2.setID(null);
//        pojo2.setName("testKeyholderInsertList2");
//        KeyHolder keyHolder = new KeyHolder();
//        dbTableShardDao.insert(new DalHints().inShard(1).inTableShard(1), keyHolder, pojoList);
//
//        assertNull(pojo1.getID());
//        assertNull(pojo2.getID());
//        assertEquals(dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
//        assertEquals(dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
//    }
//
//    @Test
//    public void testDBShardSingleInsertList() throws Exception {
////        null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setName("testDBShardSingleInsertList1");
//        pojo1.setAge(20);
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setName("testDBShardSingleInsertList2");
//        pojo2.setAge(21);
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        dbShardDao.insert(new DalHints(), pojoList);
//        assertEquals(1, dbShardDao.count(new DalHints().inShard(0)));
//        assertEquals(1, dbShardDao.count(new DalHints().inShard(1)));
//        assertNotEquals(1L, dbShardDao.queryBy(pojo1, new DalHints().inShard(0)).get(0).getID().longValue());
//        assertNotEquals(1L, dbShardDao.queryBy(pojo2, new DalHints().inShard(1)).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(3L);
//        pojo1.setAge(null);
//        pojo1.setName("testEnableIdentityInsertList1");
//        pojo2.setID(4L);
//        pojo2.setAge(null);
//        pojo2.setName("testEnableIdentityInsertList2");
//
//        dbShardDao.insert(new DalHints().enableIdentityInsert().inShard(0), pojoList);
//        assertEquals("testEnableIdentityInsertList1", dbShardDao.queryByPk(3L, new DalHints().inShard(0)).getName());
//        assertEquals("testEnableIdentityInsertList2", dbShardDao.queryByPk(4L, new DalHints().inShard(0)).getName());
//
////        setIdentityBack
//        pojo1.setID(null);
//        pojo1.setName("testSetIdentityBackInsertList1");
//        pojo2.setID(null);
//        pojo2.setName("testSetIdentityBackInsertList2");
//
//        dbShardDao.insert(new DalHints().setIdentityBack().setShardValue(21), pojoList);
//        assertNotNull(dbShardDao.queryBy(pojo1, new DalHints().setShardValue(21)).get(0));
//        assertNotNull(dbShardDao.queryBy(pojo2, new DalHints().setShardValue(21)).get(0));
//        assertNotNull(pojo1.getID());
//        assertNotNull(pojo2.getID());
//
////        keyholder
//        pojo1.setID(null);
//        pojo1.setName("testKeyholderInsertList1");
//        pojo2.setID(null);
//        pojo2.setName("testKeyholderInsertList2");
//        KeyHolder keyHolder = new KeyHolder();
//        dbShardDao.insert(new DalHints().setShardColValue("age", 20), keyHolder, pojoList);
//
//        assertNull(pojo1.getID());
//        assertNull(pojo2.getID());
//        assertEquals(dbShardDao.queryBy(pojo1, new DalHints().inShard(0)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
//        assertEquals(dbShardDao.queryBy(pojo2, new DalHints().inShard(0)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
//    }
//
//    @Test
//    public void testBatchInsert() throws Exception {
////       null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setName("BatchInsertWithNullHints1");
//
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setName("BatchInsertWithNullHints2");
//
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        dao.batchInsert(new DalHints(), pojoList);
//        assertEquals(2, dao.count(null));
//        assertNotEquals(1L, dao.queryBy(pojo1, null).get(0).getID().longValue());
//        assertNotEquals(2L, dao.queryBy(pojo2, null).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(20L);
//        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
//        pojo2.setID(21L);
//        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
//        dao.batchInsert(new DalHints().enableIdentityInsert(), pojoList);
//        assertEquals(20L, dao.queryBy(pojo1, null).get(0).getID().longValue());
//        assertEquals(21L, dao.queryBy(pojo2, null).get(0).getID().longValue());
//    }
//
//    @Test
//    public void testTableShardBatchInsert() throws Exception {
////       null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setName("BatchInsertWithNullHints1");
//        pojo1.setAge(20);
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setName("BatchInsertWithNullHints2");
//        pojo2.setAge(21);
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        tableShardDao.batchInsert(new DalHints(), pojoList);
//        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(0)));
//        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(1)));
//        assertNotEquals(1L, tableShardDao.queryBy(pojo1, new DalHints().inTableShard(0)).get(0).getID().longValue());
//        assertNotEquals(1L, tableShardDao.queryBy(pojo2, new DalHints().inTableShard(1)).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(20L);
//        pojo1.setAge(null);
//        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
//        pojo2.setID(21L);
//        pojo2.setAge(null);
//        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
//        tableShardDao.batchInsert(new DalHints().enableIdentityInsert().inTableShard(1), pojoList);
//        assertEquals(20L, tableShardDao.queryBy(pojo1, new DalHints().inTableShard(1)).get(0).getID().longValue());
//        assertEquals(21L, tableShardDao.queryBy(pojo2, new DalHints().inTableShard(1)).get(0).getID().longValue());
//    }
//
//    @Test
//    public void testDBTableShardBatchInsert() throws Exception {
////       null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setName("BatchInsertWithNullHints1");
//        pojo1.setAge(20);
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setName("BatchInsertWithNullHints2");
//        pojo2.setAge(21);
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        dbTableShardDao.batchInsert(new DalHints().inShard(0).inTableShard(0), pojoList);
//        assertEquals(2, dbTableShardDao.count(new DalHints().inShard(0).inTableShard(0)));
//        assertNotEquals(1L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());
//        assertNotEquals(2L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(20L);
//        pojo1.setAge(null);
//        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
//        pojo2.setID(21L);
//        pojo2.setAge(null);
//        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
//        dbTableShardDao.batchInsert(new DalHints().enableIdentityInsert().inShard(1).inTableShard(1), pojoList);
//        assertEquals(20L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue());
//        assertEquals(21L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue());
//    }
//
//    @Test
//    public void testDBShardBatchInsert() throws Exception {
////       null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setName("BatchInsertWithNullHints1");
//        pojo1.setAge(20);
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setName("BatchInsertWithNullHints2");
//        pojo2.setAge(21);
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        dbShardDao.batchInsert(new DalHints(), pojoList);
//        assertEquals(1, dbShardDao.count(new DalHints().inShard(0)));
//        assertEquals(1, dbShardDao.count(new DalHints().inShard(1)));
//        assertNotEquals(1L, dbShardDao.queryBy(pojo1, new DalHints().inShard(0)).get(0).getID().longValue());
//        assertNotEquals(1L, dbShardDao.queryBy(pojo2, new DalHints().inShard(1)).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(20L);
//        pojo1.setAge(null);
//        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
//        pojo2.setID(21L);
//        pojo2.setAge(null);
//        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
//        dbShardDao.batchInsert(new DalHints().enableIdentityInsert().inShard(1), pojoList);
//        assertEquals(20L, dbShardDao.queryBy(pojo1, new DalHints().inShard(1)).get(0).getID().longValue());
//        assertEquals(21L, dbShardDao.queryBy(pojo2, new DalHints().inShard(1)).get(0).getID().longValue());
//    }
//
//    @Test
//    public void testCombinedInsert() throws Exception {
////       null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setName("CombinedInsertWithNullHints1");
//
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setName("CombinedInsertWithNullHints2");
//
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        dao.combinedInsert(new DalHints(), pojoList);
//        assertEquals(2, dao.count(null));
//        assertNotEquals(1L, dao.queryBy(pojo1, null).get(0).getID().longValue());
//        assertNotEquals(2L, dao.queryBy(pojo2, null).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(20L);
//        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
//        pojo2.setID(21L);
//        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
//        dao.combinedInsert(new DalHints().enableIdentityInsert(), pojoList);
//        assertEquals(20L, dao.queryBy(pojo1, null).get(0).getID().longValue());
//        assertEquals(21L, dao.queryBy(pojo2, null).get(0).getID().longValue());
//
////        setIdentityBack
//        pojo1.setID(null);
//        pojo1.setName("CombinedInsertWithSetIdentityBack1");
//        pojo2.setID(null);
//        pojo2.setName("CombinedInsertWithSetIdentityBack2");
//
//        dao.combinedInsert(new DalHints().setIdentityBack(), pojoList);
//        assertNotNull(dao.queryBy(pojo1, null).get(0));
//        assertNotNull(dao.queryBy(pojo2, null).get(0));
//        assertNotNull(pojo1.getID());
//        assertNotNull(pojo2.getID());
//
////        keyholder
//        pojo1.setID(null);
//        pojo1.setName("CombinedInsertWithKeyholder1");
//        pojo2.setID(null);
//        pojo2.setName("CombinedInsertWithKeyholder2");
//        KeyHolder keyHolder = new KeyHolder();
//        dao.combinedInsert(new DalHints(), keyHolder, pojoList);
//
//        assertNull(pojo1.getID());
//        assertNull(pojo2.getID());
//        assertEquals(dao.queryBy(pojo1, null).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
//        assertEquals(dao.queryBy(pojo2, null).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
//    }
//
//    @Test
//    public void testTableShardCombinedInsert() throws Exception {
////       null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setAge(20);
//        pojo1.setName("CombinedInsertWithNullHints1");
//
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setAge(21);
//        pojo2.setName("CombinedInsertWithNullHints2");
//
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        tableShardDao.combinedInsert(new DalHints(), pojoList);
//        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(0)));
//        assertEquals(1, tableShardDao.count(new DalHints().inTableShard(1)));
//        assertNotEquals(1L, tableShardDao.queryBy(pojo1, null).get(0).getID().longValue());
//        assertNotEquals(2L, tableShardDao.queryBy(pojo2, null).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(20L);
//        pojo1.setAge(null);
//        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
//        pojo2.setID(21L);
//        pojo2.setAge(null);
//        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
//        tableShardDao.combinedInsert(new DalHints().enableIdentityInsert().inTableShard(0), pojoList);
//        assertEquals(20L, tableShardDao.queryBy(pojo1, new DalHints().inTableShard(0)).get(0).getID().longValue());
//        assertEquals(21L, tableShardDao.queryBy(pojo2, new DalHints().inTableShard(0)).get(0).getID().longValue());
//
////        setIdentityBack
//        pojo1.setID(null);
//        pojo1.setName("CombinedInsertWithSetIdentityBack1");
//        pojo2.setID(null);
//        pojo2.setName("CombinedInsertWithSetIdentityBack2");
//
//        tableShardDao.combinedInsert(new DalHints().setIdentityBack().setTableShardValue(21), pojoList);
//        assertNotNull(tableShardDao.queryBy(pojo1, new DalHints().setTableShardValue(21)).get(0));
//        assertNotNull(tableShardDao.queryBy(pojo2, new DalHints().setTableShardValue(21)).get(0));
//        assertNotNull(pojo1.getID());
//        assertNotNull(pojo2.getID());
//
////        keyholder
//        pojo1.setID(null);
//        pojo1.setName("CombinedInsertWithKeyholder1");
//        pojo2.setID(null);
//        pojo2.setName("CombinedInsertWithKeyholder2");
//        KeyHolder keyHolder = new KeyHolder();
//        tableShardDao.combinedInsert(new DalHints().setShardColValue("age", 20), keyHolder, pojoList);
//
//        assertNull(pojo1.getID());
//        assertNull(pojo2.getID());
//        assertEquals(tableShardDao.queryBy(pojo1, new DalHints().setShardColValue("age", 20)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
//        assertEquals(tableShardDao.queryBy(pojo2, new DalHints().setShardColValue("age", 20)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
//    }
//
//    @Test
//    public void testDBTableShardCombinedInsert() throws Exception {
////       null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setAge(20);
//        pojo1.setName("CombinedInsertWithNullHints1");
//
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setAge(21);
//        pojo2.setName("CombinedInsertWithNullHints2");
//
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        dbTableShardDao.combinedInsert(new DalHints().inShard(0).inTableShard(0), pojoList);
//        assertEquals(2, dbTableShardDao.count(new DalHints().inShard(0).inTableShard(0)));
//        assertNotEquals(1L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());
//        assertNotEquals(2L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(0).inTableShard(0)).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(20L);
//        pojo1.setAge(null);
//        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
//        pojo2.setID(21L);
//        pojo2.setAge(null);
//        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
//        dbTableShardDao.combinedInsert(new DalHints().enableIdentityInsert().inShard(0).inTableShard(1), pojoList);
//        assertEquals(20L, dbTableShardDao.queryBy(pojo1, new DalHints().inShard(0).inTableShard(1)).get(0).getID().longValue());
//        assertEquals(21L, dbTableShardDao.queryBy(pojo2, new DalHints().inShard(0).inTableShard(1)).get(0).getID().longValue());
//
////        setIdentityBack
//        pojo1.setID(null);
//        pojo1.setName("CombinedInsertWithSetIdentityBack1");
//        pojo2.setID(null);
//        pojo2.setName("CombinedInsertWithSetIdentityBack2");
//
//        dbTableShardDao.combinedInsert(new DalHints().setIdentityBack().inShard(1).inTableShard(0), pojoList);
//        assertNotNull(dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(0)).get(0));
//        assertNotNull(dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(0)).get(0));
//        assertNotNull(pojo1.getID());
//        assertNotNull(pojo2.getID());
//
////        keyholder
//        pojo1.setID(null);
//        pojo1.setName("CombinedInsertWithKeyholder1");
//        pojo2.setID(null);
//        pojo2.setName("CombinedInsertWithKeyholder2");
//        KeyHolder keyHolder = new KeyHolder();
//        dbTableShardDao.combinedInsert(new DalHints().inShard(1).inTableShard(1), keyHolder, pojoList);
//
//        assertNull(pojo1.getID());
//        assertNull(pojo2.getID());
//        assertEquals(dbTableShardDao.queryBy(pojo1, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
//        assertEquals(dbTableShardDao.queryBy(pojo2, new DalHints().inShard(1).inTableShard(1)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
//    }
//
//    @Test
//    public void testDBShardCombinedInsert() throws Exception {
////       null hints
//        List<TableWithIdentity> pojoList = new ArrayList<>();
//        TableWithIdentity pojo1 = new TableWithIdentity();
//        pojo1.setAge(20);
//        pojo1.setName("CombinedInsertWithNullHints1");
//
//        TableWithIdentity pojo2 = new TableWithIdentity();
//        pojo2.setAge(21);
//        pojo2.setName("CombinedInsertWithNullHints2");
//
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        dbShardDao.combinedInsert(new DalHints(), pojoList);
//        assertEquals(1, dbShardDao.count(new DalHints().inShard(0)));
//        assertEquals(1, dbShardDao.count(new DalHints().inShard(1)));
//        assertNotEquals(1L, dbShardDao.queryBy(pojo1, null).get(0).getID().longValue());
//        assertNotEquals(2L, dbShardDao.queryBy(pojo2, null).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(20L);
//        pojo1.setAge(null);
//        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
//        pojo2.setID(21L);
//        pojo2.setAge(null);
//        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
//        dbShardDao.combinedInsert(new DalHints().enableIdentityInsert().inShard(0), pojoList);
//        assertEquals(20L, dbShardDao.queryBy(pojo1, new DalHints().inShard(0)).get(0).getID().longValue());
//        assertEquals(21L, dbShardDao.queryBy(pojo2, new DalHints().inShard(0)).get(0).getID().longValue());
//
////        setIdentityBack
//        pojo1.setID(null);
//        pojo1.setName("CombinedInsertWithSetIdentityBack1");
//        pojo2.setID(null);
//        pojo2.setName("CombinedInsertWithSetIdentityBack2");
//
//        dbShardDao.combinedInsert(new DalHints().setIdentityBack().setShardValue(21), pojoList);
//        assertNotNull(dbShardDao.queryBy(pojo1, new DalHints().setShardValue(21)).get(0));
//        assertNotNull(dbShardDao.queryBy(pojo2, new DalHints().setShardValue(21)).get(0));
//        assertNotNull(pojo1.getID());
//        assertNotNull(pojo2.getID());
//
////        keyholder
//        pojo1.setID(null);
//        pojo1.setName("CombinedInsertWithKeyholder1");
//        pojo2.setID(null);
//        pojo2.setName("CombinedInsertWithKeyholder2");
//        KeyHolder keyHolder = new KeyHolder();
//        dbShardDao.combinedInsert(new DalHints().setShardColValue("age", 20), keyHolder, pojoList);
//
//        assertNull(pojo1.getID());
//        assertNull(pojo2.getID());
//        assertEquals(dbShardDao.queryBy(pojo1, new DalHints().setShardColValue("age", 20)).get(0).getID().longValue(), keyHolder.getKey(0).longValue());
//        assertEquals(dbShardDao.queryBy(pojo2, new DalHints().setShardColValue("age", 20)).get(0).getID().longValue(), keyHolder.getKey(1).longValue());
//    }
//
//    @Test
//    public void testIDNotAGSingleInsert() throws Exception {
////        null hints, null id
//        TableWithNoIdentity singlePojo = new TableWithNoIdentity();
//        singlePojo.setAge(20);
//        singlePojo.setName("TestSinglePojoInsertWithNullHints");
//        try {
//            idNotAGDao.insert(new DalHints(), singlePojo);
//            fail();
//        } catch (Exception e) {
//
//        }
//
//        //        null hints, not null id
//        TableWithNoIdentity singlePojo1 = new TableWithNoIdentity();
//        singlePojo1.setID(100L);
//        singlePojo1.setAge(20);
//        singlePojo1.setName("TestSinglePojoInsertWithNullHints1");
//        try {
//            idNotAGDao.insert(new DalHints(), singlePojo1);
//        } catch (Exception e) {
//            fail();
//        }
//        TableWithNoIdentity queryPojo1 = idNotAGDao.queryByPk(100L, null);
//        assertEquals("TestSinglePojoInsertWithNullHints1", queryPojo1.getName());
//
////     hints.enableIdentityInsert
//        TableWithNoIdentity singlePojo2 = new TableWithNoIdentity();
//        singlePojo2.setID(1L);
//        singlePojo2.setAge(20);
//        singlePojo2.setName("TestSinglePojoInsertWithEnableIdentityInsert");
//        idNotAGDao.insert(new DalHints().enableIdentityInsert(), singlePojo2);
//
//        TableWithNoIdentity queryPojo = idNotAGDao.queryByPk(1L, null);
//        assertEquals("TestSinglePojoInsertWithEnableIdentityInsert", queryPojo.getName());
//
////        hints.setIdentityBack
//        TableWithNoIdentity singlePojo3 = new TableWithNoIdentity();
//        singlePojo3.setID(20L);
//        singlePojo3.setAge(20);
//        singlePojo3.setName("TestSinglePojoInsertWithSetIdentityBack");
//        idNotAGDao.insert(new DalHints().setIdentityBack(), singlePojo3);
//        TableWithNoIdentity queryPojo2 = idNotAGDao.queryByPk(singlePojo3.getID(), null);
//        assertEquals("TestSinglePojoInsertWithSetIdentityBack", queryPojo2.getName());
//    }
//
//    @Test
//    public void testIdNotAGDaoBatchInsert() throws Exception {
////       null hints
//        List<TableWithNoIdentity> pojoList = new ArrayList<>();
//        TableWithNoIdentity pojo1 = new TableWithNoIdentity();
//        pojo1.setName("BatchInsertWithNullHints1");
//
//        TableWithNoIdentity pojo2 = new TableWithNoIdentity();
//        pojo2.setName("BatchInsertWithNullHints2");
//
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        try {
//            idNotAGDao.batchInsert(new DalHints(), pojoList);
//            fail();
//        } catch (Exception e) {
//
//        }
//        pojo1.setID(10L);
//        pojo2.setID(20L);
//        idNotAGDao.batchInsert(new DalHints(), pojoList);
//        assertEquals(2, idNotAGDao.count(null));
//        assertEquals(10L, idNotAGDao.queryLike(pojo1, null).get(0).getID().longValue());
//        assertEquals(20L, idNotAGDao.queryLike(pojo2, null).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(30L);
//        pojo1.setName("BatchInsertWithEnableIdentityInsert1");
//        pojo2.setID(31L);
//        pojo2.setName("BatchInsertWithEnableIdentityInsert2");
//        idNotAGDao.batchInsert(new DalHints().enableIdentityInsert(), pojoList);
//        assertEquals(30L, idNotAGDao.queryLike(pojo1, null).get(0).getID().longValue());
//        assertEquals(31L, idNotAGDao.queryLike(pojo2, null).get(0).getID().longValue());
//    }
//
//    @Test
//    public void testIdNotAGDaoCombinedInsert() throws Exception {
////       null hints
//        List<TableWithNoIdentity> pojoList = new ArrayList<>();
//        TableWithNoIdentity pojo1 = new TableWithNoIdentity();
//        pojo1.setName("CombinedInsertWithNullHints1");
//
//        TableWithNoIdentity pojo2 = new TableWithNoIdentity();
//        pojo2.setName("CombinedInsertWithNullHints2");
//
//        pojoList.add(pojo1);
//        pojoList.add(pojo2);
//
//        try {
//            idNotAGDao.combinedInsert(new DalHints(), pojoList);
//            fail();
//        } catch (Exception e) {
//
//        }
//
//        pojo1.setID(10L);
//        pojo2.setID(20L);
//        idNotAGDao.combinedInsert(new DalHints(), pojoList);
//        assertEquals(2, idNotAGDao.count(null));
//        assertEquals(10L, idNotAGDao.queryLike(pojo1, null).get(0).getID().longValue());
//        assertEquals(20L, idNotAGDao.queryLike(pojo2, null).get(0).getID().longValue());
//
////        enableIdentityInsert
//        pojo1.setID(30L);
//        pojo1.setName("CombinedInsertWithEnableIdentityInsert1");
//        pojo2.setID(31L);
//        pojo2.setName("CombinedInsertWithEnableIdentityInsert2");
//        idNotAGDao.combinedInsert(new DalHints().enableIdentityInsert(), pojoList);
//        assertEquals(30L, idNotAGDao.queryLike(pojo1, null).get(0).getID().longValue());
//        assertEquals(31L, idNotAGDao.queryLike(pojo2, null).get(0).getID().longValue());
//
////        setIdentityBack
//        pojo1.setID(40L);
//        pojo1.setName("CombinedInsertWithSetIdentityBack1");
//        pojo2.setID(41L);
//        pojo2.setName("CombinedInsertWithSetIdentityBack2");
//
//        idNotAGDao.combinedInsert(new DalHints().setIdentityBack(), pojoList);
//        assertNotNull(idNotAGDao.queryLike(pojo1, null).get(0));
//        assertNotNull(idNotAGDao.queryLike(pojo2, null).get(0));
//    }
//
//}
