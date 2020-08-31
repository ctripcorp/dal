package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.entity.ClusterListResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class DalClusterInfoApiTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @InjectMocks
    private DalClusterInfoApi api;

    @Test
    public void getClusterListDb() {
        ClusterListResponse response = api.getClusterListDb();
        assertEquals(response.getStatus(), 200);
    }
}