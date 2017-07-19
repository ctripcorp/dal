package test.com.ctrip.platform.dal.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	test.com.ctrip.platform.dal.dao.client.AllTest.class,
	test.com.ctrip.platform.dal.dao.datasource.AllTests.class,
	test.com.ctrip.platform.dal.dao.dialet.mysql.MySqlHelperTest.class,
	test.com.ctrip.platform.dal.dao.helper.AllTests.class,
	test.com.ctrip.platform.dal.dao.parser.AllTest.class,
	test.com.ctrip.platform.dal.dao.shard.AllTest.class,
	test.com.ctrip.platform.dal.dao.sqlbuilder.AllTests.class,
	test.com.ctrip.platform.dal.dao.task.AllTest.class,
	test.com.ctrip.platform.dal.dao.unittests.AllTest.class,
    test.com.ctrip.platform.dal.dao.markdown.AllTests.class,
    test.com.ctrip.platform.dal.dao.annotation.AllTest.class,

})
public class AllTest {}