package noShardTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import com.ctrip.platform.dal.dao.sqlbuilder.MatchPattern;
import org.junit.*;

import static org.junit.Assert.*;

import com.ctrip.platform.dal.dao.*;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * JUnit test of AllTypesDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
 **/
public class AllTypesOnMysqlDaoUnitTest {

    private static final String DATA_BASE = "noShardTestOnMysql";

    private static DalClient client = null;
    private static AllTypesOnMysqlDao dao = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        /**
         * Initialize DalClientFactory.
         * The Dal.config can be specified from class-path or local file path.
         * One of follow three need to be enabled.
         **/
        DalClientFactory.initClientFactory(); // load from class-path Dal.config
        DalClientFactory.warmUpConnections();
        client = DalClientFactory.getClient(DATA_BASE);
        dao = new AllTypesOnMysqlDao();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
//		To prepare test data, you can simply uncomment the following.
//		In case of DB and table shard, please revise the code to reflect shard
//		for(int i = 0; i < 10; i++) {
//			AllTypes daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		DalHints hints = new DalHints();
//		dao.testDefTruncate(hints);
//
//		AllTypesOnMysql daoPojo1 = new AllTypesOnMysql();
//		daoPojo1.setIntCol(8);
//		daoPojo1.setBigIntCol(80l);
//		daoPojo1.setMediumIntCol(80);
//		daoPojo1.setSmallIntCol(2);
//		daoPojo1.setTinyIntCol(2);
//		daoPojo1.setDoubleCol(8.99);
//		daoPojo1.setCharCol("a");
//		daoPojo1.setVarCharCol("colorful");
//		daoPojo1.setDecimalCol(new BigDecimal(2013));
////		daoPojo1.setDecimalCol(new BigDecimal("32536174.9556"));
//		daoPojo1.setBitCol(false);
//		daoPojo1.setSetCol("c");
//		dao.insert(hints, daoPojo1);
//
//		AllTypesOnMysql daoPojo2 = new AllTypesOnMysql();
//		daoPojo2.setIntCol(1);
//		daoPojo2.setBigIntCol(100l);
//		daoPojo2.setMediumIntCol(49);
//		daoPojo2.setSmallIntCol(30);
//		daoPojo2.setTinyIntCol(4);
//		daoPojo2.setFloatCol(2.3f);
//		daoPojo2.setCharCol("b");
//		daoPojo2.setVarCharCol("new");
//		daoPojo2.setDecimalCol(new BigDecimal(2016));
//		daoPojo2.setBitCol(true);
//		daoPojo2.setSetCol("a");
//		dao.insert(hints, daoPojo2);
//
//         AllTypesOnMysql daoPojo3 = new AllTypesOnMysql();
// 		daoPojo3.setIntCol(6);
// 		daoPojo3.setBigIntCol(70l);
// 		daoPojo3.setMediumIntCol(50);
// 		daoPojo3.setSmallIntCol(7);
// 		daoPojo3.setTinyIntCol(6);
// 		daoPojo3.setDoubleCol(2.33);
// 		daoPojo3.setCharCol("a");
// 		daoPojo3.setVarCharCol("hello");
// 		daoPojo3.setDecimalCol(new BigDecimal(2015));
// 		daoPojo3.setBitCol(true);
// 		daoPojo3.setSetCol("b");
// 		dao.insert(hints, daoPojo3);
//
//         AllTypesOnMysql daoPojo4 = new AllTypesOnMysql();
//  		daoPojo4.setIntCol(12);
//  		daoPojo4.setBigIntCol(90l);
//  		daoPojo4.setMediumIntCol(70);
//  		daoPojo4.setSmallIntCol(9);
//  		daoPojo4.setTinyIntCol(8);
//  		daoPojo4.setDoubleCol(5.66);
//  		daoPojo4.setCharCol("a");
//  		daoPojo4.setVarCharCol("world");
//  		daoPojo4.setDecimalCol(new BigDecimal(2014));
//  		daoPojo4.setBitCol(false);
//  		daoPojo4.setSetCol("a");
//  		dao.insert(hints, daoPojo4);

        DalHints hints = new DalHints();
        dao.testDefTruncate(hints);

        AllTypesOnMysql daoPojo1 = new AllTypesOnMysql();
        daoPojo1.setIntCol(8);
        daoPojo1.setBigIntCol(80L);
        daoPojo1.setMediumIntCol(80);
        daoPojo1.setSmallIntCol(2);
        daoPojo1.setTinyIntCol(2);
        daoPojo1.setDoubleCol(8.99);
        daoPojo1.setCharCol("a");
        daoPojo1.setVarCharCol("colorful");
        daoPojo1.setDecimalCol(new BigDecimal(2013));
//		daoPojo1.setDecimalCol(new BigDecimal("32536174.9556"));
        daoPojo1.setBitCol(false);
        daoPojo1.setSetCol("c");
        daoPojo1.setTimeStampCol(Timestamp.valueOf("2012-12-12 12:12:12"));
//		dao.insert(hints, daoPojo1);

        AllTypesOnMysql daoPojo2 = new AllTypesOnMysql();
        daoPojo2.setIntCol(1);
        daoPojo2.setBigIntCol(100L);
        daoPojo2.setMediumIntCol(49);
        daoPojo2.setSmallIntCol(30);
        daoPojo2.setTinyIntCol(4);
        daoPojo2.setFloatCol(2.3f);
        daoPojo2.setCharCol("b");
        daoPojo2.setVarCharCol("new");
        daoPojo2.setDecimalCol(new BigDecimal(2016));
        daoPojo2.setBitCol(true);
        daoPojo2.setSetCol("a");
        daoPojo2.setTimeStampCol(Timestamp.valueOf("2012-12-12 12:12:12"));
//		dao.insert(hints, daoPojo2);

        AllTypesOnMysql daoPojo3 = new AllTypesOnMysql();
        daoPojo3.setIntCol(6);
        daoPojo3.setBigIntCol(70L);
        daoPojo3.setMediumIntCol(50);
        daoPojo3.setSmallIntCol(7);
        daoPojo3.setTinyIntCol(6);
        daoPojo3.setDoubleCol(2.33);
        daoPojo3.setCharCol("a");
        daoPojo3.setVarCharCol("hello");
        daoPojo3.setDecimalCol(new BigDecimal(2015));
        daoPojo3.setBitCol(true);
        daoPojo3.setSetCol("b");
        daoPojo3.setTimeStampCol(Timestamp.valueOf("2012-12-12 12:12:12"));
// 		dao.insert(hints, daoPojo3);

        AllTypesOnMysql daoPojo4 = new AllTypesOnMysql();
        daoPojo4.setIntCol(12);
        daoPojo4.setBigIntCol(90L);
        daoPojo4.setMediumIntCol(70);
        daoPojo4.setSmallIntCol(9);
        daoPojo4.setTinyIntCol(8);
        daoPojo4.setDoubleCol(5.66);
        daoPojo4.setCharCol("a");
        daoPojo4.setVarCharCol("world");
        daoPojo4.setDecimalCol(new BigDecimal(2014));
        daoPojo4.setBitCol(false);
        daoPojo4.setSetCol("a");
        daoPojo4.setTimeStampCol(Timestamp.valueOf("2012-12-12 12:12:12"));
//  		dao.insert(hints, daoPojo4);

        AllTypesOnMysql daoPojo5 = new AllTypesOnMysql();
        daoPojo5.setIntCol(1);
        daoPojo5.setBigIntCol(1L);
        daoPojo5.setMediumIntCol(1);
        daoPojo5.setSmallIntCol(1);
        daoPojo5.setTinyIntCol(250);
        daoPojo5.setDoubleCol(8.99);
        daoPojo5.setCharCol("a");
        daoPojo5.setVarCharCol("colorful");
        daoPojo5.setDecimalCol(new BigDecimal(2013));
        daoPojo5.setBitCol(false);
        daoPojo5.setSetCol("c");
        daoPojo5.setTimeStampCol(Timestamp.valueOf("2012-12-12 12:12:12"));
//		daoPojo1.setTimeStampCol2(Timestamp.valueOf("2016-12-5 17:38:00"));
//		dao.insert(hints, daoPojo1);

