package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.ConnectionCheckInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.google.common.base.Strings;
import org.junit.Test;

/**
 * Created by lzyan on 2017/09/11.
 */
public class ConnectionCheckInputTester {

    @Test
    public void testObj2Json(){
        String dbType = TitanConstants.NAME_MYSQL;    //mysql, sqlserver
        String env = "uat";       //pro, uat, lpt, fat
        String host = "127.0.0.1";
        int port = 58551;
        String user = "testUser";
        String password = "testPwd";
        String dbName = "testDb";

        ConnectionCheckInputEntity connectionCheckInputEntity = new ConnectionCheckInputEntity(dbType, env, host, port, user, password, dbName);
        String mhaInput = GsonUtils.t2Json(connectionCheckInputEntity);
        System.out.println(mhaInput);
        assert(!Strings.isNullOrEmpty(mhaInput));
    }

    @Test
    public void testJson2Obj(){
        String connectionCheckInput = "{\"dbtype\":\"mysql\",\"env\":\"uat\",\"host\":\"127.0.0.1\",\"port\":58551,\"user\":\"testUser\",\"password\":\"testPwd\",\"dbname\":\"testDb\"}";
        ConnectionCheckInputEntity connectionCheckInputEntity = GsonUtils.json2T(connectionCheckInput,  ConnectionCheckInputEntity.class);
        System.out.println("connectionCheckInputEntity=\n" + connectionCheckInputEntity);
        assert(connectionCheckInputEntity != null);
    }

}
