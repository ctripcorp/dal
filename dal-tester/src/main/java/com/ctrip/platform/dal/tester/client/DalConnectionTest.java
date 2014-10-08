package com.ctrip.platform.dal.tester.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DbMeta;
import com.ctrip.platform.dal.sql.logging.LogEntry;

public class DalConnectionTest {
	private static final String logicDbName = "HtlOvsPubDB_INSERT_1";
	
	@Test
	public void testDalConnection() throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.createIfAbsent(logicDbName, null, true, conn));
			assertNotNull(test);
		} catch (Throwable e){
			fail();
			e.printStackTrace();
		}finally{
			if(conn != null)
				conn.close();
		}
	}

	@Test
	public void testGetConn() throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.createIfAbsent(logicDbName, null, true, conn));
			assertNotNull(test.getConn());
		} catch (Throwable e){
			fail();
			e.printStackTrace();
		}finally{
			if(conn != null)
				conn.close();
		}
	}

	@Test
	public void testGetMeta() throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.createIfAbsent(logicDbName, null, true, conn));
			assertNotNull(test.getMeta());
			LogEntry entry = new LogEntry();
			
			assertNotNull(test.getMeta());
			test.getMeta().populate(entry); 
			assertNotNull(test.getMeta());
		} catch (Throwable e){
			fail();
			e.printStackTrace();
		}finally{
			if(conn != null)
				conn.close();
		}
	}

	@Test
	public void testGetCatalog()throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.createIfAbsent(logicDbName, null, true, conn));
			assertNotNull(test.getDatabaseName());
		} catch (Throwable e){
			fail();
			e.printStackTrace();
		}finally{
			if(conn != null)
				conn.close();
		}
	}

	@Test
	public void testSetAutoCommit() throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.createIfAbsent(logicDbName, null, true, conn));
			test.setAutoCommit(false);
			assertFalse(conn.getAutoCommit());
			test.setAutoCommit(true);
			assertTrue(conn.getAutoCommit());
		} catch (Throwable e){
			fail();
			e.printStackTrace();
		}finally{
			if(conn != null)
				conn.close();
		}
	}

	@Test
	public void testApplyHints() throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.createIfAbsent(logicDbName, null, true, conn));
			DalHints hints = new DalHints();
			hints.setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE);
			test.applyHints(hints);
			assertTrue(conn.getTransactionIsolation() == Connection.TRANSACTION_SERIALIZABLE);
			
			hints.setIsolationLevel(Connection.TRANSACTION_NONE);
			test.applyHints(hints);
			assertTrue(conn.getTransactionIsolation() == Connection.TRANSACTION_NONE);
		} catch (Throwable e){
			fail();
			e.printStackTrace();
		}finally{
			if(conn != null)
				conn.close();
		}
	}

	@Test
	public void testClose() throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.createIfAbsent(logicDbName, null, true, conn));

			Statement statement = test.getConn().createStatement();
			ResultSet rs = statement.executeQuery("select * from City");
			rs.next();

			test.close();
			assertTrue(conn.isClosed());
		} catch (Throwable e){
			fail();
			e.printStackTrace();
		}finally{
			if(conn != null)
				conn.close();
		}
	}
}
