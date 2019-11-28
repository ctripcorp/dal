package com.ctrip.framework.db.cluster.domain.plugin.dal.delete;

import lombok.Builder;
import lombok.Data;

/**
 * Created by @author zhuYongMing on 2019/11/21.
 */
@Data
@Builder
public class DeleteCluster {

    private String clusterName;

    private String subEnv;
}
