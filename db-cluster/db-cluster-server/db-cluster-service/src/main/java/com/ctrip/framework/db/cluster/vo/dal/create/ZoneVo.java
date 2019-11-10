package com.ctrip.framework.db.cluster.vo.dal.create;

import com.ctrip.framework.db.cluster.domain.dto.ZoneDTO;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author c7ch23en
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneVo {

    private String zoneId;

    private Boolean enabled;

    private List<ShardVo> shards;


    public void valid(final RegexMatcher regexMatcher) {
        // zoneId
        Preconditions.checkArgument(StringUtils.isNotBlank(zoneId), "zoneId不允许为空.");

        // shards
        if (!CollectionUtils.isEmpty(shards)) {
            // shardIndex
            Preconditions.checkArgument(
                    shards.stream().map(ShardVo::getShardIndex).distinct().count() == shards.size(),
                    String.format("同一zone下, shardIndex不允许相同, zoneId = %s", zoneId));

            // dbName
            Preconditions.checkArgument(
                    shards.stream().map(shardVo -> Utils.format(shardVo.getDbName())).distinct().count() == shards.size(),
                    String.format("同一zone下, dbName不允许相同, dbName比较是否相同不区分大小写, zoneId = %s.", zoneId)
            );

            // TODO: 2019/10/30 临时: shards size == 1, 且shardIndex = 0
            Preconditions.checkArgument(
                    shards.size() == 1 && 0 == shards.get(0).getShardIndex(),
                    String.format("目前每个zone内只允许存在1个shard, 且shardIndex = 0, zoneId = %s", zoneId)
            );

            shards.forEach(shardVo -> shardVo.valid(regexMatcher));
        }
    }

    public void correct() {
        // zoneId
        this.zoneId = Utils.format(this.zoneId);

        // shards
        if (!CollectionUtils.isEmpty(shards)) {
            shards.forEach(ShardVo::correct);
        }
    }

    public ZoneDTO toDTO() {
        return ZoneDTO.builder()
                .zoneId(this.zoneId)
                .shards(CollectionUtils.isEmpty(shards) ? null : shards.stream().map(ShardVo::toDTO).collect(Collectors.toList()))
                .build();
    }
}
