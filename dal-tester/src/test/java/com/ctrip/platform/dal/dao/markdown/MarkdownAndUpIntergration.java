package com.ctrip.platform.dal.dao.markdown;

import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class MarkdownAndUpIntergration {
	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void markdownSuccessTest() throws Exception {
		String logicName = "dao_test";
		//Mark Down
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().markdown(logicName);
		try{
			this.testQuery(logicName);
			Assert.fail();
		}catch(Exception e){
			Assert.assertEquals(ErrorCode.MarkdownConnection.getCode(), 
					((DalException)e).getErrorCode());
		}
		//Mark up
		ConfigBeanFactory.getMarkdownConfigBean().markup(logicName);
		
		try{
			this.testQuery(logicName);		
		}catch(Exception e){
			Assert.fail();
		}
	}
	
	@Test
	public void markdownMulipleSlavesTest(){
		String logicName = "HA_Test";
		
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		
		//Mark Down
		ConfigBeanFactory.getMarkdownConfigBean().markdown("ha_test_1");
		ConfigBeanFactory.getMarkdownConfigBean().markdown("ha_test_2");		
		try{
			this.testQuery(logicName);		
		}catch(Exception e){
			Assert.fail();
		}
		
		ConfigBeanFactory.getMarkdownConfigBean().markdown("ha_test");
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
						// TODO Auto-generated method stub
						return null;
					}
				});
	}
}
