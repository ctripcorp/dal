package shardtest.newVersionCodeTest;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.sqlbuilder.MatchPattern;
import dao.noshard.KeyholderTestOnMysqlDao;
import dao.shard.newVersionCode.PersonShardColModShardByDBOnMysqlDao;
import entity.MysqlKeyholderTestTable;
import entity.MysqlPersonTable;
import org.junit.*;
import testUtil.DalHintsChecker;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.Assert.*;

/**
 * JUnit test of ignoreMissingFieldsAndAllowPartialTestOnMysqlDao class.
 * Before run the unit test, you should initiate the test data and change all the asserts correspond to you case.
 **/
public class PersonShardColModShardByDBOnMysqlDaoUnitTest {

    private static final String DATA_BASE = "ShardColModShardByDBOnMysql";
    private static DalClient client = null;
    private static PersonShardColModShardByDBOnMysqlDao dao = null;
    private static KeyholderTestOnMysqlDao keyholderTestOnMysqlDao = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        client = DalClientFactory.getClient(DATA_BASE);
        dao = new PersonShardColModShardByDBOnMysqlDao();
        keyholderTestOnMysqlDao = new KeyholderTestOnMysqlDao(DATA_BASE);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        dao.test_def_truncate(new DalHints().inShard(0));
        dao.test_def_truncate(new DalHints().inShard(1));
        keyholderTestOnMysqlDao.test_def_update(new DalHints().inShard(0));
        keyholderTestOnMysqlDao.test_def_update(new DalHints().inShard(1));
        List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(
                6);
        for (int i = 0; i < 6; i++) {
            MysqlPersonTable daoPojo = new MysqlPersonTable();
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
            else
                daoPojo.setName("Initial_Shard_1" + i);
            daoPojos.add(daoPojo);
        }
        dao.insert(new DalHints(), daoPojos);

