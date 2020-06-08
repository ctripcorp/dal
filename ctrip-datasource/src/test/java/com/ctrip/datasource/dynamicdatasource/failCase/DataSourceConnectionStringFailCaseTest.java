package com.ctrip.datasource.dynamicdatasource.failCase;

import com.ctrip.datasource.dynamicdatasource.provider.AbstractIPDomainStatusProvider;
import com.ctrip.datasource.dynamicdatasource.provider.AbstractPoolPropertiesProvider;
import com.ctrip.datasource.dynamicdatasource.provider.LocalConnectionStringProvider;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocator;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureLocatorManager;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataSourceConnectionStringFailCaseTest {
    private static final String name = "mysqldaltest01db_W";
    private static DataSourceConfigureLocator locator = DataSourceConfigureLocatorManager.getInstance();
    private static LocalConnectionStringProvider provider = new LocalConnectionStringProvider();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DataSourceConfigureManager.getInstance().setConnectionStringProvider(provider);
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(new AbstractPoolPropertiesProvider());
        DataSourceConfigureManager.getInstance().setIPDomainStatusProvider(new AbstractIPDomainStatusProvider());

        DalClientFactory.initClientFactory();
    }

    @Test
    public void testListenerIsNullWhenConfigChanged() throws Exception {
        try {
            boolean before = DataSourceLocator.containsKey(name.toLowerCase());
            Assert.assertFalse(before);

            DataSourceConfigure conf1 = locator.getDataSourceConfigure(name);
            Assert.assertEquals("jdbc:mysql://10.32.20.128:3306/dal_shard_0?useUnicode=true&characterEncoding=UTF-8",
                    conf1.getConnectionUrl());

            // emulate configChanged
            provider.triggerConnectionStringChanged();
            System.out.println("Sleep for 5 seconds to let the datasource being created...");
            Thread.sleep(5 * 1000);
            // end

            boolean after = DataSourceLocator.containsKey(name.toLowerCase());
            Assert.assertTrue(after);

            DataSourceConfigure conf2 = locator.getDataSourceConfigure(name);
            Assert.assertEquals("jdbc:mysql://10.32.20.128:3306/dal_shard_1?useUnicode=true&characterEncoding=UTF-8",
                    conf2.getConnectionUrl());
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            Assert.fail();
        }


    }

}
