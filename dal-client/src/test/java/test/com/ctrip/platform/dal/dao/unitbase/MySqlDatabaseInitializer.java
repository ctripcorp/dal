package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;

import org.junit.Assert;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub.DatabaseDifference;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class MySqlDatabaseInitializer {
	public final static String DATABASE_NAME = "dao_test_mysql";
	public final static String TABLE_NAME = "dal_client_test";
	
	public static final DatabaseDifference diff = new DatabaseDifference();
	static {
		diff.category = DatabaseCategory.MySql;
		diff.validateBatchUpdateCount = true;
		diff.validateBatchInsertCount = false;//When connectionProperties="rewriteBatchedStatements=true"
		diff.validateReturnCount = true;
		diff.supportGetGeneratedKeys = true;
		diff.supportInsertValues = true;
		diff.supportSpIntermediateResult = true;
		diff.supportBatchSpWithOutParameter = true;
	}
	
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE dal_client_test("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	//Only has normal parameters
	//When rewriteBatchedStatements=true, batch sp call will not allowed if there is intermediated result
	private static final String CREATE_SP_WITHOUT_OUT_PARAM = "CREATE PROCEDURE " + BaseTestStub.SP_WITHOUT_OUT_PARAM + "("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "v_address VARCHAR(64)) "
			+ "BEGIN INSERT INTO dal_client_test"
			+ "(id, quantity, type, address) "
			+ "VALUES(v_id, v_quantity, v_type, v_address);"
			+ "END";
	
	//Has out parameters store procedure
	private static final String CREATE_SP_WITH_OUT_PARAM = "CREATE PROCEDURE " + BaseTestStub.SP_WITH_OUT_PARAM + "("
			+ "v_id int,"
			+ "out count int)"
			+ "BEGIN DELETE FROM dal_client_test WHERE id=v_id;"
			+ "SELECT COUNT(*) INTO count from dal_client_test;"
			+ "END";
	
	//Has in-out parameters store procedure
	private static final String CREATE_SP_WITH_IN_OUT_PARAM = "CREATE PROCEDURE " + BaseTestStub.SP_WITH_IN_OUT_PARAM + "("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "INOUT v_address VARCHAR(64))"
			+ "BEGIN UPDATE dal_client_test "
			+ "SET quantity = v_quantity, type=v_type, address=v_address "
			+ "WHERE id=v_id;"
			+ "SELECT 'output' INTO v_address;"
			+ "END";
	
	//auto get all result parameters store procedure
	private static final String CREATE_SP_WITH_INTERMEDIATE_RESULT = "CREATE PROCEDURE " + BaseTestStub.SP_WITH_INTERMEDIATE_RESULT + "("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "INOUT v_address VARCHAR(64))"
			+ "BEGIN UPDATE dal_client_test "
			+ "SET quantity = v_quantity, type=v_type, address=v_address "
			+ "WHERE id=v_id;"
			+ "SELECT ROW_COUNT() AS result;"
			+ "SELECT 1 AS result2;"
			+ "UPDATE dal_client_test "
			+ "SET `quantity` = quantity + 1, `type`=type + 1, `address`='aaa';"
			+ "SELECT 'abc' AS result3, 456 AS count2;"
			+ "SELECT * from dal_client_test;"
			+ "SELECT 'output' INTO v_address;"
			+ "END";
	
	private static final String DROP_SP_WITHOUT_OUT_PARAM = "DROP PROCEDURE IF EXISTS " + BaseTestStub.SP_WITHOUT_OUT_PARAM;
	private static final String DROP_SP_WITH_OUT_PARAM = "DROP PROCEDURE IF EXISTS " + BaseTestStub.SP_WITH_OUT_PARAM;
	private static final String DROP_SP_WITH_IN_OUT_PARAM = "DROP PROCEDURE IF EXISTS " + BaseTestStub.SP_WITH_IN_OUT_PARAM;
	private static final String DROP_SP_WITH_INTERMEDIATE_RESULT = "DROP PROCEDURE IF EXISTS " + BaseTestStub.SP_WITH_INTERMEDIATE_RESULT;

	private static DalClient client = null;

	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { 
				DROP_TABLE_SQL, CREATE_TABLE_SQL, 
				DROP_SP_WITHOUT_OUT_PARAM, CREATE_SP_WITHOUT_OUT_PARAM, 
				DROP_SP_WITH_OUT_PARAM, CREATE_SP_WITH_OUT_PARAM,
				DROP_SP_WITH_IN_OUT_PARAM, CREATE_SP_WITH_IN_OUT_PARAM,
				DROP_SP_WITH_INTERMEDIATE_RESULT, CREATE_SP_WITH_INTERMEDIATE_RESULT};
		client.batchUpdate(sqls, hints);
	}

	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { 
				DROP_TABLE_SQL, 
				DROP_SP_WITHOUT_OUT_PARAM,
				DROP_SP_WITH_OUT_PARAM, 
				DROP_SP_WITH_IN_OUT_PARAM, 
				DROP_SP_WITH_INTERMEDIATE_RESULT};
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
