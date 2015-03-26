package com.ctrip.platform.dal.tester.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;

import org.junit.Test;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DalConnectionManager;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import com.ctrip.platform.dal.dao.client.DbMeta;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.sql.logging.CtripLogEntry;

public class ConnectionActionTest {
	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final String connectionString = "HotelPubDB";
	
	private DalConnection getDalConnection() throws Exception {
		Connection conn = null;
		conn = DataSourceLocator.newInstance().getDataSource(connectionString).getConnection();
		return new DalConnection(conn, DbMeta.createIfAbsent(connectionString, null, null, true, conn));
	}
	
	private static DalConnectionManager getDalConnectionManager() throws Exception {
		return new DalConnectionManager(connectionString, DalConfigureFactory.load());
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
			assertNotNull(test.entry.getDatabaseName());
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
//			assertNotNull(test.entry.getDatabaseName());
			assertNotNull(((CtripLogEntry)test.entry).getTag().get(CtripLogEntry.TAG_DATABASE_NAME));
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
		test.initLogEntry(connectionString, new DalHints());
		Object result = null;
		Throwable e = null;
		try {
			Thread.sleep(10l);
			test.end(result, e);
			assertTrue( test.entry.getDuration() >= 10);
			assertEquals(0, test.entry.getResultCount());
		} catch (Exception e1) {
			e1.printStackTrace();
			fail("There should be no exception here");
		}
		assertTrue(test.start > 0);
	}

	@Test
	public void testCleanup() {
		try {
			TestConnectionAction test = new TestConnectionAction();
			test.connHolder = getDalConnection();
			test.statement = test.connHolder.getConn().createStatement();
			test.rs = test.statement.executeQuery("select * from Hotel");
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
