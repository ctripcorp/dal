package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.base.MockConnectionAction;
import com.ctrip.platform.dal.exceptions.TransactionSystemException;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DalTransactionManagerTest {
	
	private static final String logicDbName = "dao_test_sqlsvr";
	
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

	private static CustomDalConnectionManager getCustomDalConnectionManager() throws Exception {
		return new CustomDalConnectionManager(logicDbName, DalClientFactory.getDalConfigure());
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
					Assert.assertNotNull(test.getConnection(hints, new MockConnectionAction(DalEventEnum.BATCH_CALL)));
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
	public void testGetLogicDbName() {
		final DalHints hints = new DalHints();
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			assertFalse(DalTransactionManager.isInTransaction());
			assertNull(DalTransactionManager.getLogicDbName());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					assertNotNull(DalTransactionManager.getCurrentDbMeta());
					assertEquals(logicDbName, DalTransactionManager.getLogicDbName());
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

	@Test
	public void testDoInTransactionCommitFail() {
		final DalHints hints = new DalHints();
		try {
			final DalTransactionManager test = new DalTransactionManager(getCustomDalConnectionManager());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute(){
					return new Object();
				}
			};
			test.doInTransaction(action, hints);
			fail();
		} catch (Exception e) {
		    e.printStackTrace();
            if (!(e instanceof TransactionSystemException)) {
                fail();
            }
		}
	}

	@Test
	public void testRegister() {
		final DalHints hints = new DalHints();
		final DalTransactionListener testListener = new DalTransactionListener(){
			@Override
			public void beforeCommit() {
			}

			@Override
			public void beforeRollback() {
			}

			@Override
			public void afterCommit() {
			}

			@Override
			public void afterRollback() {
			}			
		};
		
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			
			DalTransactionManager.register(testListener);
			fail();
		} catch (Exception e) {
		}
		
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					DalTransactionManager.register(testListener);
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, hints);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testGetCurrentListeners() {
		final DalHints hints = new DalHints();
		final DalTransactionListener testListener = new DalTransactionListener(){
			@Override
			public void beforeCommit() {
			}

			@Override
			public void beforeRollback() {
			}

			@Override
			public void afterCommit() {
			}

			@Override
			public void afterRollback() {
			}			
		};
		
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			
			DalTransactionManager.getCurrentListeners();
			fail();
		} catch (Exception e) {
		}
		
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					DalTransactionManager.register(testListener);
					DalTransactionManager.register(testListener);
					Assert.assertEquals(2, DalTransactionManager.getCurrentListeners().size());
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, hints);
			try {
				DalTransactionManager.getCurrentListeners();
				fail();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCommitListeners() {
		final DalHints hints = new DalHints();
		final DalTransactionListener testListener = new DalTransactionListener(){
			@Override
			public void beforeCommit() throws SQLException {
				Assert.assertTrue(DalTransactionManager.isInTransaction());
				DalClient c = DalClientFactory.getClient(DalTransactionManager.getLogicDbName());
				c.query("SELECT 1", new StatementParameters(), new DalHints(), new DalResultSetExtractor<Object>() {
					@Override
					public Object extract(ResultSet rs) throws SQLException {
						return null;
					}
				});
			}

			@Override
			public void beforeRollback() {
				fail();
			}

			@Override
			public void afterCommit() {
				Assert.assertFalse(DalTransactionManager.isInTransaction());
			}

			@Override
			public void afterRollback() {
				fail();
			}			
		};
		
		final DalTransactionListener testListener1 = new DalTransactionListener(){
			@Override
			public void beforeCommit() throws SQLException {
				Assert.assertTrue(DalTransactionManager.isInTransaction());
				DalCommand cmd = new DalCommand() {
					@Override
					public boolean execute(DalClient client) throws SQLException {
						client.query("SELECT 1", new StatementParameters(), new DalHints(), new DalResultSetExtractor<Object>() {
							@Override
							public Object extract(ResultSet rs) throws SQLException {
								return null;
							}
						});
						return false;
					}
				};
				
				DalClientFactory.getClient(DalTransactionManager.getLogicDbName()).execute(cmd, new DalHints());
			}

			@Override
			public void beforeRollback() {
				fail();
			}

			@Override
			public void afterCommit() {
				Assert.assertFalse(DalTransactionManager.isInTransaction());
			}

			@Override
			public void afterRollback() {
				fail();
			}			
		};
		
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					DalTransactionManager.register(testListener);
					DalTransactionManager.register(testListener1);
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, hints);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRollbackListeners() {
		final DalHints hints = new DalHints();
		final DalTransactionListener testListener = new DalTransactionListener(){
			@Override
			public void beforeCommit() {
			}

			@Override
			public void beforeRollback() {
				Assert.assertTrue(DalTransactionManager.isInTransaction());
			}

			@Override
			public void afterCommit() {
				fail();
			}

			@Override
			public void afterRollback() {
				Assert.assertFalse(DalTransactionManager.isInTransaction());
			}			
		};
		
		final DalTransactionListener testListener1 = new DalTransactionListener(){
			@Override
			public void beforeCommit() throws SQLException {
				throw new SQLException();
			}

			@Override
			public void beforeRollback() {
				Assert.assertTrue(DalTransactionManager.isInTransaction());
			}

			@Override
			public void afterCommit() {
				fail();
			}

			@Override
			public void afterRollback() {
				Assert.assertFalse(DalTransactionManager.isInTransaction());
			}			
		};
		
		try {
			final DalTransactionManager test = new DalTransactionManager(getDalConnectionManager());
			ConnectionAction<?> action = new ConnectionAction<Object>() {
				public Object execute() throws Exception {
					DalTransactionManager.register(testListener);
					// The 2nd listener will cause transaction rollback
					DalTransactionManager.register(testListener1);
					return null;
				}
			};
			action.operation = DalEventEnum.EXECUTE;
			test.doInTransaction(action, hints);
			fail();
		} catch (Exception e) {
		}
	}
}
