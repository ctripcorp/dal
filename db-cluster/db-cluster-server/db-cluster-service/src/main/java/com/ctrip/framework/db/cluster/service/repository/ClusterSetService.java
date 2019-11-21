package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.dao.ClusterSetDao;
import com.ctrip.framework.db.cluster.domain.dto.ShardDTO;
import com.ctrip.framework.db.cluster.domain.dto.ZoneDTO;
import com.ctrip.framework.db.cluster.entity.ClusterSet;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by @author zhuYongMing on 2019/10/23.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ClusterSetService {

    private final ClusterSetDao clusterSetDao;

    private final ShardService shardService;


    public List<ClusterSet> findCusterSets(final ClusterSet queryClusterSet) throws SQLException {
        return clusterSetDao.queryBy(queryClusterSet);
    }

    public void createClusterSets(final List<ZoneDTO> zoneDTOs) throws SQLException {
        // create zones
        final List<ClusterSet> zones = Lists.newArrayListWithExpectedSize(zoneDTOs.size());
        zoneDTOs.forEach(zoneDTO -> {
            final ClusterSet clusterSet = ClusterSet.builder()
                    .clusterId(zoneDTO.getClusterEntityId())
                    .setId(zoneDTO.getZoneId())
                    .region("") // TODO: 2019/10/29 上海/北京
                    .enabled(Enabled.enabled.getCode())
                    .deleted(Deleted.un_deleted.getCode())
                    .build();
            zones.add(clusterSet);
        });
        clusterSetDao.insertWithKeyHolder(new KeyHolder(), zones);

        // construct shards
        final List<ShardDTO> shardDTOs = zoneDTOs.stream()
                .flatMap(
                        zoneDTO -> zoneDTO.getShards().stream().peek(
                                shardDTO -> shardDTO.setZoneId(zoneDTO.getZoneId())
                        )
                ).collect(Collectors.toList());

        // create shards
        if (!CollectionUtils.isEmpty(shardDTOs)) {
            shardService.createShards(shardDTOs);
        }
    }

    public void updateClusterSets(final List<ClusterSet> updatedClusterSets) throws SQLException {
        clusterSetDao.update(updatedClusterSets);
    }

    public List<ZoneDTO> findUnDeletedByClusterId(final Integer clusterId) throws SQLException {
        // find unDeleted Zones by clusterId
        final ClusterSet queryClusterSet = ClusterSet.builder()
                .clusterId(clusterId)
                .deleted(Deleted.un_deleted.getCode())
                .build();

        final List<ClusterSet> clusterSets = clusterSetDao.queryBy(queryClusterSet);
        if (CollectionUtils.isEmpty(clusterSets)) {
            return Lists.newArrayList();
        }

        final Map<String, List<ShardDTO>> zoneIdWithShardDTOsMap = shardService.findUnDeletedByClusterId(clusterId)
                .stream().collect(Collectors.groupingBy(ShardDTO::getZoneId));

        return componentZoneDTOs(clusterSets, zoneIdWithShardDTOsMap);
    }

    public List<ZoneDTO> findEffectiveByClusterId(final Integer clusterId) throws SQLException {
        // find effective Zones by clusterId
        final ClusterSet queryClusterSet = ClusterSet.builder()
                .clusterId(clusterId)
                .deleted(Deleted.un_deleted.getCode())
                .enabled(Enabled.enabled.getCode())
                .build();

        final List<ClusterSet> clusterSets = clusterSetDao.queryBy(queryClusterSet);
        if (CollectionUtils.isEmpty(clusterSets)) {
            return Lists.newArrayList();
        }

        final Map<String, List<ShardDTO>> zoneIdWithShardDTOsMap = shardService.findEffectiveByClusterId(clusterId)
                .stream().collect(Collectors.groupingBy(ShardDTO::getZoneId));

        return componentZoneDTOs(clusterSets, zoneIdWithShardDTOsMap);
    }

    private List<ZoneDTO> componentZoneDTOs(final List<ClusterSet> clusterSets, final Map<String, List<ShardDTO>> zoneIdWithShardDTOsMap) {
        final List<ZoneDTO> zoneDTOS = Lists.newArrayListWithExpectedSize(clusterSets.size());
        clusterSets.forEach(zone -> {
            final ZoneDTO zoneDTO = ZoneDTO.builder()
                    .zoneEntityId(zone.getId())
                    .clusterEntityId(zone.getClusterId())
                    .zoneId(zone.getSetId())
                    .region(zone.getRegion())
                    .zoneEnabled(zone.getEnabled())
                    .zoneDeleted(zone.getDeleted())
                    .zoneCreateTime(zone.getCreateTime())
                    .zoneUpdateTime(zone.getUpdateTime())
                    .shards(zoneIdWithShardDTOsMap.get(zone.getSetId()))
                    .build();
            zoneDTOS.add(zoneDTO);
        });

        return zoneDTOS;
    }
}
