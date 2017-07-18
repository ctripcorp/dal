package test.com.ctrip.platform.dal.dao.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.com.ctrip.platform.dal.dao.task.SqlServerTestInitializer;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DalConnectionManager;

public class DalConnectionManagerTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SqlServerTestInitializer.setUpBeforeClass();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		SqlServerTestInitializer.tearDownAfterClass();
	}

	@Before
	public void setUp() throws Exception {
		SqlServerTestInitializer.setUp();
	}

	@After
	public void tearDown() throws Exception {
		SqlServerTestInitializer.tearDown();
	}
	
	private static final String noShardDb = "dao_test_sqlsvr";
	private static final String shardDb = "dao_test_sqlsvr_dbShard";
	
	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static DalConnectionManager getDalConnectionManager(String db) throws Exception {
		return new DalConnectionManager(db, DalClientFactory.getDalConfigure());
	}
	
	@Test
	public void testGetNewConnection() {
		boolean useMaster = true;
		DalHints hints = new DalHints();
		
		try {
			DalConnectionManager test = getDalConnectionManager(noShardDb);
			DalConnection conn = test.getNewConnection(hints, useMaster, DalEventEnum.BATCH_CALL);
			assertNotNull(conn);
			assertNotNull(conn.getConn());
			conn.getConn().close();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

    @Test
    public void testEvaluate() {
        boolean useMaster = true;
        DalHints hints = new DalHints();
        
        try {
            DalConnectionManager test = getDalConnectionManager(noShardDb);
            
            String id = test.evaluateShard(hints);
            assertNull(id);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        
        try {
            DalConnectionManager test = getDalConnectionManager(shardDb);
            hints = new DalHints();
            hints.inShard(1);
            String id = test.evaluateShard(hints);
            assertEquals("1", id);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        
        try {
            DalConnectionManager test = getDalConnectionManager(shardDb);
            hints = new DalHints();
            hints.inShard("1");
            String id = test.evaluateShard(hints);
            assertEquals("1", id);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        
        try {
            DalConnectionManager test = getDalConnectionManager(shardDb);
            hints = new DalHints();
            hints.setShardValue("3");
            String id = test.evaluateShard(hints);
            assertEquals("1", id);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            DalConnectionManager test = getDalConnectionManager(shardDb);
            hints = new DalHints();
            hints.setShardColValue("index", "3");
            String id = test.evaluateShard(hints);
            assertEquals("1", id);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            DalConnectionManager test = getDalConnectionManager(shardDb);
            hints = new DalHints();
            Map<String, String> shardColValues = new HashMap<>();
            shardColValues.put("index", "3");
            hints.setShardColValues(shardColValues);
            String id = test.evaluateShard(hints);
            assertEquals("1", id);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

	@Test
	public void testDoInConnection() {
		final boolean useMaster = true;
		final DalHints hints = new DalHints();
		
		try {
			final DalConnectionManager test = getDalConnectionManager(noShardDb);
			ConnectionAction<Object> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					connHolder = test.getNewConnection(hints, useMaster, DalEventEnum.BATCH_CALL);
					statement = connHolder.getConn().createStatement();
					rs = statement.executeQuery("select * from " + SqlServerTestInitializer.TABLE_NAME);
					rs.next();
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInConnection(action, hints);
			assertTrue(action.conn == null);
			assertTrue(action.statement == null);
			assertTrue(action.rs == null);
			assertTrue(action.connHolder == null);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDoInConnectionException() {
		final boolean useMaster = true;
		final DalHints hints = new DalHints();
		final String message  = "testDoInConnectionException";
		ConnectionAction<Object> action = null;
		try {
			
			final DalConnectionManager test = getDalConnectionManager(noShardDb);
			action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					connHolder = test.getNewConnection(hints, useMaster, DalEventEnum.BATCH_CALL);
					
					statement = connHolder.getConn().createStatement();
					rs = statement.executeQuery("select * from City");
					rs.next();
					throw new RuntimeException(message);
				}
			};
			
			test.doInConnection(action, hints);
			fail();
		} catch (Exception e) {
			e.printStackTrace();
			assertNotNull(action);
			assertTrue(action.conn == null);
			assertTrue(action.statement == null);
			assertTrue(action.rs == null);
			assertTrue(action.connHolder == null);
		}
	}
}
