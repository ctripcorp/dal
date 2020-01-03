package com.ctrip.framework.db.cluster.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/3/26.
 */
@Data
@Builder
public class NodeInfo {
    private String host;
    private Integer port;
}