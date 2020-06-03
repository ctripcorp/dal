package com.ctrip.platform.dal.cluster;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    PersonDaoUnitTest.class,
    RWClusterQueryDaoTest.class,
    ThreadPoolLimitTest.class
})
public class AllTests {}
