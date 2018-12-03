package com.ctrip.platform.dal.dao.annotation;

import com.ctrip.platform.dal.dao.annotation.autowire.DalTransactionalValidatorAutoWireTest;
import com.ctrip.platform.dal.dao.annotation.beanDefine.DalTransactionalValidatorTest;
import com.ctrip.platform.dal.dao.annotation.normal.BaseDalTransactionalAnnotationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DalTransactionalValidatorTest.class,
	DalTransactionalValidatorAutoWireTest.class,
	DalAnnotationValidatorTest.class,
	
	BaseDalTransactionalAnnotationTest.class,

})
public class AllTest {}
