package com.ctrip.framework.dal.dbconfig.plugin.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/4/3.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MongoClusterEntity {
    private String clusterName;
    private String clusterType;
    private String dbName;
    private String userId;
    private String password;
    private List<Node> nodes;
    private Map<String, String> extraProperties;
    private Boolean enabled = true;
}
