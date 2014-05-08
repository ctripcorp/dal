package com.ctrip.platform.dal.parser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DalDefaultJpaParserMySqlTest.class,
	DalDefaultJpaParserSqlServerTest.class
})
public class AllTest {}
