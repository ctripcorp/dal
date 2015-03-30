package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DbMeta;
import com.ctrip.platform.dal.dao.client.DefaultLogger;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class AutoMarkdownTest {

	private static final String dbName = "dao_test";

	static{
		try {
			ConfigBeanFactory.getMarkdownConfigBean().init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Before
	public void setUp(){
		ConfigBeanFactory.getTimeoutMarkDownBean().setEnableTimeoutMarkDown(false);
		ConfigBeanFactory.getTimeoutMarkDownBean().setErrorCountThreshold(10000);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(10000);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().markup(dbName);
	}
	
	@Test
	public void markdownSuccessTest() throws InterruptedException{
		ConfigBeanFactory.getTimeoutMarkDownBean().setEnableTimeoutMarkDown(true);
		ConfigBeanFactory.getTimeoutMarkDownBean().setErrorCountThreshold(5);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(10);
		
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		for (int i = 0; i < 10; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000); //Wait the collection thread complete
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));		
	}
	
	@Test
	public void markdownCanbeMarkupTest() throws Exception{
		ConfigBeanFactory.getTimeoutMarkDownBean().setEnableTimeoutMarkDown(true);
		ConfigBeanFactory.getTimeoutMarkDownBean().setErrorCountThreshold(5);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1);
		
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		for (int i = 0; i < 10; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000);
		Assert.assertTrue(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(dbName));
		
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpVolume(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().set("autoMarkUpSchedule", "3");
		
		Thread.sleep(2000); //Wait auto mark up delay
		for (int i = 0; i < MarkupPhase.length; i++) {
			if(i >= 7)
				Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
			else{
				Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
			}
		}
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		Assert.assertFalse(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(dbName));
	}
	
	@Test
	public void markdownDalayCantBeMarkupTest() throws Exception{
		ConfigBeanFactory.getTimeoutMarkDownBean().setEnableTimeoutMarkDown(true);
		ConfigBeanFactory.getTimeoutMarkDownBean().setErrorCountThreshold(5);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(1);
		
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		for (int i = 0; i < 10; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000);
		Assert.assertTrue(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(dbName));
		
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpVolume(1);
		ConfigBeanFactory.getMarkdownConfigBean().setAutoMarkUpDelay(10);
		ConfigBeanFactory.getMarkdownConfigBean().setEnableAutoMarkDown(true);
		ConfigBeanFactory.getMarkdownConfigBean().set("autoMarkUpSchedule", "3");
		
		for (int i = 0; i < MarkupPhase.length; i++) {
			Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		}

		Assert.assertTrue(ConfigBeanFactory.getMarkdownConfigBean().isMarkdown(dbName));
	}

	private SQLException mockTimeoutException(){
		return new MySQLTimeoutException("Test mysql timeout excption");
	}
	
	public DalConnection mockDalConnection(){
		DalConnection conn = EasyMock.createMock(DalConnection.class);
		DbMeta meta = EasyMock.createMock(DbMeta.class);
		EasyMock.expect(meta.getAllInOneKey()).andReturn(dbName).times(1);
		EasyMock.expect(meta.getDatabaseCategory()).andReturn(DatabaseCategory.MySql).times(1);
		
		EasyMock.expect(conn.getMeta()).andReturn(meta).times(3);
		EasyMock.expect(conn.getLogger()).andReturn(new DefaultLogger()).times(3);
		
		EasyMock.replay(meta, conn);
		return conn;
	}
}
