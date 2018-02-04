package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataSourceConnectionStringTest {
    private static final String name = "mysqldaltest01db_W";
    private static DataSourceConfigureLocator locator = DataSourceConfigureLocator.getInstance();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testConnectionStringDynamicEnabledFunction() throws Exception {
        DataSourceConfigure configure1 = locator.getDataSourceConfigure(name);
        String userName1 = configure1.getUserName();
        System.out.println(String.format("UserName:%s", userName1));
        String password1 = configure1.getPassword();
        System.out.println(String.format("Password:%s", password1));
        String url1 = configure1.getConnectionUrl();
        System.out.println(String.format("Url:%s", url1));
        String driver1 = configure1.getDriverClass();
        System.out.println(String.format("Driver:%s", driver1));
        String version1 = configure1.getVersion();
        System.out.println(String.format("Version:%s", version1));
        int maxActive1 = configure1.getIntProperty(DataSourceConfigureConstants.MAXACTIVE, 200);
        System.out.println(String.format("maxActive:%s", maxActive1));

        System.out.println("Sleep for 30 seconds.");
        Thread.sleep(120 * 1000);

        DataSourceConfigure configure2 = locator.getDataSourceConfigure(name);
        String userName2 = configure2.getUserName();
        System.out.println(String.format("UserName:%s", userName2));
        String password2 = configure2.getPassword();
        System.out.println(String.format("Password:%s", password2));
        String url2 = configure2.getConnectionUrl();
        System.out.println(String.format("Url:%s", url2));
        String driver2 = configure2.getDriverClass();
        System.out.println(String.format("Driver:%s", driver2));
        String version2 = configure2.getVersion();
        System.out.println(String.format("Version:%s", version2));
        int maxActive2 = configure2.getIntProperty(DataSourceConfigureConstants.MAXACTIVE, 200);
        System.out.println(String.format("maxActive:%s", maxActive2));

    }

    @Test
    public void testConnectionString() throws Exception {
        DataSourceConfigure configBefore = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        String urlBefore = configBefore.getConnectionUrl();
        String userNameBefore = configBefore.getUserName();
        String passwordBefore = configBefore.getPassword();

        System.out.println("*****Before editing*****");
        System.out.println(String.format("Url:%s", urlBefore));
        System.out.println(String.format("Username:%s", userNameBefore));
        System.out.println(String.format("Password:%s", passwordBefore));

        System.out.println("*****Ready to sleep 30 seconds,please edit configure in QConfig...*****");
        Thread.sleep(30 * 1000);
        System.out.println("*****Time's up.*****");

        DataSourceConfigure configAfter = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        String urlAfter = configAfter.getConnectionUrl();
        String userNameAfter = configAfter.getUserName();
        String passwordAfter = configAfter.getPassword();

        System.out.println("*****After editing*****");
        System.out.println(String.format("Url:%s", urlAfter));
        System.out.println(String.format("Username:%s", userNameAfter));
        System.out.println(String.format("Password:%s", passwordAfter));
    }

    @Test
    public void testConnectionStringVersion() throws Exception {
        DataSourceConfigure configBefore = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        String versionBefore = configBefore.getVersion();
        System.out.println("*****Before editing*****");
        System.out.println(String.format("Version:%s", versionBefore));

        System.out.println("*****Ready to sleep 30 seconds...*****");
        Thread.sleep(30 * 1000);
        System.out.println("*****Time's up.*****");

        DataSourceConfigure configAfter = DataSourceConfigureLocator.getInstance().getDataSourceConfigure(name);
        String versionAfter = configAfter.getVersion();
        System.out.println("*****After editing*****");
        System.out.println(String.format("Version:%s", versionAfter));

        if (versionBefore != null && versionAfter != null) {
            Assert.assertEquals(versionBefore, versionAfter);
        }
    }
}
