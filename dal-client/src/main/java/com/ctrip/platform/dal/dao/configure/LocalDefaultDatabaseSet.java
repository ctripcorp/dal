package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.dao.strategy.LocalShardStrategyAdapter;
import com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;

/**
 * @author c7ch23en
 */
public class LocalDefaultDatabaseSet extends DefaultDatabaseSet {

    private final LocalShardStrategyAdapter shardStrategy;

    public LocalDefaultDatabaseSet(DefaultDatabaseSet databaseSet,
                                   boolean dbShardingDisabled, boolean tableShardingDisabled) {
        super(databaseSet.getName(), databaseSet.getProvider(), databaseSet.getStrategyNullable(),
                databaseSet.getDatabases(), databaseSet.getIdGenConfig(), databaseSet.getProperties());
        DalShardingStrategy strategy = databaseSet.getStrategyNullable();
        if (strategy != null)
            shardStrategy = new LocalShardStrategyAdapter(strategy, dbShardingDisabled, tableShardingDisabled);
        else
            shardStrategy = null;
        initShards();
    }

    @Override
    protected DalShardingStrategy getStrategyNullable() {
        return shardStrategy;
    }

    @Override
    protected ShardColModShardStrategy tryGetModStrategy() {
        if (shardStrategy != null && shardStrategy.getInnerStrategy() instanceof ShardColModShardStrategy)
            return (ShardColModShardStrategy) shardStrategy.getInnerStrategy();
        else
            return null;
    }

}
