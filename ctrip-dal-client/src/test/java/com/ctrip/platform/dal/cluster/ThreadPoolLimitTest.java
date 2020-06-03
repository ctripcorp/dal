package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author c7ch23en
 */
public class ThreadPoolLimitTest {

    private static final String NORMAL_SQL = "select 1";
    private static final String SLEEP_SQL_TPL = "select sleep(%d)";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DalQueryDao dao1 = new DalQueryDao("TestCluster2");
    private final DalQueryDao dao2 = new DalQueryDao("TestCluster2Alias");

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Before
    public void before() throws SQLException {
        dao1.query(NORMAL_SQL, new StatementParameters(), new DalHints().inAllShards(), Integer.class);
        dao2.query(NORMAL_SQL, new StatementParameters(), new DalHints().inAllShards(), Integer.class);
    }

    @Test
    public void testShardThreadsLimit1() throws Exception {
        Future<TimeRecord> futureResult1 = getFutureResult(dao1, createHints(0, 1), 7);
        Future<TimeRecord> futureResult2 = getFutureResult(dao1, createHints(0, 1), 7);
        Future<TimeRecord> futureResult3 = getFutureResult(dao1, createHints(0, 1), 7);
        TimeUnit.MILLISECONDS.sleep(10);
        Future<TimeRecord> futureResult4 = getFutureResult(dao1, createHints(2, 3), 3);
        TimeRecord result1 = futureResult1.get();
        TimeRecord result2 = futureResult2.get();
        TimeRecord result3 = futureResult3.get();
        TimeRecord result4 = futureResult4.get();
        log(String.format("results:\n  %d - %d\n  %d - %d\n  %d - %d\n  %d - %d",
                result1.start, result1.end, result2.start, result2.end,
                result3.start, result3.end, result4.start, result4.end));
        Assert.assertTrue(result4.end >= min(result1.end, result2.end, result3.end));
    }

    @Test
    public void testShardThreadsLimit2() throws Exception {
        Future<TimeRecord> futureResult1 = getFutureResult(dao2, createHints(0, 1), 7);
        Future<TimeRecord> futureResult2 = getFutureResult(dao2, createHints(0, 1), 7);
        Future<TimeRecord> futureResult3 = getFutureResult(dao2, createHints(0, 1), 7);
        TimeUnit.MILLISECONDS.sleep(10);
        Future<TimeRecord> futureResult4 = getFutureResult(dao2, createHints(2, 3), 3);
        TimeRecord result1 = futureResult1.get();
        TimeRecord result2 = futureResult2.get();
        TimeRecord result3 = futureResult3.get();
        TimeRecord result4 = futureResult4.get();
        log(String.format("results:\n  %d - %d\n  %d - %d\n  %d - %d\n  %d - %d",
                result1.start, result1.end, result2.start, result2.end,
                result3.start, result3.end, result4.start, result4.end));
        Assert.assertTrue(result4.end < min(result1.end, result2.end, result3.end));
    }

    private DalHints createHints(int... shards) {
        Set<String> shardSet = new HashSet<>();
        for (int shard : shards)
            shardSet.add(String.valueOf(shard));
        return new DalHints().inShards(shardSet);
    }

    private Future<TimeRecord> getFutureResult(DalQueryDao dao, DalHints hints, int sleep) {
        return executor.submit(() -> {
            long start = System.currentTimeMillis();
            try {
                String sql = sleep > 0 ? String.format(SLEEP_SQL_TPL, sleep) : NORMAL_SQL;
                dao.query(sql, new StatementParameters(), hints, Integer.class);
            } catch (Exception e) {
                log(e.getMessage());
            }
            long end = System.currentTimeMillis();
            return new TimeRecord(start, end);
        });
    }

    private long max(long... values) {
        long max = values[0];
        for (int i = 1; i < values.length; i++)
            max = Math.max(max, values[i]);
        return max;
    }

    private long min(long... values) {
        long min = values[0];
        for (int i = 1; i < values.length; i++)
            min = Math.min(min, values[i]);
        return min;
    }

    private void log(String message) {
        System.out.println(message);
//        logger.info(message);
    }

    static class TimeRecord {
        long start;
        long end;

        public TimeRecord(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

}
