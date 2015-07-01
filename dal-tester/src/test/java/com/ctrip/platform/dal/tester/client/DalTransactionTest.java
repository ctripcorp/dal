package com.ctrip.platform.dal.tester.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DalTransaction;
import com.ctrip.platform.dal.dao.client.DbMeta;
import com.ctrip.platform.dal.tester.tasks.SqlServerTestInitializer;

public class DalTransactionTest {
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

	private static final String logicDbName = "dao_test_sqlsvr";

	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DalConnection getDalConnection() throws Exception {
		Connection conn = null;
		conn = DalClientFactory.getDalConfigure().getLocator().getConnection(logicDbName);
		return new DalConnection(conn, DbMeta.createIfAbsent(logicDbName, null, null, true, conn));
	}

	@Test
	public void testDalTransaction() {
		DalTransaction test = null;
		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			assertNotNull(test);
			assertFalse(test.getConnection().getConn().getAutoCommit());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}finally {
			if(test != null && test.getConnection() != null)
				test.getConnection().close();
		}
	}

	@Test
	public void testValidate() {
		DalTransaction test = null;
		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			test.validate(logicDbName);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}finally {
			if(test != null && test.getConnection() != null)
				test.getConnection().close();
		}
			
		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			test.validate("invalid");
			fail();
		} catch (Throwable e) {
		}finally {
			if(test != null && test.getConnection() != null)
				test.getConnection().close();
		}
	}

	@Test
	public void testGetConnection() {
		DalTransaction test = null;
		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			assertNotNull(test.getConnection());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}finally {
			if(test != null && test.getConnection() != null)
				test.getConnection().close();
		}
	}

	@Test
	public void testStartTransaction() {
		DalTransaction test = null;
		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			assertEquals(0, test.getLevel());
			test.startTransaction();
			assertEquals(1, test.getLevel());
			test.startTransaction();
			assertEquals(2, test.getLevel());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}finally {
			if(test != null && test.getConnection() != null)
				test.getConnection().close();
		}
	}

	@Test
	public void testStartOnCompletedTransaction() {
		DalTransaction test = null;
		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			assertEquals(0, test.getLevel());
			int level  = test.startTransaction();
			level  = test.startTransaction();
			test.endTransaction(level--);
			test.endTransaction(level--);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			test.startTransaction();
			fail();
		} catch (Exception e) {
		}finally {
			if(test != null && test.getConnection() != null)
				test.getConnection().close();
		}
	}

	@Test
	public void testEndTransaction() {
		DalTransaction test = null;
		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			assertEquals(0, test.getLevel());
			int level = test.startTransaction();
			assertEquals(1, test.getLevel());
			
			test.endTransaction(level);
			assertEquals(0, test.getLevel());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}finally {
			if(test != null && test.getConnection() != null)
				test.getConnection().close();
		}
	}

	@Test
	public void testEndTransactionIncorrectLevel() {
		DalTransaction test = null;
		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			assertEquals(0, test.getLevel());
			int level = test.startTransaction();
			level = test.startTransaction();
			level = test.startTransaction();
			test.endTransaction(level + 1);
			fail();
		} catch (Exception e) {
		}finally {
			if(test != null && test.getConnection() != null)
				test.getConnection().close();
		}

		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			assertEquals(0, test.getLevel());
			int level = test.startTransaction();
			level = test.startTransaction();
			level = test.startTransaction();
			test.endTransaction(level - 1);
			fail();
		} catch (Exception e) {
		}finally {
			if(test != null && test.getConnection() != null)
				test.getConnection().close();
		}
	}
	
	@Test
	public void testRollbackTransaction() {
		DalTransaction test = null;
		try {
			test = new DalTransaction(getDalConnection(), logicDbName);
			assertEquals(0, test.getLevel());
			test.startTransaction();
			test.startTransaction();
			
			test.rollbackTransaction();
			test.rollbackTransaction();
			test.rollbackTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
