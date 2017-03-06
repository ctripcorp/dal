package test.com.ctrip.platform.dal.dao.unittests;

import java.sql.SQLException;

import org.junit.Assert;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTestHelper {
	// Only for sql server
	private final static boolean ASSERT_ALLOWED = false;
	// Only for sql server
	private final static boolean CREATE_ALLOWED = false;
	// Only for sql server
	private final static String GENERATED_KEY = "GENERATED_KEYS";

	public static int getCount(DalTableDao<?> dao) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints()).size();
	}

	public static int getCount(DalTableDao<?> dao, String where) throws SQLException {
		return dao.query(where, new StatementParameters(), new DalHints()).size();
	}
	
	public static int getCountByDb(DalTableDao<?> dao, int shardId) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints().inShard(shardId)).size();
	}

	public static int getCountByTable(DalTableDao<?> dao, int shardId) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(shardId)).size();
	}
	
	public static int getCountByDbTable(DalTableDao<?> dao, int shardId, int tableShardId) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints().inShard(shardId).inTableShard(tableShardId)).size();
	}
	
	public static Object query(DalTableDao<?> dao, String where, Object... params) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints()).size();
	}
	
	public static void deleteAllShardsByDb(DalTableDao<?> dao, int mod) throws SQLException {
		for(int i = 0; i < mod; i++) {
			int j = 1;
			dao.delete("1=1", new StatementParameters(), new DalHints().inShard(i));
		}
	}
	
	public static void deleteAllShardsByDbTable(DalTableDao<?> dao, int mod, int tableMod) throws SQLException {
		for(int i = 0; i < mod; i++) {
			for(int j = 0; j < tableMod; j++) 
				dao.delete("1=1", new StatementParameters(), new DalHints().inShard(i).inTableShard(j));
		}
	}
	
	// Only for sql server
	public static void assertKeyHolder(KeyHolder holder) throws SQLException {
		if(holder == null)
			return;
		Assert.assertEquals(3, holder.size());		 
		Assert.assertTrue(holder.getKey(0).longValue() > 0);
		Assert.assertTrue(holder.getKeyList().get(0).containsKey(GENERATED_KEY));
	}
	
	// Only for sql server
	public static void assertResEquals(int expected, int real) {
		if(ASSERT_ALLOWED)
			Assert.assertEquals(expected, real);
	}
	
	// Only for sql server
	public static KeyHolder createKeyHolder() {
		return CREATE_ALLOWED ? new KeyHolder() : null;
	}
	
	// Only for sql server
	public static void assertResEquals(int[] expected, int[] real) {
		Assert.assertEquals(expected.length, real.length);
		if(ASSERT_ALLOWED)
			Assert.assertArrayEquals(expected, real);
	}
}
