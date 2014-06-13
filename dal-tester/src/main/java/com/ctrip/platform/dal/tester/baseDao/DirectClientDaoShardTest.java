package com.ctrip.platform.dal.tester.baseDao;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.freeway.config.LogConfig;
import com.ctrip.freeway.logging.ILog;
import com.ctrip.freeway.logging.LogManager;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.logging.DalEventEnum;

public class DirectClientDaoShardTest {
	private final static String DATABASE_NAME = "dao_test";
	
	private final static String TABLE_NAME = "dal_client_test";
	
	private final static String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME +"("
			+ "id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT, "
			+ "quantity int,"
			+ "type smallint, "
			+ "address VARCHAR(64) not null, "
			+ "last_changed timestamp default CURRENT_TIMESTAMP)";
	
	
	private final static String DROP_TABLE_SQL_SQL_SVR = "IF EXISTS ("
			+ "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = '"+ TABLE_NAME + "') "
			+ "DROP TABLE  "+ TABLE_NAME;
	
	//Create the the table
	private final static String CREATE_TABLE_SQL_SQL_SVR = "CREATE TABLE " + TABLE_NAME +"("
			+ "Id int NOT NULL IDENTITY(1,1) PRIMARY KEY, "
			+ "quantity int,type smallint, "
			+ "address varchar(64) not null,"
			+ "last_changed datetime default getdate())";
	
	private static DalClient client = null;

	static {
		try {
			DalClientFactory.initClientFactory("/DalMult.config");
			client = DalClientFactory.getClient(DATABASE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL};
		client.batchUpdate(sqls, hints);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		String[] sqls = new String[] { DROP_TABLE_SQL};
		client.batchUpdate(sqls, hints);
	}

	@Before
	public void setUp() throws Exception {
		DalHints hints = new DalHints();
		String[] insertSqls = new String[] {
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(1, 10, 1, 'SH INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(2, 11, 1, 'BJ INFO', NULL)",
				"INSERT INTO " + TABLE_NAME
						+ " VALUES(3, 12, 2, 'SZ INFO', NULL)" };
		int[] counts = client.batchUpdate(insertSqls, hints);
		assertArrayEquals(new int[] { 1, 1, 1 }, counts);
	}

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.update(sql, parameters, hints);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public static void test() {
		try {
			DalClient client = DalClientFactory.getClient("AbacusDB_INSERT_1");
			
			String sql = "select * from AbacusAddInfoLog";
			
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();
			Map<String, Integer> colValues = new HashMap<String, Integer>();
			colValues.put("user_id", 0);
			hints.set(DalHintEnum.shardColValues, colValues);

			client.query(sql, parameters, hints, new DalResultSetExtractor<Object>() {
				@Override
				public Object extract(ResultSet rs) throws SQLException {
					while(rs.next()){
						rs.getObject(1);
					}
					return null;
				}
				
			});
			
			hints = new DalHints();
			colValues = new HashMap<String, Integer>();
			colValues.put("user_id", 2);
			hints.masterOnly();
			hints.set(DalHintEnum.shardColValues, colValues);

			client.query(sql, parameters, hints, new DalResultSetExtractor<Object>() {
				@Override
				public Object extract(ResultSet rs) throws SQLException {
					while(rs.next()){
						rs.getObject(1);
					}
					return null;
				}
				
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public static void test2() {
		try {
			DalClient client = DalClientFactory.getClient("AbacusDB_INSERT_1");
			StatementParameters parameters = new StatementParameters();
			DalHints hints = new DalHints();
			//String delete = "update AbacusAddInfoLog set PNR='dafas' where id = 100";
			String select = "select PNR from AbacusAddInfoLog where LOGID = 100";
			String update = "update AbacusAddInfoLog set PNR='dafas11' where LOGID = 100";
			String restore = "update AbacusAddInfoLog set PNR='dafas' where LOGID = 100";
			
			hints = new DalHints();
			Map<String, Integer> colValues = new HashMap<String, Integer>();
			colValues.put("user_id", 0);
			hints.set(DalHintEnum.shardColValues, colValues);

			client.update(update, parameters, hints);
			
			client.query(select, parameters, hints, new DalResultSetExtractor<Object>() {
				@Override
				public Object extract(ResultSet rs) throws SQLException {
					while(rs.next()){
						System.out.println(rs.getObject(1));
					}
					return null;
				}
				
			});
			

			client.update(restore, parameters, hints);
			
			client.query(select, parameters, hints, new DalResultSetExtractor<Object>() {
				@Override
				public Object extract(ResultSet rs) throws SQLException {
					while(rs.next()){
						System.out.println(rs.getObject(1));
					}
					return null;
				}
				
			});
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Integer typeTest(Object objVal) {
		if(objVal == null || objVal instanceof Integer)
			return (Integer)objVal;
		
		return Integer.valueOf(((Number)objVal).intValue());

	}

	public static void main(String[] args) {
		ILog logger = LogManager.getLogger("DAL Java Client");
		logger.debug("test");
		System.out.print(LogConfig.getAppID());
		try {
			DalClientFactory.initClientFactory("/DalMult.config");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
//		test();
//		test2();

		System.out.println(typeTest(null));
		System.out.println(typeTest(Integer.valueOf(-1)));
		System.out.println(typeTest(new Short((short)-1)));
		System.out.println(typeTest(new Byte((byte)-1)));
		
		System.exit(0);
	}
}