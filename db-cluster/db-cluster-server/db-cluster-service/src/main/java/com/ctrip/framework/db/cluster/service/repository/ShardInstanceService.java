package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.dao.ShardInstanceDao;
import com.ctrip.framework.db.cluster.domain.dto.ShardInstanceDTO;
import com.ctrip.framework.db.cluster.entity.ShardInstance;
import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.enums.ShardInstanceHealthStatus;
import com.ctrip.framework.db.cluster.enums.ShardInstanceMemberStatus;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by shenjie on 2019/7/29.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ShardInstanceService {

    private final ShardInstanceDao shardInstanceDao;


    public void createShardInstances(final List<ShardInstanceDTO> shardInstanceDTOs) throws SQLException {
        final List<ShardInstance> shardInstances = Lists.newArrayListWithExpectedSize(shardInstanceDTOs.size());
        shardInstanceDTOs.forEach(shardInstanceDTO -> {
            final ShardInstance shardInstance = ShardInstance.builder()
                    .shardId(shardInstanceDTO.getShardEntityId())
                    .instanceId(shardInstanceDTO.getInstanceEntityId())
                    .role(shardInstanceDTO.getRole())
                    .readWeight(shardInstanceDTO.getReadWeight())
                    .tags(shardInstanceDTO.getTags())
                    .memberStatus(ShardInstanceMemberStatus.enabled.getCode())
                    .healthStatus(ShardInstanceHealthStatus.enabled.getCode())
                    .deleted(Deleted.un_deleted.getCode())
                    .build();
            shardInstances.add(shardInstance);
        });

        final KeyHolder keyHolder = new KeyHolder();
        shardInstanceDao.insertWithKeyHolder(keyHolder, shardInstances);
    }

    public List<ShardInstance> findUnDeletedByShardIds(final List<Integer> shardIds) throws SQLException {
        return shardInstanceDao.findUnDeletedByShardIds(shardIds);
    }

    public List<ShardInstance> findEffectiveByShardIds(final List<Integer> shardIds) throws SQLException {
        return shardInstanceDao.findEffectiveByShardIds(shardIds);
    }

    public List<ShardInstance> findByShardIdAndRole(Integer shardId, String role) throws SQLException {
        ShardInstance shardInstance = ShardInstance.builder()
                .shardId(shardId)
                .role(role)
                .build();
        return shardInstanceDao.queryBy(shardInstance);
    }

    public List<ShardInstance> findUnDeletedByShardIdsAndRole(final List<Integer> shardIds, final String role) throws SQLException {
        return shardInstanceDao.findUnDeletedByShardIdsAndRole(shardIds, role);
    }

    public void updateShardInstances(final List<ShardInstance> shardInstances) throws SQLException {
        shardInstanceDao.update(shardInstances);
    }


    // deprecated
    public int add(ShardInstance shardReadInstance) throws SQLException {
        return shardInstanceDao.insert(shardReadInstance);
    }

    public List<ShardInstance> findByShardId(Integer shardId) throws SQLException {
        ShardInstance shardInstance = ShardInstance.builder()
                .shardId(shardId)
                .build();

        return shardInstanceDao.queryBy(shardInstance);
    }

    public List<ShardInstance> findByShardIdRoleInstanceId(Integer shardId, String role, Integer instanceId) throws SQLException {
        ShardInstance shardInstance = ShardInstance.builder()
                .shardId(shardId)
                .role(role)
                .instanceId(instanceId)
                .build();
        return shardInstanceDao.queryBy(shardInstance);
    }

    public int update(ShardInstance shardInstance) throws SQLException {
        return shardInstanceDao.update(shardInstance);
    }

}