        AllTypesOnMysql daoPojo6 = new AllTypesOnMysql();
        daoPojo6.setIntCol(2);
        daoPojo6.setBigIntCol(2L);
        daoPojo6.setMediumIntCol(2);
        daoPojo6.setSmallIntCol(2);
        daoPojo6.setTinyIntCol(251);
        daoPojo6.setFloatCol(2.3f);
        daoPojo6.setCharCol("b");
        daoPojo6.setVarCharCol("new");
        daoPojo6.setDecimalCol(new BigDecimal(2016));
        daoPojo6.setBitCol(true);
        daoPojo6.setSetCol("a");
        daoPojo6.setTimeStampCol(Timestamp.valueOf("2012-12-12 12:12:12"));
//		daoPojo2.setTimeStampCol2(Timestamp.valueOf("2016-12-5 17:38:00"));
//		dao.insert(hints, daoPojo2);

        AllTypesOnMysql daoPojo7 = new AllTypesOnMysql();
        daoPojo7.setIntCol(3);
        daoPojo7.setBigIntCol(3L);
        daoPojo7.setMediumIntCol(3);
        daoPojo7.setSmallIntCol(3);
        daoPojo7.setTinyIntCol(252);
        daoPojo7.setDoubleCol(2.33);
        daoPojo7.setCharCol("a");
        daoPojo7.setVarCharCol("hello");
        daoPojo7.setDecimalCol(new BigDecimal(2015));
        daoPojo7.setBitCol(true);
        daoPojo7.setSetCol("b");
        daoPojo7.setTimeStampCol(Timestamp.valueOf("2012-12-12 12:12:12"));
//		daoPojo3.setTimeStampCol2(Timestamp.valueOf("2016-12-5 17:38:00"));
//		dao.insert(hints, daoPojo3);

        AllTypesOnMysql daoPojo8 = new AllTypesOnMysql();
        daoPojo8.setIntCol(4);
        daoPojo8.setBigIntCol(4L);
        daoPojo8.setMediumIntCol(4);
        daoPojo8.setSmallIntCol(4);
        daoPojo8.setTinyIntCol(253);
        daoPojo8.setDoubleCol(5.66);
        daoPojo8.setCharCol("a");
        daoPojo8.setVarCharCol("world");
        daoPojo8.setDecimalCol(new BigDecimal(2014));
        daoPojo8.setBitCol(false);
        daoPojo8.setSetCol("a");
        daoPojo8.setTimeStampCol(Timestamp.valueOf("2012-12-12 12:12:12"));
//		daoPojo4.setTimeStampCol2(Timestamp.valueOf("2016-12-5 17:38:00"));
//		dao.insert(hints, daoPojo4);

        List<AllTypesOnMysql> pojos = new ArrayList<>();
        pojos.add(daoPojo1);
        pojos.add(daoPojo2);
        pojos.add(daoPojo3);
        pojos.add(daoPojo4);
        pojos.add(daoPojo5);
        pojos.add(daoPojo6);
        pojos.add(daoPojo7);
        pojos.add(daoPojo8);
        dao.batchInsert(new DalHints(), pojos);

    }

    private AllTypesOnMysql createPojo(int index) {
        AllTypesOnMysql daoPojo = new AllTypesOnMysql();

        //daoPojo.setIdallTypes(index);
        //daoPojo set not null field

        return daoPojo;
    }

    private void changePojo(AllTypesOnMysql daoPojo) {
        // Change a field to make pojo different with original one
    }

    private void changePojos(List<AllTypesOnMysql> daoPojos) {
        for (AllTypesOnMysql daoPojo : daoPojos)
            changePojo(daoPojo);
    }

    private void verifyPojo(AllTypesOnMysql daoPojo) {
        //assert changed value
    }

    private void verifyPojos(List<AllTypesOnMysql> daoPojos) {
        for (AllTypesOnMysql daoPojo : daoPojos)
            verifyPojo(daoPojo);
    }

    @After
    public void tearDown() throws Exception {
//		To clean up all test data
//		dao.delete(null, dao.queryAll(null));
    }

    @Test
    public void testCombinedInsert1() throws Exception {

        List<AllTypesOnMysql> daoPojos = dao.queryAll();
        daoPojos.get(0).setTimeStampCol(Timestamp.valueOf("2012-12-12 13:13:13"));
        int affected = dao.combinedInsert(daoPojos);
        assertEquals(8, affected);

        AllTypesOnMysql combinedInsertedPojo = dao.queryByPk(9);
        assertNotEquals("2012-12-12 13:13:13", combinedInsertedPojo.getTimeStampCol().toString());

        int count = dao.count();
        assertEquals(16, count);

        dao.combinedInsert(null, daoPojos);
        assertEquals(8, affected);

        count = dao.count();
        assertEquals(24, count);
    }

    @Test
    public void testCombinedInsert2() throws Exception {

        KeyHolder keyHolder = new KeyHolder();
        List<AllTypesOnMysql> daoPojos = dao.queryAll();
        daoPojos.get(0).setTimeStampCol(Timestamp.valueOf("2012-12-12 13:13:13"));
        int affected = dao.combinedInsertWithKeyHolder(keyHolder, daoPojos);
        assertEquals(8, affected);
        assertEquals(8, keyHolder.size());

        AllTypesOnMysql combinedInsertedPojo = dao.queryByPk(9);
        assertNotEquals("2012-12-12 13:13:13", combinedInsertedPojo.getTimeStampCol().toString());

        int count = dao.count();
        assertEquals(16, count);

        affected = dao.combinedInsert(null, keyHolder, daoPojos);
        assertEquals(8, affected);
        assertEquals(8, keyHolder.size());

        count = dao.count();
        assertEquals(24, count);
    }

    @Test
    public void testCount() throws Exception {
        int affected = dao.count();
        assertEquals(8, affected);

        affected = dao.count(null);
        assertEquals(8, affected);
    }

    @Test
    public void testDelete1() throws Exception {
        AllTypesOnMysql daoPojo = createPojo(1);
        daoPojo.setIdallTypes(1);
        /**
         * WARNING !!!
         * To test delete, please make sure you can easily restore all the data. otherwise data will not be revovered.
         */
        int affected = dao.delete(daoPojo);
        assertEquals(1, affected);

        affected = dao.count();
        assertEquals(7, affected);

        daoPojo.setIdallTypes(2);
        affected = dao.delete(null, daoPojo);
        assertEquals(1, affected);

        affected = dao.count();
        assertEquals(6, affected);
    }

    @Test
    public void testDelete2() throws Exception {
        List<AllTypesOnMysql> daoPojos = dao.queryAll();
        /**
         * WARNING !!!
         * To test delete, please make sure you can easily restore all the data. otherwise data will not be revovered.
         */

        int[] affected = dao.delete(daoPojos);

        int count = dao.count();
        assertEquals(0, count);

        affected = dao.delete(null, daoPojos);

        count = dao.count();
        assertEquals(0, count);
    }

    @Test
    public void testBatchDelete() throws Exception {
        List<AllTypesOnMysql> daoPojos = dao.queryAll();
        /**
         * WARNING !!!
         * To test batchDelete, please make sure you can easily restore all the data. otherwise data will not be revovered.
         */
        int[] affected = dao.batchDelete(daoPojos);


        int count = dao.count();
        assertEquals(0, count);

        affected = dao.batchDelete(null, daoPojos);


        count = dao.count();
        assertEquals(0, count);
    }

    @Test
    public void testQueryAll() throws Exception {
        List<AllTypesOnMysql> list = dao.queryAll();
        assertEquals(8, list.size());

        AllTypesOnMysql queryPojo = dao.queryByPk(1);
        assertNotEquals("2012-12-12 12:12:12", queryPojo.getTimeStampCol().toString());

        list.clear();
        list = dao.queryAll(new DalHints().selectByNames());
        assertEquals(8, list.size());
    }

    @Test
    public void testInsert1() throws Exception {

        AllTypesOnMysql daoPojo = createPojo(1);
        daoPojo.setVarCharCol("insert1");
        daoPojo.setTimeStampCol(Timestamp.valueOf("2012-12-12 13:13:13"));
        int affected = dao.insert(daoPojo);

        AllTypesOnMysql insertPojo = dao.queryByPk(9);
        assertEquals("insert1", insertPojo.getVarCharCol());
        assertNotEquals("2012-12-12 13:13:13", insertPojo.getTimeStampCol().toString());

        affected = dao.insert(null, daoPojo);//insert(hints,daoPojo);

        insertPojo = dao.queryByPk(10);
        assertEquals("insert1", insertPojo.getVarCharCol());
        assertNotEquals("2012-12-12 13:13:13", insertPojo.getTimeStampCol().toString());

    }

    @Test
    public void testInsert2() throws Exception {

        List<AllTypesOnMysql> daoPojos = dao.queryAll();
        daoPojos.get(0).setTimeStampCol(Timestamp.valueOf("2012-12-12 13:13:13"));
        int[] affected = dao.insert(daoPojos);

        AllTypesOnMysql insertedPojo = dao.queryByPk(9);
        assertNotEquals("2012-12-12 13:13:13", insertedPojo.getTimeStampCol().toString());

        int count = dao.count();
        assertEquals(16, count);

        affected = dao.insert(null, daoPojos);//insert(hints,daoPojos)

        count = dao.count();
        assertEquals(24, count);
    }

    @Test
    public void testInsert3() throws Exception {
        KeyHolder keyHolder = new KeyHolder();
        AllTypesOnMysql daoPojo = createPojo(1);
        daoPojo.setVarCharCol("insert3");
        daoPojo.setTimeStampCol(Timestamp.valueOf("2012-12-12 13:13:13"));
        int affected = dao.insertWithKeyHolder(keyHolder, daoPojo);
//		assertEquals(1, affected);
        assertEquals(1, keyHolder.size());

        AllTypesOnMysql insertPojo = dao.queryByPk(9);
        assertEquals("insert3", insertPojo.getVarCharCol());
        assertNotEquals("2012-12-12 13:13:13", insertPojo.getTimeStampCol().toString());

        affected = dao.insert(null, keyHolder, daoPojo);//insert(hints,keyHolder, daoPojo)
//		assertEquals(1, affected);
        assertEquals(1, keyHolder.size());

        insertPojo = dao.queryByPk(10);
        assertEquals("insert3", insertPojo.getVarCharCol());
    }

    @Test
    public void testInsert4() throws Exception {

        KeyHolder keyHolder = new KeyHolder();
        List<AllTypesOnMysql> daoPojos = dao.queryAll();
        daoPojos.get(0).setTimeStampCol(Timestamp.valueOf("2012-12-12 13:13:13"));
        int[] affected = dao.insertWithKeyHolder(keyHolder, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1},  affected);
        assertEquals(8, keyHolder.size());

        AllTypesOnMysql insertedPojo = dao.queryByPk(9);
        assertNotEquals("2012-12-12 13:13:13", insertedPojo.getTimeStampCol().toString());

        int count = dao.count();
        assertEquals(16, count);

        affected = dao.insert(null, keyHolder, daoPojos);//insert(hints,keyHolder, daoPojos)
