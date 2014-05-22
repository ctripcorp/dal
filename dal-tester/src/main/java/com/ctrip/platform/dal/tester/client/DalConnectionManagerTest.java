package com.ctrip.platform.dal.tester.client;

import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
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
	public void testCleanup() {
		DalConfigure config = null;
		boolean useMaster = true;
		DalHints hints = new DalHints();
		
		try {
			config = DalConfigureFactory.load();
			
			DalConnectionManager test = new DalConnectionManager(logicDbName, config);
			DalConnection conn = test.getNewConnection(hints, useMaster, DalEventEnum.BATCH_CALL);
			
			Statement statement = conn.getConn().createStatement();
			ResultSet rs = statement.executeQuery("select * from City");
			rs.next();
			
//			test.cleanup(hints, rs, statement, conn);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
//	@Test
//	public void testCloseConnection() {
//		DalConfigure config = null;
//		boolean useMaster = true;
//		DalHints hints = new DalHints();
//		
//		try {
//			config = DalConfigureFactory.load();
//			
//			DalConnectionManager test = new DalConnectionManager(logicDbName, config);
//			ConnectionHolder conn = test.getNewConnection(hints, useMaster, DalEventEnum.BATCH_CALL);
//			
//			Statement statement = conn.getConn().createStatement();
//			ResultSet rs = statement.executeQuery("select * from City");
//			rs.next();
//			
//			test.cleanup(hints, rs, statement, conn.getConn());
//			Integer oldLevel = hints.getInt(DalHintEnum.oldIsolationLevel);
//			Assert.assertNotNull(oldLevel);
//			DalConnectionManager.closeConnection(oldLevel, conn.getConn());
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail();
//		}
//	}
}
