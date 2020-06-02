package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.configure.DalThreadPoolExecutorConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author c7ch23en
 */
public class DalThreadPoolExecutorTest {

    private final ThreadPoolExecutor executor;

    private static final String DB1 = "testDb1";
    private static final String DB2 = "testDb2";
    private static final String DB3 = "testDb3";

    public DalThreadPoolExecutorTest() {
        executor = new DalThreadPoolExecutor(new MockDalThreadPoolExecutorConfig(),
                new LinkedBlockingQueue<>(), new MockThreadFactory());
    }

    @Test
    public void testConcurrentExecutions() throws Exception {
        testStep1();
        TimeUnit.MILLISECONDS.sleep(25);
        testStep2();
    }

    private void testStep1() throws Exception {
        DalRequestCallable<long[]> db1Shard1Task1 = createTask(DB1, "1", 20);
        DalRequestCallable<long[]> db1Shard1Task2 = createTask(DB1, "1", 20);
        DalRequestCallable<long[]> db1Shard1Task3 = createTask(DB1, "1", 20);

        DalRequestCallable<long[]> db1Shard2Task1 = createTask(DB1, "2", 20);
        DalRequestCallable<long[]> db1Shard2Task2 = createTask(DB1, "2", 20);
        DalRequestCallable<long[]> db1Shard2Task3 = createTask(DB1, "2", 20);

        DalRequestCallable<long[]> db2Shard1Task1 = createTask(DB2, "1", 20);
        DalRequestCallable<long[]> db2Shard1Task2 = createTask(DB2, "1", 20);
        DalRequestCallable<long[]> db2Shard1Task3 = createTask(DB2, "1", 20);
        DalRequestCallable<long[]> db2Shard1Task4 = createTask(DB2, "1", 20);

        DalRequestCallable<long[]> db3Shard1Task1 = createTask(DB3, "1", 20);
        DalRequestCallable<long[]> db3Shard1Task2 = createTask(DB3, "1", 20);
        DalRequestCallable<long[]> db3Shard1Task3 = createTask(DB3, "1", 20);
        DalRequestCallable<long[]> db3Shard1Task4 = createTask(DB3, "1", 20);

        Future<long[]> db1Shard1Future1 = executor.submit(db1Shard1Task1);
        Future<long[]> db1Shard1Future2 = executor.submit(db1Shard1Task2);

        Future<long[]> db1Shard2Future1 = executor.submit(db1Shard2Task1);
        Future<long[]> db1Shard2Future2 = executor.submit(db1Shard2Task2);

        Future<long[]> db2Shard1Future1 = executor.submit(db2Shard1Task1);
        Future<long[]> db2Shard1Future2 = executor.submit(db2Shard1Task2);
        Future<long[]> db2Shard1Future3 = executor.submit(db2Shard1Task3);

        Future<long[]> db3Shard1Future1 = executor.submit(db3Shard1Task1);
        Future<long[]> db3Shard1Future2 = executor.submit(db3Shard1Task2);
        Future<long[]> db3Shard1Future3 = executor.submit(db3Shard1Task3);
        Future<long[]> db3Shard1Future4 = executor.submit(db3Shard1Task4);

        TimeUnit.MILLISECONDS.sleep(10);

        Future<long[]> db1Shard1Future3 = executor.submit(db1Shard1Task3);
        Future<long[]> db1Shard2Future3 = executor.submit(db1Shard2Task3);
        Future<long[]> db2Shard1Future4 = executor.submit(db2Shard1Task4);

        long[] db1Shard1Result1 = db1Shard1Future1.get();
        long[] db1Shard1Result2 = db1Shard1Future2.get();
        long[] db1Shard1Result3 = db1Shard1Future3.get();
        Assert.assertTrue(max(db1Shard1Result1[0], db1Shard1Result2[0]) <
                min(db1Shard1Result1[1], db1Shard1Result2[1]));
        Assert.assertTrue(db1Shard1Result3[0] >=
                min(db1Shard1Result1[1], db1Shard1Result2[1]));

        long[] db1Shard2Result1 = db1Shard2Future1.get();
        long[] db1Shard2Result2 = db1Shard2Future2.get();
        long[] db1Shard2Result3 = db1Shard2Future3.get();
        Assert.assertTrue(max(db1Shard2Result1[0], db1Shard2Result2[0]) <
                min(db1Shard2Result1[1], db1Shard2Result2[1]));
        Assert.assertTrue(db1Shard2Result3[0] >=
                min(db1Shard2Result1[1], db1Shard2Result2[1]));

        long[] db2Shard1Result1 = db2Shard1Future1.get();
        long[] db2Shard1Result2 = db2Shard1Future2.get();
        long[] db2Shard1Result3 = db2Shard1Future3.get();
        long[] db2Shard1Result4 = db2Shard1Future4.get();
        Assert.assertTrue(max(db2Shard1Result1[0], db2Shard1Result2[0], db2Shard1Result3[0]) <
                min(db2Shard1Result1[1], db2Shard1Result2[1], db2Shard1Result3[1]));
        Assert.assertTrue(db2Shard1Result4[0] >=
                min(db2Shard1Result1[1], db2Shard1Result2[1], db2Shard1Result3[1]));

        long[] db3Shard1Result1 = db3Shard1Future1.get();
        long[] db3Shard1Result2 = db3Shard1Future2.get();
        long[] db3Shard1Result3 = db3Shard1Future3.get();
        long[] db3Shard1Result4 = db3Shard1Future4.get();
        Assert.assertTrue(max(db3Shard1Result1[0], db3Shard1Result2[0], db3Shard1Result3[0], db3Shard1Result4[0]) <
                min(db3Shard1Result1[1], db3Shard1Result2[1], db3Shard1Result3[1], db3Shard1Result4[1]));
    }

