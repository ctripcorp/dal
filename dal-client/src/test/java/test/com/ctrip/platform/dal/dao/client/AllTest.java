package test.com.ctrip.platform.dal.dao.client;

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
	DalShardingHelperTest.class,
	DalConfigureFactoryTest.class,
})
public class AllTest {

}
