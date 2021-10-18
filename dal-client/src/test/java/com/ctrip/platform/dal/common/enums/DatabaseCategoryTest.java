package com.ctrip.platform.dal.common.enums;

import org.junit.Test;

/**
 * @Author limingdong
 * @create 2021/10/18
 */
public class DatabaseCategoryTest {

    private DatabaseCategory databaseCategory = DatabaseCategory.Custom;

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

}