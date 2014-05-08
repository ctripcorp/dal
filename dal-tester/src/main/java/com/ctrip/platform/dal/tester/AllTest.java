package com.ctrip.platform.dal.tester;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctrip.platform.dal.dao.unittests.DalDirectClientMySqlTest;
import com.ctrip.platform.dal.dao.unittests.DalDirectClientSqlServerTest;
import com.ctrip.platform.dal.dao.unittests.DalQueryDaoMySqlTest;
import com.ctrip.platform.dal.dao.unittests.DalQueryDaoSqlServerTest;
import com.ctrip.platform.dal.dao.unittests.DalTabelDaoMySqlTest;
import com.ctrip.platform.dal.dao.unittests.DalTableDaoSqlServerTest;
import com.ctrip.platform.dal.parser.DalDefaultJpaParserMySqlTest;
import com.ctrip.platform.dal.parser.DalDefaultJpaParserSqlServerTest;

/**
 * The Junit loader class
 * @author wcyuan
 * @version 2014-05-05
 */
@RunWith(Suite.class)
@SuiteClasses({
	DalDirectClientMySqlTest.class,
	DalDirectClientSqlServerTest.class,
	DalQueryDaoMySqlTest.class,
	DalQueryDaoSqlServerTest.class,
	DalTabelDaoMySqlTest.class,
	DalTableDaoSqlServerTest.class,
	DalDefaultJpaParserMySqlTest.class,
	DalDefaultJpaParserSqlServerTest.class
})
public class AllTest {}
