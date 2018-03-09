package test.com.ctrip.platform.dal.dao.shard;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalTabelDaoShardByTableOracleTest extends BaseDalTabelDaoShardByTableTest {
	public DalTabelDaoShardByTableOracleTest() {
		super(DATABASE_NAME, OracleDatabaseInitializer.diff);
	}
	private final static String DATABASE_NAME = "dao_test_oracle_tableShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 4;
	
	public final static String DROP_TABLE_SEQ_TPL = 
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from user_sequences where sequence_name = 'ID_SEQ%d';"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop SEQUENCE ID_SEQ%d' ;"
			+ "		end if;"
			+ "end;";
	
	public final static String DROP_TABLE_SQL_TPL = 
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from user_tables where table_name = upper('DAL_CLIENT_TEST_%d');"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop table DAL_CLIENT_TEST_%d' ;"
			+ "		end if;"
			+ "end;"; 
		
	public final static String DROP_TABLE_TRIG_TPL = 
			"declare num number;"
			+ "begin "
			+ "		num := 0;"
			+ "		select count(1) into num from user_triggers where trigger_name = 'DAL_CLIENT_TEST_ID_TRIG%d';"
			+ "		if num > 0 then "
			+ "			execute immediate 'drop TRIGGER DAL_CLIENT_TEST_ID_TRIG%d' ;"
			+ "		end if;"
			+ "end;";	

	public final static String CREATE_TABLE_SEQ_TPL = "CREATE SEQUENCE ID_SEQ%d  MINVALUE 1 MAXVALUE 9999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE";
	
	//Create the the table
	public final static String CREATE_TABLE_SQL_TPL = "CREATE TABLE DAL_CLIENT_TEST_%d"
			   			+ "(ID NUMBER(5) NOT NULL ENABLE, " 
						+ "QUANTITY NUMBER(5)," 
			            + "dbIndex NUMBER(10),"
						+ "tableIndex NUMBER(10),"
						+ "TYPE NUMBER(2),"
						+ "ADDRESS VARCHAR2(64 BYTE) NOT NULL ENABLE," 
						+ "LAST_CHANGED TIMESTAMP (6) DEFAULT SYSDATE, "
						+ "CONSTRAINT DAL_CLIENT_TEST_%d_PK PRIMARY KEY (ID))";
	
	public final static String CREATE_TABLE_TRIG_TPL = "CREATE OR REPLACE TRIGGER DAL_CLIENT_TEST_ID_TRIG%d" 
						+" before insert on DAL_CLIENT_TEST_%d" 
						+" for each row " 
						+" begin"  
						+"	if inserting then" 
						+"		if :NEW.ID is null then "
						+"			select ID_SEQ%d.nextval into :NEW.ID from dual;" 
						+"		end if; "
						+"	end if; "
						+" end;";
	
	private static DalClient clientMySql;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		clientMySql = DalClientFactory.getClient(DATABASE_NAME);
		DalHints hints = new DalHints();
		String[] sqls = null;
		for(int i = 0; i < mod; i++) {
			sqls = new String[] {
					String.format(DROP_TABLE_TRIG_TPL, i, i),
					String.format(DROP_TABLE_SEQ_TPL, i, i), 
					String.format(DROP_TABLE_SQL_TPL, i, i), 
					String.format(CREATE_TABLE_SEQ_TPL, i, i), 
					String.format(CREATE_TABLE_SQL_TPL, i, i), 
					String.format(CREATE_TABLE_TRIG_TPL, i, i, i),
					};
			clientMySql.batchUpdate(sqls, hints);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { 
					String.format(DROP_TABLE_SEQ_TPL, i, i),
					String.format(DROP_TABLE_TRIG_TPL, i, i),
					String.format(DROP_TABLE_SQL_TPL, i, i),};
			clientMySql.batchUpdate(sqls, hints);
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[i + 1 + 1 + 1];
			insertSqls[0] = String.format(DROP_TABLE_SEQ_TPL, i, i);
			insertSqls[1] = String.format(CREATE_TABLE_SEQ_TPL, i, i); 

			for(int j = 0; j < i + 1; j ++) {
				int id = j + 1;
				int quantity = 10 + j;
				insertSqls[j+2] = "INSERT INTO " + TABLE_NAME + "_"+ i  + "(quantity, dbIndex, tableIndex, type, address)"
						+ " VALUES(" + quantity + ", 1, " + i + ",1 , 'SH INFO')";
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