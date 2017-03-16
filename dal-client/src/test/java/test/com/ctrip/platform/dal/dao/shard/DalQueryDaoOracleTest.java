package test.com.ctrip.platform.dal.dao.shard;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalQueryDaoOracleTest extends DalQueryDaoTest {
	
	public DalQueryDaoOracleTest() {
		super(DATABASE_NAME, DatabaseCategory.Oracle);
	}

	private final static String DATABASE_NAME = "dao_test_oracle_dbShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	
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
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE DAL_CLIENT_TEST"
			   			+ "(ID NUMBER(2) NOT NULL ENABLE, " 
						+ "QUANTITY NUMBER(2)," 
						+ "TYPE NUMBER(2),"
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
	
	private static DalClient client;
	
	static {
		try {
			DalClientFactory.initClientFactory();
			client = DalClientFactory.getClient(DATABASE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { DROP_TABLE_TRIG, DROP_TABLE_SEQ, DROP_TABLE_SQL, CREATE_TABLE_SEQ, CREATE_TABLE_SQL, CREATE_TABLE_TRIG,};
			client.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		for(int i = 0; i < mod; i++) {
			sqls = new String[] { DROP_TABLE_SEQ, DROP_TABLE_TRIG, DROP_TABLE_SQL};
			client.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[3];
			for(int j = 0; j < 3; j ++) {
				int id = j + 1;
				int quantity = 10 + j;
				insertSqls[j] = "INSERT INTO " + TABLE_NAME + "(Id, quantity,type,address)"
							+ " VALUES(" + id + ", " + quantity + "," + i + ", 'SH INFO')";
			}
			int[] counts = client.batchUpdate(insertSqls, hints.inShard(i));
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
				client.update(sql, parameters, hints.inShard(i));
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
