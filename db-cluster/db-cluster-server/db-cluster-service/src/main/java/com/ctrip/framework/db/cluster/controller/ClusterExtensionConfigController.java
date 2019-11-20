package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.domain.dto.ClusterDTO;
import com.ctrip.framework.db.cluster.entity.ClusterExtensionConfig;
import com.ctrip.framework.db.cluster.entity.enums.ClusterExtensionConfigType;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.vo.ResponseStatus;
import com.ctrip.framework.db.cluster.service.repository.ClusterExtensionConfigService;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterExtensionConfigVo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by @author zhuYongMing on 2019/11/5.
 */
@Slf4j
@RestController
@RequestMapping("api/dal/v1")
@AllArgsConstructor
public class ClusterExtensionConfigController {

    private final ClusterService clusterService;

    private final ClusterExtensionConfigService clusterExtensionConfigService;


    @PostMapping(value = "/clusters/{clusterName}/extensionConfigs")
    public ResponseModel createExtensionConfig(@PathVariable String clusterName,
                                               @RequestParam(name = "operator") String operator,
                                               @RequestBody ClusterExtensionConfigVo configVo) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);

            // valid
            configVo.valid();

            // cluster exists
            final ClusterDTO cluster = clusterService.findUnDeletedClusterDTO(clusterName);
            Preconditions.checkArgument(null != cluster, "cluster does not exists.");

            final List<ClusterExtensionConfig> existsConfigs = cluster.getConfigs();

            final List<ClusterExtensionConfig> addedConfigs = Lists.newArrayList();
            final List<ClusterExtensionConfig> updatedConfigs = Lists.newArrayList();

            final String shardStrategies = configVo.getShardStrategies();
            if (null != shardStrategies) {
                final Optional<ClusterExtensionConfig> existsShardStrategies = existsConfigs.stream()
                        .filter(existsConfig ->
                                ClusterExtensionConfigType.shards_strategies.getCode() == existsConfig.getType()
                        ).findFirst();
                if (existsShardStrategies.isPresent()) {
                    final ClusterExtensionConfig updated = ClusterExtensionConfig.builder()
                            .id(existsShardStrategies.get().getId())
                            .content(shardStrategies)
                            .build();
                    updatedConfigs.add(updated);
                } else {
                    final ClusterExtensionConfig added = ClusterExtensionConfig.builder()
                            .clusterId(cluster.getClusterEntityId())
                            .content(shardStrategies)
                            .type(ClusterExtensionConfigType.shards_strategies.getCode())
                            .deleted(Deleted.un_deleted.getCode())
                            .build();
                    addedConfigs.add(added);
                }
            }

            final String idGenerators = configVo.getIdGenerators();
            if (null != idGenerators) {
                final Optional<ClusterExtensionConfig> existsIdGenerators = existsConfigs.stream()
                        .filter(existsConfig ->
                                ClusterExtensionConfigType.id_generators.getCode() == existsConfig.getType()
                        ).findFirst();
                if (existsIdGenerators.isPresent()) {
                    final ClusterExtensionConfig updated = ClusterExtensionConfig.builder()
                            .id(existsIdGenerators.get().getId())
                            .content(idGenerators)
                            .build();
                    updatedConfigs.add(updated);
                } else {
                    final ClusterExtensionConfig added = ClusterExtensionConfig.builder()
                            .clusterId(cluster.getClusterEntityId())
                            .content(idGenerators)
                            .type(ClusterExtensionConfigType.id_generators.getCode())
                            .deleted(Deleted.un_deleted.getCode())
                            .build();
                    addedConfigs.add(added);
                }
            }

            if (!CollectionUtils.isEmpty(addedConfigs)) {
                clusterExtensionConfigService.create(addedConfigs);
            }

            if (!CollectionUtils.isEmpty(updatedConfigs)) {
                clusterExtensionConfigService.update(updatedConfigs);
            }

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("create extension configs success.");
            return response;
        } catch (Exception e) {
            log.error("create extension configs error.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }
}
