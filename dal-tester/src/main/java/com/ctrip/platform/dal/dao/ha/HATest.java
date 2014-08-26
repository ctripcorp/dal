package com.ctrip.platform.dal.dao.ha;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalHAManager;
import com.ctrip.platform.dal.dao.unitbase.Database;

public class HATest {
	private static Database database = null;
	private static Database database2 = null;
	private static int markCount = 0;
	static {
		database = new Database("dao_test", "dal_client_test",
				DatabaseCategory.MySql);
		database2 = new Database("HA_Test", "dal_client_test",
				DatabaseCategory.SqlServer);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		database.init();
		database2.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		database.drop();
		database2.drop();
	}
	
	@Before
	public void setUp() throws Exception {
		markCount = 0;
		database.mock();
		database2.mock();
	}
	
	@After
	public void tearDown() throws Exception {
		database.clear();
		database2.clear();
	}

	@Test
	public void testNotRetryNotFailOver() throws SQLException {
		DalHAManager.setHaEnabled(true);
		DalHints hints = new DalHints();	
		
		String sql = "SELECT Count(*) from " + database.getTableName();
		Integer count = database.getClient().query(sql, new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {
						throw createException(-100);
					}
				});
		Assert.assertEquals(0, count ==null ? 0 : count.intValue());
		Assert.assertEquals(1, hints.get().getRetryCount());
	}

	@Test
	public void testAllRetryFailed() throws SQLException {
		DalHAManager.setHaEnabled(true);
		DalHints hints = new DalHints();
		String sql = "SELECT Count(*) from " + database.getTableName();
		Integer count = database.getClient().query(sql, new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {
						throw createException(1043);
					}
				});

		Assert.assertEquals(0, count ==null ? 0 : count.intValue());
		Assert.assertEquals(DalHAManager.getRetryCount(), hints.get()
				.getRetryCount());
	}

	@Test
	public void testTheSecondRetrySuccess() throws SQLException {
		DalHAManager.setHaEnabled(true);
		DalHints hints = new DalHints();
		String sql = "SELECT Count(*) from " + database.getTableName();
		Integer count = database.getClient().query(sql, new StatementParameters(),
				hints, new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {
						if (1 == markCount++){
							while(rs.next()){
								return rs.getInt(1);
							}
						}
						throw createException(1043);
					}
				});

		Assert.assertEquals(3, count ==null ? 0 : count.intValue());
	}

	@Test
	public void testTheSecondFailOverSuccess() throws SQLException {
		DalHAManager.setHaEnabled(true);
		DalHints hints = new DalHints();
		String sql = "SELECT Count(*) from " + database2.getTableName();
		Integer count = database2.getClient().query(sql,
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
							throw createException(1021);
						}
						return 0;
					}
				});
		Assert.assertEquals(3, count ==null ? 0 : count.intValue());
		Assert.assertEquals(1, hints.get().getRetryCount());
	}
	
	@Test
	public void testAllFailOverFailed() throws SQLException {
		DalHAManager.setHaEnabled(true);
		DalHints hints = new DalHints();
		String sql = "SELECT Count(*) from " + database2.getTableName();
		Integer count = database2.getClient().query(sql,
				new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {
						throw createException(1021);
					}
				});
		
		Assert.assertEquals(0, count ==null ? 0 : count.intValue());
		Assert.assertEquals(DalHAManager.getRetryCount(), hints.get().getRetryCount());
	}
	
	@Test
	public void testRetryFailOverDisabled(){
		DalHints hints = new DalHints();
		String sql = "SELECT * from " + database2.getTableName();
		try {
			database2.getClient().query(sql,
					new StatementParameters(), hints,
					new DalResultSetExtractor<String>() {
						@Override
						public String extract(ResultSet rs) throws SQLException {
							throw createException(1021);
						}
					});
		} catch (SQLException e) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testFirstRetrySecondeFailOver() throws SQLException{
		DalHAManager.setHaEnabled(true);
		DalHints hints = new DalHints();
		String sql = "SELECT Count(*) from " + database2.getTableName();
		Integer count = database2.getClient().query(sql,
				new StatementParameters(), hints,
				new DalResultSetExtractor<Integer>() {
					@Override
					public Integer extract(ResultSet rs) throws SQLException {		
						if(0 == markCount){
							markCount ++;
							throw createException(1043);
						}
						if(1== markCount){
							markCount ++;
							throw createException(1021);
						}
						else{
							while(rs.next()){
								return rs.getInt(1);
							}
						}
						return 0;	
					}
				});
		Assert.assertEquals(3, count ==null ? 0 : count.intValue());
	}

	private SQLException createException(int errorCode) {
		SQLException mockex = EasyMock.createMock(SQLException.class);
		EasyMock.expect(mockex.getErrorCode()).andReturn(errorCode)
				.times(Integer.MAX_VALUE);
		EasyMock.replay(mockex);
		return mockex;
	}

}
