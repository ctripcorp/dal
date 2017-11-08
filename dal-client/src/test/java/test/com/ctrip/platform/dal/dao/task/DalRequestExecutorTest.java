package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.ctrip.platform.dal.dao.task.DalRequest;
import com.ctrip.platform.dal.dao.task.DalRequestExecutor;

public class DalRequestExecutorTest {
	private class TestDalRequest implements DalRequest<Integer> {
		private SQLException e; 
		public Integer[] values;
		
		private TestDalRequest(SQLException e, Integer[] values) {
			this.e = e;
			this.values = values;
		}
		
		@Override
		public void validate() throws SQLException {
			if(e!= null)
				throw e;
		}

		@Override
		public boolean isCrossShard() throws SQLException {
			return values.length > 1;
		}

		@Override
		public Callable<Integer> createTask() throws SQLException {
			return createInternalTask(values[0]);
		}

		@Override
		public Map<String, Callable<Integer>> createTasks() throws SQLException {
			Map<String, Callable<Integer>> tasks = new HashMap<>();
			
			for(int i = 0; i < values.length; i++) {
				final int k = values[i];
				tasks.put(String.valueOf(i), createInternalTask(k));
			}
				
			return tasks;
		}
		
		public Callable<Integer> createInternalTask(final Integer k) throws SQLException {
		    return new Callable<Integer>() {
		        public Integer call() throws Exception {
		            return k;
		        }
		    };
		}

		@Override
		public ResultMerger<Integer> getMerger() {
			return new ResultMerger.IntSummary();
		}

        @Override
        public String getCaller() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isAsynExecution() {
            // TODO Auto-generated method stub
            return false;
        }
	}
	
    static ConcurrentHashMap<String, Object> all = new ConcurrentHashMap<>();
    
    private class TestThreadPoolDalRequest extends TestDalRequest {
        TestThreadPoolDalRequest(int size) {
            super(null, null);
            values = new Integer[size];
            for(int i = 0; i < size; i++)
                values[i] = i;
            
        }
        
        private boolean sleep;
        
        public Callable<Integer> createInternalTask(final Integer k) throws SQLException {
            return new Callable<Integer>() {
                public Integer call() throws Exception {
                    all.put(Thread.currentThread().getName(), 1);
                    if(sleep)
                        Thread.sleep(1000);
                    return k;
                }
            };
        }
    }
    
    @Before
    public void setUp() {
        try{
            DalRequestExecutor.init(null, null);
        }catch(Throwable e) {
            fail();
        }
    }

    @After
	public void teardown() {
		try{
			DalRequestExecutor.shutdown();
		}catch(Throwable e) {
			fail();
		}
	}

	@Test
	public void testExecuteFailByValidate() {
		DalRequestExecutor test = new DalRequestExecutor();
		SQLException ex = new SQLException("Test");
		TestDalRequest request = new TestDalRequest(ex, null);
		DalHints hints = new DalHints();
		
		try {
			test.execute(hints, request);
			fail();
		} catch (SQLException e) {
			assertEquals(ex, e);
		}
	}

	@Test
	public void testExecuteFailByValidateAsync() {
		DalRequestExecutor test = new DalRequestExecutor();
		SQLException ex = new SQLException("Test");
		TestDalRequest request = new TestDalRequest(ex, null);
		DalHints hints = new DalHints().asyncExecution();
		
		try {
			assertNull(test.execute(hints, request));
			Future<?> result = hints.getAsyncResult();
			result.get();
			fail();
		} catch (SQLException | InterruptedException | ExecutionException e) {
			assertEquals(ex, ((ExecutionException)e).getCause());
		}
	}

