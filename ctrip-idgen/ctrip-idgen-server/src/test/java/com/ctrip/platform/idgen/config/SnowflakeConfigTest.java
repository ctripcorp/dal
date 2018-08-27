package com.ctrip.platform.idgen.config;

import org.junit.Assert;
import org.junit.Test;

public class SnowflakeConfigTest {

    @Test
    public void testA() {

        int a = function();
        Assert.assertEquals(0, a);

    }

    private int function() {
        return 0;
    }

}
