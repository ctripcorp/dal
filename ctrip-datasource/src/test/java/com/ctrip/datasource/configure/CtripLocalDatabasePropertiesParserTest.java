package com.ctrip.datasource.configure;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author c7ch23en
 */
public class CtripLocalDatabasePropertiesParserTest {

    @Test
    public void testParseDbName() {
        CtripLocalDatabasePropertiesParser parser = CtripLocalDatabasePropertiesParser.newInstance(null);
        Assert.assertEquals("abcdb", parser.tryParseDalCluster("abcdb_dalcluster"));
        Assert.assertEquals("abc", parser.tryParseDalCluster("abc_dalcluster"));
        Assert.assertEquals("abcshardbase",
                parser.tryParseDalCluster("abcshardbase_dalcluster"));
        Assert.assertEquals("abcdb",
                parser.tryParseDalCluster("abcshardbasedb_dalcluster"));
        Assert.assertEquals("shardbasedb",
                parser.tryParseDalCluster("shardbasedb_dalcluster"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_w"));
        Assert.assertEquals("abc", parser.tryParseTitanKey("abc_w"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_w_sh"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_r"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_r_sh"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_s"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_s_sh"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_read"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_read_sh"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_sh"));
        Assert.assertEquals("abcdb", parser.tryParseTitanKey("abcdb_test_sh"));
    }

    @Test
    public void testParseMySQLProperties() {
        Properties properties = new Properties();
        properties.setProperty(CtripLocalDatabasePropertiesParser.PROPERTY_HOST, "1.1.1.1");
        properties.setProperty(CtripLocalDatabasePropertiesParser.PROPERTY_PORT, "1234");
        properties.setProperty(CtripLocalDatabasePropertiesParser.PROPERTY_DB_NAME, "mock");
        CtripLocalDatabasePropertiesParser parser =
                CtripLocalDatabasePropertiesParser.newInstance(properties, "mockdb_dalcluster");
        Assert.assertTrue(parser instanceof CtripLocalMySQLPropertiesParser);
        Assert.assertEquals("1.1.1.1", parser.getHost());
        Assert.assertEquals(1234, parser.getPort());
        Assert.assertEquals("mock", parser.getDbName());
        Assert.assertEquals(CtripLocalMySQLPropertiesParser.DEFAULT_UID, parser.getUid());
        Assert.assertEquals(CtripLocalMySQLPropertiesParser.DEFAULT_PWD, parser.getPwd());
    }

    @Test
    public void testParseSQLServerProperties() {
        Properties properties = new Properties();
        properties.setProperty(CtripLocalDatabasePropertiesParser.PROPERTY_DB_CATEGORY,
                CtripLocalSQLServerPropertiesParser.DB_CATEGORY);
        properties.setProperty(CtripLocalDatabasePropertiesParser.PROPERTY_HOST, "1.1.1.1");
        properties.setProperty(CtripLocalDatabasePropertiesParser.PROPERTY_PWD, "na");
        CtripLocalDatabasePropertiesParser parser =
                CtripLocalDatabasePropertiesParser.newInstance(properties, "mockdb_w_sh");
        Assert.assertTrue(parser instanceof CtripLocalSQLServerPropertiesParser);
        Assert.assertEquals("1.1.1.1", parser.getHost());
        Assert.assertEquals(CtripLocalSQLServerPropertiesParser.DEFAULT_PORT, parser.getPort());
        Assert.assertEquals("mockdb", parser.getDbName());
        Assert.assertEquals(CtripLocalSQLServerPropertiesParser.DEFAULT_UID, parser.getUid());
        Assert.assertEquals("na", parser.getPwd());
    }

}
