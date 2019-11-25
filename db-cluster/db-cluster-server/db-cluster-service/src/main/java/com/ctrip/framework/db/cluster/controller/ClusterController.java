package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.domain.dto.ClusterDTO;
import com.ctrip.framework.db.cluster.domain.dto.UserDTO;
import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.enums.Enabled;
import com.ctrip.framework.db.cluster.enums.ResponseStatus;
import com.ctrip.framework.db.cluster.service.checker.SiteAccessChecker;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.IpUtils;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.switches.ClusterSwitchesVo;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by shenjie on 2019/3/5.
 */
@Slf4j
@RestController
@RequestMapping("api/dal/v1")
@RequiredArgsConstructor
public class ClusterController {

    private final SiteAccessChecker siteAccessChecker;

    private final ClusterService clusterService;

    private final RegexMatcher regexMatcher;

    private final CipherService cipherService;


    @PostMapping(value = "/clusters")
    public ResponseModel createCluster(@RequestBody final ClusterVo cluster,
                                       @RequestParam(name = "operator") String operator,
                                       final HttpServletRequest request) {

        try {
            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            // argument valid
            cluster.valid(regexMatcher);

            // argument correct
            cluster.correct();

            // create cluster
            clusterService.createCluster(cluster.toDTO());

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("Create cluster success");
            return response;

        } catch (Exception e) {
            log.error(String.format("create dal cluster info failed, clusterVo =  %s", cluster.toString()), e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/clusters/{clusterName}")
    public ResponseModel queryCluster(@PathVariable String clusterName,
                                      @RequestParam(name = "operator") String operator,
                                      @RequestParam(name = "effective", required = false, defaultValue = "true") Boolean effective,
                                      HttpServletRequest request) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);

            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                String ip = IpUtils.getRequestIp(request);
                return ResponseModel.forbiddenResponse(String.format("Ip address is not in the whitelist, ip = %s", ip));
            }

            final ClusterDTO clusterDTO;
            if (effective) {
                clusterDTO = clusterService.findEffectiveClusterDTO(clusterName);
            } else {
                clusterDTO = clusterService.findUnDeletedClusterDTO(clusterName);
            }

            if (null == clusterDTO) {
                ResponseModel response = ResponseModel.successResponse();
                response.setMessage(String.format("cluster does not exists, clusterName = %s", clusterName));
                return response;
            }

            clusterDTO.getZones().forEach(
                    zone -> zone.getShards().forEach(
                            shard -> {
                                final List<UserDTO> users = shard.getUsers();
                                if (!CollectionUtils.isEmpty(users)) {
                                    users.forEach(
                                            user -> user.setUsername(cipherService.decrypt(user.getUsername()))
                                    );
                                }
                            }
                    )
            );
            ResponseModel response = ResponseModel.successResponse(clusterDTO.toVo());
            response.setMessage("Query cluster success");
            return response;
        } catch (Exception e) {
            log.error(String.format("Query cluster failed, clusterName = %s .", clusterName), e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/clusters")
    public ResponseModel queryClusters(@RequestParam(name = "operator") String operator,
                                       @RequestParam(name = "effective", required = false, defaultValue = "true") Boolean effective,
                                       HttpServletRequest request) {

        try {
            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                String ip = IpUtils.getRequestIp(request);
                return ResponseModel.forbiddenResponse(String.format("Ip address is not in the whitelist, ip = %s", ip));
            }

            final List<Cluster> clusters;
            if (effective) {
                clusters = clusterService.findClusters(
                        null, Deleted.un_deleted, Enabled.enabled
                );
            } else {
                clusters = clusterService.findClusters(
                        null, Deleted.un_deleted, null
                );
            }

            final List<ClusterVo> clusterVos = clusters.stream().map(
                    cluster -> ClusterVo.builder()
                            .clusterName(cluster.getClusterName())
                            .dbCategory(cluster.getDbCategory())
                            .enabled(Enabled.getEnabled(cluster.getEnabled()).convertToBoolean())
                            .build()
            ).collect(Collectors.toList());

            ResponseModel response = ResponseModel.successResponse(clusterVos);
            response.setMessage("Query clusters success");
            return response;

        } catch (Exception e) {
            log.error("Query clusters failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @PostMapping(value = "/clusters/{clusterName}/releases")
    public ResponseModel releaseCluster(@PathVariable String clusterName,
                                        @RequestParam(name = "operator") String operator,
                                        HttpServletRequest request) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);

            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            clusterService.release(Lists.newArrayList(clusterName), operator, Constants.RELEASE_TYPE_NORMAL_RELEASE);
            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("Release cluster success");

            return response;
        } catch (Exception e) {
            log.error(String.format("Release cluster failed, clusterName = %s .", clusterName), e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @PostMapping(value = "/clusters/switches")
    public ResponseModel switchClusters(@RequestBody ClusterSwitchesVo[] clusterArray,
                                        @RequestParam(name = "operator") String operator,
                                        HttpServletRequest request) {

        try {
            final List<ClusterSwitchesVo> clusterSwitchesVos = Lists.newArrayList(clusterArray);

            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            // switch
            clusterService.switches(clusterSwitchesVos, operator);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("Switch cluster success");
            return response;
        } catch (Exception e) {
            log.error("Switch cluster failed", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    // deprecated
//    @RequestMapping(value = "/add", method = RequestMethod.POST)
//    public ResponseModel releaseClusters(@RequestBody ClusterVo cluster,
//                                    @RequestParam(name = "operator", required = false) String operator,
//                                    HttpServletRequest request) {
//        try {
//            Preconditions.checkArgument(StringUtils.isNotBlank(operator), "operator参数为空");
//            if (!siteAccessChecker.isAllowed(request)) {
//                return ResponseModel.forbiddenResponse();
//            }
//
//            // check cluster
//            dalClusterValidityChecker.checkCluster(cluster, operator);
//
//            // createClusterSets cluster to db
//            dalClusterManager.releaseClusters(cluster);
//
//            // sync titanKeys to plugin
//            titanSyncService.addTitanKeysAsync(cluster, Constants.ENV);
//
//            return ResponseModel.successResponse();
//
//        } catch (Exception e) {
//            log.error("Add dal cluster info failed.", e);
//            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "/release", method = RequestMethod.GET)
//    public ResponseModel releaseAll(@RequestParam(name = "clustername", required = false) String clusterName,
//                                    @RequestParam(name = "operator", required = false) String operator,
//                                    HttpServletRequest request) {
//        try {
//            Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clustername参数为空");
//            Preconditions.checkArgument(StringUtils.isNotBlank(operator), "operator参数为空");
//            if (!siteAccessChecker.isAllowed(request)) {
//                return ResponseModel.forbiddenResponse();
//            }
//
//            dalClusterReleaseService.addClusters(clusterName, Constants.ENV, operator);
//            return ResponseModel.successResponse();
//
//        } catch (Exception e) {
//            log.error("Release dal cluster failed.", e);
//            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
//        }
//    }

//    @RequestMapping(value = "/switch", method = RequestMethod.POST)
//    public ResponseModel switchCluster(@RequestBody DatabaseGroupVo databases,
//                                       @RequestParam(name = "operator", required = false) String operator,
//                                       HttpServletRequest request) {
//        try {
//            Preconditions.checkArgument(StringUtils.isNotBlank(operator), "operator参数为空");
//            if (!siteAccessChecker.isAllowed(request)) {
//                return ResponseModel.forbiddenResponse();
//            }
//
//            // check databases
//            dalClusterValidityChecker.checkDatabases(databases);
//
//            String env = Constants.ENV;
//            // update cluster(db, qconfig)
//            List<ShardVo> shards = databases.getDatabases();
//            updateAndReleaseCluster(shards, env, operator);
//
//            // update titanKeys to qconfig
//            titanSyncService.updateTitanKeysAsync(shards, env, operator);
//
//            return ResponseModel.successResponse();
//
//        } catch (Exception e) {
//            log.error("Switch dal cluster failed.", e);
//            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
//        }
//    }

//    @RequestMapping(value = "/info", method = RequestMethod.GET)
//    public ResponseModel get(@RequestParam(name = "clustername", required = false) String clusterName,
//                             HttpServletRequest request) {
//        try {
//            Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clustername参数为空");
//            if (!siteAccessChecker.isAllowed(request)) {
//                return ResponseModel.forbiddenResponse();
//            }
//
//            clusterName = Utils.format(clusterName);
//            ClusterVo cluster = dalClusterManager.getCluster(clusterName);
//            SecurityUtil.encryptPassword(cluster);
//
//            return ResponseModel.successResponse(cluster);
//
//        } catch (Exception e) {
//            log.error("Get dal cluster failed.", e);
//            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
//        }
//    }

//    @RequestMapping(value = "/delete", method = RequestMethod.GET)
//    public ResponseModel delete(@RequestParam(name = "name") String name) {
//        return null;
//    }

//    private void updateAndReleaseCluster(List<ShardVo> shards, String env, String operator) throws SQLException {
//        // update cluster shards in db
//        DalClient client = DalClientFactory.getClient(Constants.DATABASE_SET_NAME);
//        List<DalCommand> dalCommands = Lists.newArrayList();
//        dalCommands.add(new DalCommand() {
//            public boolean execute(DalClient client) throws SQLException {
//                dalClusterManager.updateShards(shards);
//                return true;
//            }
//        });
//
//        // update clusters to plugin
//        dalCommands.add(new DalCommand() {
//            public boolean execute(DalClient client) throws SQLException {
//                dalClusterReleaseService.updateClusters(shards, env, operator);
//                return true;
//            }
//        });
//        DalHints hints = new DalHints();
//        client.execute(dalCommands, hints);
//    }
}
