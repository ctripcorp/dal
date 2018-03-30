package shardTest.newVersionCodeTest;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import noShardTest.MasterOnlyOnSqlServer;
import noShardTest.MasterOnlyOnSqlServerDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by lilj on 2018/2/23.
 */
public class RWShardTestOnSqlServer {
    private static MasterOnlyOnSqlServerDao dao = null;

    private static String DATA_BASE = "RWShardOnSqlServer";
    private static String shard_0_master_db = "DalServiceDB";
    private static String shard_0_slave_db = "DalService1DB";
    private static String shard_1_master_db= "test_masteronly_sqlserver_0";
    private static String shard_1_slave_db = "test_masteronly_sqlserver_1";


    @Before
    public void setUp() throws Exception {
//        DalClientFactory.shutdownFactory();
//        DalClientFactory.initClientFactory(this.getClass().getClassLoader().getResource(".").getPath() + "RWDalConfig/Dal.config");

        dao = new MasterOnlyOnSqlServerDao(DATA_BASE);
        dao.test_def_truncate(new DalHints().inShard(0));
        dao.test_def_truncate(new DalHints().inShard(0).slaveOnly());
        dao.test_def_truncate(new DalHints().inShard(1));
        dao.test_def_truncate(new DalHints().inShard(1).slaveOnly());

        MasterOnlyOnSqlServer masterOnlyOnSqlServer = new MasterOnlyOnSqlServer();
        masterOnlyOnSqlServer.setName(shard_0_master_db);
        dao.insert(new DalHints().inShard(0), masterOnlyOnSqlServer);

        masterOnlyOnSqlServer.setName(shard_0_slave_db);
        dao.insert(new DalHints().inShard(0).slaveOnly(), masterOnlyOnSqlServer);

        masterOnlyOnSqlServer.setName(shard_1_master_db);
        dao.insert(new DalHints().inShard(1), masterOnlyOnSqlServer);

        masterOnlyOnSqlServer.setName(shard_1_slave_db);
        dao.insert(new DalHints().inShard(1).slaveOnly(), masterOnlyOnSqlServer);
    }

    @After
    public void tearDown() throws Exception {
//        DalClientFactory.shutdownFactory();
    }

    public void clearData(MasterOnlyOnSqlServerDao dao) throws  Exception{
        dao.test_def_truncate(new DalHints().inShard(0));
        dao.test_def_truncate(new DalHints().inShard(0).slaveOnly());
        dao.test_def_truncate(new DalHints().inShard(1));
        dao.test_def_truncate(new DalHints().inShard(1).slaveOnly());
    }

   /* @Test
    public void test() throws Exception {
        //freshness>delay, read from slave
        MasterOnlyOnSqlServer affected = dao.queryByPk(1,new DalHints().freshness(3));
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
        MasterOnlyOnSqlServer masterOnlyOnSqlServer=new MasterOnlyOnSqlServer();
        masterOnlyOnSqlServer.setName("testRWShardInsert");
        masterOnlyOnSqlServer.setCityID(20);

        //标准dao
        dao.insert(new DalHints(),masterOnlyOnSqlServer);

        List<MasterOnlyOnSqlServer> listFromKey0=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0).masterOnly());
        List<MasterOnlyOnSqlServer> listFromKey1=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(0));
        List<MasterOnlyOnSqlServer> listFromKey2=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1).masterOnly());
        List<MasterOnlyOnSqlServer> listFromKey3=dao.test_build_queryByName("testRWShardInsert",new DalHints().inShard(1));

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
        dao.insert(new DalHints().slaveOnly(),masterOnlyOnSqlServer);

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

        /*//构建dao
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
        assertEquals(0,listFromKey3.size());*/
    }

   /* @Test
    public void testShard_InDatabase_Query() throws Exception {
        String name=dao.queryByPk(1,new DalHints().inShard(0).inDatabase(shard_1_master_db)).getName();
    }*/
}
