package noshardtest;


import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import dao.noshard.MasterOnlyOnMysqlDao;
import dao.shard.newVersionCode.PersonShardColModShardByDBOnMysqlDao;
import entity.MysqlPersonTable;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * JUnit test of PersonMasterOnlyOnMysqlDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
 **/
public class MasterOnlyOnMysqlDaoUnitTest {

    private static MasterOnlyOnMysqlDao dao = null;
    private static PersonShardColModShardByDBOnMysqlDao dao2 = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        dao = new MasterOnlyOnMysqlDao();
        dao2 = new PersonShardColModShardByDBOnMysqlDao();

//        先查询一遍并等待2秒，确保所有逻辑库的读库freshness已更新
        dao.count(null);
        Thread.sleep(2000);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }


    @Before
    public void setUp() throws Exception {
//		for(int i = 0; i < 10; i++) {
//			PersonMysqlPersonTable daoPojo = createPojo(i);
//
//			try {
//				dao.insert(new DalHints().enableIdentityInsert(), daoPojo);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
        dao.test_def_truncate(new DalHints());
        dao2.test_def_truncate(new DalHints().inShard(1));

        List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(
                3);
        for (int i = 0; i < 3; i++) {
            MysqlPersonTable daoPojo = new MysqlPersonTable();
            daoPojo.setAge(i + 20);
            daoPojo.setName("Master_" + i);
            daoPojos.add(daoPojo);
        }
        dao.insert(new DalHints(), daoPojos);

        List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(
                6);
        for (int i = 0; i < 6; i++) {
            MysqlPersonTable daoPojo = new MysqlPersonTable();
            daoPojo.setAge(i + 30);
            daoPojo.setName("Slave_" + i);
            daoPojos2.add(daoPojo);
        }
        dao2.insert(new DalHints().inShard(1), daoPojos2);
    }

    private MysqlPersonTable createPojo(int index) {
        MysqlPersonTable daoPojo = new MysqlPersonTable();

        //daoPojo.setId(index);
        //daoPojo set not null field

        return daoPojo;
    }

    private void changePojo(MysqlPersonTable daoPojo) {
        // Change a field to make pojo different with original one
    }

    private void changePojos(List<MysqlPersonTable> daoPojos) {
        for (MysqlPersonTable daoPojo : daoPojos)
            changePojo(daoPojo);
    }

    private void verifyPojo(MysqlPersonTable daoPojo) {
        //assert changed value
    }

    private void verifyPojos(List<MysqlPersonTable> daoPojos) {
        for (MysqlPersonTable daoPojo : daoPojos)
            verifyPojo(daoPojo);
    }

    @After
    public void tearDown() throws Exception {
//        dao.test_def_truncate(new DalHints());
//        dao2.test_def_truncate(new DalHints().inShard(1));
    }



    @Test
    public void testCountWithFreshness() throws Exception {
        //freshness>delay, read from slave
        int affected = dao.count(new DalHints().freshness(5));
        assertEquals(6, affected);

        //freshness=delay, read from slave
        affected = dao.count(new DalHints().freshness(2));
        assertEquals(6, affected);

        //freshness<delay,read from master
        affected = dao.count(new DalHints().freshness(1));
        assertEquals(3, affected);

        //freshness>delay but set masterOnly(), read from masterOnly
        affected = dao.count(new DalHints().freshness(5).masterOnly());
        assertEquals(3, affected);

        //freshness<delay but set slaveOnly(),read from slave
//        affected = dao.count(new DalHints().freshness(1).slaveOnly());
//        assertEquals(6, affected);
    }

    @Test
    public void testCount() throws Exception {
        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(3, affected);
    }


    @Test
    public void testDelete1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable daoPojo = createPojo(1);
        daoPojo.setID(2);
        int affected1 = dao.delete(hints, daoPojo);
        assertEquals(1, affected1);

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(2, affected);

        int affected2 = dao.delete(new DalHints().slaveOnly(), daoPojo);
        assertEquals(1, affected1);

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(5, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(2, affected);
    }

    @Test
    public void testDelete2() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(2);
        MysqlPersonTable daoPojo1 = createPojo(1);
        daoPojo1.setID(2);
        MysqlPersonTable daoPojo2 = createPojo(1);
        daoPojo2.setID(3);
        daoPojos.add(daoPojo1);
        daoPojos.add(daoPojo2);

        int[] affected1 = dao.delete(hints, daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected1);

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(1, affected);

        int[] affected2 = dao.delete(new DalHints().slaveOnly(), daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected2);

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(4, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(1, affected);
    }

    @Test
    public void testBatchDelete() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(2);
        MysqlPersonTable daoPojo1 = createPojo(1);
        daoPojo1.setID(2);
        MysqlPersonTable daoPojo2 = createPojo(1);
        daoPojo2.setID(3);
        daoPojos.add(daoPojo1);
        daoPojos.add(daoPojo2);

        int[] affected1 = dao.batchDelete(hints, daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected1);

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(1, affected);

        int[] affected2 = dao.batchDelete(new DalHints().slaveOnly(), daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected2);

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(4, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(1, affected);
    }

    @Test
    public void testQueryAllWithFreshness() throws Exception {
        List<MysqlPersonTable> list = dao.queryAll(new DalHints().freshness(3));
        assertEquals(6, list.size());

        list=dao.queryAll(new DalHints().freshness(2));
        assertEquals(6,list.size());

        list=dao.queryAll(new DalHints().freshness(1));
        assertEquals(3,list.size());

        list=dao.queryAll(new DalHints().freshness(3).masterOnly());
        assertEquals(3,list.size());

//        list=dao.queryAll(new DalHints().freshness(1).slaveOnly());
//        assertEquals(6,list.size());
    }

    @Test
    public void testQueryAll() throws Exception {
        List<MysqlPersonTable> list = dao.queryAll(new DalHints());
        assertEquals(6, list.size());

        list = dao.queryAll(new DalHints().slaveOnly());
        assertEquals(6, list.size());

        list = dao.queryAll(new DalHints().masterOnly());
        assertEquals(3, list.size());

    }

    @Test
    public void testInsert1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable daoPojo = createPojo(1);
        daoPojo.setName("masteronly");
        int affected1 = dao.insert(hints, daoPojo);
        assertEquals(1, affected1);

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(4, affected);

        int affected2 = dao.insert(new DalHints().slaveOnly(), daoPojo);
        assertEquals(1, affected2);

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(7, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(4, affected);
    }

    @Test
    public void testInsert2() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints());
        int[] affected1 = dao.insert(hints, daoPojos);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected1);

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);

        int[] affected2 = dao.insert(new DalHints().slaveOnly(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected2);

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(12, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);
    }

    @Test
    public void testInsert3() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        MysqlPersonTable daoPojo = createPojo(1);
        daoPojo.setName("masteronly");
        int affected1 = dao.insert(hints, keyHolder, daoPojo);
        assertEquals(1, affected1);
        assertEquals(1, keyHolder.size());

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(4, affected);

        int affected2 = dao.insert(new DalHints().slaveOnly(), keyHolder, daoPojo);
        assertEquals(1, affected2);
        assertEquals(1, keyHolder.size());

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(7, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(4, affected);
    }

    @Test
    public void testInsert4() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints());
        int[] affected1 = dao.insert(hints, keyHolder, daoPojos);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected1);
        assertEquals(6, keyHolder.size());

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);

        int[] affected2 = dao.insert(new DalHints().slaveOnly(), keyHolder, daoPojos);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected2);
        assertEquals(6, keyHolder.size());

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(12, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);
    }

    @Test
    public void testInsert5() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints());
        dao.batchInsert(hints, daoPojos);

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);

        dao.batchInsert(new DalHints().slaveOnly(), daoPojos);

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(12, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);
    }

    @Test
    public void testCombinedInsert1() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints());
        int affected1 = dao.combinedInsert(hints, daoPojos);
        assertEquals(6, affected1);

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);

        int affected2 = dao.combinedInsert(new DalHints().slaveOnly(), daoPojos);
        assertEquals(6, affected2);

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(12, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);
    }

    @Test
    public void testCombinedInsert2() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints());
        dao.combinedInsert(hints, keyHolder, daoPojos);
        assertEquals(6, keyHolder.size());

        int affected = dao.count(new DalHints());
        assertEquals(6, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);

        dao.combinedInsert(new DalHints().slaveOnly(), keyHolder, daoPojos);
        assertEquals(6, keyHolder.size());

        affected = dao.count(new DalHints().slaveOnly());
        assertEquals(12, affected);

        affected = dao.count(new DalHints().masterOnly());
        assertEquals(9, affected);
    }

    @Test
    public void testQueryAllByPageWithFreshness() throws Exception {
        int pageSize = 100;
        int pageNo = 1;
        List<MysqlPersonTable> list = dao.queryAllByPage(pageNo, pageSize,new DalHints().freshness(3));
        assertEquals(6, list.size());

        list=dao.queryAllByPage(pageNo,pageSize,new DalHints().freshness(2));
        assertEquals(6,list.size());

        list=dao.queryAllByPage(pageNo,pageSize,new DalHints().freshness(1));
        assertEquals(3,list.size());

        list=dao.queryAllByPage(pageNo,pageSize,new DalHints().freshness(3).masterOnly());
        assertEquals(3,list.size());
    }

    @Test
    public void testQueryAllByPage() throws Exception {
        DalHints hints = new DalHints();
        int pageSize = 100;
        int pageNo = 1;
        List<MysqlPersonTable> list = dao.queryAllByPage(pageNo, pageSize, hints);
        assertEquals(6, list.size());

        dao.queryAllByPage(pageNo, pageSize, new DalHints().slaveOnly());
        assertEquals(6, list.size());

        list = dao.queryAllByPage(pageNo, pageSize, hints.masterOnly());
        assertEquals(3, list.size());
    }

