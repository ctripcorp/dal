package com.ctrip.platform.dal.tester.shard;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
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

public class CrossShardTableDaoTest {
	private final static String DATABASE_NAME_MOD = "dao_test_mod";
	private final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr";
	private final static String DATABASE_NAME_MYSQL = "dao_test_mysql";
	
	private final static String TABLE_NAME = "dal_client_test";
	
	private final static String DROP_TABLE_SQL_MYSQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_MYSQL = "CREATE TABLE " + TABLE_NAME +"("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	
	private final static String DROP_TABLE_SQL_SQLSVR = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQLSVR = "CREATE TABLE " + TABLE_NAME +"("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,type smallint, "
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
		String[] sqls = new String[] { DROP_TABLE_SQL_MYSQL, CREATE_TABLE_SQL_MYSQL};
		clientMySql.batchUpdate(sqls, hints);
		
		// For SQL server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		sqls = new String[] { DROP_TABLE_SQL_SQLSVR, CREATE_TABLE_SQL_SQLSVR};
		for (int i = 0; i < sqls.length; i++) {
			clientSqlSvr.update(sqls[i], parameters, hints);
		}	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL_MYSQL};
		clientMySql.batchUpdate(sqls, hints);
		
		//For Sql Server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		sqls = new String[] { DROP_TABLE_SQL_SQLSVR};
		for (int i = 0; i < sqls.length; i++) {
			clientSqlSvr.update(sqls[i], parameters, hints);
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(1, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(2, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(3, 12, 2, 'SZ INFO', NULL)" };
		int[] counts = clientMySql.batchUpdate(insertSqls, hints);
		assertArrayEquals(new int[] { 1, 1, 1 }, counts);
		
		//For Sql Server
		hints = new DalHints();
		insertSqls = new String[] {
				"SET IDENTITY_INSERT "+ TABLE_NAME +" ON",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(4, 10, 1, 'SH INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(5, 11, 1, 'BJ INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(6, 12, 2, 'SZ INFO')",
				"SET IDENTITY_INSERT "+ TABLE_NAME +" OFF"};
		clientSqlSvr.batchUpdate(insertSqls, hints);
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			clientMySql.update(sql, parameters, hints);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		sql = "DELETE FROM " + TABLE_NAME;
		parameters = new StatementParameters();
		hints = new DalHints();
		try {
			clientSqlSvr.update(sql, parameters, hints);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCrossShardCombinedInsert() {
		try {
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();
			
			hints.inShard("0");
			dao.delete("id > 0", parameters, hints);
			hints.inShard("1");
			dao.delete("id > 0", parameters, hints);
			
			ClientTestModel p = new ClientTestModel();
			
			ClientTestModel[] pList = new ClientTestModel[3];
			p = new ClientTestModel();
			p.setId(1);
			p.setAddress("aaa");
			pList[0] = p;
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			pList[1] = p;
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			pList[2] = p;
			
			Map<String, KeyHolder> keyHolders =  new HashMap<String, KeyHolder>();
			dao.crossShardCombinedInsert(hints, keyHolders, pList);
			
			assertEquals(2, keyHolders.size());
			assertEquals(1, keyHolders.get("0").size());
			assertEquals(2, keyHolders.get("1").size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCrossShardBatchInsert() {
		try {
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();
			
			hints.inShard("0");
			dao.delete("id > 0", parameters, hints);
			hints.inShard("1");
			dao.delete("id > 0", parameters, hints);
			
			ClientTestModel p = new ClientTestModel();
			
			ClientTestModel[] pList = new ClientTestModel[3];
			p = new ClientTestModel();
			p.setId(1);
			p.setAddress("aaa");
			pList[0] = p;
			p = new ClientTestModel();
			p.setId(2);
			p.setAddress("aaa");
			pList[1] = p;
			p = new ClientTestModel();
			p.setId(3);
			p.setAddress("aaa");
			pList[2] = p;
			
			Map<String, int[]> counts = dao.crossShardBatchInsert(hints, pList);
			
			assertEquals(2, counts.size());
			assertEquals(1, counts.get("0").length);
			assertEquals(2, counts.get("1").length);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCrossShardBatchDelete() {
		try {
			DalHints hints = new DalHints();
			
			ClientTestModel p = new ClientTestModel();
			
			ClientTestModel[] pList = new ClientTestModel[3];
			p = new ClientTestModel();
			p.setId(1);
			pList[0] = p;
			p = new ClientTestModel();
			p.setId(2);
			pList[1] = p;
			p = new ClientTestModel();
			p.setId(3);
			pList[2] = p;
			
			Map<String, int[]> counts = dao.crossShardBatchDelete(hints, pList);
			
			assertEquals(2, counts.size());
			assertEquals(1, counts.get("0").length);
			assertEquals(2, counts.get("1").length);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static class ClientTestDalParser implements DalParser<ClientTestModel>{
		private static final String databaseName=DATABASE_NAME_MOD;
		private static final String tableName= "dal_client_test";
		private static final String[] columnNames = new String[]{
			"id","quantity","type","address","last_changed"
		};
		private static final String[] primaryKeyNames = new String[]{"id"};
		private static final int[] columnTypes = new int[]{
			Types.INTEGER, Types.INTEGER, Types.SMALLINT, Types.VARCHAR, Types.TIMESTAMP
		};
		@Override
		public ClientTestModel map(ResultSet rs, int rowNum)
				throws SQLException {
			ClientTestModel model = new ClientTestModel();
			model.setId(rs.getInt(1));
			model.setQuantity(rs.getInt(2));
			model.setType(rs.getShort(3));
			model.setAddress(rs.getString(4));
			model.setLastChanged(rs.getTimestamp(5));
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
			map.put("type", pojo.getType());
			map.put("address", pojo.getAddress());
			map.put("last_changed", pojo.getLastChanged());
			return map;
		}
		
	}
	
	private static class ClientTestModel {
		private Integer id;
		private Integer quantity;
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