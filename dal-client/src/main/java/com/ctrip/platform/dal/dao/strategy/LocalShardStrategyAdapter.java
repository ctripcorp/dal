package com.ctrip.platform.dal.dao.strategy;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;

import java.util.Map;

/**
 * @author c7ch23en
 */
public class LocalShardStrategyAdapter implements DalShardingStrategy {

    private final DalShardingStrategy strategy;
    private final boolean dbShardingDisabled;
    private final boolean tableShardingDisabled;

    public LocalShardStrategyAdapter(DalShardingStrategy strategy,
                                     boolean dbShardingDisabled, boolean tableShardingDisabled) {
        this.strategy = strategy;
        this.dbShardingDisabled = dbShardingDisabled;
        this.tableShardingDisabled = tableShardingDisabled;
    }

    @Override
    public void initialize(Map<String, String> settings) {
        strategy.initialize(settings);
    }

    @Override
    public boolean isMaster(DalConfigure configure, String logicDbName, DalHints hints) {
        return strategy.isMaster(configure, logicDbName, hints);
    }

    @Override
    public boolean isShardingByDb() {
        return !dbShardingDisabled && strategy.isShardingByDb();
    }

    @Override
    public String locateDbShard(DalConfigure configure, String logicDbName, DalHints hints) {
        if (dbShardingDisabled)
            throw new RuntimeException(
                    String.format("Db sharding disabled for database '%s' in local mode", logicDbName));
        return strategy.locateDbShard(configure, logicDbName, hints);
    }

    @Override
    public boolean isShardingByTable() {
        return !tableShardingDisabled && strategy.isShardingByTable();
    }

    @Override
    public boolean isShardingEnable(String tableName) {
        return !tableShardingDisabled && strategy.isShardingEnable(tableName);
    }

    @Override
    public String locateTableShard(DalConfigure configure, String logicDbName, String tabelName, DalHints hints) {
        if (tableShardingDisabled)
            throw new RuntimeException(
                    String.format("Table sharding disabled for database '%s' in local mode", logicDbName));
        return strategy.locateTableShard(configure, logicDbName, tabelName, hints);
    }

    @Override
    public String getTableShardSeparator() {
        return strategy.getTableShardSeparator();
    }

    public DalShardingStrategy getInnerStrategy() {
        return strategy;
    }

}
