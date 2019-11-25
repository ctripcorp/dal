package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.domain.dto.ClusterDTO;
import com.ctrip.framework.db.cluster.domain.dto.ShardDTO;
import com.ctrip.framework.db.cluster.domain.dto.ZoneDTO;
import com.ctrip.framework.db.cluster.entity.Shard;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.service.checker.SiteAccessChecker;
import com.ctrip.framework.db.cluster.vo.ResponseStatus;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.ShardService;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by shenjie on 2019/3/14.
 */
@Slf4j
@RestController
@RequestMapping("api/dal/v1")
@AllArgsConstructor
public class ShardController {

    private final SiteAccessChecker siteAccessChecker;

    private final ClusterService clusterService;

    private final ShardService shardService;

    private final RegexMatcher regexMatcher;


    @PostMapping(value = "/clusters/{clusterName}/zones/{zoneId}/shards")
    public ResponseModel addShards(@PathVariable String clusterName, @PathVariable String zoneId,
                                   @RequestParam(name = "operator") String operator,
                                   @RequestBody final ShardVo[] shardVos,
                                   final HttpServletRequest request) {

        try {
            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            // format parameter
            clusterName = Utils.format(clusterName);
            zoneId = Utils.format(zoneId);
            final List<ShardVo> addedShards = Lists.newArrayList(shardVos);

            // parameter valid
            addedShardsValid(addedShards);

            // cluster exists
            final ClusterDTO clusterDTO = clusterService.findEffectiveClusterDTO(clusterName);
            clusterExistsValid(clusterDTO);

            // zone exists
            final ZoneDTO existsZone = zonesExistsValidAndReturn(clusterDTO, zoneId);

            // shardIndex/dbName duplicated
            final List<ShardDTO> existsShards = existsZone.getShards();
            shardDuplicatedValid(existsShards, addedShards);

            // cluster has been released
            clusterHasBeenReleasedValid(clusterDTO);

            // save
            shardService.createShards(converterToDto(addedShards, clusterDTO.getClusterEntityId(), zoneId));

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("add shards success");
            return response;
        } catch (SQLException e) {
            log.error("add shards failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @DeleteMapping(value = "/clusters/{clusterName}/zones/{zoneId}/shards")
    public ResponseModel deleteShards(@PathVariable String clusterName, @PathVariable String zoneId,
                                      @RequestParam(name = "operator") String operator,
                                      @RequestBody Integer[] shardIndexes,
                                      final HttpServletRequest request) {

        try {
            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            // format parameter
            clusterName = Utils.format(clusterName);
            zoneId = Utils.format(zoneId);
            final List<Integer> deletedShardIndexes = Lists.newArrayList(shardIndexes);

            // cluster exists
            final ClusterDTO clusterDTO = clusterService.findEffectiveClusterDTO(clusterName);
            clusterExistsValid(clusterDTO);

            // zone exists
            final ZoneDTO existsZone = zonesExistsValidAndReturn(clusterDTO, zoneId);

            // shardIndex does not exists
            final List<ShardDTO> existsShards = existsZone.getShards();
            shardIndexesDoesNotExistsValid(existsShards, deletedShardIndexes);

            // cluster has been released
            clusterHasBeenReleasedValid(clusterDTO);

            // delete
            final List<Shard> deletedShards = Lists.newArrayListWithExpectedSize(deletedShardIndexes.size());
            deletedShardIndexes.forEach(deletedShardIndex -> {
                final Integer shardId = existsShards.stream()
                        .filter(existsShard -> existsShard.getShardIndex().equals(deletedShardIndex))
                        .map(ShardDTO::getShardEntityId).collect(Collectors.toList()).get(0);
                final Shard deletedShard = Shard.builder()
                        .id(shardId)
                        .deleted(Deleted.deleted.getCode())
                        .build();
                deletedShards.add(deletedShard);
            });
            shardService.updateShards(deletedShards);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("delete shards success");
            return response;
        } catch (Exception e) {
            log.error("delete shards failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    private void addedShardsValid(final List<ShardVo> addedShards) {
        // shards valid
        Preconditions.checkArgument(
                !CollectionUtils.isEmpty(addedShards), "Newly added shards are not allowed to be empty."
        );
        addedShards.forEach(shard -> shard.valid(regexMatcher));

        // shards correct
        addedShards.forEach(ShardVo::correct);

        // shardIndex repeated valid
        final List<Integer> distinctSortedAddedShardIndexes = addedShards.stream()
                .map(ShardVo::getShardIndex).distinct().sorted().collect(Collectors.toList());
        Preconditions.checkArgument(
                distinctSortedAddedShardIndexes.size() == addedShards.size(),
                "Newly added shardIndex are not allowed to be repeated."
        );

        // dbName repeated valid
        final List<String> distinctAddedDbNames = addedShards.stream()
                .map(ShardVo::getDbName).distinct().collect(Collectors.toList());
        Preconditions.checkArgument(
                distinctAddedDbNames.size() == addedShards.size(),
                "Newly added dbName are not allowed to be repeated."
        );
    }

    private void clusterExistsValid(final ClusterDTO clusterDTO) {
        Preconditions.checkArgument(null != clusterDTO, "cluster does not exists.");
    }

    private ZoneDTO zonesExistsValidAndReturn(final ClusterDTO clusterDTO, String zoneId) {
        // zone does not exists
        final Optional<ZoneDTO> zoneOptional = clusterDTO.getZones().stream()
                .filter(zone -> zone.getZoneId().equalsIgnoreCase(zoneId)).findFirst();
        Preconditions.checkArgument(zoneOptional.isPresent(), "zone does not exists.");

        return zoneOptional.get();
    }

    private void shardDuplicatedValid(final List<ShardDTO> existsShards, final List<ShardVo> addedShards) {
        if (!CollectionUtils.isEmpty(existsShards)) {
            // shardIndex exists
            final List<Integer> existsShardIndexes = existsShards.stream()
                    .map(ShardDTO::getShardIndex).collect(Collectors.toList());

            final List<Integer> addedShardIndexes = addedShards.stream()
                    .map(ShardVo::getShardIndex).collect(Collectors.toList());

            addedShardIndexes.forEach(addedShardIndex -> {
                if (existsShardIndexes.contains(addedShardIndex)) {
                    throw new IllegalArgumentException(
                            String.format("Newly shardIndex %s and existing shardIndex duplicated.", addedShardIndex)
                    );
                }
            });

            // dbName exists
            final List<String> existsDbNames = existsShards.stream()
                    .map(ShardDTO::getDbName).collect(Collectors.toList());

            final List<String> addedDbNames = addedShards.stream()
                    .map(ShardVo::getDbName).collect(Collectors.toList());

            addedDbNames.forEach(addedDbName -> {
                if (existsDbNames.contains(addedDbName)) {
                    throw new IllegalArgumentException(
                            String.format("Newly dbName %s and existing dbName duplicated.", addedDbName)
                    );
                }
            });
        }
    }

    private void shardIndexesDoesNotExistsValid(final List<ShardDTO> existsShards,
                                                final List<Integer> deletedShardIndexes) {

        if (CollectionUtils.isEmpty(existsShards)) {
            throw new IllegalArgumentException("All the shardIndex you want to deleted do not exists.");
        }

        final List<Integer> existsShardIndexes = existsShards.stream()
                .map(ShardDTO::getShardIndex).collect(Collectors.toList());

        deletedShardIndexes.forEach(deletedShardIndex -> {
            if (!existsShardIndexes.contains(deletedShardIndex)) {
                throw new IllegalArgumentException(
                        String.format("The shardIndex %s you want to deleted do not exists.", deletedShardIndex)
                );
            }
        });
    }

    private void clusterHasBeenReleasedValid(final ClusterDTO clusterDTO) {
        // cluster has been released
        Preconditions.checkArgument(
                clusterDTO.getClusterReleaseVersion() == 0,
                "The cluster has been released, and it is not allowed to add shards."
        );
    }

    private List<ShardDTO> converterToDto(final List<ShardVo> addedShards,
                                          final Integer clusterId, final String zoneId) {
        // converter
        return addedShards.stream().map(shardVo -> {
            final ShardDTO shardDTO = shardVo.toDTO();
            shardDTO.setClusterEntityId(clusterId);
            shardDTO.setZoneId(zoneId);
            return shardDTO;
        }).collect(Collectors.toList());
    }
}
