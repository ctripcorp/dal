package test.com.ctrip.platform.dal.dao.unittests;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.MySqlDatabaseInitializer;

public class DalQueryDaoMySqlTest extends DalQueryDaoTestStub {
	private static MySqlDatabaseInitializer initializer = new MySqlDatabaseInitializer();
	public DalQueryDaoMySqlTest() {
		super(initializer.DATABASE_NAME, initializer.diff);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		initializer.setUpBeforeClass();
		DalQueryDaoTestStub.prepareData(initializer.DATABASE_NAME);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		initializer.tearDownAfterClass();
	}
}
