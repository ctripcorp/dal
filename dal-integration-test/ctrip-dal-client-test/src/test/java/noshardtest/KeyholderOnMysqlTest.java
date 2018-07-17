package noshardtest;


import com.ctrip.platform.dal.dao.*;
import dao.noshard.KeyholderTestOnMysqlDao;
import entity.MysqlKeyholderTestTable;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * JUnit test of PersonGenDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
 **/
public class KeyholderOnMysqlTest {

    private static final String DATA_BASE = "noShardTestOnMysql";

    private static DalClient client = null;
    private static KeyholderTestOnMysqlDao dao = null;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        client = DalClientFactory.getClient(DATA_BASE);
        dao = new KeyholderTestOnMysqlDao();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        dao.test_def_update(new DalHints());
        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
            daoPojo.setID(i+1);
            daoPojo.setAge(20 + i);
            daoPojo.setName("Initial_" + i);
            daoPojos.add(daoPojo);
        }
        dao.insert(new DalHints(), daoPojos);
    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_update(new DalHints());
//		Thread.sleep(5000);
    }


    @Test
    public void testCount() throws Exception {
        int affected = dao.count(new DalHints());
        assertEquals(6, affected);
    }

    @Test
    public void testDelete1() throws Exception {
        DalHints hints = new DalHints();
        MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
        daoPojo.setAge(20);
        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);
        int affected = dao.delete(hints, daoPojo);
        assertEquals(1, affected);
        assertEquals(5,dao.count(null));
    }


	@Test
	public void testDelete2() throws Exception {
		DalHints hints = new DalHints();
		List<MysqlKeyholderTestTable> daoPojos = dao.queryAll(null);
		int[] affected = dao.delete(hints, daoPojos);
		assertArrayEquals(new int[]{1,1,1,1,1,1},  affected);
        assertEquals(0,dao.count(null));
	}

	@Test
	public void testBatchDelete() throws Exception {
		DalHints hints = new DalHints();
		List<MysqlKeyholderTestTable> daoPojos = dao.queryAll(null);
		int[] affected = dao.batchDelete(hints, daoPojos);
//		assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1},  affected);
        assertEquals(0,dao.count(null));
	}

    @Test
    public void testQueryAll() throws Exception {
        List<MysqlKeyholderTestTable> list = dao.queryAll(new DalHints().selectByNames());
        assertEquals(6, list.size());
    }

    @Test
    public void testQueryAllByPage() throws Exception {
        List<MysqlKeyholderTestTable> list = dao.queryAllByPage(1, 3, new DalHints().selectByNames());
        assertEquals(3, list.size());
    }

    @Test
    public void testQueryByPK() throws Exception {
        MysqlKeyholderTestTable ret = dao.queryByPk(1, new DalHints().selectByNames());
        assertNotNull(ret.getID());

        MysqlKeyholderTestTable ret2 = dao.queryByPk(ret, new DalHints().selectByNames());
        assertNotNull(ret2.getID());
    }

    @Test
    public void testQueryLike() throws Exception {
        MysqlKeyholderTestTable sample = new MysqlKeyholderTestTable();
        sample.setName("Initial_0");
        List<MysqlKeyholderTestTable> list = dao.queryLike(sample, new DalHints().selectByNames());
        assertEquals(1, list.size());
    }

    @Test
    public void testInsert1() throws Exception {
        DalHints hints = new DalHints();
        MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao.insert(hints, daoPojo);
        assertEquals(1, affected);

        MysqlKeyholderTestTable ret = dao.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());

        daoPojo.setID(11);
        daoPojo.setName("insert1");
        dao.insert(new DalHints().enableIdentityInsert(),daoPojo);
        ret=dao.queryByPk(daoPojo,new DalHints());
        assertEquals("insert1", ret.getName());
    }

    @Test
    public void testInsert1SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");

        int affected = dao.insert(hints.setIdentityBack(), daoPojo);
        assertEquals(1, affected);
        assertEquals(10, daoPojo.getID().intValue());

        MysqlKeyholderTestTable ret = dao.queryByPk(daoPojo, new DalHints());
        assertNotNull(ret);
        assertEquals("insert", ret.getName());
    }

    @Test
    public void testInsert2SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();

        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            daoPojo.setName("Initial_" + (20 + i * 2));
            daoPojos.add(daoPojo);
        }

        int[] affected = dao.insert(hints.setIdentityBack(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
        assertEquals(12,dao.count(null));
        for (int i = 0; i < daoPojos.size(); i++) {
            assertEquals(20 + i * 2, daoPojos.get(i).getID().intValue());
        }
    }

    @Test
    public void testInsert2() throws Exception {
        DalHints hints = new DalHints();

        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            daoPojo.setName("Initial_" + (20 + i * 2));
            daoPojos.add(daoPojo);
        }

        int[] affected = dao.insert(hints.enableIdentityInsert(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
        assertEquals(12,dao.count(null));
        for (int i = 0; i < daoPojos.size(); i++) {
            assertEquals(20 + i * 2, daoPojos.get(i).getID().intValue());
        }
        MysqlKeyholderTestTable ret = dao.queryByPk(30, new DalHints());
        assertNotNull(ret);
    }


    @Test
    public void testInsert3() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");
        int affected = dao.insert(hints.enableIdentityInsert(), keyHolder, daoPojo);
        assertEquals(1, affected);
        assertEquals("insert", dao.queryByPk(10, new DalHints()).getName());
        assertEquals(10,daoPojo.getID().intValue());
        assertEquals(1, keyHolder.size());
        assertNull(keyHolder.getKey());
    }

    @Test
    public void testInsert3SetIdentityBack() throws Exception {
        KeyHolder keyHolder = new KeyHolder();
        MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
        daoPojo.setID(10);
        daoPojo.setAge(20);
        daoPojo.setName("insert");
        int affected = dao.insert(new DalHints().setIdentityBack(), keyHolder, daoPojo);
        assertEquals(1, affected);
        assertEquals("insert", dao.queryByPk(10, new DalHints()).getName());
        assertEquals(1, keyHolder.size());
        assertNull(keyHolder.getKey());
        assertEquals(10, daoPojo.getID().intValue());
    }

    @Test
    public void testInsert4() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
			daoPojo.setID(20+i*2);
            daoPojo.setAge(i + 20);
            daoPojo.setName("Initial_" + (20+i*2));
            daoPojos.add(daoPojo);
        }
        int[] affected = dao.insert(hints, keyHolder, daoPojos);

        assertArrayEquals(new int[]{1, 1, 1, 1, 1, 1}, affected);
        assertEquals(6, keyHolder.size());
        assertNull(keyHolder.getKey(5));
        assertEquals(20,daoPojos.get(0).getID().intValue());
    }

    @Test
    public void testInsert4SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
            daoPojo.setID(20+i*2);
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
        assertNull(keyHolder.getKey(0));
        for (int i = 0; i < daoPojos.size(); i++) {
            assertEquals(20 + i * 2, daoPojos.get(i).getID().intValue());
        }
    }

    @Test
    public void testInsert5() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
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

        MysqlKeyholderTestTable ret = dao.queryByPk(30, new DalHints());
        assertNotNull(ret);

    }


    @Test
    public void testCombinedInsert1() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
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

        MysqlKeyholderTestTable ret = dao.queryByPk(30, new DalHints());
        assertNotNull(ret);
    }

    @Test
    public void testCombinedInsert1etIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
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

        for (int i = 0; i < daoPojos.size(); i++) {
            assertEquals(20 + i * 2, daoPojos.get(i).getID().intValue());
        }
    }

    @Test
    public void testCombinedInsert2() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            daoPojo.setName("Initial_" + i);
            daoPojos.add(daoPojo);
        }
        int affected = dao.combinedInsert(hints.enableIdentityInsert(), keyHolder, daoPojos);
        assertEquals(6, affected);
        assertEquals(6, keyHolder.size());
        assertNull(keyHolder.getKey(0));
        MysqlKeyholderTestTable ret = dao.queryByPk(30, new DalHints());
        assertNotNull(ret);
        for (int i = 0; i < daoPojos.size(); i++) {
            assertEquals(20 + i * 2, daoPojos.get(i).getID().intValue());
        }
    }

    @Test
    public void testCombinedInsert2SetIdentityBack() throws Exception {
        DalHints hints = new DalHints();
        KeyHolder keyHolder = new KeyHolder();
        List<MysqlKeyholderTestTable> daoPojos = new ArrayList<>(
                6);
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
            daoPojo.setID(20 + i * 2);
            daoPojo.setAge(i + 20);
            daoPojo.setName("Initial_" + i);
            daoPojos.add(daoPojo);
        }
        int affected = dao.combinedInsert(hints.setIdentityBack(), keyHolder, daoPojos);
        assertEquals(6, affected);
        assertEquals(6, keyHolder.size());
        assertNull(keyHolder.getKey(0));
        for (int i = 0; i < daoPojos.size(); i++) {
            assertEquals(20 + i * 2, daoPojos.get(i).getID().intValue());
        }
    }

    @Test
    public void testQuery() throws Exception {
//		DalHints hints = new DalHints();
//		int pageSize = 100;
//		int pageNo = 1;
        List<MysqlKeyholderTestTable> list = dao.queryAll(null);
        assertEquals(6, list.size());
    }

    //
    @Test
    public void testQueryByPk1() throws Exception {
        int id = 1;
        DalHints hints = new DalHints();
        MysqlKeyholderTestTable affected = dao.queryByPk(id, hints);
        assertNotNull(affected);
    }

    //
    @Test
    public void testQueryByPk2() throws Exception {
        DalHints hints = new DalHints();
        MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
        daoPojo.setAge(20);

        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);
        MysqlKeyholderTestTable affected = dao.queryByPk(daoPojo, hints);
        assertNotNull(affected);
    }

    //
    @Test
    public void testUpdate1() throws Exception {
        DalHints hints = new DalHints();
        MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
        daoPojo.setAge(20);

        daoPojo.setName("Initial_Shard_00");
        daoPojo.setID(1);

        int affected = dao.update(hints, daoPojo);
        assertEquals(1, affected);
//		daoPojo = dao.queryByPk(createPojo(1), null);
//		verifyPojo(daoPojo);
    }

    @Test
    public void testtest_def_update() throws Exception {
        dao.test_def_update(new DalHints());
        int ret = dao.count(new DalHints());
        assertEquals(0, ret);
    }
}
