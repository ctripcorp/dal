package com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DbMeta;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class AutoMarkdownTest {

	private static final String dbName = "dao_test";

	static{
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Before
	public void setUp(){
		DalStatusManager.getTimeoutMarkdown().setEnabled(false);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(10000);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(10000);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		MarkdownManager.autoMarkdown(dbName);
		MarkdownManager.autoMarkup(dbName);
	}
	
	@Test
	public void markdownSuccessTest() throws InterruptedException{
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(5);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(10);
		
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		for (int i = 0; i < 10; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000); //Wait the collection thread complete
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));		
	}
	
	@Test
	public void markdownCanbeMarkupTest() throws Exception{
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(5);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1);
		
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		for (int i = 0; i < 10; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000);
		Assert.assertTrue(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
		
		DalStatusManager.getMarkdownStatus().setAutoMarkUpVolume(1);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpSchedule("3");
		
		Thread.sleep(2000); //Wait auto mark up delay
		for (int i = 0; i < MarkupPhase.length; i++) {
			if(i >= 7)
				Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
			else{
				Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
			}
		}
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		Assert.assertFalse(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
	}
	
	@Test
	public void markdownDalayCantBeMarkupTest() throws Exception{
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(5);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(1);
		
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		for (int i = 0; i < 10; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000);
		Assert.assertTrue(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
		
		DalStatusManager.getMarkdownStatus().setAutoMarkUpVolume(1);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpDelay(10);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkDown(true);
		DalStatusManager.getMarkdownStatus().setAutoMarkUpSchedule("3");
		
		for (int i = 0; i < MarkupPhase.length; i++) {
			Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		}

		Assert.assertTrue(DalStatusManager.getMarkdownStatus().isMarkdown(dbName));
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
		
		EasyMock.replay(meta, conn);
		return conn;
	}
}
