package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.task.BulkTaskContext;
import com.ctrip.platform.dal.dao.task.DefaultTaskContext;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.task.BatchDeleteTask;

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
