package com.ctrip.framework.db.cluster.domain.plugin.dal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by shenjie on 2019/7/11.
 */
@Data
@Builder
public class DalClusterUpdateRequest {
    private String env;
    private List<ReleaseCluster> data;
}
