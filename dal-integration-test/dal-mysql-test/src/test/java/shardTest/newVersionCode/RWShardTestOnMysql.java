package shardTest.newVersionCode;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import noShardTest.MasterOnlyOnMysql;
import noShardTest.MasterOnlyOnMysqlDao;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/2/23.
 */
public class RWShardTestOnMysql {
    private static MasterOnlyOnMysqlDao dao = null;
    //    private static Logger log = LoggerFactory.getLogger(OtherTests.class);
//    private static String DATA_BASE = "RWOnMysql";
    private static String DATA_BASE = "RWShardOnMysql";
    private static String shard_0_master_db = "DalService2DB_W";
    private static String shard_0_slave_db = "DalService3DB_W";
    private static String shard_1_master_db= "test_masteronly_mysql_0";
    private static String shard_1_slave_db = "test_masteronly_mysql_1";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
//        DalClientFactory.shutdownFactory();
//        DalClientFactory.initClientFactory(ClassLoader.getSystemClassLoader().getResource(".").getPath()  + "DalConfigForRWTest/Dal.config");
        dao = new MasterOnlyOnMysqlDao(DATA_BASE);
        //        先查询一遍并等待2秒，确保所有逻辑库的读库freshness已更新
        dao.count(new DalHints().inShard(0));
        Thread.sleep(2000);
    }

    @Before
    public void setUp() throws Exception {
//        DalClientFactory.shutdownFactory();
//        DalClientFactory.initClientFactory(this.getClass().getClassLoader().getResource(".").getPath() + "RWDalConfig/Dal.config");

        dao.test_def_truncate(new DalHints().inShard(0));
        dao.test_def_truncate(new DalHints().inShard(0).slaveOnly());
        dao.test_def_truncate(new DalHints().inShard(1));
        dao.test_def_truncate(new DalHints().inShard(1).slaveOnly());

        MasterOnlyOnMysql masterOnlyOnMysql = new MasterOnlyOnMysql();
        masterOnlyOnMysql.setName(shard_0_master_db);
        dao.insert(new DalHints().inShard(0), masterOnlyOnMysql);

        masterOnlyOnMysql.setName(shard_0_slave_db);
        dao.insert(new DalHints().inShard(0).slaveOnly(), masterOnlyOnMysql);

        masterOnlyOnMysql.setName(shard_1_master_db);
        dao.insert(new DalHints().inShard(1), masterOnlyOnMysql);

        masterOnlyOnMysql.setName(shard_1_slave_db);
        dao.insert(new DalHints().inShard(1).slaveOnly(), masterOnlyOnMysql);
    }

    @After
    public void tearDown() throws Exception {
//        DalClientFactory.shutdownFactory();
    }

    public void clearData(MasterOnlyOnMysqlDao dao) throws  Exception{
        dao.test_def_truncate(new DalHints().inShard(0));
        dao.test_def_truncate(new DalHints().inShard(0).slaveOnly());
        dao.test_def_truncate(new DalHints().inShard(1));
        dao.test_def_truncate(new DalHints().inShard(1).slaveOnly());
    }

   /* @Test
    public void test() throws Exception {
        //freshness>delay, read from slave
        MasterOnlyOnMysql affected = dao.queryByPk(1,new DalHints().freshness(3));
        assertEquals(keyName0, affected.getName());
        Thread.sleep(20000);
    }*/

    @Test
    public void testRWShardQuery() throws Exception {
//        标准dao
       String name=dao.queryByPk(1,new DalHints().inShard(0).masterOnly()).getName();
       assertEquals(shard_0_master_db,name);

       name=dao.queryByPk(1,new DalHints().inShard(0)).getName();
       assertEquals(shard_0_slave_db,name);

       name=dao.queryByPk(1,new DalHints().inShard(0).freshness(3)).getName();
       assertEquals(shard_0_slave_db,name);

       name=dao.queryByPk(1,new DalHints().inShard(1).freshness(2)).getName();
       assertEquals(shard_1_slave_db,name);

       name=dao.queryByPk(1,new DalHints().inShard(1).freshness(1)).getName();
       assertEquals(shard_1_master_db,name);

       name=dao.queryByPk(1,new DalHints().inShard(0).freshness(3).masterOnly()).getName();
       assertEquals(shard_0_master_db,name);

//       构建dao
        name=dao.test_build_queryByPK(1,new DalHints().inShard(0).masterOnly()).getName();
        assertEquals(shard_0_master_db,name);

        name=dao.test_build_queryByPK(1,new DalHints().inShard(0)).getName();
        assertEquals(shard_0_slave_db,name);

        name=dao.test_build_queryByPK(1,new DalHints().inShard(0).freshness(3)).getName();
        assertEquals(shard_0_slave_db,name);

        name=dao.test_build_queryByPK(1,new DalHints().inShard(1).freshness(2)).getName();
        assertEquals(shard_1_slave_db,name);

        name=dao.test_build_queryByPK(1,new DalHints().inShard(1).freshness(1)).getName();
        assertEquals(shard_1_master_db,name);

        name=dao.test_build_queryByPK(1,new DalHints().inShard(0).freshness(3).masterOnly()).getName();
        assertEquals(shard_0_master_db,name);

//        自定义dao
        name=dao.test_def_queryByPK(1,new DalHints().inShard(0).masterOnly()).get(0).getName();
        assertEquals(shard_0_master_db,name);

        name=dao.test_def_queryByPK(1,new DalHints().inShard(0)).get(0).getName();
        assertEquals(shard_0_slave_db,name);

        name=dao.test_def_queryByPK(1,new DalHints().inShard(0).freshness(3)).get(0).getName();
        assertEquals(shard_0_slave_db,name);

        name=dao.test_def_queryByPK(1,new DalHints().inShard(1).freshness(2)).get(0).getName();
        assertEquals(shard_1_slave_db,name);

        name=dao.test_def_queryByPK(1,new DalHints().inShard(1).freshness(1)).get(0).getName();
        assertEquals(shard_1_master_db,name);

        name=dao.test_def_queryByPK(1,new DalHints().inShard(0).freshness(3).masterOnly()).get(0).getName();
        assertEquals(shard_0_master_db,name);
    }

    @Test
    public void testRWShardInsert() throws Exception {
        MasterOnlyOnMysql masterOnlyOnMysql=new MasterOnlyOnMysql();
        masterOnlyOnMysql.setName("testRWShardInsert");
        masterOnlyOnMysql.setAge(20);

        //标准dao
        dao.insert(new DalHints(),masterOnlyOnMysql);

        List<MasterOnlyOnMysql> listFromKey0=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0).masterOnly());
        List<MasterOnlyOnMysql> listFromKey1=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0));
        List<MasterOnlyOnMysql> listFromKey2=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1).masterOnly());
        List<MasterOnlyOnMysql> listFromKey3=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1));

        assertNotEquals(0,listFromKey0.size());
        assertEquals(0,listFromKey2.size());
        assertEquals(0,listFromKey1.size());
        assertEquals(0,listFromKey3.size());

        clearData(dao);
        listFromKey0.clear();
        listFromKey1.clear();
        listFromKey2.clear();
        listFromKey3.clear();

        //标准dao,slaveOnly
            dao.insert(new DalHints().slaveOnly(),masterOnlyOnMysql);

        listFromKey0=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0).masterOnly());
        listFromKey1=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0));
        listFromKey2=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1).masterOnly());
        listFromKey3=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1));

        assertNotEquals(0,listFromKey1.size());
        assertEquals(0,listFromKey3.size());
        assertEquals(0,listFromKey0.size());
        assertEquals(0,listFromKey2.size());

        clearData(dao);
        listFromKey0.clear();
        listFromKey1.clear();
        listFromKey2.clear();
        listFromKey3.clear();

        //构建dao
            dao.test_build_insert("testRWShardInsert",21,new DalHints());

        listFromKey0=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0).masterOnly());
        listFromKey1=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0));
        listFromKey2=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1).masterOnly());
        listFromKey3=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1));

        assertEquals(0,listFromKey0.size());
        assertNotEquals(0,listFromKey2.size());
        assertEquals(0,listFromKey1.size());
        assertEquals(0,listFromKey3.size());

        clearData(dao);
        listFromKey0.clear();
        listFromKey1.clear();
        listFromKey2.clear();
        listFromKey3.clear();

        //自定义dao,多个master随机写
            dao.testDefInsert("testRWShardInsert",new DalHints().inShard(0));

        listFromKey0=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0).masterOnly());
        listFromKey1=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0));
        listFromKey2=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1).masterOnly());
        listFromKey3=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1));

        assertNotEquals(0,listFromKey0.size());
        assertEquals(0,listFromKey2.size());
        assertEquals(0,listFromKey1.size());
        assertEquals(0,listFromKey3.size());
    }

   /* @Test
    public void testShard_InDatabase_Query() throws Exception {
        String name=dao.queryByPk(1,new DalHints().inShard(0).inDatabase(shard_1_master_db)).getName();
    }*/
}
