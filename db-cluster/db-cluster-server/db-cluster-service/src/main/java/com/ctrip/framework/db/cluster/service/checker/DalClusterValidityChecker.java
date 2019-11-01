package com.ctrip.framework.db.cluster.service.checker;


import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.dal.create.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by shenjie on 2019/3/19.
 */
@Component
@AllArgsConstructor
public class DalClusterValidityChecker {

    private final RegexMatcher regexMatcher;

    // 检查shard
    public void checkCluster(ClusterVo cluster, String operator) {
        Preconditions.checkNotNull(cluster, "cluster信息为空");
        String clusterName = cluster.getClusterName();
        Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clusterName为空");
        cluster.setClusterName(Utils.format(clusterName));
        Preconditions.checkArgument(regexMatcher.clusterName(cluster.getClusterName()), "clusterName不合法");
        if (StringUtils.isBlank(cluster.getDbCategory())) {
            cluster.setDbCategory(Constants.MYSQL_DB);
        }
        checkShards(cluster.deprGetShards(), operator);
    }

    public void checkShards(List<ShardVo> shards, String operator) {
        Preconditions.checkArgument(shards != null && !shards.isEmpty(), "shards为空");
        for (ShardVo shardVo : shards) {
            checkShard(shardVo, operator);
        }

        checkShardIndex(shards);
        checkShardDBName(shards);
    }

    private void checkShardIndex(List<ShardVo> shards) {
        Set<Integer> shardIndexes = Sets.newHashSetWithExpectedSize(shards.size());
        for (ShardVo shard : shards) {
            Integer shardIndex = shard.deprGetIndex();
            if (!shardIndexes.contains(shardIndex)) {
                shardIndexes.add(shardIndex);
            } else {
                throw new IllegalArgumentException("shard index[" + shardIndex + "]冲突");
            }
        }
    }

    private void checkShardDBName(List<ShardVo> shards) {
        Set<String> dbNames = Sets.newHashSetWithExpectedSize(shards.size());
        for (ShardVo shard : shards) {
            String dbName = shard.getDbName();
            if (!dbNames.contains(dbName)) {
                dbNames.add(dbName);
            } else {
                throw new IllegalArgumentException("dbName[" + dbName + "]冲突");
            }
        }
    }

    public void checkShard(ShardVo shard, String operator) {
        Preconditions.checkNotNull(shard, "shard为空");
        String dbName = shard.getDbName();
        Preconditions.checkArgument(StringUtils.isNotBlank(dbName), "dbName为空");
        shard.setDbName(Utils.format(dbName));
        Preconditions.checkArgument(regexMatcher.dbName(dbName), "dbName不合法.");

        if (shard.deprGetIndex() == null) {
//            shard.setIndex(0);
        }

        checkMaster(shard.getMaster());
        checkSlave(shard.getSlave());
        checkRead(shard.getRead());
        checkUserAndTitanKey(shard.deprGetUsers(), shard.deprGetTitanKeys(), operator);
    }

    public void checkDatabases(DatabaseGroupVo databaseGroup) {
        Preconditions.checkNotNull(databaseGroup, "databases为空");
        List<ShardVo> databases = databaseGroup.getDatabases();
        Preconditions.checkArgument(databases != null && !databases.isEmpty(), "databases为空");

        for (ShardVo shard : databases) {
            checkDatabase(shard);
        }

        checkShardDBName(databases);
    }

    public void checkDatabase(ShardVo database) {
        Preconditions.checkNotNull(database, "database为空");
        String dbName = database.getDbName();
        Preconditions.checkArgument(StringUtils.isNotBlank(dbName), "dbName为空");
        database.setDbName(Utils.format(dbName));
        Preconditions.checkArgument(regexMatcher.dbName(dbName), "dbName不合法.");

        checkMaster(database.getMaster());
        checkSlave(database.getSlave());
        checkRead(database.getRead());
    }

