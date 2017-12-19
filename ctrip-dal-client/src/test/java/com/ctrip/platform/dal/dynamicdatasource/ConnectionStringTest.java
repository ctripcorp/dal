package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConnectionStringTest {
    private static final String databaseName = "mysqldaltest01db_W";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
        DalClientFactory.warmUpConnections();
    }

    @Test
    public void testConnectionString() throws Exception {
        DataSourceConfigure configBefore =
                DataSourceConfigureLocator.getInstance().getDataSourceConfigureCollection(databaseName).getConfigure();
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

        DataSourceConfigure configAfter =
                DataSourceConfigureLocator.getInstance().getDataSourceConfigureCollection(databaseName).getConfigure();
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
        DataSourceConfigure configBefore =
                DataSourceConfigureLocator.getInstance().getDataSourceConfigureCollection(databaseName).getConfigure();
        String versionBefore = configBefore.getVersion();
        System.out.println("*****Before editing*****");
        System.out.println(String.format("Version:%s", versionBefore));

        System.out.println("*****Ready to sleep 30 seconds...*****");
        Thread.sleep(30 * 1000);
        System.out.println("*****Time's up.*****");

        DataSourceConfigure configAfter =
                DataSourceConfigureLocator.getInstance().getDataSourceConfigureCollection(databaseName).getConfigure();
        String versionAfter = configAfter.getVersion();
        System.out.println("*****After editing*****");
        System.out.println(String.format("Version:%s", versionAfter));

        if (versionBefore != null && versionAfter != null) {
            Assert.assertEquals(versionBefore, versionAfter);
        }
    }

}
