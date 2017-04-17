package test.com.ctrip.platform.dal.dao.ha;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.com.ctrip.platform.dal.dao.shard.DalQueryDaoMySqlTest;
import test.com.ctrip.platform.dal.dao.unitbase.Database;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalHA;
import com.ctrip.platform.dal.dao.status.DalStatusManager;

public class HATest {
	private static DalClient database = null;
	private static DalClient database2 = null;
	private static DalClient database3 = null;
	private static DalHints hints = new DalHints();
	private static int markCount = 0;
	private String sql = "SELECT Count(*) from dal_client_test";

	@BeforeClass
	public static void setUpBeforeClass() {
		/**
		 * ha_test, ha_test_1, ha_test_2 are the same DB
		 */
		try {
			DalClientFactory.initClientFactory();
			database = DalClientFactory.getClient("HA_Test_0");
			database2 = DalClientFactory.getClient("HA_Test");
			database3 = DalClientFactory.getClient("HA_Test_1");
			
			DalStatusManager.getHaStatus().setEnabled(true);
			DalStatusManager.getHaStatus().setRetryCount(3);
			DalQueryDaoMySqlTest.setUpBeforeClass();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DalStatusManager.getHaStatus().setEnabled(false);
		DalQueryDaoMySqlTest.tearDownAfterClass();
	}
	
	@Before
	public void setUp() throws Exception {
		markCount = 0;
		new DalQueryDaoMySqlTest().setUp();
	}
	
	@After
	public void tearDown() throws Exception {
		new DalQueryDaoMySqlTest().tearDown();
	}

	@Test
	public void testNotRetryNotFailOver(){
		hints = new DalHints();
		Integer count = 0;
		try{
			count = database.query(sql, new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {
						throw createException(-100);
					}
				});
		}catch(SQLException e){}
		Assert.assertEquals(0, count ==null ? 0 : count.intValue());
		Assert.assertEquals(1, hints.getHA().getRetryCount());
	}

	@Test
	public void testAllRetryFailed() {
		hints = new DalHints();
		Integer count = 0;
		try {
			count = database.query(sql, new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {
						mockRetryThrows(hints.getHA());
						return 0;
					}
				});
		}catch(SQLException e){}

		Assert.assertEquals(0, count ==null ? 0 : count.intValue());
		Assert.assertEquals(DalStatusManager.getHaStatus().getRetryCount(), hints.getHA()
				.getRetryCount());
	}

	@Test
	public void testTheSecondRetrySuccess() {
		hints = new DalHints();
		Integer count = 0;
		try{
			count = database.query(sql, new StatementParameters(),
				hints, new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {						
						if (1 == markCount++){
							while(rs.next()){
								return rs.getInt(1);
							}
						}
						mockRetryThrows(hints.getHA());
						return 0;
					}
				});
		}catch(SQLException e){}
		Assert.assertEquals(3, count ==null ? 0 : count.intValue());
	}

	@Test
	public void testTheSecondFailOverSuccess() throws SQLException {
		hints = new DalHints();
		Integer count = 0;
		try{ 
			count = database2.query(sql,
				new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {
						if(1== markCount){
							markCount++;
							while(rs.next()){
								return rs.getInt(1);
							}				
						}else{
							markCount++;
							mockFailOverThrow(hints.getHA());
						}
						return 0;
					}
				});
		}catch(SQLException e){}
		Assert.assertEquals(3, count ==null ? 0 : count.intValue());
		Assert.assertEquals(1, hints.getHA().getRetryCount());
	}
	
	@Test
	public void testAllFailOverFailed(){
		hints = new DalHints();
		Integer count = 0;
		try{
			count = database3.query(sql,
				new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {
						mockFailOverThrow(hints.getHA());
						return 0;
					}
				});
		}catch(SQLException e){ }
		Assert.assertEquals(0, count ==null ? 0 : count.intValue());
		Assert.assertEquals(3, hints.getHA().getRetryCount());
	}
	
	@Test
	public void testRetryFailOverDisabled(){
		hints = new DalHints();
		try {
			database2.query(sql,
					new StatementParameters(), hints,
					new DalResultSetExtractor<String>() {
						@Override
						public String extract(ResultSet rs) throws SQLException {
							mockFailOverThrow(hints.getHA());
							return "";
						}
					});
		} catch (SQLException e) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testFirstRetrySecondeFailOver() {
		hints = new DalHints();
		Integer count = 0;
		try{ 
			count = database2.query(sql,
				new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {		
						if(0 == markCount){
							markCount ++;
							mockRetryThrows(hints.getHA());
						}
						if(1== markCount){
							markCount ++;
							mockFailOverThrow(hints.getHA());
						}
						else{
							//Here fail over to the third slave
							while(rs.next()){
								return rs.getInt(1);
							}
						}
						return 0;	
					}
				});
		}catch(SQLException e){ }
		Assert.assertEquals(3, count ==null ? 0 : count.intValue());
	}

	@Test
	public void testHAWithMarkdowns() throws Exception{
		DalStatusManager.getDataSourceStatus("MySqlShard_1").setManualMarkdown(true);
		hints = new DalHints();
		Integer count = 0;
		try{ 
			count = database2.query(sql,
				new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {		
						if(0 == markCount){
							markCount ++;
							mockRetryThrows(hints.getHA());
						}
						if(1== markCount){
							markCount ++;
							mockFailOverThrow(hints.getHA());
						}
						else{
							//Here fail over to master
							while(rs.next()){
								return rs.getInt(1);
							}
						}
						return 0;	
					}
				});
		}catch(SQLException e){ }
		Assert.assertEquals(3, count ==null ? 0 : count.intValue());
		DalStatusManager.getDataSourceStatus("MySqlShard_1").setManualMarkdown(false);
	}
	
	private SQLException createException(int errorCode) {
		SQLException mockex = new SQLException("TEST", "SQLState", errorCode);
		/*SQLException mockex = EasyMock.createMock(SQLException.class);
		EasyMock.expect(mockex.getMessage()).andReturn("test").times(Integer.MAX_VALUE);
		EasyMock.expect(mockex.getErrorCode()).andReturn(errorCode)
				.times(Integer.MAX_VALUE);
		EasyMock.replay(mockex);*/
		return mockex;
	}

	private void mockRetryThrows(DalHA ha)throws SQLException{
		if(ha.getDatabaseCategory() == DatabaseCategory.MySql){
			throw createException(1043);
		}else{
			throw createException(1043);
		}
	}
	
	private void mockFailOverThrow(DalHA ha) throws SQLException{
		if(ha.getDatabaseCategory() == DatabaseCategory.MySql){
			throw createException(1021);
		}else{
			throw createException(2);
		}
	}
}
