package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.task.DefaultTaskContext;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.task.SingleInsertTask;

public class SingleInsertTaskTestStub extends TaskTestStub {
	
	public SingleInsertTaskTestStub (String dbName) {
		super(dbName);
	}

	@Test
	public void testExecute() {
		SingleInsertTask<ClientTestModel> test = new SingleInsertTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			int result = test.execute(hints, getAllMap().get(0), null, new DefaultTaskContext());
//			assertEquals(1, result);
			assertEquals(3+1, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testExecuteWithUpdatableEntity() throws SQLException {
		SingleInsertTask<UpdatableClientTestModel> test = new SingleInsertTask<>();
		test.initialize(getParser(UpdatableClientTestModel.class));
		DalHints hints = new DalHints();
		
		try {
			UpdatableClientTestModel model = getAll(UpdatableClientTestModel.class).get(0);
			model.setAddress("abc");
			int result = test.execute(hints, getFields(model), model, new DefaultTaskContext());
//			assertEquals(0, result);
			assertEquals(4, getCount());

			UpdatableClientTestModel model2 = getAll(UpdatableClientTestModel.class).get(3);
			assertEquals("abc", model2.getAddress());
			assertEquals(model.getTableIndex(), model2.getTableIndex());
			assertEquals(model.getDbIndex(), model2.getDbIndex());
			assertEquals(model.getQuantity(), model2.getQuantity());
			assertEquals(model.getType(), model2.getType());
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testExecuteWithId() {
		SingleInsertTask<ClientTestModel> test = new SingleInsertTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints().enableIdentityInsert();
		
		try {
			if(this instanceof SingleInsertTaskSqlSvrTest)
				SqlServerTestInitializer.turnOnIdentityInsert();

			Map<String, ?> pojo = getAllMap().get(0);
			((Map)pojo).put("id", new Integer(210));
			int result = test.execute(hints, pojo, null, new DefaultTaskContext());

			assertEquals(3+1, getCount());

			Map<Integer, Map<String, ?>> pojos = getAllMap();
			Set<Integer> ids = new HashSet<>();
			for(Map<String, ?> pojoi: pojos.values()) {
				ids.add((Integer)pojoi.get("id"));
			}
			
			assertTrue(ids.contains(210));

			if(this instanceof SingleInsertTaskSqlSvrTest)
				SqlServerTestInitializer.turnOffIdentityInsert();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExecuteWithNonInsertable() throws SQLException {
		SingleInsertTask<NonInsertableVersionModel> test = new SingleInsertTask<>();
		DalParser<NonInsertableVersionModel> parser = new DalDefaultJpaParser<>(NonInsertableVersionModel.class, getDbName());
		test.initialize(parser);
		
		DalHints hints = new DalHints();
		int result = test.execute(hints, getAllMap().get(0), null, new DefaultTaskContext());

		assertEquals(3+1, getCount());
		
		Map<Integer, Map<String, ?>> pojos = getAllMap();
		for(Map<String, ?> pojo: pojos.values()) {
			assertNotNull(pojo.get("last_changed"));
		}
	}
}