package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.client.DefaultLogger;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class AutoMarkupTest {

	private static final String dbName = "dao_test";
	static{
		try {
			ConfigBeanFactory.getTimeoutMarkDownBean().init();
			ConfigBeanFactory.getMarkdownConfigBean().init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Before
	public void setUp(){
		MarkupManager.reset(dbName);
	}
	
	@Test
	public void autoMarkupTest() throws Exception{	
		TimeoutDetector detector = new TimeoutDetector();
		ConfigBeanFactory.getTimeoutMarkDownBean().setEnableTimeoutMarkDown(true);
		ConfigBeanFactory.getTimeoutMarkDownBean().setErrorCountThreshold(5);
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			SQLException e = this.mockNotTimeoutException();
			DatabaseCategory ct = DatabaseCategory.MySql;
			if(i % 2 == 0){
				ct = random.nextBoolean() ? DatabaseCategory.MySql : DatabaseCategory.SqlServer;
				e = this.mockTimeoutException(ct);
			}
			ErrorContext ctx = new ErrorContext(dbName, ct, 1000, e, new DefaultLogger());
			detector.detect(ctx);
		}
		Assert.assertTrue(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(dbName));
		Assert.assertTrue(ConfigBeanFactory.getMarkdownConfigBean().getMarkItem(dbName).isAuto());
		
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpVolume(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().set("autoMarkUpSchedule", "3,5");
		
		for (int i = 0; i < MarkupPhase.length * 2 + 1; i++) {
			MarkupManager.isPass(dbName, new DefaultLogger());
		}
		Assert.assertFalse(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(dbName));
		
		Assert.assertEquals(0, MarkupManager.getMarkup(dbName, new DefaultLogger()).getCurrentPhase().getTotal());
		Assert.assertEquals(1, MarkupManager.getMarkup(dbName, new DefaultLogger()).getNextPhaseIndex());
	}
	
	@Test
	public void autoMarkupFailTest() throws Exception{	
		TimeoutDetector detector = new TimeoutDetector();
		ConfigBeanFactory.getTimeoutMarkDownBean().setEnableTimeoutMarkDown(true);
		ConfigBeanFactory.getTimeoutMarkDownBean().setErrorCountThreshold(5);
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			SQLException e = this.mockNotTimeoutException();
			DatabaseCategory ct = DatabaseCategory.MySql;
			if(i % 2 == 0){
				ct = random.nextBoolean() ? DatabaseCategory.MySql : DatabaseCategory.SqlServer;
				e = this.mockTimeoutException(ct);
			}
			ErrorContext ctx = new ErrorContext(dbName, ct, 1000, e, new DefaultLogger());
			detector.detect(ctx);
		}
		Assert.assertTrue(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(dbName));
		Assert.assertTrue(ConfigBeanFactory.getMarkdownConfigBean().getMarkItem(dbName).isAuto());
		
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpVolume(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().set("autoMarkUpSchedule", "3");
		
		for (int i = 0; i < MarkupPhase.length + 1; i++) {
			MarkupManager.isPass(dbName, new DefaultLogger());
			if(i == 9){
				MarkupManager.rollback(new ErrorContext(dbName, 
						DatabaseCategory.MySql, 1000, 
						mockTimeoutException(DatabaseCategory.MySql), new DefaultLogger()));
			}
		}
		Assert.assertTrue(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(dbName));
		
		Assert.assertEquals(1, MarkupManager.getMarkup(dbName, new DefaultLogger()).getCurrentPhase().getTotal());
		Assert.assertEquals(1, MarkupManager.getMarkup(dbName, new DefaultLogger()).getNextPhaseIndex());
	}
	
	@Test
	public void MarkupInfoTest(){
		Assert.assertEquals("No Info", MarkupManager.getMarkupInfo(dbName));
		MarkupManager.isPass(dbName, new DefaultLogger());
		Assert.assertEquals("total:1--loop:1", MarkupManager.getMarkupInfo(dbName));
	}
	
	private SQLException mockTimeoutException(DatabaseCategory category){
		if(category == DatabaseCategory.MySql){
			return new MySQLTimeoutException("Test mysql timeout excption");
		}
		else{
			category = DatabaseCategory.SqlServer;
			return new SQLException("The query has timed out");
		}
	}
	
	private SQLException mockNotTimeoutException() {
		return new SQLException("Test sql exception");
	}

}
