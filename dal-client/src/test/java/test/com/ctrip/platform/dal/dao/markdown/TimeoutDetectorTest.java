package test.com.ctrip.platform.dal.dao.markdown;

import java.sql.SQLException;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.markdown.ErrorContext;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.markdown.TimeoutDetector;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;

public class TimeoutDetectorTest {
	
	private Random random = new Random();
	private String dbName = "dao_test";
	
	public static void setUpBeforeClass() {
		try {
			DalClientFactory.initClientFactory();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Before
	public void setUp() throws Exception {
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(10000);
		DalStatusManager.getTimeoutMarkdown().setErrorPercentReferCount(10000);
		DalStatusManager.getTimeoutMarkdown().setErrorPercentThreshold(1f);
		DalStatusManager.getTimeoutMarkdown().setSamplingDuration(10000);
		MarkdownManager.resetAutoMarkdowns();
	}
	
	@Test
	public void countBaseLineMatchTest() {
		TimeoutDetector detector = new TimeoutDetector();
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(5);
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
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}
	
	
	@Test
	public void countBaseLineMatchButOverdueTest() {
		TimeoutDetector detector = new TimeoutDetector();
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		DalStatusManager.getTimeoutMarkdown().setErrorCountThreshold(5);
		DalStatusManager.getTimeoutMarkdown().setSamplingDuration(1);
		for (int i = 0; i < 10; i++) {
			SQLException e = this.mockNotTimeoutException();
			DatabaseCategory ct = DatabaseCategory.MySql;
			if(i % 2 == 0){
				ct = random.nextBoolean() ? DatabaseCategory.MySql : DatabaseCategory.SqlServer;
				e = this.mockTimeoutException(ct);
			}
			if(i == 4){
				try {
					Thread.sleep(1100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			ErrorContext ctx = new ErrorContext(dbName, ct, 1000, e);
			detector.detect(ctx);
		}
		Assert.assertFalse(MarkdownManager.isMarkdown(dbName));
	}
	
	@Test
	public void errorPercentMatchTest(){
		TimeoutDetector detector = new TimeoutDetector();
		DalStatusManager.getTimeoutMarkdown().setEnabled(true);
		DalStatusManager.getMarkdownStatus().setEnableAutoMarkdown(true);
		DalStatusManager.getTimeoutMarkdown().setErrorPercentReferCount(10);
		DalStatusManager.getTimeoutMarkdown().setErrorPercentThreshold(0.5f);
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
		Assert.assertTrue(MarkdownManager.isMarkdown(dbName));
	}
	
	@Test
	public void isTimeOutExceptionTest(){
		DatabaseCategory ct = DatabaseCategory.MySql;
		SQLException e = this.mockTimeoutException(ct);
		ErrorContext ctx = new ErrorContext(dbName, ct, 10000, e);
		Assert.assertTrue(TimeoutDetector.isTimeOutException(ctx));
		
		ct = DatabaseCategory.SqlServer;
		e = this.mockTimeoutException(ct);
		ctx = new ErrorContext(dbName, ct, 10000, e);
		Assert.assertTrue(TimeoutDetector.isTimeOutException(ctx));
		
		ct = DatabaseCategory.SqlServer;
		e = new SQLException("查询超时");
		ctx = new ErrorContext(dbName, ct, 10000, e);
		Assert.assertTrue(TimeoutDetector.isTimeOutException(ctx));
		
		ct = DatabaseCategory.MySql;
		e = this.mockNotTimeoutException();
		ctx = new ErrorContext(dbName, ct, 10000, e);
		Assert.assertFalse(TimeoutDetector.isTimeOutException(ctx));
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
