package com.ctrip.platform.dal.daogen.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FileResourceTest {

    @InjectMocks
    private FileResource fileResource;

    @Mock
    private BufferedReader bufferedReader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isDalCluster() {
        assertEquals(true, fileResource.isDalCluster("bbz_dalcluster"));
        assertEquals(false, fileResource.isDalCluster("clustername"));
    }

    @Test
    public void getClusterDatabaseSetTest() throws IOException {
        String s = "\t\t<databaseSet name=\"corporderprocesslogshardbasedb_dalcluster\" provider=\"sqlserver\"/>";
        String s1 = "\t\t<databasseSet name=\"corporderprocesslogshardbasedb_dalcluster\" provider=\"sqlserver\"/>";
        Mockito.when(bufferedReader.readLine()).thenReturn("next");

        String result = fileResource.getClusterDatabaseSet(s, bufferedReader);

        assertEquals("\t\t<cluster name=\"corporderprocesslogshardbasedb_dalcluster\" />", result);
        assertEquals(s1, fileResource.getClusterDatabaseSet(s1, bufferedReader));
    }
}