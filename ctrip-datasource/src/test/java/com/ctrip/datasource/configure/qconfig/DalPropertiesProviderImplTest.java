package com.ctrip.datasource.configure.qconfig;

import org.junit.Assert;
import org.junit.Test;
import qunar.tc.qconfig.client.Feature;

import java.util.Map;

public class DalPropertiesProviderImplTest {
    @Test
    public void testNullConfig() throws Exception {
        DalPropertiesProviderImpl provider = new DalPropertiesProviderImpl();
        try {
            Map<String, String> config = provider.getPropertiesMap(new MockNullMapConfig(Feature.DEFAULT));
            Assert.assertTrue(config.size()==0);
        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void testExceptionConfig() throws Exception {
        DalPropertiesProviderImpl provider = new DalPropertiesProviderImpl();
        try {
            Map<String, String> config = provider.getPropertiesMap(new MockExceptionMapConfig(Feature.DEFAULT));
            Assert.assertTrue(config.size()==0);
        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void testConfigNotFoundExceptionConfig() throws Exception {
        DalPropertiesProviderImpl provider = new DalPropertiesProviderImpl();
        try {
            Map<String, String> config = provider.getPropertiesMap(new MockConfigNotExistsMapConfig(Feature.DEFAULT));
            Assert.assertTrue(config.size()==0);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
