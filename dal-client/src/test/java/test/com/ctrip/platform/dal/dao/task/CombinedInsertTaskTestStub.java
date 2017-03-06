package test.com.ctrip.platform.dal.dao.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.task.CombinedInsertTask;

//TODO handle keyholder and set nocount on issue
public class CombinedInsertTaskTestStub extends TaskTestStub {
	private boolean enableKeyHolder;
	
	public CombinedInsertTaskTestStub (String dbName, boolean enableKeyHolder) {
		super(dbName);
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
		test.initialize(new ClientTestDalParser(getDbName()));
		DalHints hints = new DalHints();
		if(enableKeyHolder)
			hints.setKeyHolder(new KeyHolder());
		try {
			test.execute(hints, getAllMap());
			if(enableKeyHolder){
				// You have to merge before get size
				assertEquals(3, hints.getKeyHolder().size());
			}
			assertEquals(3+3, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExecuteWithId() {
		CombinedInsertTask<ClientTestModel> test = new CombinedInsertTask<>();
		test.initialize(new ClientTestDalParser(getDbName()));
		DalHints hints = new DalHints().enableIdentityInsert();
		if(enableKeyHolder)
			hints.setKeyHolder(new KeyHolder());
		try {
			if(this instanceof CombinedInsertTaskSqlSvrTest)
				SqlServerTestInitializer.turnOnIdentityInsert();

			Map<Integer, Map<String, ?>> pojos = getAllMap();
			int i = 111;
			for(Map<String, ?> pojo: pojos.values()) {
				((Map)pojo).put("id", new Integer(i++));
			}
			
			test.execute(hints, pojos);
			assertEquals(6, getCount());
			
			pojos = getAllMap();
			Set<Integer> ids = new HashSet<>();
			
			for(Map<String, ?> pojo: pojos.values()) {
				ids.add((Integer)pojo.get("id"));
			}
			
			assertTrue(ids.contains(111));
			assertTrue(ids.contains(112));
			assertTrue(ids.contains(113));
				
			if(enableKeyHolder){
				// You have to merge before get size
				assertEquals(3, hints.getKeyHolder().size());
			}
			assertEquals(3+3, getCount());
			
			if(this instanceof CombinedInsertTaskSqlSvrTest)
				SqlServerTestInitializer.turnOffIdentityInsert();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExecuteWithNonInsertable() throws SQLException {
		CombinedInsertTask<NonInsertableVersionModel> test = new CombinedInsertTask<>();
		DalParser<NonInsertableVersionModel> parser = new DalDefaultJpaParser<>(NonInsertableVersionModel.class, getDbName());
		test.initialize(parser);
		
		DalHints hints = new DalHints();
		if(enableKeyHolder)
			hints.setKeyHolder(new KeyHolder());
		try {
			test.execute(hints, getAllMap());
			if(enableKeyHolder){
				// You have to merge before get size
				assertEquals(3, hints.getKeyHolder().size());
			}
			assertEquals(3+3, getCount());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		
		Map<Integer, Map<String, ?>> pojos = getAllMap();
		for(Map<String, ?> pojo: pojos.values()) {
			assertNotNull(pojo.get("last_changed"));
		}
	}
	
	@Test
	public void testCreateMerger() {
		CombinedInsertTask<ClientTestModel> test = new CombinedInsertTask<>();
		assertNotNull(test.createMerger());
	}
}
