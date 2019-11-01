package com.ctrip.framework.db.cluster.service.builder;

import com.ctrip.framework.db.cluster.domain.plugin.titan.switches.MhaUpdateData;
import com.ctrip.framework.db.cluster.domain.plugin.titan.TitanKeyInfo;
import com.ctrip.framework.db.cluster.domain.plugin.titan.TitanUpdateDBData;
import com.ctrip.framework.db.cluster.domain.plugin.titan.TitanUpdateRequest;
import com.ctrip.framework.db.cluster.entity.Shard;
import com.ctrip.framework.db.cluster.entity.ShardUser;
import com.ctrip.framework.db.cluster.entity.TitanKey;
import com.ctrip.framework.db.cluster.service.repository.ShardService;
import com.ctrip.framework.db.cluster.service.repository.TitanKeyService;
import com.ctrip.framework.db.cluster.service.repository.UserService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.vo.dal.create.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/8/7.
 */
@Slf4j
@Component
public class TitanKeyBuilder {

    @Autowired
    private ShardService shardService;
    @Autowired
    private UserService userService;
    @Autowired
    private TitanKeyService titanKeyService;

    public List<TitanKeyInfo> build(ClusterVo cluster) {
        List<TitanKeyInfo> requestTitanKeys = Lists.newArrayList();
        for (ShardVo shard : cluster.deprGetShards()) {
            Map<String, UserVo> userMap = List2Map(shard.deprGetUsers());
            List<TitanKeyVo> titanKeys = shard.deprGetTitanKeys();
            for (TitanKeyVo titanKey : titanKeys) {
                UserVo user = userMap.get(titanKey.getUid());

                String serverName;
                Integer port;
                String serverIp = null;
                if (Constants.OPERATION_WRITE.equalsIgnoreCase(user.getPermission())) {
                    serverName = shard.getMaster().getDomain();
                    serverIp = shard.getMaster().getInstances().get(0).getIp();
                    port = shard.getMaster().getPort();
                } else {
                    if (Constants.USER_TAG_ETL.equalsIgnoreCase(user.getTag())) {
                        serverName = shard.getSlave().getDomain();
                        port = shard.getSlave().getPort();
                    } else {
                        serverName = shard.getRead().getDomain();
                        port = shard.getRead().getPort();
                    }
                }

                TitanKeyInfo requestTitanKey = buildTitanKey(cluster.getDbCategory(), titanKey, shard, user, serverName, serverIp, port);
                requestTitanKeys.add(requestTitanKey);
            }
        }

        return requestTitanKeys;
    }

    public TitanUpdateRequest buildTitanUpdateRequest(List<ShardVo> shards, String env) throws SQLException {
        List<MhaUpdateData> mhaUpdateData = Lists.newArrayList();
        List<TitanUpdateDBData> titanUpdateDBData = Lists.newArrayList();
        for (ShardVo shard : shards) {
            DatabaseVo master = shard.getMaster();
            InstanceVo masterInstance = master.getInstances().get(0);
            List<Shard> shardsInDB = shardService.findShardsByDbName(shard.getDbName());
            if (shardsInDB == null || shardsInDB.isEmpty()) {
                // build titan update data
                TitanUpdateDBData oneShardTitanUpdateDBData = TitanUpdateDBData.builder()
                        .dbName(shard.getDbName())
                        .domain(master.getDomain())
                        .ip(masterInstance.getIp())
                        .port(masterInstance.getPort())
                        .build();
                titanUpdateDBData.add(oneShardTitanUpdateDBData);
            } else {
                // find master titanKeys
                List<ShardUser> users = userService.findUsersByShardIdAndOperationType(shardsInDB.get(0).getId(), Constants.OPERATION_WRITE);
                List<TitanKey> masterTitanKeys = Lists.newArrayList();
                for (ShardUser user : users) {
                    List<TitanKey> titanKeys = titanKeyService.findTitanKeysByUserId(user.getId());
                    masterTitanKeys.addAll(titanKeys);
                }


                // build mha update data
                List<MhaUpdateData> oneShardMhaUpdateData = buildMhaUpdateData(masterTitanKeys, master);
                mhaUpdateData.addAll(oneShardMhaUpdateData);
            }
        }
        TitanUpdateRequest request = TitanUpdateRequest.builder()
                .env(env)
                .mhaData(mhaUpdateData)
                .dbData(titanUpdateDBData)
                .build();
        return request;
    }

    private TitanKeyInfo buildTitanKey(String dbCategory, TitanKeyVo titanKey, ShardVo shard, UserVo user, String serverName, String serverIp, Integer port) {
        String providerName = dbCategory.equalsIgnoreCase(Constants.MYSQL_DB) ?
                Constants.MYSQL_PROVIDER_NAME : Constants.SQL_SERVER_PROVIDER_NAME;

        TitanKeyInfo requestTitanKey = TitanKeyInfo.builder()
                .keyName(titanKey.getKeyName())
                .providerName(providerName)
                .dbName(shard.getDbName())
                .serverIp(serverIp)
                .serverName(serverName)
                .port(port.toString())
                .uid(user.getUsername())
                .password(user.getPassword())
                .extParam(titanKey.getExtParam())
                .timeOut(titanKey.getTimeOut())
                .enabled(titanKey.getEnabled())
                .createUser(titanKey.getCreateUser())
                .updateUser(titanKey.getUpdateUser())
                .permissions(titanKey.getPermissions())
                .freeVerifyAppIdList(titanKey.getFreeVerifyAppIdList())
                .freeVerifyIpList(titanKey.getFreeVerifyIpList())
                .build();

        return requestTitanKey;
    }

    private Map<String, UserVo> List2Map(List<UserVo> users) {
        Map<String, UserVo> userMap = Maps.newHashMapWithExpectedSize(users.size());
        for (UserVo userVo : users) {
            userMap.put(userVo.getUsername(), userVo);
        }
        return userMap;
    }

    private List<MhaUpdateData> buildMhaUpdateData(List<TitanKey> titanKeys, DatabaseVo master) {
        List<MhaUpdateData> mhaUpdateData = Lists.newArrayList();
        InstanceVo masterInstance = master.getInstances().get(0);
        for (TitanKey titanKey : titanKeys) {
            MhaUpdateData data = MhaUpdateData.builder()
                    .keyName(titanKey.getName())
                    .server(masterInstance.getIp())
                    .port(masterInstance.getPort())
                    .build();
            mhaUpdateData.add(data);
        }
        return mhaUpdateData;
    }
}
