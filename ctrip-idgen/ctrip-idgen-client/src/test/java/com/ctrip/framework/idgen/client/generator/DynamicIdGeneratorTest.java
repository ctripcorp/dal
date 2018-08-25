package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.IdGeneratorFactory;
import com.ctrip.framework.idgen.client.strategy.DefaultStrategy;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import com.ctrip.platform.idgen.service.api.IdSegment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DynamicIdGeneratorTest {

    @Test
    public void testPrefetchConcurrent() {
        String sequenceName = "testName1";
        int nThread = 4;
        PrefetchStrategy testStrategy = new TestStrategy();
        int nRequest = 10000;
        final CountDownLatch latch = new CountDownLatch(nRequest);
        final DynamicIdGenerator generator = new DynamicIdGenerator(sequenceName, testStrategy);
        generator.fetchPool();
        int count1 = generator.getStaticGeneratorQueue().size();
//        ExecutorService es = Executors.newFixedThreadPool(nThread);
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < nRequest; i++) {
            es.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        generator.prefetchIfNecessary();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        try {
            latch.await();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int count2 = generator.getStaticGeneratorQueue().size();
//        Assert.assertEquals(1, count2 - count1);
    }

    @Before
    public void setEnv() {
        System.setProperty("java.awt.headless", "false");
        overrideArtemisUrl("10.2.35.218");
    }

    private void overrideArtemisUrl(String ip) {
        String url = String.format("http://%s:8080/artemis-service/", ip);
        System.setProperty("artemis.client.cdubbo.service.service.domain.url", url);
        System.setProperty("artemis.client.cdubbo.client.service.domain.url", url);
    }

    public class TestStrategy extends DefaultStrategy {
        @Override
        public boolean checkIfNeedPrefetch() {
            return true;
        }
    }

}
