package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.AppIdIpCheckEntity;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by shenjie on 2019/6/11.
 */
public class AppIdIpManagerTest {

    private AppIdIpManager appIdIpManager = new AppIdIpManager();
    private static final String CHECK_APP_ID_IP_URL = "http://osg.ops.ctripcorp.com/api/CMSFATGetGroup/?_version=new";
    private static final String APP_ID = "100020032";
    private static final String APP_ID_NOT_EXIST = "100022222";
    private static final String IP = "10.5.156.193";
    private static final String IP_NOT_EXIST = "10.10.10.10";
    private static final String FAT_ENV = "fat";
    private static final String ACCESS_TOKEN = "96ddbe67728bc756466a226ec050456d";
    private static final int TIMEOUT = 1000;
    private static final List<String> PASS_CODES = Lists.newArrayList("0", "4");
    private static final int EXECUTE_COUNT = 500;
    private static final int THREAD_COUNT = 10;
    private ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    private static final String PAAS_CHECK_APP_ID_IP_URL = "http://paas.ctripcorp.com/api/v2/titan/verify/";
    private static final String PAAS_ACCESS_TOKEN = "540e79aa2d7bfb18007fa1ced9436f6515f291dddf229ff895586aa05724ba6f";

    @Test
    public void getAllAppIdIp() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        // success
        List<AppIdIpCheckEntity> appIdIps = appIdIpManager.getAllAppIdIp(FAT_ENV);
        assert appIdIps != null && !appIdIps.isEmpty();
        System.out.println("allAppIdIps.size:" + appIdIps.size());

        // null env
        appIdIps = appIdIpManager.getAllAppIdIp(null);
        assert appIdIps != null && appIdIps.isEmpty();

        stopwatch.stop();
        long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("test getAllAppIdIp() cost:" + cost);
    }

    @Test
    public void getAllAppIds() throws Exception {
        List<String> appIds = appIdIpManager.getAllAppIds();
        assert appIds != null && !appIds.isEmpty();
        System.out.println("appIds.size:" + appIds.size());

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
        ;

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

    @Test
    public void checkAppIdIpByPaaS() throws Exception {
        // success
        AppIdIpCheckEntity entity = generate();
        Integer returnCode = appIdIpManager.checkAppIdIpByPaaS(entity);
        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_SUCCESS;

        // ip not exist
        entity = generate();
        entity.setClientIp(IP_NOT_EXIST);
        returnCode = appIdIpManager.checkAppIdIpByPaaS(entity);
        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_NOT_MATCH;

        // appId not exist
        entity = generate();
        entity.setClientAppId(APP_ID_NOT_EXIST);
        returnCode = appIdIpManager.checkAppIdIpByPaaS(entity);
        assert returnCode != null && returnCode != CommonConstants.PAAS_RETURN_CODE_SUCCESS;

        // accessToken is null
        entity = generate();
        entity.setPaasServiceToken(null);
        returnCode = appIdIpManager.checkAppIdIpByPaaS(entity);
        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_SUCCESS;

        // set short timeout
        entity = generate();
        entity.setTimeoutMs(1);
        returnCode = appIdIpManager.checkAppIdIpByPaaS(entity);
        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_FAIL_INNER;
    }

    private AppIdIpCheckEntity generate() {
        AppIdIpCheckEntity entity = new AppIdIpCheckEntity();
        entity.setServiceUrl(CHECK_APP_ID_IP_URL);
        entity.setClientAppId(APP_ID);
        entity.setClientIp(IP);
        entity.setEnv(FAT_ENV);
        entity.setServiceToken(ACCESS_TOKEN);
        entity.setTimeoutMs(TIMEOUT);
        entity.setPassCodeList(PASS_CODES);
        entity.setPaasServiceUrl(PAAS_CHECK_APP_ID_IP_URL);
        entity.setPaasServiceToken(PAAS_ACCESS_TOKEN);
        return entity;
    }

    @Test
    public void checkAppIdIpPerformance() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    for (int i = 0; i < EXECUTE_COUNT; i++) {
                        count++;
                        Integer returnCode = appIdIpManager.checkAppIdIp(generate());
                        assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_SUCCESS;
                    }
                    System.out.println(Thread.currentThread().getName() + ":" + count);
                    latch.countDown();
                }
            });
        }
        latch.await();

        executor.shutdown();
        stopwatch.stop();
        long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("test checkAppIdIpPerformance() cost:" + cost);
    }

}