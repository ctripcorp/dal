package test.com.ctrip.platform.dal.dao.task;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SqlServerTestInitializer {
	public final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr";
	
	public final static String TABLE_NAME = "dal_client_test";

	//Create the the table
	private final static String DROP_TABLE_SQL_SQLSVR_TPL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQLSVR_TPL = "CREATE TABLE " + TABLE_NAME +"("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,"
			+ "dbIndex int,"
			+ "tableIndex int,"
			+ "type smallint, "
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
		sqls = new String[] { DROP_TABLE_SQL_SQLSVR_TPL, CREATE_TABLE_SQL_SQLSVR_TPL};
		clientSqlSvr.batchUpdate(sqls, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		//For Sql Server
		hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		clientSqlSvr.update(DROP_TABLE_SQL_SQLSVR_TPL, parameters, hints);
	}

	@Before
	public static void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		//For Sql Server
		hints = new DalHints();
		insertSqls = new String[5];
		insertSqls[0] = "SET IDENTITY_INSERT "+ TABLE_NAME + " ON";
		for(int i = 0; i < 3; i ++) {
			int id = i + 1;
			int quantity = 10 + i;
			insertSqls[i + 1] = "INSERT INTO " + TABLE_NAME + "(Id, quantity,dbIndex,tableIndex,type,address)"
						+ " VALUES(" + id + ", " + quantity + ", 0," + i + ",1, 'SH INFO')";
		}
				
		insertSqls[4] = "SET IDENTITY_INSERT "+ TABLE_NAME + " OFF";
		clientSqlSvr.batchUpdate(insertSqls, hints);
	}

	@After
	public static void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		clientSqlSvr.update(sql, parameters, hints);
	}
	
	@Before
	public static void turnOnIdentityInsert() throws Exception {
		clientSqlSvr.batchUpdate(new String[]{"SET IDENTITY_INSERT "+ TABLE_NAME + " ON"}, new DalHints());
	}

	@After
	public static void turnOffIdentityInsert() throws Exception {
		clientSqlSvr.batchUpdate(new String[]{"SET IDENTITY_INSERT "+ TABLE_NAME + " OFF"}, new DalHints());
	}	
}
