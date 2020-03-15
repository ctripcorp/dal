package com.ctrip.platform.dal.dao.configure;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.datasource.ApiDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.ClusterDataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.helper.CustomThreadFactory;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;
import com.ctrip.platform.dal.exceptions.DalConfigException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorConfig;
import org.apache.commons.lang.StringUtils;

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
        Set<DataBase> alldbs = new HashSet<>();
        for (DatabaseSet set : this.databaseSets.values()) {
            alldbs.addAll(set.getDatabases().values());
        }
        return alldbs;
    }

    public void warmUpConnection(DataBase db){
        Connection conn = null;
        try {
            if (db instanceof ClusterDataBase) {
                DataSourceIdentity id = new ClusterDataSourceIdentity(((ClusterDataBase) db).getDatabase());
                conn = locator.getConnection(id);
            }
            else if (db instanceof ProviderDataBase) {
                DataSourceIdentity id = new ApiDataSourceIdentity(((ProviderDataBase) db).getConnectionStringProvider());
                conn = locator.getConnection(id);
            }
            else
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

    public void validate() throws Exception {
        Set<String> sqlServerSet = new HashSet<>();
        Map<String, Set<String>> connStrMap = new HashMap<>();
        for (DatabaseSet dbSet : databaseSets.values()) {
            IIdGeneratorConfig idGenConfig = dbSet.getIdGenConfig();
            if (null == idGenConfig) {
                continue;
            }
            String dbSetName = dbSet.getName();
            if (dbSet.getDatabaseCategory() == DatabaseCategory.SqlServer) {
                sqlServerSet.add(dbSetName);
                continue;
            }
            String sequenceDbName = idGenConfig.getSequenceDbName();
            if (null == sequenceDbName) {
                continue;
            }
            Map<String, DataBase> dbs = dbSet.getDatabases();
            if (dbs != null) {
                for (DataBase db : dbs.values()) {
                    String connStr = db.getConnectionString();
                    Set<String> dbSetsForConnStr = connStrMap.get(connStr);
                    if (dbSetsForConnStr != null) {
                        dbSetsForConnStr.add(sequenceDbName);
                    } else {
                        dbSetsForConnStr = new HashSet<>();
                        dbSetsForConnStr.add(sequenceDbName);
                        connStrMap.put(connStr, dbSetsForConnStr);
                    }
                }
            }
        }
        internalCheck(sqlServerSet, connStrMap);
    }

    private void internalCheck(Set<String> sqlServerSet, Map<String, Set<String>> connStrMap) throws Exception {
        StringBuilder errorInfo = new StringBuilder();
        boolean sqlServerSetMark = false;
        if (sqlServerSet.size() > 0) {
            errorInfo.append(System.lineSeparator());
            errorInfo.append("> Id generator does not support SqlServer yet. ");
            errorInfo.append("These SqlServer logic databases have been configured with id generator: ");
            errorInfo.append(StringUtils.join(sqlServerSet, ", "));
            errorInfo.append(".");
            sqlServerSetMark = true;
        }
        boolean connStrMapMark = false;
        for (String key : connStrMap.keySet()) {
            Set<String> value = connStrMap.get(key);
            if (value.size() > 1) {
                if (!connStrMapMark) {
                    errorInfo.append(System.lineSeparator());
                    errorInfo.append("> Duplicated databases found in multiple logic databases with different sequenceDbNames. ");
                    errorInfo.append("Below are the connection strings of the duplicated databases: ");
                    connStrMapMark = true;
                }
                errorInfo.append(System.lineSeparator());
                errorInfo.append("  > ");
                errorInfo.append(key);
                errorInfo.append(" (sequenceDbNames: ");
                errorInfo.append(StringUtils.join(value, ", "));
                errorInfo.append(")");
            }
        }
        if (sqlServerSetMark || connStrMapMark) {
            throw new DalConfigException(errorInfo.toString());
        }
    }

    public void warmUpIdGenerators() {
        Map<String, Future<Integer>> futures = new HashMap<>();
        for (DatabaseSet dbSet : databaseSets.values()) {
            final IIdGeneratorConfig config = dbSet.getIdGenConfig();
            if (config != null) {
                futures.put(dbSet.getName(), executor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return config.warmUp();
                    }
                }));
            }
        }
        for (Map.Entry<String, Future<Integer>> entry : futures.entrySet()) {
            try {
                entry.getValue().get();
            } catch (Throwable t) {
                String msg = String.format("warm up id generator error for databaseSet '%s'", entry.getKey());
                dalLogger.error(msg, t);
                throw new DalRuntimeException(msg, t);
            }
        }
    }

}