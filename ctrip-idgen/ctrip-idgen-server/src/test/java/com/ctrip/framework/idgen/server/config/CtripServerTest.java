package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.util.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CtripServerTest {

    private CtripServer server = new CtripServer();

    @Test
    public void parseWorkerIdTest() {
        Map<String, String> properties = mockInitialProperties();
        try {
            server.parseWorkerId(properties);
            Assert.fail();
        } catch (Exception e) {
        }
        long workerId = 9;
        putLocalWorkerId(properties, workerId);
        Assert.assertEquals(workerId, server.parseWorkerId(properties));
    }

    @Test
    public void checkWorkerIdDuplicationTest() {
        Map<String, String> properties = mockInitialProperties();
        Assert.assertFalse(server.checkWorkerIdDuplication(properties));
        properties.put(String.format(TestUtils.WORKER_ID_PROPERTY_KEY_FORMAT, "1.1.1.4"), "1");
        Assert.assertTrue(server.checkWorkerIdDuplication(properties));
    }

    @Test
    public void initializeTest() {
        Map<String, String> properties = mockInitialProperties();
        try {
            server.initialize(properties);
            Assert.fail();
        } catch (Exception e) {
        }
        putLocalWorkerId(properties, -10);
        try {
            server.initialize(properties);
            Assert.fail();
        } catch (Exception e) {
        }
        long workerId = 11;
        putLocalWorkerId(properties, workerId);
        server.initialize(properties);
        Assert.assertEquals(workerId, server.getWorkerId());
    }

    private Map<String, String> mockInitialProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put(String.format(TestUtils.WORKER_ID_PROPERTY_KEY_FORMAT, "1.1.1.1"), "1");
        properties.put(String.format(TestUtils.WORKER_ID_PROPERTY_KEY_FORMAT, "1.1.1.2"), "2");
        properties.put(String.format(TestUtils.WORKER_ID_PROPERTY_KEY_FORMAT, "1.1.1.3"), "3");
        return properties;
    }

    private void putLocalWorkerId(Map<String, String> properties, long workerId) {
        properties.put(TestUtils.getLocalWorkerIdKey(), String.valueOf(workerId));
    }

}
