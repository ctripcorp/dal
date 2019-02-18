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

		DalClientFactory.shutdownFactory();
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

	@Test
	public void testGetConnectionCost() throws Exception{
		for(long i=0l;i<50000l;i++){
			String cost=DalCatLogger.getConnectionCostStringInterval(i);
			if(i<1) {
				assertEquals(String.format("current i: %d",i),"[0,1)", cost);
				continue;
			}
			if(i<2) {
				assertEquals(String.format("current i: %d",i),"[1,2)", cost);
				continue;
			}
			if(i<4) {
				assertEquals(String.format("current i: %d",i),"[2,4)", cost);
				continue;
			}
			if(i<6) {
				assertEquals(String.format("current i: %d",i),"[4,6)", cost);
				continue;
			}
			if(i<8) {
				assertEquals(String.format("current i: %d",i),"[6,8)", cost);
				continue;
			}
			if(i<10) {
				assertEquals(String.format("current i: %d",i),"[8,10)", cost);
				continue;
			}
			if(i<20) {
				assertEquals(String.format("current i: %d",i),"[10,20)", cost);
				continue;
			}
			if(i<30) {
				assertEquals(String.format("current i: %d",i),"[20,30)", cost);
				continue;
			}
			if(i<40) {
				assertEquals(String.format("current i: %d",i),"[30,40)", cost);
				continue;
			}
			if(i<50) {
				assertEquals(String.format("current i: %d",i),"[40,50)", cost);
				continue;
			}
			if(i<60) {
				assertEquals(String.format("current i: %d",i),"[50,60)", cost);
				continue;
			}
			if(i<70) {
				assertEquals(String.format("current i: %d",i),"[60,70)", cost);
				continue;
			}
			if(i<80) {
				assertEquals(String.format("current i: %d",i),"[70,80)", cost);
				continue;
			}
			if(i<90){
				assertEquals(String.format("current i: %d",i),"[80,90)",cost);
				continue;
			}
			if(i<100) {
				assertEquals(String.format("current i: %d",i),"[90,100)", cost);
				continue;
			}
			if(i<200) {
				assertEquals(String.format("current i: %d",i),"[100,200)", cost);
				continue;
			}
			if(i<300) {
				assertEquals(String.format("current i: %d",i),"[200,300)", cost);
				continue;
			}
			if(i<400) {
				assertEquals(String.format("current i: %d",i),"[300,400)", cost);
				continue;
			}
			if(i<500) {
				assertEquals(String.format("current i: %d",i),"[400,500)", cost);
				continue;
			}
			if(i<600) {
				assertEquals(String.format("current i: %d",i),"[500,600)", cost);
				continue;
			}
			if(i<700) {
				assertEquals(String.format("current i: %d",i),"[600,700)", cost);
				continue;
			}
			if(i<800) {
				assertEquals(String.format("current i: %d",i),"[700,800)", cost);
				continue;
			}
			if(i<900) {
				assertEquals(String.format("current i: %d",i),"[800,900)", cost);
				continue;
			}
			if(i<1000) {
				assertEquals(String.format("current i: %d",i),"[900,1000)", cost);
				continue;
			}
			if(i<2000) {
				assertEquals(String.format("current i: %d",i),"[1000,2000)", cost);
				continue;
			}
			if(i<3000) {
				assertEquals(String.format("current i: %d",i),"[2000,3000)", cost);
				continue;
			}
			if(i<4000) {
				assertEquals(String.format("current i: %d",i),"[3000,4000)", cost);
				continue;
			}
			if(i<5000) {
				assertEquals(String.format("current i: %d",i),"[4000,5000)", cost);
				continue;
			}
			if(i<6000) {
				assertEquals(String.format("current i: %d",i),"[5000,6000)", cost);
				continue;
			}
			if(i<7000) {
				assertEquals(String.format("current i: %d",i),"[6000,7000)", cost);
				continue;
			}
			if(i<8000) {
				assertEquals(String.format("current i: %d",i),"[7000,8000)", cost);
				continue;
			}
			if(i<9000) {
				assertEquals(String.format("current i: %d",i),"[8000,9000)", cost);
				continue;
			}
			if(i<10000) {
				assertEquals(String.format("current i: %d",i),"[9000,10000)", cost);
				continue;
			}
			if(i>=10000) {
				assertEquals(String.format("current i: %d",i),"[10000,+∞)", cost);
				continue;
			}
		}
	}

}
