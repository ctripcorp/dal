package test.com.ctrip.platform.dal.dao.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper;

public class DalShardingHelperTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testIsShardingEnabled() {
		assertTrue(DalShardingHelper.isShardingEnabled("dao_test_mod_mysql"));
		assertFalse(DalShardingHelper.isShardingEnabled("HA_Test_1"));
	}

	@Test
	public void testIsTableShardingEnabled() {
		assertTrue(DalShardingHelper.isTableShardingEnabled("dao_test_sqlsvr_tableShard", "dal_client_test"));
		assertFalse(DalShardingHelper.isTableShardingEnabled("dao_test_sqlsvr_tableShard", "dal_client_test123"));
		assertFalse(DalShardingHelper.isTableShardingEnabled("dao_test_simple", "aaa"));
	}

	@Test
	public void testBuildShardStr() {
		try {
			assertEquals("_1", DalShardingHelper.buildShardStr("dao_test_sqlsvr_tableShard", "1"));
			assertEquals("1", DalShardingHelper.buildShardStr("dao_test_sqlsvr_tableShard_simple", "1"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetDatabaseSet() {
		assertNotNull(DalShardingHelper.getDatabaseSet("dao_test_sqlsvr_tableShard"));
		try {
			DalShardingHelper.getDatabaseSet("xxx");
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testLocateTableShardId() {
		//tableColumns=index,tableIndex;tableMod=4;separator=_;
		StatementParameters parameters = null;;
		Map<String, Object> fields = null;
		try {
			// Preset
			assertEquals("0", DalShardingHelper.locateTableShardId("dao_test_sqlsvr_tableShard", "", new DalHints().inTableShard("0"), parameters, fields));
			
			// parameter
			parameters = new StatementParameters ();
			parameters.set(1, "index",  java.sql.Types.INTEGER, 1);
			assertEquals("1", DalShardingHelper.locateTableShardId("dao_test_sqlsvr_tableShard", "", new DalHints(), parameters, fields));

			//Fields
			parameters = null;
			fields = new HashMap<String, Object>();
			fields.put("index", 1);
			assertEquals("1", DalShardingHelper.locateTableShardId("dao_test_sqlsvr_tableShard", "", new DalHints(), parameters, fields));
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Not yet implemented");
		}
		
		// Cannot locate
		try {
			// parameter
			parameters = new StatementParameters ();
			parameters.set(1, "id",  java.sql.Types.INTEGER, 1);

			fields = new HashMap<String, Object>();
			fields.put("ab", 1);
			assertEquals("1", DalShardingHelper.locateTableShardId("dao_test_sqlsvr_tableShard", "", new DalHints(), parameters, fields));
			fail();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testShuffle() {
		final String logicDbName = "dao_test_mod";//;columns=id;mod=2
		final List<Map<String, ?>> daoPojos = new ArrayList<>();
		DalClient client;
		String shardId;
		
		Map<String, Object> pojo = new HashMap<>();
		pojo.put("id", 0);
		daoPojos.add(pojo);
		
		pojo = new HashMap<>();
		pojo.put("id", 1);
		daoPojos.add(pojo);
		
		// Test by pojos
		try {
			shardId = null;
			Map<String, Map<Integer, Map<String, ?>>> shuffled = DalShardingHelper.shuffle(logicDbName, shardId, daoPojos);
			assertEquals(2, shuffled.size());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		// Test preset shardid
		try {
			shardId = "0";
			Map<String, Map<Integer, Map<String, ?>>> shuffled = DalShardingHelper.shuffle(logicDbName, shardId, daoPojos);
			assertEquals(1, shuffled.size());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		// same shard
		client = DalClientFactory.getClient(logicDbName);
		try {
			assertFalse(DalTransactionManager.isInTransaction());
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					DalShardingHelper.shuffle(logicDbName, "0", daoPojos);
					return false;
				}
				
			}, new DalHints().inShard("0"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		// Detect in different shard with two shard ids
		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					DalShardingHelper.shuffle(logicDbName, null, daoPojos);
					return false;
				}
				
			}, new DalHints().inShard("0"));
			fail();
		} catch (SQLException e) {
		}
		
		// Detect in different shard with one shard ids
		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					DalShardingHelper.shuffle(logicDbName, "1", daoPojos);
					return false;
				}
				
			}, new DalHints().inShard("0"));
			fail();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testShuffleByTable() {
		final String logicDbName = "dao_test_sqlsvr_tableShard";//tableColumns=index,tableIndex;tableMod=4;separator=_;shardedTables=dal_client_test
		final Map<Integer, Map<String, ?>> daoPojos = new HashMap<>();
		
		Map<String, Object> pojo = new HashMap<>();
		pojo.put("index", 0);
		daoPojos.put(0, pojo);
		
		pojo = new HashMap<>();
		pojo.put("index", 1);
		daoPojos.put(1, pojo);
		

		try {
			assertEquals(2, DalShardingHelper.shuffleByTable(logicDbName, "", null, daoPojos).size());
			assertEquals(1, DalShardingHelper.shuffleByTable(logicDbName, "", "1", daoPojos).size());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIsAlreadySharded() {
		DalClient client;

		try {
			// No shard enabled
			assertTrue(DalShardingHelper.isAlreadySharded("HA_Test_1", null, null));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//is in transaction
		client = DalClientFactory.getClient("dao_test_sqlsvr_tableShard");
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					assertTrue(DalShardingHelper.isAlreadySharded("dao_test_sqlsvr_tableShard", "dal_client_test", null));
					return false;
				}
			}, new DalHints().inShard("0"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		// Test shard by DB
		try {
			assertFalse(DalShardingHelper.isAlreadySharded("dao_test_mod", null, new DalHints()));
			assertTrue(DalShardingHelper.isAlreadySharded("dao_test_mod", null, new DalHints().inShard("0")));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		// Test shard by table
		try {
			assertFalse(DalShardingHelper.isAlreadySharded("dao_test_sqlsvr_tableShard", "dal_client_test", new DalHints()));
			assertTrue(DalShardingHelper.isAlreadySharded("dao_test_sqlsvr_tableShard", "dal_client_test", new DalHints().inTableShard("0")));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void testDetectDistributedTransactionStringDalHintsListOfMapOfStringQ() {
		final String logicDbName = "dao_test_mod";//;columns=id;mod=2
		final List<Map<String, ?>> daoPojos = new ArrayList<>();
		DalClient client;
		String shardId;
		
		Set<String> shardIds = new HashSet<>();

		// Not in transaction
		try {
			shardIds.add("1");
			shardIds.add("0");
			DalShardingHelper.detectDistributedTransaction(shardIds);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		// Shard ids is null
		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					DalShardingHelper.detectDistributedTransaction(null);
					return false;
				}
				
			}, new DalHints().inShard("0"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		// More than 1 shards
		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					Set<String> shardIds = new HashSet<>();
					shardIds.add("1");
					shardIds.add("0");
					DalShardingHelper.detectDistributedTransaction(shardIds);
					return false;
				}
			}, new DalHints().inShard("0"));
			fail();
		} catch (SQLException e) {
		}
		
		// Not same shard
		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					Set<String> shardIds = new HashSet<>();
					shardIds.add("1");
					DalShardingHelper.detectDistributedTransaction(shardIds);
					return false;
				}
			}, new DalHints().inShard("0"));
			fail();
		} catch (SQLException e) {
		}
		
		// Not same shard
		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					Set<String> shardIds = new HashSet<>();
					shardIds.add("0");
					DalShardingHelper.detectDistributedTransaction(shardIds);
					return false;
				}
			}, new DalHints().inShard("0"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDetectDistributedTransactionSetOfString() {
		DalClient client;
		
		// Not a shard enabled DB
		try {
			DalShardingHelper.detectDistributedTransaction("HA_Test_1", null, null);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		final String logicDbName = "dao_test_mod";//;columns=id;mod=2
		// Not in transaction
		try {
			DalShardingHelper.detectDistributedTransaction(logicDbName, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		// Shard is not same
		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					DalShardingHelper.detectDistributedTransaction(logicDbName, new DalHints().inShard(0), null);
					return false;
				}
			}, new DalHints().inShard(1));
			fail();
		} catch (SQLException e) {
		}

		// Shard is same
		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					DalShardingHelper.detectDistributedTransaction(logicDbName, new DalHints().inShard(1), null);
					return false;
				}
			}, new DalHints().inShard(1));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		// can not decide and not same
		final List<Map<String, ?>> daoPojos = new ArrayList<>();
		
		Map<String, Object> pojo = new HashMap<>();
		pojo.put("id", 0);
		daoPojos.add(pojo);
		
		pojo = new HashMap<>();
		pojo.put("id", 1);
		daoPojos.add(pojo);

		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					DalShardingHelper.detectDistributedTransaction(logicDbName, new DalHints(), daoPojos);
					return false;
				}
			}, new DalHints().inShard(1));
			fail();
		} catch (SQLException e) {
		}		

		// can not decide and same
		daoPojos.clear();
		pojo = new HashMap<>();
		pojo.put("id", 1);
		daoPojos.add(pojo);
		
		pojo = new HashMap<>();
		pojo.put("id", 1);
		daoPojos.add(pojo);

		client = DalClientFactory.getClient(logicDbName);
		try {
			client.execute(new DalCommand(){
				public boolean execute(DalClient client) throws SQLException {
					DalShardingHelper.detectDistributedTransaction(logicDbName, new DalHints(), daoPojos);
					return false;
				}
			}, new DalHints().inShard(1));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}		
	}
}
