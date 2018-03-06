package com.ctrip.platform.dal.dao.configure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.exceptions.DalException;

public class FreshnessDatabaseSelector extends DefaultDatabaseSelector {
    private static final Map<String, Map<String, Integer>> freshnessCache = new ConcurrentHashMap<>();
    private static AtomicReference<ScheduledExecutorService> freshnessUpdatorRef = new AtomicReference<>();
    private static final int INVALID = FreshnessHelper.INVALID;
    private static final String THREAD_NAME = "FreshnessScanner";

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setName(THREAD_NAME);
                shutdown();
            }
        }));
    }

    /**
     * Need to be called during getQualifiedSlaveNames
     * 
     * @param configure
     */
    public static void initialize() {
        if (freshnessUpdatorRef.get() != null)
            return;

        synchronized (FreshnessScanner.class) {
            if (freshnessUpdatorRef.get() != null)
                return;

            freshnessCache.clear();
            DalConfigure configure = DalClientFactory.getDalConfigure();
            for (String logicDbName : configure.getDatabaseSetNames()) {
                Map<String, Integer> logicDbFreshnessMap = new ConcurrentHashMap<>();
                freshnessCache.put(logicDbName, logicDbFreshnessMap);

                DatabaseSet dbSet = configure.getDatabaseSet(logicDbName);
                for (Map.Entry<String, DataBase> dbEntry : dbSet.getDatabases().entrySet()) {
                    if (!dbEntry.getValue().isMaster())
                        logicDbFreshnessMap.put(dbEntry.getValue().getConnectionString(), INVALID);
                }
            }

            // Init task
            ScheduledExecutorService executer =
                    Executors.newScheduledThreadPool(1, new CustomThreadFactory(THREAD_NAME));
            executer.scheduleWithFixedDelay(new FreshnessScanner(freshnessCache), 0, 5, TimeUnit.SECONDS);
            freshnessUpdatorRef.set(executer);
        }
    }

    public static void shutdown() {
        if (freshnessUpdatorRef.get() == null)
            return;

        synchronized (FreshnessScanner.class) {
            if (freshnessUpdatorRef.get() == null)
                return;

            freshnessUpdatorRef.get().shutdown();
            freshnessUpdatorRef.set(null);
        }
    }

    /**
     * only called from ctrip own strategy
     * 
     * @param logicDbName
     * @param freshness
     * @return
     */
    public static List<DataBase> filterQualifiedSlaves(String logicDbName, List<DataBase> slaves,
            int qualifiedFreshness) {
        List<DataBase> qualifiedSlaves = new ArrayList<>();
        if (!freshnessCache.containsKey(logicDbName))
            return qualifiedSlaves;

        Map<String, Integer> logicDbFreshnessMap = freshnessCache.get(logicDbName);
        for (DataBase slaveDb : slaves) {
            Integer freshness = logicDbFreshnessMap.get(slaveDb.getConnectionString());
            if (freshness == null || freshness.equals(INVALID))
                continue;

            if (freshness <= qualifiedFreshness)
                qualifiedSlaves.add(slaveDb);
        }

        return qualifiedSlaves;
    }

    @Override
    public String select(SelectionContext context) throws DalException {
        // Will check if already initialized
        initialize();

        Integer freshness = context.getHints().getInt(DalHintEnum.freshness);
        List<DataBase> slaves = context.getSlaves();

        // Not specified
        if (freshness == null || slaves == null || slaves.isEmpty())
            return super.select(context);

        context.setSlaves(filterQualifiedSlaves(context.getLogicDbName(), slaves, freshness));

        return super.select(context);
    }
}
