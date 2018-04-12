package test.com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocatorManager;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;
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
        PoolPropertiesConfigure configure =
                DataSourceConfigureLocatorManager.getInstance().getUserPoolPropertiesConfigure("dao_test");
        Assert.assertEquals(10000, configure.getIntProperty(DataSourceConfigureConstants.MAXWAIT, 0));
        Assert.assertEquals(
                "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8",
                configure.getProperty(DataSourceConfigureConstants.OPTION));
    }

    @Test
    public void test2() {
        PoolPropertiesConfigure configure =
                DataSourceConfigureLocatorManager.getInstance().getUserPoolPropertiesConfigure("dao_test_select");
        Assert.assertEquals(true, configure.getBooleanProperty(DataSourceConfigureConstants.TESTWHILEIDLE, false));
        Assert.assertEquals(true, configure.getBooleanProperty(DataSourceConfigureConstants.TESTONBORROW, false));
        Assert.assertEquals("SELECT 1", configure.getProperty(DataSourceConfigureConstants.VALIDATIONQUERY));
        Assert.assertEquals(30000, configure.getIntProperty(DataSourceConfigureConstants.VALIDATIONINTERVAL, 0));
        Assert.assertEquals(30000,
                configure.getIntProperty(DataSourceConfigureConstants.TIMEBETWEENEVICTIONRUNSMILLIS, 0));
        Assert.assertEquals(100, configure.getIntProperty(DataSourceConfigureConstants.MAXACTIVE, 0));
        Assert.assertEquals(10, configure.getIntProperty(DataSourceConfigureConstants.MINIDLE, 0));
        Assert.assertEquals(1000, configure.getIntProperty(DataSourceConfigureConstants.MAXWAIT, 0));
        Assert.assertEquals(10, configure.getIntProperty(DataSourceConfigureConstants.INITIALSIZE, 0));
        Assert.assertEquals(60, configure.getIntProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT, 0));
        Assert.assertEquals(true, configure.getBooleanProperty(DataSourceConfigureConstants.REMOVEABANDONED, false));
        Assert.assertEquals(true, configure.getBooleanProperty(DataSourceConfigureConstants.LOGABANDONED, false));
        Assert.assertEquals(30000,
                configure.getIntProperty(DataSourceConfigureConstants.MINEVICTABLEIDLETIMEMILLIS, 0));
        Assert.assertEquals("rewriteBatchedStatements=true;allowMultiQueries=true",
                configure.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES));
    }

    @Test
    public void test3() {
        PoolPropertiesConfigure configure =
                DataSourceConfigureLocatorManager.getInstance().getUserPoolPropertiesConfigure("dal_test_new");
        Assert.assertEquals(10000, configure.getIntProperty(DataSourceConfigureConstants.MAXWAIT, 0));
        Assert.assertEquals("sendTimeAsDateTime=false", configure.getProperty(DataSourceConfigureConstants.OPTION));

        // Test default settings,now default value is zero
        Assert.assertEquals(0, configure.getIntProperty(DataSourceConfigureConstants.MAX_AGE, 0));
    }

    @Test
    public void test4() {
        PoolPropertiesConfigure configure =
                DataSourceConfigureLocatorManager.getInstance().getUserPoolPropertiesConfigure("dao_test_select");
        Assert.assertEquals(1000, configure.getIntProperty(DataSourceConfigureConstants.MAXWAIT, 0));
        Assert.assertEquals("rewriteBatchedStatements=true;allowMultiQueries=true",
                configure.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES));
    }

}
