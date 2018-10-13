package com.ctrip.framework.idgen.client.strategy;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicStrategyTest {

    private final DynamicStrategy strategy = new DynamicStrategy();

    @Test
    public void testConcurrentInitialize() {
        int threadCount = 1000;
        final AtomicInteger successCount = new AtomicInteger(0);
        ExecutorService executor = Executors.newCachedThreadPool();
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final CyclicBarrier barrier = new CyclicBarrier(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (strategy.initialize()) {
                        successCount.incrementAndGet();
                    }
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        Assert.assertEquals(1, successCount.get());
    }

    @Test
    public void testZeroQps() {
        strategy.initialize();
        strategy.provide(1000);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        Assert.assertEquals(0, strategy.getQps());
    }

    @Test
    public void testConcurrentConsume() {
        int initialSize = 10000;
        int consumedCount = 8000;
        strategy.initialize();
        strategy.provide(initialSize);
        ExecutorService executor = Executors.newCachedThreadPool();
        final CountDownLatch latch = new CountDownLatch(consumedCount);
        final CyclicBarrier barrier = new CyclicBarrier(consumedCount);
        for (int i = 0; i < consumedCount; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    strategy.consume();
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        long expected = initialSize - consumedCount;
        Assert.assertEquals(expected, strategy.getRemainedSize());
    }

    @Test
    public void testConcurrentProvide() {
        int initialSize = 10000;
        final int provideSize = 10;
        int provideCount = 5000;
        strategy.initialize();
        strategy.provide(initialSize);
        ExecutorService executor = Executors.newCachedThreadPool();
        final CountDownLatch latch = new CountDownLatch(provideCount);
        final CyclicBarrier barrier = new CyclicBarrier(provideCount);
        for (int i = 0; i < provideCount; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    strategy.provide(provideSize);
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        long expected = initialSize + provideCount * provideSize;
        Assert.assertEquals(expected, strategy.getRemainedSize());
    }

    @Test
    public void testConcurrentConsumeAndProvide() {
        int initialSize = 10000;
        int consumedCount = 8000;
        final int provideSize = 10;
        int provideCount = 5000;
        strategy.initialize();
        strategy.provide(initialSize);
        ExecutorService executor = Executors.newCachedThreadPool();
        final CountDownLatch latch = new CountDownLatch(consumedCount + provideCount);
        final CyclicBarrier barrier = new CyclicBarrier(consumedCount + provideCount);
        for (int i = 0; i < consumedCount; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    strategy.consume();
                    latch.countDown();
                }
            });
        }
        for (int i = 0; i < provideCount; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    strategy.provide(provideSize);
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        long expected = initialSize - consumedCount + provideCount * provideSize;
        Assert.assertEquals(expected, strategy.getRemainedSize());
    }

}
