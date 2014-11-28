package com.ctrip.platform.dal.tester.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;

import org.junit.Test;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DalConnectionManager;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.client.DbMeta;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.sql.logging.LogEntry;

public class ConnectionActionTest {
	private static final String logicDbName = "HtlOvsPubDB_INSERT_1";
	
	private DalConnection getDalConnection() throws Exception {
		Connection conn = null;
		conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
		return new DalConnection(conn, DbMeta.createIfAbsent(logicDbName, null, null, true, conn));
	}
	
	private static DalConnectionManager getDalConnectionManager() throws Exception {
		return new DalConnectionManager(logicDbName, DalConfigureFactory.load());
	}
	
	@Test
	public void testInitLogEntry() {
		TestConnectionAction test = new TestConnectionAction();
		test.initLogEntry("Test", new DalHints());
		assertNotNull(test.entry);
	}

	@Test
	public void testPopulateDbMetaOutofTransaction() {
		TestConnectionAction test = new TestConnectionAction();
		test.initLogEntry("Test", new DalHints());
		try {
			test.connHolder = getDalConnection();
			test.populateDbMeta();
			assertNotNull(test.entry.getTag().get(LogEntry.TAG_DATABASE_NAME));
			//assertNotNull(test.entry.getTag().get(LogEntry.TAG_USER_NAME)); be removed
			//assertNotNull(test.entry.getTag().get(LogEntry.TAG_SERVER_ADDRESS)); be removed
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testPopulateDbMetaInTransaction() {
		TestConnectionAction test = new TestConnectionAction();
		try {
			DalTransactionManager tranManager = new DalTransactionManager(getDalConnectionManager());
			tranManager.doInTransaction(test, new DalHints());
			assertNotNull(test.entry.getTag().get(LogEntry.TAG_DATABASE_NAME));
			//assertNotNull(test.entry.getTag().get(LogEntry.TAG_USER_NAME)); be removed
			//assertNotNull(test.entry.getTag().get(LogEntry.TAG_SERVER_ADDRESS)); be removed
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testStart() {
		TestConnectionAction test = new TestConnectionAction();
		test.start();
		assertTrue(test.start > 0);
	}

	@Test
	public void testEnd() {
		TestConnectionAction test = new TestConnectionAction();
		test.start();
		test.initLogEntry(logicDbName, new DalHints());
		Object result = null;
		Throwable e = null;
		try {
			Thread.sleep(10l);
			test.end(result, e);
			String msStr = test.entry.getTag().get(LogEntry.TAG_DURATION_TIME);
			long ms = Long.parseLong(msStr.substring(0, msStr.length() - 2));
			assertTrue( ms >= 10);
			assertEquals(0, Long.parseLong(test.entry.getTag().get(LogEntry.TAG_RECORD_COUNT)));
		} catch (Exception e1) {
			e1.printStackTrace();
			fail("There should be no exception here");
		}
		test.entry.getTag().get(LogEntry.TAG_RECORD_COUNT);
		assertTrue(test.start > 0);
	}

	@Test
	public void testCleanup() {
		try {
			TestConnectionAction test = new TestConnectionAction();
			test.connHolder = getDalConnection();
			test.statement = test.connHolder.getConn().createStatement();
			test.rs = test.statement.executeQuery("select * from City");
			test.rs.next();
			test.cleanup();
			assertNotNull(test);
			assertTrue(test.conn == null);
			assertTrue(test.statement == null);
			assertTrue(test.rs == null);
			assertTrue(test.connHolder == null);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail("There should be no exception here");
		}
	}
	
	private static class TestConnectionAction extends ConnectionAction<Object>{
		@Override
		public Object execute() throws Exception {
			return null;
		}
	}
}
