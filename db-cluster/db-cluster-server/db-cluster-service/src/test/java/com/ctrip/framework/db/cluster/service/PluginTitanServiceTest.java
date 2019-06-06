package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.domain.TitanAddRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by shenjie on 2019/3/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PluginTitanServiceTest {

    @Autowired
    private PluginTitanService pluginTitanService;

    @Test
    public void add() throws Exception {
        TitanAddRequest titanKey = TitanAddRequest.builder()
                .keyName("titantest_shenjie_v_26")
                .providerName("MySql.Data.MySqlClient")
                .serverName("mysqldaltest01.mysql.db.fat.qa.nt.ctripcorp.com")
                .serverIp("10.2.74.111")
                .port("55111")
                .uid("tt_daltest1_2")
                .password("k4AvZUIdDAcbyUvLirWG")
                .dbName("mysqldaltest01db")
                .timeOut(15)
                .enabled(true)
                .createUser("shenjie")
                .updateUser("shenjie")
                .build();
        PluginResponse response = pluginTitanService.add(titanKey, "fat");
        assert response.getStatus() == 0;

    }

}