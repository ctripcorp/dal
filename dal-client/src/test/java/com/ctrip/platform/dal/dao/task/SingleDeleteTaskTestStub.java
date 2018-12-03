package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalHints;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SingleDeleteTaskTestStub extends TaskTestStub {
	
	public SingleDeleteTaskTestStub (String dbName) {
		super(dbName);
	}

	@Test
	public void testExecute() {
		SingleDeleteTask<ClientTestModel> test = new SingleDeleteTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			int result = test.execute(hints, getAllMap().get(0), null, new DefaultTaskContext());
//			assertEquals(1, result);
			assertEquals(2, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}
