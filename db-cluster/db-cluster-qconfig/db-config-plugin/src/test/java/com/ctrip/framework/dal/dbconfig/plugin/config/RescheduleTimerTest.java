package com.ctrip.framework.dal.dbconfig.plugin.config;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by shenjie on 2019/7/19.
 */
public class RescheduleTimerTest {

    public static final int EXECUTE_COUNT = 10;

    @Test
    public void schedule() throws Exception {
        CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);
        RescheduleTimer timer = new RescheduleTimer();
        AtomicInteger counter = new AtomicInteger();
        timer.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "test1:" + counter.incrementAndGet());
                latch.countDown();
            }
        }, 1000, 1000);

        latch.await();
        timer.cancel();
    }

    @Test
    public void reschedule() throws Exception {
        CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);
        RescheduleTimer timer = new RescheduleTimer();
        AtomicInteger counter = new AtomicInteger();
        AtomicLong beginTime = new AtomicLong(System.currentTimeMillis());
        timer.schedule(new Runnable() {
            @Override
            public void run() {
                long lastExecuteTime = beginTime.get();
                long startTime = System.currentTimeMillis();
                beginTime.set(startTime);
                int count = counter.incrementAndGet();
                if (count == EXECUTE_COUNT / 2) {
//                    timer.reschedule(2000, 2000);
                }

                long cost = startTime - lastExecuteTime;
                System.out.println(String.format("threadName:%s,count:%s,cost:%sms", Thread.currentThread().getName(), count, cost));
                latch.countDown();
            }
        }, 1000, 1000);

        latch.await();
        timer.cancel();
    }

}