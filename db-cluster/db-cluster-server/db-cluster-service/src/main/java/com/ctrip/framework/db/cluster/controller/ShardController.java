package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.domain.dto.ClusterDTO;
import com.ctrip.framework.db.cluster.domain.dto.ShardDTO;
import com.ctrip.framework.db.cluster.domain.dto.ZoneDTO;
import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.entity.ShardInstance;
import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.enums.Enabled;
import com.ctrip.framework.db.cluster.enums.ResponseStatus;
import com.ctrip.framework.db.cluster.service.DalClusterManager;
import com.ctrip.framework.db.cluster.service.TitanSyncService;
import com.ctrip.framework.db.cluster.service.checker.DalClusterValidityChecker;
import com.ctrip.framework.db.cluster.service.checker.SiteAccessChecker;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.ShardService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private final ClusterService clusterService;

    private final ShardService shardService;

    private final RegexMatcher regexMatcher;

    // deprecated
    private final SiteAccessChecker siteAccessChecker;
    private final DalClusterManager dalClusterManager;
    private final TitanSyncService titanSyncService;
    private final DalClusterValidityChecker dalClusterValidityChecker;


    @PostMapping(value = "/clusters/{clusterName}/zones/{zoneId}/shards")
    public ResponseModel addShards(@PathVariable String clusterName, @PathVariable String zoneId,
                                   @RequestParam(name = "operator") String operator, @RequestBody final ShardVo[] shards) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);
            zoneId = Utils.format(zoneId);
            final List<ShardVo> addedShards = Lists.newArrayList(shards);

            // valid and converter
            final List<ShardDTO> shardDTOS = addShardsValidAndConverter(clusterName, zoneId, addedShards);

            // save
            shardService.createShards(shardDTOS);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("add shards success");
            return response;
        } catch (SQLException e) {
            log.error("add shards failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    private List<ShardDTO> addShardsValidAndConverter(final String clusterName, String zoneId,
                                                      final List<ShardVo> addedShards) throws SQLException {
        // shards valid
        Preconditions.checkArgument(
                !CollectionUtils.isEmpty(addedShards), "Newly added shards are not allowed to be empty."
        );
        addedShards.forEach(shard -> shard.valid(regexMatcher));

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

        // shards correct
        addedShards.forEach(ShardVo::correct);

        // cluster does not exists
        final ClusterDTO clusterDTO = clusterService.findUnDeletedClusterDTO(clusterName);
        Preconditions.checkArgument(null != clusterDTO, "cluster does not exists.");

        // cluster has been released
        Preconditions.checkArgument(
                clusterDTO.getClusterReleaseVersion() == 0,
                "The cluster has been released, and it is not allowed to add shards."
        );

        // zone does not exists
        final Optional<ZoneDTO> zoneOptional = clusterDTO.getZones().stream()
                .filter(zone -> zone.getZoneId().equalsIgnoreCase(zoneId)).findFirst();
        Preconditions.checkArgument(zoneOptional.isPresent(), "zone does not exists.");

        // shard exists
        final List<ShardDTO> existsShards = zoneOptional.get().getShards();
        if (!CollectionUtils.isEmpty(existsShards)) {

            // shardIndex duplicated
            final List<Integer> existsShardIndexes = existsShards.stream()
                    .map(ShardDTO::getShardIndex).collect(Collectors.toList());
            distinctSortedAddedShardIndexes.forEach(addedShardIndex -> {
                if (existsShardIndexes.contains(addedShardIndex)) {
                    throw new IllegalArgumentException(
                            String.format("Newly shardIndex %s and existing shardIndex duplicated.", addedShardIndex)
                    );
                }
            });

            // dbName duplicated
            final List<String> existsDbNames = existsShards.stream()
                    .map(ShardDTO::getDbName).collect(Collectors.toList());
            distinctAddedDbNames.forEach(addedDbName -> {
                if (existsDbNames.contains(addedDbName)) {
                    throw new IllegalArgumentException(
                            String.format("Newly dbName %s and existing dbName duplicated.", addedDbName)
                    );
                }
            });
        }

        // converter
        return addedShards.stream().map(shardVo -> {
            final ShardDTO shardDTO = shardVo.toDTO();
            shardDTO.setClusterEntityId(0);
            shardDTO.setZoneId(zoneId);
            return shardDTO;
        }).collect(Collectors.toList());
    }

    @DeleteMapping(value = "/clusters/{clusterName}/zones/{zoneId}/shards")
    public ResponseModel deleteShards(@PathVariable String clusterName, @PathVariable String zoneId,
                                      @RequestParam(name = "operator") String operator, @RequestBody String[] shardIndexes) {


        ResponseModel response = ResponseModel.successResponse();
        response.setMessage("delete shards success");
        return response;
    }





    // deprecated
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseModel switchCluster(@RequestBody List<ShardVo> shards,
                                       @RequestParam(name = "clustername", required = false) String clusterName,
                                       @RequestParam(name = "operator", required = false) String operator,
                                       HttpServletRequest request) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clustername参数为空");
            Preconditions.checkArgument(StringUtils.isNotBlank(operator), "operator参数为空");
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            // check shards
            dalClusterValidityChecker.checkShards(shards, operator);

            // createClusterSets shards
            dalClusterManager.addShard(clusterName, shards);

            // sync titan keys
            Cluster clusterInDB = clusterService.findCluster(
                    clusterName, Deleted.un_deleted, null
            );
            ClusterVo cluster = ClusterVo.builder()
                    .clusterName(clusterName)
                    .dbCategory(clusterInDB.getDbCategory())
//                    .shards(shards)
                    .build();
            titanSyncService.addTitanKeysAsync(cluster, Constants.ENV);

            return ResponseModel.successResponse();

        } catch (Exception e) {
            log.error("Add cluster shard failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseModel update() {
        return null;
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseModel query(@RequestParam(name = "name") long name) {
        return null;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ResponseModel delete(@RequestParam(name = "name") long name) {
        return null;
    }
}
