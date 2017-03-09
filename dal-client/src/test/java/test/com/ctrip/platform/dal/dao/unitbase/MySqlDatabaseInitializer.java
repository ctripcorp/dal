package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;

import org.junit.Assert;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class MySqlDatabaseInitializer {
	public final static String DATABASE_NAME = "dao_test_mysql";
	public final static String TABLE_NAME = "dal_client_test";
	
	public final static boolean VALIDATE_BATCH_UPDATE_COUNT = true;
	public final static boolean VALIDATE_BATCH_INSERT_COUNT = false;//When connectionProperties="rewriteBatchedStatements=true"
	public final static boolean VALIDATE_RETURN_COUNT = true;
	public final static boolean SUPPORT_GET_GENERATED_KEYS = true;
	public final static boolean SUPPORT_INSERT_VALUES = true;
	
	private final static String SP_I_NAME = "dal_client_test_i";
	private final static String SP_D_NAME="dal_client_test_d";
	private final static String SP_U_NAME = "dal_client_test_u";
	private final static String MULTIPLE_RESULT_SP_SQL = "MULTIPLE_RESULT_SP_SQL";
	
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE dal_client_test("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	//Only has normal parameters
	private static final String CREATE_I_SP_SQL = "CREATE PROCEDURE dal_client_test_i("
			+ "dal_id int,"
			+ "quantity int,"
			+ "type smallint,"
			+ "address VARCHAR(64)) "
			+ "BEGIN INSERT INTO dal_client_test"
			+ "(id, quantity, type, address) "
			+ "VALUES(dal_id, quantity, type, address);"
			+ "SELECT ROW_COUNT() AS result;"
			+ "END";
	//Has out parameters store procedure
	private static final String CREATE_D_SP_SQL = "CREATE PROCEDURE dal_client_test_d("
			+ "dal_id int,"
			+ "out count int)"
			+ "BEGIN DELETE FROM dal_client_test WHERE id=dal_id;"
			+ "SELECT ROW_COUNT() AS result;"
			+ "SELECT COUNT(*) INTO count from dal_client_test;"
			+ "END";
	//Has in-out parameters store procedure
	private static final String CREATE_U_SP_SQL = "CREATE PROCEDURE dal_client_test_u("
			+ "dal_id int,"
			+ "quantity int,"
			+ "type smallint,"
			+ "INOUT address VARCHAR(64))"
			+ "BEGIN UPDATE dal_client_test "
			+ "SET quantity = quantity, type=type, address=address "
			+ "WHERE id=dal_id;"
			+ "SELECT ROW_COUNT() AS result;"
			+ "END";
	
	//auto get all result parameters store procedure
	private static final String CREATE_MULTIPLE_RESULT_SP_SQL = "CREATE PROCEDURE MULTIPLE_RESULT_SP_SQL("
			+ "dal_id int,"
			+ "quantity int,"
			+ "type smallint,"
			+ "INOUT address VARCHAR(64))"
			+ "BEGIN UPDATE dal_client_test "
			+ "SET quantity = quantity, type=type, address=address "
			+ "WHERE id=dal_id;"
			+ "SELECT ROW_COUNT() AS result;"
			+ "SELECT 1 AS result2;"
			+ "UPDATE dal_client_test "
			+ "SET `quantity` = quantity + 1, `type`=type + 1, `address`='aaa';"
			+ "SELECT 'abc' AS result3, 456 AS count2;"
			+ "SELECT * from dal_client_test;"
			+ "SELECT 'output' INTO address;"
			+ "END";
	
	private static final String DROP_I_SP_SQL = "DROP PROCEDURE  IF  EXISTS dal_client_test_i";
	private static final String DROP_D_SP_SQL = "DROP PROCEDURE  IF  EXISTS dal_client_test_d";
	private static final String DROP_U_SP_SQL = "DROP PROCEDURE  IF  EXISTS dal_client_test_u";
	private static final String DROP__MULTIPLE_RESULT_SP_SQL = "DROP PROCEDURE  IF  EXISTS MULTIPLE_RESULT_SP_SQL";
	
	private static DalClient client = null;
	private static ClientTestDalRowMapper mapper = null;

	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
			mapper = new ClientTestDalRowMapper();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL, 
				DROP_I_SP_SQL, CREATE_I_SP_SQL, 
				DROP_D_SP_SQL, CREATE_D_SP_SQL,
				DROP_U_SP_SQL, CREATE_U_SP_SQL,
				CREATE_MULTIPLE_RESULT_SP_SQL};
		client.batchUpdate(sqls, hints);
	}

	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL, DROP_I_SP_SQL,
				DROP_D_SP_SQL, DROP_U_SP_SQL, DROP__MULTIPLE_RESULT_SP_SQL};
		client.batchUpdate(sqls, hints);
	}

	public static void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(1, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(2, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(3, 12, 2, 'SZ INFO', NULL)" };
		int[] counts = client.batchUpdate(insertSqls, hints);
		Assert.assertArrayEquals(new int[] { 1, 1, 1 }, counts);
	}

	public static void setUp2() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(1, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(2, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(3, 12, 2, 'SZ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(4, 12, 1, 'HK INFO', NULL)"};
		client.batchUpdate(insertSqls, hints);
	}

	public static void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.update(sql, parameters, hints);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
