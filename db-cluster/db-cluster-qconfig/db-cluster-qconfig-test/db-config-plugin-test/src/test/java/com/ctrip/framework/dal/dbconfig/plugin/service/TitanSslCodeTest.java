package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.RC4Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * Created by shenjie on 2019/4/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TitanSslCodeTest {
    public static final String ENV = "fat";
    public static final String OPERATOR = "titanTest";
    public static final String TITAN_KEY = "titantest_shenjie_v_03";
    public static final String FWS_SSL_CODE = "VZ00000000000441";
    public static final String UPDATE_SSL_CODE = "HN00000000002356";
    public static final String BAD_SSL_CODE = "HN111111111112356";

    @Autowired
    private TitanPluginService titanPluginService;

    @Test
    public void getSslCode() throws Exception {
        SslCodeGetResponse sslCodeGetResponse = titanPluginService.getSslCode(ENV);
        assert sslCodeGetResponse.getStatus() == 0;
    }

    @Test
    public void updateSslCode() throws Exception {
        PluginResponse response = titanPluginService.updateSslCode(UPDATE_SSL_CODE, ENV, OPERATOR);
        assert response.getStatus() == 0;

        // get sslCode
        SslCodeGetResponse sslCodeGetResponse = titanPluginService.getSslCode(ENV);
        assert sslCodeGetResponse.getStatus() == 0;
        assert UPDATE_SSL_CODE.equalsIgnoreCase(sslCodeGetResponse.getData());

        // sslCode改回去
        PluginResponse response2 = titanPluginService.updateSslCode(FWS_SSL_CODE, ENV, OPERATOR);
        assert response2.getStatus() == 0;

        SslCodeGetResponse sslCodeGetResponse2 = titanPluginService.getSslCode(ENV);
        assert sslCodeGetResponse2.getStatus() == 0;
        assert FWS_SSL_CODE.equalsIgnoreCase(sslCodeGetResponse2.getData());
    }

    @Test
    public void updateBadSslCode() throws Exception {
        PluginResponse response = titanPluginService.updateSslCode(BAD_SSL_CODE, ENV, OPERATOR);
        assert response.getStatus() != 0;
    }

    @Test
    public void washTitanKey() throws Exception {
        PluginResponse response = titanPluginService.washTitanKey(ENV, OPERATOR);
        assert response.getStatus() == 0;
    }

    @Test
    public void testWash() throws Exception {
        // wash
        wash(UPDATE_SSL_CODE);
        // wash back
        wash(FWS_SSL_CODE);
    }

    public void wash(String sslCode) throws Exception {
        // update sslcode
        PluginResponse response = titanPluginService.updateSslCode(sslCode, ENV, OPERATOR);
        assert response.getStatus() == 0;

        // get sslCode
        SslCodeGetResponse sslCodeGetResponse = titanPluginService.getSslCode(ENV);
        assert sslCodeGetResponse.getStatus() == 0;
        assert sslCode.equalsIgnoreCase(sslCodeGetResponse.getData());

        // wash titan key
        PluginResponse response2 = titanPluginService.washTitanKey(ENV, OPERATOR);
        assert response2.getStatus() == 0;

        // 等待洗数据的结束
        TimeUnit.MINUTES.sleep(2);

        // get titan key
        TitanKeyGetResponse titanKeyGetResponse = titanPluginService.getTitanKey(TITAN_KEY, ENV);
        assert titanKeyGetResponse.getStatus() == 0;

        TitanKeyGetOutputEntity entity = titanKeyGetResponse.getData();
        TitanKeyEntity titanKey = generateTitanKey();
        assert titanKey.getKeyName().equalsIgnoreCase(entity.getKeyName());
        assert titanKey.getServerName().equalsIgnoreCase(entity.getServerName());
        assert titanKey.getServerIp().equalsIgnoreCase(entity.getServerIp());
        assert titanKey.getDbName().equalsIgnoreCase(entity.getDbName());
        assert titanKey.getUid().equalsIgnoreCase(entity.getUid());
        assert titanKey.getPassword().equalsIgnoreCase(RC4Utils.decrypt(entity.getPassword()));
    }

    private TitanKeyEntity generateTitanKey() {
        TitanKeyEntity titanKeyEntity = TitanKeyEntity.builder()
                .keyName(TITAN_KEY)
                .providerName("MySql.Data.MySqlClient")
                .serverName("mysqldaltest01.mysql.db.fat.qa.nt.ctripcorp.com")
                .serverIp("")
                .port("55111")
                .uid("tt_daltest1_2")
                .password("k4AvZUIdDAcbyUvLirWG")
                .dbName("mysqldaltest01db")
                .timeOut(15)
                .enabled(true)
                .createUser("shenjie")
                .updateUser("shenjie")
                .build();

        return titanKeyEntity;
    }
}
