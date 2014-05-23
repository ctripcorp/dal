package com.ctrip.platform.dal.tester.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DbMeta;

public class DalConnectionTest {
	private static final String logicDbName = "HtlOvsPubDB_INSERT_1";
	
	@Test
	public void testDalConnection() throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.getDbMeta(logicDbName, conn));
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
			DalConnection test = new DalConnection(conn, DbMeta.getDbMeta(logicDbName, conn));
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
	public void testGetOldIsolationLevel() throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.getDbMeta(logicDbName, conn));
			test.getOldIsolationLevel();
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
			DalConnection test = new DalConnection(conn, DbMeta.getDbMeta(logicDbName, conn));
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
			DalConnection test = new DalConnection(conn, DbMeta.getDbMeta(logicDbName, conn));
			assertNotNull(test.getCatalog());
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
			DalConnection test = new DalConnection(conn, DbMeta.getDbMeta(logicDbName, conn));
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
	public void testApplyHints() {
//		fail("Not yet implemented");
	}

	@Test
	public void testClose() throws SQLException {
		Connection conn = null;
		try {
			conn = DataSourceLocator.newInstance().getDataSource(logicDbName).getConnection();
			DalConnection test = new DalConnection(conn, DbMeta.getDbMeta(logicDbName, conn));
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
