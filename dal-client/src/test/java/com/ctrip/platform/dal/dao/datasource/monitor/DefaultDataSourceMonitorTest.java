package com.ctrip.platform.dal.dao.datasource.monitor;

import com.ctrip.platform.dal.dao.datasource.DataSourceName;
import com.ctrip.platform.dal.dao.util.ThreadUtils;
import com.mysql.jdbc.MysqlDataTruncation;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author c7ch23en
 */
public class DefaultDataSourceMonitorTest {

    private static final long TEST_ALERT_THRESHOLD_MS = 100;
    private static final long TEST_ALERT_THRESHOLD_SAMPLES = 3;
    private static final long TEST_ALERT_INTERVAL_MS = 50;

    @Test
    public void testContinuousWriteFailures() {
        DefaultDataSourceMonitorDelegate monitor = createMonitor();
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_THRESHOLD_MS, TimeUnit.MILLISECONDS);
        monitor.report(mockException(),true);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertTrue(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertFalse(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_INTERVAL_MS, TimeUnit.MILLISECONDS);
        monitor.report(mockException(),true);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertTrue(monitor.getCheckAlertFrequencyResult());
    }

    @Test
    public void testContinuousReadFailures() {
        DefaultDataSourceMonitorDelegate monitor = createMonitor();
        monitor.report(mockException(),false);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),false);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),false);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_THRESHOLD_MS, TimeUnit.MILLISECONDS);
        monitor.report(mockException(),false);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertTrue(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),false);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertFalse(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_INTERVAL_MS, TimeUnit.MILLISECONDS);
        monitor.report(mockException(),false);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertTrue(monitor.getCheckAlertFrequencyResult());
    }

    @Test
    public void testContinuousMixedFailures() {
        DefaultDataSourceMonitorDelegate monitor = createMonitor();
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),false);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_THRESHOLD_MS, TimeUnit.MILLISECONDS);
        monitor.report(mockException(),false);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertTrue(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertFalse(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_INTERVAL_MS, TimeUnit.MILLISECONDS);
        monitor.report(mockException(),false);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertTrue(monitor.getCheckAlertFrequencyResult());
    }

    @Test
    public void testOccasionalWriteFailures() {
        DefaultDataSourceMonitorDelegate monitor = createMonitor();
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_THRESHOLD_MS, TimeUnit.MILLISECONDS);
        monitor.report(null,true);
        Assert.assertNull(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_THRESHOLD_MS, TimeUnit.MILLISECONDS);
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
    }

    @Test
    public void testOccasionalReadFailures() {
        DefaultDataSourceMonitorDelegate monitor = createMonitor();
        monitor.report(mockException(),false);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),false);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),false);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_THRESHOLD_MS, TimeUnit.MILLISECONDS);
        monitor.report(null,false);
        Assert.assertNull(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),false);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_THRESHOLD_MS, TimeUnit.MILLISECONDS);
        monitor.report(mockException(),false);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
    }

    @Test
    public void testContinuousWriteFailuresWithReadSuccesses() {
        DefaultDataSourceMonitorDelegate monitor = createMonitor();
        monitor.report(null,false);
        Assert.assertNull(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(null,false);
        Assert.assertNull(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(null,false);
        Assert.assertNull(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertTrue(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_THRESHOLD_MS, TimeUnit.MILLISECONDS);
        monitor.report(null,false);
        Assert.assertNull(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertTrue(monitor.getCheckAlertFrequencyResult());
        monitor.report(null,false);
        Assert.assertNull(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertFalse(monitor.getCheckAlertFrequencyResult());
        ThreadUtils.sleep(TEST_ALERT_INTERVAL_MS, TimeUnit.MILLISECONDS);
        monitor.report(null,false);
        Assert.assertNull(monitor.getCheckStatusResult());
        Assert.assertNull(monitor.getCheckAlertFrequencyResult());
        monitor.report(mockException(),true);
        Assert.assertFalse(monitor.getCheckStatusResult());
        Assert.assertTrue(monitor.getCheckAlertFrequencyResult());
    }

    @Test
    public void testContinuousWriteFailuresWithReadSuccessesConcurrent() {
        AtomicLong errorCount = new AtomicLong(0);
        AtomicLong alertCount = new AtomicLong(0);
        DefaultDataSourceMonitorDelegate monitor = createMonitor(errorCount::incrementAndGet, alertCount::incrementAndGet, null);
        ExecutorService executor1 = Executors.newFixedThreadPool(4);
        ExecutorService executor2 = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 25; i++) {
            executor1.submit(() -> {
                try {
                    monitor.report(mockException(), true);
                } finally {
                    latch.countDown();
                }
            });
            executor2.submit(() -> {
                try {
                    monitor.report(null, false);
                } finally {
                    latch.countDown();
                }
            });
        }
        ThreadUtils.sleep(TEST_ALERT_THRESHOLD_MS + 20, TimeUnit.MILLISECONDS);
        for (int i = 0; i < 25; i++) {
            executor1.submit(() -> {
                try {
                    monitor.report(null, false);
                } finally {
                    latch.countDown();
                }
            });
            executor2.submit(() -> {
                try {
                    monitor.report(mockException(), true);
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(25, errorCount.get());
        Assert.assertTrue(alertCount.get() > 0);
    }

    @Test
    public void parseIgnoreExceptionsTest() {
        DefaultDataSourceMonitorDelegate delegate = createMonitor("");
        Assert.assertEquals(0, delegate.ignoreExceptions.size());

        delegate.reparseIgnoreExceptions("com.mysql.jdbc.exceptions.MySQLSyntaxErrorException, com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException");
        Assert.assertEquals(2, delegate.ignoreExceptions.size());
        List<Class> expectExceptions = new ArrayList<>();
        expectExceptions.add(MySQLSyntaxErrorException.class);
        expectExceptions.add(MySQLIntegrityConstraintViolationException.class);

        Assert.assertEquals(true, delegate.ignoreExceptions.containsAll(expectExceptions));
    }

    @Test
    public void isExecuteException() {
        DefaultDataSourceMonitorDelegate delegate = createMonitor("com.mysql.jdbc.exceptions.MySQLSyntaxErrorException, com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException, ,java.sql.SQLWarning");
        Assert.assertEquals(true, delegate.isExecuteException(mockException("com.mysql.jdbc.exceptions.MySQLSyntaxErrorException")));
        Assert.assertEquals(true, delegate.isExecuteException(new MysqlDataTruncation("", 1, false, false, 100, 1000, 100)));
        Assert.assertEquals(false, delegate.isExecuteException(mockException()));
    }

    private DefaultDataSourceMonitorDelegate createMonitor() {
        return createMonitor(null, null, null);
    }

    private DefaultDataSourceMonitorDelegate createMonitor(String exception) {
        return createMonitor(null, null, exception);
    }

    private DefaultDataSourceMonitorDelegate createMonitor(Runnable checkStatusCallback,
                                                           Runnable checkAlertFrequencyCallback, String exception) {
        DefaultDataSourceMonitorDelegate monitor = new DefaultDataSourceMonitorDelegate(new DataSourceName("test"),
                checkStatusCallback, checkAlertFrequencyCallback, exception);
        monitor.setAlertThresholdMs(TEST_ALERT_THRESHOLD_MS);
        monitor.setAlertThresholdSamples(TEST_ALERT_THRESHOLD_SAMPLES);
        monitor.setAlertIntervalMs(TEST_ALERT_INTERVAL_MS);
        return monitor;
    }

    private SQLException mockException() {
        return new SQLException("test");
    }

    private SQLException mockException(String exception) {
        try {
            return  (SQLException)Class.forName(exception).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
