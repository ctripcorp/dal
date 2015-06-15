package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.task.BulkTask;
import com.ctrip.platform.dal.dao.task.BulkTaskResultMerger;
import com.ctrip.platform.dal.dao.task.DalBulkTaskRequest;
import com.ctrip.platform.dal.dao.task.ShardedIntResultMerger;

public class DalBulkTaskRequestTest {
	private class TestPojo {
		Integer index;
		TestPojo(){}
		TestPojo(Integer index){
			this.index = index;
		}
	}
	
	private class TestBulkTask implements BulkTask<Integer, TestPojo> {

		@Override
		public void initialize(DalParser<TestPojo> parser) {
		}

		@Override
		public List<Map<String, ?>> getPojosFields(List<TestPojo> daoPojos) {
			List<Map<String, ?>> daoPojoMaps = new ArrayList<>();
			for(TestPojo pojo: daoPojos) {
				Map<String, Object> value = new HashMap<String, Object>();
				value.put("index", pojo.index);
				daoPojoMaps.add(value);
			}
			return daoPojoMaps;
		}

		@Override
		public Integer getEmptyValue() {
			return 0;
		}

		@Override
		public Integer execute(DalHints hints, Map<Integer, Map<String, ?>> shaffled) throws SQLException {
			return shaffled.size();
		}

		@Override
		public BulkTaskResultMerger<Integer> createMerger() {
			return new ShardedIntResultMerger();
		}
	}

	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testDalBulkTaskRequest() {
		DalBulkTaskRequest<Integer, TestPojo> test = new DalBulkTaskRequest<>("", "", null, new ArrayList<TestPojo>(), new TestBulkTask());
		assertNotNull(test);
	}

	@Test
	public void testValidate() {
		DalBulkTaskRequest<Integer, TestPojo> test = new DalBulkTaskRequest<>("", "", null, new ArrayList<TestPojo>(), new TestBulkTask());
		try {
			test.validate();
		} catch (SQLException e) {
			fail();
		}
		
		// Null pojos
		test = new DalBulkTaskRequest<>("", "", null, null, new TestBulkTask());
		try {
			test.validate();
			fail();
		} catch (SQLException e) {
		}

		// Null task
		test = new DalBulkTaskRequest<>("", "", null, new ArrayList<TestPojo>(), null);
		try {
			test.validate();
			fail();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testIsCrossShard() {
		DalBulkTaskRequest<Integer, TestPojo> test = null;
		List<TestPojo> pojos = null;
		try {
			// Already sharded
			test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "", new DalHints().inShard(1), new ArrayList<TestPojo>(), new TestBulkTask());
			assertFalse(test.isCrossShard());
			
			// Shuffled in one shard
			pojos = new ArrayList<TestPojo>();
			pojos.add(new TestPojo(0));
			pojos.add(new TestPojo(0));
			test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "", new DalHints(), pojos, new TestBulkTask());
			assertFalse(test.isCrossShard());
			
			// Shuffled in two shards
			pojos = new ArrayList<TestPojo>();
			pojos.add(new TestPojo(0));
			pojos.add(new TestPojo(1));
			test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "", new DalHints(), pojos, new TestBulkTask());
			assertTrue(test.isCrossShard());

			// No shard
			test = new DalBulkTaskRequest<>("dao_test_sqlsvr", "", new DalHints(), new ArrayList<TestPojo>(), new TestBulkTask());
			assertFalse(test.isCrossShard());

			// Shard at table level
			test = new DalBulkTaskRequest<>("dao_test_sqlsvr_tableShard", "", new DalHints(), new ArrayList<TestPojo>(), new TestBulkTask());
			assertFalse(test.isCrossShard());
			
		} catch (SQLException e) {
			fail();
		}
	}
	

	@Test
	public void testCreateTask() {
		DalBulkTaskRequest<Integer, TestPojo> test = null;
		List<TestPojo> pojos = null;
		Callable<Integer> task = null;
		try {
			// Empty
			pojos = new ArrayList<TestPojo>();
			test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "", new DalHints(), pojos, new TestBulkTask());
			test.isCrossShard();
			task = test.createTask();
			assertEquals(0, task.call().intValue());
			
			// Shuffled in one shard
			pojos = new ArrayList<TestPojo>();
			pojos.add(new TestPojo(0));
			pojos.add(new TestPojo(0));
			test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "", new DalHints(), pojos, new TestBulkTask());
			test.isCrossShard();
			task = test.createTask();
			assertNotNull(task);
			assertEquals(2, task.call().intValue());

			// Do not shuffle
			pojos = new ArrayList<TestPojo>();
			pojos.add(new TestPojo(0));
			pojos.add(new TestPojo(0));
			test = new DalBulkTaskRequest<>("dao_test_sqlsvr_dbShard", "", new DalHints().inShard(1), pojos, new TestBulkTask());
			// To create pojos
			test.isCrossShard();
			task = test.createTask();
			assertNotNull(task);
			assertEquals(2, task.call().intValue());
		
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testCreateTasks() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMerger() {
		fail("Not yet implemented");
	}

}
