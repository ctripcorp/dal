package test.com.ctrip.platform.dal.dao.helper;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
		DalFirstResultMergerTest.class,
		DalSingleResultMergerTest.class,
		PartialQueryTableDaoUnitTest.class,
		PartialQueryQueryDaoTest.class,
		DalColumnMapRowMapperTest.class,
		DalCustomRowMapperTest.class,
		SQLCompilerTest.class,
		ServiceLoaderHelperTest.class,
		DalBase64Test.class,
		LoggerHelperTest.class
})
public class AllTests {}