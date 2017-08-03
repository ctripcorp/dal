package com.ctrip.platform.dal.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    com.ctrip.platform.dal.dao.callByIndex.AllTests.class,
    com.ctrip.platform.dal.dao.callByName.AllTests.class,
    com.ctrip.platform.dal.dao.callByNativeSyntax.AllTests.class,
//    com.ctrip.platform.dal.dao.callBySpt.AllTests.class,
    
	CallSpByIndexValidatorTest.class,
	
	CtripTaskFactoryTest.class,
    
    //TVP must be tested at last to avoid spoil old sp3 batch
    TVPTest.class,
})
public class AllTests {}
