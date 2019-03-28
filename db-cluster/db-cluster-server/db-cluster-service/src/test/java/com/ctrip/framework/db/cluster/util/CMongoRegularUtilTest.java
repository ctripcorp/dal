package com.ctrip.framework.db.cluster.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * CMongoRegrexApplication Tester.
 *
 * @author <Authors name>
 * @since <pre>三月 25, 2019</pre>
 * @version 1.0
 */
public class CMongoRegularUtilTest {

    /**
     *
     * Method: password(String weakPassWord)
     *判断密码，里面只允许有数字和字母，长度至少6位
     */
    @Test
    public void testJudgeWeakPassword() throws Exception {
        String passWord = "qwe123";
        Assert.assertEquals(true, CMongoRegularUtil.password(passWord));
        String pass_word = "test_123";
        Assert.assertEquals(true, CMongoRegularUtil.password(pass_word));

        String passWordFail = "test!123";
        Assert.assertEquals(false, CMongoRegularUtil.password(passWordFail));
        String passWordChinese = "密码";
        Assert.assertEquals(false, CMongoRegularUtil.password(passWordChinese));
        String nullPassWord = "";
        Assert.assertEquals(false, CMongoRegularUtil.password(nullPassWord));

    }

    /**
     *
     * Method: userid(String userName)
     *字母开头，允许字母数字下划线)
     */
    @Test
    public void testJudgeUserName() throws Exception {
//TODO: Test goes here...

        String allEnglsh = "testName";
        Assert.assertEquals(true, CMongoRegularUtil.userid(allEnglsh));
        String midLineEng = "test_name";
        Assert.assertEquals(true, CMongoRegularUtil.userid(midLineEng));
        String illegalChart = "qwe!1";
        Assert.assertEquals(false, CMongoRegularUtil.userid(illegalChart));
        String chineseName = "中文";
        Assert.assertEquals(false, CMongoRegularUtil.userid(chineseName));
        String nullstr = "";
        Assert.assertEquals(false, CMongoRegularUtil.userid(nullstr));
        String name = "qwe_123";
        Assert.assertEquals(false, CMongoRegularUtil.userid(name));
    }

    /**
     *
     * Method: hostName(String hostName)
     *
     */
    @Test
    public void testJudgeHostName() throws Exception {
//TODO: Test goes here...
        String hostName = "bridge.soa.uat.qa.nt.ctripcorp.com";
        Assert.assertEquals(true, CMongoRegularUtil.hostName(hostName));
        String hosteNameBai = "www.baidu.com";
        Assert.assertEquals(true, CMongoRegularUtil.hostName(hosteNameBai));

        String hostName1 = "asaaswe";
        Assert.assertEquals(false, CMongoRegularUtil.hostName(hostName1));
        String nullStr = "";
        Assert.assertEquals(false, CMongoRegularUtil.hostName(nullStr));

        String dbaHostName = "micecustomize02.mongo.db.ctripcorp.com";
        Assert.assertEquals(true, CMongoRegularUtil.hostName(dbaHostName));
    }

