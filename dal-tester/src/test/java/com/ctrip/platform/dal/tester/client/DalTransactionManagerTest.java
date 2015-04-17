package com.ctrip.platform.dal.tester.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalConnectionManager;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;

public class DalTransactionManagerTest {
	
	private static final String logicDbName = "HtlOvsPubDB_INSERT_1";
	
	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static DalConnectionManager getDalConnectionManager() throws Exception {
		return new DalConnectionManager(logicDbName, DalClientFactory.getDalConfigure());
	}
	
	@Test
	public void testDalTransactionManager() {
		try {
			DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test
	public void testIsInTransaction() {
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			assertFalse(DalTransactionManager.isInTransaction());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					Assert.assertTrue(DalTransactionManager.isInTransaction());
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, new DalHints());
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test
	public void testGetConnection() {
		final DalHints hints = new DalHints();
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			assertFalse(DalTransactionManager.isInTransaction());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					Assert.assertNotNull(test.getConnection(hints, DalEventEnum.BATCH_CALL));
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, hints);
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test
	public void testGetCurrentDbMeta() {
		final DalHints hints = new DalHints();
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			assertFalse(DalTransactionManager.isInTransaction());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					assertNotNull(DalTransactionManager.getCurrentDbMeta());
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, hints);
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
		assertFalse(DalTransactionManager.isInTransaction());
		assertNull(DalTransactionManager.getCurrentDbMeta());
	}

	@Test
	public void testClearCurrentTransaction() {
		final DalHints hints = new DalHints();
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			assertFalse(DalTransactionManager.isInTransaction());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					assertNotNull(DalTransactionManager.getCurrentDbMeta());
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, hints);
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
		assertFalse(DalTransactionManager.isInTransaction());
		assertNull(DalTransactionManager.getCurrentDbMeta());
	}

	@Test
	public void testDoInTransaction() {
		final DalHints hints = new DalHints();
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			assertFalse(DalTransactionManager.isInTransaction());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					assertNotNull(DalTransactionManager.getCurrentDbMeta());
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, hints);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertFalse(DalTransactionManager.isInTransaction());
		assertNull(DalTransactionManager.getCurrentDbMeta());
	}

	@Test
	public void testDoInTransactionFail() {
		final DalHints hints = new DalHints();
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			assertFalse(DalTransactionManager.isInTransaction());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					throw new NullPointerException("test");
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, hints);
			fail();
		} catch (Exception e) {
			assertFalse(DalTransactionManager.isInTransaction());
			e.printStackTrace();
		}
	}
}
