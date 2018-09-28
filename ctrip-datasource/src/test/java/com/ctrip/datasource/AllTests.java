package com.ctrip.datasource;

import com.ctrip.datasource.dynamicdatasource.DalDataSourceFactoryTest;
import com.ctrip.datasource.dynamicdatasource.QConfigConnectionStringProvider.QConfigConnectionStringProviderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctrip.datasource.configure.AllInOneConfigureReaderTest;
import com.ctrip.datasource.configure.ConnectionStringParserParserTest;
import com.ctrip.datasource.configure.CtripDalDataSourceTest;
import com.ctrip.datasource.mybatis.interceptor.ExecutorInterceptorTest;
import com.ctrip.datasource.mybatis.interceptor.StatementHandlerInterceptorTest;
import com.ctrip.datasource.mybatis.interceptor.StatementPrepareHandlerInterceptorTest;
import com.ctrip.datasource.spring.JavaConfigMybatisTest;
import com.ctrip.datasource.spring.XmlConfigMybatisTest;
import com.ctrip.datasource.titan.TitanServiceReaderTest;
import com.ctrip.datasource.util.DalEncrypterTest;
import com.ctrip.datasource.dynamicdatasource.DalPropertiesChangedTest;

@RunWith(Suite.class)
@SuiteClasses({
		TitanServiceReaderTest.class,
		AllInOneConfigureReaderTest.class,
		ConnectionStringParserParserTest.class,
		CtripDalDataSourceTest.class,
		MetricTest.class,
  		DalEncrypterTest.class,
		JavaConfigMybatisTest.class,
  		XmlConfigMybatisTest.class,
  		ExecutorInterceptorTest.class,
  		StatementHandlerInterceptorTest.class,
  		StatementPrepareHandlerInterceptorTest.class,
//		DataSourceConfigureLocatorTest.class,
		DalDataSourceFactoryTest.class,
		QConfigConnectionStringProviderTest.class,
		DalPropertiesChangedTest.class
	})

public class AllTests {

}
