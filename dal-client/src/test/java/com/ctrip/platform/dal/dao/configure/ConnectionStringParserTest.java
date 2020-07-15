package com.ctrip.platform.dal.dao.configure;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class ConnectionStringParserTest {

    @Test
    public void testParseMySqlUrl1() {
        String ipUrl = "jdbc:mysql://10.1.1.1:3308/test?useUnicode=true&characterEncoding=UTF-8";
        String domainUrl = "jdbc:mysql://test.db.com:3308/test?useUnicode=true&characterEncoding=UTF-8";
        HostAndPort ipAndPort = ConnectionStringParser.parseHostPortFromURL(ipUrl);
        Assert.assertEquals("10.1.1.1", ipAndPort.getHost());
        Assert.assertEquals(3308, ipAndPort.getPort().intValue());
        HostAndPort domainAndPort = ConnectionStringParser.parseHostPortFromURL(domainUrl);
        Assert.assertEquals("test.db.com", domainAndPort.getHost());
        Assert.assertEquals(3308, domainAndPort.getPort().intValue());
    }

    @Test
    public void testParseMySqlUrl2() {
        String ipUrl = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=10.1.1.1)(port=3308)/test?useUnicode=true&characterEncoding=UTF-8";
        String domainUrl = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=test.db.com)(port=3308)/test?useUnicode=true&characterEncoding=UTF-8";
        HostAndPort ipAndPort = ConnectionStringParser.parseHostPortFromURL(ipUrl);
        Assert.assertEquals("10.1.1.1", ipAndPort.getHost());
        Assert.assertEquals(3308, ipAndPort.getPort().intValue());
        HostAndPort domainAndPort = ConnectionStringParser.parseHostPortFromURL(domainUrl);
        Assert.assertEquals("test.db.com", domainAndPort.getHost());
        Assert.assertEquals(3308, domainAndPort.getPort().intValue());
    }

    @Test
    public void testParseMySqlUrl3() {
        String ipUrl = "jdbc:mysql://(host=10.1.1.1,port=3308,type=master,protocol=tcp)/test?useUnicode=true&characterEncoding=UTF-8";
        String domainUrl = "jdbc:mysql://(port=3308,host=test.db.com)/test?useUnicode=true&characterEncoding=UTF-8";
        HostAndPort ipAndPort = ConnectionStringParser.parseHostPortFromURL(ipUrl);
        Assert.assertEquals("10.1.1.1", ipAndPort.getHost());
        Assert.assertEquals(3308, ipAndPort.getPort().intValue());
        HostAndPort domainAndPort = ConnectionStringParser.parseHostPortFromURL(domainUrl);
        Assert.assertEquals("test.db.com", domainAndPort.getHost());
        Assert.assertEquals(3308, domainAndPort.getPort().intValue());
    }

    @Test
    public void testParseMySqlUrl4() {
        String ipUrl = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=10.1.1.1)(port=3308):3306:3306/";
        String domainUrl = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=test.db.com)(port=3308):3306:3306/";
        HostAndPort ipAndPort = ConnectionStringParser.parseHostPortFromURL(ipUrl);
        Assert.assertEquals("10.1.1.1", ipAndPort.getHost());
        Assert.assertEquals(3308, ipAndPort.getPort().intValue());
        HostAndPort domainAndPort = ConnectionStringParser.parseHostPortFromURL(domainUrl);
        Assert.assertEquals("test.db.com", domainAndPort.getHost());
        Assert.assertEquals(3308, domainAndPort.getPort().intValue());
    }

    @Test
    public void testParseMySqlUrl5() {
        String ipUrl = "jdbc:mysql://(host=10.1.1.1,port=3308,type=master,protocol=tcp):3306:3306/";
        String domainUrl = "jdbc:mysql://(port=3308,host=test.db.com):3306:3306/";
        HostAndPort ipAndPort = ConnectionStringParser.parseHostPortFromURL(ipUrl);
        Assert.assertEquals("10.1.1.1", ipAndPort.getHost());
        Assert.assertEquals(3308, ipAndPort.getPort().intValue());
        HostAndPort domainAndPort = ConnectionStringParser.parseHostPortFromURL(domainUrl);
        Assert.assertEquals("test.db.com", domainAndPort.getHost());
        Assert.assertEquals(3308, domainAndPort.getPort().intValue());
    }

    @Test
    public void testParseMySqlReplicationUrl() {
        String ipUrl = "jdbc:mysql:replication://" +
                "address=(type=master)(protocol=tcp)(host=10.1.1.1)(port=3308)," +
                "address=(type=master)(protocol=tcp)(host=10.2.2.2)(port=3309)," +
                "address=(type=master)(protocol=tcp)(host=10.3.3.3)(port=3310)/" +
                "mytest?useUnicode=true&characterEncoding=UTF-8&" +
                "loadBalanceStrategy=serverAffinity&serverAffinityOrder=" +
                "address=(type=master)(protocol=tcp)(host=10.3.3.3)(port=3310):3306," +
                "address=(type=master)(protocol=tcp)(host=10.2.2.2)(port=3309):3306," +
                "address=(type=master)(protocol=tcp)(host=10.1.1.1)(port=3308):3306";
        String domainUrl = "jdbc:mysql:replication://" +
                "address=(type=master)(protocol=tcp)(host=test.db.com)(port=3308)," +
                "address=(type=master)(protocol=tcp)(host=test2.db.com)(port=3309)," +
                "address=(type=master)(protocol=tcp)(host=test3.db.com)(port=3310)/" +
                "mytest?useUnicode=true&characterEncoding=UTF-8&" +
                "loadBalanceStrategy=serverAffinity&serverAffinityOrder=" +
                "address=(type=master)(protocol=tcp)(host=test3.db.com)(port=3310):3306," +
                "address=(type=master)(protocol=tcp)(host=test2.db.com)(port=3309):3306," +
                "address=(type=master)(protocol=tcp)(host=test.db.com)(port=3308):3306";
        HostAndPort ipAndPort = ConnectionStringParser.parseHostPortFromURL(ipUrl);
        Assert.assertEquals(ipUrl, ipAndPort.getConnectionUrl());
        Assert.assertNull(ipAndPort.getHost());
        Assert.assertNull(ipAndPort.getPort());
        HostAndPort domainAndPort = ConnectionStringParser.parseHostPortFromURL(domainUrl);
        Assert.assertEquals(domainUrl, domainAndPort.getConnectionUrl());
        Assert.assertNull(domainAndPort.getHost());
        Assert.assertNull(domainAndPort.getPort());
    }

    @Test
    public void testParseSqlServerUrl() {
        String ipUrl = "jdbc:sqlserver://10.1.1.1:1433;sslProtocol=TLS;databaseName=test";
        String domainUrl = "jdbc:sqlserver://test.db.com:1433;sslProtocol=TLS;databaseName=test";
        HostAndPort ipAndPort = ConnectionStringParser.parseHostPortFromURL(ipUrl);
        Assert.assertEquals("10.1.1.1", ipAndPort.getHost());
        Assert.assertEquals(1433, ipAndPort.getPort().intValue());
        HostAndPort domainAndPort = ConnectionStringParser.parseHostPortFromURL(domainUrl);
        Assert.assertEquals("test.db.com", domainAndPort.getHost());
        Assert.assertEquals(1433, domainAndPort.getPort().intValue());
    }

}
