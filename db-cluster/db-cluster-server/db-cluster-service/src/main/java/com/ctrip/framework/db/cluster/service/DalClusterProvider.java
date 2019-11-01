package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.entity.*;
import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.enums.Enabled;
import com.ctrip.framework.db.cluster.service.repository.*;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.dal.create.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/7/31.
 */
@Slf4j
@Component
public class DalClusterProvider {

    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ShardService shardService;
    @Autowired
    private ShardInstanceService shardInstanceService;
    @Autowired
    private InstanceService instanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private TitanKeyService titanKeyService;
    @Autowired
    private CipherService cipherService;

    protected ClusterVo getCluster(String clusterName) throws SQLException {
        Cluster clusterInDB = clusterService.findCluster(
                clusterName, Lists.newArrayList(Deleted.un_deleted),
                Lists.newArrayList(Enabled.enabled, Enabled.un_enabled)
        );
        Preconditions.checkNotNull(clusterInDB, "cluster[" + clusterName + "]不存在");

        ClusterVo cluster = ClusterVo.builder()
                .clusterName(clusterName)
                .dbCategory(clusterInDB.getDbCategory())
                .build();

        List<Shard> shardsInDB = shardService.findShardsByClusterId(clusterInDB.getId());
        if (shardsInDB == null || shardsInDB.isEmpty()) {
            return cluster;
        }

        List<ShardVo> shards = Lists.newArrayList();
        for (Shard shardInDB : shardsInDB) {
            ShardVo shard = getShard(shardInDB);
            shards.add(shard);
        }

//        cluster.setShards(shards);
        return cluster;
    }

    private ShardVo getShard(Shard shardInDB) throws SQLException {
        Integer shardId = shardInDB.getId();

        DatabaseVo master = getDatabase(Constants.ROLE_MASTER, shardId, shardInDB.getMasterDomain());
        DatabaseVo read = getDatabase(Constants.ROLE_SLAVE, shardId, shardInDB.getSlaveDomain());

        Pair<List<UserVo>, Map<Integer, String>> usersAndIds = getUsersAndIds(shardId);
        List<UserVo> users = usersAndIds.getKey();
        List<TitanKeyVo> titanKeys = getTitanKey(usersAndIds.getValue());

        ShardVo shard = ShardVo.builder()
//                .index(shardInDB.getShardIndex())
                .dbName(shardInDB.getDbName())
                .master(master)
                .read(read)
//                .users(users)
//                .titanKeys(titanKeys)
                .build();

        return shard;
    }

    private Pair<List<UserVo>, Map<Integer, String>> getUsersAndIds(Integer shardId) throws SQLException {
        List<ShardUser> shardUsers = userService.findUsersByShardId(shardId);
        List<UserVo> users = Lists.newArrayList();
        Map<Integer, String> userIds = Maps.newHashMap();
        if (shardUsers != null && !shardUsers.isEmpty()) {
            for (ShardUser shardUser : shardUsers) {
                String decryptedUid = cipherService.decrypt(shardUser.getUid());
                String decryptedPassword = cipherService.decrypt(shardUser.getPassword());
                UserVo user = UserVo.builder()
                        .username(decryptedUid)
                        .password(decryptedPassword)
                        .permission(shardUser.getOperationType())
                        .tag(shardUser.getTag())
                        .build();
                users.add(user);

                userIds.put(shardUser.getId(), decryptedUid);
            }
        }
        return new Pair<>(users, userIds);
    }

    private List<TitanKeyVo> getTitanKey(Map<Integer, String> userIdAndUids) throws SQLException {
        if (userIdAndUids != null && !userIdAndUids.isEmpty()) {
            List<TitanKeyVo> allTitanKeys = Lists.newArrayList();
            for (Map.Entry<Integer, String> userIdAndUid : userIdAndUids.entrySet()) {
                List<TitanKeyVo> titanKeys = Lists.newArrayList();
                Integer userId = userIdAndUid.getKey();
                String uid = userIdAndUid.getValue();
                List<TitanKey> titanKeysInDB = titanKeyService.findTitanKeysByUserId(userId);
                if (titanKeysInDB != null && !titanKeysInDB.isEmpty()) {
                    for (TitanKey titanKeyInDB : titanKeysInDB) {
                        TitanKeyVo titanKey = buildTitanKey(titanKeyInDB, uid);
                        titanKeys.add(titanKey);
                    }
                }
                allTitanKeys.addAll(titanKeys);
            }
            return allTitanKeys;
        }

        return ImmutableList.of();
    }

    private DatabaseVo getDatabase(String role, Integer shardId, String domain) throws SQLException {
        DatabaseVo database = null;
        List<InstanceVo> instances = getInstances(shardId, role);
        if (instances != null && !instances.isEmpty()) {
            database = DatabaseVo.builder()
                    .domain(domain)
                    .port(instances.get(0).getPort())
                    .instances(instances)
                    .build();
            return database;
        }
        return database;
    }

    private List<InstanceVo> getInstances(Integer shardId, String role) throws SQLException {
        List<ShardInstance> shardInstances = shardInstanceService.findByShardIdAndRole(shardId, role);
        if (shardInstances != null && !shardInstances.isEmpty()) {
            List<InstanceVo> instances = Lists.newArrayList();
            for (ShardInstance shardInstance : shardInstances) {
                Instance instanceInDB = instanceService.findById(shardInstance.getInstanceId());
                if (instanceInDB != null) {
                    InstanceVo instance = buildInstance(instanceInDB, shardInstance);
                    instances.add(instance);
                }
            }
            return instances;
        }
        return ImmutableList.of();
    }

    private InstanceVo buildInstance(Instance instanceInDB, ShardInstance shardInstance) {
        InstanceVo instance = InstanceVo.builder()
                .ip(instanceInDB.getIp())
                .port(instanceInDB.getPort())
                .readWeight(shardInstance.getReadWeight())
                .tags(shardInstance.getTags())
                .build();

        return instance;
    }

    private TitanKeyVo buildTitanKey(TitanKey titanKeyInDB, String uid) {
        TitanKeyVo titanKey = TitanKeyVo
                .builder()
                .keyName(titanKeyInDB.getName())
                .uid(uid)
                .extParam(titanKeyInDB.getExtParam())
                .timeOut(titanKeyInDB.getTimeout())
                .createUser(titanKeyInDB.getCreateUser())
                .updateUser(titanKeyInDB.getUpdateUser())
                .permissions(titanKeyInDB.getPermissions())
                .freeVerifyAppIdList(titanKeyInDB.getFreeVerifyApps())
                .freeVerifyIpList(titanKeyInDB.getFreeVerifyIps())
                .enabled(Utils.getEnabled(titanKeyInDB.getStatus()))
                .build();

        return titanKey;
    }

}
