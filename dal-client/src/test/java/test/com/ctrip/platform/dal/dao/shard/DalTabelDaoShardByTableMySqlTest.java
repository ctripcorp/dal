package test.com.ctrip.platform.dal.dao.shard;

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

/** 
 * Only test for my sql table based shard
 * @author jhhe
 *
 */
public class DalTabelDaoShardByTableMySqlTest extends BaseDalTabelDaoShardByTableTest {
	public DalTabelDaoShardByTableMySqlTest() {
		super(DATABASE_NAME_MYSQL, MySqlDatabaseInitializer.diff);
	}
	private final static String DATABASE_NAME_MYSQL = "dao_test_mysql_tableShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 4;
	
	private final static String DROP_TABLE_SQL_MYSQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	private final static String DROP_TABLE_SQL_MYSQL_TPL = "DROP TABLE IF EXISTS " + TABLE_NAME + "_%d";
	
	//Create the the table
	// Note that id is UNSIGNED int, which maps to Long in java when using rs.getObject()
	private final static String CREATE_TABLE_SQL_MYSQL_TPL = "CREATE TABLE " + TABLE_NAME +"_%d("
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
			sqls = new String[] { 
					DROP_TABLE_SQL_MYSQL,
					String.format(DROP_TABLE_SQL_MYSQL_TPL,i), 
					String.format(CREATE_TABLE_SQL_MYSQL_TPL, i)};
			clientMySql.batchUpdate(sqls, hints);
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
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[i + 1];
			for(int j = 0; j < i + 1; j ++) {
				int id = j + 1;
				int quantity = 10 + j;
				insertSqls[j] = "INSERT INTO " + TABLE_NAME + "_"+ i
						+ " VALUES(" + id + ", " + quantity + ", 1, " + i + ", 1, 'SH INFO', NULL)";
			}
			clientMySql.batchUpdate(insertSqls, hints);
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
	}
}