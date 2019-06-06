package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.*;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by shenjie on 2019/5/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TitanReadPluginTest {

    private static final String FAT_ENV = "fat";
    private static final String UAT_ENV = "uat";
    private static final String PRO_ENV = "pro";
    private static final String EXIST_TITAN_KEY = "dalclusterdemodb_w";
    private static final String ABSENT_TITAN_KEY = "titantest_shenjie_v_1212_absent";
    private static final String DB_NAME = "dalclusterdemodb";

    @Autowired
    private TitanPluginService titanPluginService;

    @Test
    public void getTitanKey() throws Exception {
        // get exist titan key
        TitanKeyGetResponse titanKeyGetResponse = titanPluginService.getTitanKey(EXIST_TITAN_KEY, PRO_ENV);
        assert titanKeyGetResponse != null;
        assert titanKeyGetResponse.getStatus() == 0;
        assert titanKeyGetResponse.getData() != null;

        // get absent titan key
        titanKeyGetResponse = titanPluginService.getTitanKey(ABSENT_TITAN_KEY, PRO_ENV);
        assert titanKeyGetResponse != null;
        assert titanKeyGetResponse.getStatus() == 0;
        assert titanKeyGetResponse.getData() == null;
    }

    @Test
    public void getSslCode() throws Exception {
        SslCodeGetResponse sslCodeGetResponse = titanPluginService.getSslCode(PRO_ENV);
        assert sslCodeGetResponse != null;
        assert sslCodeGetResponse.getStatus() == 0;
        assert Strings.isNotBlank(sslCodeGetResponse.getData());
    }

    @Test
    public void listTitanKey() throws Exception {
        String pageNo = "1";
        String pageSize = "100";
        TitanKeyListResponse response = titanPluginService.listTitanKey(PRO_ENV, pageNo, pageSize);
        assert response != null;
        assert response.getStatus() == 0;
        List<Object> data = response.getData().getData();
        assert data != null && data.size() > 0;
        System.out.println("listTitanKey:" + response.getData().getData().size());
    }

    @Test
    public void listTitanKeyByTime() throws Exception {
        // list 3 day ago titan keys
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -3); //设置为3天前
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String beginTime = dateFormat.format(calendar.getTime());

        PluginResponse response = titanPluginService.listTitanKeyByTime(PRO_ENV, beginTime);
        assert response != null;
        assert response.getStatus() == 0;
        List<SiteOutputEntity> data = (ArrayList<SiteOutputEntity>) response.getData();
        assert data != null && data.size() > 0;
        System.out.println("listTitanKeyByTime:" + data.size());
    }

    @Test
    public void listAllDBName() throws Exception {
        PluginResponse response = titanPluginService.listAllDBName(PRO_ENV);
        assert response != null;
        assert response.getStatus() == 0;
        List<String> data = (ArrayList<String>) response.getData();
        assert data != null && data.size() > 0;
    }

    @Test
    public void listTitanKeyByDBName() throws Exception {
        PluginResponse response = titanPluginService.listTitanKeyByDBName(PRO_ENV, DB_NAME);
        assert response != null;
        assert response.getStatus() == 0;
        List<String> data = (ArrayList<String>) response.getData();
        assert data != null && data.size() > 0;
    }

}
