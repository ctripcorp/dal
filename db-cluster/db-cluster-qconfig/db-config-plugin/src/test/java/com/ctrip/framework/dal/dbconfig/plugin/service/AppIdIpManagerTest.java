package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.AppIdIpCheckEntity;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * Created by shenjie on 2019/6/11.
 */
public class AppIdIpManagerTest {

    private AppIdIpManager appIdIpManager = new AppIdIpManager();
    private static final String CMS_GET_GROUP_URL = "http://osg.ops.ctripcorp.com/api/CMSFATGetGroup/?_version=new";
    private static final String APP_ID = "100020032";
    private static final String APP_ID_NOT_EXIST = "100022222";
    private static final String IP = "10.5.156.193";
    private static final String IP_NOT_EXIST = "10.10.10.10";
    private static final String FAT_ENV = "fat";
    private static final String CMS_ACCESS_TOKEN = "96ddbe67728bc756466a226ec050456d";
    private static final int TIMEOUT = 1000;
    private static final List<String> PASS_CODES = Lists.newArrayList("0", "4");

    @Test
    public void getAllAppIdIp() throws Exception {
        // success
        List<AppIdIpCheckEntity> appIdIps = appIdIpManager.getAllAppIdIp(FAT_ENV);
        assert appIdIps != null && !appIdIps.isEmpty();

        // null env
        appIdIps = appIdIpManager.getAllAppIdIp(null);
        assert appIdIps != null && appIdIps.isEmpty();
    }

    @Test
    public void getAllAppIds() throws Exception {
        List<String> appIds = appIdIpManager.getAllAppIds();
        assert appIds != null && !appIds.isEmpty();
    }

    @Test
    public void getBatchAppIdIp() throws Exception {
        // success
        List<String> appIds = appIdIpManager.getAllAppIds();
        assert appIds != null && !appIds.isEmpty();
        List<AppIdIpCheckEntity> appIdIps = appIdIpManager.getBatchAppIdIp(appIds.subList(0, 100), FAT_ENV);
        assert appIdIps != null && !appIdIps.isEmpty();

        // null env
        appIdIps = appIdIpManager.getBatchAppIdIp(appIds.subList(0, 100), "");
        assert appIdIps != null && appIdIps.isEmpty();

        // null appIds
        appIdIps = appIdIpManager.getBatchAppIdIp(null, FAT_ENV);
        assert appIdIps != null && appIdIps.isEmpty();
    }

    @Test
    public void checkAppIdIp() throws Exception {
        // success
        AppIdIpCheckEntity entity = generate();
        Integer returnCode = appIdIpManager.checkAppIdIp(entity);
        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_SUCCESS;

        // ip not exist
        entity = generate();
        entity.setClientIp(IP_NOT_EXIST);
        returnCode = appIdIpManager.checkAppIdIp(entity);
        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_NOT_MATCH;

        // appId not exist
        entity = generate();
        entity.setClientAppId(APP_ID_NOT_EXIST);
        returnCode = appIdIpManager.checkAppIdIp(entity);
        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_NOT_MATCH;

        // accessToken is null
        entity = generate();
        entity.setServiceToken(null);
        returnCode = appIdIpManager.checkAppIdIp(entity);
        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_FAIL_INNER;

        // set short timeout
        entity = generate();
        entity.setTimeoutMs(1);
        returnCode = appIdIpManager.checkAppIdIp(entity);
        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_FAIL_INNER;
    }

    private AppIdIpCheckEntity generate() {
        AppIdIpCheckEntity entity = new AppIdIpCheckEntity();
        entity.setServiceUrl(CMS_GET_GROUP_URL);
        entity.setClientAppId(APP_ID);
        entity.setClientIp(IP);
        entity.setEnv(FAT_ENV);
        entity.setServiceToken(CMS_ACCESS_TOKEN);
        entity.setTimeoutMs(TIMEOUT);
        entity.setPassCodeList(PASS_CODES);
        return entity;
    }

}