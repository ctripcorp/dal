package com.ctrip.framework.db.cluster.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/3/26.
 */
@Data
@Builder
public class MongoCluster {
    private String clusterName;
    private String clusterType;
    private String dbName;
    private String userId;
    private String password;
    private List<Node> nodes;
    private Map<String, String> extraProperties;
    private String bu;
    private String prodLine;
    private List<Contact> contacts;
    private Boolean enabled;

}
