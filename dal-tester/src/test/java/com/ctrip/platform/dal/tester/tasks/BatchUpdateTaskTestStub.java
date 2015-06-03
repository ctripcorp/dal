package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.task.BatchUpdateTask;

public class BatchUpdateTaskTestStub extends TaskTestStub {
	
	public BatchUpdateTaskTestStub (String dbName) {
		super(dbName);
	}

	@Test
	public void testGetEmptyValue() {
		BatchUpdateTask<ClientTestModel> test = new BatchUpdateTask<>();
		assertArrayEquals(new int[0], test.getEmptyValue());
	}
	
	@Test
	public void testExecute() {
		BatchUpdateTask<ClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			List<ClientTestModel> pojos = getAll();
			for(ClientTestModel model: pojos)
				model.setAddress("1122334455");
			
			int[] result = test.execute(hints, test.getPojosFieldsMap(pojos));
			assertEquals(3, result.length);
			assertEquals(3, getCount());
			
			pojos = getAll();
			for(ClientTestModel model: pojos)
				assertEquals("1122334455", model.getAddress());
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateMerger() {
		BatchUpdateTask<ClientTestModel> test = new BatchUpdateTask<>();
		assertNotNull(test.createMerger());
	}

}
