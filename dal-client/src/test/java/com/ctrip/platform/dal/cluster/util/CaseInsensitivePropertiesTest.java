package com.ctrip.platform.dal.cluster.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author c7ch23en
 */
public class CaseInsensitivePropertiesTest {

    @Test
    public void testInit() {
        Map<String, String> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", "2");
        CaseInsensitiveProperties properties = new CaseInsensitiveProperties(map);
        Assert.assertEquals("v1", properties.get("k1"));
        Assert.assertEquals("2", properties.get("k2"));
        Properties props = new Properties();
        props.setProperty("k3", "v3");
        props.setProperty("k4", "4");
        properties = new CaseInsensitiveProperties(props);
        Assert.assertEquals("v3", properties.get("k3"));
        Assert.assertEquals("4", properties.get("k4"));
    }

    @Test
    public void testGetProperty() {
        CaseInsensitiveProperties properties = new CaseInsensitiveProperties();
        properties.set("ka", "va");
        properties.set("kB", "vb");
        properties.set("kc", "1");
        properties.set("kd", "true");
        properties.set("ke", "ve1,ve2,ve3");
        properties.set("kf", "");
        properties.set("kg", ",vg");
        Assert.assertEquals("va", properties.get("ka"));
        Assert.assertEquals("vb", properties.get("kb"));
        Assert.assertEquals(1, properties.getInt("kc", 0));
        Assert.assertEquals(0, properties.getInt("kcx", 0));
        Assert.assertTrue(properties.getBool("kd", false));
        Assert.assertFalse(properties.getBool("kdx", false));
        Assert.assertEquals("ve1,ve2,ve3", properties.getString("kE", "ve"));
        Assert.assertEquals("ve", properties.getString("kex", "ve"));
        List<String> list = properties.getStringList("ke", ",", null);
        Assert.assertEquals(3, list.size());
        Set<String> set = new HashSet<>(list);
        Assert.assertTrue(set.contains("ve1"));
        Assert.assertTrue(set.contains("ve2"));
        Assert.assertTrue(set.contains("ve3"));
        Assert.assertEquals("", properties.getString("kf", "vf"));
        list = properties.getStringList("kf", ",", null);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("", list.iterator().next());
        list = properties.getStringList("kg", ",", null);
        Assert.assertEquals(2, list.size());
        set = new HashSet<>(list);
        Assert.assertTrue(set.contains(""));
        Assert.assertTrue(set.contains("vg"));
    }

}