        List<MysqlKeyholderTestTable> daoPojos2 = new ArrayList<MysqlKeyholderTestTable>();
        for (int i = 0; i < 6; i++) {
            MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
            daoPojo.setID(i + 1);
            daoPojo.setAge(i + 20);
            if (i % 2 == 0)
                daoPojo.setName("Initial_Shard_0" + i);
            else
                daoPojo.setName("Initial_Shard_1" + i);
            daoPojos2.add(daoPojo);
        }
        keyholderTestOnMysqlDao.insert(new DalHints(), daoPojos2);

    }

    @After
    public void tearDown() throws Exception {
//		dao.test_def_truncate(new DalHints().inShard(0));
//		dao.test_def_truncate(new DalHints().inShard(1));
//		Thread.sleep(5000);
    }


    @Test
    public void testCount() throws Exception {
        int affected = dao.count(new DalHints().setShardColValue("Age", 20));
        assertEquals(3, affected);

        affected = dao.count(new DalHints().setShardColValue("Age", 21));
        assertEquals(3, affected);

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.count(hints.inAllShards());
        assertEquals(6, affected);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testDelete1() throws Exception {
        MysqlPersonTable daoPojo = new MysqlPersonTable();
        daoPojo.setID(2);
        daoPojo.setAge(20);
        int affected = dao.delete(new DalHints(), daoPojo);
        assertEquals(1, affected);

        affected = dao.count(new DalHints().inShard(0));
        assertEquals(2, affected);

        affected = dao.count(new DalHints().inShard(1));
        assertEquals(3, affected);

        daoPojo.setID(20);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.delete(hints, daoPojo);
        assertEquals(0, affected);
        DalHintsChecker.checkEquals(original, hints);
    }


    //返回数组里是影响行数
    @Test
    public void testDelete2() throws Exception {
        //singleShard
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints().inShard(0));
        int[] affected = dao.delete(new DalHints().setShardColValue("Age", 20), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(0, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(3, ret);

        //crossShard
        daoPojos.get(0).setAge(21);
        daoPojos.get(2).setID(20);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.delete(hints, daoPojos);
        assertArrayEquals(new int[]{1, 0, 0}, affected);
        DalHintsChecker.checkEquals(original, hints);

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(0, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(2, ret);

    }

    @Test
    public void testBatchDelete() throws Exception {
        //singleShard
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints().inShard(1));
        int[] affected = dao.batchDelete(new DalHints(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(3, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(0, ret);

        //crossShard
        daoPojos = dao.queryAll(new DalHints().inShard(0));
        daoPojos.get(1).setAge(21);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.batchDelete(hints, daoPojos);
        assertArrayEquals(new int[]{1, 0, 1}, affected);
        DalHintsChecker.checkEquals(original, hints);

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(1, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(0, ret);
    }

    @Test
    public void testInsertSingleSetIdentityBackKeyholderTest() throws Exception {
        MysqlKeyholderTestTable daoPojo = new MysqlKeyholderTestTable();
        daoPojo.setID(10);
        daoPojo.setAge(100);
        int affected = keyholderTestOnMysqlDao.insert(new DalHints().setIdentityBack(), daoPojo);
        assertEquals(1, affected);
        assertEquals(10, daoPojo.getID().intValue());

        MysqlKeyholderTestTable ret = keyholderTestOnMysqlDao.queryByPk(10, new DalHints().inShard(0));
        assertEquals(10,ret.getID().intValue());
        ret = keyholderTestOnMysqlDao.queryByPk(10, new DalHints().inShard(1));
        assertNull(ret);

        KeyHolder keyHolder = new KeyHolder();
        MysqlKeyholderTestTable daoPojo2 = new MysqlKeyholderTestTable();
        daoPojo2.setID(11);
        daoPojo2.setAge(101);
        affected = keyholderTestOnMysqlDao.insert(new DalHints(), keyHolder, daoPojo2);
        assertEquals(1, affected);
        assertEquals(11, daoPojo2.getID().intValue());
        assertEquals(1, keyHolder.size());

        ret = keyholderTestOnMysqlDao.queryByPk(11, new DalHints().inShard(1));
        assertNotNull(ret);
        ret = keyholderTestOnMysqlDao.queryByPk(11, new DalHints().inShard(0));
        assertNull(ret);
    }


    @Test
    public void testInsert1SetIdentityBack() throws Exception {
        MysqlPersonTable daoPojo = new MysqlPersonTable();
        daoPojo.setAge(100);
        int affected = dao.insert(new DalHints().setIdentityBack(), daoPojo);
        assertEquals(1, affected);
        assertEquals(4, daoPojo.getID().intValue());

        MysqlPersonTable ret = dao.queryByPk(4, new DalHints().inShard(0));
        assertNotNull(ret);
        ret = dao.queryByPk(4, new DalHints().inShard(1));
        assertNull(ret);

        //insert null
        MysqlPersonTable nullPojo = new MysqlPersonTable();
        affected = dao.insert(new DalHints().inShard(0).setIdentityBack(), nullPojo);
        assertEquals(1, affected);
        assertEquals(5, nullPojo.getID().intValue());

        ret = dao.queryByPk(5, new DalHints().inShard(0));
        assertNotNull(ret);
        ret = dao.queryByPk(4, new DalHints().inShard(1));
        assertNull(ret);
    }

    @Test
    public void testInsert1() throws Exception {
        MysqlPersonTable daoPojo = new MysqlPersonTable();
        daoPojo.setAge(100);
        int affected = dao.insert(new DalHints(), daoPojo);
        assertEquals(1, affected);
        MysqlPersonTable ret = dao.queryByPk(4, new DalHints().inShard(0));
        assertNotNull(ret);
        ret = dao.queryByPk(4, new DalHints().inShard(1));
        assertNull(ret);

        //insert null
        MysqlPersonTable nullPojo = new MysqlPersonTable();
        affected = dao.insert(new DalHints().inShard(0), nullPojo);
        assertEquals(1, affected);
        ret = dao.queryByPk(5, new DalHints().inShard(0));
        assertNotNull(ret);
        ret = dao.queryByPk(4, new DalHints().inShard(1));
        assertNull(ret);

        //enableIdentityInsert
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        MysqlPersonTable enableIdentityInsertPojo = new MysqlPersonTable();
        enableIdentityInsertPojo.setID(20);
        affected = dao.insert(hints.inShard(0).enableIdentityInsert(), enableIdentityInsertPojo);
        assertEquals(1, affected);

        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.enableIdentityInsert);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.queryByPk(20, new DalHints().inShard(0));
        assertNotNull(ret);
        ret = dao.queryByPk(4, new DalHints().inShard(1));
        assertNull(ret);
    }

    @Test
    public void testInsert2() throws Exception {
        //singleShard
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints().inShard(0));
        int[] affected = dao.insert(new DalHints(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(6, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(3, ret);

        //crossShard
        daoPojos.get(1).setAge(33);
        affected = dao.insert(new DalHints(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);

        //enableIdentityInsert
        daoPojos.get(0).setID(null);
        daoPojos.get(1).setID(40);
        daoPojos.get(2).setID(null);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        dao.insert(hints.enableIdentityInsert(), daoPojos);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.enableIdentityInsert);
        DalHintsChecker.checkEquals(original, hints, exclude);
        assertNotNull(dao.queryByPk(40, new DalHints().inShard(1)));

        //insert null
        List<MysqlPersonTable> nullPojos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MysqlPersonTable nullPojo = new MysqlPersonTable();
            nullPojos.add(nullPojo);
        }
        affected = dao.insert(new DalHints().inShard(0), nullPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
    }

    @Test
    public void testInsert2SetIdentityBack() throws Exception {
        //singleShard
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints().inShard(0));
        int[] affected = dao.insert(new DalHints().setIdentityBack(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);

        for (int i = 0; i < affected.length; i++) {
            assertEquals(i + 4, daoPojos.get(i).getID().intValue());
        }

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(6, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(3, ret);

        //crossShard
        daoPojos.get(1).setAge(33);
        affected = dao.insert(new DalHints().setIdentityBack(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(7, daoPojos.get(0).getID().intValue());
        assertEquals(4, daoPojos.get(1).getID().intValue());
        assertEquals(8, daoPojos.get(2).getID().intValue());

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);


        //insert null
        List<MysqlPersonTable> nullPojos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MysqlPersonTable nullPojo = new MysqlPersonTable();
            nullPojos.add(nullPojo);
        }
        affected = dao.insert(new DalHints().inShard(0).setIdentityBack(), nullPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(9, nullPojos.get(0).getID().intValue());
        assertEquals(10, nullPojos.get(1).getID().intValue());
        assertEquals(11, nullPojos.get(2).getID().intValue());
    }

    @Test
    public void testInsertListSetIdentityBackKeyholderTest() throws Exception {
        //singleShard
        List<MysqlKeyholderTestTable> daoPojos = keyholderTestOnMysqlDao.queryAll(new DalHints().inShard(0));
        for (MysqlKeyholderTestTable pojo : daoPojos)
            pojo.setID(pojo.getID() + 10);
        int[] affected = keyholderTestOnMysqlDao.insert(new DalHints().setIdentityBack(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(11, daoPojos.get(0).getID().intValue());

        int ret = keyholderTestOnMysqlDao.count(new DalHints().inShard(0));
        assertEquals(6, ret);

        ret = keyholderTestOnMysqlDao.count(new DalHints().inShard(1));
        assertEquals(3, ret);

        //crossShard
        for (MysqlKeyholderTestTable pojo : daoPojos)
            pojo.setID(pojo.getID() + 20);
        daoPojos.get(1).setAge(33);
        affected = keyholderTestOnMysqlDao.insert(new DalHints().setIdentityBack(), daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(31, daoPojos.get(0).getID().intValue());
        assertEquals(33, daoPojos.get(1).getID().intValue());
        assertEquals(35, daoPojos.get(2).getID().intValue());

        ret = keyholderTestOnMysqlDao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = keyholderTestOnMysqlDao.count(new DalHints().inShard(1));
        assertEquals(4, ret);
    }

    @Test
    public void testInsert3() throws Exception {
        KeyHolder keyHolder1 = new KeyHolder();
        MysqlPersonTable daoPojo = new MysqlPersonTable();
        daoPojo.setAge(101);
        int affected = dao.insert(new DalHints(), keyHolder1, daoPojo);
        assertEquals(1, affected);
        assertEquals(1, keyHolder1.size());
        assertEquals(4l, keyHolder1.getKey());

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(3, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);

        //insert null
        KeyHolder keyHolder2 = new KeyHolder();
        daoPojo.setAge(null);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.insert(hints.inShard(0), keyHolder2, daoPojo);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        DalHintsChecker.checkEquals(original, hints, exclude);

        assertEquals(1, affected);
        assertEquals(1, keyHolder2.size());
        assertEquals(4l, keyHolder2.getKey());

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(4, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);

        //enableIdentityInsert
        KeyHolder keyHolder3 = new KeyHolder();
        daoPojo.setID(70);
        affected = dao.insert(new DalHints().inShard(0).enableIdentityInsert(), keyHolder3, daoPojo);
        assertEquals(1, affected);
        assertEquals(1, keyHolder3.size());
        assertEquals(70l, keyHolder3.getKey());

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(5, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);
    }

    @Test
    public void testInsert3SetIdentityBack() throws Exception {
        KeyHolder keyHolder1 = new KeyHolder();
        MysqlPersonTable daoPojo = new MysqlPersonTable();
        daoPojo.setAge(101);
        int affected = dao.insert(new DalHints().setIdentityBack(), keyHolder1, daoPojo);
        assertEquals(1, affected);
        assertEquals(1, keyHolder1.size());
        assertEquals(4l, keyHolder1.getKey());
        assertEquals(4, daoPojo.getID().intValue());

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(3, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);

        //insert null
        KeyHolder keyHolder2 = new KeyHolder();
        daoPojo.setAge(null);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.insert(hints.inShard(0).setIdentityBack(), keyHolder2, daoPojo);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.setIdentityBack);
        DalHintsChecker.checkEquals(original, hints, exclude);

        assertEquals(1, affected);
        assertEquals(1, keyHolder2.size());
        assertEquals(4l, keyHolder2.getKey());
        assertEquals(4, daoPojo.getID().intValue());

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(4, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);


    }

    @Test
    public void testInsert4() throws Exception {
        //singleShard
        KeyHolder keyHolder1 = new KeyHolder();
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints().inShard(0));
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        int[] affected = dao.insert(hints.inShard(1), keyHolder1, daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(3, keyHolder1.size());
        assertEquals(4l, keyHolder1.getKey(0));
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        DalHintsChecker.checkEquals(original, hints, exclude);

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(3, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(6, ret);

        //crossShard
        KeyHolder keyHolder2 = new KeyHolder();
        daoPojos.get(1).setAge(31);
        affected = dao.insert(new DalHints(), keyHolder2, daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(3, keyHolder2.size());
        assertEquals(4l, keyHolder2.getKey(0));
        assertEquals(7l, keyHolder2.getKey(1));
        assertEquals(5l, keyHolder2.getKey(2));

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(5, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(7, ret);

        //insert null
        KeyHolder keyHolder3 = new KeyHolder();
        List<MysqlPersonTable> nullPojos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MysqlPersonTable nullPojo = new MysqlPersonTable();
            nullPojos.add(nullPojo);
        }
        affected = dao.insert(new DalHints().inShard(0), keyHolder3, nullPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(3, keyHolder3.size());
        assertEquals(6l, keyHolder3.getKey(0));
        assertEquals(7l, keyHolder3.getKey(1));
        assertEquals(8l, keyHolder3.getKey(2));

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(7, ret);

        //enableIdentityInsert
        KeyHolder keyHolder4 = new KeyHolder();
        daoPojos.get(0).setID(null);
        daoPojos.get(1).setID(90);
        daoPojos.get(2).setID(null);
        affected = dao.insert(new DalHints().enableIdentityInsert(), keyHolder4, daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(3, keyHolder4.size());
        assertEquals(9l, keyHolder4.getKey(0));
        assertEquals(90l, keyHolder4.getKey(1));
        assertEquals(10l, keyHolder4.getKey(2));
    }

    @Test
    public void testInsert4SetIdentityBack() throws Exception {
        //singleShard
        KeyHolder keyHolder1 = new KeyHolder();
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints().inShard(0));
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        int[] affected = dao.insert(hints.inShard(1).setIdentityBack(), keyHolder1, daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(3, keyHolder1.size());
        int index = 0;
        for (MysqlPersonTable pojo : daoPojos)
            assertEquals(keyHolder1.getKey(index++).intValue(), pojo.getID().intValue());

        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.setIdentityBack);
        DalHintsChecker.checkEquals(original, hints, exclude);

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(3, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(6, ret);

        //crossShard
        KeyHolder keyHolder2 = new KeyHolder();
        daoPojos.get(1).setAge(31);
        affected = dao.insert(new DalHints().setIdentityBack(), keyHolder2, daoPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(3, keyHolder2.size());
        assertEquals(4l, keyHolder2.getKey(0));
        assertEquals(4, daoPojos.get(0).getID().intValue());
        assertEquals(7l, keyHolder2.getKey(1));
        assertEquals(7, daoPojos.get(1).getID().intValue());
        assertEquals(5l, keyHolder2.getKey(2));
        assertEquals(5, daoPojos.get(2).getID().intValue());

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(5, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(7, ret);

        //insert null
        KeyHolder keyHolder3 = new KeyHolder();
        List<MysqlPersonTable> nullPojos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MysqlPersonTable nullPojo = new MysqlPersonTable();
            nullPojos.add(nullPojo);
        }
        affected = dao.insert(new DalHints().inShard(0).setIdentityBack(), keyHolder3, nullPojos);
        assertArrayEquals(new int[]{1, 1, 1}, affected);
        assertEquals(3, keyHolder3.size());
        assertEquals(6l, keyHolder3.getKey(0));
        assertEquals(7l, keyHolder3.getKey(1));
        assertEquals(8l, keyHolder3.getKey(2));

        int index2 = 0;
        for (MysqlPersonTable pojo : nullPojos)
            assertEquals(keyHolder3.getKey(index2++).intValue(), pojo.getID().intValue());

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(7, ret);

    }

    @Test
    public void testInsert5() throws Exception {
        // singleShard
        List<MysqlPersonTable> daoPojos = dao.queryAll(new DalHints().inShard(0));
        int[] affected = dao.batchInsert(new DalHints(), daoPojos);
//		assertArrayEquals(new int[] { -2, -2, -2 }, affected);

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(6, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(3, ret);

        // crossShard
        daoPojos.get(1).setAge(33);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.batchInsert(hints, daoPojos);
//		assertArrayEquals(new int[] { -2, -2, -2 }, affected);
        DalHintsChecker.checkEquals(original, hints);

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);

        // enableIdentityInsert
        daoPojos.get(0).setID(null);
        daoPojos.get(1).setID(40);
        daoPojos.get(2).setID(null);
        dao.batchInsert(new DalHints().enableIdentityInsert(), daoPojos);
        assertNotNull(dao.queryByPk(40, new DalHints().inShard(1)));

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(10, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(5, ret);

        // insert null
//		List<ignoreMissingFieldsAndAllowPartialTestOnMysql> nullPojos = new ArrayList<>();
//		for (int i = 0; i < 3; i++) {
//			ignoreMissingFieldsAndAllowPartialTestOnMysql nullPojo = new ignoreMissingFieldsAndAllowPartialTestOnMysql();
//			nullPojos.add(nullPojo);
//		}
//		affected = dao.batchInsert(new DalHints().inShard(0), nullPojos);
////		assertArrayEquals(new int[] { -2, -2, -2 }, affected);
//		ret = dao.count(new DalHints().inShard(0));
//		assertEquals(13, ret);
//
//		ret = dao.count(new DalHints().inShard(1));
//		assertEquals(5, ret);
    }

    @Test
    public void testCombinedInsert1() throws Exception {
        //singleShard
        List<MysqlPersonTable> daoPojos = dao
                .queryAll(new DalHints().inShard(0));
        int affected = dao.combinedInsert(new DalHints(), daoPojos);
        assertEquals(3, affected);

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(6, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(3, ret);

        // crossShard
        daoPojos.get(1).setAge(33);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.combinedInsert(hints, daoPojos);
        assertEquals(3, affected);
        DalHintsChecker.checkEquals(original, hints);

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);

        // enableIdentityInsert
        daoPojos.get(0).setID(null);
        daoPojos.get(1).setID(40);
        daoPojos.get(2).setID(null);
        dao.combinedInsert(new DalHints().enableIdentityInsert(), daoPojos);
        assertNotNull(dao.queryByPk(40, new DalHints().inShard(1)));

        // insert null
        List<MysqlPersonTable> nullPojos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MysqlPersonTable nullPojo = new MysqlPersonTable();
            nullPojos.add(nullPojo);
        }
        affected = dao.combinedInsert(new DalHints().inShard(0), nullPojos);
        assertEquals(3, affected);
    }

    @Test
    public void testCombinedInsert1SetIdentityBack() throws Exception {
        //singleShard
        List<MysqlPersonTable> daoPojos = dao
                .queryAll(new DalHints().inShard(0));
        int affected = dao.combinedInsert(new DalHints().setIdentityBack(), daoPojos);
        assertEquals(3, affected);
        assertEquals(4, daoPojos.get(0).getID().intValue());
        assertEquals(5, daoPojos.get(1).getID().intValue());
        assertEquals(6, daoPojos.get(2).getID().intValue());

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(6, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(3, ret);

        // crossShard
        daoPojos.get(1).setAge(33);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.combinedInsert(hints.setIdentityBack(), daoPojos);
        assertEquals(3, affected);
//		DalHintsChecker.checkEquals(original,hints);

        assertEquals(7, daoPojos.get(0).getID().intValue());
        assertEquals(4, daoPojos.get(1).getID().intValue());
        assertEquals(8, daoPojos.get(2).getID().intValue());

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(4, ret);


        // insert null
        List<MysqlPersonTable> nullPojos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MysqlPersonTable nullPojo = new MysqlPersonTable();
            nullPojos.add(nullPojo);
        }
        affected = dao.combinedInsert(new DalHints().inShard(0).setIdentityBack(), nullPojos);
        assertEquals(3, affected);
        assertEquals(9, nullPojos.get(0).getID().intValue());
        assertEquals(10, nullPojos.get(1).getID().intValue());
        assertEquals(11, nullPojos.get(2).getID().intValue());
    }

    @Test
    public void testCombinedInsert2() throws Exception {
        // singleShard
        KeyHolder keyHolder1 = new KeyHolder();
        List<MysqlPersonTable> daoPojos = dao
                .queryAll(new DalHints().inShard(0));
        int affected = dao.combinedInsert(new DalHints().inShard(1),
                keyHolder1, daoPojos);
        assertEquals(3, affected);
        assertEquals(3, keyHolder1.size());
        assertEquals(4l, keyHolder1.getKey(0));

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(3, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(6, ret);

        // crossShard
        KeyHolder keyHolder2 = new KeyHolder();
        daoPojos.get(1).setAge(31);
        affected = dao.combinedInsert(new DalHints(), keyHolder2, daoPojos);
        assertEquals(3, affected);
        assertEquals(3, keyHolder2.size());
        assertEquals(4l, keyHolder2.getKey(0));
        assertEquals(7l, keyHolder2.getKey(1));
        assertEquals(5l, keyHolder2.getKey(2));

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(5, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(7, ret);

        // insert null
        KeyHolder keyHolder3 = new KeyHolder();
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<MysqlPersonTable> nullPojos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MysqlPersonTable nullPojo = new MysqlPersonTable();
            nullPojos.add(nullPojo);
        }
        affected = dao.combinedInsert(hints.inShard(0), keyHolder3,
                nullPojos);
        assertEquals(3, affected);
        assertEquals(3, keyHolder3.size());
        assertEquals(6l, keyHolder3.getKey(0));
        assertEquals(7l, keyHolder3.getKey(1));
        assertEquals(8l, keyHolder3.getKey(2));
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(7, ret);

        // enableIdentityInsert
        KeyHolder keyHolder4 = new KeyHolder();
        daoPojos.get(0).setID(null);
        daoPojos.get(1).setID(90);
        daoPojos.get(2).setID(null);
        affected = dao.combinedInsert(new DalHints().enableIdentityInsert(),
                keyHolder4, daoPojos);
        assertEquals(3, affected);
        assertEquals(3, keyHolder4.size());
        assertEquals(9l, keyHolder4.getKey(0));
        assertEquals(90l, keyHolder4.getKey(1));
        assertEquals(10l, keyHolder4.getKey(2));
    }

    @Test
    public void testCombinedInsert2SetIdentityBack() throws Exception {
        // singleShard
        KeyHolder keyHolder1 = new KeyHolder();
        List<MysqlPersonTable> daoPojos = dao
                .queryAll(new DalHints().inShard(0));
        int affected = dao.combinedInsert(new DalHints().inShard(1).setIdentityBack(),
                keyHolder1, daoPojos);
        assertEquals(3, affected);
        assertEquals(3, keyHolder1.size());
        assertEquals(4l, keyHolder1.getKey(0));

        int index = 0;
        for (MysqlPersonTable pojo : daoPojos)
            assertEquals(keyHolder1.getKey(index++).intValue(), pojo.getID().intValue());

        int ret = dao.count(new DalHints().inShard(0));
        assertEquals(3, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(6, ret);

        // crossShard
        KeyHolder keyHolder2 = new KeyHolder();
        daoPojos.get(1).setAge(31);
        affected = dao.combinedInsert(new DalHints().setIdentityBack(), keyHolder2, daoPojos);
        assertEquals(3, affected);
        assertEquals(3, keyHolder2.size());
        assertEquals(4l, keyHolder2.getKey(0));
        assertEquals(7l, keyHolder2.getKey(1));
        assertEquals(5l, keyHolder2.getKey(2));

        int index2 = 0;
        for (MysqlPersonTable pojo : daoPojos)
            assertEquals(keyHolder2.getKey(index2++).intValue(), pojo.getID().intValue());

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(5, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(7, ret);

        // insert null
        KeyHolder keyHolder3 = new KeyHolder();
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<MysqlPersonTable> nullPojos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MysqlPersonTable nullPojo = new MysqlPersonTable();
            nullPojos.add(nullPojo);
        }
        affected = dao.combinedInsert(hints.inShard(0).setIdentityBack(), keyHolder3,
                nullPojos);
        assertEquals(3, affected);
        assertEquals(3, keyHolder3.size());
        assertEquals(6l, keyHolder3.getKey(0));
        assertEquals(7l, keyHolder3.getKey(1));
        assertEquals(8l, keyHolder3.getKey(2));

        int index3 = 0;
        for (MysqlPersonTable pojo : nullPojos)
            assertEquals(keyHolder3.getKey(index3++).intValue(), pojo.getID().intValue());

        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.setIdentityBack);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.count(new DalHints().inShard(0));
        assertEquals(8, ret);

        ret = dao.count(new DalHints().inShard(1));
        assertEquals(7, ret);

    }

    @Test
    public void testQueryByPk1() throws Exception {
        Number id = 1;
        MysqlPersonTable affected = dao.queryByPk(id, new DalHints().setShardColValue("Age", 20));
        assertNotNull(affected);
        assertEquals("Initial_Shard_00", affected.getName());

        id = 2;
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.queryByPk(id, hints.setShardColValue("Age", 21));
        assertNotNull(affected);
        assertEquals("Initial_Shard_13", affected.getName());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardColValues);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testQueryByPk2() throws Exception {
        MysqlPersonTable pk = new MysqlPersonTable();
        pk.setID(2);
        MysqlPersonTable affected = dao.queryByPk(pk, new DalHints().setShardColValue("Age", 20));
        assertNotNull(affected);
        assertEquals("Initial_Shard_02", affected.getName());

        pk.setID(1);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.queryByPk(pk, hints.setShardColValue("Age", 21));
        assertNotNull(affected);
        assertEquals("Initial_Shard_11", affected.getName());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardColValues);
        exclude.add(DalHintEnum.fields);//queryByPK没有clean up fields
        DalHintsChecker.checkEquals(original, hints, exclude);

        pk.setAge(20);
        affected = dao.queryByPk(pk, new DalHints());
        assertEquals("Initial_Shard_00", affected.getName());

        pk.setAge(21);
        affected = dao.queryByPk(pk, new DalHints());
        assertEquals("Initial_Shard_11", affected.getName());
    }

    @Test
    public void testQueryLike() throws Exception {
        MysqlPersonTable sample1 = new MysqlPersonTable();
        sample1.setAge(20);
        sample1.setName("Initial_Shard_0");

        List<MysqlPersonTable> ret = dao.queryLike(sample1, new DalHints().inShard(0));
        assertEquals(0, ret.size());

        ret = dao.queryLike(sample1, new DalHints().inShard(1));
        assertEquals(0, ret.size());

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.queryLike(sample1, hints.inAllShards());
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.fields);//queryLike没有clean up fields
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testQueryAllByPage() throws Exception {
        List<MysqlPersonTable> ret = dao.queryAllByPage(2, 1, new DalHints().setShardValue(20));
        assertEquals("Initial_Shard_02", ret.get(0).getName());

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.queryAllByPage(1, 2, hints.setShardValue(21));
        assertEquals("Initial_Shard_13", ret.get(1).getName());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardValue);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testQueryAll() throws Exception {
        List<MysqlPersonTable> ret = dao.queryAll(new DalHints().setShardValue(20));
        assertEquals(3, ret.size());

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.queryAll(hints.inAllShards());
        assertEquals(6, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testUpdate1() throws Exception {
        //singleShard
        MysqlPersonTable daoPojo = new MysqlPersonTable();
        daoPojo.setID(1);
        daoPojo.setName("updateshard0");
        int affected = dao.update(new DalHints().inShard(0), daoPojo);
        assertEquals(1, affected);

        MysqlPersonTable ret = dao.queryByPk(1, new DalHints().inShard(0));
        assertEquals("updateshard0", ret.getName());
        assertEquals(20, ret.getAge().intValue());

        daoPojo.setID(20);
        affected = dao.update(new DalHints().inShard(1), daoPojo);
        assertEquals(0, affected);

        //updateNullField
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        daoPojo.setID(1);
        daoPojo.setName("updateNullField");
        affected = dao.update(hints.inShard(1).updateNullField(), daoPojo);
        assertEquals(1, affected);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.updateNullField);
        DalHintsChecker.checkEquals(original, hints, exclude);


        ret = dao.queryByPk(1, new DalHints().inShard(1));
        assertEquals("updateNullField", ret.getName());
        assertNull(ret.getAge());
    }

    //new DalHints().set(DalHintEnum.updateNullField)
    @Test
    public void testUpdate1_null() throws Exception {
        MysqlPersonTable daoPojo = new MysqlPersonTable();
        daoPojo.setID(1);
        daoPojo.setName(null);
        daoPojo.setAge(20);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        int affected = dao.update(hints.set(DalHintEnum.updateNullField).inShard(0), daoPojo);
        assertEquals(1, affected);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.updateNullField);
        DalHintsChecker.checkEquals(original, hints, exclude);

        MysqlPersonTable ret = dao.queryByPk(1, new DalHints().inShard(0));
//		assertEquals(null, ret.getName());
        assertNull(ret.getName());
    }

    @Test
    public void testUpdate2() throws Exception {
        // crossShard
        List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(2);
        MysqlPersonTable daoPojo1 = new MysqlPersonTable();

        daoPojo1.setID(1);
        daoPojo1.setAge(20);
        daoPojo1.setName("updateCrossShard1");

        daoPojos.add(daoPojo1);

        MysqlPersonTable daoPojo2 = new MysqlPersonTable();

        daoPojo2.setID(1);
        daoPojo2.setAge(21);
        daoPojo2.setName("updateCrossShard2");

        daoPojos.add(daoPojo2);

        int[] affected = dao.update(new DalHints(), daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected);

        MysqlPersonTable ret = dao.queryByPk(1,
                new DalHints().inShard(0));
        assertEquals("updateCrossShard1", ret.getName());
        ret = dao.queryByPk(1, new DalHints().inShard(1));
        assertEquals("updateCrossShard2", ret.getName());

        // single Shard
        List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(
                2);
        MysqlPersonTable daoPojo3 = new MysqlPersonTable();

        daoPojo3.setID(1);
        daoPojo3.setName("updateSingleShard1");

        daoPojos2.add(daoPojo3);

        MysqlPersonTable daoPojo4 = new MysqlPersonTable();

        daoPojo4.setID(2);
        daoPojo4.setName("updateSingleShard2");

        daoPojos2.add(daoPojo4);

        affected = dao.update(new DalHints().inShard(0), daoPojos2);
        assertArrayEquals(new int[]{1, 1}, affected);

        ret = dao.queryByPk(1, new DalHints().inShard(0));
        assertEquals("updateSingleShard1", ret.getName());
        assertNotNull(ret.getAge());
        ret = dao.queryByPk(2, new DalHints().inShard(0));
        assertEquals("updateSingleShard2", ret.getName());
        assertNotNull(ret.getAge());

        daoPojos2.get(0).setID(20);
        affected = dao.update(new DalHints().inShard(1), daoPojos2);
        assertArrayEquals(new int[]{0, 1}, affected);

        // updateNullField
        List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(
                2);
        MysqlPersonTable daoPojo5 = new MysqlPersonTable();

        daoPojo5.setID(1);

        daoPojos3.add(daoPojo5);

        MysqlPersonTable daoPojo6 = new MysqlPersonTable();

        daoPojo6.setID(2);
        daoPojo6.setName("updateNullField");

        daoPojos3.add(daoPojo6);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.update(hints.inShard(1).updateNullField(),
                daoPojos3);
        assertArrayEquals(new int[]{1, 1}, affected);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.updateNullField);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.queryByPk(1, new DalHints().inShard(1));
        assertNull(ret.getName());
        assertNull(ret.getAge());
        ret = dao.queryByPk(2, new DalHints().inShard(1));
        assertEquals("updateNullField", ret.getName());
        assertNull(ret.getAge());
    }


    @Test
    public void testBatchUpdateSingle() throws Exception {
        List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(2);
        MysqlPersonTable daoPojo1 = new MysqlPersonTable();

        daoPojo1.setID(1);
        daoPojo1.setAge(20);
        daoPojo1.setName("updateShard0");

        daoPojos.add(daoPojo1);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        int[] affected = dao.batchUpdate(hints, daoPojos);
        assertArrayEquals(new int[]{1}, affected);
        DalHintsChecker.checkEquals(original, hints);
        MysqlPersonTable ret = dao.queryByPk(1, new DalHints().inShard(0));
        assertEquals("updateShard0", ret.getName());
    }

    @Test
    public void testBatchUpdate() throws Exception {
        // crossShard
        List<MysqlPersonTable> daoPojos = new ArrayList<MysqlPersonTable>(2);
        MysqlPersonTable daoPojo1 = new MysqlPersonTable();

        daoPojo1.setID(1);
        daoPojo1.setAge(20);
        daoPojo1.setName("updateCrossShard1");

        daoPojos.add(daoPojo1);

        MysqlPersonTable daoPojo2 = new MysqlPersonTable();

        daoPojo2.setID(1);
        daoPojo2.setAge(21);
        daoPojo2.setName("updateCrossShard2");

        daoPojos.add(daoPojo2);

        int[] affected = dao.batchUpdate(new DalHints(), daoPojos);
        assertArrayEquals(new int[]{1, 1}, affected);

        MysqlPersonTable ret = dao.queryByPk(1, new DalHints().inShard(0));
        assertEquals("updateCrossShard1", ret.getName());
        ret = dao.queryByPk(1, new DalHints().inShard(1));
        assertEquals("updateCrossShard2", ret.getName());

        // single Shard
        List<MysqlPersonTable> daoPojos2 = new ArrayList<MysqlPersonTable>(2);
        MysqlPersonTable daoPojo3 = new MysqlPersonTable();

        daoPojo3.setID(1);
        daoPojo3.setName("updateSingleShard1");

        daoPojos2.add(daoPojo3);

        MysqlPersonTable daoPojo4 = new MysqlPersonTable();

        daoPojo4.setID(2);
        daoPojo4.setName("updateSingleShard2");

        daoPojos2.add(daoPojo4);

        affected = dao.batchUpdate(new DalHints().inShard(0), daoPojos2);
        assertArrayEquals(new int[]{1, 1}, affected);

        ret = dao.queryByPk(1, new DalHints().inShard(0));
        assertEquals("updateSingleShard1", ret.getName());
        assertNotNull(ret.getAge());
        ret = dao.queryByPk(2, new DalHints().inShard(0));
        assertEquals("updateSingleShard2", ret.getName());
        assertNotNull(ret.getAge());

        daoPojos2.get(0).setID(20);
        affected = dao.batchUpdate(new DalHints().inShard(1), daoPojos2);
        assertArrayEquals(new int[]{0, 1}, affected);

        // updateNullField
        List<MysqlPersonTable> daoPojos3 = new ArrayList<MysqlPersonTable>(2);
        MysqlPersonTable daoPojo5 = new MysqlPersonTable();

        daoPojo5.setID(1);

        daoPojos3.add(daoPojo5);

        MysqlPersonTable daoPojo6 = new MysqlPersonTable();

        daoPojo6.setID(2);
        daoPojo6.setName("updateNullField");

        daoPojos3.add(daoPojo6);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        affected = dao.batchUpdate(hints.inShard(1).updateNullField(), daoPojos3);
        assertArrayEquals(new int[]{1, 1}, affected);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.updateNullField);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.queryByPk(1, new DalHints().inShard(1));
        assertNull(ret.getName());
        assertNull(ret.getAge());
        ret = dao.queryByPk(2, new DalHints().inShard(1));
        assertEquals("updateNullField", ret.getName());
        assertNull(ret.getAge());
    }

    @Test
    public void testBuildQueryLikeWithMatchPattern() throws Exception {
        List<MysqlPersonTable> ret = dao.testBuildQueryLikeWithMatchPattern("_00", MatchPattern.END_WITH, new DalHints().inShard(0));
        assertEquals(1, ret.size());
        ret = dao.testBuildQueryLikeWithMatchPattern("Init", MatchPattern.BEGIN_WITH, new DalHints().inShard(1));
        assertEquals(3, ret.size());
        ret = dao.testBuildQueryLikeWithMatchPattern("Shard", MatchPattern.CONTAINS, new DalHints().inAllShards());
        assertEquals(6, ret.size());
        ret = dao.testBuildQueryLikeWithMatchPattern("Shard", MatchPattern.USER_DEFINED, new DalHints().inAllShards());
        assertEquals(0, ret.size());
    }

    @Test
    public void testBuildQueryLikeNullableWithMatchPattern() throws Exception {
        List<MysqlPersonTable> ret = dao.testBuildQueryLikeNullableWithMatchPattern("_00", MatchPattern.END_WITH, new DalHints().inShard(0));
        assertEquals(1, ret.size());
        ret = dao.testBuildQueryLikeNullableWithMatchPattern("Init", MatchPattern.BEGIN_WITH, new DalHints().inShard(1));
        assertEquals(3, ret.size());
        ret = dao.testBuildQueryLikeNullableWithMatchPattern("Shard", MatchPattern.CONTAINS, new DalHints().inAllShards());
        assertEquals(6, ret.size());
        ret = dao.testBuildQueryLikeNullableWithMatchPattern("Shard", MatchPattern.USER_DEFINED, new DalHints().inAllShards());
        assertEquals(0, ret.size());
        ret = dao.testBuildQueryLikeNullableWithMatchPattern(null, MatchPattern.CONTAINS, new DalHints().inAllShards());
        assertEquals(6, ret.size());
    }

    @Test
    public void testtest_build_queryEqual() throws Exception {
        Integer Age = 20;
        List<MysqlPersonTable> ret = dao.test(Age, new DalHints().inShard(0));
        assertEquals(1, ret.size());

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test(Age, hints);
        assertEquals(1, ret.size());
        DalHintsChecker.checkEquals(original, hints);
    }

    @Test
    public void testtest_build_delete() throws Exception {
        //crossShard
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        int ret = dao.test_build_delete(Age, hints.inAllShards());
        assertEquals(3, ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        DalHintsChecker.checkEquals(original, hints, exclude);

        List<MysqlPersonTable> pojos = dao.queryAll(new DalHints().inShard(0));
        assertEquals(1, pojos.size());

        pojos = dao.queryAll(new DalHints().inShard(1));
        assertEquals(2, pojos.size());

        //singleShard
        Age.clear();
        Age.add(25);
        ret = dao.test_build_delete(Age, new DalHints().setShardValue(31));
        assertEquals(1, ret);

        pojos = dao.queryAll(new DalHints().inShard(0));
        assertEquals(1, pojos.size());

        pojos = dao.queryAll(new DalHints().inShard(1));
        assertEquals(1, pojos.size());
    }

    @Test
    public void testtest_build_insert() throws Exception {
        //single dao.noshardtest.shardtest
        String Name = "insert";// Test value here
        Integer Age = 20;// Test value here
        int ret = dao.test_build_insert(Name, Age, new DalHints());
        assertEquals(1, ret);

        MysqlPersonTable pojo = dao.queryByPk(4, new DalHints().inShard(0));
        assertEquals("insert", pojo.getName());

        pojo = dao.queryByPk(4, new DalHints().inShard(1));
        assertNull(pojo);

        //cross dao.noshardtest.shardtest
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_build_insert(Name, Age, hints.inAllShards());
        assertEquals(2, ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        DalHintsChecker.checkEquals(original, hints, exclude);

        pojo = dao.queryByPk(5, new DalHints().inShard(0));
        assertEquals("insert", pojo.getName());

        pojo = dao.queryByPk(4, new DalHints().inShard(1));
        assertEquals("insert", pojo.getName());
    }

    @Test
    public void testtest_build_update() throws Exception {
        //corss dao.noshardtest.shardtest
        String Name = "update";
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        int ret = dao.test_build_update(Name, Age, hints.inAllShards());
        assertEquals(3, ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        DalHintsChecker.checkEquals(original, hints, exclude);

        MysqlPersonTable pojo = dao.queryByPk(1, new DalHints().inShard(0));
        assertEquals("update", pojo.getName());

        pojo = dao.queryByPk(2, new DalHints().inShard(0));
        assertEquals("update", pojo.getName());

        pojo = dao.queryByPk(2, new DalHints().inShard(1));
        assertEquals("update", pojo.getName());

        //single dao.noshardtest.shardtest
        Name = "updateSingleShard";
        Age.clear();
        Age.add(25);

        ret = dao.test_build_update(Name, Age, new DalHints().setShardColValue("Age", 25));
        assertEquals(1, ret);

        pojo = dao.queryByPk(3, new DalHints().inShard(1));
        assertEquals("updateSingleShard", pojo.getName());
    }

    @Test
    public void testtest_build_update_null() throws Exception {
        String Name = null;// Test value here
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        int ret = dao.test_build_update(Name, Age, hints.inAllShards());
        assertEquals(3, ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        DalHintsChecker.checkEquals(original, hints, exclude);

        MysqlPersonTable pojo = dao.queryByPk(1, new DalHints().inShard(0));
        assertEquals(null, pojo.getName());

        pojo = dao.queryByPk(2, new DalHints().inShard(0));
        assertEquals(null, pojo.getName());

        pojo = dao.queryByPk(2, new DalHints().inShard(1));
        assertEquals(null, pojo.getName());
    }

    @Test
    public void test_build_multiColumns_update() throws Exception {
        int age = 200;
        String name = "multiColumns_update";

        MysqlPersonTable pojo = dao.queryByPk(1, new DalHints().inShard(0));
        pojo.setAge(age);
        pojo.setName(name);

        Thread.sleep(2000);

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        int ret = dao.test_build_multiColums_update(name, age, pojo.getID(), hints.inShard(0));
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        DalHintsChecker.checkEquals(original, hints, exclude);
        assertEquals(1, ret);
    }

    @Test
    public void testtest_ClientQueryFrom_list() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<MysqlPersonTable> ret = dao.test_ClientQueryFrom_list(Age, hints.inAllShards(), 0, 1);
        assertEquals(1, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertEquals(1, ret.get(0).getID().intValue());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.test_ClientQueryFrom_list(Age, new DalHints().setShardColValue("Age", 20), 0, 1);
        assertEquals(1, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertEquals(1, ret.get(0).getID().intValue());

    }

    @Test
    public void testtest_ClientQueryTop_list() throws Exception {

        Integer Age = 20;

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<MysqlPersonTable> ret = dao.test_ClientQueryTop_list(Age, hints, 1);

        assertEquals(1, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertEquals(1, ret.get(0).getID().intValue());

    }

    @Test
    public void testtest_ClientQueryFromPartialFieldsSet_list() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<MysqlPersonTable> ret = dao.test_ClientQueryFromPartialFieldsSet_list(Age, hints.inAllShards(), 0, 1);
        assertEquals(1, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertNull(ret.get(0).getID());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.test_ClientQueryFromPartialFieldsSet_list(Age, new DalHints().setShardColValue("Age", 20), 0, 1);
        assertEquals(1, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertNull(ret.get(0).getID());

    }

    @Test
    public void testtest_ClientQueryFromPartialFieldsStrings_list() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<MysqlPersonTable> ret = dao.test_ClientQueryFromPartialFieldsStrings_list(Age, hints.inAllShards(), 0, 1);
        assertEquals(1, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertNull(ret.get(0).getID());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.test_ClientQueryFromPartialFieldsStrings_list(Age, new DalHints().setShardColValue("Age", 20), 0, 1);
        assertEquals(1, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertNull(ret.get(0).getID());

    }

    @Test
    public void testtest_build_query_first() throws Exception {
        //cross dao.noshardtest.shardtest
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        MysqlPersonTable ret = dao.test_build_query_first(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_00", ret.getName());

        //single dao.noshardtest.shardtest
        ret = dao.test_build_query_first(Age, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.getName());

        //return null
        Age.clear();
        Age.add(100);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_build_query_first(Age, hints.inAllShards());
        assertNull(ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_build_queryPartial_first() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);

        //cross dao.noshardtest.shardtest
        MysqlPersonTable ret = dao.test_build_queryPartial_first(Age, new DalHints().inAllShards());
        assertNull(ret.getName());
        assertNull(ret.getBirth());
        assertEquals(20, ret.getAge().intValue());
        //single dao.noshardtest.shardtest
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_build_queryPartial_first(Age, hints.setShardColValue("Age", 21));
        assertNull(ret.getName());
        assertNull(ret.getBirth());
        assertEquals(23, ret.getAge().intValue());
        List<DalHintEnum> exclude = new ArrayList<>();
//		exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.resultSorter);
        exclude.add(DalHintEnum.shardColValues);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_build_query_list() throws Exception {

        List<Integer> Age1 = new ArrayList<Integer>();
        Age1.add(20);
        Age1.add(23);
        Age1.add(22);

        //cross dao.noshardtest.shardtest
        List<MysqlPersonTable> ret = dao.test_build_query_list(Age1, new DalHints().inAllShards());
        assertEquals(3, ret.size());

        //single dao.noshardtest.shardtest
        ret = dao.test_build_query_list(Age1, new DalHints().setShardColValue("Age", 20));
        assertEquals(2, ret.size());

        //shardby
        ret = dao.test_build_query_list(Age1, new DalHints().shardBy("Age"));
        assertEquals(3, ret.size());

//		ret = dao.test_build_query_list(Age1, new DalHints());
//		assertEquals(3, ret.size());

        //return null
        Age1.clear();
        Age1.add(200);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_build_query_list(Age1, hints.inAllShards());
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        DalHintsChecker.checkEquals(original, hints, exclude);

    }

    @Test
    public void testtest_build_queryPartial_list() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        List<MysqlPersonTable> ret = dao.test_build_queryPartial_list(Age, new DalHints().inAllShards());
        assertEquals(3, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertNull(ret.get(0).getBirth());

	    /*for(int i=0;i<ret.size();i++){
        System.out.println(ret.get(i).getID());
	    System.out.println(ret.get(i).getName());
	    System.out.println(ret.get(i).getAge());
	    System.out.println(ret.get(i).getBirth());}*/

        ret = dao.test_build_queryPartial_list(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals(2, ret.size());
        assertNull(ret.get(0).getBirth());

        ret = dao.test_build_queryPartial_list(Age, new DalHints().shardBy("Age"));
        assertEquals(3, ret.size());
        assertNull(ret.get(2).getBirth());
        assertEquals(23, ret.get(2).getAge().intValue());

        Age.clear();
        Age.add(200);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_build_queryPartial_list(Age, hints.inAllShards());
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);

    }

    @Test
    public void testtest_build_query_instring() throws Exception {
        List<String> name = new ArrayList<String>();
        name.add("Initial_Shard_00");
        name.add("Initial_Shard_11");
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<MysqlPersonTable> ret = dao.test_def_query_in_string(name, hints.inShard(1));
        assertEquals(1, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_build_query_listByPage() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<MysqlPersonTable> ret = dao.test_build_query_listByPage(Age, 2, 1, hints.inShard(0));
        assertEquals("Initial_Shard_02", ret.get(0).getName());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.test_build_query_listByPage(Age, 1, 1, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.get(0).getName());
    }

    @Test
    public void testtest_build_queryPartial_listByPage() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<MysqlPersonTable> ret = dao.test_build_queryPartial_listByPage(Age, 2, 1, hints.inShard(0));
        assertEquals("Initial_Shard_02", ret.get(0).getName());
        assertNull(ret.get(0).getBirth());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);

        ret = dao.test_build_queryPartial_listByPage(Age, 1, 1, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.get(0).getName());
        assertNull(ret.get(0).getBirth());
    }

    @Test
    public void testtest_build_query_single() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        MysqlPersonTable ret = dao.test_build_query_single(Age, hints.setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.getName());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardColValues);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_build_queryPartial_single() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        MysqlPersonTable ret = dao.test_build_queryPartial_single(Age, hints.setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.getName());
        assertNull(ret.getBirth());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardColValues);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_build_query_field_first() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);

        //single dao.noshardtest.shardtest
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        String ret = dao.test_build_query_field_first(Age, hints.setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardColValues);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
        //cross dao.noshardtest.shardtest
        ret = dao.test_build_query_field_first(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_00", ret);

        //return null
        Age.clear();
        Age.add(30);
        ret = dao.test_build_query_field_first(Age, new DalHints().inAllShards());
        assertNull(ret);
    }

    @Test
    public void testtest_build_query_fieldList() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);

        //cross dao.noshardtest.shardtest
        List<String> ret = dao.test_build_query_fieldList(Age, new DalHints().inAllShards());
        assertEquals(3, ret.size());

        //single dao.noshardtest.shardtest
        ret = dao.test_build_query_fieldList(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals(2, ret.size());

        //dao.noshardtest.shardtest by
        ret = dao.test_build_query_fieldList(Age, new DalHints().shardBy("Age"));
        assertEquals(3, ret.size());

        //return null
        Age.clear();
        Age.add(30);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_build_query_fieldList(Age, hints.inAllShards());
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_build_query_fieldListByPage() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        List<String> ret = dao.test_build_query_fieldListByPage(Age, 2, 1, new DalHints().inShard(0));
        assertEquals("Initial_Shard_02", ret.get(0));

        ret = dao.test_build_query_fieldListByPage(Age, 1, 1, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.get(0));

        //return null
        Age.clear();
        Age.add(30);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_build_query_fieldListByPage(Age, 1, 1, new DalHints().shardBy("Age"));
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardBy);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }


    @Test
    public void testtest_build_query_field_single() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        String ret = dao.test_build_query_field_single(Age, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret);

        //return null
        Age.clear();
        Age.add(30);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_build_query_field_single(Age, hints.shardBy("Age"));
        assertNull(ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardBy);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_update_in() throws Exception {

        String Name = "def_update";// Test value here
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);

        // single dao.noshardtest.shardtest

        int ret = dao.test_def_update_in(Name, Age, new DalHints().inShard(0));
        assertEquals(2, ret);


        MysqlPersonTable pojo = dao.queryByPk(1,
                new DalHints().inShard(0));
        assertEquals("def_update", pojo.getName());

        pojo = dao.queryByPk(2, new DalHints().inShard(0));
        assertEquals("def_update", pojo.getName());

        pojo = dao.queryByPk(2, new DalHints().inShard(1));
        assertEquals("Initial_Shard_13", pojo.getName());

        // cross dao.noshardtest.shardtest
        Name = "def_update_crossShard";
        ret = dao.test_def_update_in(Name, Age, new DalHints().inAllShards());
        assertEquals(3, ret);
        pojo = dao.queryByPk(1, new DalHints().inShard(0));
        assertEquals("def_update_crossShard", pojo.getName());

        pojo = dao.queryByPk(2, new DalHints().inShard(0));
        assertEquals("def_update_crossShard", pojo.getName());

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        pojo = dao.queryByPk(2, hints.inShard(1));
        assertEquals("def_update_crossShard", pojo.getName());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_update_null() throws Exception {

        String Name = null;// Test value here
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
//		int ret = dao.test_def_update_in(Name, Age, new DalHints().inAllShards());
        int ret = dao.test_def_update_in(Name, Age, hints.inShard(0));
        assertEquals(2, ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shard);
        DalHintsChecker.checkEquals(original, hints, exclude);

        MysqlPersonTable pojo = dao.queryByPk(1, new DalHints().inShard(0));
        assertEquals(null, pojo.getName());

        pojo = dao.queryByPk(2, new DalHints().inShard(0));
        assertEquals(null, pojo.getName());

        pojo = dao.queryByPk(2, new DalHints().inShard(1));
        assertEquals("Initial_Shard_13", pojo.getName());
    }


    @Test
    public void testtest_def_truncate() throws Exception {
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        dao.test_def_truncate(hints.inAllShards());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        DalHintsChecker.checkEquals(original, hints, exclude);

        int count = dao.count(new DalHints().inAllShards());
        assertEquals(0, count);
    }

    @Test
    public void testtest_def_query_first() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(21);
        Age.add(22);
        //single dao.noshardtest.shardtest
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        MysqlPersonTable ret = dao.test_def_query_first(Age, hints.setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret.getName());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardColValues);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
        //cross dao.noshardtest.shardtest
        ret = dao.test_def_query_first(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_00", ret.getName());

        //return null
        Age.clear();
        Age.add(30);
        Age.add(31);

        ret = dao.test_def_query_first(Age, new DalHints().inShard(1));
        assertNull(ret);
    }

//	@Test
//	public void testtest_def_query_firstBySql() throws Exception {
//		List<Integer> Age=new ArrayList<Integer>();
//		Age.add(20);
//		Age.add(21);
//		Age.add(22);
//
//		ignoreMissingFieldsAndAllowPartialTestOnMysql ret = dao.test_def_query_firstBySql(Age, new DalHints().setShardColValue("Age", 20));
//	    assertEquals("Initial_Shard_00", ret.getName());
//
//	    ret = dao.test_def_query_firstBySql(Age, new DalHints().inAllShards());
//	    assertEquals("Initial_Shard_11", ret.getName());
//
//	    Age.clear();
//	    Age.add(30);
//	    Age.add(31);
//
//	    ret = dao.test_def_query_firstBySql(Age, new DalHints().inShard(1));
//	    assertNull(ret);
//	}

    @Test
    public void testtest_def_queryPartialSet_first() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(21);
        Age.add(22);

        MysqlPersonTable ret = dao.test_def_queryPartialSet_first(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret.getName());
        assertEquals(20, ret.getAge().intValue());
        assertNull(ret.getID());
        assertNull(ret.getBirth());

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_queryPartialSet_first(Age, hints.inAllShards());
        assertEquals("Initial_Shard_00", ret.getName());
        assertEquals(20, ret.getAge().intValue());
        assertNull(ret.getID());
        assertNull(ret.getBirth());

        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);

        Age.clear();
        Age.add(30);
        Age.add(31);

        ret = dao.test_def_queryPartialSet_first(Age, new DalHints().inShard(1));
        assertNull(ret);
    }

    @Test
    public void testtest_def_queryPartialStrings_first() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(21);
        Age.add(22);

        MysqlPersonTable ret = dao.test_def_queryPartialStrings_first(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret.getName());
        assertEquals(20, ret.getAge().intValue());
        assertNull(ret.getID());
        assertNull(ret.getBirth());

        ret = dao.test_def_queryPartialStrings_first(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_00", ret.getName());
        assertEquals(20, ret.getAge().intValue());
        assertNull(ret.getID());
        assertNull(ret.getBirth());

        Age.clear();
        Age.add(30);
        Age.add(31);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_queryPartialStrings_first(Age, hints.inShard(1));
        assertNull(ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_query_list() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        List<MysqlPersonTable> ret = dao.test_def_query_list(Age, new DalHints().inShard(1));
        assertEquals(1, ret.size());

        ret = dao.test_def_query_list(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals(2, ret.size());

        ret = dao.test_def_query_list(Age, new DalHints().shardBy("Age"));
        assertEquals(3, ret.size());

        ret = dao.test_def_query_list(Age, new DalHints().inAllShards());
        assertEquals(3, ret.size());

        Age.clear();
        Age.add(300);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_query_list(Age, hints.inShard(0));
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
//		exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_queryPartialSet_list() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        // List<ignoreMissingFieldsAndAllowPartialTestOnMysql> ret =
        // dao.test_def_query_list(Age, new DalHints().inAllShards());
        List<MysqlPersonTable> ret = dao.test_def_queryPartialSet_list(Age, new DalHints().inShard(1));
        assertEquals(1, ret.size());
        assertEquals(23, ret.get(0).getAge().intValue());
        assertEquals("Initial_Shard_13", ret.get(0).getName());
        assertNull(ret.get(0).getBirth());
        assertNull(ret.get(0).getID());

        ret = dao.test_def_queryPartialSet_list(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals(2, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertEquals("Initial_Shard_00", ret.get(0).getName());
        assertNull(ret.get(0).getBirth());
        assertNull(ret.get(0).getID());

        assertEquals(22, ret.get(1).getAge().intValue());
        assertEquals("Initial_Shard_02", ret.get(1).getName());
        assertNull(ret.get(1).getBirth());
        assertNull(ret.get(1).getID());

        ret = dao.test_def_queryPartialSet_list(Age, new DalHints().shardBy("Age"));

        assertEquals(3, ret.size());
        assertEquals(23, ret.get(2).getAge().intValue());
        assertEquals("Initial_Shard_13", ret.get(2).getName());
        assertNull(ret.get(2).getBirth());
        assertNull(ret.get(2).getID());

        assertEquals(20, ret.get(0).getAge().intValue());
        assertEquals("Initial_Shard_00", ret.get(0).getName());
        assertNull(ret.get(0).getBirth());
        assertNull(ret.get(0).getID());

        assertEquals(22, ret.get(1).getAge().intValue());
        assertEquals("Initial_Shard_02", ret.get(1).getName());
        assertNull(ret.get(1).getBirth());
        assertNull(ret.get(1).getID());

        Age.clear();
        Age.add(300);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_queryPartialSet_list(Age, hints.inShard(0));
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_queryPartialStrings_list() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);

        List<MysqlPersonTable> ret = dao.test_def_queryPartialStrings_list(Age, new DalHints().inShard(1));
        assertEquals(1, ret.size());
        assertEquals(23, ret.get(0).getAge().intValue());
        assertEquals("Initial_Shard_13", ret.get(0).getName());
        assertNull(ret.get(0).getBirth());
        assertNull(ret.get(0).getID());

        ret = dao.test_def_queryPartialStrings_list(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals(2, ret.size());
        assertEquals(20, ret.get(0).getAge().intValue());
        assertEquals("Initial_Shard_00", ret.get(0).getName());
        assertNull(ret.get(0).getBirth());
        assertNull(ret.get(0).getID());

        assertEquals(22, ret.get(1).getAge().intValue());
        assertEquals("Initial_Shard_02", ret.get(1).getName());
        assertNull(ret.get(1).getBirth());
        assertNull(ret.get(1).getID());

        ret = dao.test_def_queryPartialStrings_list(Age, new DalHints().shardBy("Age"));
        assertEquals(3, ret.size());
        assertEquals(23, ret.get(2).getAge().intValue());
        assertEquals("Initial_Shard_13", ret.get(2).getName());
        assertNull(ret.get(2).getBirth());
        assertNull(ret.get(2).getID());

        assertEquals(20, ret.get(0).getAge().intValue());
        assertEquals("Initial_Shard_00", ret.get(0).getName());
        assertNull(ret.get(0).getBirth());
        assertNull(ret.get(0).getID());

        assertEquals(22, ret.get(1).getAge().intValue());
        assertEquals("Initial_Shard_02", ret.get(1).getName());
        assertNull(ret.get(1).getBirth());
        assertNull(ret.get(1).getID());

        Age.clear();
        Age.add(300);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_queryPartialStrings_list(Age, hints.inShard(0));
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_query_partlist() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List<Map<String, Object>> ret = dao.test_def_query_partlist(Age, hints.inShard(1));
        assertEquals(1, ret.size());
        assertEquals("Initial_Shard_13", ret.get(0).get("myname"));
        List<DalHintEnum> exclude = new ArrayList<>();
//		exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shard);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);

    }

    @Test
    public void testtest_def_query_listByPage() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        List<MysqlPersonTable> ret = dao.test_def_query_listByPage(Age, 2, 1, new DalHints().inShard(0));
        assertEquals("Initial_Shard_02", ret.get(0).getName());

        ret = dao.test_def_query_listByPage(Age, 1, 1, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.get(0).getName());

        Age.clear();
        Age.add(300);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_query_listByPage(Age, 1, 1, new DalHints().inShard(0));
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
//		exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shard);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_queryPartialSet_listByPage() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        List<MysqlPersonTable> ret = dao.test_def_queryPartialSet_listByPage(Age, 2, 1, new DalHints().inShard(0));
        assertEquals("Initial_Shard_02", ret.get(0).getName());
        assertEquals(22, ret.get(0).getAge().intValue());
        assertNull(ret.get(0).getID());
        assertNull(ret.get(0).getBirth());

        ret = dao.test_def_queryPartialSet_listByPage(Age, 1, 1, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.get(0).getName());
        assertEquals(23, ret.get(0).getAge().intValue());
        assertNull(ret.get(0).getID());
        assertNull(ret.get(0).getBirth());

        Age.clear();
        Age.add(300);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_queryPartialSet_listByPage(Age, 1, 1, hints.inShard(0));
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shard);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_queryPartialStrings_listByPage() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        List<MysqlPersonTable> ret = dao.test_def_queryPartialStrings_listByPage(Age, 2, 1, new DalHints().inShard(0));
        assertEquals("Initial_Shard_02", ret.get(0).getName());
        assertEquals(22, ret.get(0).getAge().intValue());
        assertNull(ret.get(0).getID());
        assertNull(ret.get(0).getBirth());

        ret = dao.test_def_queryPartialStrings_listByPage(Age, 1, 1, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.get(0).getName());
        assertEquals(23, ret.get(0).getAge().intValue());
        assertNull(ret.get(0).getID());
        assertNull(ret.get(0).getBirth());

        Age.clear();
        Age.add(300);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_queryPartialStrings_listByPage(Age, 1, 1, hints.inShard(0));
        assertEquals(0, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shard);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_query_single() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        MysqlPersonTable ret = dao.test_def_query_single(Age, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.getName());

        Age.clear();
        Age.add(30);
        Age.add(31);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_query_single(Age, hints.setShardColValue("Age", 21));
        assertNull(ret);
        List<DalHintEnum> exclude = new ArrayList<>();
//		exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shardColValues);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_queryPartialSet_single() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        MysqlPersonTable ret = dao.test_def_queryPartialSet_single(Age, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.getName());
        assertEquals(23, ret.getAge().intValue());
        assertNull(ret.getBirth());
        assertNull(ret.getID());

        Age.clear();
        Age.add(11);
        Age.add(12);
        Age.add(23);
        ret = dao.test_def_queryPartialSet_single(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_13", ret.getName());
        assertEquals(23, ret.getAge().intValue());
        assertNull(ret.getBirth());
        assertNull(ret.getID());

        Age.clear();
        Age.add(30);
        Age.add(31);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_queryPartialSet_single(Age, hints.setShardColValue("Age", 21));
        assertNull(ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shardColValues);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_queryPartialStrings_single() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        MysqlPersonTable ret = dao.test_def_queryPartialStrings_single(Age, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.getName());
        assertEquals(23, ret.getAge().intValue());
        assertNull(ret.getBirth());
        assertNull(ret.getID());

        Age.clear();
        Age.add(11);
        Age.add(12);
        Age.add(23);
        ret = dao.test_def_queryPartialStrings_single(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_13", ret.getName());
        assertEquals(23, ret.getAge().intValue());
        assertNull(ret.getBirth());
        assertNull(ret.getID());

        Age.clear();
        Age.add(30);
        Age.add(31);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_queryPartialStrings_single(Age, hints.setShardColValue("Age", 21));
        assertNull(ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shardColValues);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_query_field_first() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);

        String ret = dao.test_def_query_field_first(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals("Initial_Shard_00", ret);

        ret = dao.test_def_query_field_first(Age, new DalHints().inAllShards());
        assertEquals("Initial_Shard_00", ret);

        Age.clear();
        Age.add(30);
        Age.add(31);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_query_field_first(Age, hints.setShardColValue("Age", 20));
        assertNull(ret);
        List<DalHintEnum> exclude = new ArrayList<>();
//		exclude.add(DalHintEnum.partialQuery);
        exclude.add(DalHintEnum.shardColValues);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_query_fieldList() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);

        List<String> ret = dao.test_def_query_fieldList(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals(2, ret.size());

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_query_fieldList(Age, hints.shardBy("Age"));
        assertEquals(3, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.resultSorter);
        exclude.add(DalHintEnum.shardBy);
        DalHintsChecker.checkEquals(original, hints, exclude);


        ret = dao.test_def_query_fieldList(Age, new DalHints().inAllShards().sequentialExecute());
        assertEquals(3, ret.size());

        Age.clear();
        Age.add(30);
        Age.add(31);
        ret = dao.test_def_query_fieldList(Age, new DalHints().setShardColValue("Age", 20));
        assertEquals(0, ret.size());

        ret = dao.test_def_query_fieldList(Age, new DalHints().shardBy("Age"));
        assertEquals(0, ret.size());
    }

    @Test
    public void testtest_def_query_fieldListByPage() throws Exception {
        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        List<String> ret = dao.test_def_query_fieldListByPage(Age, 2, 1, new DalHints().inShard(0));
        assertEquals("Initial_Shard_02", ret.get(0));

        ret = dao.test_def_query_fieldListByPage(Age, 1, 1, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret.get(0));

        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_query_fieldListByPage(Age, 1, 1, hints.shardBy("Age"));
        assertEquals("Initial_Shard_00", ret.get(0));
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardBy);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);

        Age.clear();
        Age.add(30);
        Age.add(31);
        ret = dao.test_def_query_fieldListByPage(Age, 2, 1, new DalHints().inShard(0));
        assertEquals(0, ret.size());
    }

    @Test
    public void testtest_def_query_field_single() throws Exception {

        List<Integer> Age = new ArrayList<Integer>();
        Age.add(20);
        Age.add(23);
        Age.add(22);
        String ret = dao.test_def_query_field_single(Age, new DalHints().setShardColValue("Age", 21));
        assertEquals("Initial_Shard_13", ret);

        Age.clear();
        Age.add(30);
        Age.add(31);
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        ret = dao.test_def_query_field_single(Age, hints.setShardColValue("Age", 21));
        assertNull(ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.shardColValues);
//		exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    @Test
    public void testtest_def_count() throws Exception {
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        long ret = dao.test_def_count(hints.inAllShards().mergeBy(new ResultMerger.LongSummary()));
        assertEquals(6l, ret);

        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.resultMerger);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }

    private class IntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return new Integer(o2.compareTo(o1));
        }
    }

    @Test
    public void testtest_def_min() throws Exception {
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        int ret = dao.test_def_min(hints.inAllShards().sortBy(new IntegerComparator()));
        assertEquals(21, ret);
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        exclude.add(DalHintEnum.resultSorter);
        DalHintsChecker.checkEquals(original, hints, exclude);
    }


    @Test
    public void test_queryMultipleAllShards() throws SQLException {
        DalHints hints = new DalHints();
        DalHints original = hints.clone();
        List ret = dao.queryListMultipleAllShards(hints.inAllShards());
//	   assertEquals(16, ret.size());
        List<DalHintEnum> exclude = new ArrayList<>();
        exclude.add(DalHintEnum.allShards);
        DalHintsChecker.checkEquals(original, hints, exclude);

        List<MysqlPersonTable> ret1 = (List<MysqlPersonTable>) ret.get(0);
        assertEquals(6, ret1.size());
        for (int i = 0; i < ret1.size(); i++)
            System.out.println("sqlList0:" + ret1.get(i).getName());

        ret1 = (List<MysqlPersonTable>) ret.get(1);
        assertEquals(6, ret1.size());
        for (int i = 0; i < ret1.size(); i++)
            System.out.println("sqlList1:" + ret1.get(i).getName());

        ret1 = (List<MysqlPersonTable>) ret.get(2);
        assertEquals(6, ret1.size());
        for (int i = 0; i < ret1.size(); i++)
            System.out.println("sqlList2:" + ret1.get(i).getName());

        ret1 = (List<MysqlPersonTable>) ret.get(3);
        assertEquals(6, ret1.size());
        for (int i = 0; i < ret1.size(); i++)
            System.out.println("sqlList3:" + ret1.get(i).getName());

        MysqlPersonTable retx = (MysqlPersonTable) ret.get(4);
        assertNotNull(retx);
        System.out.println("sqlList4:" + retx.getName());

        ret1 = (List<MysqlPersonTable>) ret.get(5);
        assertEquals(0, ret1.size());
        for (int i = 0; i < ret1.size(); i++)
            System.out.println("sqlList5:" + ret1.get(i).getName());

        ret1 = (List<MysqlPersonTable>) ret.get(6);
        assertEquals(6, ret1.size());
        for (int i = 0; i < ret1.size(); i++)
            System.out.println("sqlList6:" + ret1.get(i).getName());

        ret1 = (List<MysqlPersonTable>) ret.get(7);
        assertEquals(6, ret1.size());
        for (int i = 0; i < ret1.size(); i++)
            System.out.println("sqlList7:" + ret1.get(i).getName());

        List<Integer> count1 = (List<Integer>) ret.get(8);
        assertEquals(2, count1.size());
        for (int i = 0; i < count1.size(); i++)
            System.out.println("sqlCount8:" + count1.get(i).intValue());

        List<Integer> count2 = (List<Integer>) ret.get(9);
        assertEquals(2, count2.size());
        for (int i = 0; i < count2.size(); i++)
            System.out.println("sqlCount9:" + count2.get(i).intValue());

        List<Integer> count3 = (List<Integer>) ret.get(10);
        assertEquals(2, count3.size());
        for (int i = 0; i < count3.size(); i++)
            System.out.println("sqlCount10:" + count3.get(i).intValue());

        List<Integer> count4 = (List<Integer>) ret.get(11);
        assertEquals(2, count4.size());
        for (int i = 0; i < count4.size(); i++)
            System.out.println("sqlCount11:" + count4.get(i).intValue());

        List<Integer> count5 = (List<Integer>) ret.get(12);
        assertEquals(2, count5.size());
        for (int i = 0; i < count5.size(); i++)
            System.out.println("sqlCount12:" + count5.get(i).intValue());

        List<Integer> count6 = (List<Integer>) ret.get(13);
        assertEquals(0, count6.size());

        List<Integer> count7 = (List<Integer>) ret.get(14);
        assertEquals(2, count7.size());
        for (int i = 0; i < count7.size(); i++)
            System.out.println("sqlCount14:" + count7.get(i).intValue());

        List<String> field1 = (List<String>) ret.get(15);
        assertEquals(6, field1.size());
        for (int i = 0; i < field1.size(); i++)
            System.out.println("sqlFieldList15:" + field1.get(i).toString());

        List<String> field2 = (List<String>) ret.get(16);
        assertEquals(6, field2.size());
        for (int i = 0; i < field2.size(); i++)
            System.out.println("sqlFieldList16:" + field2.get(i).toString());

        List<String> field3 = (List<String>) ret.get(17);
        assertEquals(6, field3.size());
        for (int i = 0; i < field3.size(); i++)
            System.out.println("sqlFieldList17:" + field3.get(i).toString());

        List<String> field4 = (List<String>) ret.get(18);
        assertEquals(6, field4.size());
        for (int i = 0; i < field4.size(); i++)
            System.out.println("sqlFieldList18:" + field4.get(i).toString());

        String field5 = (String) ret.get(19);
        assertNotNull(field5);
        System.out.println("sqlFieldList19:" + field5.toString());

        List<String> field6 = (List<String>) ret.get(20);
        assertEquals(0, field6.size());

        List<String> field7 = (List<String>) ret.get(21);
        assertEquals(6, field7.size());
        for (int i = 0; i < field7.size(); i++)
            System.out.println("sqlFieldList21:" + field7.get(i).toString());

        List<String> field8 = (List<String>) ret.get(22);
        assertEquals(6, field8.size());
        for (int i = 0; i < field8.size(); i++)
            System.out.println("sqlFieldList22:" + field8.get(i).toString());

        List<MysqlPersonTable> first1 = (List<MysqlPersonTable>) ret.get(23);
        assertEquals(2, first1.size());
        for (int i = 0; i < first1.size(); i++)
            System.out.println("sqlFirst23:" + first1.get(i).getName());

        List<MysqlPersonTable> first2 = (List<MysqlPersonTable>) ret.get(24);
        assertEquals(2, first2.size());
        for (int i = 0; i < first2.size(); i++)
            System.out.println("sqlFirst24:" + first2.get(i).getName());

        List<MysqlPersonTable> first3 = (List<MysqlPersonTable>) ret.get(25);
        assertEquals(2, first3.size());
        for (int i = 0; i < first3.size(); i++)
            System.out.println("sqlFirst25:" + first3.get(i).getName());

        List<MysqlPersonTable> first4 = (List<MysqlPersonTable>) ret.get(26);
        assertEquals(2, first4.size());
        for (int i = 0; i < first4.size(); i++)
            System.out.println("sqlFirst26:" + first4.get(i).getName());

        MysqlPersonTable first5 = (MysqlPersonTable) ret.get(27);
//	   assertEquals("Initial_Shard_11", first5.getName());
        assertNotNull(first5);
        System.out.println("sqlFirst27:" + first5.getName());

        List<MysqlPersonTable> first6 = (List<MysqlPersonTable>) ret.get(28);
        assertEquals(0, first6.size());

        List<MysqlPersonTable> first7 = (List<MysqlPersonTable>) ret.get(29);
        assertEquals(2, first7.size());
        for (int i = 0; i < first7.size(); i++)
            System.out.println("sqlFirst29:" + first7.get(i).getName());

        List<MysqlPersonTable> first8 = (List<MysqlPersonTable>) ret.get(30);
        assertEquals(2, first8.size());
        for (int i = 0; i < first8.size(); i++)
            System.out.println("sqlFirst30:" + first8.get(i).getName());

        List<MysqlPersonTable> object1 = (List<MysqlPersonTable>) ret.get(31);
        assertEquals(1, object1.size());
        for (int i = 0; i < object1.size(); i++)
            System.out.println("sqlObject31:" + object1.get(i).getName());

        List<MysqlPersonTable> object2 = (List<MysqlPersonTable>) ret.get(32);
        assertEquals(1, object2.size());
        for (int i = 0; i < object2.size(); i++)
            System.out.println("sqlObject32:" + object2.get(i).getName());

        List<MysqlPersonTable> object3 = (List<MysqlPersonTable>) ret.get(33);
        assertEquals(1, object3.size());
        for (int i = 0; i < object3.size(); i++)
            System.out.println("sqlObject33:" + object3.get(i).getName());

        List<MysqlPersonTable> object4 = (List<MysqlPersonTable>) ret.get(34);
        assertEquals(1, object4.size());
        for (int i = 0; i < object4.size(); i++)
            System.out.println("sqlObject34:" + object4.get(i).getName());

        MysqlPersonTable object5 = (MysqlPersonTable) ret.get(35);
//	   assertEquals("Initial_Shard_11", object5.getName());
        assertNotNull(object5);
        System.out.println("sqlObject35:" + object5.getName());

        List<MysqlPersonTable> object6 = (List<MysqlPersonTable>) ret.get(36);
        assertEquals(0, object6.size());

        List<MysqlPersonTable> object7 = (List<MysqlPersonTable>) ret.get(37);
        assertEquals(1, object7.size());
        for (int i = 0; i < object7.size(); i++)
            System.out.println("sqlObject37:" + object7.get(i).getName());

        List<MysqlPersonTable> object8 = (List<MysqlPersonTable>) ret.get(38);
        assertEquals(1, object8.size());
        for (int i = 0; i < object8.size(); i++)
            System.out.println("sqlObject38:" + object8.get(i).getName());

        List<MysqlPersonTable> inRet1 = (List<MysqlPersonTable>) ret.get(39);
        assertEquals(3, inRet1.size());
        for (int i = 0; i < inRet1.size(); i++)
            System.out.println("sqlInParam39:" + inRet1.get(i).getName());

        List<MysqlPersonTable> inRet2 = (List<MysqlPersonTable>) ret.get(40);
        assertEquals(3, inRet2.size());
        for (int i = 0; i < inRet2.size(); i++)
            System.out.println("sqlInParam40:" + inRet2.get(i).getName());

        List<MysqlPersonTable> inRet3 = (List<MysqlPersonTable>) ret.get(41);
        assertEquals(3, inRet3.size());
        for (int i = 0; i < inRet3.size(); i++)
            System.out.println("sqlInParam41:" + inRet3.get(i).getName());

        List<MysqlPersonTable> inRet4 = (List<MysqlPersonTable>) ret.get(42);
        assertEquals(3, inRet4.size());
        for (int i = 0; i < inRet4.size(); i++)
            System.out.println("sqlInParam42:" + inRet4.get(i).getName());

        MysqlPersonTable inRet5 = (MysqlPersonTable) ret.get(43);
//	   assertEquals("Initial_Shard_11", inRet5.getName());
        assertNotNull(inRet5);
        System.out.println("sqlInParam43:" + inRet5.getName());

        List<MysqlPersonTable> inRet6 = (List<MysqlPersonTable>) ret.get(44);
        assertEquals(0, inRet6.size());

        List<MysqlPersonTable> inRet7 = (List<MysqlPersonTable>) ret.get(45);
        assertEquals(3, inRet7.size());
        for (int i = 0; i < inRet7.size(); i++)
            System.out.println("sqlInParam45:" + inRet7.get(i).getName());

        List<MysqlPersonTable> inRet8 = (List<MysqlPersonTable>) ret.get(46);
        assertEquals(3, inRet8.size());
        for (int i = 0; i < inRet8.size(); i++)
            System.out.println("sqlInParam46:" + inRet8.get(i).getName());

        List<MysqlPersonTable> noRet1 = (List<MysqlPersonTable>) ret.get(47);
        assertEquals(0, noRet1.size());

        List<MysqlPersonTable> noRet2 = (List<MysqlPersonTable>) ret.get(48);
        assertEquals(0, noRet2.size());

        List<MysqlPersonTable> noRet3 = (List<MysqlPersonTable>) ret.get(49);
        assertEquals(0, noRet3.size());

        List<MysqlPersonTable> noRet4 = (List<MysqlPersonTable>) ret.get(50);
        assertEquals(0, noRet4.size());

        MysqlPersonTable noRet5 = (MysqlPersonTable) ret.get(51);
        assertNull(noRet5);

        List<MysqlPersonTable> noRet6 = (List<MysqlPersonTable>) ret.get(52);
        assertEquals(0, noRet6.size());

        List<MysqlPersonTable> noRet7 = (List<MysqlPersonTable>) ret.get(53);
        assertEquals(0, noRet7.size());

        List<MysqlPersonTable> noRet8 = (List<MysqlPersonTable>) ret.get(54);
        assertEquals(0, noRet8.size());

        List<Long> count8 = (List<Long>) ret.get(55);
        assertEquals(2, count8.size());
        for (int i = 0; i < count8.size(); i++)
            System.out.println("sqlCount55:" + count8.get(i).longValue());

    }


    @Test
    public void testDalColumnMapRowMapper() throws Exception {
        List<Map<String, Object>> ret = dao.testDalColumnMapRowMapper(new DalHints().inShard(0));
        assertEquals(20, ret.get(0).get("Age"));
        assertEquals("Initial_Shard_00", ret.get(0).get("Name"));
    }

    @Test
    public void testDalColumnMapRowMapperWithAlias() throws Exception {
        List<Map<String, Object>> ret = dao.testDalColumnMapRowMapperWithAlias(new DalHints().inShard(0));
        assertEquals(20, ret.get(0).get("age"));
        assertEquals("Initial_Shard_00", ret.get(0).get("name"));
    }
    //	@Test
//	public void test_queryInMultipleAllShards() throws SQLException {
//		List<Integer> Age=new ArrayList<Integer>(3);
//		Age.add(20);
//		Age.add(21);
//		Age.add(23);
//
//	    List ret=dao.queryInListMultipleAllShards(new DalHints().inAllShards(),Age);
//
//		List<ignoreMissingFieldsAndAllowPartialTestOnMysql> inRet1=(List<ignoreMissingFieldsAndAllowPartialTestOnMysql>) ret.get(0);
//		   assertEquals(3, inRet1.size());
//		   for(int i=0;i<inRet1.size();i++)
//			   System.out.println("sqlInRet1:"+inRet1.get(i).getName());
//
//	}
//
//	@Test
//	public void test_queryNoRetMultipleAllShards() throws SQLException {
//		List ret=dao.queryNoRetMultipleAllShards(new DalHints().inAllShards());
//
//		List<ignoreMissingFieldsAndAllowPartialTestOnMysql> inRet1=(List<ignoreMissingFieldsAndAllowPartialTestOnMysql>) ret.get(0);
//		   assertEquals(0, inRet1.size());
////		   for(int i=0;i<inRet1.size();i++)
////			   System.out.println("sqlInRet1:"+inRet1.get(i).getName());
//	}
    @Test
    public void test_queryCountMultipleAllShards() throws SQLException {
        List ret = dao.queryCountMultipleAllShards(new DalHints().inAllShards());
        List<Integer> count1 = (List<Integer>) ret.get(0);
        assertEquals(2, count1.size());
        for (int i = 0; i < count1.size(); i++)
            System.out.println("sqlCount1:" + count1.get(i).intValue());

        List<Integer> count2 = (List<Integer>) ret.get(1);
        assertEquals(2, count2.size());
        for (int i = 0; i < count2.size(); i++)
            System.out.println("sqlCount2:" + count2.get(i).intValue());

        List<Long> count3 = (List<Long>) ret.get(2);
        assertEquals(2, count3.size());
        for (int i = 0; i < count3.size(); i++)
            System.out.println("sqlCount3:" + count3.get(i).longValue());

        List<Long> count4 = (List<Long>) ret.get(3);
        assertEquals(2, count4.size());
        for (int i = 0; i < count4.size(); i++)
            System.out.println("sqlCount4:" + count4.get(i).longValue());

        List<Integer> count5 = (List<Integer>) ret.get(4);
        assertEquals(2, count5.size());
        for (int i = 0; i < count5.size(); i++)
            System.out.println("sqlCount5:" + count5.get(i).intValue());

        List<Integer> count6 = (List<Integer>) ret.get(5);
        assertEquals(0, count6.size());

        List<Long> count7 = (List<Long>) ret.get(6);
        assertEquals(2, count7.size());
        for (int i = 0; i < count7.size(); i++)
            System.out.println("sqlCount7:" + count7.get(i).longValue());

        List<Integer> count8 = (List<Integer>) ret.get(7);
        assertEquals(2, count8.size());
        for (int i = 0; i < count8.size(); i++)
            System.out.println("sqlCount8:" + count8.get(i).intValue());
//
//		   List<Integer> count9=(List<Integer>) ret.get(8);
//		   assertEquals(2, count9.size());
//		   for(int i=0;i<count9.size();i++)
//			   System.out.println("sqlCount9:"+count9.get(i).intValue());
    }

    @Test
    public void test_queryTime() throws SQLException {
        List ret = dao.queryTime(new DalHints().inShard(0));
        List<Timestamp> ret1 = (List<Timestamp>) ret.get(0);
        assertEquals(1, ret1.size());
        for (int i = 0; i < ret1.size(); i++)
            System.out.println("timestamp:" + ret1.get(i).toString());

        List<Timestamp> ret2 = (List<Timestamp>) ret.get(1);
        assertEquals(1, ret2.size());
        for (int i = 0; i < ret2.size(); i++)
            System.out.println("timestamp:" + ret2.get(i).toString());
    }

    @Test
    public void testTransPass() throws Exception {

        DalCommand command = new DalCommand() {

            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints());
                ret.setAge(1000);
                dao.update(new DalHints(), ret);
                return true;
            }
        };
        client.execute(command, new DalHints().inShard(0));
        assertEquals(1000, dao.queryByPk(1l, new DalHints().inShard(0)).getAge().intValue());
    }

    @Test
    public void testTransFail() throws Exception {
        DalCommand command = new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable pojo = new MysqlPersonTable();
                pojo.setID(2);
                dao.delete(new DalHints().inShard(0), pojo);
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints().inShard(0));
                ret.setAge(2000);
                ret.setID(3); //插入已存在的主鍵3，造成主鍵衝突
                dao.insert(new DalHints().inShard(0).enableIdentityInsert(), ret);
                return true;
            }
        };
        try {
            client.execute(command, new DalHints().inShard(0));
        } catch (Exception e) {
        }

        assertEquals(22, dao.queryByPk(2, new DalHints().inShard(0)).getAge().intValue());
        assertEquals(3, dao.count(new DalHints().inShard(0)));
    }

    @Test
    public void testTransCommandsFail() throws SQLException {
        List<DalCommand> cmds = new LinkedList<DalCommand>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints().inShard(0));
                ret.setAge(1000);
                dao.update(new DalHints().inShard(0), ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable pojo = new MysqlPersonTable();
                pojo.setID(2);
                dao.delete(new DalHints().inShard(0), pojo);
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints().inShard(0));
                ret.setAge(2000);
                ret.setID(3);
                dao.insert(new DalHints().inShard(0).enableIdentityInsert(), ret);//主键冲突
                return true;
            }
        });

        try {
            client.execute(cmds, new DalHints().inShard(0));
        } catch (Exception e) {
//			e.printStackTrace();
        }

        assertEquals(20, dao.queryByPk(1, new DalHints().inShard(0)).getAge().intValue());
        assertEquals(22, dao.queryByPk(2, new DalHints().inShard(0)).getAge().intValue());
        assertEquals(3, dao.count(new DalHints().inShard(0)));

    }

    @Test
    public void testTransCommandsPass() throws SQLException {
        List<DalCommand> cmds = new LinkedList<DalCommand>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints().inShard(0));
                ret.setAge(1000);
                dao.update(new DalHints().inShard(0), ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable pojo = new MysqlPersonTable();
                pojo.setID(2);
                dao.delete(new DalHints().inShard(0), pojo);
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints().inShard(0));
                ret.setAge(2000);
                ret.setID(4);
                dao.insert(new DalHints().inShard(0).enableIdentityInsert(), ret);
                return true;
            }
        });


        client.execute(cmds, new DalHints().inShard(0));

        assertEquals(1000, dao.queryByPk(1, new DalHints().inShard(0)).getAge().intValue());
        assertNull(dao.queryByPk(2, new DalHints().inShard(0)));
        assertEquals(2000, dao.queryByPk(4, new DalHints().inShard(0)).getAge().intValue());
        assertEquals(3, dao.count(new DalHints().inShard(0)));

    }

    @Test
    public void testTransCommandsPassDepand() throws SQLException {
        final KeyHolder key = new KeyHolder();
        List<DalCommand> cmds = new LinkedList<DalCommand>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints().inShard(0));

                dao.insert(new DalHints().inShard(0), key, ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable pojo = new MysqlPersonTable();
                pojo.setID(key.getKey().intValue());
                dao.delete(new DalHints().inShard(0), pojo);
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints().inShard(0));
                ret.setAge(2000);
                ret.setID(5);
                dao.insert(new DalHints().inShard(0).enableIdentityInsert(), ret);
                return true;
            }
        });


        client.execute(cmds, new DalHints().inShard(0));

        assertEquals(4, dao.count(new DalHints().inShard(0)));
        assertEquals(2000, dao.queryByPk(5, new DalHints().inShard(0)).getAge().intValue());
    }

    @Test
    public void testTransCommandsFailDepand() throws SQLException {
        final KeyHolder key = new KeyHolder();
        List<DalCommand> cmds = new LinkedList<DalCommand>();
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints().inShard(0));
//			ret.setAge(1000);
//			dao.update(new DalHints().inShard(0), ret);

                dao.insert(new DalHints().inShard(0), key, ret);
                return true;
            }
        });
        cmds.add(new DalCommand() {
            @Override
            public boolean execute(DalClient client) throws SQLException {
                MysqlPersonTable pojo = new MysqlPersonTable();
                pojo.setID(key.getKey().intValue());
                dao.delete(new DalHints().inShard(0), pojo);
                MysqlPersonTable ret = dao.queryByPk(1,
                        new DalHints().inShard(0));
                ret.setAge(2000);
                ret.setID(2);
                dao.insert(new DalHints().inShard(0).enableIdentityInsert(), ret);
                return true;
            }
        });
        try {
            client.execute(cmds, new DalHints().inShard(0));
        } catch (Exception e) {
//			e.printStackTrace();
        }
//		assertEquals(1000, dao.queryByPk(1, new DalHints().inShard(0)).getAge().intValue());
//		assertNull(dao.queryByPk(2, new DalHints().inShard(0)));
//		assertEquals(2000, dao.queryByPk(4, new DalHints().inShard(0)).getAge().intValue());
        assertEquals(3, dao.count(new DalHints().inShard(0)));
//		assertEquals(2000, dao.queryByPk(5, new DalHints().inShard(0)).getAge().intValue());
    }


}
