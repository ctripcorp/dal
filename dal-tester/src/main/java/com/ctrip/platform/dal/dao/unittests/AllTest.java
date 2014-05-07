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
	DalQueryDaoMySqlTest.class,
	DalTabelDaoMySqlTest.class
})
public class AllTest {}
