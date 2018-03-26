package test.com.ctrip.platform.dal.dao.annotation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.com.ctrip.platform.dal.dao.annotation.autowire.DalTransactionalValidatorAutoWireTest;
import test.com.ctrip.platform.dal.dao.annotation.beanDefine.DalTransactionalValidatorTest;
import test.com.ctrip.platform.dal.dao.annotation.normal.BaseDalTransactionalAnnotationTest;

@RunWith(Suite.class)
@SuiteClasses({
	DalTransactionalValidatorTest.class,
	DalTransactionalValidatorAutoWireTest.class,	
	DalAnnotationValidatorTest.class,
	
	BaseDalTransactionalAnnotationTest.class,

})
public class AllTest {}
