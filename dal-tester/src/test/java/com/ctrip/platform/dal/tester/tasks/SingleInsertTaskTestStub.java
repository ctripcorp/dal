package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.task.BatchInsertTask;
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
			int result = test.execute(hints, getAllMap().get(0));
//			assertEquals(1, result);
			assertEquals(3+1, getCount());
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
			int result = test.execute(hints, pojo);

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
		int result = test.execute(hints, getAllMap().get(0));

		assertEquals(3+1, getCount());
		
		Map<Integer, Map<String, ?>> pojos = getAllMap();
		for(Map<String, ?> pojo: pojos.values()) {
			assertNotNull(pojo.get("last_changed"));
		}
	}
}