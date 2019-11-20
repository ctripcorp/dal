package com.ctrip.framework.db.cluster.service.builder;

import com.ctrip.framework.db.cluster.domain.plugin.dal.release.ReleaseCluster;
import com.ctrip.framework.db.cluster.domain.plugin.dal.release.ReleaseDatabase;
import com.ctrip.framework.db.cluster.domain.plugin.dal.release.ReleaseShard;
import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.vo.dal.create.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by shenjie on 2019/4/30.
 */
@Slf4j
@Component
public class DalClusterConfigBuilder {

    @Autowired
    private ClusterService clusterService;

    public List<ReleaseCluster> build(ClusterVo cluster) throws SQLException {
        // build all userTag's shards
        Map<String, List<ReleaseShard>> userTagShards = buildUserTagShards(cluster);

        // build all userTag clusters
        List<ReleaseCluster> clusters = buildClusters(cluster.getClusterName(), userTagShards);
        return clusters;
    }

    private List<ReleaseCluster> buildClusters(String clusterName, Map<String, List<ReleaseShard>> userTagShards) throws SQLException {
        List<ReleaseCluster> clusters = Lists.newArrayList();
        for (Map.Entry<String, List<ReleaseShard>> oneUserTagShards : userTagShards.entrySet()) {
            String userTag = oneUserTagShards.getKey();
            List<ReleaseShard> databaseShards = oneUserTagShards.getValue();

            // build cluster
            ReleaseCluster releaseCluster = buildCluster(clusterName, userTag, databaseShards);
            clusters.add(releaseCluster);
        }
        return clusters;
    }

    private ReleaseCluster buildCluster(String clusterName, String userTag, List<ReleaseShard> databaseShards) throws SQLException {
        Cluster cluster = clusterService.findCluster(
                clusterName, Deleted.un_deleted, null
        );

        // ignore default userTag fileName suffix
        String configFileName = Constants.USER_TAG_DEFAULT.equalsIgnoreCase(userTag) ?
                clusterName : (clusterName + Constants.POINT_SEPARATOR + userTag);

        // increase version
        Integer newVersion = cluster.getReleaseVersion() + 1;
        return ReleaseCluster.builder()
                .clusterName(configFileName)
                .dbCategory(cluster.getDbCategory())
                .version(newVersion)
                .databaseShards(databaseShards)
                .build();
    }

    private Map<String, List<ReleaseShard>> buildUserTagShards(ClusterVo cluster) throws SQLException {
        List<ShardVo> shards = cluster.deprGetShards();
        Map<String, List<ReleaseShard>> userTagShardsGroup = Maps.newHashMap();
        for (ShardVo shard : shards) {
            Set<String> userTags = getUserTags(shard.deprGetUsers());
            Table<String, String, UserVo> userGroup = users2Table(shard.deprGetUsers());
            for (String userTag : userTags) {
                ReleaseShard userTagShard = getUserTagShard(userGroup, userTag, shard);
                List<ReleaseShard> userTagShards = userTagShardsGroup.get(userTag);
                if (userTagShards == null) {
                    userTagShards = Lists.newArrayList();
                    userTagShardsGroup.put(userTag, userTagShards);
                }
                userTagShards.add(userTagShard);
            }
        }

        return userTagShardsGroup;
    }

    private ReleaseShard getUserTagShard(Table<String, String, UserVo> userGroup, String userTag, ShardVo shard) {
        List<ReleaseDatabase> databases = Lists.newArrayList();
        ReleaseDatabase masterDatabase = getMasterInstance(userTag, userGroup, shard);
        UserVo masterUser = getUser(userTag, Constants.OPERATION_WRITE, userGroup);
        List<TitanKeyVo> masterTitanKeys = getTitanKey(shard.deprGetTitanKeys(), masterUser.getUsername());
        databases.add(masterDatabase);

        List<ReleaseDatabase> slaveDatabases = getSlaveInstances(userTag, userGroup, shard);
        databases.addAll(slaveDatabases);

        String slaveDomain = null;
        Integer slavePort = null;
        List<TitanKeyVo> slaveTitanKeys = Lists.newArrayList();
        UserVo slaveUser = getUser(userTag, Constants.OPERATION_READ, userGroup);
        if (Constants.USER_TAG_ETL.equalsIgnoreCase(userTag)) {
            slaveDomain = shard.getSlave().getDomain();
            slavePort = shard.getSlave().getPort();
        } else if (shard.getRead() != null) {
            slaveDomain = shard.getRead().getDomain();
            slavePort = shard.getRead().getPort();
        }

        if (slaveUser != null) {
            slaveTitanKeys = getTitanKey(shard.deprGetTitanKeys(), slaveUser.getUsername());
        }

        return ReleaseShard.builder()
                .index(shard.deprGetIndex())
                .masterDomain(shard.getMaster().getDomain())
                .masterPort(shard.getMaster().getPort())
                .slaveDomain(slaveDomain)
                .slavePort(slavePort)
                .masterTitanKeys(titanKey2String(masterTitanKeys))
                .slaveTitanKeys(titanKey2String(slaveTitanKeys))
                .databases(databases)
                .build();
    }

