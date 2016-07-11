package com.ctrip.platform.dal.dao.markdown;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.status.DalStatusManager;

public class ManualMarkDownTest {

	private static final String dbName = "dao_test";
	static{
		try {
//			DalStateManager.getMarkdownState().init();
			DalClientFactory.initClientFactory();
			DalClientFactory.getClient(dbName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void appMarkdownTest(){
		DalStatusManager.getMarkdownStatus().setAppMarkDown(true);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		DalStatusManager.getMarkdownStatus().setAppMarkDown(false);
	}
	
	@Test
	public void manualMarkdownTest() throws Exception {
		DalStatusManager.getDataSourceStatus(dbName).setManualMarkdown(true);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}

	@Test
	public void manualMarkdownCanBeMarkupTest() throws Exception{
		DalStatusManager.getDataSourceStatus(dbName).setManualMarkdown(true);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		
		DalStatusManager.getDataSourceStatus(dbName).setManualMarkdown(false);
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
	}
	
	@Test
	public void manualMarkdownCantBeAutoMarkupTest() throws Exception{
		DalStatusManager.getDataSourceStatus(dbName).setManualMarkdown(true);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		
		DalStatusManager.getMarkdownStatus().setAutoMarkUpVolume(1);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpSchedule("3,5");
		
		Thread.sleep(2000);
		
		for (int i = 0; i < MarkupPhase.length * 2 + 1; i++) {
			MarkdownManager.isMarkdown(dbName);
		}
		
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}
}
