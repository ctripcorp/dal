package com.ctrip.platform.dal.dao.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctrip.datasource.MetricTest;
import com.ctrip.datasource.configure.AllInOneConfigureReaderTest;
import com.ctrip.datasource.configure.ConnectionStringParserParserTest;
import com.ctrip.datasource.configure.CtripDalDataSourceTest;
import com.ctrip.datasource.titan.TitanServiceReaderTest;
import com.ctrip.platform.dal.dao.helper.SQLParserTests;
import com.ctrip.platform.dal.sql.logging.CommonUtilTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	com.ctrip.platform.dal.dao.AllTests.class,
	SQLParserTests.class,
	CommonUtilTest.class,
	TitanServiceReaderTest.class,
	AllInOneConfigureReaderTest.class,
	ConnectionStringParserParserTest.class,
	CtripDalDataSourceTest.class,
	MetricTest.class
	})
public class AllTests {

}
