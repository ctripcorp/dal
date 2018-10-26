package com.ctrip.framework.idgen.test;

import com.ctrip.framework.idgen.client.IdGeneratorFactory;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class LimitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimitTest.class);

    private String sequenceName = System.getProperty("sequenceName", "testName1");
    private int qpsCheckPeriodSeconds = Integer.parseInt(System.getProperty("qpsCheckPeriodSeconds", "10"));
    private IdGenerator idGenerator = IdGeneratorFactory.getInstance().getIdGenerator(sequenceName);
    private AtomicLong count = new AtomicLong(0);
    private ExecutorService executor = Executors.newCachedThreadPool();
    private long lastCount;
    private long lastTime;

    public static void main(String[] args) {
        LimitTest test = new LimitTest();
        test.setQpsChecker();
        test.start();
    }

    private void start() {
        LOGGER.info(String.format("Test sequence name: %s", sequenceName));
        for (;;) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        idGenerator.nextId();
                        count.incrementAndGet();
                    } catch (Throwable t) {
                        LOGGER.error("Get id failed", t);
                    }
                }
            });
/*            try {
                TimeUnit.MICROSECONDS.sleep(1);
            } catch (InterruptedException e) {
                LOGGER.error("Thread sleep exception", e);
                Thread.currentThread().interrupt();
            }*/
        }
    }

    private void setQpsChecker() {
        LOGGER.info(String.format("QPS check period: %d s", qpsCheckPeriodSeconds));
        ScheduledExecutorService scheduledExecutor = Executors.
                newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        lastCount = count.get();
        lastTime = getTime();
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long currentCount = count.get();
                long currentTime = getTime();
                long qps = (currentCount - lastCount) / getRoundQuotient(currentTime - lastTime, 1000);
                LOGGER.info(String.format("Real QPS: %d", qps));
                lastCount = currentCount;
                lastTime = currentTime;
            }
        }, qpsCheckPeriodSeconds, qpsCheckPeriodSeconds, TimeUnit.SECONDS);
    }

    private long getTime() {
        return System.currentTimeMillis();
    }

    private long getRoundQuotient(long dividend, long divisor) {
        long quot = dividend / divisor;
        if ((quot + 1) * divisor - dividend <= dividend - quot * divisor) {
            return quot + 1;
        } else {
            return quot;
        }
    }

}
