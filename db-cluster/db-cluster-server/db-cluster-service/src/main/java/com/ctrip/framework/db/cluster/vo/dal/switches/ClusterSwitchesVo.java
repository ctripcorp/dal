package com.ctrip.framework.db.cluster.vo.dal.switches;

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

/**
 * Created by @author zhuYongMing on 2019/10/28.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterSwitchesVo {

    private String clusterName;

    private String zoneId;

    private List<ShardSwitchesVo> shards;


    public void valid(final RegexMatcher regexMatcher) {
        // clusterName
        Preconditions.checkArgument(StringUtils.isNotBlank(this.clusterName), "clusterName不允许为空.");
        Preconditions.checkArgument(regexMatcher.clusterName(this.clusterName), "clusterName不合法.");

        // zoneId
        Preconditions.checkArgument(StringUtils.isNotBlank(zoneId), "zoneId不允许为空.");

        // shards
        Preconditions.checkArgument(!CollectionUtils.isEmpty(shards), String.format("shards不允许为空, clusterName = %s", clusterName));
        shards.forEach(shard -> shard.valid(regexMatcher));
    }

    public void correct() {
        // lower case
        // clusterName
        clusterName = Utils.format(clusterName);
        // zoneId
        this.zoneId = Utils.format(this.zoneId);

        shards.forEach(ShardSwitchesVo::correct);
    }
}
