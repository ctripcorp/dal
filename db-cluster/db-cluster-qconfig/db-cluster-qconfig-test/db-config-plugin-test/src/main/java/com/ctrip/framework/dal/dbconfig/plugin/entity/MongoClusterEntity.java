package com.ctrip.framework.dal.dbconfig.plugin.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/4/3.
 */
@Data
@Builder
public class MongoClusterEntity {

    private String clusterName;
    private String clusterType;
    private String dbName;
    private String userId;
    private String password;
    private List<Node> nodes;
    private Map<String, String> extraProperties;
    private Boolean enabled = true;
    private Integer version;
    private String operator;
    private Date updateTime;
}
