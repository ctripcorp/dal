package noshardtest;


import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.sqlbuilder.MatchPattern;
import dao.noshard.EntityExtendsOnMysqlDao;
import dao.noshard.NoShardOnMysqlDao;
import entity.MysqlPersonExtendsUpdatableEntity;
import entity.MysqlPersonTable;
import entity.TestAlias;
import entity.extendsEntity.MysqlPersonTable1;
import entity.extendsEntity.MysqlPersonTable2;
import entity.extendsEntity.MysqlPersonTable3;
import entity.extendsEntity.MysqlPersonTable5;
import org.junit.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * JUnit test of PersonGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
 **/
public class EntityExtendsOnMysqlTest {

    private static final String DATA_BASE = "noShardTestOnMysql";

    private static DalClient client = null;
    private static NoShardOnMysqlDao dao=null;
    private static EntityExtendsOnMysqlDao dao1 = null;
    private static EntityExtendsOnMysqlDao dao2 = null;
    private static EntityExtendsOnMysqlDao dao3 = null;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        client = DalClientFactory.getClient(DATA_BASE);
        dao=new NoShardOnMysqlDao();
        dao1 = new EntityExtendsOnMysqlDao(MysqlPersonTable1.class);
        dao2=new EntityExtendsOnMysqlDao(MysqlPersonTable3.class);
        dao3=new EntityExtendsOnMysqlDao(MysqlPersonTable5.class);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        dao.test_def_update(null);

//		List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//				6);
//		for (int i = 0; i < 6; i++) {
//			MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//			daoPojo.setAge(i + 20);
//			if(i%2==0)
//			daoPojo.setName("Initial_Shard_0" + i);
//			else
//				daoPojo.setName("Initial_Shard_1" + i);
//			daoPojos.add(daoPojo);
//		}
//		dao.insert(new DalHints(), daoPojos);
        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();

            if (i % 2 == 0) {
                daoPojo.setAge(20);
                daoPojo.setName("Initial_Shard_0" + i);
            } else {
                daoPojo.setAge(21);
                daoPojo.setName("Initial_Shard_1" + i);
            }
            daoPojos.add(daoPojo);
        }
        dao.insert(new DalHints(), daoPojos);
    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
//		Thread.sleep(5000);
    }


//    @Test
//    public void testDalTableDaoQueryTop() throws Exception{
//        Integer age = 20;
//        Integer count = 2;
//        List<MysqlPersonExtendsUpdatableEntity> list = dao.queryTop(age, count, null);
//        assertEquals(2, list.size());
//    }
//
//    @Test
//    public void testDalTableDaoQueryFrom() throws Exception{
//        Integer age=21;
//        List<MysqlPersonExtendsUpdatableEntity> list1=dao.queryFromWithOrderBy(age,0,3,null);
//        assertEquals(3,list1.size());
//
//        List<MysqlPersonExtendsUpdatableEntity> list2=dao.queryFromWithOrderBy(age,1,3,null);
//        assertEquals(2,list2.size());
//
//
//        List<MysqlPersonExtendsUpdatableEntity> list3= dao.queryFromWithoutOrderBy(age, 0, 3, null);
//        assertEquals(3,list3.size());
//
//        List<MysqlPersonExtendsUpdatableEntity> list4=dao.queryFromWithoutOrderBy(age,1,3,null);
//        assertEquals(2,list4.size());
//
//    }


    @Test
    public void testCount() throws Exception {
        int affected = dao1.count(new DalHints());
        assertEquals(6, affected);

        affected = dao2.count(new DalHints());
        assertEquals(6, affected);

        affected = dao3.count(new DalHints());
        assertEquals(6, affected);
    }

    @Test
    public void testDao1Delete1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable1 daoPojo = new MysqlPersonTable1();
        daoPojo.setAge(20);
        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);
        int affected = dao1.delete(hints, daoPojo);
        assertEquals(1, affected);
    }

    @Test
    public void testDao2Delete1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable3 daoPojo = new MysqlPersonTable3();
        daoPojo.setAge(20);
        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);
        int affected = dao2.delete(hints, daoPojo);
        assertEquals(1, affected);
    }

    @Test
    public void testDao3Delete1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable5 daoPojo = new MysqlPersonTable5();
        daoPojo.setAge(20);
        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);
        int affected = dao3.delete(hints, daoPojo);
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
		List<MysqlPersonTable1> list1 = dao1.queryAll(new DalHints().selectByNames());
		assertEquals(6, list1.size());

        List<MysqlPersonTable3> list2 = dao2.queryAll(new DalHints().selectByNames());
        assertEquals(6, list2.size());

        List<MysqlPersonTable5> list3 = dao3.queryAll(new DalHints().selectByNames());
        assertEquals(6, list3.size());
	}

	@Test
    public void testQueryAllByPage() throws  Exception{
        List<MysqlPersonTable1> list1=dao1.queryAllByPage(1,3,new DalHints().selectByNames());
        assertEquals(3,list1.size());

        List<MysqlPersonTable3> list2=dao2.queryAllByPage(1,3,new DalHints().selectByNames());
        assertEquals(3,list2.size());

        List<MysqlPersonTable5> list3=dao3.queryAllByPage(1,3,new DalHints().selectByNames());
        assertEquals(3,list3.size());
    }

    @Test
    public void testQueryByPK() throws Exception{
        MysqlPersonTable1 ret1= (MysqlPersonTable1) dao1.queryByPk(1,new DalHints());
        assertNotNull(ret1.getID());

        MysqlPersonTable1 ret2=(MysqlPersonTable1)dao1.queryByPk(ret1,new DalHints().selectByNames());
        assertNotNull(ret2.getID());

        MysqlPersonTable3 ret3= (MysqlPersonTable3) dao2.queryByPk(1,new DalHints());
        assertNotNull(ret3.getID());

        MysqlPersonTable3 ret4=(MysqlPersonTable3)dao2.queryByPk(ret3,new DalHints().selectByNames());
        assertNotNull(ret4.getID());

        MysqlPersonTable5 ret5= (MysqlPersonTable5) dao3.queryByPk(1,new DalHints());
        assertNotNull(ret5.getID());

        MysqlPersonTable5 ret6=(MysqlPersonTable5)dao3.queryByPk(ret5,new DalHints().selectByNames());
        assertNotNull(ret6.getID());
    }

    @Test
    public void testQueryLike1() throws Exception{
        MysqlPersonTable1 sample=new MysqlPersonTable1();
        sample.setName("Initial_Shard_00");
        List<MysqlPersonTable1> list=dao1.queryLike(sample,new DalHints().selectByNames());
        assertEquals(1,list.size());

        try {
            list.clear();
            MysqlPersonTable1 nullFieldSample = new MysqlPersonTable1();
            list = dao1.queryLike(nullFieldSample, new DalHints().selectByNames());
            assertEquals(6,list.size());
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }

        try {
            list.clear();
            list = dao1.queryLike(null, new DalHints().selectByNames());
            fail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryLike2() throws Exception{
        MysqlPersonTable3 sample=new MysqlPersonTable3();
        sample.setName("Initial_Shard_00");
        List<MysqlPersonTable3> list=dao2.queryLike(sample,new DalHints().selectByNames());
        assertEquals(1,list.size());

        try {
            list.clear();
            MysqlPersonTable3 nullFieldSample = new MysqlPersonTable3();
            list = dao2.queryLike(nullFieldSample, new DalHints().selectByNames());
            assertEquals(6,list.size());
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }

        try {
            list.clear();
            list = dao2.queryLike(null, new DalHints().selectByNames());
            fail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryLike3() throws Exception{
        MysqlPersonTable5 sample=new MysqlPersonTable5();
        sample.setName("Initial_Shard_00");
        List<MysqlPersonTable5> list=dao3.queryLike(sample,new DalHints().selectByNames());
        assertEquals(1,list.size());

        try {
            list.clear();
            MysqlPersonTable5 nullFieldSample = new MysqlPersonTable5();
            list = dao3.queryLike(nullFieldSample, new DalHints().selectByNames());
            assertEquals(6,list.size());
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }

        try {
            list.clear();
            list = dao3.queryLike(null, new DalHints().selectByNames());
            fail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Test
    public void testDao1Insert1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable1 daoPojo = new MysqlPersonTable1();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao1.insert(hints.enableIdentityInsert(), daoPojo);
//		int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);


        MysqlPersonTable1 ret = (MysqlPersonTable1) dao1.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());
    }

    @Test
    public void testDao2Insert1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable3 daoPojo = new MysqlPersonTable3();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao2.insert(hints.enableIdentityInsert(), daoPojo);
//		int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);


        MysqlPersonTable3 ret = (MysqlPersonTable3) dao2.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());
    }

    @Test
    public void testDao3Insert1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable5 daoPojo = new MysqlPersonTable5();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao3.insert(hints.enableIdentityInsert(), daoPojo);
//		int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);


        MysqlPersonTable5 ret = (MysqlPersonTable5) dao3.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());
    }

    @Test
    public void testDao1Insert1SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable1 daoPojo = new MysqlPersonTable1();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao1.insert(hints.setIdentityBack(), daoPojo);