    /**
     *
     * Method: judgeIPV6(String ipv6)
     * IPV6 范围是 0000:0000:0000:0000:0000:0000:0000:0000
     * 到 ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff
     */
    @Test
    public void testJudgeIPV6() throws Exception {
//TODO: Test goes here...
        String ipv60 = "0000:0000:0000:0000:0000:0000:0000:0000";
        Assert.assertEquals(true, CMongoRegularUtil.judgeIPV6(ipv60));
        String ipv6F = "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff";
        Assert.assertEquals(true, CMongoRegularUtil.judgeIPV6(ipv6F));
        String ipveL = "1050:0:0:0:5:600:300c:326b";
        Assert.assertEquals(true, CMongoRegularUtil.judgeIPV6(ipveL));

        String ipV6NegativeNum = "-1000:0000:0000:0000:0000:0000:0000:0000";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV6(ipV6NegativeNum));
        String ipV6OverF = "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffgf";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV6(ipV6OverF));
        String lessLen = "1050:0:0:0:5:600:300c";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV6(lessLen));
        String overLen = "1050:0:0:0:5:600:300c:326b:300c";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV6(overLen));
        String nullStr = "";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV6(nullStr));
    }

    /**
     *
     * Method: judgeIPV4(String ipv4)
     * 范围是0.0.0.0
     * 到255.255.255.255
     */
    @Test
    public void testJudgeIPV4() throws Exception {
//TODO: Test goes here...
        String ipv40 = "0.0.0.0";
        Assert.assertEquals(true, CMongoRegularUtil.judgeIPV4(ipv40));
        String ipv4255 = "255.255.255.255";
        Assert.assertEquals(true, CMongoRegularUtil.judgeIPV4(ipv4255));
        String ipv4Locl = "10.32.20.8";
        Assert.assertEquals(true, CMongoRegularUtil.judgeIPV4(ipv4Locl));

        String ipv4256 = "255.255.255.256";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV4(ipv4256));
        String ipv4NegateNum = "-1.0.23.29";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV4(ipv4NegateNum));
        String lesLen = "1.1.1";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV4(lesLen));
        String overLen = "1.1.1.1.1.1";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV4(overLen));
        String nullStr = "";
        Assert.assertEquals(false, CMongoRegularUtil.judgeIPV4(nullStr));
    }

    /**
     *
     * Method: judgePort(String port)
     * 端口号范围是1-65535
     */
    @Test
    public void testJudgePort() throws Exception {
//TODO: Test goes here...
        String minPort = "1";
        Assert.assertEquals(true, CMongoRegularUtil.judgePort(minPort));
        String maxPort = "65535";
        Assert.assertEquals(true, CMongoRegularUtil.judgePort(maxPort));
        String port = "8080";
        Assert.assertEquals(true, CMongoRegularUtil.judgePort(port));

        String portStr = "test";
        Assert.assertEquals(false, CMongoRegularUtil.judgePort(portStr));
        String overMaxPort = "65536";
        Assert.assertEquals(false, CMongoRegularUtil.judgePort(overMaxPort));
        String lesMinPort = "0";
        Assert.assertEquals(false, CMongoRegularUtil.judgePort(lesMinPort));
        String nullStr = "";
        Assert.assertEquals(false, CMongoRegularUtil.judgePort(nullStr));
    }


    /**
     *
     * Method: clusterType(String clusterInfo)
     * 集群类型只有两种，直接判断是不是这两种， 只能为Sharding或者是Replication
     */
    @Test
    public void testJudgeClusterInfo() throws Exception {
//TODO: Test goes here...
        String shard = "Sharding";
        Assert.assertEquals(true, CMongoRegularUtil.clusterType(shard));
        String repli = "Replication";
        Assert.assertEquals(true, CMongoRegularUtil.clusterType(repli));

        String num = "123";
        Assert.assertEquals(false, CMongoRegularUtil.clusterType(num));
        String chart = "!@#$%";
        Assert.assertEquals(false, CMongoRegularUtil.clusterType(chart));
        String nullStr = "";
        Assert.assertEquals(false, CMongoRegularUtil.clusterType(nullStr));
    }

    /**
     *
     * Method: dbName(String dbName)
     * 数据库为纯英文
     */
    @Test
    public void testJudgeDBName() throws Exception {
//TODO: Test goes here...
        String normal = "testDB";
        Assert.assertEquals(true, CMongoRegularUtil.dbName(normal));

        String over20 = "testDBtestDBtestDBtestDB";
        Assert.assertEquals(true, CMongoRegularUtil.dbName(over20));
        String num = "1222";
        Assert.assertEquals(false, CMongoRegularUtil.dbName(num));
        String nullDB = "";
        Assert.assertEquals(false, CMongoRegularUtil.dbName(nullDB));
    }

    @Test
    public void testClusterName() throws Exception {
        String normal = "diuserprofile-diuserprofiledb";
        Assert.assertEquals(true, CMongoRegularUtil.clusterName(normal));

        String dbNameBig = "diuserprofile-diuserprofilEdb";
        Assert.assertEquals(true, CMongoRegularUtil.clusterName(dbNameBig));

        String cluserBig = "dIuserprofile-diuserprofiledb";
        Assert.assertEquals(false, CMongoRegularUtil.clusterName(cluserBig));


        String noMidLine = "diuserprofilediuserprofiledb";
        Assert.assertEquals(false, CMongoRegularUtil.clusterName(noMidLine));


    }

} 
