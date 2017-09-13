package com.ctrip.platform.dal.sql.logging;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalCatLoggerTest {
	
	private final static int SAMPLE = 10;

	private final static String DATABASE_NAME = "HotelPubDB";
	
	private final static String TABLE_NAME = "dal_client_test";
	
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
	
	private static DalClient client = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DalClientFactory.initClientFactory();
		client = DalClientFactory.getClient(DATABASE_NAME);
		
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL, CREATE_TABLE_SQL};
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalHints hints = new DalHints();
		StatementParameters parameters = new StatementParameters();
		String[] sqls = new String[] { DROP_TABLE_SQL};
		for (int i = 0; i < sqls.length; i++) {
			client.update(sqls[i], parameters, hints);
		}
	}

	@Before
	public void setUp() throws Exception {
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

	@After
	public void tearDown() throws Exception {
		String sql = "DELETE FROM " + TABLE_NAME;
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		try {
			client.update(sql, parameters, hints);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private StatementParameters parameters = new StatementParameters();
	private DalHints hints = new DalHints();
	private String sqlList = "select * from " + TABLE_NAME;
	
	@Test
	public void testCatSuccess1() throws Exception {
		for( int i = 0; i < SAMPLE; i++) {
			CtripLogEntry entry = new CtripLogEntry();
			DalCatLogger.start(entry);
			DalCatLogger.catTransactionSuccess(entry, 10);
		}
		TimeUnit.SECONDS.sleep(10);
	}
	
	@Test
	public void testCatSuccess2() throws Exception {
		DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
		for( int i = 0; i < SAMPLE; i++) {
			List<Short> result = dao.query(sqlList, parameters, hints, new DalRowMapper<Short>() {
				@Override
				public Short map(ResultSet rs, int rowNum) throws SQLException {
					return rs.getShort("quantity");
				}
			});
			assertEquals(3, result.size());
		}
		TimeUnit.SECONDS.sleep(10);
	}
	
	@Test
	public void testCatFailure1() throws Exception {
		for( int i = 0; i < SAMPLE; i++) {
			CtripLogEntry entry = new CtripLogEntry();
			DalCatLogger.start(entry);
			DalCatLogger.catTransactionFailed(entry, new Exception("failure test."));
		}
		TimeUnit.SECONDS.sleep(10);
	}
	
	@Test
	public void testCatFailure2() throws Exception {
		DalQueryDao dao = new DalQueryDao(DATABASE_NAME);
		for( int i = 0; i < SAMPLE; i++) {
			try {
				dao.query(sqlList, parameters, hints,
						new DalRowMapper<Short>() {
							@Override
							public Short map(ResultSet rs, int rowNum) throws SQLException {
								throw new SQLException("DalRowMapper failure test.");
							}
						});
			} catch (Exception e) { }
		}
		TimeUnit.SECONDS.sleep(10);
	}

}
