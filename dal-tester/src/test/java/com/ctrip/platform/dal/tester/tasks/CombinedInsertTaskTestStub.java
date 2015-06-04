package com.ctrip.platform.dal.tester.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.task.CombinedInsertTask;

//TODO handle keyholder and set nocount on issue
public class CombinedInsertTaskTestStub extends TaskTestStub {
	private String dbName;
	private boolean enableKeyHolder;
	
	public CombinedInsertTaskTestStub (String dbName, boolean enableKeyHolder) {
		super(dbName);
		this.dbName = dbName;
		this.enableKeyHolder = enableKeyHolder;
	}
	
	@Test
	public void testGetEmptyValue() {
		CombinedInsertTask<ClientTestModel> test = new CombinedInsertTask<>();
		assertEquals(0, test.getEmptyValue().intValue());
	}
	
	@Test
	public void testExecute() {
		CombinedInsertTask<ClientTestModel> test = new CombinedInsertTask<>();
		test.initialize(new ClientTestDalParser(dbName));
		DalHints hints = new DalHints();
		if(enableKeyHolder)
			hints.setKeyHolder(new KeyHolder());
		try {
			test.execute(hints, getAllMap());
			if(enableKeyHolder)
				assertEquals(3, hints.getKeyHolder().size());
			assertEquals(3+3, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateMerger() {
		CombinedInsertTask<ClientTestModel> test = new CombinedInsertTask<>();
		assertNotNull(test.createMerger());
	}
}
