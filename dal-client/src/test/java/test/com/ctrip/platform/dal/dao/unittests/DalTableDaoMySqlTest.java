package test.com.ctrip.platform.dal.dao.unittests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.MySqlDatabaseInitializer;

public class DalTableDaoMySqlTest extends DalTableDaoTestStub {
	private static MySqlDatabaseInitializer initializer = new MySqlDatabaseInitializer();
	public DalTableDaoMySqlTest() {
		super(initializer.DATABASE_NAME, initializer.diff);
	}
	
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
		initializer.setUp2();
	}

	@After
	public void tearDown() throws Exception {
		initializer.tearDown();
	}
}