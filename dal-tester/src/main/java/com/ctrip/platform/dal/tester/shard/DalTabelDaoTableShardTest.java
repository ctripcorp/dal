package com.ctrip.platform.dal.tester.shard;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTabelDaoTableShardTest {
	private final static String DATABASE_NAME_MOD = "DB_TABLE_SHARD";
	private final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr";
	private final static String DATABASE_NAME_MYSQL = "dao_test_mysql";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 4;
	
	private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME + "_%d";
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"_%d("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "tableIndex int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	
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
	private static DalClient clientMySql;
	private static DalParser<ClientTestModel> clientTestParser = new ClientTestDalParser();
	private static DalTableDao<ClientTestModel> dao;
	
	static {
		try {
//			DalClientFactory.initClientFactory("/DalMult.config");
			DalClientFactory.initClientFactory();
			clientSqlSvr = DalClientFactory.getClient(DATABASE_NAME_SQLSVR);
			clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
			dao = new DalTableDao<ClientTestModel>(clientTestParser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { 
					String.format(DROP_TABLE_SQL_MYSQL_TPL,i), 
					String.format(CREATE_TABLE_SQL_MYSQL_TPL, i)};
			clientMySql.batchUpdate(sqls, hints);
		}
		
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
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { String.format(DROP_TABLE_SQL_MYSQL_TPL, i)};
			clientMySql.batchUpdate(sqls, hints);
		}
		
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
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[] {
					"INSERT INTO " + TABLE_NAME + "_"+ i
							+ " VALUES(1, 10, " + i + " ,1, 'SH INFO', NULL)",
					"INSERT INTO " + TABLE_NAME + "_"+ i
							+ " VALUES(3, 11, " + i + " ,1, 'BJ INFO', NULL)",
					"INSERT INTO " + TABLE_NAME + "_"+ i
							+ " VALUES(5, 12, " + i + " ,2, 'SZ INFO', NULL)" };
			int[] counts = clientMySql.batchUpdate(insertSqls, hints);
			assertArrayEquals(new int[] { 1, 1, 1 }, counts);
		}
		
		//For Sql Server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[] {
					"SET IDENTITY_INSERT "+ TABLE_NAME + "_" + i + " ON",
					"INSERT INTO " + TABLE_NAME + "_" + i + "(Id, quantity,tableIndex,type,address)"
							+ " VALUES(2, 10, " + i + ",1, 'SH INFO')",
					"INSERT INTO " + TABLE_NAME + "_" + i + "(Id, quantity,tableIndex,type,address)"
							+ " VALUES(4, 11, " + i + ",1, 'BJ INFO')",
					"INSERT INTO " + TABLE_NAME + "_" + i + "(Id, quantity,tableIndex,type,address)"
							+ " VALUES(6, 12, " + i + ",2, 'SZ INFO')",
					"SET IDENTITY_INSERT "+ TABLE_NAME + "_" + i +" OFF"};
			clientSqlSvr.batchUpdate(insertSqls, hints);
		}
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			for(int i = 0; i < mod; i++) {
				clientMySql.update(sql + "_" + i, parameters, hints);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

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
		ClientTestModel model = dao.queryByPk(1, new DalHints().setShardColValue("index", 1).setShardColValue("id", 1));
		Assert.assertTrue(null != model);
		Assert.assertEquals(10, model.getQuantity().intValue());
	}
//
//	/**
//	 * Query by Entity with Primary key
//	 * @throws SQLException
//	 */
//	@Test
//	public void testQueryByPkWithEntity() throws SQLException{
//		ClientTestModel pk = new ClientTestModel();
//		pk.setId(1);
//		ClientTestModel model = dao.queryByPk(pk, new DalHints());
//		Assert.assertTrue(null != model);
//		Assert.assertEquals(10, model.getQuantity().intValue());
//	}
//	
//	/**
//	 * Query by Entity without Primary key
//	 * @throws SQLException
//	 */
//	@Test
//	public void testQueryByPkWithEntityNoId() throws SQLException{
//		ClientTestModel pk = new ClientTestModel();
//		try {
//			Assert.assertNull(dao.queryByPk(pk, new DalHints()));
//		} catch (SQLException e) { 
//			Assert.fail();
//		}
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