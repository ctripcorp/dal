package com.ctrip.datasource.helper.DNS;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
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
    private static final String THREAD_NAME = "DAL-DBDomainCheckThread";
    private static final int THREAD_SIZE = 1;
    private static final int INITIAL_DELAY = 0;
    private static final int FIXED_DELAY = 5 * 1000;

    private static AtomicReference<ScheduledExecutorService> executorRef = new AtomicReference<>();
    private static AtomicBoolean started = new AtomicBoolean(false);

    private ConcurrentMap<String, RefreshableDataSource> dataSourceMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, String> ipMap = new ConcurrentHashMap<>();
    private DNSUtil dnsUtil = new DNSUtil();

    private static final String DAL = "DAL";
    private static final String IP_OF_DOMAIN_URL_CHANGED_FORMAT = "IPOfDomainUrlChanged:%s";

    private static final String NETWORKADDRESS_CACHE_TTL = "networkaddress.cache.ttl";
    private static final String NETWORKADDRESS_CACHE_TTL_IN_SECONDS = "3";

    static {
        Runtime.getRuntime().addShutdownHook(new CustomThreadFactory(THREAD_NAME).newThread(new Runnable() {
            public void run() {
                shutdown();
            }
        }));
    }

    @Override
    public void start(RefreshableDataSource dataSource) {
        if (dataSource == null)
            return;

        SingleDataSource singleDataSource = dataSource.getSingleDataSource();
        if (singleDataSource == null)
            return;

        String name = singleDataSource.getName();

        try {
            boolean isSqlServer = isSqlServer(singleDataSource);
            if (!isSqlServer)
                return;

            putToMap(name, dataSource);
            start();
        } catch (Throwable e) {
            LOGGER.error("An error occurred while starting database domain checking task.", e);
        }
    }

    private boolean isSqlServer(SingleDataSource dataSource) {
        try {
            return dataSource.getDataSourceConfigure().getDatabaseCategory().equals(DatabaseCategory.SqlServer);
        } catch (Throwable e) {
            return false;
        }
    }

    private void putToMap(String name, RefreshableDataSource dataSource) {
        dataSourceMap.putIfAbsent(name, dataSource);
    }

    private void start() throws Exception {
        if (started.getAndSet(true)) {
            return;
        }

        try {
            setNetworkaddressCacheTtl(); // avoid jvm dns cache

            ScheduledExecutorService executor =
                    Executors.newScheduledThreadPool(THREAD_SIZE, new CustomThreadFactory(THREAD_NAME));
            executor.scheduleWithFixedDelay(new DatabaseDomainCheckerThread(), INITIAL_DELAY, FIXED_DELAY,
                    TimeUnit.MILLISECONDS);
            executorRef.set(executor);
        } catch (Throwable e) {
            LOGGER.error("An error occurred while about to execute database domain checker.", e);
        }
    }

    private void setNetworkaddressCacheTtl() {
        java.security.Security.setProperty(NETWORKADDRESS_CACHE_TTL, NETWORKADDRESS_CACHE_TTL_IN_SECONDS);
    }

    private class DatabaseDomainCheckerThread implements Runnable {
        @Override
        public void run() {
            Map<String, RefreshableDataSource> map = new HashMap<>(dataSourceMap); // avoid origin map being modified
            if (map.isEmpty())
                return;

            for (Map.Entry<String, RefreshableDataSource> entry : map.entrySet()) {
                try {
                    String key = entry.getKey();
                    RefreshableDataSource refreshableDataSource = entry.getValue();
                    SingleDataSource singleDataSource = refreshableDataSource.getSingleDataSource();
                    DataSourceConfigure configure = singleDataSource.getDataSourceConfigure();
                    String domain = configure.getConnectionString().getDomainConnectionStringConfigure().getHostName();
                    String currentIP = dnsUtil.resolveDNS(domain);
                    if (currentIP == null || currentIP.isEmpty())
                        continue;

                    String ip = ipMap.putIfAbsent(key, currentIP);
                    if (ip == null)
                        continue;

                    if (!ip.equals(currentIP)) {
                        ipMap.put(key, ip);
                        writeLog(key, domain, ip, currentIP);

                        // refresh datasource
                        refreshableDataSource.refreshDataSource(key, configure);
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
        started.set(false);
    }

}
