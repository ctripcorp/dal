package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.*;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabasePoolConfigParserTest {

    @Before
    public void setUp() throws Exception {
        DataSourceConfigureParser.getInstance();
    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void test() {
        String location = DataSourceConfigureParser.getInstance().getDatabaseConfigLocation();
        Assert.assertEquals("$classpath", location);
    }

    @Test
    public void test1() {
        DalPoolPropertiesConfigure configure =
                DataSourceConfigureLocatorManager.getInstance().getUserPoolPropertiesConfigure("dao_test");
        Assert.assertEquals(10000, configure.getMaxWait().intValue());
        Assert.assertEquals(
                "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8",
                configure.getOption());
    }

    @Test
    public void test2() {
        PoolPropertiesConfigure configure =
                DataSourceConfigureLocatorManager.getInstance().getUserPoolPropertiesConfigure("dao_test_select");
        Assert.assertEquals(true, configure.getTestWhileIdle().booleanValue());
        Assert.assertEquals(true, configure.getTestOnBorrow().booleanValue());
        Assert.assertEquals("SELECT 1", configure.getValidationQuery());
        Assert.assertEquals(30000, configure.getValidationInterval().intValue());
        Assert.assertEquals(30000,
                configure.getTimeBetweenEvictionRunsMillis().intValue());
        Assert.assertEquals(100, configure.getMaxActive().intValue());
        Assert.assertEquals(10, configure.getMinIdle().intValue());
        Assert.assertEquals(1000, configure.getMaxWait().intValue());
        Assert.assertEquals(10, configure.getInitialSize().intValue());
        Assert.assertEquals(60, configure.getRemoveAbandonedTimeout().intValue());
        Assert.assertEquals(true, configure.getRemoveAbandoned().booleanValue());
        Assert.assertEquals(true, configure.getLogAbandoned().booleanValue());
        Assert.assertEquals(30000,
                configure.getMinEvictableIdleTimeMillis().intValue());
        Assert.assertEquals("rewriteBatchedStatements=true;allowMultiQueries=true",
                configure.getConnectionProperties());
    }

    @Test
    public void test3() {
        PoolPropertiesConfigure configure =
                DataSourceConfigureLocatorManager.getInstance().getUserPoolPropertiesConfigure("dal_test_new");
        Assert.assertEquals(10000, configure.getMaxWait().intValue());
        Assert.assertEquals("sendTimeAsDateTime=false", ((DalPoolPropertiesConfigure) configure).getOption());

        // Test default settings,now default value is zero
        Assert.assertEquals(0, ((DataSourceConfigure)configure).getIntProperty(DataSourceConfigureConstants.MAX_AGE,0));
    }

    @Test
    public void test4() {
        PoolPropertiesConfigure configure =
                DataSourceConfigureLocatorManager.getInstance().getUserPoolPropertiesConfigure("dao_test_select");
        Assert.assertEquals(1000, configure.getMaxWait().intValue());
        Assert.assertEquals("rewriteBatchedStatements=true;allowMultiQueries=true",
                configure.getConnectionProperties());
    }

}
