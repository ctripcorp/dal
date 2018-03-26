package test.com.ctrip.platform.dal.dao.sqlbuilder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	DeleteSqlBuilderTest.class, 
	SelectSqlBuilderTest.class,
	UpdateSqlBuilderTest.class,
	AbstractBuilderTest.class,
	BaseQueryBuilderTest.class,
	InsertSqlBuilderTest.class,
	AbstractFreeSqlBuilderTest.class,
	ExpressionsTest.class,
	FreeUpdateSqlBuilderTest.class,
	FreeSelectSqlBuilderTest.class,
	AbstractFreeSqlBuilderMeltdownTest.class,
	AbstractFreeSqlBuilderMeltdownWithSpaceSkipDisabledTest.class,
	})
public class AllTests {

}
