package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
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
}