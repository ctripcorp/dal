package com.ctrip.platform.dal.dao.configure;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;

public class DalConfigure {
    private String name;
    private Map<String, DatabaseSet> databaseSets = new ConcurrentHashMap<String, DatabaseSet>();
    private DalLogger dalLogger;
    private DalConnectionLocator locator;
    private DalTaskFactory factory;
    private DatabaseSelector selector;
    private static ThreadPoolExecutor executor;
    private static final int CORE_POOL_SIZE = 100;
    private static final int MAX_POOL_SIZE = 100;
    private static final long KEEP_ALIVE_TIME = 1L;

    public DalConfigure(String name, Map<String, DatabaseSet> databaseSets, DalLogger dalLogger,
            DalConnectionLocator locator, DalTaskFactory factory, DatabaseSelector selector) {
        this.name = name;
        this.databaseSets.putAll(databaseSets);
        this.dalLogger = dalLogger;
        this.locator = locator;
        this.factory = factory;
        this.selector = selector;
        initExecutorService();
    }

    private void initExecutorService() {
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                                          new LinkedBlockingQueue<Runnable>(),
                                          new CustomThreadFactory("WarmUpConnections"));
        executor.allowCoreThreadTimeOut(true);
    }

    public String getName() {
        return name;
    }

    public DatabaseSet getDatabaseSet(String logicDbName) {
        if (!databaseSets.containsKey(logicDbName))
            throw new IllegalArgumentException("Can not find definition for Database Set " + logicDbName
                    + ". Please check spelling or define it in Dal.config");

        return databaseSets.get(logicDbName);
    }

    public void warmUpConnections() {
        try {
            Set<DataBase> alldbs = getAllDBs();
            final CountDownLatch latch = new CountDownLatch(alldbs.size());
            for (final DataBase db : alldbs) {
                executor.submit(new Runnable() {
                    public void run() {
                        try {
                            warmUpConnection(db);
                        } catch (Exception e) {
                            dalLogger.error(String.format("warm up connection error"), e);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }
            latch.await(5L, TimeUnit.SECONDS);
        } catch (Exception e) {
            dalLogger.error(String.format("warm up connections error"), e);
        }
    }

    public Set<DataBase> getAllDBs(){
        Set<DataBase> alldbs = new HashSet<DataBase>();
        for (DatabaseSet set : this.databaseSets.values()) {
            for (DataBase db : set.getDatabases().values()) {
                alldbs.add(db);
            }
        }
        return alldbs;
    }

    public void warmUpConnection(DataBase db){
        Connection conn = null;
        try {
            conn = locator.getConnection(db.getConnectionString());
        } catch (Throwable e) {
            dalLogger.error(String.format("create connection to %s error", db.getName()), e);
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (Throwable e) {
                    dalLogger.error(String.format("close connection from %s error", db.getName()), e);
                }
        }
    }

    public Set<String> getDatabaseSetNames() {
        return databaseSets.keySet();
    }

    public Set<String> getDataSourceNames() {
        Set<String> alldbs = new HashSet<String>();
        for (DatabaseSet set : this.databaseSets.values()) {
            for (DataBase db : set.getDatabases().values()) {
                alldbs.add(db.getConnectionString());
            }
        }
        return alldbs;
    }

    public DalLogger getDalLogger() {
        return dalLogger;
    }

    public DalConnectionLocator getLocator() {
        return locator;
    }

    public DalTaskFactory getFactory() {
        return factory;
    }

    public DatabaseSelector getSelector() {
        return selector;
    }
}