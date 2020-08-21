package com.ctrip.platform.dal.daogen.resource;

import org.junit.Assert;
import org.junit.Test;

public class FileResourceTest {

    private static volatile FileResource fileResource;

    static {
        fileResource = new FileResource();
    }

    @Test
    public void getClusterDatabaseSet() {
        String s = "myteststring";
        int first = s.indexOf("t");
        System.out.println(first);
        System.out.println(s.indexOf("t", first + 1));
    }

    @Test
    public void isDalCluster() {
        Assert.assertEquals(true, fileResource.isDalCluster("bbz_dalcluster"));
        Assert.assertEquals(false, fileResource.isDalCluster("clustername"));
    }
}