package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import org.junit.*;
import com.ctrip.platform.dal.dao.task.SqlServerTestInitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class DalConnectionTest {
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

	private static final String connectionString = "dao_test_sqlsvr";
	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private DalConnection getConnection() throws Exception {
		Connection conn = DalClientFactory.getDalConfigure().getLocator().getConnection(connectionString);
		return new DalConnection(conn, true, null, DbMeta.createIfAbsent(connectionString, null, conn));
	}
	
	@Test
	public void testDalConnection() throws SQLException {
		Connection conn = null;
		try {
			DalConnection test = getConnection();
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
			DalConnection test = getConnection();
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
			DalConnection test = getConnection();
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
			DalConnection test = getConnection();
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
			DalConnection test = getConnection();
			test.setAutoCommit(false);
			conn = test.getConn();
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
			DalConnection test = getConnection();
			DalHints hints = new DalHints();
			hints.setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE);
			test.applyHints(hints);
			conn = test.getConn();
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
			DalConnection test = getConnection();

			Statement statement = test.getConn().createStatement();
			ResultSet rs = statement.executeQuery("select * from " + SqlServerTestInitializer.TABLE_NAME);
			rs.next();

			conn = test.getConn();
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
