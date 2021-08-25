package com.ctrip.framework.dal.cluster.client.shard;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.ReadStrategyEnum;
import com.ctrip.framework.dal.cluster.client.config.DatabaseShardConfigImpl;
import com.ctrip.framework.dal.cluster.client.database.ConnectionString;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;
<<<<<<< HEAD
import com.ctrip.framework.dal.cluster.client.shard.read.RouteStrategy;
=======
import com.ctrip.framework.dal.cluster.client.exception.DalMetadataException;
>>>>>>> ctrip-master

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author c7ch23en
 */
public class DatabaseShardImpl implements DatabaseShard {

    private DatabaseShardConfigImpl databaseShardConfig;
    private List<Database> masters = new LinkedList<>();
    private List<Database> slaves = new LinkedList<>();
    private RouteStrategy routeStrategy;
    private ConcurrentHashMap<HostSpec, Database> hostToDataBase = new ConcurrentHashMap<>();

    public DatabaseShardImpl(DatabaseShardConfigImpl databaseShardConfig) {
        this.databaseShardConfig = databaseShardConfig;
    }

    public void initReadStrategy() {
        String clazz = ReadStrategyEnum.parse(databaseShardConfig.getClusterConfig().getRouteStrategyConfig().routeStrategyName());
        try{
            routeStrategy = (RouteStrategy)Class.forName(clazz).newInstance();
        } catch (Throwable t) {
            throw new ClusterRuntimeException(t);
        }

        List<Database> databases = new ArrayList<>();
        databases.addAll(masters);
        databases.addAll(slaves);
        Set<HostSpec> hostSpecs = new HashSet<>();
        databases.forEach(database -> {
            ConnectionString connString = database.getConnectionString();
            HostSpec host = HostSpec.of(connString.getPrimaryHost(), connString.getPrimaryPort(), database.getZone(), database.isMaster());
            hostToDataBase.putIfAbsent(host, database);
            hostSpecs.add(host);
        });

        try{
            routeStrategy.init(hostSpecs);
        } catch (DalMetadataException error) {
            throw new DalMetadataException(databaseShardConfig.getClusterConfig().getClusterName() + error.getMessage());
        }
    }

    @Override
    public int getShardIndex() {
        return databaseShardConfig.getShardIndex();
    }

    @Override
    public List<Database> getMasters() {
        return new LinkedList<>(masters);
    }

    @Override
    public List<Database> getSlaves() {
        return new LinkedList<>(slaves);
    }

    @Override
    public RouteStrategy getRouteStrategy() {
        return routeStrategy;
    }

    @Override
    public Database parseFromHostSpec(HostSpec hostSpec) {
        return hostToDataBase.get(hostSpec);
    }

    public void addDatabase(Database database) {
        if (database.isMaster())
            masters.add(database);
        else
            slaves.add(database);
    }

}
