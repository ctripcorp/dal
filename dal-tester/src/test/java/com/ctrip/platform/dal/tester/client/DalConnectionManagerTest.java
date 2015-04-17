package com.ctrip.platform.dal.tester.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DalConnectionManager;

public class DalConnectionManagerTest {
	
	private static final String connectionString = "HotelPubDB";
	
	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static DalConnectionManager getDalConnectionManager() throws Exception {
		return new DalConnectionManager(connectionString, DalClientFactory.getDalConfigure());
	}
	
	@Test
	public void testGetNewConnection() {
		boolean useMaster = true;
		DalHints hints = new DalHints();
		
		try {
			DalConnectionManager test = getDalConnectionManager();
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
	public void testDoInConnection() {
		final boolean useMaster = true;
		final DalHints hints = new DalHints();
		
		try {
			final DalConnectionManager test = getDalConnectionManager();
			ConnectionAction<Object> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					connHolder = test.getNewConnection(hints, useMaster, DalEventEnum.BATCH_CALL);
					statement = connHolder.getConn().createStatement();
					rs = statement.executeQuery("select * from Hotel");
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
			
			final DalConnectionManager test = getDalConnectionManager();
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
