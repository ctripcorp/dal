package com.ctrip.datasource.datasource.MockQConfigProvider;

import com.ctrip.datasource.configure.qconfig.PoolPropertiesProviderImpl;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.platform.dal.dao.DalClientFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExceptionQConfigPoolPropertiesProviderTest {
    private static ExceptionQConfigPoolPropertiesProvider exceptionProvider =
            new ExceptionQConfigPoolPropertiesProvider();
    private static PoolPropertiesProviderImpl provider = new PoolPropertiesProviderImpl();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(exceptionProvider);
    }

    @Test
    public void testThrowExceptionDuringInitialization() {
        try {
            DalClientFactory.initClientFactory();
            Assert.fail();
        } catch (Throwable e) {
            boolean result = e.getCause().getMessage().equals(ExceptionQConfigPoolPropertiesProvider.EXCEPTION_MESSAGE);
            Assert.assertTrue(result);
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(provider);
    }

}
