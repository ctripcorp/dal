package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.configure.DalConfigLoader;
import org.junit.Assert;
import org.junit.Test;

public class ServiceLoaderHelperTest {

    @Test
    public void testOrdered(){

        Ordered ordered = ServiceLoaderHelper.getInstance(Ordered.class);
        Assert.assertTrue(ordered instanceof Ordered2);

    }


    @Test
    public void testNotOrdered(){

        DalConfigLoader instance = ServiceLoaderHelper.getInstance(DalConfigLoader.class);
        Assert.assertNull(instance);

    }
}
