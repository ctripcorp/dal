package com.ctrip.platform.dal.daogen.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class DalClusterUtilsTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getModeTypeByDbBaseName() {
        assertEquals("titankey", DalClusterUtils.getModeTypeByDbBaseName(null));
        assertEquals("dalcluster", DalClusterUtils.getModeTypeByDbBaseName("abtest_dalcluster"));
    }
}