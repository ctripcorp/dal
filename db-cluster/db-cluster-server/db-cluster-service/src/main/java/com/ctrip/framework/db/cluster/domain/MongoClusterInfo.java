package com.ctrip.framework.db.cluster.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/5/28.
 */
@Data
@Builder
public class MongoClusterInfo {
    private String clusterName;
    private String clusterType;
    private String dbName;
    private String userId;
    private List<NodeInfo> nodes;
    private Map<String, String> extraProperties;
    private Boolean enabled = true;
    private Integer version;
    private String operator;
    private String updateTime;
}
