package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.service.api.IdSegment;
import org.junit.Test;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static com.ctrip.framework.idgen.server.config.TestConfig.mockConfig;

public class IdWorkerPerformanceTest {

    @Test
    public void SyncWorkerSingleThreadTest() {
        IdWorker worker = new SnowflakeWorker("testName1", mockConfig());
        runSingleThread(worker);
//        long averagedCount = runSingleThreadForTime(worker, 2, 10000);
//        System.out.println("----- Averaged count: " + averagedCount + " -----");

    }

    @Test
    public void CASWorkerSingleThreadTest() {
        IdWorker worker = new CASSnowflakeWorker("testName1", mockConfig());
        runSingleThread(worker);
        long averagedCount = runSingleThreadForTime(worker, 2, 10000);
        System.out.println("----- Averaged count: " + averagedCount + " -----");
    }

    @Test
    public void SyncWorkerMultiThreadTest() {
        IdWorker worker = new SnowflakeWorker("testName1", mockConfig());
//        runMultiThread(worker);
//        runMultiThreadWithBarrier(worker);
        int testMilliseconds = 3000;
        int initialRequestSize = 1;
        int requestSizeStep = 100;
        int loop = 10;
        for (int i = 0; i < loop; i++) {
            int requestSize = initialRequestSize + i * requestSizeStep;
            long averagedCount = runMultiThreadForTime(worker, requestSize, testMilliseconds);
            System.out.println("----- Request size: " + requestSize + " -----");
            System.out.println("----- Averaged count: " + averagedCount + " -----");
//            System.gc();
        }
    }

    @Test
    public void CASWorkerMultiThreadTest() {
        IdWorker worker = new CASSnowflakeWorker("testName1", mockConfig());
//        runMultiThread(worker);
//        runMultiThreadWithBarrier(worker);
        int testMilliseconds = 3000;
        int initialRequestSize = 1;
        int requestSizeStep = 100;
        int loop = 10;
        for (int i = 0; i < loop; i++) {
            int requestSize = initialRequestSize + i * requestSizeStep;
            long averagedCount = runMultiThreadForTime(worker, requestSize, testMilliseconds);
            System.out.println("----- Request size: " + requestSize + " -----");
            System.out.println("----- Averaged count: " + averagedCount + " -----");
//            System.gc();
        }
    }

    private void runSingleThread(final IdWorker worker) {
        int nRequest = 100000;
        int requestSize = 4000;
        int timeoutMillis = 6000;
        long responseIdCount = 0;

        long startTime = System.nanoTime();
        for (int i = 0; i < nRequest; i++) {
            List<IdSegment> segments = worker.generateIdPool(requestSize, timeoutMillis);
            if (segments != null && !segments.isEmpty()) {
                for (IdSegment segment : segments) {
                    responseIdCount += segment.getEnd().longValue() - segment.getStart().longValue() + 1;
                }
            }
        }
        long endTime = System.nanoTime();

        System.out.println("================================================================================");
        System.out.println("Single thread. Requests: " + nRequest + ". Time elapsed: " +
                (endTime - startTime) / 1000000 + "ms.");
        System.out.println("Response id count: " + responseIdCount);
        System.out.println("================================================================================");
    }

