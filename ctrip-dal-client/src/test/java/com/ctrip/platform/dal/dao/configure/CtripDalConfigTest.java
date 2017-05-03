package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.DalClientFactory;
import org.junit.Test;

public class CtripDalConfigTest {
    @Test
    public void testCtripDalConfig() throws Exception {
        DalClientFactory.initClientFactory();
    }
}
