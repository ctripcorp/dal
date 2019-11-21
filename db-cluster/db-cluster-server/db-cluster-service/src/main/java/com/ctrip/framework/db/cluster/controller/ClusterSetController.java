package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.domain.dto.ClusterDTO;
import com.ctrip.framework.db.cluster.domain.dto.ZoneDTO;
import com.ctrip.framework.db.cluster.entity.ClusterSet;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import com.ctrip.framework.db.cluster.service.checker.SiteAccessChecker;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.ClusterSetService;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.ResponseStatus;
import com.ctrip.framework.db.cluster.vo.dal.create.ZoneVo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by @author zhuYongMing on 2019/11/19.
 */
@Slf4j
@RestController
@RequestMapping("api/dal/v1")
@RequiredArgsConstructor
public class ClusterSetController {

    private final SiteAccessChecker siteAccessChecker;

    private final RegexMatcher regexMatcher;

    private final ClusterService clusterService;

    private final ClusterSetService clusterSetService;


    @PostMapping(value = "clusters/{clusterName}/zones")
    public ResponseModel createZones(@PathVariable String clusterName,
                                     @RequestBody final ZoneVo[] zoneVos,
                                     @RequestParam(name = "operator") final String operator,
                                     HttpServletRequest request) {
        try {
            // format parameter
            clusterName = Utils.format(clusterName);

            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            final List<ZoneVo> addedZones = Lists.newArrayList(zoneVos);
            // parameter valid
            addedZonesValid(addedZones);

            // cluster exists valid
            final ClusterDTO clusterDTO = clusterService.findUnDeletedClusterDTO(clusterName);
            clusterExistsValid(clusterDTO);

            // zone duplicated valid
            zoneDuplicatedValid(clusterDTO, addedZones);

            // save
            clusterSetService.createClusterSets(converterToDTO(addedZones, clusterDTO.getClusterEntityId()));

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("add zones success");
            return response;
        } catch (Exception e) {
            log.error("add zones failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @DeleteMapping(value = "clusters/{clusterName}/zones")
    public ResponseModel deleteZones(@PathVariable String clusterName,
                                     @RequestBody final String[] zoneIds,
                                     @RequestParam(name = "operator") final String operator,
                                     HttpServletRequest request) {

        try {
            // format parameter
            clusterName = Utils.format(clusterName);
            final List<String> deletedZoneIds = Lists.newArrayList(zoneIds)
                    .stream().map(Utils::format).collect(Collectors.toList());

            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            // cluster exists valid
            final ClusterDTO clusterDTO = clusterService.findUnDeletedClusterDTO(clusterName);
            clusterExistsValid(clusterDTO);

            // deleted zoneIds does not exists
            final List<ZoneDTO> existsZones = clusterDTO.getZones();
            zoneNotExistsValid(existsZones, deletedZoneIds);

            // delete
            final List<ClusterSet> deletedClusterSets = Lists.newArrayListWithExpectedSize(deletedZoneIds.size());
            deletedZoneIds.forEach(deletedZoneId -> {
                final Integer clusterSetId = existsZones.stream()
                        .filter(existsZone -> existsZone.getZoneId().equals(deletedZoneId))
                        .map(ZoneDTO::getZoneEntityId).collect(Collectors.toList()).get(0);

                final ClusterSet deleted = ClusterSet.builder()
                        .id(clusterSetId)
                        .deleted(Deleted.deleted.getCode())
                        .build();
                deletedClusterSets.add(deleted);
            });
            clusterSetService.updateClusterSets(deletedClusterSets);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("delete zones success");
            return response;
        } catch (Exception e) {
            log.error("delete zones failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    private void addedZonesValid(final List<ZoneVo> addedZones) {
        // zones valid
        Preconditions.checkArgument(
                !CollectionUtils.isEmpty(addedZones), "Newly added zones are not allowed to be empty."
        );
        addedZones.forEach(zone -> zone.valid(regexMatcher));

        // correct
        addedZones.forEach(ZoneVo::correct);

        // zoneId repeated
        Preconditions.checkArgument(
                addedZones.stream().distinct().count() == addedZones.size(),
                "Newly added zoneId are not allowed to be repeated."
        );
    }

    private void clusterExistsValid(final ClusterDTO clusterDTO) {
        Preconditions.checkArgument(null != clusterDTO, "cluster does not exists.");
    }

    private void zoneDuplicatedValid(final ClusterDTO clusterDTO, final List<ZoneVo> addedZones) {
        final List<String> existsZoneIds = clusterDTO.getZones()
                .stream().map(ZoneDTO::getZoneId)
                .collect(Collectors.toList());

        addedZones.stream().map(ZoneVo::getZoneId).forEach(
                addedZoneId -> Preconditions.checkArgument(
                        existsZoneIds.contains(addedZoneId),
                        String.format("Newly zoneId %s and existing zoneId duplicated.", addedZoneId)
                )
        );
    }

    private List<ZoneDTO> converterToDTO(final List<ZoneVo> addedZones, final Integer clusterId) {
        return addedZones.stream().map(addedZoneVo -> {
            final ZoneDTO zoneDTO = addedZoneVo.toDTO();
            zoneDTO.setClusterEntityId(clusterId);
            return zoneDTO;
        }).collect(Collectors.toList());
    }

    private void zoneNotExistsValid(final List<ZoneDTO> existsZones, final List<String> deletedZoneIds) {
        if (CollectionUtils.isEmpty(existsZones)) {
            throw new IllegalArgumentException("All the zoneIds you want to deleted do not exists.");
        }

        deletedZoneIds.forEach(
                deleteZoneId -> Preconditions.checkArgument(
                        existsZones.stream().map(ZoneDTO::getZoneId).collect(Collectors.toList()).contains(deleteZoneId),
                        String.format("The zoneId %s you want to deleted do not exists.", deleteZoneId)
                )
        );
    }
}
