package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.configure.LocalDalPropertiesProvider;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.sqlbuilder.SqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.TableSqlBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

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
							   StatementParameters parameters, DalHints hints, DalTaskContext taskContext)
				throws SQLException {
			return i;
		}

		@Override
		public DefaultTaskContext createTaskContext() throws SQLException {
			DefaultTaskContext taskContext = new DefaultTaskContext();
			return taskContext;
		}
	}
	
	private class TestSqlBuilder implements SqlBuilder {
		String sql;
		StatementParameters p;
		TestSqlBuilder(String sql, StatementParameters p) {
			this.sql = sql;
			this.p = p;
		}
		TestSqlBuilder() {
			this(new StatementParameters());
		}
		
		TestSqlBuilder(StatementParameters p) {
			this.p = p;
		}
		@Override
		public String build() {
			return sql;
		}

		@Override
		public StatementParameters buildParameters() {
			return p;
		}
	}

	private class TestTableSqlBuilder extends TestSqlBuilder implements TableSqlBuilder {

		@Override
		public TableSqlBuilder from(String tableName) throws SQLException {
			return null;
		}

		@Override
		public TableSqlBuilder setDatabaseCategory(DatabaseCategory dbCategory) throws SQLException {
			return null;
		}

		@Override
		public String getTableName() {
			return "dal_client_test";
		}

		@Override
		public String build(String shardStr) {
			return null;
		}
	}


	@Test
	public void testImplicitAllShards() throws Exception {
//		set implicitAllShards on
		DalPropertiesManager.getInstance().tearDown();
		DalPropertiesManager.getInstance().setDalPropertiesProvider(new LocalDalPropertiesProvider());
		DalPropertiesManager.getInstance().setup();

//		implicit in all shards
		DalSqlTaskRequest<Integer> testRequest = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbTableShard", new TestTableSqlBuilder(), new DalHints(), new FreeSqlUpdateTask(), new ResultMerger.IntSummary());
		Assert.assertTrue(testRequest.isCrossShard());

//		implicit in all table shards
		testRequest.validateAndPrepare();
		Map<String, TaskCallable<Integer>> tasks = testRequest.createTasks();
		for (Callable callable : tasks.values())
			assertEquals(4, ((DalSqlTaskRequest.SqlTaskCallable) callable).tableShards().size());

//		set implicitAllShards off
		DalPropertiesManager.getInstance().tearDown();
		DalPropertiesManager.getInstance().setDalPropertiesProvider(DalElementFactory.DEFAULT.getDalPropertiesProvider());
		DalPropertiesManager.getInstance().setup();

		//	not	implicit in all shards
		try {
			testRequest = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbTableShard", new TestTableSqlBuilder(), new DalHints(), new FreeSqlUpdateTask(), new ResultMerger.IntSummary());
			Assert.assertFalse(testRequest.isCrossShard());
		}catch (Exception e) {
			fail();
		}

		//	not	implicit in all shards
		try {
			testRequest = new DalSqlTaskRequest<>("dao_test_sqlsvr_tableShard", new TestTableSqlBuilder(), new DalHints(), new FreeSqlUpdateTask(), new ResultMerger.IntSummary());
			testRequest.validateAndPrepare();
			Callable<Integer> task = testRequest.createTask();
			assertNull(((DalSqlTaskRequest.SqlTaskCallable) task).tableShards());
		}catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testValidate() {
		DalSqlTaskRequest<Integer> test = null;
		try {
			test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", new TestSqlBuilder(), new DalHints(), new FreeSqlUpdateTask(), null);
			test.validateAndPrepare();
		} catch (SQLException e) {
			fail();
		}
	}

	@Test
	public void testIsCrossShard() throws SQLException {
		DalSqlTaskRequest<Integer> test = null;
		
		DalHints hints = new DalHints();
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", new TestSqlBuilder(), hints, null, null);
		assertFalse(test.isCrossShard());
			
		hints = new DalHints();
		hints.inAllShards();
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", new TestSqlBuilder(), hints, null, null);
		assertTrue(test.isCrossShard());
		
		hints = new DalHints();
		Set<String> shards = new HashSet<>();
		shards.add("0");
		shards.add("1");
		hints.inShards(shards);
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", new TestSqlBuilder(), hints, null, null);
		assertTrue(test.isCrossShard());
	}

	@Test
	public void testCreateTask() throws SQLException {
		DalSqlTaskRequest<Integer> test = null;
		
		DalHints hints = new DalHints();
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", new TestSqlBuilder(), hints, new TestSqlTask(1), new ResultMerger.IntSummary());
		test.validateAndPrepare();
		try {
			Callable<Integer> task = test.createTask();
			assertEquals(1, task.call().intValue());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testCreateTasksInAllShards() {
		DalSqlTaskRequest<Integer> test = null;
		
		try {
			// In all shards
			DalHints hints = new DalHints();
			hints = new DalHints();
			hints.inAllShards();
			test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", new TestSqlBuilder(), hints, new TestSqlTask(1), new ResultMerger.IntSummary());
			test.validateAndPrepare();
			Map<String, TaskCallable<Integer>> tasks = test.createTasks();
			int i = 0;
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
	public void testCreateTasksInShards() {
		DalSqlTaskRequest<Integer> test = null;
		try {
			// In shards
			DalHints hints = new DalHints();
			Set<String> shards = new HashSet<>();
			shards.add("0");
			shards.add("1");
			hints.inShards(shards);
			test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", new TestSqlBuilder(), hints, new TestSqlTask(1), new ResultMerger.IntSummary());
			test.validateAndPrepare();
			Map<String, TaskCallable<Integer>> tasks = test.createTasks();
			int i = 0;
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
	public void testCreateTasksByInParameters() {
		DalSqlTaskRequest<Integer> test = null;
		try {
			// Shard by in parameter
			DalHints hints = new DalHints();
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
			
			test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", new TestSqlBuilder("select * from tablea where id = ? and id in (?) and id = ?", p), hints, new TestSqlTask(1), new ResultMerger.IntSummary());
			test.validateAndPrepare();
			Map<String, TaskCallable<Integer>> tasks = test.createTasks();
			int i = 0;
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
		test = new DalSqlTaskRequest<>("dao_test_sqlsvr_dbShard", new TestSqlBuilder(), hints, null, new ResultMerger.IntSummary());
		assertNotNull(test.getMerger());
	}

}
