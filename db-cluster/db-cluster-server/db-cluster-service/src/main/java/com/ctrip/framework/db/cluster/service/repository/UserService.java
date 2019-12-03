package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.dao.UserDao;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.domain.plugin.titan.get.TitanKeyGetData;
import com.ctrip.framework.db.cluster.domain.plugin.titan.get.TitanKeyGetResponse;
import com.ctrip.framework.db.cluster.domain.plugin.titan.add.TitanKeyInfo;
import com.ctrip.framework.db.cluster.domain.dto.ClusterDTO;
import com.ctrip.framework.db.cluster.entity.ShardUser;
import com.ctrip.framework.db.cluster.entity.User;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.service.plugin.TitanPluginService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.RC4;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import com.ctrip.framework.db.cluster.vo.dal.create.UserVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ZoneVo;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/3/11.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final CipherService cipherService;

    private final TitanPluginService titanPluginService;


    public void addUsers(int shardIndex, List<UserVo> users, final ClusterDTO clusterDTO) {
        int clusterId = clusterDTO.getClusterEntityId();
        ShardVo shardVo = getShard(clusterDTO.toVo(), shardIndex);
        checkUsers(clusterId, shardIndex, users);
        List<User> userList = new ArrayList<>();
        for (UserVo user : users) {
            addUser(clusterId, shardVo, user);
            userList.add(buildUser(clusterId, shardIndex, user));
        }
        try {
            userDao.batchInsert(userList);
        } catch (SQLException e) {
            throw new DBClusterServiceException("save user info error", e);
        }
    }

    private ShardVo getShard(ClusterVo cluster, int shardIndex) {
        List<ZoneVo> zones = cluster.getZones();
        if (zones == null || zones.size() == 0)
            throw new DBClusterServiceException("no zone info");
        // TODO: 2019/11/22 titanKey for each zone.
        List<ShardVo> shards = cluster.getZones().get(0).getShards();
        for (ShardVo shard : shards) {
            if (shard.getShardIndex() == shardIndex)
                return shard;
        }
        throw new DBClusterServiceException("shard not found");
    }

    private void checkUsers(int clusterId, int shardIndex, List<UserVo> users) {
        // TODO: different users with same permission should have different tags
        try {
            List<User> existUsers = findEffectiveByClusterIdAndShardIndex(clusterId, shardIndex);
            users.forEach(user -> {
                existUsers.forEach(existUser -> {
                    if (existUser.getUsername().equalsIgnoreCase(user.getUsername()))
                        throw new DBClusterServiceException(String.format("user '%s' duplicated", user.getUsername()));
                });
            });
        } catch (Exception e) {
            throw new DBClusterServiceException(e.getMessage(), e);
        }
    }

    private void addUser(int clusterId, ShardVo shardVo, UserVo user) {
        UserInfoContext ctx = new UserInfoContext();
        ctx.clusterId = clusterId;
        fillUserInfoContext(ctx, user, shardVo);

        String titanKeyNames = user.getTitanKey();
        if (titanKeyNames != null && !titanKeyNames.isEmpty()) {
            String[] titanKeyNameArr = titanKeyNames.split(",");
            for (String titanKeyName : titanKeyNameArr) {
                TitanKeyGetData titanKeyData = queryTitanKey(titanKeyName);
                if (titanKeyData != null) {
                    validateTitanKey(titanKeyData, ctx);
                    user.setPassword(RC4.decrypt(titanKeyData.getPassword()));
                } else {
                    TitanKeyInfo newTitanKey = prepareTitanKey(titanKeyName, ctx);
                    PluginResponse response = titanPluginService.addTitanKey(newTitanKey, Constants.ENV);
                    log.info(String.format("Add Titan Key: %s. Result Code: %s; Result Msg: %s", titanKeyName,
                            response.getStatus(), response.getMessage()));
                }
            }
        }
    }

    private void fillUserInfoContext(UserInfoContext ctx, UserVo userVo, ShardVo shardVo) {
        ctx.shardIndex = shardVo.getShardIndex();
        ctx.dbName = shardVo.getDbName();
        ctx.uid = userVo.getUsername();
        ctx.pwd = userVo.getPassword();
        if ("write".equalsIgnoreCase(userVo.getPermission())) {
            ctx.domain = shardVo.getMaster().getDomain();
            ctx.ip = shardVo.getMaster().getInstance().getIp();
            ctx.port = shardVo.getMaster().getPort();  // TODO: port validation
        } else if ("read".equalsIgnoreCase(userVo.getPermission())) {
            if ("etl".equalsIgnoreCase(userVo.getTag())) {
                ctx.domain = shardVo.getSlave().getDomain();
                ctx.port = shardVo.getSlave().getPort();
            } else {
                ctx.domain = shardVo.getRead().getDomain();
                ctx.port = shardVo.getRead().getPort();
            }
        } else {
            throw new DBClusterServiceException("invalid user permission");
        }
    }

    private TitanKeyGetData queryTitanKey(String titanKeyName) {
        TitanKeyGetResponse response = titanPluginService.getTitanKey(titanKeyName, Constants.ENV);
        if (response == null || response.getStatus() != 0)  // error code?
            throw new DBClusterServiceException("query titan key error");
        return response.getData();
    }

    private void validateTitanKey(TitanKeyGetData titanKeyData, UserInfoContext ctx) {
        Preconditions.checkArgument(ctx.dbName.equalsIgnoreCase(titanKeyData.getDbName()), "dbName unmatched with titan key");
//        Preconditions.checkArgument(ctx.domain.equalsIgnoreCase(titanKeyData.getServerName()), "domain unmatched with titan key");
//        String titanKeyIp = titanKeyData.getServerIp();
//        Preconditions.checkArgument(titanKeyIp == null || titanKeyIp.isEmpty() || titanKeyIp.equalsIgnoreCase(ctx.ip), "ip unmatched with titan key");
//        Preconditions.checkArgument(ctx.port == Integer.parseInt(titanKeyData.getPort()), "port unmatched with titan key");
        Preconditions.checkArgument(ctx.uid.equals(titanKeyData.getUid()), "uid unmatched with titan key");
        Preconditions.checkArgument(ctx.pwd == null || ctx.pwd.isEmpty(), "pwd should be empty");
    }

    private TitanKeyInfo prepareTitanKey(String titanKeyName, UserInfoContext ctx) {
        Preconditions.checkArgument(ctx.pwd != null && !ctx.pwd.isEmpty(), "pwd should not be empty");
        return TitanKeyInfo.builder()
                .keyName(titanKeyName)
                .dbName(ctx.dbName)
                .serverName(ctx.domain)
                .serverIp(ctx.ip)
                .port(String.valueOf(ctx.port))
                .uid(ctx.uid)
                .password(ctx.pwd)
                .timeOut(15)
                .enabled(true)
                .providerName("MySql.Data.MySqlClient")
                .build();
    }

    private User buildUser(int clusterId, int shardIndex, UserVo userVo) {
        return User.builder()
                .clusterId(clusterId)
                .shardIndex(shardIndex)
                .username(cipherService.encrypt(userVo.getUsername()))
                .password(cipherService.encrypt(userVo.getPassword()))
                .permission(userVo.getPermission())
                .tag(userVo.getTag())
                .titanKey(userVo.getTitanKey())
                .build();
    }

    private static class UserInfoContext {
        public int clusterId;
        public int shardIndex;
        public String dbName;
        public String domain;
        public String ip;
        public int port;
        public String uid;
        public String pwd;
    }


    public List<User> findUnDeletedByClusterId(final Integer clusterId) throws SQLException {
        final User queryUser = User.builder()
                .clusterId(clusterId)
                .deleted(Deleted.un_deleted.getCode())
                .build();

        return userDao.queryBy(queryUser);
    }

    public List<User> findEffectiveByClusterId(final Integer clusterId) throws SQLException {
        final User queryUser = User.builder()
                .clusterId(clusterId)
                .deleted(Deleted.un_deleted.getCode())
                .enabled(Enabled.enabled.getCode())
                .build();

        return userDao.queryBy(queryUser);
    }

    public List<User> findEffectiveByClusterIdAndShardIndex(int clusterId, int shardIndex) throws SQLException {
        final User queryUser = User.builder()
                .clusterId(clusterId)
                .shardIndex(shardIndex)
                .deleted(Deleted.un_deleted.getCode())
                .enabled(Enabled.enabled.getCode())
                .build();

        return userDao.queryBy(queryUser);
    }

    /* Below are Deprecated */
    public Map<String, Integer> addAndGetIds(List<UserVo> users, int shardId) throws SQLException {
        if (users == null || users.isEmpty()) {
            return ImmutableMap.of();
        }

        Map<String, Integer> userIds = Maps.newHashMapWithExpectedSize(users.size());
        for (UserVo userVo : users) {
            String uid = userVo.getUsername();
            String password = userVo.getPassword();
            String encryptedUid = cipherService.encrypt(uid);
            String encryptedPassword = cipherService.encrypt(password);
            User user = User.builder()
//                    .shardId(shardId)
//                    .uid(encryptedUid)
                    .password(encryptedPassword)
                    .tag(userVo.getTag())
//                    .operationType(userVo.getPermission())
                    .build();

            KeyHolder keyHolder = new KeyHolder();
            userDao.insert(null, keyHolder, user);
            userIds.put(uid, keyHolder.getKey().intValue());
        }
        return userIds;
    }

    public List<ShardUser> findUsersByShardId(Integer shardId) throws SQLException {
        User shardUser = User.builder().build();
        List<User> shardUsers = userDao.queryBy(shardUser);
        return null;
    }

    public List<ShardUser> findUsersByShardIdAndOperationType(Integer shardId, String operationType) throws SQLException {
        User shardUser = User.builder().build();
        List<User> shardUsers = userDao.queryBy(shardUser);
        return null;
    }

}
