package com.ctrip.platform.dal.dynamicdatasource;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataSourceIPDomainStatusTest {
    private static final String name = "mysqldaltest01db_W";
    private static DataSourceConfigureLocator locator = DataSourceConfigureLocator.getInstance();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testDataSourceIPDomainStatusTest() throws Exception {
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

        System.out.println("Sleep for 30 seconds.");
        Thread.sleep(60 * 1000);

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

    }

}
