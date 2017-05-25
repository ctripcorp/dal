package com.ctrip.platform.dal.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	BatchDeleteSp3TaskTest.class,
	BatchInsertSp3TaskTest.class, 
	BatchUpdateSp3TaskTest.class, 
	SingleInsertSpaTaskTest.class, 
	SingleDeleteSpaTaskTest.class, 
	SingleUpdateSpaTaskTest.class, 
	CtripTableSpDaoTest.class,
	CallSpByIndexValidatorTest.class,
	
	// Here we will set CtripTaskFactory.callSpByName to false to test call sp by index in the following tests
	CtripTaskFactoryTest.class,
    
	// From now on the call sp is by index
	BatchDeleteSp3TaskTest.class,
    BatchInsertSp3TaskTest.class, 
    BatchUpdateSp3TaskTest.class, 
    SingleInsertSpaTaskTest.class, 
    SingleDeleteSpaTaskTest.class, 
    SingleUpdateSpaTaskTest.class, 
	
})
public class AllTests {}
