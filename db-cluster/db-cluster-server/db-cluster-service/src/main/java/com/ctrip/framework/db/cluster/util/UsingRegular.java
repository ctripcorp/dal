package com.ctrip.framework.db.cluster.util;

/**
 * Hello world!
 *
 * @author pcxie
 */
public class UsingRegular {
    public static void main(String[] args) {
        //        数据库为纯英文，字符小于20个
        String dbName = "TestDB";
        System.err.println("数据库名称判断，命名少于20个字符，纯英文：" + CMongoRegularUtil.dbName(dbName));
        String clusterInfo = "replication";
//        集群类型只有两种，直接判断是不是这两种， 只能为Sharding或者是Replication
        System.err.println("判断集群类型名称，只能为Sharding或者是Replication：" + CMongoRegularUtil.clusterType(clusterInfo));
        String port = "80";
//        端口判断需要去除系统常用端口，如果不考虑就是1-65544
        System.err.println("判断端口是否合法：" + CMongoRegularUtil.judgePort(port));

        String ipv4 = "0.0.111.255";
        System.err.println("判断是不是为IPV4:" + CMongoRegularUtil.judgeIPV4(ipv4));

        String ipv6 = "FFFF:FFFF:FFFF:FFF12111111:FFFF:FFFF:FFFF:FFFF";
        System.err.println("判断是不是为IPV6：" + CMongoRegularUtil.judgeIPV6(ipv6));

        String hostName = "http://bridge.soa.uat.qa.nt.ctripcorp.com/qconfig/restapi/configs";
        System.err.println("判断是不是为合法域名：" + CMongoRegularUtil.hostName(hostName));

//        字母开头，允许3-20字节，允许字母数字下划线)
        String userName = "tpx";
        System.err.println("判断用户名是否合法： " + CMongoRegularUtil.userid(userName));

//        字母和数字，长度不限
        String weakPassWord = "qq1111";
        System.err.println("判断密码是否合法： " + CMongoRegularUtil.password(weakPassWord));
    }
}
