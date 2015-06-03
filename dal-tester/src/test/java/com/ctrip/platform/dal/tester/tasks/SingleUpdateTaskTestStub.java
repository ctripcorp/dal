package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.task.SingleUpdateTask;

public class SingleUpdateTaskTestStub extends TaskTestStub {
	
	public SingleUpdateTaskTestStub (String dbName) {
		super(dbName);
	}

	@Test
	public void testExecute() {
		SingleUpdateTask<ClientTestModel> test = new SingleUpdateTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			ClientTestModel model = getAll().get(0);
			model.setAddress("1122334455");
			
			int result = test.execute(hints, getParser().getFields(model));
//			assertEquals(1, result);
			model = getDao().queryByPk(model, new DalHints());
			assertEquals("1122334455", model.getAddress());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}