	@Test
	public void testExecuteNotCrossShard() {
		DalRequestExecutor test = new DalRequestExecutor();
		TestDalRequest request = new TestDalRequest(null, new Integer[]{1});
		DalHints hints = new DalHints();
		
		try {
			Integer result = test.execute(hints, request);
			assertEquals(1, result.intValue());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testExecuteCrossShard() {
		DalRequestExecutor test = new DalRequestExecutor();
		TestDalRequest request = new TestDalRequest(null, new Integer[]{1, 2});
		DalHints hints = new DalHints();
		
		try {
			Integer result = test.execute(hints, request);
			assertEquals(3, result.intValue());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testExecuteNullable() {
		DalRequestExecutor test = new DalRequestExecutor();
		TestDalRequest request = new TestDalRequest(null, new Integer[]{null});
		DalHints hints = new DalHints();
		
		try {
			Integer result = test.execute(hints, request, true);
			assertNull(result);
		} catch (Exception e) {
			fail();
		}

		try {
			test.execute(hints, request, false);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testExecuteCallback() {
		DalRequestExecutor test = new DalRequestExecutor();
		TestDalRequest request = new TestDalRequest(null, new Integer[]{1});
		DalHints hints = new DalHints();
		DefaultResultCallback callback = new DefaultResultCallback();
		
		try {
			Integer result = test.execute(hints.callbackWith(callback), request, true);
			assertNull(result);
			assertEquals(1, ((Integer)hints.getAsyncResult().get()).intValue());
			assertEquals(1, ((Integer)callback.getResult()).intValue());
		} catch (Exception e) {
			fail();
		}
	}

    @Test
    public void testThreadPoolFeature() {
        DalRequestExecutor.shutdown();
        DalRequestExecutor.init("10", null);
        DalRequestExecutor test = new DalRequestExecutor();
        TestDalRequest request = new TestThreadPoolDalRequest(50);
        DalHints hints = new DalHints();

        try {
            all.clear();
            test.execute(hints, request, true);
            assertEquals(10, all.keySet().size());
        } catch (Exception e) {
            fail();
        }
        
        
        all.clear();
        DalRequestExecutor.shutdown();
        DalRequestExecutor.init("200", null);
        test = new DalRequestExecutor();
        request = new TestThreadPoolDalRequest(1000);
        hints = new DalHints();

        try {
            test.execute(hints, request, true);
            assertEquals(200, all.keySet().size());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testThreadPoolFeatureCooldown() {
        DalRequestExecutor.shutdown();
        DalRequestExecutor.init("10", "10");
        DalRequestExecutor test = new DalRequestExecutor();
        System.out.println(test.getPoolSize());
        TestDalRequest request = new TestThreadPoolDalRequest(50);
        DalHints hints = new DalHints();

        try {
            all.clear();
            test.execute(hints, request, true);
            assertEquals(10, test.getPoolSize());
            assertEquals(10, all.keySet().size());
            assertEquals(10, test.getPoolSize());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Thread.sleep(9*1000);
            System.out.println(test.getPoolSize());
            
            Thread.sleep(1*1000);
            System.out.println(test.getPoolSize());
            
            request = new TestThreadPoolDalRequest(5);
            test.execute(hints, request, true);
            System.out.println(test.getPoolSize());
            assertTrue(test.getPoolSize() < 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testThreadPoolFeatureMax() {
        DalRequestExecutor.shutdown();
        DalRequestExecutor.init("1000", "10");
        DalRequestExecutor test = new DalRequestExecutor();
        System.out.println(test.getPoolSize());
        TestDalRequest request = new TestThreadPoolDalRequest(20000);
        DalHints hints = new DalHints();

        try {
            all.clear();
            System.out.println("Start");
            long start = System.currentTimeMillis();
            test.execute(hints, request, true);
            start = System.currentTimeMillis() - start;
            System.out.println(start + "ms");
            assertEquals(1000, all.keySet().size());
            assertEquals(1000, test.getPoolSize());
        } catch (Exception e) {
            fail();
        }
        
        try {
            Thread.sleep(9*1000);
            System.out.println("after 9s: " + test.getPoolSize());
            
            Thread.sleep(1*1000);
            System.out.println("after 10s: " + test.getPoolSize());
            
            request = new TestThreadPoolDalRequest(5);
            test.execute(hints, request, true);
            System.out.println(test.getPoolSize());
            assertTrue(test.getPoolSize() < 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testThreadPoolCheckCapacity() {
        DalRequestExecutor.shutdown();
        DalRequestExecutor.init("1000", "1");
        DalRequestExecutor test = new DalRequestExecutor();
        
        testCapacity(test, 100, 1000);
        testCapacity(test, 200, 1000);
        testCapacity(test, 300, 1000);
        
        testCapacity(test, 500, 1000);
        
        testCapacity(test, 1000, 1000);
        
        testCapacity(test, 1500, 2000);
        
        testCapacity(test, 2000, 2000);
        
        testCapacity(test, 10000, 10000);
    }
    
    public void testCapacity(DalRequestExecutor test, int size, int cost) {
        int delta = 200;
        TestThreadPoolDalRequest request = new TestThreadPoolDalRequest(size);
        request.sleep = true;
        DalHints hints = new DalHints();

        try {
            System.out.print("Size: " + size + " Cost: " + cost + " pool size: " + test.getPoolSize());
            all.clear();
            long start = System.currentTimeMillis();
            test.execute(hints, request, true);
            start = System.currentTimeMillis() - start;
            System.out.println(" Actual cost: " + start + "ms");
            assertTrue(start - cost < delta);
            Thread.sleep(1500);
        } catch (Exception e) {
            fail();
        }
    }
}
