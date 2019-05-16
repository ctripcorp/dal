package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.PluginResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenjie on 2019/4/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TitanIndexTest {
    public static final String FAT_ENV = "fat";
    public static final String UAT_ENV = "uat";
    public static final String PRO_ENV = "pro";
    public static final String DB_NAME = "mysqldaltest01db";

    @Autowired
    private TitanPluginService titanPluginService;

    @Test
    public void buildFatIndex() throws Exception {
        PluginResponse response = titanPluginService.buildIndex(FAT_ENV);
        assert response.getStatus() == 0;
    }

    @Test
    public void buildUatIndex() throws Exception {
        PluginResponse response = titanPluginService.buildIndex(UAT_ENV);
        assert response.getStatus() != 0;
    }

    @Test
    public void listProAllDBName() throws Exception {
        PluginResponse response = titanPluginService.listAllDBName(PRO_ENV);
        List<String> data = (ArrayList<String>) response.getData();
        assert response.getStatus() == 0;
        assert data.size() > 0;
    }

    @Test
    public void listUatAllDBName() throws Exception {
        PluginResponse response = titanPluginService.listAllDBName(UAT_ENV);
        List<String> data = (ArrayList<String>) response.getData();
        assert response.getStatus() == 0;
        assert data.size() == 0;
    }

    @Test
    public void listProTitanKeyByDBName() throws Exception {
        PluginResponse response = titanPluginService.listTitanKeyByDBName(PRO_ENV, DB_NAME);
        List<String> data = (ArrayList<String>) response.getData();
        assert response.getStatus() == 0;
        assert data.size() > 0;
    }

    @Test
    public void listUatTitanKeyByDBName() throws Exception {
        PluginResponse response = titanPluginService.listTitanKeyByDBName(UAT_ENV, DB_NAME);
        List<String> data = (ArrayList<String>) response.getData();
        assert response.getStatus() == 0;
        assert data.size() == 0;
    }

}
