package com.ctrip.platform.dal.codegen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	PersonDaoUnitTest.class,
	FreePersonDaoDaoUnitTest.class,
})
public class _AllTests {}
