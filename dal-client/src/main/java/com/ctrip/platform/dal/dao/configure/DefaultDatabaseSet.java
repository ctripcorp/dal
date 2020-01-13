package com.ctrip.platform.dal.dao.configure;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorConfig;

public class DefaultDatabaseSet extends DatabaseSet {
    private static final String CLASS = "class";
    private static final String ENTRY_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private String name;
    private String provider;
    private DatabaseCategory dbCategory;

    private DalShardingStrategy strategy;
    private Map<String, DataBase> databases;
    // Key is shard id, value is all database under in this shard
    private Map<String, List<DataBase>> masterDbByShard = new HashMap<String, List<DataBase>>();
    private Map<String, List<DataBase>> slaveDbByShard = new HashMap<String, List<DataBase>>();

    private List<DataBase> masterDbs = new ArrayList<DataBase>();
    private List<DataBase> slaveDbs = new ArrayList<DataBase>();

    private IIdGeneratorConfig idGenConfig;

    /**
     * The target DB set does not support shard
     * 
     * @param name
     * @param provider
     * @param databases
     * @throws Exception
     */
    public DefaultDatabaseSet(String name, String provider, Map<String, DataBase> databases) throws Exception {
        this(name, provider, null, databases, null);
    }

    public DefaultDatabaseSet(String name, String provider, String shardStrategy, Map<String, DataBase> databases)
            throws Exception {
        this(name, provider, shardStrategy, databases, null);
    }

    public DefaultDatabaseSet(String name, String provider, Map<String, DataBase> databases, IIdGeneratorConfig idGenConfig)
            throws Exception {
        this(name, provider, null, databases, idGenConfig);
    }

    public DefaultDatabaseSet(String name, String provider, String shardStrategy, Map<String, DataBase> databases,
            IIdGeneratorConfig idGenConfig) throws Exception {
        this.name = name;
        this.provider = provider;
        this.dbCategory = DatabaseCategory.matchWith(provider);
        this.databases = databases;
        this.idGenConfig = idGenConfig;
        initStrategy(shardStrategy);
        initShards();
    }

    private void initStrategy(String shardStrategy) throws Exception {
        if (shardStrategy == null || shardStrategy.length() == 0)
            return;

        String[] values = shardStrategy.split(ENTRY_SEPARATOR);
        String[] strategyDef = values[0].split(KEY_VALUE_SEPARATOR);

        if (strategyDef[0].trim().equals(CLASS))
            strategy = (DalShardingStrategy) Class.forName(strategyDef[1].trim()).newInstance();
        Map<String, String> settings = new HashMap<String, String>();
        for (int i = 1; i < values.length; i++) {
            String[] entry = values[i].split(KEY_VALUE_SEPARATOR);
            settings.put(entry[0].trim(), entry[1].trim());
        }
        strategy.initialize(settings);
    }

    private void initShards() throws Exception {
        if (strategy == null || strategy.isShardingByDb() == false) {
            // Init with no shard support
            for (DataBase db : databases.values()) {
                if (db.isMaster())
                    masterDbs.add(db);
                else
                    slaveDbs.add(db);
            }
        } else {
            // Init map by shard
            for (DataBase db : databases.values()) {
                Map<String, List<DataBase>> dbByShard = db.isMaster() ? masterDbByShard : slaveDbByShard;

                List<DataBase> dbList = dbByShard.get(db.getSharding());
                if (dbList == null) {
                    dbList = new ArrayList<DataBase>();
                    dbByShard.put(db.getSharding(), dbList);
                }
                dbList.add(db);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getProvider() {
        return provider;
    }

    public DatabaseCategory getDatabaseCategory() {
        return dbCategory;
    }

    public boolean isShardingSupported() {
        return strategy != null && strategy.isShardingByDb();
    }

    public boolean isTableShardingSupported(String tableName) {
        return strategy != null && strategy.isShardingByTable() && strategy.isShardingEnable(tableName);
    }

    public Map<String, DataBase> getDatabases() {
        return new HashMap<>(databases);
    }

    public void validate(String shard) throws SQLException {
        if (!masterDbByShard.containsKey(shard))
            throw new SQLException("No shard defined for id: " + shard);
    }

    public Set<String> getAllShards() {
        return new HashSet<>(masterDbByShard.keySet());
    }

    // Currently,we are only consider the ShardColModShardStrategy case.
    public Set<String> getAllTableShards(String tableName) throws SQLException {
        String errorMsg = "Can't locate all table shards: ";

        if (tableName == null || tableName.isEmpty())
            throw new DalException(errorMsg + "Table name is null or empty.");

        if (strategy == null)
            throw new DalException(errorMsg + String.format("There is no sharding strategy for DatabaseSet %s.", name));

        if (!(strategy instanceof ShardColModShardStrategy)) {
            throw new DalException(errorMsg
                    + String.format("The sharding strategy of DatabaseSet %s is not ShardColModShardStrategy.", name));
        }

        ShardColModShardStrategy modStrategy = (ShardColModShardStrategy) strategy;
        boolean isShardingEnabled = modStrategy.isShardingEnable(tableName);
        if (!isShardingEnabled) {
            throw new DalException(errorMsg + String.format("Table sharding is not enabled for table %.", tableName));
        }

        Integer tableMod = modStrategy.getTableMod();
        if (tableMod == null) {
            throw new DalException(errorMsg
                    + String.format("There is no table mod for ShardColModShardStrategy of DatabaseSet %s", name));
        }

        Set<String> set = new HashSet<>();
        for (int i = 0; i < tableMod.intValue(); i++) {
            set.add(String.valueOf(i));
        }

        return set;
    }

    public DalShardingStrategy getStrategy() throws SQLException {
        if (strategy == null)
            throw new SQLException("No sharding strategy defined");
        return strategy;
    }

    public List<DataBase> getMasterDbs() {
        return masterDbs == null ? null : new ArrayList<>(masterDbs);
    }

    public List<DataBase> getSlaveDbs() {
        return slaveDbs == null ? null : new ArrayList<>(slaveDbs);
    }

    public List<DataBase> getMasterDbs(String shard) {
        return masterDbByShard.containsKey(shard) ? new ArrayList<>(masterDbByShard.get(shard)) : null;
    }

    public List<DataBase> getSlaveDbs(String shard) {
        return slaveDbByShard.containsKey(shard) ? new ArrayList<>(slaveDbByShard.get(shard)) : null;
    }

    public IIdGeneratorConfig getIdGenConfig() {
        return idGenConfig;
    }

}
