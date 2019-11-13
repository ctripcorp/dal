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
public class ClusterExtensionConfigVo {

    private String shardStrategies;

    private String idGenerators;


    public void valid() {
        // shardStrategies
        if (null != shardStrategies) {
            Preconditions.checkArgument(!"".equals(shardStrategies), "if shardStrategies exists, shardStrategies can't be empty.");
        }

        // idGenerators
        if (null != idGenerators) {
            Preconditions.checkArgument(!"".equals(idGenerators), "if idGenerators exists, idGenerators can't be empty.");
        }

        if (StringUtils.isBlank(shardStrategies) && StringUtils.isBlank(idGenerators)) {
            throw new IllegalStateException("At least one of shardStrategies or idGenerators exists.");
        }
    }
}
