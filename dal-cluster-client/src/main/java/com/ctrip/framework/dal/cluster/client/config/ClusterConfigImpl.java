package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.UnsupportedListenable;
import com.ctrip.framework.dal.cluster.client.cluster.*;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;
import com.ctrip.framework.dal.cluster.client.multihost.ClusterRouteStrategyConfig;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;
import com.ctrip.framework.dal.cluster.client.sharding.strategy.ShardStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class ClusterConfigImpl extends UnsupportedListenable<ClusterConfig> implements ClusterConfig {

    private String clusterName;
    private ClusterType clusterType;
    private DatabaseCategory databaseCategory;
    private long version;
    private List<DatabaseShardConfig> databaseShardConfigs = new LinkedList<>();
    private ShardStrategy defaultShardStrategy;
    private List<ShardStrategy> shardStrategies = new LinkedList<>();
    private ClusterIdGeneratorConfig idGeneratorConfig;
    private ClusterRouteStrategyConfig routeStrategyConfig;
    private Integer unitStrategyId;
    private String zoneId;
    private DrcConsistencyTypeEnum drcConsistencyType;

    private final AtomicReference<Cluster> generatedClusterRef = new AtomicReference<>();

    public ClusterConfigImpl(String clusterName, DatabaseCategory databaseCategory, long version) {
        this(clusterName, ClusterType.NORMAL, databaseCategory, version);
    }

    public ClusterConfigImpl(String clusterName, ClusterType clusterType, DatabaseCategory databaseCategory, long version) {
        this.clusterName = clusterName;
        this.clusterType = clusterType;
        this.databaseCategory = databaseCategory;
        this.version = version;
    }

    @Override
    public Cluster generate() {
        Cluster cluster = generatedClusterRef.get();
        if (cluster == null) {
            synchronized (generatedClusterRef) {
                cluster = generatedClusterRef.get();
                if (cluster == null) {
                    cluster = innerGenerate();
                    generatedClusterRef.set(cluster);
                }
            }
        }
        return cluster;
    }

    public Cluster getOrCreateCluster() {
        return generate();
    }

    private Cluster innerGenerate() {
        DefaultCluster cluster = (clusterType != ClusterType.DRC && unitStrategyId == null ?
                new DefaultCluster(this) : new DefaultDrcCluster(this));
        for (DatabaseShardConfig databaseShardConfig : databaseShardConfigs)
            cluster.addDatabaseShard(databaseShardConfig.generate());
        ShardStrategyProxy shardStrategy = new ShardStrategyProxy(defaultShardStrategy);
        for (ShardStrategy strategy : shardStrategies)
            shardStrategy.addStrategy(strategy);
        cluster.setShardStrategy(shardStrategy);
        cluster.setIdGeneratorConfig(idGeneratorConfig);
        cluster.setRouteStrategyConfig(routeStrategyConfig);
        LocalizationState localizationState = LocalizationState.NONE;
        if (clusterType == ClusterType.DRC)
            localizationState = LocalizationState.ACTIVE;
        else if (unitStrategyId != null)
            localizationState = LocalizationState.PREPARED;
        cluster.setLocalizationConfig(new LocalizationConfigImpl(unitStrategyId, zoneId, localizationState, drcConsistencyType));
        cluster.validate();
        return cluster;
    }

    @Override
    public boolean checkSwitchable(ClusterConfig newConfig) {
        if (newConfig instanceof ClusterConfigImpl) {
            ClusterConfigImpl ref = (ClusterConfigImpl) newConfig;
            return ref.getVersion() > version;
        }
        return false;
    }

    @Override
    public String getClusterName() {
        return clusterName;
    }

    public ClusterType getClusterType() {
        return clusterType;
    }

    public DatabaseCategory getDatabaseCategory() {
        return databaseCategory;
    }

    public long getVersion() {
        return version;
    }

    public void addDatabaseShardConfig(DatabaseShardConfig databaseShardConfig) {
        databaseShardConfigs.add(databaseShardConfig);
    }

    public void setDefaultStrategy(ShardStrategy shardStrategy) {
        if (shardStrategy == null)
            return;
        if (defaultShardStrategy != null)
            throw new ClusterConfigException("default shard strategy already defined");
        defaultShardStrategy = shardStrategy;
        addShardStrategy(shardStrategy);
    }

    public void addShardStrategy(ShardStrategy shardStrategy) {
        if (shardStrategy != null)
            shardStrategies.add(shardStrategy);
    }

    public void setIdGeneratorConfig(ClusterIdGeneratorConfig idGeneratorConfig) {
        this.idGeneratorConfig = idGeneratorConfig;
    }

    public void setRouteStrategyConfig(ClusterRouteStrategyConfig routeStrategyConfig) {
        this.routeStrategyConfig = routeStrategyConfig;
    }

    public void setUnitStrategyId(Integer unitStrategyId) {
        this.unitStrategyId = unitStrategyId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public void setDrcConsistencyType(DrcConsistencyTypeEnum type) {
        this.drcConsistencyType = type;
    }

    @Override
    public String toString() {
        return "clusterName=" + clusterName +
                ", version=" + version +
                ", clusterType=" + clusterType.getValue() +
                ", databaseCategory=" + databaseCategory.getValue() +
                ", databaseShardCount=" + databaseShardConfigs.size() +
                (unitStrategyId == null ? "" : ", unitStrategyId=" + unitStrategyId) +
                (zoneId == null ? "" : ", zoneId=" + zoneId);
    }

}
