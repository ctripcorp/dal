package com.ctrip.datasource.helper;

import com.ctrip.datasource.helper.DNS.DNSUtil;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.datasource.DataSourceTerminator;
import com.ctrip.platform.dal.dao.datasource.SingleDataSource;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.DatabaseDomainChecker;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CtripDatabaseDomainChecker implements DatabaseDomainChecker {
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String THREAD_NAME = "CtripDatabaseDomainChecker";
    private static final int THREAD_SIZE = 1;
    private static final int INITIAL_DELAY = 0;
    private static final int FIXED_DELAY = 5 * 1000;

    private static AtomicReference<ScheduledExecutorService> executorRef = new AtomicReference<>();
    private static AtomicBoolean startedRef = new AtomicBoolean(false);

    private ConcurrentMap<String, SingleDataSource> dataSourceMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, String> ipMap = new ConcurrentHashMap<>();
    private DNSUtil dnsUtil = new DNSUtil();

    private static final String DAL = "DAL";
    private static final String IP_OF_DOMAIN_URL_CHANGED_FORMAT = "IPOfDomainUrlChanged:%s";

    static {
        Runtime.getRuntime().addShutdownHook(new CustomThreadFactory(THREAD_NAME).newThread(new Runnable() {
            public void run() {
                shutdown();
            }
        }));
    }

    @Override
    public void startCheckingTask(String name, SingleDataSource dataSource) {
        try {
            boolean isSqlServer = isSqlServer(dataSource);
            if (!isSqlServer)
                return;

            putToMap(name, dataSource);
            start();
        } catch (Throwable e) {
            LOGGER.error("An error occured while starting database domain checking task.", e);
        }
    }

    private boolean isSqlServer(SingleDataSource dataSource) {
        return dataSource.getDataSourceConfigure().getDatabaseCategory().equals(DatabaseCategory.SqlServer);
    }

    private void putToMap(String name, SingleDataSource dataSource) {
        dataSourceMap.putIfAbsent(name, dataSource);
    }

    private void start() throws Exception {
        if (startedRef.getAndSet(true)) {
            return;
        }

        try {
            ScheduledExecutorService executor =
                    Executors.newScheduledThreadPool(THREAD_SIZE, new CustomThreadFactory(THREAD_NAME));
            executor.scheduleWithFixedDelay(new DatabaseDomainCheckerThread(), INITIAL_DELAY, FIXED_DELAY,
                    TimeUnit.MILLISECONDS);
            executorRef.set(executor);
        } catch (Throwable e) {
            LOGGER.error("An error occured while about to execute database domain checker.", e);
        }
    }

    private class DatabaseDomainCheckerThread implements Runnable {
        @Override
        public void run() {
            Map<String, SingleDataSource> map = new HashMap<>(dataSourceMap); // avoid original map being modified
            if (map.isEmpty())
                return;

            for (Map.Entry<String, SingleDataSource> entry : map.entrySet()) {
                try {
                    String key = entry.getKey();
                    SingleDataSource dataSource = entry.getValue();
                    String domain = dataSource.getDataSourceConfigure().getConnectionString()
                            .getDomainConnectionStringConfigure().getHostName();
                    String currentIP = dnsUtil.resolveDNS(domain);
                    if (currentIP == null || currentIP.isEmpty())
                        continue;

                    if (!ipMap.containsKey(key)) {
                        ipMap.put(key, currentIP);
                        continue;
                    }

                    String ip = ipMap.get(key);
                    if (!ip.equals(currentIP)) {
                        ipMap.put(key, ip);
                        writeLog(key, domain, ip, currentIP);

                        // close datasource
                        DataSourceTerminator.getInstance().close(dataSource);
                    }
                } catch (Throwable e) {
                    LOGGER.error("An error occured while executing database domain checking task.", e);
                }
            }
        }
    }

    private void writeLog(String key, String domainUrl, String ip, String currentIP) {
        String data = String.format("Domain URL: %s, Origin IP: %s, Current IP: %s", domainUrl, ip, currentIP);
        String eventName = String.format(IP_OF_DOMAIN_URL_CHANGED_FORMAT, key);
        LOGGER.logEvent(DAL, eventName, data);
        LOGGER.info(data);
    }

    private static void shutdown() {
        ExecutorService executor = executorRef.get();
        if (executor == null)
            return;

        executor.shutdown();
        executorRef.set(null);
        startedRef.compareAndSet(true, false);
    }

}
