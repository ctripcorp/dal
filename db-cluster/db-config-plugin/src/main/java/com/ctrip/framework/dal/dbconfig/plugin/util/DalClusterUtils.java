package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DalClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DatabaseInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.DatabaseShardInfo;
import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.DalConstants.*;

/**
 * Created by shenjie on 2019/4/30.
 */
public class DalClusterUtils {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public static String formatClusterName(String clusterName) {
        //to lowercase
        if (StringUtils.isNotBlank(clusterName)) {
            clusterName = clusterName.toLowerCase();
        }
        return clusterName;
    }

    public static void cleanClientConfig(DalConfigure configure) {
        Cluster cluster = configure.getCluster();
        cluster.setSslCode(null);
        cluster.setOperator(null);
        cluster.setUpdateTime(null);
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static void checkCluster(DalClusterEntity dalClusterEntity) {
        Preconditions.checkNotNull(dalClusterEntity, "DalClusterEntity为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(dalClusterEntity.getClusterName()), "ClusterName为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(dalClusterEntity.getDbCategory()), "Category为空");
        Preconditions.checkArgument(dalClusterEntity.getDatabaseShards() != null && dalClusterEntity.getDatabaseShards().size() > 0,
                "DatabaseShards为空");

        for (DatabaseShardInfo shard : dalClusterEntity.getDatabaseShards()) {
            checkShard(shard);
        }
    }

    private static void checkShard(DatabaseShardInfo shard) {
        Preconditions.checkNotNull(shard.getIndex(), "Shard index为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(shard.getMasterDomain()), "MasterDomain为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(shard.getSlaveDomain()), "SlaveDomain为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(shard.getMasterTitanKeys()), "MasterTitanKey为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(shard.getSlaveTitanKeys()), "SlaveTitanKey为空");
        Preconditions.checkArgument(shard.getDatabases() != null && shard.getDatabases().size() > 0,
                "Databases为空");
        for (DatabaseInfo databaseInfo : shard.getDatabases()) {
            checkDatabase(databaseInfo);
        }
    }

    private static void checkDatabase(DatabaseInfo database) {
        Preconditions.checkArgument(StringUtils.isNotBlank(database.getRole()), "Database role为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(database.getIp()), "Database ip为空");
        Preconditions.checkNotNull(database.getPort(), "Database port为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(database.getDbName()), "Database dbName为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(database.getUid()), "Database uid为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(database.getPassword()), "Database password为空");
        Preconditions.checkNotNull(database.getReadWeight(), "Database readWeight为空");
    }

    public static DalConfigure formatCluster2Configure(DalClusterEntity dalCluster) {
        List<DatabaseShard> shards = Lists.newArrayListWithCapacity(dalCluster.getDatabaseShards().size());
        for (DatabaseShardInfo shardInfo : dalCluster.getDatabaseShards()) {
            DatabaseShard shard = new DatabaseShard(shardInfo.getIndex(), shardInfo.getMasterDomain(), shardInfo.getSlaveDomain(),
                    shardInfo.getMasterPort(), shardInfo.getSlavePort(),
                    shardInfo.getMasterTitanKeys(), shardInfo.getSlaveTitanKeys());
            List<Database> databases = Lists.newArrayListWithCapacity(shardInfo.getDatabases().size());
            for (DatabaseInfo info : shardInfo.getDatabases()) {
                Database database = new Database(info.getRole(), info.getIp(), info.getPort(), info.getDbName(), info.getUid(),
                        info.getPassword(), info.getReadWeight(), info.getTags());
                databases.add(database);
            }
            shard.setDatabases(databases);
            shards.add(shard);
        }

        DatabaseShards databaseShards = new DatabaseShards(shards);
        Cluster cluster = new Cluster(dalCluster.getClusterName(), dalCluster.getDbCategory(), dalCluster.getVersion(), databaseShards);
        cluster.setShardStrategies(dalCluster.getShardStrategies());
        cluster.setIdGenerators(dalCluster.getIdGenerators());
        cluster.setSslCode(dalCluster.getSslCode());
        cluster.setOperator(dalCluster.getOperator());
        cluster.setUpdateTime(DalClusterUtils.formatDate(new Date()));

        DalConfigure configure = new DalConfigure(cluster);
        return configure;
    }

    public static DalClusterEntity formatConfigure2Cluster(DalConfigure configure) {
        Cluster cluster = configure.getCluster();
        DatabaseShards configDatabaseShards = cluster.getShards();
        List<DatabaseShardInfo> targetShards = Lists.newArrayList();
        for (DatabaseShard sourceShard : configDatabaseShards.getDatabaseShards()) {
            List<DatabaseInfo> targetDatabases = Lists.newArrayListWithCapacity(sourceShard.getDatabases().size());
            for (Database sourceDatabase : sourceShard.getDatabases()) {
                DatabaseInfo targetDatabase = new DatabaseInfo();
                targetDatabase.setRole(sourceDatabase.getRole());
                targetDatabase.setIp(sourceDatabase.getIp());
                targetDatabase.setPort(sourceDatabase.getPort());
                targetDatabase.setDbName(sourceDatabase.getDbName());
                targetDatabase.setUid(sourceDatabase.getUid());
                targetDatabase.setPassword(sourceDatabase.getPassword());
                targetDatabase.setReadWeight(sourceDatabase.getReadWeight());
                targetDatabase.setTags(sourceDatabase.getTags());
                targetDatabases.add(targetDatabase);
            }
            DatabaseShardInfo targetShard = new DatabaseShardInfo();
            targetShard.setIndex(sourceShard.getIndex());
            targetShard.setMasterDomain(sourceShard.getMasterDomain());
            targetShard.setSlaveDomain(sourceShard.getSlaveDomain());
            targetShard.setMasterPort(sourceShard.getMasterPort());
            targetShard.setSlavePort(sourceShard.getSlavePort());
            targetShard.setMasterTitanKeys(sourceShard.getMasterTitanKeys());
            targetShard.setSlaveTitanKeys(sourceShard.getSlaveTitanKeys());
            targetShard.setDatabases(targetDatabases);

            targetShards.add(targetShard);
        }

        DalClusterEntity dalCluster = new DalClusterEntity();
        dalCluster.setClusterName(cluster.getName());
        dalCluster.setDbCategory(cluster.getCategory());
        dalCluster.setVersion(cluster.getVersion());
        dalCluster.setSslCode(cluster.getSslCode());
        dalCluster.setOperator(cluster.getOperator());
        dalCluster.setDatabaseShards(targetShards);
        dalCluster.setShardStrategies(cluster.getShardStrategies());
        dalCluster.setIdGenerators(cluster.getIdGenerators());
        return dalCluster;
    }

    public static Properties buildDecryptProperties(String uid, String password, String sslCode) {
        Properties decryptProperties = new Properties();
        decryptProperties.setProperty(UID, uid);
        decryptProperties.setProperty(PASSWORD, password);
        decryptProperties.setProperty(SSL_CODE, sslCode);
        return decryptProperties;
    }

    public static Properties buildEncryptProperties(String uid, String password) {
        Properties encryptProperties = new Properties();
        encryptProperties.setProperty(UID, uid);
        encryptProperties.setProperty(PASSWORD, password);
        return encryptProperties;
    }
}
