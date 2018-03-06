package noShardTest;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.FreshnessHelper;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilj on 2018/2/22.
 */
public class RWTestOnMysql {

    private static MasterOnlyOnMysqlDao dao = null;
    //    private static Logger log = LoggerFactory.getLogger(OtherTests.class);
    private static String DATA_BASE = "RWOnMysql";
    private static String keyName0 = "DalService4DB_W";
    private static String keyName1 = "DalService5DB_W";
    private static String keyName2 = "DalService2DB_W";
    private static String keyName3 = "DalService3DB_W";

    @Before
    public void setUp() throws Exception {
//        DalClientFactory.shutdownFactory();
//        DalClientFactory.initClientFactory(this.getClass().getClassLoader().getResource(".").getPath() + "RWDalConfig/Dal.config");

        dao = new MasterOnlyOnMysqlDao(DATA_BASE);
        dao.test_def_truncate(new DalHints().inDatabase(keyName0));
        dao.test_def_truncate(new DalHints().inDatabase(keyName1).slaveOnly());
        dao.test_def_truncate(new DalHints().inDatabase(keyName2));
        dao.test_def_truncate(new DalHints().inDatabase(keyName3).slaveOnly());

        MasterOnlyOnMysql masterOnlyOnMysql = new MasterOnlyOnMysql();
        masterOnlyOnMysql.setName(keyName0);
        dao.insert(new DalHints().inDatabase(keyName0), masterOnlyOnMysql);

        masterOnlyOnMysql.setName(keyName1);
        dao.insert(new DalHints().inDatabase(keyName1).slaveOnly(), masterOnlyOnMysql);

        masterOnlyOnMysql.setName(keyName2);
        dao.insert(new DalHints().inDatabase(keyName2), masterOnlyOnMysql);

        masterOnlyOnMysql.setName(keyName3);
        dao.insert(new DalHints().inDatabase(keyName3).slaveOnly(), masterOnlyOnMysql);
    }

    @After
    public void tearDown() throws Exception {
//        DalClientFactory.shutdownFactory();
    }

    public void clearData(MasterOnlyOnMysqlDao dao) throws  Exception{
        dao.test_def_truncate(new DalHints().inDatabase(keyName0));
        dao.test_def_truncate(new DalHints().inDatabase(keyName1).slaveOnly());
        dao.test_def_truncate(new DalHints().inDatabase(keyName2));
        dao.test_def_truncate(new DalHints().inDatabase(keyName3).slaveOnly());
    }

    /*@Test
    public void test() throws Exception {
        //freshness>delay, read from slave
//        FreshnessHelper.getSlaveFreshness(DATA_BASE,keyName1);
        MasterOnlyOnMysql affected = dao.queryByPk(1,new DalHints().freshness(3));
        assertEquals(keyName1, affected.getName());
//        Thread.sleep(20000);
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
        assertTrue(list.contains(keyName1)&list.contains(keyName3));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName2));

        list.clear();
        //标准dao,多个master随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.queryByPk(1, new DalHints().masterOnly()).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName0)&list.contains(keyName2));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName3));

        list.clear();
        //构建dao，多个slave随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_build_queryByPK(1, new DalHints().freshness(3)).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName1)&list.contains(keyName3));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName2));

        list.clear();
        //构建dao，多个master随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_build_queryByPK(1, new DalHints().masterOnly()).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName0)&list.contains(keyName2));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName3));

        list.clear();
        //自定义dao,多个slave随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_def_queryByPK(1, new DalHints().freshness(3)).get(0).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName1)&list.contains(keyName3));
        assertFalse(list.contains(keyName0));
        assertFalse(list.contains(keyName2));

        list.clear();
        //自定义dao,多个master随机读
        for (int i = 0; i < 20; i++) {
            String name = dao.test_def_queryByPK(1, new DalHints().masterOnly()).get(0).getName();
            list.add(name);
            System.out.println(name);
        }
        assertTrue(list.contains(keyName0)&list.contains(keyName2));
        assertFalse(list.contains(keyName1));
        assertFalse(list.contains(keyName3));
    }

    @Test
    public void testRWInsert() throws Exception {
        MasterOnlyOnMysql masterOnlyOnMysql=new MasterOnlyOnMysql();
        masterOnlyOnMysql.setName("testRWInsert");
        //标准dao,多个master随机写
        for (int i = 0; i < 20; i++) {
            dao.insert(new DalHints(),masterOnlyOnMysql);
        }
        List<MasterOnlyOnMysql> listFromKey0=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName0));
        List<MasterOnlyOnMysql> listFromKey1=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName1));
        List<MasterOnlyOnMysql> listFromKey2=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName2));
        List<MasterOnlyOnMysql> listFromKey3=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName3));

        assertNotEquals(0,listFromKey0.size());
        assertNotEquals(0,listFromKey2.size());
        assertEquals(0,listFromKey1.size());
        assertEquals(0,listFromKey3.size());

        clearData(dao);
        listFromKey0.clear();
        listFromKey1.clear();
        listFromKey2.clear();
        listFromKey3.clear();

        //标准dao,多个slave随机写
        for (int i = 0; i < 20; i++) {
            dao.insert(new DalHints().slaveOnly(),masterOnlyOnMysql);
        }
        listFromKey0=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName0));
        listFromKey1=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName1));
        listFromKey2=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName2));
        listFromKey3=dao.test_build_queryByName("testRWInsert",new DalHints().inDatabase(keyName3));

        assertNotEquals(0,listFromKey1.size());
        assertNotEquals(0,listFromKey3.size());
        assertEquals(0,listFromKey0.size());
        assertEquals(0,listFromKey2.size());

        clearData(dao);
        listFromKey0.clear();
        listFromKey1.clear();
        listFromKey2.clear();
        listFromKey3.clear();

        //构建dao,多个master随机写
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
        assertEquals(0,listFromKey3.size());
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
        try{
        name = dao.queryByPk(1, new DalHints().freshness(1).inDatabase(keyName1)).getName();
        System.out.println(name);
        fail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