    private ReleaseDatabase getMasterInstance(String userTag, Table<String, String, UserVo> userGroup, ShardVo shard) {
        UserVo masterUser = getUser(userTag, Constants.OPERATION_WRITE, userGroup);
        Preconditions.checkNotNull(masterUser, "master user不存在");

        DatabaseVo master = shard.getMaster();
        InstanceVo masterInstance = master.getInstances().get(0);
        return buildDatabase(shard.getDbName(), Constants.ROLE_MASTER, masterUser, masterInstance);
    }

    private List<ReleaseDatabase> getSlaveInstances(String userTag, Table<String, String, UserVo> userGroup, ShardVo shard) {
        List<ReleaseDatabase> slaveDatabases = Lists.newArrayList();

        UserVo user = getUser(userTag, Constants.OPERATION_READ, userGroup);
        if (Constants.USER_TAG_ETL.equalsIgnoreCase(userTag)) {
            DatabaseVo slave = shard.getSlave();
            Preconditions.checkNotNull(slave, "etl tag不存在candidate_master");
            Preconditions.checkNotNull(user, "slave user不存在");

            InstanceVo slaveInstance = slave.getInstances().get(0);
            ReleaseDatabase slaveDatabase = buildDatabase(shard.getDbName(), Constants.ROLE_SLAVE, user, slaveInstance);
            slaveDatabases.add(slaveDatabase);
        } else {
            DatabaseVo read = shard.getRead();
            if (read != null && read.getInstances() != null && !read.getInstances().isEmpty()) {
                Preconditions.checkNotNull(user, "slave user不存在");
                for (InstanceVo instance : read.getInstances()) {
                    ReleaseDatabase database = buildDatabase(shard.getDbName(), Constants.ROLE_SLAVE, user, instance);
                    slaveDatabases.add(database);
                }
            }
        }

        return slaveDatabases;
    }

    private ReleaseDatabase buildDatabase(String dbName, String role, UserVo user, InstanceVo instance) {
        ReleaseDatabase database = ReleaseDatabase.builder()
                .role(role)
                .ip(instance.getIp())
                .port(instance.getPort())
                .dbName(dbName)
                .uid(user.getUsername())
                .password(user.getPassword())
                .readWeight(instance.getReadWeight())
                .tags(instance.getTags())
                .build();

        return database;
    }

    private Set<String> getUserTags(List<UserVo> users) {
        Set<String> userTags = Sets.newHashSet();
        for (UserVo userVo : users) {
            userTags.add(userVo.getTag().toLowerCase());
        }
        return userTags;
    }

    private UserVo getUser(String userTag, String operateType, Table<String, String, UserVo> userGroup) {
        UserVo userVo = userGroup.get(userTag, operateType);
        if (userVo == null) {
            userVo = userGroup.get(Constants.USER_TAG_DEFAULT, operateType);
        }
        return userVo;
    }

    private Table<String, String, UserVo> users2Table(List<UserVo> users) {
        Table<String, String, UserVo> userGroup = HashBasedTable.create();
        for (UserVo user : users) {
            userGroup.put(user.getTag(), user.getPermission(), user);
        }
        return userGroup;
    }

    private List<TitanKeyVo> getTitanKey(List<TitanKeyVo> titanKeys, String uid) {
        List<TitanKeyVo> uidTitanKeys = Lists.newArrayList();
        for (TitanKeyVo titanKey : titanKeys) {
            if (uid.equalsIgnoreCase(titanKey.getUid())) {
                uidTitanKeys.add(titanKey);
            }
        }
        return uidTitanKeys;
    }

    private String titanKey2String(List<TitanKeyVo> titanKeys) {
        if (titanKeys == null || titanKeys.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (TitanKeyVo titanKey : titanKeys) {
            sb.append(titanKey.getKeyName());
            sb.append(",");
        }
        String result = sb.toString();
        result = result.substring(0, result.length() - 1);
        return result;
    }

}
