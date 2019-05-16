package com.ctrip.datasource.configure.qconfig;

import org.junit.Assert;
import org.junit.Test;
import qunar.tc.qconfig.client.Feature;

import java.util.Map;

public class PoolPropertiesProviderImplTest {
    @Test
    public void testNullConfig() throws Exception {
        PoolPropertiesProviderImpl provider = new PoolPropertiesProviderImpl();
        try {
            Map<String, String> config = provider.getPoolPropertiesMap(new MockNullMapConfig(Feature.DEFAULT));
            Assert.assertNull(config);
        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void testExceptionConfig() throws Exception {
        PoolPropertiesProviderImpl provider = new PoolPropertiesProviderImpl();
        try {
            provider.getPoolPropertiesMap(new MockExceptionMapConfig(Feature.DEFAULT));
            Assert.fail();
        }catch (Exception e){
            Assert.assertTrue(e.getMessage().contains("MockExceptionMapConfig"));
        }
    }

    @Test
    public void testLocalConfigNotFoundExceptionConfig() throws Exception {
        PoolPropertiesProviderImpl provider = new PoolPropertiesProviderImpl();
        try {
            Map<String, String> config = provider.getPoolPropertiesMap(new MockConfigNotExistsMapConfig(Feature.DEFAULT));
            Assert.assertNull(config);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
