package com.ctrip.platform.dal.tester.shard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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
		
		// By tabelShard
		for(int i = 0; i < mod; i++) {
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().inTableShard(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().inTableShard(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}
		
		// By tableShardValue
		for(int i = 0; i < mod; i++) {
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().setTableShardValue(String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().setTableShardValue(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}
		
		// By shardColValue
		for(int i = 0; i < mod; i++) {
			if(i%2 == 0)
				model = dao.queryByPk(1, new DalHints().setShardColValue("index", String.valueOf(i)));
			else
				model = dao.queryByPk(1, new DalHints().setShardColValue("index", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}

		// By shardColValue
		for(int i = 0; i < mod; i++) {
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
		
		// By tabelShard
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setId(1);
			model = dao.queryByPk(pk, new DalHints().inTableShard(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}
		
		// By tableShardValue
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setId(1);
			model = dao.queryByPk(pk, new DalHints().setTableShardValue(i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}
		
		// By shardColValue
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setId(1);
			model = dao.queryByPk(pk, new DalHints().setShardColValue("index", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}

		// By shardColValue
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setId(1);
			model = dao.queryByPk(pk, new DalHints().setShardColValue("tableIndex", i));
			Assert.assertEquals(1, model.getId().intValue());
			Assert.assertEquals(i, model.getTableIndex().intValue());
		}
		
		// By fields
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setId(1);
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
		
		// By tabelShard
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setType((short)1);
			models = dao.queryLike(pk, new DalHints().inTableShard(i));
			Assert.assertEquals(i + 1, models.size());
		}
		
		// By tableShardValue
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setType((short)1);
			models = dao.queryLike(pk, new DalHints().setTableShardValue(i));
			Assert.assertEquals(i + 1, models.size());
		}
		
		// By shardColValue
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setType((short)1);
			models = dao.queryLike(pk, new DalHints().setShardColValue("index", i));
			Assert.assertEquals(i + 1, models.size());
		}

		// By shardColValue
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setType((short)1);
			models = dao.queryLike(pk, new DalHints().setShardColValue("tableIndex", i));
			Assert.assertEquals(i + 1, models.size());
		}
		
		// By fields
		for(int i = 0; i < mod; i++) {
			pk = new ClientTestModel();
			pk.setType((short)1);
			pk.setTableIndex(i);
			models = dao.queryLike(pk, new DalHints());
			Assert.assertEquals(i + 1, models.size());
		}
	}
	
//	/**
//	 * Query by Entity with where clause
//	 * @throws SQLException
//	 */
//	@Test
//	public void testQueryWithWhereClause() throws SQLException{
//		String whereClause = "type=? and id=?";
//		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.SMALLINT, 1);
//		parameters.set(2, Types.INTEGER, 1);
//		
//		List<ClientTestModel> models = dao.query(whereClause, parameters, new DalHints());
//		Assert.assertTrue(null != models);
//		Assert.assertEquals(1, models.size());
//		Assert.assertEquals("SH INFO", models.get(0).getAddress());
//	}
//	
//	/**
//	 * Test Query the first row with where clause
//	 * @throws SQLException 
//	 */
//	@Test
//	public void testQueryFirstWithWhereClause() throws SQLException{
//		String whereClause = "type=?";
//		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.SMALLINT, 1);
//		
//		ClientTestModel model = dao.queryFirst(whereClause, parameters, new DalHints());
//		Assert.assertTrue(null != model);
//		Assert.assertEquals(1, model.getId().intValue());
//	}
//	
//	/**
//	 * Test Query the first row with where clause failed
//	 * @throws SQLException
//	 */
//	@Test
//	public void testQueryFirstWithWhereClauseFailed() throws SQLException{
//		String whereClause = "type=?";
//		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.SMALLINT, 10);
//		try{
//			dao.queryFirst(whereClause, parameters, new DalHints());
//			Assert.fail();
//		}catch(Throwable e) {
//		}
//	}
//	
//	/**
//	 * Test Query the top rows with where clause
//	 * @throws SQLException
//	 */
//	@Test
//	public void testQueryTopWithWhereClause() throws SQLException{
//		String whereClause = "type=?";
//		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.SMALLINT, 1);
//		
//		List<ClientTestModel> models = dao.queryTop(whereClause, parameters, new DalHints(), 2);
//		Assert.assertTrue(null != models);
//		Assert.assertEquals(2, models.size());
//	}
//	
//	/**
//	 * Test Query the top rows with where clause failed
//	 * @throws SQLException
//	 */
//	@Test
//	public void testQueryTopWithWhereClauseFailed() throws SQLException{
//		String whereClause = "type=?";
//		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.SMALLINT, 10);
//		
//		List<ClientTestModel> models = dao.queryTop(whereClause, parameters, new DalHints(), 2);
//		Assert.assertTrue(null != models);
//		Assert.assertEquals(0, models.size());
//	}
//	
//	/**
//	 * Test Query range of result with where clause
//	 * @throws SQLException
//	 */
//	@Test
//	public void testQueryFromWithWhereClause() throws SQLException{
//		String whereClause = "type=?";
//		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.SMALLINT, 1);
//		
//		List<ClientTestModel> models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, 1);
//		Assert.assertTrue(null != models);
//		Assert.assertEquals(1, models.size());
//	}
//	
//	/**
//	 * Test Query range of result with where clause failed when return not enough recodes
//	 * @throws SQLException
//	 */
//	@Test
//	public void testQueryFromWithWhereClauseFailed() throws SQLException{
//		String whereClause = "type=?";
//		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.SMALLINT, 1);
//		
//		List<ClientTestModel> models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, 10);
//		Assert.assertTrue(null != models);
//		Assert.assertEquals(3, models.size());
//	}
//	
//	/**
//	 * Test Query range of result with where clause when return empty collection
//	 * @throws SQLException
//	 */
//	@Test
//	public void testQueryFromWithWhereClauseEmpty() throws SQLException{
//		String whereClause = "type=?";
//		StatementParameters parameters = new StatementParameters();
//		parameters.set(1, Types.SMALLINT, 10);
//		
//		List<ClientTestModel> models = dao.queryFrom(whereClause, parameters, new DalHints(), 0, 10);
//		Assert.assertTrue(null != models);
//		Assert.assertEquals(0, models.size());
//	}
//	
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