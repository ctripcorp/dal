package com.ctrip.framework.idgen.test;

import com.ctrip.framework.idgen.client.IdGeneratorFactory;
import com.ctrip.framework.idgen.test.exception.DuplicatedIdException;
import com.ctrip.framework.idgen.test.exception.NullIdException;
import com.ctrip.platform.dal.sharding.idgen.LongIdGenerator;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class StabilityTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StabilityTest.class);

    private String sequenceName = System.getProperty("sequenceName", "testName1.t2");
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private int expectedQPS = Integer.parseInt(System.getProperty("expectedQPS", "50000"));
    private int qpsCheckPeriod = Integer.parseInt(System.getProperty("qpsCheckPeriodSeconds", "5"));
    private boolean checkDuplication = Boolean.parseBoolean(System.getProperty("checkDuplication", "true"));
    private final AtomicReference<Set<Long>> idSet = new AtomicReference<Set<Long>>(new HashSet<Long>());
    private int setCleanPeriod = Integer.parseInt(System.getProperty("setCleanPeriodSeconds", "30"));
    private AtomicLong atomCount = new AtomicLong(0);
    private long lastCount = 0;
    private long lastTime = System.currentTimeMillis();
    private String[] workerIdList = System.getProperty("workerIdList", "10,11").split(",");
    private final Map<Long, AtomicLong> workerMap = new HashMap<>();
    private final Map<Long, AtomicLong> unexpectedWorkerMap = new HashMap<>();

    public static void main(String[] args) {
        StabilityTest test = new StabilityTest();
//        test.specifyServer("localhost");
        test.setEnvironment();
        test.initWorkerMap();
        test.start();
    }

    private void start() {
        startQPSChecker();
        if (checkDuplication) {
            startSetCleaner();
        }

        final LongIdGenerator generator = IdGeneratorFactory.getInstance().getOrCreateLongIdGenerator(sequenceName);

        int taskPeriod = 1000000 / expectedQPS;  // microseconds
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    long id = generator.nextId();
                    atomCount.incrementAndGet();
                    if (checkDuplication) {
                        boolean ok;
                        synchronized (idSet) {
                            ok = idSet.get().add(id);
                        }
                        if (!ok) {
                            String msg = "Duplicated id: " + id;
                            LOGGER.error(msg);
                            Cat.logError(new DuplicatedIdException(msg));
                        }
                    }
                    long workerId = (id >> 16) & (~(-1L << 7));
                    AtomicLong worker = workerMap.get(workerId);
                    if (worker != null) {
                        worker.incrementAndGet();
                    } else {
                        AtomicLong unexpectedWorker = unexpectedWorkerMap.get(workerId);
                        if (null == unexpectedWorker) {
                            synchronized (unexpectedWorkerMap) {
                                unexpectedWorker = unexpectedWorkerMap.get(workerId);
                                if (null == unexpectedWorker) {
                                    unexpectedWorker = new AtomicLong(0);
                                    unexpectedWorkerMap.put(workerId, unexpectedWorker);
                                }
                            }
                        }
                        unexpectedWorker.incrementAndGet();
                    }
                } catch (NullPointerException e) {
                    String msg = "Null id";
                    LOGGER.error(msg, e);
                    Cat.logError(new NullIdException(msg, e));
                } catch (Exception e) {
                    String msg = "Unexpected exception";
                    LOGGER.error(msg, e);
                    Cat.logError(new RuntimeException(msg, e));
                }
            }
        }, 0, taskPeriod, TimeUnit.MICROSECONDS);
    }

    private void startSetCleaner() {
        LOGGER.info("Set clean period: {}s", setCleanPeriod);
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                synchronized (idSet) {
                    idSet.get().clear();
                }
                LOGGER.info("Set cleaned");
                Cat.logEvent("StabilityTest", "Set.clean");
            }
        }, setCleanPeriod, setCleanPeriod, TimeUnit.SECONDS);
    }

    private void startQPSChecker() {
        LOGGER.info("Expected QPS: {}", expectedQPS);
        LOGGER.info("QPS check period: {}s", qpsCheckPeriod);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Transaction transaction = Cat.newTransaction("StabilityTest", "QPS.check");
                long count = atomCount.get();
                long now = System.currentTimeMillis();
                long qps = 1000 * (count - lastCount) / (now - lastTime);
                lastCount = count;
                lastTime = now;
                String name = "RealQPS";
                LOGGER.info(String.format("%s: %d", name, qps));
                Cat.logSizeEvent(name, qps);

                for (Map.Entry<Long, AtomicLong> entry : workerMap.entrySet()) {
                    name = String.format("Worker[%d]", entry.getKey());
                    long value = entry.getValue().get();
                    LOGGER.info(String.format("%s: %d", name, value));
                    Cat.logSizeEvent(name, value);
                }
                for (Map.Entry<Long, AtomicLong> entry : unexpectedWorkerMap.entrySet()) {
                    name = String.format("UnexpectedWorker[%d]", entry.getKey());
                    long value = entry.getValue().get();
                    LOGGER.info(String.format("%s: %d", name, value));
                    Cat.logSizeEvent(name, value);
                }
                transaction.setStatus(Transaction.SUCCESS);
                transaction.complete();
            }
        }, qpsCheckPeriod, qpsCheckPeriod, TimeUnit.SECONDS);
    }

    private void initWorkerMap() {
        for (String workerId : workerIdList) {
            workerMap.put(Long.parseLong(workerId), new AtomicLong(0));
        }
    }

    private void specifyServer(String ip) {
        System.setProperty("com.ctrip.framework.idgen.service.api.IdGenerateService",
                String.format("dubbo://%s:20880", ip));
    }

    private void setEnvironment() {
        System.setProperty("java.awt.headless", "false");
        overrideArtemisUrl("10.2.35.218");
    }

    private void overrideArtemisUrl(String ip) {
        String url = String.format("http://%s:8080/artemis-service/", ip);
        System.setProperty("artemis.client.cdubbo.service.service.domain.url", url);
        System.setProperty("artemis.client.cdubbo.client.service.domain.url", url);
    }

}
