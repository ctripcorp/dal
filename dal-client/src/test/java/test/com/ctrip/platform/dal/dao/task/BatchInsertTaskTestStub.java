package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.task.BatchInsertTask;
import com.ctrip.platform.dal.dao.task.BulkTaskContext;

public class BatchInsertTaskTestStub extends TaskTestStub {
	public BatchInsertTaskTestStub (String dbName) {
		super(dbName);
	}

	@Test
	public void testGetEmptyValue() {
		BatchInsertTask<ClientTestModel> test = new BatchInsertTask<>();
		assertArrayEquals(new int[0], test.getEmptyValue());
	}
	
	@Test
	public void testExecute() {
		BatchInsertTask<ClientTestModel> test = new BatchInsertTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			BulkTaskContext<ClientTestModel> ctx = test.createTaskContext(hints, test.getPojosFields(getAll()), getAll());
			int[] result = test.execute(hints, getAllMap(), ctx);
			assertEquals(3, result.length);
			assertEquals(6, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExecuteWithDiableAutoIncrementalId() {
		BatchInsertTask<ClientTestModel> test = new BatchInsertTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints().enableIdentityInsert();
		
		try {
			if(this instanceof BatchInsertTaskSqlSvrTest)
				SqlServerTestInitializer.turnOnIdentityInsert();
			
			Map<Integer, Map<String, ?>> pojos = getAllMap();
			int i = 111;
			for(Map<String, ?> pojo: pojos.values()) {
				((Map)pojo).put("id", new Integer(i++));
			}
			
			BulkTaskContext<ClientTestModel> ctx = test.createTaskContext(hints, test.getPojosFields(getAll()), getAll());
			int[] result = test.execute(hints, pojos, ctx);
			assertEquals(3, result.length);
			assertEquals(6, getCount());
			
			pojos = getAllMap();
			Set<Integer> ids = new HashSet<>();
			
			for(Map<String, ?> pojo: pojos.values()) {
				ids.add((Integer)pojo.get("id"));
			}
			
			assertTrue(ids.contains(111));
			assertTrue(ids.contains(112));
			assertTrue(ids.contains(113));

			if(this instanceof BatchInsertTaskSqlSvrTest)
				SqlServerTestInitializer.turnOffIdentityInsert();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExecuteWithNonInsertable() throws SQLException, InterruptedException {
		BatchInsertTask<NonInsertableVersionModel> test = new BatchInsertTask<>();
		DalParser<NonInsertableVersionModel> parser = new DalDefaultJpaParser<>(NonInsertableVersionModel.class, getDbName());
		test.initialize(parser);
		
		DalHints hints = new DalHints();
		try {
			List<ClientTestModel> old = getAll();
			
			Thread.sleep(1000);
			BulkTaskContext<NonInsertableVersionModel> ctx = test.createTaskContext(hints, test.getPojosFields(getAll(NonInsertableVersionModel.class)), getAll(NonInsertableVersionModel.class));
			test.execute(hints, getAllMap(), ctx);
			
			assertEquals(3+3, getCount());
			
			List<ClientTestModel> newModel = getAll().subList(3, 6);
			
			for(int i = 0; i < 3; i++) {
				assertTrue(newModel.get(i).getLastChanged().getTime() > old.get(i).getLastChanged().getTime());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		Map<Integer, Map<String, ?>> pojos = getAllMap();
		for(Map<String, ?> pojo: pojos.values()) {
			assertNotNull(pojo.get("last_changed"));
		}
	}
	
	@Test
	public void testExecuteNullColumns() throws SQLException {
		BatchInsertTask<NonInsertableVersionModel> test = new BatchInsertTask<>();
		DalParser<NonInsertableVersionModel> parser = new DalDefaultJpaParser<>(NonInsertableVersionModel.class, getDbName());
		test.initialize(parser);
		
		DalHints hints = new DalHints();
		try {
			List<NonInsertableVersionModel> models = getAll(NonInsertableVersionModel.class);
			BulkTaskContext<NonInsertableVersionModel> ctx = test.createTaskContext(hints, test.getPojosFields(models), models);
			for(NonInsertableVersionModel model: models) {
				model.setType(null);
				model.setDbIndex(null);
				model.setTableIndex(null);
			}
			// Type Address, tableIndex will not be included in insert
			
			test.execute(hints, test.getPojosFieldsMap(models), ctx);
			assertEquals(3+3, getCount());
			
			models = getAll(NonInsertableVersionModel.class).subList(3, 6);
			for(NonInsertableVersionModel model: models) {
				assertNull(model.getType());
				assertNull(model.getTableIndex());
				assertNull(model.getDbIndex());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		Map<Integer, Map<String, ?>> pojos = getAllMap();
		for(Map<String, ?> pojo: pojos.values()) {
			assertNotNull(pojo.get("last_changed"));
		}
	}
	
	@Test
	public void testCreateMerger() {
		BatchInsertTask<ClientTestModel> test = new BatchInsertTask<>();
		assertNotNull(test.createMerger());
	}
}
