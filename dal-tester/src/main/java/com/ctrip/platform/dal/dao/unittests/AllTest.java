package com.ctrip.platform.dal.dao.unittests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

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
	DalTableDaoSqlServerTest.class
})
public class AllTest {}
