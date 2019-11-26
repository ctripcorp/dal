package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.entity.TableConfig;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.TableConfigService;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.ResponseStatus;
import com.ctrip.framework.db.cluster.vo.dal.create.TableConfigVo;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by @author zhuYongMing on 2019/11/26.
 */
@Slf4j
@RestController
@RequestMapping("api/dal/v1")
@RequiredArgsConstructor
public class TableConfigController {

    private final ClusterService clusterService;

    private final TableConfigService tableConfigService;


    @PostMapping(value = "/clusters/{clusterName}/tableConfigs")
    public ResponseModel createTableConfig(@PathVariable String clusterName,
                                           @RequestBody TableConfigVo[] tableConfigVos,
                                           @RequestParam(name = "operator") String operator) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);

            // valid parameter
            final List<TableConfigVo> addedTableConfigs = Lists.newArrayList(tableConfigVos);
            addedTableConfigs.forEach(TableConfigVo::valid);

            // correct parameter
            addedTableConfigs.forEach(TableConfigVo::correct);

            final Cluster cluster = clusterService.findCluster(clusterName, Deleted.un_deleted, Enabled.enabled);
            if (null == cluster) {
                throw new IllegalArgumentException("cluster does not exists.");
            }

            final Integer clusterId = cluster.getId();
            final List<TableConfig> tableConfigs = tableConfigService.findTableConfigs(
                    clusterId, Deleted.un_deleted,
                    addedTableConfigs.stream().map(TableConfigVo::getTableName).collect(Collectors.toList())
            );
            if (!CollectionUtils.isEmpty(tableConfigs)) {
                throw new IllegalArgumentException("some table config already exists, can't create again.");
            }

            final List<TableConfig> added = Lists.newArrayListWithExpectedSize(addedTableConfigs.size());
            addedTableConfigs.forEach(addedTableConfigVo -> {
                final TableConfig tableConfig = TableConfig.builder()
                        .clusterId(clusterId)
                        .tableName(addedTableConfigVo.getTableName())
                        .unitShardColumn(addedTableConfigVo.getUnitShardColumn())
                        .build();
                added.add(tableConfig);
            });

            // save
            tableConfigService.createTableConfigs(added);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("create table configs success.");
            return response;
        } catch (Exception e) {
            log.error("create table configs error.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

//    @GetMapping(value = "/clusters/{clusterName}/tableConfigs")
//    public ResponseModel queryDrcConfig(@PathVariable String clusterName,
//                                        @RequestParam(name = "operator") String operator) {
//
//        try {
//            // format parameter
//            clusterName = Utils.format(clusterName);
//
//            // query
//            final Cluster cluster = clusterService.findCluster(clusterName, Deleted.un_deleted, Enabled.enabled);
//            if (null == cluster) {
//                throw new IllegalArgumentException("cluster does not exists.");
//            }
//
//            final Integer clusterId = cluster.getId();
//            final List<TableConfig> tableConfigs = tableConfigService.findTableConfigs(
//                    clusterId, Deleted.un_deleted, null
//            );
//
//            // result
//            final Map<String, Object> result = Maps.newHashMap();
//            final ClusterType type = ClusterType.getType(cluster.getType());
//            result.put("clusterType", type.getName());
//            if (ClusterType.drc.equals(type) && !CollectionUtils.isEmpty(tableConfigs)) {
//                final List<TableConfigVo> tableConfigVos = Lists.newArrayListWithExpectedSize(tableConfigs.size());
//                tableConfigs.forEach(tableConfig -> {
//                    final TableConfigVo vo = TableConfigVo.builder()
//                            .tableName(tableConfig.getTableName())
//                            .unitShardColumn(tableConfig.getUnitShardColumn())
//                            .build();
//                    tableConfigVos.add(vo);
//                });
//                result.put("configs", tableConfigVos);
//            }
//
//            ResponseModel response = ResponseModel.successResponse(result);
//            response.setMessage("query drc configs success.");
//            return response;
//        } catch (Exception e) {
//            log.error("query drc configs failed.", e);
//            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
//        }
//    }
}
