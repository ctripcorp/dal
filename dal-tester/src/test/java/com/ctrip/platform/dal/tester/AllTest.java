package com.ctrip.platform.dal.tester;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.ctrip.platform.dal.dao.dialet.test.MySqlHelperTest.class,
	com.ctrip.platform.dal.dao.unittests.AllTest.class,
	com.ctrip.platform.dal.parser.AllTest.class,
	com.ctrip.platform.dal.tester.client.AllTest.class,
	com.ctrip.platform.dal.tester.baseDao.AllTest.class,
	com.ctrip.platform.dal.tester.shard.AllTest.class,
	com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapperTest.class,
	com.ctrip.platform.dal.dao.helper.DalCustomRowMapperTest.class,
	com.ctrip.platform.dal.dao.ha.HATest.class,
	com.ctrip.platform.dal.dao.markdown.AllTests.class,
	com.ctrip.platform.dal.dao.sqlbuilder.AllTests.class,
	com.ctrip.platform.dal.dao.helper.AllTests.class,
	com.ctrip.platform.dal.tester.tasks.AllTest.class,
	com.ctrip.platform.dal.tester.crossShard.AllTest.class,
	com.ctrip.platform.dal.tester.datasource.AllTests.class,
	com.ctrip.platform.dal.codegen.AllTests.class,
	com.ctrip.platform.dal.codegen.v141.AllTests.class,
})
public class AllTest {}
