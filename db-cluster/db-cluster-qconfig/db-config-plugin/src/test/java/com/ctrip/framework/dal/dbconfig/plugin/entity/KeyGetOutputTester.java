package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.KeyGetOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.google.common.base.Strings;
import org.junit.Test;

/**
 * Created by lzyan on 2017/09/21.
 */
public class KeyGetOutputTester {
    /**
     *
     {
         "subEnv": "fat1",
         "keyName": "testKey",
         "providerName": "MySql.Data.MySqlClient",
         "serverName": "127.0.0.1",
         "serverIp": "127.0.0.1",
         "port": "28747",
         "uid": "testUid",
         "password": "testPwd",
         "dbName": "testDB",
         "extParam": "useUnicode\u003dtrue;",
         "timeOut": 10,
         "sslCode": "VZ00000000000441",
         "enabled": true,
         "createUser": "testUser",
         "updateUser": "testUser",
         "whiteList": "111111,222222",
         "blackList": "333333,444444",
         "id": 1,
         "permissions":"555555,666666",
         "freeVerifyIpList":"127.1.1.1",
         "freeVerifyAppIdList":"100001681",
         "mhaLastUpdateTime":"2018-12-27 14:40:00"
     }
     */
    @Test
    public void testObj2Json(){
        KeyGetOutputEntity sie = buildKeyGetOutputEntity();
        String sieStr = GsonUtils.t2Json(sie);
        System.out.println(sieStr);
        assert(!Strings.isNullOrEmpty(sieStr));
    }

    @Test
    public void testJson2Obj(){
        String mhaInput = "{\"subEnv\":\"fat1\",\"keyName\":\"testKey\",\"providerName\":\"MySql.Data.MySqlClient\",\"serverName\":\"127.0.0.1\",\"serverIp\":\"127.0.0.1\",\"port\":\"28747\",\"uid\":\"testUid\",\"password\":\"testPwd\",\"dbName\":\"testDB\",\"extParam\":\"useUnicode\\u003dtrue;\",\"timeOut\":10,\"sslCode\":\"VZ00000000000441\",\"enabled\":true,\"createUser\":\"testUser\",\"updateUser\":\"testUser\",\"whiteList\":\"111111,222222\",\"blackList\":\"333333,444444\",\"id\":1,\"permissions\":\"555555,666666\", \"freeVerifyIpList\":\"127.1.1.1\",\"freeVerifyAppIdList\":\"100001681\",\"mhaLastUpdateTime\":\"2018-12-27 14:40:00\"}";
        KeyGetOutputEntity keyGetOutputEntity = GsonUtils.json2T(mhaInput,  KeyGetOutputEntity.class);
        System.out.println("keyGetOutputEntity=\n" + keyGetOutputEntity);
        assert(keyGetOutputEntity != null);
    }


    //build test obj
    private KeyGetOutputEntity buildKeyGetOutputEntity(){
        String subEnv = "fat1";
        String keyName = "testKey";
        String providerName = "MySql.Data.MySqlClient";
        String serverName = "127.0.0.1";
        String serverIp = serverName;
        String port = "28747";
        String uid = "testUid";
        String password = "testPwd";
        String dbName = "testDB";
        String extParam = "useUnicode=true;";
        Integer timeOut = 10;
        String sslCode = "VZ00000000000441";
        boolean enabled = true;
        String createUser = "testUser";
        String updateUser = "testUser";
        String whiteList = "111111,222222";
        String blackList = "333333,444444";
        Integer id = 1;
        String permissions = "555555,666666";
        String freeVerifyIpList = "127.1.1.1";
        String freeVerifyAppIdList = "100001681";
        String mhaLastUpdateTime = "2018-12-27 14:40:00";

        KeyGetOutputEntity keyGetOutputEntity = new KeyGetOutputEntity(
                subEnv, keyName, providerName, serverName, serverIp, port, uid, password, dbName,
                extParam, timeOut, sslCode, enabled, createUser,
                updateUser, whiteList, blackList, id, permissions, freeVerifyIpList, freeVerifyAppIdList,
                mhaLastUpdateTime);
        return keyGetOutputEntity;

    }


}