//		int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);
        assertEquals(7, daoPojo.getID().intValue());

        MysqlPersonTable1 ret = (MysqlPersonTable1) dao1.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());
    }

    @Test
    public void testDao2Insert1SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable3 daoPojo = new MysqlPersonTable3();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao2.insert(hints.setIdentityBack(), daoPojo);
//		int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);
        assertEquals(7, daoPojo.getID().intValue());

        MysqlPersonTable3 ret = (MysqlPersonTable3) dao2.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());
    }

    @Test
    public void testDao3Insert1SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable5 daoPojo = new MysqlPersonTable5();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao3.insert(hints.setIdentityBack(), daoPojo);
//		int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);
        assertEquals(7, daoPojo.getID().intValue());

        MysqlPersonTable5 ret = (MysqlPersonTable5) dao3.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());
    }

//    @Test
//    public void testInsert2SetIdentityBack() throws Exception {
//        DalHints hints = new DalHints();
//
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setID(20 + i * 2);
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
////			else
////				daoPojo.setName("Initial_Shard_1" + i);
//            daoPojos.add(daoPojo);
//        }
//
//        int[] affected = dao.insert(hints.setIdentityBack(), daoPojos);
//        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
//        for(int i=0;i<daoPojos.size();i++) {
//            assertEquals(i + 7, daoPojos.get(i).getID().intValue());
//        }
//    }
//
//    @Test
//    public void testInsert2() throws Exception {
//        DalHints hints = new DalHints();
//
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setID(20 + i * 2);
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
////			else
////				daoPojo.setName("Initial_Shard_1" + i);
//            daoPojos.add(daoPojo);
//        }
//
//        int[] affected = dao.insert(hints.enableIdentityInsert(), daoPojos);
//        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
//
//        MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(30, new DalHints());
//        assertNotNull(ret);
//    }
//
//
//    @Test
//    public void testInsert3() throws Exception {
//        DalHints hints = new DalHints();
//        KeyHolder keyHolder = new KeyHolder();
//        MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//        daoPojo.setID(10);
//        daoPojo.setAge(20);
//        daoPojo.setName("insert");
//        int affected = dao.insert(hints.enableIdentityInsert(), keyHolder, daoPojo);
//        assertEquals(1, affected);
//        assertEquals("insert", dao.queryByPk(10, new DalHints()).getName());
//        assertEquals(1, keyHolder.size());
//        assertEquals(10L, keyHolder.getKey());
//    }
//
//    @Test
//    public void testInsert3SetIdentityBack() throws Exception {
//        KeyHolder keyHolder = new KeyHolder();
//        MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//        daoPojo.setAge(20);
//        daoPojo.setName("insert");
//        int affected = dao.insert(new DalHints().setIdentityBack(), keyHolder, daoPojo);
//        assertEquals(1, affected);
//        assertEquals("insert", dao.queryByPk(7, new DalHints()).getName());
//        assertEquals(1, keyHolder.size());
//        assertEquals(7L, keyHolder.getKey());
//        assertEquals(7, daoPojo.getID().intValue());
//    }
//
//    @Test
//    public void testInsert4() throws Exception {
//        DalHints hints = new DalHints();
//        KeyHolder keyHolder = new KeyHolder();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
////			daoPojo.setID(20+i*2);
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
//            else
//                daoPojo.setName(null);
//            daoPojos.add(daoPojo);
//        }
//        int[] affected = dao.insert(hints, keyHolder, daoPojos);
//
//        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
//        assertEquals(6, keyHolder.size());
//
//        assertEquals(12L, keyHolder.getKey(5));
//    }
//
//    @Test
//    public void testInsert4SetIdentityBack() throws Exception {
//        DalHints hints = new DalHints();
//        KeyHolder keyHolder = new KeyHolder();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
//            else
//                daoPojo.setName(null);
//            daoPojos.add(daoPojo);
//        }
//        int[] affected = dao.insert(hints.setIdentityBack(), keyHolder, daoPojos);
//
//        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
//        assertEquals(6, keyHolder.size());
//        int i = 0;
//        for (MysqlPersonExtendsUpdatableEntity pojo : daoPojos)
//            assertEquals(keyHolder.getKey(i++).intValue(), pojo.getID().intValue());
//    }
//
//    @Test
//    public void testInsert5() throws Exception {
//        DalHints hints = new DalHints();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setID(20 + i * 2);
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
//            else
//                daoPojo.setName("Initial_Shard_1" + i);
//            daoPojos.add(daoPojo);
//        }
//        int[] affected = dao.batchInsert(hints.enableIdentityInsert(), daoPojos);
//        assertEquals(6, affected.length);
//
////		for(int i=0;i<affected.length;i++)
////			System.out.print(affected[i]+" ");
////		System.out.println();
//
//        MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(30, new DalHints());
//        assertNotNull(ret);
//
//    }
//
//    @Test
//    public void testNullInsert1() throws Exception {
//        DalHints hints = new DalHints();
//        MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//        daoPojo.setAge(20);
//        daoPojo.setName("insert");
//
//        int affected = dao.insert(hints, daoPojo);
//        assertEquals(1, affected);
//
//
//        MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(7, new DalHints());
//        assertNotNull(ret);
//        assertNotNull(ret.getBirth());
//    }
//
//    @Test
//    public void testNullInsert2() throws Exception {
//        DalHints hints = new DalHints();
//
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>();
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
//            daoPojos.add(daoPojo);
//        }
//
//        int[] affected = dao.insert(hints, daoPojos);
//        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
//
//        daoPojos = dao.queryAll(hints);
//        for (int i = 0; i < daoPojos.size(); i++) {
//            assertNotNull(daoPojos.get(i).getBirth());
//            if (i == 7 || i == 9 || i == 11)
//                assertEquals("hello", daoPojos.get(i).getName());
//        }
//    }
//
//    @Test
//    public void testNullInsert5() throws Exception {
//        DalHints hints = new DalHints();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
//
//            daoPojos.add(daoPojo);
//        }
//        int[] affected = dao.batchInsert(hints, daoPojos);
//        assertEquals(6, affected.length);
//
//
//        daoPojos = dao.queryAll(hints);
//        for (int i = 0; i < daoPojos.size(); i++) {
//            assertNotNull(daoPojos.get(i).getBirth());
//            if (i == 7 || i == 9 || i == 11)
//                assertNull(daoPojos.get(i).getName());
//            else assertNotNull(daoPojos.get(i).getName());
//        }
//
//    }
//
//    @Test
//    public void testNullCombinedInsert1() throws Exception {
//        DalHints hints = new DalHints();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
//
//            daoPojos.add(daoPojo);
//        }
//        int affected = dao.combinedInsert(hints, daoPojos);
//        assertEquals(6, affected);
//
//        daoPojos = dao.queryAll(hints);
//        for (int i = 0; i < daoPojos.size(); i++) {
//            assertNotNull(daoPojos.get(i).getBirth());
//            if (i == 7 || i == 9 || i == 11)
//                assertNull(daoPojos.get(i).getName());
//            else assertNotNull(daoPojos.get(i).getName());
//        }
//    }
//
//
//    @Test
//    public void testCombinedInsert1() throws Exception {
//        DalHints hints = new DalHints();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setID(20 + i * 2);
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
//            else
//                daoPojo.setName("Initial_Shard_1" + i);
//            daoPojos.add(daoPojo);
//        }
//        int affected = dao.combinedInsert(hints.enableIdentityInsert(), daoPojos);
//        assertEquals(6, affected);
//
//        MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(30, new DalHints());
//        assertNotNull(ret);
//    }
//
//    @Test
//    public void testCombinedInsert1etIdentityBack() throws Exception {
//        DalHints hints = new DalHints();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setID(20 + i * 2);
//            daoPojo.setAge(i + 20);
//            if (i % 2 == 0)
//                daoPojo.setName("Initial_Shard_0" + i);
//            else
//                daoPojo.setName("Initial_Shard_1" + i);
//            daoPojos.add(daoPojo);
//        }
//        int affected = dao.combinedInsert(hints.setIdentityBack(), daoPojos);
//        assertEquals(6, affected);
//
//        for(int i=0;i<daoPojos.size();i++) {
//            assertEquals(i + 7, daoPojos.get(i).getID().intValue());
//        }
//    }
//
//    @Test
//    public void testCombinedInsert2() throws Exception {
//        DalHints hints = new DalHints();
//        KeyHolder keyHolder = new KeyHolder();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setID(20 + i * 2);
//            daoPojo.setAge(i + 20);
//            daoPojo.setName("Initial_" + i);
//            daoPojos.add(daoPojo);
//        }
//        int affected = dao.combinedInsert(hints.enableIdentityInsert(), keyHolder, daoPojos);
//        assertEquals(6, affected);
//
//        assertEquals(6, keyHolder.size());
//        MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(30, new DalHints());
//        assertNotNull(ret);
////		for(int i=0;i<keyHolder.size();i++) {
////			System.out.println(keyHolder.getKey(i));
////		}
////
////		assertEquals(30l, keyHolder.getKey(5));
//    }
//
//    @Test
//    public void testCombinedInsert2SetIdentityBack() throws Exception {
//        DalHints hints = new DalHints();
//        KeyHolder keyHolder = new KeyHolder();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>(
//                6);
//        for (int i = 0; i < 6; i++) {
//            MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//            daoPojo.setAge(i + 20);
//            daoPojo.setName("Initial_" + i);
//            daoPojos.add(daoPojo);
//        }
//        int affected = dao.combinedInsert(hints.setIdentityBack(), keyHolder, daoPojos);
//        assertEquals(6, affected);
//        assertEquals(6, keyHolder.size());
//        int i = 0;
//        for (MysqlPersonExtendsUpdatableEntity pojo : daoPojos) {
//            assertEquals(pojo.getID().intValue(), keyHolder.getKey(i++).intValue());
//        }
////		for(int i=0;i<keyHolder.size();i++) {
////			System.out.println(keyHolder.getKey(i));
////		}
////
////		assertEquals(30l, keyHolder.getKey(5));
//    }