//	@Test
//	public void testQueryAll() throws Exception {
//		DalHints hints = new DalHints();
//		List<PersonMysqlPersonTable> list = dao.queryAll(hints);
//		assertEquals(6, list.size());
//
//		list = dao.queryAll(hints);
//		assertEquals(3, list.size());
//	}

    @Test
    public void testQueryByPk1() throws Exception {
        Number id = 1;
        DalHints hints = new DalHints();
        MysqlPersonTable affected = dao.queryByPk(id, hints);
        assertNotNull(affected);
        assertEquals("Slave_0", affected.getName());

        affected = dao.queryByPk(id, hints.masterOnly());
        assertNotNull(affected);
        assertEquals("Master_0", affected.getName());

        affected=dao.queryByPk(1,new DalHints().freshness(3));
        assertEquals("Slave_0",affected.getName());

        affected=dao.queryByPk(1,new DalHints().freshness(2));
        assertEquals("Slave_0",affected.getName());

        affected=dao.queryByPk(1,new DalHints().freshness(1));
        assertEquals("Master_0",affected.getName());

        affected=dao.queryByPk(1,new DalHints().freshness(3).masterOnly());
        assertEquals("Master_0",affected.getName());
    }

    @Test
    public void testQueryLike() throws Exception{
        MysqlPersonTable pojo=new MysqlPersonTable();
        pojo.setName("Slave_0");
        List<MysqlPersonTable> ret=dao.queryLike(pojo,new DalHints().freshness(3));
        assertEquals(1,ret.size());

        ret=dao.queryLike(pojo,new DalHints().freshness(2));
        assertEquals(1,ret.size());

        ret=dao.queryLike(pojo,new DalHints().freshness(1));
        assertEquals(0,ret.size());

        ret=dao.queryLike(pojo,new DalHints().freshness(3).masterOnly());
        assertEquals(0,ret.size());

        ret=dao.queryLike(pojo,new DalHints());
        assertEquals(1,ret.size());

        ret=dao.queryLike(pojo,new DalHints().masterOnly());
        assertEquals(0,ret.size());
    }


    @Test
    public void testQueryByPk2() throws Exception {
        MysqlPersonTable pk = createPojo(1);
        pk.setID(2);

        DalHints hints = new DalHints();
        MysqlPersonTable affected = dao.queryByPk(pk, hints);
        assertNotNull(affected);
        assertEquals("Slave_1", affected.getName());

        affected = dao.queryByPk(pk, new DalHints().slaveOnly());
        assertNotNull(affected);
        assertEquals("Slave_1", affected.getName());

        affected = dao.queryByPk(pk, hints.masterOnly());
        assertNotNull(affected);
        assertEquals("Master_1", affected.getName());

        affected=dao.queryByPk(pk,new DalHints().freshness(3));
        assertEquals("Slave_1",affected.getName());

        affected=dao.queryByPk(pk,new DalHints().freshness(2));
        assertEquals("Slave_1",affected.getName());

        affected=dao.queryByPk(pk,new DalHints().freshness(1));
        assertEquals("Master_1",affected.getName());

        affected=dao.queryByPk(pk,new DalHints().freshness(3).masterOnly());
        assertEquals("Master_1",affected.getName());

    }

    @Test
    public void testUpdate1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable daoPojo = createPojo(1);
        daoPojo.setID(2);
        daoPojo.setName("update");
        int affected = dao.update(hints, daoPojo);
        assertEquals(1, affected);

        daoPojo = dao.queryByPk(2, null);
        assertEquals("Slave_1", daoPojo.getName());

        daoPojo = dao.queryByPk(2, hints.masterOnly());
        assertEquals("update", daoPojo.getName());

        daoPojo.setName("updateSlave");
        int affected2 = dao.update(new DalHints().slaveOnly(), daoPojo);
        assertEquals(1, affected2);

        daoPojo = dao.queryByPk(2, new DalHints().slaveOnly());
        assertEquals("updateSlave", daoPojo.getName());

        daoPojo = dao.queryByPk(2, hints.masterOnly());
        assertEquals("update", daoPojo.getName());
    }

    @Test
    public void testUpdate2() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(2);
        MysqlPersonTable daoPojo1 = createPojo(1);
        daoPojo1.setID(2);
        daoPojo1.setName("update2");
        MysqlPersonTable daoPojo2 = createPojo(1);
        daoPojo2.setID(3);
        daoPojo2.setName("update3");
        daoPojos.add(daoPojo1);
        daoPojos.add(daoPojo2);

        int[] affected1 = dao.update(hints, daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected1);

        MysqlPersonTable daoPojo = dao.queryByPk(2, hints);
        assertEquals("Slave_1", daoPojo.getName());

        daoPojo = dao.queryByPk(3, hints);
        assertEquals("Slave_2", daoPojo.getName());

        daoPojo = dao.queryByPk(2, hints.masterOnly());
        assertEquals("update2", daoPojo.getName());

        daoPojo = dao.queryByPk(3, hints.masterOnly());
        assertEquals("update3", daoPojo.getName());

        daoPojos.get(0).setName("updateSlave2");
        daoPojos.get(1).setName("updateSlave3");
        affected1 = dao.update(new DalHints().slaveOnly(), daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected1);

        daoPojo = dao.queryByPk(2, new DalHints().slaveOnly());
        assertEquals("updateSlave2", daoPojo.getName());

        daoPojo = dao.queryByPk(3, new DalHints().slaveOnly());
        assertEquals("updateSlave3", daoPojo.getName());

        daoPojo = dao.queryByPk(2, new DalHints().masterOnly());
        assertEquals("update2", daoPojo.getName());

        daoPojo = dao.queryByPk(3, new DalHints().masterOnly());
        assertEquals("update3", daoPojo.getName());
    }

    @Test
    public void testBatchUpdate() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(2);
        MysqlPersonTable daoPojo1 = createPojo(1);
        daoPojo1.setID(2);
        daoPojo1.setName("update2");
        MysqlPersonTable daoPojo2 = createPojo(1);
        daoPojo2.setID(3);
        daoPojo2.setName("update3");
        daoPojos.add(daoPojo1);
        daoPojos.add(daoPojo2);

        int[] affected = dao.batchUpdate(hints, daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected);

        MysqlPersonTable daoPojo = dao.queryByPk(2, hints);
        assertEquals("Slave_1", daoPojo.getName());

        daoPojo = dao.queryByPk(3, hints);
        assertEquals("Slave_2", daoPojo.getName());

        daoPojo = dao.queryByPk(2, hints.masterOnly());
        assertEquals("update2", daoPojo.getName());

        daoPojo = dao.queryByPk(3, hints.masterOnly());
        assertEquals("update3", daoPojo.getName());

        daoPojos.get(0).setName("updateSlave2");
        daoPojos.get(1).setName("updateSlave3");

        affected = dao.batchUpdate(new DalHints().slaveOnly(), daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected);

        daoPojo = dao.queryByPk(2, new DalHints().slaveOnly());
        assertEquals("updateSlave2", daoPojo.getName());

        daoPojo = dao.queryByPk(3, new DalHints().slaveOnly());
        assertEquals("updateSlave3", daoPojo.getName());

        daoPojo = dao.queryByPk(2, new DalHints().masterOnly());
        assertEquals("update2", daoPojo.getName());

        daoPojo = dao.queryByPk(3, new DalHints().masterOnly());
        assertEquals("update3", daoPojo.getName());

    }

    @Test
    public void testtest_build_delete() throws Exception {
        //Integer Age = null;// Test value here
        //int ret = dao.test_build_delete(Age, new DalHints());
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(22);
        Age.add(30);

        int ret = dao.test_build_delete(Age, new DalHints());

        List<MysqlPersonTable> pojos = dao.queryAll(new DalHints());
        assertEquals(6, pojos.size());

        pojos = dao.queryAll(new DalHints().masterOnly());
        assertEquals(1, pojos.size());

        dao.test_build_delete(Age, new DalHints().slaveOnly());

        pojos = dao.queryAll(new DalHints().slaveOnly());
        assertEquals(5, pojos.size());

        pojos = dao.queryAll(new DalHints().masterOnly());
        assertEquals(1, pojos.size());

    }

    @Test
    public void testtest_build_insert() throws Exception {
        String Name = "insert";// Test value here
        Integer Age = 50;// Test value here
        int ret = dao.test_build_insert(Name, Age, new DalHints());

        List<MysqlPersonTable> pojos = dao.queryAll(new DalHints());
        assertEquals(6, pojos.size());

        pojos = dao.queryAll(new DalHints().masterOnly());
        assertEquals(4, pojos.size());

        dao.test_build_insert(Name, Age, new DalHints().slaveOnly());

        pojos = dao.queryAll(new DalHints().slaveOnly());
        assertEquals(7, pojos.size());

        pojos = dao.queryAll(new DalHints().masterOnly());
        assertEquals(4, pojos.size());

    }

    @Test
    public void testtest_build_update() throws Exception {
        String Name = "update";// Test value here
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(22);
        Age.add(30);

        int ret = dao.test_build_update(Name, Age, new DalHints());

        MysqlPersonTable daoPojo = dao.queryByPk(1, new DalHints());
        assertEquals("Slave_0", daoPojo.getName());


        daoPojo = dao.queryByPk(1, new DalHints().masterOnly());
        assertEquals("update", daoPojo.getName());

        daoPojo = dao.queryByPk(3, new DalHints().masterOnly());
        assertEquals("update", daoPojo.getName());

        Name="updateSlave";
        ret = dao.test_build_update(Name, Age, new DalHints().slaveOnly());

        daoPojo = dao.queryByPk(1, new DalHints());
        assertEquals("updateSlave", daoPojo.getName());


        daoPojo = dao.queryByPk(1, new DalHints().masterOnly());
        assertEquals("update", daoPojo.getName());

        daoPojo = dao.queryByPk(3, new DalHints().masterOnly());
        assertEquals("update", daoPojo.getName());
    }

    @Test
    public void testtest_build_update_part() throws Exception {
        String Name = "update";
        int ID = 2;
        int ret = dao.test_update_part(Name, null, ID, new DalHints());
    }

    @Test
    public void testtest_build_first() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(22);
        Age.add(30);

        MysqlPersonTable ret = dao.test_build_first(Age, new DalHints());
        assertEquals("Slave_0", ret.getName());

        ret = dao.test_build_first(Age, new DalHints().slaveOnly());
        assertEquals("Slave_0", ret.getName());

        ret = dao.test_build_first(Age, new DalHints().masterOnly());
        assertEquals("Master_0", ret.getName());

        ret=dao.test_build_first(Age,new DalHints().freshness(3));
        assertEquals("Slave_0",ret.getName());

        ret=dao.test_build_first(Age,new DalHints().freshness(2));
        assertEquals("Slave_0",ret.getName());

        ret=dao.test_build_first(Age,new DalHints().freshness(1));
        assertEquals("Master_0",ret.getName());

        ret=dao.test_build_first(Age,new DalHints().freshness(3).masterOnly());
        assertEquals("Master_0",ret.getName());

    }

    @Test
    public void testtest_build_query_list() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(22);
        Age.add(30);
        List<MysqlPersonTable> ret = dao.test_build_query_list(Age, new DalHints());
        assertEquals(1, ret.size());

        ret = dao.test_build_query_list(Age, new DalHints().slaveOnly());
        assertEquals(1, ret.size());

        ret = dao.test_build_query_list(Age, new DalHints().masterOnly());
        assertEquals(2, ret.size());

        ret=dao.test_build_query_list(Age,new DalHints().freshness(3));
        assertEquals(1,ret.size());

        ret=dao.test_build_query_list(Age,new DalHints().freshness(2));
        assertEquals(1,ret.size());

        ret=dao.test_build_query_list(Age,new DalHints().freshness(1));
        assertEquals(2,ret.size());

        ret=dao.test_build_query_list(Age,new DalHints().freshness(3).masterOnly());
        assertEquals(2,ret.size());
    }

    @Test
    public void testtest_build_single() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
