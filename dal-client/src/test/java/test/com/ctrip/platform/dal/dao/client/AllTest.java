package test.com.ctrip.platform.dal.dao.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import test.com.ctrip.platform.dal.dao.client.DalCommand.DalCommandTest;

@RunWith(Suite.class)
@SuiteClasses({
		ConnectionActionTest.class,
		DalConnectionManagerTest.class,
		DalConnectionTest.class,
		DalTransactionManagerTest.class,
		DalTransactionTest.class,
		DalShardingHelperTest.class,
		DalConfigureFactoryTest.class,
		DalCommandTest.class
})
public class AllTest {

}