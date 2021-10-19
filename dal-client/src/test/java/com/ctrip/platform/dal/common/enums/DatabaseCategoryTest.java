package com.ctrip.platform.dal.common.enums;

import org.junit.Assert;
import org.junit.Test;

/**
 * @Author limingdong
 * @create 2021/10/18
 */
public class DatabaseCategoryTest {

    private DatabaseCategory databaseCategory = DatabaseCategory.Custom;

    private String connectionUrl = "jdbc:clickhouse://%s:%s/%s?ssl=false&user=%s&password=%s&use_server_time_zone=false&use_time_zone=UTC";

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedOperationQuote() {
        databaseCategory.quote(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedOperationIsTimeOutException() {
        databaseCategory.isTimeOutException(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedOperationBuildList() {
        databaseCategory.buildList(null, null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedOperationBuildTop() {
        databaseCategory.buildTop(null, null, null, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedOperationBuildPage() {
        databaseCategory.buildPage(null, null, null, 0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedOperationBuildPage1() {
        databaseCategory.buildPage(null, 0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedOperationIsSpecificException() {
        databaseCategory.isSpecificException(null);
    }

    @Test
    public void testMatchWith() {
        Assert.assertEquals(databaseCategory, DatabaseCategory.matchWith(com.ctrip.framework.dal.cluster.client.database.DatabaseCategory.CUSTOM));
    }

    @Test
    public void testMatchWithConnectionUrl() {
        Assert.assertEquals(databaseCategory, DatabaseCategory.matchWithConnectionUrl(connectionUrl));
    }

}