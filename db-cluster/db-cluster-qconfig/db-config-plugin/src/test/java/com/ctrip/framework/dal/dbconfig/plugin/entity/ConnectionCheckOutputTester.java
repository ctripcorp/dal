package com.ctrip.framework.dal.dbconfig.plugin.entity;

import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.ConnectionCheckOutputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.google.common.base.Strings;
import org.junit.Test;

/**
 * Created by lzyan on 2017/09/11.
 */
public class ConnectionCheckOutputTester {

    @Test
    public void testObj2Json(){
        boolean success = true;
        String message = "ok";

        ConnectionCheckOutputEntity connectionCheckOutputEntity = new ConnectionCheckOutputEntity(success, message);
        String ConnectionCheckOutput = GsonUtils.t2Json(connectionCheckOutputEntity);
        System.out.println(ConnectionCheckOutput);
        assert(!Strings.isNullOrEmpty(ConnectionCheckOutput));
    }

    @Test
    public void testJson2Obj(){
        String connectionCheckOutput = "{\"success\":true,\"message\":\"ok\"}";
        ConnectionCheckOutputEntity connectionCheckOutputEntity = GsonUtils.json2T(connectionCheckOutput,  ConnectionCheckOutputEntity.class);
        System.out.println("connectionCheckOutputEntity=\n" + connectionCheckOutputEntity);
        assert(connectionCheckOutputEntity != null);
    }

}
