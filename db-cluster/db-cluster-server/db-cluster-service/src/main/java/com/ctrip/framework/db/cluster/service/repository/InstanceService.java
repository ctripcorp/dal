package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.dao.InstanceDao;
import com.ctrip.framework.db.cluster.domain.dto.ShardInstanceDTO;
import com.ctrip.framework.db.cluster.entity.Instance;
import com.ctrip.framework.db.cluster.entity.ShardInstance;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.vo.dal.create.DatabaseVo;
import com.ctrip.framework.db.cluster.vo.dal.create.InstanceVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import com.ctrip.platform.dal.dao.KeyHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by shenjie on 2019/3/11.
 */
@Slf4j
@Service
@AllArgsConstructor
public class InstanceService {

    private final InstanceDao instanceDao;

    private final ShardInstanceService shardInstanceService;


    public void createInstances(final List<ShardInstanceDTO> shardInstanceDTOs) throws SQLException {
        shardInstanceDTOs.forEach(shardInstanceDTO -> {
            try {
                final Instance instance = findUnDeletedInstanceByIpAndPort(shardInstanceDTO.getIp(), shardInstanceDTO.getPort());
                final Integer instanceId;
                if (null == instance) {
                    // not exists
                    final KeyHolder keyHolder = new KeyHolder();
                    // create instance
                    instanceDao.insertWithKeyHolder(
                            keyHolder,
                            Instance.builder()
                                    .ip(shardInstanceDTO.getIp())
                                    .port(shardInstanceDTO.getPort())
                                    .idc("")
                                    .deleted(Deleted.un_deleted.getCode())
                                    .build()
                    );
                    instanceId = keyHolder.getKey().intValue();
                } else {
                    // exists
                    instanceId = instance.getId();
                }
                shardInstanceDTO.setInstanceEntityId(instanceId);
            } catch (SQLException e) {
                throw new RuntimeException("insert instance exception, please try again later.", e);
            }
        });

        // create shardInstances
        if (!CollectionUtils.isEmpty(shardInstanceDTOs)) {
            shardInstanceService.createShardInstances(shardInstanceDTOs);
        }
    }

    public List<Instance> findUnDeletedByPks(final List<Integer> pks) throws SQLException {
        return instanceDao.findUnDeletedByPks(pks);
    }

    public Instance findUnDeletedInstanceByIpAndPort(String ip, Integer port) throws SQLException {
        Instance instance = Instance.builder()
                .ip(ip)
                .port(port)
                .deleted(Deleted.un_deleted.getCode())
                .build();
        List<Instance> instances = instanceDao.queryBy(instance);
        if (instances != null && !instances.isEmpty()) {
            return instances.get(0);
        }
        return null;
    }

    // deprecated
    public void addInstances(ShardVo shard, Integer shardId) throws SQLException {
        // createClusterSets master instance
        DatabaseVo master = shard.getMaster();
        if (master != null) {
            for (InstanceVo instanceVo : master.getInstances()) {
                add(instanceVo, shardId, Constants.ROLE_MASTER);
            }
        }

        // createClusterSets slave instance
        DatabaseVo slave = shard.getSlave();
        if (slave != null) {
            for (InstanceVo instanceVo : slave.getInstances()) {
                add(instanceVo, shardId, Constants.ROLE_CANDIDATE_MASTER);
            }
        }

        // createClusterSets read instances
        DatabaseVo read = shard.getRead();
        if (read != null) {
            for (InstanceVo instanceVo : read.getInstances()) {
                add(instanceVo, shardId, Constants.ROLE_SLAVE);
            }
        }
    }

    public Instance findById(Integer instanceId) throws SQLException {
        return instanceDao.queryByPk(instanceId);
    }


    private void add(InstanceVo instanceVo, int shardId, String role) throws SQLException {
        int instanceId = addAndGetId(instanceVo);

        List<ShardInstance> shardInstances = shardInstanceService.findByShardIdRoleInstanceId(shardId, role, instanceId);
        // exist, update
        if (shardInstances != null && !shardInstances.isEmpty()) {
            ShardInstance shardInstanceInDB = shardInstances.get(0);
            shardInstanceInDB.setReadWeight(instanceVo.getReadWeight());
            shardInstanceInDB.setTags(instanceVo.getTags());
            shardInstanceService.update(shardInstanceInDB);
        } else {
            ShardInstance shardReadInstance = ShardInstance.builder()
                    .shardId(shardId)
                    .instanceId(instanceId)
                    .role(role)
                    .readWeight(instanceVo.getReadWeight())
                    .tags(instanceVo.getTags())
                    .build();
            shardInstanceService.add(shardReadInstance);
        }

    }

    private int addAndGetId(InstanceVo instanceVo) throws SQLException {
        Instance instanceInDB = findUnDeletedInstanceByIpAndPort(instanceVo.getIp(), instanceVo.getPort());
        if (instanceInDB == null) {
            Instance instance = Instance.builder()
                    .ip(instanceVo.getIp())
                    .port(instanceVo.getPort())
                    .build();
            KeyHolder keyHolder = new KeyHolder();
            instanceDao.insertWithKeyHolder(keyHolder, instance);
            return keyHolder.getKey().intValue();
        } else {
            return instanceInDB.getId();
        }
    }
}
