package com.ctrip.framework.idgen.server.service;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ctrip.framework.idgen.server.config.TestConfig.mockServerConfig;

public class IdWorkerPerformanceTest {

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
        runMultiThread(worker);
        runMultiThreadWithBarrier(worker);
    }

    @Test
    public void CASWorkerMultiThreadTest() {
        IdWorker worker = new CASSnowflakeWorker("testName1", mockServerConfig());
        runMultiThread(worker);
        runMultiThreadWithBarrier(worker);
    }

    private void runSingleThread(final IdWorker worker) {
        int nRequest = 10000;
        int requestSize = 2000;
        int timeoutMillis = 100;

        long startTime = System.nanoTime();
        for (int i = 0; i < nRequest; i++) {
            worker.generateIdPool(requestSize, timeoutMillis);
        }
        long endTime = System.nanoTime();

        System.out.println("================================================================================");
        System.out.println("Single thread. Requests: " + nRequest + ". Time elapsed: " +
                (endTime - startTime) / 1000000 + "ms.");
        System.out.println("================================================================================");
    }

    private void runMultiThread(final IdWorker worker) {
        int nThread = 200;
        int nRequest = 10000;
        final int requestSize = 2000;
        final int timeoutMillis = 100;
        final ExecutorService executor = Executors.newFixedThreadPool(nThread);
        final CountDownLatch latch = new CountDownLatch(nRequest);

        long startTime = System.nanoTime();
        for (int i = 0; i < nRequest; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    worker.generateIdPool(requestSize, timeoutMillis);
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

}
