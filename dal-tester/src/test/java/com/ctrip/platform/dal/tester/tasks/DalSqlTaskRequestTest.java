package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.task.DalSqlTaskRequest;
import com.ctrip.platform.dal.dao.task.SqlTask;

public class DalSqlTaskRequestTest {
	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
		}
	}
	
	private class TestSqlTask implements SqlTask<Integer> {
		int i;
		TestSqlTask (int i) {
			this.i = i;
		}

		@Override
		public Integer execute(DalClient client, String sql,
				StatementParameters parameters, DalHints hints)
				throws SQLException {
			return i;
		}
	}

	@Test
	public void testValidate() {
		DalSqlTaskRequest<Integer> test = null;
		try {
			test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", null, new StatementParameters(), new DalHints(), null, null);
			test.validate();
			fail();
		} catch (SQLException e) {
		}
	}

	@Test
	public void testIsCrossShard() throws SQLException {
		DalSqlTaskRequest<Integer> test = null;
		
		DalHints hints = new DalHints();
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", "", new StatementParameters(), hints, null, null);
		assertFalse(test.isCrossShard());
			
		hints = new DalHints();
		hints.inAllShards();
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", "", new StatementParameters(), hints, null, null);
		assertTrue(test.isCrossShard());
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		hints.inAllShards();
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", "", new StatementParameters(), hints, null, null);
		assertTrue(test.isCrossShard());
	}

	@Test
	public void testCreateTask() throws SQLException {
		DalSqlTaskRequest<Integer> test = null;
		
		DalHints hints = new DalHints();
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", "", new StatementParameters(), hints, new TestSqlTask(1), new ResultMerger.IntSummary());
		try {
			Callable<Integer> task = test.createTask();
			assertEquals(1, task.call().intValue());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testCreateTasks() {
		DalSqlTaskRequest<Integer> test = null;
		
		try {
			// In all shards
			DalHints hints = new DalHints();
			hints = new DalHints();
			hints.inAllShards();
			test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", "", new StatementParameters(), hints, new TestSqlTask(1), new ResultMerger.IntSummary());
			Map<String, Callable<Integer>> tasks = test.createTasks();
			int i = 0;
			for(Callable<Integer> task: tasks.values()){
				i += task.call().intValue();
			}
			assertEquals(2, i);

			
			// In shards
			hints = new DalHints();
			Set<String> shards = new HashSet<>();
			shards.add("0");
			shards.add("1");
			hints.inShards(shards);
			test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", "", new StatementParameters(), hints, new TestSqlTask(1), new ResultMerger.IntSummary());
			tasks = test.createTasks();
			i = 0;
			for(Callable<Integer> task: tasks.values()){
				i += task.call().intValue();
			}
			assertEquals(2, i);
			
			// Shard by in parameter
			hints = new DalHints();
			hints.shardBy("index");
			StatementParameters p = new StatementParameters();
			p.set(1, 1);
			
			List<Integer> l = new ArrayList<>();
			l.add(1);
			l.add(2);
			l.add(3);
			l.add(4);
			
			p.setInParameter(2, "index", Types.INTEGER, l);
			p.set(3, 1);
			
			test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", "", p, hints, new TestSqlTask(1), new ResultMerger.IntSummary());
			tasks = test.createTasks();
			i = 0;
			for(Callable<Integer> task: tasks.values()){
				i += task.call().intValue();
			}
			assertEquals(2, i);
			
		} catch (Exception e) {
			fail();
		}
		
		assertNotNull(test.getMerger());
	}

	@Test
	public void testGetMerger() throws SQLException {
		DalSqlTaskRequest<Integer> test = null;
		
		DalHints hints = new DalHints();
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", "", new StatementParameters(), hints, null, new ResultMerger.IntSummary());
		assertNotNull(test.getMerger());
	}

}
