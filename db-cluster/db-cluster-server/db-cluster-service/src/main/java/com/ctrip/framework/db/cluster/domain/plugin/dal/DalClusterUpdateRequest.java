package com.ctrip.framework.db.cluster.domain.plugin.dal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by shenjie on 2019/7/11.
 */
@Data
@Builder
@AllArgsConstructor
public class DalClusterUpdateRequest {

    private String env;

    private List<ReleaseCluster> data;
}
