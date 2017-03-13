package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub.DatabaseDifference;

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
		diff.validateBatchUpdateCount = false;
		diff.validateBatchInsertCount = false;
		diff.validateReturnCount = false;
		diff.supportGetGeneratedKeys = false;
		diff.supportInsertValues = true;
		diff.supportSpIntermediateResult = false;
		diff.supportBatchSpWithOutParameter = true;
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
	private static final String CREATE_I_SP_SQL = "CREATE PROCEDURE " + SP_I_NAME + "("
			+ "@v_id int,"
			+ "@v_quantity int,"
			+ "@v_type smallint,"
			+ "@v_address VARCHAR(64)) "
			+ "AS BEGIN INSERT INTO " + TABLE_NAME
			+ "([quantity], [type], [address]) "
			+ "VALUES(@v_quantity, @v_type, @v_address);"
			+ "RETURN @@ROWCOUNT;"
			+ "END";
	private static final String CREATE_SP_NO_OUT_SQL = "CREATE PROCEDURE " + BaseTestStub.SP_NO_OUT_NAME + "("
			+ "@v_id int,"
			+ "@v_quantity int,"
			+ "@v_type smallint,"
			+ "@v_address VARCHAR(64)) "
			+ "AS BEGIN INSERT INTO " + TABLE_NAME
			+ "([quantity], [type], [address]) "
			+ "VALUES(@v_quantity, @v_type, @v_address);"
			+ "RETURN @@ROWCOUNT;"
			+ "END";
	//Has out parameters store procedure
	private static final String CREATE_D_SP_SQL = "CREATE PROCEDURE " + SP_D_NAME + "("
			+ "@v_id int,"
			+ "@count int OUTPUT)"
			+ "AS BEGIN DELETE FROM " + TABLE_NAME
			+ " WHERE [id]=@v_id;"
			+ "SELECT @count = COUNT(*) from " + TABLE_NAME + ";"
			+ "RETURN @@ROWCOUNT;"
			+ "END";
	//Has in-out parameters store procedure
	private static final String CREATE_U_SP_SQL = "CREATE PROCEDURE " + SP_U_NAME + "("
			+ "@v_id int,"
			+ "@v_quantity int,"
			+ "@v_type smallint,"
			+ "@v_address VARCHAR(64) OUTPUT)"
			+ "AS BEGIN UPDATE " + TABLE_NAME +" "
			+ "SET [quantity] = @v_quantity, [type]=@v_type, [address]=@v_address "
			+ "WHERE [id]=@v_id;"
			+ "RETURN @@ROWCOUNT;"
			+ "END";

	//auto get all result parameters store procedure
	private static final String CREATE_MULTIPLE_RESULT_SP_SQL = "CREATE PROCEDURE MULTIPLE_RESULT_SP_SQL("
			+ "@dal_id int,"
			+ "@quantity int,"
			+ "@type smallint,"
			+ "@address VARCHAR(64) OUTPUT)"
			+ "AS BEGIN UPDATE dal_client_test "
			+ "SET [quantity] = @quantity, [type]=@type, [address]=@address "
			+ "WHERE [id]=@dal_id;"
			+ "SELECT @@ROWCOUNT AS result;"
			+ "SELECT 1 AS result2;"
			+ "UPDATE dal_client_test "
			+ "SET [quantity] = @quantity + 1, [type]=@type + 1, [address]='aaa';"
			+ "SELECT 'abc' AS result3, 456 AS count2;"
			+ "SELECT * from dal_client_test;"
			+ "SELECT @address='output';"
			+ "RETURN @@ROWCOUNT;"
			+ "END";

	private static final String DROP_I_SP_SQL = "IF OBJECT_ID('dbo." + SP_I_NAME + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + SP_I_NAME;
	private static final String DROP_D_SP_SQL = "IF OBJECT_ID('dbo." + SP_D_NAME + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + SP_D_NAME;
	private static final String DROP_U_SP_SQL = "IF OBJECT_ID('dbo." + SP_U_NAME + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + SP_U_NAME;
	private static final String DROP_MULTIPLE_RESULT_SP_SQL = "IF OBJECT_ID('dbo." + MULTIPLE_RESULT_SP_SQL + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + MULTIPLE_RESULT_SP_SQL;
	private static final String DROP_SP_NO_OUT_SQL = "IF OBJECT_ID('dbo." + BaseTestStub.SP_NO_OUT_NAME + "') IS NOT NULL "
			+ "DROP PROCEDURE dbo." + BaseTestStub.SP_NO_OUT_NAME;
	
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
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL, 
				DROP_I_SP_SQL, CREATE_I_SP_SQL,
				DROP_D_SP_SQL, CREATE_D_SP_SQL,
				DROP_U_SP_SQL, CREATE_U_SP_SQL,
				DROP_SP_NO_OUT_SQL, CREATE_SP_NO_OUT_SQL,
				DROP_MULTIPLE_RESULT_SP_SQL, CREATE_MULTIPLE_RESULT_SP_SQL};
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}

	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, DROP_I_SP_SQL, DROP_D_SP_SQL, DROP_U_SP_SQL, DROP_SP_NO_OUT_SQL, DROP_MULTIPLE_RESULT_SP_SQL};
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
