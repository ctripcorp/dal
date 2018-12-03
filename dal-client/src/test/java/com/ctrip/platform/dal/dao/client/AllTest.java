package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.dao.client.DalCommand.DalCommandTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
		ConnectionActionTest.class,
		DalConnectionManagerTest.class,
		DalConnectionTest.class,
		DalTransactionManagerTest.class,
		DalTransactionTest.class,
		DalShardingInTransactionTest.class,
		DalShardingHelperTest.class,
		DalConfigureFactoryTest.class,
		DalCommandTest.class,
		LogSamplingTest.class
})
public class AllTest {

}