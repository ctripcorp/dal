package com.ctrip.framework.db.cluster.domain.dto;

import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.framework.db.cluster.vo.dal.create.ZoneVo;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by @author zhuYongMing on 2019/10/27.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDTO {

    // zone info
    private Integer zoneEntityId;

    private Integer clusterEntityId;

    private String zoneId;

    private String region;

    private Integer zoneEnabled;

    private Integer zoneDeleted;

    private Timestamp zoneCreateTime;

    private Timestamp zoneUpdateTime;


    // Cascade info
    private List<ShardDTO> shards;


    // Redundant info
//    private String clusterName;


    public ZoneVo toVo() {
        return ZoneVo.builder()
                .zoneId(zoneId)
                .enabled(Enabled.getEnabled(zoneEnabled).convertToBoolean())
                .shards(CollectionUtils.isEmpty(shards) ? Lists.newArrayList() : shards.stream().map(ShardDTO::toVo).collect(Collectors.toList()))
                .build();
    }
}
