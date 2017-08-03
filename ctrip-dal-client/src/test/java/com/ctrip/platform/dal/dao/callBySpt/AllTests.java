package com.ctrip.platform.dal.dao.callBySpt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctrip.platform.dal.dao.callByName.SingleDeleteTest;
import com.ctrip.platform.dal.dao.callByName.SingleInsertTest;
import com.ctrip.platform.dal.dao.callByName.SingleUpdateTest;

@RunWith(Suite.class)
@SuiteClasses({
    BatchDeleteTest.class,
    BatchInsertTest.class, 
    BatchUpdateTest.class, 

    SingleDeleteTest.class,
    SingleInsertTest.class,
    SingleUpdateTest.class,

    CtripTableSpDaoTest.class,
})
public class AllTests {}
