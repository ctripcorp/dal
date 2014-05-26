package com.ctrip.platform.dal.tester.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DalConnectionManagerTest.class,
	DalTransactionManagerTest.class
})
public class AllTest {

}
