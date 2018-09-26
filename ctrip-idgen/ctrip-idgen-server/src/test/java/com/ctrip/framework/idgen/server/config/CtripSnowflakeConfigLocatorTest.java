package com.ctrip.framework.idgen.server.config;

import org.junit.Test;

public class CtripSnowflakeConfigLocatorTest {

    //@Test
    public void testNullConfig() {
        CtripSnowflakeConfigLocator locator = new CtripSnowflakeConfigLocator(new CtripServer());
        locator.setup(null);
        int i = 0;
    }

}
