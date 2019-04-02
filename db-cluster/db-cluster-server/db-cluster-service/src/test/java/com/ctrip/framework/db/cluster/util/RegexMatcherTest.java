package com.ctrip.framework.db.cluster.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by shenjie on 2019/4/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RegexMatcherTest {

    @Autowired
    private RegexMatcher regexMatcher;

    @Test
    public void password() throws Exception {
        String passWord = "qwe123";
        Assert.assertEquals(true, regexMatcher.password(passWord));
        String pass_word = "test_123";
        Assert.assertEquals(true, regexMatcher.password(pass_word));

        String passWordFail = "test!123";
        Assert.assertEquals(false, regexMatcher.password(passWordFail));
        String passWordChinese = "密码";
        Assert.assertEquals(false, regexMatcher.password(passWordChinese));
        String nullPassWord = "";
        Assert.assertEquals(false, regexMatcher.password(nullPassWord));
    }

    @Test
    public void userId() throws Exception {
        String allEnglsh = "testName";
        Assert.assertEquals(true, regexMatcher.userId(allEnglsh));
        String midLineEng = "test_name";
        Assert.assertEquals(true, regexMatcher.userId(midLineEng));
        String illegalChart = "qwe!1";
        Assert.assertEquals(false, regexMatcher.userId(illegalChart));
        String chineseName = "中文";
        Assert.assertEquals(false, regexMatcher.userId(chineseName));
        String nullstr = "";
        Assert.assertEquals(false, regexMatcher.userId(nullstr));
        String name = "qwe_123";
        Assert.assertEquals(false, regexMatcher.userId(name));
    }

    @Test
    public void host() throws Exception {
        String host = "bridge.soa.uat.qa.nt.ctripcorp.com";
        Assert.assertEquals(true, regexMatcher.host(host));
        String hosteNameBai = "www.baidu.com";
        Assert.assertEquals(true, regexMatcher.host(hosteNameBai));

        String host1 = "asaaswe";
        Assert.assertEquals(false, regexMatcher.host(host1));

        String host2 = "127.0.0.1";
        Assert.assertEquals(true, regexMatcher.host(host2));

        String nullStr = "";
        Assert.assertEquals(false, regexMatcher.host(nullStr));

        String dbaHost = "micecustomize02.mongo.db.ctripcorp.com";
        Assert.assertEquals(true, regexMatcher.host(dbaHost));
    }

    @Test
    public void hostName() throws Exception {
        String hostName = "bridge.soa.uat.qa.nt.ctripcorp.com";
        Assert.assertEquals(true, regexMatcher.hostName(hostName));
        String hosteNameBai = "www.baidu.com";
        Assert.assertEquals(true, regexMatcher.hostName(hosteNameBai));

        String hostName1 = "asaaswe";
        Assert.assertEquals(false, regexMatcher.hostName(hostName1));
        String nullStr = "";
        Assert.assertEquals(false, regexMatcher.hostName(nullStr));

        String dbaHostName = "micecustomize02.mongo.db.ctripcorp.com";
        Assert.assertEquals(true, regexMatcher.hostName(dbaHostName));
    }

    @Test
    public void ipv6() throws Exception {
        String ipv60 = "0000:0000:0000:0000:0000:0000:0000:0000";
        Assert.assertEquals(true, regexMatcher.ipv6(ipv60));
        String ipv6F = "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff";
        Assert.assertEquals(true, regexMatcher.ipv6(ipv6F));
        String ipveL = "1050:0:0:0:5:600:300c:326b";
        Assert.assertEquals(true, regexMatcher.ipv6(ipveL));

        String ipV6NegativeNum = "-1000:0000:0000:0000:0000:0000:0000:0000";
        Assert.assertEquals(false, regexMatcher.ipv6(ipV6NegativeNum));
        String ipV6OverF = "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffgf";
        Assert.assertEquals(false, regexMatcher.ipv6(ipV6OverF));
        String lessLen = "1050:0:0:0:5:600:300c";
        Assert.assertEquals(false, regexMatcher.ipv6(lessLen));
        String overLen = "1050:0:0:0:5:600:300c:326b:300c";
        Assert.assertEquals(false, regexMatcher.ipv6(overLen));
        String nullStr = "";
        Assert.assertEquals(false, regexMatcher.ipv6(nullStr));
    }

    @Test
    public void ipv4() throws Exception {
        String ipv40 = "0.0.0.0";
        Assert.assertEquals(true, regexMatcher.ipv4(ipv40));
        String ipv4255 = "255.255.255.255";
        Assert.assertEquals(true, regexMatcher.ipv4(ipv4255));
        String ipv4Locl = "10.32.20.8";
        Assert.assertEquals(true, regexMatcher.ipv4(ipv4Locl));

        String ipv4256 = "255.255.255.256";
        Assert.assertEquals(false, regexMatcher.ipv4(ipv4256));
        String ipv4NegateNum = "-1.0.23.29";
        Assert.assertEquals(false, regexMatcher.ipv4(ipv4NegateNum));
        String lesLen = "1.1.1";
        Assert.assertEquals(false, regexMatcher.ipv4(lesLen));
        String overLen = "1.1.1.1.1.1";
        Assert.assertEquals(false, regexMatcher.ipv4(overLen));
        String nullStr = "";
        Assert.assertEquals(false, regexMatcher.ipv4(nullStr));
    }

    @Test
    public void port() throws Exception {
        String minPort = "1";
        Assert.assertEquals(true, regexMatcher.port(minPort));
        String maxPort = "65535";
        Assert.assertEquals(true, regexMatcher.port(maxPort));
        String port = "8080";
        Assert.assertEquals(true, regexMatcher.port(port));

        String portStr = "test";
        Assert.assertEquals(false, regexMatcher.port(portStr));
        String overMaxPort = "65536";
        Assert.assertEquals(false, regexMatcher.port(overMaxPort));
        String lesMinPort = "0";
        Assert.assertEquals(false, regexMatcher.port(lesMinPort));
        String nullStr = "";
        Assert.assertEquals(false, regexMatcher.port(nullStr));
    }

    @Test
    public void clusterType() throws Exception {
        String shard = "Sharding";
        Assert.assertEquals(true, regexMatcher.clusterType(shard));
        String repli = "Replication";
        Assert.assertEquals(true, regexMatcher.clusterType(repli));

        String num = "123";
        Assert.assertEquals(false, regexMatcher.clusterType(num));
        String chart = "!@#$%";
        Assert.assertEquals(false, regexMatcher.clusterType(chart));
        String nullStr = "";
        Assert.assertEquals(false, regexMatcher.clusterType(nullStr));
    }

    @Test
    public void dbName() throws Exception {
        String normal = "testDB";
        Assert.assertEquals(true, regexMatcher.dbName(normal));

        String over20 = "testDBtestDBtestDBtestDB";
        Assert.assertEquals(true, regexMatcher.dbName(over20));
        String num = "1222";
        Assert.assertEquals(false, regexMatcher.dbName(num));
        String nullDB = "";
        Assert.assertEquals(false, regexMatcher.dbName(nullDB));
    }

    @Test
    public void clusterName() throws Exception {
        String normal = "diuserprofile-diuserprofiledb";
        Assert.assertEquals(true, regexMatcher.clusterName(normal));

        String dbNameBig = "diuserprofile-diuserprofilEdb";
        Assert.assertEquals(true, regexMatcher.clusterName(dbNameBig));

        String cluserBig = "dIuserprofile-diuserprofiledb";
        Assert.assertEquals(false, regexMatcher.clusterName(cluserBig));


        String noMidLine = "diuserprofilediuserprofiledb";
        Assert.assertEquals(false, regexMatcher.clusterName(noMidLine));
    }

    @Test
    public void testAll() throws Exception {
        // 数据库为纯英文，字符小于20个
        String dbName = "TestDB";
        System.err.println("数据库名称判断，命名少于20个字符，纯英文：" + regexMatcher.dbName(dbName));
        String clusterInfo = "replication";
        // 集群类型只有两种，直接判断是不是这两种， 只能为Sharding或者是Replication
        System.err.println("判断集群类型名称，只能为Sharding或者是Replication：" + regexMatcher.clusterType(clusterInfo));
        String port = "80";
        // 端口判断需要去除系统常用端口，如果不考虑就是1-65544
        System.err.println("判断端口是否合法：" + regexMatcher.port(port));

        String ipv4 = "0.0.111.255";
        System.err.println("判断是不是为IPV4:" + regexMatcher.ipv4(ipv4));

        String ipv6 = "FFFF:FFFF:FFFF:FFF12111111:FFFF:FFFF:FFFF:FFFF";
        System.err.println("判断是不是为IPV6：" + regexMatcher.ipv6(ipv6));

        String hostName = "http://bridge.soa.uat.qa.nt.ctripcorp.com/qconfig/restapi/configs";
        System.err.println("判断是不是为合法域名：" + regexMatcher.hostName(hostName));

        // 字母开头，允许3-20字节，允许字母数字下划线)
        String userName = "tpx";
        System.err.println("判断用户名是否合法： " + regexMatcher.userId(userName));

        // 字母和数字，长度不限
        String weakPassWord = "qq1111";
        System.err.println("判断密码是否合法： " + regexMatcher.password(weakPassWord));
    }

}