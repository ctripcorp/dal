package com.ctrip.platform.dal.dao.callByIndex;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

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
public class _AllTests {}
