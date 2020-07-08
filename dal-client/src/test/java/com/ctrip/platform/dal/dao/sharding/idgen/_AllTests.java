package com.ctrip.platform.dal.dao.sharding.idgen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        IdGeneratorConfigTest.class,
        IdGeneratorFactoryManagerTest.class,
})
public class _AllTests {

}
