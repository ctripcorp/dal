package com.ctrip.platform.dal.tester.shard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

/**
 * Only test shard by table case
 * @author jhhe
 *
 */
public class DalTabelDaoTableShardTest {
	private final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr_tableShard";
	private final static String DATABASE_NAME_MOD = DATABASE_NAME_SQLSVR;
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 4;
	
	//Create the the table
	private final static String DROP_TABLE_SQL_SQLSVR_TPL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "_%d') "
			+ "DROP TABLE  "+ TABLE_NAME + "_%d";
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQLSVR_TPL = "CREATE TABLE " + TABLE_NAME +"_%d("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,tableIndex int,type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	private static DalClient clientSqlSvr;
	private static DalParser<ClientTestModel> clientTestParser = new ClientTestDalParser();
	private static DalTableDao<ClientTestModel> dao;
	
	static {
		try {
//			DalClientFactory.initClientFactory("/DalMult.config");
			DalClientFactory.initClientFactory();
			clientSqlSvr = DalClientFactory.getClient(DATABASE_NAME_SQLSVR);
			dao = new DalTableDao<ClientTestModel>(clientTestParser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		// For SQL server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { 
					String.format(DROP_TABLE_SQL_SQLSVR_TPL, i, i), 
					String.format(CREATE_TABLE_SQL_SQLSVR_TPL, i)};
			for (int j = 0; j < sqls.length; j++) {
				clientSqlSvr.update(sqls[j], parameters, hints);
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		//For Sql Server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { String.format(DROP_TABLE_SQL_SQLSVR_TPL, i, i)};
			for (int j = 0; j < sqls.length; j++) {
				clientSqlSvr.update(sqls[j], parameters, hints);
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		//For Sql Server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[i + 3];
			insertSqls[0] = "SET IDENTITY_INSERT "+ TABLE_NAME + "_" + i + " ON";
			for(int j = 0; j < i + 1; j ++) {
				int id = j + 1;
				int quantity = 10 + j;
				insertSqls[j + 1] = "INSERT INTO " + TABLE_NAME + "_" + i + "(Id, quantity,tableIndex,type,address)"
							+ " VALUES(" + id + ", " + quantity + ", " + i + ",1, 'SH INFO')";
			}
					
			insertSqls[i+2] = "SET IDENTITY_INSERT "+ TABLE_NAME + "_" + i +" OFF";
			clientSqlSvr.batchUpdate(insertSqls, hints);
		}
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		sql = "DELETE FROM " + TABLE_NAME;
		parameters = new StatementParameters();
		hints = new DalHints();
		try {
			for(int i = 0; i < mod; i++) {
				clientSqlSvr.update(sql + "_" + i, parameters, hints);
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
			// By tabelShard
			res = dao.insert(new DalHints().inTableShard(i), model);
			Assert.assertEquals(1, res);

			// By tableShardValue
			res = dao.insert(new DalHints().setTableShardValue(i), model);
			Assert.assertEquals(1, res);

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), model);
			Assert.assertEquals(1, res);
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), model);
			Assert.assertEquals(1, res);
			
			// By fields
			model.setTableIndex(i);
			res = dao.insert(new DalHints(), model);
			Assert.assertEquals(1, res);
		}
	}
	
	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	@Test
	public void testInsertDouble() throws SQLException{
		ClientTestModel model = new ClientTestModel();
		model.setQuantity(10 + 1%3);
		model.setType(((Number)(1%3)).shortValue());
		model.setAddress("CTRIP");

		ClientTestModel model2 = new ClientTestModel();
		model2.setQuantity(10 + 1%3);
		model2.setType(((Number)(1%3)).shortValue());
		model2.setAddress("CTRIP");
		
		int res;
		try {
			res = dao.insert(new DalHints(), model, model2);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
			// By tabelShard
			res = dao.insert(new DalHints().inTableShard(i), model, model2);
			Assert.assertEquals(2, res);

			// By tableShardValue
			res = dao.insert(new DalHints().setTableShardValue(i), model, model2);
			Assert.assertEquals(2, res);

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), model, model2);
			Assert.assertEquals(2, res);
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), model, model2);
			Assert.assertEquals(2, res);
			
			// By fields same shard
			model.setTableIndex(i);
			model2.setTableIndex(i);
			res = dao.insert(new DalHints(), model, model2);
			Assert.assertEquals(2, res);
			
			// By fields not same shard
			model.setTableIndex(i);
			model2.setTableIndex((i+1)%mod);
			res = dao.insert(new DalHints(), model, model2);
			Assert.assertEquals(2, res);

			// By fields
			model.setTableIndex((i+1)%mod);
			model2.setTableIndex(i);
			res = dao.insert(new DalHints(), model, model2);
			Assert.assertEquals(2, res);
		}
	}

	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultiple() throws SQLException{
		ClientTestModel[] entities = new ClientTestModel[3];
		for (int i = 0; i < 3; i++) {
			ClientTestModel model = new ClientTestModel();
			model.setQuantity(10 + 1%3);
			model.setType(((Number)(1%3)).shortValue());
			model.setAddress("CTRIP");
			entities[i] = model;
		}
		
		int res;
		try {
			res = dao.insert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
			// By tabelShard
			res = dao.insert(new DalHints().inTableShard(i), entities);
			Assert.assertEquals(3, res);

			// By tableShardValue
			res = dao.insert(new DalHints().setTableShardValue(i), entities);
			Assert.assertEquals(3, res);

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), entities);
			Assert.assertEquals(3, res);
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals(3, res);
			
			// By fields same shard
			entities[0].setTableIndex(i);
			entities[1].setTableIndex(i);
			entities[2].setTableIndex(i);
			res = dao.insert(new DalHints(), entities);
			Assert.assertEquals(3, res);
			
			// By fields not same shard
			entities[0].setTableIndex(i);
			entities[1].setTableIndex(i + 1);
			entities[2].setTableIndex(i + 2);
			res = dao.insert(new DalHints(), entities);
			Assert.assertEquals(3, res);
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

		int res;
		try {
			res = dao.insert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
			// By tabelShard
			res = dao.insert(new DalHints().inTableShard(i), entities);
			Assert.assertEquals(3, res);

			// By tableShardValue
			res = dao.insert(new DalHints().setTableShardValue(i), entities);
			Assert.assertEquals(3, res);

			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("index", i), entities);
			Assert.assertEquals(3, res);
			
			// By shardColValue
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals(3, res);
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints(), entities);
			Assert.assertEquals(3, res);
			
			// By fields not same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i + 1);
			entities.get(2).setTableIndex(i + 2);
			res = dao.insert(new DalHints(), entities);
			Assert.assertEquals(3, res);
		}
	}
	
	/**
	 * Test Test Insert multiple entities one by one with continueOnError hints
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleWithContinueOnErrorHints() throws SQLException{
		ClientTestModel[] entities = new ClientTestModel[3];
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
			entities[i] = model;
		}
		
		int res;
		try {
			res = dao.insert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
			// By tabelShard
			res = dao.insert(new DalHints().continueOnError().inTableShard(i), entities);
			Assert.assertEquals(2, res);

			// By tableShardValue
			res = dao.insert(new DalHints().continueOnError().setTableShardValue(i), entities);
			Assert.assertEquals(2, res);

			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("index", i), entities);
			Assert.assertEquals(2, res);
			
			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals(2, res);
			
			// By fields same shard
			entities[0].setTableIndex(i);
			entities[1].setTableIndex(i);
			entities[2].setTableIndex(i);
			res = dao.insert(new DalHints().continueOnError(), entities);
			Assert.assertEquals(2, res);
			
			// By fields not same shard
			entities[0].setTableIndex(i);
			entities[1].setTableIndex(i + 1);
			entities[2].setTableIndex(i + 2);
			res = dao.insert(new DalHints().continueOnError(), entities);
			Assert.assertEquals(2, res);
		}
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
		
		int res;
		try {
			res = dao.insert(new DalHints(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
			// By tabelShard
			res = dao.insert(new DalHints().continueOnError().inTableShard(i), entities);
			Assert.assertEquals(2, res);

			// By tableShardValue
			res = dao.insert(new DalHints().continueOnError().setTableShardValue(i), entities);
			Assert.assertEquals(2, res);

			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("index", i), entities);
			Assert.assertEquals(2, res);
			
			// By shardColValue
			res = dao.insert(new DalHints().continueOnError().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals(2, res);
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints().continueOnError(), entities);
			Assert.assertEquals(2, res);
			
			// By fields not same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i + 1);
			entities.get(2).setTableIndex(i + 2);
			res = dao.insert(new DalHints().continueOnError(), entities);
			Assert.assertEquals(2, res);
		}
	}
	
	/**
	 * Test Insert multiple entities with key-holder
	 * @throws SQLException
	 */
	@Test
	public void testInsertMultipleWithKeyHolder() throws SQLException{
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
			res = dao.insert(new DalHints(), holder, entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
			// By tabelShard
			holder = new KeyHolder();
			res = dao.insert(new DalHints().inTableShard(i), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));

			// By tableShardValue
			holder = new KeyHolder();
			res = dao.insert(new DalHints().setTableShardValue(i), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));

			// By shardColValue
			holder = new KeyHolder();
			res = dao.insert(new DalHints().setShardColValue("index", i), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
			
			// By shardColValue
			holder = new KeyHolder();
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
			
			// By fields same shard
			holder = new KeyHolder();
			entities[0].setTableIndex(i);
			entities[1].setTableIndex(i);
			entities[2].setTableIndex(i);
			res = dao.insert(new DalHints().continueOnError(), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
			
			// By fields not same shard
			entities[0].setTableIndex(i);
			entities[1].setTableIndex(i + 1);
			entities[2].setTableIndex(i + 2);
			res = dao.insert(new DalHints().continueOnError(), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
		}
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
		int res;
		try {
			res = dao.insert(new DalHints(), holder, entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < mod; i++) {
			// By tabelShard
			holder = new KeyHolder();
			res = dao.insert(new DalHints().inTableShard(i), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));

			// By tableShardValue
			holder = new KeyHolder();
			res = dao.insert(new DalHints().setTableShardValue(i), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));

			// By shardColValue
			holder = new KeyHolder();
			res = dao.insert(new DalHints().setShardColValue("index", i), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
			
			// By shardColValue
			holder = new KeyHolder();
			res = dao.insert(new DalHints().setShardColValue("tableIndex", i), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
			
			// By fields same shard
			holder = new KeyHolder();
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(new DalHints().continueOnError(), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
			
			// By fields not same shard
			holder = new KeyHolder();
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i + 1);
			entities.get(2).setTableIndex(i + 2);
			res = dao.insert(new DalHints().continueOnError(), holder, entities);
			Assert.assertEquals(3, res);
			Assert.assertEquals(3, holder.getKeyList().size());		 
			Assert.assertTrue(holder.getKey(0).longValue() > 0);
			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
		}
	}
	
//	/**
//	 * Test Insert multiple entities with one SQL Statement
//	 * @throws SQLException
//	 */
//	@Test
//	public void testCombinedInsert() throws SQLException{
//		ClientTestModel[] entities = new ClientTestModel[3];
//		for (int i = 0; i < 3; i++) {
//			ClientTestModel model = new ClientTestModel();
//			model.setQuantity(10 + 1%3);
//			model.setType(((Number)(1%3)).shortValue());
//			model.setAddress("CTRIP");
//			entities[i] = model;
//		}
//		KeyHolder holder = new KeyHolder();
//		DalHints hints = new DalHints();
//		int res = dao.combinedInsert(hints, holder, entities);
//		Assert.assertEquals(3, res);
//	}
//	
//	/**
//	 * Test Batch Insert multiple entities
//	 * @throws SQLException
//	 */
//	@Test
//	public void testBatchInsert() throws SQLException{
//		ClientTestModel[] entities = new ClientTestModel[3];
//		for (int i = 0; i < 3; i++) {
//			ClientTestModel model = new ClientTestModel();
//			model.setQuantity(10 + 1%3);
//			model.setType(((Number)(1%3)).shortValue());
//			model.setAddress("CTRIP");
//			entities[i] = model;
//		}
//		int[] res = dao.batchInsert(new DalHints(), entities);
//		Assert.assertArrayEquals(new int[]{1,1,1}, res);
//		
//	}
//	
//	/**
//	 * Test delete multiple entities
//	 * @throws SQLException
//	 */
//	@Test
//	public void testDeleteMultiple() throws SQLException{
//		ClientTestModel[] entities = new ClientTestModel[3];
//		for (int i = 0; i < 3; i++) {
//			ClientTestModel model = new ClientTestModel();
//			model.setId(i+1);
//			entities[i] = model;
//		}
//		int res = dao.delete(new DalHints(), entities);
//		Assert.assertEquals(3, res);
//	}
//	
//	/**
//	 * Test batch delete multiple entities
//	 * @throws SQLException
//	 */
//	@Test
//	public void testBatchDelete() throws SQLException{
//		ClientTestModel[] entities = new ClientTestModel[3];
//		for (int i = 0; i < 3; i++) {
//			ClientTestModel model = new ClientTestModel();
//			model.setId(i+1);
//			entities[i] = model;
//		}
//		int[] res = dao.batchDelete(new DalHints(), entities);
//		Assert.assertArrayEquals(new int[]{1,1,1}, res);
//	}
//	
//	/**
//	 * Test update multiple entities with primary key
//	 * @throws SQLException
//	 */
//	@Test
//	public void testUpdateMultiple() throws SQLException{
//		DalHints hints = new DalHints();
//		ClientTestModel[] entities = new ClientTestModel[3];
//		for (int i = 0; i < 3; i++) {
//			ClientTestModel model = new ClientTestModel();
//			model.setId(i+1);
//			model.setAddress("CTRIP");
//			entities[i] = model;
//		}
//		int res = dao.update(hints, entities);
//		Assert.assertEquals(3, res);
//		
//		ClientTestModel model = dao.queryByPk(1, hints);
//		Assert.assertTrue(null != model);
//		Assert.assertEquals("CTRIP", model.getAddress());
//	}
//	
//	/**
//	 * Test delete entities with where clause and parameters
//	 * @throws SQLException
//	 */
//	@Test
//	public void testDeleteWithWhereClause() throws SQLException{
//		String whereClause = "type=?";
//		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.SMALLINT, 1);
//		DalHints hints = new DalHints();
//		int res = dao.delete(whereClause, parameters, hints);
//		Assert.assertEquals(3, res);
//		
//		List<ClientTestModel> models = dao.query(whereClause, parameters, hints);
//		Assert.assertEquals(0, models.size());
//	}
//	
//	/**
//	 * Test plain update with SQL
//	 * @throws SQLException
//	 */
//	@Test
//	public void testUpdatePlain() throws SQLException{
//		String sql = "UPDATE " + TABLE_NAME
//				+ " SET address = 'CTRIP' WHERE id = 1";
//		StatementParameters parameters = new StatementParameters();
//		DalHints hints = new DalHints();
//		int res = dao.update(sql, parameters, hints);
//		Assert.assertEquals(1, res);
//		
//		ClientTestModel model = dao.queryByPk(1, hints);
//		Assert.assertTrue(null != model);
//		Assert.assertEquals("CTRIP", model.getAddress());
//	}
	
	//	@Test
//	public void testCrossShardInsert() {
//		try {
//			StatementParameters parameters = new StatementParameters();
//			DalHints hints = new DalHints();
//			
//			hints.inShard("0");
//			dao.delete("id > 0", parameters, hints);
//			hints.inShard("1");
//			dao.delete("id > 0", parameters, hints);
//			
//			ClientTestModel p = new ClientTestModel();
//			
//			ClientTestModel[] pList = new ClientTestModel[3];
//			p = new ClientTestModel();
//			p.setId(1);
//			p.setAddress("aaa");
//			pList[0] = p;
//			p = new ClientTestModel();
//			p.setId(2);
//			p.setAddress("aaa");
//			pList[1] = p;
//			p = new ClientTestModel();
//			p.setId(3);
//			p.setAddress("aaa");
//			pList[2] = p;
//			
//			Map<String, KeyHolder> keyHolders =  new HashMap<String, KeyHolder>();
//			dao.crossShardCombinedInsert(new DalHints(), keyHolders, pList);
//			
//			assertEquals(2, keyHolders.size());
//			assertEquals(1, keyHolders.get("0").size());
//			assertEquals(2, keyHolders.get("1").size());
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	public void testCrossShardUpdate() {
//		try {
//			StatementParameters parameters = new StatementParameters();
//			DalHints hints = new DalHints();
//			
//			hints.inShard("0");
//			dao.delete("id > 0", parameters, hints);
//			hints.inShard("1");
//			dao.delete("id > 0", parameters, hints);
//			
//			ClientTestModel p = new ClientTestModel();
//			
//			ClientTestModel[] pList = new ClientTestModel[3];
//			p = new ClientTestModel();
//			p.setId(1);
//			p.setAddress("aaa");
//			pList[0] = p;
//			p = new ClientTestModel();
//			p.setId(2);
//			p.setAddress("aaa");
//			pList[1] = p;
//			p = new ClientTestModel();
//			p.setId(3);
//			p.setAddress("aaa");
//			pList[2] = p;
//			
//			Map<String, int[]> counts = dao.crossShardBatchInsert(new DalHints(), pList);
//			
//			assertEquals(2, counts.size());
//			assertEquals(1, counts.get("0").length);
//			assertEquals(2, counts.get("1").length);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//	
//	@Test
//	public void testCrossShardDelete() {
//		try {
//			DalHints hints = new DalHints();
//			
//			ClientTestModel p = new ClientTestModel();
//			
//			ClientTestModel[] pList = new ClientTestModel[3];
//			p = new ClientTestModel();
//			p.setId(1);
//			pList[0] = p;
//			p = new ClientTestModel();
//			p.setId(2);
//			pList[1] = p;
//			p = new ClientTestModel();
//			p.setId(3);
//			pList[2] = p;
//			
//			Map<String, int[]> counts = dao.crossShardBatchDelete(hints, pList);
//			
//			assertEquals(2, counts.size());
//			assertEquals(1, counts.get("0").length);
//			assertEquals(2, counts.get("1").length);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//	}

	private static class ClientTestDalParser implements DalParser<ClientTestModel>{
		private static final String databaseName=DATABASE_NAME_MOD;
		private static final String tableName= "dal_client_test";
		private static final String[] columnNames = new String[]{
			"id","quantity","tableIndex","type","address","last_changed"
		};
		private static final String[] primaryKeyNames = new String[]{"id"};
		private static final int[] columnTypes = new int[]{
			Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR, Types.TIMESTAMP
		};
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

//	@Test
	public void test2() {
//		try {
//			DalClient client = DalClientFactory.getClient("AbacusDB_INSERT_1");
//			StatementParameters parameters = new StatementParameters();
//			DalHints hints = new DalHints();
//			//String delete = "update AbacusAddInfoLog set PNR='dafas' where id = 100";
//			String select = "select PNR from AbacusAddInfoLog where LOGID = 100";
//			String update = "update AbacusAddInfoLog set PNR='dafas11' where LOGID = 100";
//			String restore = "update AbacusAddInfoLog set PNR='dafas' where LOGID = 100";
//			
//			hints = new DalHints();
//			Map<String, Integer> colValues = new HashMap<String, Integer>();
//			colValues.put("user_id", 0);
//			hints.set(DalHintEnum.shardColValues, colValues);
//
//			client.update(update, parameters, hints);
//			
//			client.query(select, parameters, hints, new DalResultSetExtractor<Object>() {
//				@Override
//				public Object extract(ResultSet rs) throws SQLException {
//					while(rs.next()){
//						System.out.println(rs.getObject(1));
//					}
//					return null;
//				}
//				
//			});
//			
//
//			client.update(restore, parameters, hints);
//			
//			client.query(select, parameters, hints, new DalResultSetExtractor<Object>() {
//				@Override
//				public Object extract(ResultSet rs) throws SQLException {
//					while(rs.next()){
//						System.out.println(rs.getObject(1));
//					}
//					return null;
//				}
//				
//			});
//						
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}	
}