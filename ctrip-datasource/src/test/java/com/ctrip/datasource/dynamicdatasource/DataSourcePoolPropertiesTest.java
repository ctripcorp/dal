package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.datasource.dynamicdatasource.provider.AbstractConnectionStringProvider;
import com.ctrip.datasource.dynamicdatasource.provider.AbstractIPDomainStatusProvider;
import com.ctrip.datasource.dynamicdatasource.provider.LocalPoolPropertiesProvider;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureLocator;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataSourcePoolPropertiesTest {
    private static final String name = "mysqldaltest01db_w";
    private static DefaultDataSourceConfigureLocator locator = DefaultDataSourceConfigureLocator.getInstance();
    private static LocalPoolPropertiesProvider provider = new LocalPoolPropertiesProvider();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DataSourceConfigureManager.getInstance().setConnectionStringProvider(new AbstractConnectionStringProvider());
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(provider);
        DataSourceConfigureManager.getInstance().setIPDomainStatusProvider(new AbstractIPDomainStatusProvider());

        DalClientFactory.initClientFactory();
    }

    @Test
    public void testPoolPropertiesDynamicEnabledFunction() throws Exception {
        DataSourceConfigure configure1 = locator.getDataSourceConfigure(name);
        boolean dynamicEnabled1 = configure1.dynamicPoolPropertiesEnabled();
        System.out.println(String.format("dynamicEnabled:%s", dynamicEnabled1));
        int minIdle1 = configure1.getIntProperty(DataSourceConfigureConstants.MINIDLE, -1);
        System.out.println(String.format("minIdle:%s", minIdle1));

        System.out.println("**********Trigger pool properties changed callback**********");
        provider.triggerPoolPropertiesChanged();
        Thread.sleep(1 * 1000);

        DataSourceConfigure configure2 = locator.getDataSourceConfigure(name);
        boolean dynamicEnabled2 = configure2.dynamicPoolPropertiesEnabled();
        System.out.println(String.format("dynamicEnabled:%s", dynamicEnabled2));
        int minIdle2 = configure2.getIntProperty(DataSourceConfigureConstants.MINIDLE, -1);
        System.out.println(String.format("minIdle:%s", minIdle2));
    }

}
