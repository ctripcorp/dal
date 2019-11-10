package com.ctrip.framework.db.cluster.service.builder;

import com.ctrip.framework.db.cluster.domain.plugin.titan.switches.TitanKeyMhaUpdateData;
import com.ctrip.framework.db.cluster.domain.plugin.titan.add.TitanKeyInfo;
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

    private List<TitanKeyMhaUpdateData> buildMhaUpdateData(List<TitanKey> titanKeys, DatabaseVo master) {
        List<TitanKeyMhaUpdateData> titanKeyMhaUpdateData = Lists.newArrayList();
        InstanceVo masterInstance = master.getInstances().get(0);
        for (TitanKey titanKey : titanKeys) {
            TitanKeyMhaUpdateData data = TitanKeyMhaUpdateData.builder()
                    .keyName(titanKey.getName())
                    .server(masterInstance.getIp())
                    .port(masterInstance.getPort())
                    .build();
            titanKeyMhaUpdateData.add(data);
        }
        return titanKeyMhaUpdateData;
    }
}
