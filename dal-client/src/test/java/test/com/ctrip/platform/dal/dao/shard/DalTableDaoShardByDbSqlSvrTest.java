package test.com.ctrip.platform.dal.dao.shard;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

/**
 * This test only test against shard by DB case for sql server
 * @author jhhe
 *
 */
public class DalTableDaoShardByDbSqlSvrTest extends BaseDalTableDaoShardByDbTest {
	public DalTableDaoShardByDbSqlSvrTest() {
		super(DATABASE_NAME_SQLSVR, null, SqlServerDatabaseInitializer.diff);
	}
	
	private final static String DATABASE_NAME_SQLSVR = "dao_test_sqlsvr_dbShard";
	private final static String DATABASE_NAME_MOD = DATABASE_NAME_SQLSVR;
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	
	//Create the the table
	private final static String DROP_TABLE_SQL_SQLSVR_TPL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQLSVR_TPL = "CREATE TABLE " + TABLE_NAME +"("
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
		StatementParameters parameters = new StatementParameters();
		for(int i = 0; i < mod; i++) {
			sqls = new String[] {DROP_TABLE_SQL_SQLSVR_TPL, CREATE_TABLE_SQL_SQLSVR_TPL};
			for (int j = 0; j < sqls.length; j++) {
				clientSqlSvr.update(sqls[j], parameters, hints.inShard(i));
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
			clientSqlSvr.update(DROP_TABLE_SQL_SQLSVR_TPL, parameters, hints.inShard(i));
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		//For Sql Server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[2 + 3];
			insertSqls[0] = "SET IDENTITY_INSERT "+ TABLE_NAME + " ON";
			for(int j = 0; j < 3; j ++) {
				int id = j + 1;
				int quantity = 10 + j;
				insertSqls[j + 1] = "INSERT INTO " + TABLE_NAME + "(Id, quantity,tableIndex,type,address)"
							+ " VALUES(" + id + ", " + quantity + ", " + i + ",1, 'SH INFO')";
			}
					
			insertSqls[4] = "SET IDENTITY_INSERT "+ TABLE_NAME +" OFF";
			clientSqlSvr.batchUpdate(insertSqls, hints.inShard(i));
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
		for(int i = 0; i < mod; i++) {
			clientSqlSvr.update(sql, parameters, hints.inShard(i));
		}
	}

	@Override
	public void insertBack() {
		try {
			setUp();
		} catch (Exception e) {
			fail();
		}
	}
}
