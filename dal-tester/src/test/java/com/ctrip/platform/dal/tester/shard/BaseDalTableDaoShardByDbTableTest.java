package com.ctrip.platform.dal.tester.shard;

import static com.ctrip.platform.dal.dao.unittests.DalTestHelper.deleteAllShardsByDbTable;
import static com.ctrip.platform.dal.dao.unittests.DalTestHelper.getCountByDbTable;

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

public abstract class BaseDalTableDaoShardByDbTableTest {
	public final static String TABLE_NAME = "dal_client_test";
	public final static int mod = 2;
	public final static int tableMod = 4;
	
	private static DalTableDao<ClientTestModel> dao;
	
	public BaseDalTableDaoShardByDbTableTest(String databaseName) {
		try {
			DalClientFactory.initClientFactory();
			DalParser<ClientTestModel> clientTestParser = new ClientTestDalParser(databaseName);
			dao = new DalTableDao<ClientTestModel>(clientTestParser);
			ASSERT_ALLOWED = dao.getDatabaseCategory() == DatabaseCategory.MySql;
		} catch (Exception e) {
			
		}
	}
	
	public abstract void reset() throws SQLException;
	
	private boolean ASSERT_ALLOWED = true;
	
	public void assertResEquals(int exp, int res) {
		if(ASSERT_ALLOWED)
			Assert.assertEquals(exp, res);
	}
	
	public void assertResEquals(int exp, int[] res) {
		if(ASSERT_ALLOWED) {
			int total = 0;
			for(int t: res)total+=t;
			Assert.assertEquals(exp, total);
		}
	}
	
	public int getCount(int shardId, int tableShardId) throws SQLException {
		return getCountByDbTable(dao, shardId, tableShardId);
	}

