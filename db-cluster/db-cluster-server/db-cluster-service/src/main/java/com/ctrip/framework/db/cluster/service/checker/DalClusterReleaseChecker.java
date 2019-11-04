package com.ctrip.framework.db.cluster.service.checker;

import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.vo.dal.create.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by shenjie on 2019/8/14.
 */
@Component
public class DalClusterReleaseChecker {

    @Autowired
    private DBConnectionChecker dbConnectionChecker;
    @Autowired
    private ConfigService configService;

    public void check(ClusterVo cluster, String env) {
        checkClusters(cluster);
        checkDbConnection(cluster, env);
    }

    public void checkClusters(ClusterVo cluster) {
        Preconditions.checkNotNull(cluster, "cluster不存在");
        Preconditions.checkArgument(StringUtils.isNotBlank(cluster.getClusterName()), "clusterName为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(cluster.getDbCategory()), "category为空");
        checkShards(cluster.deprGetShards());
    }

    public void checkShards(List<ShardVo> shards) {
        Preconditions.checkArgument(shards != null && !shards.isEmpty(), "shards为空");
        for (ShardVo shardVo : shards) {
            checkShard(shardVo);
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
                throw new IllegalArgumentException("shard index[" + shardIndex + "] 冲突");
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
                throw new IllegalArgumentException("dbName[" + dbName + "] 冲突");
            }
        }
    }

    public void checkShard(ShardVo shard) {
        Preconditions.checkNotNull(shard, "shard为空");
        Preconditions.checkNotNull(shard.deprGetIndex(), "shard的index为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(shard.getDbName()), "shard的dbName为空");

        checkMaster(shard.getMaster());
        checkSlave(shard.getSlave());
        checkRead(shard.getRead());
        checkUsers(shard.deprGetUsers());
        checkTitanKeys(shard.deprGetTitanKeys());
    }

    public void checkMaster(DatabaseVo master) {
        Preconditions.checkNotNull(master, "master为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(master.getDomain()), "master的domain为空");
        Preconditions.checkNotNull(master.getPort(), "master的port为空");
        List<InstanceVo> instances = master.getInstances();
        Preconditions.checkArgument(instances != null && !instances.isEmpty(), "master的instance为空");
        Preconditions.checkArgument(instances.size() == 1, "master只能有一个instance");
        checkInstances(master.getInstances(), master.getPort());
    }

    public void checkSlave(DatabaseVo slave) {
        if (slave != null) {
            Preconditions.checkArgument(StringUtils.isNotBlank(slave.getDomain()), "slave的domain为空");
            Preconditions.checkNotNull(slave.getPort(), "slave的port为空");
            List<InstanceVo> instances = slave.getInstances();
            Preconditions.checkArgument(instances != null && !instances.isEmpty(), "slave的instance为空");
            Preconditions.checkArgument(instances.size() == 1, "slave只能有一个instance");
            checkInstances(slave.getInstances(), slave.getPort());
        }
    }

    public void checkRead(DatabaseVo read) {
        if (read != null) {
            Preconditions.checkArgument(StringUtils.isNotBlank(read.getDomain()), "read的domain为空");
            Preconditions.checkNotNull(read.getPort(), "read的port为空");
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
        Integer port = instance.getPort();
        Preconditions.checkNotNull(port, "instance的port为空");
        Preconditions.checkArgument(domainPort == port.intValue(), "instance的port与domain的port不一致");
    }

    public void checkUsers(List<UserVo> users) {
        Preconditions.checkArgument(users != null && !users.isEmpty(), "shard的users为空");
        for (UserVo user : users) {
            checkUser(user);
        }
    }

    public void checkUser(UserVo user) {
        Preconditions.checkNotNull(user, "user为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(user.getUsername()), "user的uid为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(user.getPassword()), "user的password为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(user.getPermission()), "user的operateType为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(user.getTag()), "user的tag为空");
    }

    public void checkTitanKeys(List<TitanKeyVo> titanKeys) {
        Preconditions.checkArgument(titanKeys != null && !titanKeys.isEmpty(), "shard的titanKeys为空");
        for (TitanKeyVo titanKey : titanKeys) {
            checkTitanKey(titanKey);
        }
    }

    public void checkTitanKey(TitanKeyVo titanKey) {
        Preconditions.checkNotNull(titanKey, "titanKey为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(titanKey.getKeyName()), "titanKey的keyName为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(titanKey.getUid()), "titanKey的uid为空");
        Preconditions.checkNotNull(titanKey.getEnabled(), "titanKey的enabled为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(titanKey.getCreateUser()), "titanKey的createUser为空");
    }

    private void checkDbConnection(ClusterVo cluster, String env) {
        // check clusters db connection
//        if (configService.enableDBConnectionCheck()) {
        dbConnectionChecker.checkDBConnection(cluster, env);
//        }
    }
}
