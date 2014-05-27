package com.ctrip.platform.dal.tester;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.ctrip.platform.dal.dao.unittests.AllTest.class,
	com.ctrip.platform.dal.parser.AllTest.class,
	com.ctrip.platform.dal.tester.client.AllTest.class
})
public class AllTest {}
