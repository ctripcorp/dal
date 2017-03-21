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
})
public class AllTests {}
