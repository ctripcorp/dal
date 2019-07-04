package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.RC4Utils;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenjie on 2019/4/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TitanKeyTest {

    public static final String FAT_ENV = "fat";
    public static final String UAT_ENV = "uat";
    public static final String PRO_ENV = "pro";
    public static final String OPERATOR = "titanTest";
    public static final String TITAN_KEY = "titantest_shenjie_v_01";
    public static final String ABSENT_TITAN_KEY = "titantest_shenjie_v_03_absent";

    @Autowired
    private TitanPluginService titanPluginService;

    @Test
    public void addTitanKey() throws Exception {
        TitanKeyEntity titanKeyEntity = generateTitanKey();
        PluginResponse response = titanPluginService.addTitanKey(titanKeyEntity, FAT_ENV);
        assert response.getStatus() == 0;

        // get titan key
        TitanKeyGetResponse titanKeyGetResponse = titanPluginService.getTitanKey(TITAN_KEY, FAT_ENV);
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

    @Test
    public void updateTitanKey() throws Exception {
        String serverIp = "";
        TitanUpdateData data = TitanUpdateData.builder()
                .keyName(TITAN_KEY)
                .server(serverIp)
                .port(55111)
                .build();
        TitanKeyUpdateRequest request = TitanKeyUpdateRequest.builder()
                .env(FAT_ENV)
                .data(Lists.newArrayList(data))
                .build();

        PluginResponse response = titanPluginService.updateTitanKey(request, OPERATOR);
        assert response.getStatus() == 0;

        // get titan key
        TitanKeyGetResponse titanKeyGetResponse = titanPluginService.getTitanKey(TITAN_KEY, FAT_ENV);
        assert titanKeyGetResponse.getStatus() == 0;

        TitanKeyGetOutputEntity entity = titanKeyGetResponse.getData();
        assert TITAN_KEY.equalsIgnoreCase(entity.getKeyName());
        assert serverIp.equalsIgnoreCase(entity.getServerIp());
    }

    @Test
    public void getAbsentTitanKey() throws Exception {
        TitanKeyGetResponse titanKeyGetResponse = titanPluginService.getTitanKey(ABSENT_TITAN_KEY, FAT_ENV);
        assert titanKeyGetResponse.getStatus() == 0;
        assert titanKeyGetResponse.getData() == null;
    }

    @Test
    public void listTitanKey() throws Exception {
        String pageNo = "1";
        String pageSize = "100";
        TitanKeyListResponse response = titanPluginService.listTitanKey(FAT_ENV, pageNo, pageSize);
        assert response.getStatus() == 0;
        assert response.getData().getData().size() > 0;
        System.out.println("listTitanKey:" + response.getData().getData().size());
    }

    @Test
    public void listTitanKeyByTime() throws Exception {
        String beginTime = "2019-01-01 00:00:00";
        PluginResponse response = titanPluginService.listTitanKeyByTime(FAT_ENV, beginTime);
        List<SiteOutputEntity> data = (ArrayList<SiteOutputEntity>) response.getData();
        assert response.getStatus() == 0;
        assert data.size() > 0;
        System.out.println("listTitanKeyByTime:" + data.size());
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

    private TitanKeyEntity generateTitanKeyTest1() {
        TitanKeyEntity titanKeyEntity = TitanKeyEntity.builder()
                .keyName("mysqldaltest01db_W")
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

        return titanKeyEntity;
    }

    private TitanKeyEntity generateTitanKeyTest2() {
        TitanKeyEntity titanKeyEntity = TitanKeyEntity.builder()
                .keyName("mysqldaltest02db_W")
                .providerName("MySql.Data.MySqlClient")
                .serverName("mysqldaltest02.mysql.db.fat.qa.nt.ctripcorp.com")
//                .serverIp("10.2.74.111")
                .port("55111")
                .uid("tt_daltest02_2")
                .password("TpBMuSGYOTW24eQF0HAt")
                .dbName("mysqldaltest02db")
                .timeOut(15)
                .enabled(true)
                .createUser("shenjie")
                .updateUser("shenjie")
                .build();

        return titanKeyEntity;
    }

    //    @Test
    public void addTitanKeyForTest() throws Exception {
        TitanKeyEntity titanKeyEntity = generateTitanKeyTest1();
        PluginResponse response = titanPluginService.addTitanKey(titanKeyEntity, FAT_ENV);
        assert response.getStatus() == 0;

        TitanKeyEntity titanKeyEntity2 = generateTitanKeyTest2();
        PluginResponse response2 = titanPluginService.addTitanKey(titanKeyEntity2, FAT_ENV);
        assert response2.getStatus() == 0;
    }

    //    @Test
    public void updateTitanKeyForTest() throws Exception {
        String serverIp = "10.2.74.111";
        TitanUpdateData data = TitanUpdateData.builder()
                .keyName(generateTitanKeyTest1().getKeyName())
                .server(serverIp)
                .port(55111)
                .build();
        TitanKeyUpdateRequest request = TitanKeyUpdateRequest.builder()
                .env(FAT_ENV)
                .data(Lists.newArrayList(data))
                .build();

        PluginResponse response = titanPluginService.updateTitanKey(request, OPERATOR);
        assert response.getStatus() == 0;
    }

}