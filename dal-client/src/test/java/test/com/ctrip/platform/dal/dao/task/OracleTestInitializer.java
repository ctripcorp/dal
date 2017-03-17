package test.com.ctrip.platform.dal.dao.task;

import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.CREATE_TABLE_SEQ;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.CREATE_TABLE_TRIG;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.DROP_TABLE_SEQ;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.DROP_TABLE_SQL;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.DROP_TABLE_TRIG;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class OracleTestInitializer {
	
	public final static String DATABASE_NAME = "dao_test_oracle";
	
	public final static String CREATE_TABLE_SQL = "CREATE TABLE DAL_CLIENT_TEST"
   			+ "(ID NUMBER(5) NOT NULL ENABLE, " 
			+ "QUANTITY NUMBER(5)," 
			+ "dbIndex NUMBER(10),"
			+ "tableIndex NUMBER(10),"
			+ "TYPE NUMBER(2),"
			+ "ADDRESS VARCHAR2(64 BYTE) NOT NULL ENABLE," 
			+ "LAST_CHANGED TIMESTAMP (6) DEFAULT SYSDATE, "
			+ "CONSTRAINT DAL_CLIENT_TEST_PK PRIMARY KEY (ID))";
	
	
	private final static String TABLE_NAME = "dal_client_test";
	
	private static DalClient client;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		client = DalClientFactory.getClient(DATABASE_NAME);
		DalHints hints = new DalHints();
		String[] sqls = new String[] { 
				DROP_TABLE_SEQ, DROP_TABLE_SQL, CREATE_TABLE_SEQ, CREATE_TABLE_SQL, CREATE_TABLE_TRIG,
				};
		client.batchUpdate(sqls, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SEQ, DROP_TABLE_TRIG, DROP_TABLE_SQL};
		client.batchUpdate(sqls, hints);
	}
	@Before
	public static void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		insertSqls = new String[3];
		for(int i = 0; i < 3; i ++) {
			int id = i + 1;
			int quantity = 10 + i;
			insertSqls[i] = "INSERT INTO " + TABLE_NAME + "(quantity,dbIndex,tableIndex,type,address)"
						+ " VALUES(" + quantity + ", 0," + i + ",1, 'SH INFO')";
		}
				
		client.batchUpdate(insertSqls, hints);
	}

	@After
	public static void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		client.update(sql, parameters, hints);
	}
}
