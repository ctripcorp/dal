package noshardtest;


import com.ctrip.platform.dal.dao.DalHints;
import dao.noshard.MasterOnlyOnSqlServerDao;
import entity.SqlServerPeopleTable;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/2/23.
 */
public class RWTestOnSQLServer {
    private static MasterOnlyOnSqlServerDao dao = null;
    //    private static Logger log = LoggerFactory.getLogger(OtherTests.class);
    private static String DATA_BASE = "RWOnSqlServer";
    private static String keyName0 = "DalService2DB";
    private static String keyName1 = "DalService3DB";
    private static String keyName2 = "DalServiceDB";
    private static String keyName3 = "DalService1DB";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
//        DalClientFactory.shutdownFactory();
//        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath()  + "DalConfigForRWTest/Dal.config");
        dao = new MasterOnlyOnSqlServerDao(DATA_BASE);
        //        先查询一遍并等待2秒，确保所有逻辑库的读库freshness已更新
        dao.count(null);
        Thread.sleep(2000);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
//        DalClientFactory.shutdownFactory();
    }

    @Before
    public void setUp() throws Exception {
//        DalClientFactory.shutdownFactory();
//        DalClientFactory.initClientFactory(this.getClass().getClassLoader().getResource(".").getPath() + "RWDalConfig/Dal.config");


        dao.test_def_truncate(new DalHints().inDatabase(keyName0));
        dao.test_def_truncate(new DalHints().inDatabase(keyName1).slaveOnly());
        dao.test_def_truncate(new DalHints().inDatabase(keyName2));
        dao.test_def_truncate(new DalHints().inDatabase(keyName3).slaveOnly());

        SqlServerPeopleTable masterOnlyOnSqlServer = new SqlServerPeopleTable();
        masterOnlyOnSqlServer.setName(keyName0);
        dao.insert(new DalHints().inDatabase(keyName0), masterOnlyOnSqlServer);

        masterOnlyOnSqlServer.setName(keyName1);
        dao.insert(new DalHints().inDatabase(keyName1).slaveOnly(), masterOnlyOnSqlServer);

        masterOnlyOnSqlServer.setName(keyName2);
        dao.insert(new DalHints().inDatabase(keyName2), masterOnlyOnSqlServer);

        masterOnlyOnSqlServer.setName(keyName3);
        dao.insert(new DalHints().inDatabase(keyName3).slaveOnly(), masterOnlyOnSqlServer);
    }

    @After
    public void tearDown() throws Exception {
//        DalClientFactory.shutdownFactory();
    }

    public void clearData(MasterOnlyOnSqlServerDao dao) throws Exception {
        dao.test_def_truncate(new DalHints().inDatabase(keyName0));
        dao.test_def_truncate(new DalHints().inDatabase(keyName1).slaveOnly());
        dao.test_def_truncate(new DalHints().inDatabase(keyName2));
        dao.test_def_truncate(new DalHints().inDatabase(keyName3).slaveOnly());
    }

    /*@Test
    public void test() throws Exception {
        SqlServerPeopleTable affected = dao.queryByPk(1, new DalHints().freshness(3));
        System.out.println(affected.getName());
        Thread.sleep(5000);
    }*/

    @Test
    public void testRWQuery() throws Exception {
        List<String> list = new ArrayList<>();
        //标准dao,多个slave随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.queryByPk(1, new DalHints().freshness(3)).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName1) & list.contains(keyName3));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName2));

        list.clear();
        //标准dao,多个master随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.queryByPk(1, new DalHints().masterOnly()).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName0) & list.contains(keyName2));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName3));

        list.clear();
        //构建dao，多个slave随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_build_queryByPK(1, new DalHints().freshness(3)).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName1) & list.contains(keyName3));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName2));

        list.clear();
        //构建dao，多个master随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_build_queryByPK(1, new DalHints().masterOnly()).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName0) & list.contains(keyName2));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName3));

        list.clear();
        //自定义dao,多个slave随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_def_queryByPK(1, new DalHints().freshness(3)).get(0).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName1) & list.contains(keyName3));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName2));

        list.clear();
        //自定义dao,多个master随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_def_queryByPK(1, new DalHints().masterOnly()).get(0).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName0) & list.contains(keyName2));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName3));
    }

    @Test
    public void testRWInsert() throws Exception {
        SqlServerPeopleTable masterOnlyOnSqlServer = new SqlServerPeopleTable();
        masterOnlyOnSqlServer.setName("testRWInsert");
        //标准dao,多个master随机写
        for (int i = 0; i < 20; i++) {
            dao.insert(new DalHints(), masterOnlyOnSqlServer);
        }
        List<SqlServerPeopleTable> listFromKey0 = dao.test_build_queryByName("testRWInsert", new DalHints().inDatabase(keyName0));
        List<SqlServerPeopleTable> listFromKey1 = dao.test_build_queryByName("testRWInsert", new DalHints().inDatabase(keyName1));
        List<SqlServerPeopleTable> listFromKey2 = dao.test_build_queryByName("testRWInsert", new DalHints().inDatabase(keyName2));
        List<SqlServerPeopleTable> listFromKey3 = dao.test_build_queryByName("testRWInsert", new DalHints().inDatabase(keyName3));

        assertNotEquals(0, listFromKey0.size());
        assertNotEquals(0, listFromKey2.size());
        assertEquals(0, listFromKey1.size());
        assertEquals(0, listFromKey3.size());

        clearData(dao);
        listFromKey0.clear();
        listFromKey1.clear();
        listFromKey2.clear();
        listFromKey3.clear();

        //标准dao,多个slave随机写
        for (int i = 0; i < 20; i++) {
            dao.insert(new DalHints().slaveOnly(), masterOnlyOnSqlServer);
        }
        listFromKey0 = dao.test_build_queryByName("testRWInsert", new DalHints().inDatabase(keyName0));
        listFromKey1 = dao.test_build_queryByName("testRWInsert", new DalHints().inDatabase(keyName1));
        listFromKey2 = dao.test_build_queryByName("testRWInsert", new DalHints().inDatabase(keyName2));
        listFromKey3 = dao.test_build_queryByName("testRWInsert", new DalHints().inDatabase(keyName3));

        assertNotEquals(0, listFromKey1.size());
        assertNotEquals(0, listFromKey3.size());
        assertEquals(0, listFromKey0.size());
        assertEquals(0, listFromKey2.size());

        clearData(dao);
        listFromKey0.clear();
        listFromKey1.clear();
        listFromKey2.clear();
        listFromKey3.clear();

        /*//构建dao,多个master随机写
        for (int i = 0; i < 20; i++) {
            dao.test_build_insert("testRWInsert",20,new DalHints());
        }
        listFromKey0=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName0));
        listFromKey1=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName1));
        listFromKey2=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName2));
        listFromKey3=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName3));

        assertNotEquals(0,listFromKey0.size());
        assertNotEquals(0,listFromKey2.size());
        assertEquals(0,listFromKey1.size());
        assertEquals(0,listFromKey3.size());

        clearData(dao);
        listFromKey0.clear();
        listFromKey1.clear();
        listFromKey2.clear();
        listFromKey3.clear();

        //自定义dao,多个master随机写
        for (int i = 0; i < 20; i++) {
            dao.testDefInsert("testRWInsert",new DalHints());
        }
        listFromKey0=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName0));
        listFromKey1=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName1));
        listFromKey2=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName2));
        listFromKey3=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName3));

        assertNotEquals(0,listFromKey0.size());
        assertNotEquals(0,listFromKey2.size());
        assertEquals(0,listFromKey1.size());
        assertEquals(0,listFromKey3.size());*/
    }

    @Test
    public void testRW_InDatabase_Query() throws Exception {
        List<String> list = new ArrayList<>();
        //标准dao,多个slave随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.queryByPk(1, new DalHints().freshness(3).inDatabase(keyName1)).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName1));
        assertFalse(list.contains(keyName3));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName2));

        list.clear();
        //标准dao,多个master随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.queryByPk(1, new DalHints().masterOnly().inDatabase(keyName2)).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName2));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName3));

        list.clear();
        //构建dao，多个slave随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_build_queryByPK(1, new DalHints().freshness(3).inDatabase(keyName3)).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName3));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName2));

        list.clear();
        //构建dao，多个master随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_build_queryByPK(1, new DalHints().masterOnly().inDatabase(keyName2)).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName2));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName3));

        list.clear();
        //自定义dao,多个slave随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_def_queryByPK(1, new DalHints().freshness(3).inDatabase(keyName1)).get(0).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName1));
        assertFalse(list.contains(keyName3));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName2));

        list.clear();
        //自定义dao,多个master随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_def_queryByPK(1, new DalHints().masterOnly().inDatabase(keyName0)).get(0).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName0));
        assertFalse(list.contains(keyName2));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName3));
    }

    @Test
    public void testRW_InDatabase_Conflict_Query() throws Exception {

        //智能读写分离设置为从库，indatabase指定写库，则读写库
        String name = dao.queryByPk(1, new DalHints().freshness(3).inDatabase(keyName0)).getName();
        System.out.println(name);

        //智能读写分离设置为主库，indatabase指定从库，则读写库
        try {
            name = dao.queryByPk(1, new DalHints().freshness(1).inDatabase(keyName1)).getName();
            System.out.println(name);
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
