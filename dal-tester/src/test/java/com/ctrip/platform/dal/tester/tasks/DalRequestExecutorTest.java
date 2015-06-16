package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.junit.AfterClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultCallback;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.ctrip.platform.dal.dao.task.DalRequest;
import com.ctrip.platform.dal.dao.task.DalRequestExecutor;

public class DalRequestExecutorTest {
	private class TestDalRequest implements DalRequest<Integer> {
		private SQLException e; 
		private Integer[] values;
		
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
			return new Callable<Integer>() {
				public Integer call() throws Exception {
					return values[0];
				}
			};
		}

		@Override
		public Map<String, Callable<Integer>> createTasks() throws SQLException {
			Map<String, Callable<Integer>> tasks = new HashMap<>();
			
			for(int i = 0; i < values.length; i++) {
				final int k = values[i];
				tasks.put(String.valueOf(i), new Callable<Integer>() {
					public Integer call() throws Exception {
						return k;
					}
				});
			}
				
			return tasks;
		}

		@Override
		public ResultMerger<Integer> getMerger() {
			return new ResultMerger.IntSummary();
		}
	}
	
	@AfterClass
	public static void testShutdownAsyncTaskExecutor() {
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
}
