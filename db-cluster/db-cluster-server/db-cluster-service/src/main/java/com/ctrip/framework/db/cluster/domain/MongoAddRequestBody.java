package com.ctrip.framework.db.cluster.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by shenjie on 2019/3/26.
 */
@Data
@Builder
public class MongoAddRequestBody {
    private String clusterName;
    private String clusterType;
    private String dbName;
    private String userId;
    private String password;
    private List<Node> nodes;
}
