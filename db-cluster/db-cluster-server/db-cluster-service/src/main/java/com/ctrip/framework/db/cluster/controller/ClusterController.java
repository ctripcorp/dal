package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.domain.dto.ClusterDTO;
import com.ctrip.framework.db.cluster.domain.dto.UserDTO;
import com.ctrip.framework.db.cluster.domain.dto.ZoneDTO;
import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.entity.enums.ClusterType;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.vo.ResponseStatus;
import com.ctrip.framework.db.cluster.service.checker.SiteAccessChecker;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.util.IpUtils;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterConfigVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.switches.ClusterSwitchesVo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
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

            // The same user between multiple zones, so get first zone
            final Optional<ZoneDTO> firstZone = clusterDTO.getZones().stream().findFirst();
            firstZone.ifPresent(zoneDTO -> zoneDTO.getShards().forEach(
                    shard -> {
                        final List<UserDTO> users = shard.getUsers();
                        if (!CollectionUtils.isEmpty(users)) {
                            users.forEach(
                                    user -> user.setUsername(cipherService.decrypt(user.getUsername()))
                            );
                        }
                    })
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
                            .type(ClusterType.getType(cluster.getType()).getName())
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
                                        @RequestParam(name = "releaseZoneId", required = false) String releaseZoneId,
                                        @RequestParam(name = "operator") String operator,
                                        HttpServletRequest request) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);
            releaseZoneId = Utils.format(releaseZoneId);

            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            // release
            clusterService.assignZoneRelease(
                    releaseZoneId, clusterName, operator, Constants.RELEASE_TYPE_NORMAL_RELEASE)
            ;

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

    @PutMapping(value = "/clusters/{clusterName}/types/drc")
    public ResponseModel transformToDrc(@PathVariable String clusterName,
                                        @RequestParam(name = "operator") String operator,
                                        HttpServletRequest request) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);

            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            clusterService.transformToDrc(clusterName, operator);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("Transform to drc cluster success");
            return response;
        } catch (Exception e) {
            log.error("Transform to drc type cluster", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @PutMapping(value = "/clusters/{clusterName}/types/normal")
    public ResponseModel transformToNormal(@PathVariable String clusterName,
                                           @RequestParam(name = "releaseZoneId") String releaseZoneId,
                                           @RequestParam(name = "operator") String operator,
                                           HttpServletRequest request) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);
            releaseZoneId = Utils.format(releaseZoneId);

            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            clusterService.transformToNormal(clusterName, releaseZoneId, operator);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("Transform to normal cluster success");
            return response;
        } catch (Exception e) {
            log.error("Transform to normal cluster failed", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @PostMapping(value = "/clusters/{clusterName}/configs")
    public ResponseModel createClusterConfig(@PathVariable String clusterName,
                                             @RequestParam(name = "operator") String operator,
                                             @RequestBody ClusterConfigVo configVo) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);
            // valid
            configVo.valid();
            // cluster exists
            final Cluster cluster = clusterService.findCluster(clusterName, Deleted.un_deleted, Enabled.enabled);
            Preconditions.checkArgument(null != cluster, "cluster does not exists.");

            final String shardStrategies = configVo.getShardStrategies();
            final String idGenerators = configVo.getIdGenerators();
            final Integer unitStrategyId = configVo.getUnitStrategyId();
            final String unitStrategyName = configVo.getUnitStrategyName();

            // if exists, update it.
            final Cluster updateCluster = Cluster.builder()
                    .id(cluster.getId())
                    .shardStrategies(StringUtils.isNotBlank(shardStrategies) ? shardStrategies : null)
                    .idGenerators(StringUtils.isNotBlank(idGenerators) ? idGenerators : null)
                    .unitStrategyId(unitStrategyId != null && unitStrategyId != 0 ? unitStrategyId : null)
                    .unitStrategyName(StringUtils.isNotBlank(unitStrategyName) ? unitStrategyName : null)
                    .build();
            clusterService.updateCluster(updateCluster);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("create cluster configs success.");
            return response;
        } catch (Exception e) {
            log.error("create cluster configs error.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }
}
