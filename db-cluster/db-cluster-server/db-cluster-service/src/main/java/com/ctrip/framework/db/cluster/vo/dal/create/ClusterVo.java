package com.ctrip.framework.db.cluster.vo.dal.create;

import com.ctrip.framework.db.cluster.domain.dto.ClusterDTO;
import com.ctrip.framework.db.cluster.util.RegexMatcher;
import com.ctrip.framework.db.cluster.util.Utils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.ctrip.framework.db.cluster.util.Constants.MYSQL_DB;
import static com.ctrip.framework.db.cluster.util.Constants.SQL_SERVER_DB;

/**
 * Created by shenjie on 2019/3/5.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterVo {

    private String clusterName;

    private String dbCategory;

    private Boolean enabled;

    private List<ZoneVo> zones;

    public List<ShardVo> deprGetShards() {
        return null;
    }


    public void valid(final RegexMatcher regexMatcher) {
        // clusterName
        Preconditions.checkArgument(StringUtils.isNotBlank(this.clusterName), "clusterName不允许为空.");
        Preconditions.checkArgument(regexMatcher.clusterName(this.clusterName), "clusterName不合法.");

        // dbCategory
        Preconditions.checkArgument(
                getDbCategoryLegalSet().contains(dbCategory), "dbCategory只允许为mysql或sqlServer."
        );

        if (!CollectionUtils.isEmpty(zones)) {
            // zoneId
            Preconditions.checkArgument(
                    zones.stream().map(zoneVo -> Utils.format(zoneVo.getZoneId())).distinct().count() == zones.size(),
                    "zoneId不允许相同, zoneId比较是否相同不区分大小写."
            );

            // TODO: 2019/10/30 临时:zone size == 1
            Preconditions.checkArgument(
                    zones.size() == 1, "目前一个集群中只能存在1个zone."
            );

            // zones
            zones.forEach(zoneVo -> zoneVo.valid(regexMatcher));
        }
    }

    private List<String> getDbCategoryLegalSet() {
        return Lists.newArrayList(null, "", MYSQL_DB, SQL_SERVER_DB);
    }

    public void correct() {
        // lower case
        this.clusterName = Utils.format(this.clusterName);

        // db category default mysql
        if (StringUtils.isBlank(this.dbCategory)) {
            this.dbCategory = MYSQL_DB;
        }

        // zones
        if (!CollectionUtils.isEmpty(zones)) {
            zones.forEach(ZoneVo::correct);
        }
    }

    public ClusterDTO toDTO() {
        return ClusterDTO.builder()
                .clusterName(this.clusterName)
                .dbCategory(this.dbCategory)
                .zones(CollectionUtils.isEmpty(zones) ? null : zones.stream().map(ZoneVo::toDTO).collect(Collectors.toList()))
                .build();
    }
}
