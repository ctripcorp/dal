package com.ctrip.platform.dal.dao.markdown;

import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class MarkdownAndUpIntergration {
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			DalClientFactory.initClientFactory();
			DalStatusManager.getMarkdownStatus().setAutoMarkupDelay(1);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
		
	private void autoMarkdown(String key) {
		MarkDownInfo info = new MarkDownInfo(key, "1", MarkDownPolicy.TIMEOUT, 0);
		info.setReason(MarkDownReason.ERRORCOUNT);
		
		MarkdownManager.autoMarkdown(info);
	}
	
	@Test
	public void markdownSuccessTest() throws Exception {
		String logicName = "dao_test";
		//Mark Down
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
		autoMarkdown(logicName);
		try{
			this.testQuery(logicName);
			Assert.fail();
		}catch(Exception e){
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(), 
					((DalException)e).getErrorCode());
		}
		
		//Mark up
		Thread.sleep(1000 * 1);
		
		try{
			this.testQuery(logicName);		
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void markdownMulipleSlavesTest(){
		String logicName = "HA_Test";
		
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
		
		//Mark Down. It has 3 slaves
		autoMarkdown("ha_test_1");
		autoMarkdown("ha_test_2");
		try{
			this.testQuery(logicName);		
		}catch(Exception e){
			Assert.fail();
		}
		
		autoMarkdown("ha_test");

		try{
			this.testQuery(logicName);
			Assert.fail();
		}catch(Exception e){
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(), 
					((DalException)e).getErrorCode());
		}
	}
	
	
	private void testQuery(String logicName) throws SQLException{
		DalClient client = DalClientFactory.getClient(logicName);
		client.query("SELECT 1", 
				new StatementParameters(), new DalHints(), new DalResultSetExtractor<Integer>() {

					@Override
					public Integer extract(ResultSet rs) throws SQLException {
						return null;
					}
				});
	}
}
