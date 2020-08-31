package com.ctrip.platform.dal.dao.configure;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author c7ch23en
 */
public class InjectableComponentTest {

    @Test
    public void testInject() throws Exception {
        MockComponent component = new MockComponent();
        DatabaseSets databaseSets1 = new DatabaseSetsImpl(null);
        component.inject(databaseSets1);
        Assert.assertEquals(databaseSets1, component.getDatabaseSets());
        component.initialize(null);
        Assert.assertEquals(1, component.c);
        try {
            DatabaseSets databaseSets2 = new DatabaseSetsImpl(null);
            component.inject(databaseSets2);
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
        Assert.assertEquals(databaseSets1, component.getDatabaseSets());
        component.initialize(null);
        Assert.assertEquals(1, component.c);
    }

    static class MockComponent extends InjectableComponentSupport {
        int c = 0;

        @Override
        protected void pInitialize(Map<String, String> settings) {
            c++;
        }
    }

}
