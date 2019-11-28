package com.ctrip.framework.db.cluster.vo.dal.create;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

/**
 * Created by @author zhuYongMing on 2019/11/5.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterConfigVo {

    private String shardStrategies;

    private String idGenerators;

    private Integer unitStrategyId;

    private String unitStrategyName;


    public void valid() {
        // shardStrategies
        if (null != shardStrategies) {
            Preconditions.checkArgument(!"".equals(shardStrategies), "if shardStrategies exists, shardStrategies can't be empty.");
        }

        // idGenerators
        if (null != idGenerators) {
            Preconditions.checkArgument(!"".equals(idGenerators), "if idGenerators exists, idGenerators can't be empty.");
        }

        // unitStrategyId
        if (null != unitStrategyId) {
            Preconditions.checkArgument(0 != unitStrategyId, "if unitStrategyName exists, unitStrategyName can't be zero.");
        }

        // unitStrategyName
        if (null != unitStrategyName) {
            Preconditions.checkArgument(!"".equals(unitStrategyName), "if unitStrategyName exists, unitStrategyName can't be empty");
        }


        if (StringUtils.isBlank(shardStrategies) && StringUtils.isBlank(idGenerators)
                && null == unitStrategyId && StringUtils.isBlank(unitStrategyName)) {
            throw new IllegalStateException("At least one of shardStrategies or idGenerators or unitStrategyId or unitStrategyName exists.");
        }
    }
}
