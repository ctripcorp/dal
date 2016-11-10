package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.task.BatchUpdateTask;
import com.ctrip.platform.dal.exceptions.ErrorCode;

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
	public void testValidteNullField() {
		BatchUpdateTask<ClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			List<ClientTestModel> pojos = getAll();
			Short s = null;
			for(ClientTestModel model: pojos) {
				model.setAddress(null);
				model.setType((Short)s);
				model.setQuantity(null);
				model.setDbIndex(null);
				model.setLastChanged(null);
				model.setTableIndex(null);
			}
			
			int[] result = test.execute(hints, test.getPojosFieldsMap(pojos));
			fail();
		} catch (SQLException e) {
			assertEquals(e.getMessage(), ErrorCode.ValidateFieldCount.getMessage());
		}
	}
	
	@Test
	public void testIgnorNullField() {
		BatchUpdateTask<ClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			List<ClientTestModel> pojos = getAll();
			for(ClientTestModel model: pojos) {
				model.setAddress("1122334455");
				model.setType((Short)null);
				model.setQuantity(null);
				model.setDbIndex(null);
				model.setLastChanged(null);
				model.setTableIndex(null);
			}
			
			int[] result = test.execute(hints, test.getPojosFieldsMap(pojos));
			assertEquals(3, result.length);
			assertEquals(3, getCount());
			
			pojos = getAll();
			for(ClientTestModel model: pojos) {
				assertEquals("1122334455", model.getAddress());
				assertNotNull(model.getQuantity());
				assertNotNull(model.getType());
				assertNotNull(model.getDbIndex());
				assertNotNull(model.getLastChanged());
				assertNotNull(model.getTableIndex());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testUpdateNullField() {
		BatchUpdateTask<ClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			List<ClientTestModel> pojos = getAll();
			for(ClientTestModel model: pojos) {
				model.setType((Short)null);
				model.setQuantity(-100);
				model.setDbIndex(null);
				// My sql, it will use default value, while sqlserver will not
//				model.setLastChanged(null);
				model.setTableIndex(null);
			}
			
			int[] result = test.execute(hints.updateNullField(), test.getPojosFieldsMap(pojos));
			assertEquals(3, result.length);
			assertEquals(3, getCount());
			
			pojos = getAll();
			for(ClientTestModel model: pojos) {
				assertEquals(model.getQuantity().intValue(), -100);
				assertEquals(0, model.getType().intValue());
				assertNull(model.getDbIndex());
				// The default value
//				assertNotNull(model.getLastChanged());
				assertNull(model.getTableIndex());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testUpdateIfNullField() {
		BatchUpdateTask<ClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser());
		DalHints hints = new DalHints();
		
		try {
			List<ClientTestModel> pojos = getAll();
			int i = 0;
			for(ClientTestModel model: pojos) {
				model.setType((Short)null);
				if(i%2 == 0)
					model.setQuantity(-100);
				else
					model.setQuantity(null);
				
				if(i%2 == 1)
					model.setDbIndex(-200);
				else
					model.setDbIndex(null);
				
				if(i%2 == 1)
					model.setTableIndex(-300);
				else
					model.setTableIndex(null);
				i++;
			}
			
			int[] result = test.execute(hints, test.getPojosFieldsMap(pojos));
			assertEquals(3, result.length);
			assertEquals(3, getCount());
			
			i = 0;
			pojos = getAll();
			for(ClientTestModel model: pojos) {
				assertEquals(1, model.getType().intValue());
				assertNotNull(model.getLastChanged());
				
				if(i%2 == 0)
					assertEquals(model.getQuantity().intValue(), -100);
				else
					assertEquals(model.getQuantity().intValue(), 10 + i);
				
				if(i%2 == 1)
					assertEquals(model.getDbIndex().intValue(), -200);
				else
					assertEquals(model.getDbIndex().intValue(), 0);
				
				if(i%2 == 1)
					assertEquals(model.getTableIndex().intValue(), -300);
				else
					assertEquals(model.getTableIndex().intValue(), i);
				i++;
			}
			
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
