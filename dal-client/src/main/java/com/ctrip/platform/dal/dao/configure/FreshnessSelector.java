package com.ctrip.platform.dal.dao.configure;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper;
import com.ctrip.platform.dal.exceptions.DalException;

public class FreshnessSelector implements DatabaseSelector, DalComponent {
    public static final String FRESHNESS_READER = "freshnessReader";
    public static final String UPDATE_INTERVAL = "updateInterval"; 
    public static final int DEFAULT_INTERVAL = 5; 
    
    private static final Map<String, Map<String, Integer>> freshnessCache = new ConcurrentHashMap<>();
    private static AtomicReference<ScheduledExecutorService> freshnessUpdatorRef = new AtomicReference<>();
    
    private static final int INVALID = FreshnessReader.INVALID;
    
    private DefaultDatabaseSelector defaultSelector = new DefaultDatabaseSelector();
    private FreshnessReader reader;
    private int interval;
    
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                shutdown();
            }
        }));
    }

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        if(settings.containsKey(FRESHNESS_READER)) {
            String readerClass = settings.get(FRESHNESS_READER).trim();
            reader = (FreshnessReader)Class.forName(readerClass).newInstance();
        }
        
        interval = settings.containsKey(UPDATE_INTERVAL) ? Integer.parseInt(settings.get(UPDATE_INTERVAL)) : DEFAULT_INTERVAL;
    }

    /**
     * Need to be called during getQualifiedSlaveNames
     * 
     * @param configure
     */
    private void initialize() {
        if(freshnessUpdatorRef.get() != null)
            return;
        
        synchronized (FreshnessReader.class) {
            if(freshnessUpdatorRef.get() != null)
                return;
            
            freshnessCache.clear();
            DalConfigure configure = DalClientFactory.getDalConfigure();
            for(String logicDbName: configure.getDatabaseSetNames()) {
                Map<String, Integer> logicDbFreshnessMap = new ConcurrentHashMap<>();
                freshnessCache.put(logicDbName, logicDbFreshnessMap);
                
                DatabaseSet dbSet = configure.getDatabaseSet(logicDbName);
                for(Map.Entry<String, DataBase> dbEntry: dbSet.getDatabases().entrySet()) {
                    if(!dbEntry.getValue().isMaster())
                        logicDbFreshnessMap.put(dbEntry.getValue().getConnectionString(), INVALID);
                }
            }
            
            if(reader == null)
                return;
            
            //Init task
            ScheduledExecutorService executer = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                AtomicInteger atomic = new AtomicInteger();
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "Dal-FreshnessScanner" + this.atomic.getAndIncrement());
                }
            });
            executer.scheduleWithFixedDelay(new FreshnessScanner(reader), 0, interval, TimeUnit.SECONDS);
            freshnessUpdatorRef.set(executer);
        }
    }
    
    private static void shutdown() {
        if (freshnessUpdatorRef.get() == null)
            return;
        
        synchronized (FreshnessReader.class) {
            if (freshnessUpdatorRef.get() == null)
                return;
            
            freshnessUpdatorRef.get().shutdown();
            freshnessUpdatorRef.set(null);
        }
    }
    
    /**
     * Freshness can also be update from outside
     * @param logicDbName
     * @param slaveConnectionString
     * @param freshness
     */
    public static void update(String logicDbName, String slaveConnectionString, int freshness) {
        Map<String, Integer> logicDbFreshnessMap = freshnessCache.get(logicDbName);
        logicDbFreshnessMap.put(slaveConnectionString, freshness);
    }
    
    /**
     * Get freshness for given logic db and slave
     * @param logicDbName
     * @param slaveDbName
     * @return
     */
    public static int getFreshness(String logicDbName, String connectionString) {
        return freshnessCache.get(logicDbName).get(connectionString);
    }
    
    /**
     * A handy way of getting qualified slaves
     * 
     * @param logicDbName
     * @param freshness
     * @return
     */
    private List<DataBase> filterQualifiedSlaves(String logicDbName, List<DataBase> slaves, int qualifiedFreshness) {
        List<DataBase> qualifiedSlaves = new ArrayList<>();
        if(!freshnessCache.containsKey(logicDbName))
            return qualifiedSlaves;
        
        Map<String, Integer> logicDbFreshnessMap = freshnessCache.get(logicDbName);
        for(DataBase slaveDb: slaves) {
            Integer freshness = logicDbFreshnessMap.get(slaveDb.getConnectionString());
            if(freshness == null || freshness.equals(INVALID))
                continue;
            
            if(freshness <= qualifiedFreshness)
                qualifiedSlaves.add(slaveDb);
        }
        
        return qualifiedSlaves;
    }

    @Override
    public String select(SelectionContext context) throws DalException {
        //Will check if already initialized
        initialize();
        
        Integer freshness = context.getHints().getInt(DalHintEnum.freshness);
        List<DataBase> slaves = context.getSlaves();
        
        // Not specified 
        if(freshness == null || slaves == null || slaves.isEmpty())
            return defaultSelector.select(context);

        context.setSlaves(filterQualifiedSlaves(context.getLogicDbName(), slaves, freshness));

        return defaultSelector.select(context);
    }
    
    private static class FreshnessScanner implements Runnable {
        private FreshnessReader reader;
        
        public FreshnessScanner(FreshnessReader reader) {
            this.reader = reader;
        }
        
        @Override
        public void run() {
            for(String logicDbName: freshnessCache.keySet()) {
                for(String slaveConnectionString: freshnessCache.get(logicDbName).keySet()) {
                    int freshness = INVALID;
                    try {
                        freshness = reader.getSlaveFreshness(logicDbName, slaveConnectionString);
                    } catch (Throwable e) {
                        DalClientFactory.getDalConfigure().getDalLogger().error(String.format("Can not get freshness from slave %s for logic db %s. Error message: %s", logicDbName, slaveConnectionString, e.getMessage()), e);
                    }
                    freshness = freshness > 0 ? freshness : INVALID;
                    update(logicDbName, slaveConnectionString, freshness);
                }
            }
        }
    }
}
