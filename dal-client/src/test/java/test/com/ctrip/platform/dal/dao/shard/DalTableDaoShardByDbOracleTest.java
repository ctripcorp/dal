package test.com.ctrip.platform.dal.dao.shard;

import static org.junit.Assert.fail;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.CREATE_TABLE_SEQ;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.CREATE_TABLE_SQL2;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.CREATE_TABLE_TRIG;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.DROP_TABLE_SEQ;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.DROP_TABLE_SQL;
import static test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer.DROP_TABLE_TRIG;

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

public class DalTableDaoShardByDbOracleTest extends BaseDalTableDaoShardByDbTest {
	public DalTableDaoShardByDbOracleTest() {
		super(DATABASE_NAME, GENERATED_KEY, OracleDatabaseInitializer.diff);
	}

	private final static String DATABASE_NAME = "dao_test_oracle_dbShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	private final static String GENERATED_KEY = "GENERATED_KEY";
	
	private static DalClient client;
	
    public final static String CREATE_TABLE_SQL_TPL = "CREATE TABLE DAL_CLIENT_TEST"
            + "(ID NUMBER(5) NOT NULL ENABLE, " 
            + "QUANTITY NUMBER(5)," 
            + "dbIndex NUMBER(10),"
            + "tableIndex NUMBER(10),"
            + "TYPE NUMBER(2),"
            + "ADDRESS VARCHAR2(64 BYTE) NOT NULL ENABLE," 
            + "LAST_CHANGED TIMESTAMP (6) DEFAULT SYSDATE, "
            + "CONSTRAINT DAL_CLIENT_TEST_PK PRIMARY KEY (ID))";
    
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		client = DalClientFactory.getClient(DATABASE_NAME);
		for(int i = 0; i < mod; i++) {
			DalHints hints = new DalHints();
			String[] sqls = new String[] { 
					DROP_TABLE_SEQ, DROP_TABLE_SQL, CREATE_TABLE_SEQ, CREATE_TABLE_SQL_TPL, CREATE_TABLE_TRIG,
					};
			client.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		for(int i = 0; i < mod; i++) {
			DalHints hints = new DalHints();
			String[] sqls = new String[] { DROP_TABLE_SEQ, DROP_TABLE_TRIG, DROP_TABLE_SQL};
			client.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@Before
	public void setUp() throws Exception {
		setUpBeforeClass();
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		for(int i = 0; i < mod; i++) {
			insertSqls = new String[5];
			insertSqls[0] = DROP_TABLE_SEQ;
			insertSqls[1] = CREATE_TABLE_SEQ; 
			insertSqls = new String[] {
					"INSERT INTO " + TABLE_NAME  + "(quantity,tableIndex,type,address)"
							+ " VALUES(10, " + i + " ,1, 'SH INFO')",
					"INSERT INTO " + TABLE_NAME  + "(quantity,tableIndex,type,address)"
							+ " VALUES(11, " + i + " ,1, 'BJ INFO')",
					"INSERT INTO " + TABLE_NAME  + "(quantity,tableIndex,type,address)"
							+ " VALUES(12, " + i + " ,1, 'SZ INFO')" };
			client.batchUpdate(insertSqls, hints.inShard(i));
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