    public void checkMaster(DatabaseVo master) {
        Preconditions.checkNotNull(master, "master为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(master.getDomain()), "master的domain为空");
        Preconditions.checkArgument(regexMatcher.domain(master.getDomain()), "master的domain不合法");
        Preconditions.checkNotNull(master.getPort(), "master的port为空");
        Preconditions.checkArgument(regexMatcher.port(master.getPort().toString()), "master的port不合法");
        List<InstanceVo> instances = master.getInstances();
        Preconditions.checkArgument(instances != null && !instances.isEmpty(), "master的instance为空");
        Preconditions.checkArgument(instances.size() == 1, "master只能有一个instance");
        checkInstances(master.getInstances(), master.getPort());
    }

    public void checkSlave(DatabaseVo slave) {
        if (slave != null) {
            Preconditions.checkArgument(StringUtils.isNotBlank(slave.getDomain()), "slave的domain为空");
            Preconditions.checkArgument(regexMatcher.domain(slave.getDomain()), "slave的domain不合法");
            Preconditions.checkNotNull(slave.getPort(), "slave的port为空");
            Preconditions.checkArgument(regexMatcher.port(slave.getPort().toString()), "slave的port不合法");
            List<InstanceVo> instances = slave.getInstances();
            Preconditions.checkArgument(instances != null && !instances.isEmpty(), "slave的instance为空");
            Preconditions.checkArgument(instances.size() == 1, "slave只能有一个instance");
            checkInstances(slave.getInstances(), slave.getPort());
        }
    }

    public void checkRead(DatabaseVo read) {
        if (read != null) {
            Preconditions.checkArgument(StringUtils.isNotBlank(read.getDomain()), "read的domain为空");
            Preconditions.checkArgument(regexMatcher.domain(read.getDomain()), "read的domain不合法");
            Preconditions.checkNotNull(read.getPort(), "read的port为空");
            Preconditions.checkArgument(regexMatcher.port(read.getPort().toString()), "read的port不合法");
            List<InstanceVo> instances = read.getInstances();
            Preconditions.checkArgument(instances != null && !instances.isEmpty(), "read的instances为空");
            checkInstances(read.getInstances(), read.getPort());
        }
    }

    public void checkInstances(List<InstanceVo> instances, int domainPort) {
        for (InstanceVo instanceVo : instances) {
            checkInstance(instanceVo, domainPort);
        }
    }

    public void checkInstance(InstanceVo instance, int domainPort) {
        Preconditions.checkNotNull(instance, "instance为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(instance.getIp()), "instance的ip为空");
        Preconditions.checkArgument(regexMatcher.ip(instance.getIp()), "instance的ip不合法");

        Integer port = instance.getPort();
        if (port == null) {
            instance.setPort(domainPort);
        } else {
            Preconditions.checkArgument(regexMatcher.port(port.toString()), "instance的port不合法");
            Preconditions.checkArgument(domainPort == port.intValue(), "instance的port与domain的port不一致");
        }

        if (instance.getReadWeight() == null) {
            instance.setReadWeight(1);
        }

//        if (instance.getEnabled() == null) {
//            instance.setEnabled(true);
//        }
    }

    public void checkUserAndTitanKey(List<UserVo> users, List<TitanKeyVo> titanKeys, String operator) {
        if (users != null && !users.isEmpty()) {
            checkUsers(users);
            checkTitanKeys(titanKeys, users, operator);
        } else if (titanKeys != null && !titanKeys.isEmpty()) {
            throw new IllegalArgumentException("users为空，无法添加titanKey");
        }
    }

    public void checkUsers(List<UserVo> users) {
        for (UserVo user : users) {
            checkUser(user);
        }
    }

    public void checkUser(UserVo user) {
        Preconditions.checkNotNull(user, "user为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(user.getUsername()), "user的uid为空");
        Preconditions.checkArgument(regexMatcher.userId(user.getUsername()), "user的uid不合法");
        Preconditions.checkArgument(StringUtils.isNotBlank(user.getPassword()), "user的password为空");
        Preconditions.checkArgument(regexMatcher.password(user.getPassword()), "user的password不合法");
        String operateType = user.getPermission();
        Preconditions.checkArgument(StringUtils.isNotBlank(operateType), "user的operateType为空");
        Preconditions.checkArgument(regexMatcher.checkOperateType(operateType), "user的operateType必须为write或read");
        user.setPermission(Utils.format(operateType));

        String userTag = user.getTag();
        if (userTag == null) {
            user.setTag(Constants.USER_TAG_DEFAULT);
        } else {
            user.setTag(Utils.format(userTag));
        }
    }

    public void checkTitanKeys(List<TitanKeyVo> titanKeys, List<UserVo> users, String operator) {
        if (titanKeys != null && !titanKeys.isEmpty()) {
            Set<String> uids = getUids(users);
            for (TitanKeyVo titanKey : titanKeys) {
                checkTitanKey(titanKey, uids, operator);
            }
        }
    }

    public void checkTitanKey(TitanKeyVo titanKey, Set<String> uids, String operator) {
        Preconditions.checkNotNull(titanKey, "titanKey为空");
        String titanKeyName = titanKey.getKeyName();
        Preconditions.checkArgument(StringUtils.isNotBlank(titanKeyName), "titanKey的keyName为空");
        titanKey.setKeyName(Utils.format(titanKeyName));
        String uid = titanKey.getUid();
        Preconditions.checkArgument(StringUtils.isNotBlank(uid), "titanKey的uid为空");

        if (!uids.contains(uid)) {
            throw new IllegalArgumentException("titanKey[" + titanKeyName + "]对应的user[" + uid + "]不存在");
        }

        if (StringUtils.isBlank(titanKey.getCreateUser())) {
            titanKey.setCreateUser(operator);
        }

        if (StringUtils.isBlank(titanKey.getUpdateUser())) {
            titanKey.setUpdateUser(operator);
        }

        if (titanKey.getTimeOut() == null) {
            titanKey.setTimeOut(15);
        }

        if (titanKey.getEnabled() == null) {
            titanKey.setEnabled(true);
        }
    }

    private Set<String> getUids(List<UserVo> users) {
        Set<String> uids = Sets.newHashSet();
        for (UserVo user : users) {
            uids.add(user.getUsername());
        }
        return uids;
    }
}