	public void deleteAllShards(int shardId) throws SQLException {
		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			dao.delete("1=1", new StatementParameters(), new DalHints().inShard(shardId).inTableShard(i));
		}
	}
	
	public ClientTestModel[] createNoId(int count) throws SQLException {
		ClientTestModel[] entities = new ClientTestModel[count];
		for (int i = 0; i < count; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities[i] = model;
		}
		return entities;
	}
	
	public List<ClientTestModel> create(int count) throws SQLException {
		ClientTestModel[] entities = createNoId(count);
		for (int i = 0; i < count; i++) {
			entities[i].setId(i + 1);
		}
		return Arrays.asList(entities);
	}
	
	public List<ClientTestModel> createListNoId(int count) throws SQLException {
		List<ClientTestModel> entities = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities.add(model);
		}
		return entities;
	}
	
	List<ClientTestModel> getModels(int shardId, int tableShardId) throws SQLException {
		ClientTestModel[] entities = dao.query("1=1", new StatementParameters(), new DalHints().inShard(shardId).inTableShard(tableShardId)).toArray(new ClientTestModel[tableShardId + 1]);
		for (ClientTestModel model: entities) {
			model.setTableIndex(null);
			model.setDbIndex(null);
		}
		return Arrays.asList(entities);
	}
	
	private final static String GENERATED_KEY = "GENERATED_KEY";
	
	public void assertKeyHolder(KeyHolder holder) throws SQLException {
		if(!ASSERT_ALLOWED)
			return;

		Assert.assertEquals(3, holder.getKeyList().size());		 
		Assert.assertTrue(holder.getKey(0).longValue() > 0);
		Assert.assertTrue(holder.getKeyList().get(0).containsKey(GENERATED_KEY));
	}

	// Only for sql server
	public KeyHolder createKeyHolder() {
		return ASSERT_ALLOWED ? new KeyHolder() : null;
	}
	
	// Only for sql server
	public void assertResEquals(int[] expected, int[] res) {
		Assert.assertEquals(expected.length, res.length);
		if(ASSERT_ALLOWED)
			Assert.assertArrayEquals(expected, res);
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
				testQueryByPk(i, new DalHints().inShard(String.valueOf(i)));
			else
				testQueryByPk(i, new DalHints().inShard(i));

			// By shardValue
			if(i%2 == 0)
				testQueryByPk(i, new DalHints().setShardValue(String.valueOf(i)));
			else
				testQueryByPk(i, new DalHints().setShardValue(i));

			// By shardColValue
			if(i%2 == 0)
				testQueryByPk(i, new DalHints().setShardColValue("index", String.valueOf(i)));
			else
				testQueryByPk(i, new DalHints().setShardColValue("index", i));

			// By shardColValue
			if(i%2 == 0)
				testQueryByPk(i, new DalHints().setShardColValue("dbIndex", String.valueOf(i)));
			else
				testQueryByPk(i, new DalHints().setShardColValue("dbIndex", i));

		}
	}
	
	private void testQueryByPk(int shardId, DalHints hints) throws SQLException {
		ClientTestModel model = null;
		
		for(int i = 0; i < tableMod; i++) {
			int id = 1;
			// By tabelShard
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.clone().inTableShard(String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.clone().inTableShard(i));
			assertQueryByPk(shardId, model, i, id);

			// By tableShardValue
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.clone().setTableShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.clone().setTableShardValue(i));
			assertQueryByPk(shardId, model, i, id);

			// By shardColValue
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.clone().setShardColValue("table", String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.clone().setShardColValue("table", i));
			assertQueryByPk(shardId, model, i, id);
			
			// By shardColValue
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.clone().setShardColValue("tableIndex", String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.clone().setShardColValue("tableIndex", i));
			assertQueryByPk(shardId, model, i, id);
		}
	}

	private void assertQueryByPk(int shardId, ClientTestModel model, int i,
			int id) {
		assertQueryFirstWithWhereClause(shardId, model, i);
		Assert.assertEquals(id * (shardId + 1) * (i+1), model.getQuantity().intValue());
	}
	
	@Test
	public void testQueryByPkWithEntity() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testQueryByPkWithEntity(i, new DalHints().inShard(i));

			// By shardValue
			testQueryByPkWithEntity(i, new DalHints().setShardValue(i));

			// By shardColValue
			testQueryByPkWithEntity(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testQueryByPkWithEntity(i, new DalHints().setShardColValue("dbIndex", i));

			// By fields
			// This is merged with the sub test
		}
	}
	
	public void testQueryByPkWithEntity(int shardId, DalHints hints) throws SQLException{
		ClientTestModel pk = null;
		ClientTestModel model = null;
		
		for(int i = 0; i < tableMod; i++) {
			int id = 1;
			pk = new ClientTestModel();
			pk.setId(1);

			// By tabelShard
			model = dao.queryByPk(pk, hints.clone().inTableShard(i));
			assertQueryByPk(shardId, model, i, id);
			
			// By tableShardValue
			model = dao.queryByPk(pk, hints.clone().setTableShardValue(i));
			assertQueryByPk(shardId, model, i, id);

			// By shardColValue
			model = dao.queryByPk(pk, hints.clone().setShardColValue("table", i));
			assertQueryByPk(shardId, model, i, id);
			
			// By shardColValue
			model = dao.queryByPk(pk, hints.clone().setShardColValue("tableIndex", i));
			assertQueryByPk(shardId, model, i, id);

			// By fields
			pk.setTableIndex(i);
			pk.setDbIndex(shardId);
			model = dao.queryByPk(pk, new DalHints());
			assertQueryByPk(shardId, model, i, id);
		}
	}
	
	/**
	 * Query by Entity without Primary key
	 * @throws SQLException
	 */
	@Test
	public void testQueryByPkWithEntityNoId() throws SQLException{
		for(int i = 0; i < mod; i++) {
			ClientTestModel pk = new ClientTestModel();
			pk.setDbIndex(i);
			
			// By shard
			testQueryByPkWithEntityNoId(i, new DalHints().inShard(i), pk);

			// By shardValue
			testQueryByPkWithEntityNoId(i, new DalHints().setShardValue(i), pk);

			// By shardColValue
			testQueryByPkWithEntityNoId(i, new DalHints().setShardColValue("index", i), pk);
			
			// By shardColValue
			testQueryByPkWithEntityNoId(i, new DalHints().setShardColValue("dbIndex", i), pk);
		}
	}
	
	/**
	 * Query by Entity without Primary key
	 * @throws SQLException
	 */
	public void testQueryByPkWithEntityNoId(int shardId, DalHints hints, ClientTestModel pk) throws SQLException{
		ClientTestModel model = null;
		// By fields
		for(int i = 0; i < tableMod; i++) {
			pk = new ClientTestModel();
			pk.setTableIndex(i);
			if(i%2 == 0)
				model = dao.queryByPk(pk, hints.clone());
			else
				model = dao.queryByPk(pk, hints.clone());
			Assert.assertNull(model);
		}
	}

	/**
	 * Query against sample entity
	 * @throws SQLException
	 */
	@Test
	public void testQueryLike() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testQueryLike(i, new DalHints().inShard(i));

			// By shardValue
			testQueryLike(i, new DalHints().setShardValue(i));

			// By shardColValue
			testQueryLike(i, new DalHints().setShardColValue("index", i));

			// By shardColValue
			testQueryLike(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	/**
	 * Query against sample entity
	 * @throws SQLException
	 */
	public void testQueryLike(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> models = null;

		ClientTestModel pk = null;
		
		for(int i = 0; i < tableMod; i++) {
			pk = new ClientTestModel();
			pk.setType((short)1);

			// By tabelShard
			models = dao.queryLike(pk, hints.clone().inTableShard(i));
			assertQueryLike(shardId, models, i);

			// By tableShardValue
			models = dao.queryLike(pk, hints.clone().setTableShardValue(i));
			assertQueryLike(shardId, models, i);

			// By shardColValue
			models = dao.queryLike(pk, hints.clone().setShardColValue("table", i));
			assertQueryLike(shardId, models, i);

			// By shardColValue
			models = dao.queryLike(pk, hints.clone().setShardColValue("tableIndex", i));
			assertQueryLike(shardId, models, i);

			// By fields
			pk.setDbIndex(shardId);
			pk.setTableIndex(i);
			models = dao.queryLike(pk, new DalHints());
			assertQueryLike(shardId, models, i);
		}
	}

	private void assertQueryLike(int shardId, List<ClientTestModel> models,
			int i) {
		assertQueryX(shardId, models, i);
		Assert.assertEquals(i, models.get(0).getTableIndex().intValue());
	}
	
	/**
	 * Query by Entity with where clause
	 * @throws SQLException
	 */
	@Test
	public void testQueryWithWhereClause() throws SQLException{
		List<ClientTestModel> models = null;
		
		for(int i = 0; i < mod; i++) {
			// By shard
			testQueryWithWhereClause(i, new DalHints().inShard(i));

			// By shardValue
			testQueryWithWhereClause(i, new DalHints().setShardValue(i));

			// By shardColValue
			testQueryWithWhereClause(i, new DalHints().setShardColValue("index", i));

			// By shardColValue
			testQueryWithWhereClause(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	/**
	 * Query by Entity with where clause
	 * @throws SQLException
	 */
	public void testQueryWithWhereClause(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> models = null;
		
		for(int i = 0; i < tableMod; i++) {
			String whereClause = "type=? and id=?";
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);
			parameters.set(2, Types.INTEGER, 1);
			
			// By tabelShard
			models = dao.query(whereClause, parameters, hints.clone().inTableShard(i));
			assertQueryWithWhereClause(shardId, models, i);

			// By tableShardValue
			models = dao.query(whereClause, parameters, hints.clone().setTableShardValue(i));
			assertQueryWithWhereClause(shardId, models, i);

			// By shardColValue
			models = dao.query(whereClause, parameters, hints.clone().setShardColValue("table", i));
			assertQueryWithWhereClause(shardId, models, i);

			// By shardColValue
			models = dao.query(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i));
			assertQueryWithWhereClause(shardId, models, i);

			// By parameters
			whereClause += " and tableIndex=? and dbIndex=?";
			parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "id", Types.SMALLINT, i + 1);
			parameters.set(3, "tableIndex", Types.SMALLINT, i);
			parameters.set(4, "dbIndex", Types.SMALLINT, shardId);

			models = dao.query(whereClause, parameters, new DalHints());
			assertQueryWithWhereClause(shardId, models, i);
		}
	}

	private void assertQueryWithWhereClause(int shardId,
			List<ClientTestModel> models, int i) {
		Assert.assertEquals(1, models.size());
		Assert.assertEquals("SH INFO", models.get(0).getAddress());
		Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));
		Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
	}
	
	/**
	 * Test Query the first row with where clause
	 * @throws SQLException 
	 */
	@Test
	public void testQueryFirstWithWhereClause() throws SQLException{
		for(int i = 0; i < mod; i++) {
			testQueryFirstWithWhereClause(i, new DalHints().inShard(i));

			// By shardValue
			testQueryFirstWithWhereClause(i, new DalHints().setShardValue(i));

			// By shardColValue
			testQueryFirstWithWhereClause(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testQueryFirstWithWhereClause(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	/**
	 * Test Query the first row with where clause
	 * @throws SQLException 
	 */
	public void testQueryFirstWithWhereClause(int shardId, DalHints hints) throws SQLException{
		ClientTestModel model = null;
		for(int i = 0; i < tableMod; i++) {
			String whereClause = "type=?";

			// By tabelShard
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);

			model = dao.queryFirst(whereClause, parameters, hints.clone().inTableShard(i));
			assertQueryFirstWithWhereClause(shardId, model, i);
			
			// By tableShardValue
			model = dao.queryFirst(whereClause, parameters, hints.clone().setTableShardValue(i));
			assertQueryFirstWithWhereClause(shardId, model, i);

			// By shardColValue
			model = dao.queryFirst(whereClause, parameters, hints.clone().setShardColValue("table", i));
			assertQueryFirstWithWhereClause(shardId, model, i);
			
			// By shardColValue
			model = dao.queryFirst(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i));
			assertQueryFirstWithWhereClause(shardId, model, i);

			// By parameters
			whereClause += " and tableIndex=? and dbIndex=?";
			parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);
			parameters.set(3, "dbIndex", Types.SMALLINT, shardId);
			model = dao.queryFirst(whereClause, parameters, hints.clone());
			assertQueryFirstWithWhereClause(shardId, model, i);
		}
	}

	private void assertQueryFirstWithWhereClause(int shardId,
			ClientTestModel model, int i) {
		Assert.assertEquals(1, model.getId().intValue());
		Assert.assertEquals(i, model.getTableIndex().intValue());
		Assert.assertEquals(shardId, model.getDbIndex().intValue());
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
			dao.queryFirst(whereClause, parameters, new DalHints().inTableShard(1).inShard(0));
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
		for(int i = 0; i < mod; i++) {
			// By shard
			testQueryTopWithWhereClause(i, new DalHints().inShard(i));
			
			// By shardValue
			testQueryTopWithWhereClause(i, new DalHints().setShardValue(i));

			// By shardColValue
			testQueryTopWithWhereClause(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testQueryTopWithWhereClause(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	/**
	 * Test Query the top rows with where clause
	 * @throws SQLException
	 */
	public void testQueryTopWithWhereClause(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> models = null;
		
		for(int i = 0; i < tableMod; i++) {
			String whereClause = "type=?";
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);

			// By tabelShard
			models = dao.queryTop(whereClause, parameters, hints.clone().inTableShard(i), i + 1);
			assertQueryX(shardId, models, i);
			
			// By tableShardValue
			models = dao.queryTop(whereClause, parameters, hints.clone().setTableShardValue(i), i + 1);
			assertQueryX(shardId, models, i);

			// By shardColValue
			models = dao.queryTop(whereClause, parameters, hints.clone().setShardColValue("table", i), i + 1);
			assertQueryX(shardId, models, i);
			
			// By shardColValue
			models = dao.queryTop(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i), i + 1);
			assertQueryX(shardId, models, i);

			whereClause += " and tableIndex=? and dbIndex=?";
			// By parameters
			parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);
			parameters.set(3, "dbIndex", Types.SMALLINT, shardId);
			models = dao.queryTop(whereClause, parameters, hints.clone(), i + 1);
			assertQueryX(shardId, models, i);
		}
	}

	private void assertQueryX(int shardId, List<ClientTestModel> models, int i) {
		Assert.assertEquals(i + 1, models.size());
		Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
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
		
		models = dao.queryTop(whereClause, parameters, new DalHints().inTableShard(1).inShard(0), 2);
		Assert.assertTrue(null != models);
		Assert.assertEquals(0, models.size());
	}
	
	/**
	 * Test Query range of result with where clause
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClause() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testQueryFromWithWhereClause(i, new DalHints().inShard(i));
		
			// By shardValue
			testQueryFromWithWhereClause(i, new DalHints().setShardValue(i));

			// By shardColValue
			testQueryFromWithWhereClause(i, new DalHints().setShardColValue("index", i));

			// By shardColValue
			testQueryFromWithWhereClause(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	/**
	 * Test Query range of result with where clause
	 * @throws SQLException
	 */
	public void testQueryFromWithWhereClause(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> models = null;
		String whereClause = "type=?";
		
		for(int i = 0; i < tableMod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);

			// By tabelShard
			models = dao.queryFrom(whereClause, parameters, hints.clone().inTableShard(i), 0, i + 1);
			assertQueryX(shardId, models, i);
		
			// By tableShardValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setTableShardValue(i), 0, i + 1);
			assertQueryX(shardId, models, i);
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("table", i), 0, i + 1);
			assertQueryX(shardId, models, i);
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i), 0, i + 1);
			assertQueryX(shardId, models, i);
		}

		whereClause += " and tableIndex=? and dbIndex=?";
		// By parameters
		for(int i = 0; i < tableMod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);
			parameters.set(3, "dbIndex", Types.SMALLINT, shardId);

			models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, i + 1);
			assertQueryX(shardId, models, i);
		}
	}
	
	/**
	 * Test Query range of result with where clause failed when return not enough recodes
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClauseFailed() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testQueryFromWithWhereClauseFailed(i, new DalHints().inShard(i));

			// By shardValue
			testQueryFromWithWhereClauseFailed(i, new DalHints().setShardValue(i));

			// By shardColValue
			testQueryFromWithWhereClauseFailed(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testQueryFromWithWhereClauseFailed(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	/**
	 * Test Query range of result with where clause failed when return not enough recodes
	 * @throws SQLException
	 */
	public void testQueryFromWithWhereClauseFailed(int shardId, DalHints hints) throws SQLException{
		String whereClause = "type=?";
		List<ClientTestModel> models = null;
		for(int i = 0; i < tableMod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 1);
			
			// By tabelShard
			models = dao.queryFrom(whereClause, parameters, hints.clone().inTableShard(i), 0, 10);
			Assert.assertTrue(null != models);
			assertQueryX(shardId, models, i);

			// By tableShardValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setTableShardValue(i), 0, 10);
			Assert.assertTrue(null != models);
			assertQueryX(shardId, models, i);

			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("table", i), 0, 10);
			Assert.assertTrue(null != models);
			assertQueryX(shardId, models, i);
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i), 0, 10);
			Assert.assertTrue(null != models);
			assertQueryX(shardId, models, i);
		}
	}
	
	/**
	 * Test Query range of result with where clause when return empty collection
	 * @throws SQLException
	 */
	@Test
	public void testQueryFromWithWhereClauseEmpty() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testQueryFromWithWhereClauseEmpty(i, new DalHints().inShard(i));

			// By shardValue
			testQueryFromWithWhereClauseEmpty(i, new DalHints().setShardValue(i));

			// By shardColValue
			testQueryFromWithWhereClauseEmpty(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testQueryFromWithWhereClauseEmpty(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	/**
	 * Test Query range of result with where clause when return empty collection
	 * @throws SQLException
	 */
	public void testQueryFromWithWhereClauseEmpty(int shardId, DalHints hints) throws SQLException{
		String whereClause = "type=?";
		List<ClientTestModel> models = null;
		for(int i = 0; i < tableMod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, Types.SMALLINT, 10);
			
			// By tabelShard
			models = dao.queryFrom(whereClause, parameters, hints.clone().inTableShard(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(0, models.size());

			// By tableShardValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setTableShardValue(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(0, models.size());

			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("table", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(0, models.size());
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(0, models.size());
		}
	}
	
	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	@Test
	public void testInsertSingleFail() throws SQLException{
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
	}
		
	@Test
	public void testInsertSingleByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testInsertSingle(i, new DalHints().inShard(i));
		}
	}
	
	@Test
	public void testInsertSingleByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testInsertSingle(i, new DalHints().setShardValue(i));
		}
	}
	
	@Test
	public void testInsertSingleByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
		// By shardColValue
			testInsertSingle(i, new DalHints().setShardColValue("index", i));
		}
	}

	@Test
	public void testInsertSingleByShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testInsertSingle(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	@Test
	public void testInsertSingleByFields() throws SQLException{
		int res;
		deleteAllShardsByDbTable(dao, mod, tableMod);
		ClientTestModel model = new ClientTestModel();
		model.setQuantity(10 + 1%3);
		model.setType(((Number)(1%3)).shortValue());
		model.setAddress("CTRIP");
		model.setTableIndex(0);
		model.setDbIndex(3);
		
		res = dao.insert(new DalHints(), model);
		assertResEquals(1, res);
		Assert.assertEquals(1, getCount(1, 0));
	}

	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	public void testInsertSingle(int shardId, DalHints hints) throws SQLException{
		ClientTestModel model = new ClientTestModel();
		model.setQuantity(10 + 1%3);
		model.setType(((Number)(1%3)).shortValue());
		model.setAddress("CTRIP");
		int res;
		try {
			res = dao.insert(hints, model);
			Assert.fail();
		} catch (Exception e) {
		}
		
		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			res = dao.insert(hints.clone().inTableShard(i), model);
			assertResEquals(1, res);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));

			// By tableShardValue
			res = dao.insert(hints.clone().setTableShardValue(i), model);
			assertResEquals(1, res);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));

			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("table", i), model);
			assertResEquals(1, res);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));
			
			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("tableIndex", i), model);
			assertResEquals(1, res);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));
			
			// By fields
			model.setTableIndex(i);
			res = dao.insert(hints.clone(), model);
			assertResEquals(1, res);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));
		}
	}
	
	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleAsListFail() throws SQLException{
		List<ClientTestModel> entities = createListNoId(3);

		int[] res;
		try {
			res = dao.insert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testInsertMultipleAsListByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testInsertMultipleAsList(i, new DalHints().inShard(i));
		}
	}
	
	@Test
	public void testInsertMultipleAsListByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testInsertMultipleAsList(i, new DalHints().setShardValue(i));
		}
	}	

	@Test
	public void testInsertMultipleAsListByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testInsertMultipleAsList(i, new DalHints().setShardColValue("index", i));
		}
	}
	
	@Test
	public void testInsertMultipleAsListByShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
		// By shardColValue
			testInsertMultipleAsList(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}	

	@Test
	public void testInsertMultipleAsListByField() throws SQLException{
		int[] res;
		List<ClientTestModel> entities = createListNoId(3);
		deleteAllShardsByDbTable(dao, mod, tableMod);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(0).setDbIndex(0);
		
		entities.get(1).setTableIndex(1);
		entities.get(1).setDbIndex(1);

		entities.get(2).setTableIndex(2);
		entities.get(2).setDbIndex(2);
		
		res = dao.insert(new DalHints().continueOnError(), entities);
		assertResEquals(3, res);
		Assert.assertEquals(1, getCountByDbTable(dao, 0, 0));
		Assert.assertEquals(1, getCountByDbTable(dao, 1, 1));
		Assert.assertEquals(1, getCountByDbTable(dao, 0, 2));
	}
	
	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	public void testInsertMultipleAsList(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> entities = createListNoId(3);

		int[] res = null;
		try {
			res = dao.insert(hints.clone(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			res = dao.insert(hints.clone().inTableShard(i), entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

			// By tableShardValue
			res = dao.insert(hints.clone().setTableShardValue(i), entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("table", i), entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			
			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("tableIndex", i), entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(hints.clone(), entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
		}
		
		deleteAllShards(shardId);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		dao.insert(hints.clone(), entities);
		assertResEquals(3, res);
		Assert.assertEquals(1, getCount(shardId, 0));
		Assert.assertEquals(1, getCount(shardId, 1));
		Assert.assertEquals(1, getCount(shardId, 2));
	}
	
	/**
	 * Test Test Insert multiple entities one by one with continueOnError hints
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleAsListWithContinueOnErrorHintsFail() throws SQLException{
		List<ClientTestModel> entities = createListNoId(3);
		entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
				+ "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
				+ "CTRIPCTRIPCTRIPCTRIP");

		int[] res;
		try {
			res = dao.insert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testInsertMultipleAsListWithContinueOnErrorHintsByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testInsertMultipleAsListWithContinueOnErrorHints(i, new DalHints().continueOnError().inShard(i));
		}
	}

	@Test
	public void testInsertMultipleAsListWithContinueOnErrorHintsBYShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testInsertMultipleAsListWithContinueOnErrorHints(i, new DalHints().continueOnError().setShardValue(i));
		}
	}
	
	@Test
	public void testInsertMultipleAsListWithContinueOnErrorHintsShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testInsertMultipleAsListWithContinueOnErrorHints(i, new DalHints().continueOnError().setShardColValue("index", i));
		}
	}
	
	@Test
	public void testInsertMultipleAsListWithContinueOnErrorHintsShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testInsertMultipleAsListWithContinueOnErrorHints(i, new DalHints().continueOnError().setShardColValue("dbIndex", i));
		}
	}

	@Test
	public void testInsertMultipleAsListWithContinueOnErrorHintsByFields() throws SQLException{
		List<ClientTestModel> entities = createListNoId(3);
		entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
				+ "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
				+ "CTRIPCTRIPCTRIPCTRIP");

		int[] res;
		deleteAllShardsByDbTable(dao, mod, tableMod);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(0).setDbIndex(0);

		entities.get(1).setTableIndex(1);
		entities.get(1).setDbIndex(1);
		
		entities.get(2).setTableIndex(2);
		entities.get(2).setDbIndex(2);
		res = dao.insert(new DalHints().continueOnError(), entities);
		assertResEquals(2, res);
		Assert.assertEquals(1, getCount(0, 0));
		Assert.assertEquals(0, getCount(1, 1));
		Assert.assertEquals(1, getCount(0, 2));
	}
	
	/**
	 * Test Test Insert multiple entities one by one with continueOnError hints
	 * @throws SQLException
	 */
	public void testInsertMultipleAsListWithContinueOnErrorHints(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> entities = createListNoId(3);
		entities.get(1).setAddress("CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
				+ "CTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIPCTRIP"
				+ "CTRIPCTRIPCTRIPCTRIP");
		
		int[] res;
		try {
			res = dao.insert(hints.clone(), entities);
		} catch (Exception e) {
			Assert.fail();
		}

		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			res = dao.insert(hints.clone().continueOnError().inTableShard(i), entities);
			assertResEquals(2, res);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By tableShardValue
			res = dao.insert(hints.clone().continueOnError().setTableShardValue(i), entities);
			assertResEquals(2, res);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

			// By shardColValue
			res = dao.insert(hints.clone().continueOnError().setShardColValue("table", i), entities);
			assertResEquals(2, res);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By shardColValue
			res = dao.insert(hints.clone().continueOnError().setShardColValue("tableIndex", i), entities);
			assertResEquals(2, res);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(hints.clone().continueOnError(), entities);
			assertResEquals(2, res);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
		}
		
		deleteAllShards(shardId);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(0).setDbIndex(0);

		entities.get(1).setTableIndex(1);
		entities.get(1).setDbIndex(1);
		
		entities.get(2).setTableIndex(2);
		entities.get(2).setDbIndex(2);
		res = dao.insert(hints.clone().continueOnError(), entities);
		assertResEquals(2, res);
		Assert.assertEquals(1, getCount(shardId, 0));
		Assert.assertEquals(0, getCount(shardId, 1));
		Assert.assertEquals(1, getCount(shardId, 2));
	}
	
	/**
	 * Test Insert multiple entities with key-holder
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleAsListWithKeyHolderFail() throws SQLException{
		List<ClientTestModel> entities = createListNoId(3);
		KeyHolder holder = createKeyHolder();
		int[] res;
		try {
			res = dao.insert(new DalHints(), holder, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
	}
	
	@Test
	public void testInsertMultipleAsListWithKeyHolderByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testInsertMultipleAsListWithKeyHolder(i, new DalHints().inShard(i));
		}
	}

	@Test
	public void testInsertMultipleAsListWithKeyHolderByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testInsertMultipleAsListWithKeyHolder(i, new DalHints().setShardValue(i));
		}
	}
	
	@Test
	public void testInsertMultipleAsListWithKeyHolderByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testInsertMultipleAsListWithKeyHolder(i, new DalHints().setShardColValue("index", i));
		}
	}
	
	@Test
	public void testInsertMultipleAsListWithKeyHolderByShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testInsertMultipleAsListWithKeyHolder(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}

	@Test
	public void testInsertMultipleAsListWithKeyHolderByFields() throws SQLException{
		List<ClientTestModel> entities = createListNoId(3);
		int[] res;
		KeyHolder holder = createKeyHolder();

		deleteAllShardsByDbTable(dao, mod, tableMod);
		
		// By fields not same shard
		holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(0).setDbIndex(0);

		entities.get(1).setTableIndex(1);
		entities.get(1).setDbIndex(1);
		
		entities.get(2).setTableIndex(2);
		entities.get(2).setDbIndex(2);
		
		res = dao.insert(new DalHints(), holder, entities);
		assertResEquals(3, res);
		Assert.assertEquals(1, getCount(0, 0));
		Assert.assertEquals(1, getCount(1, 1));
		Assert.assertEquals(1, getCount(0, 2));
		assertKeyHolder(holder);
	}

	/**
	 * Test Insert multiple entities with key-holder
	 * @throws SQLException
	 */
	public void testInsertMultipleAsListWithKeyHolder(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> entities = createListNoId(3);

		KeyHolder holder = new KeyHolder();
		int[] res;
		try {
			res = dao.insert(hints.clone(), holder, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}

		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			holder = createKeyHolder();
			res = dao.insert(hints.clone().inTableShard(i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			assertKeyHolder(holder);

			// By tableShardValue
			holder = createKeyHolder();
			res = dao.insert(hints.clone().setTableShardValue(i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			assertKeyHolder(holder);
			
			// By shardColValue
			holder = createKeyHolder();
			res = dao.insert(hints.clone().setShardColValue("table", i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			assertKeyHolder(holder);
			
			// By shardColValue
			holder = createKeyHolder();
			res = dao.insert(hints.clone().setShardColValue("tableIndex", i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			assertKeyHolder(holder);
			
			// By fields same shard
			holder = createKeyHolder();
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(hints.clone(), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			assertKeyHolder(holder);
		}
		
		deleteAllShards(shardId);
		
		// By fields not same shard
		holder = createKeyHolder();
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		res = dao.insert(hints.clone(), holder, entities);
		Assert.assertEquals(1, getCount(shardId, 0));
		Assert.assertEquals(1, getCount(shardId, 1));
		Assert.assertEquals(1, getCount(shardId, 2));
		assertResEquals(3, res);
		assertKeyHolder(holder);
	}
	
	/**
	 * Test Insert multiple entities with one SQL Statement
	 * @throws SQLException
	 */
	@Test
	public void testCombinedInsertFail() throws SQLException{
		List<ClientTestModel> entities = create(3);
		
		KeyHolder holder = createKeyHolder();
		int res;
		try {
			res = dao.combinedInsert(new DalHints(), holder, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
	}

	@Test
	public void testCombinedInsertByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testCombinedInsert(i, new DalHints().inShard(i));
		}
	}

	@Test
	public void testCombinedInsertByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testCombinedInsert(i, new DalHints().setShardValue(i));
		}
	}

	@Test
	public void testCombinedInsertByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testCombinedInsert(i, new DalHints().setShardColValue("index", i));
		}
	}
	
	@Test
	public void testCombinedInsertByShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testCombinedInsert(i, new DalHints().setShardColValue("dbIndex", i));
		}
		// For combined insert, the shard id must be defined or change bd deduced.
	}
	
	/**
	 * Test Insert multiple entities with one SQL Statement
	 * @throws SQLException
	 */
	public void testCombinedInsert(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> entities = create(3);
		
		KeyHolder holder = createKeyHolder();
		int res;
		try {
			res = dao.combinedInsert(hints.clone(), holder, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			holder = null;
			// By tabelShard
			holder = createKeyHolder();
			res = dao.combinedInsert(hints.clone().inTableShard(i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			assertKeyHolder(holder);
			
			// By tableShardValue
			holder = createKeyHolder();
			res = dao.combinedInsert(hints.clone().setTableShardValue(i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			assertKeyHolder(holder);

			// By shardColValue
			holder = createKeyHolder();
			res = dao.combinedInsert(hints.clone().setShardColValue("table", i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			assertKeyHolder(holder);
			
			// By shardColValue
			holder = createKeyHolder();
			res = dao.combinedInsert(hints.clone().setShardColValue("tableIndex", i), holder, entities);
			assertResEquals(3, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			assertKeyHolder(holder);
		}
		// For combined insert, the shard id must be defined or change bd deduced.
	}
	
	/**
	 * Test Batch Insert multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testBatchInsertFail() throws SQLException{
		List<ClientTestModel> entities = create(3);

		int[] res;
		try {
			res = dao.batchInsert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
	}
	
	@Test
	public void testBatchInsertByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testBatchInsert(i, new DalHints().inShard(i));
		}
	}

	@Test
	public void testBatchInsertByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testBatchInsert(i, new DalHints().setShardValue(i));
		}
	}

	@Test
	public void testBatchInsertByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testBatchInsert(i, new DalHints().setShardColValue("index", i));
		}
	}

	@Test
	public void testBatchInsertByShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testBatchInsert(i, new DalHints().setShardColValue("dbIndex", i));
		}
		// For batch insert, the shard id must be defined or change bd deduced.
	}
	
	/**
	 * Test Batch Insert multiple entities
	 * @throws SQLException
	 */
	public void testBatchInsert(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> entities = create(3);

		int[] res;
		try {
			res = dao.batchInsert(hints.clone(), entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			res = dao.batchInsert(hints.clone().inTableShard(i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			
			// By tableShardValue
			res = dao.batchInsert(hints.clone().setTableShardValue(i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

			// By shardColValue
			res = dao.batchInsert(hints.clone().setShardColValue("table", i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			
			// By shardColValue
			res = dao.batchInsert(hints.clone().setShardColValue("tableIndex", i), entities);
			assertResEquals(new int[]{1,1,1}, res);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
		}
		
		// For combined insert, the shard id must be defined or change bd deduced.
	}
	
	/**
	 * Test delete multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testDeleteMultipleFail() throws SQLException{
		List<ClientTestModel> entities = create(3);
		
		int res[];
		try {
			res = dao.delete(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
	}
	
	@Test
	public void testDeleteMultipleByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testDeleteMultiple(i, new DalHints().inShard(i));
		}
	}

	@Test
	public void testDeleteMultipleByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testDeleteMultiple(i, new DalHints().setShardValue(i));
		}
	}

	@Test
	public void testDeleteMultipleByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testDeleteMultiple(i, new DalHints().setShardColValue("index", i));
		}
	}
	
	@Test
	public void testDeleteMultipleByShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testDeleteMultiple(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	@Test
	public void testDeleteMultipleByFields() throws SQLException{
		int[] res;
		
		// By fields not same shard
		List<ClientTestModel> entities = create(3);
		int i = 0;
		for(ClientTestModel model: entities) {
			model.setDbIndex(i%2);
			model.setTableIndex(i++);
		}

		res = dao.delete(new DalHints(), entities);
		assertResEquals(3, res);
		Assert.assertEquals(0, getCount(0, 0));
		Assert.assertEquals(1, getCount(1, 1));
		Assert.assertEquals(2, getCount(0, 2));
	}
	
	/**
	 * Test delete multiple entities
	 * @throws SQLException
	 */
	public void testDeleteMultiple(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> entities = create(3);

		int[] res;
		try {
			res = dao.delete(hints.clone(), entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < tableMod; i++) {
			// By tabelShard
			deleteTest(shardId, i, hints.clone().inTableShard(i));
	
			// By tableShardValue
			deleteTest(shardId, i, hints.clone().setTableShardValue(i));
			
			// By shardColValue
			deleteTest(shardId, i, hints.clone().setShardColValue("table", i));
			
			// By shardColValue
			deleteTest(shardId, i, hints.clone().setShardColValue("tableIndex", i));

			// By fields same shard
			entities = getModels(shardId, i);
			for(ClientTestModel model: entities)
				model.setTableIndex(i);

			Assert.assertEquals(1 + i, getCount(shardId, i));
			res = dao.delete(hints.clone(), entities);
			assertResEquals(1 + i, res);
			Assert.assertEquals(0, getCount(shardId, i));
		}		

		// By fields not same shard
		reset();
		entities = create(3);
		int i = 0;
		for(ClientTestModel model: entities) {
			model.setTableIndex(i++);
			model.setDbIndex(shardId);
		}

		res = dao.delete(hints.clone(), entities);
		assertResEquals(3, res);
		Assert.assertEquals(0, getCount(shardId, 0));
		Assert.assertEquals(1, getCount(shardId, 1));
		Assert.assertEquals(2, getCount(shardId, 2));
	}
	
	private void deleteTest(int shardId, int tableShardId, DalHints hints) throws SQLException {
		int count = 1 + tableShardId;
		Assert.assertEquals(count, getCount(shardId, tableShardId));
		int[] res = dao.delete(hints, getModels(shardId, tableShardId));
		assertResEquals(1 + tableShardId, res);
		Assert.assertEquals(0, getCount(shardId, tableShardId));
		dao.insert(hints, create(count));
	}

	/**
	 * Test batch delete multiple entities
	 * @throws SQLException
	 */
	@Test
	public void testBatchDeleteFail() throws SQLException{
		List<ClientTestModel> entities = create(3);
		
		int[] res;
		try {
			res = dao.batchDelete(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
	}
	
	@Test
	public void testBatchDeleteByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testBatchDelete(i, new DalHints().inShard(i));
		}
	}

	@Test
	public void testBatchDeleteByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testBatchDelete(i, new DalHints().setShardValue(i));
		}
	}

	@Test
	public void testBatchDeleteByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testBatchDelete(i, new DalHints().setShardColValue("index", i));
		}
	}
	
	@Test
	public void testBatchDeleteByShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testBatchDelete(i, new DalHints().setShardColValue("dbIndex", i));
		}
		// Currently does not support not same shard 
	}
	
	/**
	 * Test batch delete multiple entities
	 * @throws SQLException
	 */
	public void testBatchDelete(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> entities = create(3);
		
		int[] res;
		try {
			res = dao.batchDelete(hints.clone(), entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < tableMod; i++) {
			// By tabelShard
			batchDeleteTest(shardId, i, hints.clone().inTableShard(i));
	
			// By tableShardValue
			batchDeleteTest(shardId, i, hints.clone().setTableShardValue(i));
			
			// By shardColValue
			batchDeleteTest(shardId, i, hints.clone().setShardColValue("table", i));
			
			// By shardColValue
			batchDeleteTest(shardId, i, hints.clone().setShardColValue("tableIndex", i));

			// By fields same shard
			entities = getModels(shardId, i);
			int[] exp = new int[i + 1];
			for(int k = 0; k < exp.length; k++)
				exp[k] = 1;
			
			for(ClientTestModel model: entities)
				model.setTableIndex(i);

			Assert.assertEquals(1 + i, getCount(shardId, i));
			res = dao.batchDelete(hints.clone(), entities);
			assertResEquals(exp, res);
			Assert.assertEquals(0, getCount(shardId, i));
		}		
		// Currently does not support not same shard 
	}
	
	private void batchDeleteTest(int shardId, int tableShardId, DalHints hints) throws SQLException {
		int count = 1 + tableShardId;
		Assert.assertEquals(count, getCount(shardId, tableShardId));
		int[] res = dao.batchDelete(hints, getModels(shardId, tableShardId));
		int[] exp = new int[count];
		for(int i = 0;i < count; i++) exp[i] = 1;
		assertResEquals(exp, res);
		Assert.assertEquals(0, getCount(shardId, tableShardId));
		dao.insert(hints, create(count));
	}
	

	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	@Test
	public void testUpdateMultipleFail() throws SQLException{
		List<ClientTestModel> entities = create(3);
		
		int[] res;
		try {
			res = dao.update(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
	}		

	@Test
	public void testUpdateMultipleByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testUpdateMultiple(i, new DalHints().inShard(i));
		}
	}
	
	@Test
	public void testUpdateMultipleByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testUpdateMultiple(i, new DalHints().setShardValue(i));
		}
	}
	
	@Test
	public void testUpdateMultipleByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testUpdateMultiple(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testUpdateMultiple(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	@Test
	public void testUpdateMultipleByFields() throws SQLException{
		// By fields not same shard
		List<ClientTestModel> entities = create(4);
		int[] res;
		int i = 0;
		for(ClientTestModel model: entities) {
			model.setTableIndex(i);
			model.setDbIndex(i++);
			model.setAddress("1234");
		}
		
		res = dao.update(new DalHints(), entities);
		assertResEquals(4, res);
		for(ClientTestModel model: entities)
			Assert.assertEquals("1234", dao.queryByPk(model, new DalHints()).getAddress());
	}
	
	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	public void testUpdateMultiple(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> entities = create(4);
		
		int[] res;
		try {
			res = dao.update(hints, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < tableMod; i++) {
			// By tabelShard
			entities = create(i + 1);
			for(ClientTestModel model: entities) model.setAddress("test1");
			res = dao.update(hints.clone().inTableShard(i), entities);
			assertResEquals(i + 1, res);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals("test1", model.getAddress());
	
			// By tableShardValue
			entities = create(i + 1);
			for(ClientTestModel model: entities) model.setQuantity(-11);
			res = dao.update(hints.clone().inTableShard(i), entities);
			assertResEquals(i + 1, res);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals(-11, model.getQuantity().intValue());
			
			// By shardColValue
			entities = create(i + 1);
			for(ClientTestModel model: entities) model.setType((short)3);
			assertResEquals(i + 1, res);
			res = dao.update(hints.clone().inTableShard(i), entities);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals((short)3, model.getType().intValue());
	
			// By shardColValue
			entities = create(i + 1);
			for(ClientTestModel model: entities) model.setAddress("testa");
			res = dao.update(hints.clone().inTableShard(i), entities);
			assertResEquals(i + 1, res);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals("testa", model.getAddress());
			
			// By fields same shard
			// holder = new KeyHolder();
			entities = create(i + 1);
			for(ClientTestModel model: entities) {
				model.setAddress("testx");
				model.setTableIndex(i);
				model.setDbIndex(shardId);
			}
			res = dao.update(hints.clone(), entities);
			assertResEquals(i + 1, res);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals("testx", model.getAddress());
		}
		
		// By fields not same shard
		entities = create(4);
		int i = 0;
		for(ClientTestModel model: entities) {
			model.setAddress("testy");
			model.setTableIndex(i++);
			model.setDbIndex(shardId);
		}
		res = dao.update(hints, entities);
		assertResEquals(4, res);
		for(ClientTestModel model: entities)
			Assert.assertEquals("testy", dao.queryByPk(model, hints).getAddress());
	}
	

	@Test
	public void testBatchUpdateByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testBatchUpdate(i, new DalHints().inShard(i));
		}
	}
	
	@Test
	public void testBatchUpdateByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testBatchUpdate(i, new DalHints().setShardValue(i));
		}
	}
	
	@Test
	public void testBatchUpdateByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testUpdateMultiple(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testBatchUpdate(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	@Test
	public void testBatchUpdateByFields() throws SQLException{
		// By fields not same shard
		List<ClientTestModel> entities = create(4);
		int[] res;
		int i = 0;
		for(ClientTestModel model: entities) {
			model.setTableIndex(i);
			model.setDbIndex(i++);
			model.setAddress("1234");
		}
		
		try {
			res = dao.batchUpdate(new DalHints(), entities);
			assertResEquals(4, res);
			for(ClientTestModel model: entities)
				Assert.assertEquals("1234", dao.queryByPk(model, new DalHints()).getAddress());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	/**
	 * Test update multiple entities with primary key
	 * @throws SQLException
	 */
	public void testBatchUpdate(int shardId, DalHints hints) throws SQLException{
		List<ClientTestModel> entities = create(4);
		
		int[] res;
		try {
			res = dao.batchUpdate(hints, entities);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		for(int i = 0; i < tableMod; i++) {
			// By tabelShard
			entities = create(i + 1);
			for(ClientTestModel model: entities) model.setAddress("test1");
			res = dao.batchUpdate(hints.clone().inTableShard(i), entities);
			assertResEquals(i + 1, res);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals("test1", model.getAddress());
	
			// By tableShardValue
			entities = create(i + 1);
			for(ClientTestModel model: entities) model.setQuantity(-11);
			res = dao.batchUpdate(hints.clone().inTableShard(i), entities);
			assertResEquals(i + 1, res);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals(-11, model.getQuantity().intValue());
			
			// By shardColValue
			entities = create(i + 1);
			for(ClientTestModel model: entities) model.setType((short)3);
			assertResEquals(i + 1, res);
			res = dao.batchUpdate(hints.clone().inTableShard(i), entities);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals((short)3, model.getType().intValue());
	
			// By shardColValue
			entities = create(i + 1);
			for(ClientTestModel model: entities) model.setAddress("testa");
			res = dao.batchUpdate(hints.clone().inTableShard(i), entities);
			assertResEquals(i + 1, res);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals("testa", model.getAddress());
			
			// By fields same shard
			// holder = new KeyHolder();
			entities = create(i + 1);
			for(ClientTestModel model: entities) {
				model.setAddress("testx");
				model.setTableIndex(i);
				model.setDbIndex(shardId);
			}
			res = dao.batchUpdate(hints.clone(), entities);
			assertResEquals(i + 1, res);
			for(ClientTestModel model: getModels(shardId, i))
				Assert.assertEquals("testx", model.getAddress());
		}
		
		// By fields not same shard
		entities = create(4);
		int i = 0;
		for(ClientTestModel model: entities) {
			model.setAddress("testy");
			model.setTableIndex(i++);
			model.setDbIndex(shardId);
		}
		res = dao.batchUpdate(hints, entities);
		assertResEquals(4, res);
		for(ClientTestModel model: entities)
			Assert.assertEquals("testy", dao.queryByPk(model, hints).getAddress());
	}
	
	/**
	 * Test delete entities with where clause and parameters
	 * @throws SQLException
	 */
	@Test
	public void testDeleteWithWhereClauseFail() throws SQLException{
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
	}
	
	@Test
	public void testDeleteWithWhereClauseByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testDeleteWithWhereClause(i, new DalHints().inShard(i));
		}
	}
	
	@Test
	public void testDeleteWithWhereClauseByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testDeleteWithWhereClause(i, new DalHints().setShardValue(i));
		}
	}
	
	@Test
	public void testDeleteWithWhereClauseByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testDeleteWithWhereClause(i, new DalHints().setShardColValue("index", i));
		}
	}
	
	@Test
	public void testDeleteWithWhereClauseByShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testDeleteWithWhereClause(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	/**
	 * Test delete entities with where clause and parameters
	 * @throws SQLException
	 */
	public void testDeleteWithWhereClause(int shardId, DalHints hints) throws SQLException{
		String whereClause = "type=?";
		StatementParameters parameters = new StatementParameters();
		parameters.set(1, Types.SMALLINT, 1);

		int res;
		try {
			res = dao.delete(whereClause, parameters, hints);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		// By tabelShard
		Assert.assertEquals(1, getCount(shardId, 0));
		res = dao.delete(whereClause, parameters, hints.clone().inTableShard(0));
		assertResEquals(1, res);
		Assert.assertEquals(0, dao.query(whereClause, parameters, hints.clone().inTableShard(0)).size());

		// By tableShardValue
		Assert.assertEquals(2, getCount(shardId, 1));
		res = dao.delete(whereClause, parameters, hints.clone().setTableShardValue(1));
		assertResEquals(2, res);
		Assert.assertEquals(0, dao.query(whereClause, parameters, hints.clone().setTableShardValue(1)).size());
		
		// By shardColValue
		Assert.assertEquals(3, getCount(shardId, 2));
		res = dao.delete(whereClause, parameters, hints.clone().setShardColValue("table", 2));
		assertResEquals(3, res);
		Assert.assertEquals(0, dao.query(whereClause, parameters, hints.clone().setShardColValue("table", 2)).size());
		
		// By shardColValue
		Assert.assertEquals(4, getCount(shardId, 3));
		res = dao.delete(whereClause, parameters, hints.clone().setShardColValue("tableIndex", 3));
		assertResEquals(4, res);
		Assert.assertEquals(0, dao.query(whereClause, parameters, hints.clone().setShardColValue("tableIndex", 3)).size());
	}
	
	
	/**
	 * Test plain update with SQL
	 * @throws SQLException
	 */
	@Test
	public void testUpdatePlainFail() throws SQLException{
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
	}
	
	@Test
	public void testUpdatePlainByShard() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shard
			testUpdatePlain(i, new DalHints().inShard(i));
		}
	}
	
	@Test
	public void testUpdatePlainByShardValue() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardValue
			testUpdatePlain(i, new DalHints().setShardValue(i));
		}
	}

	@Test
	public void testUpdatePlainByShardCol() throws SQLException{
		for(int i = 0; i < mod; i++) {
		// By shardColValue
			testUpdatePlain(i, new DalHints().setShardColValue("index", i));
		}
	}

	@Test
	public void testUpdatePlainByShardCol2() throws SQLException{
		for(int i = 0; i < mod; i++) {
			// By shardColValue
			testUpdatePlain(i, new DalHints().setShardColValue("dbIndex", i));
		}
	}
	
	/**
	 * Test plain update with SQL
	 * @throws SQLException
	 */
	public void testUpdatePlain(int shardId, DalHints hints) throws SQLException{
		String sql = "UPDATE " + TABLE_NAME
				+ " SET address = 'CTRIP' WHERE id = 1";
		StatementParameters parameters = new StatementParameters();

		int res;
		try {
			res = dao.update(sql, parameters, hints);
			Assert.fail();
		} catch (Exception e) {
			
		}
		
		// By tabelShard
		sql = "UPDATE " + TABLE_NAME
				+ "_0 SET address = 'CTRIP' WHERE id = 1";
		res = dao.update(sql, parameters, hints.clone().inTableShard(0));
		assertResEquals(1, res);
		Assert.assertEquals("CTRIP", dao.queryByPk(1, hints.clone().inTableShard(0)).getAddress());

		// By tableShardValue
		sql = "UPDATE " + TABLE_NAME
				+ "_1 SET address = 'CTRIP' WHERE id = 1";
		Assert.assertEquals(2, getCount(shardId, 1));
		res = dao.update(sql, parameters, hints.clone().setTableShardValue(1));
		assertResEquals(1, res);
		Assert.assertEquals("CTRIP", dao.queryByPk(1, hints.clone().setTableShardValue(1)).getAddress());
		
		// By shardColValue
		sql = "UPDATE " + TABLE_NAME
				+ "_2 SET address = 'CTRIP' WHERE id = 1";
		Assert.assertEquals(3, getCount(shardId, 2));
		res = dao.update(sql, parameters, hints.clone().setShardColValue("table", 2));
		assertResEquals(1, res);
		Assert.assertEquals("CTRIP", dao.queryByPk(1, hints.clone().setShardColValue("table", 2)).getAddress());
		
		// By shardColValue
		sql = "UPDATE " + TABLE_NAME
				+ "_3 SET address = 'CTRIP' WHERE id = 1";
		Assert.assertEquals(4, getCount(shardId, 3));
		res = dao.update(sql, parameters, hints.clone().setShardColValue("tableIndex", 3));
		assertResEquals(1, res);
		Assert.assertEquals("CTRIP", dao.queryByPk(1, hints.clone().setShardColValue("tableIndex", 3)).getAddress());

	}
	
	@Test
	public void testCrossShardCombinedInsert() throws SQLException{
		try {
			deleteAllShardsByDbTable(dao, mod, tableMod);
			
			ClientTestModel[] pList = new ClientTestModel[mod * (1 + tableMod)*tableMod/2];
			int x = 0;
			for(int i = 0; i < mod; i++) {
				for(int j = 0; j < tableMod; j++) {
					for(int k = 0; k < j + 1; k ++) {
						ClientTestModel p = new ClientTestModel();
					
						p = new ClientTestModel();
						p.setId(1 + k);
						p.setAddress("aaa");
						p.setDbIndex(i);
						p.setTableIndex(j);
						
						pList[x++] = p;
					}
				}
			}
			
			KeyHolder keyHolder = createKeyHolder();
			DalHints hints = new DalHints();
			hints.set(DalHintEnum.sequentialExecution);
			dao.combinedInsert(hints, keyHolder, Arrays.asList(pList));
			assertKeyHolderCrossShard(keyHolder);
			for(int i = 0; i < mod; i++) {
				for(int j = 0; j < tableMod; j++) {
					Assert.assertEquals(j + 1, getCount(i, j));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	public void assertKeyHolderCrossShard(KeyHolder holder) throws SQLException {
		if(!ASSERT_ALLOWED)
			return;
/*
Shard 0
1\2
12\34
123\456
1234\5678

Shard 1
1\2
12\23
123\456
1234\5678
 */
		int x = 0;
		for(int i = 0; i < mod; i++) {
			for(int j = 0; j < tableMod; j++) {
				for(int k = 0; k < j + 1; k ++) {
					Assert.assertTrue(holder.getKey(x++).intValue() > 0);
				}
			}
		}
	}
	
	@Test
	public void testCrossShardBatchInsert() {
		try {
			deleteAllShardsByDbTable(dao, mod, tableMod);
			
			ClientTestModel[] pList = new ClientTestModel[mod * (1 + tableMod)*tableMod/2];
			int x = 0;
			for(int i = 0; i < mod; i++) {
				for(int j = 0; j < tableMod; j++) {
					for(int k = 0; k < j + 1; k ++) {
						ClientTestModel p = new ClientTestModel();
					
						p = new ClientTestModel();
						p.setId(1 + k);
						p.setAddress("aaa");
						p.setDbIndex(i);
						p.setTableIndex(j);
						
						pList[x++] = p;
					}
				}
			}
			
			dao.batchInsert(new DalHints(), Arrays.asList(pList));

			for(int i = 0; i < mod; i++) {
				for(int j = 0; j < tableMod; j++) {
					Assert.assertEquals(j + 1, getCount(i, j));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testCrossShardDelete() {
		try {
			for(int i = 0; i < mod; i++) {
				for(int j = 0; j < tableMod; j++) {
					Assert.assertEquals(j + 1, getCount(i, j));
				}
			}
			
			ClientTestModel[] pList = new ClientTestModel[mod * (1 + tableMod)*tableMod/2];
			int x = 0;
			for(int i = 0; i < mod; i++) {
				for(int j = 0; j < tableMod; j++) {
					for(int k = 0; k < j + 1; k ++) {
						ClientTestModel p = new ClientTestModel();
					
						p = new ClientTestModel();
						p.setId(1 + k);
						p.setAddress("aaa");
						p.setDbIndex(i);
						p.setTableIndex(j);
						
						pList[x++] = p;
					}
				}
			}
			
			dao.batchDelete(new DalHints(), Arrays.asList(pList));

			for(int i = 0; i < mod; i++) {
				for(int j = 0; j < tableMod; j++) {
					Assert.assertEquals(0, getCount(i, j));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private class ClientTestDalParser implements DalParser<ClientTestModel>{
		private String databaseName;
		private static final String tableName= "dal_client_test";
		private String[] columnNames = new String[]{
			"id","quantity","dbIndex","tableIndex","type","address","last_changed"
		};
		private String[] primaryKeyNames = new String[]{"id"};
		private int[] columnTypes = new int[]{
			Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR, Types.TIMESTAMP
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
			model.setDbIndex(rs.getInt(3));
			model.setTableIndex(rs.getInt(4));
			model.setType(rs.getShort(5));
			model.setAddress(rs.getString(6));
			model.setLastChanged(rs.getTimestamp(7));
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
			map.put("dbIndex", pojo.getDbIndex());
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
		private Integer dbIndex;
		private Integer tableIndex;
		private Short type;
		private String address;
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