//    @Test
//    public void testQuery() throws Exception {
//        List<MysqlPersonTable1> list1 = dao1.queryAll(null);
//        assertEquals(6, list1.size());
//
//        List<MysqlPersonTable3> list2 = dao2.queryAll(null);
//        assertEquals(6, list2.size());
//
//        List<MysqlPersonTable5> list3 = dao3.queryAll(null);
//        assertEquals(6, list3.size());
//    }
//
//    //
//    @Test
//    public void testQueryByPk1() throws Exception {
//        int id = 1;
//        DalHints hints = new DalHints();
//        MysqlPersonExtendsUpdatableEntity affected = dao.queryByPk(id, hints);
//        assertNotNull(affected);
//    }
//
//    //
//    @Test
//    public void testQueryByPk2() throws Exception {
//        DalHints hints = new DalHints();
//        MysqlPersonExtendsUpdatableEntity daoPojo = new MysqlPersonExtendsUpdatableEntity();
//        daoPojo.setAge(20);
//
//        daoPojo.setName("Initial_Shard_00");
//        daoPojo.setID(1);
//        MysqlPersonExtendsUpdatableEntity affected = dao.queryByPk(daoPojo, hints);
//        assertNotNull(affected);
//    }

    //
    @Test
    public void testDao1Update1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable1 daoPojo = new MysqlPersonTable1();
        daoPojo.setAge(20);

        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);

        int affected = dao1.update(hints, daoPojo);
        assertEquals(1, affected);
    }

    @Test
    public void testDao2Update1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable3 daoPojo = new MysqlPersonTable3();
        daoPojo.setAge(20);

        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);

        int affected = dao2.update(hints, daoPojo);
        assertEquals(1, affected);
    }

    @Test
    public void testDao3Update1() throws Exception {
        DalHints hints = new DalHints();
        MysqlPersonTable5 daoPojo = new MysqlPersonTable5();
        daoPojo.setAge(20);

        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);

        int affected = dao3.update(hints, daoPojo);
        assertEquals(1, affected);
    }

