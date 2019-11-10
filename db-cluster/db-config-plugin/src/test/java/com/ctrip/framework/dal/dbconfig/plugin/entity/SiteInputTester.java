package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.SiteInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.google.common.base.Strings;
import org.junit.Test;

import java.util.HashMap;

/**
 * Created by lzyan on 2017/8/23.
 */
public class SiteInputTester {

    @Test
    public void testObj2Json(){
        SiteInputEntity sie = buildSiteInputEntity();
        String sieStr = GsonUtils.t2Json(sie);
        System.out.println(sieStr);
        assert(!Strings.isNullOrEmpty(sieStr));
    }

    @Test
    public void testJson2Obj(){
        String input = "{\"id\":1,\"keyName\":\"testKey\",\"providerName\":\"MySql.Data.MySqlClient\",\"serverName\":\"localhost\",\"serverIp\":\"127.0.0.1\",\"port\":\"28747\",\"uid\":\"testUid\",\"password\":\"testPwd\",\"dbName\":\"testDB\",\"extParam\":\"useUnicode\\u003dtrue;\",\"timeOut\":10,\"sslCode\":\"VZ00000000000441\",\"enabled\":true,\"createUser\":\"testUser\",\"updateUser\":\"testUser\",\"whiteList\":\"111111,222222\",\"blackList\":\"333333,444444\",\"permissions\":\"555555,666666\", \"freeVerifyIpList\":\"127.1.1.1\",\"freeVerifyAppIdList\":\"100001681\",\"mhaLastUpdateTime\":\"2018-12-27 14:40:00\"}";
        SiteInputEntity siteInputEntity = GsonUtils.json2T(input,  SiteInputEntity.class);
        System.out.println("siteInputEntity=\n" + siteInputEntity);
        assert(siteInputEntity != null);
    }

    @Test
    public void testReflect() throws Exception {
        SiteInputEntity sie = buildSiteInputEntity();
        HashMap<String, Object> map = CommonHelper.getFieldMap(sie);
        System.out.println(map);
    }

    //build test obj
    private SiteInputEntity buildSiteInputEntity(){
        Integer id = 1;
        String keyName = "testKey";
        String providerName = "MySql.Data.MySqlClient";
        String serverName = "localhost";
        String serverIp = "127.0.0.1";
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
        String permissions = "555555,666666";
        String freeVerifyIpList = "127.1.1.1";
        String freeVerifyAppIdList = "100001681";
        String mhaLastUpdateTime = "2018-12-27 14:40:00";

        SiteInputEntity siteInputEntity = new SiteInputEntity(id, keyName, providerName, serverName, serverIp,
                port, uid, password, dbName, extParam, timeOut, sslCode, enabled, createUser,
                updateUser, whiteList, blackList, permissions, freeVerifyIpList, freeVerifyAppIdList,
                mhaLastUpdateTime);
        return siteInputEntity;

    }


}