//		Age.add(22);
        Age.add(30);
        MysqlPersonTable ret = dao.test_build_single(Age, new DalHints());
        assertEquals("Slave_0", ret.getName());

        ret = dao.test_build_single(Age, new DalHints().slaveOnly());
        assertEquals("Slave_0", ret.getName());

        ret = dao.test_build_single(Age, new DalHints().masterOnly());
        assertEquals("Master_0", ret.getName());

        ret=dao.test_build_single(Age,new DalHints().freshness(3));
        assertEquals("Slave_0",ret.getName());

        ret=dao.test_build_single(Age,new DalHints().freshness(2));
        assertEquals("Slave_0",ret.getName());

        ret=dao.test_build_single(Age,new DalHints().freshness(1));
        assertEquals("Master_0",ret.getName());

        ret=dao.test_build_single(Age,new DalHints().freshness(3).masterOnly());
        assertEquals("Master_0",ret.getName());
    }

    @Test
    public void testtest_build_queryFieldFirst() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(22);
        Age.add(30);

        String ret = dao.test_build_queryFieldFirst(Age, new DalHints());
        assertEquals("Slave_0", ret);

        ret = dao.test_build_queryFieldFirst(Age, new DalHints().masterOnly());
        assertEquals("Master_0", ret);
    }

    @Test
    public void testtest_build_queryFieldList() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(22);
        Age.add(30);
        List<String> ret = dao.test_build_queryFieldList(Age, new DalHints());
        assertEquals(1, ret.size());

        ret = dao.test_build_queryFieldList(Age, new DalHints().slaveOnly());
        assertEquals(1, ret.size());

        ret = dao.test_build_queryFieldList(Age, new DalHints().masterOnly());
        assertEquals(2, ret.size());

        ret=dao.test_build_queryFieldList(Age,new DalHints().freshness(3));
        assertEquals(1,ret.size());

        ret=dao.test_build_queryFieldList(Age,new DalHints().freshness(2));
        assertEquals(1,ret.size());

        ret=dao.test_build_queryFieldList(Age,new DalHints().freshness(1));
        assertEquals(2,ret.size());

        ret=dao.test_build_queryFieldList(Age,new DalHints().freshness(3).masterOnly());
        assertEquals(2,ret.size());
    }


    @Test
    public void testtest_build_queryFieldSingle() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
