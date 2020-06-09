package com.ctrip.datasource.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class HeraldTokenUtilsTest {

    @Test
    public void testHeraldTokenUtils() {
        Assert.assertNull(HeraldTokenUtils.tryGetHeraldToken());
        HeraldTokenUtils.registerHeraldToken("mock_group", "mock_config");
    }

}
