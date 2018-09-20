package com.ctrip.framework.idgen.client.generator;

import com.ctrip.framework.idgen.client.common.TestUtils;
import com.ctrip.framework.idgen.client.strategy.ConstantStrategy;
import com.ctrip.platform.idgen.service.api.IdSegment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicIdGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicIdGeneratorTest.class);
    private static final String SEQUENCE_NAME = "testName";

    private List<Long> mockIds;
    private DynamicIdGenerator noPrefetchGenerator;
    private DynamicIdGenerator alwaysPrefetchGenerator;

    @Before
    public void initialize() {
        LOGGER.info("initializing...");
        int size1 = 10000;
        int size2 = 6000;
        int offset1 = 0;
        int offset2 = 1000 + size1;
        mockIds = TestUtils.mockIds(size1, offset1);
        List<Long> ids2 = TestUtils.mockIds(size2, offset2);
        IdSegment segment1 = TestUtils.mockSegment(mockIds);
        IdSegment segment2 = TestUtils.mockSegment(ids2);
        List<IdSegment> pool = new LinkedList<>();
        pool.add(segment1);
        pool.add(segment2);
        mockIds.addAll(ids2);
        LOGGER.info("segment1 start: {}", segment1.getStart());
        LOGGER.info("segment1 end: {}", segment1.getEnd());
        LOGGER.info("segment2 start: {}", segment2.getStart());
        LOGGER.info("segment2 end: {}", segment2.getEnd());
        StaticIdGenerator staticGenerator = new MockStaticIdGenerator(SEQUENCE_NAME, pool);
        staticGenerator.initialize();
        StaticIdGenerator staticGenerator2 = new MockStaticIdGenerator(SEQUENCE_NAME, pool);
        staticGenerator2.initialize();
        noPrefetchGenerator = new MockDynamicIdGenerator(SEQUENCE_NAME, new ConstantStrategy(false), staticGenerator);
        noPrefetchGenerator.initialize();
        alwaysPrefetchGenerator = new MockDynamicIdGenerator(SEQUENCE_NAME, new ConstantStrategy(true), staticGenerator2);
        alwaysPrefetchGenerator.initialize();
    }

    @Test
    public void testSimpleNextId() {
        for (Long id : mockIds) {
            Assert.assertEquals(id.longValue(), noPrefetchGenerator.simpleNextId().longValue());
        }
        Assert.assertNull(noPrefetchGenerator.simpleNextId());
    }

    @Test
    public void testActiveFetch() {
        for (Long id : mockIds) {
            Assert.assertEquals(-1L, noPrefetchGenerator.activeFetch(10).longValue());
        }
    }

    @Test
    public void testNextId() {
        for (Long id : mockIds) {
            Assert.assertEquals(id.longValue(), noPrefetchGenerator.nextId().longValue());
        }
        Assert.assertEquals(-1L, noPrefetchGenerator.nextId().longValue());
    }

    @Test
    public void testConcurrentNextId() {
        ExecutorService executor = Executors.newCachedThreadPool();
        int requests = mockIds.size();
        final Set<Long> set = new HashSet<>();
        final CountDownLatch latch = new CountDownLatch(requests);
        for (int i = 0; i < requests; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Long id = noPrefetchGenerator.nextId();
                    if (id != null) {
                        synchronized (set) {
                            set.add(id);
                        }
                    }
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(requests, set.size());
        Assert.assertEquals(-1L, noPrefetchGenerator.nextId().longValue());
    }

    @Test
    public void testConcurrentPrefetch() {
        ExecutorService executor = Executors.newCachedThreadPool();
        int requests = 1000;
        final AtomicInteger success = new AtomicInteger(0);
        final CountDownLatch latch = new CountDownLatch(requests);
        for (int i = 0; i < requests; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    if (alwaysPrefetchGenerator.prefetchIfNecessary()) {
                        success.incrementAndGet();
                    }
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(1, success.get());
    }

}
