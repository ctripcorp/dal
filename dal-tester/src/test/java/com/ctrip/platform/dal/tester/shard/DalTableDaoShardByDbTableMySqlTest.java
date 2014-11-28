package com.ctrip.platform.dal.tester.shard;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTableDaoShardByDbTableMySqlTest extends BaseDalTableDaoShardByDbTableTest{
	public DalTableDaoShardByDbTableMySqlTest() {
		super(DATABASE_NAME_MYSQL);
	}
	
	private final static String DATABASE_NAME_MYSQL = "dao_test_mysql_dbTableShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	private final static int tableMod = 4;
	
	//Create the the table
	private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME + "_%d";
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"_%d("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "dbIndex int,"
			+ "tableIndex int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	private static DalClient clientMySql;
	
	static {
		try {
			DalClientFactory.initClientFactory();
			clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
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
				sqls[i]= String.format(DROP_TABLE_SQL_MYSQL_TPL, k);
				k++;
			}
			try {
				clientMySql.batchUpdate(sqls, hints.inShard(0).continueOnError());
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
						String.format(DROP_TABLE_SQL_MYSQL_TPL, j), 
						String.format(CREATE_TABLE_SQL_MYSQL_TPL, j)};
				clientMySql.batchUpdate(sqls, hints.inShard(i));
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
				sqls[j] = String.format(DROP_TABLE_SQL_MYSQL_TPL, j);
			}
			clientMySql.batchUpdate(sqls, hints.inShard(i));
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
				insertSqls = new String[i + 1];
				for(int j = 0; j < i + 1; j ++) {
					int id = j + 1;
					int quantity = id * (k + 1) * (i+1);
					insertSqls[j] = "INSERT INTO " + TABLE_NAME + "_"+ i
							+ " VALUES(" + id + ", " + quantity + ", " + k + ", " + i + ",1, 'SH INFO', NULL)";
				}
				clientMySql.batchUpdate(insertSqls, hints.inShard(k));
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
					clientMySql.update(sql + "_" + i, parameters, hints.inShard(j));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reset() throws SQLException {
		tearDown();
		setUp();
	}
}
