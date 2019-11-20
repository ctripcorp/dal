package com.ctrip.framework.db.cluster.domain.plugin.dal;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/4/18.
 */
@Data
@Builder
public class ReleaseCluster {

    private String clusterName;

    private String subEnv;

    private String dbCategory;

    private Integer version;

    private List<ReleaseShard> databaseShards;

    private String shardStrategies;

    private String idGenerators;
}