//    @Test
//    public void testDirtyFlagUpdate1() throws Exception {
//        DalHints hints = new DalHints();
//        MysqlPersonExtendsUpdatableEntity daoPojo = dao.queryByPk(1, hints);
//        daoPojo.setName("updateDirtyFlag1");
//        daoPojo.setID(2);
//
//        int affected = dao.update(hints, daoPojo);
//        assertEquals(1, affected);
//        daoPojo = dao.queryByPk(2, hints);
//        assertEquals("updateDirtyFlag1", daoPojo.getName());
//        assertEquals(21, daoPojo.getAge().intValue());
//
//        daoPojo = dao.queryByPk(3, hints);
//        daoPojo.setName("updateDirtyFlag2");
//        daoPojo.setID(4);
//
////		affected=dao.update(hints.updateUnchangedField(), daoPojo);
////		assertEquals(1, affected);
////		daoPojo = dao.queryByPk(4, hints);
////		assertEquals("updateDirtyFlag2", daoPojo.getName());
////		assertEquals(20, daoPojo.getAge().intValue());
//    }
//
//    @Test
//    public void testDirtyFlagUpdate2() throws Exception {
//        DalHints hints = new DalHints();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>();
//        MysqlPersonExtendsUpdatableEntity daoPojo = dao.queryByPk(1, hints);
//        daoPojo.setID(2);
//        daoPojo.setName("updateDirtyFlag2");
//        daoPojos.add(daoPojo);
//
//        daoPojo = dao.queryByPk(3, hints);
//        daoPojo.setID(4);
//        daoPojo.setName("updateDirtyFlag4");
//        daoPojos.add(daoPojo);
//
//        int[] affected = dao.update(hints, daoPojos);
//        assertArrayEquals(new int[]{1, 1}, affected);
//        daoPojo = dao.queryByPk(2, hints);
//        assertEquals("updateDirtyFlag2", daoPojo.getName());
//        assertEquals(21, daoPojo.getAge().intValue());
//
//        daoPojo = dao.queryByPk(4, hints);
//        assertEquals("updateDirtyFlag4", daoPojo.getName());
//        assertEquals(21, daoPojo.getAge().intValue());
//
//        daoPojos.get(0).setID(5);
//        daoPojos.get(0).setName("updateDirtyFlag5");
//        daoPojos.get(1).setID(6);
//        daoPojos.get(1).setName("updateDirtyFlag6");
//
//        affected = dao.update(hints.updateUnchangedField(), daoPojos);
//        assertArrayEquals(new int[]{1, 1}, affected);
//        daoPojo = dao.queryByPk(5, hints);
//        assertEquals("updateDirtyFlag5", daoPojo.getName());
//        assertEquals(20, daoPojo.getAge().intValue());
//
//        daoPojo = dao.queryByPk(6, hints);
//        assertEquals("updateDirtyFlag6", daoPojo.getName());
//        assertEquals(20, daoPojo.getAge().intValue());
//
//
//    }
//
//    @Test
//    public void testDirtyFlagBatchUpdate2() throws Exception {
//        DalHints hints = new DalHints();
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = new ArrayList<>();
//        MysqlPersonExtendsUpdatableEntity daoPojo = dao.queryByPk(1, hints);
//        daoPojo.setID(2);
//        daoPojo.setName("updateDirtyFlag2");
//        daoPojos.add(daoPojo);
//
//        daoPojo = dao.queryByPk(3, hints);
//        daoPojo.setID(4);
//        daoPojo.setName("updateDirtyFlag4");
//        daoPojos.add(daoPojo);
//
//        int[] affected = dao.batchUpdate(hints, daoPojos);
//        assertArrayEquals(new int[]{1, 1}, affected);
//        daoPojo = dao.queryByPk(2, hints);
//        assertEquals("updateDirtyFlag2", daoPojo.getName());
//        assertEquals(21, daoPojo.getAge().intValue());
//
//        daoPojo = dao.queryByPk(4, hints);
//        assertEquals("updateDirtyFlag4", daoPojo.getName());
//        assertEquals(21, daoPojo.getAge().intValue());
//
//        daoPojos.get(0).setID(5);
//        daoPojos.get(0).setName("updateDirtyFlag5");
//        daoPojos.get(1).setID(6);
//        daoPojos.get(1).setName("updateDirtyFlag6");
//
//        affected = dao.batchUpdate(hints.updateUnchangedField(), daoPojos);
//        assertArrayEquals(new int[]{1, 1}, affected);
//        daoPojo = dao.queryByPk(5, hints);
//        assertEquals("updateDirtyFlag5", daoPojo.getName());
//        assertEquals(20, daoPojo.getAge().intValue());
//
//        daoPojo = dao.queryByPk(6, hints);
//        assertEquals("updateDirtyFlag6", daoPojo.getName());
//        assertEquals(20, daoPojo.getAge().intValue());
//
//
//    }
////	@Test
////	public void testUpdate2() throws Exception {
////		DalHints hints = new DalHints();
////		List<MysqlPersonExtendsUpdatableEntity> daoPojos = dao.queryAll(new DalHints());
////		changePojos(daoPojos);
////		int[] affected = dao.update(hints, daoPojos);
////		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
////		verifyPojos(dao.queryAll(new DalHints()));
////	}
//
////	@Test
////	public void testBatchUpdate() throws Exception {
////		DalHints hints = new DalHints();
////		List<MysqlPersonExtendsUpdatableEntity> daoPojos = dao.queryAll(new DalHints());
////		changePojos(daoPojos);
////		int[] affected = dao.batchUpdate(hints, daoPojos);
////		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
////		verifyPojos(dao.queryAll(new DalHints()));
////	}
//
//
//    @Test
//    public void testtest_def_update() throws Exception {
//        dao.test_def_update(new DalHints());
//        int ret = dao.count(new DalHints());
//        assertEquals(0, ret);
//    }
//
//    @Test
//    public void testtest_build_query_fieldList_multipleOrderBy() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<String> ret = dao.test_build_query_fieldList_multipleOrderBy(Age, null);
//        assertEquals("Initial_Shard_11", ret.get(0));
//        assertEquals("Initial_Shard_00", ret.get(3));
//    }
//
//    @Test
//    public void testtest_build_query_fieldList_multipleOrderByReverse() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<String> ret = dao.test_build_query_fieldList_multipleOrderByReverse(Age, null);
//        assertEquals("Initial_Shard_00", ret.get(0));
//        assertEquals("Initial_Shard_13", ret.get(3));
//    }
//
//    @Test
//    public void testtest_build_query_fieldListByPage_multipleOrderBy() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<String> ret = dao.test_build_query_fieldListByPage_multipleOrderBy(Age, 1, 3, null);
//        assertEquals("Initial_Shard_11", ret.get(0));
//    }
//
//    @Test
//    public void testtest_build_query_fieldListByPage_multipleOrderByReverse() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<String> ret = dao.test_build_query_fieldListByPage_multipleOrderByReverse(Age, 1, 3, null);
//        assertEquals("Initial_Shard_00", ret.get(0));
//    }
//
//    @Test
//    public void testtest_build_query_field_first_multipleOrderBy() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        String ret = dao.test_build_query_field_first_multipleOrderBy(Age, null);
//        assertEquals("Initial_Shard_11", ret);
//    }
//
//    @Test
//    public void testtest_build_query_field_first_multipleOrderByReverse() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        String ret = dao.test_build_query_field_first_multipleOrderByReverse(Age, null);
//        assertEquals("Initial_Shard_00", ret);
//    }
//
//    @Test
//    public void testtest_build_query_list_multipleOrderBy() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.test_build_query_list_multipleOrderBy(Age, null);
//        assertEquals("Initial_Shard_11", ret.get(0).getName());
//    }
//
//    @Test
//    public void testtest_build_query_list_multipleOrderByReverse() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.test_build_query_list_multipleOrderByReverse(Age, null);
//        assertEquals("Initial_Shard_00", ret.get(0).getName());
//    }
//
//    @Test
//    public void testtest_build_queryPartial_list_multipleOrderBy() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.test_build_queryPartial_list_multipleOrderBy(Age, null);
//        assertEquals("Initial_Shard_11", ret.get(0).getName());
//    }
//
//    @Test
//    public void testtest_build_queryPartial_list_multipleOrderByReverse() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.test_build_queryPartial_list_multipleOrderByReverse(Age, null);
//        assertEquals("Initial_Shard_00", ret.get(0).getName());
//    }
//
//    @Test
//    public void testtest_build_query_listByPage_multipleOrderBy() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.test_build_query_listByPage_multipleOrderBy(Age, 2, 3, null);
//        assertEquals("Initial_Shard_00", ret.get(0).getName());
//    }
//
//    @Test
//    public void testtest_build_query_listByPage_multipleOrderByReverse() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.test_build_query_listByPage_multipleOrderByReverse(Age, 1, 3, null);
//        assertEquals("Initial_Shard_00", ret.get(0).getName());
//    }
//
//    @Test
//    public void testtest_build_queryPartial_listByPage_multipleOrderBy() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.test_build_queryPartial_listByPage_multipleOrderBy(Age, 2, 3, null);
//        assertEquals("Initial_Shard_00", ret.get(0).getName());
//    }
//
//    @Test
//    public void testtest_build_queryPartial_listByPage_multipleOrderByReverse() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.test_build_queryPartial_listByPage_multipleOrderByReverse(Age, 2, 3, null);
//        assertEquals("Initial_Shard_13", ret.get(0).getName());
//    }
//
//    @Test
//    public void testtest_build_query_first_multipleOrderBy() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        MysqlPersonExtendsUpdatableEntity daoPojo = dao.test_build_query_first_multipleOrderBy(Age, new DalHints());
//        assertEquals("Initial_Shard_11", daoPojo.getName());
//    }
//
//    @Test
//    public void testtest_build_query_first_multipleOrderByReverse() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        MysqlPersonExtendsUpdatableEntity daoPojo = dao.test_build_query_first_multipleOrderByReverse(Age, new DalHints());
//        assertEquals("Initial_Shard_00", daoPojo.getName());
//    }
//
//
//    @Test
//    public void testtest_build_queryPartial_first_multipleOrderBy() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        MysqlPersonExtendsUpdatableEntity daoPojo = dao.test_build_queryPartial_first_multipleOrderBy(Age, new DalHints());
//        assertEquals("Initial_Shard_11", daoPojo.getName());
//    }
//
//    @Test
//    public void testtest_build_queryPartial_first_multipleOrderByReverse() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(21);
//        MysqlPersonExtendsUpdatableEntity daoPojo = dao.test_build_queryPartial_first_multipleOrderByReverse(Age, new DalHints());
//        assertEquals("Initial_Shard_00", daoPojo.getName());
//    }
//
//    @Test
//    public void testBuildQueryLikeWithMatchPattern() throws Exception {
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.testBuildQueryLikeWithMatchPattern("_00", MatchPattern.END_WITH, null);
//        assertEquals(1, ret.size());
//        ret = dao.testBuildQueryLikeWithMatchPattern("Init", MatchPattern.BEGIN_WITH, null);
//        assertEquals(6, ret.size());
//        ret = dao.testBuildQueryLikeWithMatchPattern("Shard", MatchPattern.CONTAINS, null);
//        assertEquals(6, ret.size());
//        ret = dao.testBuildQueryLikeWithMatchPattern("Shard", MatchPattern.USER_DEFINED, null);
//        assertEquals(0, ret.size());
//    }
//
//    @Test
//    public void testBuildQueryLikeNullableWithMatchPattern() throws Exception {
//        List<MysqlPersonExtendsUpdatableEntity> ret = dao.testBuildQueryLikeNullableWithMatchPattern("_00", MatchPattern.END_WITH, null);
//        assertEquals(1, ret.size());
//        ret = dao.testBuildQueryLikeNullableWithMatchPattern("Init", MatchPattern.BEGIN_WITH, null);
//        assertEquals(6, ret.size());
//        ret = dao.testBuildQueryLikeNullableWithMatchPattern("Shard", MatchPattern.CONTAINS, null);
//        assertEquals(6, ret.size());
//        ret = dao.testBuildQueryLikeNullableWithMatchPattern("Shard", MatchPattern.USER_DEFINED, null);
//        assertEquals(0, ret.size());
//        ret = dao.testBuildQueryLikeNullableWithMatchPattern(null, MatchPattern.CONTAINS, null);
//        assertEquals(6, ret.size());
//    }
//
//    @Test
//    public void test_def_query() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = dao.test_def_query(Age, null);
//        assertEquals(0, daoPojos.size());
//
//        Age.add(20);
//        daoPojos = dao.test_def_query(Age, null);
//        assertEquals(3, daoPojos.size());
//    }
//
//    @Test
//    public void test_def_partialQuery() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = dao.test_def_partialQuery(Age, null);
//        assertEquals(0, daoPojos.size());
//
//        Age.add(20);
//        daoPojos = dao.test_def_partialQuery(Age, null);
//        assertEquals(3, daoPojos.size());
//        assertEquals("Initial_Shard_00", daoPojos.get(0).getName());
//        assertNull(daoPojos.get(0).getAge());
//    }
//
//    @Test
//    public void test_def_queryForObject() throws Exception {
//        List<Integer> ID = new ArrayList<>();
//        ID.add(3);
//        ID.add(30);
//        MysqlPersonExtendsUpdatableEntity daoPojos = dao.test_def_queryForObject(ID, null);
//        assertEquals("Initial_Shard_02", daoPojos.getName());
//    }
//
//    @Test
//    public void test_def_partialQueryForObject() throws Exception {
//        List<Integer> ID = new ArrayList<>();
//        ID.add(3);
//        ID.add(30);
//        MysqlPersonExtendsUpdatableEntity daoPojos = dao.test_def_partialQueryForObject(ID, null);
//        assertEquals("Initial_Shard_02", daoPojos.getName());
//        assertNull(daoPojos.getAge());
//    }
//
//    @Test
//    public void test_def_queryForObjectNullable() throws Exception {
//        List<Integer> ID = new ArrayList<>();
//        ID.add(0);
//        ID.add(30);
//        MysqlPersonExtendsUpdatableEntity daoPojos = dao.test_def_queryForObjectNullable(ID, null);
//        assertNull(daoPojos);
//
//        ID.add(3);
//        daoPojos = dao.test_def_queryForObjectNullable(ID, null);
//        assertEquals("Initial_Shard_02", daoPojos.getName());
//    }
//
//    @Test
//    public void test_def_partialQueryForObjectNullable() throws Exception {
//        List<Integer> ID = new ArrayList<>();
//        ID.add(0);
//        ID.add(30);
//        MysqlPersonExtendsUpdatableEntity daoPojos = dao.test_def_partialQueryForObjectNullable(ID, null);
//        assertNull(daoPojos);
//
//        ID.add(3);
//        daoPojos = dao.test_def_partialQueryForObjectNullable(ID, null);
//        assertEquals("Initial_Shard_02", daoPojos.getName());
//        assertNull(daoPojos.getAge());
//    }
//
//    @Test
//    public void test_def_queryFirst() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(31);
//        MysqlPersonExtendsUpdatableEntity daoPojos = dao.test_def_queryFirst(Age, null);
//        assertEquals("Initial_Shard_04", daoPojos.getName());
//    }
//
//    @Test
//    public void test_def_partialQueryFirst() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(20);
//        Age.add(31);
//        MysqlPersonExtendsUpdatableEntity daoPojos = dao.test_def_partialQueryFirst(Age, null);
//        assertEquals("Initial_Shard_04", daoPojos.getName());
//        assertNull(daoPojos.getAge());
//    }
//
//    @Test
//    public void test_def_queryFirstNullable() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        MysqlPersonExtendsUpdatableEntity daoPojos = dao.test_def_queryFirstNullable(Age, null);
//        assertNull(daoPojos);
//
//        Age.add(20);
//        daoPojos = dao.test_def_queryFirstNullable(Age, null);
//        assertEquals("Initial_Shard_04", daoPojos.getName());
//    }
//
//    @Test
//    public void test_def_partialQueryFirstNullable() throws Exception {
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        MysqlPersonExtendsUpdatableEntity daoPojos = dao.test_def_partialQueryFirstNullable(Age, null);
//        assertNull(daoPojos);
//
//        Age.add(20);
//        daoPojos = dao.test_def_partialQueryFirstNullable(Age, null);
//        assertEquals("Initial_Shard_04", daoPojos.getName());
//        assertNull(daoPojos.getAge());
//    }
//
//    @Test
//    public void test_def_queryTop() throws Exception {
//        int count = 3;
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = dao.test_def_queryTop(Age, null, count);
//        assertEquals(0, daoPojos.size());
//
//        Age.add(20);
//        daoPojos = dao.test_def_queryTop(Age, null, count);
//        assertEquals("Initial_Shard_04", daoPojos.get(0).getName());
//    }
//
//    @Test
//    public void test_def_partialQueryTop() throws Exception {
//        int count = 3;
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = dao.test_def_partialQueryTop(Age, null, count);
//        assertEquals(0, daoPojos.size());
//
//        Age.add(20);
//        daoPojos = dao.test_def_partialQueryTop(Age, null, count);
//        assertEquals("Initial_Shard_04", daoPojos.get(0).getName());
//        assertNull(daoPojos.get(0).getAge());
//    }
//
//    @Test
//    public void test_def_queryFrom() throws Exception {
//        int start = 1;
//        int count = 2;
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = dao.test_def_queryFrom(Age, null, start, count);
//        assertEquals(0, daoPojos.size());
//
//        Age.add(20);
//        daoPojos = dao.test_def_queryFrom(Age, null, start, count);
//        assertEquals("Initial_Shard_02", daoPojos.get(0).getName());
//    }
//
//    @Test
//    public void testTableBuilderParameterIndex() throws Exception{
//        int start=0;
//        int count=2;
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        Age.add(20);
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos=dao.testTableBuilderParameterIndex("Initial_Shard_02",Age,3,null,start,count);
//        assertEquals(1,daoPojos.size());
//    }
//
//    @Test
//    public void testTableBuilderParameterIndexNoIn() throws Exception{
//        int start=0;
//        int count=2;
//        Integer Age = 20;
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos=dao.testTableBuilderParameterIndexNoIn("Initial_Shard_02",Age,3,null,start,count);
//        assertEquals(1,daoPojos.size());
//    }
//
//    @Test
//    public void test_def_update_ParameterIndex() throws Exception{
//        Integer Age = 20;
//        int ret=dao.test_def_update_ParameterIndex("Initial_Shard_02",Age,3,null);
//        assertEquals(1,ret);
//        assertEquals(5,dao.count(null));
//    }
//
//    @Test
//    public void testColumnParameter() throws Exception{
//        String columnName="Age";
//        List<MysqlPersonTable> ret=dao.testColumnParameter(columnName,null);
//        assertEquals(6,ret.size());
//    }
//
//
//    @Test
//    public void test_def_update_InParameterIndex() throws Exception{
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        Age.add(20);
//        int ret=dao.test_def_update_InParameterIndex("Initial_Shard_02",Age,3,null);
//        assertEquals(1,ret);
//        assertEquals(5,dao.count(null));
//    }
//
//    @Test
//    public void testFreeSqlBuilderParameterIndex() throws Exception{
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        Age.add(20);
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos=dao.testFreeSqlBuilderParameterIndex("Initial_Shard_02",Age,3,null);
//        assertEquals(1,daoPojos.size());
//    }
//    @Test
//    public void testFreeSqlBuilderMultipleInParams() throws Exception{
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        Age.add(20);
//
//        List<Integer> ID = new ArrayList<>();
//        ID.add(1);
//        ID.add(2);
//        ID.add(3);
//
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos=dao.testFreeSqlBuilderMultipleInParams("Initial_Shard_02",Age,ID,null);
//        assertEquals(1,daoPojos.size());
//    }
//
//    @Test
//    public void testFreeSqlBuilderWithDuplicateParameterIndex() throws Exception{
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        Age.add(20);
//        try{
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos=dao.testFreeSqlBuilderWithDuplicateParameterIndex("Initial_Shard_02",Age,3,null);
//        fail();
//        assertEquals(1,daoPojos.size());
//        }catch (Throwable e){
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testFreeSqlBuilderWithDiscontinuedParameterIndex() throws Exception{
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        Age.add(20);
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos=dao.testFreeSqlBuilderWithDiscontinuedParameterIndex("Initial_Shard_02",Age,3,null);
//        assertEquals(1,daoPojos.size());
//    }
//
//
//    @Test
//    public void testFreeSqlBuilderParameterIndexNotIn() throws Exception{
//        Integer Age =20;
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos=dao.testFreeSqlBuilderParameterIndexNotIn("Initial_Shard_02",Age,3,null);
//        assertEquals(1,daoPojos.size());
//    }
//
//    @Test
//    public void testFreeSqlBuilderWithNoParameters() throws Exception{
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos=dao.testFreeSqlBuilderWithNoParameter(null);
//        assertEquals(1,daoPojos.size());
//    }
//
//    @Test
//    public void test_def_partialQueryFrom() throws Exception {
//        int start = 1;
//        int count = 2;
//        List<Integer> Age = new ArrayList<>();
//        Age.add(30);
//        Age.add(31);
//        List<MysqlPersonExtendsUpdatableEntity> daoPojos = dao.test_def_partialQueryFrom(Age, null, start, count);
//        assertEquals(0, daoPojos.size());
//
//        Age.add(20);
//        daoPojos = dao.test_def_partialQueryFrom(Age, null, start, count);
//        assertEquals("Initial_Shard_02", daoPojos.get(0).getName());
//        assertNull(daoPojos.get(0).getAge());
//    }
//
//    @Test
//    public void test_countWhereCondition() throws Exception {
//
//        String name = "Initial";
//        int count = dao.countWhereCondition(name, new DalHints());
//        assertEquals(6, count);
//    }
//
//    @Test
//    public void testTransPass() throws Exception {
//
//        DalCommand command = new DalCommand() {
//
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1, new DalHints());
//                ret.setAge(1000);
//                dao.update(new DalHints(), ret);
//                return true;
//            }
//        };
//        try {
//            client.execute(command, new DalHints());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        assertEquals(1000, dao.queryByPk(1, new DalHints()).getAge().intValue());
//    }
//
//    @Test
//    public void testTransSetRollback() throws Exception {
//        DalCommand command = new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                try {
//                    MysqlPersonExtendsUpdatableEntity pojo = new MysqlPersonExtendsUpdatableEntity();
//                    pojo.setID(2);
//                    dao.delete(new DalHints(), pojo);
////                    DalTransactionManager.setRollbackOnly();
//                }catch (Exception e){
//                    throw e;
//                }
//                return true;
//            }
//        };
//        try {
//            client.execute(command, new DalHints());
//        } catch (Exception e) {
////			e.printStackTrace();
//        }
//        assertEquals(21, dao.queryByPk(2, new DalHints()).getAge().intValue());
//        assertEquals(6, dao.count(new DalHints()));
//    }
//
//    @Test
//    public void testTransFail() throws Exception {
//        DalCommand command = new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                try {
//                    MysqlPersonExtendsUpdatableEntity pojo = new MysqlPersonExtendsUpdatableEntity();
//                    pojo.setID(2);
//                    dao.delete(new DalHints(), pojo);
//                    MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1, new DalHints());
//                    ret.setAge(2000);
//                    ret.setID(3); //插入已存在的主鍵3，造成主鍵衝突
//                    dao.insert(new DalHints().enableIdentityInsert(), ret);
//                }catch (Exception e){
//                    throw e;
//                }
//                return true;
//            }
//        };
//        try {
//            client.execute(command, new DalHints());
//        } catch (Exception e) {
////			e.printStackTrace();
//        }
//        assertEquals(21, dao.queryByPk(2, new DalHints()).getAge().intValue());
//        assertEquals(6, dao.count(new DalHints()));
//    }
//
//    @Test
//    public void testTransCommandsFail() throws SQLException {
//        List<DalCommand> cmds = new LinkedList<>();
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1, new DalHints());
//                ret.setAge(1000);
//                dao.update(new DalHints(), ret);
//                return true;
//            }
//        });
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity pojo = new MysqlPersonExtendsUpdatableEntity();
//                pojo.setID(2);
//                dao.delete(new DalHints(), pojo);
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1, new DalHints());
//                ret.setAge(2000);
//                ret.setID(3);
//                dao.insert(new DalHints().enableIdentityInsert(), ret);//主键冲突
//                return true;
//            }
//        });
//
//        try {
//            client.execute(cmds, new DalHints());
//        } catch (Exception e) {
////			e.printStackTrace();
//        }
//
//        assertEquals(20, dao.queryByPk(1, new DalHints()).getAge().intValue());
//        assertEquals(21, dao.queryByPk(2, new DalHints()).getAge().intValue());
//        assertEquals(6, dao.count(new DalHints()));
//
//    }
//
//    @Test
//    public void testTransCommandsSetRollback() throws SQLException {
//        List<DalCommand> cmds = new LinkedList<>();
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1, new DalHints());
//                ret.setAge(1000);
//                dao.update(new DalHints(), ret);
//                return true;
//            }
//        });
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity pojo = new MysqlPersonExtendsUpdatableEntity();
//                pojo.setID(2);
//                dao.delete(new DalHints(), pojo);
////                DalTransactionManager.setRollbackOnly();
//                return true;
//            }
//        });
//
//        try {
//            client.execute(cmds, new DalHints());
//        } catch (Exception e) {
////			e.printStackTrace();
//        }
//
//        assertEquals(20, dao.queryByPk(1, new DalHints()).getAge().intValue());
//        assertEquals(21, dao.queryByPk(2, new DalHints()).getAge().intValue());
//        assertEquals(6, dao.count(new DalHints()));
//
//    }
//
//    @Test
//    public void testTransCommandsPass() throws SQLException {
//        List<DalCommand> cmds = new LinkedList<>();
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1, new DalHints());
//                ret.setAge(1000);
//                dao.update(new DalHints(), ret);
//                return true;
//            }
//        });
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity pojo = new MysqlPersonExtendsUpdatableEntity();
//                pojo.setID(2);
//                dao.delete(new DalHints(), pojo);
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1, new DalHints());
//                ret.setAge(2000);
//                ret.setID(7);
//                dao.insert(new DalHints().enableIdentityInsert(), ret);
//                return true;
//            }
//        });
//
//        try {
//            client.execute(cmds, new DalHints());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        assertEquals(1000, dao.queryByPk(1, new DalHints()).getAge().intValue());
//        assertNull(dao.queryByPk(2, new DalHints()));
//        assertEquals(2000, dao.queryByPk(7, new DalHints()).getAge().intValue());
//        assertEquals(6, dao.count(new DalHints()));
//
//    }
//
//    @Test
//    public void testTransCommandsPassDepand() throws SQLException {
//        final KeyHolder key = new KeyHolder();
//        List<DalCommand> cmds = new LinkedList<>();
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1, new DalHints());
//                dao.insert(new DalHints(), key, ret);
//                return true;
//            }
//        });
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity pojo = new MysqlPersonExtendsUpdatableEntity();
//                pojo.setID(key.getKey().intValue());
//                dao.delete(new DalHints(), pojo);
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1,
//                        new DalHints());
//                ret.setAge(2000);
//                ret.setID(8);
//                dao.insert(new DalHints().enableIdentityInsert(), ret);
//                return true;
//            }
//        });
//
//        try {
//            client.execute(cmds, new DalHints());
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }
//
//        assertEquals(7, dao.count(new DalHints()));
//        assertEquals(2000, dao.queryByPk(8, new DalHints()).getAge().intValue());
//
//    }
//
//    @Test
//    public void testTransCommandsFailDepand() throws SQLException {
//        final KeyHolder key = new KeyHolder();
//        List<DalCommand> cmds = new LinkedList<>();
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1,
//                        new DalHints());
////			ret.setAge(1000);
////			dao.update(new DalHints(), ret);
//
//                dao.insert(new DalHints(), key, ret);
//                return true;
//            }
//        });
//        cmds.add(new DalCommand() {
//            @Override
//            public boolean execute(DalClient client) throws SQLException {
//                MysqlPersonExtendsUpdatableEntity pojo = new MysqlPersonExtendsUpdatableEntity();
//                pojo.setID(key.getKey().intValue());
//                dao.delete(new DalHints(), pojo);
//                MysqlPersonExtendsUpdatableEntity ret = dao.queryByPk(1,
//                        new DalHints());
//                ret.setAge(2000);
//                ret.setID(2);
//                dao.insert(new DalHints().enableIdentityInsert(), ret);
//                return true;
//            }
//        });
//
//        try {
//            client.execute(cmds, new DalHints());
//        } catch (SQLException e) {
////			e.printStackTrace();
//        }
//
//
////		assertEquals(1000, dao.queryByPk(1, new DalHints()).getAge().intValue());
////		assertNull(dao.queryByPk(2, new DalHints()));
////		assertEquals(2000, dao.queryByPk(4, new DalHints()).getAge().intValue());
//        assertEquals(6, dao.count(new DalHints()));
////		assertEquals(2000, dao.queryByPk(5, new DalHints()).getAge().intValue());
//    }
//
//    @Test
//    public void testAlias() throws Exception {
//        Integer id = 1;// Test value here
//        List<TestAlias> ret = dao.test(id, new DalHints());
//        assertEquals(1, ret.get(0).getNum().intValue());
//        assertEquals("Initial_Shard_00", ret.get(0).getMyName());
//        assertEquals(1, ret.size());
//    }
}
