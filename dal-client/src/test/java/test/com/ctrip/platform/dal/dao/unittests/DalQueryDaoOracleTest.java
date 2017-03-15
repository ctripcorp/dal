package test.com.ctrip.platform.dal.dao.unittests;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import test.com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer;

public class DalQueryDaoOracleTest extends DalQueryDaoTestStub {
	private static OracleDatabaseInitializer initializer = new OracleDatabaseInitializer();
	public DalQueryDaoOracleTest() {
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
