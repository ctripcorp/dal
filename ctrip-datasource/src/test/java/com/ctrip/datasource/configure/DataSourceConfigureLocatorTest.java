package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocatorManager;
import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class DataSourceConfigureLocatorTest implements DataSourceConfigureConstants {
    private String ipConnectionString =
            "Server=10.32.21.149;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_1;version=2";

    private String domainConnectionString =
            "Server=DST56614;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_1;version=2";

    @Test
    public void testSetAndGetIPDomainStatus() {
        DataSourceConfigureLocator locator = DataSourceConfigureLocatorManager.getInstance();
        IPDomainStatus ipStatus = getIPStatus();
        locator.setIPDomainStatus(ipStatus);
        IPDomainStatus ipStatus2 = locator.getIPDomainStatus();
        Assert.assertEquals(ipStatus, ipStatus2);

        IPDomainStatus domainStatus = getDomainStatus();
        locator.setIPDomainStatus(domainStatus);
        IPDomainStatus domainStatus2 = locator.getIPDomainStatus();
        Assert.assertEquals(domainStatus, domainStatus2);
    }

    @Test
    public void testSetAndGetDataSourceConfigureKeySet() {
        DataSourceConfigureLocator locator = DataSourceConfigureLocatorManager.getInstance();
        Set<String> names = getKeySet();
        locator.addDataSourceConfigureKeySet(names);
        Set<String> names2 = locator.getDataSourceConfigureKeySet();
        Assert.assertEquals(names, names2);
    }

    @Test
    public void testAddAndGetUserPoolPropertiesConfigure() {
        String name = "test";
        DataSourceConfigureLocator locator = DataSourceConfigureLocatorManager.getInstance();
        Properties p = getProperties();
        DataSourceConfigure configure = new DataSourceConfigure(name, p);
        locator.addUserPoolPropertiesConfigure(name, configure);
        PoolPropertiesConfigure ppc = locator.getUserPoolPropertiesConfigure(name);
        Assert.assertEquals(p.getProperty(TESTWHILEIDLE), ppc.getProperty(TESTWHILEIDLE));
        Assert.assertEquals(p.getProperty(TESTONBORROW), ppc.getProperty(TESTONBORROW));
        Assert.assertEquals(p.getProperty(TESTONRETURN), ppc.getProperty(TESTONRETURN));
        Assert.assertEquals(p.getProperty(VALIDATIONQUERY), ppc.getProperty(VALIDATIONQUERY));
        Assert.assertEquals(p.getProperty(VALIDATIONINTERVAL), ppc.getProperty(VALIDATIONINTERVAL));
        Assert.assertEquals(p.getProperty(VALIDATORCLASSNAME), ppc.getProperty(VALIDATORCLASSNAME));
        Assert.assertEquals(p.getProperty(TIMEBETWEENEVICTIONRUNSMILLIS),
                ppc.getProperty(TIMEBETWEENEVICTIONRUNSMILLIS));
        Assert.assertEquals(p.getProperty(MAXACTIVE), ppc.getProperty(MAXACTIVE));
        Assert.assertEquals(p.getProperty(MINIDLE), ppc.getProperty(MINIDLE));
        Assert.assertEquals(p.getProperty(MAXWAIT), ppc.getProperty(MAXWAIT));
        Assert.assertEquals(p.getProperty(MAX_AGE), ppc.getProperty(MAX_AGE));
        Assert.assertEquals(p.getProperty(INITIALSIZE), ppc.getProperty(INITIALSIZE));
        Assert.assertEquals(p.getProperty(REMOVEABANDONEDTIMEOUT), ppc.getProperty(REMOVEABANDONEDTIMEOUT));
        Assert.assertEquals(p.getProperty(REMOVEABANDONED), ppc.getProperty(REMOVEABANDONED));
        Assert.assertEquals(p.getProperty(LOGABANDONED), ppc.getProperty(LOGABANDONED));
        Assert.assertEquals(p.getProperty(MINEVICTABLEIDLETIMEMILLIS), ppc.getProperty(MINEVICTABLEIDLETIMEMILLIS));
        Assert.assertEquals(p.getProperty(CONNECTIONPROPERTIES), ppc.getProperty(CONNECTIONPROPERTIES));
        Assert.assertEquals(p.getProperty(JDBC_INTERCEPTORS), ppc.getProperty(JDBC_INTERCEPTORS));
    }

    @Test
    public void testSetAndGetDataSourceConfigure() {
        String name = "test";
        DataSourceConfigureLocator locator = DataSourceConfigureLocatorManager.getInstance();
        ConnectionString connectionString = new ConnectionString(name, ipConnectionString, domainConnectionString);
        Map<String, ConnectionString> map = new HashMap<>();
        map.put(name, connectionString);
        locator.setConnectionStrings(map);
        Properties p = getProperties();
        DataSourceConfigure poolPropertiesConfigure = new DataSourceConfigure(name, p);
        locator.setPoolProperties(poolPropertiesConfigure);

        DataSourceConfigure configure = locator.getDataSourceConfigure(name);
        ConnectionString connectionString2 = configure.getConnectionString();
        Properties p2 = configure.getProperties();

        Assert.assertEquals(connectionString, connectionString2);

        Assert.assertEquals(p.getProperty(TESTWHILEIDLE), p2.getProperty(TESTWHILEIDLE));
        Assert.assertEquals(p.getProperty(TESTONBORROW), p2.getProperty(TESTONBORROW));
        Assert.assertEquals(p.getProperty(TESTONRETURN), p2.getProperty(TESTONRETURN));
        Assert.assertEquals(p.getProperty(VALIDATIONQUERY), p2.getProperty(VALIDATIONQUERY));
        Assert.assertEquals(p.getProperty(VALIDATIONINTERVAL), p2.getProperty(VALIDATIONINTERVAL));
        Assert.assertEquals(p.getProperty(VALIDATORCLASSNAME), p2.getProperty(VALIDATORCLASSNAME));
        Assert.assertEquals(p.getProperty(TIMEBETWEENEVICTIONRUNSMILLIS),
                p2.getProperty(TIMEBETWEENEVICTIONRUNSMILLIS));
        Assert.assertEquals(p.getProperty(MAXACTIVE), p2.getProperty(MAXACTIVE));
        Assert.assertEquals(p.getProperty(MINIDLE), p2.getProperty(MINIDLE));
        Assert.assertEquals(p.getProperty(MAXWAIT), p2.getProperty(MAXWAIT));
        Assert.assertEquals(p.getProperty(MAX_AGE), p2.getProperty(MAX_AGE));
        Assert.assertEquals(p.getProperty(INITIALSIZE), p2.getProperty(INITIALSIZE));
        Assert.assertEquals(p.getProperty(REMOVEABANDONEDTIMEOUT), p2.getProperty(REMOVEABANDONEDTIMEOUT));
        Assert.assertEquals(p.getProperty(REMOVEABANDONED), p2.getProperty(REMOVEABANDONED));
        Assert.assertEquals(p.getProperty(LOGABANDONED), p2.getProperty(LOGABANDONED));
        Assert.assertEquals(p.getProperty(MINEVICTABLEIDLETIMEMILLIS), p2.getProperty(MINEVICTABLEIDLETIMEMILLIS));
        Assert.assertEquals(p.getProperty(CONNECTIONPROPERTIES), p2.getProperty(CONNECTIONPROPERTIES));
        Assert.assertEquals(p.getProperty(JDBC_INTERCEPTORS), p2.getProperty(JDBC_INTERCEPTORS));
    }

    private Properties getProperties() {
        Properties p = new Properties();
        p.setProperty(TESTWHILEIDLE, "false");
        p.setProperty(TESTONBORROW, "true");
        p.setProperty(TESTONRETURN, "false");
        p.setProperty(VALIDATIONQUERY, "SELECT 1");
        p.setProperty(VALIDATIONINTERVAL, "30000");
        p.setProperty(VALIDATORCLASSNAME, "com.ctrip.platform.dal.dao.datasource.DataSourceValidator");
        p.setProperty(TIMEBETWEENEVICTIONRUNSMILLIS, "5000");
        p.setProperty(MAXACTIVE, "100");
        p.setProperty(MINIDLE, "0");
        p.setProperty(MAXWAIT, "10000");
        p.setProperty(MAX_AGE, "28000000");
        p.setProperty(INITIALSIZE, "1");
        p.setProperty(REMOVEABANDONEDTIMEOUT, "65");
        p.setProperty(REMOVEABANDONED, "true");
        p.setProperty(LOGABANDONED, "false");
        p.setProperty(MINEVICTABLEIDLETIMEMILLIS, "30000");
        p.setProperty(CONNECTIONPROPERTIES,
                "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false");
        p.setProperty(JDBC_INTERCEPTORS,
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        return p;
    }

    private IPDomainStatus getIPStatus() {
        return IPDomainStatus.IP;
    }

    private IPDomainStatus getDomainStatus() {
        return IPDomainStatus.Domain;
    }

    private Set<String> getKeySet() {
        Set<String> names = new HashSet<>();
        names.add("name1");
        names.add("name2");
        return names;
    }

}
