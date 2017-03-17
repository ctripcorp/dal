package test.com.ctrip.platform.dal.dao.task;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class UpdateSqlTaskOracleTest extends UpdateSqlTaskTestStub {
	public UpdateSqlTaskOracleTest() {
		super(initializer.DATABASE_NAME);
	}

	private static OracleTestInitializer initializer = new OracleTestInitializer();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		initializer.setUpBeforeClass();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		initializer.tearDownAfterClass();
	}

	@Before
	public void setUp() throws Exception {
		initializer.setUp();
	}

	@After
	public void tearDown() throws Exception {
		initializer.tearDown();
	}
}
