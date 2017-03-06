package test.com.ctrip.platform.dal.dao.task;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class MySqlTestInitializer {
	public final static String DATABASE_NAME_MYSQL = "dao_test_mysql";
	
	private final static String TABLE_NAME = "dal_client_test";
	
	private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "dbIndex int,"
			+ "tableIndex int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null,"
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	private static DalClient clientMySql;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
		DalHints hints = new DalHints();
		String[] sqls = new String[] {DROP_TABLE_SQL_MYSQL_TPL, CREATE_TABLE_SQL_MYSQL_TPL};
		clientMySql.batchUpdate(sqls, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] {DROP_TABLE_SQL_MYSQL_TPL};
		clientMySql.batchUpdate(sqls, hints);
	}

	@Before
	public static void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		insertSqls = new String[3];
		for(int i = 0; i < 3; i ++) {
			int id = i + 1;
			int quantity = 10 + i;
			insertSqls[i] = "INSERT INTO " + TABLE_NAME + "(Id, quantity,dbIndex,tableIndex,type,address)"
						+ " VALUES(" + id + ", " + quantity + ", 0," + i + ",1, 'SH INFO')";
		}
				
		clientMySql.batchUpdate(insertSqls, hints);
	}

	@After
	public static void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		clientMySql.update(sql, parameters, hints);
	}
}
