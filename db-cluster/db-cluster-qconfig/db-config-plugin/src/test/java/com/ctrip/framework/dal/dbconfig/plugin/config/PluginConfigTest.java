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
 * @author c7ch23en
 */
public class PluginConfigTest {

    private QconfigService qconfigService;
    private EnvProfile envProfile;
    private static final int THREAD_COUNT = 10;
    private static final int EXECUTE_COUNT = 1000000;
    private ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    @Before
    public void init() {
        qconfigService = new MockQconfigService();
        envProfile = new EnvProfile("uat:");
    }

    @Test
    public void testGet() throws Exception {
        PluginConfig config = new PluginConfig(qconfigService, envProfile);
        String keyServiceUri = config.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
        String sslcode = config.getParamValue(TitanConstants.SSLCODE);
        System.out.println("keyServiceUri=" + keyServiceUri);
        System.out.println("sslCode=" + sslcode);
        assert (!Strings.isNullOrEmpty(keyServiceUri));
        assert (!Strings.isNullOrEmpty(sslcode));
    }

    @Test
    public void getParamValueConcurrence() throws Exception {
        PluginConfig config = new PluginConfig(qconfigService, envProfile);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    for (int i = 0; i < EXECUTE_COUNT; i++) {
                        String keyServiceUri = config.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
                        String sslCode = config.getParamValue(TitanConstants.SSLCODE);
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
