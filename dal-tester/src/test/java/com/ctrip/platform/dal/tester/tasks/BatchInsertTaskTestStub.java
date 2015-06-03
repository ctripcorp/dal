package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.task.BatchInsertTask;

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
			int[] result = test.execute(hints, getAllMap());
			assertEquals(3, result.length);
			assertEquals(6, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateMerger() {
		BatchInsertTask<ClientTestModel> test = new BatchInsertTask<>();
		assertNotNull(test.createMerger());
	}
}
