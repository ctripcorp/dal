package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.datasource.dynamicdatasource.provider.AbstractConnectionStringProvider;
import com.ctrip.datasource.dynamicdatasource.provider.AbstractPoolPropertiesProvider;
import com.ctrip.datasource.dynamicdatasource.provider.LocalIPDomainStatusProvider;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataSourceIPDomainStatusTest {
    private static final String name = "mysqldaltest01db_W";
    private static DataSourceConfigureLocator locator = DataSourceConfigureLocator.getInstance();
    private static LocalIPDomainStatusProvider provider = new LocalIPDomainStatusProvider();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DataSourceConfigureManager.getInstance().setConnectionStringProvider(new AbstractConnectionStringProvider());
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(new AbstractPoolPropertiesProvider());
        DataSourceConfigureManager.getInstance().setIPDomainStatusProvider(provider);
    }

    @Test
    public void testInitializedByIPStatus() throws Exception {
        provider.setIPStatus();
        DalClientFactory.initClientFactory();
        testSwitchStatus();
    }

    @Test
    public void testInitializedByDomainStatus() throws Exception {
        provider.setDomainStatus();
        DalClientFactory.initClientFactory();
        testSwitchStatus();
    }

    private void testSwitchStatus() throws Exception {
        displayConnectionStringProperties();
        provider.initStatus();

        for (int i = 0; i < 10; i++) {
            provider.triggerIPDomainStatusChanged();
            Thread.sleep(3 * 1000);
            displayConnectionStringProperties();
        }
    }

    private void displayConnectionStringProperties() {
        DataSourceConfigure configure = locator.getDataSourceConfigure(name);
        String userName1 = configure.getUserName();
        System.out.println(String.format("UserName:%s", userName1));
        String password1 = configure.getPassword();
        System.out.println(String.format("Password:%s", password1));
        String url1 = configure.getConnectionUrl();
        System.out.println(String.format("Url:%s", url1));
        String driver1 = configure.getDriverClass();
        System.out.println(String.format("Driver:%s", driver1));
        String version1 = configure.getVersion();
        System.out.println(String.format("Version:%s", version1));
    }

}
