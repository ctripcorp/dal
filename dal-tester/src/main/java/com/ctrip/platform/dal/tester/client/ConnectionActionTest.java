package com.ctrip.platform.dal.tester.client;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DbMeta;

public class ConnectionActionTest {
	private static final String logicDbName = "HtlOvsPubDB_INSERT_1";
	
	public DalConnection getDalConnection() throws Exception {
		Connection conn = null;
		conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
		return new DalConnection(conn, DbMeta.getDbMeta(logicDbName, conn));
	}
	
	@Test
	public void testInitLogEntry() {
		TestConnectionAction test = new TestConnectionAction();
		test.initLogEntry("Test", new DalHints());
		assertNotNull(test.entry);
		test.populateDbMeta();
	}

	@Test
	public void testPopulateDbMetaOutofTransaction() throws Exception {
		TestConnectionAction test = new TestConnectionAction();
		test.initLogEntry("Test", new DalHints());
		test.connHolder = getDalConnection();
		test.populateDbMeta();
		fail("Not yet implemented");
	}
	
	@Test
	public void testPopulateDbMetaInTransaction() {
		TestConnectionAction test = new TestConnectionAction();
		test.initLogEntry("Test", new DalHints());
		test.populateDbMeta();
		fail("Not yet implemented");
	}

	@Test
	public void testStart() {
		fail("Not yet implemented");
	}

	@Test
	public void testEnd() {
		fail("Not yet implemented");
	}

	@Test
	public void testCleanup() {
		fail("Not yet implemented");
	}
	
	private static class TestConnectionAction extends ConnectionAction<Object>{

		@Override
		public Object execute() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
