package test.com.ctrip.platform.dal.dao.shard;

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

public class DalTableDaoShardByDbTableOracleTest extends BaseDalTableDaoShardByDbTableTest{
	public DalTableDaoShardByDbTableOracleTest() {
		super(DATABASE_NAME, OracleDatabaseInitializer.diff);
	}
	
	private final static String DATABASE_NAME = "dao_test_oracle_dbTableShard";
	
	private final static String TABLE_NAME = "dal_client_test";
	private final static int mod = 2;
	private final static int tableMod = 4;
	
	public final static String CREATE_TABLE_SQL_TPL = "CREATE TABLE DAL_CLIENT_TEST_%d"
   			+ "(ID NUMBER(5) NOT NULL ENABLE, " 
			+ "QUANTITY NUMBER(5)," 
			+ "dbIndex NUMBER(10),"
			+ "tableIndex NUMBER(10),"
			+ "TYPE NUMBER(2),"
			+ "ADDRESS VARCHAR2(64 BYTE) NOT NULL ENABLE," 
			+ "LAST_CHANGED TIMESTAMP (6) DEFAULT SYSDATE, "
			+ "CONSTRAINT DAL_CLIENT_TEST_%d_PK PRIMARY KEY (ID))";
	
	private static DalClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		client = DalClientFactory.getClient(DATABASE_NAME);
		DalHints hints = new DalHints();
		String[] sqls = null;
		// For SQL server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			for(int j = 0; j < tableMod; j++) {
				sqls = new String[] { 
						String.format(DalTabelDaoShardByTableOracleTest.DROP_TABLE_TRIG_TPL, j, j),
						String.format(DalTabelDaoShardByTableOracleTest.DROP_TABLE_SEQ_TPL, j, j), 
						String.format(DalTabelDaoShardByTableOracleTest.DROP_TABLE_SQL_TPL, j, j), 
						String.format(DalTabelDaoShardByTableOracleTest.CREATE_TABLE_SEQ_TPL, j, j), 
						String.format(CREATE_TABLE_SQL_TPL, j, j), 
						String.format(DalTabelDaoShardByTableOracleTest.CREATE_TABLE_TRIG_TPL, j, j, j),
						};
				client.batchUpdate(sqls, hints.inShard(i));
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = null;
		//For Sql Server
		hints = new DalHints();
		for(int i = 0; i < mod; i++) {
			sqls = new String[tableMod];
			for(int j = 0; j < tableMod; j++) {
				sqls = new String[] { 
						String.format(DalTabelDaoShardByTableOracleTest.DROP_TABLE_TRIG_TPL, j, j),
						String.format(DalTabelDaoShardByTableOracleTest.DROP_TABLE_SEQ_TPL, j, j), 
						String.format(DalTabelDaoShardByTableOracleTest.DROP_TABLE_SQL_TPL, j, j),
						};
			}
			client.batchUpdate(sqls, hints.inShard(i));
		}
	}

	@Before
	public void setUp() throws SQLException {
		DalHints hints = new DalHints();
		String[] insertSqls = null;
		//For Sql Server
		hints = new DalHints();
		for(int k = 0; k < mod; k++) {
			for(int i = 0; i < tableMod; i++) {
				insertSqls = new String[i + 1 + 1 + 1];
				insertSqls[0] = String.format(DalTabelDaoShardByTableOracleTest.DROP_TABLE_SEQ_TPL, i, i);
				insertSqls[1] = String.format(DalTabelDaoShardByTableOracleTest.CREATE_TABLE_SEQ_TPL, i, i); 
				for(int j = 0; j < i + 1; j ++) {
					int id = j + 1;
					int quantity = id * (k + 1) * (i+1);
					insertSqls[j+2] = "INSERT INTO " + TABLE_NAME + "_"+ i + "(quantity,dbIndex,tableIndex,type,address)"
							+ " VALUES(" + quantity + ", " + k + ", " + i + ",1, 'SH INFO')";
				}
				client.batchUpdate(insertSqls, hints.inShard(k));
			}
		}
	}

	@After
	public void tearDown() throws SQLException {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		sql = "DELETE FROM " + TABLE_NAME;
		parameters = new StatementParameters();
		hints = new DalHints();
		try {
			for(int j = 0; j < mod; j++) {
				for(int i = 0; i < tableMod; i++) {
					client.update(sql + "_" + i, parameters, hints.inShard(j));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reset() throws SQLException {
		tearDown();
		setUp();
	}
}
