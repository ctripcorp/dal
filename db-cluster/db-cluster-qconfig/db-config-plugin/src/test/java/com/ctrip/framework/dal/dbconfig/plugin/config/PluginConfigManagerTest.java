package com.ctrip.framework.dal.dbconfig.plugin.config;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.MockQconfigService;
import com.google.common.base.Strings;
import org.junit.Before;
import org.junit.Test;
import qunar.tc.qconfig.plugin.QconfigService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shenjie on 2019/7/19.
 */
public class PluginConfigManagerTest {

    private QconfigService qconfigService;
    private static final String PRO_ENV = "pro";
    private static final String UAT_ENV = "uat";
    private static final String FAT_ENV = "fat";
    private EnvProfile proProfile;
    private EnvProfile uatProfile;
    private EnvProfile fatProfile;

    private static final int THREAD_COUNT = 10;
    private static final int EXECUTE_COUNT = 1000000;
    private ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    @Before
    public void init() {
        qconfigService = new MockQconfigService();
        proProfile = new EnvProfile(PRO_ENV);
        uatProfile = new EnvProfile(UAT_ENV);
        fatProfile = new EnvProfile(FAT_ENV);
    }

    @Test
    public void getParamValue() throws Exception {
        PluginConfigManager pluginConfigManager = new PluginConfigManager(qconfigService);
        PluginConfig fatPluginConfig = pluginConfigManager.getPluginConfig(fatProfile);
        String keyServiceUri = fatPluginConfig.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
        String sslCode = fatPluginConfig.getParamValue(TitanConstants.SSLCODE);
        System.out.println("keyServiceUri=" + keyServiceUri);
        System.out.println("sslCode=" + sslCode);
        assert (!Strings.isNullOrEmpty(keyServiceUri));
        assert (!Strings.isNullOrEmpty(sslCode));

        PluginConfig uatPluginConfig = pluginConfigManager.getPluginConfig(uatProfile);
        keyServiceUri = uatPluginConfig.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
        sslCode = uatPluginConfig.getParamValue(TitanConstants.SSLCODE);
        System.out.println("keyServiceUri=" + keyServiceUri);
        System.out.println("sslCode=" + sslCode);
        assert (!Strings.isNullOrEmpty(keyServiceUri));
        assert (!Strings.isNullOrEmpty(sslCode));

        PluginConfig proPluginConfig = pluginConfigManager.getPluginConfig(proProfile);
        keyServiceUri = proPluginConfig.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
        sslCode = proPluginConfig.getParamValue(TitanConstants.SSLCODE);
        System.out.println("keyServiceUri=" + keyServiceUri);
        System.out.println("sslCode=" + sslCode);
        assert (!Strings.isNullOrEmpty(keyServiceUri));
        assert (!Strings.isNullOrEmpty(sslCode));
    }

    @Test
    public void getParamValueConcurrence() throws Exception {
        PluginConfigManager pluginConfigManager = new PluginConfigManager(qconfigService);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    for (int i = 0; i < EXECUTE_COUNT; i++) {
                        PluginConfig fatPluginConfig = pluginConfigManager.getPluginConfig(fatProfile);
                        String keyServiceUri = fatPluginConfig.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
                        String sslCode = fatPluginConfig.getParamValue(TitanConstants.SSLCODE);
                        assert (!Strings.isNullOrEmpty(keyServiceUri));
                        assert (!Strings.isNullOrEmpty(sslCode));

                        PluginConfig uatPluginConfig = pluginConfigManager.getPluginConfig(uatProfile);
                        keyServiceUri = uatPluginConfig.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
                        sslCode = uatPluginConfig.getParamValue(TitanConstants.SSLCODE);
                        assert (!Strings.isNullOrEmpty(keyServiceUri));
                        assert (!Strings.isNullOrEmpty(sslCode));

                        PluginConfig proPluginConfig = pluginConfigManager.getPluginConfig(proProfile);
                        keyServiceUri = proPluginConfig.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
                        sslCode = proPluginConfig.getParamValue(TitanConstants.SSLCODE);
                        assert (!Strings.isNullOrEmpty(keyServiceUri));
                        assert (!Strings.isNullOrEmpty(sslCode));
                        count++;
                    }
                    System.out.println(Thread.currentThread().getName() + ":" + count);
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();
    }

}