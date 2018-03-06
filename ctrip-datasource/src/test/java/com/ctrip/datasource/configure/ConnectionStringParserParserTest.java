package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.ConnectionStringParser;
import org.junit.*;
import org.junit.Before;
import org.junit.Test;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

public class ConnectionStringParserParserTest {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testSqlServer() {
        ConnectionStringParser parser = new ConnectionStringParser();
        DataSourceConfigure c = parser.parse("SimpleShard_0",
                "Data Source=DST56614,1433;UID=sa;password=!QAZ@WSX1qaz2wsx; database=SimpleShard_0;");
        Assert.assertNotNull(c);
        // Assert.assertEquals("jdbc:sqlserver://DST56614:1433;DatabaseName=SimpleShard_0;rewriteBatchedStatements=true;allowMultiQueries=true",
        // c.getConnectionUrl());
        Assert.assertEquals("jdbc:sqlserver://DST56614:1433;DatabaseName=SimpleShard_0", c.getConnectionUrl());
        Assert.assertEquals("com.microsoft.sqlserver.jdbc.SQLServerDriver", c.getDriverClass());
        Assert.assertEquals("sa", c.getUserName());
        Assert.assertEquals("!QAZ@WSX1qaz2wsx", c.getPassword());
    }

    @Test
    public void testMySql() {
        ConnectionStringParser parser = new ConnectionStringParser();
        DataSourceConfigure c = parser.parse("dao_test_mysql",
                "Server=DST56614;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dao_test_mysql;");
        Assert.assertNotNull(c);
        Assert.assertEquals("jdbc:mysql://DST56614:3306/dao_test_mysql?useUnicode=true&characterEncoding=UTF-8",
                c.getConnectionUrl());
        Assert.assertEquals("com.mysql.jdbc.Driver", c.getDriverClass());
        Assert.assertEquals("root", c.getUserName());
        Assert.assertEquals("!QAZ@WSX1qaz2wsx", c.getPassword());
    }
}
