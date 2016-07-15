package com.ctrip.platform.dal.dao.markdown;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class AsyncMarkupManagerTest {
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	public void asyncMarkupWithoutRollbackTest() throws InterruptedException {
		final String dbName = "dao_test";
		MarkdownManager.autoMarkdown(dbName);
		Assert.assertTrue(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
		
		for (int i = 0; i < 10; i++) {
			Thread tt = new Thread(new Runnable(){
				@Override
				public void run() {
					for (int j = 0; j < 300; j++) {
						boolean passed = AsyncMarkupManager.isPass(dbName);
						/*try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						System.out.println(passed);
					}
					AysncMarkupPhase pro = AsyncMarkupManager.getStatus(dbName);
					System.out.println("total: " + pro.getTotalCount() + ", passed: " + pro.getPassed() + 
							", phase: " + pro.getPhaseIndex() + ", markdown: " + 
							DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
				}});
			tt.start();
			tt.join();
		}
		
		Assert.assertFalse(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
	}
	
	@Test
	public void asyncMarkupWithRollbackTest() throws InterruptedException{
		final String dbName = "dao_test";
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		MarkdownManager.autoMarkdown(dbName);
		Assert.assertTrue(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
		
		for (int i = 0; i < 10; i++) {
			Thread tt = new Thread(new Runnable(){
				@Override
				public void run() {
					for (int j = 0; j < 300; j++) {
						boolean passed = AsyncMarkupManager.isPass(dbName);
						if (passed)
							AsyncMarkupManager.callback(new ErrorContext(dbName, DatabaseCategory.MySql, 1000,
									new MySQLTimeoutException()));
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
//						System.out.println(passed);
					}
					AysncMarkupPhase pro = AsyncMarkupManager.getStatus(dbName);
//					System.out.println("total: " + pro.getTotalCount() + ", passed: " + pro.getPassed() + 
//							", phase: " + pro.getPhaseIndex() + ", markdown: " + 
//							DalStateManager.getMarkdownState().isMarkdown(dbName));
				}});
			tt.start();
			tt.join();
		}
		
		Assert.assertTrue(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
		Assert.assertEquals(0, AsyncMarkupManager.getStatus(dbName).getPhaseIndex());
	}

}
