package com.ctrip.platform.dal.tester.shard;

import static com.ctrip.platform.dal.dao.unittests.DalTestHelper.deleteAllShardsByDb;
import static com.ctrip.platform.dal.dao.unittests.DalTestHelper.getCountByDb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalDetailResults;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public abstract class BaseDalTableDaoShardByDbTest {
	private boolean ASSERT_ALLOWED = false;

	public BaseDalTableDaoShardByDbTest(String databaseName, String generatedKey) {
		try {
			DalClientFactory.initClientFactory();
			DalParser<ClientTestModel> clientTestParser = new ClientTestDalParser(databaseName);
			dao = new DalTableDao<ClientTestModel>(clientTestParser);
			//ASSERT_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;
			GENERATED_KEY = generatedKey;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	private String GENERATED_KEY;
	
	private static DalTableDao<ClientTestModel> dao;

	// Only for sql server
	public KeyHolder createKeyHolder() {
		return ASSERT_ALLOWED ? new KeyHolder() : null;
	}
	

	public void assertResEquals(int exp, int res) {
		if(ASSERT_ALLOWED)
			Assert.assertEquals(exp, res);
	}
	
	public void assertResEquals(int[] exp, int[] res) {
		Assert.assertEquals(exp.length, res.length);
		
		if(ASSERT_ALLOWED)
			Assert.assertArrayEquals(exp, res);
	}

	public void assertResEquals(int exp, int[] res) {
		if(ASSERT_ALLOWED) {
			int total = 0;
			for(int t: res)total+=t;
			Assert.assertEquals(exp, total);
		}
	}
	
	/**
	 * Test Query by Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPk() throws SQLException {
		ClientTestModel model = null;
		
		for(int i = 0; i < mod; i++) {
			// By shard
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().inShard(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().inShard(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By shardValue
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().setShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().setShardValue(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().setShardColValue("index", String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().setShardColValue("index", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().setShardColValue("tableIndex", String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().setShardColValue("tableIndex", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}
	}

	/**
	 * Query by Entity with Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPkWithEntity() throws SQLException{
		ClientTestModel pk = null;
		ClientTestModel model = null;
		
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setId(1);

			// By shard
			model = dao.queryByPk(pk, new DalHints().inShard(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By shardValue
			model = dao.queryByPk(pk, new DalHints().setShardValue(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			model = dao.queryByPk(pk, new DalHints().setShardColValue("index", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			
			// By shardColValue
			model = dao.queryByPk(pk, new DalHints().setShardColValue("tableIndex", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By fields
			pk.setTableIndex(i);
			model = dao.queryByPk(pk, new DalHints());
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}
	}
	
	/**
	 * Query by Entity without Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPkWithEntityNoId() throws SQLException{
		ClientTestModel pk = new ClientTestModel();
		ClientTestModel model = null;
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setTableIndex(i);
			
			// By shard
			model = dao.queryByPk(pk, new DalHints().inShard(i));
			Assert.assertNull(model);

			// By shardValue
			model = dao.queryByPk(pk, new DalHints().setShardValue(i));
			Assert.assertNull(model);

			// By shardColValue
			model = dao.queryByPk(pk, new DalHints().setShardColValue("index", i));
			Assert.assertNull(model);
			
			// By shardColValue
			model = dao.queryByPk(pk, new DalHints().setShardColValue("tableIndex", i));
			Assert.assertNull(model);

			// By fields
			model = dao.queryByPk(pk, new DalHints());
			Assert.assertNull(model);
		}
	}

	/**
	 * Query against sample entity
	 * @throws SQLException
	 */
	@Test
	public void testQueryLike() throws SQLException{
		List<ClientTestModel> models = null;

		ClientTestModel pk = null;
		
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setType((short)1);

			// By shard
			models = dao.queryLike(pk, new DalHints().inShard(i));
			Assert.assertEquals(3, models.size());

			// By shardValue
			models = dao.queryLike(pk, new DalHints().setShardValue(i));
			Assert.assertEquals(3, models.size());

			// By shardColValue
			models = dao.queryLike(pk, new DalHints().setShardColValue("index", i));
			Assert.assertEquals(3, models.size());

			// By shardColValue
			models = dao.queryLike(pk, new DalHints().setShardColValue("tableIndex", i));
			Assert.assertEquals(3, models.size());

			// By fields
			pk.setTableIndex(i);
			models = dao.queryLike(pk, new DalHints());
			Assert.assertEquals(3, models.size());
		}
	}
	
	/**
	 * Query by Entity with where clause
	 * @throws SQLException
	 */
	@Test
	public void testQueryWithWhereClause() throws SQLException{
		List<ClientTestModel> models = null;
		
		for(int i = 0; i < mod; i++) {
			String whereClause = "type=? and id=?";
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);
			parameters.set(2, Types.INTEGER, 1);
			
			// By shard
			models = dao.query(whereClause, parameters, new DalHints().inShard(i));
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));

			// By shardValue
			models = dao.query(whereClause, parameters, new DalHints().setShardValue(i));
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));

			// By shardColValue
			models = dao.query(whereClause, parameters, new DalHints().setShardColValue("index", i));
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));

			// By shardColValue
			models = dao.query(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i));
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));

			// By parameters
			whereClause += " and tableIndex=?";
			parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "id", Types.SMALLINT, i + 1);
			parameters.set(3, "tableIndex", Types.SMALLINT, i);

			models = dao.query(whereClause, parameters, new DalHints());
			Assert.assertEquals(1, models.size());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));
		}
	}
	
	/**
	 * Test Query the first row with where clause
	 * @throws SQLException 
	 */
	@Test
	public void testQueryFirstWithWhereClause() throws SQLException{
		ClientTestModel model = null;
		for(int i = 0; i < mod; i++) {
			String whereClause = "type=?";

			// By shard
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);

			model = dao.queryFirst(whereClause, parameters, new DalHints().inShard(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By shardValue
			model = dao.queryFirst(whereClause, parameters, new DalHints().setShardValue(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By shardColValue
			model = dao.queryFirst(whereClause, parameters, new DalHints().setShardColValue("index", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			
			// By shardColValue
			model = dao.queryFirst(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By parameters
			whereClause += " and tableIndex=?";
			parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);
			model = dao.queryFirst(whereClause, parameters, new DalHints());
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}
	}
	
	/**
	 * Test Query the first row with where clause failed
	 * @throws SQLException
	 */
	@Test
	public void testQueryFirstWithWhereClauseFailed() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 10);
		try{
			dao.queryFirst(whereClause, parameters, new DalHints().inShard(1));
			Assert.fail();
		}catch(Throwable e) {
		}
	}
	
	/**
	 * Test Query the top rows with where clause
	 * @throws SQLException
	 */
	@Test
	public void testQueryTopWithWhereClause() throws SQLException{
		List<ClientTestModel> models = null;
		
		for(int i = 0; i < mod; i++) {
			String whereClause = "type=?";
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);

			// By shard
			models = dao.queryTop(whereClause, parameters, new DalHints().inShard(i), i + 1);
			Assert.assertEquals(i + 1, models.size());
			
			// By shardValue
			models = dao.queryTop(whereClause, parameters, new DalHints().setShardValue(i), i + 1);
			Assert.assertEquals(i + 1, models.size());

			// By shardColValue
			models = dao.queryTop(whereClause, parameters, new DalHints().setShardColValue("index", i), i + 1);
			Assert.assertEquals(i + 1, models.size());
			
			// By shardColValue
			models = dao.queryTop(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), i + 1);
			Assert.assertEquals(i + 1, models.size());

			whereClause += " and tableIndex=?";
			// By parameters
			parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);
			models = dao.queryTop(whereClause, parameters, new DalHints(), i + 1);
			Assert.assertEquals(i + 1, models.size());
		}
	}
	
	/**
	 * Test Query the top rows with where clause failed
	 * @throws SQLException
	 */
	@Test
	public void testQueryTopWithWhereClauseFailed() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 10);
		
		List<ClientTestModel> models;
		try {
			models = dao.queryTop(whereClause, parameters, new DalHints(), 2);
			Assert.fail();
		} catch (Exception e) {
		}
		
		models = dao.queryTop(whereClause, parameters, new DalHints().inShard(1), 2);
		Assert.assertTrue(null != models);
		Assert.assertEquals(0, models.size());
	}
	
	/**
	 * Test Query range of result with where clause
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClause() throws SQLException{
		List<ClientTestModel> models = null;
		String whereClause = "type=?";
		
		for(int i = 0; i < mod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);

			// By shard
			models = dao.queryFrom(whereClause, parameters, new DalHints().inShard(i), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());
		
			// By shardValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue(i), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());

			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("index", i), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());

			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());
		}

		whereClause += " and tableIndex=?";
		// By parameters
		for(int i = 0; i < mod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);

			models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());
		}
	}
	
	/**
	 * Test Query range of result with where clause failed when return not enough recodes
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClauseFailed() throws SQLException{
		String whereClause = "type=?";
		List<ClientTestModel> models = null;
		for(int i = 0; i < mod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);
			
			// By shard
			models = dao.queryFrom(whereClause, parameters, new DalHints().inShard(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(3, models.size());

			// By shardValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(3, models.size());

			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("index", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(3, models.size());
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(3, models.size());
		}
	}
	
	/**
	 * Test Query range of result with where clause when return empty collection
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClauseEmpty() throws SQLException{
		String whereClause = "type=?";
		List<ClientTestModel> models = null;
		for(int i = 0; i < mod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 10);
			
			// By shard
			models = dao.queryFrom(whereClause, parameters, new DalHints().inShard(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(0, models.size());

			// By shardValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardValue(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(0, models.size());

			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("index", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(0, models.size());
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(0, models.size());
		}
	}
	
	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	@Test
	public void testInsertSingle() throws SQLException{
		ClientTestModel model = new ClientTestModel();
		model.setQuantity(10 + 1%3);
		model.setType(((Number)(1%3)).shortValue());
		model.setAddress("CTRIP");
		int res;
		try {
			res = dao.insert(new DalHints(), model);
			Assert.fail();
		} catch (Exception e) {
		}
		
		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			res = dao.insert(new DalHints().inShard(i), model);
			assertResEquals(1, res);
			Assert.assertEquals(3 + j++ * 1, getCountByDb(dao, i));

			// By shardValue
			res = dao.insert(new DalHints().setShardValue(i), model);
			assertResEquals(1, res);
			Assert.assertEquals(3 + j++ * 1, getCountByDb(dao, i));

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), model);
			assertResEquals(1, res);
			Assert.assertEquals(3 + j++ * 1, getCountByDb(dao, i));
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), model);
			assertResEquals(1, res);
			Assert.assertEquals(3 + j++ * 1, getCountByDb(dao, i));
			
			// By fields
			model.setTableIndex(i);
			res = dao.insert(new DalHints(), model);
			assertResEquals(1, res);
			Assert.assertEquals(3 + j++ * 1, getCountByDb(dao, i));
		}
	}

	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleAsList() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}

		int[] res;
		try {
			res = dao.insert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			res = dao.insert(new DalHints().inShard(i), entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));

			// By shardValue
			res = dao.insert(new DalHints().setShardValue(i), entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints(), entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
		}
		
		deleteAllShardsByDb(dao, mod);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(new DalHints().continueOnError(), entities);
		assertResEquals(3, res);
		Assert.assertEquals(2, getCountByDb(dao, 0));
		Assert.assertEquals(1, getCountByDb(dao, 1));
	}
	
	/**
	 * Test Test Insert multiple entities one by one with continueOnError hints
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleAsListWithContinueOnErrorHints() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			if(i==1){
				model.setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
						+ "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
						+ "CTRIPCTRIPCTRIPCTRIP");
			}
			else{
				model.setAddress("CTRIP");
			}
			entities.add(model);
		}
		
		int[] res;
		try {
			res = dao.insert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			res = dao.insert(new DalHints().continueOnError().inShard(i), entities);
			assertResEquals(2, res);
			Assert.assertEquals(3 + j++ * 2, getCountByDb(dao, i));

			// By shardValue
			res = dao.insert(new DalHints().continueOnError().setShardValue(i), entities);
			assertResEquals(2, res);
			Assert.assertEquals(3 + j++ * 2, getCountByDb(dao, i));

			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("index", i), entities);
			assertResEquals(2, res);
			Assert.assertEquals(3 + j++ * 2, getCountByDb(dao, i));
			
			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("tableIndex", i), entities);
			assertResEquals(2, res);
			Assert.assertEquals(3 + j++ * 2, getCountByDb(dao, i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints().continueOnError(), entities);
			assertResEquals(2, res);
			Assert.assertEquals(3 + j++ * 2, getCountByDb(dao, i));
		}
		
		deleteAllShardsByDb(dao, mod);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(new DalHints().continueOnError(), entities);
		assertResEquals(2, res);
		Assert.assertEquals(2, getCountByDb(dao, 0));
		Assert.assertEquals(0, getCountByDb(dao, 1));
	}

	/**
	 * Test Insert multiple entities with key-holder
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleAsListWithKeyHolder() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}

		KeyHolder holder = createKeyHolder();
		int[] res;
		try {
			res = dao.insert(new DalHints(), holder, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}

		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			holder = createKeyHolder();
			res = dao.insert(new DalHints().inShard(i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);

			// By shardValue
			holder = createKeyHolder();
			res = dao.insert(new DalHints().setShardValue(i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
			
			// By shardColValue
			holder = createKeyHolder();
			res = dao.insert(new DalHints().setShardColValue("index", i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
			
			// By shardColValue
			holder = createKeyHolder();
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
			
			// By fields same shard
			holder = createKeyHolder();
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints(), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertResEquals(3, res);
			assertKeyHolder(holder);
		}
		
		deleteAllShardsByDb(dao, mod);
		
		// By fields not same shard
		holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(new DalHints(), holder, entities);
		assertResEquals(3, res);
		Assert.assertEquals(2, getCountByDb(dao, 0));
		Assert.assertEquals(1, getCountByDb(dao, 1));
		assertResEquals(3, res);
		assertKeyHolder(holder);
	}

	private void assertKeyHolder(KeyHolder holder) throws SQLException {
		if(!ASSERT_ALLOWED)
			return;
		Assert.assertEquals(3, holder.getKeyList().size());		 
		Assert.assertTrue(holder.getKey(0).longValue() > 0);
		Assert.assertTrue(holder.getKeyList().get(0).containsKey(GENERATED_KEY));
	}
	
	/**
	 * Test Insert multiple entities with one SQL Statement
	 * @throws SQLException
	 */
	@Test
	public void testCombinedInsert() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		KeyHolder holder = createKeyHolder();
		int res;
		try {
			res = dao.combinedInsert(new DalHints(), holder, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < mod; i++) {
			int j = 1;
			holder = null;
			// By shard
			holder = createKeyHolder();
			res = dao.combinedInsert(new DalHints().inShard(i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);

			// By shardValue
			holder = createKeyHolder();
			res = dao.combinedInsert(new DalHints().setShardValue(i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);

			// By shardColValue
			holder = createKeyHolder();
			res = dao.combinedInsert(new DalHints().setShardColValue("index", i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);

			// By shardColValue
			holder = createKeyHolder();
			res = dao.combinedInsert(new DalHints().setShardColValue("tableIndex", i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			assertKeyHolder(holder);
		}
		
		// For combined insert, the shard id must be defined or change bd deduced.
	}
	
	/**
	 * Test Batch Insert multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testBatchInsert() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}

		int[] res;
		try {
			res = dao.batchInsert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < mod; i++) {
			int j = 1;
			// By shard
			res = dao.batchInsert(new DalHints().inShard(i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By shardValue
			res = dao.batchInsert(new DalHints().setShardValue(i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));

			// By shardColValue
			res = dao.batchInsert(new DalHints().setShardColValue("index", i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
			
			// By shardColValue
			res = dao.batchInsert(new DalHints().setShardColValue("tableIndex", i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			Assert.assertEquals(3 + j++ * 3, getCountByDb(dao, i));
		}
		
		// For combined insert, the shard id must be defined or change bd deduced.
	}
	
	public abstract void insertBack();
	
	/**
	 * Test delete multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testDeleteMultiple() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		int[] res;
		// By shard
		Assert.assertEquals(3, getCountByDb(dao, 0));
		res = dao.delete(new DalHints().inShard(0), entities);
		assertResEquals(3, res);
		Assert.assertEquals(0, getCountByDb(dao, 0));

		// By shardValue
		Assert.assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(new DalHints().setShardValue(1), entities);
		assertResEquals(3, res);
		Assert.assertEquals(0, getCountByDb(dao, 1));

		insertBack();
		
		// By shardColValue
		Assert.assertEquals(3, getCountByDb(dao, 0));
		res = dao.delete(new DalHints().setShardColValue("index", 2), entities);
		assertResEquals(3, res);
		Assert.assertEquals(0, getCountByDb(dao, 0));
		
		// By shardColValue
		Assert.assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(new DalHints().setShardColValue("tableIndex", 3), entities);
		assertResEquals(3, res);
		Assert.assertEquals(0, getCountByDb(dao, 1));
		
		// By fields same shard
		// holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		dao.insert(new DalHints(), entities);
		Assert.assertEquals(2, getCountByDb(dao, 0));
		Assert.assertEquals(1, getCountByDb(dao, 1));
		entities.set(0, dao.queryTop("1=1", new StatementParameters(), new DalHints().inShard(0), 2).get(0));
		entities.set(1, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inShard(1)));
		entities.set(2, dao.queryTop("1=1", new StatementParameters(), new DalHints().inShard(0), 2).get(1));
		res = dao.delete(new DalHints(), entities);
		assertResEquals(3, res);
		Assert.assertEquals(0, getCountByDb(dao, 0));
		Assert.assertEquals(0, getCountByDb(dao, 1));
	}
	
	/**
	 * Test batch delete multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testBatchDelete() throws SQLException{
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		int[] res;
		try {
			res = dao.batchDelete(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		// By shard
		Assert.assertEquals(3, getCountByDb(dao, 0));
		res = dao.batchDelete(new DalHints().inShard(0), entities);
		assertResEquals(new int[]{1,1,1}, res);
		Assert.assertEquals(0, getCountByDb(dao, 0));

		// By shardValue
		Assert.assertEquals(3, getCountByDb(dao, 1));
		res = dao.batchDelete(new DalHints().setShardValue(1), entities);
		assertResEquals(new int[]{1,1,1}, res);
		Assert.assertEquals(0, getCountByDb(dao, 1));
		
		insertBack();

		// By shardColValue
		Assert.assertEquals(3, getCountByDb(dao, 0));
		res = dao.batchDelete(new DalHints().setShardColValue("index", 2), entities);
		assertResEquals(new int[]{1,1,1}, res);
		Assert.assertEquals(0, getCountByDb(dao, 0));
		
		// By shardColValue
		Assert.assertEquals(3, getCountByDb(dao, 1));
		res = dao.batchDelete(new DalHints().setShardColValue("tableIndex", 3), entities);
		assertResEquals(new int[]{1,1,1}, res);
		Assert.assertEquals(0, getCountByDb(dao, 1));
		
		// By fields same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(0);
		entities.get(2).setTableIndex(0);
		dao.insert(new DalHints(), entities);
		Assert.assertEquals(3, getCountByDb(dao, 0));
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));
		res = dao.batchDelete(new DalHints().inShard(0), result);
		assertResEquals(new int[]{1,1,1}, res);
		Assert.assertEquals(0, getCountByDb(dao, 0));
	}
	
	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	@Test
	public void testUpdateMultiple() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		
		int[] ress;
		try {
			ress = dao.update(hints, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		int res;
		// By shard
		entities.get(0).setAddress("test1");
		res = dao.update(new DalHints().inShard(0), entities.get(0));
		assertResEquals(1, res);
		Assert.assertEquals("test1", dao.queryByPk(1, hints.inShard(0)).getAddress());

		// By shardValue
		entities.get(1).setQuantity(-11);
		res = dao.update(new DalHints().setShardValue(1), entities.get(1));
		assertResEquals(1, res);
		Assert.assertEquals(-11, dao.queryByPk(2, hints.inShard(1)).getQuantity().intValue());
		
		// By shardColValue
		entities.get(2).setType((short)3);
		res = dao.update(new DalHints().setShardColValue("index", 2), entities.get(2));
		assertResEquals(1, res);
		Assert.assertEquals((short)3, dao.queryByPk(3, hints.inShard(0)).getType().shortValue());

		// By shardColValue
		entities.get(0).setAddress("testa");
		res = dao.update(new DalHints().setShardColValue("tableIndex", 3), entities.get(0));
		assertResEquals(1, res);
		Assert.assertEquals("testa", dao.queryByPk(1, hints.inShard(1)).getAddress());
		
		// By fields same shard
		// holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(0).setAddress("1234");
		entities.get(1).setTableIndex(0);
		entities.get(1).setAddress("1234");
		entities.get(2).setTableIndex(0);
		entities.get(2).setAddress("1234");
		ress = dao.update(new DalHints(), entities);
		assertResEquals(3, res);
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));
		for(ClientTestModel m: result)
			Assert.assertEquals("1234", m.getAddress());
	}
	
	@Test
	public void testBatchUpdateMultiple() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		
		int[] ress;
		try {
			ress = dao.batchUpdate(hints, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		// By fields same shard
		// holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(0).setAddress("1234");
		entities.get(1).setTableIndex(0);
		entities.get(1).setAddress("1234");
		entities.get(2).setTableIndex(0);
		entities.get(2).setAddress("1234");
		ress = dao.batchUpdate(new DalHints(), entities);
		assertResEquals(3, ress);
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inShard(0));
		for(ClientTestModel m: result)
			Assert.assertEquals("1234", m.getAddress());
	}
	
	/**
	 * Test delete entities with where clause and parameters
	 * @throws SQLException
	 */
	@Test
	public void testDeleteWithWhereClause() throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);

		DalHints hints = new DalHints();
		int res;
		try {
			res = dao.delete(whereClause, parameters, hints);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		// By shard
		res = dao.delete(whereClause, parameters, new DalHints().inShard(0));
		assertResEquals(3, res);
		Assert.assertEquals(0, dao.query(whereClause, parameters, new DalHints().inShard(0)).size());

		// By shardValue
		Assert.assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, new DalHints().setShardValue(1));
		assertResEquals(3, res);
		Assert.assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardValue(1)).size());
		
		insertBack();

		// By shardColValue
		Assert.assertEquals(3, getCountByDb(dao, 0));
		res = dao.delete(whereClause, parameters, new DalHints().setShardColValue("index", 2));
		assertResEquals(3, res);
		Assert.assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardColValue("index", 2)).size());
		
		// By shardColValue
		Assert.assertEquals(3, getCountByDb(dao, 1));
		res = dao.delete(whereClause, parameters, new DalHints().setShardColValue("tableIndex", 3));
		assertResEquals(3, res);
		Assert.assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardColValue("tableIndex", 3)).size());
	}
	
	/**
	 * Test plain update with SQL
	 * @throws SQLException
	 */
	@Test
	public void testUpdatePlain() throws SQLException{
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int res;
		try {
			res = dao.update(sql, parameters, hints);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		// By shard
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().inShard(0));
		assertResEquals(1, res);
		Assert.assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inShard(0)).getAddress());

		// By shardValue
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().setShardValue(1));
		assertResEquals(1, res);
		Assert.assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardValue(1)).getAddress());
		
		// By shardColValue
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().setShardColValue("index", 2));
		assertResEquals(1, res);
		Assert.assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("index", 2)).getAddress());
		
		// By shardColValue
		sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().setShardColValue("tableIndex", 3));
		assertResEquals(1, res);
		Assert.assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("tableIndex", 3)).getAddress());

	}
	
		@Test
	public void testCrossShardInsert() {
		try {
			int res = 0;
			deleteAllShardsByDb(dao, mod);
			
			ClientTestModel p = new ClientTestModel();
			
			ClientTestModel[] pList = new ClientTestModel[6];
			p = new ClientTestModel();
			p.setId(1);
			p.setAddress("aaa");
			p.setTableIndex(0);
			pList[0] = p;
			
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(1);
			pList[1] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(2);
			pList[2] = p;
			
			p = new ClientTestModel();
			p.setId(4);
			p.setAddress("aaa");
			p.setTableIndex(3);
			pList[3] = p;
			
			p = new ClientTestModel();
			p.setId(5);
			p.setAddress("aaa");
			p.setTableIndex(4);
			pList[4] = p;
			
			p = new ClientTestModel();
			p.setId(5);
			p.setAddress("aaa");
			p.setTableIndex(5);
			pList[5] = p;
			
			Assert.assertEquals(0, getCountByDb(dao, 0));
			Assert.assertEquals(0, getCountByDb(dao, 1));

			KeyHolder keyholder = createKeyHolder();
			DalHints hints = new DalHints();
			res = dao.combinedInsert(hints, keyholder, Arrays.asList(pList));
			assertResEquals(6, res);
			Assert.assertEquals(3, getCountByDb(dao, 0));
			Assert.assertEquals(3, getCountByDb(dao, 1));
			assertKeyHolderCrossShard(keyholder, hints.getDetailResults());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	public void assertKeyHolderCrossShard(KeyHolder holder, DalDetailResults<KeyHolder> detailResults) throws SQLException {
		if(!ASSERT_ALLOWED)
			return;

		Assert.assertEquals(6, holder.size());
		for(int i = 0; i < mod; i++) {
			Assert.assertEquals(3, detailResults.getResultByDb(String.valueOf(i)).size());
		}
		for(int i = 0; i < 6; i++) {
			Assert.assertTrue(holder.getKey(i).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(i).containsKey(GENERATED_KEY));
		}
	}
		
		
	@Test
	public void testCrossShardBatchInsert() {
		try {
			Map<String, int[]>  res;
			deleteAllShardsByDb(dao, mod);
			
			ClientTestModel p = new ClientTestModel();
			
			ClientTestModel[] pList = new ClientTestModel[6];
			p = new ClientTestModel();
			p.setId(1);
			p.setAddress("aaa");
			p.setTableIndex(0);
			pList[0] = p;
			
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(1);
			pList[1] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(2);
			pList[2] = p;
			
			p = new ClientTestModel();
			p.setId(4);
			p.setAddress("aaa");
			p.setTableIndex(3);
			pList[3] = p;
			
			p = new ClientTestModel();
			p.setId(5);
			p.setAddress("aaa");
			p.setTableIndex(4);
			pList[4] = p;
			
			p = new ClientTestModel();
			p.setId(5);
			p.setAddress("aaa");
			p.setTableIndex(5);
			pList[5] = p;
			
			DalHints hints = new DalHints();
			int[] resx = dao.batchInsert(hints, Arrays.asList(pList));
			new DalHints().get(DalHintEnum.detailResults);
			
			Assert.assertEquals(6, resx.length);
			Assert.assertEquals(3, getCountByDb(dao, 0));
			Assert.assertEquals(3, getCountByDb(dao, 1));
			
			assertResEquals(new int[]{1, 1, 1}, ((int[])hints.getDetailResults().getResultByDb("0")));
			assertResEquals(new int[]{1, 1, 1}, ((int[])hints.getDetailResults().getResultByDb("1")));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testCrossShardDelete() {
		try {
			int[] res;
			ClientTestModel p = new ClientTestModel();
			
			ClientTestModel[] pList = new ClientTestModel[6];
			p = new ClientTestModel();
			p.setId(1);
			p.setAddress("aaa");
			p.setTableIndex(0);
			pList[0] = p;
			
			p = new ClientTestModel();
			p.setId(1);
			p.setAddress("aaa");
			p.setTableIndex(1);
			pList[1] = p;
			
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(2);
			pList[2] = p;
			
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(3);
			pList[3] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(5);
			pList[4] = p;
			
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			p.setTableIndex(6);
			pList[5] = p;
			
			DalHints hints = new DalHints();
			res = dao.batchDelete(hints, Arrays.asList(pList));
			Assert.assertEquals(0, getCountByDb(dao, 0));
			Assert.assertEquals(0, getCountByDb(dao, 1));
			
			assertResEquals(new int[]{1, 1, 1}, ((int[])hints.getDetailResults().getResultByDb("0")));
			assertResEquals(new int[]{1, 1, 1}, ((int[])hints.getDetailResults().getResultByDb("1")));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private static class ClientTestDalParser implements DalParser<ClientTestModel>{
		private String databaseName;
		private static final String tableName= "dal_client_test";
		private static final String[] columnNames = new String[]{
			"id","quantity","tableIndex","type","address","last_changed"
		};
		private static final String[] primaryKeyNames = new String[]{"id"};
		private static final int[] columnTypes = new int[]{
			Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR, Types.TIMESTAMP
		};
		
		public ClientTestDalParser(String databaseName) {
			this.databaseName = databaseName;
		}

		@Override
		public ClientTestModel map(ResultSet rs, int rowNum)
				throws SQLException {
			ClientTestModel model = new ClientTestModel();
			model.setId(rs.getInt(1));
			model.setQuantity(rs.getInt(2));
			model.setTableIndex(rs.getInt(3));
			model.setType(rs.getShort(4));
			model.setAddress(rs.getString(5));
			model.setLastChanged(rs.getTimestamp(6));
			return model;
		}

		@Override
		public String getDatabaseName() {
			return databaseName;
		}

		@Override
		public String getTableName() {
			return tableName;
		}

		@Override
		public String[] getColumnNames() {
			return columnNames;
		}

		@Override
		public String[] getPrimaryKeyNames() {
			return primaryKeyNames;
		}

		@Override
		public int[] getColumnTypes() {
			return columnTypes;
		}

		@Override
		public boolean isAutoIncrement() {
			return true;
		}

		@Override
		public Number getIdentityValue(ClientTestModel pojo) {
			return pojo.getId();
		}

		@Override
		public Map<String, ?> getPrimaryKeys(ClientTestModel pojo) {
			Map<String, Object> keys = new LinkedHashMap<String, Object>();
			keys.put("id", pojo.getId());
			return keys;
		}

		@Override
		public Map<String, ?> getFields(ClientTestModel pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("id", pojo.getId());
			map.put("quantity", pojo.getQuantity());
			map.put("tableIndex", pojo.getTableIndex());
			map.put("type", pojo.getType());
			map.put("address", pojo.getAddress());
			map.put("last_changed", pojo.getLastChanged());
			return map;
		}
		
	}
	
	private static class ClientTestModel {
		private Integer id;
		private Integer quantity;
		private Integer tableIndex;
		private Short type;
		private String address;
		private Timestamp lastChanged;

		public Integer getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public Integer getTableIndex() {
			return tableIndex;
		}

		public void setTableIndex(int tableIndex) {
			this.tableIndex = tableIndex;
		}
		
		public Short getType() {
			return type;
		}

		public void setType(short type) {
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
