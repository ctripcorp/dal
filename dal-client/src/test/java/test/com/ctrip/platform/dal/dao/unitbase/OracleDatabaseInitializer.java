package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;

import org.junit.Assert;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub.DatabaseDifference;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class OracleDatabaseInitializer {
	public final static String DATABASE_NAME = "dao_test_oracle";
	public final static String TABLE_NAME = "dal_client_test";
	
	public static final DatabaseDifference diff = new DatabaseDifference();
	static {
		diff.validateBatchUpdateCount = false;
		diff.validateBatchInsertCount = false;
		diff.validateReturnCount = true;
		diff.supportGetGeneratedKeys = false;
		diff.supportInsertValues = false;
		diff.supportSpIntermediateResult = false;
		diff.supportBatchSpWithOutParameter = false;
	}
	
	private final static String SP_I_NAME = "dal_client_test_i";
	private final static String SP_D_NAME="dal_client_test_d";
	private final static String SP_U_NAME = "dal_client_test_u";
	private final static String MULTIPLE_RESULT_SP_SQL = "MULTIPLE_RESULT_SP_SQL";
	private final static String MULTIPLE_SP_SQL = "MULTIPLE_SP_SQL";
	public final static String SP_NO_OUT_NAME = "dal_client_test_no_out";
	
	private final static String DROP_TABLE_SEQ = 
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from user_sequences where sequence_name = 'ID_SEQ';"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop SEQUENCE ID_SEQ' ;"
			+ "		end if;"
			+ "end;";
	
	private final static String DROP_TABLE_SQL = String.format(
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from user_tables where table_name = upper('%s');"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop table %s' ;"
			+ "		end if;"
			+ "end;", TABLE_NAME, TABLE_NAME); 
	
	private final static String CREATE_TABLE_SEQ = "CREATE SEQUENCE ID_SEQ  MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 4 CACHE 20 NOORDER  NOCYCLE";
	private final static String CREATE_TABLE_SEQ2 = "CREATE SEQUENCE ID_SEQ  MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 5 CACHE 20 NOORDER  NOCYCLE";
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE DAL_CLIENT_TEST"
			   			+ "(ID NUMBER(*,0) NOT NULL ENABLE, " 
						+ "QUANTITY NUMBER(*,0)," 
						+ "TYPE NUMBER(*,0),"
						+ "ADDRESS VARCHAR2(64 BYTE) NOT NULL ENABLE," 
						+ "LAST_CHANGED TIMESTAMP (6) DEFAULT SYSDATE, "
						+ "CONSTRAINT DAL_CLIENT_TEST_PK PRIMARY KEY (ID))";
	
	private final static String CREATE_TABLE_TRIG = "CREATE OR REPLACE TRIGGER DAL_CLIENT_TEST_ID_TRIG" 
						+" before insert on DAL_CLIENT_TEST" 
						+" for each row " 
						+" begin"  
						+"	if inserting then" 
						+"		if :NEW.ID is null then "
						+"			select ID_SEQ.nextval into :NEW.ID from dual;" 
						+"		end if; "
						+"	end if; "
						+" end;";
	
	private final static String DROP_TABLE_TRIG = 
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from user_triggers where trigger_name = 'DAL_CLIENT_TEST_ID_TRIG';"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop TRIGGER DAL_CLIENT_TEST_ID_TRIG' ;"
			+ "		end if;"
			+ "end;";
	
	//Only has normal parameters
	private static final String CREATE_I_SP_SQL = "CREATE OR REPLACE PROCEDURE dal_client_test_i("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "v_address VARCHAR) AS "
			+ "BEGIN INSERT INTO dal_client_test"
			+ "(id, quantity, type, address) "
			+ "VALUES(v_id, v_quantity, v_type, v_address);"
			//+ "SELECT sql%rowcount AS result;"
			+ "END;";
	// Oracle does not support out parameter in batch call
	private static final String CREATE_SP_NO_OUT_SQL = "CREATE OR REPLACE PROCEDURE dal_client_test_no_out("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "v_address VARCHAR) AS "
			+ "BEGIN INSERT INTO dal_client_test"
			+ "(id, quantity, type, address) "
			+ "VALUES(v_id, v_quantity, v_type, v_address);"
			//+ "SELECT sql%rowcount AS result;"
			+ "END;";
	//Has out parameters store procedure
	private static final String CREATE_D_SP_SQL = "CREATE OR REPLACE PROCEDURE dal_client_test_d("
			+ "v_id int,"
			+ "count out int) AS "
			+ "BEGIN DELETE FROM dal_client_test WHERE id=v_id;"
//			+ "SELECT sql%rowcount AS result;"
			+ "SELECT COUNT(*) INTO count from dal_client_test;"
			+ "END;";
	//Has in-out parameters store procedure
	private static final String CREATE_U_SP_SQL = "CREATE OR REPLACE PROCEDURE dal_client_test_u("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "v_address IN OUT VARCHAR) AS "
			+ "BEGIN UPDATE dal_client_test "
			+ "SET quantity = v_quantity, type=v_type, address=v_address "
			+ "WHERE id=v_id;"
//			+ "SELECT sql%rowcount AS result;"
			+ "END;";
	
	//auto get all result parameters store procedure
	private static final String CREATE_MULTIPLE_RESULT_SP_SQL = "CREATE OR REPLACE PROCEDURE MULTIPLE_RESULT_SP_SQL("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "v_address IN OUT VARCHAR) AS " 
			+ "BEGIN " 
			+ "UPDATE dal_client_test " 
			+ "SET quantity = v_quantity, type=v_type, address=v_address " 
			+ "WHERE id=v_id;"
			+ "SELECT 'output' AS result2 INTO v_address FROM DUAL;"
			+ "UPDATE dal_client_test " 
			+ "SET quantity = quantity + 1, type=type + 1, address='aaa';"
			+ "END;";
	
	private static final String CREATE_MULTIPLE_SP_SQL = "CREATE OR REPLACE PROCEDURE MULTIPLE_SP_SQL("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "v_address IN VARCHAR) AS " 
			+ "BEGIN " 
			+ "UPDATE dal_client_test " 
			+ "SET quantity = v_quantity, type=v_type, address=v_address " 
			+ "WHERE id=v_id;"
			+ "UPDATE dal_client_test " 
			+ "SET quantity = quantity + 1, type=type + 1, address='aaa';"
			+ "END;";
	
	private static final String DROP_SP_TPL = 
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from USER_SOURCE where TYPE = 'PROCEDURE' and name = '%s';"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop PROCEDURE %s' ;"
			+ "		end if;"
			+ "end;";

	private static final String DROP_I_SP_SQL = String.format(DROP_SP_TPL, SP_I_NAME, SP_I_NAME);
	private static final String DROP_D_SP_SQL = String.format(DROP_SP_TPL, SP_D_NAME, SP_D_NAME);
	private static final String DROP_U_SP_SQL = String.format(DROP_SP_TPL, SP_U_NAME, SP_U_NAME);
	private static final String DROP_SP_NO_OUT_SQL = String.format(DROP_SP_TPL, SP_NO_OUT_NAME, SP_NO_OUT_NAME);
	private static final String DROP_MULTIPLE_RESULT_SP_SQL = String.format(DROP_SP_TPL, MULTIPLE_RESULT_SP_SQL, MULTIPLE_RESULT_SP_SQL);;
	private static final String DROP_MULTIPLE_SP_SQL = String.format(DROP_SP_TPL, MULTIPLE_SP_SQL, MULTIPLE_SP_SQL);;
	
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
				DROP_TABLE_SEQ, DROP_TABLE_SQL, CREATE_TABLE_SEQ, CREATE_TABLE_SQL, CREATE_TABLE_TRIG,
				CREATE_I_SP_SQL, 
				CREATE_D_SP_SQL,
				CREATE_U_SP_SQL,
				CREATE_MULTIPLE_RESULT_SP_SQL,
				CREATE_MULTIPLE_SP_SQL,
				CREATE_SP_NO_OUT_SQL
				};
		try {
			client.batchUpdate(sqls, hints);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void setUpBeforeClass2() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { 
				DROP_TABLE_SEQ, DROP_TABLE_SQL, CREATE_TABLE_SEQ2, CREATE_TABLE_SQL, CREATE_TABLE_TRIG,
				CREATE_I_SP_SQL, 
				CREATE_D_SP_SQL,
				CREATE_U_SP_SQL,
				CREATE_MULTIPLE_RESULT_SP_SQL,
				CREATE_MULTIPLE_SP_SQL
				};
		try {
			client.batchUpdate(sqls, hints);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SEQ, DROP_TABLE_TRIG, DROP_TABLE_SQL, DROP_I_SP_SQL,
				DROP_D_SP_SQL, DROP_U_SP_SQL, DROP_MULTIPLE_RESULT_SP_SQL, DROP_MULTIPLE_SP_SQL, DROP_SP_NO_OUT_SQL};
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
