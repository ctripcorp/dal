package com.ctrip.framework.idgen.client.generator;

import com.ctrip.platform.idgen.service.api.IdSegment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StaticIdGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticIdGeneratorTest.class);

    private List<Long> mockIds;
    private List<IdSegment> mockPool;

    @Before
    public void initialize() {
        LOGGER.info("initializing...");
        int size1 = 1000;
        int size2 = 600;
        mockIds = mockIds(size1, 0);
        List<Long> ids2 = mockIds(size2, size1 + 200);
        IdSegment segment1 = getSegment(mockIds);
        IdSegment segment2 = getSegment(ids2);
        mockPool = new LinkedList<>();
        mockPool.add(segment1);
        mockPool.add(segment2);
        mockIds.addAll(ids2);
        LOGGER.info("segment1 start: {}", segment1.getStart());
        LOGGER.info("segment1 end: {}", segment1.getEnd());
        LOGGER.info("segment2 start: {}", segment2.getStart());
        LOGGER.info("segment2 end: {}", segment2.getEnd());
    }

    @Test
    public void testNextId() {
        StaticIdGenerator generator = getGenerator();
        generator.initialize();
        Assert.assertEquals(mockIds.size(), generator.getRemainedSize());
        for (Long id : mockIds) {
            Assert.assertEquals(id.longValue(), generator.nextId().longValue());
        }
        Assert.assertNull(generator.nextId());
    }

    @Test
    public void testConcurrentNextId() {
        final StaticIdGenerator generator = getGenerator();
        generator.initialize();
        ExecutorService executor = Executors.newCachedThreadPool();
        int requests = mockIds.size();
        final Set<Long> set = new HashSet<>();
        final CountDownLatch latch = new CountDownLatch(requests);
        for (int i = 0; i < requests; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Long id = generator.nextId();
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
        Assert.assertNull(generator.nextId());
    }

    private List<Long> mockIds(int size, long offset) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ids.add(i + offset);
        }
        return ids;
    }

    private IdSegment getSegment(List<Long> ids) {
        return new IdSegment(ids.get(0), ids.get(ids.size() - 1));
    }

    private StaticIdGenerator getGenerator() {
        return new MockStaticIdGenerator("testName", mockPool);
    }

}
