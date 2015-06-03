package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Types;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.task.DeleteSqlTask;

public class DeleteSqlTaskTestStub extends TaskTestStub {
	
	public DeleteSqlTaskTestStub(String dbName) {
		super(dbName);
	}

	@Test
	public void testExecute() {
		DeleteSqlTask<ClientTestModel> test = new DeleteSqlTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.INTEGER, 1);
		
		try {
			int result = test.execute(getClient(), "id=?", parameters, hints);
//			assertEquals(1, result);
			assertEquals(2, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}
