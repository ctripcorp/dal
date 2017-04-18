package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.task.BatchUpdateTask;
import com.ctrip.platform.dal.dao.task.BulkTaskContext;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class BatchUpdateTaskTestStub extends TaskTestStub {
	private boolean isMySql;
	public BatchUpdateTaskTestStub (String dbName) {
		super(dbName);
	}
	
	public void setMySql(boolean value) {
		isMySql = value;
	}

	@Test
	public void testGetEmptyValue() {
		BatchUpdateTask<ClientTestModel> test = new BatchUpdateTask<>();
		assertArrayEquals(new int[0], test.getEmptyValue());
	}
	
	private <T> int[] execute(BatchUpdateTask test, DalHints hints, List<T> rawPojos) throws SQLException{
		BulkTaskContext<T> taskContext = test.createTaskContext(hints, test.getPojosFields(rawPojos), rawPojos);
		return test.execute(hints, test.getPojosFieldsMap(rawPojos), taskContext);
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
			
			int[] result = execute(test, hints, pojos);
			assertEquals(3, result.length);
			assertArrayEquals(new int[]{1, 1 , 1}, result);
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
	public void testExecuteUpdatableEntity() throws SQLException {
		BatchUpdateTask<UpdatableClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser(UpdatableClientTestModel.class));
		DalHints hints = new DalHints();
		
		try {
			List<UpdatableClientTestModel> pojos = getAll(UpdatableClientTestModel.class);
			for(UpdatableClientTestModel model: pojos)
				model.setAddress("1122334455");
			
			int[] result = execute(test, hints, pojos);
			assertEquals(3, result.length);
			assertArrayEquals(new int[]{1, 1 , 1}, result);
			assertEquals(3, getCount());
			
			pojos = getAll(UpdatableClientTestModel.class);
			for(UpdatableClientTestModel model: pojos)
				assertEquals("1122334455", model.getAddress());
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testDaoExecuteUpdatableEntity() throws SQLException {
		DalTableDao<UpdatableClientTestModel> dao = new DalTableDao<>(UpdatableClientTestModel.class, getDbName());
		DalHints hints = new DalHints();

		try {
			List<UpdatableClientTestModel> pojos = getAll(UpdatableClientTestModel.class);
			for(UpdatableClientTestModel model: pojos)
				model.setAddress("1122334455");
			
			int[] result = dao.batchUpdate(hints,pojos);
			
			assertEquals(3, result.length);
			assertArrayEquals(new int[]{1, 1 , 1}, result);
			assertEquals(3, getCount());
			
			pojos = getAll(UpdatableClientTestModel.class);
			for(UpdatableClientTestModel model: pojos)
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
			for(ClientTestModel model: pojos) {
				model.setAddress(null);
				model.setType(null);
				model.setQuantity(null);
				model.setDbIndex(null);
				model.setLastChanged(null);
				model.setTableIndex(null);
			}
			
			int[] result = execute(test, hints, pojos);
			fail();
		} catch (SQLException e) {
			assertEquals(e.getMessage(), ErrorCode.ValidateFieldCount.getMessage());
		}
	}
	
	@Test
	public void testValidteUnchangedField() throws SQLException {
		BatchUpdateTask<UpdatableClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser(UpdatableClientTestModel.class));
		DalHints hints = new DalHints();
		
		try {
			List<UpdatableClientTestModel> pojos = getAll(UpdatableClientTestModel.class);
			int[] result = execute(test, hints, pojos);
			fail();
		} catch (SQLException e) {
			assertEquals(e.getMessage(), ErrorCode.ValidateFieldCount.getMessage());
		}
		
		try {
			List<UpdatableClientTestModel> pojos = getAll(UpdatableClientTestModel.class);
			int[] result = execute(test, hints.updateUnchangedField(), pojos);
		} catch (SQLException e) {
			fail();
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
			
			int[] result = execute(test, hints, pojos);
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
	public void testIgnorUnchangedField() throws SQLException {
		BatchUpdateTask<UpdatableClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser(UpdatableClientTestModel.class));
		DalHints hints = new DalHints();
		
		try {
			List<UpdatableClientTestModel> pojos = getAll(UpdatableClientTestModel.class);
			for(UpdatableClientTestModel model: pojos)
				model.setAddress("1122334455");
			
			List<ClientTestModel> pojos2 = getAll();
			for(ClientTestModel model: pojos2) {
				model.setTableIndex(-1);
				model.setDbIndex(-2);
				model.setType((short)-1);
				model.setQuantity(-100);
			}
			
			// Change existing data to other value, so that we can compare after the first pojos get updated
			int[] result = getDao().batchUpdate(new DalHints(), pojos2);

			// Update with UpdatableEntity with only changed values
			result = execute(test, hints, pojos);
			
			pojos = getAll(UpdatableClientTestModel.class);
			for(UpdatableClientTestModel model: pojos) {
				assertEquals("1122334455", model.getAddress());
				assertEquals(-100, model.getQuantity().intValue());
				assertEquals(-1, model.getType().shortValue());
				assertEquals(-2, model.getDbIndex().intValue());
				assertEquals(-1, model.getTableIndex().intValue());
				assertNotNull(model.getLastChanged());
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
			
			int[] result = execute(test, hints.updateNullField(), pojos);
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
	public void testUpdateUnchangedField() throws SQLException {
		BatchUpdateTask<UpdatableClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser(UpdatableClientTestModel.class));
		DalHints hints = new DalHints();
		
		try {
			List<UpdatableClientTestModel> pojos = getAll(UpdatableClientTestModel.class);
			for(UpdatableClientTestModel model: pojos) {
				model.setType((Short)null);
				model.setQuantity(-100);
			}
			
			List<ClientTestModel> pojos2 = getAll();
			for(ClientTestModel model: pojos2) {
				model.setTableIndex(-1);
				model.setDbIndex(-2);
			}
			
			// Update data
			int[] result = getDao().batchUpdate(new DalHints(), pojos2);
			
			result = execute(test, hints.updateUnchangedField(), pojos);
			
			pojos = getAll(UpdatableClientTestModel.class);
			
			// Check if still old value in UpdatabvleEntity
			int i = 0;
			for(UpdatableClientTestModel model: pojos) {
				assertNull(model.getType());
				assertEquals(-100, model.getQuantity().intValue());
				assertEquals(i++, model.getTableIndex().intValue());
				assertEquals(0, model.getDbIndex().intValue());
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
			
			int[] result = execute(test, hints, pojos);
			assertArrayEquals(new int[]{1, 1 , 1}, result);

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
	public void testUpdateIfChangedField() throws SQLException {
		BatchUpdateTask<UpdatableClientTestModel> test = new BatchUpdateTask<>();
		test.initialize(getParser(UpdatableClientTestModel.class));
		DalHints hints = new DalHints();
		
		try {
			List<UpdatableClientTestModel> pojos = getAll(UpdatableClientTestModel.class);
			int i = 0;
			for(UpdatableClientTestModel model: pojos) {
				model.setType((Short)null);
				if(i%2 == 0)
					model.setQuantity(-100);
				
				if(i%2 == 1)
					model.setDbIndex(-200);
				
				if(i%2 == 1)
					model.setTableIndex(-300);
				i++;
			}
			
			int[] result = execute(test, hints, pojos);
			assertArrayEquals(new int[]{1, 1 , 1}, result);

			i = 0;
			pojos = getAll(UpdatableClientTestModel.class);
			for(UpdatableClientTestModel model: pojos) {
				assertNull(model.getType());
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
	public void testDaoUpdateIfChangedField() throws SQLException {
		DalTableDao<UpdatableClientTestModel> dao = new DalTableDao<>(UpdatableClientTestModel.class, getDbName());
		DalHints hints = new DalHints();
		
		try {
			List<UpdatableClientTestModel> pojos = getAll(UpdatableClientTestModel.class);
			int i = 0;
			for(UpdatableClientTestModel model: pojos) {
				model.setType((Short)null);
				if(i%2 == 0)
					model.setQuantity(-100);
				
				if(i%2 == 1)
					model.setDbIndex(-200);
				
				if(i%2 == 1)
					model.setTableIndex(-300);
				i++;
			}
			
			int[] result = dao.batchUpdate(hints, pojos);
			assertArrayEquals(new int[]{1, 1 , 1}, result);

			i = 0;
			pojos = getAll(UpdatableClientTestModel.class);
			for(UpdatableClientTestModel model: pojos) {
				assertNull(model.getType());
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
	public void testVersionNotSet() throws SQLException {
		BatchUpdateTask<UpdatableVersionModel> test = new BatchUpdateTask<>();
		DalParser<UpdatableVersionModel> parser = new DalDefaultJpaParser<>(UpdatableVersionModel.class, getDbName());
		test.initialize(parser);
		DalTableDao<UpdatableVersionModel> dao = new DalTableDao<UpdatableVersionModel>(parser);
		
		DalHints hints = new DalHints();
		
		List<UpdatableVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableVersionModel model: pojos){
			model.setAddress("1122334455");
			model.setLastChanged(null);
		}
		
		try {
			execute(test, hints, pojos);
			fail();
		} catch (SQLException e) {
			assertEquals(ErrorCode.ValidateVersion.getMessage(), e.getMessage());
		}
	}
	
	@Test
	public void testVersionIncorrect() throws SQLException {
		BatchUpdateTask<UpdatableVersionModel> test = new BatchUpdateTask<>();
		DalParser<UpdatableVersionModel> parser = new DalDefaultJpaParser<>(UpdatableVersionModel.class, getDbName());
		test.initialize(parser);
		DalTableDao<UpdatableVersionModel> dao = new DalTableDao<UpdatableVersionModel>(parser);
		
		DalHints hints = new DalHints();
		
		List<UpdatableVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableVersionModel model: pojos){
			model.setAddress("1122334455");
			Timestamp t = model.getLastChanged();
			t.setTime(t.getTime()+100);
			model.setLastChanged(t);
		}
		
		int[] result = execute(test, hints, pojos);
		assertArrayEquals(new int[]{0, 0 , 0}, result);
		
		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableVersionModel model: pojos)
			assertEquals("SH INFO", model.getAddress());
	}
	
	@Test
	public void testUpdatableWithVersion() throws SQLException {
		BatchUpdateTask<UpdatableVersionModel> test = new BatchUpdateTask<>();
		DalParser<UpdatableVersionModel> parser = new DalDefaultJpaParser<>(UpdatableVersionModel.class, getDbName());
		test.initialize(parser);
		DalTableDao<UpdatableVersionModel> dao = new DalTableDao<UpdatableVersionModel>(parser);
		
		DalHints hints = new DalHints();
		
		List<UpdatableVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		long[] oldVer = new long[3];
		int i = 0;
		for(UpdatableVersionModel model: pojos){
			model.setAddress("1122334455");
			oldVer[i++] = model.getLastChanged().getTime();
		}
		
		int[] result = execute(test, hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		i = 0;
		for(UpdatableVersionModel model: pojos){
			assertEquals("1122334455", model.getAddress());
			Assert.assertTrue(oldVer[i++] <= model.getLastChanged().getTime());
		}
	}
	
	@Test
	public void testUpdatableWithVersionByDao() throws SQLException {
		DalParser<UpdatableVersionModel> parser = new DalDefaultJpaParser<>(UpdatableVersionModel.class, getDbName());
		DalTableDao<UpdatableVersionModel> dao = new DalTableDao<UpdatableVersionModel>(parser);
		
		DalHints hints = new DalHints();
		
		List<UpdatableVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableVersionModel model: pojos){
			model.setAddress("1122334455");//Old value is SH INFO
			Timestamp t = model.getLastChanged();
			t.setTime(t.getTime()+100);
			model.setLastChanged(t);
		}
		
		int[] result = dao.batchUpdate(hints, pojos);
		assertArrayEquals(new int[]{0, 0 , 0}, result);
		
		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableVersionModel model: pojos)
			assertEquals("SH INFO", model.getAddress());//Still old value because version is incorrect

		// Now the right case
		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		long[] oldVer = new long[3];
		int i = 0;
		for(UpdatableVersionModel model: pojos){
			model.setAddress("1122334455");
			oldVer[i++] = model.getLastChanged().getTime();
		}
		
		result = dao.batchUpdate(hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		i = 0;
		for(UpdatableVersionModel model: pojos){
			assertEquals("1122334455", model.getAddress());
			Assert.assertTrue(oldVer[i++] <= model.getLastChanged().getTime());
		}
	}
	
	@Test
	public void testUpdatableWithIntVersion() throws SQLException {
		BatchUpdateTask<UpdatableIntVersionModel> test = new BatchUpdateTask<>();
		DalParser<UpdatableIntVersionModel> parser = new DalDefaultJpaParser<>(UpdatableIntVersionModel.class, getDbName());
		test.initialize(parser);
		DalTableDao<UpdatableIntVersionModel> dao = new DalTableDao<>(parser);
		
		DalHints hints = new DalHints();
		
		Integer[] oldValue = new Integer[3];
		List<UpdatableIntVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		int i = 0;
		for(UpdatableIntVersionModel model: pojos){
			model.setAddress("1122334455");
			oldValue[i++] = model.getTableIndex();
		}
		
		int[] result = execute(test, hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		i = 0;
		for(UpdatableIntVersionModel model: pojos) {
			assertEquals("1122334455", model.getAddress());
			Assert.assertTrue(oldValue[i++]+1 == model.getTableIndex());
		}
	}
	
	@Test
	public void testUpdatableWithIntVersionByDao() throws SQLException {
		DalParser<UpdatableIntVersionModel> parser = new DalDefaultJpaParser<>(UpdatableIntVersionModel.class, getDbName());
		DalTableDao<UpdatableIntVersionModel> dao = new DalTableDao<>(parser);
		
		DalHints hints = new DalHints();
		
		List<UpdatableIntVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		int i = 0;
		for(UpdatableIntVersionModel model: pojos){
			model.setAddress("1122334455");
			model.setTableIndex(-1);// Make the version incorrect
		}
		
		int[] result = dao.batchUpdate(hints, pojos);
		assertArrayEquals(new int[]{0, 0, 0}, result);
		
		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos)
			assertEquals("SH INFO", model.getAddress());//Still old value because version is incorrect

		// Now the right case
		Integer[] oldValue = new Integer[3];
		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		i = 0;
		for(UpdatableIntVersionModel model: pojos){
			model.setAddress("1122334455");
			oldValue[i++] = model.getTableIndex();
		}
		
		result = dao.batchUpdate(hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		i = 0;
		for(UpdatableIntVersionModel model: pojos) {
			assertEquals("1122334455", model.getAddress());
			Assert.assertTrue(oldValue[i++]+1 == model.getTableIndex());
		}
	}
	
	@Test
	public void testNotUpdatableVersion() throws SQLException {
		BatchUpdateTask<NonUpdatableVersionModel> test = new BatchUpdateTask<>();
		DalParser<NonUpdatableVersionModel> parser = new DalDefaultJpaParser<>(NonUpdatableVersionModel.class, getDbName());
		test.initialize(parser);
		DalTableDao<NonUpdatableVersionModel> dao = new DalTableDao<>(parser);
		
		DalHints hints = new DalHints();
		
		List<NonUpdatableVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(NonUpdatableVersionModel model: pojos){
			model.setAddress("1122334455");
		}
		
		int[] result = execute(test, hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(NonUpdatableVersionModel model: pojos)
			assertEquals("1122334455", model.getAddress());
	}

	@Test
	public void testIncludeColumns() throws SQLException {
		BatchUpdateTask<UpdatableIntVersionModel> test = new BatchUpdateTask<>();
		DalParser<UpdatableIntVersionModel> parser = new DalDefaultJpaParser<>(UpdatableIntVersionModel.class, getDbName());
		test.initialize(parser);
		DalTableDao<UpdatableIntVersionModel> dao = new DalTableDao<>(parser);
		
		DalHints hints = new DalHints().include("address", "quantity");
		
		List<UpdatableIntVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos){
			model.setQuantity(500);
			model.setDbIndex(100);
			model.setAddress("1122334455");
		}
		
		int[] result = execute(test, hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos) {
			assertEquals("1122334455", model.getAddress());
			assertEquals(500, model.getQuantity().intValue());
			assertEquals(0, model.getDbIndex().intValue());
		}
	}

	@Test
	public void testDaoIncludeColumns() throws SQLException {
		DalParser<UpdatableIntVersionModel> parser = new DalDefaultJpaParser<>(UpdatableIntVersionModel.class, getDbName());
		DalTableDao<UpdatableIntVersionModel> dao = new DalTableDao<>(parser);
		
		DalHints hints = new DalHints().include("address", "quantity");
		
		List<UpdatableIntVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos){
			model.setQuantity(500);
			model.setDbIndex(100);
			model.setAddress("1122334455");
		}
		
		int[] result = dao.batchUpdate(hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos) {
			assertEquals("1122334455", model.getAddress());
			assertEquals(500, model.getQuantity().intValue());
			assertEquals(0, model.getDbIndex().intValue());
		}
	}

	@Test
	public void testExcludeColumns() throws SQLException {
		BatchUpdateTask<UpdatableIntVersionModel> test = new BatchUpdateTask<>();
		DalParser<UpdatableIntVersionModel> parser = new DalDefaultJpaParser<>(UpdatableIntVersionModel.class, getDbName());
		test.initialize(parser);
		DalTableDao<UpdatableIntVersionModel> dao = new DalTableDao<>(parser);
		
		DalHints hints = new DalHints().exclude("dbIndex");
		
		List<UpdatableIntVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos){
			model.setQuantity(500);
			model.setDbIndex(100);
			model.setAddress("1122334455");
		}
		
		int[] result = execute(test, hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos) {
			assertEquals("1122334455", model.getAddress());
			assertEquals(500, model.getQuantity().intValue());
			assertEquals(0, model.getDbIndex().intValue());
		}
	}

	@Test
	public void testDaoExcludeColumns() throws SQLException {
		DalTableDao<UpdatableIntVersionModel> dao = new DalTableDao<>(UpdatableIntVersionModel.class, getDbName());
		
		DalHints hints = new DalHints().exclude("dbIndex");
		
		List<UpdatableIntVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos){
			model.setQuantity(500);
			model.setDbIndex(100);
			model.setAddress("1122334455");
		}
		
		int[] result = dao.batchUpdate(hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos) {
			assertEquals("1122334455", model.getAddress());
			assertEquals(500, model.getQuantity().intValue());
			assertEquals(0, model.getDbIndex().intValue());
		}
	}
	
	@Test
	public void testIncludeExcludeColumns() throws SQLException {
		BatchUpdateTask<UpdatableIntVersionModel> test = new BatchUpdateTask<>();
		DalParser<UpdatableIntVersionModel> parser = new DalDefaultJpaParser<>(UpdatableIntVersionModel.class, getDbName());
		test.initialize(parser);
		DalTableDao<UpdatableIntVersionModel> dao = new DalTableDao<>(parser);
		
		DalHints hints = new DalHints().exclude("dbIndex").include("quantity", "dbIndex", "address");
		
		List<UpdatableIntVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos){
			model.setQuantity(500);
			model.setDbIndex(100);
			model.setAddress("1122334455");
		}
		
		int[] result = execute(test, hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos) {
			assertEquals("1122334455", model.getAddress());
			assertEquals(500, model.getQuantity().intValue());
			assertEquals(0, model.getDbIndex().intValue());
		}
	}

	@Test
	public void testDaoIncludeExcludeColumns() throws SQLException {
		DalTableDao<UpdatableIntVersionModel> dao = new DalTableDao<>(UpdatableIntVersionModel.class, getDbName());
		
		DalHints hints = new DalHints().exclude("dbIndex").include("quantity", "dbIndex", "address");
		
		List<UpdatableIntVersionModel> pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos){
			model.setQuantity(500);
			model.setDbIndex(100);
			model.setAddress("1122334455");
		}
		
		int[] result = dao.batchUpdate(hints, pojos);
		assertArrayEquals(new int[]{1, 1, 1}, result);

		pojos = dao.query("1=1", new StatementParameters(), new DalHints());
		for(UpdatableIntVersionModel model: pojos) {
			assertEquals("1122334455", model.getAddress());
			assertEquals(500, model.getQuantity().intValue());
			assertEquals(0, model.getDbIndex().intValue());
		}
	}

	@Test
	public void testCreateMerger() {
		BatchUpdateTask<ClientTestModel> test = new BatchUpdateTask<>();
		assertNotNull(test.createMerger());
	}
	
	private void assertArrayEquals(int[] exp, int[] actual) {
		if(isMySql)
			Assert.assertArrayEquals(exp, actual);
	}
	
	@Entity
	@Database(name="MySqlSimpleDbTableShard")
	@Table(name="dal_client_test")
	public static class UpdatableVersionModel implements DalPojo {
		@Id
		@Column(name="id")
		@Type(value=Types.INTEGER)
		private Integer id;
		
		@Column(name="quantity")
		@Type(value=Types.INTEGER)
		private Integer quantity;
		
		@Column(name="dbIndex")
		@Type(value=Types.INTEGER)
		private Integer dbIndex;
		
		@Column(name="tableIndex")
		@Type(value=Types.INTEGER)
		private Integer tableIndex;
		
		@Column(name="type")
		@Type(value=Types.SMALLINT)
		private Short type;
		
		@Column(name="address")
		@Type(value=Types.VARCHAR)
		private String address;
		
		@Column(name="last_changed")
		@Type(value=Types.TIMESTAMP)
		@Version
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public Integer getDbIndex() {
			return dbIndex;
		}

		public void setDbIndex(Integer dbIndex) {
			this.dbIndex = dbIndex;
		}

		public Integer getTableIndex() {
			return tableIndex;
		}

		public void setTableIndex(Integer tableIndex) {
			this.tableIndex = tableIndex;
		}
		
		public Short getType() {
			return type;
		}

		public void setType(Short type) {
			this.type = type;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Timestamp getLastChanged() {
			return lastChanged;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.lastChanged = lastChanged;
		}
	}
	
	@Entity
	@Database(name="MySqlSimpleDbTableShard")
	@Table(name="dal_client_test")
	public static class NonUpdatableVersionModel implements DalPojo {
		@Id
		@Column(name="id")
		@Type(value=Types.INTEGER)
		private Integer id;
		
		@Column(name="quantity")
		@Type(value=Types.INTEGER)
		private Integer quantity;
		
		@Column(name="dbIndex")
		@Type(value=Types.INTEGER)
		private Integer dbIndex;
		
		@Column(name="tableIndex")
		@Type(value=Types.INTEGER)
		private Integer tableIndex;
		
		@Column(name="type")
		@Type(value=Types.SMALLINT)
		private Short type;
		
		@Column(name="address")
		@Type(value=Types.VARCHAR)
		private String address;
		
		@Column(name="last_changed", updatable=false)
		@Type(value=Types.TIMESTAMP)
		@Version
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public Integer getDbIndex() {
			return dbIndex;
		}

		public void setDbIndex(Integer dbIndex) {
			this.dbIndex = dbIndex;
		}

		public Integer getTableIndex() {
			return tableIndex;
		}

		public void setTableIndex(Integer tableIndex) {
			this.tableIndex = tableIndex;
		}
		
		public Short getType() {
			return type;
		}

		public void setType(Short type) {
			this.type = type;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Timestamp getLastChanged() {
			return lastChanged;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.lastChanged = lastChanged;
		}
	}
	@Entity
	@Database(name="MySqlSimpleDbTableShard")
	@Table(name="dal_client_test")
	public static class UpdatableIntVersionModel implements DalPojo {
		@Id
		@Column(name="id")
		@Type(value=Types.INTEGER)
		private Integer id;
		
		@Column(name="quantity")
		@Type(value=Types.INTEGER)
		private Integer quantity;
		
		@Column(name="dbIndex")
		@Type(value=Types.INTEGER)
		private Integer dbIndex;
		
		@Column(name="tableIndex")
		@Type(value=Types.INTEGER)
		@Version
		private Integer tableIndex;
		
		@Column(name="type")
		@Type(value=Types.SMALLINT)
		private Short type;
		
		@Column(name="address")
		@Type(value=Types.VARCHAR)
		private String address;
		
		@Column(name="last_changed")
		@Type(value=Types.TIMESTAMP)
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public Integer getDbIndex() {
			return dbIndex;
		}

		public void setDbIndex(Integer dbIndex) {
			this.dbIndex = dbIndex;
		}

		public Integer getTableIndex() {
			return tableIndex;
		}

		public void setTableIndex(Integer tableIndex) {
			this.tableIndex = tableIndex;
		}
		
		public Short getType() {
			return type;
		}

		public void setType(Short type) {
			this.type = type;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public Timestamp getLastChanged() {
			return lastChanged;
		}

		public void setLastChanged(Timestamp lastChanged) {
			this.lastChanged = lastChanged;
		}
	}
}
