package com.ctrip.platform.dal.tester.shard;

import static com.ctrip.platform.dal.dao.unittests.DalTestHelper.deleteAllShardsByDbTable;
import static com.ctrip.platform.dal.dao.unittests.DalTestHelper.getCountByDbTable;

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
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTableDaoShardByDbTableTest {
	private final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr_dbTableShard";
	private final static String DATABASE_NAME_MOD = DATABASE_NAME_SQLSVR;
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	private final static int tableMod = 4;
	
	//Create the the table
	private final static String DROP_TABLE_SQL_SQLSVR_TPL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "_%d') "
			+ "DROP TABLE  "+ TABLE_NAME + "_%d";
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQLSVR_TPL = "CREATE TABLE " + TABLE_NAME +"_%d("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,dbIndex int,tableIndex int,type smallint, "
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
	
	public static void clear() {
		DalHints hints = new DalHints();
		String[] sqls = null;
		// For SQL server
		hints = new DalHints();
		int k = 0;
		for(int j = 0; j < 10; j++) {
			sqls = new String[1000];
			for(int i = 0; i < 1000; i++) {
				sqls[i]= String.format(DROP_TABLE_SQL_SQLSVR_TPL, k, k);
				k++;
			}
			try {
				clientSqlSvr.batchUpdate(sqls, hints.inShard(0).continueOnError());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		// For SQL server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			for(int j = 0; j < tableMod; j++) {
				sqls = new String[] { 
						String.format(DROP_TABLE_SQL_SQLSVR_TPL, j, j), 
						String.format(CREATE_TABLE_SQL_SQLSVR_TPL, j)};
				clientSqlSvr.batchUpdate(sqls, hints.inShard(i));
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		//For Sql Server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			sqls = new String[tableMod];
			for(int j = 0; j < tableMod; j++) {
				sqls[j] = String.format(DROP_TABLE_SQL_SQLSVR_TPL, j, j);
			}
			clientSqlSvr.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@Before
	public void setUp() throws SQLException {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		//For Sql Server
		hints = new DalHints();
		for(int k = 0; k < mod; k++) {
			for(int i = 0; i < tableMod; i++) {
				insertSqls = new String[i + 3];
				insertSqls[0] = "SET IDENTITY_INSERT "+ TABLE_NAME + "_" + i + " ON";
				for(int j = 0; j < i + 1; j ++) {
					int id = j + 1;
					int quantity = id * (k + 1) * (i+1);
					insertSqls[j + 1] = "INSERT INTO " + TABLE_NAME + "_" + i + "(Id, quantity,dbIndex,tableIndex,type,address)"
								+ " VALUES(" + id + ", " + quantity + ", " + k + ", " + i + ",1, 'SH INFO')";
				}
						
				insertSqls[i+2] = "SET IDENTITY_INSERT "+ TABLE_NAME + "_" + i +" OFF";
				clientSqlSvr.batchUpdate(insertSqls, hints.inShard(k));
			}
		}
	}

	@After
	public void tearDown() throws SQLException {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		sql = "DELETE FROM " + TABLE_NAME;
		parameters = new StatementParameters();
		hints = new DalHints();
		try {
			for(int j = 0; j < mod; j++) {
				for(int i = 0; i < tableMod; i++) {
					clientSqlSvr.update(sql + "_" + i, parameters, hints.inShard(j));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void reset() throws SQLException {
		tearDown();
		setUp();
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
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
			Assert.assertEquals(id * (shardId + 1) * (i+1), model.getQuantity().intValue());

			// By tableShardValue
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.clone().setTableShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.clone().setTableShardValue(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
			Assert.assertEquals(id * (shardId + 1) * (i+1), model.getQuantity().intValue());

			// By shardColValue
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.clone().setShardColValue("table", String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.clone().setShardColValue("table", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
			Assert.assertEquals(id * (shardId + 1) * (i+1), model.getQuantity().intValue());
			
			// By shardColValue
			if(i%2 == 0)
				model = dao.queryByPk(1, hints.clone().setShardColValue("tableIndex", String.valueOf(i)));
			else
				model = dao.queryByPk(1, hints.clone().setShardColValue("tableIndex", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
			Assert.assertEquals(id * (shardId + 1) * (i+1), model.getQuantity().intValue());
		}
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
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
			Assert.assertEquals(id * (shardId + 1) * (i+1), model.getQuantity().intValue());
			
			// By tableShardValue
			model = dao.queryByPk(pk, hints.clone().setTableShardValue(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());

			// By shardColValue
			model = dao.queryByPk(pk, hints.clone().setShardColValue("table", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
			
			// By shardColValue
			model = dao.queryByPk(pk, hints.clone().setShardColValue("tableIndex", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());

			// By fields
			pk.setTableIndex(i);
			pk.setDbIndex(shardId);
			model = dao.queryByPk(pk, new DalHints());
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
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
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			Assert.assertEquals(i, models.get(0).getTableIndex().intValue());

			// By tableShardValue
			models = dao.queryLike(pk, hints.clone().setTableShardValue(i));
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			Assert.assertEquals(i, models.get(0).getTableIndex().intValue());

			// By shardColValue
			models = dao.queryLike(pk, hints.clone().setShardColValue("table", i));
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			Assert.assertEquals(i, models.get(0).getTableIndex().intValue());

			// By shardColValue
			models = dao.queryLike(pk, hints.clone().setShardColValue("tableIndex", i));
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			Assert.assertEquals(i, models.get(0).getTableIndex().intValue());

			// By fields
			pk.setDbIndex(shardId);
			pk.setTableIndex(i);
			models = dao.queryLike(pk, new DalHints());
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			Assert.assertEquals(i, models.get(0).getTableIndex().intValue());
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
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());

			// By tableShardValue
			models = dao.query(whereClause, parameters, hints.clone().setTableShardValue(i));
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());

			// By shardColValue
			models = dao.query(whereClause, parameters, hints.clone().setShardColValue("table", i));
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());

			// By shardColValue
			models = dao.query(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i));
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());

			// By parameters
			whereClause += " and tableIndex=? and dbIndex=?";
			parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "id", Types.SMALLINT, i + 1);
			parameters.set(3, "tableIndex", Types.SMALLINT, i);
			parameters.set(4, "dbIndex", Types.SMALLINT, shardId);

			models = dao.query(whereClause, parameters, new DalHints());
			Assert.assertEquals(1, models.size());
			Assert.assertEquals("SH INFO", models.get(0).getAddress());
			Assert.assertEquals(models.get(0).getTableIndex(), new Integer(i));
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
		}
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
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
			
			// By tableShardValue
			model = dao.queryFirst(whereClause, parameters, hints.clone().setTableShardValue(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());

			// By shardColValue
			model = dao.queryFirst(whereClause, parameters, hints.clone().setShardColValue("table", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
			
			// By shardColValue
			model = dao.queryFirst(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());

			// By parameters
			whereClause += " and tableIndex=? and dbIndex=?";
			parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);
			parameters.set(3, "dbIndex", Types.SMALLINT, shardId);
			model = dao.queryFirst(whereClause, parameters, hints.clone());
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
			Assert.assertEquals(shardId, model.getDbIndex().intValue());
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
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			
			// By tableShardValue
			models = dao.queryTop(whereClause, parameters, hints.clone().setTableShardValue(i), i + 1);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());

			// By shardColValue
			models = dao.queryTop(whereClause, parameters, hints.clone().setShardColValue("table", i), i + 1);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			
			// By shardColValue
			models = dao.queryTop(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i), i + 1);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());

			whereClause += " and tableIndex=? and dbIndex=?";
			// By parameters
			parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);
			parameters.set(3, "dbIndex", Types.SMALLINT, shardId);
			models = dao.queryTop(whereClause, parameters, hints.clone(), i + 1);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
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
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
		
			// By tableShardValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setTableShardValue(i), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("table", i), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
		}

		whereClause += " and tableIndex=? and dbIndex=?";
		// By parameters
		for(int i = 0; i < tableMod; i++) {
			StatementParameters parameters = new StatementParameters();
			parameters.set(1, "type", Types.SMALLINT, 1);
			parameters.set(2, "tableIndex", Types.SMALLINT, i);
			parameters.set(3, "dbIndex", Types.SMALLINT, shardId);

			models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, i + 1);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
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
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());

			// By tableShardValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setTableShardValue(i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());

			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("table", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
			
			// By shardColValue
			models = dao.queryFrom(whereClause, parameters, hints.clone().setShardColValue("tableIndex", i), 0, 10);
			Assert.assertTrue(null != models);
			Assert.assertEquals(i + 1, models.size());
			Assert.assertEquals(shardId, models.get(0).getDbIndex().intValue());
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
			// By shard
			testInsertSingle(i, new DalHints().inShard(i));

			// By shardValue
			testInsertSingle(i, new DalHints().setShardValue(i));

			// By shardColValue
			testInsertSingle(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testInsertSingle(i, new DalHints().setShardColValue("dbIndex", i));
		}
		
		deleteAllShardsByDbTable(dao, mod, tableMod);
		
		model.setTableIndex(0);
		model.setDbIndex(3);
		
		res = dao.insert(new DalHints(), model);
		Assert.assertEquals(1, getCount(1, 0));
	}

	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	public void testInsertSingle(int shardId, DalHints hints) throws SQLException{
		reset();
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
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));

			// By tableShardValue
			res = dao.insert(hints.clone().setTableShardValue(i), model);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));

			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("table", i), model);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));
			
			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("tableIndex", i), model);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));
			
			// By fields
			model.setTableIndex(i);
			res = dao.insert(hints.clone(), model);
			Assert.assertEquals((i + 1) + j++ * 1, getCount(shardId, i));
		}
	}
	
	private void deleteAllShards(int shardId) throws SQLException {
		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			dao.delete("1=1", new StatementParameters(), new DalHints().inShard(shardId).inTableShard(i));
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
			// By shard
			testInsertDouble(i, new DalHints().inShard(i));

			// By shardValue
			testInsertDouble(i, new DalHints().setShardValue(i));

			// By shardColValue
			testInsertDouble(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testInsertDouble(i, new DalHints().setShardColValue("dbIndex", i));
		}
		
		deleteAllShardsByDbTable(dao, mod, tableMod);
		
		// By fields not same shard
		model.setTableIndex(0);
		model.setDbIndex(0);
		
		model2.setTableIndex(1);
		model2.setDbIndex(1);
		
		dao.insert(new DalHints(), model, model2);
		Assert.assertEquals(1, getCount(0, 0));
		Assert.assertEquals(1, getCount(1, 1));

	}

	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	public void testInsertDouble(int shardId, DalHints hints) throws SQLException{
		reset();
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
			res = dao.insert(hints, model, model2);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			res = dao.insert(hints.clone().inTableShard(i), model, model2);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

			// By tableShardValue
			res = dao.insert(hints.clone().setTableShardValue(i), model, model2);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("table", i), model, model2);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("tableIndex", i), model, model2);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By fields same shard
			model.setTableIndex(i);
			model2.setTableIndex(i);
			res = dao.insert(hints.clone(), model, model2);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
		}
		
		deleteAllShards(shardId);
		
		// By fields not same shard
		model.setTableIndex(0);
		model2.setTableIndex(1);
		dao.insert(hints.clone(), model, model2);
		Assert.assertEquals(1, getCount(shardId, 0));
		Assert.assertEquals(1, getCount(shardId, 1));
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
			// By shard
			testInsertMultiple(i, new DalHints().inShard(i));

			// By shardValue
			testInsertMultiple(i, new DalHints().setShardValue(i));

			// By shardColValue
			testInsertMultiple(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testInsertMultiple(i, new DalHints().setShardColValue("dbIndex", i));
		}
		
		deleteAllShardsByDbTable(dao, mod, tableMod);
		
		// By fields not same shard
		entities[0].setTableIndex(0);
		entities[0].setDbIndex(0);
		
		entities[1].setTableIndex(1);
		entities[1].setDbIndex(0);
		
		entities[2].setTableIndex(2);
		entities[2].setDbIndex(1);
		
		dao.insert(new DalHints().continueOnError(), entities);
		Assert.assertEquals(1, getCountByDbTable(dao, 0, 0));
		Assert.assertEquals(1, getCountByDbTable(dao, 0, 1));
		Assert.assertEquals(1, getCountByDbTable(dao, 1, 2));
	}
	
	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	public void testInsertMultiple(int shardId, DalHints hints) throws SQLException{
		reset();
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
			res = dao.insert(hints.clone().inShard(shardId), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			res = dao.insert(hints.clone().inTableShard(i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

			// By tableShardValue
			res = dao.insert(hints.clone().setTableShardValue(i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("table", i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			
			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			
			// By fields same shard
			entities[0].setTableIndex(i);
			entities[1].setTableIndex(i);
			entities[2].setTableIndex(i);
			res = dao.insert(hints.clone(), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
		}
		
		deleteAllShards(shardId);
		
		// By fields not same shard
		entities[0].setTableIndex(0);
		entities[1].setTableIndex(1);
		entities[2].setTableIndex(2);
		dao.insert(hints.clone(), entities);
		Assert.assertEquals(1, getCount(shardId, 0));
		Assert.assertEquals(1, getCount(shardId, 1));
		Assert.assertEquals(1, getCount(shardId, 2));
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
			// By shard
			testInsertMultipleAsList(i, new DalHints().inShard(i));

			// By shardValue
			testInsertMultipleAsList(i, new DalHints().setShardValue(i));

			// By shardColValue
			testInsertMultipleAsList(i, new DalHints().setShardColValue("index", i));
			
			// By shardColValue
			testInsertMultipleAsList(i, new DalHints().setShardColValue("dbIndex", i));
		}
		
		deleteAllShardsByDbTable(dao, mod, tableMod);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(0).setDbIndex(0);
		
		entities.get(1).setTableIndex(1);
		entities.get(1).setDbIndex(1);

		entities.get(2).setTableIndex(2);
		entities.get(2).setDbIndex(2);
		
		res = dao.insert(new DalHints().continueOnError(), entities);
		Assert.assertEquals(1, getCountByDbTable(dao, 0, 0));
		Assert.assertEquals(1, getCountByDbTable(dao, 1, 1));
		Assert.assertEquals(1, getCountByDbTable(dao, 0, 2));
	}
	
	/**
	 * Test Insert multiple entities one by one
	 * @throws SQLException
	 */
	public void testInsertMultipleAsList(int shardId, DalHints hints) throws SQLException{
		reset();
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
			res = dao.insert(hints.clone(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			res = dao.insert(hints.clone().inTableShard(i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

			// By tableShardValue
			res = dao.insert(hints.clone().setTableShardValue(i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));

			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("table", i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			
			// By shardColValue
			res = dao.insert(hints.clone().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(hints.clone(), entities);
			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
		}
		
		deleteAllShards(shardId);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(1).setTableIndex(1);
		entities.get(2).setTableIndex(2);
		dao.insert(hints.clone(), entities);
		Assert.assertEquals(1, getCount(shardId, 0));
		Assert.assertEquals(1, getCount(shardId, 1));
		Assert.assertEquals(1, getCount(shardId, 2));
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
			// By shard
			testInsertMultipleWithContinueOnErrorHints(i, new DalHints().continueOnError().inShard(i));

			// By shardValue
			testInsertMultipleWithContinueOnErrorHints(i, new DalHints().continueOnError().setShardValue(i));

			// By shardColValue
			testInsertMultipleWithContinueOnErrorHints(i, new DalHints().continueOnError().setShardColValue("index", i));
			
			// By shardColValue
			testInsertMultipleWithContinueOnErrorHints(i, new DalHints().continueOnError().setShardColValue("dbIndex", i));
		}
		
		deleteAllShardsByDbTable(dao, mod, tableMod);
		
		// By fields not same shard
		entities[0].setTableIndex(0);
		entities[0].setDbIndex(0);

		entities[1].setTableIndex(1);
		entities[1].setDbIndex(1);
		
		entities[2].setTableIndex(2);
		entities[2].setDbIndex(2);
		
		res = dao.insert(new DalHints().continueOnError(), entities);
		Assert.assertEquals(1, getCountByDbTable(dao, 0, 0));
		Assert.assertEquals(0, getCountByDbTable(dao, 1, 1));
		Assert.assertEquals(1, getCountByDbTable(dao, 0, 2));
	}

	/**
	 * Test Test Insert multiple entities one by one with continueOnError hints
	 * @throws SQLException
	 */
	public void testInsertMultipleWithContinueOnErrorHints(int shardId, DalHints hints) throws SQLException{
		reset();
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
			res = dao.insert(hints.clone(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			res = dao.insert(hints.clone().continueOnError().inTableShard(i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

			// By tableShardValue
			res = dao.insert(hints.clone().continueOnError().setTableShardValue(i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

			// By shardColValue
			res = dao.insert(hints.clone().continueOnError().setShardColValue("table", i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By shardColValue
			res = dao.insert(hints.clone().continueOnError().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By fields same shard
			entities[0].setTableIndex(i);
			entities[0].setDbIndex(shardId);
			
			entities[1].setTableIndex(i);
			entities[1].setDbIndex(shardId);

			entities[2].setTableIndex(i);
			entities[2].setDbIndex(shardId);
			res = dao.insert(hints.clone().continueOnError(), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
		}
		
		deleteAllShards(shardId);
		
		// By fields not same shard
		entities[0].setTableIndex(0);
		entities[1].setTableIndex(1);
		entities[2].setTableIndex(2);
		dao.insert(hints.clone().continueOnError(), entities);
		Assert.assertEquals(1, getCount(shardId, 0));
		Assert.assertEquals(0, getCount(shardId, 1));
		Assert.assertEquals(1, getCount(shardId, 2));
	}

	private int getCount(int shardId, int tableShardId) throws SQLException {
		return getCountByDbTable(dao, shardId, tableShardId);
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
			// By shard
			testInsertMultipleAsListWithContinueOnErrorHints(i, new DalHints().continueOnError().inShard(i));

			// By shardValue
			testInsertMultipleAsListWithContinueOnErrorHints(i, new DalHints().continueOnError().setShardValue(i));

			// By shardColValue
			testInsertMultipleAsListWithContinueOnErrorHints(i, new DalHints().continueOnError().setShardColValue("index", i));
			
			// By shardColValue
			testInsertMultipleAsListWithContinueOnErrorHints(i, new DalHints().continueOnError().setShardColValue("dbIndex", i));
		}
		
		deleteAllShardsByDbTable(dao, mod, tableMod);
		
		// By fields not same shard
		entities.get(0).setTableIndex(0);
		entities.get(0).setDbIndex(0);

		entities.get(1).setTableIndex(1);
		entities.get(1).setDbIndex(1);
		
		entities.get(2).setTableIndex(2);
		entities.get(2).setDbIndex(2);
		res = dao.insert(new DalHints().continueOnError(), entities);
		Assert.assertEquals(1, getCount(0, 0));
		Assert.assertEquals(0, getCount(1, 1));
		Assert.assertEquals(1, getCount(0, 2));
	}
	
	/**
	 * Test Test Insert multiple entities one by one with continueOnError hints
	 * @throws SQLException
	 */
	public void testInsertMultipleAsListWithContinueOnErrorHints(int shardId, DalHints hints) throws SQLException{
		reset();
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
			res = dao.insert(hints.clone(), entities);
			Assert.fail();
		} catch (Exception e) {
		}

		for(int i = 0; i < tableMod; i++) {
			int j = 1;
			// By tabelShard
			dao.insert(hints.clone().continueOnError().inTableShard(i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By tableShardValue
			res = dao.insert(hints.clone().continueOnError().setTableShardValue(i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));

			// By shardColValue
			res = dao.insert(hints.clone().continueOnError().setShardColValue("table", i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By shardColValue
			res = dao.insert(hints.clone().continueOnError().setShardColValue("tableIndex", i), entities);
			Assert.assertEquals((i + 1) + j++ * 2, getCount(shardId, i));
			
			// By fields same shard
			entities.get(0).setTableIndex(i);
			entities.get(1).setTableIndex(i);
			entities.get(2).setTableIndex(i);
			res = dao.insert(hints.clone().continueOnError(), entities);
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
		dao.insert(hints.clone().continueOnError(), entities);
		Assert.assertEquals(1, getCount(shardId, 0));
		Assert.assertEquals(0, getCount(shardId, 1));
		Assert.assertEquals(1, getCount(shardId, 2));
	}
	
//	/**
//	 * Test Insert multiple entities with key-holder
//	 * @throws SQLException
//	 */
//	@Test
//	public void testInsertMultipleWithKeyHolder() throws SQLException{
//		ClientTestModel[] entities = new ClientTestModel[3];
//		for (int i = 0; i < 3; i++) {
//			ClientTestModel model = new ClientTestModel();
//			model.setQuantity(10 + 1%3);
//			model.setType(((Number)(1%3)).shortValue());
//			model.setAddress("CTRIP");
//			entities[i] = model;
//		}
//
//		KeyHolder holder = new KeyHolder();
//		int res;
//		try {
//			res = dao.insert(hints.clone(), holder, entities);
//			Assert.fail();
//		} catch (Exception e) {
//		}
//
//		for(int i = 0; i < tableMod; i++) {
//			int j = 1;
//			holder = null;
//			// By tabelShard
//			// holder = new KeyHolder();
//
//			res = dao.insert(hints.clone().inTableShard(i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
////			Assert.assertEquals(3, res);
////			Assert.assertEquals(3, holder.getKeyList().size());		 
////			Assert.assertTrue(holder.getKey(0).longValue() > 0);
////			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
//
//			// By tableShardValue
//			res = dao.insert(hints.clone().setTableShardValue(i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//
//			// By shardColValue
//			res = dao.insert(hints.clone().setShardColValue("table", i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//			// By shardColValue
//			// holder = new KeyHolder();
//			res = dao.insert(hints.clone().setShardColValue("tableIndex", i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//			// By fields same shard
//			// holder = new KeyHolder();
//			entities[0].setTableIndex(i);
//			entities[1].setTableIndex(i);
//			entities[2].setTableIndex(i);
//			res = dao.insert(hints.clone().continueOnError(), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//		}
//		
//		deleteAllShards(shardId);
//		
//		// By fields not same shard
//		entities[0].setTableIndex(0);
//		entities[1].setTableIndex(1);
//		entities[2].setTableIndex(2);
//		res = dao.insert(new DalHints().continueOnError(), holder, entities);
//		Assert.assertEquals(1, getCount(0));
//		Assert.assertEquals(1, getCount(1));
//		Assert.assertEquals(1, getCount(2));
//	}
//	
//	/**
//	 * Test Insert multiple entities with key-holder
//	 * @throws SQLException
//	 */
//	@Test
//	public void testInsertMultipleAsListWithKeyHolder() throws SQLException{
//		List<ClientTestModel> entities = new ArrayList<ClientTestModel>();
//		for (int i = 0; i < 3; i++) {
//			ClientTestModel model = new ClientTestModel();
//			model.setQuantity(10 + 1%3);
//			model.setType(((Number)(1%3)).shortValue());
//			model.setAddress("CTRIP");
//			entities.add(model);
//		}
//
//		KeyHolder holder = new KeyHolder();
//		int res;
//		try {
//			res = dao.insert(hints.clone(), holder, entities);
//			Assert.fail();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		for(int i = 0; i < tableMod; i++) {
//			int j = 1;
//			holder = null;
//			// By tabelShard
//			// holder = new KeyHolder();
//			res = dao.insert(hints.clone().inTableShard(i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
////			Assert.assertEquals(3, res);
////			Assert.assertEquals(3, holder.getKeyList().size());		 
////			Assert.assertTrue(holder.getKey(0).longValue() > 0);
////			Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
//
//			// By tableShardValue
//			// holder = new KeyHolder();
//			res = dao.insert(hints.clone().setTableShardValue(i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//			// By shardColValue
//			// holder = new KeyHolder();
//			res = dao.insert(hints.clone().setShardColValue("table", i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//			// By shardColValue
//			// holder = new KeyHolder();
//			res = dao.insert(hints.clone().setShardColValue("tableIndex", i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//			// By fields same shard
//			// holder = new KeyHolder();
//			entities.get(0).setTableIndex(i);
//			entities.get(1).setTableIndex(i);
//			entities.get(2).setTableIndex(i);
//			res = dao.insert(hints.clone(), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//		}
//		
//		deleteAllShards(shardId);
//		
//		// By fields not same shard
//		holder = new KeyHolder();
//		entities.get(0).setTableIndex(0);
//		entities.get(1).setTableIndex(1);
//		entities.get(2).setTableIndex(2);
//		res = dao.insert(new DalHints(), null, entities);
//		Assert.assertEquals(1, getCount(0));
//		Assert.assertEquals(1, getCount(1));
//		Assert.assertEquals(1, getCount(2));
////		Assert.assertEquals(3, res);
////		Assert.assertEquals(3, holder.getKeyList().size());		 
////		Assert.assertTrue(holder.getKey(0).longValue() > 0);
////		Assert.assertTrue(holder.getKeyList().get(0).containsKey("GENERATED_KEYS"));
//	}
//	
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
//		
//		KeyHolder holder = new KeyHolder();
//		int res;
//		try {
//			res = dao.combinedInsert(hints.clone(), holder, entities);
//			Assert.fail();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		for(int i = 0; i < tableMod; i++) {
//			int j = 1;
//			holder = null;
//			// By tabelShard
//			// holder = new KeyHolder();
//			res = dao.combinedInsert(hints.clone().inTableShard(i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//			// By tableShardValue
//			// holder = new KeyHolder();
//			res = dao.combinedInsert(hints.clone().setTableShardValue(i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//
//			// By shardColValue
//			// holder = new KeyHolder();
//			res = dao.combinedInsert(hints.clone().setShardColValue("table", i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//			// By shardColValue
//			// holder = new KeyHolder();
//			res = dao.combinedInsert(hints.clone().setShardColValue("tableIndex", i), holder, entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//		}
//		
//		// For combined insert, the shard id must be defined or change bd deduced.
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
//
//		int[] res;
//		try {
//			res = dao.batchInsert(hints.clone(), entities);
//			Assert.fail();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		for(int i = 0; i < tableMod; i++) {
//			int j = 1;
//			// By tabelShard
//			res = dao.batchInsert(hints.clone().inTableShard(i), entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//			// By tableShardValue
//			res = dao.batchInsert(hints.clone().setTableShardValue(i), entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//
//			// By shardColValue
//			res = dao.batchInsert(hints.clone().setShardColValue("table", i), entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//			
//			// By shardColValue
//			res = dao.batchInsert(hints.clone().setShardColValue("tableIndex", i), entities);
//			Assert.assertEquals((i + 1) + j++ * 3, getCount(shardId, i));
//		}
//		
//		// For combined insert, the shard id must be defined or change bd deduced.
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
//			model.setQuantity(10 + 1%3);
//			model.setType(((Number)(1%3)).shortValue());
//			model.setAddress("CTRIP");
//			entities[i] = model;
//		}
//		
//		int res;
//		// By tabelShard
//		Assert.assertEquals(1, getCount(0));
//		res = dao.delete(hints.clone().inTableShard(0), entities);
//		Assert.assertEquals(0, getCount(0));
//
//		// By tableShardValue
//		Assert.assertEquals(2, getCount(1));
//		res = dao.delete(hints.clone().setTableShardValue(1), entities);
//		Assert.assertEquals(0, getCount(1));
//		
//		// By shardColValue
//		Assert.assertEquals(3, getCount(2));
//		res = dao.delete(hints.clone().setShardColValue("table", 2), entities);
//		Assert.assertEquals(0, getCount(2));
//		
//		// By shardColValue
//		Assert.assertEquals(4, getCount(3));
//		res = dao.delete(hints.clone().setShardColValue("tableIndex", 3), entities);
//		Assert.assertEquals(1, getCount(3));
//		
//		// By fields same shard
//		// holder = new KeyHolder();
//		entities[0].setTableIndex(0);
//		entities[1].setTableIndex(1);
//		entities[2].setTableIndex(2);
//		dao.insert(hints.clone(), entities);
//		Assert.assertEquals(1, getCount(0));
//		Assert.assertEquals(1, getCount(1));
//		Assert.assertEquals(1, getCount(2));
//		entities[0] = dao.queryFirst("1=1", new StatementParameters(), hints.clone().inTableShard(0));
//		entities[1] = dao.queryFirst("1=1", new StatementParameters(), hints.clone().inTableShard(1));
//		entities[2] = dao.queryFirst("1=1", new StatementParameters(), hints.clone().inTableShard(2));
//		res = dao.delete(hints.clone(), entities);
//		Assert.assertEquals(0, getCount(0));
//		Assert.assertEquals(0, getCount(1));
//		Assert.assertEquals(0, getCount(2));
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
//			model.setQuantity(10 + 1%3);
//			model.setType(((Number)(1%3)).shortValue());
//			model.setAddress("CTRIP");
//			entities[i] = model;
//		}
//		
//		int[] res;
//		try {
//			res = dao.batchDelete(hints.clone(), entities);
//			Assert.fail();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		// By tabelShard
//		Assert.assertEquals(1, getCount(0));
//		res = dao.batchDelete(hints.clone().inTableShard(0), entities);
//		Assert.assertEquals(0, getCount(0));
//
//		// By tableShardValue
//		Assert.assertEquals(2, getCount(1));
//		res = dao.batchDelete(hints.clone().setTableShardValue(1), entities);
//		Assert.assertEquals(0, getCount(1));
//		
//		// By shardColValue
//		Assert.assertEquals(3, getCount(2));
//		res = dao.batchDelete(hints.clone().setShardColValue("table", 2), entities);
//		Assert.assertEquals(0, getCount(2));
//		
//		// By shardColValue
//		Assert.assertEquals(4, getCount(3));
//		res = dao.batchDelete(hints.clone().setShardColValue("tableIndex", 3), entities);
//		Assert.assertEquals(1, getCount(3));
//		
//		// By fields same shard
//		// holder = new KeyHolder();
//		entities[0].setTableIndex(0);
//		entities[1].setTableIndex(0);
//		entities[2].setTableIndex(0);
//		dao.insert(hints.clone(), entities);
//		Assert.assertEquals(3, getCount(0));
//		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), hints.clone().inTableShard(0));
//		res = dao.batchDelete(hints.clone().inTableShard(0), result);
//		Assert.assertEquals(0, getCount(0));
//	}
//	
//	/**
//	 * Test update multiple entities with primary key
//	 * @throws SQLException
//	 */
//	@Test
//	public void testUpdateMultiple() throws SQLException{
//		DalHints hints = hints.clone();
//		ClientTestModel[] entities = new ClientTestModel[4];
//		for (int i = 0; i < 4; i++) {
//			ClientTestModel model = new ClientTestModel();
//			model.setId(i+1);
//			model.setAddress("CTRIP");
//			entities[i] = model;
//		}
//		
//		
//		int res;
//		try {
//			res = dao.update(hints, entities);
//			Assert.fail();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		// By tabelShard
//		entities[0].setAddress("test1");
//		res = dao.update(hints.clone().inTableShard(0), entities[0]);
//		Assert.assertEquals("test1", dao.queryByPk(1, hints.inTableShard(0)).getAddress());
//
//		// By tableShardValue
//		entities[1].setQuantity(-11);
//		res = dao.update(hints.clone().setTableShardValue(1), entities[1]);
//		Assert.assertEquals(-11, dao.queryByPk(2, hints.inTableShard(1)).getQuantity().intValue());
//		
//		// By shardColValue
//		entities[2].setType((short)3);
//		res = dao.update(hints.clone().setShardColValue("table", 2), entities[2]);
//		Assert.assertEquals((short)3, dao.queryByPk(3, hints.inTableShard(2)).getType().shortValue());
//
//		// By shardColValue
//		entities[3].setAddress("testa");
//		res = dao.update(hints.clone().setShardColValue("tableIndex", 3), entities);
//		Assert.assertEquals("testa", dao.queryByPk(4, hints.inTableShard(3)).getAddress());
//		
//		// By fields same shard
//		// holder = new KeyHolder();
//		entities[0].setTableIndex(0);
//		entities[0].setAddress("1234");
//		entities[1].setTableIndex(0);
//		entities[1].setAddress("1234");
//		entities[2].setTableIndex(0);
//		entities[2].setAddress("1234");
//		entities[3].setTableIndex(0);
//		entities[3].setAddress("1234");
//		dao.update(hints.clone(), entities);
//		List<ClientTestModel> result = dao.query("1=1", new StatementParameters(), hints.clone().inTableShard(0));
//		for(ClientTestModel m: result)
//			Assert.assertEquals("1234", m.getAddress());
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
//
//		DalHints hints = hints.clone();
//		int res;
//		try {
//			res = dao.delete(whereClause, parameters, hints);
//			Assert.fail();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		// By tabelShard
//		res = dao.delete(whereClause, parameters, hints.clone().inTableShard(0));
//		Assert.assertEquals(0, dao.query(whereClause, parameters, hints.clone().inTableShard(0)).size());
//
//		// By tableShardValue
//		Assert.assertEquals(2, getCount(1));
//		res = dao.delete(whereClause, parameters, hints.clone().setTableShardValue(1));
//		Assert.assertEquals(0, dao.query(whereClause, parameters, hints.clone().setTableShardValue(1)).size());
//		
//		// By shardColValue
//		Assert.assertEquals(3, getCount(2));
//		res = dao.delete(whereClause, parameters, hints.clone().setShardColValue("table", 2));
//		Assert.assertEquals(0, dao.query(whereClause, parameters, hints.clone().setShardColValue("table", 2)).size());
//		
//		// By shardColValue
//		Assert.assertEquals(4, getCount(3));
//		res = dao.delete(whereClause, parameters, hints.clone().setShardColValue("tableIndex", 3));
//		Assert.assertEquals(0, dao.query(whereClause, parameters, hints.clone().setShardColValue("tableIndex", 3)).size());
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
//		DalHints hints = hints.clone();
//		int res;
//		try {
//			res = dao.update(sql, parameters, hints);
//			Assert.fail();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		// By tabelShard
//		sql = "UPDATE " + TABLE_NAME
//				+ "_0 SET address = 'CTRIP' WHERE id = 1";
//		res = dao.update(sql, parameters, hints.clone().inTableShard(0));
//		Assert.assertEquals("CTRIP", dao.queryByPk(1, hints.clone().inTableShard(0)).getAddress());
//
//		// By tableShardValue
//		sql = "UPDATE " + TABLE_NAME
//				+ "_1 SET address = 'CTRIP' WHERE id = 1";
//		Assert.assertEquals(2, getCount(1));
//		res = dao.update(sql, parameters, hints.clone().setTableShardValue(1));
//		Assert.assertEquals("CTRIP", dao.queryByPk(1, hints.clone().setTableShardValue(1)).getAddress());
//		
//		// By shardColValue
//		sql = "UPDATE " + TABLE_NAME
//				+ "_2 SET address = 'CTRIP' WHERE id = 1";
//		Assert.assertEquals(3, getCount(2));
//		res = dao.update(sql, parameters, hints.clone().setShardColValue("table", 2));
//		Assert.assertEquals("CTRIP", dao.queryByPk(1, hints.clone().setShardColValue("table", 2)).getAddress());
//		
//		// By shardColValue
//		sql = "UPDATE " + TABLE_NAME
//				+ "_3 SET address = 'CTRIP' WHERE id = 1";
//		Assert.assertEquals(4, getCount(3));
//		res = dao.update(sql, parameters, hints.clone().setShardColValue("tableIndex", 3));
//		Assert.assertEquals("CTRIP", dao.queryByPk(1, hints.clone().setShardColValue("tableIndex", 3)).getAddress());
//
//	}
//	
//		@Test
//	public void testCrossShardInsert() {
//		try {
//			deleteAllShards(shardId);
//			
//			ClientTestModel p = new ClientTestModel();
//			
//			ClientTestModel[] pList = new ClientTestModel[6];
//			p = new ClientTestModel();
//			p.setId(1);
//			p.setAddress("aaa");
//			p.setTableIndex(0);
//			pList[0] = p;
//			
//			p = new ClientTestModel();
//			p.setId(2);
//			p.setAddress("aaa");
//			p.setTableIndex(1);
//			pList[1] = p;
//			
//			p = new ClientTestModel();
//			p.setId(3);
//			p.setAddress("aaa");
//			p.setTableIndex(2);
//			pList[2] = p;
//			
//			p = new ClientTestModel();
//			p.setId(4);
//			p.setAddress("aaa");
//			p.setTableIndex(3);
//			pList[3] = p;
//			
//			p = new ClientTestModel();
//			p.setId(5);
//			p.setAddress("aaa");
//			p.setTableIndex(4);
//			pList[4] = p;
//			
//			p = new ClientTestModel();
//			p.setId(5);
//			p.setAddress("aaa");
//			p.setTableIndex(5);
//			pList[5] = p;
//			
//			Map<String, KeyHolder> keyHolders =  new HashMap<String, KeyHolder>();
//			keyHolders = null;
//			dao.crossShardCombinedInsert(hints.clone(), keyHolders, pList);
//			Assert.assertEquals(2, getCount(0));
//			Assert.assertEquals(2, getCount(1));
//			Assert.assertEquals(1, getCount(2));
//			Assert.assertEquals(1, getCount(3));
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail();
//		}
//	}
//
//	@Test
//	public void testCrossShardBatchInsert() {
//		try {
//			deleteAllShards(shardId);
//			
//			ClientTestModel p = new ClientTestModel();
//			
//			ClientTestModel[] pList = new ClientTestModel[6];
//			p = new ClientTestModel();
//			p.setId(1);
//			p.setAddress("aaa");
//			p.setTableIndex(0);
//			pList[0] = p;
//			
//			p = new ClientTestModel();
//			p.setId(2);
//			p.setAddress("aaa");
//			p.setTableIndex(1);
//			pList[1] = p;
//			
//			p = new ClientTestModel();
//			p.setId(3);
//			p.setAddress("aaa");
//			p.setTableIndex(2);
//			pList[2] = p;
//			
//			p = new ClientTestModel();
//			p.setId(4);
//			p.setAddress("aaa");
//			p.setTableIndex(3);
//			pList[3] = p;
//			
//			p = new ClientTestModel();
//			p.setId(5);
//			p.setAddress("aaa");
//			p.setTableIndex(4);
//			pList[4] = p;
//			
//			p = new ClientTestModel();
//			p.setId(5);
//			p.setAddress("aaa");
//			p.setTableIndex(5);
//			pList[5] = p;
//			
//			dao.crossShardBatchInsert(hints.clone(), pList);
//			Assert.assertEquals(2, getCount(0));
//			Assert.assertEquals(2, getCount(1));
//			Assert.assertEquals(1, getCount(2));
//			Assert.assertEquals(1, getCount(3));
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail();
//		}
//	}
//	
//	@Test
//	public void testCrossShardDelete() {
//		try {
//			ClientTestModel p = new ClientTestModel();
//			
//			ClientTestModel[] pList = new ClientTestModel[6];
//			p = new ClientTestModel();
//			p.setId(1);
//			p.setAddress("aaa");
//			p.setTableIndex(0);
//			pList[0] = p;
//			
//			p = new ClientTestModel();
//			p.setId(1);
//			p.setAddress("aaa");
//			p.setTableIndex(1);
//			pList[1] = p;
//			
//			p = new ClientTestModel();
//			p.setId(1);
//			p.setAddress("aaa");
//			p.setTableIndex(2);
//			pList[2] = p;
//			
//			p = new ClientTestModel();
//			p.setId(1);
//			p.setAddress("aaa");
//			p.setTableIndex(3);
//			pList[3] = p;
//			
//			p = new ClientTestModel();
//			p.setId(2);
//			p.setAddress("aaa");
//			p.setTableIndex(5);
//			pList[4] = p;
//			
//			p = new ClientTestModel();
//			p.setId(2);
//			p.setAddress("aaa");
//			p.setTableIndex(6);
//			pList[5] = p;
//			
//			dao.crossShardBatchDelete(new DalHints(), pList);
//			Assert.assertEquals(0, getCount(0));
//			Assert.assertEquals(0, getCount(1));
//			Assert.assertEquals(1, getCount(2));
//			Assert.assertEquals(3, getCount(3));
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail();
//		}
//	}

	private static class ClientTestDalParser implements DalParser<ClientTestModel>{
		private static final String databaseName=DATABASE_NAME_MOD;
		private static final String tableName= "dal_client_test";
		private static final String[] columnNames = new String[]{
			"id","quantity","dbIndex","tableIndex","type","address","last_changed"
		};
		private static final String[] primaryKeyNames = new String[]{"id"};
		private static final int[] columnTypes = new int[]{
			Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR, Types.TIMESTAMP
		};
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

		public void setId(int id) {
			this.id = id;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public Integer getDbIndex() {
			return dbIndex;
		}

		public void setDbIndex(int dbIndex) {
			this.dbIndex = dbIndex;
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
