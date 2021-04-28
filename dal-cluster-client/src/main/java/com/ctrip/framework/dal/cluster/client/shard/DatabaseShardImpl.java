package com.ctrip.framework.dal.cluster.client.shard;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.config.DatabaseShardConfigImpl;
import com.ctrip.framework.dal.cluster.client.database.ConnectionString;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.exception.ClusterRuntimeException;
import com.ctrip.framework.dal.cluster.client.shard.read.ReadMasterStrategy;
import com.ctrip.framework.dal.cluster.client.shard.read.ReadStrategy;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLConstants.ORDERED_ACCESS_STRATEGY;

/**
 * @author c7ch23en
 */
public class DatabaseShardImpl implements DatabaseShard {

    private DatabaseShardConfigImpl databaseShardConfig;
    private List<Database> masters = new LinkedList<>();
    private List<Database> slaves = new LinkedList<>();
    private ReadStrategy readStrategy;
    private ConcurrentHashMap<HostSpec, Database> hostToDataBase = new ConcurrentHashMap<>();

    public DatabaseShardImpl(DatabaseShardConfigImpl databaseShardConfig) {
        this.databaseShardConfig = databaseShardConfig;
    }

    public void initReadStrategy() {
        String clazz = databaseShardConfig.getClusterConfig().getCustomizedOption().getReadStrategy();
        try{
            if (StringUtils.isEmpty(clazz) || clazz.equalsIgnoreCase(ORDERED_ACCESS_STRATEGY))
                readStrategy = new ReadMasterStrategy();
            else readStrategy = (ReadStrategy)Class.forName(clazz).newInstance();
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

        readStrategy.init(hostSpecs);
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
    public ReadStrategy getReadStrategy() {
        return null;
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
