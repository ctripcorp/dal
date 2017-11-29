package test.com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureParser;
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
        DataSourceConfigure config = DataSourceConfigureLocator.getInstance().getUserDataSourceConfigure("dao_test");
        Assert.assertEquals("dao_test", config.getName());
        Assert.assertEquals(10000, config.getIntProperty(DataSourceConfigureConstants.MAXWAIT, 0));
        Assert.assertEquals(
                "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8",
                config.getProperty(DataSourceConfigureConstants.OPTION));
    }

    @Test
    public void test2() {
        DataSourceConfigure config =
                DataSourceConfigureLocator.getInstance().getUserDataSourceConfigure("dao_test_select");
        Assert.assertEquals("dao_test_select", config.getName());
        Assert.assertEquals(true, config.getBooleanProperty(DataSourceConfigureConstants.TESTWHILEIDLE, false));
        Assert.assertEquals(true, config.getBooleanProperty(DataSourceConfigureConstants.TESTONBORROW, false));
        Assert.assertEquals("SELECT 1", config.getProperty(DataSourceConfigureConstants.VALIDATIONQUERY));
        Assert.assertEquals(30000, config.getIntProperty(DataSourceConfigureConstants.VALIDATIONINTERVAL, 0));
        Assert.assertEquals(30000,
                config.getIntProperty(DataSourceConfigureConstants.TIMEBETWEENEVICTIONRUNSMILLIS, 0));
        Assert.assertEquals(100, config.getIntProperty(DataSourceConfigureConstants.MAXACTIVE, 0));
        Assert.assertEquals(10, config.getIntProperty(DataSourceConfigureConstants.MINIDLE, 0));
        Assert.assertEquals(1000, config.getIntProperty(DataSourceConfigureConstants.MAXWAIT, 0));
        Assert.assertEquals(10, config.getIntProperty(DataSourceConfigureConstants.INITIALSIZE, 0));
        Assert.assertEquals(60, config.getIntProperty(DataSourceConfigureConstants.REMOVEABANDONEDTIMEOUT, 0));
        Assert.assertEquals(true, config.getBooleanProperty(DataSourceConfigureConstants.REMOVEABANDONED, false));
        Assert.assertEquals(true, config.getBooleanProperty(DataSourceConfigureConstants.LOGABANDONED, false));
        Assert.assertEquals(30000, config.getIntProperty(DataSourceConfigureConstants.MINEVICTABLEIDLETIMEMILLIS, 0));
        Assert.assertEquals("rewriteBatchedStatements=true;allowMultiQueries=true",
                config.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES));
    }

    @Test
    public void test3() {
        DataSourceConfigure config =
                DataSourceConfigureLocator.getInstance().getUserDataSourceConfigure("dal_test_new");
        Assert.assertEquals("dal_test_new", config.getName());
        Assert.assertEquals(10000, config.getIntProperty(DataSourceConfigureConstants.MAXWAIT, 0));
        Assert.assertEquals("sendTimeAsDateTime=false", config.getProperty(DataSourceConfigureConstants.OPTION));

        // Test default settings,now default value is zero
        Assert.assertEquals(0, config.getIntProperty(DataSourceConfigureConstants.MAX_AGE, 0));
    }

    @Test
    public void test4() {
        DataSourceConfigure config =
                DataSourceConfigureLocator.getInstance().getUserDataSourceConfigure("dao_test_select");
        Assert.assertEquals("dao_test_select", config.getName());
        Assert.assertEquals(1000, config.getIntProperty(DataSourceConfigureConstants.MAXWAIT, 0));
        Assert.assertEquals("rewriteBatchedStatements=true;allowMultiQueries=true",
                config.getProperty(DataSourceConfigureConstants.CONNECTIONPROPERTIES));
    }

}
