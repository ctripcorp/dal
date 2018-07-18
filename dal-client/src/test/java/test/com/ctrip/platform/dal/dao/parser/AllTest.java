package test.com.ctrip.platform.dal.dao.parser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DalDefaultJpaParserMySqlTest.class,
	DalDefaultJpaParserSqlServerTest.class,
	DalDefaultJpaParserMySqlTest2.class,
	DalDefaultJpaParserSqlServerTest2.class,
	DalDefaultJpaParserTest.class
})
public class AllTest {}
