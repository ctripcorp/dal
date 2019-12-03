package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.dao.ShardDao;
import com.ctrip.framework.db.cluster.domain.dto.ShardDTO;
import com.ctrip.framework.db.cluster.domain.dto.ShardInstanceDTO;
import com.ctrip.framework.db.cluster.domain.dto.UserDTO;
import com.ctrip.framework.db.cluster.entity.Instance;
import com.ctrip.framework.db.cluster.entity.Shard;
import com.ctrip.framework.db.cluster.entity.ShardInstance;
import com.ctrip.framework.db.cluster.entity.User;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by shenjie on 2019/3/5.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ShardService {

    private final ShardDao shardDao;

    private final InstanceService instanceService;

    private final ShardInstanceService shardInstanceService;

    private final UserService userService;


    public void createShards(final List<ShardDTO> shardDTOs) throws SQLException {
        // create shards
        final List<Shard> shards = shardDTOs.stream().map(
                shardDTO -> Shard.builder()
                        .shardIndex(shardDTO.getShardIndex())
                        .clusterId(shardDTO.getClusterEntityId())
                        .setId(shardDTO.getZoneId())
                        .dbName(shardDTO.getDbName())
                        .masterDomain(shardDTO.getMasterDomain())
                        .masterPort(shardDTO.getMasterPort())
                        .slaveDomain(shardDTO.getSlaveDomain())
                        .slavePort(shardDTO.getSlavePort())
                        .readDomain(shardDTO.getReadDomain())
                        .readPort(shardDTO.getReadPort())
                        .deleted(Deleted.un_deleted.getCode())
                        .build()
        ).collect(Collectors.toList());

        final KeyHolder keyHolder = new KeyHolder();
        shardDao.insertWithKeyHolder(keyHolder, shards);
        final List<Number> shardIds = keyHolder.getIdList();

        // construct instances
        final List<ShardInstanceDTO> shardInstanceDTOs = Lists.newArrayList();
        IntStream.range(0, shardDTOs.size()).forEach(shardIndex -> {
            final ShardDTO shardDTO = shardDTOs.get(shardIndex);
            final List<ShardInstanceDTO> shardInstances = Lists.newArrayList();
            shardInstances.add(shardDTO.getMaster());
            shardInstances.addAll(shardDTO.getSlaves());
            shardInstances.addAll(shardDTO.getReads());
            shardInstances.forEach(shardInstanceDTO -> shardInstanceDTO.setShardEntityId(shardIds.get(shardIndex).intValue()));
            shardInstanceDTOs.addAll(shardInstances);
        });

        // create instances
        if (!CollectionUtils.isEmpty(shardInstanceDTOs)) {
            instanceService.createInstances(shardInstanceDTOs);
        }
    }

    public List<Shard> findShards(final List<Integer> clusterIds, final Deleted deleted) throws SQLException {
        return shardDao.findShards(clusterIds, deleted);
    }

    public List<ShardDTO> findUnDeletedByClusterId(final Integer clusterId) throws SQLException {
        final Shard queryShard = Shard.builder()
                .clusterId(clusterId)
                .deleted(Deleted.un_deleted.getCode())
                .build();
        final List<Shard> shards = shardDao.queryBy(queryShard);
        if (CollectionUtils.isEmpty(shards)) {
            return Lists.newArrayList();
        }

        final List<ShardInstance> shardInstances = shardInstanceService.findUnDeletedByShardIds(
                shards.stream().map(Shard::getId).collect(Collectors.toList())
        );
        final List<Instance> instances = instanceService.findUnDeletedByPks(
                shardInstances.stream().map(ShardInstance::getInstanceId).collect(Collectors.toList())
        );
        final Map<ShardInstance, Instance> combineInstances = shardInstances.stream().collect(
                Collectors.toMap(
                        Function.identity(),
                        shardInstance -> instances.stream().filter(
                                instance -> shardInstance.getInstanceId().equals(instance.getId())
                        ).findFirst().orElse(null)
                )
        );

        final List<User> users = userService.findUnDeletedByClusterId(clusterId);

        return componentShardDTOs(shards, combineInstances, users, clusterId);
    }

    public List<ShardDTO> findEffectiveByClusterId(final Integer clusterId) throws SQLException {
        final Shard queryShard = Shard.builder()
                .clusterId(clusterId)
                .deleted(Deleted.un_deleted.getCode())
                .build();
        final List<Shard> shards = shardDao.queryBy(queryShard);
        if (CollectionUtils.isEmpty(shards)) {
            return Lists.newArrayList();
        }

        final List<ShardInstance> shardInstances = shardInstanceService.findEffectiveByShardIds(
                shards.stream().map(Shard::getId).collect(Collectors.toList())
        );
        final List<Instance> instances = instanceService.findUnDeletedByPks(
                shardInstances.stream().map(ShardInstance::getInstanceId).collect(Collectors.toList())
        );
        final Map<ShardInstance, Instance> combineInstances = shardInstances.stream().collect(
                Collectors.toMap(
                        Function.identity(),
                        shardInstance -> instances.stream().filter(
                                instance -> shardInstance.getInstanceId().equals(instance.getId())
                        ).findFirst().orElse(null)
                )
        );

        final List<User> users = userService.findEffectiveByClusterId(clusterId);

        return componentShardDTOs(shards, combineInstances, users, clusterId);
    }

    private List<ShardDTO> componentShardDTOs(final List<Shard> shards,
                                              final Map<ShardInstance, Instance> combineInstances,
                                              final List<User> users, final Integer clusterId) {

        // construct users
        final Map<Integer, List<UserDTO>> shardIndexWithUsersMap = users.stream().map(
                user -> UserDTO.builder()
                        .userEntityId(user.getId())
                        .clusterEntityId(clusterId)
                        .shardIndex(user.getShardIndex())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .permission(user.getPermission())
                        .tag(user.getTag())
                        .titanKeys(user.getTitanKey())
                        .userEnabled(user.getEnabled())
                        .userDeleted(user.getDeleted())
                        .userCreateTime(user.getCreateTime())
                        .userUpdateTime(user.getUpdateTime())
                        .build()
        ).collect(Collectors.groupingBy(UserDTO::getShardIndex));

        final List<ShardDTO> shardDTOS = Lists.newArrayListWithExpectedSize(shards.size());
        shards.forEach(shard -> {
            final Integer shardId = shard.getId();

            // construct master
            final ShardInstanceDTO master;
            final Map.Entry<ShardInstance, Instance> masterEntry =
                    combineInstances.entrySet().stream()
                            .filter(entry -> shardId.equals(entry.getKey().getShardId()))
                            .filter(entry -> Constants.ROLE_MASTER.equalsIgnoreCase(entry.getKey().getRole()))
                            .findFirst().orElse(null);

            if (null == masterEntry) {
                master = null;
            } else if (null == masterEntry.getValue()) {
                log.error(String.format(
                        "ShardInstance is exists, but Instance is not exists, maybe something error, please check it. %s",
                        masterEntry.getKey().toString())
                );
                master = null;
            } else {
                master = componentShardInstanceDTO(masterEntry.getKey(), masterEntry.getValue(), shard);
            }

            // construct slaves
            final List<ShardInstanceDTO> slaves = Lists.newArrayList();
            combineInstances.entrySet().stream()
                    .filter(entry -> shardId.equals(entry.getKey().getShardId()))
                    .filter(entry -> Constants.ROLE_SLAVE.equalsIgnoreCase(entry.getKey().getRole()))
                    .forEach(slaveEntry -> {
                        if (null != slaveEntry.getValue()) {
                            slaves.add(componentShardInstanceDTO(slaveEntry.getKey(), slaveEntry.getValue(), shard));
                        } else {
                            log.error(String.format(
                                    "ShardInstance is exists, but Instance is not exists, maybe something error, please check it. %s",
                                    slaveEntry.getKey().toString())
                            );
                        }
                    });

            // construct reads
            final List<ShardInstanceDTO> reads = Lists.newArrayList();
            combineInstances.entrySet().stream()
                    .filter(entry -> shardId.equals(entry.getKey().getShardId()))
                    .filter(entry -> Constants.ROLE_READ.equalsIgnoreCase(entry.getKey().getRole()))
                    .forEach(readEntry -> {
                        if (null != readEntry.getValue()) {
                            reads.add(componentShardInstanceDTO(readEntry.getKey(), readEntry.getValue(), shard));
                        } else {
                            log.error(String.format(
                                    "ShardInstance is exists, but Instance is not exists, maybe something error, please check it. %s",
                                    readEntry.getKey().toString())
                            );
                        }
                    });

            // construct shardDTO
            final ShardDTO shardDTO = ShardDTO.builder()
                    .shardEntityId(shard.getId())
                    .shardIndex(shard.getShardIndex())
                    .clusterEntityId(shard.getClusterId())
                    .zoneId(shard.getSetId())
                    .dbName(shard.getDbName())
                    .masterDomain(shard.getMasterDomain())
                    .masterPort(shard.getMasterPort())
                    .slaveDomain(shard.getSlaveDomain())
                    .slavePort(shard.getSlavePort())
                    .readDomain(shard.getReadDomain())
                    .readPort(shard.getReadPort())
                    .shardDeleted(shard.getDeleted())
                    .shardCreateTime(shard.getCreateTime())
                    .shardUpdateTime(shard.getUpdateTime())
                    .master(master)
                    .slaves(slaves)
                    .reads(reads)
                    .users(shardIndexWithUsersMap.get(shard.getShardIndex()))
                    .build();
            shardDTOS.add(shardDTO);
        });

        return shardDTOS;
    }

    private ShardInstanceDTO componentShardInstanceDTO(final ShardInstance shardInstance, final Instance instance, final Shard shard) {
        return ShardInstanceDTO.builder()
                .shardInstanceEntityId(shardInstance.getId())
                .shardEntityId(shard.getId())
                .instanceEntityId(instance.getId())
                .role(shardInstance.getRole())
                .readWeight(shardInstance.getReadWeight())
                .tags(shardInstance.getTags())
                .shardInstanceMemberStatus(shardInstance.getMemberStatus())
                .shardInstanceHealthStatus(shardInstance.getHealthStatus())
                .shardInstanceDeleted(shardInstance.getDeleted())
                .shardInstanceCreateTime(shardInstance.getCreateTime())
                .shardInstanceUpdateTime(shardInstance.getUpdateTime())
                .ip(instance.getIp())
                .port(instance.getPort())
                .idc(instance.getIdc())
                .instanceDeleted(instance.getDeleted())
                .instanceCreateTime(instance.getCreateTime())
                .instanceUpdateTime(instance.getUpdateTime())
                .shardIndex(shard.getShardIndex())
                .dbName(shard.getDbName())
                .build();
    }

    public void updateShards(final List<Shard> shards) throws SQLException {
        shardDao.update(shards);
    }

    public List<Shard> findByAllDomain(final String domain, final Deleted deleted) throws SQLException {
        return shardDao.findByAllDomain(domain, deleted);
    }
}
