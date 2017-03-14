package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub.DatabaseDifference;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class SqlServerDatabaseInitializer {
	public final static String DATABASE_NAME = "dao_test_sqlsvr";
	public final static String TABLE_NAME = "dal_client_test";

	public static final DatabaseDifference diff = new DatabaseDifference();
	static {
		// This settings are based on SET NO COUNT, which sqlserver will not return affected rows
		diff.category = DatabaseCategory.SqlServer;
		diff.validateBatchUpdateCount = false;
		diff.validateBatchInsertCount = false;
		diff.validateReturnCount = false;
		diff.supportGetGeneratedKeys = false;
		diff.supportInsertValues = true;
		diff.supportSpIntermediateResult = true;
		diff.supportBatchSpWithOutParameter = false;
	}

	private final static String SP_I_NAME = "dal_client_test_i";
	private final static String SP_D_NAME="dal_client_test_d";
	private final static String SP_U_NAME = "dal_client_test_u";
	private final static String MULTIPLE_RESULT_SP_SQL = "MULTIPLE_RESULT_SP_SQL";
	
	private final static String DROP_TABLE_SQL = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME +"("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	//Only has normal parameters
	private static final String CREATE_SP_WITHOUT_OUT_PARAM = "CREATE PROCEDURE " + BaseTestStub.SP_WITHOUT_OUT_PARAM + "("
			+ "@v_id int,"
			+ "@v_quantity int,"
			+ "@v_type smallint,"
			+ "@v_address VARCHAR(64)) "
			+ "AS BEGIN INSERT INTO " + TABLE_NAME
			+ "([quantity], [type], [address]) "
			+ "VALUES(@v_quantity, @v_type, @v_address);"
			+ "END";
	//Has out parameters store procedure
	private static final String CREATE_SP_WITH_OUT_PARAM = "CREATE PROCEDURE " + BaseTestStub.SP_WITH_OUT_PARAM + "("
			+ "@v_id int,"
			+ "@count int OUTPUT)"
			+ "AS BEGIN DELETE FROM " + TABLE_NAME
			+ " WHERE [id]=@v_id;"
			+ "SELECT @count = COUNT(*) from " + TABLE_NAME + ";"
			+ "END";
	//Has in-out parameters store procedure
	private static final String CREATE_SP_WITH_IN_OUT_PARAM = "CREATE PROCEDURE " + BaseTestStub.SP_WITH_IN_OUT_PARAM + "("
			+ "@v_id int,"
			+ "@v_quantity int,"
			+ "@v_type smallint,"
			+ "@v_address VARCHAR(64) OUTPUT)"
			+ "AS BEGIN UPDATE " + TABLE_NAME +" "
			+ "SET [quantity] = @v_quantity, [type]=@v_type, [address]=@v_address "
			+ "WHERE [id]=@v_id;"
			+ "SELECT @v_address='output';"
			+ "END";

	//auto get all result parameters store procedure
	private static final String CREATE_SP_WITH_INTERMEDIATE_RESULT = "CREATE PROCEDURE " + BaseTestStub.SP_WITH_INTERMEDIATE_RESULT + "("
			+ "@v_id int,"
			+ "@v_quantity int,"
			+ "@v_type smallint,"
			+ "@v_address VARCHAR(64) OUTPUT)"
			+ "AS BEGIN UPDATE dal_client_test "
			+ "SET [quantity] = @v_quantity, [type]=@v_type, [address]=@v_address "
			+ "WHERE [id]=@v_id;"
			+ "SELECT @@ROWCOUNT AS result;"
			+ "SELECT 1 AS result2;"
			+ "UPDATE dal_client_test "
			+ "SET [quantity] = @v_quantity + 1, [type]=@v_type + 1, [address]='aaa';"
			+ "SELECT 'abc' AS result3, 456 AS count2;"
			+ "SELECT * from dal_client_test;"
			+ "SELECT @v_address='output';"
			+ "END";

	private static final String DROP_SP_WITHOUT_OUT_PARAM = "IF OBJECT_ID('dbo." + BaseTestStub.SP_WITHOUT_OUT_PARAM + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + BaseTestStub.SP_WITHOUT_OUT_PARAM;
	private static final String DROP_SP_WITH_OUT_PARAM = "IF OBJECT_ID('dbo." + BaseTestStub.SP_WITH_OUT_PARAM + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + BaseTestStub.SP_WITH_OUT_PARAM;
	private static final String DROP_SP_WITH_IN_OUT_PARAM = "IF OBJECT_ID('dbo." + BaseTestStub.SP_WITH_IN_OUT_PARAM + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + BaseTestStub.SP_WITH_IN_OUT_PARAM;
	private static final String DROP_SP_WITH_INTERMEDIATE_RESULT = "IF OBJECT_ID('dbo." + BaseTestStub.SP_WITH_INTERMEDIATE_RESULT + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + BaseTestStub.SP_WITH_INTERMEDIATE_RESULT;
	
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
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL, 
				DROP_SP_WITHOUT_OUT_PARAM, CREATE_SP_WITHOUT_OUT_PARAM,
				DROP_SP_WITH_OUT_PARAM, CREATE_SP_WITH_OUT_PARAM,
				DROP_SP_WITH_IN_OUT_PARAM, CREATE_SP_WITH_IN_OUT_PARAM,
				DROP_SP_WITH_INTERMEDIATE_RESULT, CREATE_SP_WITH_INTERMEDIATE_RESULT};
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}

	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, DROP_SP_WITHOUT_OUT_PARAM, DROP_SP_WITH_OUT_PARAM, DROP_SP_WITH_IN_OUT_PARAM, DROP_SP_WITH_INTERMEDIATE_RESULT};
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}
	
	public static void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"SET IDENTITY_INSERT "+ TABLE_NAME +" ON",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(1, 10, 1, 'SH INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(2, 11, 1, 'BJ INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(3, 12, 2, 'SZ INFO')",
				"SET IDENTITY_INSERT "+ TABLE_NAME +" OFF"};
		client.batchUpdate(insertSqls, hints);
	}

	public void setUp2() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"SET IDENTITY_INSERT "+ TABLE_NAME +" ON",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(1, 10, 1, 'SH INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(2, 11, 1, 'BJ INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(3, 12, 2, 'SZ INFO')",
				"INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
						+ " VALUES(4, 12, 1, 'HK INFO')",
				"SET IDENTITY_INSERT "+ TABLE_NAME +" OFF"};
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
