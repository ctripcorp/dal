package com.ctrip.platform.dal.tester.baseDao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DalQueryDaoTest.class,
	DalTableDaoTest.class,
	DirectClientDaoShardTest.class,
})
public class AllTest {}