    private void testStep2() throws Exception {
        DalRequestCallable<long[]> db1Shard1Task1 = createTask(DB1, "1", 20);
        DalRequestCallable<long[]> db1Shard1Task2 = createTask(DB1, "1", 20);

        DalRequestCallable<long[]> db1Shard2Task1 = createTask(DB1, "2", 20);
        DalRequestCallable<long[]> db1Shard2Task2 = createTask(DB1, "2", 20);

        DalRequestCallable<long[]> db2Shard1Task1 = createTask(DB2, "1", 20);
        DalRequestCallable<long[]> db2Shard1Task2 = createTask(DB2, "1", 20);
        DalRequestCallable<long[]> db2Shard1Task3 = createTask(DB2, "1", 20);

        DalRequestCallable<long[]> db3Shard1Task1 = createTask(DB3, "1", 20);
        DalRequestCallable<long[]> db3Shard1Task2 = createTask(DB3, "1", 20);
        DalRequestCallable<long[]> db3Shard1Task3 = createTask(DB3, "1", 20);
        DalRequestCallable<long[]> db3Shard1Task4 = createTask(DB3, "1", 20);

        Future<long[]> db1Shard1Future1 = executor.submit(db1Shard1Task1);
        Future<long[]> db1Shard1Future2 = executor.submit(db1Shard1Task2);

        Future<long[]> db1Shard2Future1 = executor.submit(db1Shard2Task1);
        Future<long[]> db1Shard2Future2 = executor.submit(db1Shard2Task2);

        Future<long[]> db2Shard1Future1 = executor.submit(db2Shard1Task1);
        Future<long[]> db2Shard1Future2 = executor.submit(db2Shard1Task2);
        Future<long[]> db2Shard1Future3 = executor.submit(db2Shard1Task3);

        Future<long[]> db3Shard1Future1 = executor.submit(db3Shard1Task1);
        Future<long[]> db3Shard1Future2 = executor.submit(db3Shard1Task2);
        Future<long[]> db3Shard1Future3 = executor.submit(db3Shard1Task3);
        Future<long[]> db3Shard1Future4 = executor.submit(db3Shard1Task4);

        long[] db1Shard1Result1 = db1Shard1Future1.get();
        long[] db1Shard1Result2 = db1Shard1Future2.get();
        Assert.assertTrue(max(db1Shard1Result1[0], db1Shard1Result2[0]) <
                min(db1Shard1Result1[1], db1Shard1Result2[1]));

        long[] db1Shard2Result1 = db1Shard2Future1.get();
        long[] db1Shard2Result2 = db1Shard2Future2.get();
        Assert.assertTrue(max(db1Shard2Result1[0], db1Shard2Result2[0]) <
                min(db1Shard2Result1[1], db1Shard2Result2[1]));

        long[] db2Shard1Result1 = db2Shard1Future1.get();
        long[] db2Shard1Result2 = db2Shard1Future2.get();
        long[] db2Shard1Result3 = db2Shard1Future3.get();
        Assert.assertTrue(max(db2Shard1Result1[0], db2Shard1Result2[0], db2Shard1Result3[0]) <
                min(db2Shard1Result1[1], db2Shard1Result2[1], db2Shard1Result3[1]));

        long[] db3Shard1Result1 = db3Shard1Future1.get();
        long[] db3Shard1Result2 = db3Shard1Future2.get();
        long[] db3Shard1Result3 = db3Shard1Future3.get();
        long[] db3Shard1Result4 = db3Shard1Future4.get();
        Assert.assertTrue(max(db3Shard1Result1[0], db3Shard1Result2[0], db3Shard1Result3[0], db3Shard1Result4[0]) <
                min(db3Shard1Result1[1], db3Shard1Result2[1], db3Shard1Result3[1], db3Shard1Result4[1]));
    }

    private DalRequestCallable<long[]> createTask(String logicDbName, String shard, long delayMs) {
        return new DalRequestCallable<>(logicDbName, shard, () -> {
            long[] timestamps = new long[2];
            timestamps[0] = System.currentTimeMillis();
            TimeUnit.MILLISECONDS.sleep(delayMs);
            timestamps[1] = System.currentTimeMillis();
            return timestamps;
        });
    }

    private long max(long... values) {
        long result = Long.MIN_VALUE;
        for (long value : values)
            result = Math.max(result, value);
        return result;
    }

    private long min(long... values) {
        long result = Long.MAX_VALUE;
        for (long value : values)
            result = Math.min(result, value);
        return result;
    }

    static class MockDalThreadPoolExecutorConfig implements DalThreadPoolExecutorConfig {
        @Override
        public int getCorePoolSize() {
            return 20;
        }

        @Override
        public int getMaxPoolSize() {
            return 20;
        }

        @Override
        public long getKeepAliveSeconds() {
            return 10;
        }

        @Override
        public int getMaxThreadsPerShard(String logicDbName) {
            if ("testDb1".equalsIgnoreCase(logicDbName))
                return 2;
            if ("testDb2".equalsIgnoreCase(logicDbName))
                return 3;
            return 0;
        }
    }

    static class MockThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "test");
        }
    }

}
