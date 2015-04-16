package com.ctrip.platform.dal.tester.shard;

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
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public abstract class BaseDalTabelDaoTableShardTest {
	private boolean ASSERT_ALLOWED = true;

	public BaseDalTabelDaoTableShardTest(String databaseName) {
		try {
			DalClientFactory.initClientFactory();
			DalParser<ClientTestModel> clientTestParser = new ClientTestDalParser(databaseName);
			dao = new DalTableDao<ClientTestModel>(clientTestParser);
			ASSERT_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 4;

	private static DalTableDao<ClientTestModel> dao;
	
	public void assertResEquals(int exp, int res) {
		if(ASSERT_ALLOWED)
			Assert.assertEquals(exp, res);
	}
	
	/**
	 * Test Query by Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPk() throws SQLException {
		ClientTestModel model = null;
		
		for(int i = 0; i < mod; i++) {
			// By tabelShard
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().inTableShard(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().inTableShard(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By tableShardValue
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().setTableShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().setTableShardValue(i));
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

			// By tabelShard
			model = dao.queryByPk(pk, new DalHints().inTableShard(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By tableShardValue
			model = dao.queryByPk(pk, new DalHints().setTableShardValue(i));
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
		// By fields
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setTableIndex(i);
			if(i%2 == 0)
				model = dao.queryByPk(pk, new DalHints());
			else
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

			// By tabelShard
			models = dao.queryLike(pk, new DalHints().inTableShard(i));
			Assert.assertEquals(i + 1, models.size());

			// By tableShardValue
			models = dao.queryLike(pk, new DalHints().setTableShardValue(i));
			Assert.assertEquals(i + 1, models.size());

			// By shardColValue
			models = dao.queryLike(pk, new DalHints().setShardColValue("index", i));
			Assert.assertEquals(i + 1, models.size());

			// By shardColValue
			models = dao.queryLike(pk, new DalHints().setShardColValue("tableIndex", i));
			Assert.assertEquals(i + 1, models.size());

			// By fields
			pk.setTableIndex(i);
			models = dao.queryLike(pk, new DalHints());
			Assert.assertEquals(i + 1, models.size());
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
			
			// By tabelShard
			models = dao.query(whereClause, parameters, new DalHints().inTableShard(i));
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));

			// By tableShardValue
			models = dao.query(whereClause, parameters, new DalHints().setTableShardValue(i));
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
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
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

			// By tabelShard
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);

			model = dao.queryFirst(whereClause, parameters, new DalHints().inTableShard(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());

			// By tableShardValue
			model = dao.queryFirst(whereClause, parameters, new DalHints().setTableShardValue(i));
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
			dao.queryFirst(whereClause, parameters, new DalHints().inTableShard(1));
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

			// By tabelShard
			models = dao.queryTop(whereClause, parameters, new DalHints().inTableShard(i), i + 1);
			Assert.assertEquals(i + 1, models.size());
			
			// By tableShardValue
			models = dao.queryTop(whereClause, parameters, new DalHints().setTableShardValue(i), i + 1);
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
		
		models = dao.queryTop(whereClause, parameters, new DalHints().inTableShard(1), 2);
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

			// By tabelShard
			models = dao.queryFrom(whereClause, parameters, new DalHints().inTableShard(i), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());
		
			// By tableShardValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setTableShardValue(i), 0, i + 1);
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
			
			// By tabelShard
			models = dao.queryFrom(whereClause, parameters, new DalHints().inTableShard(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(i + 1, models.size());

			// By tableShardValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setTableShardValue(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(i + 1, models.size());

			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("index", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(i + 1, models.size());
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setShardColValue("tableIndex", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(i + 1, models.size());
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
			
			// By tabelShard
			models = dao.queryFrom(whereClause, parameters, new DalHints().inTableShard(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(0, models.size());

			// By tableShardValue
			models = dao.queryFrom(whereClause, parameters, new DalHints().setTableShardValue(i), 0, 10);
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
			// By tabelShard
			res = dao.insert(new DalHints().inTableShard(i), model);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(i));

			// By tableShardValue
			res = dao.insert(new DalHints().setTableShardValue(i), model);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(i));

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), model);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(i));
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), model);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(i));
			
			// By fields
			model.setTableIndex(i);
			res = dao.insert(new DalHints(), model);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(i));
		}
	}
	
	private void deleteAllShards() throws SQLException {
		for(int i = 0; i < mod; i++) {
			int j = 1;
			dao.delete("1=1", new StatementParameters(), new DalHints().inTableShard(i));
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
			// By tabelShard
			res = dao.insert(new DalHints().inTableShard(i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));

			// By tableShardValue
			res = dao.insert(new DalHints().setTableShardValue(i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints(), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
		}
		
		deleteAllShards();
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		dao.insert(new DalHints().continueOnError(), entities);
		Assert.assertEquals(1, getCount(0));
		Assert.assertEquals(1, getCount(1));
		Assert.assertEquals(1, getCount(2));
	}
	
	private int getCount(int shardId) throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(shardId)).size();
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
			// By tabelShard
			dao.insert(new DalHints().continueOnError().inTableShard(i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(i));

			// By tableShardValue
			res = dao.insert(new DalHints().continueOnError().setTableShardValue(i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(i));

			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("index", i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(i));
			
			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints().continueOnError(), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(i));
		}
		
		deleteAllShards();
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		dao.insert(new DalHints().continueOnError(), entities);
		Assert.assertEquals(1, getCount(0));
		Assert.assertEquals(0, getCount(1));
		Assert.assertEquals(1, getCount(2));
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

		KeyHolder holder = new KeyHolder();
		int[] res;
		try {
			res = dao.insert(new DalHints(), holder, entities);
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(int i = 0; i < mod; i++) {
			int j = 1;
			holder = null;
			// By tabelShard
			// holder = new KeyHolder();
			res = dao.insert(new DalHints().inTableShard(i), holder, entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
//			Assert.assertEquals(3, res);
//			Assert.assertEquals(3, holder.getKeyList().size());		 
//			Assert.assertTrue(holder.getKey(0).longValue() > 0);
//			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));

			// By tableShardValue
			// holder = new KeyHolder();
			res = dao.insert(new DalHints().setTableShardValue(i), holder, entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
			// By shardColValue
			// holder = new KeyHolder();
			res = dao.insert(new DalHints().setShardColValue("index", i), holder, entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
			// By shardColValue
			// holder = new KeyHolder();
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), holder, entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
			// By fields same shard
			// holder = new KeyHolder();
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints(), holder, entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
		}
		
		deleteAllShards();
		
		// By fields not same shard
		holder = new KeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(new DalHints(), null, entities);
		Assert.assertEquals(1, getCount(0));
		Assert.assertEquals(1, getCount(1));
		Assert.assertEquals(1, getCount(2));
//		Assert.assertEquals(3, res);
//		Assert.assertEquals(3, holder.getKeyList().size());		 
//		Assert.assertTrue(holder.getKey(0).longValue() > 0);
//		Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
	}
	
	/**
	 * Test Insert multiple entities with one SQL Statement
	 * @throws SQLException
	 */
	@Test
	public void testCombinedInsert() throws SQLException{
		ClientTestModel[] entities = new ClientTestModel[3];
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities[i] = model;
		}
		
		KeyHolder holder = new KeyHolder();
		int res;
		try {
			res = dao.combinedInsert(new DalHints(), holder, Arrays.asList(entities));
			Assert.fail();
		} catch (Exception e) {
		}
		
		for(int i = 0; i < mod; i++) {
			int j = 1;
			holder = null;
			// By tabelShard
			// holder = new KeyHolder();
			res = dao.combinedInsert(new DalHints().inTableShard(i), holder, Arrays.asList(entities));
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
			// By tableShardValue
			// holder = new KeyHolder();
			res = dao.combinedInsert(new DalHints().setTableShardValue(i), holder, Arrays.asList(entities));
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));

			// By shardColValue
			// holder = new KeyHolder();
			res = dao.combinedInsert(new DalHints().setShardColValue("index", i), holder, Arrays.asList(entities));
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
			// By shardColValue
			// holder = new KeyHolder();
			res = dao.combinedInsert(new DalHints().setShardColValue("tableIndex", i), holder, Arrays.asList(entities));
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
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
			// By tabelShard
			res = dao.batchInsert(new DalHints().inTableShard(i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
			// By tableShardValue
			res = dao.batchInsert(new DalHints().setTableShardValue(i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));

			// By shardColValue
			res = dao.batchInsert(new DalHints().setShardColValue("index", i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
			
			// By shardColValue
			res = dao.batchInsert(new DalHints().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(i));
		}
		
		// For combined insert, the shard id must be defined or change bd deduced.
	}
	
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
		// By tabelShard
		Assert.assertEquals(1, getCount(0));
		res = dao.delete(new DalHints().inTableShard(0), entities);
		Assert.assertEquals(0, getCount(0));

		// By tableShardValue
		Assert.assertEquals(2, getCount(1));
		res = dao.delete(new DalHints().setTableShardValue(1), entities);
		Assert.assertEquals(0, getCount(1));
		
		// By shardColValue
		Assert.assertEquals(3, getCount(2));
		res = dao.delete(new DalHints().setShardColValue("index", 2), entities);
		Assert.assertEquals(0, getCount(2));
		
		// By shardColValue
		Assert.assertEquals(4, getCount(3));
		res = dao.delete(new DalHints().setShardColValue("tableIndex", 3), entities);
		Assert.assertEquals(1, getCount(3));
		
		// By fields same shard
		// holder = new KeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		dao.insert(new DalHints(), entities);
		Assert.assertEquals(1, getCount(0));
		Assert.assertEquals(1, getCount(1));
		Assert.assertEquals(1, getCount(2));
		entities.set(0, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inTableShard(0)));
		entities.set(1, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inTableShard(1)));
		entities.set(2, dao.queryFirst("1=1", new StatementParameters(), new DalHints().inTableShard(2)));
		res = dao.delete(new DalHints(), entities);
		Assert.assertEquals(0, getCount(0));
		Assert.assertEquals(0, getCount(1));
		Assert.assertEquals(0, getCount(2));
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
		
		// By tabelShard
		Assert.assertEquals(1, getCount(0));
		res = dao.batchDelete(new DalHints().inTableShard(0), entities);
		Assert.assertEquals(0, getCount(0));

		// By tableShardValue
		Assert.assertEquals(2, getCount(1));
		res = dao.batchDelete(new DalHints().setTableShardValue(1), entities);
		Assert.assertEquals(0, getCount(1));
		
		// By shardColValue
		Assert.assertEquals(3, getCount(2));
		res = dao.batchDelete(new DalHints().setShardColValue("index", 2), entities);
		Assert.assertEquals(0, getCount(2));
		
		// By shardColValue
		Assert.assertEquals(4, getCount(3));
		res = dao.batchDelete(new DalHints().setShardColValue("tableIndex", 3), entities);
		Assert.assertEquals(1, getCount(3));
		
		// By fields same shard
		// holder = new KeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(0);
		entities.get(2).setTableIndex(0);
		dao.insert(new DalHints(), entities);
		Assert.assertEquals(3, getCount(0));
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
		res = dao.batchDelete(new DalHints().inTableShard(0), result);
		Assert.assertEquals(0, getCount(0));
	}
	
	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	@Test
	public void testUpdateMultiple() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		
		int[] res;
		try {
			res = dao.update(hints, entities);
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// By tabelShard
		entities.get(0).setAddress("test1");
		dao.update(new DalHints().inTableShard(0), entities.get(0));
		Assert.assertEquals("test1", dao.queryByPk(1, hints.inTableShard(0)).getAddress());

		// By tableShardValue
		entities.get(1).setQuantity(-11);
		dao.update(new DalHints().setTableShardValue(1), entities.get(1));
		Assert.assertEquals(-11, dao.queryByPk(2, hints.inTableShard(1)).getQuantity().intValue());
		
		// By shardColValue
		entities.get(2).setType((short)3);
		dao.update(new DalHints().setShardColValue("index", 2), entities.get(2));
		Assert.assertEquals((short)3, dao.queryByPk(3, hints.inTableShard(2)).getType().shortValue());

		// By shardColValue
		entities.get(3).setAddress("testa");
		res = dao.update(new DalHints().setShardColValue("tableIndex", 3), entities);
		Assert.assertEquals("testa", dao.queryByPk(4, hints.inTableShard(3)).getAddress());
		
		// By fields same shard
		// holder = new KeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(0).setAddress("1234");
		entities.get(1).setTableIndex(0);
		entities.get(1).setAddress("1234");
		entities.get(2).setTableIndex(0);
		entities.get(2).setAddress("1234");
		entities.get(3).setTableIndex(0);
		entities.get(3).setAddress("1234");
		dao.update(new DalHints(), entities);
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
		for(ClientTestModel m: result)
			Assert.assertEquals("1234", m.getAddress());
	}
	
	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	@Test
	public void testBatchUpdate() throws SQLException{
		DalHints hints = new DalHints();
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setId(i+1);
			model.setAddress("CTRIP");
			entities.add(model);
		}
		
		
		int[] res;
		try {
			res = dao.batchUpdate(hints, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		// By fields same shard
		// holder = new KeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(0).setAddress("1234");
		entities.get(1).setTableIndex(0);
		entities.get(1).setAddress("1234");
		entities.get(2).setTableIndex(0);
		entities.get(2).setAddress("1234");
		entities.get(3).setTableIndex(0);
		entities.get(3).setAddress("1234");
		dao.batchUpdate(new DalHints(), entities);
		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), new DalHints().inTableShard(0));
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
		
		// By tabelShard
		res = dao.delete(whereClause, parameters, new DalHints().inTableShard(0));
		Assert.assertEquals(0, dao.query(whereClause, parameters, new DalHints().inTableShard(0)).size());

		// By tableShardValue
		Assert.assertEquals(2, getCount(1));
		res = dao.delete(whereClause, parameters, new DalHints().setTableShardValue(1));
		Assert.assertEquals(0, dao.query(whereClause, parameters, new DalHints().setTableShardValue(1)).size());
		
		// By shardColValue
		Assert.assertEquals(3, getCount(2));
		res = dao.delete(whereClause, parameters, new DalHints().setShardColValue("index", 2));
		Assert.assertEquals(0, dao.query(whereClause, parameters, new DalHints().setShardColValue("index", 2)).size());
		
		// By shardColValue
		Assert.assertEquals(4, getCount(3));
		res = dao.delete(whereClause, parameters, new DalHints().setShardColValue("tableIndex", 3));
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
		
		// By tabelShard
		sql = "UPDATE " + TABLE_NAME
				+ "_0 SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, new DalHints().inTableShard(0));
		Assert.assertEquals("CTRIP", dao.queryByPk(1, new DalHints().inTableShard(0)).getAddress());

		// By tableShardValue
		sql = "UPDATE " + TABLE_NAME
				+ "_1 SET address = 'CTRIP' WHERE id = 1";
		Assert.assertEquals(2, getCount(1));
		res = dao.update(sql, parameters, new DalHints().setTableShardValue(1));
		Assert.assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setTableShardValue(1)).getAddress());
		
		// By shardColValue
		sql = "UPDATE " + TABLE_NAME
				+ "_2 SET address = 'CTRIP' WHERE id = 1";
		Assert.assertEquals(3, getCount(2));
		res = dao.update(sql, parameters, new DalHints().setShardColValue("index", 2));
		Assert.assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("index", 2)).getAddress());
		
		// By shardColValue
		sql = "UPDATE " + TABLE_NAME
				+ "_3 SET address = 'CTRIP' WHERE id = 1";
		Assert.assertEquals(4, getCount(3));
		res = dao.update(sql, parameters, new DalHints().setShardColValue("tableIndex", 3));
		Assert.assertEquals("CTRIP", dao.queryByPk(1, new DalHints().setShardColValue("tableIndex", 3)).getAddress());

	}
	
		@Test
	public void testCrossShardInsert() {
		try {
			deleteAllShards();
			
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
			
			KeyHolder keyHolder = null;//new KeyHolder();
			dao.combinedInsert(new DalHints(), keyHolder, Arrays.asList(pList));
			Assert.assertEquals(2, getCount(0));
			Assert.assertEquals(2, getCount(1));
			Assert.assertEquals(1, getCount(2));
			Assert.assertEquals(1, getCount(3));
		} catch (Exception e) {
			
			Assert.fail();
		}
	}

	@Test
	public void testCrossShardBatchInsert() {
		try {
			deleteAllShards();
			
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
			
			dao.batchInsert(new DalHints(), Arrays.asList(pList));
			Assert.assertEquals(2, getCount(0));
			Assert.assertEquals(2, getCount(1));
			Assert.assertEquals(1, getCount(2));
			Assert.assertEquals(1, getCount(3));
		} catch (Exception e) {
			
			Assert.fail();
		}
	}
	
	@Test
	public void testCrossShardDelete() {
		try {
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
			p.setId(1);
			p.setAddress("aaa");
			p.setTableIndex(2);
			pList[2] = p;
			
			p = new ClientTestModel();
			p.setId(1);
			p.setAddress("aaa");
			p.setTableIndex(3);
			pList[3] = p;
			
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(5);
			pList[4] = p;
			
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			p.setTableIndex(6);
			pList[5] = p;
			
			dao.batchDelete(new DalHints(), Arrays.asList(pList));
			Assert.assertEquals(0, getCount(0));
			Assert.assertEquals(0, getCount(1));
			Assert.assertEquals(1, getCount(2));
			Assert.assertEquals(3, getCount(3));
			
		} catch (Exception e) {
			
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