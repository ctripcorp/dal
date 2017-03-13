package test.com.ctrip.platform.dal.dao.unittests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer;

/**
 * Unit tests for Dal direct client
 * 
 * @author wcyuan
 * @version 2014-05-04
 */
public class DalDirectClientOracleTest extends DalDirectClientTestStub {
	private static OracleDatabaseInitializer initializer = new OracleDatabaseInitializer();
	public DalDirectClientOracleTest() {
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
		initializer.setUp();
	}

	@After
	public void tearDown() throws Exception {
		initializer.tearDown();
	}
}
