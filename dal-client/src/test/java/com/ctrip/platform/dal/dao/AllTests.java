package com.ctrip.platform.dal.dao;


import com.ctrip.platform.dal.dao.helper.EntityManagerTest.EntityManagerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
		com.ctrip.platform.dal.dao.client.AllTest.class,
		com.ctrip.platform.dal.dao.common.AllTest.class,
		com.ctrip.platform.dal.dao.datasource.AllTests.class,
		com.ctrip.platform.dal.dao.dialet.mysql.MySqlHelperTest.class,
		com.ctrip.platform.dal.dao.helper.AllTests.class,
		com.ctrip.platform.dal.dao.parser.AllTest.class,
		com.ctrip.platform.dal.dao.shard.AllTest.class,
		com.ctrip.platform.dal.dao.sqlbuilder.AllTests.class,
		com.ctrip.platform.dal.dao.task.AllTest.class,
		com.ctrip.platform.dal.dao.unittests.AllTest.class,
		com.ctrip.platform.dal.dao.annotation.AllTest.class,
		com.ctrip.platform.dal.dao.configure.AllTest.class,
        com.ctrip.platform.dal.dao.sharding.idgen.AllTests.class,
        EntityManagerTest.class,
		/**
		 * IMPORTANT NOTE! markdown test must be the last one to avoid interfere other test
		 */
		com.ctrip.platform.dal.dao.markdown.AllTests.class,

})
public class AllTests {}