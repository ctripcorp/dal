package com.ctrip.platform.dal.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    com.ctrip.platform.dal.dao.callByIndex._AllTests.class,
    com.ctrip.platform.dal.dao.callByName._AllTests.class,
    com.ctrip.platform.dal.dao.callByNativeSyntax._AllTests.class,
    com.ctrip.platform.dal.dao.callBySpt._AllTests.class,
    
	CallSpByIndexValidatorTest.class,
	
	CtripTaskFactoryTest.class,

	DaoInitializationTest.class,
    
    //TVP must be tested at last to avoid spoil old sp3 batch
    TVPTest.class,
})
public class _AllTests {}
