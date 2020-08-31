package com.ctrip.datasource.titan;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class DataSourceConfigureHelperTest {

    @Test
    public void testParseDatabaseConfigLocation() {
        DataSourceConfigureHelper helper = new DataSourceConfigureHelper();
        helper.databaseConfigLocation = "/abc/xyz/";
        helper.parseDatabaseConfigLocation();
        Assert.assertEquals("/abc/xyz/", helper.getParsedDatabaseConfigPath());
        Assert.assertNull(helper.getParsedDatabaseConfigFile());

        helper = new DataSourceConfigureHelper();
        helper.databaseConfigLocation = "/abc/xyz";
        helper.parseDatabaseConfigLocation();
        Assert.assertEquals("/abc/", helper.getParsedDatabaseConfigPath());
        Assert.assertEquals("xyz", helper.getParsedDatabaseConfigFile());

        helper = new DataSourceConfigureHelper();
        helper.databaseConfigLocation = "/abc.xyz";
        helper.parseDatabaseConfigLocation();
        Assert.assertEquals("/", helper.getParsedDatabaseConfigPath());
        Assert.assertEquals("abc.xyz", helper.getParsedDatabaseConfigFile());

        helper = new DataSourceConfigureHelper();
        helper.databaseConfigLocation = "$classpath";
        helper.parseDatabaseConfigLocation();
        Assert.assertEquals("$classpath", helper.getParsedDatabaseConfigPath());
        Assert.assertNull(helper.getParsedDatabaseConfigFile());
    }

}
