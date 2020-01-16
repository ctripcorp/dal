package com.ctrip.platform.dal.dao.unittests;

import com.ctrip.platform.dal.dao.unitbase.OracleDatabaseInitializer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class DalTableDaoOracleTest extends DalTableDaoTestStub {
	private static OracleDatabaseInitializer initializer = new OracleDatabaseInitializer();

	public DalTableDaoOracleTest() {
		super(initializer.DATABASE_NAME, 
				initializer.diff);
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
