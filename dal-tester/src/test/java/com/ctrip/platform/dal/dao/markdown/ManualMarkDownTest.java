package com.ctrip.platform.dal.dao.markdown;

import junit.framework.Assert;

import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;

public class ManualMarkDownTest {

	private static final String dbName = "dao_test";
	static{
		try {
			ConfigBeanFactory.getMarkdownConfigBean().init();
			DalClientFactory.initClientFactory();
			DalClientFactory.getClient(dbName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void appMarkdownTest(){
		ConfigBeanFactory.getMarkdownConfigBean().setAppMarkDown(true);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}
	
	@Test
	public void manualMarkdownTest() throws Exception {
		ConfigBeanFactory.getMarkdownConfigBean().set("markDownKeys", dbName);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}

	@Test
	public void manualMarkdownCanBeMarkupTest() throws Exception{
		ConfigBeanFactory.getMarkdownConfigBean().set("markDownKeys", dbName);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		
		ConfigBeanFactory.getMarkdownConfigBean().set("markDownKeys", "");
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
	}
	
	@Test
	public void manualMarkdownCantBeAutoMarkupTest() throws Exception{
		ConfigBeanFactory.getMarkdownConfigBean().set("markDownKeys", dbName);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpVolume(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().set("autoMarkUpSchedule", "3,5");
		
		Thread.sleep(2000);
		
		for (int i = 0; i < MarkupPhase.length * 2 + 1; i++) {
			MarkdownManager.isMarkdown(dbName);
		}
		
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}
}
