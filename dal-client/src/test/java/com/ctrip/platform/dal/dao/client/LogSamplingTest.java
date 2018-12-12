package com.ctrip.platform.dal.dao.client;


import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;


public class LogSamplingTest {
    private DefaultLogSamplingStrategy strategy = new DefaultLogSamplingStrategy();
    private final static String QUERY = "query";
    private final static String UPDATE = "update";
    private final static String CALL = "call";
    private final static String OTHERS = "others";


    @Test
    public void testSamplingSettings() throws Exception {
        DalClientFactory.initClientFactory();
        assertEquals(10, LoggerAdapter.samplingRate);
        assertTrue(LoggerAdapter.samplingLogging);
    }

    @Test
    public void testDefaultLogSamplingStrategyConcurrent() throws Exception {
        String logicDB = "logicDB";
        Set<String> tables = new HashSet<>();
        tables.add("table");
        DalEventEnum event = DalEventEnum.UPDATE_SIMPLE;
        String method = "method";
        final MockLogEntry logEntry = createLogEntry(logicDB, tables, event, method);
        for (int j = 0; j < 100; j++) {
            int requireSize = 500;
            final CountDownLatch latch = new CountDownLatch(requireSize);
            final AtomicBoolean result = new AtomicBoolean(true);
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(500);
            for (int i = 0; i < requireSize; i++) {
                executor.submit(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            strategy.validate(logEntry);
                        } catch (Exception e) {
                            e.printStackTrace();
                            result.set(false);
                        } finally {
                            latch.countDown();
                        }
                    }
                }));
            }
            latch.await();
            try {
                Assert.assertTrue(result.get());
            }catch (Throwable e){
                fail("test DefaultLogSamplingStrategyConcurrent fail!");
            }
        }
    }

    @Test
    public void testDefaultLogSamplingStrategy() {
        String logicDB;
        Set<String> tables;
        DalEventEnum event;
        String method;
        MockLogEntry logEntry;
        for (int db = 0; db < 5; db++) {
            logicDB = "logicDB" + db;
            for (int table = 0; table < 5; table++) {
                tables = new HashSet<>();
                tables.add("table1" + table);
                tables.add("table2" + table);
                for (int eventNum = 0; eventNum < 4; eventNum++) {
                    switch (eventNum) {
                        case 0:
                            event = DalEventEnum.QUERY;
                            break;
                        case 1:
                            event = DalEventEnum.UPDATE_KH;
                            break;
                        case 2:
                            event = DalEventEnum.CALL;
                            break;
                        case 3:
                            event = DalEventEnum.EXECUTE;
                            break;
                        default:
                            event = DalEventEnum.QUERY;
                            break;
                    }
                    for (int m = 0; m < 200; m++) {
                        method = "method" + m;
                        logEntry = new MockLogEntry(logicDB, tables, event, method);
                        strategy.validate(logEntry);
                    }
                }
            }
        }
//        strategy
        LogCacheForDatabaseSetAndTables logCacheForDatabaseSetAndTables = strategy.getLogCacheForDatabaseSetAndTables();
        Map<String, LogCacheForTableAndOperations> databaseSetAndTableCacheMap = logCacheForDatabaseSetAndTables.getDatabaseSetAndTableCache();
        assertEquals(5, databaseSetAndTableCacheMap.size());
        for (int db = 0; db < 5; db++)
            assertTrue(databaseSetAndTableCacheMap.containsKey("logicDB" + db));


//        LogCacheForTableAndOperations
        LogCacheForTableAndOperations logCacheForTableAndOperations = databaseSetAndTableCacheMap.get("logicDB1");
        Map<String, LogCacheForOperationAndDaoMethods> tableAndOperationMap = logCacheForTableAndOperations.getTableAndOperationCache();
        assertEquals(5, tableAndOperationMap.size());
        for (int table = 0; table < 5; table++) {
            tables = new HashSet<>();
            tables.add("table1" + table);
            tables.add("table2" + table);
            assertTrue(tableAndOperationMap.containsKey(LoggerHelper.setToOrderedString(tables)));
        }

//        LogCacheForOperationAndDaoMethods
        LogCacheForOperationAndDaoMethods logCacheForOperationAndDaoMethods = tableAndOperationMap.get("table10,table20");
        Map<String, LogCacheForDaoMethodsAndRandomNum> operationAndMethodsMap = logCacheForOperationAndDaoMethods.getOperationAndDaoMethodCache();
        assertEquals(4, operationAndMethodsMap.size());
        assertTrue(operationAndMethodsMap.containsKey(QUERY));
        assertTrue(operationAndMethodsMap.containsKey(UPDATE));
        assertTrue(operationAndMethodsMap.containsKey(CALL));
        assertTrue(operationAndMethodsMap.containsKey(OTHERS));

//        LogCacheForDaoMethodsAndRandomNum
        LogCacheForDaoMethodsAndRandomNum logCacheForDaoMethodsAndRandomNum = operationAndMethodsMap.get(QUERY);
        Map<String, Random> daoMethodAndRandomNumMap = logCacheForDaoMethodsAndRandomNum.getMethodsAndRandomNumCache();
        assertEquals(100, daoMethodAndRandomNumMap.size());
        for (int m = 0; m < 100; m++) {
            assertTrue(daoMethodAndRandomNumMap.containsKey("method" + m));
        }
        for (int m = 100; m < 200; m++) {
            assertFalse(daoMethodAndRandomNumMap.containsKey("method" + m));
        }
    }

    @Test
    public void testLogCacheForDatabaseSetAndTablesSequence() throws Exception {
        LogCacheForDatabaseSetAndTables logCacheForDatabaseSetAndTables = new LogCacheForDatabaseSetAndTables();

        String logicDB = "logicDB";
        Set<String> tables = new HashSet<>();
        tables.add("table");
        DalEventEnum event = DalEventEnum.UPDATE_SIMPLE;
        String method = "method";
        MockLogEntry logEntry = createLogEntry(logicDB, tables, event, method);

        logCacheForDatabaseSetAndTables.validateDatabaseSetAndTablesCache(logEntry);

        Map<String, LogCacheForTableAndOperations> map = logCacheForDatabaseSetAndTables.getDatabaseSetAndTableCache();
        assertEquals(1, map.size());
        assertTrue(map.containsKey(logicDB));
        LogCacheForTableAndOperations tableCache = map.get(logicDB);

        logCacheForDatabaseSetAndTables.validateDatabaseSetAndTablesCache(logEntry);
        map = logCacheForDatabaseSetAndTables.getDatabaseSetAndTableCache();
        assertEquals(1, map.size());
        assertTrue(map.containsKey(logicDB));
        assertEquals(tableCache, map.get(logicDB));
    }

    @Test
    public void testLogCacheForDatabaseSetAndTablesConcurrent() throws Exception {
        final LogCacheForDatabaseSetAndTables logCacheForDatabaseSetAndTables = new LogCacheForDatabaseSetAndTables();

        final String logicDB = "logicDB";
        final Set<String> tables = new HashSet<>();
        tables.add("table");
        final DalEventEnum event = DalEventEnum.CALL;
        final String method = "method";

        final List<LogCacheForTableAndOperations> list = Collections.synchronizedList(new ArrayList<LogCacheForTableAndOperations>());
        int requireSize = 5000;
        final CountDownLatch latch = new CountDownLatch(requireSize);
        final AtomicBoolean result = new AtomicBoolean(true);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < requireSize; i++) {
            executor.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MockLogEntry logEntry = createLogEntry(logicDB, tables, event, method);
                        logCacheForDatabaseSetAndTables.validateDatabaseSetAndTablesCache(logEntry);
                        list.add(logCacheForDatabaseSetAndTables.getDatabaseSetAndTableCache().get(logicDB));
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            }));
        }
        latch.await();
        assertTrue(result.get());
        assertEquals(requireSize, list.size());

        Set<LogCacheForTableAndOperations> set = new HashSet<>();
        set.addAll(list);
        assertEquals(1, set.size());
    }

    @Test
    public void testLogCacheForTableAndOperationsSequence() throws Exception {
        LogCacheForTableAndOperations logCacheForTableAndOperations = new LogCacheForTableAndOperations();

        String logicDB = "logicDB";
        Set<String> tables = new HashSet<>();
        tables.add("table");
        DalEventEnum event = DalEventEnum.EXECUTE;
        String method = "method";
        MockLogEntry logEntry = createLogEntry(logicDB, tables, event, method);

        String tableString = LoggerHelper.setToOrderedString(tables);
        logCacheForTableAndOperations.validateTableAndOperationCache(tableString, logEntry);

        Map<String, LogCacheForOperationAndDaoMethods> map = logCacheForTableAndOperations.getTableAndOperationCache();
        assertEquals(1, map.size());
        assertTrue(map.containsKey(tableString));
        LogCacheForOperationAndDaoMethods operationCache = map.get(tableString);

        logCacheForTableAndOperations.validateTableAndOperationCache(tableString, logEntry);
        map = logCacheForTableAndOperations.getTableAndOperationCache();
        assertEquals(1, map.size());
        assertTrue(map.containsKey(tableString));
        assertEquals(operationCache, map.get(tableString));
    }

    @Test
    public void testLogCacheForTableAndOperationsConcurrent() throws Exception {
        final LogCacheForTableAndOperations logCacheForTableAndOperations = new LogCacheForTableAndOperations();

        final String logicDB = "logicDB";
        final Set<String> tables = new HashSet<>();
        tables.add("table");
        final DalEventEnum event = DalEventEnum.BATCH_UPDATE;
        final String method = "method";
        final String tableString = LoggerHelper.setToOrderedString(tables);
        final List<LogCacheForOperationAndDaoMethods> list = Collections.synchronizedList(new ArrayList<LogCacheForOperationAndDaoMethods>());
        int requireSize = 5000;
        final CountDownLatch latch = new CountDownLatch(requireSize);
        final AtomicBoolean result = new AtomicBoolean(true);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < requireSize; i++) {
            executor.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MockLogEntry logEntry = createLogEntry(logicDB, tables, event, method);
                        logCacheForTableAndOperations.validateTableAndOperationCache(tableString, logEntry);
                        list.add(logCacheForTableAndOperations.getTableAndOperationCache().get(tableString));
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            }));
        }
        latch.await();
        assertTrue(result.get());
        assertEquals(requireSize, list.size());

        Set<LogCacheForOperationAndDaoMethods> set = new HashSet<>();
        set.addAll(list);
        assertEquals(1, set.size());
    }

    @Test
    public void testLogCacheForOperationAndDaoMethodsSequence() throws Exception {
        LogCacheForOperationAndDaoMethods logCacheForOperationAndDaoMethods = new LogCacheForOperationAndDaoMethods();

        String logicDB = "logicDB";
        Set<String> tables = new HashSet<>();
        tables.add("table");
        DalEventEnum event = DalEventEnum.CONNECTION_FAILED;
        String method = "method";
        MockLogEntry logEntry = createLogEntry(logicDB, tables, event, method);

        logCacheForOperationAndDaoMethods.validateOperationAndDaoMethodsCache(logEntry);

        Map<String, LogCacheForDaoMethodsAndRandomNum> map = logCacheForOperationAndDaoMethods.getOperationAndDaoMethodCache();
        assertEquals(1, map.size());
        assertTrue(map.containsKey(OTHERS));
        LogCacheForDaoMethodsAndRandomNum methodCache = map.get(OTHERS);

        logCacheForOperationAndDaoMethods.validateOperationAndDaoMethodsCache(logEntry);
        map = logCacheForOperationAndDaoMethods.getOperationAndDaoMethodCache();
        assertEquals(1, map.size());
        assertTrue(map.containsKey(OTHERS));
        assertEquals(methodCache, map.get(OTHERS));
    }

    @Test
    public void testLogCacheForOperationAndDaoMethodsConcurrent() throws Exception {
        final LogCacheForOperationAndDaoMethods logCacheForOperationAndDaoMethods = new LogCacheForOperationAndDaoMethods();

        final String logicDB = "logicDB";
        final Set<String> tables = new HashSet<>();
        tables.add("table");
        final DalEventEnum event = DalEventEnum.UPDATE_KH;
        final String method = "method";

        final List<LogCacheForDaoMethodsAndRandomNum> list = Collections.synchronizedList(new ArrayList<LogCacheForDaoMethodsAndRandomNum>());
        int requireSize = 5000;
        final CountDownLatch latch = new CountDownLatch(requireSize);
        final AtomicBoolean result = new AtomicBoolean(true);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < requireSize; i++) {
            executor.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MockLogEntry logEntry = createLogEntry(logicDB, tables, event, method);
                        logCacheForOperationAndDaoMethods.validateOperationAndDaoMethodsCache(logEntry);
                        list.add(logCacheForOperationAndDaoMethods.getOperationAndDaoMethodCache().get(UPDATE));
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            }));
        }
        latch.await();
        assertTrue(result.get());
        assertEquals(requireSize, list.size());

        Set<LogCacheForDaoMethodsAndRandomNum> set = new HashSet<>();
        set.addAll(list);
        assertEquals(1, set.size());
    }

    @Test
    public void testLogCacheForDaoMethodsAndRandomNumSequence() throws Exception {
        LogCacheForDaoMethodsAndRandomNum logCacheForDaoMethodsAndRandomNum = new LogCacheForDaoMethodsAndRandomNum();

        String logicDB = "logicDB";
        Set<String> tables = new HashSet<>();
        tables.add("table");
        DalEventEnum event = DalEventEnum.BATCH_CALL;
        MockLogEntry logEntry;
        String method = "method";
        boolean validate;

        logEntry = createLogEntry(logicDB, tables, event, method);
        validate = logCacheForDaoMethodsAndRandomNum.validateMethodAndCountCache(logEntry);

//      log first time
        assertTrue(validate);
        Map<String, Random> map = logCacheForDaoMethodsAndRandomNum.getMethodsAndRandomNumCache();
        assertEquals(1, map.size());
        assertTrue(map.containsKey(method));
        Random random = map.get(method);


//      log same method multiple times
        for (int rate = 0; rate <= 100; rate++) {
            validateRandom(rate, logEntry, logCacheForDaoMethodsAndRandomNum);
            map = logCacheForDaoMethodsAndRandomNum.getMethodsAndRandomNumCache();
            assertEquals(1, map.size());
            assertTrue(map.containsKey(method));
            assertEquals(random, map.get(method));
        }

//      log different method
        int currentSize = map.size();
        for (int time = 0; time <= 100; time++) {
            String newMethod = "method" + time;
            logEntry = new MockLogEntry(logicDB, tables, event, newMethod);
            logCacheForDaoMethodsAndRandomNum.validateMethodAndCountCache(logEntry);
            map = logCacheForDaoMethodsAndRandomNum.getMethodsAndRandomNumCache();

//          MAX_METHODS_COUNT=100
            if (time < 99) {
                assertEquals(++currentSize, map.size());
                assertTrue(map.containsKey(newMethod));
                assertNotEquals(random, map.get(newMethod));
            } else {
                assertEquals(100, map.size());
                assertFalse(map.containsKey(newMethod));
            }
        }
    }

    private void validateRandom(int samplingRate, ILogEntry logEntry, LogCacheForDaoMethodsAndRandomNum logCacheForDaoMethodsAndRandomNum) {
        logCacheForDaoMethodsAndRandomNum.setSamplingRate(samplingRate);

        int validateCount = 0;
        int logCount = 1000;
        boolean validate;

        int upper = samplingRate * logCount / 100 + samplingRate * logCount * 9 / 1000;
        int lower = samplingRate * logCount / 100 - samplingRate * logCount * 9 / 1000;

        for (int i = 0; i < logCount; i++) {
            validate = logCacheForDaoMethodsAndRandomNum.validateMethodAndCountCache(logEntry);
            if (validate == true)
                validateCount++;
        }

        if (samplingRate == 0) {
            assertTrue(String.format("current rate: %d, validateCount: %d not equal to 0", samplingRate, validateCount), validateCount == 0);
            return;
        }
        if (samplingRate == 100) {
            assertTrue(String.format("current rate: %d, validateCount: %d not equal to %d", samplingRate, validateCount, logCount), validateCount == logCount);
            return;
        }
        assertTrue(String.format("current rate: %d, validateCount: %d less than lower: %d", samplingRate, validateCount, lower), validateCount > lower);
        assertTrue(String.format("current rate: %d, validateCount: %d greater than upper: %d", samplingRate, validateCount, upper), validateCount < upper);
    }

    @Test
    public void testLogCacheForDaoMethodsAndRandomNumConcurrent() throws Exception {
        final LogCacheForDaoMethodsAndRandomNum logCacheForDaoMethodsAndRandomNum = new LogCacheForDaoMethodsAndRandomNum();

        final String logicDB = "logicDB";
        final Set<String> tables = new HashSet<>();
        tables.add("table");
        final DalEventEnum event = DalEventEnum.UPDATE_KH;
        final String method = "method";

        final List<Random> list = Collections.synchronizedList(new ArrayList<Random>());
        int requireSize = 5000;
        final CountDownLatch latch = new CountDownLatch(requireSize);
        final AtomicBoolean result = new AtomicBoolean(true);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < requireSize; i++) {
            executor.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MockLogEntry logEntry = createLogEntry(logicDB, tables, event, method);
                        logCacheForDaoMethodsAndRandomNum.validateMethodAndCountCache(logEntry);
                        list.add(logCacheForDaoMethodsAndRandomNum.getMethodsAndRandomNumCache().get(method));
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.set(false);
                    } finally {
                        latch.countDown();
                    }
                }
            }));
        }
        latch.await();
        assertTrue(result.get());
        assertEquals(requireSize, list.size());

        Set<Random> set = new HashSet<>();
        set.addAll(list);
        assertEquals(1, set.size());

        Map<String, Random> map = logCacheForDaoMethodsAndRandomNum.getMethodsAndRandomNumCache();
        assertEquals(1, map.size());
    }

    private MockLogEntry createLogEntry(String logicDB, Set<String> tables, DalEventEnum event, String method) {
        return new MockLogEntry(logicDB, tables, event, method);
    }

}
