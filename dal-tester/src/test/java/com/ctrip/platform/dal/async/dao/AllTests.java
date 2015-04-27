package com.ctrip.platform.dal.async.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BatchDeleteSp3AsyncTaskTest.class, BatchInsertSp3AsyncTaskTest.class,
		BatchUpdateSp3AsyncTaskTest.class, CtripTableSpAsyncDaoTest.class,
		SingleDeleteSpaAsyncTaskTest.class, SingleInsertSpaAsyncTaskTest.class,
		SingleUpdateSpaAsyncTaskTest.class })
public class AllTests {

}
