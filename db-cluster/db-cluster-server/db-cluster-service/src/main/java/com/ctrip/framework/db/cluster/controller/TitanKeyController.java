package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.entity.Shard;
import com.ctrip.framework.db.cluster.entity.TitanKey;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.ShardService;
import com.ctrip.framework.db.cluster.service.repository.TitanKeyService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.ResponseStatus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by @author zhuYongMing on 2019/11/27.
 */
@Slf4j
@RestController
@RequestMapping("api/dal/v1")
@RequiredArgsConstructor
public class TitanKeyController {

    private final TitanKeyService titanKeyService;

    private final ShardService shardService;

    private final ClusterService clusterService;


    @GetMapping(value = "/titanKeys/{titanKeyName}")
    public ResponseModel query(@PathVariable String titanKeyName,
                               @RequestParam(name = "operator") String operator) {

        try {
            // format parameter
            titanKeyName = Utils.format(titanKeyName);
            final List<TitanKey> titanKeys = titanKeyService.queryByNamesAndSubEnv(
                    Lists.newArrayList(titanKeyName), ""
            );

            final Map<String, Object> result = Maps.newHashMap();
            if (!CollectionUtils.isEmpty(titanKeys)) {
                // if exists, size == 1
                final TitanKey titanKey = titanKeys.get(0);

                final String domain = titanKey.getDomain();
                final List<Shard> shards = shardService.findByAllDomain(domain, Deleted.un_deleted);
                if (!CollectionUtils.isEmpty(shards)) {
                    // if exists, size == 1
                    final Shard shard = shards.get(0);
                    final Cluster queryCluster = Cluster.builder()
                            .id(shard.getClusterId())
                            .deleted(Deleted.un_deleted.getCode())
                            .enabled(Enabled.enabled.getCode())
                            .build();

                    final List<Cluster> clusters = clusterService.findCluster(queryCluster);
                    if (!CollectionUtils.isEmpty(clusters)) {
                        // if exists, size == 1
                        final Cluster cluster = clusters.get(0);
                        result.put("clusterName", cluster.getClusterName());
                        result.put("shardIndex", shard.getShardIndex());
                        if (domain.equalsIgnoreCase(shard.getMasterDomain())) {
                            result.put("role", Constants.ROLE_MASTER);
                        } else if (domain.equalsIgnoreCase(shard.getSlaveDomain())) {
                            result.put("role", Constants.ROLE_SLAVE);
                        } else {
                            result.put("role", Constants.ROLE_READ);
                        }
                    }
                }
            }

            ResponseModel response = ResponseModel.successResponse(result);
            response.setMessage("query by titanKey success.");
            return response;
        } catch (Exception e) {
            log.error("query by titanKey error.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }
}
