package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.enums.ClusterExtensionConfigType;
import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.enums.ResponseStatus;
import com.ctrip.framework.db.cluster.service.repository.ClusterExtensionConfigService;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterExtensionConfigVo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
                                               @RequestBody ClusterExtensionConfigVo[] addedConfigs) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);
            final List<ClusterExtensionConfigVo> configs = Lists.newArrayList(addedConfigs);

            // valid
            valid(configs);

            // cluster exists
            final Cluster cluster = clusterService.findCluster(clusterName, Deleted.un_deleted, null);
            Preconditions.checkArgument(null != cluster, "cluster does not exists.");

            // save
            clusterExtensionConfigService.create(cluster.getId(), configs);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("create extension configs success.");
            return response;
        } catch (Exception e) {
            log.error("create extension configs error.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    private void valid(final List<ClusterExtensionConfigVo> addedConfigs) {
        final List<String> allTypes = Lists.newArrayList(ClusterExtensionConfigType.values())
                .stream().map(ClusterExtensionConfigType::getName).collect(Collectors.toList());

        addedConfigs.forEach(config -> {
            // content can't be empty
            Preconditions.checkArgument(StringUtils.isNoneBlank(config.getContent()), "config content can't be empty.");

            // type name is correct
            Preconditions.checkArgument(allTypes.contains(config.getTypeName()));
        });
    }
}