//		assertArrayEquals(new int[]{1,1,1,1},  affected);
        assertEquals(8, keyHolder.size());

        count = dao.count();
        assertEquals(24, count);
    }

    @Test
    public void testInsert5() throws Exception {

        List<AllTypesOnMysql> daoPojos = dao.queryAll();
        daoPojos.get(0).setTimeStampCol(Timestamp.valueOf("2012-12-12 13:13:13"));
        int[] affected = dao.batchInsert(daoPojos);

        AllTypesOnMysql insertedPojo = dao.queryByPk(9);
        assertNotEquals("2012-12-12 13:13:13", insertedPojo.getTimeStampCol().toString());

        int count = dao.count();
        assertEquals(16, count);

        affected = dao.batchInsert(null, daoPojos);

        count = dao.count();
        assertEquals(24, count);
    }


    @Test
    public void testQueryAllByPage() throws Exception {

        int pageSize = 2;
        int pageNo = 2;
        List<AllTypesOnMysql> list = dao.queryAllByPage(pageNo, pageSize);
        assertEquals(2, list.size());

        assertNotEquals("2012-12-12 12:12:12", list.get(0).getTimeStampCol().toString());

        list = dao.queryAllByPage(pageNo, pageSize, new DalHints().selectByNames());
        assertEquals(2, list.size());
    }

    @Test
    public void testQueryByPk1() throws Exception {
        Number id = 1;

        AllTypesOnMysql affected = dao.queryByPk(id);
        assertNotNull(affected);
        assertNotEquals("2012-12-12 12:12:12", affected.getTimeStampCol().toString());

        affected = dao.queryByPk(id, new DalHints().selectByNames());
        assertNotNull(affected);
    }

    @Test
    public void testQueryByPk2() throws Exception {
        AllTypesOnMysql pk = createPojo(1);
        pk.setIdallTypes(2);
        AllTypesOnMysql affected = dao.queryByPk(pk);
        assertNotNull(affected);
        assertNotEquals("2012-12-12 12:12:12", affected.getTimeStampCol().toString());
        affected = dao.queryByPk(pk, new DalHints().selectByNames());
        assertNotNull(affected);
    }

    @Test
    public void testQueryLike() throws Exception {
        AllTypesOnMysql sample = createPojo(1);
        sample.setVarCharCol("new");
        List<AllTypesOnMysql> affected = dao.queryLike(sample);
        assertEquals(2, affected.get(0).getIdallTypes().intValue());
        assertNotEquals("2012-12-12 12:12:12", affected.get(0).getTimeStampCol().toString());
        affected = dao.queryLike(sample, new DalHints().selectByNames());
        assertEquals(2, affected.get(0).getIdallTypes().intValue());
    }

    @Test
    public void testUpdate1() throws Exception {

        AllTypesOnMysql daoPojo = dao.queryByPk(1);
        daoPojo.setVarCharCol("update1");
        daoPojo.setTimeStampCol(Timestamp.valueOf("2012-12-12 14:14:14"));
        int affected = dao.update(daoPojo);

        daoPojo = dao.queryByPk(1);
        assertEquals("update1", daoPojo.getVarCharCol());
        assertNotEquals("2012-12-12 14:14:14", daoPojo.getTimeStampCol().toString());

        daoPojo.setVarCharCol("update11");
        affected = dao.update(null, daoPojo);//update(hints,daoPojo)

        daoPojo = dao.queryByPk(1);
        assertEquals("update11", daoPojo.getVarCharCol());
    }

    @Test
    public void testUpdate2() throws Exception {

        List<AllTypesOnMysql> daoPojos = dao.queryAll();
        daoPojos.get(0).setVarCharCol("update21");

        daoPojos.get(1).setVarCharCol("update22");
        daoPojos.get(2).setVarCharCol("update23");
        daoPojos.get(2).setTimeStampCol(Timestamp.valueOf("2012-12-12 14:14:14"));
        daoPojos.get(3).setVarCharCol("update24");

        int[] affected = dao.update(daoPojos);

        AllTypesOnMysql daoPojo = dao.queryByPk(3);
        assertEquals("update23", daoPojo.getVarCharCol());
        assertNotEquals("2012-12-12 14:14:14", daoPojo.getTimeStampCol().toString());
        daoPojos.get(0).setVarCharCol("update211");
        daoPojos.get(1).setVarCharCol("update222");
        daoPojos.get(2).setVarCharCol("update233");
        daoPojos.get(3).setVarCharCol("update244");
        affected = dao.update(null, daoPojos);//update(hints,daoPojos)

        daoPojo = dao.queryByPk(3);
        assertEquals("update233", daoPojo.getVarCharCol());
    }

    @Test
    public void testBatchUpdate() throws Exception {

        List<AllTypesOnMysql> daoPojos = dao.queryAll();
        daoPojos.get(0).setVarCharCol("batchUpdate1");
        daoPojos.get(1).setVarCharCol("batchUpdate2");
        daoPojos.get(2).setVarCharCol("batchUpdate3");
        daoPojos.get(2).setTimeStampCol(Timestamp.valueOf("2012-12-12 14:14:14"));
        daoPojos.get(3).setVarCharCol("batchUpdate4");
        int[] affected = dao.batchUpdate(daoPojos);

        AllTypesOnMysql daoPojo = dao.queryByPk(3);
        assertEquals("batchUpdate3", daoPojo.getVarCharCol());
        assertNotEquals("2012-12-12 14:14:14", daoPojo.getTimeStampCol().toString());
        daoPojos.get(0).setVarCharCol("batchUpdate11");
        daoPojos.get(1).setVarCharCol("batchUpdate22");
        daoPojos.get(2).setVarCharCol("batchUpdate33");
        daoPojos.get(3).setVarCharCol("batchUpdate44");
        affected = dao.batchUpdate(null, daoPojos);//batchUpdate(hints,daoPojos)

        daoPojo = dao.queryByPk(3);
        assertEquals("batchUpdate33", daoPojo.getVarCharCol());
    }

    @Test
    public void testUpdateSet() throws Exception {
        DalHints hints = new DalHints();

        Set<String> updateColumns = new HashSet<>();
        updateColumns.add("BigIntCol");
        updateColumns.add("DecimalCol");
        updateColumns.add("VarCharCol");

        Set<String> noUpdateColumns = new HashSet<>();
        noUpdateColumns.add("BigIntCol");
        noUpdateColumns.add("SmallIntCol");
        noUpdateColumns.add("CharCol");

        //include,单个shard
        AllTypesOnMysql pojo = dao.queryByPk(5, new DalHints());
        pojo.setIdallTypes(6);
        int ret = dao.update(new DalHints().include(updateColumns), pojo);
        assertEquals(1, ret);

        pojo = dao.queryByPk(6, new DalHints());
        //include列更新
        assertEquals(1L, pojo.getBigIntCol().longValue());
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals("colorful", pojo.getVarCharCol());
        //其余列没有更新
        assertEquals(2, pojo.getIntCol().intValue());
        assertEquals(2, pojo.getMediumIntCol().intValue());
        assertEquals(2, pojo.getSmallIntCol().intValue());
        assertEquals(251, pojo.getTinyIntCol().intValue());
        assertEquals("b", pojo.getCharCol());
        assertEquals(2.3, pojo.getFloatCol(), 0.1);
        assertNull(pojo.getDoubleCol());
        assertEquals(true, pojo.getBitCol());
        assertEquals("a", pojo.getSetCol());

        //exclude
        pojo = dao.queryByPk(5, new DalHints());
        pojo.setIdallTypes(7);
        ret = dao.update(new DalHints().exclude(noUpdateColumns), pojo);
        assertEquals(1, ret);

        pojo = dao.queryByPk(7, new DalHints());
        //exclude列没有更新
        assertEquals(3L, pojo.getBigIntCol().longValue());
        assertEquals(3, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        //其余列更新
        assertEquals(1, pojo.getIntCol().intValue());
        assertEquals(1, pojo.getMediumIntCol().intValue());
        assertEquals(250, pojo.getTinyIntCol().intValue());
        assertEquals(8.99, pojo.getDoubleCol(), 0.11);
        assertEquals("colorful", pojo.getVarCharCol());
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals(false, pojo.getBitCol());
        assertEquals("c", pojo.getSetCol());

        //include&exclude
        pojo = dao.queryByPk(5, new DalHints());
        pojo.setIdallTypes(8);
        ret = dao.update(new DalHints().include(updateColumns).exclude(noUpdateColumns), pojo);
        assertEquals(1, ret);

        pojo = dao.queryByPk(8, new DalHints());

        //exclude列没有更新
        assertEquals(4L, pojo.getBigIntCol().longValue());
        assertEquals(4, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        //include列更新
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals("colorful", pojo.getVarCharCol());
        //其余列没有更新
        assertEquals(4, pojo.getIntCol().intValue());
        assertEquals(4, pojo.getMediumIntCol().intValue());
        assertEquals(253, pojo.getTinyIntCol().intValue());
        assertEquals(5.66, pojo.getDoubleCol(), 0.11);
        assertEquals(false, pojo.getBitCol());
        assertEquals("a", pojo.getSetCol());
    }

    @Test
    public void testUpdateStrings() throws Exception {
        DalHints hints = new DalHints();

        //include
        AllTypesOnMysql pojo = dao.queryByPk(5, new DalHints());
        pojo.setIdallTypes(6);
        int ret = dao.update(new DalHints().include("BigIntCol", "DecimalCol", "VarCharCol"), pojo);
        assertEquals(1, ret);

        pojo = dao.queryByPk(6, new DalHints());
        //include列更新
        assertEquals(1L, pojo.getBigIntCol().longValue());
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals("colorful", pojo.getVarCharCol());
        //其余列没有更新
        assertEquals(2, pojo.getIntCol().intValue());
        assertEquals(2, pojo.getMediumIntCol().intValue());
        assertEquals(2, pojo.getSmallIntCol().intValue());
        assertEquals(251, pojo.getTinyIntCol().intValue());
        assertEquals("b", pojo.getCharCol());
        assertEquals(2.3, pojo.getFloatCol(), 0.1);
        assertNull(pojo.getDoubleCol());
        assertEquals(true, pojo.getBitCol());
        assertEquals("a", pojo.getSetCol());

        //exclude
        pojo = dao.queryByPk(5, new DalHints());
        pojo.setIdallTypes(7);
        ret = dao.update(new DalHints().exclude("BigIntCol", "SmallIntCol", "CharCol"), pojo);
        assertEquals(1, ret);

        pojo = dao.queryByPk(7, new DalHints());
        //exclude列没有更新
        assertEquals(3L, pojo.getBigIntCol().longValue());
        assertEquals(3, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        //其余列更新
        assertEquals(1, pojo.getIntCol().intValue());
        assertEquals(1, pojo.getMediumIntCol().intValue());
        assertEquals(250, pojo.getTinyIntCol().intValue());
        assertEquals(8.99, pojo.getDoubleCol(), 0.11);
        assertEquals("colorful", pojo.getVarCharCol());
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals(false, pojo.getBitCol());
        assertEquals("c", pojo.getSetCol());

        //include&exclude
        pojo = dao.queryByPk(5, new DalHints());
        pojo.setIdallTypes(8);
        ret = dao.update(new DalHints().inShard(1).include("BigIntCol", "DecimalCol", "VarCharCol").exclude("BigIntCol", "SmallIntCol", "CharCol"), pojo);
        assertEquals(1, ret);

        pojo = dao.queryByPk(8, new DalHints());

        //exclude列没有更新
        assertEquals(4L, pojo.getBigIntCol().longValue());
        assertEquals(4, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        //include列更新
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals("colorful", pojo.getVarCharCol());
        //其余列没有更新
        assertEquals(4, pojo.getIntCol().intValue());
        assertEquals(4, pojo.getMediumIntCol().intValue());
        assertEquals(253, pojo.getTinyIntCol().intValue());
        assertEquals(5.66, pojo.getDoubleCol(), 0.11);
        assertEquals(false, pojo.getBitCol());
        assertEquals("a", pojo.getSetCol());
    }


    @Test
    public void testUpdate2Set() throws Exception {
        DalHints hints = new DalHints();

        Set<String> updateColumns = new HashSet<>();
        updateColumns.add("BigIntCol");
        updateColumns.add("DecimalCol");
        updateColumns.add("VarCharCol");

        Set<String> noUpdateColumns = new HashSet<>();
        noUpdateColumns.add("BigIntCol");
        noUpdateColumns.add("SmallIntCol");
        noUpdateColumns.add("CharCol");

        List<AllTypesOnMysql> daoPojos = new ArrayList<>();
        AllTypesOnMysql daoPojo1 = dao.queryByPk(5, new DalHints());
        daoPojo1.setIdallTypes(7);
        daoPojos.add(daoPojo1);
        AllTypesOnMysql daoPojo2 = dao.queryByPk(6, new DalHints());
        daoPojo2.setIdallTypes(8);
        daoPojos.add(daoPojo2);

        int[] ret = dao.update(new DalHints().exclude(noUpdateColumns).include(updateColumns), daoPojos);

        assertEquals(1, ret[0]);
        assertEquals(1, ret[1]);

        AllTypesOnMysql pojo = dao.queryByPk(7, new DalHints());
        // exclude列没有更新
        assertEquals(3L, pojo.getBigIntCol().longValue());
        assertEquals(3, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        // include列更新
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals("colorful", pojo.getVarCharCol());
        // 其余列没有更新
        assertEquals(3, pojo.getIntCol().intValue());
        assertEquals(3, pojo.getMediumIntCol().intValue());
        assertEquals(252, pojo.getTinyIntCol().intValue());
        assertEquals(2.33, pojo.getDoubleCol(), 0.11);
        assertEquals(true, pojo.getBitCol());
        assertEquals("b", pojo.getSetCol());

        pojo = dao.queryByPk(8, new DalHints());
        //exclude列没有更新
        assertEquals(4L, pojo.getBigIntCol().longValue());
        assertEquals(4, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        //include列更新
        assertEquals(new BigDecimal(2016), pojo.getDecimalCol());
        assertEquals("new", pojo.getVarCharCol());
        //其余列没有更新
        assertEquals(4, pojo.getIntCol().intValue());
        assertEquals(4, pojo.getMediumIntCol().intValue());
        assertEquals(253, pojo.getTinyIntCol().intValue());
        assertEquals(5.66, pojo.getDoubleCol(), 0.11);
        assertEquals(false, pojo.getBitCol());
        assertEquals("a", pojo.getSetCol());
    }

    @Test
    public void testUpdate2Strings() throws Exception {
        DalHints hints = new DalHints();

        List<AllTypesOnMysql> daoPojos = new ArrayList<>();
        AllTypesOnMysql daoPojo1 = dao.queryByPk(5, new DalHints());
        daoPojo1.setIdallTypes(7);
        daoPojos.add(daoPojo1);
        AllTypesOnMysql daoPojo2 = dao.queryByPk(6, new DalHints());
        daoPojo2.setIdallTypes(8);
        daoPojos.add(daoPojo2);

        int[] ret = dao.update(new DalHints().inShard(0).exclude("BigIntCol", "SmallIntCol", "CharCol").include("BigIntCol", "DecimalCol", "VarCharCol"), daoPojos);

        assertEquals(1, ret[0]);
        assertEquals(1, ret[1]);

        AllTypesOnMysql pojo = dao.queryByPk(7, new DalHints());
        // exclude列没有更新
        assertEquals(3L, pojo.getBigIntCol().longValue());
        assertEquals(3, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        // include列更新
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals("colorful", pojo.getVarCharCol());
        // 其余列没有更新
        assertEquals(3, pojo.getIntCol().intValue());
        assertEquals(3, pojo.getMediumIntCol().intValue());
        assertEquals(252, pojo.getTinyIntCol().intValue());
        assertEquals(2.33, pojo.getDoubleCol(), 0.11);
        assertEquals(true, pojo.getBitCol());
        assertEquals("b", pojo.getSetCol());

        pojo = dao.queryByPk(8, new DalHints());
        //exclude列没有更新
        assertEquals(4L, pojo.getBigIntCol().longValue());
        assertEquals(4, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        //include列更新
        assertEquals(new BigDecimal(2016), pojo.getDecimalCol());
        assertEquals("new", pojo.getVarCharCol());
        //其余列没有更新
        assertEquals(4, pojo.getIntCol().intValue());
        assertEquals(4, pojo.getMediumIntCol().intValue());
        assertEquals(253, pojo.getTinyIntCol().intValue());
        assertEquals(5.66, pojo.getDoubleCol(), 0.11);
        assertEquals(false, pojo.getBitCol());
        assertEquals("a", pojo.getSetCol());
    }

    // 符合条件的pojo更新成功，version字段自增，不符合条件的pojo没有更新，version字段不变
    @Test
    public void testBatchUpdateSet() throws Exception {
        DalHints hints = new DalHints();

        Set<String> updateColumns = new HashSet<>();
        updateColumns.add("BigIntCol");
        updateColumns.add("DecimalCol");
        updateColumns.add("VarCharCol");

        Set<String> noUpdateColumns = new HashSet<>();
        noUpdateColumns.add("BigIntCol");
        noUpdateColumns.add("SmallIntCol");
        noUpdateColumns.add("CharCol");

        List<AllTypesOnMysql> daoPojos = new ArrayList<>();
        AllTypesOnMysql daoPojo1 = dao.queryByPk(5, new DalHints());
        daoPojo1.setIdallTypes(7);
        daoPojos.add(daoPojo1);
        AllTypesOnMysql daoPojo2 = dao.queryByPk(6, new DalHints());
        daoPojo2.setIdallTypes(8);
        daoPojos.add(daoPojo2);

        dao.batchUpdate(new DalHints().exclude(noUpdateColumns).include(updateColumns), daoPojos);

        AllTypesOnMysql pojo = dao.queryByPk(7, new DalHints());
        // exclude列没有更新
        assertEquals(3L, pojo.getBigIntCol().longValue());
        assertEquals(3, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        // include列更新
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals("colorful", pojo.getVarCharCol());
        // 其余列没有更新
        assertEquals(3, pojo.getIntCol().intValue());
        assertEquals(3, pojo.getMediumIntCol().intValue());
        assertEquals(252, pojo.getTinyIntCol().intValue());
        assertEquals(2.33, pojo.getDoubleCol(), 0.11);
        assertEquals(true, pojo.getBitCol());
        assertEquals("b", pojo.getSetCol());

        pojo = dao.queryByPk(8, new DalHints());
        //exclude列没有更新
        assertEquals(4L, pojo.getBigIntCol().longValue());
        assertEquals(4, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        //include列更新
        assertEquals(new BigDecimal(2016), pojo.getDecimalCol());
        assertEquals("new", pojo.getVarCharCol());
        //其余列没有更新
        assertEquals(4, pojo.getIntCol().intValue());
        assertEquals(4, pojo.getMediumIntCol().intValue());
        assertEquals(253, pojo.getTinyIntCol().intValue());
        assertEquals(5.66, pojo.getDoubleCol(), 0.11);
        assertEquals(false, pojo.getBitCol());
        assertEquals("a", pojo.getSetCol());
    }

    @Test
    public void testBatchUpdateStrings() throws Exception {
        DalHints hints = new DalHints();

        List<AllTypesOnMysql> daoPojos = new ArrayList<>();
        AllTypesOnMysql daoPojo1 = dao.queryByPk(5, new DalHints());
        daoPojo1.setIdallTypes(7);
        daoPojos.add(daoPojo1);
        AllTypesOnMysql daoPojo2 = dao.queryByPk(6, new DalHints());
        daoPojo2.setIdallTypes(8);
        daoPojos.add(daoPojo2);

        int[] ret = dao.batchUpdate(new DalHints().exclude("BigIntCol", "SmallIntCol", "CharCol").include("BigIntCol", "DecimalCol", "VarCharCol"), daoPojos);

        assertEquals(1, ret[0]);
        assertEquals(1, ret[1]);

        AllTypesOnMysql pojo = dao.queryByPk(7, new DalHints());
        // exclude列没有更新
        assertEquals(3L, pojo.getBigIntCol().longValue());
        assertEquals(3, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        // include列更新
        assertEquals(new BigDecimal(2013), pojo.getDecimalCol());
        assertEquals("colorful", pojo.getVarCharCol());
        // 其余列没有更新
        assertEquals(3, pojo.getIntCol().intValue());
        assertEquals(3, pojo.getMediumIntCol().intValue());
        assertEquals(252, pojo.getTinyIntCol().intValue());
        assertEquals(2.33, pojo.getDoubleCol(), 0.11);
        assertEquals(true, pojo.getBitCol());
        assertEquals("b", pojo.getSetCol());

        pojo = dao.queryByPk(8, new DalHints());
        //exclude列没有更新
        assertEquals(4L, pojo.getBigIntCol().longValue());
        assertEquals(4, pojo.getSmallIntCol().intValue());
        assertEquals("a", pojo.getCharCol());
        //include列更新
        assertEquals(new BigDecimal(2016), pojo.getDecimalCol());
        assertEquals("new", pojo.getVarCharCol());
        //其余列没有更新
        assertEquals(4, pojo.getIntCol().intValue());
        assertEquals(4, pojo.getMediumIntCol().intValue());
        assertEquals(253, pojo.getTinyIntCol().intValue());
        assertEquals(5.66, pojo.getDoubleCol(), 0.11);
        assertEquals(false, pojo.getBitCol());
        assertEquals("a", pojo.getSetCol());
    }

    @Test
    public void testtest_build_query_notnull() throws Exception {
        String CharCol = "a%";
        String SetCol = "a";
        Boolean BitCol = false;
        Integer IntCol = 3;
        Integer SmallIntCol = 20;
        Integer MediumIntCol = 50;
        BigDecimal DecimalCol = new BigDecimal(2015);
        Long BigIntCol_start = 70L;
        Long BigIntCol_end = 90L;
        List<String> varCharCol = new ArrayList<>();
        varCharCol.add("hello");
        varCharCol.add("colorful");
        varCharCol.add("world");
        List<AllTypesOnMysql> ret = dao.test_build_query_notnull(CharCol, SetCol, BitCol,
                IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
                BigIntCol_end, varCharCol, new DalHints());
        assertEquals(2, ret.size());
        assertNotEquals("2012-12-12 12:12:12", ret.get(0).getTimeStampCol().toString());
    }

    @Test
    public void testFreeSqlLikePattern() throws Exception {
        List<AllTypesOnMysql> ret1 = dao.testFreeSqlLikePattern("n", MatchPattern.BEGIN_WITH);
        assertEquals(2, ret1.size());
        assertEquals(100, ret1.get(0).getBigIntCol().intValue());
        assertEquals(2, ret1.get(1).getBigIntCol().intValue());

        List<AllTypesOnMysql> ret2 = dao.testFreeSqlLikePattern("e", MatchPattern.CONTAINS);
        assertEquals(4, ret2.size());

        List<AllTypesOnMysql> ret3 = dao.testFreeSqlLikePattern("ful", MatchPattern.END_WITH);
        assertEquals(2, ret3.size());

        List<AllTypesOnMysql> ret4 = dao.testFreeSqlLikePattern("%ful", MatchPattern.USER_DEFINED);
        assertEquals(2, ret4.size());
    }

    @Test
    public void testFreeSqlNotLikePattern() throws Exception {
        List<AllTypesOnMysql> ret1 = dao.testFreeSqlNotLikePattern("n", MatchPattern.BEGIN_WITH);
        assertEquals(6, ret1.size());

        List<AllTypesOnMysql> ret2 = dao.testFreeSqlNotLikePattern("e", MatchPattern.CONTAINS);
        assertEquals(4, ret2.size());

        List<AllTypesOnMysql> ret3 = dao.testFreeSqlNotLikePattern("ful", MatchPattern.END_WITH);
        assertEquals(6, ret3.size());

        List<AllTypesOnMysql> ret4 = dao.testFreeSqlNotLikePattern("%ful", MatchPattern.USER_DEFINED);
        assertEquals(6, ret4.size());
    }

    @Test
    public void testFreeSqlGroupByHaving() throws Exception {
        List<String> VarCharColList = new ArrayList<>();
        VarCharColList.add("hello");
        VarCharColList.add("world");
        List<Map<String, Object>> retList = dao.testFreeSqlGroupByHaving2(VarCharColList);
        assertEquals("hello", retList.get(0).get("VarCharCol").toString());
        assertEquals(2, Integer.parseInt(retList.get(0).get("Count").toString()));

        assertEquals("world", retList.get(1).get("VarCharCol").toString());
        assertEquals(2, Integer.parseInt(retList.get(1).get("Count").toString()));

        List<Map<String, Object>> retList2 = dao.testFreeSqlGroupByHaving(VarCharColList);
        assertEquals("hello", retList2.get(0).get("VarCharCol").toString());
        assertEquals(2, Integer.parseInt(retList2.get(0).get("Count").toString()));

        assertEquals("world", retList2.get(1).get("VarCharCol").toString());
        assertEquals(2, Integer.parseInt(retList2.get(1).get("Count").toString()));
    }

    @Test
    public void testFreeSqlNotNotNull() throws Exception {
        int low = 2;
        int upper = 4;
        List<Long> bigIntCol = new ArrayList<>();
        bigIntCol.add(1l);
        bigIntCol.add(70l);
        String varCharCol = "%ful";
        List<AllTypesOnMysql> ret = dao.testNotNotNull(low, upper, bigIntCol, varCharCol);
        assertEquals(2, ret.size());
        assertEquals(2, ret.get(0).getIdallTypes().intValue());
        assertEquals(4, ret.get(1).getIdallTypes().intValue());
    }

    @Test
    public void testFreeSqlNotNullable() throws Exception {
        List<Long> bigIntCol = new ArrayList<>();
        bigIntCol.add(1l);
        bigIntCol.add(null);
        String varCharCol = null;
        List<AllTypesOnMysql> ret = dao.testNotNullable(null, 4, bigIntCol, varCharCol);
        assertEquals(7, ret.size());
    }

    @Test
    public void testFreeSqlQueryNotNullWithSet() throws Exception {
        String CharCol = "a%";
        String SetCol = "a";
        Boolean BitCol = false;
        Integer IntCol = 3;
        Integer SmallIntCol = 20;
        Integer MediumIntCol = 50;
        BigDecimal DecimalCol = new BigDecimal(2015);
        Long BigIntCol_start = 70L;
        Long BigIntCol_end = 90L;
        List<String> varCharCol = new ArrayList<>();
        varCharCol.add("hello");
        varCharCol.add("colorful");
        varCharCol.add("world");
        List<AllTypesOnMysql> ret = dao.testFreeSqlQueryNotNullWithSet(CharCol, SetCol, BitCol,
                IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
                BigIntCol_end, varCharCol, new DalHints());
        assertEquals(2, ret.size());
        assertNotEquals("2012-12-12 12:12:12", ret.get(0).getTimeStampCol().toString());
    }

    @Test
    public void testFreeSqlQueryNotNull() throws Exception {
        String CharCol = "a%";
        String SetCol = "a";
        Boolean BitCol = false;
        Integer IntCol = 3;
        Integer SmallIntCol = 20;
        Integer MediumIntCol = 50;
        BigDecimal DecimalCol = new BigDecimal(2015);
        Long BigIntCol_start = 70L;
        Long BigIntCol_end = 90L;
        List<String> varCharCol = new ArrayList<>();
        varCharCol.add("hello");
        varCharCol.add("colorful");
        varCharCol.add("world");
        List<AllTypesOnMysql> ret = dao.testFreeSqlQueryNotNull(CharCol, SetCol, BitCol,
                IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
                BigIntCol_end, varCharCol, new DalHints());
        assertEquals(2, ret.size());
        assertNotEquals("2012-12-12 12:12:12", ret.get(0).getTimeStampCol().toString());
    }

    @Test
    public void testFreeSqlSetNullable() throws Exception {
        List<AllTypesOnMysql> ret1 = dao.testFreeSqlSetNullableAndIncludeAll(null, new DalHints());
        assertEquals(8, ret1.size());

        List<AllTypesOnMysql> ret2 = dao.testFreeSqlSetNullableAndExcludeAll(null, new DalHints());
        assertEquals(0, ret2.size());
    }

    @Test
    public void testFreeSqlQueryWithAppendNullable() throws Exception {
        String CharCol = null;
        String SetCol = null;
        Boolean BitCol = null;
        Integer IntCol = null;
        Integer SmallIntCol = null;
        Integer MediumIntCol = null;
        BigDecimal DecimalCol = null;
        Long BigIntCol_start = null;
        Long BigIntCol_end = null;
        List<String> varCharCol = new ArrayList<>();
        varCharCol.add("hello");
        varCharCol.add(null);
        varCharCol.add("world");
        List<AllTypesOnMysql> ret = dao.testFreeSqlQueryWithAppendNullable(CharCol, SetCol, BitCol,
                IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
                BigIntCol_end, varCharCol, new DalHints());
        assertEquals(4, ret.size());
        assertNotEquals("2012-12-12 12:12:12", ret.get(0).getTimeStampCol().toString());
    }

    @Test
    public void testFreeSqlQueryNullable() throws Exception {
        String CharCol = null;
        String SetCol = null;
        Boolean BitCol = null;
        Integer IntCol = null;
        Integer SmallIntCol = null;
        Integer MediumIntCol = null;
        BigDecimal DecimalCol = null;
        Long BigIntCol_start = null;
        Long BigIntCol_end = null;
        List<String> varCharCol = new ArrayList<>();
        varCharCol.add("hello");
        varCharCol.add(null);
        varCharCol.add("world");
        List<AllTypesOnMysql> ret = dao.testFreeSqlQueryNullable(CharCol, SetCol, BitCol,
                IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
                BigIntCol_end, varCharCol, new DalHints());
        assertEquals(4, ret.size());
        assertNotEquals("2012-12-12 12:12:12", ret.get(0).getTimeStampCol().toString());
    }

    @Test
    public void testtest_build_query_nullable() throws Exception {
        String CharCol = null;
        String SetCol = null;
        Boolean BitCol = null;
        Integer IntCol = null;
        Integer SmallIntCol = null;
        Integer MediumIntCol = null;
        BigDecimal DecimalCol = null;
        Long BigIntCol_start = null;
        Long BigIntCol_end = null;
        List<String> varCharCol = new ArrayList<>();
        varCharCol.add("hello");
        varCharCol.add(null);
        varCharCol.add("world");
        List<AllTypesOnMysql> ret = dao.test_build_query_nullable(CharCol, SetCol, BitCol,
                IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
                BigIntCol_end, varCharCol, new DalHints());
        assertEquals(4, ret.size());
        assertNotEquals("2012-12-12 12:12:12", ret.get(0).getTimeStampCol().toString());
    }

    @Test
    public void testtest_build_query_firstnullable() throws Exception {
        String CharCol = null;
        String SetCol = null;
        Boolean BitCol = null;
        Integer IntCol = null;
        Integer SmallIntCol = null;
        Integer MediumIntCol = null;
        BigDecimal DecimalCol = null;
        Long BigIntCol_start = null;
        Long BigIntCol_end = null;
        List<String> varCharCol = new ArrayList<>();
        varCharCol.add("hello");
        varCharCol.add(null);
        varCharCol.add("world");
        Date date = Date.valueOf("2016-5-3");
//		Date date=new Timestamp(DateUtil.stringToDate("2016-06-18","yyyy-MM-dd").getTime())
        AllTypesOnMysql ret = dao.test_build_query_firstnullable(CharCol, SetCol, BitCol,
                IntCol, SmallIntCol, MediumIntCol, DecimalCol, BigIntCol_start,
                BigIntCol_end, varCharCol, date, new DalHints());
        assertNull(ret);
    }

    @Test
    public void testtestBuildDelete() throws Exception {
        Integer id = 1;// Test value here
        int ret = dao.testBuildDelete(id);
        AllTypesOnMysql daoPojo = dao.queryByPk(1);
        assertNull(daoPojo);

        ret = dao.testBuildDelete(2, null);
        daoPojo = dao.queryByPk(2);
        assertNull(daoPojo);
    }

    @Test
    public void testtestBuildInsert() throws Exception {
        String VarCharCol = "buildinsert";// Test value here
        Integer TinyIntCol = 3;// Test value here
        int ret = dao.testBuildInsert(VarCharCol, TinyIntCol);
        AllTypesOnMysql daoPojo = dao.queryByPk(5);
        assertNotNull(daoPojo);

        ret = dao.testBuildInsert(VarCharCol, TinyIntCol, null);
        daoPojo = dao.queryByPk(5);
        assertNotNull(daoPojo);
    }

    @Test
    public void testtestBuildUpdate() throws Exception {
        String VarCharCol = "buildupdate";// Test value here
        Integer id = 3;// Test value here
        int ret = dao.testBuildUpdate(VarCharCol, id);

        AllTypesOnMysql daoPojo = dao.queryByPk(3);
        assertEquals("buildupdate", daoPojo.getVarCharCol());

        ret = dao.testBuildUpdate(VarCharCol, id, null);

        daoPojo = dao.queryByPk(3);
        assertEquals("buildupdate", daoPojo.getVarCharCol());
    }

    @Test
    public void testtestBuildQueryPojoFirst() throws Exception {
        Integer id = 1;// Test value here
        AllTypesOnMysql ret = dao.testBuildQueryPojoFirst(id);
        assertEquals("world", ret.getVarCharCol());
        assertEquals("world", ret.getVarCharCol());
        ret = dao.testBuildQueryPojoFirst(id, null);
        assertEquals("world", ret.getVarCharCol());
    }

    @Test
    public void testtestBuildQueryPojoList() throws Exception {
        Integer id = 1;// Test value here
        List<AllTypesOnMysql> ret = dao.testBuildQueryPojoList(id);
        assertEquals(7, ret.size());

        ret = dao.testBuildQueryPojoList(id, null);
        assertEquals(7, ret.size());
    }

    @Test
    public void testtestBuildQueryPojoListByPage() throws Exception {
        Integer id = 1;// Test value here
        List<AllTypesOnMysql> ret = dao.testBuildQueryPojoListByPage(id, 2, 2);
        assertEquals(2, ret.size());

        ret = dao.testBuildQueryPojoListByPage(id, 2, 2, null);
        assertEquals(2, ret.size());
    }

    @Test
    public void testtestBuildQueryPojoSingle() throws Exception {
        Integer id = 2;// Test value here
        AllTypesOnMysql ret = dao.testBuildQueryPojoSingle(id);
        assertEquals("new", ret.getVarCharCol());

        ret = dao.testBuildQueryPojoSingle(id, null);
        assertEquals("new", ret.getVarCharCol());
    }

    @Test
    public void testtestBuildQueryFieldFirst() throws Exception {
        Integer id = 1;// Test value here
        int ret = dao.testBuildQueryFieldFirst(id);
        assertEquals(4, ret);

        ret = dao.testBuildQueryFieldFirst(id, null);
        assertEquals(4, ret);
    }

    @Test
    public void testtestBuildQueryFieldList() throws Exception {
        Integer id = 1;// Test value here
        List<Integer> ret = dao.testBuildQueryFieldList(id);
        assertEquals(7, ret.size());

        ret = dao.testBuildQueryFieldList(id, null);
        assertEquals(7, ret.size());
    }

    @Test
    public void testtestBuildQueryFieldListByPage() throws Exception {
        Integer id = 1;// Test value here
        List<Integer> ret = dao.testBuildQueryFieldListByPage(id, 2, 2);
        assertEquals(2, ret.size());

        ret = dao.testBuildQueryFieldListByPage(id, 2, 2, null);
        assertEquals(2, ret.size());
    }


    @Test
    public void testtestBuildQueryFieldSingle() throws Exception {
        Integer id = 2;// Test value here
        Integer ret = dao.testBuildQueryFieldSingle(id);
        assertEquals(1, ret.intValue());

        ret = dao.testBuildQueryFieldSingle(id, null);
        assertEquals(1, ret.intValue());
    }

    @Test
    public void testtestDefUpdate() throws Exception {
        String varcharcol = "defupdate";// Test value here
        String charcol = "b";// Test value here
        int ret = dao.testDefUpdate(varcharcol, charcol);

        AllTypesOnMysql pojo = dao.queryByPk(2);
        assertEquals("defupdate", pojo.getVarCharCol());

        varcharcol = "defupdate1";
        ret = dao.testDefUpdate(varcharcol, charcol, null);

        pojo = dao.queryByPk(2);
        assertEquals("defupdate1", pojo.getVarCharCol());
    }

    @Test
    public void testFreeSqlUpdate() throws Exception {
        String varcharcol = "defupdate";// Test value here
        String charcol = "b";// Test value here
        int id = 2;
        dao.testFreeSqlUpdate(varcharcol, charcol, id, new DalHints());

        AllTypesOnMysql pojo = dao.queryByPk(2);
        assertEquals("defupdate", pojo.getVarCharCol());
        assertEquals("b", pojo.getCharCol());
    }

    @Test
    public void testFreeSqlDelete() throws Exception {
        Integer id = 1;// Test value here
        int ret = dao.testFreeSqlDelete(id, null);

        AllTypesOnMysql pojo = dao.queryByPk(1);
        assertNull(pojo);

        ret = dao.testFreeSqlDelete(2, null);

        pojo = dao.queryByPk(2);
        assertNull(pojo);
    }

    @Test
    public void testtestDefDelete() throws Exception {
        Integer id = 1;// Test value here
        int ret = dao.testDefDelete(id);

        AllTypesOnMysql pojo = dao.queryByPk(1);
        assertNull(pojo);

        ret = dao.testDefDelete(2);

        pojo = dao.queryByPk(2);
        assertNull(pojo);
    }

    @Test
    public void testFreeSqlInsert() throws Exception {
        String varcharcol = "defInsert";// Test value here
        Integer intcol = 1;// Test value here
        dao.testFreeSqlInsert(varcharcol, intcol, null);
        AllTypesOnMysql pojo = dao.queryByPk(9);
        assertEquals("defInsert", pojo.getVarCharCol());
    }

    @Test
    public void testtestDefInsert() throws Exception {
        String varcharcol = "defInsert";// Test value here
        Integer intcol = 1;// Test value here
        int ret = dao.testDefInsert(varcharcol, intcol);
        AllTypesOnMysql pojo = dao.queryByPk(9);
        assertEquals("defInsert", pojo.getVarCharCol());

        ret = dao.testDefInsert(varcharcol, intcol, null);
        pojo = dao.queryByPk(9);
        assertEquals("defInsert", pojo.getVarCharCol());
    }

    @Test
    public void testtestDefQueryPojoFirst() throws Exception {
        Integer id = 1;// Test value here
        AllTypesOnMysql ret = dao.testDefQueryPojoFirst(id);
        assertEquals("new", ret.getVarCharCol());

        ret = dao.testDefQueryPojoFirst(id, null);
        assertEquals("new", ret.getVarCharCol());
    }

    @Test
    public void testFreeSqlQueryPojoFirst() throws Exception {
        Integer id = 1;// Test value here
        AllTypesOnMysql ret = dao.testFreeSqlQueryFirst(id, null);
        assertEquals("new", ret.getVarCharCol());
    }

    @Test
    public void testtestDefQueryFieldFirst() throws Exception {
        Integer id = 1;// Test value here
        int ret = dao.testDefQueryFieldFirst(id);
        assertEquals(1, ret);

        ret = dao.testDefQueryFieldFirst(id, null);
        assertEquals(1, ret);
    }

    @Test
    public void testFreeSqlQueryFieldFirst() throws Exception {
        Integer id = 1;// Test value here
        int ret = dao.testFreeSQLBuilderQueryFieldFirst(id, null);
        assertEquals(1, ret);
    }

    @Test
    public void testtestDefQueryPojoList() throws Exception {
        List<Integer> intcol = new ArrayList<>();
        intcol.add(1);
        intcol.add(12);
        intcol.add(99);
        List<AllTypesOnMysql> ret = dao.testDefQueryPojoList(intcol);
        assertEquals(3, ret.size());

        ret = dao.testDefQueryPojoList(intcol, null);
        assertEquals(3, ret.size());
    }

    @Test
    public void testtestDefQueryPojoListByPage() throws Exception {
        Integer id = 1;// Test value here
        List<AllTypesOnMysql> ret = dao.testDefQueryPojoListByPage(id, 2, 2);
        assertEquals(2, ret.size());

        ret = dao.testDefQueryPojoListByPage(id, 2, 2, null);
        assertEquals(2, ret.size());
    }

    @Test
    public void testFreeSqlQueryPojoListByPage() throws Exception {
        Integer id = 1;// Test value here
        List<AllTypesOnMysql> ret = dao.testFreeSqlQueryListByPage(id, 2, 2, null);
        assertEquals(2, ret.size());
    }

    @Test
    public void testtestDefQueryFieldList() throws Exception {
        Integer id = 1;// Test value here
        List<Integer> ret = dao.testDefQueryFieldList(id);
        assertEquals(7, ret.size());

        ret = dao.testDefQueryFieldList(id, null);
        assertEquals(7, ret.size());

        ret = dao.testFreeSQLBuilderQueryFieldList(id, null);
        assertEquals(7, ret.size());
    }

    @Test
    public void testtestDefQueryFieldListByPage() throws Exception {
        Integer id = 1;// Test value here
        List<Integer> ret = dao.testDefQueryFieldListByPage(id, 2, 2);
        assertEquals(2, ret.size());

        ret = dao.testDefQueryFieldListByPage(id, 2, 2, null);
        assertEquals(2, ret.size());

        ret = dao.testFreeSQLBuilderQueryFieldListByPage(id, 2, 2, null);
        assertEquals(2, ret.size());
    }

    @Test
    public void testFreeSQLBuilderQueryMax() throws Exception {
        Integer id = 2;
        int maxIntCol = dao.testFreeSQLBuilderQueryMax(id, new DalHints());
        assertEquals(12, maxIntCol);
    }


    @Test
    public void testtestDefQueryPojoSingle() throws Exception {
        Integer id = 1;// Test value here
        AllTypesOnMysql ret = dao.testDefQueryPojoSingle(id);
        assertEquals("colorful", ret.getVarCharCol());

        ret = dao.testDefQueryPojoSingle(id, null);
        assertEquals("colorful", ret.getVarCharCol());

        ret = dao.testFreeSqlQuerySingle(id, null);
        assertEquals("colorful", ret.getVarCharCol());
    }

    @Test
    public void testtestDefQueryFieldSingle() throws Exception {
        Integer id = 1;// Test value here
        int ret = dao.testDefQueryFieldSingle(id);
        assertEquals(8, ret);

        ret = dao.testDefQueryFieldSingle(id, null);
        assertEquals(8, ret);

        ret = dao.testFreeSQLBuilderQueryFieldSingle(id, null);
        assertEquals(8, ret);
    }
}
