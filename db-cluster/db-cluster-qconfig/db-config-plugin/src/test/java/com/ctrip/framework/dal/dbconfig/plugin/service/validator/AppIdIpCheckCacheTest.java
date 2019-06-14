package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.AppIdIpCheckEntity;
import com.ctrip.framework.dal.dbconfig.plugin.mock.CmsDataGenerator;
import com.ctrip.framework.dal.dbconfig.plugin.mock.MockAppIdManager;
import com.ctrip.framework.dal.dbconfig.plugin.service.AppIdIpManager;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by shenjie on 2019/6/12.
 */
public class AppIdIpCheckCacheTest {

    private AppIdIpManager appIdIpManager = new MockAppIdManager();
    private AppIdIpCheckCache appIdIpCheckCache = AppIdIpCheckCache.getInstance();
    private static final int THREAD_COUNT = 10;
    private ExecutorService exec = Executors.newFixedThreadPool(THREAD_COUNT);

    @Before
    public void init() throws Exception {
        System.out.println("-------------------------init begin-------------------------");
        appIdIpCheckCache.setAppIdIpManager(appIdIpManager);
        appIdIpCheckCache.reBuildNormalCache();
        System.out.println("-------------------------init end-------------------------");
    }

    @Test
    public void getNormalCache() throws Exception {
        System.out.println("-------------------------getNormalCache begin-------------------------");
        List<AppIdIpCheckEntity> normalCache = CmsDataGenerator.generateNormalCacheAppIdIps();
        for (AppIdIpCheckEntity appIdIp : normalCache) {
            Integer returnCode = appIdIpCheckCache.getNormalCache().getIfPresent(appIdIp);
            assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_SUCCESS;
        }
        System.out.println("-------------------------getNormalCache end-------------------------");
    }

    @Test
    public void getTmpCache() throws Exception {
        System.out.println("-------------------------getTmpCache begin-------------------------");
        List<AppIdIpCheckEntity> tmpCache = CmsDataGenerator.generateTmpCacheAppIdIps();
        for (AppIdIpCheckEntity appIdIp : tmpCache) {
            Integer returnCode = appIdIpCheckCache.getTmpCache().get(appIdIp);
            assert returnCode != null && returnCode == CommonConstants.PAAS_RETURN_CODE_SUCCESS;
        }
        System.out.println("-------------------------getTmpCache end-------------------------");
    }

    @Test
    public void cacheTest() throws Exception {
        // clean tmp cache
        appIdIpCheckCache.getTmpCache().invalidateAll();
        List<AppIdIpCheckEntity> tmpCache = CmsDataGenerator.generateTmpCacheAppIdIps();
        for (AppIdIpCheckEntity appIdIp : tmpCache) {
            // get tmp cache appIdIp from normal cache, not exist
            Integer normalCacheReturnCode = appIdIpCheckCache.getNormalCache().getIfPresent(appIdIp);
            assert normalCacheReturnCode == null;

            // get tmp cache appIdIp from tmp cache, not exist
            Integer tmpReturnCode = appIdIpCheckCache.getNormalCache().getIfPresent(appIdIp);
            assert tmpReturnCode == null;

            // tmp cache invoke load method, then put appIdIp to normal cache
            boolean returnCode = appIdIpCheckCache.isAppIdIpMatch(appIdIp);
            assert returnCode;

            // get tmp cache appIdIp from tmp cache, invoke tmp cache load method, then put appIdIp to normal cache
            tmpReturnCode = appIdIpCheckCache.getTmpCache().getIfPresent(appIdIp);
            assert tmpReturnCode != null && tmpReturnCode == CommonConstants.PAAS_RETURN_CODE_SUCCESS;

            // get get tmp cache appIdIp from normal cache success
            normalCacheReturnCode = appIdIpCheckCache.getNormalCache().getIfPresent(appIdIp);
            assert normalCacheReturnCode != null && normalCacheReturnCode == CommonConstants.PAAS_RETURN_CODE_SUCCESS;
        }
    }


    @Test
    public void isAppIdIpMatch() throws Exception {
        System.out.println("-------------------------isAppIdIpMatch begin-------------------------");
        List<AppIdIpCheckEntity> normalCache = CmsDataGenerator.generateNormalCacheAppIdIps();
        List<AppIdIpCheckEntity> tmpCache = CmsDataGenerator.generateTmpCacheAppIdIps();
        List<AppIdIpCheckEntity> allAppIdIps = Lists.newArrayList();
        allAppIdIps.addAll(normalCache);
        allAppIdIps.addAll(tmpCache);
        for (AppIdIpCheckEntity appIdIp : allAppIdIps) {
            boolean isMatch = appIdIpCheckCache.isAppIdIpMatch(appIdIp);
            assert isMatch;
        }

        List<AppIdIpCheckEntity> notMatchAppIdIps = CmsDataGenerator.generateNotMarchAppIdIps();
        for (AppIdIpCheckEntity appIdIp : notMatchAppIdIps) {
            boolean isMatch = appIdIpCheckCache.isAppIdIpMatch(appIdIp);
            assert !isMatch;
        }
        System.out.println("-------------------------isAppIdIpMatch end-------------------------");
    }

    @Test
    public void getValueInCache() throws Exception {
        for (int i = 0; i < THREAD_COUNT; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Map<AppIdIpCheckEntity, Integer> appIdIpAndReturnCodes = CmsDataGenerator.generateAppIdIpAndReturnCodes();
                            for (Map.Entry<AppIdIpCheckEntity, Integer> appIdIpAndResult : appIdIpAndReturnCodes.entrySet()) {
                                AppIdIpCheckEntity appIdIp = appIdIpAndResult.getKey();
                                Integer expectedReturnCode = appIdIpAndResult.getValue();
                                Integer realReturnCode = appIdIpCheckCache.getValueInCache(appIdIp);
                                assert realReturnCode != null;
                                assert realReturnCode == expectedReturnCode;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        TimeUnit.MINUTES.sleep(1);
    }

}