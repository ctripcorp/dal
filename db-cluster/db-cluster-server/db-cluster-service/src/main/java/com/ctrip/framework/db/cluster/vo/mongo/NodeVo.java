package com.ctrip.framework.db.cluster.vo.mongo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by shenjie on 2019/3/26.
 */
@Data
@Builder
public class NodeVo {
    private String host;
    private Integer port;
}
