package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.task.SingleDeleteTask;

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
			int result = test.execute(hints, getAllMap().get(0));
//			assertEquals(1, result);
			assertEquals(2, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}
