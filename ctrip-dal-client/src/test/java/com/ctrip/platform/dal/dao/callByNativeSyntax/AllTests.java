package com.ctrip.platform.dal.dao.callByNativeSyntax;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctrip.platform.dal.dao.CtripTaskFactoryOptionSetter;

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
public class AllTests {
    @BeforeClass
    public static void tearDownAfterClass() throws Exception {
        CtripTaskFactoryOptionSetter.callSpByNativeSyntax();
    }
}