//		Age.add(22);
        Age.add(30);
        String ret = dao.test_build_queryFieldSingle(Age, new DalHints());
        assertEquals("Slave_0", ret);

        ret = dao.test_build_queryFieldSingle(Age, new DalHints().masterOnly());
        assertEquals("Master_0", ret);
    }

    @Test
    public void testtest_def_truncate() throws Exception {
        dao.test_def_truncate(new DalHints());

        List<MysqlPersonTable> pojos = dao.queryAll(new DalHints());
        assertEquals(6, pojos.size());

        pojos = dao.queryAll(new DalHints().masterOnly());
        assertEquals(0, pojos.size());

        dao.test_def_truncate(new DalHints().slaveOnly());

        pojos = dao.queryAll(new DalHints());
        assertEquals(0, pojos.size());

        pojos = dao.queryAll(new DalHints().masterOnly());
        assertEquals(0, pojos.size());
    }

    @Test
    public void testtest_def_query() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(22);
        Age.add(30);
        List<MysqlPersonTable> ret = dao.test_def_query(Age, new DalHints());
        assertEquals(1, ret.size());

        ret=dao.test_def_query(Age,new DalHints().slaveOnly());
        assertEquals(1,ret.size());

        ret = dao.test_def_query(Age, new DalHints().masterOnly());
        assertEquals(2, ret.size());

        ret=dao.test_def_query(Age,new DalHints().freshness(3));
        assertEquals(1,ret.size());

        ret=dao.test_def_query(Age,new DalHints().freshness(2));
        assertEquals(1,ret.size());

        ret=dao.test_def_query(Age,new DalHints().freshness(1));
        assertEquals(2,ret.size());

        ret=dao.test_def_query(Age,new DalHints().freshness(3).masterOnly());
        assertEquals(2,ret.size());
    }
}