    private long runSingleThreadForTime(final IdWorker worker, int requestSize, int milliseconds) {
        int timeoutMillis = 10000;
        final Timer timer = new Timer();
        final AtomicBoolean timeUp = new AtomicBoolean(false);
        long responseIdCount = 0;

        final AtomicLong startTimerTime = new AtomicLong();
        final AtomicLong timeupTime = new AtomicLong();
        final AtomicLong startExecutionTime = new AtomicLong();
        final AtomicLong endExecutionTime = new AtomicLong();

        startTimerTime.set(System.nanoTime());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeUp.set(true);
                timeupTime.set(System.nanoTime());
            }
        }, milliseconds);

        startExecutionTime.set(System.nanoTime());
        while (!timeUp.get()) {
            List<IdSegment> segments = worker.generateIdPool(requestSize, timeoutMillis);
            if (segments != null && !segments.isEmpty()) {
                for (IdSegment segment : segments) {
                    responseIdCount += segment.getEnd().longValue() - segment.getStart().longValue() + 1;
                }
            }
        }
        endExecutionTime.set(System.nanoTime());

        System.out.println("================================================================================");
        System.out.println("Single thread test for " + milliseconds + " ms");
        System.out.println("Response id count: " + responseIdCount);
        System.out.println("Time up time: " + (timeupTime.get() - startTimerTime.get()) / 1000000 + " ms");
        System.out.println("Execution time: " + (endExecutionTime.get() - startExecutionTime.get()) / 1000000 + " ms");
        System.out.println("================================================================================");

        long validTime = (timeupTime.get() - startTimerTime.get()) / 1000000;

        return responseIdCount / validTime * 1000;
    }

    private void runMultiThread(final IdWorker worker) {
        int nThread = 200;
        int nRequest = 100000;
        final int requestSize = 4000;
        final int timeoutMillis = 10000;
        final ExecutorService executor = Executors.newCachedThreadPool();
        final CountDownLatch latch = new CountDownLatch(nRequest);

        final AtomicLong requestCount = new AtomicLong(0);
        final AtomicLong validRequestCount = new AtomicLong(0);
        final AtomicLong responseIdCount = new AtomicLong(0);

        long startTime = System.nanoTime();
        for (int i = 0; i < nRequest; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    requestCount.incrementAndGet();
                    List<IdSegment> segments = worker.generateIdPool(requestSize, timeoutMillis);
                    if (segments != null) {
                        validRequestCount.incrementAndGet();
                        long count = 0;
                        for (IdSegment segment : segments) {
                            count += segment.getEnd().longValue() - segment.getStart().longValue() + 1;
                        }
/*                        if (count < requestSize) {
                            System.out.println("breakpoint");
                        }*/
                        responseIdCount.addAndGet(count);
                    }
                    latch.countDown();
                }
            });
        }
        long endTime0 = System.nanoTime();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        long endTime = System.nanoTime();

        System.out.println("================================================================================");
        System.out.println("Multi thread test");
        System.out.println("Threads: " + nThread);
        System.out.println("Request count: " + requestCount.get());
        System.out.println("Valid request count: " + validRequestCount.get());
        System.out.println("Response id count: " + responseIdCount.get());
        System.out.println("Time elapsed0: " + (endTime0 - startTime) / 1000000 + " ms");
        System.out.println("Time elapsed: " + (endTime - startTime) / 1000000 + " ms");
        System.out.println("================================================================================");
    }

    private void runMultiThreadWithBarrier(final IdWorker worker) {
        int nThread = 200;
        int nLoop = 1000;
        final int requestSize = 2000;
        final int timeoutMillis = 100;
        final ExecutorService executor = Executors.newFixedThreadPool(nThread);
        final CyclicBarrier barrier = new CyclicBarrier(nThread);

        long startTime = System.nanoTime();
        for (int i = 0; i < nLoop; i++) {
            final CountDownLatch latch = new CountDownLatch(nThread);
            for (int j = 0; j < nThread; j++) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            barrier.await();
                            worker.generateIdPool(requestSize, timeoutMillis);
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
    }

    private long runMultiThreadForTime(final IdWorker worker, final int requestSize, int milliseconds) {
        int nThread = 200;
        final int timeoutMillis = 10000;
        final ExecutorService executor = Executors.newFixedThreadPool(nThread);
//        final ExecutorService executor = Executors.newCachedThreadPool();
        final Timer timer = new Timer();
        final AtomicBoolean timeUp = new AtomicBoolean(false);

        final AtomicLong requestCount = new AtomicLong(0);
        final AtomicLong validRequestCount = new AtomicLong(0);
        final AtomicLong responseIdCount = new AtomicLong(0);

        final AtomicLong startTimerTime = new AtomicLong();
        final AtomicLong timeupTime = new AtomicLong();
        final AtomicLong startExecutionTime = new AtomicLong();
        final AtomicLong endExecutionTime = new AtomicLong();

        startTimerTime.set(System.nanoTime());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeUp.set(true);
                timeupTime.set(System.nanoTime());
            }
        }, milliseconds);

        startExecutionTime.set(System.nanoTime());
        while (!timeUp.get()) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    requestCount.incrementAndGet();
                    List<IdSegment> segments = worker.generateIdPool(requestSize, timeoutMillis);
                    if (!timeUp.get() && segments != null) {
                        validRequestCount.incrementAndGet();
                        for (IdSegment segment : segments) {
                            responseIdCount.addAndGet(segment.getEnd().longValue() - segment.getStart().longValue() + 1);
                        }
                    }
                }
            });
        }
        endExecutionTime.set(System.nanoTime());

        executor.shutdown();
        executor.shutdownNow();
/*        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }*/

        System.out.println("================================================================================");
        System.out.println("Multi thread test for " + milliseconds + " ms");
        System.out.println("Threads: " + nThread);
        System.out.println("Request count: " + requestCount.get());
        System.out.println("Valid request count: " + validRequestCount.get());
        System.out.println("Response id count: " + responseIdCount.get());
        System.out.println("Time up time: " + (timeupTime.get() - startTimerTime.get()) / 1000000 + " ms");
        System.out.println("Execution time: " + (endExecutionTime.get() - startExecutionTime.get()) / 1000000 + " ms");
        System.out.println("================================================================================");

        long validTime = (timeupTime.get() - startTimerTime.get()) / 1000000;

        return responseIdCount.get() / validTime * 1000;
    }

}
