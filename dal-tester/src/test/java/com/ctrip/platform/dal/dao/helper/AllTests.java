package com.ctrip.platform.dal.dao.helper;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DalFirstResultMergerTest.class,
	DalSingleResultMergerTest.class,
	DalTableDaoUnitTest.class,
	DalQueryDaoUnitTest.class,
})
public class AllTests {}
