package com.ctrip.framework.idgen.server.service;

import com.ctrip.platform.idgen.service.api.IdSegment;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ctrip.framework.idgen.server.config.TestConfig.mockServerConfig;

public class IdWorkerFunctionTest {

    @Test
    public void SyncWorkerSingleThreadTest() {
        IdWorker worker = new SnowflakeWorker("testName1", mockServerConfig());
        runSingleThread(worker);
    }

    @Test
    public void CASWorkerSingleThreadTest() {
        IdWorker worker = new CASSnowflakeWorker("testName1", mockServerConfig());
        runSingleThread(worker);
    }

    @Test
    public void SyncWorkerMultiThreadTest() {
        IdWorker worker = new SnowflakeWorker("testName1", mockServerConfig());
//        runMultiThread(worker);
        runMultiThreadWithBarrier(worker);
    }

    @Test
    public void CASWorkerMultiThreadTest() {
        IdWorker worker = new CASSnowflakeWorker("testName1", mockServerConfig());
//        runMultiThread(worker);
        runMultiThreadWithBarrier(worker);
    }

    private void runSingleThread(final IdWorker worker) {
        int nRequest = 10000;
        int requestSize = 2000;
        int timeoutMillis = 100;
        List<IdSegment> segments = new LinkedList<>();

        long startTime = System.nanoTime();
        for (int i = 0; i < nRequest; i++) {
            segments.addAll(worker.generateIdPool(requestSize, timeoutMillis));
        }
        long endTime = System.nanoTime();

        System.out.println("================================================================================");
        System.out.println("Single thread. Requests: " + nRequest + ". Time elapsed: " +
                (endTime - startTime) / 1000000 + "ms.");
        System.out.println("================================================================================");

        Assert.assertTrue(checkResult(segments, nRequest * requestSize));
    }

    private void runMultiThread(final IdWorker worker) {
        int nThread = 200;
        int nRequest = 10000;
        final int requestSize = 2000;
        final int timeoutMillis = 100;
        final ExecutorService executor = Executors.newFixedThreadPool(nThread);
        final CountDownLatch latch = new CountDownLatch(nRequest);
        final Vector<IdSegment> segments = new Vector<>();

        long startTime = System.nanoTime();
        for (int i = 0; i < nRequest; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    segments.addAll(worker.generateIdPool(requestSize, timeoutMillis));
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();

        System.out.println("================================================================================");
        System.out.println("Multi thread. Threads: " + nThread + ", requests: " + nRequest + ". Time elapsed: " +
                (endTime - startTime) / 1000000 + "ms.");
        System.out.println("================================================================================");

        Assert.assertTrue(checkResult(segments, nRequest * requestSize));
    }

    private void runMultiThreadWithBarrier(final IdWorker worker) {
        int nThread = 20;
        int nLoop = 1000;
        final int requestSize = 2000;
        final int timeoutMillis = 100;
        final ExecutorService executor = Executors.newFixedThreadPool(nThread);
        final CyclicBarrier barrier = new CyclicBarrier(nThread);
        final Vector<IdSegment> segments = new Vector<>();

        long startTime = System.nanoTime();
        for (int i = 0; i < nLoop; i++) {
            final CountDownLatch latch = new CountDownLatch(nThread);
            for (int j = 0; j < nThread; j++) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            barrier.await();
                            segments.addAll(worker.generateIdPool(requestSize, timeoutMillis));
                            latch.countDown();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            barrier.reset();
        }
        long endTime = System.nanoTime();

        System.out.println("================================================================================");
        System.out.println("Multi thread with barrier. Threads: " + nThread + ", loops: " + nLoop + ". Time elapsed: " +
                (endTime - startTime) / 1000000 + "ms.");
        System.out.println("================================================================================");

        Assert.assertTrue(checkResult(segments, nLoop * nThread * requestSize));
    }

    private boolean checkResult(List<IdSegment> segments, long expectedCount) {
        Set<Long> idSet = new HashSet<>();
        long fetchCount = 0;
        for (IdSegment segment : segments) {
            fetchCount += segment.getEnd().longValue() - segment.getStart().longValue() + 1;
            for (long i = segment.getStart().longValue(); i <= segment.getEnd().longValue(); i++) {
                idSet.add(i);
            }
        }
        long effectiveCount = idSet.size();

        System.out.println("================================================================================");
        System.out.println("Expected count: " + expectedCount);
        System.out.println("Fetch count: " + fetchCount);
        System.out.println("Effective count: " + effectiveCount);
        System.out.println("================================================================================");

        return fetchCount == effectiveCount;
    }

}
