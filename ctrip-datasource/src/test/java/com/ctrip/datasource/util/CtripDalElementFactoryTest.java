package com.ctrip.datasource.util;

import com.ctrip.platform.dal.dao.helper.EnvUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class CtripDalElementFactoryTest {

    @Test
    public void testEnvUtils() {
        EnvUtils envUtils = new CtripDalElementFactory().getEnvUtils();
        Assert.assertTrue(envUtils instanceof CtripEnvUtils);
        Assert.assertNotNull(envUtils.getEnv());
    }

}
