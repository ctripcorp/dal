package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class DalSingleTaskRequestTest {
	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
		}
	}
	private class TestSingleTask implements SingleTask<Integer>{

		@Override
		public void initialize(DalParser<Integer> parser) {
		}

		@Override
		public void initTaskSettings(Map<String, String> settings) {
		}

		@Override
		public List<Map<String, ?>> getPojosFields(List<Integer> daoPojos) {
			List<Map<String, ?>> maps = new ArrayList<>(); 
			for(Integer i: daoPojos){
				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put("id", i);
				maps.add(map);
			}
			return maps;
		}

		@Override
		public int execute(DalHints hints, Map<String, ?> daoPojo, Integer a, DalTaskContext dalTaskContext)
				throws SQLException {
			return (Integer)daoPojo.values().iterator().next();
		}

		@Override
		public DefaultTaskContext createTaskContext() throws SQLException {
			DefaultTaskContext taskContext = new DefaultTaskContext();
			return taskContext;
		}
	}
	
	@Test
	public void testDalSingleTaskRequest() {
		DalSingleTaskRequest<Integer> test = new DalSingleTaskRequest<>("", new DalHints(), 1, new TestSingleTask());
		List<Integer> pojos = new ArrayList<>();
		test = new DalSingleTaskRequest<>("", new DalHints(), pojos, new TestSingleTask());
	}

	@Test
	public void testValidateAndPrepare() {
		DalSingleTaskRequest<Integer> test = null;
		try {
			Integer i = null;
			test = new DalSingleTaskRequest<>("", new DalHints(), i, new TestSingleTask());
			test.validateAndPrepare();
			fail();
		} catch (SQLException e) {
		}

		try {
			List<Integer> pojos = null;
			test = new DalSingleTaskRequest<>("", new DalHints(), pojos, new TestSingleTask());
			test.validateAndPrepare();
			fail();
		} catch (SQLException e) {
		}

		try {
			List<Integer> pojos = new ArrayList<>();
			test = new DalSingleTaskRequest<>("", new DalHints(), pojos, (TestSingleTask)null);
			test.validateAndPrepare();
			fail();
		} catch (SQLException e) {
		}

		try {
			Integer i = 1;
			test = new DalSingleTaskRequest<>("dao_test_sqlsvr_dbShard", new DalHints(), i, new TestSingleTask());
			test.validateAndPrepare();
		} catch (SQLException e) {
			fail();
		}

		try {
			List<Integer> pojos = new ArrayList<>();
			test = new DalSingleTaskRequest<>("dao_test_sqlsvr_dbShard", new DalHints(), pojos, new TestSingleTask());
			test.validateAndPrepare();
		} catch (SQLException e) {
			fail();
		}
	}

	@Test
	public void testIsCrossShard() {
		DalSingleTaskRequest<Integer> test = null;
		List<Integer> pojos = new ArrayList<>();
		test = new DalSingleTaskRequest<>("dao_test_sqlsvr_dbShard", new DalHints(), pojos, new TestSingleTask());
		assertFalse(test.isCrossShard());
	}

	@Test
	public void testCreateTask() {
		DalSingleTaskRequest<Integer> test = null;
		test = new DalSingleTaskRequest<>("dao_test_sqlsvr_dbShard", new DalHints(), 1, new TestSingleTask());
		try {
			test.validateAndPrepare();
			Callable<int[]> task = test.createTask();
			assertNotNull(task);
			assertArrayEquals(new int[]{1}, task.call());
		} catch (Exception e1) {
			fail();
		}

		try {
			List<Integer> pojos = new ArrayList<>();
			pojos.add(1);
			pojos.add(2);
			test = new DalSingleTaskRequest<>("dao_test_sqlsvr_dbShard", new DalHints(), pojos, new TestSingleTask());
			test.validateAndPrepare();
			Callable<int[]> task = test.createTask();
			assertNotNull(task);
			assertArrayEquals(new int[]{1, 2}, task.call());
		} catch (Exception e1) {
			fail();
		}
		
	}

	@Test
	public void testCreateTasks() {
		DalSingleTaskRequest<Integer> test = null;
		List<Integer> pojos = new ArrayList<>();
		test = new DalSingleTaskRequest<>("dao_test_sqlsvr_dbShard", new DalHints(), pojos, new TestSingleTask());
		try {
			test.createTasks();
			fail();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testGetMerger() {
		DalSingleTaskRequest<Integer> test = null;
		List<Integer> pojos = new ArrayList<>();
		test = new DalSingleTaskRequest<>("dao_test_sqlsvr_dbShard", new DalHints(), pojos, new TestSingleTask());
		assertNull(test.getMerger());
	}
}
