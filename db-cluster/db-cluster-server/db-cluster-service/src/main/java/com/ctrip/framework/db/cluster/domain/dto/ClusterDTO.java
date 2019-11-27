package com.ctrip.framework.db.cluster.domain.dto;

import com.ctrip.framework.db.cluster.entity.TableConfig;
import com.ctrip.framework.db.cluster.entity.enums.ClusterType;
import com.ctrip.framework.db.cluster.entity.enums.Enabled;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.create.TableConfigVo;
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
public class ClusterDTO {

    // cluster info
    private Integer clusterEntityId;

    private String clusterName;

    private Integer type;

    private String zoneId;

    private Integer unitStrategyId;

    private String unitStrategyName;

    private String shardStrategies;

    private String idGenerators;

    private String dbCategory;

    private Integer clusterEnabled;

    private Integer clusterDeleted;

    private Timestamp clusterCreateTime;

    private Timestamp clusterReleaseTime;

    private Integer clusterReleaseVersion;

    private Timestamp clusterUpdateTime;

    // Cascade info
    private List<ZoneDTO> zones;

    private List<TableConfig> tableConfigs;


    public ClusterVo toVo() {
        final ClusterType clusterType = ClusterType.getType(type);

        return ClusterVo.builder()
                .clusterName(clusterName)
                .type(clusterType.getName())
                .zoneId(zoneId)
                .unitStrategyId(ClusterType.drc.equals(clusterType) ? unitStrategyId : null)
                .unitStrategyName(ClusterType.drc.equals(clusterType) ? unitStrategyName : null)
                .dbCategory(dbCategory)
                .enabled(Enabled.getEnabled(clusterEnabled).convertToBoolean())
                .zones(
                        CollectionUtils.isEmpty(zones) ?
                                Lists.newArrayList() :
                                zones.stream().map(ZoneDTO::toVo).collect(Collectors.toList())
                )
                .tableConfigs(
                        CollectionUtils.isEmpty(tableConfigs) ?
                                Lists.newArrayList() :
                                tableConfigs.stream().map(
                                        tableConfig -> TableConfigVo.builder()
                                                .tableName(tableConfig.getTableName())
                                                .unitShardColumn(tableConfig.getUnitShardColumn())
                                                .build()
                                ).collect(Collectors.toList())
                )
                .build();
    }
}
