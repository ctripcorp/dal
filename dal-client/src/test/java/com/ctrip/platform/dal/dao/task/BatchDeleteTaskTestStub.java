package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalHints;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class BatchDeleteTaskTestStub extends TaskTestStub {
	
	public BatchDeleteTaskTestStub (String dbName) {
		super(dbName);
	}

	@Test
	public void testGetEmptyValue() {
		BatchDeleteTask<ClientTestModel> test = new BatchDeleteTask<>();
		assertArrayEquals(new int[0], test.getEmptyValue());
	}

	@Test
	public void testExecute() {
		BatchDeleteTask<ClientTestModel> test = new BatchDeleteTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			int[] result = test.execute(hints, getAllMap(), new BulkTaskContext<>(getAll()));
			assertEquals(3, result.length);
			assertEquals(0, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateMerger() {
		BatchDeleteTask<ClientTestModel> test = new BatchDeleteTask<>();
		assertNotNull(test.createMerger());
	}

}
