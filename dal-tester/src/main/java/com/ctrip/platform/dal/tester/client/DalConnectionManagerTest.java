package com.ctrip.platform.dal.tester.client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DalConnectionManager;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.dao.logging.DalEventEnum;

public class DalConnectionManagerTest {
	private static final String logicDbName = "HtlOvsPubDB_INSERT_1";
	
	@Test
	public void testGetNewConnection() {
		DalConfigure config = null;
		boolean useMaster = true;
		DalHints hints = new DalHints();
		
		try {
			config = DalConfigureFactory.load();
			
			DalConnectionManager test = new DalConnectionManager(logicDbName, config);
			DalConnection conn = test.getNewConnection(hints, useMaster, DalEventEnum.BATCH_CALL);
			Assert.assertNotNull(conn);
			Assert.assertNotNull(conn.getConn());
			conn.getConn().close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testDoInConnection() {
		DalConfigure config = null;
		final boolean useMaster = true;
		final DalHints hints = new DalHints();
		
		try {
			config = DalConfigureFactory.load();
			
			final DalConnectionManager test = new DalConnectionManager(logicDbName, config);
			ConnectionAction<Object> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					DalConnection conn = test.getNewConnection(hints, useMaster, DalEventEnum.BATCH_CALL);
					
					Statement statement = conn.getConn().createStatement();
					ResultSet rs = statement.executeQuery("select * from City");
					rs.next();
					return null;
				}
			};
			test.doInConnection(action, hints);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testDoInConnectionException() {
		DalConfigure config = null;
		final boolean useMaster = true;
		final DalHints hints = new DalHints();
		final String message  = "testDoInConnectionException";
		final Connection connTest;
		
		try {
			config = DalConfigureFactory.load();
			
			final DalConnectionManager test = new DalConnectionManager(logicDbName, config);
			ConnectionAction<Object> action = new ConnectionAction<Object>() {
				public Connection connTest;
				public Object execute() throws Exception {
					DalConnection conn = test.getNewConnection(hints, useMaster, DalEventEnum.BATCH_CALL);
					connTest = conn.getConn();
					
					Statement statement = conn.getConn().createStatement();
					ResultSet rs = statement.executeQuery("select * from City");
					rs.next();
					throw new RuntimeException(message);
				}
			};
			
			test.doInConnection(action, hints);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
