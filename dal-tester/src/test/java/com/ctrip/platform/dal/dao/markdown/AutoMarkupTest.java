package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class AutoMarkupTest {

	private static final String dbName = "dao_test";
	static{
		try {
//			DalStateManager.getTimeoutMarkDown().init();
//			DalStateManager.getMarkdownState().init();
		} catch (Exception e) {
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
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(5);
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			SQLException e = this.mockNotTimeoutException();
			DatabaseCategory ct = DatabaseCategory.MySql;
			if(i % 2 == 0){
				ct = random.nextBoolean() ? DatabaseCategory.MySql : DatabaseCategory.SqlServer;
				e = this.mockTimeoutException(ct);
			}
			ErrorContext ctx = new ErrorContext(dbName, ct, 1000, e);
			detector.detect(ctx);
		}
		Assert.assertTrue(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
		Assert.assertTrue(DalStatusManager.getDataSourceStatus(dbName).isAutoMarkdown());
		
		DalStatusManager.getMarkdownStatus().setAutoMarkUpVolume(1);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpSchedule("3,5");
		
		for (int i = 0; i < MarkupPhase.length * 2 + 1; i++) {
			MarkupManager.isPass(dbName);
		}
		Assert.assertFalse(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
		
		Assert.assertEquals(0, MarkupManager.getMarkup(dbName).getCurrentPhase().getTotal());
		Assert.assertEquals(1, MarkupManager.getMarkup(dbName).getNextPhaseIndex());
	}
	
	@Test
	public void autoMarkupFailTest() throws Exception{	
		TimeoutDetector detector = new TimeoutDetector();
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(5);
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			SQLException e = this.mockNotTimeoutException();
			DatabaseCategory ct = DatabaseCategory.MySql;
			if(i % 2 == 0){
				ct = random.nextBoolean() ? DatabaseCategory.MySql : DatabaseCategory.SqlServer;
				e = this.mockTimeoutException(ct);
			}
			ErrorContext ctx = new ErrorContext(dbName, ct, 1000, e);
			detector.detect(ctx);
		}
		Assert.assertTrue(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
		Assert.assertTrue(DalStatusManager.getDataSourceStatus(dbName).isAutoMarkdown());
		
		DalStatusManager.getMarkdownStatus().setAutoMarkUpVolume(1);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpSchedule("3");
		
		for (int i = 0; i < MarkupPhase.length + 1; i++) {
			MarkupManager.isPass(dbName);
			if(i == 9){
				MarkupManager.rollback(new ErrorContext(dbName, 
						DatabaseCategory.MySql, 1000, 
						mockTimeoutException(DatabaseCategory.MySql)));
			}
		}
		Assert.assertTrue(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
		
		Assert.assertEquals(1, MarkupManager.getMarkup(dbName).getCurrentPhase().getTotal());
		Assert.assertEquals(1, MarkupManager.getMarkup(dbName).getNextPhaseIndex());
	}
	
	@Test
	public void MarkupInfoTest(){
		Assert.assertEquals("No Info", MarkupManager.getMarkupInfo(dbName));
		MarkupManager.isPass(dbName);
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
