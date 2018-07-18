package test.com.ctrip.platform.dal.dao.shard;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.MySqlDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTableDaoShardByDbMySqlTest extends BaseDalTableDaoShardByDbTest {
	public DalTableDaoShardByDbMySqlTest() {
		super(DATABASE_NAME_MYSQL, GENERATED_KEY, MySqlDatabaseInitializer.diff);
	}

	private final static String DATABASE_NAME_MYSQL = "dao_test_mysql_dbShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	private final static String GENERATED_KEY = "GENERATED_KEY";
	
	private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	// Note that id is UNSIGNED int, which maps to Long in java when using rs.getObject()
	private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
            + "dbIndex int,"
			+ "tableIndex int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	private static DalClient clientMySql;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		clientMySql = DalClientFactory.getClient(DATABASE_NAME_MYSQL);
		DalHints hints = new DalHints();
		String[] sqls = null;
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { DROP_TABLE_SQL_MYSQL_TPL, CREATE_TABLE_SQL_MYSQL_TPL};
			clientMySql.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { DROP_TABLE_SQL_MYSQL_TPL};
			clientMySql.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[] {
					"INSERT INTO " + TABLE_NAME
							+ " VALUES(1, 10, 1, " + i + " , 1, 'SH INFO', NULL)",
					"INSERT INTO " + TABLE_NAME
							+ " VALUES(2, 11, 1, " + i + " , 1, 'BJ INFO', NULL)",
					"INSERT INTO " + TABLE_NAME
							+ " VALUES(3, 12, 1, " + i + " , 1, 'SZ INFO', NULL)" };
			int[] counts = clientMySql.batchUpdate(insertSqls, hints.inShard(i));
			assertArrayEquals(new int[] { 1, 1, 1 }, counts);
		}
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			for(int i = 0; i < mod; i++) {
				clientMySql.update(sql, parameters, hints.inShard(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
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
