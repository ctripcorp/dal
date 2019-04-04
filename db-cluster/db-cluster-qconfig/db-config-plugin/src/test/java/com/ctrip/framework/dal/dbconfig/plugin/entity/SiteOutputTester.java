package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.ConnectionInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.SiteOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.google.common.base.Strings;
import org.junit.Test;

/**
 * Created by lzyan on 2017/09/04.
 */
public class SiteOutputTester {

    /**
     *
     {
        "id": 1,
        "name": "testKey",
        "subEnv": "fat2",
        "enabled": true,
        "connectionString": "Server\u003dlocalhost,28747;UID\u003dtestUid;password\u003dtestPwd;database\u003dtestDB;useUnicode\u003dtrue;",
        "sslCode": "VZ00000000000441",
        "providerName": "MySql.Data.MySqlClient",
        "timeOut": 10,
        "createUser": "lzyan",
        "updateUser": "lzyan",
        "whiteList":"111111,222222",
        "blackList":"333333,444444",
        "permissions":"555555,666666",
        "freeVerifyIpList":"127.1.1.1",
        "freeVerifyAppIdList":"100001681",
        "mhaLastUpdateTime":"2018-12-27 14:40:00",
        "connectionInfo": {
             "server": "localhost",
             "serverIp": "127.0.0.1",
             "port": "28747",
             "uid": "testUid",
             "password": "testPwd",
             "dbName": "testDB",
             "extParam": "useUnicode\u003dtrue;"
        }
     }
      */
    @Test
    public void testObj2Json(){
        SiteOutputEntity soe = buildSiteOutputEntity();
        String soeStr = GsonUtils.t2Json(soe);
        System.out.println(soeStr);
        assert(!Strings.isNullOrEmpty(soeStr));
    }

    @Test
    public void testJson2Obj(){
        String input = "{\"id\":1,\"name\":\"testKey\",\"subEnv\":\"fat2\",\"enabled\":true,\"connectionString\":\"Server\\u003dlocalhost;port\\u003d28747;UID\\u003dtestUid;password\\u003dtestPwd;database\\u003dtestDB;useUnicode\\u003dtrue;\",\"sslCode\":\"VZ00000000000441\",\"providerName\":\"MySql.Data.MySqlClient\",\"timeOut\":10,\"createUser\":\"lzyan\",\"updateUser\":\"lzyan\",\"whiteList\":\"111111,222222\",\"blackList\":\"333333,444444\",\"permissions\":\"555555,666666\", \"freeVerifyIpList\":\"127.1.1.1\",\"freeVerifyAppIdList\":\"100001681\",\"mhaLastUpdateTime\":\"2018-12-27 14:40:00\",\"connectionInfo\":{\"server\":\"localhost\",\"serverIp\":\"127.0.0.1\",\"port\":\"28747\",\"uid\":\"testUid\",\"password\":\"testPwd\",\"dbName\":\"testDB\",\"extParam\":\"useUnicode\\u003dtrue;\"}}";
        SiteOutputEntity siteOutputEntity = GsonUtils.json2T(input,  SiteOutputEntity.class);
        System.out.println("siteOutputEntity=\n" + siteOutputEntity);
        assert(siteOutputEntity != null);
    }



    //build test obj
    private SiteOutputEntity buildSiteOutputEntity(){
        Integer id = 1;
        String name = "testKey";
        String subEnv = "fat2";
        Boolean enabled = Boolean.valueOf(true);
        String connectionString = "333333,444444";
        String sslCode = "VZ00000000000441";
        String providerName = "MySql.Data.MySqlClient";
        Integer timeOut = 10;
        String createUser = "lzyan";
        String updateUser = createUser;
        String whiteList = "111111,222222";
        String blackList = "333333,444444";
        String permissions = "555555,666666";
        String freeVerifyIpList = "127.1.1.1";
        String freeVerifyAppIdList = "100001681";
        String mhaLastUpdateTime = "2018-12-27 14:40:00";

        String server = "localhost";
        String serverIp = "127.0.0.1";
        String port = "28747";
        String uid = "testUid";
        String password = "testPwd";
        String dbName = "testDB";
        String extParam = "useUnicode=true;";

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setServer(server)
                .setServerIp(serverIp)
                .setPort(port)
                .setUid(uid)
                .setPassword(password)
                .setDbName(dbName)
                .setExtParam(extParam);


        SiteOutputEntity siteOutputEntity = new SiteOutputEntity(id, name, subEnv, enabled, connectionString, sslCode,
                providerName, timeOut, createUser, updateUser, whiteList, blackList, permissions, freeVerifyIpList,
                freeVerifyAppIdList, mhaLastUpdateTime, connectionInfo);
        return siteOutputEntity;

    }


}
