package test.com.ctrip.platform.dal.dao.shard;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTableDaoShardByDbTableSqlSvrTest extends BaseDalTableDaoShardByDbTableTest {
	public DalTableDaoShardByDbTableSqlSvrTest() {
		super(DATABASE_NAME_SQLSVR, SqlServerDatabaseInitializer.diff);
	}

	private final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr_dbTableShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	private final static int tableMod = 4;
	
	//Create the the table
	private final static String DROP_TABLE_SQL_SQLSVR_TPL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "_%d') "
			+ "DROP TABLE  "+ TABLE_NAME + "_%d";
	
	//Create the the table
	private final static String DROP_TABLE_SQL_SQLSVR_TPL_1 = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQLSVR_TPL = "CREATE TABLE " + TABLE_NAME +"_%d("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,dbIndex int,tableIndex int,type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	private static DalClient clientSqlSvr;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		clientSqlSvr = DalClientFactory.getClient(DATABASE_NAME_SQLSVR);
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
			clientSqlSvr.update(DROP_TABLE_SQL_SQLSVR_TPL_1, new StatementParameters(), hints.inShard(i));
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
					insertSqls[j + 1] = "INSERT INTO " + TABLE_NAME + "_" + i + "(Id, quantity, dbIndex, tableIndex, type, address)"
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
}
