package test.com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.markdown.MarkupPhase;
import com.ctrip.platform.dal.dao.status.DalStatusManager;

public class ManualMarkDownTest {

	private static final String dbName = "dao_test";

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			DalClientFactory.initClientFactory();
			MarkdownManager.resetAutoMarkdowns();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void appMarkdownTest(){
		DalStatusManager.getMarkdownStatus().setAppMarkdown(true);
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
		DalStatusManager.getMarkdownStatus().setAppMarkdown(false);
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
		
		DalStatusManager.getMarkdownStatus().setAutoMarkupDelay(1);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
		
		Thread.sleep(2000);
		
		for (int i = 0; i < MarkupPhase.length * 2 + 1; i++) {
			MarkdownManager.isMarkdown(dbName);
		}
		
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}

	@Test
	public void logicDbMarkdownTest(){
		String logicDb = "dao_test_sqlsvr_tableShard_simple";
		
		DalStatusManager.getDatabaseSetStatus(logicDb).setMarkdown(true);
		DalClient client = DalClientFactory.getClient(logicDb);
		try {
			client.query("select 1", new StatementParameters(), new DalHints(), new DalScalarExtractor());
			Assert.fail();
		} catch (SQLException e) {
		}
		
		DalStatusManager.getDatabaseSetStatus(logicDb).setMarkdown(false);
		try {
			client.query("select 1", new StatementParameters(), new DalHints(), new DalScalarExtractor());
		} catch (SQLException e) {
			Assert.fail();
		}
	}	
}
