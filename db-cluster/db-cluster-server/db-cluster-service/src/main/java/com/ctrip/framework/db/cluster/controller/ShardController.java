package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.enums.Enabled;
import com.ctrip.framework.db.cluster.enums.ResponseStatus;
import com.ctrip.framework.db.cluster.service.DalClusterManager;
import com.ctrip.framework.db.cluster.service.TitanSyncService;
import com.ctrip.framework.db.cluster.service.checker.DalClusterValidityChecker;
import com.ctrip.framework.db.cluster.service.checker.SiteAccessChecker;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by shenjie on 2019/3/14.
 */
@Slf4j
@RestController
@RequestMapping("dal/cluster/shard")
public class ShardController {

    @Autowired
    private SiteAccessChecker siteAccessChecker;
    @Autowired
    private DalClusterManager dalClusterManager;
    @Autowired
    private TitanSyncService titanSyncService;
    @Autowired
    private DalClusterValidityChecker dalClusterValidityChecker;
    @Autowired
    private ClusterService clusterService;

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
                    clusterName, Lists.newArrayList(Deleted.un_deleted),
                    Lists.newArrayList(Enabled.enabled, Enabled.un_enabled)
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
