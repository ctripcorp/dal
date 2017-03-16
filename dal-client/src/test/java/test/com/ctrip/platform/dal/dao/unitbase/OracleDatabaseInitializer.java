package test.com.ctrip.platform.dal.dao.unitbase;

import java.sql.SQLException;

import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub.DatabaseDifference;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class OracleDatabaseInitializer {
	public final static String DATABASE_NAME = "dao_test_oracle";
	public final static String TABLE_NAME = "dal_client_test";
	
	public static final DatabaseDifference diff = new DatabaseDifference();
	static {
		diff.category = DatabaseCategory.Oracle;
		diff.validateBatchUpdateCount = false;
		diff.validateBatchInsertCount = false;
		diff.validateReturnCount = true;
		diff.supportGetGeneratedKeys = false;
		diff.supportInsertValues = false;
		diff.supportSpIntermediateResult = false;
		diff.supportBatchSpWithOutParameter = false;
	}
	
	public final static String DROP_TABLE_SEQ = 
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from user_sequences where sequence_name = 'ID_SEQ';"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop SEQUENCE ID_SEQ' ;"
			+ "		end if;"
			+ "end;";
	
	public final static String DROP_TABLE_SQL = String.format(
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from user_tables where table_name = upper('%s');"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop table %s' ;"
			+ "		end if;"
			+ "end;", TABLE_NAME, TABLE_NAME); 
	
	public final static String CREATE_TABLE_SEQ = "CREATE SEQUENCE ID_SEQ  MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE";
	
	//Create the the table
	public final static String CREATE_TABLE_SQL = "CREATE TABLE DAL_CLIENT_TEST"
			   			+ "(ID NUMBER(5) NOT NULL ENABLE, " 
						+ "QUANTITY NUMBER(5)," 
						+ "TYPE NUMBER(2),"
						+ "ADDRESS VARCHAR2(64 BYTE) NOT NULL ENABLE," 
						+ "LAST_CHANGED TIMESTAMP (6) DEFAULT SYSDATE, "
						+ "CONSTRAINT DAL_CLIENT_TEST_PK PRIMARY KEY (ID))";
	
	//Create the the table
	public final static String CREATE_TABLE_SQL2 = "CREATE TABLE DAL_CLIENT_TEST"
			   			+ "(ID NUMBER(5) NOT NULL ENABLE, " 
						+ "QUANTITY NUMBER(5)," 
						+ "tableIndex NUMBER(10),"
						+ "TYPE NUMBER(2),"
						+ "ADDRESS VARCHAR2(64 BYTE) NOT NULL ENABLE," 
						+ "LAST_CHANGED TIMESTAMP (6) DEFAULT SYSDATE, "
						+ "CONSTRAINT DAL_CLIENT_TEST_PK PRIMARY KEY (ID))";
	
	public final static String CREATE_TABLE_TRIG = "CREATE OR REPLACE TRIGGER DAL_CLIENT_TEST_ID_TRIG" 
						+" before insert on DAL_CLIENT_TEST" 
						+" for each row " 
						+" begin"  
						+"	if inserting then" 
						+"		if :NEW.ID is null then "
						+"			select ID_SEQ.nextval into :NEW.ID from dual;" 
						+"		end if; "
						+"	end if; "
						+" end;";
	
	public final static String DROP_TABLE_TRIG = 
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from user_triggers where trigger_name = 'DAL_CLIENT_TEST_ID_TRIG';"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop TRIGGER DAL_CLIENT_TEST_ID_TRIG' ;"
			+ "		end if;"
			+ "end;";
	
	//Only has normal parameters
	// Oracle does not support out parameter in batch call
	private static final String CREATE_SP_WITHOUT_OUT_PARAM = "CREATE OR REPLACE PROCEDURE " + BaseTestStub.SP_WITHOUT_OUT_PARAM + "("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "v_address VARCHAR) AS "
			+ "BEGIN INSERT INTO dal_client_test"
			+ "(id, quantity, type, address) "
			+ "VALUES(v_id, v_quantity, v_type, v_address);"
			+ "END;";
	//Has out parameters store procedure
	private static final String CREATE_SP_WITH_OUT_PARAM = "CREATE OR REPLACE PROCEDURE " + BaseTestStub.SP_WITH_OUT_PARAM + "("
			+ "v_id int,"
			+ "count OUT int) AS "
			+ "BEGIN DELETE FROM dal_client_test WHERE id=v_id;"
			+ "SELECT COUNT(*) INTO count from dal_client_test;"
			+ "END;";
	//Has in-out parameters store procedure
	private static final String CREATE_SP_WITH_IN_OUT_PARAM = "CREATE OR REPLACE PROCEDURE " + BaseTestStub.SP_WITH_IN_OUT_PARAM + "("
			+ "v_id int,"
			+ "v_quantity int,"
			+ "v_type smallint,"
			+ "v_address IN OUT VARCHAR) AS "
			+ "BEGIN UPDATE dal_client_test "
			+ "SET quantity = v_quantity, type=v_type, address=v_address "
			+ "WHERE id=v_id;"
			+ "SELECT 'output' INTO v_address FROM DUAL;"
			+ "END;";
	
	//auto get all result parameters store procedure
	private static final String CREATE_SP_WITH_INTERMEDIATE_RESULT = "CREATE OR REPLACE PROCEDURE " + BaseTestStub.SP_WITH_INTERMEDIATE_RESULT + "("
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
	
	private static final String DROP_SP_TPL = 
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from USER_SOURCE where TYPE = 'PROCEDURE' and name = '%s';"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop PROCEDURE %s' ;"
			+ "		end if;"
			+ "end;";

	private static final String DROP_SP_WITHOUT_OUT_PARAM = String.format(DROP_SP_TPL, BaseTestStub.SP_WITHOUT_OUT_PARAM, BaseTestStub.SP_WITHOUT_OUT_PARAM);
	private static final String DROP_SP_WITH_OUT_PARAM = String.format(DROP_SP_TPL, BaseTestStub.SP_WITH_OUT_PARAM, BaseTestStub.SP_WITH_OUT_PARAM);
	private static final String DROP_SP_WITH_IN_OUT_PARAM = String.format(DROP_SP_TPL, BaseTestStub.SP_WITH_IN_OUT_PARAM, BaseTestStub.SP_WITH_IN_OUT_PARAM);
	private static final String DROP_SP_WITH_INTERMEDIATE_RESULT = String.format(DROP_SP_TPL, BaseTestStub.SP_WITH_INTERMEDIATE_RESULT, BaseTestStub.SP_WITH_INTERMEDIATE_RESULT);;
	
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
				CREATE_SP_WITHOUT_OUT_PARAM, 
				CREATE_SP_WITH_OUT_PARAM,
				CREATE_SP_WITH_IN_OUT_PARAM,
				CREATE_SP_WITH_INTERMEDIATE_RESULT,
				};
		try {
			client.batchUpdate(sqls, hints);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SEQ, DROP_TABLE_TRIG, DROP_TABLE_SQL, DROP_SP_WITHOUT_OUT_PARAM,
				DROP_SP_WITH_OUT_PARAM, DROP_SP_WITH_IN_OUT_PARAM, DROP_SP_WITH_INTERMEDIATE_RESULT};
		client.batchUpdate(sqls, hints);
	}

	public static void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				DROP_TABLE_SEQ,CREATE_TABLE_SEQ,
				"INSERT INTO " + TABLE_NAME
						+ " (quantity,type,address) VALUES(10, 1, 'SH INFO')",
				"INSERT INTO " + TABLE_NAME
						+ " (quantity,type,address) VALUES(11, 1, 'BJ INFO')",
				"INSERT INTO " + TABLE_NAME
						+ " (quantity,type,address) VALUES(12, 2, 'SZ INFO')",};
		client.batchUpdate(insertSqls, hints);
	}

	public static void setUp2() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				DROP_TABLE_SEQ,CREATE_TABLE_SEQ,
				"INSERT INTO " + TABLE_NAME
						+ " (quantity,type,address) VALUES(10, 1, 'SH INFO')",
				"INSERT INTO " + TABLE_NAME
						+ " (quantity,type,address) VALUES(11, 1, 'BJ INFO')",
				"INSERT INTO " + TABLE_NAME
						+ " (quantity,type,address) VALUES(12, 2, 'SZ INFO')",
				"INSERT INTO " + TABLE_NAME
						+ " (quantity,type,address) VALUES(12, 1, 'HK INFO')"};
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
