package com.ctrip.datasource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctrip.datasource.configure.AllInOneConfigureReaderTest;
import com.ctrip.datasource.configure.ConnectionStringParserParserTest;
import com.ctrip.datasource.configure.CtripDalDataSourceTest;
import com.ctrip.datasource.titan.TitanServiceReaderTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	TitanServiceReaderTest.class,
	AllInOneConfigureReaderTest.class,
	ConnectionStringParserParserTest.class,
	CtripDalDataSourceTest.class,
	MetricTest.class
	})
public class AllTests {

}
