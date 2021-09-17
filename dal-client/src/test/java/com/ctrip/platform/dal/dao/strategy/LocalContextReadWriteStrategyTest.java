package com.ctrip.platform.dal.dao.strategy;

import org.junit.Assert;
import org.junit.Test;

public class LocalContextReadWriteStrategyTest {

    @Test
    public void testGetReadFromMaster(){
        Assert.assertFalse(LocalContextReadWriteStrategy.getReadFromMaster());
        LocalContextReadWriteStrategy.setReadFromMaster();
        Assert.assertTrue(LocalContextReadWriteStrategy.getReadFromMaster());
        LocalContextReadWriteStrategy.clearContext();
        Assert.assertFalse(LocalContextReadWriteStrategy.getReadFromMaster());
    }

}
