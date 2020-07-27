package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import org.junit.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SlaveFreshnessScannerMysqlTest {
    /**
     * shard_0: 3
     * shard_1: 5
     * dal_shard_0: 7
     * dal_shard_1: 9
     */
    
    private static final String GET_DB_NAME = "select DATABASE() as id";
    private static final String NO_FRESHNESS_DATABASE_NAME = "MysqlNoFreshness";
    private static final String NO_FRESHNESS_MASTER = "dao_test";//should be real db name
    
    private static final String DATABASE_NAME = "SimpleMysqlFreshness";
    private static final String master = "shard_0";
    private static final Map<String, Integer> freshnessMap = new HashMap<>();
    
    private static final String SHARD_DATABASE_NAME = "SimpleMysqlShardFreshness";
    private static final String[] masterShard = new String[]{"dao_test", "dao_test_mysql"};    
    private static final Map<Integer, Map<String, Integer>> freshnessShardMap = new HashMap<>();
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        freshnessMap.put("shard_0", 3);
        freshnessMap.put("shard_1", 5);
        freshnessMap.put("dal_shard_0", 7);
        freshnessMap.put("dal_shard_1", 9);
        
        Map<String, Integer> shardMap = new HashMap<>();
        shardMap.put("shard_0", 3);
        shardMap.put("shard_1", 5);
        
        freshnessShardMap.put(0, shardMap);
        
        shardMap = new HashMap<>();
        shardMap.put("dal_shard_0", 7);
        shardMap.put("dal_shard_1", 9);
        
        freshnessShardMap.put(1, shardMap);
        DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
        String id = dao.queryForObject(GET_DB_NAME, new StatementParameters(), new DalHints(), String.class);
        // make sure warmup is done.
        Thread.sleep(1 * 1000);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        
    }

    @Test
    public void testNoFreshness() throws SQLException {
        DalQueryDao dao = new DalQueryDao(NO_FRESHNESS_DATABASE_NAME);
        String id = dao.queryForObject(GET_DB_NAME, new StatementParameters(), new DalHints().freshness(10), String.class);
        Assert.assertEquals(NO_FRESHNESS_MASTER, id);
        dao = new DalQueryDao(NO_FRESHNESS_DATABASE_NAME);
        id = dao.queryForObject(GET_DB_NAME, new StatementParameters(), new DalHints().freshness(10).slaveOnly(), String.class);
        Assert.assertEquals(NO_FRESHNESS_MASTER, id);
    }
    
    @Test
    public void testSlaveOnlyAndFreshness() throws SQLException {
        int freshness = 9;
        DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
        for(int i = 0; i < 100; i++){
            String id = dao.queryForObject(GET_DB_NAME, new StatementParameters(), new DalHints().freshness(freshness).slaveOnly(), String.class);
            Assert.assertTrue(freshnessMap.get(id) <= freshness);
        }
    }    
    @Test
    public void testBelow() throws SQLException {
        testBelow(3);
        testBelow(5);
        testBelow(7);
        testBelow(9);
    }
    
    @Test
    public void testMaster() throws SQLException {
        testMaster(2);
        testMaster(1);
        testMaster(0);
        testMaster(-1);
    }
    
    private void testBelow(int freshness) throws SQLException {
        DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
        for(int i = 0; i < 100; i++){
            String id = dao.queryForObject(GET_DB_NAME, new StatementParameters(), new DalHints().freshness(freshness), String.class);
            Assert.assertTrue(freshnessMap.get(id) <= freshness);
        }
    }
    
    private void testMaster(int freshness) throws SQLException {
        DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
        for(int i = 0; i < 100; i++){
            String id = dao.queryForObject(GET_DB_NAME, new StatementParameters(), new DalHints().freshness(freshness), String.class);
            Assert.assertEquals(master, id);
        }
    }
    
    @Test
    public void testShardBelow() throws SQLException {
        testShardBelow(0, 3);
        testShardBelow(0, 5);
        testShardBelow(1, 7);
        testShardBelow(1, 9);
    }
    
    @Test
    public void testShardMaster() throws SQLException {
        testShardMaster(2);
        testShardMaster(1);
        testShardMaster(0);
        testShardMaster(-1);
    }
    
    private void testShardBelow(int shardId, int freshness) throws SQLException {
        DalQueryDao dao = new DalQueryDao(SHARD_DATABASE_NAME);
        
        for(int i = 0; i < 100; i++){
            String id = dao.queryForObject(GET_DB_NAME, new StatementParameters(), new DalHints().freshness(freshness).inShard(shardId), String.class);
            Assert.assertTrue(freshnessShardMap.get(shardId).get(id) <= freshness);
        }
    }
    
    private void testShardMaster(int freshness) throws SQLException {
        DalQueryDao dao = new DalQueryDao(SHARD_DATABASE_NAME);
        
        int shardId = 0;
        while(shardId<2) {
            for(int i = 0; i < 100; i++){
                String id = dao.queryForObject(GET_DB_NAME, new StatementParameters(), new DalHints().freshness(freshness).inShard(shardId), String.class);
                Assert.assertEquals(masterShard[shardId], id);
            }
            shardId++;
        }
    }   
}