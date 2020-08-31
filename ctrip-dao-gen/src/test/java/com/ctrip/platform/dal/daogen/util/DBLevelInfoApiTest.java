package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.entity.DbInfos;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DBLevelInfoApiTest {

    @InjectMocks
    private DBLevelInfoApi api;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllDbInfosTest() {
        List<DbInfos> dbInfos = api.getAllDbInfos("test");
        assertEquals(0, dbInfos.size());
    }
}