package test.com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.client.DalConnection;
import com.ctrip.platform.dal.dao.client.DbMeta;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class AutoMarkdownTest {

	private static final String dbName = "dao_test";
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Before
	public void setUp(){
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(false);
		DalStatusManager.getTimeoutMarkdown().setEnabled(false);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(5);
		DalStatusManager.getMarkdownStatus().setAutoMarkupDelay(10);
		MarkdownManager.resetAutoMarkdowns();
	}
	
	@Test
	public void normalMarkdownAndUpTest() throws InterruptedException{
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		for (int i = 0; i < 10; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000); //Wait the collection thread complete
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		Thread.sleep(1000 * 10);
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
	}
	
	@Test
	public void markdownAndDisableAutoMarkdownTest() throws Exception{
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		for (int i = 0; i < 10; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		
		// Disable auto markdown
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(false);
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));

		//Enable again
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		
		for (int i = 0; i < 4; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
			Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		}
		
		MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		Thread.sleep(1000);

		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}
	
	@Test
	public void disableMarkdownAndEnableMarkdown() throws Exception{
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		for (int i = 0; i < 10; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000);
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
		
		for (int i = 0; i < 5; i++) {
			MarkdownManager.detect(this.mockDalConnection(), 1000, this.mockTimeoutException());
		}
		Thread.sleep(1000);
		
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(false);
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
	}

	private SQLException mockTimeoutException(){
		return new MySQLTimeoutException("Test mysql timeout excption");
	}
	
	public DalConnection mockDalConnection(){
		DalConnection conn = EasyMock.createMock(DalConnection.class);
		DbMeta meta = EasyMock.createMock(DbMeta.class);
		EasyMock.expect(meta.getDataBaseKeyName()).andReturn(dbName).times(1);
		EasyMock.expect(meta.getDatabaseCategory()).andReturn(DatabaseCategory.MySql).times(1);
		
		EasyMock.expect(conn.getMeta()).andReturn(meta).times(3);
		
		EasyMock.replay(meta, conn);
		return conn;
	}
}
