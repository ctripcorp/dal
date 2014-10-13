package com.ctrip.platform.dal.tester.baseDao;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class DirectClientDaoShardTest {
	private final static String DATABASE_NAME_MOD = "dao_test_mod_mysql";
	private final static String DATABASE_NAME_SIMPLE = "dao_test_simple";
	private final static String DATABASE_NAME_SQLSVR = "dao_test_mysql1";//"dao_test_sqlsvr";
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
	
	static {
		try {
//			DalClientFactory.initClientFactory("/DalMult.config");
			DalClientFactory.initClientFactory();
			clientSqlSvr = DalClientFactory.getClient(DATABASE_NAME_SQLSVR);
			clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
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
		sqls = new String[] { DROP_TABLE_SQL_MYSQL, CREATE_TABLE_SQL_MYSQL};//DROP_TABLE_SQL_SQLSVR, CREATE_TABLE_SQL_SQLSVR};
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
		sqls = new String[] { DROP_TABLE_SQL_MYSQL};//DROP_TABLE_SQL_SQLSVR};
		for (int i = 0; i < sqls.length; i++) {
			clientSqlSvr.update(sqls[i], parameters, hints);
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(4, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(5, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(6, 12, 2, 'SZ INFO', NULL)" };
		int[] counts = clientMySql.batchUpdate(insertSqls, hints);
		assertArrayEquals(new int[] { 1, 1, 1 }, counts);
		
		//For Sql Server
		hints = new DalHints();
		insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(1, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(2, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(3, 12, 2, 'SZ INFO', NULL)" };
//				"SET IDENTITY_INSERT "+ TABLE_NAME +" ON",
//				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
//						+ " VALUES(4, 10, 1, 'SH INFO')",
//				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
//						+ " VALUES(5, 11, 1, 'BJ INFO')",
//				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
//						+ " VALUES(6, 12, 2, 'SZ INFO')",
//				"SET IDENTITY_INSERT "+ TABLE_NAME +" OFF"};
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
	public void testModByShardValue() {
		try {
			DalClient client = DalClientFactory.getClient(DATABASE_NAME_MOD);
			
			String sql = "select id from " + TABLE_NAME;
			
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints().setShardValue(1);
			Number o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(1, o.longValue());
			
			hints.setShardValue(2);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(4, o.longValue());

			hints.setShardValue(3);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(1, o.longValue());

			hints.setShardValue(4);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(4, o.longValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testModByParameters() {
		try {
			DalClient client = DalClientFactory.getClient(DATABASE_NAME_MOD);
			
			String sql = "select id from " + TABLE_NAME + " where id = ?";
			StatementParameters parameters;
			DalHints hints;
			Number o;
			
			parameters = new StatementParameters();
			hints = new DalHints();
			parameters.set(1, "id", Types.INTEGER, 1);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(1, o.longValue());
			
			parameters = new StatementParameters();
			hints = new DalHints();
			parameters.set(1, "id", Types.INTEGER, 3);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(3, o.longValue());

			parameters = new StatementParameters();
			hints = new DalHints();
			parameters.set(1, "id", Types.INTEGER, 4);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(4, o.longValue());

			parameters = new StatementParameters();
			hints = new DalHints();
			parameters.set(1, "id", Types.INTEGER, 6);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(6, o.longValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testModByShardCol() {
		try {
			DalClient client = DalClientFactory.getClient(DATABASE_NAME_MOD);
			
			String sql = "select id from " + TABLE_NAME + " where id = ?";
			StatementParameters parameters;
			DalHints hints;
			Number o;
			Map<String, Integer> shardColValues;
			
			parameters = new StatementParameters();
			shardColValues = new HashMap<String, Integer>();
			hints = new DalHints().setShardColValue("id", 1);
			parameters.set(1, Types.INTEGER, 1);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(1, o.longValue());
			
			parameters = new StatementParameters();
			shardColValues = new HashMap<String, Integer>();
			hints = new DalHints().setShardColValue("id", 3);
			parameters.set(1, Types.INTEGER, 3);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(3, o.longValue());
			
			parameters = new StatementParameters();
			shardColValues = new HashMap<String, Integer>();
			hints = new DalHints().setShardColValue("id", 4);
			parameters.set(1, Types.INTEGER, 4);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(4, o.longValue());
			
			parameters = new StatementParameters();
			hints = new DalHints().setShardColValue("id", 6);
			parameters.set(1, Types.INTEGER, 6);
			o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertEquals(6, o.longValue());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSimple() {
		try {
			DalClient client = DalClientFactory.getClient(DATABASE_NAME_SIMPLE);
			
			String sql = "select id from " + TABLE_NAME;
			
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints().inShard("0").masterOnly();
			
			Number o = (Number)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertNotNull(o);
			assertEquals(4, o.longValue());
			
			hints = new DalHints().inShard("1").masterOnly();

			Long l = (Long)client.query(sql, parameters, hints, new DalScalarExtractor());
			assertNotNull(l);
			assertEquals(1, l.longValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}