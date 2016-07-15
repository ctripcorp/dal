package com.ctrip.platform.dal.dao.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctrip.datasource.MetricTest;
import com.ctrip.datasource.configure.AllInOneConfigureReaderTest;
import com.ctrip.datasource.configure.ConnectionStringParserParserTest;
import com.ctrip.datasource.configure.CtripDalDataSourceTest;
import com.ctrip.datasource.titan.TitanServiceReaderTest;
import com.ctrip.platform.dal.dao.BatchDeleteSp3TaskTest;
import com.ctrip.platform.dal.dao.BatchInsertSp3TaskTest;
import com.ctrip.platform.dal.dao.BatchUpdateSp3TaskTest;
import com.ctrip.platform.dal.dao.CtripTableSpDaoTest;
import com.ctrip.platform.dal.dao.SingleDeleteSpaTaskTest;
import com.ctrip.platform.dal.dao.SingleInsertSpaTaskTest;
import com.ctrip.platform.dal.dao.SingleUpdateSpaTaskTest;
import com.ctrip.platform.dal.dao.helper.SQLParserTests;
import com.ctrip.platform.dal.sql.logging.CommonUtilTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	BatchDeleteSp3TaskTest.class,
	BatchInsertSp3TaskTest.class, 
	BatchUpdateSp3TaskTest.class, 
	SingleInsertSpaTaskTest.class, 
	SingleDeleteSpaTaskTest.class, 
	SingleUpdateSpaTaskTest.class, 
	CtripTableSpDaoTest.class,
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
