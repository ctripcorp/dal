package com.ctrip.platform.dal.dao.datasource.read;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrimaryVisitedManagerTest {

    @Test
    public void getPrimaryVisited() {
        assertEquals(false, PrimaryVisitedManager.getPrimaryVisited());
        PrimaryVisitedManager.setPrimaryVisited();
        assertEquals(true, PrimaryVisitedManager.getPrimaryVisited());
        PrimaryVisitedManager.clear();
        assertEquals(false, PrimaryVisitedManager.getPrimaryVisited());
    }

}