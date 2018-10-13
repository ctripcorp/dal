package com.ctrip.framework.idgen.server.config;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CtripWhitelistTest {

    private CtripWhitelist whitelist = new CtripWhitelist();

    @Test
    public void loadTest() {
        String dbName1 = "db1";
        String dbName2 = "db2";
        String tbName1 = "tb1";
        String tbName2 = "tb2";
        String tbName3 = "tb3";
        Map<String, String> properties = new HashMap<>();
        properties.put(String.format("%s.%s", dbName1, tbName1), "on");
        properties.put(String.format("%s.%s", dbName1, tbName2), "On");
        properties.put(String.format("%s.%s", dbName1, tbName3), "off");
        properties.put(dbName2, "OFF");
        whitelist.load(properties);
        Assert.assertTrue(whitelist.validate(String.format("%s.%s", dbName1, tbName1.toLowerCase())));
        Assert.assertTrue(whitelist.validate(String.format("%s.%s", dbName1.toLowerCase(), tbName2)));
        Assert.assertFalse(whitelist.validate(String.format("%s.%s", dbName1, tbName3)));
        Assert.assertFalse(whitelist.validate(String.format("%s.%s", dbName2, tbName1.toLowerCase())));
        Assert.assertFalse(whitelist.validate(String.format("%s.%s", dbName2.toLowerCase(), tbName2)));
        Assert.assertFalse(whitelist.validate(String.format("%s.%s", dbName2, tbName3)));
        Assert.assertFalse(whitelist.validate(dbName1));
        Assert.assertFalse(whitelist.validate(dbName2));
        Assert.assertFalse(whitelist.validate(tbName1));
        Assert.assertFalse(whitelist.validate(tbName2));
        Assert.assertFalse(whitelist.validate(tbName3));
        properties.put(String.format("%s.%s", dbName1, tbName2), "Off");
        properties.put(dbName2, "ON");
        whitelist.load(properties);
        Assert.assertTrue(whitelist.validate(String.format("%s.%s", dbName1, tbName1.toLowerCase())));
        Assert.assertFalse(whitelist.validate(String.format("%s.%s", dbName1.toLowerCase(), tbName2)));
        Assert.assertFalse(whitelist.validate(String.format("%s.%s", dbName1, tbName3)));
        Assert.assertTrue(whitelist.validate(String.format("%s.%s", dbName2, tbName1.toLowerCase())));
        Assert.assertTrue(whitelist.validate(String.format("%s.%s", dbName2.toLowerCase(), tbName2)));
        Assert.assertTrue(whitelist.validate(String.format("%s.%s", dbName2, tbName3)));
        Assert.assertFalse(whitelist.validate(dbName1));
        Assert.assertTrue(whitelist.validate(dbName2));
        Assert.assertFalse(whitelist.validate(tbName1));
        Assert.assertFalse(whitelist.validate(tbName2));
        Assert.assertFalse(whitelist.validate(tbName3));
    }

}
