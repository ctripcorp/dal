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
		ConfigBeanFactory.getMarkdownConfigBean().setMarkdown(true);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}
	
	@Test
	public void manualMarkdownTest() throws Exception {
		ConfigBeanFactory.getMarkdownConfigBean().set("dbMarkdown", dbName);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}

	@Test
	public void manualMarkdownCanBeMarkupTest() throws Exception{
		ConfigBeanFactory.getMarkdownConfigBean().set("dbMarkdown", dbName);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		
		ConfigBeanFactory.getMarkdownConfigBean().set("dbMarkdown", "");
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
	}
	
	@Test
	public void manualMarkdownCantBeAutoMarkupTest() throws Exception{
		ConfigBeanFactory.getMarkdownConfigBean().set("dbMarkdown", dbName);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkupBatches(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkupDelay(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutomarkup(true);
		ConfigBeanFactory.getMarkdownConfigBean().set("markUpSchedule", "3,5");
		
		Thread.sleep(2000);
		
		for (int i = 0; i < MarkupPhase.length * 2 + 1; i++) {
			MarkdownManager.isMarkdown(dbName);
		}
		
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}
}
