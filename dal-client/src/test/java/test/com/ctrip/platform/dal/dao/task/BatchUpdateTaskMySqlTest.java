package test.com.ctrip.platform.dal.dao.task;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class BatchUpdateTaskMySqlTest extends BatchUpdateTaskTestStub {
	public BatchUpdateTaskMySqlTest() {
		super(MySqlTestInitializer.DATABASE_NAME_MYSQL);
		setMySql(true);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MySqlTestInitializer.setUpBeforeClass();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MySqlTestInitializer.tearDownAfterClass();
	}

	@Before
	public void setUp() throws Exception {
		MySqlTestInitializer.setUp();
	}

	@After
	public void tearDown() throws Exception {
		MySqlTestInitializer.tearDown();
	}
}
